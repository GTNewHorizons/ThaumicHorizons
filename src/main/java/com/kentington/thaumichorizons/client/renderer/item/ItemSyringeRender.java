//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.item;

import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.client.renderer.model.ModelSyringe;
import com.kentington.thaumichorizons.common.items.ItemSyringeBlood;
import com.kentington.thaumichorizons.common.items.ItemSyringeBloodSample;
import com.kentington.thaumichorizons.common.items.ItemSyringeEmpty;
import com.kentington.thaumichorizons.common.items.ItemSyringeInjection;

import thaumcraft.client.lib.UtilsFX;

public class ItemSyringeRender implements IItemRenderer {

    private final ModelSyringe syringe;
    private final String tx1;

    public ItemSyringeRender() {
        this.syringe = new ModelSyringe();
        this.tx1 = "textures/models/syringe.png";
    }

    public boolean handleRenderType(final ItemStack item, final IItemRenderer.ItemRenderType type) {
        return item.getItemDamage() == 0;
    }

    public boolean shouldUseRenderHelper(final IItemRenderer.ItemRenderType type, final ItemStack item,
            final IItemRenderer.ItemRendererHelper helper) {
        return helper != IItemRenderer.ItemRendererHelper.BLOCK_3D;
    }

    public void renderItem(final IItemRenderer.ItemRenderType type, final ItemStack item, final Object... data) {
        if (item == null
                || (!(item.getItem() instanceof ItemSyringeBlood) && !(item.getItem() instanceof ItemSyringeBloodSample)
                        && !(item.getItem() instanceof ItemSyringeInjection)
                        && !(item.getItem() instanceof ItemSyringeEmpty))) {
            return;
        }
        final ItemRenderer ir = RenderManager.instance.itemRenderer;
        GL11.glPushMatrix();
        if (type != IItemRenderer.ItemRenderType.INVENTORY) {
            if (type == IItemRenderer.ItemRenderType.ENTITY) {
                GL11.glTranslated(0.0, -2.0, 0.0);
            } else {
                GL11.glRotatef(-66.0f, 0.0f, 0.0f, 1.0f);
                GL11.glTranslated(-1.0, -2.25, 0.75);
            }
        } else {
            GL11.glTranslated(0.0, -2.75, 0.0);
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        UtilsFX.bindTexture(new ResourceLocation("thaumichorizons", this.tx1));
        this.syringe.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.125f, item);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
