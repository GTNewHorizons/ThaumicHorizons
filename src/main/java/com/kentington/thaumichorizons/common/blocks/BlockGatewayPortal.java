package com.kentington.thaumichorizons.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.tiles.TileSlot;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.common.config.ConfigBlocks;

public class BlockGatewayPortal extends Block {

    public IIcon CornerTR;
    public IIcon CornerTL;
    public IIcon CornerBR;
    public IIcon CornerBL;
    public IIcon topR;
    public IIcon topL;
    public IIcon R;
    public IIcon L;
    public IIcon B;
    public IIcon lapiz;
    public IIcon stone;

    public BlockGatewayPortal() {
        super(Material.rock);
        this.setHardness(2.5f);
        this.setResistance(2.5f);
        this.setBlockName("ThaumicHorizons_gateway");
    }

    public void breakBlock(final World world, final int x, final int y, final int z, final Block block, final int md) {
        int slotX = 0;
        int slotY = 0;
        int slotZ = 0;
        if (md < 5) {
            slotY = y + md;
            if (world.getBlock(x + 1, y, z) == ThaumicHorizons.blockPortal
                    || world.getBlock(x + 1, y, z) == ThaumicHorizons.blockGateway) {
                slotX = x + 2;
                slotZ = z;
            } else {
                slotX = x;
                slotZ = z + 2;
            }
        } else if (md == 5) {
            slotY = y;
            if (world.getBlock(x + 1, y, z) == ThaumicHorizons.blockSlot) {
                slotX = x + 1;
                slotZ = z;
            } else {
                slotX = x;
                slotZ = z + 1;
            }
        } else if (md == 8) {
            slotY = y;
            if (world.getBlock(x - 1, y, z) == ThaumicHorizons.blockSlot) {
                slotX = x - 1;
                slotZ = z;
            } else {
                slotX = x;
                slotZ = z - 1;
            }
        } else if (md == 6 || md == 7 || md == 9) {
            slotY = y + 4;
            if (world.getBlock(x + 1, y, z) == ThaumicHorizons.blockGateway) {
                slotZ = z;
                switch (md) {
                    case 6 -> slotX = x + 1;
                    case 7 -> slotX = x;
                    case 9 -> slotX = x - 1;
                }
            } else {
                slotX = x;
                switch (md) {
                    case 6 -> slotZ = z + 1;
                    case 7 -> slotZ = z;
                    case 9 -> slotZ = z - 1;
                }
            }
        } else {
            slotY = y + md - 10;
            if (world.getBlock(x - 1, y, z) == ThaumicHorizons.blockPortal
                    || world.getBlock(x - 1, y, z) == ThaumicHorizons.blockGateway) {
                slotX = x - 2;
                slotZ = z;
            } else {
                slotX = x;
                slotZ = z - 2;
            }
        }
        final TileEntity te = world.getTileEntity(slotX, slotY, slotZ);
        if (te instanceof TileSlot && ((TileSlot) te).portalOpen) {
            ((TileSlot) te).destroyPortal();
        }
    }

    @SideOnly(Side.CLIENT)
    public Item getItem(final World p_149694_1_, final int p_149694_2_, final int p_149694_3_, final int p_149694_4_) {
        return Item.getItemById(0);
    }

    public int quantityDropped(final Random p_149745_1_) {
        return 0;
    }

