//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.util.ForgeDirection;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.blocks.BlockTransductionAmplifier;

import cpw.mods.fml.common.network.NetworkRegistry;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileNodeConverter;
import thaumcraft.common.tiles.TileNodeEnergized;

public class TileTransductionAmplifier extends TileThaumcraft {

    public int count;
    public byte direction;
    public boolean activated;
    public boolean shouldActivate;
    boolean lastActivated;
    boolean fireOnce;

    public TileTransductionAmplifier() {
        this.count = -1;
        this.direction = -1;
        this.activated = false;
        this.shouldActivate = false;
        this.fireOnce = false;
    }

    public void updateEntity() {
        super.updateEntity();

        if (this.worldObj.provider.dimensionId == ThaumicHorizons.dimensionPocketId) {
            this.worldObj.createExplosion(null, this.xCoord, this.yCoord, this.zCoord, 1.0f, false);
        }

        if (!this.fireOnce) {
            this.direction = (byte) this.getBlockMetadata();
            this.worldObj.getBlock(this.xCoord, this.yCoord, this.zCoord)
                    .onNeighborBlockChange(this.worldObj, this.xCoord, this.yCoord, this.zCoord, null);
            this.fireOnce = true;
        }
        if (this.activated) {
            ++this.count;
        } else if (!this.activated && this.count > 0) {
            if (this.count > 50) {
                this.count = 50;
            }
            --this.count;
        }
        if (this.shouldActivate && !this.activated) {
            if (this.direction == -1) {
                this.direction = (byte) this.getBlockMetadata();
            }
            final ForgeDirection dir = ForgeDirection.getOrientation(this.direction);
            if (dir == ForgeDirection.UP && this.worldObj
                    .getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof TileNodeEnergized) {
                this.boostNode(this.xCoord, this.yCoord - 1, this.zCoord);
            } else if (dir == ForgeDirection.DOWN && this.worldObj
                    .getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof TileNodeEnergized) {
                        this.boostNode(this.xCoord, this.yCoord + 1, this.zCoord);
                    } else
                if (dir == ForgeDirection.NORTH && this.worldObj
                        .getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileNodeEnergized) {
                            this.boostNode(this.xCoord, this.yCoord, this.zCoord + 1);
                        } else
                    if (dir == ForgeDirection.SOUTH && this.worldObj
                            .getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileNodeEnergized) {
                                this.boostNode(this.xCoord, this.yCoord, this.zCoord - 1);
                            } else
                        if (dir == ForgeDirection.WEST && this.worldObj.getTileEntity(
                                this.xCoord + 1,
                                this.yCoord,
                                this.zCoord) instanceof TileNodeEnergized) {
                                    this.boostNode(this.xCoord + 1, this.yCoord, this.zCoord);
                                } else
                            if (dir == ForgeDirection.EAST && this.worldObj.getTileEntity(
                                    this.xCoord - 1,
                                    this.yCoord,
                                    this.zCoord) instanceof TileNodeEnergized) {
                                        this.boostNode(this.xCoord - 1, this.yCoord, this.zCoord);
                                    } else {
                                        ((BlockTransductionAmplifier) ThaumicHorizons.blockTransducer)
                                                .killMe(this.worldObj, this.xCoord, this.yCoord, this.zCoord, true);
                                        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
                                    }
            this.activated = true;
            this.markDirty();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        } else if (!this.shouldActivate && this.activated) {
            if (this.direction == -1) {
                this.direction = (byte) this.getBlockMetadata();
            }
            final ForgeDirection dir = ForgeDirection.getOrientation(this.direction);
            if (dir == ForgeDirection.UP && this.worldObj
                    .getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof TileNodeEnergized) {
                this.unboostNode(this.xCoord, this.yCoord - 1, this.zCoord);
            } else if (dir == ForgeDirection.DOWN && this.worldObj
                    .getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof TileNodeEnergized) {
                        this.unboostNode(this.xCoord, this.yCoord + 1, this.zCoord);
                    } else
                if (dir == ForgeDirection.NORTH && this.worldObj
                        .getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileNodeEnergized) {
                            this.unboostNode(this.xCoord, this.yCoord, this.zCoord + 1);
                        } else
                    if (dir == ForgeDirection.SOUTH && this.worldObj
                            .getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileNodeEnergized) {
                                this.unboostNode(this.xCoord, this.yCoord, this.zCoord - 1);
                            } else
                        if (dir == ForgeDirection.WEST && this.worldObj.getTileEntity(
                                this.xCoord + 1,
                                this.yCoord,
                                this.zCoord) instanceof TileNodeEnergized) {
                                    this.unboostNode(this.xCoord + 1, this.yCoord, this.zCoord);
                                } else
                            if (dir == ForgeDirection.EAST && this.worldObj.getTileEntity(
                                    this.xCoord - 1,
                                    this.yCoord,
                                    this.zCoord) instanceof TileNodeEnergized) {
                                        this.unboostNode(this.xCoord - 1, this.yCoord, this.zCoord);
                                    } else {
                                        ((BlockTransductionAmplifier) ThaumicHorizons.blockTransducer)
                                                .killMe(this.worldObj, this.xCoord, this.yCoord, this.zCoord, true);
                                        this.worldObj.setBlockToAir(this.xCoord, this.yCoord, this.zCoord);
                                    }
            this.activated = false;
            this.markDirty();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
        if (!this.worldObj.isRemote && this.activated && this.count % 10 == 0) {
            int ecks = this.xCoord;
            final int why = this.yCoord;
            int zee = this.zCoord;
            final ForgeDirection dir2 = ForgeDirection.getOrientation(this.direction);
            if (dir2 == ForgeDirection.NORTH && (this.worldObj
                    .getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileNodeEnergized
                    || this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileNode)) {
                ++zee;
            } else if (dir2 == ForgeDirection.SOUTH && (this.worldObj
                    .getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileNodeEnergized
                    || this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileNode)) {
                        --zee;
                    } else
                if (dir2 == ForgeDirection.WEST && (this.worldObj
                        .getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof TileNodeEnergized
                        || this.worldObj
                                .getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof TileNode)) {
                                    ++ecks;
                                } else
                    if (dir2 == ForgeDirection.EAST && (this.worldObj
                            .getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof TileNodeEnergized
                            || this.worldObj
                                    .getTileEntity(this.xCoord - 1, this.yCoord, this.zCoord) instanceof TileNode)) {
                                        --ecks;
                                    }
            int transducers = 0;
            if (this.worldObj.getBlock(ecks + 1, why, zee) == ThaumicHorizons.blockTransducer
                    && ((TileTransductionAmplifier) this.worldObj.getTileEntity(ecks + 1, why, zee)).activated) {
                ++transducers;
            }
            if (this.worldObj.getBlock(ecks - 1, why, zee) == ThaumicHorizons.blockTransducer
                    && ((TileTransductionAmplifier) this.worldObj.getTileEntity(ecks - 1, why, zee)).activated) {
                ++transducers;
            }
            if (this.worldObj.getBlock(ecks, why, zee + 1) == ThaumicHorizons.blockTransducer
                    && ((TileTransductionAmplifier) this.worldObj.getTileEntity(ecks, why, zee + 1)).activated) {
                ++transducers;
            }
            if (this.worldObj.getBlock(ecks, why, zee - 1) == ThaumicHorizons.blockTransducer
                    && ((TileTransductionAmplifier) this.worldObj.getTileEntity(ecks, why, zee - 1)).activated) {
                ++transducers;
            }
            if (transducers > 3 && this.count % 50 == 0) {
                this.unboostNode(ecks, why, zee);
                ((TileNodeConverter) this.worldObj.getTileEntity(ecks, why + 1, zee)).status = -1;
                final AspectList aspects = ((TileNodeEnergized) this.worldObj.getTileEntity(ecks, why, zee))
                        .getAuraBase().copy();
                this.worldObj.setBlock(ecks, why, zee, ThaumicHorizons.blockVortex, 0, 3);
                ((TileVortex) this.worldObj.getTileEntity(ecks, why, zee)).aspects = aspects;
            }
            if (transducers > 2 && this.worldObj.rand.nextInt(4) == 2 && this.count % 50 == 0) {
                final int dx = this.worldObj.rand.nextInt(16) - 8;
                final int dy = this.worldObj.rand.nextInt(16) - 8;
                final int dz = this.worldObj.rand.nextInt(16) - 8;
                if (this.worldObj.isAirBlock(this.xCoord + dx, this.yCoord + dy, this.zCoord + dz)) {
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketFXBlockZap(
                                    this.xCoord + 0.5f,
                                    this.yCoord + 0.5f,
                                    this.zCoord + 0.5f,
                                    this.xCoord + 0.5f + dx,
                                    this.yCoord + 0.5f + dy,
                                    this.zCoord + 0.5f + dz),
                            new NetworkRegistry.TargetPoint(
                                    this.worldObj.provider.dimensionId,
                                    this.xCoord,
                                    this.yCoord,
                                    this.zCoord,
                                    32.0));
                    if (dy > 0) {
                        this.worldObj.setBlock(
                                this.xCoord + dx,
                                this.yCoord + dy,
                                this.zCoord + dz,
                                ConfigBlocks.blockFluxGas);
                    } else {
                        this.worldObj.setBlock(
                                this.xCoord + dx,
                                this.yCoord + dy,
                                this.zCoord + dz,
                                ConfigBlocks.blockFluxGoo);
                    }
                }
            }
            if (transducers > 1 && this.count % 50 == 0) {
                final List<EntityLivingBase> targets = this.worldObj.getEntitiesWithinAABB(
                        EntityLivingBase.class,
                        AxisAlignedBB.getBoundingBox(
                                this.xCoord,
                                this.yCoord,
                                this.zCoord,
                                this.xCoord + 1,
                                this.yCoord + 1,
                                this.zCoord + 1).expand(10.0, 10.0, 10.0));
                if (targets != null && !targets.isEmpty()) {
                    for (final Entity target : targets) {
                        PacketHandler.INSTANCE.sendToAllAround(
                                new PacketFXBlockZap(
                                        this.xCoord + 0.5f,
                                        this.yCoord + 0.5f,
                                        this.zCoord + 0.5f,
                                        (float) target.posX,
                                        (float) target.posY + target.height / 2.0f,
                                        (float) target.posZ),
                                new NetworkRegistry.TargetPoint(
                                        this.worldObj.provider.dimensionId,
                                        this.xCoord,
                                        this.yCoord,
                                        this.zCoord,
                                        32.0));
                        target.attackEntityFrom(DamageSource.magic, (float) (4 + this.worldObj.rand.nextInt(1)));
                    }
                }
            }
            if (this.worldObj.getTileEntity(ecks, why, zee) instanceof TileNode) {
                for (int i = 0; i < transducers; ++i) {
                    this.unboostNode(ecks, why, zee);
                }
                this.activated = false;
                this.markDirty();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            }
        }
    }

