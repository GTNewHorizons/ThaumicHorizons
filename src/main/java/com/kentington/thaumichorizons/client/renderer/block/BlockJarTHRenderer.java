//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.block;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.blocks.BlockSoulJar;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import thaumcraft.client.renderers.block.BlockRenderer;

public class BlockJarTHRenderer extends BlockRenderer implements ISimpleBlockRenderingHandler {

    public void renderInventoryBlock(final Block block, final int metadata, final int modelID,
            final RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        final IIcon i1 = ((BlockSoulJar) block).iconJarTop;
        final IIcon i2 = ((BlockSoulJar) block).iconJarSide;
        block.setBlockBounds(
                BlockJarTHRenderer.W3,
                0.0f,
                BlockJarTHRenderer.W3,
                BlockJarTHRenderer.W13,
                BlockJarTHRenderer.W12,
                BlockJarTHRenderer.W13);
        renderer.setRenderBoundsFromBlock(block);
        drawFaces(renderer, block, ((BlockSoulJar) block).iconJarBottom, i1, i2, i2, i2, i2, true);
        block.setBlockBounds(
                BlockJarTHRenderer.W5,
                BlockJarTHRenderer.W12,
                BlockJarTHRenderer.W5,
                BlockJarTHRenderer.W11,
                BlockJarTHRenderer.W14,
                BlockJarTHRenderer.W11);
        renderer.setRenderBoundsFromBlock(block);
        drawFaces(renderer, block, ((BlockSoulJar) block).iconJarBottom, i1, i2, i2, i2, i2, true);
        GL11.glPopMatrix();
    }

    public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block,
            final int modelId, final RenderBlocks renderer) {
        final int bb = setBrightness(world, x, y, z, block);
        final int metadata = world.getBlockMetadata(x, y, z);
        block.setBlockBounds(
                BlockJarTHRenderer.W3,
                0.0f,
                BlockJarTHRenderer.W3,
                BlockJarTHRenderer.W13,
                BlockJarTHRenderer.W12,
                BlockJarTHRenderer.W13);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
        block.setBlockBounds(
                BlockJarTHRenderer.W5,
                BlockJarTHRenderer.W12,
                BlockJarTHRenderer.W5,
                BlockJarTHRenderer.W11,
                BlockJarTHRenderer.W14,
                BlockJarTHRenderer.W11);
        renderer.setRenderBoundsFromBlock(block);
        renderer.renderStandardBlock(block, x, y, z);
        renderer.clearOverrideBlockTexture();
        block.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
        renderer.setRenderBoundsFromBlock(block);
        return true;
    }

    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }

    public int getRenderId() {
        return ThaumicHorizons.blockJarRI;
    }
}
