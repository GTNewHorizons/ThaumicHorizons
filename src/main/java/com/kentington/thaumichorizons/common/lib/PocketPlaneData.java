//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib;

import java.awt.*;
import java.io.*;
import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.*;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorOctaves;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityMeatSlime;
import com.kentington.thaumichorizons.common.entities.EntityMercurialSlime;
import com.kentington.thaumichorizons.common.tiles.TileVortex;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintSporeSwarmer;
import thaumcraft.common.entities.monster.EntityTaintacle;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class PocketPlaneData {

    public int radius;
    public int color;
    public int[] potionEffects;
    public int[] portalA;
    public int[] portalB;
    public int[] portalC;
    public int[] portalD;
    public String name;
    private static final short MAX_CREATURES = 100;
    private static short creatures;
    public static final LinkedList<PocketPlaneData> planes = new LinkedList<>();
    public static final HashMap<Integer, Vec3> positions = new HashMap<>();
    public static int pocketPlaneMAXID;

    public PocketPlaneData() {
        this.radius = 32;
        this.color = 0;
        this.name = "Generic Pocket Plane";
    }

    private static int fastFloor(Number a) {
        return a.intValue() > a.floatValue() ? a.intValue() - 1 : a.intValue();
    }

    public static void generatePocketPlane(final AspectList aspects, final PocketPlaneData data, final World world,
            final int vortexX, final int vortexY, final int vortexZ, final int returnID) {
        if (!world.isRemote) {
            final int xCenter = 0;
            final int yCenter = 128;
            final int zCenter = 256 * PocketPlaneData.planes.size();
            if (aspects.visSize() * 0.75f < 128.0f) {
                data.radius = (int) Math.max(32.0f, aspects.visSize() * 0.75f);
            } else {
                data.radius = 127;
            }
            data.color = getColor(aspects);
            final BiomeGenBase bio = setBiome(xCenter, yCenter, zCenter, data, world, aspects);
            final int noise = fastFloor(calcNoise(aspects));
            final int life = fastFloor(calcLife(aspects));
            drawLayers(xCenter, yCenter, zCenter, data, world, aspects, noise, bio, life);
            drawCaves(xCenter, yCenter, zCenter, data, world, aspects, noise);
            drawPockets(xCenter, yCenter, zCenter, data, world, aspects, noise);
            drawRavines(xCenter, yCenter, zCenter, data, world, aspects, noise);
            drawClouds(xCenter, yCenter, zCenter, data, world, aspects, noise);
            drawSurfaceFeatures(xCenter, yCenter, zCenter, data, world, aspects, noise, life);
            drawUndergroundFeatures(xCenter, yCenter, zCenter, data, world, aspects, noise, life);
            drawLeviathanBones(xCenter, yCenter, zCenter, data, world, aspects, noise);
            addEffects(data, aspects);
            drawRings(xCenter, yCenter, zCenter, data, world, aspects);
            drawSphere(xCenter, yCenter, zCenter, data.radius, ThaumicHorizons.blockVoid, 0, world);
            for (int x = -2; x <= 2; ++x) {
                for (int z = -2; z <= 2; ++z) {
                    world.setBlock(xCenter + x, yCenter, zCenter + z, ConfigBlocks.blockCosmeticSolid, 6, 0);
                    world.setBlockToAir(xCenter + x, yCenter + 1, zCenter + z);
                    world.setBlockToAir(xCenter + x, yCenter + 2, zCenter + z);
                }
            }
            world.setBlock(xCenter, yCenter + 1, zCenter, ThaumicHorizons.blockVortex);
            final TileVortex vortex = (TileVortex) world.getTileEntity(xCenter, yCenter + 1, zCenter);
            vortex.cheat = true;
            vortex.returnID = returnID;
            vortex.dimensionID = PocketPlaneData.planes.size();
            vortex.createdDimension = true;
            world.setTileEntity(xCenter, yCenter + 1, zCenter, vortex);
            data.portalA = new int[4];
            data.portalB = new int[4];
            data.portalC = new int[4];
            data.portalD = new int[4];
            PocketPlaneData.planes.add(data);
            PocketPlaneData.positions.put(pocketPlaneMAXID, Vec3.createVectorHelper(vortexX, vortexY, vortexZ));
            world.getChunkFromBlockCoords(vortexX, vortexZ).isModified = true;
            creatures = 0;
            ++pocketPlaneMAXID;
        }
    }

    static int getColor(final AspectList aspects) {
        int r = 0;
        int g = 0;
        int b = 0;
        for (final Aspect asp : aspects.getAspects()) {
            if (asp != null) {
                final Color aspColor = new Color(asp.getColor());
                r += (int) (aspColor.getRed() * aspectFraction(asp, aspects));
                g += (int) (aspColor.getGreen() * aspectFraction(asp, aspects));
                b += (int) (aspColor.getBlue() * aspectFraction(asp, aspects));
            }
        }
        return r * 256 * 256 + g * 256 + b;
    }

    public static void addEffects(final PocketPlaneData data, final AspectList aspects) {
        int pointer = 0;
        data.potionEffects = new int[8];
        if (aspects.getAmount(Aspect.MOTION) > 0) {
            data.potionEffects[pointer] = Potion.moveSpeed.id;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.FLIGHT) > 0) {
            data.potionEffects[pointer] = Potion.jump.id;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.HEAL) > 0) {
            data.potionEffects[pointer] = Potion.regeneration.id;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.TRAVEL) > 0) {
            data.potionEffects[pointer] = Potion.moveSpeed.id;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.TOOL) > 0) {
            data.potionEffects[pointer] = Potion.digSpeed.id;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.WEAPON) > 0) {
            data.potionEffects[pointer] = Potion.damageBoost.id;
            ++pointer;
        }
        if (aspects.getAmount(Aspect.ARMOR) > 0) {
            data.potionEffects[pointer] = Potion.resistance.id;
            ++pointer;
        }
    }

    public static boolean drawRings(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects) {
        boolean drewAnything = false;
        int numRings = 0;
        if (aspects.getAmount(Aspect.ELDRITCH) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, ConfigBlocks.blockCosmeticSolid, 11, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.DARKNESS) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, Blocks.obsidian, 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.ELDRITCH) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, ConfigBlocks.blockCosmeticSolid, 11, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.CRYSTAL) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, Blocks.glass, 0, world);
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.COLD) > 0) {
            ++numRings;
            drawSphere(xCenter, yCenter, zCenter, data.radius - numRings, Blocks.packed_ice, 0, world);
            drewAnything = true;
        }
        return drewAnything;
    }

    public static double calcNoise(final AspectList aspects) {
        double noise = 50d;
        noise -= (aspectFraction(Aspect.ORDER, aspects) * 100.0d);
        noise += (aspectFraction(Aspect.ENTROPY, aspects) * 100.0d);
        noise += (aspectFraction(Aspect.MOTION, aspects) * 50.0d);
        noise += (aspectFraction(Aspect.EXCHANGE, aspects) * 100.0d);
        noise += (aspectFraction(Aspect.FLIGHT, aspects) * 50.0d);
        noise -= (aspectFraction(Aspect.TRAVEL, aspects) * 100.0d);
        if (noise > 100d) {
            return 100d;
        }
        return Math.max(noise, 0d);
    }

    public static double calcLife(final AspectList aspects) {
        double life = 0;
        life += (aspectFraction(Aspect.LIGHT, aspects) * 25.0d);
        life += (aspectFraction(Aspect.WEATHER, aspects) * 25.0d);
        life += (aspectFraction(Aspect.LIFE, aspects) * 150.0d);
        life -= (aspectFraction(Aspect.POISON, aspects) * 75.0d);
        life -= (aspectFraction(Aspect.DEATH, aspects) * 100.0d);
        life -= (aspectFraction(Aspect.DARKNESS, aspects) * 25.0d);
        life += (aspectFraction(Aspect.SOUL, aspects) * 50.0d);
        life += (aspectFraction(Aspect.HEAL, aspects) * 150.0d);
        life += (aspectFraction(Aspect.AURA, aspects) * 50.0d);
        life += (aspectFraction(Aspect.SLIME, aspects) * 50.0d);
        life += (aspectFraction(Aspect.PLANT, aspects) * 100.0d);
        life += (aspectFraction(Aspect.TREE, aspects) * 100.0d);
        life += (aspectFraction(Aspect.BEAST, aspects) * 100.0d);
        life += (aspectFraction(Aspect.FLESH, aspects) * 50.0d);
        life -= (aspectFraction(Aspect.UNDEAD, aspects) * 50.0d);
        life += (aspectFraction(Aspect.MIND, aspects) * 25.0d);
        life += (aspectFraction(Aspect.SENSES, aspects) * 25.0d);
        life += (aspectFraction(Aspect.MAN, aspects) * 25.0d);
        life += (aspectFraction(Aspect.CROP, aspects) * 50.0d);
        life += (aspectFraction(Aspect.HARVEST, aspects) * 25.0d);
        life -= (aspectFraction(Aspect.WEAPON, aspects) * 25.0d);
        life -= (aspectFraction(Aspect.HUNGER, aspects) * 25.0d);
        life += (aspectFraction(Aspect.CLOTH, aspects) * 25.0d);
        if (life < 0d) {
            life = 0d;
        } else if (life > 100d) {
            life = 100d;
        }
        return life;
    }

    public static double aspectFraction(final Aspect asp, final AspectList aspects) {
        return (double) aspects.getAmount(asp) / (double) aspects.visSize();
    }

    private static Aspect getMainAspect(final AspectList aspects) {
        int max = 0;
        Aspect ret = null;
        for (Aspect a : aspects.aspects.keySet()) {
            if (aspects.aspects.get(a) > max) {
                max = aspects.aspects.get(a);
                ret = a;
            }
        }
        return ret;
    }

    public static void drawLayers(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects, final int noise, final BiomeGenBase bio, final int life) {
        final int total = aspects.visSize();
        int level = yCenter;
        boolean drewAnything = false;

        if (aspects.getAmount(Aspect.COLD) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.packed_ice, 0, level, 0, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.COLD, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.water, 0, level, 0, bio, life, aspects);
            level -= fastFloor((aspectFraction(Aspect.WATER, aspects) * data.radius / 1.5f));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            drawLayer(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    ConfigBlocks.blockTaint,
                    1,
                    level,
                    noise,
                    bio,
                    life,
                    aspects);
            level -= fastFloor((aspectFraction(Aspect.TAINT, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.CLOTH) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.wool, 0, level, noise, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.CLOTH, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.FLESH) > 0) {
            drawLayer(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    ConfigBlocks.blockTaint,
                    2,
                    level,
                    noise,
                    null,
                    0,
                    aspects);
            level -= fastFloor((aspectFraction(Aspect.CLOTH, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.EARTH) > 0) {
            if (bio == BiomeGenBase.desert) {
                drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.sand, 0, level, noise, bio, life, aspects);
            } else {
                drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.dirt, 0, level, noise, bio, life, aspects);
            }
            if (aspects.getAmount(Aspect.ORDER) >= aspects.getAmount(Aspect.ENTROPY)) {
                drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.stone, 0, level - 5, noise, null, 0, aspects);
            } else {
                drawLayer(
                        xCenter,
                        yCenter,
                        zCenter,
                        data,
                        world,
                        Blocks.cobblestone,
                        0,
                        level - 5,
                        noise,
                        null,
                        0,
                        aspects);
            }
            level -= fastFloor((aspectFraction(Aspect.EARTH, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.netherrack, 0, level, noise, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.FIRE, aspects) * data.radius));
            drewAnything = true;
        }
        if (aspects.getAmount(Aspect.ARMOR) > 0) {
            drawLayer(xCenter, yCenter, zCenter, data, world, Blocks.obsidian, 0, level, noise, null, 0, aspects);
            level -= fastFloor((aspectFraction(Aspect.ARMOR, aspects) * data.radius));
            drewAnything = true;
        }
        if (!drewAnything) {
            drawLayer(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    ThaumicHorizons.blockDust,
                    0,
                    level,
                    noise,
                    bio,
                    life,
                    aspects);
            level -= fastFloor((aspectFraction(Aspect.ARMOR, aspects) * data.radius));
            drewAnything = true;
        }
    }

    public static void drawLayer(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final Block block, final int md, final int level, final int noise,
            final BiomeGenBase bio, final int life, final AspectList aspects) {
        final NoiseGeneratorOctaves noiseGen = new NoiseGeneratorOctaves(world.rand, 10);
        double[] noiseData = null;
        if (noise != 0) {
            noiseData = noiseGen.generateNoiseOctaves(
                    noiseData,
                    xCenter - data.radius,
                    yCenter,
                    zCenter - data.radius,
                    2 * data.radius,
                    1,
                    2 * data.radius,
                    noise / 25.0f,
                    noise / 50.0f,
                    noise / 25.0f);
        }
        for (int x = -data.radius; x <= data.radius; ++x) {
            for (int z = -data.radius; z <= data.radius; ++z) {
                int top = 0;
                if (noise != 0
                        && x + data.radius + (z * 2 * data.radius + data.radius * data.radius * 2) < noiseData.length) {
                    top = (int) (noiseData[x + data.radius + (z * 2 * data.radius + data.radius * data.radius * 2)]
                            / 8.0);
                }
                final int offsettop = -level + yCenter;
                final int ctop = x * x + z * z + offsettop * offsettop - data.radius * data.radius;
                final int tmax = offsettop + (int) Math.sqrt(offsettop * offsettop - ctop);
                final int offsetbottom = level - yCenter;
                final int cbottom = x * x + z * z + offsetbottom * offsetbottom - data.radius * data.radius;
                final int bmax = offsetbottom + (int) Math.sqrt(offsetbottom * offsetbottom - cbottom);
                if (top > tmax) {
                    top = tmax;
                }
                int y;
                for (int bottom = y = -bmax; y <= top; ++y) {
                    if (top != bottom && level + y > 0) {
                        if (y == top) {
                            if (block != null) {
                                world.setBlock(x + xCenter, y + level, z + zCenter, block, md, 0);
                            }
                            if (block == Blocks.water) {
                                if (bio != null && bio.getTempCategory() == BiomeGenBase.TempCategory.COLD) {
                                    world.setBlock(x + xCenter, y + level, z + zCenter, Blocks.ice, 0, 0);
                                } else {
                                    world.setBlock(x + xCenter, y + level, z + zCenter, block, 0, 0);
                                    if ((life > 40 || aspects.getAmount(Aspect.BEAST) > 0)
                                            && world.rand.nextInt(100) > 98
                                            && creatures < MAX_CREATURES) {
                                        final EntitySquid squiddie = new EntitySquid(world);
                                        squiddie.setPosition(x + xCenter, y + level, z + zCenter);
                                        squiddie.func_110163_bv();
                                        world.spawnEntityInWorld(squiddie);
                                        ++creatures;
                                    }
                                    if (life > 0 && world.rand.nextInt(100) > 98) {
                                        world.setBlock(x + xCenter, y + level + 1, z + zCenter, Blocks.waterlily, 0, 0);
                                    }
                                }
                            } else if (block == Blocks.dirt) {
                                if (life > 0 && world.isAirBlock(x + xCenter, y + level + 1, z + zCenter)) {
                                    world.setBlock(x + xCenter, y + level, z + zCenter, Blocks.grass, 0, 0);
                                    if (life >= 10) {
                                        if ((life >= 20 || aspects.getAmount(Aspect.CROP) > 0
                                                || aspects.getAmount(Aspect.HARVEST) > 0)
                                                && world.rand.nextInt(100) > 97) {
                                            switch (world.rand.nextInt(10)) {
                                                case 0 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            Blocks.farmland,
                                                            0,
                                                            0);
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.carrots,
                                                            0,
                                                            0);
                                                }
                                                case 1 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            Blocks.farmland,
                                                            0,
                                                            0);
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.potatoes,
                                                            0,
                                                            0);
                                                }
                                                case 2 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            Blocks.farmland,
                                                            0,
                                                            0);
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.wheat,
                                                            0,
                                                            0);
                                                }
                                                case 3 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.melon_block,
                                                            0,
                                                            0);
                                                }
                                                case 4 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.pumpkin,
                                                            0,
                                                            0);
                                                }
                                                case 5 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.reeds,
                                                            0,
                                                            0);
                                                }
                                                case 6 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            Blocks.mycelium,
                                                            0,
                                                            0);
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.red_mushroom,
                                                            0,
                                                            0);
                                                }
                                                case 7 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level,
                                                            z + zCenter,
                                                            Blocks.mycelium,
                                                            0,
                                                            0);
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            Blocks.brown_mushroom,
                                                            0,
                                                            0);
                                                }
                                                case 8 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            ConfigBlocks.blockCustomPlant,
                                                            2,
                                                            0);
                                                }
                                                case 9 -> {
                                                    world.setBlock(
                                                            x + xCenter,
                                                            y + level + 1,
                                                            z + zCenter,
                                                            ConfigBlocks.blockCustomPlant,
                                                            5,
                                                            0);
                                                }
                                            }
                                        } else if ((life >= 15 || aspects.getAmount(Aspect.TREE) > 0)
                                                && world.rand.nextInt(100) > 97) {
                                                    switch (world.rand.nextInt(10)) {
                                                        case 0 -> {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.sapling,
                                                                    0,
                                                                    0);
                                                        }
                                                        case 1 -> {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.sapling,
                                                                    1,
                                                                    0);
                                                        }
                                                        case 2 -> {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.sapling,
                                                                    2,
                                                                    0);
                                                        }
                                                        case 3 -> {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.sapling,
                                                                    3,
                                                                    0);
                                                        }
                                                        case 4 -> {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.sapling,
                                                                    4,
                                                                    0);
                                                        }
                                                        case 5 -> {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.sapling,
                                                                    5,
                                                                    0);
                                                        }
                                                        case 6 -> {
                                                            if (((bio != null
                                                                    && bio.biomeID == Config.biomeMagicalForestID)
                                                                    || aspects.getAmount(Aspect.MAGIC) > 10
                                                                    || aspects.getAmount(Aspect.AURA) > 5)
                                                                    && world.rand.nextInt(4) == 0) {
                                                                world.setBlock(
                                                                        x + xCenter,
                                                                        y + level + 1,
                                                                        z + zCenter,
                                                                        ConfigBlocks.blockCustomPlant,
                                                                        1,
                                                                        0);
                                                                break;
                                                            }
                                                        }
                                                        case 7 -> {
                                                            if (world.rand.nextInt(3) == 0) {
                                                                world.setBlock(
                                                                        x + xCenter,
                                                                        y + level + 1,
                                                                        z + zCenter,
                                                                        ConfigBlocks.blockCustomPlant,
                                                                        0,
                                                                        0);
                                                                break;
                                                            }
                                                        }
                                                    }
                                                } else
                                            if ((life >= 10 || aspects.getAmount(Aspect.PLANT) > 0)
                                                    && world.rand.nextInt(100) > 94) {
                                                        final int random = world.rand.nextInt(18);
                                                        if (random <= 15) {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.red_flower,
                                                                    random,
                                                                    0);
                                                        } else {
                                                            world.setBlock(
                                                                    x + xCenter,
                                                                    y + level + 1,
                                                                    z + zCenter,
                                                                    Blocks.tallgrass,
                                                                    random - 16,
                                                                    0);
                                                        }
                                                    }
                                    }
                                } else {
                                    world.setBlock(x + xCenter, y + level, z + zCenter, block, 0, 0);
                                }
                            } else if (block == Blocks.sand) {
                                world.setBlock(x + xCenter, y + level, z + zCenter, block, 0, 0);
                                if (life > 10) {
                                    if (life >= 20 && world.rand.nextInt(100) > 98) {
                                        world.setBlock(
                                                x + xCenter,
                                                y + level + 1,
                                                z + zCenter,
                                                ConfigBlocks.blockCustomPlant,
                                                3,
                                                0);
                                    } else if (world.rand.nextInt(100) > 97) {
                                        world.setBlock(x + xCenter, y + level + 1, z + zCenter, Blocks.cactus, 0, 0);
                                    }
                                }
                            } else if (block == ConfigBlocks.blockTaint && md == 2 && world.rand.nextInt(300) > 298) {
                                if (world.rand.nextBoolean()) {
                                    for (int ecks = 0; ecks < 3; ++ecks) {
                                        for (int zee = 0; zee < 2; ++zee) {
                                            for (int why = 0; why < 2; ++why) {
                                                world.setBlock(
                                                        x + xCenter + ecks,
                                                        y + level + why,
                                                        z + zCenter + zee,
                                                        ThaumicHorizons.blockBrain,
                                                        0,
                                                        0);
                                            }
                                        }
                                    }
                                } else {
                                    for (int ecks = 0; ecks < 2; ++ecks) {
                                        for (int zee = 0; zee < 3; ++zee) {
                                            for (int why = 0; why < 2; ++why) {
                                                world.setBlock(
                                                        x + xCenter + ecks,
                                                        y + level + why,
                                                        z + zCenter + zee,
                                                        ThaumicHorizons.blockBrain,
                                                        0,
                                                        0);
                                            }
                                        }
                                    }
                                }
                            }
                            if (bio != null && block.isNormalCube()
                                    && bio.getTempCategory() == BiomeGenBase.TempCategory.COLD) {
                                world.setBlock(x + xCenter, y + level + 1, z + zCenter, Blocks.snow_layer, 1, 0);
                            }
                            if ((life >= 50 || aspects.getAmount(Aspect.BEAST) > 0) && world.rand.nextInt(100) > 98) {
                                EntityLiving critter = null;
                                switch (world.rand.nextInt(7)) {
                                    case 0 -> {
                                        critter = new EntityCow(world);
                                    }
                                    case 1 -> {
                                        critter = new EntityPig(world);
                                    }
                                    case 2 -> {
                                        critter = new EntityChicken(world);
                                    }
                                    case 3 -> {
                                        critter = new EntitySheep(world);
                                    }
                                    case 4 -> {
                                        critter = new EntityHorse(world);
                                    }
                                    case 5 -> {
                                        critter = new EntityOcelot(world);
                                    }
                                    case 6 -> {
                                        critter = new EntityWolf(world);
                                    }
                                }
                                if (critter != null && creatures < MAX_CREATURES) {
                                    critter.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                    world.spawnEntityInWorld(critter);
                                    ++creatures;
                                }
                            }
                            if ((aspects.getAmount(Aspect.POISON) > 0 || aspects.getAmount(Aspect.EXCHANGE) > 0
                                    || aspects.getAmount(Aspect.METAL) > 0
                                    || aspects.getAmount(Aspect.MECHANISM) > 0) && world.rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                final EntityMercurialSlime slime = new EntityMercurialSlime(world);
                                slime.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime);
                                ++creatures;
                            }
                            if ((aspects.getAmount(Aspect.SLIME) > 0 || aspects.getAmount(Aspect.FLESH) > 0
                                    || aspects.getAmount(Aspect.HUNGER) > 0) && world.rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                final EntityMeatSlime slime2 = new EntityMeatSlime(world);
                                slime2.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime2);
                                ++creatures;
                            }
                            if (aspects.getAmount(Aspect.SLIME) > 0 && world.rand.nextInt(100) > 98
                                    && creatures < MAX_CREATURES) {
                                final EntitySlime slime3 = new EntitySlime(world);
                                slime3.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime3);
                                ++creatures;
                            }
                            if (aspects.getAmount(Aspect.ELDRITCH) > 0 && world.rand.nextInt(200) > 198) {
                                world.setBlock(
                                        x + xCenter,
                                        y + level,
                                        z + zCenter,
                                        ConfigBlocks.blockCosmeticSolid,
                                        1,
                                        0);
                                world.setBlock(
                                        x + xCenter,
                                        y + level + 1,
                                        z + zCenter,
                                        ConfigBlocks.blockCosmeticSolid,
                                        0,
                                        0);
                                world.setBlock(
                                        x + xCenter,
                                        y + level + 2,
                                        z + zCenter,
                                        ConfigBlocks.blockCosmeticSolid,
                                        0,
                                        0);
                                world.setBlock(
                                        x + xCenter,
                                        y + level + 3,
                                        z + zCenter,
                                        ConfigBlocks.blockCosmeticSolid,
                                        0,
                                        0);
                            }
                            if (aspects.getAmount(Aspect.CRAFT) > 0 && world.rand.nextInt(200) > 198) {
                                world.setBlock(
                                        x + xCenter,
                                        y + level + 1,
                                        z + zCenter,
                                        ConfigBlocks.blockStoneDevice,
                                        1,
                                        0);
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && world.rand.nextInt(100) > 98) {
                                world.setBlock(
                                        x + xCenter,
                                        y + level,
                                        z + zCenter,
                                        ConfigBlocks.blockTaintFibres,
                                        0,
                                        0);
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && world.rand.nextInt(200) > 198
                                    && creatures < MAX_CREATURES) {
                                final EntityTaintacle slime4 = new EntityTaintacle(world);
                                slime4.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime4);
                                ++creatures;
                            }
                            if (aspects.getAmount(Aspect.TAINT) > 0 && world.rand.nextInt(200) > 198
                                    && creatures < MAX_CREATURES) {
                                final EntityTaintSporeSwarmer slime5 = new EntityTaintSporeSwarmer(world);
                                slime5.setPosition(x + xCenter + 0.5, y + level + 1, z + zCenter + 0.5);
                                world.spawnEntityInWorld(slime5);
                                ++creatures;
                            }
                        } else if (block != null) {
                            world.setBlock(x + xCenter, y + level, z + zCenter, block, md, 0);
                        }
                    }
                }
            }
        }
    }

    public static void drawCaves(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects, final int noise) {
        float solidity = 0.5f;
        solidity -= aspectFraction(Aspect.AIR, aspects) * 0.5f;
        solidity += aspectFraction(Aspect.ORDER, aspects) * 0.25f;
        solidity -= aspectFraction(Aspect.VOID, aspects);
        solidity -= aspectFraction(Aspect.FLIGHT, aspects) * 0.5f;
        solidity += aspectFraction(Aspect.CRAFT, aspects) * 0.5f;
        solidity -= aspectFraction(Aspect.TRAP, aspects) * 0.5f;
        if (solidity <= 0.0f) {
            solidity = 0.0f;
        } else if (solidity > 1.0f) {
            solidity = 1.0f;
        }
        final int numCaves = (int) (data.radius * data.radius * data.radius / 10000 * (solidity + 0.1f));
        final HashMap<Aspect, Number> dressing = new HashMap<>();
        if (aspects.getAmount(Aspect.EARTH) > 0) {
            dressing.put(Aspect.EARTH, aspectFraction(Aspect.EARTH, aspects));
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            dressing.put(Aspect.FIRE, aspectFraction(Aspect.FIRE, aspects));
        }
        if (aspects.getAmount(Aspect.ORDER) > 0) {
            dressing.put(Aspect.ORDER, aspectFraction(Aspect.ORDER, aspects));
        }
        if (aspects.getAmount(Aspect.AIR) > 0) {
            dressing.put(Aspect.AIR, aspectFraction(Aspect.AIR, aspects));
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            dressing.put(Aspect.WATER, aspectFraction(Aspect.WATER, aspects));
        }
        if (aspects.getAmount(Aspect.ENTROPY) > 0) {
            dressing.put(Aspect.ENTROPY, aspectFraction(Aspect.ENTROPY, aspects));
        }
        if (aspects.getAmount(Aspect.METAL) > 0) {
            dressing.put(Aspect.METAL, aspectFraction(Aspect.METAL, aspects));
        }
        if (aspects.getAmount(Aspect.DEATH) > 0) {
            dressing.put(Aspect.DEATH, aspectFraction(Aspect.DEATH, aspects));
        }
        if (aspects.getAmount(Aspect.UNDEAD) > 0) {
            dressing.put(Aspect.UNDEAD, aspectFraction(Aspect.UNDEAD, aspects));
        }
        if (aspects.getAmount(Aspect.SENSES) > 0) {
            dressing.put(Aspect.SENSES, aspectFraction(Aspect.SENSES, aspects));
        }
        if (aspects.getAmount(Aspect.GREED) > 0) {
            dressing.put(Aspect.GREED, aspectFraction(Aspect.GREED, aspects));
        }
        for (int i = 0; i < numCaves; ++i) {
            drawACave(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    aspects,
                    noise,
                    Blocks.air,
                    world.rand.nextInt(10) + 10,
                    world.rand.nextInt(10) + 10,
                    world.rand.nextInt((int) (data.radius * 1.5f)) - (int) (data.radius * 0.75f),
                    -world.rand.nextInt((int) (data.radius * 0.5f)),
                    world.rand.nextInt((int) (data.radius * 1.5f)) - (int) (data.radius * 0.75f),
                    dressing);
        }
    }

    public static void drawACave(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects, final int noise, final Block block, final int xSize,
            final int zSize, final int xOffset, final int yOffset, final int zOffset,
            final HashMap<Aspect, Number> dressing) {
        final NoiseGeneratorOctaves noiseGen = new NoiseGeneratorOctaves(world.rand, 10);
        double[] noiseData = null;
        noiseData = noiseGen.generateNoiseOctaves(
                noiseData,
                xCenter - xSize + xOffset,
                yCenter + yOffset,
                zCenter - zSize + zOffset,
                xSize,
                16,
                zSize,
                noise / 10.0f,
                noise / 25.0f,
                noise / 10.0f);
        final int avg = getAvg(noiseData);
        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                for (int y = 0; y < 16; ++y) {
                    if (noiseData[x + xSize * z + y * xSize * zSize] > avg && world.getBlock(
                            xCenter - xSize + xOffset + x,
                            yCenter + yOffset + y,
                            zCenter - zSize + zOffset + z) != Blocks.water) {
                        if (!world.isAirBlock(
                                xCenter - xSize + xOffset + x,
                                yCenter + yOffset + y - 1,
                                zCenter - zSize + zOffset + z) && world.rand.nextInt(50) == 49) {
                            float which = world.rand.nextFloat();
                            Aspect whichAspect = null;
                            final Iterator<Aspect> it = dressing.keySet().iterator();
                            boolean doDressing = false;
                            while (it.hasNext()) {
                                whichAspect = it.next();
                                if (which <= dressing.get(whichAspect).floatValue()) {
                                    doDressing = true;
                                    break;
                                }
                                which -= dressing.get(whichAspect).floatValue();
                            }
                            if (doDressing && whichAspect != null
                                    && !world.isAirBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z)
                                    && world.getBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z) != Blocks.water) {
                                if (whichAspect == Aspect.EARTH) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            ConfigBlocks.blockCrystal,
                                            3,
                                            0);
                                } else if (whichAspect == Aspect.AIR) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            ConfigBlocks.blockCrystal,
                                            0,
                                            0);
                                } else if (whichAspect == Aspect.WATER) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            ConfigBlocks.blockCrystal,
                                            2,
                                            0);
                                } else if (whichAspect == Aspect.FIRE) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            ConfigBlocks.blockCrystal,
                                            1,
                                            0);
                                } else if (whichAspect == Aspect.ORDER) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            ConfigBlocks.blockCrystal,
                                            4,
                                            0);
                                } else if (whichAspect == Aspect.ENTROPY) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            ConfigBlocks.blockCrystal,
                                            5,
                                            0);
                                } else if (whichAspect == Aspect.DEATH || whichAspect == Aspect.UNDEAD) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            Blocks.skull,
                                            0,
                                            0);
                                } else if (whichAspect == Aspect.SENSES) {
                                    world.setBlock(
                                            xCenter - xSize + xOffset + x,
                                            yCenter + yOffset + y,
                                            zCenter - zSize + zOffset + z,
                                            Blocks.lapis_block,
                                            0,
                                            0);
                                } else if (whichAspect == Aspect.GREED) {
                                    if (world.rand.nextBoolean()) {
                                        world.setBlock(
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                Blocks.gold_block,
                                                0,
                                                0);
                                    } else {
                                        world.setBlock(
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                Blocks.emerald_block,
                                                0,
                                                0);
                                    }
                                } else if (whichAspect == Aspect.METAL) {
                                    if (world.rand.nextBoolean()) {
                                        world.setBlock(
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                Blocks.gold_block,
                                                0,
                                                0);
                                    } else {
                                        world.setBlock(
                                                xCenter - xSize + xOffset + x,
                                                yCenter + yOffset + y,
                                                zCenter - zSize + zOffset + z,
                                                Blocks.iron_block,
                                                0,
                                                0);
                                    }
                                }
                            }
                        } else {
                            world.setBlock(
                                    xCenter - xSize + xOffset + x,
                                    yCenter + yOffset + y,
                                    zCenter - zSize + zOffset + z,
                                    Blocks.air,
                                    0,
                                    0);
                        }
                    }
                }
            }
        }
    }

    public static void drawRavines(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects, final int noise) {}

    public static void drawClouds(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects, final int noise) {}

    public static void drawSurfaceFeatures(final int xCenter, final int yCenter, final int zCenter,
            final PocketPlaneData data, final World world, final AspectList aspects, final int noise,
            final float life) {}

    public static void drawUndergroundFeatures(final int xCenter, final int yCenter, final int zCenter,
            final PocketPlaneData data, final World world, final AspectList aspects, final int noise,
            final float life) {}

    public static void drawLeviathanBones(final int xCenter, final int yCenter, final int zCenter,
            final PocketPlaneData data, final World world, final AspectList aspects, final int noise) {}

    public static int getAvg(final double[] array) {
        long x = 0L;
        for (double v : array) {
            x += (long) v;
        }
        x /= array.length;
        return (int) x;
    }

    public static void drawPockets(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects, final int noise) {
        final int numPockets = data.radius * data.radius * data.radius / 200;
        final HashMap<Aspect, Number> probabilities = new HashMap<>();
        if (aspects.getAmount(Aspect.EARTH) > 0) {
            probabilities.put(Aspect.EARTH, aspectFraction(Aspect.EARTH, aspects));
        }
        if (aspects.getAmount(Aspect.FIRE) > 0) {
            probabilities.put(Aspect.FIRE, aspectFraction(Aspect.FIRE, aspects));
        }
        if (aspects.getAmount(Aspect.ORDER) > 0) {
            probabilities.put(Aspect.ORDER, aspectFraction(Aspect.ORDER, aspects));
        }
        if (aspects.getAmount(Aspect.AIR) > 0) {
            probabilities.put(Aspect.AIR, aspectFraction(Aspect.AIR, aspects));
        }
        if (aspects.getAmount(Aspect.WATER) > 0) {
            probabilities.put(Aspect.WATER, aspectFraction(Aspect.WATER, aspects));
        }
        if (aspects.getAmount(Aspect.ENTROPY) > 0) {
            probabilities.put(Aspect.ENTROPY, aspectFraction(Aspect.ENTROPY, aspects));
        }
        if (aspects.getAmount(Aspect.METAL) > 0) {
            probabilities.put(Aspect.METAL, aspectFraction(Aspect.METAL, aspects));
        }
        if (aspects.getAmount(Aspect.CRYSTAL) > 0) {
            probabilities.put(Aspect.CRYSTAL, aspectFraction(Aspect.CRYSTAL, aspects));
        }
        if (aspects.getAmount(Aspect.SOUL) > 0) {
            probabilities.put(Aspect.SOUL, aspectFraction(Aspect.SOUL, aspects));
        }
        if (aspects.getAmount(Aspect.MAGIC) > 0) {
            probabilities.put(Aspect.MAGIC, aspectFraction(Aspect.MAGIC, aspects));
        }
        if (aspects.getAmount(Aspect.AURA) > 0) {
            probabilities.put(Aspect.AURA, aspectFraction(Aspect.AURA, aspects));
        }
        if (aspects.getAmount(Aspect.ENERGY) > 0) {
            probabilities.put(Aspect.ENERGY, aspectFraction(Aspect.ENERGY, aspects));
        }
        if (aspects.getAmount(Aspect.TREE) > 0) {
            probabilities.put(Aspect.TREE, aspectFraction(Aspect.TREE, aspects));
        }
        if (aspects.getAmount(Aspect.FLESH) > 0) {
            probabilities.put(Aspect.FLESH, aspectFraction(Aspect.FLESH, aspects));
        }
        if (aspects.getAmount(Aspect.SENSES) > 0) {
            probabilities.put(Aspect.SENSES, aspectFraction(Aspect.SENSES, aspects));
        }
        if (aspects.getAmount(Aspect.MINE) > 0) {
            probabilities.put(Aspect.MINE, aspectFraction(Aspect.MINE, aspects));
        }
        if (aspects.getAmount(Aspect.TOOL) > 0) {
            probabilities.put(Aspect.TOOL, aspectFraction(Aspect.TOOL, aspects));
        }
        if (aspects.getAmount(Aspect.GREED) > 0) {
            probabilities.put(Aspect.GREED, aspectFraction(Aspect.GREED, aspects));
        }
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            probabilities.put(Aspect.TAINT, aspectFraction(Aspect.TAINT, aspects));
        }
        if (aspects.getAmount(Aspect.POISON) > 0) {
            probabilities.put(Aspect.POISON, aspectFraction(Aspect.POISON, aspects));
        }
        if (aspects.getAmount(Aspect.EXCHANGE) > 0) {
            probabilities.put(Aspect.EXCHANGE, aspectFraction(Aspect.EXCHANGE, aspects));
        }
        if (aspects.getAmount(Aspect.MIND) > 0) {
            probabilities.put(Aspect.MIND, aspectFraction(Aspect.MIND, aspects));
        }
        if (aspects.getAmount(Aspect.MAN) > 0) {
            probabilities.put(Aspect.MAN, aspectFraction(Aspect.MAN, aspects));
        }
        if (aspects.getAmount(Aspect.HUNGER) > 0) {
            probabilities.put(Aspect.HUNGER, aspectFraction(Aspect.HUNGER, aspects));
        }
        if (aspects.getAmount(Aspect.CRAFT) > 0) {
            probabilities.put(Aspect.CRAFT, aspectFraction(Aspect.CRAFT, aspects));
        }
        Block block = Blocks.air;
        int md = 0;
        for (int i = 0; i < numPockets; ++i) {
            float which = world.rand.nextFloat();
            Aspect whichAspect = null;
            final Iterator<Aspect> it = probabilities.keySet().iterator();
            boolean doDressing = false;
            while (it.hasNext()) {
                whichAspect = it.next();
                if (which <= probabilities.get(whichAspect).floatValue()) {
                    doDressing = true;
                    break;
                }
                which -= probabilities.get(whichAspect).floatValue();
            }
            if (doDressing && whichAspect != null) {
                if (whichAspect == Aspect.EARTH) {
                    block = ConfigBlocks.blockCustomOre;
                    md = 4;
                } else if (whichAspect == Aspect.AIR) {
                    block = ConfigBlocks.blockCustomOre;
                    md = 1;
                } else if (whichAspect == Aspect.WATER) {
                    block = ConfigBlocks.blockCustomOre;
                    md = 3;
                } else if (whichAspect == Aspect.FIRE) {
                    if (world.rand.nextBoolean()) {
                        block = ConfigBlocks.blockCustomOre;
                        md = 2;
                    } else {
                        block = Blocks.lava;
                    }
                } else if (whichAspect == Aspect.ORDER) {
                    block = ConfigBlocks.blockCustomOre;
                    md = 5;
                } else if (whichAspect == Aspect.ENTROPY) {
                    block = ConfigBlocks.blockCustomOre;
                    md = 6;
                } else if (whichAspect == Aspect.METAL) {
                    if (world.rand.nextFloat() <= 0.5) {
                        block = Blocks.iron_ore;
                    } else if (world.rand.nextFloat() <= 0.8) {
                        block = Blocks.gold_ore;
                    } else {
                        block = ConfigBlocks.blockCustomOre;
                        md = 0;
                    }
                } else if (whichAspect == Aspect.MAGIC || whichAspect == Aspect.AURA) {
                    block = ConfigBlocks.blockCustomOre;
                    md = world.rand.nextInt(6) + 1;
                } else if (whichAspect == Aspect.CRYSTAL) {
                    block = Blocks.quartz_block;
                } else if (whichAspect == Aspect.SOUL) {
                    block = Blocks.soul_sand;
                } else if (whichAspect == Aspect.ENERGY) {
                    block = Blocks.redstone_block;
                } else if (whichAspect == Aspect.TREE) {
                    if (world.rand.nextBoolean()) {
                        block = Blocks.log;
                    } else {
                        block = ConfigBlocks.blockCustomOre;
                        md = 7;
                    }
                } else if (whichAspect == Aspect.FLESH) {
                    block = ConfigBlocks.blockTaint;
                    md = 2;
                } else if (whichAspect == Aspect.SENSES) {
                    block = Blocks.lapis_ore;
                } else if (whichAspect == Aspect.MINE || whichAspect == Aspect.TOOL) {
                    final float f = world.rand.nextFloat();
                    if (f < 0.4f) {
                        block = Blocks.iron_ore;
                    } else if (f < 0.5f) {
                        block = Blocks.gold_ore;
                    } else if (f < 0.6f) {
                        block = Blocks.diamond_ore;
                    } else if (f < 0.7f) {
                        block = Blocks.emerald_ore;
                    } else if (f < 0.8f) {
                        block = Blocks.lapis_ore;
                    } else {
                        block = ConfigBlocks.blockCustomOre;
                        md = world.rand.nextInt(8);
                    }
                } else if (whichAspect == Aspect.GREED) {
                    final float f = world.rand.nextFloat();
                    if (f < 0.4f) {
                        block = Blocks.gold_ore;
                    } else if (f < 0.6f) {
                        block = Blocks.diamond_ore;
                    } else if (f < 0.8f) {
                        block = Blocks.emerald_ore;
                    } else {
                        block = Blocks.lapis_ore;
                    }
                } else if (whichAspect == Aspect.TAINT) {
                    block = ConfigBlocks.blockTaint;
                    md = 0;
                } else if (whichAspect == Aspect.POISON) {
                    block = ConfigBlocks.blockFluidDeath;
                    md = 16;
                } else if (whichAspect == Aspect.EXCHANGE) {
                    block = ConfigBlocks.blockCustomOre;
                    md = 0;
                } else if (whichAspect == Aspect.MIND) {
                    if (world.rand.nextBoolean()) {
                        block = Blocks.bookshelf;
                    } else {
                        block = ThaumicHorizons.blockBrain;
                    }
                } else if (whichAspect == Aspect.MAN) {
                    final int man = world.rand.nextInt(3);
                    if (man == 0) {
                        block = Blocks.bookshelf;
                    } else if (man == 1) {
                        block = ThaumicHorizons.blockBrain;
                    } else {
                        block = ConfigBlocks.blockTaint;
                        md = 2;
                    }
                } else if (whichAspect == Aspect.HUNGER) {
                    block = ConfigBlocks.blockTaint;
                    md = 2;
                } else if (whichAspect == Aspect.CRAFT) {
                    block = Blocks.crafting_table;
                }
            }
            drawAPocket(
                    xCenter,
                    yCenter,
                    zCenter,
                    data,
                    world,
                    aspects,
                    noise,
                    block,
                    md,
                    world.rand.nextInt(3) + 1,
                    world.rand.nextInt(3) + 1,
                    world.rand.nextInt((int) (data.radius * 2.0f)) - (int) (data.radius * 1.0f),
                    -world.rand.nextInt((int) (data.radius * 1.0f)),
                    world.rand.nextInt((int) (data.radius * 2.0f)) - (int) (data.radius * 1.0f));
        }
    }

    public static void drawAPocket(final int xCenter, final int yCenter, final int zCenter, final PocketPlaneData data,
            final World world, final AspectList aspects, final int noise, final Block block, final int md,
            final int xSize, final int zSize, final int xOffset, final int yOffset, final int zOffset) {
        final NoiseGeneratorOctaves noiseGen = new NoiseGeneratorOctaves(world.rand, 10);
        double[] noiseDataTop = null;
        noiseDataTop = noiseGen.generateNoiseOctaves(
                noiseDataTop,
                xCenter - xSize + xOffset,
                yCenter + yOffset,
                zCenter - zSize + zOffset,
                xSize,
                1,
                zSize,
                noise / 50.0d,
                noise / 25.0d,
                noise / 50.0d);
        double[] noiseDataBottom = null;
        noiseDataBottom = noiseGen.generateNoiseOctaves(
                noiseDataBottom,
                xCenter - xSize + xOffset,
                yCenter + yOffset,
                zCenter - zSize + zOffset,
                xSize,
                1,
                zSize,
                noise / 50.0d,
                noise / 25.0d,
                noise / 50.0d);
        for (int x = 0; x < xSize; ++x) {
            for (int z = 0; z < zSize; ++z) {
                int y;
                for (int top = (int) (noiseDataTop[x + z * xSize] / 16.0) + 1,
                        bottom = y = (int) (noiseDataBottom[x + z * xSize] / 16.0) - 1; y < top; ++y) {
                    if (!world.isAirBlock(
                            xCenter - xSize + xOffset + x,
                            yCenter + yOffset + y,
                            zCenter - zSize + zOffset + z)
                            && world.getBlock(
                                    xCenter - xSize + xOffset + x,
                                    yCenter + yOffset + y,
                                    zCenter - zSize + zOffset + z) != Blocks.water) {
                        world.setBlock(
                                xCenter - xSize + xOffset + x,
                                yCenter + yOffset + y,
                                zCenter - zSize + zOffset + z,
                                block,
                                md,
                                0);
                    }
                }
            }
        }
    }

    public static BiomeGenBase setBiome(final int xCenter, final int yCenter, final int zCenter,
            final PocketPlaneData data, final World world, final AspectList aspects) {
        BiomeGenBase bio = null;
        if (aspects.getAmount(Aspect.TAINT) > 0) {
            bio = ThaumcraftWorldGenerator.biomeTaint;
        } else if (aspects.getAmount(Aspect.ELDRITCH) > 0 || aspects.getAmount(Aspect.UNDEAD) > 0) {
            bio = ThaumcraftWorldGenerator.biomeEerie;
        } else if (aspects.getAmount(Aspect.MAGIC) > 0 || aspects.getAmount(Aspect.AURA) > 0) {
            bio = ThaumcraftWorldGenerator.biomeMagicalForest;
        } else {
            float temp = 0.5f;
            float moist = 0.5f;
            temp += aspectFraction(Aspect.FIRE, aspects);
            temp -= aspectFraction(Aspect.WATER, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.ORDER, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.VOID, aspects) * 0.5f;
            temp += aspectFraction(Aspect.LIGHT, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.WEATHER, aspects) * 0.25f;
            temp += aspectFraction(Aspect.MOTION, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.COLD, aspects);
            temp += aspectFraction(Aspect.ENERGY, aspects) * 0.5f;
            temp -= aspectFraction(Aspect.DARKNESS, aspects) * 0.25f;
            if (temp < 0.0f) {
                temp = 0.0f;
            } else if (temp > 1.0f) {
                temp = 1.0f;
            }
            moist += aspectFraction(Aspect.WATER, aspects);
            moist -= aspectFraction(Aspect.FIRE, aspects) * 0.5f;
            moist -= aspectFraction(Aspect.ENTROPY, aspects) * 0.25f;
            moist -= aspectFraction(Aspect.VOID, aspects) * 0.25f;
            moist += aspectFraction(Aspect.WEATHER, aspects) * 0.5f;
            moist += aspectFraction(Aspect.SLIME, aspects) * 0.5f;
            if (moist < 0.0f) {
                moist = 0.0f;
            } else if (moist > 1.0f) {
                moist = 1.0f;
            }
            if (temp > 0.8) {
                bio = BiomeGenBase.desert;
            } else if (temp > 0.5) {
                if (moist < 0.4) {
                    bio = BiomeGenBase.savanna;
                } else {
                    bio = BiomeGenBase.jungle;
                }
            } else if (temp > 0.2) {
                if (moist < 0.4) {
                    bio = BiomeGenBase.plains;
                } else {
                    bio = BiomeGenBase.forest;
                }
            } else {
                bio = BiomeGenBase.icePlains;
            }
        }
        for (int x = -data.radius; x <= data.radius; ++x) {
            for (int z = -data.radius; z <= data.radius; ++z) {
                Utils.setBiomeAt(world, x + xCenter, z + zCenter, bio);
            }
        }
        return bio;
    }

    public static void drawCircle(final int x0, final int y0, final int z0, final int y1, final int radius,
            final int error0, final Block block, final int md, final World world) {
        int x = radius;
        int z = 0;
        int radiusError = error0;
        while (x >= z) {
            world.setBlock(x0 + x, y0 + y1, z0 + z, block, md, 0);
            world.setBlock(x0 + z, y0 + y1, z0 + x, block, md, 0);
            world.setBlock(x0 - x, y0 + y1, z0 + z, block, md, 0);
            world.setBlock(x0 - z, y0 + y1, z0 + x, block, md, 0);
            world.setBlock(x0 - x, y0 + y1, z0 - z, block, md, 0);
            world.setBlock(x0 - z, y0 + y1, z0 - x, block, md, 0);
            world.setBlock(x0 + x, y0 + y1, z0 - z, block, md, 0);
            world.setBlock(x0 + z, y0 + y1, z0 - x, block, md, 0);
            world.setBlock(x0 + x, y0 - y1, z0 + z, block, md, 0);
            world.setBlock(x0 + z, y0 - y1, z0 + x, block, md, 0);
            world.setBlock(x0 - x, y0 - y1, z0 + z, block, md, 0);
            world.setBlock(x0 - z, y0 - y1, z0 + x, block, md, 0);
            world.setBlock(x0 - x, y0 - y1, z0 - z, block, md, 0);
            world.setBlock(x0 - z, y0 - y1, z0 - x, block, md, 0);
            world.setBlock(x0 + x, y0 - y1, z0 - z, block, md, 0);
            world.setBlock(x0 + z, y0 - y1, z0 - x, block, md, 0);
            world.setBlock(x0 + y1, y0 + x, z0 + z, block, md, 0);
            world.setBlock(x0 + z, y0 + x, z0 + y1, block, md, 0);
            world.setBlock(x0 - y1, y0 + x, z0 + z, block, md, 0);
            world.setBlock(x0 - z, y0 + x, z0 + y1, block, md, 0);
            world.setBlock(x0 + y1, y0 + x, z0 - z, block, md, 0);
            world.setBlock(x0 + z, y0 + x, z0 - y1, block, md, 0);
            world.setBlock(x0 - y1, y0 + x, z0 - z, block, md, 0);
            world.setBlock(x0 - z, y0 + x, z0 - y1, block, md, 0);
            world.setBlock(x0 + y1, y0 - x, z0 + z, block, md, 0);
            world.setBlock(x0 + z, y0 - x, z0 + y1, block, md, 0);
            world.setBlock(x0 - y1, y0 - x, z0 + z, block, md, 0);
            world.setBlock(x0 - z, y0 - x, z0 + y1, block, md, 0);
            world.setBlock(x0 + y1, y0 - x, z0 - z, block, md, 0);
            world.setBlock(x0 + z, y0 - x, z0 - y1, block, md, 0);
            world.setBlock(x0 - y1, y0 - x, z0 - z, block, md, 0);
            world.setBlock(x0 - z, y0 - x, z0 - y1, block, md, 0);
            ++z;
            if (radiusError < 0) {
                radiusError += 2 * z + 1;
            } else {
                --x;
                radiusError += 2 * (z - x + 1);
            }
        }
    }

    public static void drawSphere(final int x0, final int y0, final int z0, final int radius, final Block block,
            final int md, final World world) {
        int x = radius;
        int y = 0;
        int radiusError = 1 - x;
        while (x >= y) {
            drawCircle(x0, y0, z0, y, x, radiusError, block, md, world);
            ++y;
            if (radiusError < 0) {
                radiusError += 2 * y + 1;
            } else {
                --x;
                radiusError += 2 * (y - x + 1);
            }
        }
    }

    public static void loadPocketPlanes(final World world) {
        final File planeFile = new File(world.getSaveHandler().getWorldDirectory(), "pocketplane.dat");
        NBTTagCompound root = null;
        if (planeFile.exists()) {
            try {
                root = CompressedStreamTools.readCompressed(new FileInputStream(planeFile));
            } catch (IOException e2) {
                e2.printStackTrace();
            }
            if (root != null) {
                PocketPlaneData.planes.clear();
                final NBTTagList planeNBT = root.getTagList("Data", 10);
                pocketPlaneMAXID = root.getInteger("MaxID");
                for (int i = 0; i < planeNBT.tagCount(); ++i) {
                    final NBTTagCompound thePlane = planeNBT.getCompoundTagAt(i);
                    final PocketPlaneData data = new PocketPlaneData();
                    data.radius = thePlane.getInteger("radius");
                    data.potionEffects = thePlane.getIntArray("effects");
                    data.name = thePlane.getString("name");
                    data.color = thePlane.getInteger("color");
                    data.portalA = thePlane.getIntArray("portalA");
                    data.portalB = thePlane.getIntArray("portalB");
                    data.portalC = thePlane.getIntArray("portalC");
                    data.portalD = thePlane.getIntArray("portalD");
                    PocketPlaneData.planes.add(data);
                }
                final NBTTagCompound positionz = root.getCompoundTag("Positions");
                PocketPlaneData.positions.clear();
                Set<String> keySet = positionz.func_150296_c();
                for (final String id : keySet) {
                    PocketPlaneData.positions.put(
                            Integer.valueOf(id),
                            Vec3.createVectorHelper(
                                    positionz.getIntArray(id)[0],
                                    positionz.getIntArray(id)[1],
                                    positionz.getIntArray(id)[2]));
                }
            }
        }
    }

    public static void savePocketPlanes(final World world) {
        final File planeFile = new File(world.getSaveHandler().getWorldDirectory(), "pocketplane.dat");
        final NBTTagCompound root = new NBTTagCompound();
        final NBTTagCompound positionz = new NBTTagCompound();
        for (Map.Entry<Integer, Vec3> idToPos : PocketPlaneData.positions.entrySet()) {
            positionz.setIntArray(
                    idToPos.getKey().toString(),
                    new int[] { (int) idToPos.getValue().xCoord, (int) idToPos.getValue().yCoord,
                            (int) idToPos.getValue().zCoord });
        }
        root.setInteger("MaxID", pocketPlaneMAXID);
        root.setTag("Positions", positionz);
        final NBTTagList planeNBT = new NBTTagList();
        root.setTag("Data", planeNBT);
        for (final PocketPlaneData data : PocketPlaneData.planes) {
            if (data != null) {
                final NBTTagCompound thePlane = new NBTTagCompound();
                thePlane.setInteger("radius", data.radius);
                thePlane.setIntArray("effects", data.potionEffects);
                thePlane.setString("name", data.name);
                thePlane.setInteger("color", data.color);
                thePlane.setIntArray("portalA", data.portalA);
                thePlane.setIntArray("portalB", data.portalB);
                thePlane.setIntArray("portalC", data.portalC);
                thePlane.setIntArray("portalD", data.portalD);
                planeNBT.appendTag(thePlane);
            }
        }
        try {
            CompressedStreamTools.writeCompressed(root, new FileOutputStream(planeFile));
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public static int firstAvailablePortal(final int num) {
        final PocketPlaneData data = PocketPlaneData.planes.get(num);
        if (data.portalA[0] == 0 && data.portalA[1] == 0 && data.portalA[2] == 0) {
            return 1;
        }
        if (data.portalB[0] == 0 && data.portalB[1] == 0 && data.portalB[2] == 0) {
            return 2;
        }
        if (data.portalC[0] == 0 && data.portalC[1] == 0 && data.portalC[2] == 0) {
            return 3;
        }
        if (data.portalD[0] == 0 && data.portalD[1] == 0 && data.portalD[2] == 0) {
            return 4;
        }
        return 0;
    }

    public static void destroyPortal(final int id, final int which) {
        final PocketPlaneData data = PocketPlaneData.planes.get(id);
        final World world = MinecraftServer.getServer().worldServerForDimension(ThaumicHorizons.dimensionPocketId);
        if (which == 1) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(x, y, 256 * id + data.radius, ThaumicHorizons.blockVoid);
                }
            }
            data.portalA[0] = 0;
            data.portalA[1] = 0;
            data.portalA[2] = 0;
        } else if (which == 2) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(x, y, 256 * id - data.radius, ThaumicHorizons.blockVoid);
                }
            }
            data.portalB[0] = 0;
            data.portalB[1] = 0;
            data.portalB[2] = 0;
        } else if (which == 3) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(data.radius, y, 256 * id + z, ThaumicHorizons.blockVoid);
                }
            }
            data.portalC[0] = 0;
            data.portalC[1] = 0;
            data.portalC[2] = 0;
        } else if (which == 4) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(-data.radius, y, 256 * id + z, ThaumicHorizons.blockVoid);
                }
            }
            data.portalD[0] = 0;
            data.portalD[1] = 0;
            data.portalD[2] = 0;
        }
    }

    public static void makePortal(final int id, final int which, final int xCoord, final int yCoord, final int zCoord) {
        final PocketPlaneData data = PocketPlaneData.planes.get(id);
        final World world = MinecraftServer.getServer().worldServerForDimension(ThaumicHorizons.dimensionPocketId);
        if (which == 1) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(x, y, 256 * id + data.radius, ThaumicHorizons.blockPortal, 0, 3);
                    world.setBlock(x, y, 256 * id + data.radius + 1, ThaumicHorizons.blockVoid);
                }
            }
            data.portalA[0] = xCoord;
            data.portalA[1] = yCoord;
            data.portalA[2] = zCoord;
        } else if (which == 2) {
            for (int x = -1; x <= 1; ++x) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(x, y, 256 * id - data.radius, ThaumicHorizons.blockPortal, 2, 3);
                    world.setBlock(x, y, 256 * id - data.radius - 1, ThaumicHorizons.blockVoid);
                }
            }
            data.portalB[0] = xCoord;
            data.portalB[1] = yCoord;
            data.portalB[2] = zCoord;
        } else if (which == 3) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(data.radius, y, 256 * id + z, ThaumicHorizons.blockPortal, 1, 3);
                    world.setBlock(data.radius + 1, y, 256 * id + z, ThaumicHorizons.blockVoid);
                }
            }
            data.portalC[0] = xCoord;
            data.portalC[1] = yCoord;
            data.portalC[2] = zCoord;
        } else if (which == 4) {
            for (int z = -1; z <= 1; ++z) {
                for (int y = 126; y <= 128; ++y) {
                    world.setBlock(-data.radius, y, 256 * id + z, ThaumicHorizons.blockPortal, 3, 3);
                    world.setBlock(-data.radius - 1, y, 256 * id + z, ThaumicHorizons.blockVoid);
                }
            }
            data.portalD[0] = xCoord;
            data.portalD[1] = yCoord;
            data.portalD[2] = zCoord;
        }
    }

    void buildStructure(final int x, final int y, final int z, final Structure struct) {}

    private static class Structure {

        public int x;
        public int y;
        public int z;
        public Block[] blocks;
        public int[] meta;

        public Structure(final int x, final int y, final int z, final Block[] blocks, final int[] meta) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.blocks = blocks;
            this.meta = meta;
        }
    }
}
