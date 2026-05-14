//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.tiles.TileVortex;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.UtilsFX;

@SideOnly(Side.CLIENT)
public class TileVortexRender extends TileEntitySpecialRenderer {

    public static final ResourceLocation nodetex;
    public static final ResourceLocation vortextex;

    public void renderTileEntityAt(final TileEntity tile, final double x, final double y, final double z,
            final float partialTicks) {
        if (!(tile instanceof final TileVortex node) || !node.clientSynced) {
            return;
        }
        renderNode(
                Minecraft.getMinecraft().renderViewEntity,
                64.0,
                true,
                false,
                10.0f,
                tile.xCoord,
                tile.yCoord,
                tile.zCoord,
                partialTicks,
                node.aspects,
                node.count,
                node.collapsing,
                node.beams,
                node.createdDimension,
                node.cheat);
    }

    public static void renderNode(final EntityLivingBase viewer, final double viewDistance, final boolean visible,
            final boolean depthIgnore, final float size, final int x, final int y, final int z,
            final float partialTicks, final AspectList aspects, final int timeOpen, final boolean collapsing,
            final int beams, final boolean plane, final boolean cheat) {
        final long nt = System.nanoTime();
        final int frames = 32;
        if (aspects.size() > 0 && visible) {
            final double distance = viewer.getDistance(x + 0.5, y + 0.5, z + 0.5);
            if (distance > viewDistance) {
                return;
            }
            float alpha = (float) ((viewDistance - distance) / viewDistance);
            // Camera-relative center — avoids float precision loss at large world coords.
            final double cx = x + 0.5 - RenderManager.renderPosX;
            final double cy = y + 0.5 - RenderManager.renderPosY;
            final double cz = z + 0.5 - RenderManager.renderPosZ;
            GL11.glPushMatrix();
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
            GL11.glDepthMask(false);
            if (depthIgnore) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            }
            GL11.glDisable(GL11.GL_CULL_FACE);
            final float bscale = 0.25f;
            GL11.glPushMatrix();
            final float rad = ((float) Math.PI * 2F);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
            final int i = (int) ((nt / 40000000L + x) % frames);
            int count = 0;
            float scale = 0.0f;
            float angle = 0.0f;
            float average = 0.0f;
            UtilsFX.bindTexture(TileVortexRender.nodetex);
            for (Aspect aspect : aspects.getAspects()) {
                if (aspect == null) {
                    aspect = Aspect.WATER;
                }
                if (aspect.getBlend() == 771) {
                    alpha *= 1.5;
                }
                average += aspects.getAmount(aspect);
                GL11.glPushMatrix();
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, aspect.getBlend());
                scale = 0.4f;
                scale *= size;
                final long periodNs = (5000L + 500L * count) * 5_000_000L;
                angle = (float) ((double) (nt % periodNs) / periodNs) * rad;
                if (beams < 6 || timeOpen < 50) {
                    UtilsFX.renderFacingStrip(
                            x + 0.5,
                            y + 0.5,
                            z + 0.5,
                            angle,
                            scale,
                            alpha / Math.max(1.0f, aspects.size() / 2.0f),
                            frames,
                            0,
                            i,
                            partialTicks,
                            aspect.getColor());
                } else {
                    UtilsFX.renderFacingStrip(
                            x + 0.5,
                            y + 0.5,
                            z + 0.5,
                            angle,
                            scale / 3.0f,
                            alpha / Math.max(1.0f, aspects.size() / 2.0f),
                            frames,
                            0,
                            i,
                            partialTicks,
                            aspect.getColor());
                }
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glPopMatrix();
                ++count;
                if (aspect.getBlend() == 771) {
                    alpha /= 1.5;
                }
            }
            average /= aspects.size();
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glColor4f(1.0f, 0.0f, 1.0f, alpha);
            float corescale = 1.0f;
            if (timeOpen < 50 && !collapsing) {
                corescale = timeOpen / 50.0f;
            } else if (collapsing) {
                corescale = 1.0f - timeOpen / 25.0f;
            }
            if (!cheat && (beams < 6 || timeOpen < 50)) {
                UtilsFX.bindTexture(TileVortexRender.vortextex);
                renderVortex(
                        cx,
                        cy,
                        cz,
                        angle * 20.0f * corescale / (1 + 2 * beams),
                        scale / 5.0f * corescale,
                        0.8f,
                        partialTicks,
                        16777215);
            } else {
                UtilsFX.bindTexture(TileVortexRender.nodetex);
                UtilsFX.renderFacingStrip(
                        x + 0.5,
                        y + 0.5,
                        z + 0.5,
                        angle,
                        scale * 0.75f,
                        alpha,
                        frames,
                        2,
                        i,
                        partialTicks,
                        16777215);
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
            if (plane) {
                for (Aspect aspect2 : aspects.getAspects()) {
                    if (aspect2 == null) {
                        aspect2 = Aspect.WATER;
                    }
                    if (aspect2.getBlend() == 771) {
                        alpha *= 1.5;
                    }
                    average += aspects.getAmount(aspect2);
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    scale = 0.4f;
                    scale *= size;
                    final long periodNs2 = (5000L + 500L * count) * 5_000_000L;
                    angle = (float) ((double) (nt % periodNs2) / periodNs2) * rad;
                    UtilsFX.renderFacingStrip(
                            x + 0.5,
                            y + 0.5,
                            z + 0.5,
                            angle,
                            0.5f,
                            alpha / Math.max(1.0f, aspects.size() / 2.0f),
                            frames,
                            0,
                            i,
                            partialTicks,
                            aspect2.getColor());
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                    ++count;
                    if (aspect2.getBlend() == 771) {
                        alpha /= 1.5;
                    }
                }
            }
            GL11.glPopMatrix();
            GL11.glEnable(GL11.GL_CULL_FACE);
            if (depthIgnore) {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
            GL11.glDepthMask(true);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            GL11.glPopMatrix();
        }
    }

