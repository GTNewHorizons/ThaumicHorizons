package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.client.renderer.model.ModelVisweaver;
import com.kentington.thaumichorizons.common.tiles.TileVisweaver;

import thaumcraft.client.lib.UtilsFX;

public class TileVisweaverRender extends TileEntitySpecialRenderer {

    private static final ResourceLocation modelTex = new ResourceLocation(
            "thaumichorizons",
            "textures/models/visweaver.png");
    ModelVisweaver model = new ModelVisweaver();

    @Override
    public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

        TileVisweaver visweaver = (TileVisweaver) tileentity;

        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1F, 1F, 1F, 1F);
        GL11.glTranslatef((float) x, (float) y, (float) z);

        UtilsFX.bindTexture(modelTex);

        GL11.glTranslatef(0.5F, 1.5F, 0.5F);
        GL11.glScalef(1F, -1F, -1F);

        model.render(visweaver.isWorking(), visweaver.getTickCounter(), visweaver.getCvType());

        GL11.glRotatef(90F, 1F, 0F, 0F);
        GL11.glTranslatef(0F, 0F, -0.6F);

        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        GL11.glPopMatrix();
    }
}
