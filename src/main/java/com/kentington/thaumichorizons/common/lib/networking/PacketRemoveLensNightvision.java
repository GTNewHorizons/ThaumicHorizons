//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib.networking;

import static com.kentington.thaumichorizons.common.items.lenses.LensPotionEffects.isNightVisionGrantedByLens;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.kentington.thaumichorizons.common.items.lenses.LensManager;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;

public class PacketRemoveLensNightvision implements IMessage, IMessageHandler<PacketRemoveLensNightvision, IMessage> {

    @SideOnly(Side.CLIENT)
    public IMessage onMessage(final PacketRemoveLensNightvision message, final MessageContext ctx) {
        final PotionEffect effect = Minecraft.getMinecraft().thePlayer.getActivePotionEffect(Potion.nightVision);
        if (isNightVisionGrantedByLens(effect)) {
            Minecraft.getMinecraft().thePlayer.removePotionEffect(Potion.nightVision.id);
            LensManager.nightVisionOffTime = Minecraft.getSystemTime() + 100L;
        }
        return null;
    }

    public void fromBytes(final ByteBuf buf) {}

    public void toBytes(final ByteBuf buf) {}
}
