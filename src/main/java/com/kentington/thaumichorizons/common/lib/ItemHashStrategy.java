package com.kentington.thaumichorizons.common.lib;

import java.util.Objects;

import javax.annotation.Nullable;

import net.minecraft.item.ItemStack;

import it.unimi.dsi.fastutil.Hash;

/*
 * Simple hashing strategy that can properly compare items
 */
public class ItemHashStrategy implements Hash.Strategy<ItemStack> {

    @Override
    public int hashCode(@Nullable ItemStack key) {
        return key == null ? 0 : Objects.hash(key.getItem(), key.getItemDamage());
    }

    @Override
    public boolean equals(@Nullable ItemStack key, @Nullable ItemStack value) {
        if (key == null || value == null) return false;
        return key.isItemEqual(value);
    }
}
