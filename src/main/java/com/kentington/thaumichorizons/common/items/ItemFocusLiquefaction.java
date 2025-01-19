//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items;

import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;

import com.kentington.thaumichorizons.common.ThaumicHorizons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;

public class ItemFocusLiquefaction extends ItemFocusBasic {

    public static FocusUpgradeType nuggets;
    public static FocusUpgradeType purity;
    private static final AspectList cost;
    private static final AspectList costCritter;
    static HashMap<String, Long> soundDelay;
    static HashMap<String, Object> beam;
    static HashMap<String, Float> breakcount;
    static HashMap<String, Integer> lastX;
    static HashMap<String, Integer> lastY;
    static HashMap<String, Integer> lastZ;

    public ItemFocusLiquefaction() {
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:focus_liquefaction");
    }

    @Override
    public String getSortingHelper(final ItemStack itemstack) {
        return "BE" + super.getSortingHelper(itemstack);
    }

    public int getFocusColor() {
        return 16729156;
    }

    public boolean isVisCostPerTick() {
        return false;
    }

    @Override
    public ItemStack onFocusRightClick(final ItemStack itemstack, final World world, final EntityPlayer p,
            final MovingObjectPosition mop) {
        p.setItemInUse(itemstack, Integer.MAX_VALUE);
        return itemstack;
    }

