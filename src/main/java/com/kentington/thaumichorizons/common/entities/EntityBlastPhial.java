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

    public EntityBlastPhial(final World world) {
        super(world);
    }

    public EntityBlastPhial(final World world, final double x, final double y, final double z, final ItemStack stack) {
        super(world, x, y, z, stack);
    }

    public EntityBlastPhial(final World world, final EntityLivingBase thrower, final float power,
            final ItemStack stack) {
        super(world, thrower, stack);
        this.setSize(0.25f, 0.25f);
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(
                thrower.posX,
                thrower.posY + thrower.getEyeHeight(),
                thrower.posZ,
                thrower.rotationYaw,
                thrower.rotationPitch);
        this.posX -= MathHelper.cos(this.rotationYaw / 180.0f * (float) Math.PI) * 0.16f;
        this.posY -= 0.1;
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
