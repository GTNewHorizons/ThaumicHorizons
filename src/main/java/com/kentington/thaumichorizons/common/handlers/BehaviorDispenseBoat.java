package com.kentington.thaumichorizons.common.handlers;

import java.util.Set;

import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BehaviorDispenseBoat extends BehaviorDefaultDispenseItem {

    public interface BoatFactory {

        EntityBoat create(World world, double x, double y, double z);
    }

    private final Set<Material> validMaterials;
    private final BoatFactory boatFactory;

    public BehaviorDispenseBoat(Set<Material> validMaterials, BoatFactory boatFactory) {
        this.validMaterials = validMaterials;
        this.boatFactory = boatFactory;
    }

    @Override
    protected ItemStack dispenseStack(IBlockSource dispenser, ItemStack stack) {
        EnumFacing facing = BlockDispenser.func_149937_b(dispenser.getBlockMetadata());
        World world = dispenser.getWorld();

        // Calculate spawn position slightly in front of the dispenser
        double spawnX = dispenser.getX() + (facing.getFrontOffsetX() * 1.25F);
        double spawnY = dispenser.getY() + (facing.getFrontOffsetY() * 1.125F) - 0.5F;
        double spawnZ = dispenser.getZ() + (facing.getFrontOffsetZ() * 1.25F);

        // Block coordinates in front of the dispenser
        int targetX = dispenser.getXInt() + facing.getFrontOffsetX();
        int targetY = dispenser.getYInt() + facing.getFrontOffsetY();
        int targetZ = dispenser.getZInt() + facing.getFrontOffsetZ();

        Material frontMaterial = world.getBlock(targetX, targetY, targetZ).getMaterial();

        if (validMaterials.contains(frontMaterial)) {
            spawnY++;
        } else if (!Material.air.equals(frontMaterial)) {
            return stack;
        }

        EntityBoat boat = boatFactory.create(world, spawnX, spawnY, spawnZ);

        boat.rotationYaw = switch (facing) {
            case NORTH -> 90.0F;
            case WEST -> 180.0F;
            case EAST -> 0.0F;
            default -> -90.0F; // SOUTH or other
        };

        world.spawnEntityInWorld(boat);

        stack.stackSize--;
        return stack;
    }
}
