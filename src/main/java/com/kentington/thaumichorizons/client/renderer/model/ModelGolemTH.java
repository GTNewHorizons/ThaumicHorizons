//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.model;

import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import thaumcraft.client.renderers.models.entities.ModelGolem;
import thaumcraft.common.entities.golems.EntityGolemBase;

public class ModelGolemTH extends ModelGolem {

    public ModelGolemTH(final boolean p) {
        super(p);
    }

    public void setRotationAngles(final Entity en, final float par1, final float par2, final float par3,
            final float par4, final float par5, final float par6) {
        int core = 0;
        if (en instanceof EntityGolemBase) {
            core = ((EntityGolemBase) en).getCore();
            if (this.pass == 0 && ((EntityGolemBase) en).healing > 0) {
                final float h1 = ((EntityGolemBase) en).healing / 10.0f;
                final float h2 = ((EntityGolemBase) en).healing / 5.0f;
                GL11.glColor3f(0.5f + h1, 0.9f + h2, 0.5f + h1);
            }
        }
        this.golemHead.rotateAngleY = par4 / (180F / (float) Math.PI);
        this.golemHead.rotateAngleX = par5 / (180F / (float) Math.PI);
        this.golemRightLeg.rotateAngleX = -1.5f * this.func_78172_a(par1, 13.0f) * par2;
        this.golemLeftLeg.rotateAngleX = 1.5f * this.func_78172_a(par1, 13.0f) * par2;
        this.golemRightLeg.rotateAngleY = 0.0f;
        this.golemLeftLeg.rotateAngleY = 0.0f;
        this.golemLeftArm.rotateAngleZ = 0.0f;
        this.golemRightArm.rotateAngleZ = 0.0f;
        if (core == 6) {
            final float s = (1.0f - (0.5f + Math.min(64, ((EntityGolemBase) en).getCarryLimit()) / 128.0f)) * 25.0f;
            this.golemLeftArm.rotateAngleZ = s / (180F / (float) Math.PI);
            this.golemRightArm.rotateAngleZ = -s / (180F / (float) Math.PI);
        }
    }

    private float func_78172_a(final float par1, final float par2) {
        return (Math.abs(par1 % par2 - par2 * 0.5f) - par2 * 0.25f) / (par2 * 0.25f);
    }
}
