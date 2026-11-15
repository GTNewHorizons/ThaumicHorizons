//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import java.util.List;

import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import baubles.api.BaublesApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.baubles.ItemAmuletVis;
import thaumcraft.common.items.wands.ItemWandCasting;

public class EntityFamiliar extends EntityOcelot {

    public EntityFamiliar(final World p_i1688_1_) {
        super(p_i1688_1_);
    }

    @Override
    public String getCommandSenderName() {
        return this.hasCustomNameTag() ? this.getCustomNameTag()
                : (this.isTamed() ? StatCollector.translateToLocal("entity.ThaumicHorizons.Familiar.name")
                        : super.getCommandSenderName());
    }

    @Override
    public IChatComponent func_145748_c_() {
        if (this.hasCustomNameTag() || !this.isTamed()) {
            return super.func_145748_c_();
        } else {
            return new ChatComponentTranslation("entity.ThaumicHorizons.Familiar.name");
        }
    }

    @Override
    public void updateAITick() {
        super.updateAITick();
        if (this.ticksExisted % 10 == 0) {
            final List<EntityPlayer> players = this.worldObj.getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getBoundingBox(
                            this.posX - 5.0,
                            this.posY - 5.0,
                            this.posZ - 5.0,
                            this.posX + 5.0,
                            this.posY + 5.0,
                            this.posZ + 5.0));
            for (final EntityPlayer player : players) {
                if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof final ItemWandCasting wand) {
                    final AspectList al = wand.getAspectsWithRoom(player.getHeldItem());
                    for (final Aspect aspect : al.getAspects()) {
                        if (aspect != null) {
                            wand.storeVis(player.getHeldItem(), aspect, this.getVis(player.getHeldItem(), aspect) + 1);
                        }
                    }
                }
                if (BaublesApi.getBaubles(player).getStackInSlot(0) != null
                        && BaublesApi.getBaubles(player).getStackInSlot(0).getItem() == ConfigItems.itemAmuletVis) {
                    final AspectList al2 = ((ItemAmuletVis) ConfigItems.itemAmuletVis)
                            .getAspectsWithRoom(BaublesApi.getBaubles(player).getStackInSlot(0));
                    for (final Aspect aspect2 : al2.getAspects()) {
                        if (aspect2 != null) {
                            ((ItemAmuletVis) ConfigItems.itemAmuletVis)
                                    .addRealVis(BaublesApi.getBaubles(player).getStackInSlot(0), aspect2, 1, true);
                        }
                    }
                }
            }
        }
    }

    protected void entityInit() {
        super.entityInit();
        final byte b0 = this.dataWatcher.getWatchableObjectByte(16);
        this.dataWatcher.updateObject(16, (byte) (b0 | 0x4));
    }

    public boolean isTamed() {
        return true;
    }

    public int getVis(final ItemStack is, final Aspect aspect) {
        int out = 0;
        if (is.hasTagCompound() && is.stackTagCompound.hasKey(aspect.getTag())) {
            out = is.stackTagCompound.getInteger(aspect.getTag());
        }
        return out;
    }
}
