package com.kentington.thaumichorizons.common.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants;

import com.kentington.thaumichorizons.api.VisweaverRecipe;
import com.kentington.thaumichorizons.api.VisweaverRecipeMap;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.visnet.VisNetHandler;

public class TileVisweaver extends TileEntity implements IInventory {

    private static final String TAG_INTERNAL_VIS = "internalVis";
    private static final String TAG_REQUIRED_VIS = "requiredVis";
    private static final String TAG_WORKING = "working";
    private static final String TAG_ASPECT = "cvType";
    private static final String TAG_INPUT = "currentInput";
    private static final String TAG_OUTPUT = "currentOutput";

    private int tickCounter = -1;
    private int internalVis = 0;
    private boolean working = false;
    private ItemStack currentOutput;
    private ItemStack currentInput;
    private Aspect cvType;
    private int requiredVis;

    ItemStack[] inventorySlots = new ItemStack[2];

    @Override
    public void updateEntity() {
        tickCounter++;
        if (worldObj.isRemote) return;
        if (working) {
            ItemStack currentStack = getStackInSlot(0);
            if (currentStack == null || currentInput == null || !currentStack.isItemEqual(currentInput)) {
                flushRecipe();
                worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            }
        }
        if (tickCounter % 20 != 0) return;
        if (!working) {
            recipeCheck();
        } else {
            internalVis += VisNetHandler.drainVis(this.worldObj, this.xCoord, this.yCoord, this.zCoord, cvType, 500);
            progressRecipe();
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
        }
    }

    private void progressRecipe() {
        if (internalVis >= requiredVis) {
            internalVis -= requiredVis;
            finishRecipe();
        }
    }

    private void flushRecipe() {
        internalVis = 0;
        requiredVis = 0;
        currentOutput = null;
        currentInput = null;
        cvType = null;
        working = false;
    }

    private void recipeCheck() {
        VisweaverRecipe recipe = VisweaverRecipeMap.lookup(getStackInSlot(0));
        if (recipe != null) {
            ItemStack output = recipe.getOutput();
            if (canAddToSlot(output, 1) <= 0) return;
            cvType = recipe.getCentivisType();
            requiredVis = recipe.getCentivisCost();
            currentOutput = output;
            currentInput = recipe.getInput();
            working = true;
        }
    }

    private void finishRecipe() {
        decrStackSize(0, 1);
        addToSlot(currentOutput, 1);
        ItemStack input = getStackInSlot(0);
        if (input != null && currentInput.isItemEqual(input) && canAddToSlot(currentOutput, 1) > 0) {
            progressRecipe();
        } else {
            flushRecipe();
            recipeCheck();
        }
    }

    @Override
    public int getSizeInventory() {
        return inventorySlots.length;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return inventorySlots[slotIn];
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (inventorySlots[i] != null) {
            ItemStack stackAt;

            if (inventorySlots[i].stackSize <= j) {
                stackAt = inventorySlots[i];
                inventorySlots[i] = null;
            } else {
                stackAt = inventorySlots[i].splitStack(j);

                if (inventorySlots[i].stackSize == 0) inventorySlots[i] = null;

            }
            return stackAt;
        }
        return null;
    }

    /**
     * Returns the amount of items from the input stack that can be stacked into this slot
     */
    public int canAddToSlot(ItemStack input, int slot) {
        ItemStack currentStack = getStackInSlot(slot);
        if (currentStack == null) return Math.min(64, input.stackSize);
        if (!currentStack.isItemEqual(input)) return 0;
        return Math.min(input.stackSize, 64 - currentStack.stackSize);
    }

    public void addToSlot(ItemStack stack, int slot) {
        int movable = canAddToSlot(stack, slot);
        if (movable == 0) return;
        ItemStack currentStack = getStackInSlot(slot);
        ItemStack newStack = stack.copy();

        if (currentStack == null) {
            newStack.stackSize = movable;
            setInventorySlotContents(slot, newStack);
        } else {
            currentStack.stackSize += movable;
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int i) {
        return getStackInSlot(i);
    }

    @Override
    public void setInventorySlotContents(int i, ItemStack stack) {
        inventorySlots[i] = stack;
    }

    @Override
    public String getInventoryName() {
        return "container.visweaver";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer entityplayer) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
                && entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readFromNBT(par1NBTTagCompound);

        working = par1NBTTagCompound.getBoolean(TAG_WORKING);
        internalVis = par1NBTTagCompound.getInteger(TAG_INTERNAL_VIS);
        requiredVis = par1NBTTagCompound.getInteger(TAG_REQUIRED_VIS);
        cvType = Aspect.getAspect(par1NBTTagCompound.getString(TAG_ASPECT));
        currentInput = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag(TAG_INPUT));
        currentOutput = ItemStack.loadItemStackFromNBT(par1NBTTagCompound.getCompoundTag(TAG_OUTPUT));

        NBTTagList var2 = par1NBTTagCompound.getTagList("Items", Constants.NBT.TAG_COMPOUND);
        inventorySlots = new ItemStack[getSizeInventory()];
        for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
            NBTTagCompound var4 = var2.getCompoundTagAt(var3);
            byte var5 = var4.getByte("Slot");
            if (var5 >= 0 && var5 < inventorySlots.length) inventorySlots[var5] = ItemStack.loadItemStackFromNBT(var4);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeToNBT(par1NBTTagCompound);

        NBTTagCompound input = new NBTTagCompound();
        NBTTagCompound output = new NBTTagCompound();

        par1NBTTagCompound.setBoolean(TAG_WORKING, working);
        par1NBTTagCompound.setInteger(TAG_INTERNAL_VIS, internalVis);
        par1NBTTagCompound.setInteger(TAG_REQUIRED_VIS, requiredVis);

        if (working) {
            par1NBTTagCompound.setString(TAG_ASPECT, cvType.getTag());
            par1NBTTagCompound.setTag(TAG_INPUT, currentInput.writeToNBT(input));
            par1NBTTagCompound.setTag(TAG_OUTPUT, currentOutput.writeToNBT(output));
        }

        NBTTagList var2 = new NBTTagList();
        for (int var3 = 0; var3 < inventorySlots.length; ++var3) {
            if (inventorySlots[var3] != null) {
                NBTTagCompound var4 = new NBTTagCompound();
                var4.setByte("Slot", (byte) var3);
                inventorySlots[var3].writeToNBT(var4);
                var2.appendTag(var4);
            }
        }
        par1NBTTagCompound.setTag("Items", var2);
    }

    @Override
    public S35PacketUpdateTileEntity getDescriptionPacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        writeToNBT(nbttagcompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, -999, nbttagcompound);
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0;
    }

    public boolean isWorking() {
        return working;
    }

    public int getTickCounter() {
        return tickCounter;
    }

    public Aspect getCvType() {
        return cvType;
    }

    public int getInternalVis() {
        return internalVis;
    }

    public int getRequiredVis() {
        return requiredVis;
    }
}
