//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common;

import java.awt.Color;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.container.ContainerBloodInfuser;
import com.kentington.thaumichorizons.common.container.ContainerCase;
import com.kentington.thaumichorizons.common.container.ContainerFingers;
import com.kentington.thaumichorizons.common.container.ContainerInjector;
import com.kentington.thaumichorizons.common.container.ContainerInspiratron;
import com.kentington.thaumichorizons.common.container.ContainerSoulExtractor;
import com.kentington.thaumichorizons.common.container.ContainerSoulforge;
import com.kentington.thaumichorizons.common.container.ContainerVat;
import com.kentington.thaumichorizons.common.container.ContainerVisDynamo;
import com.kentington.thaumichorizons.common.items.WandManagerTH;
import com.kentington.thaumichorizons.common.tiles.TileBloodInfuser;
import com.kentington.thaumichorizons.common.tiles.TileInspiratron;
import com.kentington.thaumichorizons.common.tiles.TileSoulExtractor;
import com.kentington.thaumichorizons.common.tiles.TileSoulforge;
import com.kentington.thaumichorizons.common.tiles.TileVat;
import com.kentington.thaumichorizons.common.tiles.TileVisDynamo;
import com.kentington.thaumichorizons.common.tiles.TileVortex;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import thaumcraft.api.wands.IWandTriggerManager;

public class CommonProxy implements IGuiHandler {

    public IWandTriggerManager wandManager;

    public CommonProxy() {
        this.wandManager = new WandManagerTH();
    }

    public void registerRenderers() {}

    public void registerKeyBindings() {}

    public void sendCustomPacketToAllNear(final Packet packet, final double range, final Entity enity) {
        if (enity != null) {
            FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().sendToAllNear(
                    enity.posX,
                    enity.posY,
                    enity.posZ,
                    range,
                    enity.worldObj.provider.dimensionId,
                    packet);
        }
    }

    public void essentiaTrailEntityFx(final WorldClient world, final int posX, final int posY, final int posZ,
            final Entity end, final int i, final int color, final float scale) {}

    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x,
            final int y, final int z) {
        return null;
    }

    public Object getServerGuiElement(final int ID, final EntityPlayer player, final World world, final int x,
            final int y, final int z) {
        switch (ID) {
            case 1 -> {
                return new ContainerVisDynamo(player, (TileVisDynamo) world.getTileEntity(x, y, z));
            }
            case 2 -> {
                return new ContainerSoulExtractor(player.inventory, (TileSoulExtractor) world.getTileEntity(x, y, z));
            }
            case 3 -> {
                return new ContainerInspiratron(player.inventory, (TileInspiratron) world.getTileEntity(x, y, z));
            }
            case 4 -> {
                return new ContainerSoulforge(player, (TileSoulforge) world.getTileEntity(x, y, z));
            }
            case 5 -> {
                return new ContainerBloodInfuser(player, (TileBloodInfuser) world.getTileEntity(x, y, z));
            }
            case 6 -> {
                return new ContainerInjector(player);
            }
            case 7 -> {
                return new ContainerVat(player, (TileVat) world.getTileEntity(x, y, z));
            }
            case 8 -> {
                return new ContainerCase(player.inventory, world, x, y, z);
            }
            case 9 -> {
                return new ContainerFingers(player.inventory);
            }
            default -> {
                return null;
            }
        }
    }

    public void registerDisplayInformation() {}

    public void soulParticles(final int blockX, final int blockY, final int blockZ, final World world) {}

    public void disintegrateFX(final double blockX, final double blockY, final double blockZ, final EntityPlayer p,
            final int howMany, final boolean enlarged) {}

    public void smeltFX(final double blockX, final double blockY, final double blockZ, final World w, final int howMany,
            final boolean enlarged) {}

    public void containmentFX(final double blockX, final double blockY, final double blockZ, final EntityPlayer p,
            final Entity ent, final int times) {}

    public void registerHandlers() {}

    public void disintegrateExplodeFX(final World worldObj, final double posX, final double posY, final double posZ) {}

    public void illuminationFX(final World world, final int xCoord, final int yCoord, final int zCoord, final int md,
            final Color col) {}

    public void blockSplosionFX(final int x, final int y, final int z, final Block block, final int md) {}

    public void alchemiteFX(final World worldObj, final double x, final double y, final double z) {}

    public boolean readyToRender() {
        return false;
    }

    public void addEffect(final Entity entity) {}

    public void lightningBolt(final World worldObj, final double x, final double y, final double z,
            final int boltLength) {}

    public void playVortexSound(TileVortex tile) {}

    public void stopVortexSound(TileVortex tile) {}
}
