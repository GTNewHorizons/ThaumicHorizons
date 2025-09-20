package com.kentington.thaumichorizons.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class VortexSound extends MovingSound {

    private final TileEntity tile;

    public VortexSound(TileEntity tile) {
        super(new ResourceLocation("thaumichorizons:vortex"));
        this.tile = tile;
        this.repeat = true;
        this.field_147665_h = 0;
        this.volume = 1.0F;
    }

    // Let me know if update needs to be cached or if that's a good idea or not for performance.

    @Override
    public void update() {
        if (tile == null || tile.isInvalid() || tile.getWorldObj() == null) {
            this.donePlaying = true;
            return;
        }

        // follow tile
        this.xPosF = tile.xCoord + 0.5F;
        this.yPosF = tile.yCoord + 0.5F;
        this.zPosF = tile.zCoord + 0.5F;

        // distance-based volume
        EntityPlayer p = Minecraft.getMinecraft().thePlayer;

        if (p != null) {
            double dist = p.getDistance(this.xPosF, this.yPosF, this.zPosF);
            this.volume = Math.max(0.0F, 1.0F / (1.0F + (float) (dist / 8.0F)));
        }
    }

    public void markDone() {
        this.donePlaying = true;
    }

}
