//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.client.renderer.model.ModelSoulBeacon;
import com.kentington.thaumichorizons.common.tiles.TileSoulBeacon;

import thaumcraft.client.lib.UtilsFX;

public class TileSoulBeaconRender extends TileEntitySpecialRenderer {

    static String tx1;
    private final ModelSoulBeacon base;
    private static final ResourceLocation field_147523_b;

    public void renderTileEntityAt(final TileSoulBeacon tco, final double p_147500_2_, final double p_147500_4_,
            final double p_147500_6_, final float p_147500_8_) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) p_147500_2_ + 0.5f, (float) p_147500_4_ + 1.5f, (float) p_147500_6_ + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", TileSoulBeaconRender.tx1);
        this.base.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
        if (tco.getWorldObj() == null || !tco.getWorldObj().canBlockSeeTheSky(tco.xCoord, tco.yCoord, tco.zCoord)) {
            return;
        }
        final float f1 = tco.func_146002_i();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        if (f1 > 0.0f) {
            final Tessellator tessellator = Tessellator.instance;
            this.bindTexture(TileSoulBeaconRender.field_147523_b);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10497.0f);
            GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10497.0f);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthMask(true);
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            final float f2 = tco.getWorldObj().getTotalWorldTime() + p_147500_8_;
            final float f3 = -f2 * 0.2f - MathHelper.floor_float(-f2 * 0.1f);
            final byte b0 = 1;
            final double d3 = f2 * 0.025 * (1.0 - (b0 & 0x1) * 2.5);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(255, 255, 255, 32);
            final double d4 = b0 * 0.2;
            final double d5 = 0.5 + Math.cos(d3 + 2.356194490192345) * d4;
            final double d6 = 0.5 + Math.sin(d3 + 2.356194490192345) * d4;
            final double d7 = 0.5 + Math.cos(d3 + 0.7853981633974483) * d4;
            final double d8 = 0.5 + Math.sin(d3 + 0.7853981633974483) * d4;
            final double d9 = 0.5 + Math.cos(d3 + 3.9269908169872414) * d4;
            final double d10 = 0.5 + Math.sin(d3 + 3.9269908169872414) * d4;
            final double d11 = 0.5 + Math.cos(d3 + 5.497787143782138) * d4;
            final double d12 = 0.5 + Math.sin(d3 + 5.497787143782138) * d4;
            final double d13 = 256.0f * f1;
            final double d14 = 0.0;
            final double d15 = 1.0;
            final double d16 = -1.0f + f3;
            final double d17 = 256.0f * f1 * (0.5 / d4) + d16;
            tessellator.addVertexWithUV(p_147500_2_ + d5, p_147500_4_ + d13, p_147500_6_ + d6, d15, d17);
            tessellator.addVertexWithUV(p_147500_2_ + d5, p_147500_4_, p_147500_6_ + d6, d15, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d7, p_147500_4_, p_147500_6_ + d8, d14, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d7, p_147500_4_ + d13, p_147500_6_ + d8, d14, d17);
            tessellator.addVertexWithUV(p_147500_2_ + d11, p_147500_4_ + d13, p_147500_6_ + d12, d15, d17);
            tessellator.addVertexWithUV(p_147500_2_ + d11, p_147500_4_, p_147500_6_ + d12, d15, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d9, p_147500_4_, p_147500_6_ + d10, d14, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d9, p_147500_4_ + d13, p_147500_6_ + d10, d14, d17);
            tessellator.addVertexWithUV(p_147500_2_ + d7, p_147500_4_ + d13, p_147500_6_ + d8, d15, d17);
            tessellator.addVertexWithUV(p_147500_2_ + d7, p_147500_4_, p_147500_6_ + d8, d15, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d11, p_147500_4_, p_147500_6_ + d12, d14, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d11, p_147500_4_ + d13, p_147500_6_ + d12, d14, d17);
            tessellator.addVertexWithUV(p_147500_2_ + d9, p_147500_4_ + d13, p_147500_6_ + d10, d15, d17);
            tessellator.addVertexWithUV(p_147500_2_ + d9, p_147500_4_, p_147500_6_ + d10, d15, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d5, p_147500_4_, p_147500_6_ + d6, d14, d16);
            tessellator.addVertexWithUV(p_147500_2_ + d5, p_147500_4_ + d13, p_147500_6_ + d6, d14, d17);
            tessellator.draw();
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glDepthMask(false);
            tessellator.startDrawingQuads();
            tessellator.setColorRGBA(255, 255, 255, 32);
            final double d18 = 0.2;
            final double d19 = 0.2;
            final double d20 = 0.8;
            final double d21 = 0.2;
            final double d22 = 0.2;
            final double d23 = 0.8;
            final double d24 = 0.8;
            final double d25 = 0.8;
            final double d26 = 256.0f * f1;
            final double d27 = 0.0;
            final double d28 = 1.0;
            final double d29 = -1.0f + f3;
            final double d30 = 256.0f * f1 + d29;
            tessellator.addVertexWithUV(p_147500_2_ + d18, p_147500_4_ + d26, p_147500_6_ + d19, d28, d30);
            tessellator.addVertexWithUV(p_147500_2_ + d18, p_147500_4_, p_147500_6_ + d19, d28, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d20, p_147500_4_, p_147500_6_ + d21, d27, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d20, p_147500_4_ + d26, p_147500_6_ + d21, d27, d30);
            tessellator.addVertexWithUV(p_147500_2_ + d24, p_147500_4_ + d26, p_147500_6_ + d25, d28, d30);
            tessellator.addVertexWithUV(p_147500_2_ + d24, p_147500_4_, p_147500_6_ + d25, d28, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d22, p_147500_4_, p_147500_6_ + d23, d27, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d22, p_147500_4_ + d26, p_147500_6_ + d23, d27, d30);
            tessellator.addVertexWithUV(p_147500_2_ + d20, p_147500_4_ + d26, p_147500_6_ + d21, d28, d30);
            tessellator.addVertexWithUV(p_147500_2_ + d20, p_147500_4_, p_147500_6_ + d21, d28, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d24, p_147500_4_, p_147500_6_ + d25, d27, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d24, p_147500_4_ + d26, p_147500_6_ + d25, d27, d30);
            tessellator.addVertexWithUV(p_147500_2_ + d22, p_147500_4_ + d26, p_147500_6_ + d23, d28, d30);
            tessellator.addVertexWithUV(p_147500_2_ + d22, p_147500_4_, p_147500_6_ + d23, d28, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d18, p_147500_4_, p_147500_6_ + d19, d27, d29);
            tessellator.addVertexWithUV(p_147500_2_ + d18, p_147500_4_ + d26, p_147500_6_ + d19, d27, d30);
            tessellator.draw();
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDepthMask(true);
        }
    }

    public TileSoulBeaconRender() {
        this.base = new ModelSoulBeacon();
    }

    public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float f) {
        this.renderTileEntityAt((TileSoulBeacon) te, x, y, z, f);
    }

    static {
        TileSoulBeaconRender.tx1 = "textures/models/soulbeacon.png";
        field_147523_b = new ResourceLocation("textures/entity/beacon_beam.png");
    }
}
