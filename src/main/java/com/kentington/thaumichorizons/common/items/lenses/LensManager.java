//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items.lenses;

import java.util.HashMap;
import java.util.TreeMap;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;

import baubles.api.BaublesApi;
import thaumcraft.api.nodes.IRevealer;

public class LensManager {

    public static long nightVisionOffTime;

    public static void changeLens(final ItemStack is, final World w, final EntityPlayer player, final String lens) {
        final TreeMap<String, Integer> lenses = new TreeMap<>();
        final HashMap<Integer, Integer> pouches = new HashMap<>();
        int pouchcount = 0;
        ItemStack item = null;
        final IInventory baubles = BaublesApi.getBaubles(player);
        final int baubleSlots = baubles.getSizeInventory();
        for (int a = 0; a < baubleSlots; ++a) {
            if (baubles.getStackInSlot(a) != null && baubles.getStackInSlot(a).getItem() instanceof ItemLensCase lensCase) {
                ++pouchcount;
                item = baubles.getStackInSlot(a);
                pouches.put(pouchcount, a - baubleSlots);
                final ItemStack[] inv = lensCase.getInventory(item);
                for (int q = 0; q < inv.length; ++q) {
                    item = inv[q];
                    if (item != null && item.getItem() instanceof ILens newLens) {
                        lenses.put(newLens.lensName(), q + pouchcount * 1000);
                    }
                }
            }
        }
        for (int newkey = 0; newkey < 36; ++newkey) {
            item = player.inventory.mainInventory[newkey];
            if (item != null && item.getItem() instanceof ILens) {
                lenses.put(((ILens) item.getItem()).lensName(), newkey);
            }
            if (item != null && item.getItem() instanceof ItemLensCase lensCase) {
                ++pouchcount;
                pouches.put(pouchcount, newkey);
                final ItemStack[] pid = lensCase.getInventory(item);
                for (int pouchslot = 0; pouchslot < pid.length; ++pouchslot) {
                    item = pid[pouchslot];
                    if (item != null && item.getItem() instanceof ILens newLens) {
                        lenses.put(newLens.lensName(), pouchslot + pouchcount * 1000);
                    }
                }
            }
        }
        ItemStack oldLens;
        if (!lens.equals("REMOVE") && !lenses.isEmpty()) {
            String var13 = lens;
            if (lenses.get(lens) == null) {
                var13 = lenses.higherKey(lens);
            }
            if (var13 == null || lenses.get(var13) == null) {
                var13 = lenses.firstKey();
            }
            if (lenses.get(var13) < 1000) {
                item = player.inventory.mainInventory[lenses.get(var13)].copy();
            } else {
                final int var14 = lenses.get(var13) / 1000;
                if (pouches.containsKey(var14)) {
                    final int pouchSlot = pouches.get(var14);
                    final int lensSlot = lenses.get(var13) - (var14 * 1000);
                    ItemStack tmp;
                    if (pouchSlot >= 0) {
                        tmp = player.inventory.mainInventory[pouchSlot].copy();
                    } else {
                        tmp = baubles.getStackInSlot(pouchSlot + baubleSlots).copy();
                    }
                    item = fetchLensFromPouch(player, lensSlot, tmp, pouchSlot);
                }
            }
            if (item == null) {
                return;
            }
            if (lenses.get(var13) < 1000) {
                player.inventory.decrStackSize(lenses.get(var13), 1);
            }
            w.playSoundAtEntity(player, "thaumcraft:cameraticks", 0.3f, 1.0f);
            String currentLens = "";
            if (is.stackTagCompound != null) {
                currentLens = is.stackTagCompound.getString("Lens");
            }
            oldLens = getLensItem(currentLens);
            if (!currentLens.isEmpty() && (addLensToPouch(player, oldLens, pouches)
                    || player.inventory.addItemStackToInventory(oldLens))) {
                setLensItem(is, item);
            } else if (currentLens.isEmpty()) {
                setLensItem(is, item);
            } else if (!addLensToPouch(player, item, pouches)) {
                player.inventory.addItemStackToInventory(item);
            }
        } else {
            String currentLens2 = "";
            if (is.stackTagCompound != null) {
                currentLens2 = is.stackTagCompound.getString("Lens");
            }
            oldLens = getLensItem(currentLens2);
            if (!currentLens2.isEmpty() && (addLensToPouch(player, oldLens, pouches)
                    || player.inventory.addItemStackToInventory(oldLens))) {
                setLensItem(is, null);
                w.playSoundAtEntity(player, "thaumcraft:cameraticks", 0.3f, 0.9f);
            }
        }
        if (oldLens != null) {
            ((ILens) oldLens.getItem()).handleRemoval(player);
        }
    }

