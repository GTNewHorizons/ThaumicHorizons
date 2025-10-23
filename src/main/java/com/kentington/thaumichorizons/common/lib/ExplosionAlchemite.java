//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityItemInvulnerable;

import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.nodes.NodeModifier;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blocks.BlockAiry;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemCrystalEssence;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileNode;

public class ExplosionAlchemite extends Explosion {

    public boolean isFlaming;
    public boolean isSmoking;
    private int field_77289_h;
    private final Random explosionRNG;
    private final World worldObj;
    public double explosionX;
    public double explosionY;
    public double explosionZ;
    public Entity exploder;
    public float explosionSize;
    public List<ChunkPosition> affectedBlockPositions;
    private Map field_77288_k;

    public ExplosionAlchemite(final World p_i1948_1_, final Entity p_i1948_2_, final double p_i1948_3_,
            final double p_i1948_5_, final double p_i1948_7_, final float p_i1948_9_) {
        super(p_i1948_1_, p_i1948_2_, p_i1948_3_, p_i1948_5_, p_i1948_7_, p_i1948_9_);
        this.isSmoking = true;
        this.field_77289_h = 16;
        this.explosionRNG = new Random();
        this.affectedBlockPositions = new ArrayList<>();
        this.field_77288_k = new HashMap<>();
        this.worldObj = p_i1948_1_;
        this.exploder = p_i1948_2_;
        this.explosionSize = p_i1948_9_;
        this.explosionX = p_i1948_3_;
        this.explosionY = p_i1948_5_;
        this.explosionZ = p_i1948_7_;
    }

