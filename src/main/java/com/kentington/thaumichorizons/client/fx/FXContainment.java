//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.fx;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import thaumcraft.client.lib.UtilsFX;

public class FXContainment extends EntityFX {

    public static final ResourceLocation portaltex;

    public FXContainment(final World par1World, final double par2, final double par4, final double par6) {
        super(par1World, par2, par4, par6, 0.0, 0.0, 0.0);
        final float particleRed = 0.6f;
        this.particleBlue = particleRed;
        this.particleGreen = particleRed;
        this.particleRed = particleRed;
        this.particleAlpha = 0.5f;
        this.particleScale = 1.0f;
        this.particleMaxAge = 40;
        this.motionZ = 0.0;
        this.motionY = 0.0;
        this.motionX = 0.0;
        this.particleRed = 1.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGravity = 0.0f;
        this.noClip = true;
    }

    public void renderParticle(final Tessellator p_70539_1_, final float p_70539_2_, final float p_70539_3_,
            final float p_70539_4_, final float p_70539_5_, final float p_70539_6_, final float p_70539_7_) {
        final long nt = System.nanoTime();
        final long time = nt / 50000000L;
        final int frame = (int) time % 16;
        final float f2 = frame / 16.0f;
        final float f3 = f2 + 0.0625f;
        final float f4 = 0.0f;
        final float f5 = 1.0f;
        UtilsFX.bindTexture(FXContainment.portaltex);
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        final float f6 = 3.0f * this.particleScale;
        final float f7 = (float) (this.posX - FXContainment.interpPosX);
        final float f8 = (float) (this.posY - FXContainment.interpPosY);
        final float f9 = (float) (this.posZ - FXContainment.interpPosZ);
        p_70539_1_.setColorRGBA_F(this.particleRed, this.particleGreen, this.particleBlue, 0.1f);
        p_70539_1_.addVertexWithUV(
                f7 - p_70539_3_ * f6 - p_70539_6_ * f6,
                f8 - p_70539_4_ * f6,
                f9 - p_70539_5_ * f6 - p_70539_7_ * f6,
                f2,
                f4);
        p_70539_1_.addVertexWithUV(
                f7 - p_70539_3_ * f6 + p_70539_6_ * f6,
                f8 + p_70539_4_ * f6,
                f9 - p_70539_5_ * f6 + p_70539_7_ * f6,
                f3,
                f4);
        p_70539_1_.addVertexWithUV(
                f7 + p_70539_3_ * f6 + p_70539_6_ * f6,
                f8 + p_70539_4_ * f6,
                f9 + p_70539_5_ * f6 + p_70539_7_ * f6,
                f3,
                f5);
        p_70539_1_.addVertexWithUV(
                f7 + p_70539_3_ * f6 - p_70539_6_ * f6,
                f8 - p_70539_4_ * f6,
                f9 + p_70539_5_ * f6 - p_70539_7_ * f6,
                f2,
                f5);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public int getFXLayer() {
        return 1;
    }

    public void onUpdate() {
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
            return;
        }
        this.particleScale /= 1.15f;
    }

    static {
        portaltex = new ResourceLocation("thaumcraft", "textures/misc/eldritch_portal.png");
    }
}
