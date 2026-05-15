package com.kentington.thaumichorizons.common.tiles;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.IEntityInfusedStats;
import com.kentington.thaumichorizons.common.lib.CreatureInfusionRecipe;
import com.kentington.thaumichorizons.common.lib.EntityInfusionProperties;
import com.kentington.thaumichorizons.common.lib.SelfInfusionRecipe;
import com.kentington.thaumichorizons.common.lib.networking.PacketFXEssentiaBubble;
import com.kentington.thaumichorizons.common.lib.networking.PacketFXInfusionDone;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketInfusionFX;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.visnet.TileVisNode;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TilePedestal;

public class TileVatMatrix extends TileVisNode implements IWandable {

    // infusion state
    public boolean crafting = false;
    public boolean active = false;
    public boolean checkSurroundings = true;
    public int symmetry = 0;
    public int instability = 0;
    public AspectList recipeEssentia = new AspectList();
    public ArrayList<ItemStack> recipeIngredients = null;
    public Object recipeOutput = null;
    public String recipePlayer = null;
    public String recipeOutputLabel = "";
    public int recipeInstability = 0;
    public int recipeType = 0;
    public float selfInfusionHealth = 20f;
    public ArrayList<ChunkCoordinates> pedestals = new ArrayList<>();
    public int count = 0;
    private final int countDelay = 10;
    public int craftCount = 0;
    private int itemCount = 0;
    private AspectList myEssentiaCost = new AspectList();
    public HashMap<String, SourceFX> sourceFX = new HashMap<>();

    public TileVat getVat() {
        if (this.worldObj == null) return null;
        final TileEntity t = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
        return (t instanceof TileVat) ? (TileVat) t : null;
    }

    @Override
    public void updateEntity() {
        super.updateEntity(); // TileVisNode vis network tick
        if (worldObj.isRemote) {
            doEffects();
            return;
        }
        ++count;
        if (checkSurroundings) {
            checkSurroundings = false;
            getSurroundings();
        }
        if (active && crafting && count % countDelay == 0) {
            craftCycle();
            markDirty();
        }
    }

    // -------------------------------------------------------------------------
    // Pedestal / symmetry scanning
    // -------------------------------------------------------------------------

    public void getSurroundings() {
        final ArrayList<ChunkCoordinates> stuff = new ArrayList<>();
        pedestals.clear();
        try {
            for (int xx = -12; xx <= 12; ++xx) {
                for (int zz = -12; zz <= 12; ++zz) {
                    boolean skip = false;
                    for (int yy = -5; yy <= 10; ++yy) {
                        if (xx == 0 && zz == 0) continue;
                        final int x = xCoord + xx, y = yCoord - yy, z = zCoord + zz;
                        final TileEntity te = worldObj.getTileEntity(x, y, z);
                        if (!skip && yy > 0 && Math.abs(xx) <= 8 && Math.abs(zz) <= 8 && te instanceof TilePedestal) {
                            pedestals.add(new ChunkCoordinates(x, y, z));
                            skip = true;
                        } else {
                            final Block bi = worldObj.getBlock(x, y, z);
                            if (bi == Blocks.skull || (bi instanceof IInfusionStabiliser
                                    && ((IInfusionStabiliser) bi).canStabaliseInfusion(worldObj, x, y, z))) {
                                stuff.add(new ChunkCoordinates(x, y, z));
                            }
                        }
                    }
                }
            }
            symmetry = 0;
            for (final ChunkCoordinates cc : pedestals) {
                boolean items = false;
                final int dx = xCoord - cc.posX, dz = zCoord - cc.posZ;
                TileEntity te2 = worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
                if (te2 instanceof TilePedestal) {
                    symmetry += 2;
                    if (((TilePedestal) te2).getStackInSlot(0) != null) {
                        ++symmetry;
                        items = true;
                    }
                }
                te2 = worldObj.getTileEntity(xCoord + dx, cc.posY, zCoord + dz);
                if (te2 instanceof TilePedestal) {
                    symmetry -= 2;
                    if (((TilePedestal) te2).getStackInSlot(0) == null || !items) continue;
                    --symmetry;
                }
            }
            float sym = 0f;
            for (final ChunkCoordinates cc2 : stuff) {
                final int dx = xCoord - cc2.posX, dz = zCoord - cc2.posZ;
                Block bi2 = worldObj.getBlock(cc2.posX, cc2.posY, cc2.posZ);
                if (bi2 == Blocks.skull || (bi2 instanceof IInfusionStabiliser
                        && ((IInfusionStabiliser) bi2).canStabaliseInfusion(worldObj, cc2.posX, cc2.posY, cc2.posZ)))
                    sym += 0.1f;
                bi2 = worldObj.getBlock(xCoord + dx, cc2.posY, zCoord + dz);
                if (bi2 == Blocks.skull || (bi2 instanceof IInfusionStabiliser
                        && ((IInfusionStabiliser) bi2).canStabaliseInfusion(worldObj, cc2.posX, cc2.posY, cc2.posZ)))
                    sym -= 0.2f;
            }
            symmetry += (int) sym;
        } catch (Exception ignored) {}
    }

