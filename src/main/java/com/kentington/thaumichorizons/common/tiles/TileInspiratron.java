//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.config.ConfigItems;

public class TileInspiratron extends TileThaumcraft implements ISoulReceiver, ISidedInventory {

    public ItemStack paper;
    public ItemStack knowledge;
    public int progress;
    private final int PROGRESS_MAX = 100;
    public float rota;
    public float rotb;
    public float field_40063_b;
    public float field_40061_d;
    public float field_40059_f;
    public float field_40066_q;

    public void updateEntity() {
        super.updateEntity();
        if (this.worldObj.isRemote) {
            Entity entity = null;
            this.rotb = this.rota;
            if (entity == null) {
                entity = this.worldObj
                        .getClosestPlayer(this.xCoord + 0.5f, this.yCoord + 0.5f, this.zCoord + 0.5f, 6.0);
            }
            if (entity != null) {
                final double d = entity.posX - (this.xCoord + 0.5f);
                final double d2 = entity.posZ - (this.zCoord + 0.5f);
                this.field_40066_q = (float) Math.atan2(d2, d);
                this.field_40059_f += 0.1f;
                if (this.field_40059_f < 0.5f || entity.worldObj.rand.nextInt(40) == 0) {
                    final float f3 = this.field_40061_d;
                    do {
                        this.field_40061_d += entity.worldObj.rand.nextInt(4) - entity.worldObj.rand.nextInt(4);
                    } while (f3 == this.field_40061_d);
                }
            } else {
                this.field_40066_q += 0.01f;
            }
            while (this.rota >= (float) Math.PI) {
                this.rota -= ((float) Math.PI * 2F);
            }
            while (this.rota < -(float) Math.PI) {
                this.rota += ((float) Math.PI * 2F);
            }
            while (this.field_40066_q >= (float) Math.PI) {
                this.field_40066_q -= ((float) Math.PI * 2F);
            }
            while (this.field_40066_q < -(float) Math.PI) {
                this.field_40066_q += ((float) Math.PI * 2F);
            }
            float f4;
            for (f4 = this.field_40066_q - this.rota; f4 < -(float) Math.PI; f4 += ((float) Math.PI * 2F)) {}
            this.rota += f4 * 0.04f;
        }
    }

    @Override
    public void addSoulBits(final int bits) {
        for (int i = 0; i < bits; ++i) {
            if (Math.random() >= 0.97) {
                this.worldObj.playSoundEffect(
                        this.xCoord + 0.5,
                        this.yCoord + 0.5,
                        this.zCoord + 0.5,
                        "thaumcraft:write",
                        0.2f,
                        this.worldObj.rand.nextFloat());
                ++this.progress;
            }
        }
        if (this.progress >= 100) {
            this.progress -= 100;
            if (this.knowledge == null) {
                this.knowledge = new ItemStack(ConfigItems.itemResource, 1, 9);
            } else {
                final ItemStack knowledge = this.knowledge;
                ++knowledge.stackSize;
            }
            final ItemStack paper = this.paper;
            --paper.stackSize;
            if (this.paper.stackSize <= 0) {
                this.paper = null;
            }
        }
    }

    @Override
    public boolean canAcceptSouls() {
        return this.paper != null && this.paper.stackSize > 0
                && (this.knowledge == null || this.knowledge.stackSize < 64);
    }

    public int getSizeInventory() {
        return 2;
    }

    public ItemStack getStackInSlot(final int p_70301_1_) {
        if (p_70301_1_ == 0) {
            return this.paper;
        }
        if (p_70301_1_ == 1) {
            return this.knowledge;
        }
        return null;
    }

    public ItemStack decrStackSize(final int p_70298_1_, final int p_70298_2_) {
        if (p_70298_1_ == 0) {
            final int oldsize = this.paper.stackSize;
            final ItemStack paper = this.paper;
            paper.stackSize -= p_70298_2_;
            if (this.paper.stackSize <= 0) {
                this.paper = null;
            }
            return new ItemStack(Items.paper, Math.min(p_70298_2_, oldsize));
        }
        if (p_70298_1_ == 1) {
            final int oldsize = this.knowledge.stackSize;
            final ItemStack knowledge = this.knowledge;
            knowledge.stackSize -= p_70298_2_;
            if (this.knowledge.stackSize <= 0) {
                this.knowledge = null;
            }
            return new ItemStack(ConfigItems.itemResource, Math.min(p_70298_2_, oldsize), 9);
        }
        return null;
    }

    public ItemStack getStackInSlotOnClosing(final int p_70304_1_) {
        return null;
    }

    public void setInventorySlotContents(final int p_70299_1_, final ItemStack p_70299_2_) {
        if (p_70299_1_ == 0) {
            this.paper = p_70299_2_;
        } else if (p_70299_1_ == 1) {
            this.knowledge = p_70299_2_;
        }
    }

    public String getInventoryName() {
        return "container.inspiratron";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(final EntityPlayer p_70300_1_) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && p_70300_1_.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }

    public void openInventory() {}

    public void closeInventory() {}

    public boolean isItemValidForSlot(final int p_94041_1_, final ItemStack p_94041_2_) {
        if (p_94041_1_ == 0) {
            return p_94041_2_.isItemEqual(new ItemStack(Items.paper));
        }
        return p_94041_1_ == 1 && p_94041_2_.isItemEqual(new ItemStack(ConfigItems.itemResource, 1, 9));
    }

    public int[] getAccessibleSlotsFromSide(final int p_94128_1_) {
        return new int[] { 0, 1 };
    }

    public boolean canInsertItem(final int p_102007_1_, final ItemStack p_102007_2_, final int p_102007_3_) {
        return p_102007_1_ != 1 && this.isItemValidForSlot(p_102007_1_, p_102007_2_);
    }

    public boolean canExtractItem(final int p_102008_1_, final ItemStack p_102008_2_, final int p_102008_3_) {
        return p_102008_1_ == 1 && this.knowledge != null;
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setInteger("progress", this.progress);
        final NBTTagList nbttaglist = new NBTTagList();
        final NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        if (this.paper != null) {
            this.paper.writeToNBT(nbttagcompound2);
        }
        nbttaglist.appendTag(nbttagcompound2);
        final NBTTagCompound nbttagcompound3 = new NBTTagCompound();
        if (this.knowledge != null) {
            this.knowledge.writeToNBT(nbttagcompound3);
        }
        nbttaglist.appendTag(nbttagcompound3);
        nbttagcompound.setTag("Items", nbttaglist);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.progress = nbttagcompound.getInteger("progress");
        final NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
        NBTTagCompound nbttagcompound2 = nbttaglist.getCompoundTagAt(0);
        this.paper = ItemStack.loadItemStackFromNBT(nbttagcompound2);
        nbttagcompound2 = nbttaglist.getCompoundTagAt(1);
        this.knowledge = ItemStack.loadItemStackFromNBT(nbttagcompound2);
    }

    @SideOnly(Side.CLIENT)
    public int getTimeRemainingScaled(final int p_145955_1_) {
        final int n = this.progress * p_145955_1_;
        this.getClass();
        return n / 100;
    }
}
