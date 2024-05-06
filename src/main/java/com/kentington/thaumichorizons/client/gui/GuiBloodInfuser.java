//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import java.awt.Color;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerBloodInfuser;
import com.kentington.thaumichorizons.common.tiles.TileBloodInfuser;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;

public class GuiBloodInfuser extends GuiContainer {

    TileBloodInfuser tile;
    AspectList aspectsKnown;
    Aspect[] aspectsSelected;
    int numSelected;
    int offset;
    boolean scrollLClicked;
    boolean scrollRClicked;
    int flashX;
    int flashY;
    Color flashColor;
    int flashTimer;
    int topOut;
    int bottomOut;
    Aspect mousedOver;
    HashMap<Integer, Integer> mousedEffects;
    NBTTagList cachedEffects;

    public GuiBloodInfuser(final EntityPlayer p, final TileBloodInfuser tile) {
        super(new ContainerBloodInfuser(p, tile));
        this.aspectsSelected = new Aspect[8];
        this.numSelected = 0;
        this.offset = 0;
        this.flashColor = null;
        this.flashTimer = 0;
        this.topOut = 2;
        this.bottomOut = 2;
        this.mousedOver = null;
        this.mousedEffects = null;
        this.cachedEffects = null;
        for (final Aspect asp : tile.aspectsSelected.getAspects()) {
            if (asp != null) {
                for (int i = 0; i < tile.aspectsSelected.getAmount(asp); ++i) {
                    this.aspectsSelected[this.numSelected] = asp;
                    ++this.numSelected;
                }
            }
        }
        this.aspectsKnown = Thaumcraft.proxy.getPlayerKnowledge().getAspectsDiscovered(p.getCommandSenderName());
        this.tile = tile;
        this.xSize = 176;
        this.ySize = 219;
        this.cachedEffects = tile.getCurrentEffects();
    }