    public void doExplosionA() {
        final float f = this.explosionSize;
        final HashSet<ChunkPosition> hashset = new HashSet<>();
        for (int i = 0; i < this.field_77289_h; ++i) {
            for (int j = 0; j < this.field_77289_h; ++j) {
                for (int k = 0; k < this.field_77289_h; ++k) {
                    if (i == 0 || i == this.field_77289_h - 1
                            || j == 0
                            || j == this.field_77289_h - 1
                            || k == 0
                            || k == this.field_77289_h - 1) {
                        double d0 = i / (this.field_77289_h - 1.0f) * 2.0f - 1.0f;
                        double d2 = j / (this.field_77289_h - 1.0f) * 2.0f - 1.0f;
                        double d3 = k / (this.field_77289_h - 1.0f) * 2.0f - 1.0f;
                        final double d4 = Math.sqrt(d0 * d0 + d2 * d2 + d3 * d3);
                        d0 /= d4;
                        d2 /= d4;
                        d3 /= d4;
                        float f2 = this.explosionSize * (0.7f + this.worldObj.rand.nextFloat() * 0.6f);
                        double d5 = this.explosionX;
                        double d6 = this.explosionY;
                        double d7 = this.explosionZ;
                        for (float f3 = 0.3f; f2 > 0.0f; f2 -= f3 * 0.75f) {
                            final int j2 = MathHelper.floor_double(d5);
                            final int k2 = MathHelper.floor_double(d6);
                            final int l1 = MathHelper.floor_double(d7);
                            final Block block = this.worldObj.getBlock(j2, k2, l1);
                            if (this.worldObj.getTileEntity(j2, k2, l1) != null
                                    && this.worldObj.getTileEntity(j2, k2, l1) instanceof TileNode) {
                                hashset.add(new ChunkPosition(j2, k2, l1));
                            } else if (block.getMaterial() != Material.air) {
                                final float f4 = (this.exploder != null)
                                        ? this.exploder.func_145772_a(this, this.worldObj, j2, k2, l1, block)
                                        : block.getExplosionResistance(
                                                this.exploder,
                                                this.worldObj,
                                                j2,
                                                k2,
                                                l1,
                                                this.explosionX,
                                                this.explosionY,
                                                this.explosionZ);
                                f2 -= (f4 + 0.3f) * f3;
                            }
                            if (f2 > 0.0f && (this.exploder == null
                                    || this.exploder.func_145774_a(this, this.worldObj, j2, k2, l1, block, f2))) {
                                hashset.add(new ChunkPosition(j2, k2, l1));
                            }
                            d5 += d0 * f3;
                            d6 += d2 * f3;
                            d7 += d3 * f3;
                        }
                    }
                }
            }
        }
        this.affectedBlockPositions.addAll(hashset);
        this.explosionSize *= 2.0f;
        int i = MathHelper.floor_double(this.explosionX - this.explosionSize - 1.0);
        int j = MathHelper.floor_double(this.explosionX + this.explosionSize + 1.0);
        int k = MathHelper.floor_double(this.explosionY - this.explosionSize - 1.0);
        final int i2 = MathHelper.floor_double(this.explosionY + this.explosionSize + 1.0);
        final int m = MathHelper.floor_double(this.explosionZ - this.explosionSize - 1.0);
        final int j3 = MathHelper.floor_double(this.explosionZ + this.explosionSize + 1.0);
        final List list = this.worldObj
                .getEntitiesWithinAABBExcludingEntity(this.exploder, AxisAlignedBB.getBoundingBox(i, k, m, j, i2, j3));
        final Vec3 vec3 = Vec3.createVectorHelper(this.explosionX, this.explosionY, this.explosionZ);
        for (Object o : list) {
            final Entity entity = (Entity) o;
            final double d8 = entity.getDistance(this.explosionX, this.explosionY, this.explosionZ)
                    / this.explosionSize;
            if (d8 <= 1.0) {
                double d5 = entity.posX - this.explosionX;
                double d6 = entity.posY + entity.getEyeHeight() - this.explosionY;
                double d7 = entity.posZ - this.explosionZ;
                final double d9 = MathHelper.sqrt_double(d5 * d5 + d6 * d6 + d7 * d7);
                if (d9 != 0.0) {
                    d5 /= d9;
                    d6 /= d9;
                    d7 /= d9;
                    final double d10 = this.worldObj.getBlockDensity(vec3, entity.boundingBox);
                    final double d11 = (1.0 - d8) * d10;
                    entity.attackEntityFrom(
                            DamageSourceThaumcraft.dissolve,
                            (float) (int) ((d11 * d11 + d11) / 2.0 * 16.0 * this.explosionSize + 1.0));
                    final double d12 = EnchantmentProtection.func_92092_a(entity, d11);
                    entity.motionX += d5 * d12;
                    entity.motionY += d6 * d12;
                    entity.motionZ += d7 * d12;
                    if (entity instanceof EntityPlayer) {
                        this.field_77288_k.put(entity, Vec3.createVectorHelper(d5 * d11, d6 * d11, d7 * d11));
                    }
                }
            }
        }
        this.explosionSize = f;
    }

