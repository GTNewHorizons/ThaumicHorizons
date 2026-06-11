//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib.networking;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;

import com.kentington.thaumichorizons.common.items.lenses.LensManager;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import thaumcraft.api.nodes.IRevealer;

public class PacketLensChangeToServer implements IMessage, IMessageHandler<PacketLensChangeToServer, IMessage> {

    private int dim;
    private int playerid;
    private String lens;

    public PacketLensChangeToServer() {}

    public PacketLensChangeToServer(final EntityPlayer player, final String lens) {
        this.dim = player.worldObj.provider.dimensionId;
        this.playerid = player.getEntityId();
        this.lens = lens;
    }

    public void toBytes(final ByteBuf buffer) {
        buffer.writeInt(this.dim);
        buffer.writeInt(this.playerid);
        ByteBufUtils.writeUTF8String(buffer, this.lens);
    }

    public void fromBytes(final ByteBuf buffer) {
        this.dim = buffer.readInt();
        this.playerid = buffer.readInt();
        this.lens = ByteBufUtils.readUTF8String(buffer);
    }

    public IMessage onMessage(final PacketLensChangeToServer message, final MessageContext ctx) {
        final World world = DimensionManager.getWorld(message.dim);
        if (world == null || (ctx.getServerHandler().playerEntity != null
                && ctx.getServerHandler().playerEntity.getEntityId() != message.playerid)) {
            return null;
        }
        final Entity e = world.getEntityByID(message.playerid);
        if (e instanceof EntityPlayer player && player.inventory.armorItemInSlot(3) != null
                && player.inventory.armorItemInSlot(3).getItem() instanceof IRevealer) {
            LensManager.changeLens(player.inventory.armorItemInSlot(3), world, player, message.lens);
        }
        return null;
    }
}
