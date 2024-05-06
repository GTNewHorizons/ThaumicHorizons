//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBoat;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityBoatGreatwood;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBoatGreatwood extends ItemBoat {

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    public ItemBoatGreatwood() {
        this.maxStackSize = 1;
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    public ItemStack onItemRightClick(final ItemStack p_77659_1_, final World p_77659_2_,
            final EntityPlayer p_77659_3_) {
        final float f = 1.0f;
        final float f2 = p_77659_3_.prevRotationPitch + (p_77659_3_.rotationPitch - p_77659_3_.prevRotationPitch) * f;
        final float f3 = p_77659_3_.prevRotationYaw + (p_77659_3_.rotationYaw - p_77659_3_.prevRotationYaw) * f;
        final double d0 = p_77659_3_.prevPosX + (p_77659_3_.posX - p_77659_3_.prevPosX) * f;
        final double d2 = p_77659_3_.prevPosY + (p_77659_3_.posY - p_77659_3_.prevPosY) * f + 1.62 - p_77659_3_.yOffset;
        final double d3 = p_77659_3_.prevPosZ + (p_77659_3_.posZ - p_77659_3_.prevPosZ) * f;
        final Vec3 vec3 = Vec3.createVectorHelper(d0, d2, d3);
        final float f4 = MathHelper.cos(-f3 * 0.017453292f - (float) Math.PI);
        final float f5 = MathHelper.sin(-f3 * 0.017453292f - (float) Math.PI);
        final float f6 = -MathHelper.cos(-f2 * 0.017453292f);
        final float f7 = MathHelper.sin(-f2 * 0.017453292f);
        final float f8 = f5 * f6;
        final float f9 = f4 * f6;
        final double d4 = 5.0;
        final Vec3 vec4 = vec3.addVector(f8 * d4, f7 * d4, f9 * d4);
        final MovingObjectPosition movingobjectposition = p_77659_2_.rayTraceBlocks(vec3, vec4, true);
        if (movingobjectposition == null) {
            return p_77659_1_;
        }
        final Vec3 vec5 = p_77659_3_.getLook(f);
        boolean flag = false;
        final float f10 = 1.0f;
        final List list = p_77659_2_.getEntitiesWithinAABBExcludingEntity(
                p_77659_3_,
                p_77659_3_.boundingBox.addCoord(vec5.xCoord * d4, vec5.yCoord * d4, vec5.zCoord * d4)
                        .expand(f10, f10, f10));
        for (Object o : list) {
            final Entity entity = (Entity) o;
            if (entity.canBeCollidedWith()) {
                final float f11 = entity.getCollisionBorderSize();
                final AxisAlignedBB axisalignedbb = entity.boundingBox.expand(f11, f11, f11);
                if (axisalignedbb.isVecInside(vec3)) {
                    flag = true;
                }
            }
        }
        if (flag) {
            return p_77659_1_;
        }
        if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            final int i = movingobjectposition.blockX;
            int j = movingobjectposition.blockY;
            final int k = movingobjectposition.blockZ;
            if (p_77659_2_.getBlock(i, j, k) == Blocks.snow_layer) {
                --j;
            }
            final EntityBoatGreatwood entityboat = new EntityBoatGreatwood(p_77659_2_, i + 0.5f, j + 1.0f, k + 0.5f);
            entityboat.rotationYaw = ((MathHelper.floor_double(p_77659_3_.rotationYaw * 4.0f / 360.0f + 0.5) & 0x3) - 1)
                    * 90;
            if (!p_77659_2_.getCollidingBoundingBoxes(entityboat, entityboat.boundingBox.expand(-0.1, -0.1, -0.1))
                    .isEmpty()) {
                return p_77659_1_;
            }
            if (!p_77659_2_.isRemote) {
                p_77659_2_.spawnEntityInWorld(entityboat);
            }
            if (!p_77659_3_.capabilities.isCreativeMode) {
                --p_77659_1_.stackSize;
            }
        }
        return p_77659_1_;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:boatgreatwood");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.icon;
    }

    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        return "item.boatGreatwood";
    }
}
