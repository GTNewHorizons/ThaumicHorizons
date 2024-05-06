//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.entities.EntityTaintPig;

import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.TileNodeRenderer;

public class RenderTaintfeeder extends RenderPig {

    private static final ResourceLocation pigTextures;
    private static final ResourceLocation pigEyesTextures;

    public RenderTaintfeeder(final ModelBase p_i1265_1_, final ModelBase p_i1265_2_, final float p_i1265_3_) {
        super(p_i1265_1_, p_i1265_2_, p_i1265_3_);
    }

    protected ResourceLocation getEntityTexture(final EntityPig p_110775_1_) {
        return RenderTaintfeeder.pigTextures;
    }

    public void doRender(final EntityTaintPig p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
        GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_CULL_FACE);
        final int i = p_76986_1_.ticksExisted % 32;
        UtilsFX.bindTexture(TileNodeRenderer.nodetex);
        UtilsFX.renderFacingStrip(
                p_76986_1_.posX,
                p_76986_1_.posY + p_76986_1_.height / 1.75,
                p_76986_1_.posZ,
                0.0f,
                2.0f,
                1.0f,
                32,
                6,
                i,
                p_76986_9_,
                11197951);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glPopMatrix();
    }

    public void doRender(final Entity p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        this.doRender((EntityTaintPig) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRender(final EntityLiving p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        this.doRender((EntityTaintPig) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRender(final EntityLivingBase p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        this.doRender((EntityTaintPig) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    protected int shouldRenderPass(final EntityLivingBase p_77032_1_, final int p_77032_2_, final float p_77032_3_) {
        return this.shouldRenderPass((EntityTaintPig) p_77032_1_, p_77032_2_, p_77032_3_);
    }

    protected int shouldRenderPass(final EntityTaintPig p_77032_1_, final int p_77032_2_, final float p_77032_3_) {
        if (p_77032_2_ != 0) {
            return -1;
        }
        final float f = (float) Math.sin(Math.toRadians(p_77032_1_.ticksExisted % 45) * 8.0);
        this.bindTexture(RenderTaintfeeder.pigEyesTextures);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDepthMask(!p_77032_1_.isInvisible());
        final char c0 = '\uf0f0';
        final int j = c0 % 65536;
        final int k = c0 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.32f + 0.32f * f);
        return 1;
    }

    static {
        pigTextures = new ResourceLocation("thaumichorizons", "textures/entity/taintfeeder.png");
        pigEyesTextures = new ResourceLocation("thaumichorizons", "textures/entity/taintfeeder_eyes.png");
    }
}
