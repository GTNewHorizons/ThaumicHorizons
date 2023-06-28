//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.tiles;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.INpc;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.IEntityInfusedStats;
import com.kentington.thaumichorizons.common.lib.CreatureInfusionRecipe;
import com.kentington.thaumichorizons.common.lib.EntityInfusionProperties;
import com.kentington.thaumichorizons.common.lib.SelfInfusionRecipe;
import com.kentington.thaumichorizons.common.lib.networking.PacketFXEssentiaBubble;
import com.kentington.thaumichorizons.common.lib.networking.PacketFXInfusionDone;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketInfusionFX;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.visnet.VisNetHandler;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.lib.network.fx.PacketFXBlockZap;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TilePedestal;

public class TileVat extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, ISidedInventory {

    public int mode;
    AspectList myEssentia;
    AspectList essentiaDemanded;
    Aspect currentlySucking;
    public ItemStack sample;
    public ItemStack nutrients;
    public int progress;
    private EntityLivingBase entityContained;
    public final int CLONE_TIME = 800;
    public int[] selfInfusions;
    public float selfInfusionHealth;
    private final ArrayList<ChunkCoordinates> pedestals;
    private final int dangerCount;
    public boolean checkSurroundings;
    public int symmetry;
    public int instability;
    private ArrayList<ItemStack> recipeIngredients;
    private Object recipeOutput;
    private String recipePlayer;
    private String recipeOutputLabel;
    private int recipeInstability;
    private final int recipeXP;
    public int recipeType;
    int itemCount;
    public int count;
    public int craftCount;
    public float startUp;
    private final int countDelay;
    ArrayList<ItemStack> ingredients;
    public HashMap<String, SourceFX> sourceFX;
    private NBTTagCompound entityNBTObj = null;
    private Boolean firstTick = true;

    public TileVat() {
        this.mode = 0;
        this.myEssentia = new AspectList();
        this.essentiaDemanded = new AspectList();
        this.currentlySucking = null;
        this.sample = null;
        this.nutrients = null;
        this.entityContained = null;
        this.selfInfusions = new int[12];
        this.selfInfusionHealth = 20.0f;
        this.pedestals = new ArrayList<>();
        this.dangerCount = 0;
        this.checkSurroundings = true;
        this.symmetry = 0;
        this.instability = 0;
        this.recipeIngredients = null;
        this.recipeOutput = null;
        this.recipePlayer = null;
        this.recipeOutputLabel = "";
        this.recipeInstability = 0;
        this.recipeXP = 0;
        this.recipeType = 0;
        this.itemCount = 0;
        this.count = 0;
        this.craftCount = 0;
        this.countDelay = 10;
        this.ingredients = new ArrayList<>();
        this.sourceFX = new HashMap<>();
    }

    public boolean activate(final EntityPlayer player, final boolean direct) {
        final ItemStack possibleJar = player.getHeldItem();
        if (possibleJar != null && Block.getBlockFromItem(possibleJar.getItem()) == ThaumicHorizons.blockJar
                && possibleJar.hasTagCompound()
                && !possibleJar.stackTagCompound.getBoolean("isSoul")) {
            if (this.mode == 0 && this.getEntityContained() == null
                    && player.inventory.addItemStackToInventory(new ItemStack(ConfigBlocks.blockJar))) {
                this.setEntityContained(
                        (EntityLivingBase) EntityList.createEntityFromNBT(possibleJar.getTagCompound(), this.worldObj));
                --possibleJar.stackSize;
                this.markDirty();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                return true;
            }
        } else if (possibleJar != null && Block.getBlockFromItem(possibleJar.getItem()) == ConfigBlocks.blockJar
                && possibleJar.getItemDamage() == 0
                && this.getEntityContained() != null
                && !(this.getEntityContained() instanceof EntityPlayer)) {
                    if (this.mode == 0) {
                        return this.jarCritter(possibleJar, player);
                    }
                } else
            if (this.mode == 4 && possibleJar != null
                    && Block.getBlockFromItem(possibleJar.getItem()) == ThaumicHorizons.blockJar
                    && possibleJar.hasTagCompound()
                    && possibleJar.stackTagCompound.getBoolean("isSoul")) {
                        if (this.selfInfusions[1] == 0
                                && player.inventory.addItemStackToInventory(new ItemStack(ConfigBlocks.blockJar))) {
                            this.worldObj.playSoundEffect(
                                    this.xCoord + 0.5,
                                    this.yCoord + 0.5,
                                    this.zCoord + 0.5,
                                    "thaumcraft:whispers",
                                    1.0f,
                                    this.worldObj.rand.nextFloat());
                            Thaumcraft.proxy.blockSparkle(
                                    this.worldObj,
                                    this.xCoord,
                                    this.yCoord - 1,
                                    this.zCoord,
                                    16777215,
                                    20);
                            Thaumcraft.proxy.blockSparkle(
                                    this.worldObj,
                                    this.xCoord,
                                    this.yCoord - 2,
                                    this.zCoord,
                                    16777215,
                                    20);
                            final EntityVillager villager = new EntityVillager(this.worldObj);
                            villager.setProfession(possibleJar.getTagCompound().getInteger("villagerType"));
                            this.setEntityContained(villager);
                            this.mode = 0;
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            this.selfInfusions = new int[12];
                            --possibleJar.stackSize;
                        }
                    } else {
                        if (this.mode == 0 && direct && this.getEntityContained() == null) {
                            player.setPositionAndUpdate(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5);
                            this.setEntityContained(player);
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            return true;
                        }
                        if (this.mode == 0 && this.getEntityContained() == player) {
                            if (this.worldObj.getBlock(this.xCoord, this.yCoord + 1, this.zCoord)
                                    == ThaumicHorizons.blockSoulBeacon) {
                                player.setPositionAndUpdate(this.xCoord + 0.5, this.yCoord + 2.0, this.zCoord + 0.5);
                            } else {
                                player.setPositionAndUpdate(this.xCoord + 0.5, this.yCoord + 1.0, this.zCoord + 0.5);
                            }
                            this.setEntityContained(null);
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            return true;
                        }
                        player.openGui(
                                ThaumicHorizons.instance,
                                7,
                                this.worldObj,
                                this.xCoord,
                                this.yCoord,
                                this.zCoord);
                        return true;
                    }
        return false;
    }

    private void loadContainedEntity(NBTTagCompound EntityNBT) {
        if (EntityNBT.getString("id") != null) {
            if (EntityNBT.getString("id").equals("PLAYER")) {
                this.setEntityContained(this.worldObj.getPlayerEntityByName(EntityNBT.getString("playerName")));
            } else if (EntityNBT.hasKey("id")) {
                this.setEntityContained((EntityLivingBase) EntityList.createEntityFromNBT(EntityNBT, this.worldObj));
            }
        }
    }

