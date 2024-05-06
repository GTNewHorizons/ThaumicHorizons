//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.client.renderer.model.ModelQuarterBlock;
import com.kentington.thaumichorizons.common.tiles.TileVisDynamo;

import thaumcraft.client.lib.UtilsFX;

public class TileVisDynamoRender extends TileEntitySpecialRenderer {

    private final IModelCustom model;
    private static final ResourceLocation SCANNER;
    static String tx1;
    static String tx2;
    static String tx3;
    private final ModelQuarterBlock base;

    public TileVisDynamoRender() {
        this.model = AdvancedModelLoader.loadModel(TileVisDynamoRender.SCANNER);
        this.base = new ModelQuarterBlock();
    }

    public void renderTileEntityAt(final TileEntity te, final double x, final double y, final double z, final float f) {
        final TileVisDynamo tco = (TileVisDynamo) te;
        if (tco.rise >= 0.3f && tco.ticksProvided > 0) {
            GL11.glPushMatrix();
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            final long nt = System.nanoTime();
            UtilsFX.bindTexture(TileVisDynamoRender.tx3);
            final int frames = UtilsFX.getTextureAnimationSize(TileVisDynamoRender.tx3);
            final int i = (int) ((nt / 40000000L + x) % frames);
            UtilsFX.renderFacingQuad(
                    tco.xCoord + 0.5,
                    tco.yCoord + 0.5f,
                    tco.zCoord + 0.5,
                    0.0f,
                    0.2f,
                    0.9f,
                    frames,
                    i,
                    f,
                    tco.color.getRGB());
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
            GL11.glPopMatrix();
        }
        if (tco.drainEntity != null && tco.drainCollision != null) {
            final Entity drainEntity = tco.drainEntity;
            if (drainEntity instanceof EntityPlayer && !((EntityPlayer) drainEntity).isUsingItem()) {
                tco.drainEntity = null;
                tco.drainCollision = null;
            } else {
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
                        drainCollision.blockX + 0.5,
                        drainCollision.blockY + 0.5,
                        drainCollision.blockZ + 0.5,
                        d3,
                        d4 + d6,
                        d5,
                        f,
                        tco.color.getRGB(),
                        "textures/misc/wispy.png",
                        -0.02f,
                        Math.min(iiud, 10) / 10.0f);
                GL11.glPopMatrix();
            }
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);
        UtilsFX.bindTexture("thaumichorizons", TileVisDynamoRender.tx2);
        this.base.render();
        GL11.glTranslatef(0.5f, 0.2f + tco.rise, 0.5f);
        GL11.glRotatef(tco.rotation, 0.0f, 1.0f, 0.0f);
        GL11.glRotatef(tco.rotation2, 1.0f, 0.0f, 0.0f);
        UtilsFX.bindTexture("thaumichorizons", TileVisDynamoRender.tx1);
        GL11.glScalef(0.36f, 0.36f, 0.36f);
        this.model.renderAll();
        GL11.glRotatef(-2.0f * tco.rotation2, 1.0f, 0.0f, 0.0f);
        GL11.glScalef(0.5f, 1.0f, 0.5f);
        this.model.renderAll();
        GL11.glPopMatrix();
    }

    static {
        SCANNER = new ResourceLocation("thaumcraft", "textures/models/scanner.obj");
        TileVisDynamoRender.tx1 = "textures/models/goldring.png";
        TileVisDynamoRender.tx2 = "textures/models/dynamobase.png";
        TileVisDynamoRender.tx3 = "textures/items/lightningringv.png";
    }
}