    public void doExplosionB(final boolean p_77279_1_) {
        this.worldObj.playSoundEffect(
                this.explosionX,
                this.explosionY,
                this.explosionZ,
                "random.explode",
                4.0f,
                (1.0f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 0.2f) * 0.7f);
        if (this.explosionSize >= 2.0f && this.isSmoking) {
            this.worldObj
                    .spawnParticle("hugeexplosion", this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
        } else {
            this.worldObj
                    .spawnParticle("largeexplode", this.explosionX, this.explosionY, this.explosionZ, 1.0, 0.0, 0.0);
        }
        ThaumicHorizons.proxy.alchemiteFX(this.worldObj, this.explosionX, this.explosionY, this.explosionZ);
        if (this.isSmoking) {
            for (final ChunkPosition chunkposition : this.affectedBlockPositions) {
                final int i = chunkposition.chunkPosX;
                final int j = chunkposition.chunkPosY;
                final int k = chunkposition.chunkPosZ;
                final Block block = this.worldObj.getBlock(i, j, k);
                if (this.worldObj.getTileEntity(i, j, k) != null
                        && this.worldObj.getTileEntity(i, j, k) instanceof final TileNode node) {
                    final double d = Math.random();
                    if (d < 0.25) {
                        final AspectList aspects = node.getAspects();
                        if (aspects != null) {
                            for (final Aspect asp : aspects.getAspects()) {
                                final ItemStack stack = new ItemStack(
                                        ConfigItems.itemCrystalEssence,
                                        aspects.getAmount(asp));
                                ((ItemCrystalEssence) stack.getItem()).setAspects(stack, new AspectList().add(asp, 1));
                                this.worldObj
                                        .spawnEntityInWorld(new EntityItemInvulnerable(this.worldObj, i, j, k, stack));
                            }
                            ThaumicHorizons.proxy.disintegrateExplodeFX(this.worldObj, i, j, k);
                        }
                        BlockAiry.explodify(this.worldObj, i, j, k);
                    } else if (d < 0.5) {
                        node.setNodeModifier(NodeModifier.FADING);
                    } else {
                        node.setNodeModifier(null);
                        node.setNodeType(NodeType.UNSTABLE);
                    }
                } else {
                    if (block.getMaterial() == Material.air) {
                        continue;
                    }
                    Item item = Item.getItemFromBlock(block);
                    int meta = worldObj.getBlockMetadata(i, j, k);
                    if (item == null) {
                        item = block.getItemDropped(meta, worldObj.rand, 0);
                    }
                    if (item == null) {
                        continue;
                    }

                    final AspectList aspects = this.getAspects(item, meta);
                    if (aspects != null && aspects.size() > 0) {
                        for (final Aspect asp2 : aspects.getAspects()) {
                            final ItemStack stack = new ItemStack(
                                    ConfigItems.itemCrystalEssence,
                                    aspects.getAmount(asp2));
                            ((ItemCrystalEssence) stack.getItem()).setAspects(stack, new AspectList().add(asp2, 1));
                            this.worldObj.spawnEntityInWorld(new EntityItemInvulnerable(this.worldObj, i, j, k, stack));
                        }
                        ThaumicHorizons.proxy.disintegrateExplodeFX(this.worldObj, i, j, k);
                    } else {
                        block.dropBlockAsItemWithChance(
                                this.worldObj,
                                i,
                                j,
                                k,
                                this.worldObj.getBlockMetadata(i, j, k),
                                1.0f,
                                0);
                    }
                    block.onBlockExploded(this.worldObj, i, j, k, this);
                }
            }
        }
        if (this.isFlaming) {
            for (final ChunkPosition chunkposition : this.affectedBlockPositions) {
                final int i = chunkposition.chunkPosX;
                final int j = chunkposition.chunkPosY;
                final int k = chunkposition.chunkPosZ;
                final Block block = this.worldObj.getBlock(i, j, k);
                final Block block2 = this.worldObj.getBlock(i, j - 1, k);
                if (block.getMaterial() == Material.air && block2.func_149730_j()
                        && this.explosionRNG.nextInt(3) == 0) {
                    this.worldObj.setBlock(i, j, k, Blocks.fire);
                }
            }
        }
    }

    private AspectList getAspects(final Item block, final int meta) {
        final ItemStack tmpStack = new ItemStack(block, 1, meta);
        AspectList tmp = ThaumcraftCraftingManager.getObjectTags(tmpStack);
        tmp = ThaumcraftCraftingManager.getBonusTags(tmpStack, tmp);
        if (tmp == null || tmp.size() == 0) {
            tmp = ThaumcraftApi.objectTags.get(Arrays.asList(block, 32767));
            if (meta == 32767 && tmp == null) {
                int index = 0;
                do {
                    tmp = ThaumcraftApi.objectTags.get(Arrays.asList(block, index));
                } while (++index < 16 && tmp == null);
            }
        }
        return tmp;
    }

    public Map func_77277_b() {
        return this.field_77288_k;
    }

    public EntityLivingBase getExplosivePlacedBy() {
        return (this.exploder == null) ? null
                : (EntityLivingBase) ((this.exploder instanceof EntityTNTPrimed)
                        ? ((EntityTNTPrimed) this.exploder).getTntPlacedBy()
                        : ((this.exploder instanceof EntityLivingBase) ? this.exploder : null));
    }
}
