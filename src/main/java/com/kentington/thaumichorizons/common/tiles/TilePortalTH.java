//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import java.util.List;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.Thaumcraft;

public class TilePortalTH extends TileThaumcraft {

    public int opencount;
    private int count;
    public int dimension;
    public int pocket;

    public TilePortalTH() {
        this.opencount = -1;
        this.count = 0;
        this.dimension = 0;
        this.pocket = -1;
    }

    public boolean canUpdate() {
        return true;
    }

    public double getMaxRenderDistanceSquared() {
        return 9216.0;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB.getBoundingBox(
                this.xCoord - 1,
                this.yCoord - 1,
                this.zCoord - 1,
                this.xCoord + 2,
                this.yCoord + 2,
                this.zCoord + 2);
    }

    public void updateEntity() {
        ++this.count;
        if (this.worldObj.isRemote && (this.count % 250 == 0 || this.count == 0)) {
            this.worldObj.playSound(
                    this.xCoord + 0.5,
                    this.yCoord + 0.5,
                    this.zCoord + 0.5,
                    "thaumcraft:evilportal",
                    1.0f,
                    1.0f,
                    false);
        }
        if (this.worldObj.isRemote && this.opencount < 30) {
            ++this.opencount;
        }
        if (!this.worldObj.isRemote && this.count % 5 == 0) {
            final List ents = this.worldObj.getEntitiesWithinAABB(
                    EntityPlayerMP.class,
                    AxisAlignedBB.getBoundingBox(
                            this.xCoord,
                            this.yCoord,
                            this.zCoord,
                            this.xCoord + 1,
                            this.yCoord + 1,
                            this.zCoord + 1).expand(0.5, 1.0, 0.5));
            if (ents.size() > 0) {
                for (final Object e : ents) {
                    final EntityPlayerMP player = (EntityPlayerMP) e;
                    if (player.ridingEntity == null && player.riddenByEntity == null) {
                        final MinecraftServer mServer = FMLCommonHandler.instance().getMinecraftServerInstance();
                        if (player.timeUntilPortal > 0) {
                            continue;
                        }
                        player.timeUntilPortal = 50;
                        final int oldDim = player.dimension;
                    }
                }
            }
        }

        if (this.count > 250) {
            this.worldObj.playSound(
                    this.xCoord + 0.5,
                    this.yCoord + 0.5,
                    this.zCoord + 0.5,
                    "thaumcraft:craftfail",
                    1.0f,
                    1.0f,
                    false);
            Thaumcraft.proxy
                    .burst(this.getWorldObj(), this.xCoord + 0.5f, this.yCoord + 0.5f, this.zCoord + 0.5f, 3.0f);
            this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setInteger("dimension", this.dimension);
        nbttagcompound.setInteger("count", this.count);
        nbttagcompound.setInteger("pocket", this.pocket);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.dimension = nbttagcompound.getInteger("dimension");
        this.count = nbttagcompound.getInteger("count");
        this.pocket = nbttagcompound.getInteger("pocket");
    }
}
