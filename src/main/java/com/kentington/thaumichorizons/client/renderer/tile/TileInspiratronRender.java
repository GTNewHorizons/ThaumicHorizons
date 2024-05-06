//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.client.renderer.model.ModelInspiratron;
import com.kentington.thaumichorizons.common.tiles.TileInspiratron;

import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.ModelBrain;
import thaumcraft.client.renderers.models.ModelJar;

public class TileInspiratronRender extends TileEntitySpecialRenderer {

    private final ModelJar model;
    private final ModelBrain brain;
    private final ModelInspiratron inspiratron;
    static String tx1;

    public TileInspiratronRender() {
        this.model = new ModelJar();
        this.brain = new ModelBrain();
        this.inspiratron = new ModelInspiratron();
    }

    public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z,
            final float f) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glTranslatef((float) x + 0.5f, (float) y, (float) z + 0.5f);
        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -0.125f, 0.0f);
        this.renderBrain((TileInspiratron) tile, x, y, z, f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(0.0f, -1.5f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", TileInspiratronRender.tx1);
        this.inspiratron.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
        GL11.glPopMatrix();
    }

    public void renderBrain(final TileInspiratron te, final double x, final double y, final double z, final float f) {
        final float bob = MathHelper.sin(Minecraft.getMinecraft().thePlayer.ticksExisted / 14.0f) * 0.03f + 0.03f;
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0f, -0.8f + bob, 0.0f);
        if (te != null) {
            float f2;
            for (f2 = te.rota - te.rotb; f2 < -(float) Math.PI; f2 += ((float) Math.PI * 2F)) {}
            final float f3 = te.rotb + f2 * f;
            GL11.glRotatef(f3 * 180.0f / (float) Math.PI, 0.0f, 1.0f, 0.0f);
        }
        GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", "textures/models/brain.png");
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        this.brain.render();
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
        UtilsFX.bindTexture("thaumichorizons", "textures/models/jarbrine.png");
        this.model.renderBrine();
    }

    static {
        TileInspiratronRender.tx1 = "textures/models/inspiratron.png";
    }
}
