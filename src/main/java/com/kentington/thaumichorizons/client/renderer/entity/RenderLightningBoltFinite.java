//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.entity;

import java.util.Random;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderLightningBolt;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.entities.EntityLightningBoltFinite;

public class RenderLightningBoltFinite extends RenderLightningBolt {

    public void doRender(final Entity p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {}

    public void doRender(final EntityLightningBoltFinite p_76986_1_, final double p_76986_2_, final double p_76986_4_,
            final double p_76986_6_, final float p_76986_8_, final float p_76986_9_) {
        final Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        final double[] adouble = new double[8];
        final double[] adouble2 = new double[8];
        double d3 = 0.0;
        double d4 = 0.0;
        final Random random = new Random(p_76986_1_.boltVertex);
        final double mult = p_76986_1_.boltLength / 128.0;
        for (int i = 7; i >= 0; --i) {
            adouble[i] = d3;
            adouble2[i] = d4;
            d3 += (random.nextInt(11) - 5) * mult;
            d4 += (random.nextInt(11) - 5) * mult;
        }
        for (int k1 = 0; k1 < 4; ++k1) {
            final Random random2 = new Random(p_76986_1_.boltVertex);
            for (int j = 0; j < 3; ++j) {
                int l = 7;
                int m = 0;
                if (j > 0) {
                    l = 7 - j;
                }
                if (j > 0) {
                    m = l - 2;
                }
                double d5 = adouble[l] - d3;
                double d6 = adouble2[l] - d4;
                for (int i2 = l; i2 >= m; --i2) {
                    final double d7 = d5;
                    final double d8 = d6;
                    if (j == 0) {
                        d5 += (random2.nextInt(11) - 5) * mult;
                        d6 += (random2.nextInt(11) - 5) * mult;
                    } else {
                        d5 += (random2.nextInt(31) - 15) * mult;
                        d6 += (random2.nextInt(31) - 15) * mult;
                    }
                    tessellator.startDrawing(5);
                    final float f2 = 0.5f;
                    tessellator.setColorRGBA_F(0.9f * f2, 0.9f * f2, f2, 0.3f);
                    double d9 = 0.1 + k1 * 0.2;
                    if (j == 0) {
                        d9 *= i2 * 0.1 + 1.0;
                    }
                    double d10 = 0.1 + k1 * 0.2;
                    if (j == 0) {
                        d10 *= (i2 - 1) * 0.1 + 1.0;
                    }
                    for (int j2 = 0; j2 < 5; ++j2) {
                        double d11 = p_76986_2_ + 0.5 - d9;
                        double d12 = p_76986_6_ + 0.5 - d9;
                        if (j2 == 1 || j2 == 2) {
                            d11 += d9 * 2.0;
                        }
                        if (j2 == 2 || j2 == 3) {
                            d12 += d9 * 2.0;
                        }
                        double d13 = p_76986_2_ + 0.5 - d10;
                        double d14 = p_76986_6_ + 0.5 - d10;
                        if (j2 == 1 || j2 == 2) {
                            d13 += d10 * 2.0;
                        }
                        if (j2 == 2 || j2 == 3) {
                            d14 += d10 * 2.0;
                        }
                        tessellator.addVertex(
                                d13 + d5,
                                p_76986_4_ + p_76986_1_.boltLength / (l - m + 1) * i2 / 2.0,
                                d14 + d6);
                        tessellator.addVertex(
                                d11 + d7,
                                p_76986_4_ + p_76986_1_.boltLength / (l - m + 1) * (i2 + 1) / 2.0,
                                d12 + d8);
                    }
                    tessellator.draw();
                }
            }
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
}
