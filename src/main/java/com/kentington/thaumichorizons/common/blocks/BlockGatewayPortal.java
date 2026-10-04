package com.kentington.thaumichorizons.common.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
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
    public IIcon TopR;
    public IIcon TopL;
    public IIcon RightT;
    public IIcon RightM;
    public IIcon RightB;
    public IIcon LeftT;
    public IIcon LeftM;
    public IIcon LeftB;
    public IIcon BottomR;
    public IIcon BottomM;
    public IIcon BottomL;
    public IIcon Inner;
    public IIcon OuterT;
    public IIcon OuterMTB;
    public IIcon OuterB;
    public IIcon OuterL;
    public IIcon OuterMLR;
    public IIcon OuterR;
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
        this.CornerTR = ir.registerIcon("thaumichorizons:gateway_corner_top_right");
        this.CornerTL = ir.registerIcon("thaumichorizons:gateway_corner_top_left");
        this.CornerBR = ir.registerIcon("thaumichorizons:gateway_corner_bottom_right");
        this.CornerBL = ir.registerIcon("thaumichorizons:gateway_corner_bottom_left");
        this.TopR = ir.registerIcon("thaumichorizons:gateway_top_right");
        this.TopL = ir.registerIcon("thaumichorizons:gateway_top_left");
        this.RightT = ir.registerIcon("thaumichorizons:gateway_right_top");
        this.RightM = ir.registerIcon("thaumichorizons:gateway_right_middle");
        this.RightB = ir.registerIcon("thaumichorizons:gateway_right_bottom");
        this.LeftT = ir.registerIcon("thaumichorizons:gateway_left_top");
        this.LeftM = ir.registerIcon("thaumichorizons:gateway_left_middle");
        this.LeftB = ir.registerIcon("thaumichorizons:gateway_left_bottom");
        this.BottomR = ir.registerIcon("thaumichorizons:gateway_bottom_right");
        this.BottomM = ir.registerIcon("thaumichorizons:gateway_bottom_middle");
        this.BottomL = ir.registerIcon("thaumichorizons:gateway_bottom_left");
        this.OuterT = ir.registerIcon("thaumichorizons:gateway_outer_top");
        this.OuterMTB = ir.registerIcon("thaumichorizons:gateway_outer_middle_tb");
        this.OuterB = ir.registerIcon("thaumichorizons:gateway_outer_bottom");
        this.OuterL = ir.registerIcon("thaumichorizons:gateway_outer_left");
        this.OuterMLR = ir.registerIcon("thaumichorizons:gateway_outer_middle_lr");
        this.OuterR = ir.registerIcon("thaumichorizons:gateway_outer_right");
        this.Inner = ir.registerIcon("thaumichorizons:blockCrystalMagenta");
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
                    case 0 -> this.Inner;
                    case 1 -> isXAligned ? this.OuterL : this.OuterT;
                    case 2 -> isXAligned ? this.CornerTR : this.OuterT;
                    case 3 -> isXAligned ? this.CornerTL : this.OuterT;
                    case 4 -> isXAligned ? this.OuterT : this.CornerTL;
                    case 5 -> isXAligned ? this.OuterT : this.CornerTR;
                    default -> this.Inner;
                };
            case 1 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.RightT : this.OuterMTB;
                    case 3 -> isXAligned ? this.LeftT : this.Inner;
                    case 4 -> isXAligned ? this.OuterMTB : this.LeftT;
                    case 5 -> isXAligned ? this.Inner : this.RightT;
                    default -> this.stone;
                };
            case 2 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.RightM : this.OuterMTB;
                    case 3 -> isXAligned ? this.LeftM : this.Inner;
                    case 4 -> isXAligned ? this.OuterMTB : this.LeftM;
                    case 5 -> isXAligned ? this.Inner : this.RightM;
                    default -> this.stone;
                };
            case 3 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.RightB : this.OuterMTB;
                    case 3 -> isXAligned ? this.LeftB : this.Inner;
                    case 4 -> isXAligned ? this.OuterMTB : this.LeftB;
                    case 5 -> isXAligned ? this.Inner : this.RightB;
                    default -> this.stone;
                };
            case 4 -> switch (side) {
                    case 0 -> isXAligned ? this.OuterL : this.OuterT;
                    case 1 -> this.Inner;
                    case 2 -> isXAligned ? this.CornerBR : this.OuterB;
                    case 3 -> isXAligned ? this.CornerBL : this.OuterB;
                    case 4 -> isXAligned ? this.OuterB : this.CornerBL;
                    case 5 -> isXAligned ? this.OuterB : this.CornerBR;
                    default -> this.stone;
                };
            case 5 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> isXAligned ? this.OuterR : this.OuterB;
                    case 2 -> isXAligned ? this.TopR : this.stone;
                    case 3 -> isXAligned ? this.TopL : this.stone;
                    case 4 -> isXAligned ? this.stone : this.TopL;
                    case 5 -> isXAligned ? this.stone : this.TopR;
                    default -> this.stone;
                };
            case 6 -> switch (side) {
                    case 0 -> isXAligned ? this.OuterMLR : this.OuterMTB;
                    case 1 -> this.Inner;
                    case 2 -> isXAligned ? this.BottomR : this.stone;
                    case 3 -> isXAligned ? this.BottomL : this.stone;
                    case 4 -> isXAligned ? this.stone : this.BottomL;
                    case 5 -> isXAligned ? this.stone : this.BottomR;
                    default -> this.stone;
                };
            case 7 -> switch (side) {
                    case 0 -> isXAligned ? this.OuterMLR : this.OuterMTB;
                    case 1 -> this.Inner;
                    default -> this.BottomM;
                };
            case 8 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> isXAligned ? this.OuterL : this.OuterT;
                    case 2 -> isXAligned ? this.TopL : this.stone;
                    case 3 -> isXAligned ? this.TopR : this.stone;
                    case 4 -> isXAligned ? this.stone : this.TopR;
                    case 5 -> isXAligned ? this.stone : this.TopL;
                    default -> this.stone;
                };
            case 9 -> switch (side) {
                    case 0 -> isXAligned ? this.OuterMLR : this.OuterMTB;
                    case 1 -> this.Inner;
                    case 2 -> isXAligned ? this.BottomL : this.stone;
                    case 3 -> isXAligned ? this.BottomR : this.stone;
                    case 4 -> isXAligned ? this.stone : this.BottomR;
                    case 5 -> isXAligned ? this.stone : this.BottomL;
                    default -> this.stone;
                };
            case 10 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> isXAligned ? this.OuterR : this.OuterB;
                    case 2 -> isXAligned ? this.CornerTL : this.OuterT;
                    case 3 -> isXAligned ? this.CornerTR : this.OuterT;
                    case 4 -> isXAligned ? this.OuterT : this.CornerTR;
                    case 5 -> isXAligned ? this.OuterT : this.CornerTL;
                    default -> this.stone;
                };
            case 11 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.LeftT : this.Inner;
                    case 3 -> isXAligned ? this.RightT : this.OuterMTB;
                    case 4 -> isXAligned ? this.Inner : this.RightT;
                    case 5 -> isXAligned ? this.OuterMTB : this.LeftT;
                    default -> this.stone;
                };
            case 12 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.LeftM : this.Inner;
                    case 3 -> isXAligned ? this.RightM : this.OuterMTB;
                    case 4 -> isXAligned ? this.Inner : this.RightM;
                    case 5 -> isXAligned ? this.OuterMTB : this.LeftM;
                    default -> this.stone;
                };
            case 13 -> switch (side) {
                    case 0 -> this.Inner;
                    case 1 -> this.stone;
                    case 2 -> isXAligned ? this.LeftB : this.Inner;
                    case 3 -> isXAligned ? this.RightB : this.OuterMTB;
                    case 4 -> isXAligned ? this.Inner : this.RightB;
                    case 5 -> isXAligned ? this.OuterMTB : this.LeftB;
                    default -> this.stone;
                };
            case 14 -> switch (side) {
                    case 0 -> isXAligned ? this.OuterR : this.OuterB;
                    case 1 -> this.Inner;
                    case 2 -> isXAligned ? this.CornerBL : this.OuterB;
                    case 3 -> isXAligned ? this.CornerBR : this.OuterB;
                    case 4 -> isXAligned ? this.OuterB : this.CornerBR;
                    case 5 -> isXAligned ? this.OuterB : this.CornerBL;
                    default -> this.stone;
                };
            default -> this.stone;
        };
    }
}
