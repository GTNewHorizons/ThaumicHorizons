//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class EntityEndersteed extends EntityHorse {

    boolean initialized;

    public EntityEndersteed(final World p_i1685_1_) {
        super(p_i1685_1_);
        this.initialized = false;
    }

    @Override
    public void readEntityFromNBT(final NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        if (!(this.initialized = p_70037_1_.getBoolean("initialized"))) {
            final Multimap<String, AttributeModifier> map = HashMultimap.create();
            map.put("generic.movementSpeed", new AttributeModifier("generic.movementSpeed", 0.1, 1));
            map.put("horse.jumpStrength", new AttributeModifier("horse.jumpStrength", 0.25, 1));
            map.put("generic.maxHealth", new AttributeModifier("generic.maxHealth", 4.0, 1));
            this.getAttributeMap().applyAttributeModifiers(map);
            this.initialized = true;
        }
    }

    @Override
    public void writeEntityToNBT(final NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setBoolean("initialized", this.initialized);
    }

    @Override
    public void setJumpPower(final int p_110206_1_) {
        final double blocks = p_110206_1_ / 7.0;
        this.teleportTo(
                this.posX - blocks * Math.sin(Math.toRadians(this.rotationYaw)),
                this.posY,
                this.posZ + blocks * Math.cos(Math.toRadians(this.rotationYaw)));
    }

    @Override
    public String getCommandSenderName() {
        if (this.hasCustomNameTag()) {
            return this.getCustomNameTag();
        }
        return StatCollector.translateToLocal("entity.ThaumicHorizons.Endersteed.name");
    }

    @Override
    public IChatComponent func_145748_c_() {
        if (this.hasCustomNameTag()) {
            return super.func_145748_c_();
        } else {
            return new ChatComponentTranslation("entity.ThaumicHorizons.Endersteed.name");
        }
    }

    protected boolean teleportTo(final double p_70825_1_, final double p_70825_3_, final double p_70825_5_) {
        final EnderTeleportEvent event = new EnderTeleportEvent(this, p_70825_1_, p_70825_3_, p_70825_5_, 0.0f);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return false;
        }
        final double d3 = this.posX;
        final double d4 = this.posY;
        final double d5 = this.posZ;
        this.posX = event.targetX;
        this.posY = MathHelper.floor_double(event.targetY) - 3;
        this.posZ = event.targetZ;
        boolean flag = false;
        final int i = MathHelper.floor_double(this.posX);
        int j = MathHelper.floor_double(this.posY);
        final int k = MathHelper.floor_double(this.posZ);
        for (boolean foundGround = false, foundAir = false; (!foundGround || !foundAir)
                && j < d4 + 4.0; ++j, ++this.posY) {
            final Block block = this.worldObj.getBlock(i, j - 1, k);
            if (block.getMaterial().blocksMovement()) {
                foundGround = true;
            } else if (!foundGround) {
                ++j;
                ++this.posY;
            }
            if (foundGround) {
                this.setPosition(this.posX, this.posY, this.posZ);
                if (this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                        && !this.worldObj.isAnyLiquid(this.boundingBox)) {
                    flag = true;
                    foundAir = true;
                }
            }
        }
        this.setPosition(this.posX, this.posY, this.posZ);
        if (!flag) {
            this.setPosition(d3, d4, d5);
            return false;
        }
        final short short1 = 128;
        for (int l = 0; l < short1; ++l) {
            final double d6 = l / (short1 - 1.0);
            final float f = (this.rand.nextFloat() - 0.5f) * 0.2f;
            final float f2 = (this.rand.nextFloat() - 0.5f) * 0.2f;
            final float f3 = (this.rand.nextFloat() - 0.5f) * 0.2f;
            final double d7 = d3 + (this.posX - d3) * d6 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            final double d8 = d4 + (this.posY - d4) * d6 + this.rand.nextDouble() * this.height;
            final double d9 = d5 + (this.posZ - d5) * d6 + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
            this.worldObj.spawnParticle("portal", d7, d8, d9, f, f2, f3);
        }
        this.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0f, 1.0f);
        this.playSound("mob.endermen.portal", 1.0f, 1.0f);
        return true;
    }
}