    void boostNode(final int x, final int y, final int z) {
        final TileEntity tyle = this.worldObj.getTileEntity(x, y, z);
        if (tyle instanceof final TileNodeEnergized node) {
            final AspectList baseVis = node.getAuraBase();
            for (final Aspect asp : baseVis.getAspects()) {
                baseVis.add(asp, 10);
            }
            node.setAspects(baseVis);
            node.setupNode();
        }
    }

    public void unBoostNode(final int x, final int y, final int z) {
        final ForgeDirection dir = ForgeDirection.getOrientation(this.direction);
        if (dir == ForgeDirection.UP && this.worldObj
                .getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord) instanceof TileNodeEnergized) {
            this.unboostNode(this.xCoord, this.yCoord - 1, this.zCoord);
        } else if (dir == ForgeDirection.DOWN && this.worldObj
                .getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof TileNodeEnergized) {
                    this.unboostNode(this.xCoord, this.yCoord + 1, this.zCoord);
                } else
            if (dir == ForgeDirection.NORTH && this.worldObj
                    .getTileEntity(this.xCoord, this.yCoord, this.zCoord + 1) instanceof TileNodeEnergized) {
                        this.unboostNode(this.xCoord, this.yCoord, this.zCoord + 1);
                    } else
                if (dir == ForgeDirection.SOUTH && this.worldObj
                        .getTileEntity(this.xCoord, this.yCoord, this.zCoord - 1) instanceof TileNodeEnergized) {
                            this.unboostNode(this.xCoord, this.yCoord, this.zCoord - 1);
                        } else
                    if (dir == ForgeDirection.WEST && this.worldObj
                            .getTileEntity(this.xCoord + 1, this.yCoord, this.zCoord) instanceof TileNodeEnergized) {
                                this.unboostNode(this.xCoord + 1, this.yCoord, this.zCoord);
                            } else
                        if (dir == ForgeDirection.EAST && this.worldObj.getTileEntity(
                                this.xCoord - 1,
                                this.yCoord,
                                this.zCoord) instanceof TileNodeEnergized) {
                                    this.unboostNode(this.xCoord - 1, this.yCoord, this.zCoord);
                                }
    }

    void unboostNode(final int x, final int y, final int z) {
        final TileEntity tyle = this.worldObj.getTileEntity(x, y, z);
        if (tyle instanceof final TileNodeEnergized node) {
            final AspectList baseVis = node.getAuraBase();
            for (final Aspect asp : baseVis.getAspects()) {
                baseVis.remove(asp, 10);
            }
            node.setAspects(baseVis);
            node.setupNode();
        } else if (tyle instanceof final TileNode node2) {
            final AspectList baseVis = node2.getAspectsBase();
            for (final Aspect asp : baseVis.getAspects()) {
                baseVis.remove(asp, 10);
            }
            node2.setAspects(baseVis);
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setBoolean("active", this.activated);
        nbttagcompound.setByte("dir", this.direction);
        nbttagcompound.setBoolean("shouldactivate", this.shouldActivate);
        nbttagcompound.setInteger("count", this.count);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.activated = nbttagcompound.getBoolean("active");
        this.direction = nbttagcompound.getByte("dir");
        this.shouldActivate = nbttagcompound.getBoolean("shouldactivate");
        this.count = nbttagcompound.getInteger("count");
    }
}
