//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import com.kentington.thaumichorizons.common.items.lenses.LensManager;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.nodes.IRevealer;

public class PacketLensChangeToServer implements IMessage, IMessageHandler<PacketLensChangeToServer, IMessage> {

    private String lens;

    public PacketLensChangeToServer() {}

    public PacketLensChangeToServer(final String lens) {
        this.lens = lens;
    }

    public void toBytes(final ByteBuf buffer) {
        ByteBufUtils.writeUTF8String(buffer, this.lens);
    }

    public void fromBytes(final ByteBuf buffer) {
        this.lens = ByteBufUtils.readUTF8String(buffer);
    }

    public IMessage onMessage(final PacketLensChangeToServer message, final MessageContext ctx) {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player.worldObj == null) {
            return null;
        }
        if (player.inventory.armorItemInSlot(3) != null
                && player.inventory.armorItemInSlot(3).getItem() instanceof IRevealer) {
            LensManager.changeLens(player.inventory.armorItemInSlot(3), player.worldObj, player, message.lens);
        }
        return null;
    }
}