    public void updateEntity() {
        super.updateEntity();
        if (this.firstTick && this.entityNBTObj != null) {
            loadContainedEntity(entityNBTObj);
            this.firstTick = false;
        }
        if (this.getEntityContained() != null && this.getEntityContained().isBurning()) {
            this.getEntityContained().extinguish();
        }
        if (this.worldObj.isRemote) {
            if (this.mode == 2) {
                this.doEffects();
            } else if (this.mode != 2 && this.startUp > 0.0f) {
                if (this.startUp > 0.0f) {
                    this.startUp -= this.startUp / 10.0f;
                }
                if (this.startUp < 0.001) {
                    this.startUp = 0.0f;
                }
            }
            if (this.mode == 1) {
                Thaumcraft.proxy.blockSparkle(this.worldObj, this.xCoord, this.yCoord - 1, this.zCoord, 14184241, 1);
                Thaumcraft.proxy.blockSparkle(this.worldObj, this.xCoord, this.yCoord - 2, this.zCoord, 14184241, 1);
            }
            return;
        }
        if (this.mode == 0) {
            this.essentiaDemanded = new AspectList();
            if (this.getEntityContained() != null) {
                if (this.getEntityContained().getHealth() < this.getEntityContained().getMaxHealth()) {
                    if (this.getEntityContained().getCreatureAttribute() != EnumCreatureAttribute.UNDEAD) {
                        if (this.myEssentia.getAmount(Aspect.HEAL) > 0 && this.progress <= 0) {
                            this.getEntityContained().heal(8.0f);
                            this.myEssentia.remove(Aspect.HEAL, 1);
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            this.progress += 40;
                        }
                        if (this.getEntityContained().getHealth() < this.getEntityContained().getMaxHealth()
                                && this.essentiaDemanded.getAmount(Aspect.HEAL) < 1) {
                            this.essentiaDemanded.add(Aspect.HEAL, 1);
                        }
                        if (this.myEssentia.getAmount(Aspect.LIFE) > 0 && this.progress <= 0) {
                            this.getEntityContained().heal(4.0f);
                            this.myEssentia.remove(Aspect.LIFE, 1);
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            this.progress += 50;
                        }
                        if (this.getEntityContained().getHealth() < this.getEntityContained().getMaxHealth()
                                && this.essentiaDemanded.getAmount(Aspect.LIFE) < 1) {
                            this.essentiaDemanded.add(Aspect.LIFE, 1);
                        }
                    } else {
                        if (this.myEssentia.getAmount(Aspect.UNDEAD) > 0 && this.progress <= 0) {
                            this.getEntityContained().heal(8.0f);
                            this.myEssentia.remove(Aspect.UNDEAD, 1);
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            this.progress += 40;
                        }
                        if (this.getEntityContained().getHealth() < this.getEntityContained().getMaxHealth()
                                && this.essentiaDemanded.getAmount(Aspect.UNDEAD) < 1) {
                            this.essentiaDemanded.add(Aspect.UNDEAD, 1);
                        }
                        if (this.myEssentia.getAmount(Aspect.DEATH) > 0 && this.progress <= 0) {
                            this.getEntityContained().heal(4.0f);
                            this.myEssentia.remove(Aspect.DEATH, 1);
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            this.progress += 50;
                        }
                        if (this.getEntityContained().getHealth() < this.getEntityContained().getMaxHealth()
                                && this.essentiaDemanded.getAmount(Aspect.DEATH) < 1) {
                            this.essentiaDemanded.add(Aspect.DEATH, 1);
                        }
                    }
                }
                if (this.hasNegativeEffect(this.getEntityContained())) {
                    if (this.getEntityContained().getCreatureAttribute() != EnumCreatureAttribute.UNDEAD) {
                        if (this.myEssentia.getAmount(Aspect.HEAL) > 0 && this.progress <= 0) {
                            this.removeNegativeEffects(this.getEntityContained());
                            this.myEssentia.remove(Aspect.HEAL, 1);
                            this.markDirty();
                            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                            this.progress += 50;
                        } else if (this.essentiaDemanded.getAmount(Aspect.HEAL) < 1) {
                            this.essentiaDemanded.add(Aspect.HEAL, 1);
                        }
                    } else if (this.myEssentia.getAmount(Aspect.UNDEAD) > 0 && this.progress <= 0) {
                        this.removeNegativeEffects(this.getEntityContained());
                        this.myEssentia.remove(Aspect.UNDEAD, 1);
                        this.markDirty();
                        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                        this.progress += 50;
                    } else if (this.essentiaDemanded.getAmount(Aspect.UNDEAD) < 1) {
                        this.essentiaDemanded.add(Aspect.UNDEAD, 1);
                    }
                }
                if (this.getEntityContained() instanceof EntityPlayer
                        && ((EntityPlayer) this.getEntityContained()).getFoodStats().needFood()) {
                    if (this.myEssentia.getAmount(Aspect.HUNGER) > 0 && this.progress <= 0) {
                        ((EntityPlayer) this.getEntityContained()).getFoodStats().addStats(4, 2.0f);
                        this.markDirty();
                        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                        this.progress += 50;
                    }
                    if (((EntityPlayer) this.getEntityContained()).getFoodStats().needFood()
                            && this.essentiaDemanded.getAmount(Aspect.HUNGER) < 1) {
                        this.essentiaDemanded.add(Aspect.HUNGER, 1);
                    }
                }
            } else if (this.sample != null && this.sample.getItem() == ThaumicHorizons.itemCorpseEffigy) {
                this.mode = 3;
                this.essentiaDemanded = new AspectList().add(Aspect.LIFE, 8).add(Aspect.HEAL, 8);
                this.progress = 80;
                this.markDirty();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            } else if (this.sample != null && this.nutrients != null) {
                this.mode = 1;
                this.essentiaDemanded = new AspectList().add(Aspect.LIFE, 4);
                if (this.sample.getItem() == ThaumicHorizons.itemSyringeBloodSample && this.sample.hasTagCompound()
                        && this.sample.stackTagCompound.getCompoundTag("critter") != null
                        && this.sample.stackTagCompound.getCompoundTag("critter").getCompoundTag("ForgeData") != null) {
                    final NBTTagCompound tlist = this.sample.stackTagCompound.getCompoundTag("critter")
                            .getCompoundTag("CreatureInfusion").getCompoundTag("InfusionCosts");
                    if (tlist != null && tlist.hasKey("Aspects")) {
                        final NBTTagList aspex = tlist.getTagList("Aspects", 10);
                        for (int j = 0; j < aspex.tagCount(); ++j) {
                            final NBTTagCompound rs = aspex.getCompoundTagAt(j);
                            if (rs.hasKey("key")) {
                                this.essentiaDemanded
                                        .add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
                            }
                        }
                    }
                }
                this.progress = 40;
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.markDirty();
            }
        } else if (this.mode == 1) {
            if (this.sample == null && this.getEntityContained() == null) {
                this.progress = 0;
                this.mode = 0;
                this.essentiaDemanded = new AspectList();
                this.myEssentia = new AspectList();
                return;
            }
            if (this.getEntityContained() == null && this.myEssentia.getAmount(Aspect.LIFE) >= 4) {
                if (this.sample.getItem() == ThaumicHorizons.itemSyringeBloodSample) {
                    this.setEntityContained(
                            (EntityLivingBase) EntityList.createEntityFromNBT(
                                    this.sample.getTagCompound().getCompoundTag("critter"),
                                    this.worldObj));
                    if (this.getEntityContained() instanceof EntityTameable) {
                        ((EntityTameable) this.getEntityContained()).setTamed(false);
                    }
                } else {
                    this.setEntityContained(
                            (EntityLivingBase) EntityList.createEntityByID(
                                    ThaumicHorizons.incarnationItems.get(this.sample.getItem()),
                                    this.worldObj));
                }
                final ItemStack sample = this.sample;
                --sample.stackSize;
                if (this.sample.stackSize <= 0) {
                    this.sample = null;
                }
                final ItemStack nutrients = this.nutrients;
                --nutrients.stackSize;
                if (this.nutrients.stackSize <= 0) {
                    this.nutrients = null;
                }
                this.progress = 800;
                this.essentiaDemanded = new AspectList();
                this.myEssentia = new AspectList();
                if (this.getEntityContained() == null) {
                    this.progress = 0;
                    this.mode = 0;
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                    this.markDirty();
                    return;
                }
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.markDirty();
            } else if (this.progress <= 0) {
                this.mode = 0;
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.markDirty();
            }
        } else if (this.mode == 2) {
            ++this.count;
            if (this.checkSurroundings) {
                this.checkSurroundings = false;
                this.getSurroundings();
            } else if (this.count % this.countDelay == 0) {
                this.craftCycle();
                this.markDirty();
            }
        } else if (this.mode == 3) {
            if (this.sample == null || this.sample.getItem() != ThaumicHorizons.itemCorpseEffigy) {
                this.progress = 0;
                this.mode = 0;
                this.essentiaDemanded = new AspectList();
                this.myEssentia = new AspectList();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.markDirty();
                return;
            }
            if (this.progress <= 0 && this.myEssentia.getAmount(Aspect.LIFE) >= 8
                    && this.myEssentia.getAmount(Aspect.HEAL) >= 8) {
                this.worldObj.playSoundEffect(
                        this.xCoord + 0.5,
                        this.yCoord + 0.5,
                        this.zCoord + 0.5,
                        "thaumcraft:wand",
                        1.0f,
                        this.worldObj.rand.nextFloat());
                Thaumcraft.proxy.blockSparkle(this.worldObj, this.xCoord, this.yCoord - 2, this.zCoord, 16720418, 20);
                Thaumcraft.proxy.blockSparkle(this.worldObj, this.xCoord, this.yCoord - 1, this.zCoord, 16720418, 20);
                this.mode = 4;
                this.selfInfusionHealth = 20.0f;
                this.sample = null;
                this.essentiaDemanded = new AspectList();
                this.myEssentia = new AspectList();
                this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                this.markDirty();
            }
        }
        if (this.mode != 2 && this.needsEssentia()) {
            this.tryDrawAllEssentia();
        }
        if (this.progress > 0) {
            --this.progress;
            this.markDirty();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        }
    }

