//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.tiles.TileSoulJar;

import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigBlocks;

public class ItemJarTHRenderer implements IItemRenderer {

    static String tx3;

    public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
        return true;
    }

    public boolean shouldUseRenderHelper(final IItemRenderer.ItemRenderType type, final ItemStack item,
            final IItemRenderer.ItemRendererHelper helper) {
        return helper != IItemRenderer.ItemRendererHelper.EQUIPPED_BLOCK;
    }

    public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
        if (item.getItem() == Item.getItemFromBlock(ThaumicHorizons.blockJar)) {
            final float short1 = 240.0f;
            final float short2 = 240.0f;
            GL11.glTranslated(0.5, 0.0, 0.5);
            if (type == IItemRenderer.ItemRenderType.ENTITY) {
                GL11.glTranslatef(-0.5f, -0.25f, -0.5f);
            } else if (type == IItemRenderer.ItemRenderType.EQUIPPED && data[1] instanceof EntityPlayer) {
                GL11.glTranslatef(0.0f, 0.0f, -0.5f);
            }
            if (item.hasTagCompound() && item.stackTagCompound.getBoolean("isSoul")) {
                final long nt = System.nanoTime();
                UtilsFX.bindTexture("thaumichorizons", ItemJarTHRenderer.tx3);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glPushMatrix();
                GL11.glTranslated(0.0, 0.4, 0.0);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                UtilsFX.renderAnimatedQuad(0.3f, 0.9f, 16, (int) (nt / 40000000L % 16L), 0.0f, 16777215);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_BLEND);
            } else if (item.hasTagCompound()) {
                final TileSoulJar th = new TileSoulJar();
                GL11.glPushMatrix();
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
                final EntityLivingBase viewer = Minecraft.getMinecraft().thePlayer;
                th.entity = EntityList.createEntityFromNBT(item.getTagCompound(), viewer.worldObj);
                TileEntityRendererDispatcher.instance.renderTileEntityAt(th, 0.0, 0.0, 0.0, 0.0f);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                Minecraft.getMinecraft().entityRenderer.disableLightmap(0.0);
                GL11.glPopMatrix();
            }
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0f, 0.5f, 0.0f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            final RenderBlocks rb = (RenderBlocks) data[0];
            rb.useInventoryTint = true;
            rb.renderBlockAsItem(ConfigBlocks.blockJar, 0, 1.0f);
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_BLEND);
        }
    }

    static {
        ItemJarTHRenderer.tx3 = "textures/misc/soul.png";
    }
}
