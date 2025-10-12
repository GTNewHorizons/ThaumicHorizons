//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityGolemTH;
import com.kentington.thaumichorizons.common.entities.EntityMeatSlime;
import com.kentington.thaumichorizons.common.entities.EntityMercurialSlime;
import com.kentington.thaumichorizons.common.entities.EntityNightmare;
import com.kentington.thaumichorizons.common.entities.EntityVoltSlime;
import com.kentington.thaumichorizons.common.entities.ai.EntityAIAttackOnCollideTH;
import com.kentington.thaumichorizons.common.entities.ai.EntityAIFollowOwnerTH;
import com.kentington.thaumichorizons.common.entities.ai.EntityAIHurtByTargetTH;
import com.kentington.thaumichorizons.common.entities.ai.EntityAIOwnerHurtByTargetTH;
import com.kentington.thaumichorizons.common.entities.ai.EntityAIOwnerHurtTargetTH;
import com.kentington.thaumichorizons.common.entities.ai.EntityAISitTH;
import com.kentington.thaumichorizons.common.entities.ai.EntityAIWanderTH;
import com.kentington.thaumichorizons.common.items.ItemAmuletMirror;
import com.kentington.thaumichorizons.common.items.ItemFocusContainment;
import com.kentington.thaumichorizons.common.lib.networking.PacketFXContainment;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketNoMoreItems;
import com.kentington.thaumichorizons.common.lib.networking.PacketPlayerInfusionSync;
import com.kentington.thaumichorizons.common.tiles.TileSoulBeacon;
import com.kentington.thaumichorizons.common.tiles.TileVat;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.EntityAspectOrb;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.entities.EntityPermanentItem;
import thaumcraft.common.items.relics.ItemHandMirror;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.utils.EntityUtils;

public class EventHandlerEntity {

    @SideOnly(Side.CLIENT)
    public static int clientNightmareID;

    @SideOnly(Side.CLIENT)
    public static int clientPlayerID;

