//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.google.common.collect.HashMultimap;
import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityBlastPhial;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSyringeInjection extends ItemPotion {

    private IIcon field_94590_d;
    private IIcon field_94591_c;
    private IIcon field_94592_ct;

    public ItemSyringeInjection() {
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    public int getMaxItemUseDuration(final ItemStack p_77626_1_) {
        return 8;
    }

    public EnumAction getItemUseAction(final ItemStack p_77661_1_) {
        return EnumAction.bow;
    }

    public String getItemStackDisplayName(final ItemStack p_77653_1_) {
        return StatCollector.translateToLocal("item.injection." + p_77653_1_.getItemDamage() + ".name");
    }

    @SideOnly(Side.CLIENT)
    public void getSubItems(final Item p_150895_1_, final CreativeTabs p_150895_2_, final List p_150895_3_) {}

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.field_94590_d = ir.registerIcon("thaumichorizons:phialBlood");
        this.field_94591_c = ir.registerIcon("thaumichorizons:phialBlood");
        this.field_94592_ct = ir.registerIcon("thaumichorizons:phialBlood");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int p_77617_1_) {
        return isSplash(p_77617_1_) ? this.field_94591_c : this.field_94590_d;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(final int p_77618_1_, final int p_77618_2_) {
        return (p_77618_2_ == 0) ? this.field_94592_ct : super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_);
    }

    @SideOnly(Side.CLIENT)
    public static IIcon func_94589_d(final String p_94589_0_) {
        return p_94589_0_.equals("bottle_drinkable")
                ? ((ItemSyringeInjection) ThaumicHorizons.itemSyringeInjection).field_94590_d
                : (p_94589_0_.equals("bottle_splash")
                        ? ((ItemSyringeInjection) ThaumicHorizons.itemSyringeInjection).field_94591_c
                        : (p_94589_0_.equals("overlay")
                                ? ((ItemSyringeInjection) ThaumicHorizons.itemSyringeInjection).field_94592_ct
                                : null));
    }

    public boolean isPhial(final int p_77831_0_) {
        return p_77831_0_ != 0;
    }

    public ItemStack onItemRightClick(final ItemStack p_77659_1_, final World p_77659_2_,
            final EntityPlayer p_77659_3_) {
        if (this.isPhial(p_77659_1_.getItemDamage())) {
            if (!p_77659_3_.capabilities.isCreativeMode) {
                --p_77659_1_.stackSize;
            }
            p_77659_2_.playSoundAtEntity(
                    p_77659_3_,
                    "random.bow",
                    0.5f,
                    0.4f / (ItemSyringeInjection.itemRand.nextFloat() * 0.4f + 0.8f));
            if (!p_77659_2_.isRemote) {
                p_77659_2_.spawnEntityInWorld(new EntityBlastPhial(p_77659_2_, p_77659_3_, 0.5f, p_77659_1_));
            }
            return p_77659_1_;
        }
        p_77659_3_.setItemInUse(p_77659_1_, this.getMaxItemUseDuration(p_77659_1_));
        return p_77659_1_;
    }

    @SideOnly(Side.CLIENT)
    public void addInformation(final ItemStack itemStack, final EntityPlayer player, final List messages,
            final boolean ignored) {
        @SuppressWarnings("unchecked") // Vanilla code uses raw types
        final List<PotionEffect> potionEffects = Items.potionitem.getEffects(itemStack);
        final HashMultimap<IAttribute, AttributeModifier> preparedEffects = HashMultimap.create();

        if (potionEffects != null && !potionEffects.isEmpty()) {
            for (PotionEffect potioneffect : potionEffects) {
                String message = StatCollector.translateToLocal(potioneffect.getEffectName()).trim();
                final Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                @SuppressWarnings("unchecked") // Vanilla code uses raw types
                final Map<IAttribute, AttributeModifier> map = potion.func_111186_k();

                if (map != null && !map.isEmpty()) {
                    for (final Map.Entry<IAttribute, AttributeModifier> entry : map.entrySet()) {
                        final AttributeModifier entryValue = entry.getValue();
                        final AttributeModifier attributeModifier = new AttributeModifier(
                                entryValue.getName(),
                                potion.func_111183_a(potioneffect.getAmplifier(), entryValue),
                                entryValue.getOperation());
                        preparedEffects.put(entry.getKey(), attributeModifier);
                    }
                }
                if (potioneffect.getAmplifier() > 0) {
                    message = message + " "
                            + StatCollector.translateToLocal("potion.potency." + potioneffect.getAmplifier()).trim();
                }
                if (potioneffect.getDuration() > 20) {
                    message = message + " (" + Potion.getDurationString(potioneffect) + ")";
                }
                if (potion.isBadEffect()) {
                    messages.add(EnumChatFormatting.RED + message);
                } else {
                    messages.add(EnumChatFormatting.GRAY + message);
                }
            }
        } else {
            messages.add(EnumChatFormatting.GRAY + StatCollector.translateToLocal("potion.empty").trim());
        }
        if (!preparedEffects.isEmpty()) {
            messages.add("");
            messages.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("potion.effects.whenDrank"));
            for (final Map.Entry<IAttribute, AttributeModifier> entry : preparedEffects.entries()) {
                final AttributeModifier entryValue = entry.getValue();
                final double valueAmount = entryValue.getAmount();
                double convertedAmount;
                if (entryValue.getOperation() != 1 && entryValue.getOperation() != 2) {
                    convertedAmount = entryValue.getAmount();
                } else {
                    convertedAmount = entryValue.getAmount() * 100.0;
                }
                if (valueAmount > 0.0) {
                    messages.add(
                            EnumChatFormatting.BLUE + StatCollector.translateToLocalFormatted(
                                    "attribute.modifier.plus." + entryValue.getOperation(),
                                    new Object[] { ItemStack.field_111284_a.format(convertedAmount),
                                            StatCollector.translateToLocal("attribute.name." + entry.getKey()) }));
                } else {
                    if (valueAmount >= 0.0) {
                        continue;
                    }
                    convertedAmount *= -1.0;
                    messages.add(
                            EnumChatFormatting.RED + StatCollector.translateToLocalFormatted(
                                    "attribute.modifier.take." + entryValue.getOperation(),
                                    new Object[] { ItemStack.field_111284_a.format(convertedAmount),
                                            StatCollector.translateToLocal("attribute.name." + entry.getKey()) }));
                }
            }
        }
    }

    public boolean hitEntity(final ItemStack is, final EntityLivingBase target, final EntityLivingBase hitter) {
        if (!target.worldObj.isRemote) {
            final List<PotionEffect> list = this.getEffects(is);
            if (list != null) {
                for (final PotionEffect potioneffect : list) {
                    target.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }
        --is.stackSize;
        return super.hitEntity(is, target, hitter);
    }

    public int getColorFromItemStack(final ItemStack stack, final int p_82790_2_) {
        if (stack.getItem() == ThaumicHorizons.itemSyringeInjection) {
            if (stack.hasTagCompound()) {
                return stack.getTagCompound().getInteger("color");
            }
        } else if (stack.getItem() != ThaumicHorizons.itemSyringeEmpty) {
            return Color.RED.getRGB();
        }
        return 16777215;
    }

    public ItemStack onEaten(final ItemStack p_77654_1_, final World p_77654_2_, final EntityPlayer p_77654_3_) {
        if (!p_77654_3_.capabilities.isCreativeMode) {
            --p_77654_1_.stackSize;
        }
        if (!p_77654_2_.isRemote) {
            final List<PotionEffect> list = this.getEffects(p_77654_1_);
            if (list != null) {
                for (final PotionEffect potioneffect : list) {
                    p_77654_3_.addPotionEffect(new PotionEffect(potioneffect));
                }
            }
        }
        if (p_77654_1_.stackSize <= 0) {
            return null;
        }
        return p_77654_1_;
    }
}
