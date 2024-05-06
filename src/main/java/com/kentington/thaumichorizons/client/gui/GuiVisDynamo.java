//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import java.awt.Color;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerVisDynamo;
import com.kentington.thaumichorizons.common.tiles.TileVisDynamo;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.Config;

@SideOnly(Side.CLIENT)
public class GuiVisDynamo extends GuiContainer {

    TileVisDynamo tile;
    int flashX;
    int flashY;
    Color flashColor;
    int flashTimer;

    public GuiVisDynamo(final EntityPlayer player, final TileVisDynamo tileEntity) {
        super(new ContainerVisDynamo(player, tileEntity));
        this.flashColor = null;
        this.flashTimer = 0;
        this.tile = tileEntity;
        this.xSize = 111;
        this.ySize = 104;
    }

    protected void drawGuiContainerBackgroundLayer(final float par1, final int par2, final int par3) {
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

    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        UtilsFX.drawTag(11, 12, Aspect.AIR, 0.0f, 0, this.zLevel, 771, 1.0f, !this.tile.provideAer);
        UtilsFX.drawTag(83, 11, Aspect.EARTH, 0.0f, 0, this.zLevel, 771, 1.0f, !this.tile.provideTerra);
        UtilsFX.drawTag(11, 45, Aspect.FIRE, 0.0f, 0, this.zLevel, 771, 1.0f, !this.tile.provideIgnis);
        UtilsFX.drawTag(83, 45, Aspect.WATER, 0.0f, 0, this.zLevel, 771, 1.0f, !this.tile.provideAqua);
        UtilsFX.drawTag(11, 78, Aspect.ORDER, 0.0f, 0, this.zLevel, 771, 1.0f, !this.tile.provideOrdo);
        UtilsFX.drawTag(83, 78, Aspect.ENTROPY, 0.0f, 0, this.zLevel, 771, 1.0f, !this.tile.providePerditio);
        if (this.flashTimer > 0) {
            --this.flashTimer;
            this.drawFlash();
        }
    }

