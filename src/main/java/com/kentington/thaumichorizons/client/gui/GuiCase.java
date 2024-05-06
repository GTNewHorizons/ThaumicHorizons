//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerCase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class GuiCase extends GuiContainer {

    private final int blockSlot;

    public GuiCase(final InventoryPlayer par1InventoryPlayer, final World world, final int x, final int y,
            final int z) {
        super(new ContainerCase(par1InventoryPlayer, world, x, y, z));
        this.blockSlot = par1InventoryPlayer.currentItem;
        this.xSize = 175;
        this.ySize = 232;
    }

    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        UtilsFX.bindTexture("textures/gui/gui_focuspouch.png");
        final float t = this.zLevel;
        this.zLevel = 200.0f;
        GL11.glEnable(GL11.GL_BLEND);
        this.drawTexturedModalRect(8 + this.blockSlot * 18, 209, 240, 0, 16, 16);
        GL11.glDisable(GL11.GL_BLEND);
        this.zLevel = t;
    }

    protected boolean checkHotbarKeys(final int par1) {
        return false;
    }

    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        if (this.mc.thePlayer.inventory.mainInventory[this.blockSlot] == null) {
            this.mc.thePlayer.closeScreen();
        }
        UtilsFX.bindTexture("textures/gui/gui_focuspouch.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        GL11.glEnable(GL11.GL_BLEND);
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(GL11.GL_BLEND);
    }
}
