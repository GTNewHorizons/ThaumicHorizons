//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib.networking;

import net.minecraft.entity.player.EntityPlayerMP;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.lib.EntityInfusionProperties;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler {

    public static final SimpleNetworkWrapper INSTANCE;
    private static final Marker SECURITY_MARKER = MarkerManager.getMarker("SuspiciousPackets");

    public static void init() {
        int idx = 0;
        PacketHandler.INSTANCE
                .registerMessage(PacketLensChangeToServer.class, PacketLensChangeToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE
                .registerMessage(PacketFXEssentiaBubble.class, PacketFXEssentiaBubble.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketInfusionFX.class, PacketInfusionFX.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketFXInfusionDone.class, PacketFXInfusionDone.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketFXDeadCreature.class, PacketFXDeadCreature.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketFXContainment.class, PacketFXContainment.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE.registerMessage(PacketNoMoreItems.class, PacketNoMoreItems.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketFXBlocksplosion.class, PacketFXBlocksplosion.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketRemoveNightvision.class, PacketRemoveNightvision.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketFingersToServer.class, PacketFingersToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE
                .registerMessage(PacketPlayerInfusionSync.class, PacketPlayerInfusionSync.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketMountNightmare.class, PacketMountNightmare.class, idx++, Side.CLIENT);
        PacketHandler.INSTANCE
                .registerMessage(PacketToggleClimbToServer.class, PacketToggleClimbToServer.class, idx++, Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(
                PacketToggleInvisibleToServer.class,
                PacketToggleInvisibleToServer.class,
                idx++,
                Side.SERVER);
        PacketHandler.INSTANCE.registerMessage(PacketRainState.class, PacketRainState.class, idx++, Side.CLIENT);
    }

    static {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("ThaumicHorizons".toLowerCase());
    }

    private static void securityWarn(String message, Object... args) {
        ThaumicHorizons.log.warn(SECURITY_MARKER, message, args);
    }

    static boolean selfInfusionSecurityCheck(final MessageContext ctx, String action, int sentPlayerID,
            int requiredInfusion) {
        final EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player.getEntityId() != sentPlayerID) {
            PacketHandler.securityWarn("Player {} tried to {} for other people!", player.getGameProfile(), action);
            return false;
        }
        EntityInfusionProperties ieep = (EntityInfusionProperties) player.getExtendedProperties("CreatureInfusion");
        if (!ieep.hasInfusion(requiredInfusion)) {
            PacketHandler.securityWarn(
                    "Player {} tried to {} getting the ability legitimately",
                    player.getGameProfile(),
                    action);
            return false;
        }
        return true;
    }
}
