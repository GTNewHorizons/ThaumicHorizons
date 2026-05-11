//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.lib.PocketPlaneData;
import com.kentington.thaumichorizons.common.tiles.TileVortex;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKeystone extends Item {

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    public ItemKeystone() {
        this.setCreativeTab(ThaumicHorizons.tabTH);
        this.setMaxStackSize(1);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:keystone");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.icon;
    }

    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        if (par1ItemStack.getTagCompound() != null && par1ItemStack.getTagCompound().getInteger("dimension") != 0) {
            return "item.keystoneTH";
        }
        return "item.keystoneBlank";
    }

    public void addInformation(final ItemStack par1ItemStack, final EntityPlayer par2EntityPlayer, final List par3List,
            final boolean par4) {
        if (par1ItemStack.getTagCompound() != null) {
            final int dim = par1ItemStack.getTagCompound().getInteger("dimension");
            if (dim >= 0 && dim < PocketPlaneData.planes.size()) {
                par3List.add(PocketPlaneData.planes.get(dim).name);
            }
        }
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack par1ItemStack, int p_82790_2_) {
        if (par1ItemStack.getTagCompound() != null) {
            int dimID = par1ItemStack.getTagCompound().getInteger("dimension");
            if (dimID < PocketPlaneData.planes.size()) return PocketPlaneData.planes.get(dimID).color;
            else par1ItemStack = null;
        }
        return 16777215;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        if (stack.getTagCompound() == null && player.dimension == ThaumicHorizons.dimensionPocketId
                && !world.isRemote) {
            final net.minecraft.tileentity.TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileVortex) {
                (stack.stackTagCompound = new NBTTagCompound()).setInteger("dimension", ((TileVortex) te).dimensionID);
                player.inventory.markDirty();
                return true;
            }
            return false;
        }
        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }
}
