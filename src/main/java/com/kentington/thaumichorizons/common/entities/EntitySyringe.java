//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import java.awt.Color;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.items.ItemSyringeInjection;

import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class EntitySyringe extends Entity implements IProjectile, IEntityAdditionalSpawnData {

    public int color;
    public NBTTagCompound effects;
    private boolean inGround;
    private int field_145791_d;
    private int field_145792_e;
    private int field_145789_f;
    private Block field_145790_g;
    private int inData;
    private int ticksInGround;
    private int ticksInAir;
    private int knockbackStrength;
    private EntityLivingBase shootingEntity;
    private int canBePickedUp;
    private byte arrowShake;
    private double damage;

    public EntitySyringe(final World p_i1753_1_) {
        super(p_i1753_1_);
        this.color = Color.RED.getRGB();
        this.field_145791_d = -1;
        this.field_145792_e = -1;
        this.field_145789_f = -1;
        this.damage = 1.5;
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
    }

    public EntitySyringe(final World p_i1754_1_, final double p_i1754_2_, final double p_i1754_4_,
            final double p_i1754_6_) {
        super(p_i1754_1_);
        this.color = Color.RED.getRGB();
        this.field_145791_d = -1;
        this.field_145792_e = -1;
        this.field_145789_f = -1;
        this.damage = 1.5;
        this.renderDistanceWeight = 10.0;
        this.setSize(0.5f, 0.5f);
        this.setPosition(p_i1754_2_, p_i1754_4_, p_i1754_6_);
        this.yOffset = 0.0f;
    }

    public EntitySyringe(final World p_i1755_1_, final EntityLivingBase p_i1755_2_, final EntityLivingBase p_i1755_3_,
            final float p_i1755_4_, final float p_i1755_5_, final NBTTagCompound tag) {
        super(p_i1755_1_);
        this.color = Color.RED.getRGB();
        this.field_145791_d = -1;
        this.field_145792_e = -1;
        this.field_145789_f = -1;
        this.damage = 1.5;
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = p_i1755_2_;
        this.effects = (NBTTagCompound) tag.copy();
        this.color = this.effects.getInteger("color");
        if (p_i1755_2_ instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }
        this.posY = p_i1755_2_.posY + p_i1755_2_.getEyeHeight() - 0.10000000149011612;
        final double d0 = p_i1755_3_.posX - p_i1755_2_.posX;
        final double d2 = p_i1755_3_.boundingBox.minY + p_i1755_3_.height / 3.0f - this.posY;
        final double d3 = p_i1755_3_.posZ - p_i1755_2_.posZ;
        final double d4 = MathHelper.sqrt_double(d0 * d0 + d3 * d3);
        if (d4 >= 1.0E-7) {
            final float f2 = (float) (Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
            final float f3 = (float) (-(Math.atan2(d2, d4) * 180.0 / 3.141592653589793));
            final double d5 = d0 / d4;
            final double d6 = d3 / d4;
            this.setLocationAndAngles(p_i1755_2_.posX + d5, this.posY, p_i1755_2_.posZ + d6, f2, f3);
            this.yOffset = 0.0f;
            final float f4 = (float) d4 * 0.2f;
            this.setThrowableHeading(d0, d2 + f4, d3, p_i1755_4_, p_i1755_5_);
        }
    }

    public EntitySyringe(final World p_i1756_1_, final EntityLivingBase p_i1756_2_, final float p_i1756_3_,
            final NBTTagCompound tag) {
        super(p_i1756_1_);
        this.color = Color.RED.getRGB();
        this.field_145791_d = -1;
        this.field_145792_e = -1;
        this.field_145789_f = -1;
        this.damage = 1.5;
        this.renderDistanceWeight = 10.0;
        this.shootingEntity = p_i1756_2_;
        this.effects = (NBTTagCompound) tag.copy();
        this.color = this.effects.getInteger("color");
        if (p_i1756_2_ instanceof EntityPlayer) {
            this.canBePickedUp = 1;
        }
        this.setSize(0.5f, 0.5f);
        this.setLocationAndAngles(
                p_i1756_2_.posX,
                p_i1756_2_.posY + p_i1756_2_.getEyeHeight(),
                p_i1756_2_.posZ,
                p_i1756_2_.rotationYaw,
                p_i1756_2_.rotationPitch);
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
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, p_i1756_3_ * 1.5f, 1.0f);
    }

    public void writeEntityToNBT(final NBTTagCompound p_70014_1_) {
        p_70014_1_.setTag("effects", this.effects);
        p_70014_1_.setShort("xTile", (short) this.field_145791_d);
        p_70014_1_.setShort("yTile", (short) this.field_145792_e);
        p_70014_1_.setShort("zTile", (short) this.field_145789_f);
        p_70014_1_.setShort("life", (short) this.ticksInGround);
        p_70014_1_.setByte("inTile", (byte) Block.getIdFromBlock(this.field_145790_g));
        p_70014_1_.setByte("inData", (byte) this.inData);
        p_70014_1_.setByte("shake", this.arrowShake);
        p_70014_1_.setByte("inGround", (byte) (this.inGround ? 1 : 0));
        p_70014_1_.setByte("pickup", (byte) this.canBePickedUp);
        p_70014_1_.setDouble("damage", this.damage);
        p_70014_1_.setInteger("color", this.color);
    }

    public void readEntityFromNBT(final NBTTagCompound p_70037_1_) {
        this.effects = p_70037_1_.getCompoundTag("effects");
        this.field_145791_d = p_70037_1_.getShort("xTile");
        this.field_145792_e = p_70037_1_.getShort("yTile");
        this.field_145789_f = p_70037_1_.getShort("zTile");
        this.ticksInGround = p_70037_1_.getShort("life");
        this.field_145790_g = Block.getBlockById(p_70037_1_.getByte("inTile") & 0xFF);
        this.inData = (p_70037_1_.getByte("inData") & 0xFF);
        this.arrowShake = (byte) (p_70037_1_.getByte("shake") & 0xFF);
        this.inGround = (p_70037_1_.getByte("inGround") == 1);
        if (p_70037_1_.hasKey("damage", 99)) {
            this.damage = p_70037_1_.getDouble("damage");
        }
        if (p_70037_1_.hasKey("pickup", 99)) {
            this.canBePickedUp = p_70037_1_.getByte("pickup");
        } else if (p_70037_1_.hasKey("player", 99)) {
            this.canBePickedUp = (p_70037_1_.getBoolean("player") ? 1 : 0);
        }
        this.color = p_70037_1_.getInteger("color");
    }

    void applyEffects(final Entity entityHit) {
        if (entityHit instanceof final EntityLivingBase ent && !this.worldObj.isRemote) {
            final ItemStack psuedoPotion = new ItemStack(ThaumicHorizons.itemSyringeInjection);
            psuedoPotion.setTagCompound(this.effects);
            final List<PotionEffect> list = ((ItemSyringeInjection) ThaumicHorizons.itemSyringeInjection)
                    .getEffects(psuedoPotion);
            if (list != null) {
                for (final PotionEffect potioneffect : list) {
                    ent.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }
    }

    public void onCollideWithPlayer(final EntityPlayer p_70100_1_) {
        if (!this.worldObj.isRemote && this.inGround && this.arrowShake <= 0) {
            boolean flag = this.canBePickedUp == 1
                    || (this.canBePickedUp == 2 && p_70100_1_.capabilities.isCreativeMode);
            final ItemStack psuedoPotion = new ItemStack(ThaumicHorizons.itemSyringeInjection);
            psuedoPotion.setTagCompound(this.effects);
            if (this.canBePickedUp == 1 && !p_70100_1_.inventory.addItemStackToInventory(psuedoPotion)) {
                flag = false;
            }
            if (flag) {
                this.playSound(
                        "random.pop",
                        0.2f,
                        ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7f + 1.0f) * 2.0f);
                p_70100_1_
                        .onItemPickup(new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, psuedoPotion), 1);
                this.setDead();
            }
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            final float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            final float n = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.141592653589793);
            this.rotationYaw = n;
            this.prevRotationYaw = n;
            final float n2 = (float) (Math.atan2(this.motionY, f) * 180.0 / 3.141592653589793);
            this.rotationPitch = n2;
            this.prevRotationPitch = n2;
        }
        final Block block = this.worldObj.getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
        if (block.getMaterial() != Material.air) {
            block.setBlockBoundsBasedOnState(
                    this.worldObj,
                    this.field_145791_d,
                    this.field_145792_e,
                    this.field_145789_f);
            final AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(
                    this.worldObj,
                    this.field_145791_d,
                    this.field_145792_e,
                    this.field_145789_f);
            if (axisalignedbb != null
                    && axisalignedbb.isVecInside(Vec3.createVectorHelper(this.posX, this.posY, this.posZ))) {
                this.inGround = true;
            }
        }
        if (this.arrowShake > 0) {
            --this.arrowShake;
        }
        if (this.inGround) {
            final int j = this.worldObj.getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);
            if (block == this.field_145790_g && j == this.inData) {
                ++this.ticksInGround;
                if (this.ticksInGround == 1200) {
                    this.setDead();
                }
            } else {
                this.inGround = false;
                this.motionX *= this.rand.nextFloat() * 0.2f;
                this.motionY *= this.rand.nextFloat() * 0.2f;
                this.motionZ *= this.rand.nextFloat() * 0.2f;
                this.ticksInGround = 0;
                this.ticksInAir = 0;
            }
        } else {
            ++this.ticksInAir;
            Vec3 vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            Vec3 vec32 = Vec3
                    .createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            MovingObjectPosition movingobjectposition = this.worldObj.func_147447_a(vec31, vec32, false, true, false);
            vec31 = Vec3.createVectorHelper(this.posX, this.posY, this.posZ);
            vec32 = Vec3
                    .createVectorHelper(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
            if (movingobjectposition != null) {
                vec32 = Vec3.createVectorHelper(
                        movingobjectposition.hitVec.xCoord,
                        movingobjectposition.hitVec.yCoord,
                        movingobjectposition.hitVec.zCoord);
            }
            Entity entity = null;
            final List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(
                    this,
                    this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
            double d0 = 0.0;
            for (Object o : list) {
                final Entity entity2 = (Entity) o;
                if (entity2.canBeCollidedWith() && (entity2 != this.shootingEntity || this.ticksInAir >= 5)) {
                    final float f2 = 0.3f;
                    final AxisAlignedBB axisalignedbb2 = entity2.boundingBox.expand(f2, f2, f2);
                    final MovingObjectPosition movingobjectposition2 = axisalignedbb2.calculateIntercept(vec31, vec32);
                    if (movingobjectposition2 != null) {
                        final double d2 = vec31.distanceTo(movingobjectposition2.hitVec);
                        if (d2 < d0 || d0 == 0.0) {
                            entity = entity2;
                            d0 = d2;
                        }
                    }
                }
            }
            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }
            if (movingobjectposition != null && movingobjectposition.entityHit instanceof final EntityPlayer entityplayer) {
                if (entityplayer.capabilities.disableDamage || (this.shootingEntity instanceof EntityPlayer
                        && !((EntityPlayer) this.shootingEntity).canAttackPlayer(entityplayer))) {
                    movingobjectposition = null;
                }
            }
            if (movingobjectposition != null) {
                if (movingobjectposition.entityHit != null) {
                    final float f3 = MathHelper.sqrt_double(
                            this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    int k = MathHelper.ceiling_double_int(f3 * this.getDamage());
                    if (this.getIsCritical()) {
                        k += this.rand.nextInt(k / 2 + 2);
                    }
                    DamageSource damagesource = null;
                    if (this.shootingEntity == null) {
                        damagesource = DamageSource.causeThrownDamage(this, this);
                    } else {
                        damagesource = DamageSource.causeThrownDamage(this, this.shootingEntity);
                    }
                    if (this.isBurning() && !(movingobjectposition.entityHit instanceof EntityEnderman)) {
                        movingobjectposition.entityHit.setFire(5);
                    }
                    if (movingobjectposition.entityHit.attackEntityFrom(damagesource, (float) k)) {
                        if (movingobjectposition.entityHit instanceof final EntityLivingBase entitylivingbase) {
                            if (!this.worldObj.isRemote) {
                                entitylivingbase.setArrowCountInEntity(entitylivingbase.getArrowCountInEntity() + 1);
                            }
                            if (this.knockbackStrength > 0) {
                                final float f4 = MathHelper
                                        .sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
                                if (f4 > 0.0f) {
                                    movingobjectposition.entityHit.addVelocity(
                                            this.motionX * this.knockbackStrength * 0.6000000238418579 / f4,
                                            0.1,
                                            this.motionZ * this.knockbackStrength * 0.6000000238418579 / f4);
                                }
                            }
                            if (this.shootingEntity != null && this.shootingEntity instanceof EntityLivingBase) {
                                EnchantmentHelper.func_151384_a(entitylivingbase, this.shootingEntity);
                                EnchantmentHelper.func_151385_b(this.shootingEntity, entitylivingbase);
                            }
                            if (this.shootingEntity != null && movingobjectposition.entityHit != this.shootingEntity
                                    && movingobjectposition.entityHit instanceof EntityPlayer
                                    && this.shootingEntity instanceof EntityPlayerMP) {
                                ((EntityPlayerMP) this.shootingEntity).playerNetServerHandler
                                        .sendPacket(new S2BPacketChangeGameState(6, 0.0f));
                            }
                        }
                        this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                        if (!(movingobjectposition.entityHit instanceof EntityEnderman)) {
                            this.setDead();
                            this.applyEffects(movingobjectposition.entityHit);
                        }
                    } else {
                        this.motionX *= -0.10000000149011612;
                        this.motionY *= -0.10000000149011612;
                        this.motionZ *= -0.10000000149011612;
                        this.rotationYaw += 180.0f;
                        this.prevRotationYaw += 180.0f;
                        this.ticksInAir = 0;
                    }
                } else {
                    this.field_145791_d = movingobjectposition.blockX;
                    this.field_145792_e = movingobjectposition.blockY;
                    this.field_145789_f = movingobjectposition.blockZ;
                    this.field_145790_g = this.worldObj
                            .getBlock(this.field_145791_d, this.field_145792_e, this.field_145789_f);
                    this.inData = this.worldObj
                            .getBlockMetadata(this.field_145791_d, this.field_145792_e, this.field_145789_f);
                    this.motionX = (float) (movingobjectposition.hitVec.xCoord - this.posX);
                    this.motionY = (float) (movingobjectposition.hitVec.yCoord - this.posY);
                    this.motionZ = (float) (movingobjectposition.hitVec.zCoord - this.posZ);
                    final float f3 = MathHelper.sqrt_double(
                            this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
                    this.posX -= this.motionX / f3 * 0.05000000074505806;
                    this.posY -= this.motionY / f3 * 0.05000000074505806;
                    this.posZ -= this.motionZ / f3 * 0.05000000074505806;
                    this.playSound("random.bowhit", 1.0f, 1.2f / (this.rand.nextFloat() * 0.2f + 0.9f));
                    this.inGround = true;
                    this.arrowShake = 7;
                    this.setIsCritical(false);
                    if (this.field_145790_g.getMaterial() != Material.air) {
                        this.field_145790_g.onEntityCollidedWithBlock(
                                this.worldObj,
                                this.field_145791_d,
                                this.field_145792_e,
                                this.field_145789_f,
                                this);
                    }
                }
            }
            if (this.getIsCritical()) {
                for (int i = 0; i < 4; ++i) {
                    this.worldObj.spawnParticle(
                            "crit",
                            this.posX + this.motionX * i / 4.0,
                            this.posY + this.motionY * i / 4.0,
                            this.posZ + this.motionZ * i / 4.0,
                            -this.motionX,
                            -this.motionY + 0.2,
                            -this.motionZ);
                }
            }
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            final float f3 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0 / 3.141592653589793);
            this.rotationPitch = (float) (Math.atan2(this.motionY, f3) * 180.0 / 3.141592653589793);
            while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
                this.prevRotationPitch -= 360.0f;
            }
            while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
                this.prevRotationPitch += 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
                this.prevRotationYaw -= 360.0f;
            }
            while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
                this.prevRotationYaw += 360.0f;
            }
            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
            float f5 = 0.99f;
            final float f2 = 0.05f;
            if (this.isInWater()) {
                for (int l = 0; l < 4; ++l) {
                    final float f4 = 0.25f;
                    this.worldObj.spawnParticle(
                            "bubble",
                            this.posX - this.motionX * f4,
                            this.posY - this.motionY * f4,
                            this.posZ - this.motionZ * f4,
                            this.motionX,
                            this.motionY,
                            this.motionZ);
                }
                f5 = 0.8f;
            }
            if (this.isWet()) {
                this.extinguish();
            }
            this.motionX *= f5;
            this.motionY *= f5;
            this.motionZ *= f5;
            this.motionY -= f2;
            this.setPosition(this.posX, this.posY, this.posZ);
            this.func_145775_I();
        }
    }

    public void setThrowableHeading(double p_70186_1_, double p_70186_3_, double p_70186_5_, final float p_70186_7_,
            final float p_70186_8_) {
        final float f2 = MathHelper
                .sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_3_ * p_70186_3_ + p_70186_5_ * p_70186_5_);
        p_70186_1_ /= f2;
        p_70186_3_ /= f2;
        p_70186_5_ /= f2;
        p_70186_1_ += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * p_70186_8_;
        p_70186_3_ += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * p_70186_8_;
        p_70186_5_ += this.rand.nextGaussian() * (this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937 * p_70186_8_;
        p_70186_1_ *= p_70186_7_;
        p_70186_3_ *= p_70186_7_;
        p_70186_5_ *= p_70186_7_;
        this.motionX = p_70186_1_;
        this.motionY = p_70186_3_;
        this.motionZ = p_70186_5_;
        final float f3 = MathHelper.sqrt_double(p_70186_1_ * p_70186_1_ + p_70186_5_ * p_70186_5_);
        final float n = (float) (Math.atan2(p_70186_1_, p_70186_5_) * 180.0 / 3.141592653589793);
        this.rotationYaw = n;
        this.prevRotationYaw = n;
        final float n2 = (float) (Math.atan2(p_70186_3_, f3) * 180.0 / 3.141592653589793);
        this.rotationPitch = n2;
        this.prevRotationPitch = n2;
        this.ticksInGround = 0;
    }

    @SideOnly(Side.CLIENT)
    public void setPositionAndRotation2(final double p_70056_1_, final double p_70056_3_, final double p_70056_5_,
            final float p_70056_7_, final float p_70056_8_, final int p_70056_9_) {}

    @SideOnly(Side.CLIENT)
    public void setVelocity(final double p_70016_1_, final double p_70016_3_, final double p_70016_5_) {
        this.motionX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.motionZ = p_70016_5_;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            final float f = MathHelper.sqrt_double(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            final float n = (float) (Math.atan2(p_70016_1_, p_70016_5_) * 180.0 / 3.141592653589793);
            this.rotationYaw = n;
            this.prevRotationYaw = n;
            final float n2 = (float) (Math.atan2(p_70016_3_, f) * 180.0 / 3.141592653589793);
            this.rotationPitch = n2;
            this.prevRotationPitch = n2;
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    protected void entityInit() {
        this.dataWatcher.addObject(16, (byte) 0);
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    @SideOnly(Side.CLIENT)
    public float getShadowSize() {
        return 0.0f;
    }

    public void setDamage(final double p_70239_1_) {
        this.damage = p_70239_1_;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setKnockbackStrength(final int p_70240_1_) {
        this.knockbackStrength = p_70240_1_;
    }

    public boolean canAttackWithItem() {
        return false;
    }

    public void setIsCritical(final boolean p_70243_1_) {
        final byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        if (p_70243_1_) {
            this.dataWatcher.updateObject(16, (byte) (b0 | 0x1));
        } else {
            this.dataWatcher.updateObject(16, (byte) (b0 & 0xFFFFFFFE));
        }
    }

    public boolean getIsCritical() {
        final byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        return (b0 & 0x1) != 0x0;
    }

    public void writeSpawnData(final ByteBuf data) {
        data.writeInt(this.color);
        data.writeDouble(this.motionX);
        data.writeDouble(this.motionY);
        data.writeDouble(this.motionZ);
        data.writeFloat(this.rotationYaw);
        data.writeFloat(this.rotationPitch);
    }

    public void readSpawnData(final ByteBuf data) {
        this.color = data.readInt();
        this.motionX = data.readDouble();
        this.motionY = data.readDouble();
        this.motionZ = data.readDouble();
        this.rotationYaw = data.readFloat();
        this.rotationPitch = data.readFloat();
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }
}
