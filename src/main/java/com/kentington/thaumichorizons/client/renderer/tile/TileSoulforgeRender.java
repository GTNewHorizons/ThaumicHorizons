//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.client.renderer.model.ModelSoulforge;
import com.kentington.thaumichorizons.common.tiles.TileSoulforge;

import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.ModelBrain;

public class TileSoulforgeRender extends TileEntitySpecialRenderer {

    private final ModelBrain brain;
    private final ModelSoulforge forge;
    static String tx1;
    static String tx2;
    static String tx3;

    public TileSoulforgeRender() {
        this.brain = new ModelBrain();
        this.forge = new ModelSoulforge();
    }

    public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z,
            final float f) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -0.25f, 0.0f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        this.renderBrains((TileSoulforge) tile, x, y, z, f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glTranslatef(0.0f, -1.5f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", TileSoulforgeRender.tx1);
        this.forge.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
        final long nt = System.nanoTime();
        if (((TileSoulforge) tile).forging > 0) {
            final int frames = UtilsFX.getTextureAnimationSize(TileSoulforgeRender.tx2);
            final int i = (int) ((nt / 40000000L + x) % frames);
            UtilsFX.bindTexture("thaumcraft", TileSoulforgeRender.tx2);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPushMatrix();
            UtilsFX.renderFacingQuad(
                    tile.xCoord + 0.025,
                    tile.yCoord + 0.75,
                    tile.zCoord + 0.025,
                    0.0f,
                    0.2f,
                    0.9f,
                    frames,
                    i,
                    f,
                    16777215);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            UtilsFX.renderFacingQuad(
                    tile.xCoord + 0.975,
                    tile.yCoord + 0.75,
                    tile.zCoord + 0.025,
                    0.0f,
                    0.2f,
                    0.9f,
                    frames,
                    i,
                    f,
                    16777215);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            UtilsFX.renderFacingQuad(
                    tile.xCoord + 0.975,
                    tile.yCoord + 0.75,
                    tile.zCoord + 0.975,
                    0.0f,
                    0.2f,
                    0.9f,
                    frames,
                    i,
                    f,
                    16777215);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            UtilsFX.renderFacingQuad(
                    tile.xCoord + 0.025,
                    tile.yCoord + 0.75,
                    tile.zCoord + 0.975,
                    0.0f,
                    0.2f,
                    0.9f,
                    frames,
                    i,
                    f,
                    16777215);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_BLEND);
        }
        if (((TileSoulforge) tile).souls > 0) {
            final double offset = 6.283185307179586 / ((TileSoulforge) tile).souls;
            final int frames = 16;
            final double radian = Math.toRadians((int) (nt / 40000000L % 360L));
            final double dist = 0.1 + 0.1 * Math.cos(radian);
            UtilsFX.bindTexture("thaumichorizons", TileSoulforgeRender.tx3);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_CULL_FACE);
            for (int which = 0; which < ((TileSoulforge) tile).souls; ++which) {
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                UtilsFX.renderFacingQuad(
                        tile.xCoord + 0.5 + dist * Math.sin(2.0 * radian + offset * which),
                        tile.yCoord + 0.85,
                        tile.zCoord + 0.5 + dist * Math.cos(2.0 * radian + offset * which),
                        0.0f,
                        0.1f,
                        0.9f,
                        frames,
                        (int) (nt / 40000000L % frames),
                        f,
                        16777215);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
            }
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    public void renderBrains(final TileSoulforge te, final double x, final double y, final double z, final float f) {
        GL11.glPushMatrix();
        if (te != null) {
            final float f2 = te.rota;
            GL11.glRotatef(f2, 0.0f, 1.0f, 0.0f);
        }
        GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.0f, -0.55f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", "textures/models/brain.png");
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        this.brain.render();
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        if (te != null) {
            final float f2 = te.rota;
            GL11.glRotatef(f2, 0.0f, 1.0f, 0.0f);
        }
        GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.0f, -0.55f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", "textures/models/brain.png");
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        this.brain.render();
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        GL11.glTranslatef(0.0f, -1.25f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", "textures/models/jarbrine2.png");
        this.forge.renderBrine();
    }

    static {
        TileSoulforgeRender.tx1 = "textures/models/soulforge.png";
        TileSoulforgeRender.tx2 = "textures/items/lightningringv.png";
        TileSoulforgeRender.tx3 = "textures/misc/soul.png";
    }
}
