//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.item;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.client.renderer.model.ModelInjector;

import thaumcraft.client.lib.UtilsFX;

public class ItemInjectorRender implements IItemRenderer {

    private final ModelBase injector;
    private final String tx1;

    public ItemInjectorRender() {
        this.injector = new ModelInjector();
        this.tx1 = "textures/models/injector.png";
    }

    public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
        return true;
    }

    public boolean shouldUseRenderHelper(final IItemRenderer.ItemRenderType type, final ItemStack item,
            final IItemRenderer.ItemRendererHelper helper) {
        return helper != IItemRenderer.ItemRendererHelper.BLOCK_3D;
    }

    public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
        int ticksUsed = 0;
        int rotation = 0;
        if (item.stackTagCompound != null) {
            ticksUsed = item.stackTagCompound.getInteger("usetime");
            rotation = item.stackTagCompound.getInteger("rotation");
            final int rotationTarget = item.stackTagCompound.getInteger("rotationTarget");
            if (rotation < rotationTarget) {
                ++rotation;
                item.stackTagCompound.setInteger("rotation", rotation);
            } else if (rotation > rotationTarget) {
                --rotation;
                item.stackTagCompound.setInteger("rotation", rotation);
            }
        }
        float f = ticksUsed / 30.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        if (f > 1.0f) {
            f = 1.0f;
        }
        final ItemRenderer ir = RenderManager.instance.itemRenderer;
        GL11.glPushMatrix();
        GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
        if (type != IItemRenderer.ItemRenderType.INVENTORY) {
            if (type == IItemRenderer.ItemRenderType.ENTITY) {
                GL11.glTranslated(0.0, -0.5, 0.0);
            } else if (type != IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glTranslated(0.25, -1.0, -1.25);
                GL11.glRotated(-45.0, 0.0, 0.0, 1.0);
                GL11.glRotated(-90.0, 0.0, 1.0, 0.0);
            } else {
                GL11.glScaled(4.0, 4.0, 4.0);
                GL11.glTranslated(1.5, 0.0, 0.0);
                GL11.glRotated(230.0, 0.0, 1.0, 0.0);
            }
        } else {
            GL11.glTranslated(0.38, -0.1, 0.38);
            GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
        }
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", this.tx1));
        this.injector.render(
                null,
                f * (float) Math.PI / 16.0f,
                f * (float) Math.PI / 4.0f,
                (float) rotation,
                f,
                0.0f,
                0.125f);
        GL11.glPopMatrix();
    }
}
