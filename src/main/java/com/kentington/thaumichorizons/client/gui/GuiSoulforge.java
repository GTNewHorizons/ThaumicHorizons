//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerSoulforge;
import com.kentington.thaumichorizons.common.tiles.TileSoulforge;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class GuiSoulforge extends GuiContainer {

    TileSoulforge tile;

    public GuiSoulforge(final EntityPlayer player, final TileSoulforge tile) {
        super(new ContainerSoulforge(player, tile));
        this.xSize = 0;
        this.ySize = 0;
    }

    protected void drawGuiContainerBackgroundLayer(final float p_146976_1_, final int p_146976_2_,
            final int p_146976_3_) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guidynamo.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {}
}
