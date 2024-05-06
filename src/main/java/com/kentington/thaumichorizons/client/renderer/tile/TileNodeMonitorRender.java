//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.common.tiles.TileNodeMonitor;

import thaumcraft.client.lib.UtilsFX;

public class TileNodeMonitorRender extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private final IModelCustom modelScreen;
    private static final ResourceLocation SCANNER;
    private static final ResourceLocation SCANNERSCREEN;
    static String tx1;
    static String tx2;
    static String tx3;

    public TileNodeMonitorRender() {
        this.model = AdvancedModelLoader.loadModel(TileNodeMonitorRender.SCANNER);
        this.modelScreen = AdvancedModelLoader.loadModel(TileNodeMonitorRender.SCANNERSCREEN);
    }

    public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float f) {
        final TileNodeMonitor tco = (TileNodeMonitor) te;
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5f, (float) (y + 0.5), (float) z + 0.5f);
        GL11.glScalef(0.4f, 0.4f, 0.4f);
        switch (tco.direction) {
            case 1 -> {
                GL11.glTranslatef(0.0f, -0.8f, 0.0f);
                GL11.glRotatef((float) tco.rotation, 0.0f, 1.0f, 0.0f);
            }
            case 0 -> {
                GL11.glTranslatef(0.0f, 0.8f, 0.0f);
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef((float) tco.rotation, 0.0f, 1.0f, 0.0f);
            }
            case 2 -> {
                GL11.glTranslatef(0.0f, 0.0f, 0.8f);
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef((float) tco.rotation, 0.0f, 1.0f, 0.0f);
            }
            case 3 -> {
                GL11.glTranslatef(0.0f, 0.0f, -0.8f);
                GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
                GL11.glRotatef((float) tco.rotation, 0.0f, 1.0f, 0.0f);
            }
            case 4 -> {
                GL11.glTranslatef(0.8f, 0.0f, 0.0f);
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef((float) tco.rotation, 0.0f, 1.0f, 0.0f);
            }
            case 5 -> {
                GL11.glTranslatef(-0.8f, 0.0f, 0.0f);
                GL11.glRotatef(-90.0f, 0.0f, 0.0f, 1.0f);
                GL11.glRotatef((float) tco.rotation, 0.0f, 1.0f, 0.0f);
            }
        }
        UtilsFX.bindTexture("thaumichorizons", TileNodeMonitorRender.tx1);
        this.model.renderAll();
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        if (tco.switchy) {
            UtilsFX.bindTexture("thaumichorizons", TileNodeMonitorRender.tx3);
        } else {
            UtilsFX.bindTexture("thaumichorizons", TileNodeMonitorRender.tx2);
        }
        this.modelScreen.renderAll();
        GL11.glPopMatrix();
    }

    static {
        SCANNER = new ResourceLocation("thaumcraft", "textures/models/scanner.obj");
        SCANNERSCREEN = new ResourceLocation("thaumichorizons", "textures/models/hexagon.obj");
        TileNodeMonitorRender.tx1 = "textures/models/nodemon.png";
        TileNodeMonitorRender.tx2 = "textures/models/nodemonscreen.png";
        TileNodeMonitorRender.tx3 = "textures/models/nodemonscreenactive.png";
    }
}
