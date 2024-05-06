//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.client.fx.FXEssentiaTrail;
import com.kentington.thaumichorizons.client.renderer.model.ModelQuarterBlock;
import com.kentington.thaumichorizons.common.tiles.TileEssentiaDynamo;

import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;

public class TileEssentiaDynamoRender extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private static final ResourceLocation SCANNER;
    static String tx1;
    static String tx2;
    static String tx3;
    private final ModelQuarterBlock base;

    public TileEssentiaDynamoRender() {
        this.model = AdvancedModelLoader.loadModel(TileEssentiaDynamoRender.SCANNER);
        this.base = new ModelQuarterBlock();
    }

    public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float f) {
        final TileEssentiaDynamo tco = (TileEssentiaDynamo) te;
        if (tco.essentia != null && tco.ticksProvided > 0) {
            final FXEssentiaTrail fb = new FXEssentiaTrail(
                    tco.getWorldObj(),
                    tco.xCoord + 0.5,
                    tco.yCoord,
                    tco.zCoord + 0.5,
                    tco.xCoord + 0.5,
                    tco.yCoord + 0.5,
                    tco.zCoord + 0.5,
                    Minecraft.getMinecraft().thePlayer.ticksExisted,
                    tco.essentia.getColor(),
                    0.3f);
            fb.noClip = true;
            ParticleEngine.instance.addEffect(tco.getWorldObj(), fb);
        }
        if (tco.rise >= 0.3f && tco.ticksProvided > 0) {
            GL11.glPushMatrix();
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            final long nt = System.nanoTime();
            UtilsFX.bindTexture(TileEssentiaDynamoRender.tx3);
            final int frames = UtilsFX.getTextureAnimationSize(TileEssentiaDynamoRender.tx3);
            final int i = (int) ((nt / 40000000L + x) % frames);
            UtilsFX.renderFacingQuad(
                    tco.xCoord + 0.5,
                    tco.yCoord + 0.5f,
                    tco.zCoord + 0.5,
                    0.0f,
                    0.2f,
                    0.9f,
                    frames,
                    i,
                    f,
                    tco.essentia.getColor());
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            GL11.glPopMatrix();
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        UtilsFX.bindTexture("thaumichorizons", TileEssentiaDynamoRender.tx2);
        this.base.render();
        GL11.glTranslatef(0.5f, 0.2f + tco.rise, 0.5f);
        GL11.glRotatef(tco.rotation, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(tco.rotation2, 1.0f, 0.0f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", TileEssentiaDynamoRender.tx1);
        GL11.glScalef(0.36f, 0.36f, 0.36f);
        this.model.renderAll();
        GL11.glRotatef(-2.0f * tco.rotation2, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(0.5f, 1.0f, 0.5f);
        this.model.renderAll();
        GL11.glPopMatrix();
    }

    static {
        SCANNER = new ResourceLocation("thaumcraft", "textures/models/scanner.obj");
        TileEssentiaDynamoRender.tx1 = "textures/models/thaumiumring.png";
        TileEssentiaDynamoRender.tx2 = "textures/models/dynamoessentiabase.png";
        TileEssentiaDynamoRender.tx3 = "textures/items/lightningringv.png";
    }
}
