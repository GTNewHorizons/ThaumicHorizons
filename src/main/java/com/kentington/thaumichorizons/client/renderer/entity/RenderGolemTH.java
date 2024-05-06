//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.client.renderer.model.ModelGolemTH;
import com.kentington.thaumichorizons.common.entities.EntityGolemTH;

import thaumcraft.client.lib.UtilsFX;
import thaumcraft.client.renderers.entity.RenderGolemBase;
import thaumcraft.client.renderers.models.entities.ModelGolemAccessories;
import thaumcraft.common.config.ConfigItems;

public class RenderGolemTH extends RenderGolemBase {

    ResourceLocation voidGolem;
    ModelBase damage;
    ModelBase accessories;

    public RenderGolemTH(final ModelBase arg0) {
        super(arg0);
        this.voidGolem = new ResourceLocation("thaumichorizons", "textures/models/golem_void.png");
        this.accessories = new ModelGolemAccessories(0.0f, 30.0f);
        if (arg0 instanceof ModelGolemTH) {
            final ModelGolemTH mg = new ModelGolemTH(false);
            mg.pass = 2;
            this.damage = mg;
        }
    }

    protected ResourceLocation getEntityTexture(final Entity entity) {
        if (entity instanceof final EntityGolemTH golem) {
            if (golem.texture == null && golem.blocky != null && golem.blocky != Blocks.air) {
                golem.loadTexture();
            } else if (golem.texture == null) {
                return this.voidGolem;
            }
            return golem.texture;
        }
        return null;
    }

    public void render(final EntityGolemTH e, final double par2, final double par4, final double par6, final float par8,
            final float par9) {
        super.doRender(e, par2, par4, par6, par8, par9);
    }

    public void doRender(final EntityLiving par1EntityLiving, final double par2, final double par4, final double par6,
            final float par8, final float par9) {
        this.render((EntityGolemTH) par1EntityLiving, par2, par4, par6, par8, par9);
    }

    public void doRender(final Entity par1Entity, final double par2, final double par4, final double par6,
            final float par8, final float par9) {
        this.render((EntityGolemTH) par1Entity, par2, par4, par6, par8, par9);
    }

    protected int shouldRenderPass(final EntityLivingBase entity, final int pass, final float par3) {
        if (pass == 0) {
            final String deco = ((EntityGolemTH) entity).getGolemDecoration();
            if (((EntityGolemTH) entity).getCore() > -1) {
                GL11.glPushMatrix();
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GL11.glTranslatef(0.0875f, -0.96f, 0.15f + (deco.contains("P") ? 0.03f : 0.0f));
                GL11.glScaled(0.175, 0.175, 0.175);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                final Tessellator tessellator = Tessellator.instance;
                final IIcon icon = ConfigItems.itemGolemCore.getIconFromDamage(((EntityGolemTH) entity).getCore());
                final float f1 = icon.getMaxU();
                final float f2 = icon.getMinV();
                final float f3 = icon.getMinU();
                final float f4 = icon.getMaxV();
                this.renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);
                ItemRenderer
                        .renderItemIn2D(tessellator, f1, f2, f3, f4, icon.getIconWidth(), icon.getIconHeight(), 0.2f);
                GL11.glPopMatrix();
            }
            final int upgrades = ((EntityGolemTH) entity).upgrades.length;
            final float shift = 0.08f;
            GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            for (int a = 0; a < upgrades; ++a) {
                GL11.glPushMatrix();
                GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
                GL11.glTranslatef(-0.05f - shift * (upgrades - 1) / 2.0f + shift * a, -1.106f, 0.099f);
                GL11.glScaled(0.1, 0.1, 0.1);
                final Tessellator tessellator2 = Tessellator.instance;
                final IIcon icon2 = ConfigItems.itemGolemUpgrade
                        .getIconFromDamage(((EntityGolemTH) entity).getUpgrade(a));
                final float f5 = icon2.getMaxU();
                final float f6 = icon2.getMinV();
                final float f7 = icon2.getMinU();
                final float f8 = icon2.getMaxV();
                this.renderManager.renderEngine.bindTexture(TextureMap.locationItemsTexture);
                tessellator2.startDrawingQuads();
                tessellator2.setNormal(0.0f, 0.0f, 1.0f);
                tessellator2.addVertexWithUV(0.0, 0.0, 0.0, f5, f8);
                tessellator2.addVertexWithUV(1.0, 0.0, 0.0, f7, f8);
                tessellator2.addVertexWithUV(1.0, 1.0, 0.0, f7, f6);
                tessellator2.addVertexWithUV(0.0, 1.0, 0.0, f5, f6);
                tessellator2.draw();
                GL11.glPopMatrix();
            }
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
        } else {
            if (pass == 1 && (((EntityGolemTH) entity).getGolemDecoration().length() > 0
                    || ((EntityGolemTH) entity).advanced)) {
                UtilsFX.bindTexture("textures/models/golem_decoration.png");
                this.setRenderPassModel(this.accessories);
                return 1;
            }
            if (pass == 2 && ((EntityGolemTH) entity).getHealthPercentage() < 1.0f) {
                UtilsFX.bindTexture("textures/models/golem_damage.png");
                this.setRenderPassModel(this.damage);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f - ((EntityGolemTH) entity).getHealthPercentage());
                return 2;
            }
        }
        return -1;
    }
}
