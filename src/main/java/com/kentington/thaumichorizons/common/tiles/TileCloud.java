//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityItemInvulnerable;
import com.kentington.thaumichorizons.common.entities.EntityLightningBoltFinite;
import com.kentington.thaumichorizons.common.items.ItemFocusLiquefaction;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketRainState;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.EntityAspectOrb;
import thaumcraft.common.entities.monster.EntityFireBat;

public class TileCloud extends TileThaumcraft {

    public int md;
    int dropTimer;
    public static boolean raining;
    public int howManyDown;
    public Block cachedBlock;
    int cachedMD;
    static int[] dropTimers;
    boolean rainstate;
    int timer = 0;

    public TileCloud() {
        this.md = -1;
        this.dropTimer = -1;
        raining = false;
    }

    public boolean isRaining() {
        if (!worldObj.isRemote) PacketHandler.INSTANCE.sendToDimension(
                new PacketRainState(DimensionManager.getWorld(0) != null && DimensionManager.getWorld(0).isRaining()),
                this.worldObj.provider.dimensionId);
        return TileCloud.raining;
    }

    public void updateEntity() {
        super.updateEntity();
        timer++;
        if (this.md == -1) {
            this.md = this.worldObj.getBlockMetadata(this.xCoord, this.yCoord, this.zCoord);
            this.dropTimer = TileCloud.dropTimers[this.md];
            this.findBlockBelow();
        }
        if (timer % 100 == 0) {
            if (rainstate != isRaining()) {
                rainstate = raining;
                this.markDirty();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
        if (timer > 200) timer = 0;
        Label_4978: {
            if (this.rainstate) {
                if (this.worldObj.getTotalWorldTime() % 10L == 0L) {
                    this.findBlockBelow();
                    switch (this.md) {
                        case 1, 4 -> {
                            final int meltable = ((ItemFocusLiquefaction) ThaumicHorizons.itemFocusLiquefaction)
                                    .isMeltableBlock(this.cachedBlock, this.cachedMD);
                            if (meltable == 1) {
                                this.worldObj.setBlock(
                                        this.xCoord,
                                        this.yCoord - this.howManyDown,
                                        this.zCoord,
                                        Blocks.fire);
                                ThaumicHorizons.proxy.smeltFX(
                                        this.xCoord,
                                        this.yCoord - this.howManyDown,
                                        this.zCoord,
                                        this.worldObj,
                                        25,
                                        false);
                                break;
                            }
                            if (meltable == 2) {
                                if (this.worldObj.provider.dimensionId != -1) {
                                    this.worldObj.setBlock(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            Blocks.water,
                                            0,
                                            3);
                                    break;
                                }
                                this.worldObj.setBlockToAir(this.xCoord, this.yCoord - this.howManyDown, this.zCoord);
                            } else {
                                if (meltable == 3) {
                                    this.worldObj.setBlock(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            Blocks.dirt,
                                            0,
                                            3);
                                    break;
                                }
                                if (meltable == 4) {
                                    Blocks.tnt.onBlockDestroyedByPlayer(
                                            this.worldObj,
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            1);
                                    this.worldObj
                                            .setBlockToAir(this.xCoord, this.yCoord - this.howManyDown, this.zCoord);
                                    break;
                                }
                                if (this.cachedBlock.isFlammable(
                                        this.worldObj,
                                        this.xCoord,
                                        this.yCoord - this.howManyDown,
                                        this.zCoord,
                                        ForgeDirection.UNKNOWN)) {
                                    this.worldObj.setBlock(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            Blocks.fire);
                                    ThaumicHorizons.proxy.smeltFX(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            this.worldObj,
                                            25,
                                            false);
                                    break;
                                }
                            }
                        }
                        case 3 -> {
                            final int meltable = ((ItemFocusLiquefaction) ThaumicHorizons.itemFocusLiquefaction)
                                    .isMeltableBlock(this.cachedBlock, this.cachedMD);
                            if (meltable == 1) {
                                this.worldObj
                                        .setBlock(this.xCoord, this.yCoord - this.howManyDown, this.zCoord, Blocks.air);
                                ThaumicHorizons.proxy.disintegrateExplodeFX(
                                        this.worldObj,
                                        this.xCoord + 0.5,
                                        this.yCoord - this.howManyDown + 0.5,
                                        this.zCoord + 0.5);
                                break;
                            }
                            if (meltable == 3) {
                                this.worldObj.setBlock(
                                        this.xCoord,
                                        this.yCoord - this.howManyDown,
                                        this.zCoord,
                                        Blocks.dirt,
                                        0,
                                        3);
                                break;
                            }
                            if (this.cachedBlock.isFlammable(
                                    this.worldObj,
                                    this.xCoord,
                                    this.yCoord - this.howManyDown,
                                    this.zCoord,
                                    ForgeDirection.UNKNOWN)) {
                                this.worldObj
                                        .setBlock(this.xCoord, this.yCoord - this.howManyDown, this.zCoord, Blocks.air);
                                ThaumicHorizons.proxy.disintegrateExplodeFX(
                                        this.worldObj,
                                        this.xCoord + 0.5,
                                        this.yCoord - this.howManyDown + 0.5,
                                        this.zCoord + 0.5);
                                break;
                            }
                        }
                        default -> {
                            if (this.worldObj.getBiomeGenForCoords(this.xCoord, this.zCoord)
                                    .getFloatTemperature(this.xCoord, this.yCoord, this.zCoord) >= 0.15) {
                                if (this.cachedBlock == Blocks.farmland && this.worldObj
                                        .getBlockMetadata(this.xCoord, this.yCoord - this.howManyDown, this.zCoord)
                                        != 7) {
                                    this.worldObj.setBlockMetadataWithNotify(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            7,
                                            3);
                                    break;
                                }
                                if (this.worldObj.getBlock(this.xCoord, this.yCoord - this.howManyDown + 1, this.zCoord)
                                        == Blocks.fire) {
                                    this.worldObj.setBlockToAir(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown + 1,
                                            this.zCoord);
                                    break;
                                }
                                if (this.cachedBlock == Blocks.cauldron) {
                                    this.worldObj.setBlockMetadataWithNotify(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            3,
                                            3);
                                    break;
                                }
                            } else {
                                if (this.cachedBlock.isOpaqueCube() && this.worldObj
                                        .isAirBlock(this.xCoord, this.yCoord - this.howManyDown + 1, this.zCoord)) {
                                    this.worldObj.setBlock(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown + 1,
                                            this.zCoord,
                                            Blocks.snow_layer);
                                    break;
                                }
                            }
                            // break;
                        }
                    }
                }
                switch (this.md) {
                    case 1, 4 -> {
                        final List<Entity> critters = this.getCrittersBelow();
                        for (final Entity ent : critters) {
                            ent.setFire(6);
                        }
                    }
                    case 3 -> {
                        final List<Entity> critters = this.getCrittersBelow();
                        for (final Entity ent : critters) {
                            ent.attackEntityFrom(DamageSourceThaumcraft.dissolve, 1.0f);
                        }
                    }
                    default -> {
                        if (this.worldObj.getBiomeGenForCoords(this.xCoord, this.zCoord)
                                .getFloatTemperature(this.xCoord, this.yCoord, this.zCoord) >= 0.15) {
                            final List<Entity> critters = this.getCrittersBelow();
                            for (final Entity ent : critters) {
                                if (ent instanceof EntityEnderman || ent instanceof EntityBlaze
                                        || ent instanceof EntityFireBat) {
                                    ent.attackEntityFrom(DamageSource.drown, 1.0f);
                                }
                                if (ent.isBurning()) {
                                    ent.extinguish();
                                }
                                if (!this.worldObj.isRemote && this.md == 8
                                        && ent instanceof EntityCow
                                        && !(ent instanceof EntityMooshroom)) {
                                    ent.setDead();
                                    final EntityMooshroom entitycow = new EntityMooshroom(this.worldObj);
                                    entitycow.setLocationAndAngles(
                                            ent.posX,
                                            ent.posY,
                                            ent.posZ,
                                            ent.rotationYaw,
                                            ent.rotationPitch);
                                    entitycow.setHealth(((EntityCow) ent).getHealth());
                                    entitycow.renderYawOffset = ((EntityCow) ent).renderYawOffset;
                                    this.worldObj.spawnEntityInWorld(entitycow);
                                    this.worldObj.spawnParticle(
                                            "largeexplode",
                                            ent.posX,
                                            ent.posY + ent.height / 2.0f,
                                            ent.posZ,
                                            0.0,
                                            0.0,
                                            0.0);
                                }
                            }
                            break;
                        }
                    }
                }
                if (this.dropTimer != -1) {
                    --this.dropTimer;
                    if (this.dropTimer == 0 && !this.worldObj.isRemote) {
                        this.dropTimer = TileCloud.dropTimers[this.md] / 2
                                + this.worldObj.rand.nextInt(TileCloud.dropTimers[this.md] / 2);
                        switch (this.md) {
                            case 2 -> {
                                this.worldObj.spawnEntityInWorld(
                                        new EntityLightningBoltFinite(
                                                this.worldObj,
                                                this.xCoord + 0.5,
                                                this.yCoord - this.howManyDown,
                                                this.zCoord + 0.5,
                                                this.howManyDown,
                                                false));
                            }
                            case 4 -> {
                                final int type = this.worldObj.rand.nextInt(75);
                                if (type < 6) {
                                    this.entityDropItem(new ItemStack(Items.gold_nugget), 0.3f);
                                    break;
                                }
                                if (type < 12) {
                                    if (Config.foundSilverIngot) {
                                        this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 3), 0.3f);
                                        break;
                                    }
                                    this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 0), 0.3f);
                                } else if (type < 20) {
                                    if (Config.foundCopperIngot) {
                                        this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 1), 0.3f);
                                        break;
                                    }
                                    this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 0), 0.3f);
                                } else if (type < 30) {
                                    if (Config.foundTinIngot) {
                                        this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 2), 0.3f);
                                        break;
                                    }
                                    this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 0), 0.3f);
                                } else if (type < 40) {
                                    if (Config.foundLeadIngot) {
                                        this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 4), 0.3f);
                                        break;
                                    }
                                    this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 0), 0.3f);
                                } else {
                                    if (type < 50) {
                                        this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 5), 0.3f);
                                        break;
                                    }
                                    this.entityDropItem(new ItemStack(ConfigItems.itemNugget, 1, 0), 0.3f);
                                }
                                // break;
                            }
                            case 5 -> {
                                switch (this.worldObj.rand.nextInt(10)) {
                                    case 0 -> {
                                        this.entityDropItem(new ItemStack(Items.beef), 0.3f);
                                        break Label_4978;
                                    }
                                    case 1 -> {
                                        this.entityDropItem(new ItemStack(Items.porkchop), 0.3f);
                                        break Label_4978;
                                    }
                                    case 2 -> {
                                        this.entityDropItem(new ItemStack(Items.chicken), 0.3f);
                                        break Label_4978;
                                    }
                                    case 3 -> {
                                        switch (this.worldObj.rand.nextInt(3)) {
                                            case 0 -> {
                                                this.entityDropItem(new ItemStack(Items.fish), 0.3f);
                                            }
                                            case 1 -> {
                                                this.entityDropItem(new ItemStack(Items.fish, 1, 1), 0.3f);
                                            }
                                            case 2 -> {
                                                this.entityDropItem(new ItemStack(Items.fish, 1, 2), 0.3f);
                                            }
                                        }
                                        break Label_4978;
                                    }
                                    default -> {
                                        this.entityDropItem(new ItemStack(ThaumicHorizons.itemMeat), 0.3f);
                                        break Label_4978;
                                    }
                                }
                                // break;
                            }
                            case 6 -> {
                                Aspect asp = null;
                                switch (this.worldObj.rand.nextInt(6)) {
                                    case 1 -> {
                                        asp = Aspect.FIRE;
                                    }
                                    case 2 -> {
                                        asp = Aspect.ORDER;
                                    }
                                    case 3 -> {
                                        asp = Aspect.ENTROPY;
                                    }
                                    case 4 -> {
                                        asp = Aspect.AIR;
                                    }
                                    case 5 -> {
                                        asp = Aspect.EARTH;
                                    }
                                    default -> {
                                        asp = Aspect.WATER;
                                    }
                                }
                                final EntityAspectOrb orb = new EntityAspectOrb(
                                        this.worldObj,
                                        this.xCoord + this.worldObj.rand.nextDouble(),
                                        this.yCoord + 0.5,
                                        this.zCoord + this.worldObj.rand.nextDouble(),
                                        asp,
                                        1);
                                this.worldObj.spawnEntityInWorld(orb);
                            }
                            case 7 -> {
                                final EntityXPOrb xporb = new EntityXPOrb(
                                        this.worldObj,
                                        this.xCoord + this.worldObj.rand.nextDouble(),
                                        this.yCoord + 0.5,
                                        this.zCoord + this.worldObj.rand.nextDouble(),
                                        this.worldObj.rand.nextInt(4));
                                this.worldObj.spawnEntityInWorld(xporb);
                            }
                            case 8 -> {
                                this.findBlockBelow();
                                if (!this.worldObj
                                        .isAirBlock(this.xCoord, this.yCoord - this.howManyDown + 1, this.zCoord)) {
                                    break;
                                }
                                if (this.cachedBlock == Blocks.farmland) {
                                    switch (this.worldObj.rand.nextInt(8)) {
                                        case 1: {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    Blocks.melon_stem);
                                            break Label_4978;
                                        }
                                        case 2: {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    Blocks.pumpkin_stem);
                                            break Label_4978;
                                        }
                                        case 3: {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    Blocks.carrots);
                                        }
                                        case 4: {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    Blocks.potatoes);
                                            break;
                                        }
                                    }
                                    this.worldObj.setBlock(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown + 1,
                                            this.zCoord,
                                            Blocks.wheat);
                                    break;
                                }
                                if (this.cachedBlock == Blocks.dirt) {
                                    if (this.worldObj.rand.nextInt(10) == 4) {
                                        this.worldObj.setBlock(
                                                this.xCoord,
                                                this.yCoord - this.howManyDown,
                                                this.zCoord,
                                                Blocks.mycelium);
                                        break Label_4978;
                                    }
                                    this.worldObj.setBlock(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown,
                                            this.zCoord,
                                            Blocks.grass);
                                    break Label_4978;
                                } else if (this.cachedBlock == Blocks.stone || this.cachedBlock == Blocks.mycelium) {
                                    if (this.worldObj.rand.nextInt(3) == 1) {
                                        this.worldObj.setBlock(
                                                this.xCoord,
                                                this.yCoord - this.howManyDown + 1,
                                                this.zCoord,
                                                Blocks.brown_mushroom);
                                        break Label_4978;
                                    }
                                    this.worldObj.setBlock(
                                            this.xCoord,
                                            this.yCoord - this.howManyDown + 1,
                                            this.zCoord,
                                            Blocks.red_mushroom);
                                    break Label_4978;
                                } else {
                                    if (this.cachedBlock == Blocks.grass) {
                                        final int plant = this.worldObj.rand.nextInt(1000);
                                        if (plant == 666) {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    ConfigBlocks.blockCustomPlant);
                                            this.worldObj.setBlockMetadataWithNotify(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    1,
                                                    3);
                                        } else if (plant < 750) {
                                            switch (this.worldObj.rand.nextInt(14)) {
                                                case 0 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.tallgrass);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            1,
                                                            3);
                                                }
                                                case 1 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.tallgrass);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            2,
                                                            3);
                                                }
                                                case 2 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.yellow_flower);
                                                }
                                                case 3, 4 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            0,
                                                            3);
                                                }
                                                case 5 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            1,
                                                            3);
                                                }
                                                case 6 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            2,
                                                            3);
                                                }
                                                case 7 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            3,
                                                            3);
                                                }
                                                case 8 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            4,
                                                            3);
                                                }
                                                case 9 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            5,
                                                            3);
                                                }
                                                case 10 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            6,
                                                            3);
                                                }
                                                case 11 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            7,
                                                            3);
                                                }
                                                case 12 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.red_flower);
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            8,
                                                            3);
                                                }
                                                case 13 -> {
                                                    this.worldObj.setBlock(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            Blocks.reeds);
                                                }
                                            }
                                        } else if (plant < 950) {
                                            final int sapling = this.worldObj.rand.nextInt(100);
                                            if (sapling < 10) {
                                                this.worldObj.setBlock(
                                                        this.xCoord,
                                                        this.yCoord - this.howManyDown + 1,
                                                        this.zCoord,
                                                        ConfigBlocks.blockCustomPlant);
                                                this.worldObj.setBlockMetadataWithNotify(
                                                        this.xCoord,
                                                        this.yCoord - this.howManyDown + 1,
                                                        this.zCoord,
                                                        0,
                                                        3);
                                            } else {
                                                this.worldObj.setBlock(
                                                        this.xCoord,
                                                        this.yCoord - this.howManyDown + 1,
                                                        this.zCoord,
                                                        Blocks.sapling);
                                                if (sapling < 25) {
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            0,
                                                            3);
                                                } else if (sapling < 40) {
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            1,
                                                            3);
                                                } else if (sapling < 55) {
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            2,
                                                            3);
                                                } else if (sapling < 70) {
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            3,
                                                            3);
                                                } else if (sapling < 85) {
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            4,
                                                            3);
                                                } else {
                                                    this.worldObj.setBlockMetadataWithNotify(
                                                            this.xCoord,
                                                            this.yCoord - this.howManyDown + 1,
                                                            this.zCoord,
                                                            5,
                                                            3);
                                                }
                                            }
                                        } else if (plant < 975) {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    ConfigBlocks.blockCustomPlant);
                                            this.worldObj.setBlockMetadataWithNotify(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    2,
                                                    3);
                                        } else {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    ConfigBlocks.blockCustomPlant);
                                            this.worldObj.setBlockMetadataWithNotify(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    5,
                                                    3);
                                        }
                                        break;
                                    }
                                    if (this.cachedBlock == Blocks.sand) {
                                        if (this.worldObj.rand.nextInt(10) == 4) {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    ConfigBlocks.blockCustomPlant);
                                            this.worldObj.setBlockMetadataWithNotify(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    3,
                                                    3);
                                            break Label_4978;
                                        }
                                        this.worldObj.setBlock(
                                                this.xCoord,
                                                this.yCoord - this.howManyDown + 1,
                                                this.zCoord,
                                                Blocks.cactus);
                                        break Label_4978;
                                    } else {
                                        if (this.cachedBlock == Blocks.soul_sand) {
                                            this.worldObj.setBlock(
                                                    this.xCoord,
                                                    this.yCoord - this.howManyDown + 1,
                                                    this.zCoord,
                                                    Blocks.nether_wart);
                                            break;
                                        }
                                    }
                                }
                                // break;
                            }
                            case 9 -> {
                                this.worldObj.spawnEntityInWorld(
                                        new EntityLightningBoltFinite(
                                                this.worldObj,
                                                this.xCoord + 0.5,
                                                this.yCoord - this.howManyDown,
                                                this.zCoord + 0.5,
                                                this.howManyDown,
                                                true));
                            }
                        }
                    }
                }
            }
        }
    }

    private void entityDropItem(final ItemStack itemStack, final float f) {
        final EntityItemInvulnerable theItem = new EntityItemInvulnerable(
                this.worldObj,
                this.xCoord + this.worldObj.rand.nextDouble(),
                this.yCoord + 0.5,
                this.zCoord + this.worldObj.rand.nextDouble(),
                itemStack);
        this.worldObj.spawnEntityInWorld(theItem);
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setBoolean("raining", this.rainstate);
        nbttagcompound.setInteger("dropTimer", this.dropTimer);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.rainstate = nbttagcompound.getBoolean("raining");
        this.dropTimer = nbttagcompound.getInteger("dropTimer");
    }

    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared() {
        return 65536.0;
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return AxisAlignedBB
                .getBoundingBox(this.xCoord - 32, 0.0, this.zCoord - 32, this.xCoord + 32, 256.0, this.zCoord + 32);
    }

    public void findBlockBelow() {
        final MovingObjectPosition mop = this.worldObj.rayTraceBlocks(
                Vec3.createVectorHelper(this.xCoord + 0.5, this.yCoord - 0.5, this.zCoord + 0.5),
                Vec3.createVectorHelper(this.xCoord + 0.5, this.yCoord - 256, this.zCoord + 0.5));
        if (mop != null) {
            this.howManyDown = this.yCoord - mop.blockY;
            this.cachedBlock = this.worldObj.getBlock(this.xCoord, mop.blockY, this.zCoord);
            this.cachedMD = this.worldObj.getBlockMetadata(this.xCoord, mop.blockY, this.zCoord);
        } else {
            this.howManyDown = 256;
            this.cachedBlock = Blocks.air;
        }
    }

    public List getCrittersBelow() {
        return this.worldObj.getEntitiesWithinAABBExcludingEntity(
                null,
                AxisAlignedBB.getBoundingBox(
                        this.xCoord,
                        this.yCoord - this.howManyDown,
                        this.zCoord,
                        this.xCoord + 1,
                        this.yCoord,
                        this.zCoord + 1));
    }

    static {
        TileCloud.dropTimers = new int[] { -1, -1, 120, -1, 480, 480, 80, 360, 280, 550 };
    }
}
