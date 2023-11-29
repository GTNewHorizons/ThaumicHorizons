//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib;

import java.util.ArrayList;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import org.lwjgl.input.Keyboard;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.lib.networking.PacketFingersToServer;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketLensChangeToServer;
import com.kentington.thaumichorizons.common.lib.networking.PacketToggleClimbToServer;
import com.kentington.thaumichorizons.common.lib.networking.PacketToggleInvisibleToServer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.nodes.IRevealer;

public class THKeyHandler {

    public KeyBinding keyV;
    public KeyBinding keyM;
    public KeyBinding keyC;
    public KeyBinding keyX;
    private boolean keyPressedM;
    public static long lastPressM;
    private boolean keyPressedC;
    public static long lastPressC;
    private boolean keyPressedX;
    public static long lastPressX;
    private boolean keyPressedV;
    public static boolean radialActive;
    public static boolean radialLock;
    public static long lastPressV;

    public THKeyHandler() {
        this.keyV = new KeyBinding("Change Arcane Lens", Keyboard.KEY_NONE, "Thaumcraft");
        this.keyM = new KeyBinding("Activate Morphic Fingers", Keyboard.KEY_NONE, "Thaumcraft");
        this.keyC = new KeyBinding("Toggle Spider Climb", Keyboard.KEY_NONE, "Thaumcraft");
        this.keyX = new KeyBinding("Toggle Chameleon Skin", Keyboard.KEY_NONE, "Thaumcraft");
        this.keyPressedM = false;
        this.keyPressedC = false;
        this.keyPressedX = false;
        this.keyPressedV = false;
        ClientRegistry.registerKeyBinding(this.keyV);
        ClientRegistry.registerKeyBinding(this.keyM);
        ClientRegistry.registerKeyBinding(this.keyC);
        ClientRegistry.registerKeyBinding(this.keyX);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void playerTick(final TickEvent.PlayerTickEvent event) {
        if (event.side == Side.SERVER) {
            return;
        }
        if (event.phase == TickEvent.Phase.START) {
            if (this.keyV.getIsKeyPressed()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    final EntityPlayer player = event.player;
                    if (player != null) {
                        if (!this.keyPressedV) {
                            THKeyHandler.lastPressV = System.currentTimeMillis();
                            THKeyHandler.radialLock = false;
                        }
                        if (!THKeyHandler.radialLock && player.inventory.armorItemInSlot(3) != null
                                && player.inventory.armorItemInSlot(3).getItem() instanceof IRevealer) {
                            if (player.isSneaking()) {
                                PacketHandler.INSTANCE.sendToServer(new PacketLensChangeToServer(player, "REMOVE"));
                            } else {
                                THKeyHandler.radialActive = true;
                            }
                        }
                    }
                    this.keyPressedV = true;
                }
            } else {
                THKeyHandler.radialActive = false;
                if (this.keyPressedV) {
                    THKeyHandler.lastPressV = System.currentTimeMillis();
                }
                this.keyPressedV = false;
            }
            if (this.keyM.getIsKeyPressed()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    final EntityPlayer player = event.player;
                    if (player != null) {
                        if (!this.keyPressedM) {
                            THKeyHandler.lastPressM = System.currentTimeMillis();
                        }
                        if (((EntityInfusionProperties) player.getExtendedProperties("CreatureInfusion"))
                                .hasPlayerInfusion(2) && !this.keyPressedM) {
                            player.openGui(
                                    ThaumicHorizons.instance,
                                    9,
                                    player.worldObj,
                                    (int) player.posX,
                                    (int) player.posY,
                                    (int) player.posZ);
                            PacketHandler.INSTANCE.sendToServer(new PacketFingersToServer(player, player.dimension));
                        }
                    }
                    this.keyPressedM = true;
                }
            } else {
                if (this.keyPressedM) {
                    THKeyHandler.lastPressM = System.currentTimeMillis();
                }
                this.keyPressedM = false;
            }
            if (this.keyC.getIsKeyPressed()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    final EntityPlayer player = event.player;
                    if (player != null) {
                        if (!this.keyPressedC) {
                            THKeyHandler.lastPressC = System.currentTimeMillis();
                        }
                        if (((EntityInfusionProperties) player.getExtendedProperties("CreatureInfusion"))
                                .hasPlayerInfusion(9) && !this.keyPressedC) {
                            ((EntityInfusionProperties) player.getExtendedProperties(
                                    "CreatureInfusion")).toggleClimb = !((EntityInfusionProperties) player
                                            .getExtendedProperties("CreatureInfusion")).toggleClimb;
                            if (((EntityInfusionProperties) player
                                    .getExtendedProperties("CreatureInfusion")).toggleClimb) {
                                player.addChatMessage(
                                        new ChatComponentText(
                                                EnumChatFormatting.ITALIC + ""
                                                        + EnumChatFormatting.GRAY
                                                        + "Spider Climb disabled."));
                            } else {
                                player.addChatMessage(
                                        new ChatComponentText(
                                                EnumChatFormatting.ITALIC + ""
                                                        + EnumChatFormatting.GRAY
                                                        + "Spider Climb enabled."));
                            }
                            PacketHandler.INSTANCE
                                    .sendToServer(new PacketToggleClimbToServer(player, player.dimension));
                        }
                    }
                    this.keyPressedC = true;
                }
            } else {
                if (this.keyPressedC) {
                    THKeyHandler.lastPressC = System.currentTimeMillis();
                }
                this.keyPressedC = false;
            }
            if (this.keyX.getIsKeyPressed()) {
                if (FMLClientHandler.instance().getClient().inGameHasFocus) {
                    final EntityPlayer player = event.player;
                    if (player != null) {
                        if (!this.keyPressedX) {
                            THKeyHandler.lastPressX = System.currentTimeMillis();
                        }
                        if (((EntityInfusionProperties) player.getExtendedProperties("CreatureInfusion"))
                                .hasPlayerInfusion(10) && !this.keyPressedX) {
                            ((EntityInfusionProperties) player.getExtendedProperties(
                                    "CreatureInfusion")).toggleInvisible = !((EntityInfusionProperties) player
                                            .getExtendedProperties("CreatureInfusion")).toggleInvisible;
                            if (((EntityInfusionProperties) player
                                    .getExtendedProperties("CreatureInfusion")).toggleInvisible) {
                                player.removePotionEffectClient(Potion.invisibility.id);
                                player.setInvisible(false);
                                player.addChatMessage(
                                        new ChatComponentText(
                                                EnumChatFormatting.ITALIC + ""
                                                        + EnumChatFormatting.GRAY
                                                        + "Chameleon Skin disabled."));
                            } else {
                                final PotionEffect effect = new PotionEffect(
                                        Potion.invisibility.id,
                                        Integer.MAX_VALUE,
                                        0,
                                        true);
                                effect.setCurativeItems(new ArrayList<>());
                                player.addPotionEffect(effect);
                                player.setInvisible(true);
                                player.addChatMessage(
                                        new ChatComponentText(
                                                EnumChatFormatting.ITALIC + ""
                                                        + EnumChatFormatting.GRAY
                                                        + "Chameleon Skin enabled."));
                            }
                            PacketHandler.INSTANCE
                                    .sendToServer(new PacketToggleInvisibleToServer(player, player.dimension));
                        }
                    }
                    this.keyPressedX = true;
                }
            } else {
                if (this.keyPressedX) {
                    THKeyHandler.lastPressX = System.currentTimeMillis();
                }
                this.keyPressedX = false;
            }
        }
    }

    static {
        THKeyHandler.lastPressM = 0L;
        THKeyHandler.lastPressC = 0L;
        THKeyHandler.lastPressX = 0L;
        THKeyHandler.radialActive = false;
        THKeyHandler.radialLock = false;
        THKeyHandler.lastPressV = 0L;
    }
}
