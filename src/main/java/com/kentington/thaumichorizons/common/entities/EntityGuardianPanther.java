//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIFollowOwner;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIOwnerHurtByTarget;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class EntityGuardianPanther extends EntityOcelot implements IEntityInfusedStats {

    public EntityGuardianPanther(final World p_i1688_1_) {
        super(p_i1688_1_);
        this.setSize(1.2f, 1.6f);
        final ArrayList<EntityAITasks.EntityAITaskEntry> toRemove = new ArrayList<>(this.tasks.taskEntries);
        for (final EntityAITasks.EntityAITaskEntry task : toRemove) {
            this.tasks.removeTask(task.action);
        }
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.5f));
        this.tasks.addTask(4, new EntityAIAttackOnCollide(this, 1.0, true));
        this.tasks.addTask(5, new EntityAIFollowOwner(this, 1.0, 10.0f, 5.0f));
        this.tasks.addTask(6, new EntityAIMate(this, 0.8));
        this.tasks.addTask(7, new EntityAIWander(this, 0.8));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0f));
        this.targetTasks.addTask(1, new EntityAIOwnerHurtByTarget(this));
        this.targetTasks.addTask(2, new EntityAIOwnerHurtTarget(this));
        this.targetTasks.addTask(3, new EntityAIHurtByTarget(this, true));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(24.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5000000119209289);
    }

    @Override
    public boolean interact(final EntityPlayer p_70085_1_) {
        final ItemStack itemstack = p_70085_1_.inventory.getCurrentItem();
        if (itemstack != null && itemstack.getItem() instanceof final ItemFood itemfood) {
            if (this.getHealth() < this.getMaxHealth()) {
                --itemstack.stackSize;
                this.heal((float) itemfood.func_150905_g(itemstack));
                if (itemstack.stackSize <= 0) {
                    p_70085_1_.inventory.setInventorySlotContents(p_70085_1_.inventory.currentItem, null);
                }
                return true;
            }
        }
        return super.interact(p_70085_1_);
    }

    @Override
    public boolean attackEntityAsMob(final Entity p_70652_1_) {
        return p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 6.0f);
    }

    @Override
    public String getCommandSenderName() {
        return this.hasCustomNameTag() ? this.getCustomNameTag()
                : (this.isTamed() ? StatCollector.translateToLocal("entity.ThaumicHorizons.GuardianPanther.name")
                        : super.getCommandSenderName());
    }

    @Override
    public IChatComponent func_145748_c_() {
        if (this.hasCustomNameTag() || !this.isTamed()) {
            return super.func_145748_c_();
        } else {
            return new ChatComponentTranslation("entity.ThaumicHorizons.GuardianPanther.name");
        }
    }

    @Override
    public void updateAITick() {
        super.updateAITick();
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        final byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        this.dataWatcher.updateObject(16, (byte) (b0 | 0x4));
    }

    @Override
    public boolean isTamed() {
        return true;
    }

    @Override
    public void resetStats() {
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(24.0);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.5000000119209289);
    }
}
