//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityGiantBrainyZombie;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;

public class EntityGravekeeper extends EntityOcelot {

    public EntityGravekeeper(final World p_i1688_1_) {
        super(p_i1688_1_);
        List<EntityAITasks.EntityAITaskEntry> task = tasks.taskEntries;
        this.tasks.removeTask(task.get(6).action);
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntitySkeleton.class, 0, false));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget(this, EntityZombie.class, 0, false));
        this.targetTasks.addTask(4, new EntityAINearestAttackableTarget(this, EntityWither.class, 0, false));
        this.targetTasks.addTask(5, new EntityAINearestAttackableTarget(this, EntityPigZombie.class, 0, false));
        this.targetTasks.addTask(6, new EntityAINearestAttackableTarget(this, EntityGiantZombie.class, 0, false));
        this.targetTasks.addTask(7, new EntityAINearestAttackableTarget(this, EntityBrainyZombie.class, 0, false));
        this.targetTasks.addTask(8, new EntityAINearestAttackableTarget(this, EntityEldritchGuardian.class, 0, false));
        this.targetTasks.addTask(9, new EntityAINearestAttackableTarget(this, EntityGiantBrainyZombie.class, 0, false));
        this.targetTasks.addTask(10, new EntityAINearestAttackableTarget(this, EntityInhabitedZombie.class, 0, false));
    }

    @Override
    public boolean attackEntityAsMob(final Entity p_70652_1_) {
        return (p_70652_1_ instanceof EntityLivingBase && ((EntityLivingBase) p_70652_1_).isEntityUndead())
                || p_70652_1_.attackEntityFrom(DamageSource.causeMobDamage(this), 2.0f);
    }

    @Override
    public String getCommandSenderName() {
        return this.hasCustomNameTag() ? this.getCustomNameTag()
                : (this.isTamed() ? StatCollector.translateToLocal("entity.ThaumicHorizons.Gravekeeper.name")
                        : super.getCommandSenderName());
    }

    @Override
    public IChatComponent func_145748_c_() {
        if (this.hasCustomNameTag() || !this.isTamed()) {
            return super.func_145748_c_();
        } else {
            return new ChatComponentTranslation("entity.ThaumicHorizons.Gravekeeper.name");
        }
    }

    @Override
    public void updateAITick() {
        super.updateAITick();
        final List<EntityLivingBase> critters = this.worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(
                        this.posX - 5.0,
                        this.posY - 5.0,
                        this.posZ - 5.0,
                        this.posX + 5.0,
                        this.posY + 5.0,
                        this.posZ + 5.0));
        for (final EntityLivingBase ent : critters) {
            if (ent.isEntityUndead()) {
                ent.setFire(1);
                Thaumcraft.proxy.beam(
                        this.worldObj,
                        this.posX,
                        this.posY + this.height / 2.0f,
                        this.posZ,
                        ent.posX,
                        ent.posY + ent.height / 2.0f,
                        ent.posZ,
                        0,
                        16773444,
                        false,
                        2.5f,
                        1);
            }
        }
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
}
