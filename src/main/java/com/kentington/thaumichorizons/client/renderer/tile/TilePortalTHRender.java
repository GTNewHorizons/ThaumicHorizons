//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.tiles.TilePortalTH;

import thaumcraft.client.lib.UtilsFX;

public class TilePortalTHRender extends TileEntitySpecialRenderer {

    public static final ResourceLocation portaltex;

    public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float f) {
        if (te.getWorldObj() != null) {
            this.renderPortal((TilePortalTH) te, x, y, z, f);
        }
    }

    private void renderPortal(final TilePortalTH te, final double x, final double y, final double z, final float f) {
        final long nt = System.nanoTime();
        final long time = nt / 50000000L;
        final int c = (int) Math.min(30.0f, te.opencount + f);
        final int e = (int) Math.min(5.0f, te.opencount + f);
        final float scale = e / 5.0f;
        final float scaley = c / 30.0f;
        UtilsFX.bindTexture(TilePortalTHRender.portaltex);
        GL11.glPushMatrix();
        GL11.glDepthMask(false);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glColor4f(1.0f, 0.0f, 1.0f, 1.0f);
        if (Minecraft.getMinecraft().renderViewEntity instanceof final EntityPlayer player) {
            final Tessellator tessellator = Tessellator.instance;
            final float arX = ActiveRenderInfo.rotationX;
            final float arZ = ActiveRenderInfo.rotationZ;
            final float arYZ = ActiveRenderInfo.rotationYZ;
            final float arXY = ActiveRenderInfo.rotationXY;
            final float arXZ = ActiveRenderInfo.rotationXZ;
            final double iPX = player.prevPosX + (player.posX - player.prevPosX) * f;
            final double iPY = player.prevPosY + (player.posY - player.prevPosY) * f;
            final double iPZ = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
            tessellator.startDrawingQuads();
            tessellator.setBrightness(220);
            switch (te.dimension) {
                case -1 -> {
                    tessellator.setColorRGBA_F(1.0f, 0.1f, 0.1f, 1.0f);
                }
                case 0 -> {
                    tessellator.setColorRGBA_F(0.1f, 0.8f, 0.8f, 1.0f);
                }
                case 1 -> {
                    tessellator.setColorRGBA_F(0.25f, 0.25f, 0.25f, 1.0f);
                }
                default -> {
                    tessellator.setColorRGBA_F(0.5f, 0.5f, 0.1f, 1.0f);
                }
            }
            final double px = x + 0.5;
            final double py = y + 0.5;
            final double pz = z + 0.5;
            final Vec3 v1 = Vec3.createVectorHelper(-arX - arYZ, -arXZ, -arZ - arXY);
            final Vec3 v2 = Vec3.createVectorHelper(-arX + arYZ, arXZ, -arZ + arXY);
            final Vec3 v3 = Vec3.createVectorHelper(arX + arYZ, arXZ, arZ + arXY);
            final Vec3 v4 = Vec3.createVectorHelper(arX - arYZ, -arXZ, arZ - arXY);
            final int frame = (int) time % 16;
            final float f2 = frame / 16.0f;
            final float f3 = f2 + 0.0625f;
            final float f4 = 0.0f;
            final float f5 = 1.0f;
            tessellator.setNormal(0.0f, 0.0f, -1.0f);
            tessellator
                    .addVertexWithUV(px + v1.xCoord * scale, py + v1.yCoord * scaley, pz + v1.zCoord * scale, f2, f5);
            tessellator
                    .addVertexWithUV(px + v2.xCoord * scale, py + v2.yCoord * scaley, pz + v2.zCoord * scale, f3, f5);
            tessellator
                    .addVertexWithUV(px + v3.xCoord * scale, py + v3.yCoord * scaley, pz + v3.zCoord * scale, f3, f4);
            tessellator
                    .addVertexWithUV(px + v4.xCoord * scale, py + v4.yCoord * scaley, pz + v4.zCoord * scale, f2, f4);
            tessellator.draw();
        }
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDepthMask(true);
        GL11.glPopMatrix();
    }

    static {
        portaltex = new ResourceLocation("thaumichorizons", "textures/misc/vortex.png");
    }
}
