//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class ModelInjector extends ModelBase {

    ModelRenderer Drum;
    ModelRenderer Front;
    ModelRenderer BowL1;
    ModelRenderer BowR1;
    ModelRenderer BowL2;
    ModelRenderer BowR2;
    ModelRenderer Stock;
    ModelRenderer Grip;
    ModelRenderer Thingy;

    public ModelInjector() {
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.Drum = new ModelRenderer(this, 0, 0)).addBox(-2.5f, -2.5f, 0.0f, 5, 5, 1);
        this.Drum.setRotationPoint(2.5f, 2.25f, 0.0f);
        this.Drum.setTextureSize(64, 32);
        this.Drum.mirror = true;
        this.setRotation(this.Drum, 0.0f, 0.0f, 0.0f);
        (this.Front = new ModelRenderer(this, 12, 0)).addBox(0.0f, 0.0f, 0.0f, 3, 1, 8);
        this.Front.setRotationPoint(1.0f, 1.0f, -8.0f);
        this.Front.setTextureSize(64, 32);
        this.Front.mirror = true;
        this.setRotation(this.Front, 0.0f, 0.0f, 0.0f);
        (this.BowL1 = new ModelRenderer(this, 0, 6)).addBox(0.0f, -1.0f, 0.0f, 4, 1, 1);
        this.BowL1.setRotationPoint(2.5f, 1.0f, -8.0f);
        this.BowL1.setTextureSize(64, 32);
        this.BowL1.mirror = true;
        this.setRotation(this.BowL1, 0.0f, 0.0f, 0.0f);
        (this.BowR1 = new ModelRenderer(this, 0, 6)).addBox(0.0f, -1.0f, -1.0f, 4, 1, 1);
        this.BowR1.setRotationPoint(2.5f, 1.0f, -8.0f);
        this.BowR1.setTextureSize(64, 32);
        this.BowR1.mirror = true;
        this.setRotation(this.BowR1, 0.0f, (float) Math.PI, 0.0f);
        (this.BowL2 = new ModelRenderer(this, 0, 8)).addBox(0.0f, -1.0f, 0.0f, 3, 1, 1);
        this.BowL2.setRotationPoint(6.0f, 1.0f, -7.5f);
        this.BowL2.setTextureSize(64, 32);
        this.BowL2.mirror = true;
        this.setRotation(this.BowL2, 0.0f, -0.3490659f, 0.0f);
        (this.BowR2 = new ModelRenderer(this, 0, 8)).addBox(0.0f, -1.0f, -1.0f, 3, 1, 1);
        this.BowR2.setRotationPoint(-1.0f, 1.0f, -7.5f);
        this.BowR2.setTextureSize(64, 32);
        this.BowR2.mirror = true;
        this.setRotation(this.BowR2, 0.0f, 3.490659f, 0.0f);
        (this.Thingy = new ModelRenderer(this, 13, 0)).addBox(-0.5f, 0.0f, 0.5f, 1, 1, 1);
        this.Thingy.setRotationPoint((this.BowR2.rotationPointX + this.BowL2.rotationPointX) / 2.0f, 0.0f, -6.5f);
        this.Thingy.setTextureSize(64, 32);
        this.Thingy.mirror = true;
        (this.Stock = new ModelRenderer(this, 0, 10)).addBox(0.0f, 0.0f, 0.0f, 3, 1, 3);
        this.Stock.setRotationPoint(1.0f, 1.0f, 1.0f);
        this.Stock.setTextureSize(64, 32);
        this.Stock.mirror = true;
        this.setRotation(this.Stock, 0.0f, 0.0f, 0.0f);
        (this.Grip = new ModelRenderer(this, 0, 14)).addBox(0.0f, 0.0f, 0.0f, 3, 2, 1);
        this.Grip.setRotationPoint(1.0f, 2.0f, 3.0f);
        this.Grip.setTextureSize(64, 32);
        this.Grip.mirror = true;
        this.setRotation(this.Grip, 0.0f, 0.0f, 0.0f);
    }

    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3,
            final float f4, final float f5) {
        super.render(entity, f, f1, f2, f3, f4, f5);
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.Drum.render(f5);
        this.Front.render(f5);
        this.BowL1.render(f5);
        this.BowR1.render(f5);
        this.BowL2.render(f5);
        this.BowR2.render(f5);
        this.Stock.render(f5);
        this.Grip.render(f5);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        final Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawing(3);
        tessellator.setColorOpaque_I(0);
        tessellator.addVertex(
                this.BowR2.rotationPointX + 0.6 + f3 * 0.25,
                this.BowR2.rotationPointY - 0.9375,
                this.BowR2.rotationPointZ + 6.8125 + f3 * 0.125);
        tessellator.addVertex(
                (this.BowR2.rotationPointX + this.BowL2.rotationPointX) / 2.0f - 2.175,
                this.BowR2.rotationPointY - 0.9375,
                this.BowR2.rotationPointZ + 6.8125 + f3 * 0.65);
        tessellator.addVertex(
                this.BowL2.rotationPointX - 4.95 - f3 * 0.25,
                this.BowL2.rotationPointY - 0.9375,
                this.BowL2.rotationPointZ + 6.8125 + f3 * 0.125);
        tessellator.draw();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        this.Thingy.setRotationPoint(
                (this.BowR2.rotationPointX + this.BowL2.rotationPointX) / 2.0f,
                0.0f,
                -6.5f + 5.0f * f3);
        this.Thingy.render(f5);
    }

    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(final float f, final float f1, final float f2, final float f3, final float f4,
            final float f5, final Entity ent) {
        super.setRotationAngles(f, f1, f2, f3, f4, f5, ent);
        this.BowL1.rotateAngleY = -f;
        this.BowR1.rotateAngleY = f + (float) Math.PI;
        this.BowL2.rotateAngleY = -0.34906238f - f1;
        this.BowR2.rotateAngleY = 3.490655f + f1;
        this.Drum.rotateAngleZ = f2 * 2.0f * (float) Math.PI / 180.0f;
    }
}