    public void registerBlockIcons(final IIconRegister ir) {
        this.CornerTR = ir.registerIcon("thaumichorizons:gateway_topright");
        this.CornerTL = ir.registerIcon("thaumichorizons:gateway_topleft");
        this.CornerBR = ir.registerIcon("thaumichorizons:gateway_bottomright");
        this.CornerBL = ir.registerIcon("thaumichorizons:gateway_bottomleft");
        this.topR = ir.registerIcon("thaumichorizons:gateway_top2");
        this.topL = ir.registerIcon("thaumichorizons:gateway_top");
        this.R = ir.registerIcon("thaumichorizons:gateway_right");
        this.L = ir.registerIcon("thaumichorizons:gateway_left");
        this.B = ir.registerIcon("thaumichorizons:gateway_bottom");
        this.lapiz = Blocks.lapis_block.getIcon(0, 0);
        this.stone = ConfigBlocks.blockCosmeticSolid.getIcon(0, 11);
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(final IBlockAccess world, final int x, final int y, final int z, final int side) {
        final boolean isXAligned = world.getBlock(x + 1, y, z) == ThaumicHorizons.blockGateway
                || world.getBlock(x + 1, y, z) == ThaumicHorizons.blockPortal
                || world.getBlock(x - 1, y, z) == ThaumicHorizons.blockGateway
                || world.getBlock(x - 1, y, z) == ThaumicHorizons.blockPortal;
        return switch (world.getBlockMetadata(x, y, z)) {
            case 0 -> switch (side) {
                    case 0 -> this.lapiz;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.CornerTR : this.stone;
                    case 3 -> isXAligned ? this.CornerTL : this.stone;
                    case 4 -> isXAligned ? this.stone : this.CornerTL;
                    case 5 -> isXAligned ? this.stone : this.CornerTR;
                    default -> this.leftSide(isXAligned, side);
                };
            case 1, 2, 3 -> this.leftSide(isXAligned, side);
            case 4 -> switch (side) {
                    case 0 -> this.stone;
                    case 1 -> this.lapiz;
                    case 2 -> isXAligned ? this.CornerBR : this.stone;
                    case 3 -> isXAligned ? this.CornerBL : this.stone;
                    case 4 -> isXAligned ? this.stone : this.CornerBL;
                    case 5 -> isXAligned ? this.stone : this.CornerBR;
                    default -> this.lapiz;
                };
            case 5 -> switch (side) {
                    case 0 -> this.lapiz;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.topR : this.stone;
                    case 3 -> isXAligned ? this.topL : this.stone;
                    case 4 -> isXAligned ? this.stone : this.topL;
                    case 5 -> isXAligned ? this.stone : this.topR;
                    default -> this.lapiz;
                };
            case 6, 7, 9 -> this.bottomSide(isXAligned, side);
            case 8 -> switch (side) {
                    case 0 -> this.lapiz;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.topL : this.stone;
                    case 3 -> isXAligned ? this.topR : this.stone;
                    case 4 -> isXAligned ? this.stone : this.topR;
                    case 5 -> isXAligned ? this.stone : this.topL;
                    default -> this.lapiz;
                };
            case 10 -> switch (side) {
                    case 0 -> this.lapiz;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.CornerTL : this.stone;
                    case 3 -> isXAligned ? this.CornerTR : this.stone;
                    case 4 -> isXAligned ? this.stone : this.CornerTR;
                    case 5 -> isXAligned ? this.stone : this.CornerTL;
                    default -> this.rightSide(isXAligned, side);
                };
            case 11, 12, 13 -> this.rightSide(isXAligned, side);
            case 14 -> switch (side) {
                    case 0 -> this.stone;
                    case 1 -> this.lapiz;
                    case 2 -> isXAligned ? this.CornerBL : this.stone;
                    case 3 -> isXAligned ? this.CornerBR : this.stone;
                    case 4 -> isXAligned ? this.stone : this.CornerBR;
                    case 5 -> isXAligned ? this.stone : this.CornerBL;
                    default -> this.lapiz;
                };
            default -> this.lapiz;
        };
    }

    IIcon leftSide(final boolean xAligned, final int side) {
        return switch (side) {
            case 2 -> xAligned ? this.R : this.stone;
            case 3 -> xAligned ? this.L : this.lapiz;
            case 4 -> xAligned ? this.stone : this.L;
            case 5 -> xAligned ? this.lapiz : this.R;
            default -> this.lapiz;
        };
    }

    IIcon rightSide(final boolean xAligned, final int side) {
        return switch (side) {
            case 2 -> xAligned ? this.L : this.lapiz;
            case 3 -> xAligned ? this.R : this.stone;
            case 4 -> xAligned ? this.lapiz : this.R;
            case 5 -> xAligned ? this.stone : this.L;
            default -> this.lapiz;
        };
    }

    IIcon bottomSide(final boolean xAligned, final int side) {
        return switch (side) {
            case 0 -> this.stone;
            case 1 -> this.lapiz;
            default -> this.B;
        };
    }
}
