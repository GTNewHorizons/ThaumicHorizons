//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.entities;

import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import com.kentington.thaumichorizons.client.lib.GolemTHTexture;
import com.kentington.thaumichorizons.common.entities.ai.EntityAIFollowPlayer;
import com.kentington.thaumichorizons.common.lib.networking.PacketFXBlocksplosion;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.ai.combat.AIAvoidCreeperSwell;
import thaumcraft.common.entities.ai.combat.AIGolemAttackOnCollide;
import thaumcraft.common.entities.ai.combat.AIHurtByTarget;
import thaumcraft.common.entities.ai.combat.AINearestAttackableTarget;
import thaumcraft.common.entities.ai.misc.AIOpenDoor;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.EnumGolemType;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;

public class EntityGolemTH extends EntityGolemBase {

    public ResourceLocation texture;
    public Block blocky;
    public int md;
    public int ticksAlive;
    public int voidCount;
    public boolean berserk;
    public boolean kaboom;
    public EnumGolemTHType type;

    public EntityGolemTH(final World par1World) {
        super(par1World);
        this.texture = null;
        this.ticksAlive = 0;
        this.voidCount = 0;
        this.type = EnumGolemTHType.SAND;
    }

    public void loadGolem(final double x, final double y, final double z, final Block block, final int md,
            final int ticksAlive, final boolean adv, final boolean berserk, final boolean kaboom) {
        this.setPosition(x, y, z);
        this.md = md;
        this.blocky = block;
        this.ticksAlive = ticksAlive;
        this.advanced = adv;
        this.berserk = berserk;
        this.kaboom = kaboom;
        this.loadGolemTexturesAndStats();
        this.setupGolem();
        this.upgrades = new byte[this.type.upgrades + (this.advanced ? 1 : 0)];
        Arrays.fill(this.upgrades, (byte) -1);
    }

    public void loadGolemTexturesAndStats() {
        if (this.blocky == null) {
            this.type = EnumGolemTHType.VOID;
            return;
        }
        final Material m = this.blocky.getMaterial();
        if (m == Material.grass) {
            this.type = EnumGolemTHType.GRASS;
        } else if (m == Material.ground) {
            this.type = EnumGolemTHType.DIRT;
        } else if (m == Material.wood || m == Material.gourd) {
            this.type = EnumGolemTHType.WOOD;
        } else if (m == Material.leaves || m == Material.plants || m == Material.vine) {
            this.type = EnumGolemTHType.PLANT;
        } else if (m == Material.rock) {
            this.type = EnumGolemTHType.ROCK;
        } else if (m == Material.iron || m == Material.anvil) {
            this.type = EnumGolemTHType.METAL;
        } else if (m == Material.sponge || m == Material.cloth || m == Material.carpet) {
            this.type = EnumGolemTHType.CLOTH;
        } else if (m == Material.sand) {
            this.type = EnumGolemTHType.SAND;
        } else if (m == Material.redstoneLight || m == Material.circuits) {
            this.type = EnumGolemTHType.REDSTONE;
        } else if (m == Material.tnt) {
            this.type = EnumGolemTHType.TNT;
        } else if (m == Material.ice || m == Material.packedIce || m == Material.snow || m == Material.craftedSnow) {
            this.type = EnumGolemTHType.ICE;
        } else if (m == Material.cactus) {
            this.type = EnumGolemTHType.CACTUS;
        } else if (m == Material.cake) {
            this.type = EnumGolemTHType.CAKE;
        } else if (m == Material.web) {
            this.type = EnumGolemTHType.WEB;
        } else {
            this.type = EnumGolemTHType.ROCK;
        }
        if (this.worldObj.isRemote) {
            this.loadTexture();
        }
    }

    public boolean isValidTarget(final Entity target) {
        if (!this.berserk) {
            return super.isValidTarget(target) || target instanceof EntityCreeper;
        }
        return target.isEntityAlive()
                && (!(target instanceof EntityPlayer) || !target.getCommandSenderName().equals(this.getOwnerName()))
                && !target.getCommandSenderName().equals(this.getCommandSenderName());
    }

