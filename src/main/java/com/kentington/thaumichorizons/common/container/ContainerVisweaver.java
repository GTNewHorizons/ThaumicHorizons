package com.kentington.thaumichorizons.common.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import com.kentington.thaumichorizons.common.tiles.TileVisweaver;

import thaumcraft.common.container.SlotOutput;

public class ContainerVisweaver extends Container {

    TileVisweaver visweaver;

    public ContainerVisweaver(InventoryPlayer inv, TileVisweaver visweaver) {
        this.visweaver = visweaver;

        addSlotToContainer(new Slot(visweaver, 0, 20, 30));
        addSlotToContainer(new SlotOutput(visweaver, 1, 140, 30));

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
        for (int i = 0; i < 9; ++i) {
            this.addSlotToContainer(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack itemstack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack stackInSlot = slot.getStack();
            itemstack = stackInSlot.copy();

            if (index == 0 || index == 1) {
                if (!this.mergeItemStack(stackInSlot, 2, 38, true)) {
                    return null;
                }
                slot.onSlotChange(stackInSlot, itemstack);
            } else {
                if (!this.mergeItemStack(stackInSlot, 0, 1, false)) {
                    return null;
                }
            }

            if (stackInSlot.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
            if (stackInSlot.stackSize == itemstack.stackSize) {
                return null;
            }
            slot.onPickupFromSlot(player, stackInSlot);
        }
        return itemstack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return visweaver.isUseableByPlayer(player);
    }

}
