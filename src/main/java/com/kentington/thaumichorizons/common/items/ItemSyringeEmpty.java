//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSyringeEmpty extends Item {

    @SideOnly(Side.CLIENT)
    public IIcon icon;

    public ItemSyringeEmpty() {
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:syringeEmpty");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.icon;
    }

    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        return "item.syringeEmpty";
    }

    public ItemStack onItemRightClick(final ItemStack p_77659_1_, final World world, final EntityPlayer p) {
        final Entity ent = ItemFocusContainment.getPointedEntity(world, p, 1.5);
        if (ent instanceof final EntityLiving critter && !(ent instanceof EntityPlayer)) {
            if (critter.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD && !(critter instanceof INpc)
                    && !(critter instanceof IMerchant)
                    && (critter.isCreatureType(EnumCreatureType.creature, false)
                            || critter.isCreatureType(EnumCreatureType.ambient, false)
                            || critter.isCreatureType(EnumCreatureType.waterCreature, false))) {
                final ItemStack bloodSample = new ItemStack(ThaumicHorizons.itemSyringeBloodSample);
                bloodSample.stackTagCompound = new NBTTagCompound();
                final NBTTagCompound critterTag = new NBTTagCompound();
                critter.writeToNBT(critterTag);
                critterTag.setString("id", EntityList.getEntityString(ent));
                // Strip inventory-related tags - a blood sample should only carry genetic data
                critterTag.removeTag("Items");
                critterTag.removeTag("ArmorItem");
                critterTag.removeTag("SaddleItem");
                critterTag.removeTag("ChestedHorse");
                bloodSample.stackTagCompound.setString("critterName", ent.getCommandSenderName());
                bloodSample.stackTagCompound.setTag("critter", critterTag);
                if (p.inventory.addItemStackToInventory(bloodSample)) {
                    --p_77659_1_.stackSize;
                }
            }
        } else {
            final ItemStack result = new ItemStack(ThaumicHorizons.itemSyringeHuman);
            if (p.inventory.addItemStackToInventory(result)) {
                --p_77659_1_.stackSize;
            }
        }
        return p_77659_1_;
    }
}
