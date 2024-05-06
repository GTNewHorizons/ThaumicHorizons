//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.tiles.TileVat;
import com.kentington.thaumichorizons.common.tiles.TileVatMatrix;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.ModelCube;
import thaumcraft.codechicken.lib.math.MathHelper;

@SideOnly(Side.CLIENT)
public class TileVatMatrixRender extends TileEntitySpecialRenderer {

    private final ModelCube model;
    private final ModelCube model_over;
    int type;

    public TileVatMatrixRender(final int type) {
        this.model = new ModelCube(0);
        this.model_over = new ModelCube(32);
        this.type = 0;
        this.type = type;
    }

    private void drawHalo(final TileEntity is, final double x, final double y, final double z, final float par8,
            final int count) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
        final int q = FMLClientHandler.instance().getClient().gameSettings.fancyGraphics ? 20 : 10;
        final Tessellator tessellator = Tessellator.instance;
        RenderHelper.disableStandardItemLighting();
        final float f1 = count / 500.0f;
        final float f2 = 0.9f;
        final float f3 = 0.0f;
        final Random random = new Random(245L);
        GL11.glDisable(3553);
        GL11.glShadeModel(7425);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        GL11.glDisable(3008);
        GL11.glEnable(2884);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        for (int i = 0; i < q; ++i) {
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 0.0f, 1.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 1.0f, 0.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f, 0.0f, 1.0f, 0.0f);
            GL11.glRotatef(random.nextFloat() * 360.0f + f1 * 360.0f, 0.0f, 0.0f, 1.0f);
            tessellator.startDrawing(6);
            float fa = random.nextFloat() * 20.0f + 5.0f + f3 * 10.0f;
            float f4 = random.nextFloat() * 2.0f + 1.0f + f3 * 2.0f;
            fa /= 20.0f / (Math.min(count, 50) / 50.0f);
            f4 /= 20.0f / (Math.min(count, 50) / 50.0f);
            tessellator.setColorRGBA_I(16777215, (int) (255.0f * (1.0f - f3)));
            tessellator.addVertex(0.0, 0.0, 0.0);
            tessellator.setColorRGBA_I(13369599, 0);
            tessellator.addVertex(-0.866 * f4, fa, -0.5f * f4);
            tessellator.addVertex(0.866 * f4, fa, -0.5f * f4);
            tessellator.addVertex(0.0, fa, f4);
            tessellator.addVertex(-0.866 * f4, fa, -0.5f * f4);
            tessellator.draw();
        }
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2884);
        GL11.glDisable(3042);
        GL11.glShadeModel(7424);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(3553);
        GL11.glEnable(3008);
        RenderHelper.enableStandardItemLighting();
        GL11.glBlendFunc(770, 771);
        GL11.glPopMatrix();
    }

    public void renderInfusionMatrix(final TileVatMatrix tile, final double par2, final double par4, final double par6,
            final float par8) {
        final TileVat vat = tile.getVat();
        GL11.glPushMatrix();
        UtilsFX.bindTexture("textures/models/infuser.png");
        GL11.glTranslatef((float) par2 + 0.5f, (float) par4 + 0.5f, (float) par6 + 0.5f);
        final float ticks = Minecraft.getMinecraft().renderViewEntity.ticksExisted + par8;
        float instability = 0.0f;
        float startUp = 0.0f;
        float craftCount = 0.0f;
        if (vat != null) {
            startUp = vat.startUp;
            instability = vat.instability;
            craftCount = vat.craftCount;
        }
        if (tile.getWorldObj() != null) {
            GL11.glRotatef(ticks % 360.0f * startUp, 0.0f, 1.0f, 0.0f);
        }
        instability = Math.min(6.0f, 1.0f + instability * 0.66f * (Math.min(craftCount, 50.0f) / 50.0f));
        float b1 = 0.0f;
        float b2 = 0.0f;
        float b3 = 0.0f;
        int aa = 0;
        int bb = 0;
        int cc = 0;
        for (int a = 0; a < 2; ++a) {
            for (int b4 = 0; b4 < 2; ++b4) {
                for (int c = 0; c < 2; ++c) {
                    b1 = (float) (MathHelper.sin((ticks + a * 10) / (15.0f - instability / 2.0f)) * 0.009999999776482582
                            * startUp
                            * instability);
                    b2 = (float) (MathHelper.sin((ticks + b4 * 10) / (14.0f - instability / 2.0f))
                            * 0.009999999776482582
                            * startUp
                            * instability);
                    b3 = (float) (MathHelper.sin((ticks + c * 10) / (13.0f - instability / 2.0f)) * 0.009999999776482582
                            * startUp
                            * instability);
                    aa = ((a == 0) ? -1 : 1);
                    bb = ((b4 == 0) ? -1 : 1);
                    cc = ((c == 0) ? -1 : 1);
                    GL11.glPushMatrix();
                    GL11.glTranslatef(b1 + aa * 0.25f, b2 + bb * 0.25f, b3 + cc * 0.25f);
                    if (a > 0) {
                        GL11.glRotatef(90.0f, (float) a, 0.0f, 0.0f);
                    }
                    if (b4 > 0) {
                        GL11.glRotatef(90.0f, 0.0f, (float) b4, 0.0f);
                    }
                    if (c > 0) {
                        GL11.glRotatef(90.0f, 0.0f, 0.0f, (float) c);
                    }
                    GL11.glScaled(0.45, 0.45, 0.45);
                    this.model.render();
                    GL11.glPopMatrix();
                }
            }
        }
        GL11.glPushMatrix();
        GL11.glAlphaFunc(516, 0.003921569f);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 1);
        for (int a = 0; a < 2; ++a) {
            for (int b4 = 0; b4 < 2; ++b4) {
                for (int c = 0; c < 2; ++c) {
                    b1 = (float) (MathHelper.sin((ticks + a * 10) / (15.0f - instability / 2.0f)) * 0.009999999776482582
                            * startUp
                            * instability);
                    b2 = (float) (MathHelper.sin((ticks + b4 * 10) / (14.0f - instability / 2.0f))
                            * 0.009999999776482582
                            * startUp
                            * instability);
                    b3 = (float) (MathHelper.sin((ticks + c * 10) / (13.0f - instability / 2.0f)) * 0.009999999776482582
                            * startUp
                            * instability);
                    aa = ((a == 0) ? -1 : 1);
                    bb = ((b4 == 0) ? -1 : 1);
                    cc = ((c == 0) ? -1 : 1);
                    GL11.glPushMatrix();
                    GL11.glTranslatef(b1 + aa * 0.25f, b2 + bb * 0.25f, b3 + cc * 0.25f);
                    if (a > 0) {
                        GL11.glRotatef(90.0f, (float) a, 0.0f, 0.0f);
                    }
                    if (b4 > 0) {
                        GL11.glRotatef(90.0f, 0.0f, (float) b4, 0.0f);
                    }
                    if (c > 0) {
                        GL11.glRotatef(90.0f, 0.0f, 0.0f, (float) c);
                    }
                    GL11.glScaled(0.45, 0.45, 0.45);
                    final int j = 15728880;
                    final int k = j % 65536;
                    final int l = j / 65536;
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, k, l);
                    GL11.glColor4f(
                            0.8f,
                            0.1f,
                            1.0f,
                            (float) ((MathHelper.sin((ticks + a * 2 + b4 * 3 + c * 4) / 4.0f) * 0.10000000149011612
                                    + 0.20000000298023224) * startUp));
                    this.model_over.render();
                    GL11.glPopMatrix();
                }
            }
        }
        GL11.glDisable(3042);
        GL11.glAlphaFunc(516, 0.1f);
        GL11.glBlendFunc(770, 771);
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        if (vat != null && vat.mode == 2) {
            this.drawHalo(vat, par2, par4, par6, par8, vat.craftCount);
        }
    }

    public void renderTileEntityAt(final TileEntity par1TileEntity, final double par2, final double par4,
            final double par6, final float par8) {
        this.renderInfusionMatrix((TileVatMatrix) par1TileEntity, par2, par4, par6, par8);
    }
}
