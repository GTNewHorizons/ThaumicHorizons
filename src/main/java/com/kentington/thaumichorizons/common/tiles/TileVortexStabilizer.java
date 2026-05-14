//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.kentington.thaumichorizons.client.fx.FXSonic;
import com.kentington.thaumichorizons.common.ThaumicHorizons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.IWandable;
import thaumcraft.common.Thaumcraft;

public class TileVortexStabilizer extends TileThaumcraft implements IWandable {

    public boolean hasTarget;
    public int prevType;
    public int xTarget;
    public int yTarget;
    public int zTarget;
    public TileEntity target;
    public int direction;
    boolean fireOnce;
    public boolean redstoned;
    public ForgeDirection dir;
    public Object theBeam;
    private Entity[] sonicFX;

    public TileVortexStabilizer() {
        this.xTarget = Integer.MAX_VALUE;
        this.yTarget = Integer.MAX_VALUE;
        this.zTarget = Integer.MAX_VALUE;
        this.target = null;
        this.fireOnce = false;
        this.theBeam = null;
        this.sonicFX = null;
    }

    public void updateEntity() {
        super.updateEntity();
        if (!this.fireOnce) {
            ThaumicHorizons.blockVortexStabilizer.onNeighborBlockChange(
                    this.worldObj,
                    this.xCoord,
                    this.yCoord,
                    this.zCoord,
                    ThaumicHorizons.blockVortexStabilizer);
            this.direction = (byte) this.getBlockMetadata();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.dir = ForgeDirection.getOrientation(this.direction);
            if (this.target == null) {
                this.target = this.worldObj.getTileEntity(this.xTarget, this.yTarget, this.zTarget);
            }
            this.fireOnce = true;
        }
        if (this.worldObj.getWorldTime() % 5L == 0L) {
            MovingObjectPosition mop = null;
            if (this.redstoned) {
                mop = this.worldObj.rayTraceBlocks(
                        Vec3.createVectorHelper(
                                this.xCoord + this.dir.offsetX + 0.75,
                                this.yCoord + this.dir.offsetY + 0.75,
                                this.zCoord + this.dir.offsetZ + 0.75),
                        Vec3.createVectorHelper(
                                this.xCoord + this.dir.offsetX * 10 + 0.5,
                                this.yCoord + this.dir.offsetY * 10 + 0.5,
                                this.zCoord + this.dir.offsetZ * 10 + 0.5));
            }
            if (mop != null) {
                if (mop.blockX != this.xTarget || mop.blockY != this.yTarget || mop.blockZ != this.zTarget) {
                    if (this.hasTarget) {
                        this.reHungrifyTarget();
                        this.hasTarget = false;
                    }
                    // Separate if (not else if) so a new valid target is acquired in the same tick,
                    // preventing a window where beams is decremented but not yet restored.
                    final TileEntity newTE = this.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
                    if (newTE instanceof INode) {
                        this.hasTarget = true;
                        this.target = newTE;
                        this.prevType = ((INode) newTE).getNodeType().ordinal();
                        this.deHungrifyTarget();
                    } else if (newTE instanceof TileVortex) {
                        this.hasTarget = true;
                        this.target = newTE;
                        this.deHungrifyTarget();
                    }
                    this.xTarget = mop.blockX;
                    this.yTarget = mop.blockY;
                    this.zTarget = mop.blockZ;
                    this.markDirty();
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                }
            } else {
                if (this.hasTarget) {
                    this.reHungrifyTarget();
                    this.hasTarget = false;
                    this.markDirty();
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                }
                this.xTarget = this.xCoord + this.dir.offsetX * 10;
                this.yTarget = this.yCoord + this.dir.offsetY * 10;
                this.zTarget = this.zCoord + this.dir.offsetZ * 10;
                this.target = null;
            }
        }
        if (this.worldObj.isRemote && this.redstoned
                && ThaumicHorizons.proxy.readyToRender()
                && this.xTarget != Integer.MAX_VALUE
                && this.yTarget != Integer.MAX_VALUE
                && this.zTarget != Integer.MAX_VALUE) {
            if (this.sonicFX == null) {
                this.sonicFX = new Entity[3];
            }
            for (int i = 0; i < 3; ++i) {
                if (this.sonicFX[i] == null || this.sonicFX[i].isDead) {
                    this.sonicFX[i] = new FXSonic(
                            Thaumcraft.proxy.getClientWorld(),
                            this.xTarget + 0.5,
                            this.yTarget + 0.5,
                            this.zTarget + 0.5,
                            10,
                            this.direction);
                    ThaumicHorizons.proxy.addEffect(this.sonicFX[i]);
                    break;
                }
            }
            this.theBeam = Thaumcraft.proxy.beamBore(
                    this.worldObj,
                    this.xCoord + 0.5,
                    this.yCoord + 0.5,
                    this.zCoord + 0.5,
                    this.xTarget + 0.5 - this.dir.offsetX,
                    this.yTarget + 0.5 - this.dir.offsetY,
                    this.zTarget + 0.5 - this.dir.offsetZ,
                    1,
                    33023,
                    false,
                    2.0f,
                    this.theBeam,
                    1);
        } else if (this.sonicFX != null) {
            for (int i = 0; i < 3; ++i) {
                if (this.sonicFX[i] != null) {
                    this.sonicFX[i].setDead();
                    this.sonicFX[i] = null;
                }
            }
        }
    }

