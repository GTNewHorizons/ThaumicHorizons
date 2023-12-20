//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.lib.NightmareTeleporter;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketMountNightmare;

public class EntityNightmare extends EntityEndersteed {

    NightmareTeleporter nightmareTeleporterOverworld;
    NightmareTeleporter nightmareTeleporterNether;

    public EntityNightmare(final World p_i1685_1_) {
        super(p_i1685_1_);
        this.isImmuneToFire = true;
        if (!p_i1685_1_.isRemote) {
            this.nightmareTeleporterOverworld = new NightmareTeleporter(
                    MinecraftServer.getServer().worldServerForDimension(0));
            this.nightmareTeleporterNether = new NightmareTeleporter(
                    MinecraftServer.getServer().worldServerForDimension(-1));
        }
    }

    @Override
    public void setJumpPower(final int p_110206_1_) {
        final double blocks = p_110206_1_ / 7.0;
        if (p_110206_1_ < 90 || (this.worldObj.provider.dimensionId != 0 && this.worldObj.provider.dimensionId != -1)) {
            this.teleportTo(
                    this.posX - blocks * Math.sin(Math.toRadians(this.rotationYaw)),
                    this.posY,
                    this.posZ + blocks * Math.cos(Math.toRadians(this.rotationYaw)));
        } else if (this.dimension == 0 || this.dimension == -1) {
            if (this.dimension == 0) {
                this.netherport(-1);
            } else {
                this.netherport(0);
            }
        }
    }

    private void netherport(final int dim) {
        this.worldObj.newExplosion(this, this.posX, this.posY + this.height / 2.0f, this.posZ, 2.0f, true, true);
        EntityPlayerMP player = (EntityPlayerMP) this.riddenByEntity;
        player.mountEntity(null);
        Entity newNightmare;
        if (this.dimension == 0) {
            player = this.playerTravelToDimension(player, -1);
            newNightmare = this.nightmareTravelToDimension(-1);
        } else {
            player = this.playerTravelToDimension(player, 0);
            newNightmare = this.nightmareTravelToDimension(0);
        }
        player.rotationYaw = newNightmare.rotationYaw;
        player.rotationPitch = newNightmare.rotationPitch;
        player.mountEntity(null);
        player.mountEntity(newNightmare);
        PacketHandler.INSTANCE.sendTo(new PacketMountNightmare(newNightmare, player), player);
    }

    public Entity nightmareTravelToDimension(final int p_71027_1_) {
        if (!this.worldObj.isRemote && !this.isDead) {
            this.worldObj.theProfiler.startSection("changeDimension");
            final MinecraftServer minecraftserver = MinecraftServer.getServer();
            final int j = this.dimension;
            final WorldServer worldserver = minecraftserver.worldServerForDimension(j);
            final WorldServer worldserver2 = minecraftserver.worldServerForDimension(p_71027_1_);
            this.dimension = p_71027_1_;
            this.worldObj.removeEntity(this);
            this.isDead = false;
            this.worldObj.theProfiler.startSection("reposition");
            if (p_71027_1_ == -1) {
                minecraftserver.getConfigurationManager()
                        .transferEntityToWorld(this, j, worldserver, worldserver2, this.nightmareTeleporterNether);
            } else {
                minecraftserver.getConfigurationManager()
                        .transferEntityToWorld(this, j, worldserver, worldserver2, this.nightmareTeleporterOverworld);
            }
            this.worldObj.theProfiler.endStartSection("reloading");
            final Entity entity = EntityList.createEntityByName(EntityList.getEntityString(this), worldserver2);
            if (entity != null) {
                entity.copyDataFrom(this, true);
                worldserver2.spawnEntityInWorld(entity);
            }
            this.isDead = true;
            this.worldObj.theProfiler.endSection();
            worldserver.resetUpdateEntityTick();
            worldserver2.resetUpdateEntityTick();
            this.worldObj.theProfiler.endSection();
            return entity;
        }
        return null;
    }

    public EntityPlayerMP playerTravelToDimension(final EntityPlayerMP player, final int p_71027_1_) {
        if (p_71027_1_ == -1) {
            player.mcServer.getConfigurationManager()
                    .transferPlayerToDimension(player, p_71027_1_, this.nightmareTeleporterNether);
        } else {
            player.mcServer.getConfigurationManager()
                    .transferPlayerToDimension(player, p_71027_1_, this.nightmareTeleporterOverworld);
        }
        return player;
    }

    @Override
    public String getCommandSenderName() {
        if (this.hasCustomNameTag()) {
            return this.getCustomNameTag();
        }
        return StatCollector.translateToLocal("entity.ThaumicHorizons.NightmareTH.name");
    }

    public void onUpdate() {
        final AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox(
                this.boundingBox.minX,
                this.boundingBox.minY,
                this.boundingBox.minZ,
                this.boundingBox.maxX,
                this.boundingBox.maxY,
                this.boundingBox.maxZ);
        if (this.worldObj.isAABBInMaterial(axisalignedbb, Material.lava)) {
            this.motionY += 0.1;
            if (this.motionY > 0.25) {
                this.motionY = 0.25;
            }
            this.onGround = true;
            this.fallDistance = 0.0f;
        } else if (this.worldObj.getBlock((int) this.posX, (int) Math.floor(this.posY - 1.0), (int) this.posZ)
                .getMaterial() == Material.lava) {
                    this.onGround = true;
                    this.fallDistance = 0.0f;
                    if (this.motionY < 0.0) {
                        this.motionY = 0.0;
                    }
                }
        super.onUpdate();
        final Block underfoot = this.worldObj.getBlock((int) this.posX, (int) this.posY - 1, (int) this.posZ);
        final Block in = this.worldObj.getBlock((int) this.posX, (int) this.posY, (int) this.posZ);
        final Block up = this.worldObj.getBlock((int) this.posX, (int) this.posY + 1, (int) this.posZ);
        if (underfoot.getMaterial() == Material.grass) {
            this.worldObj.setBlock((int) this.posX, (int) this.posY - 1, (int) this.posZ, Blocks.dirt);
            ThaumicHorizons.proxy
                    .smeltFX((int) this.posX, (int) this.posY - 1, (int) this.posZ, this.worldObj, 10, false);
        }
        if (this.worldObj.isRemote && underfoot.isBlockNormalCube() && this.moveForward > 0.0f) {
            ThaumicHorizons.proxy
                    .smeltFX((int) this.posX, (int) this.posY - 1, (int) this.posZ, this.worldObj, 3, false);
        }
        if (in.getMaterial() == Material.leaves || in.getMaterial() == Material.web
                || in.getMaterial() == Material.vine
                || in.getMaterial() == Material.plants) {
            this.worldObj.setBlockToAir((int) this.posX, (int) this.posY, (int) this.posZ);
            ThaumicHorizons.proxy.smeltFX((int) this.posX, (int) this.posY, (int) this.posZ, this.worldObj, 15, false);
        }
        if (up.getMaterial() == Material.leaves || up.getMaterial() == Material.web
                || up.getMaterial() == Material.vine
                || up.getMaterial() == Material.plants) {
            this.worldObj.setBlockToAir((int) this.posX, (int) this.posY + 1, (int) this.posZ);
            ThaumicHorizons.proxy
                    .smeltFX((int) this.posX, (int) this.posY + 1, (int) this.posZ, this.worldObj, 15, false);
        }
    }

    public void setInWeb() {
        this.isInWeb = false;
        this.fallDistance = 0.0f;
    }
}