    // -------------------------------------------------------------------------
    // Wand interaction
    // -------------------------------------------------------------------------

    @Override
    public ItemStack onWandRightClick(final World world, final ItemStack wandstack, final EntityPlayer player) {
        final TileVat vat = getVat();
        if (vat != null && !crafting && ((vat.isValidInfusionTarget() && vat.mode == 0) || vat.mode == 4)) {
            craftingStart(player);
        }
        return wandstack;
    }

    @Override
    public int onWandRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final int x,
            final int y, final int z, final int side, final int md) {
        onWandRightClick(world, wandstack, player);
        return 0;
    }

    @Override
    public void onUsingWandTick(final ItemStack wandstack, final EntityPlayer player, final int count) {}

    @Override
    public void onWandStoppedUsing(final ItemStack wandstack, final World world, final EntityPlayer player,
            final int count) {}

    // -------------------------------------------------------------------------
    // Infusion start
    // -------------------------------------------------------------------------

    public void craftingStart(final EntityPlayer player) {
        final TileVat vat = getVat();
        if (vat == null) return;

        getSurroundings();

        final ArrayList<ItemStack> components = new ArrayList<>();
        for (final ChunkCoordinates cc : pedestals) {
            final TileEntity te = worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
            if (te instanceof TilePedestal) {
                final ItemStack s = ((TilePedestal) te).getStackInSlot(0);
                if (s != null) components.add(s.copy());
            }
        }
        if (components.isEmpty()) return;

        if (vat.mode != 4) {
            final CreatureInfusionRecipe recipe = ThaumicHorizons
                    .getCreatureInfusion(vat.getEntityContained(), components, player);
            if (recipe == null) return;
            if (recipe.getID(null) != 0
                    && ((EntityInfusionProperties) vat.getEntityContained().getExtendedProperties("CreatureInfusion"))
                            .hasInfusion(recipe.getID(null)))
                return;
            if (recipe.getRecipeOutput() instanceof NBTTagCompound
                    && ((NBTTagCompound) recipe.getRecipeOutput()).getInteger("instilledLoyalty") != 0
                    && ((EntityLiving) vat.getEntityContained()).tasks.taskEntries.isEmpty())
                return;

            recipeType = 0;
            recipeIngredients = new ArrayList<>();
            for (final ItemStack ing : recipe.getComponents()) recipeIngredients.add(ing.copy());
            final Object out = recipe.getRecipeOutput(vat.getEntityContained().getClass());
            if (out instanceof Object[]) {
                recipeOutputLabel = (String) ((Object[]) out)[0];
                recipeOutput = ((Object[]) out)[1];
            } else {
                recipeOutput = out;
            }
            recipeInstability = recipe.getInstability(vat.getEntityContained().getClass());
            recipeEssentia = recipe.getAspects(vat.getEntityContained().getClass()).copy();
        } else {
            final SelfInfusionRecipe recipe = ThaumicHorizons.getSelfInfusion(components, player);
            if (recipe == null) return;
            for (final int id : vat.selfInfusions) {
                if (id == recipe.getID()) return;
            }
            recipeType = 1;
            recipeIngredients = new ArrayList<>();
            for (final ItemStack ing : recipe.getComponents()) recipeIngredients.add(ing.copy());
            recipeOutputLabel = "";
            recipeOutput = recipe.getID();
            recipeInstability = recipe.getInstability();
            recipeEssentia = recipe.getAspects().copy();
        }

        myEssentiaCost = recipeEssentia.copy();
        recipePlayer = player.getCommandSenderName();
        instability = symmetry + recipeInstability;
        active = true;
        crafting = true;
        worldObj.playSoundEffect(xCoord, yCoord, zCoord, "thaumcraft:craftstart", 0.5f, 1.0f);
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        markDirty();
    }

    // -------------------------------------------------------------------------
    // Essentia drawing
    // -------------------------------------------------------------------------

    private boolean tryDrawAllEssentia() {
        final TileVat vat = getVat();
        if (vat == null) return false;
        final int vx = vat.xCoord, vy = vat.yCoord, vz = vat.zCoord;
        boolean drew = false;
        TileEntity conn;
        conn = worldObj.getTileEntity(vx - 1, vy - 3, vz);
        if (conn instanceof TileVatConnector) drew |= tryDrawEssentia((TileVatConnector) conn);
        if (drew) return true;
        conn = worldObj.getTileEntity(vx + 1, vy - 3, vz);
        if (conn instanceof TileVatConnector) drew |= tryDrawEssentia((TileVatConnector) conn);
        if (drew) return true;
        conn = worldObj.getTileEntity(vx, vy - 3, vz - 1);
        if (conn instanceof TileVatConnector) drew |= tryDrawEssentia((TileVatConnector) conn);
        if (drew) return true;
        conn = worldObj.getTileEntity(vx, vy - 3, vz + 1);
        if (conn instanceof TileVatConnector) drew |= tryDrawEssentia((TileVatConnector) conn);
        return drew;
    }

    private boolean tryDrawEssentia(final TileVatConnector conn) {
        for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            final TileEntity te = ThaumcraftApiHelper
                    .getConnectableTile(worldObj, conn.xCoord, conn.yCoord, conn.zCoord, dir);
            if (te == null) continue;
            final IEssentiaTransport ic = (IEssentiaTransport) te;
            if (ic.getEssentiaAmount(dir.getOpposite()) > 0 && ic.getSuctionAmount(dir.getOpposite()) < 128
                    && ic.getMinimumSuction() <= 128) {
                for (final Aspect asp : recipeEssentia.getAspects()) {
                    if (recipeEssentia.getAmount(asp) > 0) {
                        final int got = ic.takeEssentia(asp, 1, dir.getOpposite());
                        if (got > 0) {
                            recipeEssentia.reduce(asp, got);
                            clientEssentiaFX(asp);
                            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                            markDirty();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void clientEssentiaFX(final Aspect asp) {
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXEssentiaBubble(xCoord + 0.5, yCoord - 3, zCoord + 0.5, asp.getColor()),
                new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 32.0));
    }

    // -------------------------------------------------------------------------
    // Instability events
    // -------------------------------------------------------------------------

    private void inEvZap(final boolean all) {
        final List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                        .expand(10, 10, 10));
        if (targets == null || targets.isEmpty()) return;
        for (final Entity t : targets) {
            thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXBlockZap(
                            xCoord + 0.5f,
                            yCoord + 0.5f,
                            zCoord + 0.5f,
                            (float) t.posX,
                            (float) t.posY + t.height / 2f,
                            (float) t.posZ),
                    new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 32.0));
            t.attackEntityFrom(DamageSource.magic, 4 + worldObj.rand.nextInt(4));
            if (!all) break;
        }
    }

    private void inEvHarm(final boolean all) {
        final List<EntityLivingBase> targets = worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                        .expand(10, 10, 10));
        if (targets == null || targets.isEmpty()) return;
        for (final EntityLivingBase t : targets) {
            if (worldObj.rand.nextBoolean()) {
                t.addPotionEffect(new PotionEffect(Config.potionTaintPoisonID, 120, 0, false));
            } else {
                final PotionEffect pe = new PotionEffect(Config.potionVisExhaustID, 2400, 0, true);
                pe.getCurativeItems().clear();
                t.addPotionEffect(pe);
            }
            if (!all) break;
        }
    }

    private void inEvWarp() {
        final List<EntityPlayer> targets = worldObj.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1)
                        .expand(10, 10, 10));
        if (targets == null || targets.isEmpty()) return;
        final EntityPlayer t = targets.get(worldObj.rand.nextInt(targets.size()));
        if (worldObj.rand.nextFloat() < 0.25f) {
            Thaumcraft.addStickyWarpToPlayer(t, 1);
        } else {
            Thaumcraft.addWarpToPlayer(t, 1 + worldObj.rand.nextInt(5), true);
        }
    }

    private void inEvEjectItem(final int type) {
        for (int q = 0; q < 50 && !pedestals.isEmpty(); ++q) {
            final ChunkCoordinates cc = pedestals.get(worldObj.rand.nextInt(pedestals.size()));
            final TileEntity te = worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
            if (!(te instanceof TilePedestal)) continue;
            final TilePedestal ped = (TilePedestal) te;
            if (ped.getStackInSlot(0) == null) continue;
            if (type < 3 || type == 5) InventoryUtils.dropItems(worldObj, cc.posX, cc.posY, cc.posZ);
            else ped.setInventorySlotContents(0, null);
            if (type == 1 || type == 3) {
                worldObj.setBlock(cc.posX, cc.posY + 1, cc.posZ, ConfigBlocks.blockFluxGoo, 7, 3);
                worldObj.playSoundEffect(cc.posX, cc.posY, cc.posZ, "game.neutral.swim", 0.3f, 1f);
            } else if (type == 2 || type == 4) {
                worldObj.setBlock(cc.posX, cc.posY + 1, cc.posZ, ConfigBlocks.blockFluxGas, 7, 3);
                worldObj.playSoundEffect(cc.posX, cc.posY, cc.posZ, "random.fizz", 0.3f, 1f);
            } else if (type == 5) {
                worldObj.createExplosion(null, cc.posX + 0.5f, cc.posY + 0.5f, cc.posZ + 0.5f, 1f, false);
            }
            worldObj.addBlockEvent(cc.posX, cc.posY, cc.posZ, ConfigBlocks.blockStoneDevice, 11, 0);
            thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXBlockZap(
                            xCoord + 0.5f,
                            yCoord + 0.5f,
                            zCoord + 0.5f,
                            cc.posX + 0.5f,
                            cc.posY + 1.5f,
                            cc.posZ + 0.5f),
                    new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 32.0));
            return;
        }
    }

    // -------------------------------------------------------------------------
    // Craft cycle
    // -------------------------------------------------------------------------

    public void craftCycle() {
        final TileVat vat = getVat();
        final EntityLivingBase entity = (vat != null) ? vat.getEntityContained() : null;

        // instability events
        if (instability > 0 && worldObj.rand.nextInt(500) <= instability) {
            switch (worldObj.rand.nextInt(21)) {
                case 0, 2, 10, 13 -> inEvEjectItem(0);
                case 6, 17 -> inEvEjectItem(1);
                case 1, 11 -> inEvEjectItem(2);
                case 3, 8, 14 -> inEvZap(false);
                case 5, 16 -> inEvHarm(false);
                case 12 -> inEvZap(true);
                case 19 -> inEvEjectItem(3);
                case 7 -> inEvEjectItem(4);
                case 4, 15 -> inEvEjectItem(5);
                case 18 -> inEvHarm(true);
                case 9 -> worldObj.createExplosion(
                        null,
                        xCoord + 0.5f,
                        yCoord + 0.5f,
                        zCoord + 0.5f,
                        1.5f + worldObj.rand.nextFloat(),
                        false);
                case 20 -> inEvWarp();
            }
        }

        // instability damage to subject
        if (instability > 0) {
            float visDrawn = 999f;
            if (!worldObj.isRemote) {
                visDrawn = Math
                        .min(visDrawn, VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, Aspect.EARTH, 100));
                visDrawn = Math
                        .min(visDrawn, VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, Aspect.WATER, 100));
                visDrawn = Math
                        .min(visDrawn, VisNetHandler.drainVis(worldObj, xCoord, yCoord, zCoord, Aspect.ORDER, 100));
            }
            final float dmg = instability / 10f / (5f + visDrawn);
            if (entity != null) {
                entity.setHealth(entity.getHealth() - dmg);
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                if (entity.getHealth() <= 0f) {
                    killSubject();
                    return;
                }
            } else {
                selfInfusionHealth -= dmg;
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                if (selfInfusionHealth <= 0f) {
                    killSubject();
                    return;
                }
            }
        }

        // draw essentia
        if (recipeEssentia.visSize() > 0) {
            for (final Aspect asp : recipeEssentia.getAspects()) {
                if (recipeEssentia.getAmount(asp) > 0) {
                    if (tryDrawAllEssentia()) {
                        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                        markDirty();
                        return;
                    }
                    if (worldObj.rand.nextInt(100 - recipeInstability * 3) == 0) {
                        ++instability;
                    }
                    if (instability > 25) instability = 25;
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    markDirty();
                    break;
                }
            }
            checkSurroundings = true;
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
            return;
        }

        // consume ingredients from pedestals
        if (recipeIngredients != null && !recipeIngredients.isEmpty()) {
            for (int a = 0; a < recipeIngredients.size(); ++a) {
                for (final ChunkCoordinates cc : pedestals) {
                    final TileEntity te = worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
                    if (!(te instanceof TilePedestal)) continue;
                    final TilePedestal ped = (TilePedestal) te;
                    if (ped.getStackInSlot(0) == null) continue;
                    if (!InfusionRecipe.areItemStacksEqual(ped.getStackInSlot(0), recipeIngredients.get(a), true))
                        continue;
                    if (itemCount == 0) {
                        itemCount = 5;
                        PacketHandler.INSTANCE.sendToAllAround(
                                new PacketInfusionFX(
                                        xCoord,
                                        yCoord - 2,
                                        zCoord,
                                        (byte) (xCoord - cc.posX),
                                        (byte) (yCoord - cc.posY - 2),
                                        (byte) (zCoord - cc.posZ),
                                        0),
                                new NetworkRegistry.TargetPoint(
                                        worldObj.provider.dimensionId,
                                        xCoord,
                                        yCoord,
                                        zCoord,
                                        32.0));
                    } else if (itemCount-- <= 1) {
                        final ItemStack container = ped.getStackInSlot(0).getItem()
                                .getContainerItem(ped.getStackInSlot(0));
                        ped.setInventorySlotContents(0, container == null ? null : container.copy());
                        recipeIngredients.remove(a);
                    }
                    worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
                    markDirty();
                    return;
                }
            }
            return;
        }

        // all done
        instability = 0;
        craftingFinish(recipeOutput, recipeOutputLabel);
        recipeOutput = null;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        markDirty();
    }

    // -------------------------------------------------------------------------
    // Kill subject (infusion failure)
    // -------------------------------------------------------------------------

    public void killSubject() {
        final TileVat vat = getVat();
        if (!worldObj.isRemote && vat != null) {
            final EntityLivingBase entity = vat.getEntityContained();
            if (entity != null || recipeType == 1) {
                worldObj.createExplosion(null, xCoord + 0.5, yCoord - 0.5, zCoord + 0.5, 0.5f, false);
                for (int a = 0; a < 25; ++a) {
                    final int xx = xCoord + worldObj.rand.nextInt(8) - worldObj.rand.nextInt(8);
                    final int yy = yCoord + worldObj.rand.nextInt(8) - worldObj.rand.nextInt(8);
                    final int zz = zCoord + worldObj.rand.nextInt(8) - worldObj.rand.nextInt(8);
                    if (worldObj.isAirBlock(xx, yy, zz)) {
                        worldObj.setBlock(
                                xx,
                                yy,
                                zz,
                                yy < yCoord ? ConfigBlocks.blockFluxGoo : ConfigBlocks.blockFluxGas,
                                8,
                                3);
                    }
                }
            }
        }
        onInfusionInterrupted();
        if (vat != null) {
            vat.selfInfusions = new int[12];
            vat.setEntityContained(null);
            vat.mode = 0;
            vat.markDirty();
            worldObj.markBlockForUpdate(vat.xCoord, vat.yCoord, vat.zCoord);
        }
    }

    // -------------------------------------------------------------------------
    // Infusion finish
    // -------------------------------------------------------------------------

    public void craftingFinish(final Object out, final String label) {
        final TileVat vat = getVat();
        if (vat == null) {
            onInfusionInterrupted();
            return;
        }

        if (recipeType == 0) {
            if (out instanceof Integer) {
                EntityLivingBase created = null;
                if ((Integer) out < 0) {
                    created = (EntityLivingBase) net.minecraft.entity.EntityList
                            .createEntityByID(-(Integer) out, worldObj);
                }
                final ModContainer mc = Loader.instance().getIndexedModList().get("ThaumicHorizons");
                try {
                    created = (EntityLivingBase) EntityRegistry.instance().lookupModSpawn(mc, (Integer) out)
                            .getEntityClass().getConstructor(World.class).newInstance(worldObj);
                } catch (InvocationTargetException e) {
                    e.getCause().printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                created.copyLocationAndAnglesFrom(vat.getEntityContained());
                created.copyDataFrom(vat.getEntityContained(), true);
                if (created instanceof IEntityInfusedStats) {
                    ((IEntityInfusedStats) created).resetStats();
                }
                vat.setEntityContained(created);
            } else if (out instanceof NBTBase) {
                final NBTTagCompound tagMods = (NBTTagCompound) out;
                final Multimap<String, net.minecraft.entity.ai.attributes.AttributeModifier> map = HashMultimap
                        .create();
                if (tagMods.getDouble("generic.movementSpeed") > 0.0) {
                    map.put(
                            "generic.movementSpeed",
                            new net.minecraft.entity.ai.attributes.AttributeModifier(
                                    "generic.movementSpeed",
                                    tagMods.getDouble("generic.movementSpeed") / 10.0,
                                    1));
                }
                if (tagMods.getDouble("generic.maxHealth") > 0.0) {
                    map.put(
                            "generic.maxHealth",
                            new net.minecraft.entity.ai.attributes.AttributeModifier(
                                    "generic.maxHealth",
                                    tagMods.getDouble("generic.maxHealth"),
                                    1));
                }
                if (tagMods.getDouble("generic.attackDamage") > 0.0) {
                    map.put(
                            "generic.attackDamage",
                            new net.minecraft.entity.ai.attributes.AttributeModifier(
                                    "generic.attackDamage",
                                    tagMods.getDouble("generic.attackDamage"),
                                    1));
                }
                if (!map.isEmpty()) {
                    vat.getEntityContained().getAttributeMap().applyAttributeModifiers(map);
                }
                final Set<String> keys = (Set<String>) tagMods.func_150296_c();
                for (final String s : keys) {
                    if (!s.startsWith("generic.")) {
                        final EntityInfusionProperties props = (EntityInfusionProperties) vat.getEntityContained()
                                .getExtendedProperties("CreatureInfusion");
                        props.addInfusion(tagMods.getInteger(s));
                        if (tagMods.getInteger(s) == 7) props.setOwner(recipePlayer);
                    }
                }
            }
            ((EntityInfusionProperties) vat.getEntityContained().getExtendedProperties("CreatureInfusion"))
                    .addCost(myEssentiaCost);
            if (vat.getEntityContained() instanceof EntityLiving) {
                ((EntityLiving) vat.getEntityContained()).func_110163_bv();
            }
            vat.mode = 0;
        } else {
            for (int i = 0; i < vat.selfInfusions.length; ++i) {
                if (vat.selfInfusions[i] == 0) {
                    vat.selfInfusions[i] = (Integer) recipeOutput;
                    break;
                }
            }
            vat.mode = 4;
        }

        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXInfusionDone(xCoord, yCoord - 1, zCoord),
                new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, 32.0));

        onInfusionInterrupted();
        vat.markDirty();
        worldObj.markBlockForUpdate(vat.xCoord, vat.yCoord, vat.zCoord);
    }

    // -------------------------------------------------------------------------
    // Interrupt / reset
    // -------------------------------------------------------------------------

    public void onInfusionInterrupted() {
        crafting = false;
        active = false;
        recipeEssentia = new AspectList();
        recipeIngredients = null;
        recipeOutput = null;
        recipePlayer = null;
        recipeOutputLabel = "";
        instability = 0;
        selfInfusionHealth = 20f;
        itemCount = 0;
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        markDirty();
    }

    // -------------------------------------------------------------------------
    // Client-side effects
    // -------------------------------------------------------------------------

    protected void doEffects() {
        if (!crafting) {
            if (craftCount > 0) {
                craftCount -= 2;
                if (craftCount < 0) craftCount = 0;
            }
            return;
        }
        if (craftCount == 0) {
            worldObj.playSound(xCoord, yCoord, zCoord, "thaumcraft:infuserstart", 0.5f, 1f, false);
        } else if (craftCount % 65 == 0) {
            worldObj.playSound(xCoord, yCoord, zCoord, "thaumcraft:infuser", 0.5f, 1f, false);
        }
        ++craftCount;
        Thaumcraft.proxy.blockRunes(
                worldObj,
                xCoord,
                yCoord - 3,
                zCoord,
                0.5f + worldObj.rand.nextFloat() * 0.2f,
                0.1f,
                0.7f + worldObj.rand.nextFloat() * 0.3f,
                25,
                -0.03f);
        if (instability > 0 && worldObj.rand.nextInt(200) <= instability) {
            Thaumcraft.proxy.nodeBolt(
                    worldObj,
                    xCoord + 0.5f,
                    yCoord + 0.5f,
                    zCoord + 0.5f,
                    xCoord + 0.5f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 2f,
                    yCoord + 0.5f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 2f,
                    zCoord + 0.5f + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * 2f);
        }
        for (final String fxk : sourceFX.keySet().toArray(new String[0])) {
            final SourceFX fx = sourceFX.get(fxk);
            if (fx.ticks <= 0) {
                sourceFX.remove(fxk);
            } else {
                if (fx.loc.posX == xCoord && fx.loc.posY == yCoord && fx.loc.posZ == zCoord) {
                    final Entity player = worldObj.getEntityByID(fx.color);
                    if (player != null) {
                        for (int a = 0; a < Thaumcraft.proxy.particleCount(2); ++a) {
                            Thaumcraft.proxy.drawInfusionParticles4(
                                    worldObj,
                                    player.posX
                                            + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * player.width,
                                    player.boundingBox.minY + worldObj.rand.nextFloat() * player.height,
                                    player.posZ
                                            + (worldObj.rand.nextFloat() - worldObj.rand.nextFloat()) * player.width,
                                    xCoord,
                                    yCoord,
                                    zCoord);
                        }
                    }
                } else {
                    final TileEntity tile = worldObj.getTileEntity(fx.loc.posX, fx.loc.posY, fx.loc.posZ);
                    if (tile instanceof TilePedestal) {
                        final ItemStack is = ((TilePedestal) tile).getStackInSlot(0);
                        if (is != null) {
                            if (worldObj.rand.nextInt(3) == 0) {
                                Thaumcraft.proxy.drawInfusionParticles3(
                                        worldObj,
                                        fx.loc.posX + worldObj.rand.nextFloat(),
                                        fx.loc.posY + worldObj.rand.nextFloat() + 1f,
                                        fx.loc.posZ + worldObj.rand.nextFloat(),
                                        xCoord,
                                        yCoord,
                                        zCoord);
                            } else {
                                final Item bi = is.getItem();
                                final int md = is.getItemDamage();
                                if (is.getItemSpriteNumber() == 0 && bi instanceof ItemBlock) {
                                    for (int a = 0; a < Thaumcraft.proxy.particleCount(2); ++a) {
                                        Thaumcraft.proxy.drawInfusionParticles2(
                                                worldObj,
                                                fx.loc.posX + worldObj.rand.nextFloat(),
                                                fx.loc.posY + worldObj.rand.nextFloat() + 1f,
                                                fx.loc.posZ + worldObj.rand.nextFloat(),
                                                xCoord,
                                                yCoord,
                                                zCoord,
                                                Block.getBlockFromItem(bi),
                                                md);
                                    }
                                } else {
                                    for (int a = 0; a < Thaumcraft.proxy.particleCount(2); ++a) {
                                        Thaumcraft.proxy.drawInfusionParticles1(
                                                worldObj,
                                                fx.loc.posX + 0.4f + worldObj.rand.nextFloat() * 0.2f,
                                                fx.loc.posY + 1.23f + worldObj.rand.nextFloat() * 0.2f,
                                                fx.loc.posZ + 0.4f + worldObj.rand.nextFloat() * 0.2f,
                                                xCoord,
                                                yCoord,
                                                zCoord,
                                                bi,
                                                md);
                                    }
                                }
                            }
                        }
                    } else {
                        fx.ticks = 0;
                    }
                }
                --fx.ticks;
                sourceFX.put(fxk, fx);
            }
        }
    }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

    @Override
    public void readCustomNBT(final NBTTagCompound tag) {
        super.readCustomNBT(tag);
        crafting = tag.getBoolean("crafting");
        active = tag.getBoolean("active");
        instability = tag.getShort("instability");
        selfInfusionHealth = tag.getFloat("selfInfusionHealth");
        recipeEssentia = new AspectList();
        final NBTTagList essList = tag.getTagList("recipeEssentia", 10);
        for (int i = 0; i < essList.tagCount(); ++i) {
            final NBTTagCompound e = essList.getCompoundTagAt(i);
            if (e.hasKey("key")) recipeEssentia.add(Aspect.getAspect(e.getString("key")), e.getInteger("amount"));
        }
        recipeType = tag.getInteger("recipetype");
        recipeInstability = tag.getInteger("recipeinst");
        recipePlayer = tag.getString("recipeplayer");
        if (recipePlayer.isEmpty()) recipePlayer = null;
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound tag) {
        super.writeCustomNBT(tag);
        tag.setBoolean("crafting", crafting);
        tag.setBoolean("active", active);
        tag.setShort("instability", (short) instability);
        tag.setFloat("selfInfusionHealth", selfInfusionHealth);
        final NBTTagList essList = new NBTTagList();
        for (final Aspect asp : recipeEssentia.getAspects()) {
            if (asp != null) {
                final NBTTagCompound e = new NBTTagCompound();
                e.setString("key", asp.getTag());
                e.setInteger("amount", recipeEssentia.getAmount(asp));
                essList.appendTag(e);
            }
        }
        tag.setTag("recipeEssentia", essList);
        tag.setInteger("recipetype", recipeType);
        tag.setInteger("recipeinst", recipeInstability);
        tag.setString("recipeplayer", recipePlayer == null ? "" : recipePlayer);
    }

    @Override
    public void readFromNBT(final NBTTagCompound tag) {
        super.readFromNBT(tag);
        recipeIngredients = new ArrayList<>();
        final NBTTagList ingredList = tag.getTagList("recipein", 10);
        for (int i = 0; i < ingredList.tagCount(); ++i) {
            final NBTTagCompound c = ingredList.getCompoundTagAt(i);
            recipeIngredients.add(ItemStack.loadItemStackFromNBT(c));
        }
        final String rot = tag.getString("rotype");
        if (rot.equals("@")) {
            recipeOutput = tag.getInteger("recipeout");
        } else if (!rot.isEmpty()) {
            recipeOutputLabel = rot;
            recipeOutput = tag.getTag("recipeout");
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (recipeIngredients != null && !recipeIngredients.isEmpty()) {
            final NBTTagList ingredList = new NBTTagList();
            int idx = 0;
            for (final ItemStack stack : recipeIngredients) {
                if (stack != null) {
                    final NBTTagCompound c = new NBTTagCompound();
                    c.setByte("item", (byte) idx++);
                    stack.writeToNBT(c);
                    ingredList.appendTag(c);
                }
            }
            tag.setTag("recipein", ingredList);
        }
        if (recipeOutput instanceof Integer) {
            tag.setString("rotype", "@");
            tag.setTag("recipeout", new NBTTagInt((Integer) recipeOutput));
        } else if (recipeOutput instanceof NBTBase) {
            tag.setString("rotype", recipeOutputLabel);
            tag.setTag("recipeout", (NBTBase) recipeOutput);
        }
    }

    // -------------------------------------------------------------------------
    // TileVisNode
    // -------------------------------------------------------------------------

    @Override
    public int getRange() {
        return 8;
    }

    @Override
    public boolean isSource() {
        return false;
    }

    public static class SourceFX {

        public ChunkCoordinates loc;
        public int ticks;
        public int color;

        public SourceFX(final ChunkCoordinates loc, final int ticks, final int color) {
            this.loc = loc;
            this.ticks = ticks;
            this.color = color;
        }
    }
}