    @SubscribeEvent
    public void ConstructEntity(final EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityLivingBase
                && event.entity.getExtendedProperties("CreatureInfusion") == null) {
            final EntityInfusionProperties prop = new EntityInfusionProperties();
            prop.entity = event.entity;
            event.entity.registerExtendedProperties("CreatureInfusion", prop);
        }
    }

    @SubscribeEvent
    public void EntityJoinWorld(final EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityLivingBase) {
            this.applyInfusions((EntityLivingBase) event.entity);
        }
        if (event.world.isRemote && event.entity instanceof EntityNightmare
                && event.entity.getEntityId() == EventHandlerEntity.clientNightmareID) {
            event.entity.worldObj.getEntityByID(EventHandlerEntity.clientPlayerID).ridingEntity = event.entity;
            event.entity.riddenByEntity = event.entity.worldObj.getEntityByID(EventHandlerEntity.clientPlayerID);
            EventHandlerEntity.clientNightmareID = -2;
            EventHandlerEntity.clientPlayerID = -2;
        }
    }

    @SubscribeEvent
    public void Respawn(cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent event) {
        // Exit Portal fix
        if (!event.player.worldObj.isRemote) {
            this.applyInfusions(event.player);
        }
    }

    public void applyInfusions(final EntityLivingBase entity) {
        EntityInfusionProperties infusionProperties = (EntityInfusionProperties) entity
                .getExtendedProperties("CreatureInfusion");
        if (entity instanceof EntityPlayer) {
            final int[] infusions = infusionProperties.getPlayerInfusions();
            for (int infusion : infusions) {
                if (infusion != 0) {
                    if (infusion == 8 && !entity.worldObj.isRemote) {
                        this.warpTumor(
                                (EntityPlayer) entity,
                                ThaumicHorizons.warpedTumorValue - infusionProperties.tumorWarpPermanent
                                        - infusionProperties.tumorWarp
                                        - infusionProperties.tumorWarpTemp);
                    }
                }
            }
            this.applyPlayerPotionInfusions((EntityPlayer) entity, infusions, infusionProperties.toggleInvisible);
            if (!entity.worldObj.isRemote) {
                PacketHandler.INSTANCE.sendToAll(
                        new PacketPlayerInfusionSync(
                                entity.getCommandSenderName(),
                                infusions,
                                infusionProperties.toggleClimb,
                                infusionProperties.toggleInvisible));
            }
        } else {
            final int[] infusions = infusionProperties.getInfusions();
            for (int i = 0; i < EntityInfusionProperties.NUM_INFUSIONS; ++i) {
                if (infusions[i] != 0) {
                    if (infusions[i] == 1) {
                        final PotionEffect effect = new PotionEffect(Potion.jump.id, Integer.MAX_VALUE, 0, true);
                        effect.setCurativeItems(new ArrayList<>());
                        entity.addPotionEffect(effect);
                    } else if (infusions[i] == 3) {
                        final PotionEffect effect = new PotionEffect(
                                Potion.regeneration.id,
                                Integer.MAX_VALUE,
                                0,
                                true);
                        effect.setCurativeItems(new ArrayList<>());
                        entity.addPotionEffect(effect);
                    } else if (infusions[i] == 4) {
                        final PotionEffect effect = new PotionEffect(Potion.resistance.id, Integer.MAX_VALUE, 0, true);
                        effect.setCurativeItems(new ArrayList<>());
                        entity.addPotionEffect(effect);
                        ThaumicHorizons.instance.renderEventHandler.thingsThatSparkle.add(entity);
                    } else if (infusions[i] == 8 && !entity.getEntityData().hasKey("runicCharge")) {
                        entity.getEntityData().setInteger("runicCharge", 6);
                    } else if (infusions[i] == 7) {
                        this.applyNewAI((EntityLiving) entity);
                    }
                }
            }
        }
    }

    public void applyPlayerPotionInfusions(final EntityPlayer entity, final int[] infusions, final boolean toggled) {
        for (int infusion : infusions) {
            if (infusion == 1) {
                PotionEffect effect = new PotionEffect(Potion.jump.id, Integer.MAX_VALUE, 0, true);
                effect.setCurativeItems(new ArrayList<>());
                entity.addPotionEffect(effect);
                effect = new PotionEffect(Potion.moveSpeed.id, Integer.MAX_VALUE, 0, true);
                effect.setCurativeItems(new ArrayList<>());
                entity.addPotionEffect(effect);
            } else if (infusion == 3) {
                final PotionEffect effect = new PotionEffect(Potion.regeneration.id, Integer.MAX_VALUE, 0, true);
                effect.setCurativeItems(new ArrayList<>());
                entity.addPotionEffect(effect);
            } else if (infusion == 4) {
                final PotionEffect effect = new PotionEffect(Potion.resistance.id, Integer.MAX_VALUE, 0, true);
                effect.setCurativeItems(new ArrayList<>());
                entity.addPotionEffect(effect);
            } else if (infusion == 10 && !toggled) {
                final PotionEffect effect = new PotionEffect(Potion.invisibility.id, Integer.MAX_VALUE, 0, true);
                effect.setCurativeItems(new ArrayList<>());
                entity.addPotionEffect(effect);
                entity.setInvisible(true);
            }
        }
    }

    public void warpTumor(final EntityPlayer entity, int capacity) {
        if (capacity <= 0) {
            return;
        }
        final int warpPermanent = Thaumcraft.proxy.getPlayerKnowledge().getWarpPerm(entity.getCommandSenderName());
        final int warp = Thaumcraft.proxy.getPlayerKnowledge().getWarpSticky(entity.getCommandSenderName());
        final int tempWarp = Thaumcraft.proxy.getPlayerKnowledge().getWarpTemp(entity.getCommandSenderName());
        if (warpPermanent > capacity) {
            Thaumcraft.proxy.getPlayerKnowledge().addWarpPerm(entity.getCommandSenderName(), -capacity);
            final EntityInfusionProperties entityInfusionProperties = (EntityInfusionProperties) entity
                    .getExtendedProperties("CreatureInfusion");
            entityInfusionProperties.tumorWarpPermanent += capacity;
            capacity = 0;
        } else {
            capacity -= warpPermanent;
            Thaumcraft.proxy.getPlayerKnowledge().addWarpPerm(entity.getCommandSenderName(), -warpPermanent);
            final EntityInfusionProperties entityInfusionProperties2 = (EntityInfusionProperties) entity
                    .getExtendedProperties("CreatureInfusion");
            entityInfusionProperties2.tumorWarpPermanent += warpPermanent;
            if (warp > capacity) {
                Thaumcraft.proxy.getPlayerKnowledge().addWarpSticky(entity.getCommandSenderName(), -capacity);
                final EntityInfusionProperties entityInfusionProperties3 = (EntityInfusionProperties) entity
                        .getExtendedProperties("CreatureInfusion");
                entityInfusionProperties3.tumorWarp += capacity;
                capacity = 0;
            } else {
                capacity -= warp;
                Thaumcraft.proxy.getPlayerKnowledge().addWarpSticky(entity.getCommandSenderName(), -warp);
                final EntityInfusionProperties entityInfusionProperties4 = (EntityInfusionProperties) entity
                        .getExtendedProperties("CreatureInfusion");
                entityInfusionProperties4.tumorWarp += warp;
                if (tempWarp > capacity) {
                    Thaumcraft.proxy.getPlayerKnowledge().addWarpTemp(entity.getCommandSenderName(), -capacity);
                    final EntityInfusionProperties entityInfusionProperties5 = (EntityInfusionProperties) entity
                            .getExtendedProperties("CreatureInfusion");
                    entityInfusionProperties5.tumorWarpTemp += capacity;
                    capacity = 0;
                } else {
                    capacity -= tempWarp;
                    Thaumcraft.proxy.getPlayerKnowledge().addWarpTemp(entity.getCommandSenderName(), -tempWarp);
                    final EntityInfusionProperties entityInfusionProperties6 = (EntityInfusionProperties) entity
                            .getExtendedProperties("CreatureInfusion");
                    entityInfusionProperties6.tumorWarpTemp += tempWarp;
                }
            }
        }
    }

    public void applyNewAI(final EntityLiving entity) {
        removeTasks(entity.tasks);
        removeTasks(entity.targetTasks);

        entity.tasks.addTask(1, new EntityAISwimming(entity));
        entity.tasks.addTask(2, new EntityAISitTH(entity));
        entity.tasks.addTask(3, new EntityAILeapAtTarget(entity, 0.4f));
        entity.tasks.addTask(4, new EntityAIAttackOnCollideTH(entity, 1.0, true));
        entity.tasks.addTask(5, new EntityAIFollowOwnerTH(entity, 1.0, 10.0f, 2.0f));
        if (entity instanceof EntityAnimal) {
            entity.tasks.addTask(6, new EntityAIMate((EntityAnimal) entity, 1.0));
        }
        entity.tasks.addTask(7, new EntityAIWanderTH(entity, 1.0));
        entity.tasks.addTask(9, new EntityAIWatchClosest(entity, EntityPlayer.class, 8.0f));
        entity.tasks.addTask(9, new EntityAILookIdle(entity));
        entity.targetTasks.addTask(1, new EntityAIOwnerHurtByTargetTH(entity));
        entity.targetTasks.addTask(2, new EntityAIOwnerHurtTargetTH(entity));
        entity.targetTasks.addTask(3, new EntityAIHurtByTargetTH(entity, true));
    }

    private void removeTasks(EntityAITasks entityAITasks) {
        final ArrayList<Object> taskEntries = new ArrayList<Object>(entityAITasks.taskEntries);
        for (Object task : taskEntries) {
            EntityAITasks.EntityAITaskEntry taskEntry = (EntityAITasks.EntityAITaskEntry) task;
            entityAITasks.removeTask(taskEntry.action);
        }
    }

    @SubscribeEvent
    public void livingTick(final LivingEvent.LivingUpdateEvent event) {
        if (event.entity instanceof final EntityPlayer player) {
            final String pp = "R" + player.getDisplayName();
            if (ItemFocusContainment.hitCritters.containsKey(pp)) {
                ItemFocusContainment.contain.put(pp, ItemFocusContainment.contain.get(pp) - 1.0f);
                if (ItemFocusContainment.contain.get(pp) > 0.0f) {
                    final Entity ent = ItemFocusContainment.hitCritters.get(pp);
                    ThaumicHorizons.proxy.containmentFX(
                            ent.posX,
                            ent.posY,
                            ent.posZ,
                            player,
                            ent,
                            (int) (ItemFocusContainment.contain.get(pp) / ((EntityLiving) ent).getHealth()) / 3 + 1);
                } else {
                    ItemFocusContainment.contain.remove(pp);
                    ItemFocusContainment.hitCritters.remove(pp);
                }
            }
            final EntityInfusionProperties prop = (EntityInfusionProperties) player
                    .getExtendedProperties("CreatureInfusion");
            if (prop.hasPlayerInfusion(5) && (player.getActivePotionEffect(Potion.poison) != null
                    || player.getActivePotionEffect(Potion.wither) != null
                    || player.getActivePotionEffect(Potion.potionTypes[Config.potionInfVisExhaustID]) != null
                    || player.getActivePotionEffect(Potion.potionTypes[Config.potionVisExhaustID]) != null
                    || player.getActivePotionEffect(Potion.potionTypes[Config.potionThaumarhiaID]) != null
                    || player.getActivePotionEffect(Potion.potionTypes[Config.potionTaintPoisonID]) != null)) {

                final Collection activePotionEffects = event.entityLiving.getActivePotionEffects();
                final ArrayList<PotionEffect> toAdd = new ArrayList<>();

                for (Object activePotionEffect : activePotionEffects) {
                    final PotionEffect effect = (PotionEffect) activePotionEffect;
                    if (effect.getPotionID() == Potion.poison.id || effect.getPotionID() == Potion.wither.id
                            || effect.getPotionID() == Config.potionInfVisExhaustID
                            || effect.getPotionID() == Config.potionTaintPoisonID
                            || effect.getPotionID() == Config.potionVisExhaustID
                            || effect.getPotionID() == Config.potionThaumarhiaID) {
                        final int id = effect.getPotionID();
                        final int amplifier = 0;
                        final int duration = effect.getDuration() - 1;
                        toAdd.add(new PotionEffect(id, duration, amplifier, false));
                    } else {
                        toAdd.add(effect);
                    }
                }
                event.entityLiving.clearActivePotions();
                for (final PotionEffect effect : toAdd) {
                    event.entityLiving.addPotionEffect(effect);
                }
            }
            if (prop.hasPlayerInfusion(6) && event.entity.ticksExisted % 200 == 0
                    && player.worldObj.isDaytime()
                    && player.worldObj.canBlockSeeTheSky(
                            MathHelper.floor_double(player.posX),
                            MathHelper.floor_double(player.posY),
                            MathHelper.floor_double(player.posZ))) {
                player.getFoodStats().addStats(1, 0.0f);
            }
            if (prop.hasPlayerInfusion(7) && player.isInWater()) {
                player.setAir(300);
            }
            if (event.entityLiving.ticksExisted % 30 == 0) {
                if (prop.hasPlayerInfusion(8) && !event.entityLiving.worldObj.isRemote) {
                    this.warpTumor(
                            (EntityPlayer) event.entityLiving,
                            ThaumicHorizons.warpedTumorValue - prop.tumorWarpPermanent
                                    - prop.tumorWarp
                                    - prop.tumorWarpTemp);
                }
                this.applyPlayerPotionInfusions(player, prop.playerInfusions, prop.toggleInvisible);
            }
            if (prop.hasPlayerInfusion(9) && !prop.toggleClimb) {
                if (event.entityLiving.isCollidedHorizontally) {
                    event.entityLiving.motionY = 0.2;
                    if (event.entityLiving.isSneaking()) {
                        event.entityLiving.motionY = 0.0;
                    }
                    event.entity.fallDistance = 0.0f;
                } else {
                    final List listy = event.entityLiving.worldObj.func_147461_a(
                            AxisAlignedBB.getBoundingBox(
                                    event.entityLiving.posX - event.entityLiving.width / 1.5,
                                    event.entityLiving.posY,
                                    event.entityLiving.posZ - event.entityLiving.width / 1.5,
                                    event.entityLiving.posX + event.entityLiving.width / 1.5,
                                    event.entityLiving.posY + event.entityLiving.height * 0.9,
                                    event.entityLiving.posZ + event.entityLiving.width / 1.5));
                    if (listy.size() > 0) {
                        if (event.entityLiving.isSneaking()) {
                            event.entityLiving.motionY = 0.0;
                        } else {
                            event.entityLiving.motionY = -0.15;
                        }
                        event.entity.fallDistance = 0.0f;
                    }
                }
            }
        }
        if (event.entityLiving.ticksExisted % 30 == 0) {
            final boolean shock = ((EntityInfusionProperties) event.entityLiving
                    .getExtendedProperties("CreatureInfusion")).hasInfusion(6);
            if (shock && event.entityLiving.getAITarget() != null) {
                event.entityLiving.getAITarget().attackEntityFrom(DamageSource.magic, 1.0f);
                Thaumcraft.proxy.arcLightning(
                        event.entityLiving.worldObj,
                        event.entityLiving.posX,
                        event.entityLiving.posY + event.entityLiving.height / 2.0f,
                        event.entityLiving.posZ,
                        event.entityLiving.getAITarget().posX,
                        event.entityLiving.getAITarget().posY + event.entityLiving.getAITarget().height / 2.0f,
                        event.entityLiving.getAITarget().posZ,
                        0.2f,
                        0.8f,
                        0.8f,
                        1.0f);
                event.entityLiving.worldObj.playSoundAtEntity(
                        event.entityLiving,
                        "thaumcraft:zap",
                        1.0f,
                        1.0f + (event.entityLiving.worldObj.rand.nextFloat()
                                - event.entityLiving.worldObj.rand.nextFloat()) * 0.2f);
            } else if (shock && event.entityLiving instanceof EntityLiving
                    && ((EntityLiving) event.entityLiving).getAttackTarget() != null) {
                        ((EntityLiving) event.entityLiving).getAttackTarget()
                                .attackEntityFrom(DamageSource.magic, 1.0f);
                        Thaumcraft.proxy.arcLightning(
                                event.entityLiving.worldObj,
                                event.entityLiving.posX,
                                event.entityLiving.posY + event.entityLiving.height / 2.0f,
                                event.entityLiving.posZ,
                                ((EntityLiving) event.entityLiving).getAttackTarget().posX,
                                ((EntityLiving) event.entityLiving).getAttackTarget().posY
                                        + ((EntityLiving) event.entityLiving).getAttackTarget().height / 2.0f,
                                ((EntityLiving) event.entityLiving).getAttackTarget().posZ,
                                0.2f,
                                0.8f,
                                0.8f,
                                1.0f);
                        event.entityLiving.worldObj.playSoundAtEntity(
                                event.entityLiving,
                                "thaumcraft:zap",
                                1.0f,
                                1.0f + (event.entityLiving.worldObj.rand.nextFloat()
                                        - event.entityLiving.worldObj.rand.nextFloat()) * 0.2f);
                    }
        }
        if (event.entityLiving instanceof EntityVoltSlime && event.entityLiving.ticksExisted % 2 == 0
                && event.entityLiving.getAITarget() != null) {
            event.entityLiving.getAITarget().attackEntityFrom(DamageSource.magic, 0.5f);
            Thaumcraft.proxy.arcLightning(
                    event.entityLiving.worldObj,
                    event.entityLiving.posX,
                    event.entityLiving.posY + event.entityLiving.height / 2.0f,
                    event.entityLiving.posZ,
                    event.entityLiving.getAITarget().posX,
                    event.entityLiving.getAITarget().posY + event.entityLiving.getAITarget().height / 2.0f,
                    event.entityLiving.getAITarget().posZ,
                    0.2f,
                    0.8f,
                    0.8f,
                    1.0f);
            event.entityLiving.worldObj.playSoundAtEntity(
                    event.entityLiving,
                    "thaumcraft:zap",
                    1.0f,
                    1.0f + (event.entityLiving.worldObj.rand.nextFloat() - event.entityLiving.worldObj.rand.nextFloat())
                            * 0.2f);
        }
        if (event.entityLiving.ticksExisted % 100 == 0) {
            final boolean runic = ((EntityInfusionProperties) event.entityLiving
                    .getExtendedProperties("CreatureInfusion")).hasInfusion(8);
            if (runic) {
                int charge = event.entityLiving.getEntityData().getInteger("runicCharge") + 1;
                if (charge > 6) {
                    charge = 6;
                }
                event.entityLiving.getEntityData().setInteger("runicCharge", charge);
            }
        }
        if (((EntityLivingBase) event.entity).isPotionActive(ThaumicHorizons.potionShockID)) {
            final EntityLivingBase player2 = (EntityLivingBase) event.entity;
            final ArrayList<Entity> stuff = EntityUtils.getEntitiesInRange(
                    player2.worldObj,
                    player2.posX,
                    player2.posY,
                    player2.posZ,
                    player2,
                    EntityLivingBase.class,
                    10.0);
            if (stuff != null && stuff.size() > 0) {
                final int boost = player2.getActivePotionEffect(Potion.potionTypes[ThaumicHorizons.potionShockID])
                        .getAmplifier();
                for (final Entity e : stuff) {
                    final int r = player2.worldObj.rand.nextInt(1000);
                    if (r < 20 * (1 + boost) && !e.isDead && e instanceof EntityLivingBase) {
                        if (player2 instanceof EntityPlayer) {
                            e.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) player2), 1.0f);
                        } else {
                            e.attackEntityFrom(DamageSource.magic, 1.0f);
                        }
                        Thaumcraft.proxy.arcLightning(
                                player2.worldObj,
                                player2.posX,
                                player2.posY + player2.height / 2.0f,
                                player2.posZ,
                                e.posX,
                                e.posY + e.height / 2.0f,
                                e.posZ,
                                0.2f,
                                0.8f,
                                0.8f,
                                1.0f);
                        player2.worldObj.playSoundAtEntity(
                                player2,
                                "thaumcraft:zap",
                                1.0f,
                                1.0f + (player2.worldObj.rand.nextFloat() - player2.worldObj.rand.nextFloat()) * 0.2f);
                    }
                }
            }
        }
        if (((EntityLivingBase) event.entity).isPotionActive(ThaumicHorizons.potionVisRegenID)) {
            final EntityLivingBase target = (EntityLivingBase) event.entity;
            final int boost2 = target.getActivePotionEffect(Potion.potionTypes[ThaumicHorizons.potionVisRegenID])
                    .getAmplifier();
            if (event.entity.ticksExisted % (20 - 3 * boost2) == 0) {
                Aspect aspect = null;
                switch (target.worldObj.rand.nextInt(6)) {
                    case 0 -> {
                        aspect = Aspect.AIR;
                    }
                    case 1 -> {
                        aspect = Aspect.EARTH;
                    }
                    case 2 -> {
                        aspect = Aspect.FIRE;
                    }
                    case 3 -> {
                        aspect = Aspect.WATER;
                    }
                    case 4 -> {
                        aspect = Aspect.ORDER;
                    }
                    case 5 -> {
                        aspect = Aspect.ENTROPY;
                    }
                }
                if (aspect != null) {
                    final EntityAspectOrb orb = new EntityAspectOrb(
                            target.worldObj,
                            target.posX,
                            target.posY,
                            target.posZ,
                            aspect,
                            1);
                    target.worldObj.spawnEntityInWorld(orb);
                }
            }
        }
        if (((EntityLivingBase) event.entity).isPotionActive(ThaumicHorizons.potionVacuumID)) {
            final EntityLivingBase player2 = (EntityLivingBase) event.entity;
            final int boost2 = player2.getActivePotionEffect(Potion.potionTypes[ThaumicHorizons.potionVacuumID])
                    .getAmplifier();
            final ArrayList<Entity> stuff2 = EntityUtils.getEntitiesInRange(
                    player2.worldObj,
                    player2.posX,
                    player2.posY,
                    player2.posZ,
                    player2,
                    EntityItem.class,
                    10.0 + 2 * boost2);
            if (stuff2 != null && stuff2.size() > 0) {
                for (final Entity e : stuff2) {
                    if ((!(e instanceof EntityFollowingItem) || ((EntityFollowingItem) e).target == null) && !e.isDead
                            && e instanceof EntityItem) {
                        double d6 = e.posX - player2.posX;
                        double d7 = e.posY - player2.posY + player2.height / 2.0f;
                        double d8 = e.posZ - player2.posZ;
                        final double d9 = MathHelper.sqrt_double(d6 * d6 + d7 * d7 + d8 * d8);
                        d6 /= d9;
                        d7 /= d9;
                        d8 /= d9;
                        final double d10 = 0.1;
                        e.motionX -= d6 * d10;
                        e.motionY -= d7 * d10;
                        e.motionZ -= d8 * d10;
                        if (e.motionX > 0.35) {
                            e.motionX = 0.35;
                        }
                        if (e.motionX < -0.35) {
                            e.motionX = -0.35;
                        }
                        if (e.motionY > 0.35) {
                            e.motionY = 0.35;
                        }
                        if (e.motionY < -0.35) {
                            e.motionY = -0.35;
                        }
                        if (e.motionZ > 0.35) {
                            e.motionZ = 0.35;
                        }
                        if (e.motionZ < -0.35) {
                            e.motionZ = -0.35;
                        }
                        Thaumcraft.proxy.spark(
                                (float) e.posX + (player2.worldObj.rand.nextFloat() - player2.worldObj.rand.nextFloat())
                                        * 0.125f,
                                (float) e.posY + (player2.worldObj.rand.nextFloat() - player2.worldObj.rand.nextFloat())
                                        * 0.125f,
                                (float) e.posZ + (player2.worldObj.rand.nextFloat() - player2.worldObj.rand.nextFloat())
                                        * 0.125f,
                                1.0f,
                                0.25f,
                                0.25f,
                                0.25f,
                                1.0f);
                    }
                }
            }
        }
        if (((EntityLivingBase) event.entity).isPotionActive(ThaumicHorizons.potionSynthesisID)) {
            final EntityLivingBase player2 = (EntityLivingBase) event.entity;
            final int boost2 = player2.getActivePotionEffect(Potion.potionTypes[ThaumicHorizons.potionSynthesisID])
                    .getAmplifier();
            if (event.entity.ticksExisted % (15 - 2 * boost2) == 0 && player2.worldObj.isDaytime()
                    && player2.worldObj.canBlockSeeTheSky(
                            MathHelper.floor_double(player2.posX),
                            MathHelper.floor_double(player2.posY),
                            MathHelper.floor_double(player2.posZ))) {
                player2.heal(0.5f);
                if (player2 instanceof EntityPlayer) {
                    ((EntityPlayer) player2).getFoodStats().addStats(1, 0.5f);
                }
            }
        }
    }

    @SubscribeEvent
    public void livingDeath(final LivingDeathEvent event) {
        if (!event.entityLiving.worldObj.isRemote) {
            if (event.entityLiving instanceof EntityMeatSlime
                    && ((EntityMeatSlime) event.entityLiving).getSlimeSize() == 1) {
                switch (event.entityLiving.worldObj.rand.nextInt(5)) {
                    case 0 -> {
                        event.entityLiving.entityDropItem(new ItemStack(Items.beef), 0.0f);
                    }
                    case 1 -> {
                        event.entityLiving.entityDropItem(new ItemStack(Items.porkchop), 0.0f);
                    }
                    case 2 -> {
                        event.entityLiving.entityDropItem(new ItemStack(Items.chicken), 0.0f);
                    }
                    case 3 -> {
                        event.entityLiving.entityDropItem(new ItemStack(Items.fish), 0.0f);
                    }
                    default -> {
                        event.entityLiving.entityDropItem(new ItemStack(Items.rotten_flesh), 0.0f);
                    }
                }
            } else if (event.entityLiving instanceof EntityMercurialSlime
                    && ((EntityMercurialSlime) event.entityLiving).getSlimeSize() == 1) {
                        event.entityLiving.entityDropItem(new ItemStack(ConfigItems.itemResource, 1, 3), 0.0f);
                    } else
                if (event.entityLiving instanceof EntityPlayer) {
                    final EntityInfusionProperties prop = (EntityInfusionProperties) event.entity
                            .getExtendedProperties("CreatureInfusion");
                    if (prop != null) {
                        if (prop.tumorWarpPermanent > 0 || prop.tumorWarp > 0 || prop.tumorWarpTemp > 0) {
                            Thaumcraft.proxy.getPlayerKnowledge()
                                    .addWarpPerm(event.entity.getCommandSenderName(), prop.tumorWarpPermanent);
                            Thaumcraft.proxy.getPlayerKnowledge()
                                    .addWarpSticky(event.entity.getCommandSenderName(), prop.tumorWarp);
                            Thaumcraft.proxy.getPlayerKnowledge()
                                    .addWarpTemp(event.entity.getCommandSenderName(), prop.tumorWarpTemp);
                        }
                        prop.resetPlayerInfusions();
                    }
                }
        }
    }

    @SubscribeEvent
    public void onPlayerHurt(final LivingHurtEvent event) {
        final EntityInfusionProperties prop = (EntityInfusionProperties) event.entity
                .getExtendedProperties("CreatureInfusion");
        if (prop.hasPlayerInfusion(5) && event.source == DamageSourceThaumcraft.taint) {
            event.setCanceled(true);
            event.ammount = 0.0f;
            return;
        }
        if (!event.entity.worldObj.isRemote && event.entity instanceof final EntityPlayer player
                && event.entityLiving.getHealth() - event.ammount <= 0.0f) {
            // Clear player infusions
            if (prop.tumorWarpPermanent > 0 || prop.tumorWarp > 0 || prop.tumorWarpTemp > 0) {
                Thaumcraft.proxy.getPlayerKnowledge()
                        .addWarpPerm(event.entity.getCommandSenderName(), prop.tumorWarpPermanent);
                Thaumcraft.proxy.getPlayerKnowledge()
                        .addWarpSticky(event.entity.getCommandSenderName(), prop.tumorWarp);
                Thaumcraft.proxy.getPlayerKnowledge()
                        .addWarpTemp(event.entity.getCommandSenderName(), prop.tumorWarpTemp);
            }
            prop.resetPlayerInfusions();

            // Mirrored Amulet returning items
            ItemStack amulet = null;
            for (ItemStack bauble : PlayerHandler.getPlayerBaubles(player).stackList) {
                if (bauble != null && bauble.getItem() instanceof ItemAmuletMirror) {
                    amulet = bauble;
                    break;
                }
            }
            if (amulet != null) {
                boolean transportedSomething = false;
                for (int i = 0; i < player.inventory.armorInventory.length; ++i) {
                    final ItemStack item = player.inventory.armorInventory[i];
                    if (item != null && ItemHandMirror.transport(amulet, item, player, player.worldObj)) {
                        transportedSomething = true;
                        player.inventory.armorInventory[i] = null;
                    }
                }
                for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                    final ItemStack item = player.inventory.mainInventory[i];
                    if (item != null && ItemHandMirror.transport(amulet, item, player, player.worldObj)) {
                        transportedSomething = true;
                        player.inventory.mainInventory[i] = null;
                    }
                }
                InventoryBaubles baubles = PlayerHandler.getPlayerBaubles(player);
                int amuletIndex = 0;
                for (int i = 0; i < baubles.stackList.length; ++i) {
                    final ItemStack item = baubles.stackList[i];
                    if (item == amulet) {
                        amuletIndex = i;
                    } else if (item != null && ItemHandMirror.transport(amulet, item, player, player.worldObj)) {
                        transportedSomething = true;
                        baubles.stackList[i] = null;
                    }
                }
                PlayerHandler.setPlayerBaubles(player, baubles);
                if (transportedSomething) {
                    baubles.stackList[amuletIndex] = null;
                    PlayerHandler.setPlayerBaubles(player, baubles);
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketFXContainment(
                                    player.posX,
                                    player.posY + player.getEyeHeight(),
                                    player.posZ),
                            new NetworkRegistry.TargetPoint(
                                    player.worldObj.provider.dimensionId,
                                    player.posX,
                                    player.posY,
                                    player.posZ,
                                    32.0));
                    player.worldObj.playSoundEffect(
                            player.posX,
                            player.posY + player.getEyeHeight(),
                            player.posZ,
                            "thaumcraft:craftfail",
                            1.0f,
                            1.0f);
                    player.inventory.markDirty();
                    final ItemStack droppedPearl = new ItemStack(ConfigItems.itemEldritchObject, 1, 3);
                    final EntityPermanentItem drop = new EntityPermanentItem(
                            player.worldObj,
                            player.posX,
                            player.posY,
                            player.posZ,
                            droppedPearl);
                    player.worldObj.spawnEntityInWorld(drop);
                }
            }

            // Return to Soul Beacon before death
            if (player.getEntityData().getBoolean("soulBeacon")) {
                final int dim = player.getEntityData().getInteger("soulBeaconDim");
                final World beaconWorld = MinecraftServer.getServer().worldServerForDimension(dim);
                final World playerWorld = player.worldObj;
                final int x = player.getEntityData().getIntArray("soulBeaconCoords")[0];
                final int y = player.getEntityData().getIntArray("soulBeaconCoords")[1];
                final int z = player.getEntityData().getIntArray("soulBeaconCoords")[2];
                if (beaconWorld.getTileEntity(x, y, z) instanceof TileSoulBeacon
                        && beaconWorld.getTileEntity(x, y - 1, z) instanceof TileVat vat
                        && vat.mode == 4) {
                    event.setCanceled(true);
                    event.ammount = 0.0f;
                    playerWorld.createExplosion(
                            null,
                            player.posX,
                            player.posY + player.getEyeHeight(),
                            player.posZ,
                            0.0f,
                            false);
                    for (int i = 0; i < 25; ++i) {
                        final int xx = (int) player.posX + playerWorld.rand.nextInt(8) - playerWorld.rand.nextInt(8);
                        final int yy = (int) player.posY + playerWorld.rand.nextInt(8) - playerWorld.rand.nextInt(8);
                        final int zz = (int) player.posZ + playerWorld.rand.nextInt(8) - playerWorld.rand.nextInt(8);
                        if (playerWorld.isAirBlock(xx, yy, zz)) {
                            if (yy <= (int) player.posY + 1) {
                                playerWorld.setBlock(xx, yy, zz, ConfigBlocks.blockFluxGoo, 8, 3);
                            } else {
                                playerWorld.setBlock(xx, yy, zz, ConfigBlocks.blockFluxGas, 8, 3);
                            }
                        }
                    }
                    player.inventory.dropAllItems();
                    for (ItemStack bauble : PlayerHandler.getPlayerBaubles(player).stackList) {
                        if (bauble == null) {
                            continue;
                        }
                        player.func_146097_a(bauble, true, false);
                    }
                    PlayerHandler.clearPlayerBaubles(player);
                    player.inventory.markDirty();
                    PacketHandler.INSTANCE.sendTo(new PacketNoMoreItems(), (EntityPlayerMP) player);
                    player.curePotionEffects(new ItemStack(Items.milk_bucket));
                    player.heal(Float.MAX_VALUE);
                    if (dim != player.worldObj.provider.dimensionId) {
                        player.travelToDimension(dim);
                    }
                    player.setPositionAndUpdate(x + 0.5, y - 2.5, z + 0.5);
                    Thaumcraft.proxy.blockSparkle(beaconWorld, x, y - 2, z, 16777215, 20);
                    Thaumcraft.proxy.blockSparkle(beaconWorld, x, y - 3, z, 16777215, 20);
                    beaconWorld.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "thaumcraft:whispers", 1.0f, beaconWorld.rand.nextFloat());
                    this.applyPlayerInfusions(player, vat);
                    vat.selfInfusions = new int[12];
                    vat.mode = 0;
                    vat.setEntityContained(player);
                    beaconWorld.getTileEntity(x, y - 1, z).markDirty();
                    player.worldObj.markBlockForUpdate(x, y - 1, z);
                }
            }
        }
    }

    void applyPlayerInfusions(final EntityPlayer player, final TileVat tile) {
        final EntityInfusionProperties prop = (EntityInfusionProperties) player
                .getExtendedProperties("CreatureInfusion");
        for (int i = 0; i < tile.selfInfusions.length; ++i) {
            if (tile.selfInfusions[i] != 0) {
                prop.addPlayerInfusion(tile.selfInfusions[i]);
            }
        }
        this.applyInfusions(player);
    }

    @SubscribeEvent
    public void onAttack(final LivingAttackEvent event) {
        if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityLivingBase) {
            final boolean poison = ((EntityInfusionProperties) event.entityLiving
                    .getExtendedProperties("CreatureInfusion")).hasInfusion(9);
            if (poison) {
                event.entityLiving.addPotionEffect(new PotionEffect(Potion.poison.id, 40, 0, false));
            }
        }
        if (!event.source.isProjectile()) {
            return;
        }
        final boolean ender = ((EntityInfusionProperties) event.entityLiving.getExtendedProperties("CreatureInfusion"))
                .hasInfusion(5);
        if (!ender) {
            return;
        }
        event.setCanceled(true);
        this.teleport(event.entityLiving);
    }

    public void teleport(final EntityLivingBase entity) {
        final EnderTeleportEvent event = new EnderTeleportEvent(
                entity,
                entity.posX + (entity.worldObj.rand.nextDouble() - 0.5) * 64.0,
                entity.posY + (entity.worldObj.rand.nextInt(64) - 32),
                entity.posZ + (entity.worldObj.rand.nextDouble() - 0.5) * 64.0,
                0.0f);
        if (MinecraftForge.EVENT_BUS.post(event)) {
            return;
        }
        final double d3 = entity.posX;
        final double d4 = entity.posY;
        final double d5 = entity.posZ;
        entity.posX = event.targetX;
        entity.posY = event.targetY;
        entity.posZ = event.targetZ;
        boolean flag = false;
        final int i = MathHelper.floor_double(entity.posX);
        int j = MathHelper.floor_double(entity.posY);
        final int k = MathHelper.floor_double(entity.posZ);
        if (entity.worldObj.blockExists(i, j, k)) {
            boolean flag2 = false;
            while (!flag2 && j > 0) {
                final Block block = entity.worldObj.getBlock(i, j - 1, k);
                if (block.getMaterial().blocksMovement()) {
                    flag2 = true;
                } else {
                    --entity.posY;
                    --j;
                }
            }
            if (flag2) {
                entity.setPosition(entity.posX, entity.posY, entity.posZ);
                if (entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty()
                        && !entity.worldObj.isAnyLiquid(entity.boundingBox)) {
                    flag = true;
                }
            }
        }
        if (!flag) {
            entity.setPosition(d3, d4, d5);
            return;
        }
        final short short1 = 128;
        for (int l = 0; l < short1; ++l) {
            final double d6 = l / (short1 - 1.0);
            final float f = (entity.worldObj.rand.nextFloat() - 0.5f) * 0.2f;
            final float f2 = (entity.worldObj.rand.nextFloat() - 0.5f) * 0.2f;
            final float f3 = (entity.worldObj.rand.nextFloat() - 0.5f) * 0.2f;
            final double d7 = d3 + (entity.posX - d3) * d6
                    + (entity.worldObj.rand.nextDouble() - 0.5) * entity.width * 2.0;
            final double d8 = d4 + (entity.posY - d4) * d6 + entity.worldObj.rand.nextDouble() * entity.height;
            final double d9 = d5 + (entity.posZ - d5) * d6
                    + (entity.worldObj.rand.nextDouble() - 0.5) * entity.width * 2.0;
            entity.worldObj.spawnParticle("portal", d7, d8, d9, f, f2, f3);
        }
        entity.worldObj.playSoundEffect(d3, d4, d5, "mob.endermen.portal", 1.0f, 1.0f);
        entity.playSound("mob.endermen.portal", 1.0f, 1.0f);
    }

    @SubscribeEvent
    public void entityHurt(final LivingHurtEvent event) {
        final boolean runic = ((EntityInfusionProperties) event.entityLiving.getExtendedProperties("CreatureInfusion"))
                .hasInfusion(8);
        if (runic) {
            int charge = event.entityLiving.getEntityData().getInteger("runicCharge");
            if (charge <= 0 || event.source == DamageSource.drown
                    || event.source == DamageSource.wither
                    || event.source == DamageSource.outOfWorld
                    || event.source == DamageSource.starve) {
                return;
            }
            int target = -1;
            if (event.source.getEntity() != null) {
                target = event.source.getEntity().getEntityId();
            }
            if (event.source == DamageSource.fall) {
                target = -2;
            }
            if (event.source == DamageSource.fallingBlock) {
                target = -3;
            }
            thaumcraft.common.lib.network.PacketHandler.INSTANCE.sendToAllAround(
                    new PacketFXShield(event.entity.getEntityId(), target),
                    new NetworkRegistry.TargetPoint(
                            event.entity.worldObj.provider.dimensionId,
                            event.entity.posX,
                            event.entity.posY,
                            event.entity.posZ,
                            64.0));
            if (charge > event.ammount) {
                charge -= (int) event.ammount;
                event.ammount = 0.0f;
            } else {
                event.ammount -= charge;
                charge = 0;
            }
            event.entityLiving.getEntityData().setInteger("runicCharge", charge);
        }
    }

    @SubscribeEvent
    public void golemDies(final LivingDeathEvent event) {
        if (event.entity instanceof EntityGolemTH) {
            ((EntityGolemTH) event.entity).die();
        }
    }

    @SubscribeEvent
    public void sitStay(final EntityInteractEvent event) {
        if (event.target.getExtendedProperties("CreatureInfusion") != null) {
            final EntityInfusionProperties prop = (EntityInfusionProperties) event.target
                    .getExtendedProperties("CreatureInfusion");
            if (prop.hasInfusion(10) && event.entityPlayer.getHeldItem() != null
                    && event.entityPlayer.getHeldItem().getItem() == ConfigItems.itemWandCasting) {
                final ItemStack jar = new ItemStack(ThaumicHorizons.blockJar);
                final NBTTagCompound entityData = new NBTTagCompound();
                entityData.setString("id", EntityList.getEntityString(event.target));
                event.target.writeToNBT(entityData);
                jar.setTagCompound(entityData);
                jar.getTagCompound().setString("jarredCritterName", event.target.getCommandSenderName());
                jar.getTagCompound().setBoolean("isSoul", false);
                event.target.entityDropItem(jar, 1.0f);
                if (!event.target.worldObj.isRemote) {
                    PacketHandler.INSTANCE.sendToAllAround(
                            new PacketFXContainment(
                                    event.target.posX,
                                    event.target.posY + event.target.height / 2.0f,
                                    event.target.posZ),
                            new NetworkRegistry.TargetPoint(
                                    event.target.worldObj.provider.dimensionId,
                                    event.target.posX,
                                    event.target.posY + event.target.height / 2.0f,
                                    event.target.posZ,
                                    32.0));
                }
                event.target.worldObj.removeEntity(event.target);
            } else if (prop.hasInfusion(7) && event.entityPlayer.getCommandSenderName().equals(prop.getOwner())) {
                prop.setSitting(!prop.isSitting());
                if (event.target.worldObj.isRemote) {
                    if (prop.isSitting()) {
                        event.entityPlayer.addChatMessage(
                                new ChatComponentText(
                                        EnumChatFormatting.ITALIC + ""
                                                + EnumChatFormatting.GRAY
                                                + event.target.getCommandSenderName()
                                                + " is waiting."));
                    } else {
                        event.entityPlayer.addChatMessage(
                                new ChatComponentText(
                                        EnumChatFormatting.ITALIC + ""
                                                + EnumChatFormatting.GRAY
                                                + event.target.getCommandSenderName()
                                                + " will follow you."));
                    }
                }
            }
        }
    }
}
