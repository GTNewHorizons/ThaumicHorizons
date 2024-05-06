//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.container.ContainerVat;
import com.kentington.thaumichorizons.common.lib.EntityInfusionProperties;
import com.kentington.thaumichorizons.common.tiles.TileVat;

import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.research.ResearchManager;

public class GuiVat extends GuiContainer {

    TileVat tile;
    EntityPlayer player;

    public GuiVat(final EntityPlayer p, final TileVat t) {
        super(new ContainerVat(p, t));
        this.tile = t;
        this.player = p;
        this.xSize = 176;
        this.ySize = 209;
    }

    protected void drawGuiContainerBackgroundLayer(final float p_146976_1_, final int p_146976_2_,
            final int p_146976_3_) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", "textures/gui/guivat.png"));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        if (!ResearchManager.isResearchComplete(this.player.getCommandSenderName(), "incarnationVat")) {
            this.drawTexturedModalRect(var5 + 58, var6 + 30, 176, 163, 57, 20);
        }
        if (this.tile.getEntity() != null) {
            final EntityLivingBase critter = this.tile.getEntity();
            final float adjustedCritterHealth = critter.getHealth() / 2.0f;
            final float max = critter.getMaxHealth() / 2.0f;
            for (int i = 0; i < (int) max; ++i) {
                int x = var5 + 56 + 7 * i - 63 * (i / 9);
                int y = var6 + 12 + 7 * (i / 9);
                int textureX = 176;
                int height = 6;
                if (adjustedCritterHealth >= i) {
                    this.drawTexturedModalRect(x, y, textureX, 126, 7, height);
                } else {
                    this.drawTexturedModalRect(x, y, textureX, 120, 7, height);
                    if (adjustedCritterHealth >= i - 0.5f) {
                        this.drawTexturedModalRect(x, y, textureX, 126, 4, height);
                    }
                }
            }
        } else if (this.tile.mode == 4 || this.tile.mode == 2) {
            final float adjustedSelfInfusionHealth = this.tile.selfInfusionHealth / 2.0f;
            final float max2 = 10.0f;
            for (int j = 0; j < (int) max2; ++j) {
                int x = var5 + 56 + 7 * j - 63 * (j / 9);
                int y = var6 + 12 + 7 * (j / 9);
                int textureX = 176;
                int height = 6;
                if (adjustedSelfInfusionHealth >= j) {
                    this.drawTexturedModalRect(x, y, textureX, 126, 7, height);
                } else {
                    this.drawTexturedModalRect(x, y, textureX, 120, 7, height);
                    if (adjustedSelfInfusionHealth >= j - 0.5f) {
                        this.drawTexturedModalRect(x, y, textureX, 126, 4, height);
                    }
                }
            }
        }
        if (this.tile.getEntity() != null) {
            final int[] infusions = ((EntityInfusionProperties) this.tile.getEntity()
                    .getExtendedProperties("CreatureInfusion")).getInfusions();
            if (infusions[0] != 0) {
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                for (int k = 0; k < 12; ++k) {
                    if (infusions[k] == 0) {
                        break;
                    }
                    this.drawTexturedModalRect(
                            var5 + 55 + 16 * (k % 4),
                            var6 + 56 + 17 * (k / 4),
                            (infusions[k] - 1) * 16,
                            209,
                            16,
                            16);
                }
            }
        }
        if (this.tile.mode == 4 || this.tile.selfInfusions[0] != 0) {
            final int[] infusions = this.tile.selfInfusions;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            for (int k = 0; k < 12; ++k) {
                if (infusions[k] == 0) {
                    break;
                }
                this.drawTexturedModalRect(
                        var5 + 55 + 16 * (k % 4),
                        var6 + 56 + 17 * (k / 4),
                        (infusions[k] - 1) * 16,
                        225,
                        16,
                        16);
            }
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    protected void drawGuiContainerForegroundLayer(final int par1, final int par2) {}
}
