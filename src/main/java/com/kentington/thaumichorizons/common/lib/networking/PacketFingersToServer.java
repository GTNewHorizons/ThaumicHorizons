//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib.networking;

import net.minecraft.entity.player.EntityPlayer;

import com.kentington.thaumichorizons.common.ThaumicHorizons;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketFingersToServer implements IMessage, IMessageHandler<PacketFingersToServer, IMessage> {

    public PacketFingersToServer() {}

    public void toBytes(final ByteBuf buffer) {}

    public void fromBytes(final ByteBuf buffer) {}

    public IMessage onMessage(final PacketFingersToServer message, final MessageContext ctx) {
        if (PacketHandler.selfInfusionSecurityCheck(ctx, "open workbench", 2)) {
            return null;
        }
        final EntityPlayer player = ctx.getServerHandler().playerEntity;
        player.openGui(
                ThaumicHorizons.instance,
                9,
                player.worldObj,
                (int) player.posX,
                (int) player.posY,
                (int) player.posZ);
        return null;
    }
}