    private static ItemStack fetchLensFromPouch(final EntityPlayer player, final int lensId, final ItemStack pouch,
            final int pouchSlot) {
        ItemStack lens = null;
        final ItemStack[] inv = ((ItemLensCase) pouch.getItem()).getInventory(pouch);
        final ItemStack contents = inv[lensId];
        if (contents != null && contents.getItem() instanceof ILens) {
            lens = contents.copy();
            if (contents.stackSize <= 1) {
                inv[lensId] = null;
            } else {
                contents.stackSize--;
            }
            ((ItemLensCase) pouch.getItem()).setInventory(pouch, inv);
            if (pouchSlot >= 0) {
                player.inventory.setInventorySlotContents(pouchSlot, pouch);
                player.inventory.markDirty();
            } else {
                final IInventory baubles = BaublesApi.getBaubles(player);
                baubles.setInventorySlotContents(pouchSlot + baubles.getSizeInventory(), pouch);
                baubles.markDirty();
            }
        }
        return lens;
    }

    private static boolean addLensToPouch(final EntityPlayer player, final ItemStack lens,
            final HashMap<Integer, Integer> pouches) {
        for (int pouchSlot : pouches.values()) {
            final IInventory baubles = BaublesApi.getBaubles(player);
            ItemStack pouch;
            if (pouchSlot >= 0) {
                pouch = player.inventory.mainInventory[pouchSlot];
            } else {
                pouch = baubles.getStackInSlot(pouchSlot + baubles.getSizeInventory());
            }
            final ItemStack[] inv = ((ItemLensCase) pouch.getItem()).getInventory(pouch);
            for (int q = 0; q < inv.length; ++q) {
                final ItemStack contents = inv[q];
                if (contents == null) {
                    inv[q] = lens.copy();
                    ((ItemLensCase) pouch.getItem()).setInventory(pouch, inv);
                    if (pouchSlot >= 0) {
                        player.inventory.setInventorySlotContents(pouchSlot, pouch);
                        player.inventory.markDirty();
                    } else {
                        baubles.setInventorySlotContents(pouchSlot + baubles.getSizeInventory(), pouch);
                        baubles.markDirty();
                    }
                    player.inventory.markDirty();
                    return true;
                }
            }
        }
        return false;
    }

    public static ItemStack getLensItem(final String lens) {
        if (getLens(lens) != null) {
            return new ItemStack(getLens(lens));
        }
        return null;
    }

    public static ILens getLensFromItem(final ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof IRevealer) || stack.stackTagCompound == null) {
            return null;
        }
        final String lensName = stack.stackTagCompound.getString("Lens");
        if (lensName.isEmpty()) {
            return null;
        }
        final Item lensItem = getLens(lensName);
        return lensItem instanceof ILens ? (ILens) lensItem : null;
    }

    public static Item getLens(final String lens) {
        return switch (lens) {
            case "LensFire" -> ThaumicHorizons.itemLensFire;
            case "LensWater" -> ThaumicHorizons.itemLensWater;
            case "LensEarth" -> ThaumicHorizons.itemLensEarth;
            case "LensAir" -> ThaumicHorizons.itemLensAir;
            case "LensOrderEntropy" -> ThaumicHorizons.itemLensOrderEntropy;
            default -> null;
        };
    }

    public static void setLensItem(final ItemStack goggles, final ItemStack lens) {
        if (!goggles.hasTagCompound()) {
            goggles.stackTagCompound = new NBTTagCompound();
        }
        int lensIndex = goggles.stackTagCompound.getInteger("LensIndex");
        NBTTagList lore = null;
        if (goggles.stackTagCompound != null && goggles.stackTagCompound.getCompoundTag("display") != null) {
            lore = goggles.stackTagCompound.getCompoundTag("display").getTagList("Lore", 8);
        }
        if (lore == null || lore.tagCount() == 0) {
            if (goggles.stackTagCompound == null) {
                goggles.stackTagCompound = new NBTTagCompound();
            }
            if (goggles.stackTagCompound.getCompoundTag("display").hasNoTags()) {
                goggles.stackTagCompound.setTag("display", new NBTTagCompound());
            }
            if (goggles.stackTagCompound.getCompoundTag("display").getTagList("Lore", 8).tagCount() == 0) {
                goggles.stackTagCompound.getCompoundTag("display").setTag("Lore", new NBTTagList());
            }
            lore = goggles.stackTagCompound.getCompoundTag("display").getTagList("Lore", 8);
            lensIndex = 0;
        }
        if (lens == null) {
            goggles.stackTagCompound.removeTag("Lens");
            if (lensIndex >= 0 && lore.tagCount() > lensIndex) {
                lore.removeTag(lensIndex);
            }
            goggles.stackTagCompound.setInteger("LensIndex", -1);
        } else {
            goggles.stackTagCompound.removeTag("Lens");
            goggles.stackTagCompound.setString("Lens", ((ILens) lens.getItem()).lensName());
            if (lensIndex != -1 && lore.tagCount() > lensIndex) {
                lore.removeTag(lensIndex);
            }
            goggles.stackTagCompound.setInteger("LensIndex", lore.tagCount());
            lore.appendTag(
                    new NBTTagString(
                            StatCollector.translateToLocal("item." + ((ILens) lens.getItem()).lensName() + ".name")));
        }
    }

    static {
        LensManager.nightVisionOffTime = 0L;
    }
}
