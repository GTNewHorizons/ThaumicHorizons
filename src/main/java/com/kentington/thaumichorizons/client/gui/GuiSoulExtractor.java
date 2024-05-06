//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerSoulExtractor;
import com.kentington.thaumichorizons.common.tiles.TileSoulExtractor;

import thaumcraft.client.lib.UtilsFX;

public class GuiSoulExtractor extends GuiContainer {

    TileSoulExtractor tile;

    public GuiSoulExtractor(final InventoryPlayer player, final TileSoulExtractor tile) {
        super(new ContainerSoulExtractor(player, tile));
        this.tile = tile;
        this.xSize = 175;
        this.ySize = 165;
    }

    protected void drawGuiContainerBackgroundLayer(final float p_146976_1_, final int p_146976_2_,
            final int p_146976_3_) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guisieve.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        if (this.tile.isExtracting() || this.tile.ticksLeft > 0) {
            final int i1 = this.tile.getTimeRemainingScaled(39);
            this.drawTexturedModalRect(var5 + 91, var6 + 58 - i1, 176, 166 - i1, 35, i1);
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
