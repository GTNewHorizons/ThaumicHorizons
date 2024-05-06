//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.MathHelper;

public class ModelFamiliar extends ModelBase {

    ModelRenderer Body;
    ModelRenderer Tail1;
    ModelRenderer Tail2;
    ModelRenderer LegBackL;
    ModelRenderer Ear1;
    ModelRenderer Nose;
    ModelRenderer Main;
    ModelRenderer Ear2;
    ModelRenderer HatBuckle;
    ModelRenderer HatC;
    ModelRenderer HatB;
    ModelRenderer HatA;
    ModelRenderer HatBase;
    ModelRenderer LegBackR;
    ModelRenderer LegFrontL;
    ModelRenderer LegFrontR;
    int field_78163_i;

    public ModelFamiliar() {
        this.field_78163_i = 1;
        this.textureWidth = 64;
        this.textureHeight = 32;
        (this.Body = new ModelRenderer(this, 20, 0)).addBox(-2.0f, 3.0f, -8.0f, 4, 16, 6);
        this.Body.setRotationPoint(0.0f, 12.0f, -10.0f);
        this.Body.setTextureSize(64, 32);
        this.Body.mirror = true;
        this.setRotation(this.Body, ((float) Math.PI / 2F), 0.0f, 0.0f);
        (this.Tail1 = new ModelRenderer(this, 0, 15)).addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1);
        this.Tail1.setRotationPoint(0.0f, 15.0f, 8.0f);
        this.Tail1.setTextureSize(64, 32);
        this.Tail1.mirror = true;
        this.setRotation(this.Tail1, ((float) Math.PI / 2F), 0.0f, 0.0f);
        (this.Tail2 = new ModelRenderer(this, 4, 15)).addBox(-0.5f, 0.0f, 0.0f, 1, 8, 1);
        this.Tail2.setRotationPoint(0.0f, 15.0f, 14.0f);
        this.Tail2.setTextureSize(64, 32);
        this.Tail2.mirror = true;
        this.setRotation(this.Tail2, ((float) Math.PI / 2F), 0.0f, 0.0f);
        (this.LegBackL = new ModelRenderer(this, 8, 13)).addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2);
        this.LegBackL.setRotationPoint(1.1f, 18.0f, 5.0f);
        this.LegBackL.setTextureSize(64, 32);
        this.LegBackL.mirror = true;
        this.setRotation(this.LegBackL, 0.0f, 0.0f, 0.0f);
        (this.Ear1 = new ModelRenderer(this, 0, 10)).addBox(-2.0f, -3.0f, 0.0f, 1, 1, 2);
        this.Ear1.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.Ear1.setTextureSize(64, 32);
        this.Ear1.mirror = true;
        this.setRotation(this.Ear1, 0.0f, 0.0f, 0.0f);
        (this.Nose = new ModelRenderer(this, 0, 24)).addBox(-1.5f, 0.0f, -4.0f, 3, 2, 2);
        this.Nose.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.Nose.setTextureSize(64, 32);
        this.Nose.mirror = true;
        this.setRotation(this.Nose, 0.0f, 0.0f, 0.0f);
        (this.Main = new ModelRenderer(this, 0, 0)).addBox(-2.5f, -2.0f, -3.0f, 5, 4, 5);
        this.Main.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.Main.setTextureSize(64, 32);
        this.Main.mirror = true;
        this.setRotation(this.Main, 0.0f, 0.0f, 0.0f);
        (this.Ear2 = new ModelRenderer(this, 6, 10)).addBox(1.0f, -3.0f, 0.0f, 1, 1, 2);
        this.Ear2.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.Ear2.setTextureSize(64, 32);
        this.Ear2.mirror = true;
        this.setRotation(this.Ear2, 0.0f, 0.0f, 0.0f);
        (this.HatBuckle = new ModelRenderer(this, 48, 13)).addBox(-1.0f, -4.0f, -3.0f, 2, 1, 1);
        this.HatBuckle.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.HatBuckle.setTextureSize(64, 32);
        this.HatBuckle.mirror = true;
        this.setRotation(this.HatBuckle, 0.0f, 0.0f, 0.0f);
        (this.HatC = new ModelRenderer(this, 48, 11)).addBox(-0.5f, -7.0f, -1.0f, 1, 1, 1);
        this.HatC.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.HatC.setTextureSize(64, 32);
        this.HatC.mirror = true;
        this.setRotation(this.HatC, 0.0f, 0.0f, 0.0f);
        (this.HatB = new ModelRenderer(this, 48, 8)).addBox(-1.0f, -6.0f, -1.5f, 2, 1, 2);
        this.HatB.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.HatB.setTextureSize(64, 32);
        this.HatB.mirror = true;
        this.setRotation(this.HatB, 0.0f, 0.0f, 0.0f);
        (this.HatA = new ModelRenderer(this, 48, 0)).addBox(-1.5f, -5.0f, -2.0f, 3, 2, 3);
        this.HatA.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.HatA.setTextureSize(64, 32);
        this.HatA.mirror = true;
        this.setRotation(this.HatA, 0.0f, 0.0f, 0.0f);
        (this.HatBase = new ModelRenderer(this, 16, 24)).addBox(-3.0f, -3.0f, -3.5f, 6, 1, 6);
        this.HatBase.setRotationPoint(0.0f, 15.0f, -9.0f);
        this.HatBase.setTextureSize(64, 32);
        this.HatBase.mirror = true;
        this.setRotation(this.HatBase, 0.0f, 0.0f, 0.0f);
        (this.LegBackR = new ModelRenderer(this, 8, 13)).addBox(-1.0f, 0.0f, 1.0f, 2, 6, 2);
        this.LegBackR.setRotationPoint(-1.1f, 18.0f, 5.0f);
        this.LegBackR.setTextureSize(64, 32);
        this.LegBackR.mirror = true;
        this.setRotation(this.LegBackR, 0.0f, 0.0f, 0.0f);
        (this.LegFrontL = new ModelRenderer(this, 40, 0)).addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2);
        this.LegFrontL.setRotationPoint(1.2f, 13.8f, -5.0f);
        this.LegFrontL.setTextureSize(64, 32);
        this.LegFrontL.mirror = true;
        this.setRotation(this.LegFrontL, 0.0f, 0.0f, 0.0f);
        (this.LegFrontR = new ModelRenderer(this, 40, 0)).addBox(-1.0f, 0.0f, 0.0f, 2, 10, 2);
        this.LegFrontR.setRotationPoint(-1.2f, 13.8f, -5.0f);
        this.LegFrontR.setTextureSize(64, 32);
        this.LegFrontR.mirror = true;
        this.setRotation(this.LegFrontR, 0.0f, 0.0f, 0.0f);
    }

    public void render(final Entity entity, final float f, final float f1, final float f2, final float f3,
            final float f4, final float f5) {
        this.setRotationAngles(f, f1, f2, f3, f4, f5, entity);
        this.Body.render(f5);
        this.Tail1.render(f5);
        this.Tail2.render(f5);
        this.LegBackL.render(f5);
        this.Ear1.render(f5);
        this.Nose.render(f5);
        this.Main.render(f5);
        this.Ear2.render(f5);
        this.HatBuckle.render(f5);
        this.HatC.render(f5);
        this.HatB.render(f5);
        this.HatA.render(f5);
        this.HatBase.render(f5);
        this.LegBackR.render(f5);
        this.LegFrontL.render(f5);
        this.LegFrontR.render(f5);
    }

    private void setRotation(final ModelRenderer model, final float x, final float y, final float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }

    public void setRotationAngles(final float p_78087_1_, final float p_78087_2_, final float p_78087_3_,
            final float p_78087_4_, final float p_78087_5_, final float p_78087_6_, final Entity p_78087_7_) {
        this.Main.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.Main.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.Nose.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.Nose.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.Ear1.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.Ear1.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.Ear2.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.Ear2.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.HatA.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.HatA.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.HatB.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.HatB.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.HatC.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.HatC.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.HatBuckle.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.HatBuckle.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        this.HatBase.rotateAngleX = p_78087_5_ / (180F / (float) Math.PI);
        this.HatBase.rotateAngleY = p_78087_4_ / (180F / (float) Math.PI);
        if (this.field_78163_i != 3) {
            this.Body.rotateAngleX = ((float) Math.PI / 2F);
            if (this.field_78163_i == 2) {
                this.LegBackL.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f) * 1.0f * p_78087_2_;
                this.LegBackR.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f + 0.3f) * 1.0f * p_78087_2_;
                this.LegFrontL.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f + (float) Math.PI + 0.3f) * 1.0f
                        * p_78087_2_;
                this.LegFrontR.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f + (float) Math.PI) * 1.0f
                        * p_78087_2_;
                this.Tail2.rotateAngleX = 1.7278761f
                        + ((float) Math.PI / 10F) * MathHelper.cos(p_78087_1_) * p_78087_2_;
            } else {
                this.LegBackL.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f) * 1.0f * p_78087_2_;
                this.LegBackR.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f + (float) Math.PI) * 1.0f * p_78087_2_;
                this.LegFrontL.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f + (float) Math.PI) * 1.0f
                        * p_78087_2_;
                this.LegFrontR.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662f) * 1.0f * p_78087_2_;
                if (this.field_78163_i == 1) {
                    this.Tail2.rotateAngleX = 1.7278761f
                            + ((float) Math.PI / 4F) * MathHelper.cos(p_78087_1_) * p_78087_2_;
                } else {
                    this.Tail2.rotateAngleX = 1.7278761f + 0.47123894f * MathHelper.cos(p_78087_1_) * p_78087_2_;
                }
            }
        }
    }

    public void setLivingAnimations(final EntityLivingBase p_78086_1_, final float p_78086_2_, final float p_78086_3_,
            final float p_78086_4_) {
        final EntityOcelot entityocelot = (EntityOcelot) p_78086_1_;
        this.Body.rotationPointY = 12.0f;
        this.Body.rotationPointZ = -10.0f;
        this.Main.rotationPointY = 15.0f;
        this.Main.rotationPointZ = -9.0f;
        this.Nose.rotationPointY = 15.0f;
        this.Nose.rotationPointZ = -9.0f;
        this.Ear1.rotationPointY = 15.0f;
        this.Ear1.rotationPointZ = -9.0f;
        this.Ear2.rotationPointY = 15.0f;
        this.Ear2.rotationPointZ = -9.0f;
        this.HatBase.rotationPointY = 15.0f;
        this.HatBase.rotationPointZ = -9.0f;
        this.HatB.rotationPointY = 15.0f;
        this.HatB.rotationPointZ = -9.0f;
        this.HatA.rotationPointY = 15.0f;
        this.HatA.rotationPointZ = -9.0f;
        this.HatC.rotationPointY = 15.0f;
        this.HatC.rotationPointZ = -9.0f;
        this.HatBuckle.rotationPointY = 15.0f;
        this.HatBuckle.rotationPointZ = -9.0f;
        this.Tail1.rotationPointY = 15.0f;
        this.Tail1.rotationPointZ = 8.0f;
        this.Tail2.rotationPointY = 20.0f;
        this.Tail2.rotationPointZ = 14.0f;
        final ModelRenderer legFrontL = this.LegFrontL;
        final ModelRenderer legFrontR = this.LegFrontR;
        final float n = 13.8f;
        legFrontR.rotationPointY = n;
        legFrontL.rotationPointY = n;
        final ModelRenderer legFrontL2 = this.LegFrontL;
        final ModelRenderer legFrontR2 = this.LegFrontR;
        final float n2 = -5.0f;
        legFrontR2.rotationPointZ = n2;
        legFrontL2.rotationPointZ = n2;
        final ModelRenderer legBackL = this.LegBackL;
        final ModelRenderer legBackR = this.LegBackR;
        final float n3 = 18.0f;
        legBackR.rotationPointY = n3;
        legBackL.rotationPointY = n3;
        final ModelRenderer legBackL2 = this.LegBackL;
        final ModelRenderer legBackR2 = this.LegBackR;
        final float n4 = 5.0f;
        legBackR2.rotationPointZ = n4;
        legBackL2.rotationPointZ = n4;
        this.Tail1.rotateAngleX = 0.9f;
        if (entityocelot.isSneaking()) {
            final ModelRenderer body = this.Body;
            ++body.rotationPointY;
            final ModelRenderer main = this.Main;
            main.rotationPointY += 2.0f;
            final ModelRenderer nose = this.Nose;
            nose.rotationPointY += 2.0f;
            final ModelRenderer ear1 = this.Ear1;
            ear1.rotationPointY += 2.0f;
            final ModelRenderer ear2 = this.Ear2;
            ear2.rotationPointY += 2.0f;
            final ModelRenderer hatBase = this.HatBase;
            hatBase.rotationPointY += 2.0f;
            final ModelRenderer hatA = this.HatA;
            hatA.rotationPointY += 2.0f;
            final ModelRenderer hatB = this.HatB;
            hatB.rotationPointY += 2.0f;
            final ModelRenderer hatC = this.HatC;
            hatC.rotationPointY += 2.0f;
            final ModelRenderer hatBuckle = this.HatBuckle;
            hatBuckle.rotationPointY += 2.0f;
            final ModelRenderer tail1 = this.Tail1;
            ++tail1.rotationPointY;
            final ModelRenderer tail2 = this.Tail2;
            tail2.rotationPointY -= 4.0f;
            final ModelRenderer tail3 = this.Tail2;
            tail3.rotationPointZ += 2.0f;
            this.Tail1.rotateAngleX = ((float) Math.PI / 2F);
            this.Tail2.rotateAngleX = ((float) Math.PI / 2F);
            this.field_78163_i = 0;
        } else if (entityocelot.isSprinting()) {
            this.Tail2.rotationPointY = this.Tail1.rotationPointY;
            final ModelRenderer tail4 = this.Tail2;
            tail4.rotationPointZ += 2.0f;
            this.Tail1.rotateAngleX = ((float) Math.PI / 2F);
            this.Tail2.rotateAngleX = ((float) Math.PI / 2F);
            this.field_78163_i = 2;
        } else if (entityocelot.isSitting()) {
            this.Body.rotateAngleX = ((float) Math.PI / 4F);
            final ModelRenderer body2 = this.Body;
            body2.rotationPointY -= 4.0f;
            final ModelRenderer body3 = this.Body;
            body3.rotationPointZ += 5.0f;
            final ModelRenderer main2 = this.Main;
            main2.rotationPointY -= 3.3f;
            final ModelRenderer main3 = this.Main;
            ++main3.rotationPointZ;
            final ModelRenderer nose2 = this.Nose;
            nose2.rotationPointY -= 3.3f;
            final ModelRenderer nose3 = this.Nose;
            ++nose3.rotationPointZ;
            final ModelRenderer ear3 = this.Ear1;
            ear3.rotationPointY -= 3.3f;
            final ModelRenderer ear4 = this.Ear1;
            ++ear4.rotationPointZ;
            final ModelRenderer ear5 = this.Ear2;
            ear5.rotationPointY -= 3.3f;
            final ModelRenderer ear6 = this.Ear2;
            ++ear6.rotationPointZ;
            final ModelRenderer hatBase2 = this.HatBase;
            hatBase2.rotationPointY -= 3.3f;
            final ModelRenderer hatBase3 = this.HatBase;
            ++hatBase3.rotationPointZ;
            final ModelRenderer hatA2 = this.HatA;
            hatA2.rotationPointY -= 3.3f;
            final ModelRenderer hatA3 = this.HatA;
            ++hatA3.rotationPointZ;
            final ModelRenderer hatB2 = this.HatB;
            hatB2.rotationPointY -= 3.3f;
            final ModelRenderer hatB3 = this.HatB;
            ++hatB3.rotationPointZ;
            final ModelRenderer hatC2 = this.HatC;
            hatC2.rotationPointY -= 3.3f;
            final ModelRenderer hatC3 = this.HatC;
            ++hatC3.rotationPointZ;
            final ModelRenderer hatBuckle2 = this.HatBuckle;
            hatBuckle2.rotationPointY -= 3.3f;
            final ModelRenderer hatBuckle3 = this.HatBuckle;
            ++hatBuckle3.rotationPointZ;
            final ModelRenderer tail5 = this.Tail1;
            tail5.rotationPointY += 8.0f;
            final ModelRenderer tail6 = this.Tail1;
            tail6.rotationPointZ -= 2.0f;
            final ModelRenderer tail7 = this.Tail2;
            tail7.rotationPointY += 2.0f;
            final ModelRenderer tail8 = this.Tail2;
            tail8.rotationPointZ -= 0.8f;
            this.Tail1.rotateAngleX = 1.7278761f;
            this.Tail2.rotateAngleX = 2.670354f;
            final ModelRenderer legFrontL3 = this.LegFrontL;
            final ModelRenderer legFrontR3 = this.LegFrontR;
            final float n5 = -0.15707964f;
            legFrontR3.rotateAngleX = n5;
            legFrontL3.rotateAngleX = n5;
            final ModelRenderer legFrontL4 = this.LegFrontL;
            final ModelRenderer legFrontR4 = this.LegFrontR;
            final float n6 = 15.8f;
            legFrontR4.rotationPointY = n6;
            legFrontL4.rotationPointY = n6;
            final ModelRenderer legFrontL5 = this.LegFrontL;
            final ModelRenderer legFrontR5 = this.LegFrontR;
            final float n7 = -7.0f;
            legFrontR5.rotationPointZ = n7;
            legFrontL5.rotationPointZ = n7;
            final ModelRenderer legBackL3 = this.LegBackL;
            final ModelRenderer legBackR3 = this.LegBackR;
            final float n8 = -((float) Math.PI / 2F);
            legBackR3.rotateAngleX = n8;
            legBackL3.rotateAngleX = n8;
            final ModelRenderer legBackL4 = this.LegBackL;
            final ModelRenderer legBackR4 = this.LegBackR;
            final float n9 = 21.0f;
            legBackR4.rotationPointY = n9;
            legBackL4.rotationPointY = n9;
            final ModelRenderer legBackL5 = this.LegBackL;
            final ModelRenderer legBackR5 = this.LegBackR;
            final float n10 = 1.0f;
            legBackR5.rotationPointZ = n10;
            legBackL5.rotationPointZ = n10;
            this.field_78163_i = 3;
        } else {
            this.field_78163_i = 1;
        }
    }
}
