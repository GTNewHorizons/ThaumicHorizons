//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.items.lenses.ILens;
import com.kentington.thaumichorizons.common.items.lenses.ItemLensCase;

import thaumcraft.common.container.SlotLimitedByClass;

public class ContainerCase extends Container {

    private final World worldObj;
    private int posX;
    private int posY;
    private int posZ;
    private int blockSlot;
    public IInventory input;
    ItemStack pouch;
    EntityPlayer player;

    public ContainerCase(final InventoryPlayer iinventory, final World par2World, final int par3, final int par4,
            final int par5) {
        this.input = new InventoryCase(this);
        this.pouch = null;
        this.player = null;
        this.worldObj = par2World;
        this.posX = par3;
        this.posY = par4;
        this.posZ = par5;
        this.player = iinventory.player;
        this.pouch = iinventory.getCurrentItem();
        this.blockSlot = iinventory.currentItem + 45;
        for (int a = 0; a < 18; ++a) {
            this.addSlotToContainer(
                    new SlotLimitedByClass(ILens.class, this.input, a, 37 + a % 6 * 18, 51 + a / 6 * 18));
        }
        this.bindPlayerInventory(iinventory);
        if (!par2World.isRemote && this.pouch != null
                && this.pouch.getItem() instanceof ItemLensCase lensCase
                && this.input instanceof InventoryCase inv) {
            inv.stackList = lensCase.getInventory(this.pouch);
        }
        this.onCraftMatrixChanged(this.input);
    }

    protected void bindPlayerInventory(final InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 151 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 209));
        }
    }

    public ItemStack transferStackInSlot(final EntityPlayer par1EntityPlayer, final int slot) {
        if (slot == this.blockSlot) {
            return null;
        }
        ItemStack stack = null;
        final Slot slotObject = (Slot) this.inventorySlots.get(slot);
        if (slotObject != null && slotObject.getHasStack()) {
            final ItemStack stackInSlot = slotObject.getStack();
            stack = stackInSlot.copy();
            if (slot < 18) {
                if (!this.input.isItemValidForSlot(slot, stackInSlot)
                        || !this.mergeItemStack(stackInSlot, 18, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.input.isItemValidForSlot(slot, stackInSlot)
                    || !this.mergeItemStack(stackInSlot, 0, 18, false)) {
                        return null;
                    }
            if (stackInSlot.stackSize == 0) {
                slotObject.putStack(null);
            } else {
                slotObject.onSlotChanged();
            }
        }
        return stack;
    }

    public boolean canInteractWith(final EntityPlayer var1) {
        return true;
    }

    public ItemStack slotClick(final int par1, final int par2, final int par3, final EntityPlayer par4EntityPlayer) {
        if (par1 == this.blockSlot) {
            return null;
        }
        return super.slotClick(par1, par2, par3, par4EntityPlayer);
    }

    public void onContainerClosed(final EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!this.worldObj.isRemote) {
            ((ItemLensCase) this.pouch.getItem()).setInventory(this.pouch, ((InventoryCase) this.input).stackList);
            if (this.player == null) {
                return;
            }
            if (this.player.getHeldItem() != null && this.player.getHeldItem().isItemEqual(this.pouch)) {
                this.player.setCurrentItemOrArmor(0, this.pouch);
            }
            this.player.inventory.markDirty();
        }
    }
}
