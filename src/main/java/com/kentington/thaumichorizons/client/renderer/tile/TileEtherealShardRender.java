//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import java.awt.Color;
import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.common.tiles.TileSyntheticNode;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.models.ModelCrystal;

public class TileEtherealShardRender extends TileEntitySpecialRenderer {

    private final ModelCrystal model;
    static String tx1;
    static String tx2;

    public TileEtherealShardRender() {
        this.model = new ModelCrystal();
    }

    public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float f) {
        GL11.glPushMatrix();
        final TileSyntheticNode tco = (TileSyntheticNode) te;
        long red = 255;
        long green = 255;
        long blue = 255;
        long numPoints = 0;
        long numPointsFilled = 0;
        if (tco != null && tco.getAspectsBase() != null
                && tco.getAspects() != null
                && tco.getAspects().size() > 0
                && tco.getAspectsBase().size() > 0) {
            for (final Aspect asp : tco.getAspectsBase().getAspects()) {
                final long amt = tco.getAspectsBase().getAmount(asp);
                final Color col = new Color(asp.getColor());
                red += col.getRed() * amt;
                green += col.getGreen() * amt;
                blue += col.getBlue() * amt;
                numPoints += amt;
                numPointsFilled += tco.getAspects().getAmount(asp);
            }
            red /= numPoints + 1;
            green /= numPoints + 1;
            blue /= numPoints + 1;
            red = Math.min(255, Math.max(red, 0));
            green = Math.min(255, Math.max(green, 0));
            blue = Math.min(255, Math.max(blue, 0));
        }

        try {
            final Color col2 = new Color((int) red, (int) green, (int) blue);
            UtilsFX.bindTexture("textures/models/crystal.png");
            final Random rand = new Random(tco.getBlockMetadata() + tco.xCoord + (long) tco.yCoord * tco.zCoord);
            this.drawCrystal(0, (float) x, (float) y, (float) z, tco.rotation, 0.0f, rand, col2.getRGB(), 1.1f);
            final long nt = System.nanoTime();
            UtilsFX.bindTexture(TileEtherealShardRender.tx2);
            final int frames = 32;
            final int i = (int) ((nt / 40000000L + x) % frames);
            if (tco != null && tco.getAspectsBase() != null
                    && tco.getAspects() != null
                    && tco.getAspectsBase().size() > 0
                    && tco.getAspects().getAspects()[0] != null
                    && tco.getAspectsBase().getAspects()[0] != null) {
                final double offset = 6.283185307179586 / tco.getAspectsBase().size();
                int which = 0;
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
                GL11.glDepthMask(false);
                for (final Aspect asp2 : tco.getAspectsBase().getAspects()) {
                    if (asp2 == null) {
                        break;
                    }
                    final Color colo = new Color(asp2.getColor());
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_BLEND);
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                    final double radian = Math.toRadians(tco.rotation);
                    final double dist = 0.4 + 0.1 * Math.cos(radian);
                    UtilsFX.renderFacingStrip(
                            tco.xCoord + 0.5 + dist * Math.sin(2.0 * radian + offset * which),
                            tco.yCoord + 0.64 + 0.10000000149011612 * Math.sin(Math.toRadians(tco.rotation)),
                            tco.zCoord + 0.5 + dist * Math.cos(2.0 * radian + offset * which),
                            0.0f,
                            0.1f + 0.005f * tco.getAspects().getAmount(asp2),
                            0.9f,
                            frames,
                            1,
                            (int) tco.rotation % frames,
                            f,
                            colo.getRGB());
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glPopMatrix();
                    ++which;
                }
                GL11.glDepthMask(true);
                GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            }
            if (tco != null && tco.drainEntity != null && tco.drainCollision != null) {
                final Entity drainEntity = tco.drainEntity;
                if (drainEntity instanceof EntityPlayer && !((EntityPlayer) drainEntity).isUsingItem()) {
                    tco.drainEntity = null;
                    tco.drainCollision = null;
                    return;
                }
                final MovingObjectPosition drainCollision = tco.drainCollision;
                GL11.glPushMatrix();
                float f2 = 0.0f;
                final int iiud = ((EntityPlayer) drainEntity).getItemInUseDuration();
                if (drainEntity instanceof EntityPlayer) {
                    f2 = MathHelper.sin(iiud / 10.0f) * 10.0f;
                }
                final Vec3 vec3 = Vec3.createVectorHelper(-0.1, -0.1, 0.5);
                vec3.rotateAroundX(
                        -(drainEntity.prevRotationPitch
                                + (drainEntity.rotationPitch - drainEntity.prevRotationPitch) * f) * (float) Math.PI
                                / 180.0f);
                vec3.rotateAroundY(
                        -(drainEntity.prevRotationYaw + (drainEntity.rotationYaw - drainEntity.prevRotationYaw) * f)
                                * (float) Math.PI
                                / 180.0f);
                vec3.rotateAroundY(-f2 * 0.01f);
                vec3.rotateAroundX(-f2 * 0.015f);
                final double d3 = drainEntity.prevPosX + (drainEntity.posX - drainEntity.prevPosX) * f + vec3.xCoord;
                final double d4 = drainEntity.prevPosY + (drainEntity.posY - drainEntity.prevPosY) * f + vec3.yCoord;
                final double d5 = drainEntity.prevPosZ + (drainEntity.posZ - drainEntity.prevPosZ) * f + vec3.zCoord;
                final double d6 = (drainEntity == Minecraft.getMinecraft().thePlayer) ? 0.0
                        : drainEntity.getEyeHeight();
                UtilsFX.drawFloatyLine(
                        d3,
                        d4 + d6,
                        d5,
                        drainCollision.blockX + 0.5,
                        drainCollision.blockY + 0.5,
                        drainCollision.blockZ + 0.5,
                        f,
                        tco.color.getRGB(),
                        "textures/misc/wispy.png",
                        -0.02f,
                        Math.min(iiud, 10) / 10.0f);
                GL11.glPopMatrix();
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            GL11.glPopMatrix();
        }
    }

    private void drawCrystal(final int ori, final float x, final float y, final float z, final float a1, final float a2,
            final Random rand, final int color, final float size) {
        final EntityPlayer p = Minecraft.getMinecraft().thePlayer;
        final float shade = MathHelper.sin((p.ticksExisted + rand.nextInt(10)) / (5.0f + rand.nextFloat())) * 0.075f
                + 0.925f;
        final Color c = new Color(color);
        final float r = c.getRed() / 220.0f;
        final float g = c.getGreen() / 220.0f;
        final float b = c.getBlue() / 220.0f;
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_NORMALIZE);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(x + 0.5f, (float) (y - 0.15f + 0.10000000149011612 * Math.sin(Math.toRadians(a1))), z + 0.5f);
        GL11.glRotatef(a1, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(a2, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(
                (0.15f + rand.nextFloat() * 0.075f) * size,
                (0.5f + rand.nextFloat() * 0.1f) * size,
                (0.15f + rand.nextFloat() * 0.05f) * size);
        final int var19 = (int) (210.0f * shade);
        final int var20 = var19 % 65536;
        final int var21 = var19 / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, var20, var21);
        GL11.glColor4f(r, g, b, 1.0f);
        this.model.render();
        GL11.glScalef(1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    static {
        TileEtherealShardRender.tx1 = "textures/items/lightningringv.png";
        TileEtherealShardRender.tx2 = "textures/misc/nodes.png";
    }
}
