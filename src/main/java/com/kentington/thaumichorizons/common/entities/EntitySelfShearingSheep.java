//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntitySelfShearingSheep extends EntitySheep {

    public EntitySelfShearingSheep(final World p_i1691_1_) {
        super(p_i1691_1_);
    }

    public void onLivingUpdate() {
        if (!this.worldObj.isRemote && !this.getSheared() && this.ticksExisted % 100 == 0) {
            final ArrayList<ItemStack> drops = this.onSheared(
                    new ItemStack(Items.shears),
                    this.worldObj,
                    (int) this.posX,
                    (int) this.posY,
                    (int) this.posZ,
                    0);
            final Random rand = new Random();
            for (final ItemStack stack : drops) {
                final EntityItem entityDropItem;
                final EntityItem ent = entityDropItem = this.entityDropItem(stack, 1.0f);
                entityDropItem.motionY += rand.nextFloat() * 0.05f;
                ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1f;
                ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1f;
            }
        }
        super.onLivingUpdate();
    }
}
