//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib.networking;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import com.kentington.thaumichorizons.common.lib.EntityInfusionProperties;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketToggleInvisibleToServer
        implements IMessage, IMessageHandler<PacketToggleInvisibleToServer, IMessage> {

    public PacketToggleInvisibleToServer() {}

    public void toBytes(final ByteBuf buffer) {
    }

    public void fromBytes(final ByteBuf buffer) {
    }

    public IMessage onMessage(final PacketToggleInvisibleToServer message, final MessageContext ctx) {
        if (PacketHandler
                .selfInfusionSecurityCheck(ctx, "toggle chamelon skin (i.e. invisible)", 10)) {
            return null;
        }
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        EntityInfusionProperties ieep = (EntityInfusionProperties) player.getExtendedProperties("CreatureInfusion");
        ieep.toggleInvisible = !ieep.toggleInvisible;
        if (ieep.toggleInvisible) {
            player.removePotionEffect(Potion.invisibility.id);
            player.setInvisible(false);
        } else {
            final PotionEffect effect = new PotionEffect(Potion.invisibility.id, Integer.MAX_VALUE, 0, true);
            effect.setCurativeItems(new ArrayList<>());
            player.addPotionEffect(effect);
            player.setInvisible(true);
        }
        return null;
    }
}
