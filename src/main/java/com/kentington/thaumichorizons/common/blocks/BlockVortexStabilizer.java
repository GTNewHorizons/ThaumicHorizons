// 
// Decompiled by Procyon v0.5.30
// 

package com.kentington.thaumichorizons.common.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import com.kentington.thaumichorizons.common.tiles.TileVortexStabilizer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.kentington.thaumichorizons.common.ThaumicHorizons;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.block.BlockContainer;

public class BlockVortexStabilizer extends BlockContainer
{
    IIcon icon;
    
    public BlockVortexStabilizer() {
        super(Material.iron);
        this.setBlockName("ThaumicHorizons_vortexStabilizer");
        this.setHardness(10.0f);
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }
    
    public int onBlockPlaced(final World p_149742_1_, final int p_149742_2_, final int p_149742_3_, final int p_149742_4_, final int p_149660_5_, final float p_149660_6_, final float p_149660_7_, final float p_149660_8_, final int p_149660_9_) {
        return p_149660_5_;
    }
    
    public TileEntity createTileEntity(final World world, final int metadata) {
        return new TileVortexStabilizer();
    }
    
    public void killMe(final World world, final int x, final int y, final int z, final boolean drop) {
        if (((TileVortexStabilizer)world.getTileEntity(x, y, z)).hasTarget) {
            ((TileVortexStabilizer)world.getTileEntity(x, y, z)).reHungrifyTarget();
        }
        if (drop) {
            this.dropBlockAsItem(world, x, y, z, 0, 0);
        }
    }
    
    public void onBlockPreDestroy(final World world, final int x, final int y, final int z, final int md) {
        this.killMe(world, x, y, z, false);
    }
    
    public void onNeighborBlockChange(final World world, final int x, final int y, final int z, final Block nbid) {
        if (world.isRemote)
            return;

        final TileVortexStabilizer tile = (TileVortexStabilizer) world.getTileEntity(x, y, z);
        tile.redstoned = world.isBlockIndirectlyGettingPowered(x, y, z);
        if ((!tile.redstoned && !world.isBlockIndirectlyGettingPowered(x, y, z)) || (tile.redstoned && world.isBlockIndirectlyGettingPowered(x, y, z))) {
            tile.markDirty();
            tile.getWorldObj().markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
        }
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
        return ThaumicHorizons.blockVortexStabilizerRI;
    }
    
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("iron_block");
    }
    
    public IIcon getIcon(final int par1, final int par2) {
        return this.icon;
    }
    
    public TileEntity createNewTileEntity(final World world, final int md) {
        return this.createTileEntity(world, md);
    }
}
