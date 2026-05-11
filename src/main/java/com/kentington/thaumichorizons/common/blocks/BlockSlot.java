//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.tiles.TileSlot;

import thaumcraft.common.items.wands.ItemWandCasting;

public class BlockSlot extends BlockContainer {

    public BlockSlot() {
        super(Material.rock);
        this.setHardness(2.5f);
        this.setResistance(2.5f);
        this.setBlockName("ThaumicHorizons_slot");
        this.setBlockTextureName("ThaumicHorizons:void");
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int md) {
        final TileSlot tco = (TileSlot) world.getTileEntity(x, y, z);
        if (tco == null) return;
        if (tco.portalOpen) {
            tco.destroyPortal();
        }
        if (tco.hasKeystone) {
            final int dim = tco.removeKeystone();
            final ItemStack keystone = new ItemStack(ThaumicHorizons.itemKeystone);
            (keystone.stackTagCompound = new NBTTagCompound()).setInteger("dimension", dim);
            final EntityItem dropped = new EntityItem(world);
            dropped.setEntityItemStack(keystone);
            dropped.setPosition(x + 0.5, y + 0.5, z + 0.5);
            world.spawnEntityInWorld(dropped);
        }
    }

    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player,
            final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_) {
        final TileSlot tco = (TileSlot) world.getTileEntity(x, y, z);
        final ItemStack theItem = player.getHeldItem();
        if (tco.hasKeystone) {
            if (player.getHeldItem() == null) {
                final int dim = tco.removeKeystone();
                final ItemStack keystone = new ItemStack(ThaumicHorizons.itemKeystone);
                (keystone.stackTagCompound = new NBTTagCompound()).setInteger("dimension", dim);
                player.inventory.addItemStackToInventory(keystone);
                if (tco.portalOpen) {
                    tco.destroyPortal();
                }
                world.markBlockForUpdate(x, y, z);
                return true;
            } else if (!tco.portalOpen && player.getHeldItem().getItem() instanceof ItemWandCasting) {
                tco.makePortal(player);
                world.markBlockForUpdate(x, y, z);
                return true;
            }
        } else if (theItem != null && theItem.getItem() == ThaumicHorizons.itemKeystone
                && theItem.stackTagCompound != null) {
                    tco.insertKeystone(theItem.stackTagCompound.getInteger("dimension"));
                    --theItem.stackSize;
                    world.markBlockForUpdate(x, y, z);
                    return true;
                }
        return false;
    }

    public TileEntity createNewTileEntity(final World p_149915_1_, final int p_149915_2_) {
        return new TileSlot();
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getRenderType() {
        return ThaumicHorizons.blockSlotRI;
    }
}