    @Override
    public void onUsingFocusTick(final ItemStack stack, final EntityPlayer player, final int count) {
        final ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        final int size = 2 + wand.getFocusEnlarge(stack) * 8;
        if (!wand.consumeAllVis(stack, player, this.getVisCost(stack), false, true)) {
            player.stopUsingItem();
        } else {
            String playerProfile = "SP|" + player.getCommandSenderName();
            if (!player.worldObj.isRemote) {
                playerProfile = "MP|" + player.getCommandSenderName();
            }
            ItemFocusLiquefaction.soundDelay.putIfAbsent(playerProfile, 0L);
            ItemFocusLiquefaction.breakcount.putIfAbsent(playerProfile, 0.0f);
            ItemFocusLiquefaction.lastX.putIfAbsent(playerProfile, 0);
            ItemFocusLiquefaction.lastY.putIfAbsent(playerProfile, 0);
            ItemFocusLiquefaction.lastZ.putIfAbsent(playerProfile, 0);

            final MovingObjectPosition mop = BlockUtils.getTargetBlock(player.worldObj, player, true);
            final Entity ent = getPointedEntity(player.worldObj, player, 10.0);
            final Vec3 v = player.getLookVec();
            double tx = player.posX + v.xCoord * 10.0;
            double ty = player.posY + v.yCoord * 10.0;
            double tz = player.posZ + v.zCoord * 10.0;
            byte impact = 0;

            ItemStack droppedItem = null;
            ItemStack resultItem = null;

            if (ent instanceof EntityItem) {
                droppedItem = ((EntityItem) ent).getEntityItem();
                if (droppedItem != null && droppedItem.stackSize > 0) {
                    ItemStack smeltingOutput = FurnaceRecipes.smelting().getSmeltingResult(droppedItem);
                    if (smeltingOutput != null) {
                        resultItem = smeltingOutput.copy();
                    }
                }
            }

            if (ent instanceof EntityLiving) {
                tx = ent.posX;
                ty = ent.posY;
                tz = ent.posZ;
                impact = 5;
                if (!player.worldObj.isRemote
                        && ItemFocusLiquefaction.soundDelay.get(playerProfile) < System.currentTimeMillis()) {
                    player.worldObj.playSoundEffect(tx, ty, tz, "fire.fire", 0.3f, 1.0f);
                    ItemFocusLiquefaction.soundDelay.put(playerProfile, System.currentTimeMillis() + 1200L);
                }
            } else if (resultItem == null && mop != null) {
                tx = mop.hitVec.xCoord;
                ty = mop.hitVec.yCoord;
                tz = mop.hitVec.zCoord;
                impact = 5;
                if (!player.worldObj.isRemote
                        && ItemFocusLiquefaction.soundDelay.get(playerProfile) < System.currentTimeMillis()) {
                    player.worldObj.playSoundEffect(tx, ty, tz, "fire.fire", 8.0f, 1.0f);
                    ItemFocusLiquefaction.soundDelay.put(playerProfile, System.currentTimeMillis() + 1200L);
                }
            } else {
                ItemFocusLiquefaction.soundDelay.put(playerProfile, 0L);
            }
            if (player.worldObj.isRemote) {
                ItemFocusLiquefaction.beam.put(
                        playerProfile,
                        Thaumcraft.proxy.beamCont(
                                player.worldObj,
                                player,
                                tx,
                                ty,
                                tz,
                                0,
                                16729156,
                                false,
                                (impact > 0) ? ((float) size) : 0.0f,
                                ItemFocusLiquefaction.beam.get(playerProfile),
                                5));
            }

            if (ent instanceof EntityLiving) {
                if (wand.consumeAllVis(stack, player, ItemFocusLiquefaction.costCritter, true, true)) {
                    ThaumicHorizons.proxy.smeltFX(ent.posX - 0.5, ent.posY, ent.posZ - 0.5, player.worldObj, 5, false);
                    ent.attackEntityFrom(DamageSource.inFire, 1.0f + 0.5f * wand.getFocusPotency(stack));
                }
            } else if (resultItem != null) {
                final int quantity = ((EntityItem) ent).getEntityItem().stackSize;
                if (wand.consumeAllVis(stack, player, this.getVisCost(stack), true, true)) {
                    ThaumicHorizons.proxy
                            .smeltFX(ent.posX - 0.5, ent.posY, ent.posZ - 0.5, player.worldObj, 5 * quantity, false);

                    resultItem.stackSize = quantity;
                    ((EntityItem) ent).setEntityItemStack(resultItem);
                }
            } else if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                final Block block = player.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
                final int meta = player.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
                final ItemStack resultBlock = FurnaceRecipes.smelting()
                        .getSmeltingResult(new ItemStack(block, 1, meta));
                final int meltingBehaviour = this.isMeltableBlock(block, meta);
                final boolean flammable = block
                        .isFlammable(player.worldObj, mop.blockX, mop.blockY, mop.blockZ, ForgeDirection.UNKNOWN);
                if (meltingBehaviour > 0 || flammable || resultBlock != null) {
                    final int pot = wand.getFocusPotency(stack);
                    final float speed = 0.15f + pot * 0.05f;
                    float hardness = 2.0f;

                    if (meltingBehaviour == 6) {
                        hardness = 20.0f;
                    } else if (meltingBehaviour > 0 || block
                            .isFlammable(player.worldObj, mop.blockX, mop.blockY, mop.blockZ, ForgeDirection.UP)) {
                                hardness = 0.25f;
                            }

                    if (ItemFocusLiquefaction.lastX.get(playerProfile) == mop.blockX
                            && ItemFocusLiquefaction.lastY.get(playerProfile) == mop.blockY
                            && ItemFocusLiquefaction.lastZ.get(playerProfile) == mop.blockZ) {
                        final float breakcount = ItemFocusLiquefaction.breakcount.get(playerProfile);

                        if (player.worldObj.isRemote) {
                            if (breakcount > 0.0f) {
                                ThaumicHorizons.proxy
                                        .smeltFX(mop.blockX, mop.blockY, mop.blockZ, player.worldObj, 15, size > 2);
                            }
                            if (breakcount >= hardness) {
                                player.worldObj.playAuxSFX(2001, mop.blockX, mop.blockY, mop.blockZ, 0);
                                ItemFocusLiquefaction.breakcount.put(playerProfile, 0.0f);
                            } else {
                                ItemFocusLiquefaction.breakcount.put(playerProfile, breakcount + speed);
                            }
                        } else if (breakcount >= hardness) {
                            this.processBlock(mop.blockX, mop.blockY, mop.blockZ, wand, stack, player, playerProfile);
                            if (size > 2) {
                                for (int x = -1; x < 2; ++x) {
                                    for (int y = -1; y < 2; ++y) {
                                        for (int z = -1; z < 2; ++z) {
                                            if (x != 0 || y != 0 || z != 0) {
                                                this.processBlock(
                                                        mop.blockX + x,
                                                        mop.blockY + y,
                                                        mop.blockZ + z,
                                                        wand,
                                                        stack,
                                                        player,
                                                        playerProfile);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            ItemFocusLiquefaction.breakcount.put(playerProfile, breakcount + speed);
                        }
                    } else {
                        ItemFocusLiquefaction.lastX.put(playerProfile, mop.blockX);
                        ItemFocusLiquefaction.lastY.put(playerProfile, mop.blockY);
                        ItemFocusLiquefaction.lastZ.put(playerProfile, mop.blockZ);
                        ItemFocusLiquefaction.breakcount.put(playerProfile, 0.0f);
                    }
                }
            } else {
                ItemFocusLiquefaction.lastX.put(playerProfile, Integer.MAX_VALUE);
                ItemFocusLiquefaction.lastY.put(playerProfile, Integer.MAX_VALUE);
                ItemFocusLiquefaction.lastZ.put(playerProfile, Integer.MAX_VALUE);
                ItemFocusLiquefaction.breakcount.put(playerProfile, 0.0f);
            }
        }
    }

    public void onPlayerStoppedUsing(final ItemStack stack, final World world, final EntityPlayer p, final int count) {
        String pp = "SP|" + p.getCommandSenderName();
        if (!p.worldObj.isRemote) {
            pp = "MP|" + p.getCommandSenderName();
        }
        ItemFocusLiquefaction.soundDelay.putIfAbsent(pp, 0L);
        ItemFocusLiquefaction.breakcount.putIfAbsent(pp, 0.0f);
        ItemFocusLiquefaction.lastX.putIfAbsent(pp, 0);
        ItemFocusLiquefaction.lastY.putIfAbsent(pp, 0);
        ItemFocusLiquefaction.lastZ.putIfAbsent(pp, 0);
        ItemFocusLiquefaction.beam.put(pp, null);
        ItemFocusLiquefaction.lastX.put(pp, Integer.MAX_VALUE);
        ItemFocusLiquefaction.lastY.put(pp, Integer.MAX_VALUE);
        ItemFocusLiquefaction.lastZ.put(pp, Integer.MAX_VALUE);
        ItemFocusLiquefaction.breakcount.put(pp, 0.0f);
    }

    @Override
    public WandFocusAnimation getAnimation(final ItemStack focusstack) {
        return WandFocusAnimation.CHARGE;
    }

    public int isMeltableBlock(final Block block, final int meta) {
        if (block == Blocks.stone) {
            return 6;
        }
        final Material mat = block.getMaterial();
        if (mat == Material.tnt) {
            return 4;
        }
        if (mat == Material.grass) {
            return 3;
        }
        if (mat == Material.ice || mat == Material.packedIce) {
            return 2;
        }
        if (mat == Material.craftedSnow || mat == Material.leaves
                || mat == Material.plants
                || mat == Material.snow
                || mat == Material.vine
                || mat == Material.web) {
            return 1;
        }
        return 0;
    }

    @Override
    public int getFocusColor(final ItemStack focusstack) {
        return 16711680;
    }

    @Override
    public AspectList getVisCost(final ItemStack focusstack) {
        return ItemFocusLiquefaction.cost;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(final ItemStack focusstack, final int rank) {
        switch (rank) {
            case 1, 4, 2 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.potency,
                        ItemFocusLiquefaction.nuggets };
            }
            case 3 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.potency,
                        FocusUpgradeType.enlarge };
            }
            case 5 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.potency,
                        ItemFocusLiquefaction.purity };
            }
            default -> {
                return null;
            }
        }
    }

    public String getItemStackDisplayName(final ItemStack stack) {
        return StatCollector.translateToLocal("item.focusLiquefaction.name");
    }

    public static Entity getPointedEntity(final World world, final EntityLivingBase entityplayer, final double range) {
        Entity pointedEntity = null;
        final Vec3 vec3d = Vec3.createVectorHelper(
                entityplayer.posX,
                entityplayer.posY + entityplayer.getEyeHeight(),
                entityplayer.posZ);
        final Vec3 vec3d2 = entityplayer.getLookVec();
        final Vec3 vec3d3 = vec3d.addVector(vec3d2.xCoord * range, vec3d2.yCoord * range, vec3d2.zCoord * range);
        final float f1 = 1.1f;
        final List list = world.getEntitiesWithinAABBExcludingEntity(
                entityplayer,
                entityplayer.boundingBox.addCoord(vec3d2.xCoord * range, vec3d2.yCoord * range, vec3d2.zCoord * range)
                        .expand(f1, f1, f1));
        double d2 = 0.0;
        for (Object o : list) {
            final Entity entity = (Entity) o;
            if (world.rayTraceBlocks(
                    Vec3.createVectorHelper(
                            entityplayer.posX,
                            entityplayer.posY + entityplayer.getEyeHeight(),
                            entityplayer.posZ),
                    Vec3.createVectorHelper(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ),
                    false) == null) {
                final float f2 = Math.max(0.8f, entity.getCollisionBorderSize());
                final AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f2, f2, f2);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3d, vec3d3);
                if (axisalignedbb.isVecInside(vec3d)) {
                    if (0.0 < d2 || d2 == 0.0) {
                        pointedEntity = entity;
                        d2 = 0.0;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3d.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0) {
                        pointedEntity = entity;
                        d2 = d3;
                    }
                }
            }
        }
        return pointedEntity;
    }

    void processBlock(final int x, final int y, final int z, final ItemWandCasting wand, final ItemStack stack,
            final EntityPlayer p, final String pp) {
        WorldSettings.GameType gt = WorldSettings.GameType.SURVIVAL;
        if (p.capabilities.allowEdit) {
            if (p.capabilities.isCreativeMode) {
                gt = WorldSettings.GameType.CREATIVE;
            }
        } else {
            gt = WorldSettings.GameType.ADVENTURE;
        }
        final BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(p.worldObj, gt, (EntityPlayerMP) p, x, y, z);
        if (event.isCanceled()) {
            return;
        }
        final Block bi = p.worldObj.getBlock(x, y, z);
        if (bi.getBlockHardness(p.worldObj, x, y, z) == -1.0f) {
            return;
        }
        final int md = p.worldObj.getBlockMetadata(x, y, z);
        final int meltable = this.isMeltableBlock(bi, md);
        final boolean flammable = bi.isFlammable(p.worldObj, x, y, z, ForgeDirection.UNKNOWN);
        ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(bi, 1, md));
        if (result != null && wand.consumeAllVis(stack, p, this.getVisCost(stack), true, true)) {
            if (this.getUpgradeLevel(wand.getFocusItem(stack), ItemFocusLiquefaction.purity) > 0
                    && Utils.findSpecialMiningResult(new ItemStack(bi, 1, md), 9999.0f, p.worldObj.rand) != null) {
                final ItemStack pure = Utils
                        .findSpecialMiningResult(new ItemStack(bi, 1, md), 9999.0f, p.worldObj.rand);
                if (FurnaceRecipes.smelting().getSmeltingResult(pure) != null) {
                    result = FurnaceRecipes.smelting().getSmeltingResult(pure);
                }
            }
            if (this.getUpgradeLevel(wand.getFocusItem(stack), ItemFocusLiquefaction.nuggets) > 0
                    && ThaumcraftApi.getSmeltingBonus(new ItemStack(bi, 1, md)) != null) {
                final ItemStack bonus = ThaumcraftApi.getSmeltingBonus(new ItemStack(bi, 1, md)).copy();
                for (int i = 0; i
                        < this.getUpgradeLevel(wand.getFocusItem(stack), ItemFocusLiquefaction.nuggets); ++i) {
                    if (p.worldObj.rand.nextFloat() < 0.45f) {
                        ++bonus.stackSize;
                    }
                }
                if (bonus.stackSize > 0) {
                    final EntityItem bonusEntity = new EntityItem(p.worldObj, x + 0.5f, y + 0.5f, z + 0.5f, bonus);
                    p.worldObj.spawnEntityInWorld(bonusEntity);
                }
            }
            if (result.getItem() instanceof ItemBlock) {
                p.worldObj.setBlock(x, y, z, Block.getBlockFromItem(result.getItem()));
                p.worldObj.setBlockMetadataWithNotify(x, y, z, result.getItemDamage(), 3);
            } else {
                p.worldObj.setBlockToAir(x, y, z);
                final EntityItem theItem = new EntityItem(p.worldObj, x + 0.5f, y + 0.5f, z + 0.5f);
                theItem.setEntityItemStack(result.copy());
                p.worldObj.spawnEntityInWorld(theItem);
            }
        } else if (meltable > 0 && wand.consumeAllVis(stack, p, this.getVisCost(stack), true, true)) {
            if (meltable == 1) {
                p.worldObj.setBlockToAir(x, y, z);
            } else if (meltable == 2) {
                if (p.worldObj.provider.dimensionId != -1) {
                    p.worldObj.setBlock(x, y, z, Blocks.water, 0, 3);
                } else {
                    p.worldObj.setBlockToAir(x, y, z);
                }
            } else if (meltable == 3) {
                p.worldObj.setBlock(x, y, z, Blocks.dirt, 0, 3);
            } else if (meltable == 4) {
                Blocks.tnt.onBlockDestroyedByPlayer(p.worldObj, x, y, z, 1);
                p.worldObj.setBlockToAir(x, y, z);
            } else if (meltable == 6) {
                p.worldObj.setBlock(x, y, z, Blocks.lava, 0, 3);
            }
        } else if (flammable && wand.consumeAllVis(stack, p, this.getVisCost(stack), true, true)) {
            p.worldObj.setBlock(x, y, z, Blocks.fire);
        }
        ItemFocusLiquefaction.lastX.put(pp, Integer.MAX_VALUE);
        ItemFocusLiquefaction.lastY.put(pp, Integer.MAX_VALUE);
        ItemFocusLiquefaction.lastZ.put(pp, Integer.MAX_VALUE);
        ItemFocusLiquefaction.breakcount.put(pp, 0.0f);
        p.worldObj.markBlockForUpdate(x, y, z);
    }

    static {
        ItemFocusLiquefaction.nuggets = new FocusUpgradeType(
                FocusUpgradeType.types.length,
                new ResourceLocation("thaumichorizons", "textures/foci/nuggets.png"),
                "focus.upgrade.nuggets.name",
                "focus.upgrade.nuggets.text",
                new AspectList().add(Aspect.METAL, 8));
        ItemFocusLiquefaction.purity = new FocusUpgradeType(
                FocusUpgradeType.types.length,
                new ResourceLocation("thaumichorizons", "textures/foci/purity.png"),
                "focus.upgrade.purity.name",
                "focus.upgrade.purity.text",
                new AspectList().add(Aspect.ORDER, 8));
        cost = new AspectList().add(Aspect.FIRE, 40);
        costCritter = new AspectList().add(Aspect.FIRE, 15);
        ItemFocusLiquefaction.soundDelay = new HashMap<>();
        ItemFocusLiquefaction.beam = new HashMap<>();
        ItemFocusLiquefaction.breakcount = new HashMap<>();
        ItemFocusLiquefaction.lastX = new HashMap<>();
        ItemFocusLiquefaction.lastY = new HashMap<>();
        ItemFocusLiquefaction.lastZ = new HashMap<>();
    }
}
