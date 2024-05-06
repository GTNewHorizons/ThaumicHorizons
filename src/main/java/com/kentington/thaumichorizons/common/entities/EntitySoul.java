//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import java.awt.Color;

import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.IMob;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import thaumcraft.common.Thaumcraft;

public class EntitySoul extends EntityFlying implements IMob {

    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;

    public EntitySoul(final World world) {
        super(world);
        this.courseChangeCooldown = 0;
        this.setSize(0.9f, 0.9f);
        this.experienceValue = 5;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(1.0);
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public int decreaseAirSupply(final int par1) {
        return par1;
    }

    public boolean attackEntityFrom(final DamageSource damagesource, final float i) {
        return false;
    }

    protected void entityInit() {
        super.entityInit();
    }

    public void onDeath(final DamageSource par1DamageSource) {
        super.onDeath(par1DamageSource);
        if (this.worldObj.isRemote) {
            Thaumcraft.proxy.burst(this.worldObj, this.posX, this.posY, this.posZ, 1.0f);
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote && this.worldObj.rand.nextBoolean()) {
            final Color color = Color.CYAN;
            Thaumcraft.proxy.wispFX(
                    this.worldObj,
                    this.posX + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.7f,
                    this.posY + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.7f,
                    this.posZ + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.7f,
                    0.1f,
                    color.getRed() / 255.0f,
                    color.getGreen() / 255.0f,
                    color.getBlue() / 255.0f);
        }
    }

    protected void updateEntityActionState() {
        final double attackrange = 16.0;
        final double d = this.waypointX - this.posX;
        final double d2 = this.waypointY - this.posY;
        final double d3 = this.waypointZ - this.posZ;
        double d4 = d * d + d2 * d2 + d3 * d3;
        if (d4 < 1.0 || d4 > 3600.0) {
            this.waypointX = this.posX + (this.rand.nextFloat() * 2.0f - 1.0f) * 16.0;
            this.waypointY = this.posY + (this.rand.nextFloat() * 2.0f - 1.0f) * 16.0;
            this.waypointZ = this.posZ + (this.rand.nextFloat() * 2.0f - 1.0f) * 16.0;
        }
        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown += this.rand.nextInt(5) + 2;
            d4 = MathHelper.sqrt_double(d4);
            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d4)) {
                this.motionX += d / d4 * 0.1;
                this.motionY += d2 / d4 * 0.1;
                this.motionZ += d3 / d4 * 0.1;
            } else {
                this.waypointX = this.posX;
                this.waypointY = this.posY;
                this.waypointZ = this.posZ;
            }
        }
        final float n = -(float) Math.atan2(this.motionX, this.motionZ) * 180.0f / (float) Math.PI;
        this.rotationYaw = n;
        this.renderYawOffset = n;
    }

    private boolean isCourseTraversable(final double d, final double d1, final double d2, final double d3) {
        final double d4 = (this.waypointX - this.posX) / d3;
        final double d5 = (this.waypointY - this.posY) / d3;
        final double d6 = (this.waypointZ - this.posZ) / d3;
        final AxisAlignedBB axisalignedbb = this.boundingBox.copy();
        for (int i = 1; i < d3; ++i) {
            axisalignedbb.offset(d4, d5, d6);
            if (!this.worldObj.getCollidingBoundingBoxes(this, axisalignedbb).isEmpty()) {
                return false;
            }
        }
        final int x = (int) this.waypointX;
        final int y = (int) this.waypointY;
        final int z = (int) this.waypointZ;
        if (this.worldObj.getBlock(x, y, z).getMaterial().isLiquid()) {
            return false;
        }
        for (int a = 0; a < 11; ++a) {
            if (!this.worldObj.isAirBlock(x, y - a, z)) {
                return true;
            }
        }
        return false;
    }

    protected String getLivingSound() {
        return "thaumcraft:wisplive";
    }

    protected String getHurtSound() {
        return "random.fizz";
    }

    protected String getDeathSound() {
        return "thaumcraft:wispdead";
    }

    protected Item getDropItem() {
        return null;
    }

    protected void dropFewItems(final boolean flag, final int i) {}

    protected float getSoundVolume() {
        return 0.25f;
    }

    protected boolean canDespawn() {
        return false;
    }

    public boolean getCanSpawnHere() {
        return true;
    }

    protected boolean isValidLightLevel() {
        return true;
    }
}
