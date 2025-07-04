//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items.lenses;

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
import com.kentington.thaumichorizons.common.lib.networking.PacketRemoveNightvision;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemLensFire extends Item implements ILens {

    IIcon icon;

    public ItemLensFire() {
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    public String lensName() {
        return "LensFire";
    }

    public void handleRender(final Minecraft mc, final float partialTicks) {
        final boolean inWater = mc.thePlayer.isInsideOfMaterial(Material.water);
        final PotionEffect effect = mc.thePlayer.getActivePotionEffect(Potion.nightVision);

        if (!inWater) {
            // Apply effect.
            if ((effect == null || (isEffectGrantedByLens(effect) && effect.getDuration() < 242))
                    && Minecraft.getSystemTime() > LensManager.nightVisionOffTime) {
                LensManager.nightVisionOffTime = Minecraft.getSystemTime();
                mc.thePlayer.addPotionEffect(new IlluminePotionEffect(Potion.nightVision.id, 255, -1, true));
            }
        } else {
            // Remove effect.
            if (isEffectGrantedByLens(effect)) {
                mc.thePlayer.removePotionEffect(Potion.nightVision.id);
            }
        }
    }

    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        return "item.LensFire";
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:lensfire");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.icon;
    }

    public void handleRemoval(final EntityPlayer p) {
        if (isEffectGrantedByLens(p.getActivePotionEffect(Potion.nightVision))) {
            p.removePotionEffect(Potion.nightVision.id);
            PacketHandler.INSTANCE.sendTo(new PacketRemoveNightvision(), (EntityPlayerMP) p);
        }
    }

    public static boolean isEffectGrantedByLens(PotionEffect effect) {
        return effect instanceof IlluminePotionEffect illumineEffect && illumineEffect.isGrantedByLens;
    }

    private static class IlluminePotionEffect extends PotionEffect {

        public boolean isGrantedByLens;

        public IlluminePotionEffect(int potionID, int duration, int amplifier, boolean isAmbient) {
            super(potionID, duration, amplifier, isAmbient);
            isGrantedByLens = true;
        }

        public void combine(PotionEffect effect) {
            // If this is replaced by another night vision effect, for example from drinking a potion, it should not be
            // turned off by the lens.
            super.combine(effect);
            isGrantedByLens = effect instanceof IlluminePotionEffect;
        }
    }
}