    protected void drawGuiContainerBackgroundLayer(final float p_146976_1_, final int p_146976_2_,
            final int p_146976_3_) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guibloodinfuser.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {
        this.drawEssentiaSelected();
        this.drawAspectList();
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guibloodinfuser.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        if (this.canScrollLeft()) {
            this.drawTexturedModalRect(111, 75, 177, 136, 24, 8);
        } else {
            this.drawTexturedModalRect(111, 75, 177, 144, 24, 8);
        }
        if (this.canScrollRight()) {
            this.drawTexturedModalRect(135, 75, 201, 136, 24, 8);
        } else {
            this.drawTexturedModalRect(135, 75, 201, 144, 24, 8);
        }
        if (this.tile.mode == 1) {
            this.drawTexturedModalRect(38, 57, 178, 130, 6, 6);
        } else if (this.tile.mode == 2) {
            this.drawTexturedModalRect(38, 70, 178, 130, 6, 6);
        }
        final int gx = (this.width - this.xSize) / 2;
        final int gy = (this.height - this.ySize) / 2;
        int x = par1 - (gx + 15);
        int y = par2 - (gy + 83);
        if (x >= 0 && y >= 0 && x <= 144 && y <= 34) {
            final int ecks = x / 18;
            final int why = y / 18 - 1;
            final int i = ecks * 2 - why;
            Aspect asp = null;
            if (i + this.offset < this.aspectsKnown.size()) {
                asp = this.aspectsKnown.getAspectsSorted()[i + this.offset];
            }
            if (asp != null && (this.mousedOver == null || !asp.getTag().equals(this.mousedOver.getTag()))) {
                this.mousedOver = asp;
                this.mousedEffects = this.tile.getEffects(this.mousedOver);
            }
        } else {
            x = par1 - (gx + 110);
            y = par2 - (gy + 74);
            if (x >= 0 && y >= 0 && x <= 49 && y <= 9) {
                this.mousedOver = null;
            }
        }
        if (this.mousedOver == null) {
            if (this.bottomOut > 2) {
                this.bottomOut -= 2;
            }
            this.drawTexturedModalRect(171, 69, 215 - this.bottomOut, 194, this.bottomOut, 59);
        } else {
            if (this.bottomOut < 38) {
                this.bottomOut += 2;
            }
            this.drawTexturedModalRect(171, 69, 215 - this.bottomOut, 194, this.bottomOut, 59);
            int j = 0;
            int k = 0;
            if (this.bottomOut == 38 && this.mousedEffects != null) {
                for (final Integer key : this.mousedEffects.keySet()) {
                    if (key >= 0) {
                        final Potion potion = Potion.potionTypes[key];
                        if (potion.hasStatusIcon()) {
                            this.mc.getTextureManager().bindTexture(GuiBloodInfuser.field_147001_a);
                            final int l = potion.getStatusIconIndex();
                            this.drawTexturedModalRect(j * 18 + 171, k * 18 + 72, l % 8 * 18, 198 + l / 8 * 18, 18, 18);
                            this.fontRendererObj.drawString(
                                    "" + this.mousedEffects.get(key),
                                    j * 18 + 171,
                                    k * 18 + 72,
                                    Color.GRAY.getRGB());
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                            if (j == 0) {
                                j = 1;
                            } else {
                                j = 0;
                                ++k;
                            }
                        } else {
                            this.mc.getTextureManager()
                                    .bindTexture(new ResourceLocation("thaumichorizons", "textures/misc/potions.png"));
                            int ecks2 = 0; // Potion.heal.id
                            final int why2 = 216;
                            if (potion.getId() == 23) {
                                ecks2 = 36;
                            } else if (potion.getId() == Potion.harm.id) {
                                ecks2 = 18;
                            }
                            this.drawTexturedModalRect(j * 18 + 171, k * 18 + 72, ecks2, why2, 18, 18);
                            this.fontRendererObj.drawString(
                                    "" + this.mousedEffects.get(key),
                                    j * 18 + 171,
                                    k * 18 + 72,
                                    Color.GRAY.getRGB());
                            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                            if (j == 0) {
                                j = 1;
                            } else {
                                j = 0;
                                ++k;
                            }
                        }
                    }
                }
            }
        }
        if (this.cachedEffects != null && this.cachedEffects.tagCount() > 0) {
            UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guibloodinfuser.png"));
            if (this.topOut < 38) {
                this.topOut += 2;
            }
            this.drawTexturedModalRect(171, 25, 215 - this.topOut, 152, this.topOut, 41);
            int j = 0;
            int k = 0;
            if (this.topOut == 38) {
                for (int index = 0; index < this.cachedEffects.tagCount(); ++index) {
                    final byte id = this.cachedEffects.getCompoundTagAt(index).getByte("Id");
                    if (id < 0 || id >= Potion.potionTypes.length) continue;
                    final Potion potion = Potion.potionTypes[id];
                    if (potion != null) {
                        this.mc.getTextureManager().bindTexture(GuiBloodInfuser.field_147001_a);
                        final int l = potion.getStatusIconIndex();
                        this.drawTexturedModalRect(j * 18 + 171, k * 18 + 28, l % 8 * 18, 198 + l / 8 * 18, 18, 18);
                        this.fontRendererObj.drawString(
                                "" + (this.cachedEffects.getCompoundTagAt(index).getByte("Amplifier") + 1),
                                j * 18 + 171,
                                k * 18 + 28,
                                Color.GRAY.getRGB());
                        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                        if (j == 0) {
                            j = 1;
                        } else {
                            j = 0;
                            ++k;
                        }
                    }
                }
            }
        } else {
            if (this.topOut > 2) {
                this.topOut -= 2;
            }
            UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guibloodinfuser.png"));
            this.drawTexturedModalRect(171, 25, 215 - this.topOut, 152, this.topOut, 41);
        }
        if (this.flashTimer > 0) {
            --this.flashTimer;
            this.drawFlash();
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    protected void mouseClicked(final int par1, final int par2, final int par3) {
        super.mouseClicked(par1, par2, par3);
        final int gx = (this.width - this.xSize) / 2;
        final int gy = (this.height - this.ySize) / 2;
        this.scrollLClicked = false;
        this.scrollRClicked = false;
        int x = par1 - (gx + 38);
        int y = par2 - (gy + 57);
        if (x >= 0 && y >= 0 && x <= 6 && y <= 6) {
            if (this.tile.mode == 1) {
                this.tile.mode = 0;
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
            } else {
                this.tile.mode = 1;
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 1);
            }
        }
        y = par2 - (gy + 70);
        if (x >= 0 && y >= 0 && x <= 6 && y <= 6) {
            if (this.tile.mode == 2) {
                this.tile.mode = 0;
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 0);
            } else {
                this.tile.mode = 2;
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, 2);
            }
        }
        x = par1 - (gx + 111);
        y = par2 - (gy + 75);
        if (this.canScrollLeft() && x >= 0 && y >= 0 && x <= 24 && y <= 8) {
            this.offset -= 2;
        }
        x = par1 - (gx + 135);
        if (this.canScrollRight() && x >= 0 && y >= 0 && x <= 24 && y <= 8) {
            this.offset += 2;
        }
        for (int i = 0; i < 16; ++i) {
            x = par1 - (gx + 14 + 18 * (i / 2));
            y = par2 - (gy + 83 + ((i % 2 == 0) ? 18 : 0));
            if (this.offset + i < this.aspectsKnown.getAspectsSorted().length && this.numSelected < 8
                    && x >= 0
                    && x <= 16
                    && y >= 0
                    && y <= 16) {
                this.aspectsSelected[this.numSelected] = this.aspectsKnown.getAspectsSorted()[this.offset + i];
                ++this.numSelected;
                this.tile.aspectsSelected.add(this.aspectsKnown.getAspectsSorted()[this.offset + i], 1);
                this.mc.playerController.sendEnchantPacket(this.inventorySlots.windowId, -1 * (i + this.offset) - 1);
                this.flashTimer = 8;
                this.flashColor = new Color(this.aspectsKnown.getAspectsSorted()[this.offset + i].getColor());
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
        for (int i = 0; i < 8; ++i) {
            x = par1 - (gx + 54 + ((i % 2 == 1) ? 17 : 0));
            y = par2 - (gy + 12 + 17 * (i / 2));
            if (this.numSelected > i && x >= 0
                    && x <= 16
                    && y >= 0
                    && y <= 16
                    && Thaumcraft.proxy.playerKnowledge
                            .hasDiscoveredAspect(this.mc.thePlayer.getCommandSenderName(), this.aspectsSelected[i])) {
                this.tile.aspectsSelected.remove(this.aspectsSelected[i], 1);
                this.mc.playerController
                        .sendEnchantPacket(this.inventorySlots.windowId, 3 + this.findInList(this.aspectsSelected[i]));
                this.flashTimer = 8;
                this.flashColor = new Color(this.aspectsSelected[i].getColor());
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
                for (int j = i; j < 7; ++j) {
                    this.aspectsSelected[j] = this.aspectsSelected[j + 1];
                }
                this.aspectsSelected[7] = null;
                --this.numSelected;
            }
        }
        this.cachedEffects = this.tile.getCurrentEffects();
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

    boolean canScrollLeft() {
        return this.offset > 0;
    }

    boolean canScrollRight() {
        return this.offset + 16 < this.aspectsKnown.size();
    }

    void drawAspectList() {
        final Aspect[] known = this.aspectsKnown.getAspectsSorted();
        for (int i = 0; i < 16 && this.offset + i < known.length; ++i) {
            final Aspect asp = known[this.offset + i];
            UtilsFX.drawTag(
                    14 + 18 * (i / 2),
                    83 + ((i % 2 == 0) ? 18 : 0),
                    asp,
                    0.0f,
                    0,
                    this.zLevel,
                    771,
                    1.0f,
                    false);
        }
    }

    void drawEssentiaSelected() {
        final AspectList alreadyUsed = new AspectList();
        for (int i = 0; i < this.numSelected; ++i) {
            final Aspect asp = this.aspectsSelected[i];
            alreadyUsed.add(asp, 1);
            if (Thaumcraft.proxy.playerKnowledge
                    .hasDiscoveredAspect(this.mc.thePlayer.getCommandSenderName(), this.aspectsSelected[i])) {
                UtilsFX.drawTag(
                        54 + ((i % 2 == 1) ? 17 : 0),
                        12 + 17 * (i / 2),
                        asp,
                        0.0f,
                        0,
                        this.zLevel,
                        771,
                        1.0f,
                        this.tile.aspectsAcquired.getAmount(asp) < alreadyUsed.getAmount(asp));
            } else if (this.tile.aspectsAcquired.getAmount(asp) < alreadyUsed.getAmount(asp)) {
                final Color col = Color.DARK_GRAY;
                this.drawQuestionMark(54 + ((i % 2 == 1) ? 17 : 0), 12 + 17 * (i / 2), col);
            } else {
                final Color col = new Color(asp.getColor());
                this.drawQuestionMark(54 + ((i % 2 == 1) ? 17 : 0), 12 + 17 * (i / 2), col);
            }
        }
    }

    void drawQuestionMark(final int x, final int y, final Color color) {
        final Minecraft mc = Minecraft.getMinecraft();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glPushMatrix();
        mc.renderEngine.bindTexture(new ResourceLocation("thaumcraft", "textures/aspects/_unknown.png"));
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.8f);
        final Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.setColorRGBA_F(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.8f);
        var9.addVertexWithUV(x + 0.0, y + 16.0, this.zLevel, 0.0, 1.0);
        var9.addVertexWithUV(x + 16.0, y + 16.0, this.zLevel, 1.0, 1.0);
        var9.addVertexWithUV(x + 16.0, y + 0.0, this.zLevel, 1.0, 0.0);
        var9.addVertexWithUV(x + 0.0, y + 0.0, this.zLevel, 0.0, 0.0);
        var9.draw();
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    int findInList(final Aspect asp) {
        final Aspect[] aspects = this.aspectsKnown.getAspectsSorted();
        for (int i = 0; i < aspects.length; ++i) {
            if (asp != null && aspects[i] != null && aspects[i].getTag().equals(asp.getTag())) {
                return i;
            }
        }
        return -1;
    }
}
