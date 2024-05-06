//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.block;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.tiles.TileVortexStabilizer;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import thaumcraft.client.renderers.block.BlockRenderer;

public class BlockVortexStabilizerRender extends BlockRenderer implements ISimpleBlockRenderingHandler {

    public void renderInventoryBlock(final Block block, final int metadata, final int modelId,
            final RenderBlocks renderer) {
        GL11.glPushMatrix();
        GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        final TileVortexStabilizer tc = new TileVortexStabilizer();
        tc.blockMetadata = metadata;
        TileEntityRendererDispatcher.instance.renderTileEntityAt(tc, 0.0, 0.0, 0.0, 0.0f);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
    }

    public boolean renderWorldBlock(final IBlockAccess world, final int x, final int y, final int z, final Block block,
            final int modelId, final RenderBlocks renderer) {
        return false;
    }

    public boolean shouldRender3DInInventory(final int modelId) {
        return true;
    }

    public int getRenderId() {
        return ThaumicHorizons.blockVortexStabilizerRI;
    }
}