    static void renderVortex(final double px, final double py, final double pz, final float angle, final float scale,
            final float alpha, final float partialTicks, final int color) {
        final Tessellator tessellator = Tessellator.instance;
        final float arX = ActiveRenderInfo.rotationX;
        final float arZ = ActiveRenderInfo.rotationZ;
        final float arYZ = ActiveRenderInfo.rotationYZ;
        final float arXY = ActiveRenderInfo.rotationXY;
        final float arXZ = ActiveRenderInfo.rotationXZ;

        // Rotate screen-plane basis vectors (H = right, V = up) by angle.
        // This avoids Rodrigues rotation around a qvec that shifts with every head movement,
        // which caused the vortex to jerk whenever the player moved the camera.
        final float cos = MathHelper.cos(angle);
        final float sin = MathHelper.sin(angle);
        final float hX = cos * arX + sin * arYZ;
        final float hY = sin * arXZ;
        final float hZ = cos * arZ + sin * arXY;
        final float vX = cos * arYZ - sin * arX;
        final float vY = cos * arXZ;
        final float vZ = cos * arXY - sin * arZ;

        tessellator.startDrawingQuads();
        tessellator.setBrightness(220);
        tessellator.setColorRGBA_I(color, (int) (alpha * 255.0f));
        tessellator.setNormal(0.0f, 0.0f, -1.0f);
        tessellator
                .addVertexWithUV(px + (-hX - vX) * scale, py + (-hY - vY) * scale, pz + (-hZ - vZ) * scale, 0.0, 1.0);
        tessellator
                .addVertexWithUV(px + (-hX + vX) * scale, py + (-hY + vY) * scale, pz + (-hZ + vZ) * scale, 1.0, 1.0);
        tessellator.addVertexWithUV(px + (hX + vX) * scale, py + (hY + vY) * scale, pz + (hZ + vZ) * scale, 1.0, 0.0);
        tessellator.addVertexWithUV(px + (hX - vX) * scale, py + (hY - vY) * scale, pz + (hZ - vZ) * scale, 0.0, 0.0);
        tessellator.draw();
    }

    static {
        nodetex = new ResourceLocation("thaumcraft", "textures/misc/nodes.png");
        vortextex = new ResourceLocation("thaumcraft", "textures/misc/vortex.png");
    }
}
