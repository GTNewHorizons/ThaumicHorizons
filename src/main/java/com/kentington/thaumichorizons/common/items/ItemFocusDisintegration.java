//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityItemInvulnerable;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemCrystalEssence;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.BlockUtils;

public class ItemFocusDisintegration extends ItemFocusBasic {

    public static FocusUpgradeType enervation;
    private static final AspectList cost;
    private static final AspectList costCritter;
    static HashMap<String, Long> soundDelay;
    static HashMap<String, Object> beam;
    static HashMap<String, Float> breakcount;
    static HashMap<String, Integer> lastX;
    static HashMap<String, Integer> lastY;
    static HashMap<String, Integer> lastZ;

    public ItemFocusDisintegration() {
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:focus_disintegration");
    }

    @Override
    public String getSortingHelper(final ItemStack itemstack) {
        return "DS" + super.getSortingHelper(itemstack);
    }

    public int getFocusColor() {
        return 7930047;
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
    public void onUsingFocusTick(ItemStack stack, final EntityPlayer p, final int count) {
        final ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        final int size = 2 + wand.getFocusEnlarge(stack) * 8;
        if (!wand.consumeAllVis(stack, p, this.getVisCost(stack), false, true)) {
            p.stopUsingItem();
        } else {
            String pp = "SP|" + p.getCommandSenderName();
            if (!p.worldObj.isRemote) {
                pp = "MP|" + p.getCommandSenderName();
            }
            ItemFocusDisintegration.soundDelay.putIfAbsent(pp, 0L);
            ItemFocusDisintegration.breakcount.putIfAbsent(pp, 0.0f);
            ItemFocusDisintegration.lastX.putIfAbsent(pp, 0);
            ItemFocusDisintegration.lastY.putIfAbsent(pp, 0);
            ItemFocusDisintegration.lastZ.putIfAbsent(pp, 0);
            final MovingObjectPosition mop = BlockUtils.getTargetBlock(p.worldObj, p, true);
            final Entity ent = getPointedEntity(p.worldObj, p, 10.0);
            final Vec3 v = p.getLookVec();
            double tx = p.posX + v.xCoord * 10.0;
            double ty = p.posY + v.yCoord * 10.0;
            double tz = p.posZ + v.zCoord * 10.0;
            byte impact = 0;
            if ((ent == null || (ent instanceof EntityItem && (this.getAspects(
                    ((EntityItem) ent).getEntityItem().getItem(),
                    ((EntityItem) ent).getEntityItem().getItemDamage()) == null
                    || this.getAspects(
                            ((EntityItem) ent).getEntityItem().getItem(),
                            ((EntityItem) ent).getEntityItem().getItemDamage()).size() == 0)))
                    && mop != null) {
                tx = mop.hitVec.xCoord;
                ty = mop.hitVec.yCoord;
                tz = mop.hitVec.zCoord;
                impact = 5;
                if (!p.worldObj.isRemote && ItemFocusDisintegration.soundDelay.get(pp) < System.currentTimeMillis()) {
                    p.worldObj.playSoundEffect(tx, ty, tz, "thaumcraft:zap", 0.3f, 1.0f);
                    ItemFocusDisintegration.soundDelay.put(pp, System.currentTimeMillis() + 100L);
                }
            } else if (ent != null) {
                tx = ent.posX;
                ty = ent.posY;
                tz = ent.posZ;
                impact = 5;
                if (!p.worldObj.isRemote && ItemFocusDisintegration.soundDelay.get(pp) < System.currentTimeMillis()) {
                    p.worldObj.playSoundEffect(tx, ty, tz, "thaumcraft:zap", 0.3f, 1.0f);
                    ItemFocusDisintegration.soundDelay.put(pp, System.currentTimeMillis() + 100L);
                }
            }
            if (p.worldObj.isRemote) {
                ItemFocusDisintegration.beam.put(
                        pp,
                        Thaumcraft.proxy.beamCont(
                                p.worldObj,
                                p,
                                tx,
                                ty,
                                tz,
                                0,
                                7930047,
                                false,
                                (impact > 0) ? ((float) size) : 0.0f,
                                ItemFocusDisintegration.beam.get(pp),
                                5));
            }
            if ((ent == null || (!(ent instanceof EntityLiving) && (!(ent instanceof EntityItem)
                    || ((EntityItem) ent).getEntityItem().getItem() == ConfigItems.itemCrystalEssence
                    || this.getAspects(
                            ((EntityItem) ent).getEntityItem().getItem(),
                            ((EntityItem) ent).getEntityItem().getItemDamage()) == null
                    || this.getAspects(
                            ((EntityItem) ent).getEntityItem().getItem(),
                            ((EntityItem) ent).getEntityItem().getItemDamage()).size() == 0)))
                    && mop != null
                    && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                final Block bi = p.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ);
                final int md = p.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ);
                if (Item.getItemFromBlock(bi) != null && this.getAspects(Item.getItemFromBlock(bi), md) != null) {
                    final int pot = wand.getFocusPotency(stack);
                    final float speed = 0.05f + pot * 0.015f;
                    final float hardness = bi.getBlockHardness(p.worldObj, mop.blockX, mop.blockY, mop.blockZ);
                    if (hardness == -1.0f) {
                        return;
                    }
                    if (ItemFocusDisintegration.lastX.get(pp) == mop.blockX
                            && ItemFocusDisintegration.lastY.get(pp) == mop.blockY
                            && ItemFocusDisintegration.lastZ.get(pp) == mop.blockZ) {
                        final float bc = ItemFocusDisintegration.breakcount.get(pp);
                        if (p.worldObj.isRemote && bc > 0.0f && bi != null) {
                            final int gt = (int) (bc / hardness * 9.0f);
                            ThaumicHorizons.proxy.disintegrateFX(mop.blockX, mop.blockY, mop.blockZ, p, 15, size > 2);
                        }
                        if (p.worldObj.isRemote) {
                            if (bc >= hardness) {
                                p.worldObj.playAuxSFX(2001, mop.blockX, mop.blockY, mop.blockZ, 0);
                                ItemFocusDisintegration.breakcount.put(pp, 0.0f);
                            } else {
                                ItemFocusDisintegration.breakcount.put(pp, bc + speed);
                            }
                        } else if (bc >= hardness) {
                            this.processBlock(mop.blockX, mop.blockY, mop.blockZ, wand, stack, p, pp);
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
                                                        p,
                                                        pp);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            ItemFocusDisintegration.breakcount.put(pp, bc + speed);
                        }
                    } else {
                        ItemFocusDisintegration.lastX.put(pp, mop.blockX);
                        ItemFocusDisintegration.lastY.put(pp, mop.blockY);
                        ItemFocusDisintegration.lastZ.put(pp, mop.blockZ);
                        ItemFocusDisintegration.breakcount.put(pp, 0.0f);
                    }
                }
            } else if (ent instanceof EntityLiving
                    && wand.consumeAllVis(stack, p, ItemFocusDisintegration.costCritter, true, true)) {
                        if (this.getUpgradeLevel(wand.getFocusItem(stack), ItemFocusDisintegration.enervation) > 0) {
                            ((EntityLiving) ent).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 40, 0));
                        }
                        ThaumicHorizons.proxy.disintegrateFX(ent.posX - 0.5, ent.posY, ent.posZ - 0.5, p, 5, false);
                        ent.attackEntityFrom(
                                DamageSourceThaumcraft.dissolve,
                                0.5f + 0.25f * wand.getFocusPotency(stack)
                                        + 0.5f * this.getUpgradeLevel(
                                                wand.getFocusItem(stack),
                                                ItemFocusDisintegration.enervation));
                    } else
                if (ent instanceof EntityItem
                        && this.getAspects(
                                ((EntityItem) ent).getEntityItem().getItem(),
                                ((EntityItem) ent).getEntityItem().getItemDamage()) != null
                        && this.getAspects(
                                ((EntityItem) ent).getEntityItem().getItem(),
                                ((EntityItem) ent).getEntityItem().getItemDamage()).size() > 0) {
                                    final int num = ((EntityItem) ent).getEntityItem().stackSize;
                                    if (wand.consumeAllVis(stack, p, this.getVisCost(stack), true, true)) {
                                        ThaumicHorizons.proxy.disintegrateFX(
                                                ent.posX - 0.5,
                                                ent.posY,
                                                ent.posZ - 0.5,
                                                p,
                                                5 * num,
                                                false);
                                        final AspectList aspects = this.getAspects(
                                                ((EntityItem) ent).getEntityItem().getItem(),
                                                ((EntityItem) ent).getEntityItem().getItemDamage());
                                        if (aspects != null && !p.worldObj.isRemote) {
                                            if (!p.worldObj.isRemote) {
                                                for (final Aspect asp : aspects.getAspects()) {
                                                    stack = new ItemStack(
                                                            ConfigItems.itemCrystalEssence,
                                                            aspects.getAmount(asp) * num);
                                                    ((ItemCrystalEssence) stack.getItem())
                                                            .setAspects(stack, new AspectList().add(asp, 1));
                                                    p.worldObj.spawnEntityInWorld(
                                                            new EntityItemInvulnerable(
                                                                    p.worldObj,
                                                                    ent.posX,
                                                                    ent.posY,
                                                                    ent.posZ,
                                                                    stack));
                                                }
                                            } else {
                                                ThaumicHorizons.proxy.disintegrateExplodeFX(
                                                        p.worldObj,
                                                        ent.posX,
                                                        ent.posY,
                                                        ent.posZ);
                                            }
                                        }
                                    }
                                    ent.setDead();
                                } else {
                                    ItemFocusDisintegration.lastX.put(pp, Integer.MAX_VALUE);
                                    ItemFocusDisintegration.lastY.put(pp, Integer.MAX_VALUE);
                                    ItemFocusDisintegration.lastZ.put(pp, Integer.MAX_VALUE);
                                    ItemFocusDisintegration.breakcount.put(pp, 0.0f);
                                }
        }
    }

    public void onPlayerStoppedUsing(final ItemStack stack, final World world, final EntityPlayer p, final int count) {
        String pp = "R" + p.getCommandSenderName();
        if (!p.worldObj.isRemote) {
            pp = "S" + p.getCommandSenderName();
        }
        ItemFocusDisintegration.soundDelay.putIfAbsent(pp, 0L);
        ItemFocusDisintegration.breakcount.putIfAbsent(pp, 0.0f);
        ItemFocusDisintegration.lastX.putIfAbsent(pp, 0);
        ItemFocusDisintegration.lastY.putIfAbsent(pp, 0);
        ItemFocusDisintegration.lastZ.putIfAbsent(pp, 0);
        ItemFocusDisintegration.beam.put(pp, null);
        ItemFocusDisintegration.lastX.put(pp, Integer.MAX_VALUE);
        ItemFocusDisintegration.lastY.put(pp, Integer.MAX_VALUE);
        ItemFocusDisintegration.lastZ.put(pp, Integer.MAX_VALUE);
        ItemFocusDisintegration.breakcount.put(pp, 0.0f);
    }

    @Override
    public WandFocusAnimation getAnimation(final ItemStack focusstack) {
        return WandFocusAnimation.CHARGE;
    }

    @Override
    public int getFocusColor(final ItemStack focusstack) {
        return 7930047;
    }

    @Override
    public AspectList getVisCost(final ItemStack focusstack) {
        return ItemFocusDisintegration.cost;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(final ItemStack focusstack, final int rank) {
        switch (rank) {
            case 1, 4, 2 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.potency };
            }
            case 3 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.potency,
                        FocusUpgradeType.enlarge };
            }
            case 5 -> {
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.potency,
                        ItemFocusDisintegration.enervation };
            }
            default -> {
                return null;
            }
        }
    }

    public String getItemStackDisplayName(final ItemStack stack) {
        return StatCollector.translateToLocal("item.focusDisintegration.name");
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
            if (!(entity instanceof EntityItem)
                    || ((EntityItem) entity).getEntityItem().getItem() != ConfigItems.itemCrystalEssence) {
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
        }
        return pointedEntity;
    }

    void processBlock(final int x, final int y, final int z, final ItemWandCasting wand, ItemStack stack,
            final EntityPlayer p, final String pp) {
        WorldSettings.GameType gt = WorldSettings.GameType.SURVIVAL;
        if (p.capabilities.allowEdit) {
            if (p.capabilities.isCreativeMode) {
                gt = WorldSettings.GameType.CREATIVE;
            }
        } else {
            gt = WorldSettings.GameType.ADVENTURE;
        }
        final Block bi = p.worldObj.getBlock(x, y, z);
        if (bi.getBlockHardness(p.worldObj, x, y, z) == -1.0f) {
            return;
        }
        final BlockEvent.BreakEvent event = ForgeHooks.onBlockBreakEvent(p.worldObj, gt, (EntityPlayerMP) p, x, y, z);
        if (event.isCanceled()) {
            return;
        }
        final int md = p.worldObj.getBlockMetadata(x, y, z);
        if (Item.getItemFromBlock(bi) != null && this.getAspects(Item.getItemFromBlock(bi), md) != null
                && this.getAspects(Item.getItemFromBlock(bi), md).size() > 0
                && wand.consumeAllVis(stack, p, this.getVisCost(stack), true, true)) {
            final AspectList aspects = this.getAspects(
                    Item.getItemFromBlock(p.worldObj.getBlock(x, y, z)),
                    p.worldObj.getBlockMetadata(x, y, z));
            if (aspects != null) {
                for (final Aspect asp : aspects.getAspects()) {
                    stack = new ItemStack(ConfigItems.itemCrystalEssence, aspects.getAmount(asp));
                    ((ItemCrystalEssence) stack.getItem()).setAspects(stack, new AspectList().add(asp, 1));
                    p.worldObj.spawnEntityInWorld(
                            new EntityItemInvulnerable(p.worldObj, x + 0.5, y + 0.5, z + 0.5, stack));
                }
                ThaumicHorizons.proxy.disintegrateExplodeFX(p.worldObj, x + 0.5, y + 0.5, z + 0.5);
            }
            p.worldObj.setBlockToAir(x, y, z);
        }
        ItemFocusDisintegration.lastX.put(pp, Integer.MAX_VALUE);
        ItemFocusDisintegration.lastY.put(pp, Integer.MAX_VALUE);
        ItemFocusDisintegration.lastZ.put(pp, Integer.MAX_VALUE);
        ItemFocusDisintegration.breakcount.put(pp, 0.0f);
        p.worldObj.markBlockForUpdate(x, y, z);
    }

    private AspectList getAspects(final Item block, final int meta) {
        final ItemStack tmpStack = new ItemStack(block, 1, meta);
        AspectList tmp = ThaumcraftCraftingManager.getObjectTags(tmpStack);
        tmp = ThaumcraftCraftingManager.getBonusTags(tmpStack, tmp);
        if (tmp == null || tmp.size() == 0) {
            tmp = ThaumcraftApi.objectTags.get(Arrays.asList(block, 32767));
            if (meta == 32767 && tmp == null) {
                int index = 0;
                do {
                    tmp = ThaumcraftApi.objectTags.get(Arrays.asList(block, index));
                } while (++index < 16 && tmp == null);
            }
        }
        return tmp;
    }

    static {
        ItemFocusDisintegration.enervation = new FocusUpgradeType(
                FocusUpgradeType.types.length,
                new ResourceLocation("thaumichorizons", "textures/foci/enervation.png"),
                "focus.upgrade.enervation.name",
                "focus.upgrade.enervation.text",
                new AspectList().add(Aspect.DEATH, 8));
        cost = new AspectList().add(Aspect.FIRE, 30).add(Aspect.ENTROPY, 30).add(Aspect.EARTH, 30);
        costCritter = new AspectList().add(Aspect.FIRE, 12).add(Aspect.ENTROPY, 12).add(Aspect.EARTH, 12);
        ItemFocusDisintegration.soundDelay = new HashMap<>();
        ItemFocusDisintegration.beam = new HashMap<>();
        ItemFocusDisintegration.breakcount = new HashMap<>();
        ItemFocusDisintegration.lastX = new HashMap<>();
        ItemFocusDisintegration.lastY = new HashMap<>();
        ItemFocusDisintegration.lastZ = new HashMap<>();
    }
}
