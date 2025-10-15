package com.kentington.thaumichorizons.client.gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerVisweaver;
import com.kentington.thaumichorizons.common.tiles.TileVisweaver;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVisweaver extends GuiContainer {

    private static final ResourceLocation gui = new ResourceLocation(
            "thaumichorizons",
            "textures/gui/guivisweaver.png");
    public TileVisweaver visweaver;
    public List<String> tooltip = new ArrayList<>();
    int x, y;
    ItemStack lastTickStack;
    ItemStack currentStack;

    public GuiVisweaver(InventoryPlayer inv, TileVisweaver visweaver) {
        super(new ContainerVisweaver(inv, visweaver));
        this.visweaver = visweaver;
        lastTickStack = visweaver.getStackInSlot(0);
        currentStack = visweaver.getStackInSlot(0);
    }

    @Override
    public void initGui() {
        super.initGui();

        x = (width - xSize) / 2;
        y = (height - ySize) / 2;
    }

    @Override
    public void updateScreen() {
        currentStack = visweaver.getStackInSlot(0);
        lastTickStack = currentStack;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1F, 1F, 1F, 1F);
        mc.renderEngine.bindTexture(gui);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        GL11.glEnable(GL11.GL_BLEND);

        if (visweaver.isWorking()) {
            int pixels = (int) (67D * ((double) visweaver.getInternalVis() / (double) visweaver.getRequiredVis()));

            // Draw aspect color to fill
            Color color = new Color(visweaver.getCvType().getColor());
            GL11.glColor3ub((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
            drawTexturedModalRect(x + 56, y + 35, 185, 10, pixels, 8);
        }

        // Draw aspect "container" over progressbar
        GL11.glColor3f(1F, 1F, 1F);
        drawTexturedModalRect(x + 49, y + 34, 178, 0, 78, 10);

        GL11.glDisable(GL11.GL_BLEND);
    }
}
