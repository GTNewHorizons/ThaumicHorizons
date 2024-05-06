//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import thaumcraft.client.lib.UtilsFX;

public class FXSonic extends EntityFX {

    float yaw;
    float pitch;
    public static IModelCustom model;
    private static final ResourceLocation MODEL;

    public FXSonic(final World world, final double d, final double d1, final double d2, final int age, final int dir) {
        super(world, d, d1, d2, 0.0, 0.0, 0.0);
        this.yaw = 0.0f;
        this.pitch = 0.0f;
        this.particleRed = 0.0f;
        this.particleGreen = 1.0f;
        this.particleBlue = 1.0f;
        this.particleGravity = 0.0f;
        this.motionZ = 0.0;
        this.motionY = 0.0;
        this.motionX = 0.0;
        this.particleMaxAge = age + this.rand.nextInt(age / 2);
        this.noClip = false;
        this.setSize(0.01f, 0.01f);
        this.noClip = true;
        this.particleScale = 1.0f;
        switch (dir) {
            case 0 -> {
                this.pitch = 90.0f;
            }
            case 1 -> {
                this.pitch = -90.0f;
            }
            case 2 -> {
                this.yaw = 180.0f;
            }
            case 3 -> {
                this.yaw = 0.0f;
            }
            case 4 -> {
                this.yaw = 90.0f;
            }
            case 5 -> {
                this.yaw = 270.0f;
            }
        }
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
    }

    public void renderParticle(final Tessellator tessellator, final float f, final float f1, final float f2,
            final float f3, final float f4, final float f5) {
        tessellator.draw();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        if (model == null) {
            model = AdvancedModelLoader.loadModel(FXSonic.MODEL);
        }
        final float fade = (this.particleAge + f) / this.particleMaxAge;
        final float xx = (float) (this.prevPosX + (this.posX - this.prevPosX) * f - FXSonic.interpPosX);
        final float yy = (float) (this.prevPosY + (this.posY - this.prevPosY) * f - FXSonic.interpPosY);
        final float zz = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * f - FXSonic.interpPosZ);
        GL11.glTranslated(xx, yy, zz);
        float b = 1.0f;
        final int frame = Math.min(15, (int) (14.0f * fade) + 1);
        UtilsFX.bindTexture("textures/models/ripple" + frame + ".png");
        b = 0.5f;
        final int i = 220;
        final int j = i % 65536;
        final int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
        GL11.glRotatef(-this.yaw, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(this.pitch, 1.0f, 0.0f, 0.0f);
        GL11.glScaled(0.5, 0.5, -0.5);
        GL11.glColor4f(0.0f, b, b, 1.0f);
        model.renderAll();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glPopMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(UtilsFX.getParticleTexture());
        tessellator.startDrawingQuads();
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setDead();
        }
    }

    static {
        MODEL = new ResourceLocation("thaumcraft", "textures/models/hemis.obj");
    }
}
