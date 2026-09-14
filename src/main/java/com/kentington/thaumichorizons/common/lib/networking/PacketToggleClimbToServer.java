//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib.networking;

import net.minecraft.entity.player.EntityPlayer;

import com.kentington.thaumichorizons.common.lib.EntityInfusionProperties;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketToggleClimbToServer implements IMessage, IMessageHandler<PacketToggleClimbToServer, IMessage> {

    public PacketToggleClimbToServer() {}

    public void toBytes(final ByteBuf buffer) {}

    public void fromBytes(final ByteBuf buffer) {}

    public IMessage onMessage(final PacketToggleClimbToServer message, final MessageContext ctx) {
        if (PacketHandler.selfInfusionSecurityCheck(ctx, "toggle spider climb", 9)) {
            return null;
        }
        final EntityPlayer player = ctx.getServerHandler().playerEntity;
        ((EntityInfusionProperties) player
                .getExtendedProperties("CreatureInfusion")).toggleClimb = !((EntityInfusionProperties) player
                        .getExtendedProperties("CreatureInfusion")).toggleClimb;
        return null;
    }
}
