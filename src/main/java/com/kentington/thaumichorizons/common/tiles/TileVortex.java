//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import static com.kentington.thaumichorizons.common.lib.PocketPlaneData.pocketPlaneMAXID;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityGolemTH;
import com.kentington.thaumichorizons.common.lib.PocketPlaneData;
import com.kentington.thaumichorizons.common.lib.PocketPlaneThread;
import com.kentington.thaumichorizons.common.lib.VortexTeleporter;

import cpw.mods.fml.common.FMLCommonHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.items.wands.ItemWandCasting;

public class TileVortex extends TileThaumcraft implements IWandable, IAspectContainer {

    final int MAX_COUNT = 2400;
    public int count;
    public int beams;
    public int dimensionID;
    public int returnID;
    public AspectList aspects;
    boolean ateDevices;
    public boolean collapsing;
    public boolean createdDimension;
    public boolean generating;
    public boolean cheat;
    public ArrayList<ItemStack> items;
    Thread ppThread;

    public TileVortex() {
        this.aspects = new AspectList();
        this.ateDevices = false;
        this.collapsing = false;
        this.createdDimension = false;
        this.generating = false;
        this.cheat = false;
        this.items = new ArrayList<ItemStack>();
        this.ppThread = null;
    }

    public void updateEntity() {
        super.updateEntity();

        if (this.generating) {
            this.worldObj.createExplosion(
                    null,
                    this.xCoord + this.worldObj.rand.nextFloat(),
                    this.yCoord + this.worldObj.rand.nextFloat(),
                    this.zCoord + this.worldObj.rand.nextFloat(),
                    1.0f,
                    false);
            if (this.ppThread == null) {
                this.createDimension(null);
                return;
            }
            if (!this.ppThread.isAlive()) {
                this.generating = false;
                this.createdDimension = true;
                this.markDirty();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        } else {
            if (this.collapsing) {
                ++this.count;
                if (this.count > 25) {
                    if (this.createdDimension) {
                        WorldServer wsd = DimensionManager.getWorld(ThaumicHorizons.dimensionPocketId);
                        if (wsd != null) {
                            wsd.setBlockToAir(0, 129, this.dimensionID * 256);
                        }
                    }
                    this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
                    if (worldObj.isRemote)
                        Thaumcraft.proxy.burst(this.worldObj, this.xCoord, this.yCoord, this.zCoord, 4.0f);
                    BlockAiry.explodify(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
                }
                return;
            }
            if (this.count < 50) {
                ++this.count;
            } else {
                if (!this.ateDevices) {
                    if (!this.cheat && this.worldObj.provider.dimensionId != ThaumicHorizons.dimensionPocketId) {
                        for (int dx = -2; dx <= 2; ++dx) {
                            for (int dy = -2; dy <= 2; ++dy) {
                                for (int dz = -2; dz <= 2; ++dz) {
                                    if (dx != 0 || dy != 0 || dz != 0) {
                                        this.worldObj
                                                .setBlockToAir(this.xCoord + dx, this.yCoord + dy, this.zCoord + dz);
                                        if (worldObj.isRemote) Thaumcraft.proxy.burst(
                                                this.worldObj,
                                                this.xCoord + dx,
                                                this.yCoord + dy,
                                                this.zCoord + dz,
                                                4.0f);
                                    }
                                }
                            }
                        }
                    }
                    this.ateDevices = true;
                }
                if (this.beams < 6 && !this.cheat
                        && this.worldObj.provider.dimensionId != ThaumicHorizons.dimensionPocketId) {
                    this.handleHungryNode();
                } else if (!this.createdDimension && !this.generating) {
                    this.handlePocketPlaneStuff();
                }
                if (!this.cheat) {
                    this.count += 6 - this.beams;
                }
                if (this.count > MAX_COUNT && !this.cheat
                        && this.worldObj.provider.dimensionId != ThaumicHorizons.dimensionPocketId) {
                    this.collapsing = true;
                    this.count = 0;
                }
                if (this.createdDimension) {
                    final List ents = this.worldObj.getEntitiesWithinAABB(
                            EntityPlayerMP.class,
                            AxisAlignedBB.getBoundingBox(
                                    this.xCoord,
                                    this.yCoord,
                                    this.zCoord,
                                    this.xCoord + 1,
                                    this.yCoord + 1,
                                    this.zCoord + 1));
                    if (ents.size() > 0) {
                        for (final Object e : ents) {
                            final EntityPlayerMP player = (EntityPlayerMP) e;
                            if (player.ridingEntity == null && player.riddenByEntity == null) {
                                final MinecraftServer mServer = FMLCommonHandler.instance()
                                        .getMinecraftServerInstance();
                                if (player.timeUntilPortal > 0) {
                                    player.timeUntilPortal = 10;
                                } else if (player.dimension != ThaumicHorizons.dimensionPocketId) {
                                    player.timeUntilPortal = 10;
                                    player.mcServer.getConfigurationManager().transferPlayerToDimension(
                                            player,
                                            ThaumicHorizons.dimensionPocketId,
                                            new VortexTeleporter(
                                                    mServer.worldServerForDimension(ThaumicHorizons.dimensionPocketId),
                                                    this.dimensionID));
                                } else {
                                    player.timeUntilPortal = 10;
                                    player.mcServer.getConfigurationManager().transferPlayerToDimension(
                                            player,
                                            this.returnID,
                                            new VortexTeleporter(
                                                    mServer.worldServerForDimension(this.returnID),
                                                    this.dimensionID));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    void handlePocketPlaneStuff() {
        final List ents = this.worldObj.getEntitiesWithinAABB(
                Entity.class,
                AxisAlignedBB.getBoundingBox(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this.xCoord + 1,
                        this.yCoord + 1,
                        this.zCoord + 1).expand(1.0, 1.0, 1.0));
        if (ents != null && ents.size() > 0) {
            for (final Object ent : ents) {
                final Entity eo = (Entity) ent;
                if (eo instanceof final EntityItem item) {
                    if (ThaumicHorizons.enablePocket && item.getEntityItem().getItem() == ConfigItems.itemEldritchObject
                            && item.getEntityItem().getItemDamage() == 3) {
                        this.createDimension(item);
                        item.setDead();
                    } else {
                        this.handleVoidCrafting(item);
                    }
                }
            }
        }
    }

    void handleVoidCrafting(final EntityItem item) {
        if (item.getEntityItem().getItem() == ConfigItems.itemResource && item.getEntityItem().getItemDamage() == 16) {
            this.items.add(new ItemStack(ThaumicHorizons.itemVoidPutty, item.getEntityItem().stackSize));
            item.setDead();
        } else if (item.getEntityItem().getItem() == ConfigItems.itemResource
                && item.getEntityItem().getItemDamage() == 14) {
                    for (int i = 0; i < item.getEntityItem().stackSize; ++i) {
                        this.spawnWisps();
                    }
                    item.setDead();
                } else
            if (item.getEntityItem().getItem() == ThaumicHorizons.itemCrystalWand) {
                final ItemStack theWand = new ItemStack(ThaumicHorizons.itemWandCastingDisposable);
                ((ItemWandCasting) theWand.getItem()).setRod(theWand, ThaumicHorizons.ROD_CRYSTAL);
                ((ItemWandCasting) theWand.getItem()).setCap(theWand, ThaumicHorizons.CAP_CRYSTAL);
                ((ItemWandCasting) theWand.getItem()).storeVis(theWand, Aspect.EARTH, 25000);
                ((ItemWandCasting) theWand.getItem()).storeVis(theWand, Aspect.AIR, 25000);
                ((ItemWandCasting) theWand.getItem()).storeVis(theWand, Aspect.FIRE, 25000);
                ((ItemWandCasting) theWand.getItem()).storeVis(theWand, Aspect.WATER, 25000);
                ((ItemWandCasting) theWand.getItem()).storeVis(theWand, Aspect.ORDER, 25000);
                ((ItemWandCasting) theWand.getItem()).storeVis(theWand, Aspect.ENTROPY, 25000);
                this.items.add(theWand);
                item.setDead();
            } else
                if (item.getEntityItem().getItem() == ThaumicHorizons.itemGolemPowder && item.func_145800_j() != null) {
                    if (!this.worldObj.isRemote) {
                        for (int i = 0; i < item.getEntityItem().stackSize; ++i) {
                            final EntityGolemTH golem = new EntityGolemTH(this.worldObj);
                            golem.setOwner(item.func_145800_j());
                            golem.loadGolem(
                                    this.xCoord + 0.5,
                                    this.yCoord,
                                    this.zCoord + 0.5,
                                    null,
                                    0,
                                    -420,
                                    false,
                                    false,
                                    false);
                            this.worldObj.playSoundEffect(
                                    this.xCoord + 0.5,
                                    this.yCoord + 0.5,
                                    this.zCoord + 0.5,
                                    "thaumcraft:wand",
                                    1.0f,
                                    1.0f);
                            golem.setHomeArea((int) golem.posX, (int) golem.posY, (int) golem.posZ, 32);
                            this.worldObj.spawnEntityInWorld(golem);
                            this.worldObj.setEntityState(golem, (byte) 7);
                        }
                    }
                    item.setDead();
                }
    }

    void spawnWisps() {
        if (this.worldObj.isRemote) {
            return;
        }
        for (int wisps = this.worldObj.rand.nextInt(4) + 1, i = 0; i < wisps; ++i) {
            final EntityWisp wisp = new EntityWisp(this.worldObj);
            wisp.setPosition(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
            if (this.aspects.size() > 0 && this.aspects.getAspects()[0] != null) {
                wisp.setType(this.aspects.getAspects()[this.worldObj.rand.nextInt(this.aspects.size())].getTag());
            }
            this.worldObj.spawnEntityInWorld(wisp);
        }
    }

    void createDimension(final EntityItem pearl) {
        final MinecraftServer server = MinecraftServer.getServer();
        if (server == null) return;
        final PocketPlaneData data = new PocketPlaneData();
        String name = "";
        if (pearl != null && pearl.getEntityItem().hasTagCompound()) {
            name = pearl.getEntityItem().getDisplayName();
        } else if (pearl != null) {
            name = pearl.func_145800_j() + StatCollector.translateToLocal("thaumichorizons.pocketplane");
        }
        data.name = name;
        this.dimensionID = pocketPlaneMAXID;
        if (DimensionManager.getWorld(ThaumicHorizons.dimensionPocketId) == null) {
            DimensionManager.initDimension(ThaumicHorizons.dimensionPocketId);
        }
        this.generating = true;
        if (!this.worldObj.isRemote) {
            this.returnID = this.worldObj.provider.dimensionId;
            (this.ppThread = new Thread(
                    new PocketPlaneThread(
                            data,
                            this.aspects,
                            (World) MinecraftServer.getServer()
                                    .worldServerForDimension(ThaumicHorizons.dimensionPocketId),
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            this.returnID))).run();
        }
        this.markDirty();
    }

    void handleHungryNode() {
        if (this.worldObj.isRemote && this.beams < 6) {
            for (int a = 0; a < Thaumcraft.proxy.particleCount(1); ++a) {
                int tx = this.xCoord + this.worldObj.rand.nextInt(16) - this.worldObj.rand.nextInt(16);
                int ty = this.yCoord + this.worldObj.rand.nextInt(16) - this.worldObj.rand.nextInt(16);
                int tz = this.zCoord + this.worldObj.rand.nextInt(16) - this.worldObj.rand.nextInt(16);
                if (ty > this.worldObj.getHeightValue(tx, tz)) {
                    ty = this.worldObj.getHeightValue(tx, tz);
                }
                final Vec3 v1 = Vec3.createVectorHelper(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
                final Vec3 v2 = Vec3.createVectorHelper(tx + 0.5, ty + 0.5, tz + 0.5);
                final MovingObjectPosition mop = ThaumcraftApiHelper
                        .rayTraceIgnoringSource(this.worldObj, v1, v2, true, false, false);
                if (mop != null && this.getDistanceFrom(mop.blockX, mop.blockY, mop.blockZ) < 256.0) {
                    tx = mop.blockX;
                    ty = mop.blockY;
                    tz = mop.blockZ;
                    final Block bi = this.worldObj.getBlock(tx, ty, tz);
                    final int md = this.worldObj.getBlockMetadata(tx, ty, tz);
                    if (!bi.isAir(this.worldObj, tx, ty, tz)) {
                        Thaumcraft.proxy
                                .hungryNodeFX(this.worldObj, tx, ty, tz, this.xCoord, this.yCoord, this.zCoord, bi, md);
                    }
                }
            }
        }
        final List ents = this.worldObj.getEntitiesWithinAABB(
                Entity.class,
                AxisAlignedBB.getBoundingBox(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this.xCoord + 1,
                        this.yCoord + 1,
                        this.zCoord + 1).expand(15.0, 15.0, 15.0));
        if (ents != null && ents.size() > 0) {
            for (final Object ent : ents) {
                final Entity eo = (Entity) ent;
                if (!(eo instanceof EntityPlayer) || !((EntityPlayer) eo).capabilities.disableDamage) {
                    if (eo.isEntityAlive() && !eo.isEntityInvulnerable()) {
                        final double d = this.getDistanceTo(eo.posX, eo.posY, eo.posZ);
                        if (d < 2.0) {
                            if (eo instanceof EntityFallingBlock) {
                                eo.setDead();
                            } else {
                                eo.attackEntityFrom(DamageSource.outOfWorld, 3.0f - this.beams / 2.0f);
                            }
                        }
                    }
                    final double var3 = (this.xCoord + 0.5 - eo.posX) / 15.0;
                    final double var4 = (this.yCoord + 0.5 - eo.posY) / 15.0;
                    final double var5 = (this.zCoord + 0.5 - eo.posZ) / 15.0;
                    final double var6 = Math.sqrt(var3 * var3 + var4 * var4 + var5 * var5);
                    double var7 = 1.0 - var6;
                    final double modifier = 2.0 - this.beams / 3.0;
                    if (var7 <= 0.0) {
                        continue;
                    }
                    var7 *= var7;
                    eo.motionX += var3 / var6 * var7 * 0.15 * modifier;
                    eo.motionY += var4 / var6 * var7 * 0.25 * modifier;
                    eo.motionZ += var5 / var6 * var7 * 0.15 * modifier;
                }
            }
        }
        for (int i = 0; i < 3; ++i) {
            if (!this.worldObj.isRemote && (this.beams <= 0 || this.worldObj.rand.nextInt(this.beams) == 0)) {
                int tx2 = this.xCoord + this.worldObj.rand.nextInt(16) - this.worldObj.rand.nextInt(16);
                int ty2 = this.yCoord + this.worldObj.rand.nextInt(16) - this.worldObj.rand.nextInt(16);
                int tz2 = this.zCoord + this.worldObj.rand.nextInt(16) - this.worldObj.rand.nextInt(16);
                if (ty2 > this.worldObj.getHeightValue(tx2, tz2)) {
                    ty2 = this.worldObj.getHeightValue(tx2, tz2);
                }
                final Vec3 v3 = Vec3.createVectorHelper(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
                final Vec3 v4 = Vec3.createVectorHelper(tx2 + 0.5, ty2 + 0.5, tz2 + 0.5);
                final MovingObjectPosition mop2 = ThaumcraftApiHelper
                        .rayTraceIgnoringSource(this.worldObj, v3, v4, true, false, false);
                if (mop2 != null && this.getDistanceFrom(mop2.blockX, mop2.blockY, mop2.blockZ) < 256.0) {
                    tx2 = mop2.blockX;
                    ty2 = mop2.blockY;
                    tz2 = mop2.blockZ;
                    final Block bi2 = this.worldObj.getBlock(tx2, ty2, tz2);
                    final int md2 = this.worldObj.getBlockMetadata(tx2, ty2, tz2);
                    if (!bi2.isAir(this.worldObj, tx2, ty2, tz2)) {
                        final float h = bi2.getBlockHardness(this.worldObj, tx2, ty2, tz2);
                        if (h >= 0.0f && h < 10.0f) {
                            this.worldObj.func_147480_a(tx2, ty2, tz2, true);
                        }
                    }
                }
            }
        }
    }

    public double getDistanceTo(final double par1, final double par3, final double par5) {
        final double var7 = this.xCoord + 0.5 - par1;
        final double var8 = this.yCoord + 0.5 - par3;
        final double var9 = this.zCoord + 0.5 - par5;
        return var7 * var7 + var8 * var8 + var9 * var9;
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setInteger("count", this.count);
        nbttagcompound.setInteger("beams", this.beams);
        nbttagcompound.setInteger("dimensionID", this.dimensionID);
        nbttagcompound.setInteger("returnID", this.returnID);
        nbttagcompound.setBoolean("ateDevices", this.ateDevices);
        nbttagcompound.setBoolean("collapsing", this.collapsing);
        nbttagcompound.setBoolean("createdDimension", this.createdDimension);
        nbttagcompound.setBoolean("isGenerating", this.generating);
        nbttagcompound.setBoolean("cheat", this.cheat);
        final NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("aspects", tlist);
        for (final Aspect aspect : this.aspects.getAspects()) {
            if (aspect != null) {
                final NBTTagCompound f = new NBTTagCompound();
                f.setString("key", aspect.getTag());
                f.setInteger("amount", this.aspects.getAmount(aspect));
                tlist.appendTag(f);
            }
        }
        final NBTTagList itemz = new NBTTagList();
        for (final ItemStack item : this.items) {
            final NBTTagCompound itemTag = new NBTTagCompound();
            item.writeToNBT(itemTag);
            itemz.appendTag(itemTag);
        }
        nbttagcompound.setTag("items", itemz);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.count = nbttagcompound.getInteger("count");
        this.beams = nbttagcompound.getInteger("beams");
        this.dimensionID = nbttagcompound.getInteger("dimensionID");
        this.returnID = nbttagcompound.getInteger("returnID");
        this.ateDevices = nbttagcompound.getBoolean("ateDevices");
        this.collapsing = nbttagcompound.getBoolean("collapsing");
        this.createdDimension = nbttagcompound.getBoolean("createdDimension");
        this.generating = nbttagcompound.getBoolean("isGenerating");
        this.cheat = nbttagcompound.getBoolean("cheat");
        final AspectList al = new AspectList();
        final NBTTagList tlist = nbttagcompound.getTagList("aspects", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            final NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (rs.hasKey("key")) {
                al.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
            }
        }
        this.aspects = al.copy();
        this.items.clear();
        final NBTTagList itemz = nbttagcompound.getTagList("items", 10);
        for (int i = 0; i < itemz.tagCount(); ++i) {
            final ItemStack item = ItemStack.loadItemStackFromNBT(itemz.getCompoundTagAt(i));
            this.items.add(item);
        }
    }

    @Override
    public int onWandRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final int x,
            final int y, final int z, final int side, final int md) {
        if (!this.worldObj.isRemote && this.items.size() > 0) {
            final ItemStack item = this.items.get(0);
            final EntityItem key = new EntityItem(this.worldObj);
            key.setEntityItemStack(item);
            key.setPosition(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
            this.worldObj.spawnEntityInWorld(key);
            this.items.remove(0);
            player.worldObj.playSound(
                    x + 0.5,
                    y + 0.5,
                    z + 0.5,
                    "thaumcraft:wand",
                    0.5f,
                    0.9f + player.worldObj.rand.nextFloat() * 0.2f,
                    false);
            player.swingItem();
            this.markDirty();
        }
        player.worldObj.playSound(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                "thaumcraft:wand",
                0.5f,
                0.9f + player.worldObj.rand.nextFloat() * 0.2f,
                false);
        player.swingItem();
        this.markDirty();
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(final World world, final ItemStack wandstack, final EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(final ItemStack wandstack, final EntityPlayer player, final int count) {}

    @Override
    public void onWandStoppedUsing(final ItemStack wandstack, final World world, final EntityPlayer player,
            final int count) {}

    @Override
    public AspectList getAspects() {
        if (this.aspects.getAspects()[0] != null) {
            return this.aspects;
        }
        return new AspectList();
    }

    @Override
    public void setAspects(final AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return false;
    }

    @Override
    public int addToContainer(final Aspect tag, final int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(final Aspect tag) {
        return 0;
    }
}
