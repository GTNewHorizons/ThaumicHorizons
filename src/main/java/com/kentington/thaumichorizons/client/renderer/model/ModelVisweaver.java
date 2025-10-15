package com.kentington.thaumichorizons.client.renderer.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

import thaumcraft.api.aspects.Aspect;

public class ModelVisweaver extends ModelBase {

    public ModelRenderer base;
    public ModelRenderer top;
    public ModelRenderer pillar1;
    public ModelRenderer pillar2;
    public ModelRenderer pillar3;
    public ModelRenderer pillar4;
    public ModelRenderer orbBase;

    public ModelRenderer orbIgnis;
    public ModelRenderer orbAqua;
    public ModelRenderer orbAer;
    public ModelRenderer orbTerra;
    public ModelRenderer orbOrdo;
    public ModelRenderer orbPerditio;

    public ModelRenderer orbCarrier;

    public ModelVisweaver() {
        this.textureWidth = 64;
        this.textureHeight = 36;

        this.pillar1 = new ModelRenderer(this, 0, 0);
        this.pillar1.setRotationPoint(-7.0F, 14.0F, 5.0F);
        this.pillar1.addBox(0.0F, 0.0F, 0.0F, 2, 8, 2, 0.0F);
        this.pillar2 = new ModelRenderer(this, 0, 0);
        this.pillar2.setRotationPoint(5.0F, 14.0F, 5.0F);
        this.pillar2.addBox(0.0F, 0.0F, 0.0F, 2, 8, 2, 0.0F);
        this.pillar3 = new ModelRenderer(this, 0, 0);
        this.pillar3.setRotationPoint(5.0F, 14.0F, -7.0F);
        this.pillar3.addBox(0.0F, 0.0F, 0.0F, 2, 8, 2, 0.0F);
        this.pillar4 = new ModelRenderer(this, 0, 0);
        this.pillar4.setRotationPoint(-7.0F, 14.0F, -7.0F);
        this.pillar4.addBox(0.0F, 0.0F, 0.0F, 2, 8, 2, 0.0F);

        this.top = new ModelRenderer(this, 0, 0);
        this.top.setRotationPoint(-8.0F, 12.0F, -8.0F);
        this.top.addBox(0.0F, 0.0F, 0.0F, 16, 2, 16, 0.0F);

        this.base = new ModelRenderer(this, 0, 18);
        this.base.setRotationPoint(-8.0F, 22.0F, -8.0F);
        this.base.addBox(0.0F, 0.0F, 0.0F, 16, 2, 16, 0.0F);

        this.orbBase = new ModelRenderer(this, 48, 0);
        this.orbBase.setRotationPoint(0, 8, 0);
        this.orbBase.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);

        this.orbIgnis = new ModelRenderer(this, 56, 4);
        this.orbIgnis.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.orbIgnis.setRotationPoint(0, 8, 0);
        this.orbAqua = new ModelRenderer(this, 56, 12);
        this.orbAqua.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.orbAqua.setRotationPoint(0, 8, 0);
        this.orbAer = new ModelRenderer(this, 56, 8);
        this.orbAer.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.orbAer.setRotationPoint(0, 8, 0);
        this.orbTerra = new ModelRenderer(this, 48, 8);
        this.orbTerra.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.orbTerra.setRotationPoint(0, 8, 0);
        this.orbOrdo = new ModelRenderer(this, 48, 12);
        this.orbOrdo.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.orbOrdo.setRotationPoint(0, 8, 0);
        this.orbPerditio = new ModelRenderer(this, 48, 4);
        this.orbPerditio.addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, 0.0F);
        this.orbPerditio.setRotationPoint(0, 8, 0);

        this.orbCarrier = new ModelRenderer(this);
        this.orbCarrier.addChild(this.orbBase);
        this.orbCarrier.addChild(this.orbIgnis);
        this.orbCarrier.addChild(this.orbAqua);
        this.orbCarrier.addChild(this.orbAer);
        this.orbCarrier.addChild(this.orbTerra);
        this.orbCarrier.addChild(this.orbOrdo);
        this.orbCarrier.addChild(this.orbPerditio);
    }

    public void render(boolean isWorking, int tickCounter, Aspect cvType) {
        final float scale = 1F / 16F;

        if (tickCounter != -1) {
            float xyRotate = (float) tickCounter / 20F;
            float zRotate = (float) tickCounter / 100F;

            orbCarrier.rotationPointX = (float) Math.sin((float) tickCounter / 10.0F) * 2.0F;

            float orbitRotation = ((float) tickCounter / 40F) % (2F * (float) Math.PI);
            setRotateAngle(orbCarrier, 0F, orbitRotation, 0F);

            ModelRenderer toRender = getToRender(isWorking, cvType);
            setRotateAngle(toRender, xyRotate, xyRotate, zRotate);
            toRender.render(scale);
        }
        this.pillar3.render(scale);
        this.top.render(scale);
        this.pillar4.render(scale);
        this.base.render(scale);
        this.pillar1.render(scale);
        this.pillar2.render(scale);
    }

    private ModelRenderer getToRender(boolean isWorking, Aspect cvType) {
        ModelRenderer toRender = orbBase;

        if (isWorking) {
            if (cvType == Aspect.AIR) {
                toRender = orbAer;
            } else if (cvType == Aspect.FIRE) {
                toRender = orbIgnis;
            } else if (cvType == Aspect.ORDER) {
                toRender = orbOrdo;
            } else if (cvType == Aspect.ENTROPY) {
                toRender = orbPerditio;
            } else if (cvType == Aspect.WATER) {
                toRender = orbAqua;
            } else if (cvType == Aspect.EARTH) {
                toRender = orbTerra;
            }
        }
        return toRender;
    }

    public void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
