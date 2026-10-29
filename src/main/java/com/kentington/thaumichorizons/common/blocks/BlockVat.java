//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.tiles.TileVat;
import com.kentington.thaumichorizons.common.tiles.TileVatConnector;
import com.kentington.thaumichorizons.common.tiles.TileVatMatrix;
import com.kentington.thaumichorizons.common.tiles.TileVatSlave;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;

public class BlockVat extends BlockContainer {

    public IIcon iconGlassTL;
    public IIcon iconGlassT;
    public IIcon iconGlassTR;
    public IIcon iconGlassBL;
    public IIcon iconGlassB;
    public IIcon iconGlassBR;

    public BlockVat() {
        super(Material.wood);
        this.setHardness(3.0f);
        this.setResistance(15.0f);
        this.lightValue = 8;
        this.setBlockName("ThaumicHorizons_vat");
    }

    public BlockVat(final Material m) {
        super(m);
        this.setHardness(3.0f);
        this.setResistance(15.0f);
        this.lightValue = 8;
        this.setBlockName("ThaumicHorizons_vatSolid");
    }

    public TileEntity createNewTileEntity(final World world, final int md) {
        return this.createTileEntity(world, md);
    }

    public TileEntity createTileEntity(final World world, final int metadata) {
        TileThaumcraft vat;
        if (metadata == 7) {
            vat = new TileVat();
        } else if (metadata == 4) {
            vat = new TileVatConnector();
        } else {
            vat = new TileVatSlave();
        }
        return vat;
    }

    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int md) {
        if (md == 7) {
            final TileVat vat = (TileVat) world.getTileEntity(x, y, z);
            vat.killMe();
            final net.minecraft.tileentity.TileEntity above = world.getTileEntity(x, y + 1, z);
            if (above instanceof TileVatMatrix) {
                ((TileVatMatrix) above).onInfusionInterrupted();
            }
        } else {
            ((TileVatSlave) world.getTileEntity(x, y, z)).killMyBoss(md);
        }
        super.breakBlock(world, x, y, z, block, md);
    }

    public boolean onBlockActivated(final World world, final int x, final int y, final int z, final EntityPlayer player,
            final int p_149727_6_, final float p_149727_7_, final float p_149727_8_, final float p_149727_9_) {
        if (world.getTileEntity(x, y, z) instanceof TileVat) {
            return ((TileVat) world.getTileEntity(x, y, z)).activate(player, true);
        }
        return world.getTileEntity(x, y, z) instanceof TileVatSlave
                && ((TileVatSlave) world.getTileEntity(x, y, z)).activate(player);
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
        return ThaumicHorizons.blockVatRI;
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(final IIconRegister ir) {
        this.iconGlassTL = ir.registerIcon("thaumichorizons:vattopleft");
        this.iconGlassT = ir.registerIcon("thaumichorizons:vattop");
        this.iconGlassTR = ir.registerIcon("thaumichorizons:vattopright");
        this.iconGlassBL = ir.registerIcon("thaumichorizons:vatbottomleft");
        this.iconGlassB = ir.registerIcon("thaumichorizons:vatbottom");
        this.iconGlassBR = ir.registerIcon("thaumichorizons:vatbottomright");
    }

    public IIcon getIcon(final int par1, final int par2) {
        return this.iconGlassT;
    }
}
