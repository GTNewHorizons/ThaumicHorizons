//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityBlastPhial extends EntityPotion {

    public EntityBlastPhial(final World p_i1790_1_) {
        super(p_i1790_1_);
    }

    public EntityBlastPhial(final World p_i1790_1_, final EntityLivingBase p_i1790_2_, final float power,
            final ItemStack p_i1790_3_) {
        super(p_i1790_1_, p_i1790_2_, p_i1790_3_);
        this.setSize(0.25f, 0.25f);
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(
                p_i1790_2_.posX,
                p_i1790_2_.posY + p_i1790_2_.getEyeHeight(),
                p_i1790_2_.posZ,
                p_i1790_2_.rotationYaw,
                p_i1790_2_.rotationPitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0f * (float) Math.PI) * 0.16f;
        this.posY -= 0.10000000149011612;
        this.posZ -= MathHelper.sin(this.rotationYaw / 180.0f * (float) Math.PI) * 0.16f;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.yOffset = 0.0f;
        this.motionX = -MathHelper.sin(this.rotationYaw / 180.0f * (float) Math.PI)
                * MathHelper.cos(this.rotationPitch / 180.0f * (float) Math.PI);
        this.motionZ = MathHelper.cos(this.rotationYaw / 180.0f * (float) Math.PI)
                * MathHelper.cos(this.rotationPitch / 180.0f * (float) Math.PI);
        this.motionY = -MathHelper.sin(this.rotationPitch / 180.0f * (float) Math.PI);
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, power * 1.5f, 1.0f);
    }

    public int getPotionDamage() {
        return 8229;
    }
}
