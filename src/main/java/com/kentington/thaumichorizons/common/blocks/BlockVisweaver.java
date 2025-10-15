package com.kentington.thaumichorizons.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.api.VisweaverRecipeMap;
import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.tiles.TileVisweaver;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;

public class BlockVisweaver extends BlockContainer {

    public BlockVisweaver() {
        super(Material.wood);
        setHardness(5F);
        setResistance(10F);

        this.setBlockName("ThaumicHorizons_visweaver");
        this.setBlockTextureName("ThaumicHorizons:visweaver");

        VisweaverRecipeMap.putRecipe(25, Aspect.FIRE, new ItemStack(Items.bed), new ItemStack(Items.spider_eye));
        VisweaverRecipeMap.putRecipe(50, Aspect.EARTH, new ItemStack(Items.brick), new ItemStack(Items.netherbrick));
    }

    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player,
            final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_) {
        player.openGui(ThaumicHorizons.instance, 10, world, x, y, z);
        return true;
    }

    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int md) {
        final TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof final TileVisweaver tile) {
            ItemStack input = tile.getStackInSlot(0);
            ItemStack output = tile.getStackInSlot(1);
            if (input != null) {
                world.spawnEntityInWorld(new EntityItem(world, x, y, z, input));
            }
            if (output != null) {
                world.spawnEntityInWorld(new EntityItem(world, x, y, z, output));
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(0, 1);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return createNewTileEntity(world, metadata);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int var2) {
        return new TileVisweaver();
    }

    @Override
    public int getRenderType() {
        return ThaumicHorizons.blockVisweaverRI;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
