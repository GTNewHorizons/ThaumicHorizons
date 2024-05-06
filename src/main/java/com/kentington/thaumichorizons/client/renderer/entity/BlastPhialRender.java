//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.entity;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionHelper;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.common.ThaumicHorizons;

public class BlastPhialRender extends RenderSnowball {

    Item item;

    public BlastPhialRender() {
        super(ThaumicHorizons.itemSyringeInjection);
        this.item = ThaumicHorizons.itemSyringeInjection;
    }

    public void doRender(final Entity p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        final IIcon iicon = this.item.getIconFromDamage(1);
        if (iicon != null) {
            GL11.glPushMatrix();
            GL11.glTranslatef((float) p_76986_2_, (float) p_76986_4_, (float) p_76986_6_);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            this.bindEntityTexture(p_76986_1_);
            final Tessellator tessellator = Tessellator.instance;
            final int i = PotionHelper.func_77915_a(((EntityPotion) p_76986_1_).getPotionDamage(), false);
            final float f2 = (i >> 16 & 0xFF) / 255.0f;
            final float f3 = (i >> 8 & 0xFF) / 255.0f;
            final float f4 = (i & 0xFF) / 255.0f;
            GL11.glColor3f(f2, f3, f4);
            GL11.glPushMatrix();
            this.func_77026_a(tessellator, iicon);
            GL11.glPopMatrix();
            GL11.glColor3f(1.0f, 1.0f, 1.0f);
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
        }
    }

    private void func_77026_a(final Tessellator p_77026_1_, final IIcon p_77026_2_) {
        final float f = p_77026_2_.getMinU();
        final float f2 = p_77026_2_.getMaxU();
        final float f3 = p_77026_2_.getMinV();
        final float f4 = p_77026_2_.getMaxV();
        final float f5 = 1.0f;
        final float f6 = 0.5f;
        final float f7 = 0.25f;
        GL11.glRotatef(180.0f - this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
        p_77026_1_.startDrawingQuads();
        p_77026_1_.setNormal(0.0f, 1.0f, 0.0f);
        p_77026_1_.addVertexWithUV(0.0f - f6, 0.0f - f7, 0.0, f, f4);
        p_77026_1_.addVertexWithUV(f5 - f6, 0.0f - f7, 0.0, f2, f4);
        p_77026_1_.addVertexWithUV(f5 - f6, f5 - f7, 0.0, f2, f3);
        p_77026_1_.addVertexWithUV(0.0f - f6, f5 - f7, 0.0, f, f3);
        p_77026_1_.draw();
    }

    protected ResourceLocation getEntityTexture(final Entity p_110775_1_) {
        return TextureMap.locationItemsTexture;
    }
}