    public boolean setupGolem() {
        super.setupGolem();
        if (this.getCore() == -1) {
            this.tasks.addTask(0, new AIAvoidCreeperSwell(this));
            this.targetTasks.addTask(1, new AIHurtByTarget(this, false));
            this.targetTasks.addTask(2, new AINearestAttackableTarget(this, 0, true));
            this.tasks.addTask(3, new AIGolemAttackOnCollide(this));
            this.tasks.addTask(5, new AIOpenDoor(this, true));
            if (!this.kaboom) {
                this.tasks.addTask(6, new EntityAIFollowPlayer(this, this.getAIMoveSpeed() * 3.0f, 2.0f, 12.0f));
            } else {
                this.tasks.addTask(6, new EntityAIFollowPlayer(this, this.getAIMoveSpeed() * 3.0f, 8.0f, 12.0f));
            }
            this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0f));
            this.tasks.addTask(8, new EntityAILookIdle(this));
            this.paused = false;
            this.inactive = false;
            this.bootup = 0.0f;
        }
        if (!this.worldObj.isRemote) {
            this.dataWatcher.updateObject(19, (byte) this.type.ordinal());
        }
        this.getNavigator().setAvoidsWater(
                this.getGolemTHType() != EnumGolemTHType.ROCK && this.getGolemTHType() != EnumGolemTHType.METAL
                        && this.getGolemTHType() != EnumGolemTHType.REDSTONE);
        int bonus = 0;
        try {
            bonus = (this.getGolemDecoration().contains("H") ? 5 : 0);
        } catch (Exception ignored) {}
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.getGolemTHType().health + bonus);
        return true;
    }

    public boolean attackEntityFrom(final DamageSource ds, float par2) {
        this.paused = false;
        if (ds == DamageSource.cactus) {
            return false;
        }
        if (this.blocky == ConfigBlocks.blockCosmeticSolid && this.md == 4 && ds == DamageSource.magic) {
            par2 *= 0.5f;
        }
        if (ds.getSourceOfDamage() != null && this.getUpgradeAmount(5) > 0
                && ds.getSourceOfDamage().getEntityId() != this.getEntityId()) {
            ds.getSourceOfDamage().attackEntityFrom(
                    DamageSource.causeThornsDamage(this),
                    (float) (this.getUpgradeAmount(5) * 2 + this.rand.nextInt(2 * this.getUpgradeAmount(5))));
            ds.getSourceOfDamage().playSound("damage.thorns", 0.5f, 1.0f);
        } else if (ds.getSourceOfDamage() != null && this.blocky == Blocks.cactus
                && ds.getSourceOfDamage().getEntityId() != this.getEntityId()) {
                    ds.getSourceOfDamage().attackEntityFrom(
                            DamageSource.causeThornsDamage(this),
                            (float) (this.getUpgradeAmount(5) * 2 + this.rand.nextInt(2)));
                    ds.getSourceOfDamage().playSound("damage.thorns", 0.5f, 1.0f);
                }
        return super.attackEntityFrom(ds, par2);
    }

    public int getCarryLimit() {
        int base = this.type.carry;
        if (this.worldObj.isRemote) {
            base = this.getGolemTHType().carry;
        }
        base += Math.min(16, Math.max(4, base)) * this.getUpgradeAmount(1);
        return base;
    }

    public float getAIMoveSpeed() {
        if (this.paused || this.inactive) {
            return 0.0f;
        }
        float speed = this.type.speed * (this.decoration.contains("B") ? 1.1f : 1.0f);
        if (this.decoration.contains("P")) {
            speed *= 0.88f;
        }
        speed *= 1.0f + this.getUpgradeAmount(0) * 0.15f;
        if (this.advanced) {
            speed *= 1.1f;
        }
        if (this.isInWater()
                && (this.getGolemTHType() == EnumGolemTHType.ROCK || this.getGolemTHType() == EnumGolemTHType.METAL
                        || this.getGolemTHType() == EnumGolemTHType.REDSTONE)) {
            speed *= 2.0f;
        }
        return speed;
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote) {
            if (this.getCore() == -1 && this.ticksAlive > 0) {
                --this.ticksAlive;
                if (this.worldObj.getPlayerEntityByName(this.getOwnerName()) != null) {
                    this.setHomeArea(
                            (int) this.worldObj.getPlayerEntityByName(this.getOwnerName()).posX,
                            (int) this.worldObj.getPlayerEntityByName(this.getOwnerName()).posY,
                            (int) this.worldObj.getPlayerEntityByName(this.getOwnerName()).posZ,
                            16);
                }
            } else if (this.getCore() == -1 && this.ticksAlive != -420) {
                this.die();
            } else if (this.getCore() == -1) {
                if (this.ticksExisted % 10 == 0 && this.worldObj.rand.nextInt(500) == 0) {
                    final EntityPlayer player = this.worldObj.getPlayerEntityByName(this.getOwnerName());
                    switch (this.voidCount) {
                        case 0 -> {
                            if (player != null) {
                                player.addChatMessage(
                                        new ChatComponentText(
                                                EnumChatFormatting.ITALIC + ""
                                                        + EnumChatFormatting.DARK_PURPLE
                                                        + StatCollector
                                                                .translateToLocal("thaumichorizons.golemWarning1")));
                                break;
                            }
                        }
                        case 1 -> {
                            if (player != null) {
                                player.addChatMessage(
                                        new ChatComponentText(
                                                EnumChatFormatting.ITALIC + ""
                                                        + EnumChatFormatting.DARK_PURPLE
                                                        + StatCollector
                                                                .translateToLocal("thaumichorizons.golemWarning2")));
                                break;
                            }
                        }
                        case 2 -> {
                            if (player != null) {
                                player.addChatMessage(
                                        new ChatComponentText(
                                                EnumChatFormatting.ITALIC + ""
                                                        + EnumChatFormatting.DARK_PURPLE
                                                        + StatCollector
                                                                .translateToLocal("thaumichorizons.golemWarning3")));
                                break;
                            }
                        }
                        case 3 -> {
                            this.die();
                            Thaumcraft.proxy
                                    .burst(this.worldObj, this.posX, this.posY + this.height / 2.0f, this.posZ, 2.0f);
                            final EntityEldritchGuardian scaryThing = new EntityEldritchGuardian(this.worldObj);
                            scaryThing.setPosition(this.posX, this.posY, this.posZ);
                            this.worldObj.spawnEntityInWorld(scaryThing);
                            scaryThing.setHomeArea((int) this.posX, (int) this.posY, (int) this.posZ, 32);
                        }
                    }
                    ++this.voidCount;
                }
                if (this.worldObj.getPlayerEntityByName(this.getOwnerName()) != null) {
                    this.setHomeArea(
                            (int) this.worldObj.getPlayerEntityByName(this.getOwnerName()).posX,
                            (int) this.worldObj.getPlayerEntityByName(this.getOwnerName()).posY,
                            (int) this.worldObj.getPlayerEntityByName(this.getOwnerName()).posZ,
                            16);
                }
            }
            if (this.regenTimer <= 0) {
                this.regenTimer = this.type.regenDelay;
                if (this.decoration.contains("F")) {
                    this.regenTimer *= (int) 0.66f;
                }
                if (!this.worldObj.isRemote && this.getHealth() < this.getMaxHealth()) {
                    this.worldObj.setEntityState(this, (byte) 5);
                    this.heal(1.0f);
                }
            }
        }
    }

    public boolean isWithinHomeDistance(final int par1, final int par2, final int par3) {
        return this.getCore() == -1 || super.isWithinHomeDistance(par1, par2, par3);
    }

    public void setFire(final int par1) {
        if (!this.type.fireResist) {
            super.setFire(par1);
        }
    }

    public void writeEntityToNBT(final NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setByte("GolemTypeTH", (byte) this.type.ordinal());
        nbt.setByte("GolemType", (byte) EnumGolemType.FLESH.ordinal());
        if (this.blocky != null) {
            final String s = "block";
            final Block blocky = this.blocky;
            nbt.setInteger(s, Block.getIdFromBlock(this.blocky));
        } else {
            nbt.setInteger("block", 0);
        }
        nbt.setByteArray("upgrades", this.upgrades);
        nbt.setInteger("metadata", this.md);
        nbt.setInteger("ticksAlive", this.ticksAlive);
        nbt.setInteger("voidCount", this.voidCount);
        nbt.setBoolean("berserk", this.berserk);
        nbt.setBoolean("explosive", this.kaboom);
    }

    public void readEntityFromNBT(final NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.type = EnumGolemTHType.getType(nbt.getByte("GolemTypeTH"));
        this.upgrades = new byte[this.type.upgrades + (this.advanced ? 1 : 0)];
        final int ul = this.upgrades.length;
        this.upgrades = nbt.getByteArray("upgrades");
        if (ul != this.upgrades.length) {
            final byte[] tt = new byte[ul];
            for (int a = 0; a < ul; ++a) {
                tt[a] = -1;
            }
            for (int a = 0; a < this.upgrades.length; ++a) {
                if (a < ul) {
                    tt[a] = this.upgrades[a];
                }
            }
            this.upgrades = tt;
        }
        StringBuilder st = new StringBuilder();
        for (final byte c : this.upgrades) {
            st.append(Integer.toHexString(c));
        }
        this.dataWatcher.updateObject(23, st.toString());
        this.blocky = Block.getBlockById(nbt.getInteger("block"));
        this.md = nbt.getInteger("metadata");
        this.ticksAlive = nbt.getInteger("ticksAlive");
        this.voidCount = nbt.getInteger("voidCount");
        this.berserk = nbt.getBoolean("berserk");
        this.kaboom = nbt.getBoolean("explosive");
        if (this.worldObj.isRemote) {
            this.loadTexture();
        }
    }

    protected void damageEntity(final DamageSource ds, final float par2) {
        if (ds.isFireDamage() && this.type.fireResist) {
            return;
        }
        super.damageEntity(ds, par2);
    }

    public int getTotalArmorValue() {
        int var1 = super.getTotalArmorValue() + this.type.armor;
        if (this.decoration.contains("V")) {
            ++var1;
        }
        if (this.decoration.contains("P")) {
            var1 += 4;
        }
        if (var1 > 20) {
            var1 = 20;
        }
        return var1;
    }

    public EnumGolemTHType getGolemTHType() {
        return EnumGolemTHType.getType(this.dataWatcher.getWatchableObjectByte(19));
    }

    @SideOnly(Side.CLIENT)
    public void loadTexture() {
        if (this.blocky == null || this.blocky == Blocks.air) {
            return;
        }
        final IIcon bottom = this.blocky.getIcon(0, this.md);
        final IIcon top = this.blocky.getIcon(1, this.md);
        final IIcon east = this.blocky.getIcon(2, this.md);
        final IIcon west = this.blocky.getIcon(3, this.md);
        final IIcon north = this.blocky.getIcon(4, this.md);
        final IIcon south = this.blocky.getIcon(5, this.md);
        final GolemTHTexture newTex = new GolemTHTexture(
                new IIcon[] { bottom, top, east, west, north, south },
                (this.blocky == Blocks.grass) ? 1 : ((this.blocky == Blocks.cake) ? 2 : 0));
        this.texture = new ResourceLocation("thaumichorizons", "TEMPGOLEMTEX" + this.getEntityId());
        Minecraft.getMinecraft().getTextureManager().loadTexture(this.texture, newTex);
    }

    public boolean interact(final EntityPlayer player) {
        if (player.isSneaking()) {
            return false;
        }
        final ItemStack itemstack = player.inventory.getCurrentItem();
        if (this.getCore() == -1 && itemstack != null
                && itemstack.getItem() == ConfigItems.itemGolemCore
                && itemstack.getItemDamage() != 100) {
            this.setHomeArea(
                    (int) Math.round(this.posX - 0.5),
                    (int) Math.round(this.posY),
                    (int) Math.round(this.posZ - 0.5),
                    32);
            this.setCore((byte) itemstack.getItemDamage());
            this.setupGolem();
            this.setupGolemInventory();
            --itemstack.stackSize;
            if (itemstack.stackSize <= 0) {
                player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            }
            this.worldObj.playSoundAtEntity(this, "thaumcraft:upgrade", 0.5f, 1.0f);
            player.swingItem();
            this.worldObj.setEntityState(this, (byte) 7);
            return true;
        }
        if (this.getCore() == -1) {
            return false;
        }
        if (itemstack != null && itemstack.getItem() == ConfigItems.itemGolemUpgrade) {
            for (int a = 0; a < this.upgrades.length; ++a) {
                if (this.getUpgrade(a) == -1 && this.getUpgradeAmount(itemstack.getItemDamage()) < 2) {
                    this.setUpgrade(a, (byte) itemstack.getItemDamage());
                    this.setupGolem();
                    this.setupGolemInventory();
                    --itemstack.stackSize;
                    if (itemstack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                    }
                    this.worldObj.playSoundAtEntity(this, "thaumcraft:upgrade", 0.5f, 1.0f);
                    player.swingItem();
                    return true;
                }
            }
            return false;
        }
        return super.interact(player);
    }

    public void die() {
        this.setDead();
        this.spawnExplosionParticle();
        this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "thaumcraft:craftfail", 1.0f, 1.0f);
        if (this.blocky != null && this.blocky != Blocks.air && this.blocky.getMaterial() == Material.tnt) {
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 4.0f, true);
            return;
        }
        if (this.kaboom) {
            this.worldObj.createExplosion(this, this.posX, this.posY, this.posZ, 3.0f, true);
            return;
        }
        if (this.worldObj.isAirBlock(
                (int) Math.round(this.posX - 0.5),
                (int) Math.round(this.posY),
                (int) Math.round(this.posZ - 0.5))) {
            if (this.blocky != null && this.blocky != Blocks.air) {
                this.worldObj.setBlock(
                        (int) Math.round(this.posX - 0.5),
                        (int) Math.round(this.posY),
                        (int) Math.round(this.posZ - 0.5),
                        this.blocky,
                        this.md,
                        3);
                final SimpleNetworkWrapper instance = PacketHandler.INSTANCE;
                final Block blocky = this.blocky;
                instance.sendToAllAround(
                        new PacketFXBlocksplosion(
                                Block.getIdFromBlock(this.blocky),
                                this.md,
                                (int) Math.round(this.posX - 0.5),
                                (int) Math.round(this.posY),
                                (int) Math.round(this.posZ - 0.5)),
                        new NetworkRegistry.TargetPoint(
                                this.worldObj.provider.dimensionId,
                                (int) Math.round(this.posX - 0.5),
                                (int) Math.round(this.posY),
                                (int) Math.round(this.posZ - 0.5),
                                32.0));
            }
            return;
        }
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                if (this.worldObj.isAirBlock(
                        (int) Math.round(this.posX - 0.5) + x,
                        (int) Math.round(this.posY),
                        (int) Math.round(this.posZ - 0.5) + z)) {
                    if (this.blocky != null && this.blocky != Blocks.air) {
                        this.worldObj.setBlock(
                                (int) Math.round(this.posX - 0.5) + x,
                                (int) Math.round(this.posY),
                                (int) Math.round(this.posZ - 0.5) + z,
                                this.blocky,
                                this.md,
                                3);
                        final SimpleNetworkWrapper instance2 = PacketHandler.INSTANCE;
                        final Block blocky2 = this.blocky;
                        instance2.sendToAllAround(
                                new PacketFXBlocksplosion(
                                        Block.getIdFromBlock(this.blocky),
                                        this.md,
                                        (int) Math.round(this.posX - 0.5) + x,
                                        (int) Math.round(this.posY),
                                        (int) Math.round(this.posZ - 0.5) + z),
                                new NetworkRegistry.TargetPoint(
                                        this.worldObj.provider.dimensionId,
                                        (int) Math.round(this.posX - 0.5) + x,
                                        (int) Math.round(this.posY),
                                        (int) Math.round(this.posZ - 0.5) + z,
                                        32.0));
                    }
                    return;
                }
            }
        }
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                if (this.worldObj.isAirBlock(
                        (int) Math.round(this.posX - 0.5) + x,
                        (int) Math.round(this.posY) - 1,
                        (int) Math.round(this.posZ - 0.5) + z)) {
                    if (this.blocky != null && this.blocky != Blocks.air) {
                        this.worldObj.setBlock(
                                (int) Math.round(this.posX - 0.5) + x,
                                (int) Math.round(this.posY) - 1,
                                (int) Math.round(this.posZ - 0.5) + z,
                                this.blocky,
                                this.md,
                                3);
                        final SimpleNetworkWrapper instance3 = PacketHandler.INSTANCE;
                        final Block blocky3 = this.blocky;
                        instance3.sendToAllAround(
                                new PacketFXBlocksplosion(
                                        Block.getIdFromBlock(this.blocky),
                                        this.md,
                                        (int) Math.round(this.posX - 0.5) + x,
                                        (int) Math.round(this.posY) - 1,
                                        (int) Math.round(this.posZ - 0.5) + z),
                                new NetworkRegistry.TargetPoint(
                                        this.worldObj.provider.dimensionId,
                                        (int) Math.round(this.posX - 0.5) + x,
                                        (int) Math.round(this.posY) - 1,
                                        (int) Math.round(this.posZ - 0.5) + z,
                                        32.0));
                    }
                    return;
                }
            }
        }
        for (int x = -1; x < 2; ++x) {
            for (int z = -1; z < 2; ++z) {
                if (this.worldObj.isAirBlock(
                        (int) Math.round(this.posX - 0.5) + x,
                        (int) Math.round(this.posY) + 1,
                        (int) Math.round(this.posZ - 0.5) + z)) {
                    if (this.blocky != null && this.blocky != Blocks.air) {
                        this.worldObj.setBlock(
                                (int) Math.round(this.posX - 0.5) + x,
                                (int) Math.round(this.posY) + 1,
                                (int) Math.round(this.posZ - 0.5) + z,
                                this.blocky,
                                this.md,
                                3);
                        final SimpleNetworkWrapper instance4 = PacketHandler.INSTANCE;
                        final Block blocky4 = this.blocky;
                        instance4.sendToAllAround(
                                new PacketFXBlocksplosion(
                                        Block.getIdFromBlock(this.blocky),
                                        this.md,
                                        (int) Math.round(this.posX - 0.5) + x,
                                        (int) Math.round(this.posY) + 1,
                                        (int) Math.round(this.posZ - 0.5) + z),
                                new NetworkRegistry.TargetPoint(
                                        this.worldObj.provider.dimensionId,
                                        (int) Math.round(this.posX - 0.5) + x,
                                        (int) Math.round(this.posY) + 1,
                                        (int) Math.round(this.posZ - 0.5) + z,
                                        32.0));
                    }
                    return;
                }
            }
        }
    }

    public void writeSpawnData(final ByteBuf data) {
        super.writeSpawnData(data);
        if (this.blocky != null) {
            final Block blocky = this.blocky;
            data.writeInt(Block.getIdFromBlock(this.blocky));
        } else {
            data.writeInt(0);
        }
        data.writeByte(this.md);
        data.writeByte(this.upgrades.length);
        for (final byte b : this.upgrades) {
            data.writeByte(b);
        }
    }

    public void readSpawnData(final ByteBuf data) {
        super.readSpawnData(data);
        this.blocky = Block.getBlockById(data.readInt());
        this.md = data.readByte();
        this.upgrades = new byte[data.readByte()];
        for (int a = 0; a < this.upgrades.length; ++a) {
            this.upgrades[a] = data.readByte();
        }
    }

    public String getCommandSenderName() {
        if (this.hasCustomNameTag()) {
            return this.getCustomNameTag();
        }
        final ItemStack stack = new ItemStack(this.blocky, 1, this.md);
        if (stack.getItem() != null) {
            return stack.getDisplayName() + " Golem";
        }
        if (this.blocky == null || this.blocky == Blocks.air) {
            return "Voidling Golem";
        }
        return this.blocky.getLocalizedName() + " Golem";
    }

    public EnumGolemType getGolemType() {
        return EnumGolemType.WOOD;
    }

    public int getGolemStrength() {
        return this.type.strength + this.getUpgradeAmount(1);
    }

    public boolean attackEntityAsMob(final Entity par1Entity) {
        if (this.blocky == Blocks.web && par1Entity instanceof EntityLivingBase) {
            ((EntityLivingBase) par1Entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 20, 0));
        }
        return super.attackEntityAsMob(par1Entity);
    }

    @SideOnly(Side.CLIENT)
    public void handleHealthUpdate(final byte par1) {
        if (par1 == 4) {
            this.action = 6;
        } else if (par1 == 5) {
            this.healing = 5;
            int bonus = 0;
            try {
                bonus = (this.getGolemDecoration().contains("H") ? 5 : 0);
            } catch (Exception ignored) {}
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(this.type.health + bonus);
        } else if (par1 == 6) {
            this.leftArm = 5;
        } else if (par1 == 8) {
            this.rightArm = 5;
        } else if (par1 == 7) {
            this.bootup = 33.0f;
        } else {
            super.handleHealthUpdate(par1);
        }
    }

    public void onStruckByLightning(final EntityLightningBolt bolt) {
        if (bolt instanceof EntityLightningBoltFinite) {
            return;
        }
        super.onStruckByLightning(bolt);
    }
}
