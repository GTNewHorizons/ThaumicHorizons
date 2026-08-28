//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.tiles.TileSyntheticNode;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemWispEssence;
import thaumcraft.common.tiles.TileJarNode;

public class BlockSyntheticNode extends BlockContainer {

    IIcon icon;

    public BlockSyntheticNode() {
        super(Material.glass);
        this.setHardness(0.7f);
        this.setResistance(1.0f);
        this.setLightLevel(0.5f);
        this.setBlockName("ThaumicHorizons_synthNode");
        this.setCreativeTab(ThaumicHorizons.tabTH);
        this.setBlockBounds(0.3f, 0.0f, 0.3f, 0.7f, 1.0f, 0.7f);
    }

    public TileEntity createNewTileEntity(final World world, final int md) {
        return this.createTileEntity(world, md);
    }

    public TileEntity createTileEntity(final World world, final int metadata) {
        return new TileSyntheticNode();
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 1;
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getRenderType() {
        return ThaumicHorizons.blockSyntheticNodeRI;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumcraft:crystal");
    }

    public IIcon getIcon(final int par1, final int par2) {
        return this.icon;
    }

    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player,
            final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_) {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == ConfigItems.itemWispEssence) {
            ((TileSyntheticNode) world.getTileEntity(x, y, z)).addEssence(player);
            return true;
        }
        return false;
    }

    public void breakBlock(final World p_149749_1_, final int p_149749_2_, final int p_149749_3_, final int p_149749_4_,
            final Block p_149749_5_, final int p_149749_6_) {
        if (p_149749_1_.getTileEntity(p_149749_2_, p_149749_3_, p_149749_4_) instanceof TileJarNode) {
            super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
            return;
        }
        final TileSyntheticNode tile = (TileSyntheticNode) p_149749_1_
                .getTileEntity(p_149749_2_, p_149749_3_, p_149749_4_);
        if (tile != null) {
            for (final Aspect asp : tile.getMaxAspects().getAspects()) {
                final ItemStack essence = new ItemStack(
                        ConfigItems.itemWispEssence,
                        tile.getMaxAspects().getAmount(asp) / 4);
                ((ItemWispEssence) ConfigItems.itemWispEssence).setAspects(essence, new AspectList().add(asp, 2));
                p_149749_1_.spawnEntityInWorld(
                        new EntityItem(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, essence));
            }
        }
        super.breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_, p_149749_5_, p_149749_6_);
    }
}
