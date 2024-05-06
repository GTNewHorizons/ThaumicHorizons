//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.entity;

import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderCow;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.entities.EntityWizardCow;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.tile.TileNodeRenderer;

public class RenderWizardCow extends RenderCow {

    public static HashMap<String, AspectList> cowAspects;
    public static HashMap<String, NodeType> cowTypes;
    public static HashMap<String, NodeModifier> cowMods;

    public RenderWizardCow(final ModelBase p_i1253_1_, final float p_i1253_2_) {
        super(p_i1253_1_, p_i1253_2_);
    }

    public void doRender(final EntityWizardCow p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
        super.doRender(p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (RenderWizardCow.cowAspects.containsKey(p_76986_1_.getUniqueID().toString())) {
            this.renderMyNode(
                    p_76986_1_.posX,
                    p_76986_1_.posY + p_76986_1_.height / 2.0f,
                    p_76986_1_.posZ,
                    Minecraft.getMinecraft().renderViewEntity,
                    32.0,
                    true,
                    true,
                    1.0f,
                    p_76986_9_,
                    RenderWizardCow.cowAspects.get(p_76986_1_.getUniqueID().toString()),
                    RenderWizardCow.cowTypes.get(p_76986_1_.getUniqueID().toString()),
                    RenderWizardCow.cowMods.get(p_76986_1_.getUniqueID().toString()));
        }
        GL11.glDisable(GL11.GL_BLEND);
    }

    public void renderMyNode(final double x, final double y, final double z, final EntityLivingBase viewer,
            final double viewDistance, final boolean visible, final boolean depthIgnore, final float size,
            final float partialTicks, final AspectList aspects, final NodeType type, final NodeModifier mod) {
        final long nt = System.nanoTime();
        UtilsFX.bindTexture(TileNodeRenderer.nodetex);
        final int frames = 32;
        if (aspects.size() > 0 && visible) {
            final double distance = viewer.getDistance(x, y, z);
            if (distance > viewDistance) {
                return;
            }
            float alpha = (float) ((viewDistance - distance) / viewDistance);
            if (mod != null) {
                switch (mod) {
                    case BRIGHT -> {
                        alpha *= 1.5f;
                    }
                    case PALE -> {
                        alpha *= 0.66f;
                    }
                    case FADING -> {
                        alpha *= MathHelper.sin(viewer.ticksExisted / 3.0f) * 0.25f + 0.33f;
                    }
                }
            }
            GL11.glPushMatrix();
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
            GL11.glDepthMask(false);
            if (depthIgnore) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            final long time = nt / 5000000L;
            final float bscale = 0.25f;
            GL11.glPushMatrix();
            final float rad = ((float) Math.PI * 2F);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            int i = (int) ((nt / 40000000L + x) % frames);
            int count = 0;
            float scale = 0.0f;
            float angle = 0.0f;
            float average = 0.0f;
            for (final Aspect aspect : aspects.getAspects()) {
                if (aspect.getBlend() == 771) {
                    alpha *= 1.5;
                }
                average += aspects.getAmount(aspect);
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, aspect.getBlend());
                scale = MathHelper.sin(viewer.ticksExisted / (14.0f - count)) * bscale + bscale * 2.0f;
                scale = 0.2f + scale * (aspects.getAmount(aspect) / 50.0f);
                scale *= size;
                angle = time % (5000 + 500 * count) / (5000.0f + 500 * count) * rad;
                UtilsFX.renderFacingStrip(
                        x,
                        y,
                        z,
                        angle,
                        scale,
                        alpha / Math.max(1.0f, aspects.size() / 2.0f),
                        frames,
                        0,
                        i,
                        partialTicks,
                        aspect.getColor());
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                ++count;
                if (aspect.getBlend() == 771) {
                    alpha /= 1.5;
                }
            }
            average /= aspects.size();
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            i = (int) ((nt / 40000000L + x) % frames);
            scale = 0.1f + average / 150.0f;
            scale *= size;
            int strip = 1;
            switch (type) {
                case NORMAL -> {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                }
                case UNSTABLE -> {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    strip = 6;
                    angle = 0.0f;
                }
                case DARK -> {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    strip = 2;
                }
                case TAINTED -> {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    strip = 5;
                }
                case HUNGRY -> {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    strip = 4;
                }
                case PURE -> {
                    scale *= 0.75f;
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                    strip = 3;
                }
            }
            GL11.glColor4f(1.0f, 0.0f, 1.0f, alpha);
            UtilsFX.renderFacingStrip(x, y, z, angle, scale, alpha, frames, strip, i, partialTicks, 16777215);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (depthIgnore) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            GL11.glDepthMask(true);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            GL11.glPopMatrix();
        } else {
            GL11.glPushMatrix();
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            GL11.glDepthMask(false);
            final int j = (int) ((nt / 40000000L + x) % frames);
            GL11.glColor4f(1.0f, 0.0f, 1.0f, 0.1f);
            UtilsFX.renderFacingStrip(x, y, z, 0.0f, 0.5f, 0.1f, frames, 1, j, partialTicks, 16777215);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            GL11.glPopMatrix();
        }
    }

    public void doRender(final Entity p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        this.doRender((EntityWizardCow) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRender(final EntityLiving p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        this.doRender((EntityWizardCow) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    public void doRender(final EntityLivingBase p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        this.doRender((EntityWizardCow) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    static {
        RenderWizardCow.cowAspects = new HashMap<>();
        RenderWizardCow.cowTypes = new HashMap<>();
        RenderWizardCow.cowMods = new HashMap<>();
    }
}
