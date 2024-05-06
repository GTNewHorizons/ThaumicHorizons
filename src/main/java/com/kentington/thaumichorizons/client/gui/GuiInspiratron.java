//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerInspiratron;
import com.kentington.thaumichorizons.common.tiles.TileInspiratron;

import thaumcraft.client.lib.UtilsFX;

public class GuiInspiratron extends GuiContainer {

    TileInspiratron tile;

    public GuiInspiratron(final InventoryPlayer player, final TileInspiratron tile) {
        super(new ContainerInspiratron(player, tile));
        this.tile = tile;
        this.xSize = 175;
        this.ySize = 219;
    }

    protected void drawGuiContainerBackgroundLayer(final float p_146976_1_, final int p_146976_2_,
            final int p_146976_3_) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guiinspiratron.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        final int i1 = this.tile.getTimeRemainingScaled(28);
        this.drawTexturedModalRect(var5 + 66, var6 + 102, 176, 158 - i1, 44, i1);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