    public void reHungrifyTarget() {
        if (this.target instanceof INode) {
            if (!isNodeTargetedByOtherStabilizer()) {
                ((INode) this.target).setNodeType(NodeType.values()[this.prevType]);
            }
        } else if (this.target instanceof TileVortex) {
            --((TileVortex) this.target).beams;
        }
        if (this.target != null) {
            this.target.markDirty();
        }
    }

    private boolean isNodeTargetedByOtherStabilizer() {
        // A stabilizer can only reach a node from up to 10 blocks away in one of 6 directions,
        // so check those 60 positions instead of scanning all loaded tile entities.
        int tx = this.target.xCoord;
        int ty = this.target.yCoord;
        int tz = this.target.zCoord;
        for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
            for (int dist = 1; dist <= 10; dist++) {
                TileEntity te = this.worldObj
                        .getTileEntity(tx - d.offsetX * dist, ty - d.offsetY * dist, tz - d.offsetZ * dist);
                if (te instanceof TileVortexStabilizer && te != this) {
                    TileVortexStabilizer other = (TileVortexStabilizer) te;
                    if (other.hasTarget && other.target == this.target) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    void deHungrifyTarget() {
        if (this.target instanceof INode) {
            ((INode) this.target).setNodeType(NodeType.NORMAL);
        } else if (this.target instanceof TileVortex) {
            ++((TileVortex) this.target).beams;
        }
        if (this.target != null) {
            this.target.markDirty();
        }
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setInteger("xT", this.xTarget);
        nbttagcompound.setInteger("yT", this.yTarget);
        nbttagcompound.setInteger("zT", this.zTarget);
        nbttagcompound.setInteger("direction", this.direction);
        nbttagcompound.setBoolean("hasTarget", this.hasTarget);
        nbttagcompound.setBoolean("active", this.redstoned);
        nbttagcompound.setInteger("prevType", this.prevType);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.xTarget = nbttagcompound.getInteger("xT");
        this.yTarget = nbttagcompound.getInteger("yT");
        this.zTarget = nbttagcompound.getInteger("zT");
        this.direction = nbttagcompound.getInteger("direction");
        this.hasTarget = nbttagcompound.getBoolean("hasTarget");
        this.redstoned = nbttagcompound.getBoolean("active");
        this.prevType = nbttagcompound.getInteger("prevType");
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
        return TileVortexStabilizer.INFINITE_EXTENT_AABB;
    }

    @Override
    public int onWandRightClick(final World world, final ItemStack wandstack, final EntityPlayer player, final int x,
            final int y, final int z, final int side, final int md) {
        this.dir = ForgeDirection.getOrientation(side);
        world.setBlockMetadataWithNotify(x, y, z, this.direction = side, 3);
        player.worldObj.playSound(
                x + 0.5,
                y + 0.5,
                z + 0.5,
                "thaumcraft:tool",
                0.5f,
                0.9f + player.worldObj.rand.nextFloat() * 0.2f,
                false);
        player.swingItem();
        this.markDirty();
        return 0;
    }

    @Override
    public ItemStack onWandRightClick(final World world, final ItemStack wandstack, final EntityPlayer player) {
        return null;
    }

    @Override
    public void onUsingWandTick(final ItemStack wandstack, final EntityPlayer player, final int count) {}

    @Override
    public void onWandStoppedUsing(final ItemStack wandstack, final World world, final EntityPlayer player,
            final int count) {}
}
