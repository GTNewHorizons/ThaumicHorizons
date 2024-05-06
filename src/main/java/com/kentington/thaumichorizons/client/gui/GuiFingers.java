//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import java.util.ArrayList;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.MathHelper;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerFingers;
import com.kentington.thaumichorizons.common.container.InventoryFingers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

@SideOnly(Side.CLIENT)
public class GuiFingers extends GuiContainer {

    private final InventoryPlayer ip;
    private final InventoryFingers tileEntity;
    private final int[][] aspectLocs;
    ArrayList<Aspect> primals;

    public GuiFingers(final InventoryPlayer par1InventoryPlayer) {
        super(new ContainerFingers(par1InventoryPlayer));
        this.aspectLocs = new int[][] { { 72, 21 }, { 24, 43 }, { 24, 102 }, { 72, 124 }, { 120, 102 }, { 120, 43 } };
        this.primals = Aspect.getPrimalAspects();
        this.tileEntity = ((ContainerFingers) this.inventorySlots).tileEntity;
        this.ip = par1InventoryPlayer;
        this.ySize = 234;
        this.xSize = 190;
    }

    protected void drawGuiContainerForegroundLayer() {}

    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
        UtilsFX.bindTexture("thaumichorizons", "textures/gui/guifingers.png");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnable(GL11.GL_BLEND);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(GL11.GL_BLEND);
        ItemWandCasting wand = null;
        if (this.tileEntity.getStackInSlot(10) != null
                && this.tileEntity.getStackInSlot(10).getItem() instanceof ItemWandCasting) {
            wand = (ItemWandCasting) this.tileEntity.getStackInSlot(10).getItem();
        }
        AspectList cost = null;
        if (ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.ip.player) != null) {
            cost = ThaumcraftCraftingManager.findMatchingArcaneRecipeAspects(this.tileEntity, this.ip.player);
            int count = 0;
            for (final Aspect primal : this.primals) {
                float amt = cost.getAmount(primal);
                if (cost.getAmount(primal) > 0) {
                    float alpha = 0.5f
                            + (MathHelper.sin((this.ip.player.ticksExisted + count * 10) / 2.0f) * 0.2f - 0.2f);
                    if (wand != null) {
                        amt *= wand.getConsumptionModifier(
                                this.tileEntity.getStackInSlot(10),
                                this.ip.player,
                                primal,
                                true);
                        if (amt * 100.0f <= wand.getVis(this.tileEntity.getStackInSlot(10), primal)) {
                            alpha = 1.0f;
                        }
                    }
                    UtilsFX.drawTag(
                            var5 + this.aspectLocs[count][0] - 8,
                            var6 + this.aspectLocs[count][1] - 8,
                            primal,
                            amt,
                            0,
                            this.zLevel,
                            771,
                            alpha,
                            false);
                }
                if (++count > 5) {
                    break;
                }
            }
        }
        if (wand != null && cost != null
                && !wand.consumeAllVisCrafting(this.tileEntity.getStackInSlot(10), this.ip.player, cost, false)) {
            GL11.glPushMatrix();
            final float var7 = 0.33f;
            GL11.glColor4f(var7, var7, var7, 0.66f);
            GuiFingers.itemRender.renderWithColor = false;
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);
            GuiFingers.itemRender.renderItemAndEffectIntoGUI(
                    this.mc.fontRenderer,
                    this.mc.renderEngine,
                    ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.ip.player),
                    var5 + 160,
                    var6 + 64);
            GuiFingers.itemRender.renderItemOverlayIntoGUI(
                    this.mc.fontRenderer,
                    this.mc.renderEngine,
                    ThaumcraftCraftingManager.findMatchingArcaneRecipe(this.tileEntity, this.ip.player),
                    var5 + 160,
                    var6 + 64);
            GuiFingers.itemRender.renderWithColor = true;
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslatef((float) (var5 + 168), (float) (var6 + 46), 0.0f);
            GL11.glScalef(0.5f, 0.5f, 0.0f);
            final String text = "Insufficient vis";
            final int ll = this.fontRendererObj.getStringWidth(text) / 2;
            this.fontRendererObj.drawString(text, -ll, 0, 15625838);
            GL11.glScalef(1.0f, 1.0f, 1.0f);
            GL11.glPopMatrix();
        }
    }
}
