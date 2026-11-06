//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.Thaumcraft;

public class TileSoulBeacon extends TileThaumcraft {

    @SideOnly(Side.CLIENT)
    private float field_146014_j;

    @SideOnly(Side.CLIENT)
    private long field_146016_i;

    public boolean activate(final EntityPlayer p) {
        p.getEntityData().setInteger("soulBeaconDim", this.worldObj.provider.dimensionId);
        p.getEntityData().setIntArray("soulBeaconCoords", new int[] { this.xCoord, this.yCoord, this.zCoord });
        p.addChatMessage(
                new ChatComponentText(
                        EnumChatFormatting.ITALIC + ""
                                + EnumChatFormatting.GRAY
                                + StatCollector.translateToLocal("thaumichorizons.setBeacon")));
        Thaumcraft.proxy.arcLightning(
                this.worldObj,
                p.posX,
                p.posY + p.getEyeHeight(),
                p.posZ,
                this.xCoord + 0.5,
                this.yCoord + 0.75,
                this.zCoord + 0.5,
                0.05f,
                1.0f,
                0.05f,
                0.5f);
        this.worldObj.playSoundEffect(
                this.xCoord + 0.5,
                this.yCoord + 0.75,
                this.zCoord + 0.5,
                "thaumcraft:zap",
                1.0f,
                1.0f);
        return true;
    }

    @SideOnly(Side.CLIENT)
    public float func_146002_i() {
        final int i = (int) (this.worldObj.getTotalWorldTime() - this.field_146016_i);
        this.field_146016_i = this.worldObj.getTotalWorldTime();
        if (i > 1) {
            this.field_146014_j -= i / 40.0f;
            if (this.field_146014_j < 0.0f) {
                this.field_146014_j = 0.0f;
            }
        }
        this.field_146014_j += 0.025f;
        if (this.field_146014_j > 1.0f) {
            this.field_146014_j = 1.0f;
        }
        return this.field_146014_j;
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return TileSoulBeacon.INFINITE_EXTENT_AABB;
    }
}
