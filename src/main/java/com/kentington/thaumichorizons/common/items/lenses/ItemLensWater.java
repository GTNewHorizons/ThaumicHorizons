//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items.lenses;

import static com.kentington.thaumichorizons.common.items.lenses.LensPotionEffects.isNightVisionGrantedByLens;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketRemoveLensNightvision;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLensWater extends Item implements ILens {

    IIcon icon;

    public ItemLensWater() {
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    public String lensName() {
        return "LensWater";
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    public void handleRender(final Minecraft mc, final float partialTicks) {
        final boolean inWater = mc.thePlayer.isInsideOfMaterial(Material.water);
        final PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.nightVision);

        if (inWater) {
            // Apply effect.
            if ((effect == null || (isNightVisionGrantedByLens(effect) && effect.getDuration() < 242))
                    && Minecraft.getSystemTime() > LensManager.nightVisionOffTime) {
                LensManager.nightVisionOffTime = Minecraft.getSystemTime();
                mc.thePlayer
                        .addPotionEffect(new LensPotionEffects.LensNightVision(Potion.nightVision.id, 255, -1, true));
            }
        } else {
            // Remove effect.
            if (isNightVisionGrantedByLens(effect)) {
                mc.thePlayer.removePotionEffect(Potion.nightVision.id);
            }
        }
    }

    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        return "item.LensWater";
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:lenswater");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.icon;
    }

    public void handleRemoval(final EntityPlayer p) {
        PacketHandler.INSTANCE.sendTo(new PacketRemoveLensNightvision(), (EntityPlayerMP) p);
    }
}
