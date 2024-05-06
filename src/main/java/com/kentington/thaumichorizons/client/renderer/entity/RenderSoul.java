//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.entity;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import thaumcraft.client.lib.UtilsFX;

public class RenderSoul extends Render {

    int size1;
    int size2;

    public RenderSoul() {
        this.size1 = 0;
        this.size2 = 0;
        this.shadowSize = 0.0f;
    }

    public void renderEntityAt(final Entity entity, final double x, final double y, final double z, final float fq,
            final float pticks) {
        if (((EntityLiving) entity).getHealth() <= 0.0f) {
            return;
        }
        final float f1 = ActiveRenderInfo.rotationX;
        final float f2 = ActiveRenderInfo.rotationXZ;
        final float f3 = ActiveRenderInfo.rotationZ;
        final float f4 = ActiveRenderInfo.rotationYZ;
        final float f5 = ActiveRenderInfo.rotationXY;
        final float f6 = 0.25f;
        final float f7 = (float) x;
        final float f8 = (float) y;
        final float f9 = (float) z;
        final Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        UtilsFX.bindTexture("thaumichorizons", "textures/misc/soul.png");
        final int i = entity.ticksExisted % 16;
        final float x2 = i / this.size1;
        final float x3 = (i + 1) / this.size1;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(240);
        tessellator.addVertexWithUV(f7 - f1 * f6 - f4 * f6, f8 - f2 * f6, f9 - f3 * f6 - f5 * f6, x3, 0.0);
        tessellator.addVertexWithUV(f7 - f1 * f6 + f4 * f6, f8 + f2 * f6, f9 - f3 * f6 + f5 * f6, x3, 1.0);
        tessellator.addVertexWithUV(f7 + f1 * f6 + f4 * f6, f8 + f2 * f6, f9 + f3 * f6 + f5 * f6, x2, 1.0);
        tessellator.addVertexWithUV(f7 + f1 * f6 - f4 * f6, f8 - f2 * f6, f9 + f3 * f6 - f5 * f6, x2, 0.0);
        tessellator.draw();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    public void doRender(final Entity entity, final double d, final double d1, final double d2, final float f,
            final float f1) {
        if (this.size1 == 0) {
            this.size1 = 16;
        }
        this.renderEntityAt(entity, d, d1, d2, f, f1);
    }

    protected ResourceLocation getEntityTexture(final Entity entity) {
        return AbstractClientPlayer.locationStevePng;
    }
}