    boolean needsEssentia() {
        this.currentlySucking = null;
        if (this.progress > 0) {
            return false;
        }
        for (final Aspect asp : this.essentiaDemanded.getAspects()) {
            if (this.myEssentia.getAmount(asp) < this.essentiaDemanded.getAmount(asp)) {
                this.currentlySucking = asp;
                break;
            }
        }
        return this.currentlySucking != null;
    }

    boolean tryDrawAllEssentia() {
        boolean drew = false;
        TileEntity conn = this.worldObj.getTileEntity(this.xCoord - 1, this.yCoord - 3, this.zCoord);
        if (conn instanceof TileVatConnector) {
            drew |= this.tryDrawEssentia((TileVatConnector) conn);
        }
        conn = this.worldObj.getTileEntity(this.xCoord + 1, this.yCoord - 3, this.zCoord);
        if (conn instanceof TileVatConnector) {
            drew |= this.tryDrawEssentia((TileVatConnector) conn);
        }
        conn = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 3, this.zCoord - 1);
        if (conn instanceof TileVatConnector) {
            drew |= this.tryDrawEssentia((TileVatConnector) conn);
        }
        conn = this.worldObj.getTileEntity(this.xCoord, this.yCoord - 3, this.zCoord + 1);
        if (conn instanceof TileVatConnector) {
            drew |= this.tryDrawEssentia((TileVatConnector) conn);
        }
        return drew;
    }

    boolean tryDrawEssentia(final TileVatConnector conn) {
        TileEntity te = null;
        IEssentiaTransport ic = null;
        for (final ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
            te = ThaumcraftApiHelper.getConnectableTile(this.worldObj, conn.xCoord, conn.yCoord, conn.zCoord, dir);
            if (te != null) {
                ic = (IEssentiaTransport) te;
                if (ic.getEssentiaAmount(dir.getOpposite()) > 0
                        && ic.getSuctionAmount(dir.getOpposite()) < this.getSuctionAmount(null)
                        && this.getSuctionAmount(null) >= ic.getMinimumSuction()) {
                    for (final Aspect asp : this.essentiaDemanded.getAspects()) {
                        if (this.mode == 2 || this.myEssentia.getAmount(asp) < this.essentiaDemanded.getAmount(asp)) {
                            final int ess = ic.takeEssentia(asp, 1, dir.getOpposite());
                            if (ess > 0) {
                                this.addToContainer(asp, ess);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    boolean hasNegativeEffect(final EntityLivingBase ent) {
        return ent.getActivePotionEffect(Potion.poison) != null || ent.getActivePotionEffect(Potion.blindness) != null
                || ent.getActivePotionEffect(Potion.hunger) != null
                || ent.getActivePotionEffect(Potion.weakness) != null
                || ent.getActivePotionEffect(Potion.wither) != null
                || ent.getActivePotionEffect(Potion.confusion) != null
                || ent.getActivePotionEffect(Potion.digSlowdown) != null
                || ent.getActivePotionEffect(Potion.moveSlowdown) != null
                || ent.getActivePotionEffect(Potion.potionTypes[Config.potionBlurredID]) != null
                || ent.getActivePotionEffect(Potion.potionTypes[Config.potionInfVisExhaustID]) != null
                || ent.getActivePotionEffect(Potion.potionTypes[Config.potionTaintPoisonID]) != null
                || ent.getActivePotionEffect(Potion.potionTypes[Config.potionThaumarhiaID]) != null
                || ent.getActivePotionEffect(Potion.potionTypes[Config.potionVisExhaustID]) != null;
    }

    void removeNegativeEffects(final EntityLivingBase ent) {
        ent.removePotionEffect(Config.potionBlurredID);
        ent.removePotionEffect(Config.potionInfVisExhaustID);
        ent.removePotionEffect(Config.potionTaintPoisonID);
        ent.removePotionEffect(Config.potionThaumarhiaID);
        ent.removePotionEffect(Config.potionVisExhaustID);
        ent.removePotionEffect(Potion.blindness.id);
        ent.removePotionEffect(Potion.confusion.id);
        ent.removePotionEffect(Potion.digSlowdown.id);
        ent.removePotionEffect(Potion.hunger.id);
        ent.removePotionEffect(Potion.moveSlowdown.id);
        ent.removePotionEffect(Potion.hunger.id);
        ent.removePotionEffect(Potion.poison.id);
        ent.removePotionEffect(Potion.weakness.id);
        ent.removePotionEffect(Potion.wither.id);
    }

    public boolean jarCritter(final ItemStack possibleJar, final EntityPlayer player) {
        final ItemStack jar = new ItemStack(ThaumicHorizons.blockJar);
        final NBTTagCompound entityData = new NBTTagCompound();
        entityData.setString("id", EntityList.getEntityString(this.getEntityContained()));
        this.getEntityContained().writeToNBT(entityData);
        jar.setTagCompound(entityData);
        jar.getTagCompound().setString("jarredCritterName", this.getEntityContained().getCommandSenderName());
        jar.getTagCompound().setBoolean("isSoul", false);
        if (player.inventory.addItemStackToInventory(jar)) {
            --possibleJar.stackSize;
            this.setEntityContained(null);
            this.markDirty();
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            return true;
        }
        return false;
    }

    public EntityLivingBase getEntity() {
        return this.getEntityContained();
    }

    public void startInfusion(final EntityPlayer player) {
        this.getSurroundings();
        final ArrayList<ItemStack> components = new ArrayList<>();
        for (final ChunkCoordinates cc : this.pedestals) {
            final TileEntity te = this.worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
            if (te instanceof final TilePedestal ped) {
                if (ped.getStackInSlot(0) == null) {
                    continue;
                }
                components.add(ped.getStackInSlot(0).copy());
            }
        }
        if (components.size() == 0) {
            return;
        }
        if (this.mode != 4) {
            final CreatureInfusionRecipe recipe = ThaumicHorizons
                    .getCreatureInfusion(this.getEntityContained(), components, player);
            if (recipe == null || (recipe.getID(null) != 0
                    && ((EntityInfusionProperties) this.getEntityContained().getExtendedProperties("CreatureInfusion"))
                            .hasInfusion(recipe.getID(null)))) {
                return;
            }
            if (recipe.getRecipeOutput() instanceof NBTTagCompound
                    && ((NBTTagCompound) recipe.getRecipeOutput()).getInteger("instilledLoyalty") != 0
                    && ((EntityLiving) this.entityContained).tasks.taskEntries.size() == 0) {
                return;
            }
            this.recipeType = 0;
            this.recipeIngredients = new ArrayList<>();
            for (final ItemStack ing : recipe.getComponents()) {
                this.recipeIngredients.add(ing.copy());
            }
            if (recipe.getRecipeOutput(this.getEntityContained().getClass()) instanceof final Object[] obj) {
                this.recipeOutputLabel = (String) obj[0];
                this.recipeOutput = obj[1];
            } else {
                this.recipeOutput = recipe.getRecipeOutput(this.getEntityContained().getClass());
            }
            this.recipeInstability = recipe.getInstability(this.getEntityContained().getClass());
            this.essentiaDemanded = recipe.getAspects(this.getEntityContained().getClass()).copy();
            this.myEssentia = recipe.getAspects(this.getEntityContained().getClass()).copy();
        } else {
            final SelfInfusionRecipe recipe2 = ThaumicHorizons.getSelfInfusion(components, player);
            if (recipe2 == null) {
                return;
            }
            for (int selfInfusion : this.selfInfusions) {
                if (selfInfusion == recipe2.getID()) {
                    return;
                }
            }
            this.recipeType = 1;
            this.recipeIngredients = new ArrayList<>();
            for (final ItemStack ing : recipe2.getComponents()) {
                this.recipeIngredients.add(ing.copy());
            }
            this.recipeOutputLabel = "";
            this.recipeOutput = recipe2.getID();
            this.recipeInstability = recipe2.getInstability();
            this.myEssentia = recipe2.getAspects().copy();
            this.essentiaDemanded = recipe2.getAspects().copy();
        }
        this.recipePlayer = player.getCommandSenderName();
        this.instability = this.symmetry + this.recipeInstability;
        this.mode = 2;
        this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "thaumcraft:craftstart", 0.5f, 1.0f);
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }

    public boolean validLocation() {
        return true;
    }

    private void getSurroundings() {
        final ArrayList<ChunkCoordinates> stuff = new ArrayList<>();
        this.pedestals.clear();
        try {
            for (int xx = -12; xx <= 12; ++xx) {
                for (int zz = -12; zz <= 12; ++zz) {
                    boolean skip = false;
                    for (int yy = -5; yy <= 10; ++yy) {
                        if (xx != 0 || zz != 0) {
                            final int x = this.xCoord + xx;
                            final int y = this.yCoord - yy;
                            final int z = this.zCoord + zz;
                            final TileEntity te = this.worldObj.getTileEntity(x, y, z);
                            if (!skip && yy > 0
                                    && Math.abs(xx) <= 8
                                    && Math.abs(zz) <= 8
                                    && te instanceof TilePedestal) {
                                this.pedestals.add(new ChunkCoordinates(x, y, z));
                                skip = true;
                            } else {
                                final Block bi = this.worldObj.getBlock(x, y, z);
                                if (bi == Blocks.skull
                                        || (bi instanceof IInfusionStabiliser && ((IInfusionStabiliser) bi)
                                                .canStabaliseInfusion(this.getWorldObj(), x, y, z))) {
                                    stuff.add(new ChunkCoordinates(x, y, z));
                                }
                            }
                        }
                    }
                }
            }
            this.symmetry = 0;
            for (final ChunkCoordinates cc : this.pedestals) {
                boolean items = false;
                final int x2 = this.xCoord - cc.posX;
                final int z2 = this.zCoord - cc.posZ;
                TileEntity te2 = this.worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
                if (te2 instanceof TilePedestal) {
                    this.symmetry += 2;
                    if (((TilePedestal) te2).getStackInSlot(0) != null) {
                        ++this.symmetry;
                        items = true;
                    }
                }
                final int xx2 = this.xCoord + x2;
                final int zz2 = this.zCoord + z2;
                te2 = this.worldObj.getTileEntity(xx2, cc.posY, zz2);
                if (te2 instanceof TilePedestal) {
                    this.symmetry -= 2;
                    if (((TilePedestal) te2).getStackInSlot(0) == null || !items) {
                        continue;
                    }
                    --this.symmetry;
                }
            }
            float sym = 0.0f;
            for (final ChunkCoordinates cc2 : stuff) {
                final boolean items2 = false;
                final int x = this.xCoord - cc2.posX;
                final int z3 = this.zCoord - cc2.posZ;
                Block bi2 = this.worldObj.getBlock(cc2.posX, cc2.posY, cc2.posZ);
                if (bi2 == Blocks.skull || (bi2 instanceof IInfusionStabiliser && ((IInfusionStabiliser) bi2)
                        .canStabaliseInfusion(this.getWorldObj(), cc2.posX, cc2.posY, cc2.posZ))) {
                    sym += 0.1f;
                }
                final int xx3 = this.xCoord + x;
                final int zz3 = this.zCoord + z3;
                bi2 = this.worldObj.getBlock(xx3, cc2.posY, zz3);
                if (bi2 == Blocks.skull || (bi2 instanceof IInfusionStabiliser && ((IInfusionStabiliser) bi2)
                        .canStabaliseInfusion(this.getWorldObj(), cc2.posX, cc2.posY, cc2.posZ))) {
                    sym -= 0.2f;
                }
            }
            this.symmetry += (int) sym;
        } catch (Exception ignored) {}
    }

    private void doEffects() {
        if (this.mode == 2) {
            if (this.craftCount == 0) {
                this.worldObj
                        .playSound(this.xCoord, this.yCoord, this.zCoord, "thaumcraft:infuserstart", 0.5f, 1.0f, false);
            } else if (this.craftCount % 65 == 0) {
                this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "thaumcraft:infuser", 0.5f, 1.0f, false);
            }
            ++this.craftCount;
            Thaumcraft.proxy.blockRunes(
                    this.worldObj,
                    this.xCoord,
                    this.yCoord - 2,
                    this.zCoord,
                    0.5f + this.worldObj.rand.nextFloat() * 0.2f,
                    0.1f,
                    0.7f + this.worldObj.rand.nextFloat() * 0.3f,
                    25,
                    -0.03f);
        } else if (this.craftCount > 0) {
            this.craftCount -= 2;
            if (this.craftCount < 0) {
                this.craftCount = 0;
            }
            if (this.craftCount > 50) {
                this.craftCount = 50;
            }
        }
        if (this.mode == 2 && this.startUp != 1.0f) {
            if (this.startUp < 1.0f) {
                this.startUp += Math.max(this.startUp / 10.0f, 0.001f);
            }
            if (this.startUp > 0.999) {
                this.startUp = 1.0f;
            }
        }
        for (final String fxk : this.sourceFX.keySet().toArray(new String[0])) {
            final SourceFX fx = this.sourceFX.get(fxk);
            if (fx.ticks <= 0) {
                this.sourceFX.remove(fxk);
            } else {
                if (fx.loc.posX == this.xCoord && fx.loc.posY == this.yCoord && fx.loc.posZ == this.zCoord) {
                    final Entity player = this.worldObj.getEntityByID(fx.color);
                    if (player != null) {
                        for (int a = 0; a < Thaumcraft.proxy.particleCount(2); ++a) {
                            Thaumcraft.proxy.drawInfusionParticles4(
                                    this.worldObj,
                                    player.posX + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat())
                                            * player.width,
                                    player.boundingBox.minY + this.worldObj.rand.nextFloat() * player.height,
                                    player.posZ + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat())
                                            * player.width,
                                    this.xCoord,
                                    this.yCoord,
                                    this.zCoord);
                        }
                    }
                } else {
                    final TileEntity tile = this.worldObj.getTileEntity(fx.loc.posX, fx.loc.posY, fx.loc.posZ);
                    if (tile instanceof TilePedestal) {
                        final ItemStack is = ((TilePedestal) tile).getStackInSlot(0);
                        if (is != null) {
                            if (this.worldObj.rand.nextInt(3) == 0) {
                                Thaumcraft.proxy.drawInfusionParticles3(
                                        this.worldObj,
                                        fx.loc.posX + this.worldObj.rand.nextFloat(),
                                        fx.loc.posY + this.worldObj.rand.nextFloat() + 1.0f,
                                        fx.loc.posZ + this.worldObj.rand.nextFloat(),
                                        this.xCoord,
                                        this.yCoord,
                                        this.zCoord);
                            } else {
                                final Item bi = is.getItem();
                                final int md = is.getItemDamage();
                                if (is.getItemSpriteNumber() == 0 && bi instanceof ItemBlock) {
                                    for (int a2 = 0; a2 < Thaumcraft.proxy.particleCount(2); ++a2) {
                                        Thaumcraft.proxy.drawInfusionParticles2(
                                                this.worldObj,
                                                fx.loc.posX + this.worldObj.rand.nextFloat(),
                                                fx.loc.posY + this.worldObj.rand.nextFloat() + 1.0f,
                                                fx.loc.posZ + this.worldObj.rand.nextFloat(),
                                                this.xCoord,
                                                this.yCoord,
                                                this.zCoord,
                                                Block.getBlockFromItem(bi),
                                                md);
                                    }
                                } else {
                                    for (int a2 = 0; a2 < Thaumcraft.proxy.particleCount(2); ++a2) {
                                        Thaumcraft.proxy.drawInfusionParticles1(
                                                this.worldObj,
                                                fx.loc.posX + 0.4f + this.worldObj.rand.nextFloat() * 0.2f,
                                                fx.loc.posY + 1.23f + this.worldObj.rand.nextFloat() * 0.2f,
                                                fx.loc.posZ + 0.4f + this.worldObj.rand.nextFloat() * 0.2f,
                                                this.xCoord,
                                                this.yCoord,
                                                this.zCoord,
                                                bi,
                                                md);
                                    }
                                }
                            }
                        }
                    } else {
                        fx.ticks = 0;
                    }
                }
                --fx.ticks;
                this.sourceFX.put(fxk, fx);
            }
        }
        if (this.mode == 2 && this.instability > 0 && this.worldObj.rand.nextInt(200) <= this.instability) {
            Thaumcraft.proxy.nodeBolt(
                    this.worldObj,
                    this.xCoord + 0.5f,
                    this.yCoord + 0.5f,
                    this.zCoord + 0.5f,
                    this.xCoord + 0.5f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 2.0f,
                    this.yCoord + 0.5f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 2.0f,
                    this.zCoord + 0.5f + (this.worldObj.rand.nextFloat() - this.worldObj.rand.nextFloat()) * 2.0f);
        }
    }

    public void craftCycle() {
        if (this.instability > 0 && this.worldObj.rand.nextInt(500) <= this.instability) {
            switch (this.worldObj.rand.nextInt(21)) {
                case 0, 2, 10, 13 -> {
                    this.inEvEjectItem(0);
                }
                case 6, 17 -> {
                    this.inEvEjectItem(1);
                }
                case 1, 11 -> {
                    this.inEvEjectItem(2);
                }
                case 3, 8, 14 -> {
                    this.inEvZap(false);
                }
                case 5, 16 -> {
                    this.inEvHarm(false);
                }
                case 12 -> {
                    this.inEvZap(true);
                }
                case 19 -> {
                    this.inEvEjectItem(3);
                }
                case 7 -> {
                    this.inEvEjectItem(4);
                }
                case 4, 15 -> {
                    this.inEvEjectItem(5);
                }
                case 18 -> {
                    this.inEvHarm(true);
                }
                case 9 -> {
                    this.worldObj.createExplosion(
                            null,
                            this.xCoord + 0.5f,
                            this.yCoord + 0.5f,
                            this.zCoord + 0.5f,
                            1.5f + this.worldObj.rand.nextFloat(),
                            false);
                }
                case 20 -> {
                    this.inEvWarp();
                }
            }
        }
        if (this.instability > 0 && this.entityContained != null) {
            float visDrawn = 999.0f;
            if (!this.worldObj.isRemote) {
                float temp = VisNetHandler
                        .drainVis(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, Aspect.EARTH, 100);
                if (temp < visDrawn) {
                    visDrawn = temp;
                }
                temp = VisNetHandler
                        .drainVis(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, Aspect.WATER, 100);
                if (temp < visDrawn) {
                    visDrawn = temp;
                }
                temp = VisNetHandler
                        .drainVis(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, Aspect.ORDER, 100);
                if (temp < visDrawn) {
                    visDrawn = temp;
                }
            }
            this.getEntityContained()
                    .setHealth(this.getEntityContained().getHealth() - this.instability / 10.0f / (5.0f + visDrawn));
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            if (this.getEntityContained().getHealth() <= 0.0f) {
                this.killSubject();
                return;
            }
        } else if (this.instability > 0) {
            float visDrawn = 999.0f;
            if (!this.worldObj.isRemote) {
                float temp = VisNetHandler
                        .drainVis(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, Aspect.EARTH, 100);
                if (temp < visDrawn) {
                    visDrawn = temp;
                }
                temp = VisNetHandler
                        .drainVis(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, Aspect.WATER, 100);
                if (temp < visDrawn) {
                    visDrawn = temp;
                }
                temp = VisNetHandler
                        .drainVis(this.worldObj, this.xCoord, this.yCoord + 1, this.zCoord, Aspect.ORDER, 100);
                if (temp < visDrawn) {
                    visDrawn = temp;
                }
            }
            this.selfInfusionHealth -= this.instability / 10.0f / (5.0f + visDrawn);
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            if (this.selfInfusionHealth <= 0.0f) {
                this.killSubject();
                return;
            }
        }
        if (this.essentiaDemanded.visSize() > 0) {
            final Aspect[] aspects = this.essentiaDemanded.getAspects();
            final int length = aspects.length;
            int i = 0;
            while (i < length) {
                final Aspect aspect = aspects[i];
                if (this.essentiaDemanded.getAmount(aspect) > 0) {
                    this.currentlySucking = aspect;
                    if (this.tryDrawAllEssentia()) {
                        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                        this.markDirty();
                        return;
                    }
                    if (this.worldObj.rand.nextInt(100 - this.recipeInstability * 3) == 0) {
                        ++this.instability;
                    }
                    if (this.instability > 25) {
                        this.instability = 25;
                    }
                    this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                    this.markDirty();
                    break;
                } else {
                    ++i;
                }
            }
            this.checkSurroundings = true;
            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.markDirty();
            return;
        }
        if (this.recipeIngredients.size() > 0) {
            for (int a = 0; a < this.recipeIngredients.size(); ++a) {
                for (final ChunkCoordinates cc : this.pedestals) {
                    final TileEntity te = this.worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
                    if (te instanceof TilePedestal && ((TilePedestal) te).getStackInSlot(0) != null
                            && InfusionRecipe.areItemStacksEqual(
                                    ((TilePedestal) te).getStackInSlot(0),
                                    this.recipeIngredients.get(a),
                                    true)) {
                        if (this.itemCount == 0) {
                            this.itemCount = 5;
                            PacketHandler.INSTANCE.sendToAllAround(
                                    new PacketInfusionFX(
                                            this.xCoord,
                                            this.yCoord - 2,
                                            this.zCoord,
                                            (byte) (this.xCoord - cc.posX),
                                            (byte) (this.yCoord - cc.posY - 2),
                                            (byte) (this.zCoord - cc.posZ),
                                            0),
                                    new NetworkRegistry.TargetPoint(
                                            this.getWorldObj().provider.dimensionId,
                                            this.xCoord,
                                            this.yCoord,
                                            this.zCoord,
                                            32.0));
                        } else if (this.itemCount-- <= 1) {
                            final ItemStack is = ((TilePedestal) te).getStackInSlot(0).getItem()
                                    .getContainerItem(((TilePedestal) te).getStackInSlot(0));
                            ((TilePedestal) te).setInventorySlotContents(0, (is == null) ? null : is.copy());
                            this.recipeIngredients.remove(a);
                        }
                        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
                        this.markDirty();
                        return;
                    }
                }
            }
            return;
        }
        this.instability = 0;
        this.craftingFinish(this.recipeOutput, this.recipeOutputLabel);
        this.recipeOutput = null;
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }

    private void inEvZap(final boolean all) {
        final List<Entity> targets = (List<Entity>) this.worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this.xCoord + 1,
                        this.yCoord + 1,
                        this.zCoord + 1).expand(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (final Entity target : targets) {
                thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendToAllAround(
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
                target.attackEntityFrom(DamageSource.magic, (float) (4 + this.worldObj.rand.nextInt(4)));
                if (!all) {
                    break;
                }
            }
        }
    }

    private void inEvHarm(final boolean all) {
        final List<EntityLivingBase> targets = (List<EntityLivingBase>) this.worldObj.getEntitiesWithinAABB(
                EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this.xCoord + 1,
                        this.yCoord + 1,
                        this.zCoord + 1).expand(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (final EntityLivingBase target : targets) {
                if (this.worldObj.rand.nextBoolean()) {
                    target.addPotionEffect(new PotionEffect(Config.potionTaintPoisonID, 120, 0, false));
                } else {
                    final PotionEffect pe = new PotionEffect(Config.potionVisExhaustID, 2400, 0, true);
                    pe.getCurativeItems().clear();
                    target.addPotionEffect(pe);
                }
                if (!all) {
                    break;
                }
            }
        }
    }

    private void inEvWarp() {
        final List<EntityPlayer> targets = (List<EntityPlayer>) this.worldObj.getEntitiesWithinAABB(
                EntityPlayer.class,
                AxisAlignedBB.getBoundingBox(
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        this.xCoord + 1,
                        this.yCoord + 1,
                        this.zCoord + 1).expand(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            final EntityPlayer target = targets.get(this.worldObj.rand.nextInt(targets.size()));
            if (this.worldObj.rand.nextFloat() < 0.25f) {
                Thaumcraft.addStickyWarpToPlayer(target, 1);
            } else {
                Thaumcraft.addWarpToPlayer(target, 1 + this.worldObj.rand.nextInt(5), true);
            }
        }
    }

    private void inEvEjectItem(final int type) {
        for (int q = 0; q < 50 && this.pedestals.size() > 0; ++q) {
            final ChunkCoordinates cc = this.pedestals.get(this.worldObj.rand.nextInt(this.pedestals.size()));
            final TileEntity te = this.worldObj.getTileEntity(cc.posX, cc.posY, cc.posZ);
            if (te instanceof TilePedestal && ((TilePedestal) te).getStackInSlot(0) != null) {
                if (type < 3 || type == 5) {
                    InventoryUtils.dropItems(this.worldObj, cc.posX, cc.posY, cc.posZ);
                } else {
                    ((TilePedestal) te).setInventorySlotContents(0, null);
                }
                if (type == 1 || type == 3) {
                    this.worldObj.setBlock(cc.posX, cc.posY + 1, cc.posZ, ConfigBlocks.blockFluxGoo, 7, 3);
                    this.worldObj.playSoundEffect(cc.posX, cc.posY, cc.posZ, "game.neutral.swim", 0.3f, 1.0f);
                } else if (type == 2 || type == 4) {
                    this.worldObj.setBlock(cc.posX, cc.posY + 1, cc.posZ, ConfigBlocks.blockFluxGas, 7, 3);
                    this.worldObj.playSoundEffect(cc.posX, cc.posY, cc.posZ, "random.fizz", 0.3f, 1.0f);
                } else if (type == 5) {
                    this.worldObj.createExplosion(null, cc.posX + 0.5f, cc.posY + 0.5f, cc.posZ + 0.5f, 1.0f, false);
                }
                this.worldObj.addBlockEvent(cc.posX, cc.posY, cc.posZ, ConfigBlocks.blockStoneDevice, 11, 0);
                thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendToAllAround(
                        new PacketFXBlockZap(
                                this.xCoord + 0.5f,
                                this.yCoord + 0.5f,
                                this.zCoord + 0.5f,
                                cc.posX + 0.5f,
                                cc.posY + 1.5f,
                                cc.posZ + 0.5f),
                        new NetworkRegistry.TargetPoint(
                                this.worldObj.provider.dimensionId,
                                this.xCoord,
                                this.yCoord,
                                this.zCoord,
                                32.0));
                return;
            }
        }
    }

    public void craftingFinish(final Object out, final String label) {
        if (this.recipeType == 0) {
            if (out instanceof Integer) {
                EntityLivingBase created = null;
                if ((Integer) out < 0) {
                    created = (EntityLivingBase) EntityList.createEntityByID(-(Integer) out, this.worldObj);
                }
                final ModContainer mc = Loader.instance().getIndexedModList().get("ThaumicHorizons");
                try {
                    created = (EntityLivingBase) EntityRegistry.instance().lookupModSpawn(mc, (Integer) out)
                            .getEntityClass().getConstructor(World.class).newInstance(this.worldObj);
                } catch (InvocationTargetException e) {
                    e.getCause().printStackTrace();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
                created.copyLocationAndAnglesFrom(this.getEntityContained());
                created.copyDataFrom(this.getEntityContained(), true);
                if (created instanceof IEntityInfusedStats) {
                    ((IEntityInfusedStats) created).resetStats();
                }
                this.setEntityContained(created);
            } else if (out instanceof NBTBase) {
                final NBTTagCompound tagMods = (NBTTagCompound) out;
                final Multimap map = HashMultimap.create();
                if (tagMods.getDouble("generic.movementSpeed") > 0.0) {
                    map.put(
                            "generic.movementSpeed",
                            new AttributeModifier(
                                    "generic.movementSpeed",
                                    tagMods.getDouble("generic.movementSpeed") / 10.0,
                                    1));
                }
                if (tagMods.getDouble("generic.maxHealth") > 0.0) {
                    map.put(
                            "generic.maxHealth",
                            new AttributeModifier("generic.maxHealth", tagMods.getDouble("generic.maxHealth"), 1));
                }
                if (tagMods.getDouble("generic.attackDamage") > 0.0) {
                    map.put(
                            "generic.attackDamage",
                            new AttributeModifier(
                                    "generic.attackDamage",
                                    tagMods.getDouble("generic.attackDamage"),
                                    1));
                }
                if (map.size() > 0) {
                    this.getEntityContained().getAttributeMap().applyAttributeModifiers(map);
                }
                final Set<String> keys = (Set<String>) tagMods.func_150296_c();
                for (final String s : keys) {
                    if (!s.startsWith("generic.")) {
                        ((EntityInfusionProperties) this.getEntityContained().getExtendedProperties("CreatureInfusion"))
                                .addInfusion(tagMods.getInteger(s));
                        if (tagMods.getInteger(s) != 7) {
                            continue;
                        }
                        ((EntityInfusionProperties) this.getEntityContained().getExtendedProperties("CreatureInfusion"))
                                .setOwner(this.recipePlayer);
                    }
                }
            }
            ((EntityInfusionProperties) this.getEntityContained().getExtendedProperties("CreatureInfusion"))
                    .addCost(this.myEssentia);
            if (this.entityContained instanceof EntityLiving) {
                ((EntityLiving) this.entityContained).func_110163_bv();
            }
            this.mode = 0;
        } else {
            for (int i = 0; i < this.selfInfusions.length; ++i) {
                if (this.selfInfusions[i] == 0) {
                    this.selfInfusions[i] = (Integer) this.recipeOutput;
                    break;
                }
            }
            this.mode = 4;
        }
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXInfusionDone(this.xCoord, this.yCoord - 1, this.zCoord),
                new NetworkRegistry.TargetPoint(
                        this.worldObj.provider.dimensionId,
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        32.0));
        this.essentiaDemanded = new AspectList();
        this.myEssentia = new AspectList();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }

    @Override
    public void writeCustomNBT(final NBTTagCompound nbttagcompound) {
        super.writeCustomNBT(nbttagcompound);
        nbttagcompound.setInteger("mode", this.mode);
        nbttagcompound.setInteger("progress", this.progress);
        nbttagcompound.setShort("instability", (short) this.instability);
        if (this.currentlySucking != null) {
            nbttagcompound.setString("sucking", this.currentlySucking.getTag());
        } else {
            nbttagcompound.setString("sucking", "");
        }
        NBTTagList tlist = new NBTTagList();
        nbttagcompound.setTag("myEssentia", tlist);
        for (final Aspect aspect : this.myEssentia.getAspects()) {
            if (aspect != null) {
                final NBTTagCompound f = new NBTTagCompound();
                f.setString("key", aspect.getTag());
                f.setInteger("amount", this.myEssentia.getAmount(aspect));
                tlist.appendTag(f);
            }
        }
        tlist = new NBTTagList();
        nbttagcompound.setTag("essentiaDemanded", tlist);
        for (final Aspect aspect : this.essentiaDemanded.getAspects()) {
            if (aspect != null) {
                final NBTTagCompound f = new NBTTagCompound();
                f.setString("key", aspect.getTag());
                f.setInteger("amount", this.essentiaDemanded.getAmount(aspect));
                tlist.appendTag(f);
            }
        }
        final NBTTagCompound entityData = new NBTTagCompound();
        if (this.getEntityContained() != null && !(this.getEntityContained() instanceof EntityPlayer)) {
            entityData.setString("id", EntityList.getEntityString(this.getEntityContained()));
            this.getEntityContained().writeToNBT(entityData);
        } else if (this.getEntityContained() != null) {
            entityData.setString("id", "PLAYER");
            entityData.setString("playerName", this.getEntityContained().getCommandSenderName());
        }
        nbttagcompound.setTag("entity", entityData);
        final NBTTagCompound item = new NBTTagCompound();
        if (this.sample != null) {
            this.sample.writeToNBT(item);
        }
        nbttagcompound.setTag("sample", item);
        final NBTTagCompound itemtoo = new NBTTagCompound();
        if (this.nutrients != null) {
            this.nutrients.writeToNBT(itemtoo);
        }
        nbttagcompound.setTag("nutrients", itemtoo);
        nbttagcompound.setIntArray("selfInfusions", this.selfInfusions);
        nbttagcompound.setFloat("selfInfusionHealth", this.selfInfusionHealth);
    }

    @Override
    public void readCustomNBT(final NBTTagCompound nbttagcompound) {
        super.readCustomNBT(nbttagcompound);
        this.mode = nbttagcompound.getInteger("mode");
        this.progress = nbttagcompound.getInteger("progress");
        this.instability = nbttagcompound.getShort("instability");
        this.currentlySucking = Aspect.getAspect(nbttagcompound.getString("sucking"));
        AspectList al = new AspectList();
        NBTTagList tlist = nbttagcompound.getTagList("myEssentia", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            final NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (rs.hasKey("key")) {
                al.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
            }
        }
        this.myEssentia = al.copy();
        al = new AspectList();
        tlist = nbttagcompound.getTagList("essentiaDemanded", 10);
        for (int j = 0; j < tlist.tagCount(); ++j) {
            final NBTTagCompound rs = tlist.getCompoundTagAt(j);
            if (rs.hasKey("key")) {
                al.add(Aspect.getAspect(rs.getString("key")), rs.getInteger("amount"));
            }
        }
        this.essentiaDemanded = al.copy();
        if (this.firstTick) {
            entityNBTObj = nbttagcompound.getCompoundTag("entity");
        } else {
            if (nbttagcompound.getCompoundTag("entity").getString("id").equals("PLAYER")) {
                this.setEntityContained(
                        this.worldObj.getPlayerEntityByName(
                                nbttagcompound.getCompoundTag("entity").getString("playerName")));
            } else if (nbttagcompound.getCompoundTag("entity").hasKey("id")) {

                this.setEntityContained(
                        (EntityLivingBase) EntityList
                                .createEntityFromNBT(nbttagcompound.getCompoundTag("entity"), this.worldObj));
            }
        }
        this.sample = ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("sample"));
        this.nutrients = ItemStack.loadItemStackFromNBT(nbttagcompound.getCompoundTag("nutrients"));
        this.selfInfusions = nbttagcompound.getIntArray("selfInfusions");
        if (this.selfInfusions.length == 0) {
            this.selfInfusions = new int[12];
        }
        this.selfInfusionHealth = nbttagcompound.getFloat("selfInfusionHealth");
    }

    @Override
    public void readFromNBT(final NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        final NBTTagList nbttaglist = nbtCompound.getTagList("recipein", 10);
        this.recipeIngredients = new ArrayList<>();
        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            final NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            final byte b0 = nbttagcompound1.getByte("item");
            this.recipeIngredients.add(ItemStack.loadItemStackFromNBT(nbttagcompound1));
        }
        final String rot = nbtCompound.getString("rotype");
        if (rot != null && rot.equals("@")) {
            this.recipeOutput = nbtCompound.getInteger("recipeout");
        } else if (rot != null) {
            this.recipeOutputLabel = rot;
            this.recipeOutput = nbtCompound.getTag("recipeout");
        }
        this.recipeInstability = nbtCompound.getInteger("recipeinst");
        this.recipeType = nbtCompound.getInteger("recipetype");
        this.recipePlayer = nbtCompound.getString("recipeplayer");
        if (this.recipePlayer.isEmpty()) {
            this.recipePlayer = null;
        }
    }

    @Override
    public void writeToNBT(final NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        if (this.recipeIngredients != null && this.recipeIngredients.size() > 0) {
            final NBTTagList nbttaglist = new NBTTagList();
            int count = 0;
            for (final ItemStack stack : this.recipeIngredients) {
                if (stack != null) {
                    final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("item", (byte) count);
                    stack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                    ++count;
                }
            }
            nbtCompound.setTag("recipein", nbttaglist);
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof Integer) {
            nbtCompound.setString("rotype", "@");
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase) {
            nbtCompound.setString("rotype", this.recipeOutputLabel);
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof Integer) {
            nbtCompound.setTag("recipeout", new NBTTagInt((Integer) this.recipeOutput));
        }
        if (this.recipeOutput != null && this.recipeOutput instanceof NBTBase) {
            nbtCompound.setTag("recipeout", (NBTBase) this.recipeOutput);
        }
        nbtCompound.setInteger("recipeinst", this.recipeInstability);
        nbtCompound.setInteger("recipetype", this.recipeType);
        nbtCompound.setInteger("recipexp", this.recipeXP);
        if (this.recipePlayer == null) {
            nbtCompound.setString("recipeplayer", "");
        } else {
            nbtCompound.setString("recipeplayer", this.recipePlayer);
        }
    }

    public boolean isValidInfusionTarget() {
        if (this.getEntityContained() != null
                && this.getEntityContained().getCreatureAttribute() != EnumCreatureAttribute.UNDEAD
                && !(this.getEntityContained() instanceof EntityPlayer)
                && !(this.getEntityContained() instanceof EntityGolem)
                && !(this.getEntityContained() instanceof EntityGolemBase)
                && !(this.getEntityContained() instanceof IMerchant)
                && !(this.getEntityContained() instanceof INpc)) {
            for (final Class clazz : ThaumicHorizons.classBanList) {
                if (this.getEntityContained().getClass().isAssignableFrom(clazz)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public void killMe() {
        if (this.entityContained != null) {
            this.killSubject();
        }
        if (this.sample != null) {
            final EntityItem item = new EntityItem(
                    this.worldObj,
                    this.xCoord + 0.5,
                    this.yCoord + 1.5,
                    this.zCoord - 0.5,
                    this.sample);
            this.worldObj.spawnEntityInWorld(item);
            this.sample = null;
        }
        if (this.nutrients != null) {
            final EntityItem item = new EntityItem(
                    this.worldObj,
                    this.xCoord + 0.5,
                    this.yCoord + 1.5,
                    this.zCoord - 0.5,
                    this.nutrients);
            this.worldObj.spawnEntityInWorld(item);
            this.nutrients = null;
        }
        for (int y = 0; y < 4; ++y) {
            for (int x = -1; x < 2; ++x) {
                for (int z = -1; z < 2; ++z) {
                    if (x != 0 || z != 0) {
                        if (y == 0 || y == 3) {
                            this.worldObj.setBlock(
                                    this.xCoord + x,
                                    this.yCoord - y,
                                    this.zCoord + z,
                                    ConfigBlocks.blockWoodenDevice,
                                    6,
                                    3);
                        } else {
                            this.worldObj
                                    .setBlock(this.xCoord + x, this.yCoord - y, this.zCoord + z, Blocks.glass, 0, 3);
                        }
                    } else if (y == 0 || y == 3) {
                        this.worldObj.setBlock(
                                this.xCoord + x,
                                this.yCoord - y,
                                this.zCoord + z,
                                ConfigBlocks.blockMetalDevice,
                                9,
                                3);
                    } else {
                        this.worldObj.setBlock(this.xCoord + x, this.yCoord - y, this.zCoord + z, Blocks.water, 0, 3);
                    }
                }
            }
        }
    }

    public void killSubject() {
        if (!this.worldObj.isRemote
                && ((this.entityContained != null && !(this.entityContained instanceof EntityPlayer))
                        || this.recipeType == 1)) {
            this.worldObj.createExplosion(null, this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5, 0.5f, false);
            for (int a = 0; a < 25; ++a) {
                final int xx = this.xCoord + this.worldObj.rand.nextInt(8) - this.worldObj.rand.nextInt(8);
                final int yy = this.yCoord + this.worldObj.rand.nextInt(8) - this.worldObj.rand.nextInt(8);
                final int zz = this.zCoord + this.worldObj.rand.nextInt(8) - this.worldObj.rand.nextInt(8);
                if (this.worldObj.isAirBlock(xx, yy, zz)) {
                    if (yy < this.yCoord) {
                        this.worldObj.setBlock(xx, yy, zz, ConfigBlocks.blockFluxGoo, 8, 3);
                    } else {
                        this.worldObj.setBlock(xx, yy, zz, ConfigBlocks.blockFluxGas, 8, 3);
                    }
                }
            }
        }
        this.selfInfusions = new int[12];
        this.setEntityContained(null);
        this.mode = 0;
        this.currentlySucking = null;
        this.myEssentia = new AspectList();
        this.essentiaDemanded = new AspectList();
        this.progress = 0;
        this.symmetry = 0;
        this.instability = 0;
        this.craftCount = 0;
        this.count = 0;
        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public int getSizeInventory() {
        return 2;
    }

    public ItemStack getStackInSlot(final int slot) {
        if (slot == 0) {
            return this.sample;
        }
        return this.nutrients;
    }

    public ItemStack decrStackSize(final int p_70298_1_, final int p_70298_2_) {
        ItemStack theStack;
        if (p_70298_1_ == 0) {
            theStack = this.sample;
        } else {
            theStack = this.nutrients;
        }
        if (theStack == null) {
            return null;
        }
        if (theStack.stackSize <= p_70298_2_) {
            ItemStack outStack;
            if (p_70298_1_ == 0) {
                outStack = this.sample.copy();
                this.sample = null;
            } else {
                outStack = this.nutrients.copy();
                this.nutrients = null;
            }
            return outStack;
        }
        ItemStack outStack = theStack.splitStack(p_70298_2_);
        if (theStack.stackSize == 0) {
            if (p_70298_1_ == 0) {
                this.sample = null;
            } else {
                this.nutrients = null;
            }
        }
        return outStack;
    }

    public ItemStack getStackInSlotOnClosing(final int p_70304_1_) {
        return null;
    }

    public void setInventorySlotContents(final int slot, final ItemStack stack) {
        if (slot == 0) {
            this.sample = stack;
        } else {
            this.nutrients = stack;
        }
        this.markDirty();
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
    }

    public String getInventoryName() {
        return "container.vat";
    }

    public boolean hasCustomInventoryName() {
        return false;
    }

    public int getInventoryStackLimit() {
        return 64;
    }

    public boolean isUseableByPlayer(final EntityPlayer p_70300_1_) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
                && p_70300_1_.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }

    public void openInventory() {}

    public void closeInventory() {}

    public boolean isItemValidForSlot(final int slot, final ItemStack stack) {
        if (slot == 0) {
            return stack.getItem() == ThaumicHorizons.itemSyringeBloodSample || stack.getItem() == Items.chicken
                    || stack.getItem() == Items.beef
                    || stack.getItem() == Items.porkchop;
        }
        return stack.getItem() == ThaumicHorizons.itemNutrients;
    }

    public int[] getAccessibleSlotsFromSide(final int side) {
        return new int[] { 0, 1 };
    }

    public boolean canInsertItem(final int slot, final ItemStack item, final int side) {
        return this.isItemValidForSlot(slot, item);
    }

    public boolean canExtractItem(final int p_102008_1_, final ItemStack p_102008_2_, final int p_102008_3_) {
        return false;
    }

    @Override
    public boolean isConnectable(final ForgeDirection face) {
        return this.mode == 1 && face == ForgeDirection.UP;
    }

    @Override
    public boolean canInputFrom(final ForgeDirection face) {
        return this.mode == 1 && face == ForgeDirection.UP;
    }

    @Override
    public boolean canOutputTo(final ForgeDirection face) {
        return false;
    }

    @Override
    public void setSuction(final Aspect aspect, final int amount) {}

    @Override
    public Aspect getSuctionType(final ForgeDirection face) {
        if (this.mode != 2) {
            return null;
        }
        return this.currentlySucking;
    }

    @Override
    public int getSuctionAmount(final ForgeDirection face) {
        return (this.essentiaDemanded.size() > 0) ? 128 : 0;
    }

    @Override
    public int takeEssentia(final Aspect aspect, final int amount, final ForgeDirection face) {
        return 0;
    }

    @Override
    public int addEssentia(final Aspect aspect, final int amount, final ForgeDirection face) {
        return this.canInputFrom(face) ? (amount - this.addToContainer(aspect, amount)) : 0;
    }

    @Override
    public Aspect getEssentiaType(final ForgeDirection face) {
        return null;
    }

    @Override
    public int getEssentiaAmount(final ForgeDirection face) {
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }

    @Override
    public AspectList getAspects() {
        if (this.mode != 2) {
            if (this.myEssentia.getAspects().length > 0 && this.myEssentia.getAspects()[0] != null) {
                return this.myEssentia;
            }
        } else {
            if (this.essentiaDemanded.getAspects().length > 0 && this.essentiaDemanded.getAspects()[0] != null) {
                return this.essentiaDemanded;
            }
        }
        return null;
    }

    @Override
    public void setAspects(final AspectList aspects) {}

    @Override
    public boolean doesContainerAccept(final Aspect tag) {
        return this.currentlySucking != null && tag.getTag().equals(this.currentlySucking.getTag());
    }

    @Override
    public int addToContainer(final Aspect tag, final int amount) {
        if (this.mode != 2) {
            this.myEssentia.add(tag, amount);
        } else {
            this.essentiaDemanded.reduce(tag, amount);
        }
        this.clientEssentiaFX(tag);
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
        return 0;
    }

    public void clientEssentiaFX(final Aspect tag) {
        PacketHandler.INSTANCE.sendToAllAround(
                new PacketFXEssentiaBubble(this.xCoord + 0.5, this.yCoord - 2, this.zCoord + 0.5, tag.getColor()),
                new NetworkRegistry.TargetPoint(
                        this.getWorldObj().provider.dimensionId,
                        this.xCoord,
                        this.yCoord,
                        this.zCoord,
                        32.0));
    }

    @Override
    public boolean takeFromContainer(final Aspect tag, final int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(final AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(final Aspect tag, final int amount) {
        return this.containerContains(tag) >= amount;
    }

    @Override
    public boolean doesContainerContain(final AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(final Aspect tag) {
        return this.myEssentia.getAmount(tag);
    }

    public EntityLivingBase getEntityContained() {
        return this.entityContained;
    }

    public void setEntityContained(final EntityLivingBase newEntity) {
        this.entityContained = newEntity;
        if (this.entityContained != null) {
            this.entityContained
                    .setLocationAndAngles(this.xCoord + 0.5, this.yCoord - 1.75, this.zCoord + 0.5, 0.0f, 0.0f);
        }
    }

    public static class SourceFX {

        public ChunkCoordinates loc;
        public int ticks;
        public int color;
        public int entity;

        public SourceFX(final ChunkCoordinates loc, final int ticks, final int color) {
            this.loc = loc;
            this.ticks = ticks;
            this.color = color;
        }
    }
}