    protected void mouseClicked(final int par1, final int par2, final int par3) {
        super.mouseClicked(par1, par2, par3);
        final int gx = (this.width - this.xSize) / 2;
        final int gy = (this.height - this.ySize) / 2;
        int x = par1 - (gx + 11);
        int y = par2 - (gy + 12);
        if (x >= 0 && y >= 0 && x <= 16 && y <= 16) {
            this.tile.provideAer = !this.tile.provideAer;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
            this.flashTimer = 8;
            this.flashColor = new Color(Aspect.AIR.getColor());
            this.flashX = par1 - gx - 8;
            this.flashY = par2 - gy - 8;
            this.mc.renderViewEntity.worldObj.playSound(
                    this.mc.renderViewEntity.posX,
                    this.mc.renderViewEntity.posY,
                    this.mc.renderViewEntity.posZ,
                    "thaumcraft:hhoff",
                    0.2f,
                    1.0f + this.mc.renderViewEntity.worldObj.rand.nextFloat() * 0.1f,
                    false);
            return;
        }
        x = par1 - (gx + 83);
        if (x >= 0 && y >= 0 && x <= 16 && y <= 16) {
            this.tile.provideTerra = !this.tile.provideTerra;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 2);
            this.flashTimer = 8;
            this.flashColor = new Color(Aspect.EARTH.getColor());
            this.flashX = par1 - gx - 8;
            this.flashY = par2 - gy - 8;
            this.mc.renderViewEntity.worldObj.playSound(
                    this.mc.renderViewEntity.posX,
                    this.mc.renderViewEntity.posY,
                    this.mc.renderViewEntity.posZ,
                    "thaumcraft:hhoff",
                    0.2f,
                    1.0f + this.mc.renderViewEntity.worldObj.rand.nextFloat() * 0.1f,
                    false);
            return;
        }
        x = par1 - (gx + 11);
        y = par2 - (gy + 43);
        if (x >= 0 && y >= 0 && x <= 16 && y <= 16) {
            this.tile.provideIgnis = !this.tile.provideIgnis;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 3);
            this.flashTimer = 8;
            this.flashColor = new Color(Aspect.FIRE.getColor());
            this.flashX = par1 - gx - 8;
            this.flashY = par2 - gy - 8;
            this.mc.renderViewEntity.worldObj.playSound(
                    this.mc.renderViewEntity.posX,
                    this.mc.renderViewEntity.posY,
                    this.mc.renderViewEntity.posZ,
                    "thaumcraft:hhoff",
                    0.2f,
                    1.0f + this.mc.renderViewEntity.worldObj.rand.nextFloat() * 0.1f,
                    false);
            return;
        }
        x = par1 - (gx + 83);
        if (x >= 0 && y >= 0 && x <= 16 && y <= 16) {
            this.tile.provideAqua = !this.tile.provideAqua;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 4);
            this.flashTimer = 8;
            this.flashColor = new Color(Aspect.WATER.getColor());
            this.flashX = par1 - gx - 8;
            this.flashY = par2 - gy - 8;
            this.mc.renderViewEntity.worldObj.playSound(
                    this.mc.renderViewEntity.posX,
                    this.mc.renderViewEntity.posY,
                    this.mc.renderViewEntity.posZ,
                    "thaumcraft:hhoff",
                    0.2f,
                    1.0f + this.mc.renderViewEntity.worldObj.rand.nextFloat() * 0.1f,
                    false);
            return;
        }
        x = par1 - (gx + 11);
        y = par2 - (gy + 78);
        if (x >= 0 && y >= 0 && x <= 16 && y <= 16) {
            this.tile.provideOrdo = !this.tile.provideOrdo;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 5);
            this.flashTimer = 8;
            this.flashColor = new Color(Aspect.ORDER.getColor());
            this.flashX = par1 - gx - 8;
            this.flashY = par2 - gy - 8;
            this.mc.renderViewEntity.worldObj.playSound(
                    this.mc.renderViewEntity.posX,
                    this.mc.renderViewEntity.posY,
                    this.mc.renderViewEntity.posZ,
                    "thaumcraft:hhoff",
                    0.2f,
                    1.0f + this.mc.renderViewEntity.worldObj.rand.nextFloat() * 0.1f,
                    false);
            return;
        }
        x = par1 - (gx + 83);
        if (x >= 0 && y >= 0 && x <= 16 && y <= 16) {
            this.tile.providePerditio = !this.tile.providePerditio;
            this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 6);
            this.flashTimer = 8;
            this.flashColor = new Color(Aspect.ENTROPY.getColor());
            this.flashX = par1 - gx - 8;
            this.flashY = par2 - gy - 8;
            this.mc.renderViewEntity.worldObj.playSound(
                    this.mc.renderViewEntity.posX,
                    this.mc.renderViewEntity.posY,
                    this.mc.renderViewEntity.posZ,
                    "thaumcraft:hhoff",
                    0.2f,
                    1.0f + this.mc.renderViewEntity.worldObj.rand.nextFloat() * 0.1f,
                    false);
        }
    }

    private void drawFlash() {
        float red = this.flashColor.getRed() / 255.0f;
        float green = this.flashColor.getGreen() / 255.0f;
        float blue = this.flashColor.getBlue() / 255.0f;
        if (Config.colorBlind) {
            red /= 1.8f;
            green /= 1.8f;
            blue /= 1.8f;
        }
        GL11.glPushMatrix();
        UtilsFX.bindTexture(ParticleEngine.particleTexture);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glTranslated(this.flashX, this.flashY, 0.0);
        final Tessellator tessellator = Tessellator.instance;
        final int part = this.flashTimer;
        final float var8 = 0.5f + part / 8.0f;
        final float var9 = var8 + 0.0624375f;
        final float var10 = 0.5f;
        final float var11 = var10 + 0.0624375f;
        tessellator.startDrawingQuads();
        tessellator.setBrightness(240);
        tessellator.setColorRGBA_F(red, green, blue, 1.0f);
        tessellator.addVertexWithUV(0.0, 16.0, this.zLevel, var9, var11);
        tessellator.addVertexWithUV(16.0, 16.0, this.zLevel, var9, var10);
        tessellator.addVertexWithUV(16.0, 0.0, this.zLevel, var8, var10);
        tessellator.addVertexWithUV(0.0, 0.0, this.zLevel, var8, var11);
        tessellator.draw();
        GL11.glPopMatrix();
    }
}
