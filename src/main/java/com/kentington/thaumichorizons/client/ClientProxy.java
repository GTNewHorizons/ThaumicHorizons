//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.kentington.thaumichorizons.client.audio.VortexSound;
import com.kentington.thaumichorizons.client.fx.FXSonic;
import com.kentington.thaumichorizons.client.gui.GuiBloodInfuser;
import com.kentington.thaumichorizons.client.gui.GuiCase;
import com.kentington.thaumichorizons.client.gui.GuiFingers;
import com.kentington.thaumichorizons.client.gui.GuiInjector;
import com.kentington.thaumichorizons.client.gui.GuiInspiratron;
import com.kentington.thaumichorizons.client.gui.GuiSoulExtractor;
import com.kentington.thaumichorizons.client.gui.GuiSoulforge;
import com.kentington.thaumichorizons.client.gui.GuiVat;
import com.kentington.thaumichorizons.client.gui.GuiVisDynamo;
import com.kentington.thaumichorizons.client.renderer.block.BlockBloodInfuserRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockEssentiaDynamoRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockInspiratronRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockJarTHRenderer;
import com.kentington.thaumichorizons.client.renderer.block.BlockNodeMonitorRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockRecombinatorRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockSlotRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockSoulBeaconRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockSoulSieveRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockSoulforgeRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockSpikeRenderer;
import com.kentington.thaumichorizons.client.renderer.block.BlockSyntheticNodeRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockTransductionAmplifierRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockVatInteriorRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockVatMatrixRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockVatRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockVatSolidRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockVisDynamoRender;
import com.kentington.thaumichorizons.client.renderer.block.BlockVortexStabilizerRender;
import com.kentington.thaumichorizons.client.renderer.entity.BlastPhialRender;
import com.kentington.thaumichorizons.client.renderer.entity.RenderAlchemitePrimed;
import com.kentington.thaumichorizons.client.renderer.entity.RenderBoatGreatwood;
import com.kentington.thaumichorizons.client.renderer.entity.RenderBoatThaumium;
import com.kentington.thaumichorizons.client.renderer.entity.RenderChocolateCow;
import com.kentington.thaumichorizons.client.renderer.entity.RenderEndersteed;
import com.kentington.thaumichorizons.client.renderer.entity.RenderFamiliar;
import com.kentington.thaumichorizons.client.renderer.entity.RenderGoldChicken;
import com.kentington.thaumichorizons.client.renderer.entity.RenderGolemTH;
import com.kentington.thaumichorizons.client.renderer.entity.RenderGravekeeper;
import com.kentington.thaumichorizons.client.renderer.entity.RenderGuardianPanther;
import com.kentington.thaumichorizons.client.renderer.entity.RenderLightningBoltFinite;
import com.kentington.thaumichorizons.client.renderer.entity.RenderLunarWolf;
import com.kentington.thaumichorizons.client.renderer.entity.RenderMeatSlime;
import com.kentington.thaumichorizons.client.renderer.entity.RenderMedSlime;
import com.kentington.thaumichorizons.client.renderer.entity.RenderMercurialSlime;
import com.kentington.thaumichorizons.client.renderer.entity.RenderNetherHound;
import com.kentington.thaumichorizons.client.renderer.entity.RenderNightmare;
import com.kentington.thaumichorizons.client.renderer.entity.RenderOreBoar;
import com.kentington.thaumichorizons.client.renderer.entity.RenderScholarChicken;
import com.kentington.thaumichorizons.client.renderer.entity.RenderSeawolf;
import com.kentington.thaumichorizons.client.renderer.entity.RenderSheeder;
import com.kentington.thaumichorizons.client.renderer.entity.RenderSoul;
import com.kentington.thaumichorizons.client.renderer.entity.RenderSyringe;
import com.kentington.thaumichorizons.client.renderer.entity.RenderTaintfeeder;
import com.kentington.thaumichorizons.client.renderer.entity.RenderVoltSlime;
import com.kentington.thaumichorizons.client.renderer.item.ItemCorpseEffigyRender;
import com.kentington.thaumichorizons.client.renderer.item.ItemInjectorRender;
import com.kentington.thaumichorizons.client.renderer.item.ItemSyringeRender;
import com.kentington.thaumichorizons.client.renderer.model.ModelFamiliar;
import com.kentington.thaumichorizons.client.renderer.model.ModelGolemTH;
import com.kentington.thaumichorizons.client.renderer.tile.ItemJarTHRenderer;
import com.kentington.thaumichorizons.client.renderer.tile.TileBloodInfuserRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileCloudRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileEssentiaDynamoRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileEtherealShardRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileInspiratronRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileJarTHRenderer;
import com.kentington.thaumichorizons.client.renderer.tile.TileNodeMonitorRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileRecombinatorRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileSlotRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileSoulBeaconRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileSoulSieveRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileSoulforgeRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileSpikeRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileTransductionAmplifierRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileVatMatrixRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileVatSlaveRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileVisDynamoRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileVortexRender;
import com.kentington.thaumichorizons.client.renderer.tile.TileVortexStabilizerRender;
import com.kentington.thaumichorizons.common.CommonProxy;
import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityAlchemitePrimed;
import com.kentington.thaumichorizons.common.entities.EntityBlastPhial;
import com.kentington.thaumichorizons.common.entities.EntityBoatGreatwood;
import com.kentington.thaumichorizons.common.entities.EntityBoatThaumium;
import com.kentington.thaumichorizons.common.entities.EntityChocolateCow;
import com.kentington.thaumichorizons.common.entities.EntityEndersteed;
import com.kentington.thaumichorizons.common.entities.EntityFamiliar;
import com.kentington.thaumichorizons.common.entities.EntityGoldChicken;
import com.kentington.thaumichorizons.common.entities.EntityGolemTH;
import com.kentington.thaumichorizons.common.entities.EntityGravekeeper;
import com.kentington.thaumichorizons.common.entities.EntityGuardianPanther;
import com.kentington.thaumichorizons.common.entities.EntityLightningBoltFinite;
import com.kentington.thaumichorizons.common.entities.EntityLunarWolf;
import com.kentington.thaumichorizons.common.entities.EntityMeatSlime;
import com.kentington.thaumichorizons.common.entities.EntityMedSlime;
import com.kentington.thaumichorizons.common.entities.EntityMercurialSlime;
import com.kentington.thaumichorizons.common.entities.EntityNetherHound;
import com.kentington.thaumichorizons.common.entities.EntityNightmare;
import com.kentington.thaumichorizons.common.entities.EntityOrePig;
import com.kentington.thaumichorizons.common.entities.EntityScholarChicken;
import com.kentington.thaumichorizons.common.entities.EntitySeawolf;
import com.kentington.thaumichorizons.common.entities.EntitySheeder;
import com.kentington.thaumichorizons.common.entities.EntitySoul;
import com.kentington.thaumichorizons.common.entities.EntitySyringe;
import com.kentington.thaumichorizons.common.entities.EntityTaintPig;
import com.kentington.thaumichorizons.common.entities.EntityVoltSlime;
import com.kentington.thaumichorizons.common.items.WandManagerTH;
import com.kentington.thaumichorizons.common.lib.THKeyHandler;
import com.kentington.thaumichorizons.common.tiles.TileBloodInfuser;
import com.kentington.thaumichorizons.common.tiles.TileCloud;
import com.kentington.thaumichorizons.common.tiles.TileEssentiaDynamo;
import com.kentington.thaumichorizons.common.tiles.TileInspiratron;
import com.kentington.thaumichorizons.common.tiles.TileNodeMonitor;
import com.kentington.thaumichorizons.common.tiles.TileRecombinator;
import com.kentington.thaumichorizons.common.tiles.TileSlot;
import com.kentington.thaumichorizons.common.tiles.TileSoulBeacon;
import com.kentington.thaumichorizons.common.tiles.TileSoulExtractor;
import com.kentington.thaumichorizons.common.tiles.TileSoulJar;
import com.kentington.thaumichorizons.common.tiles.TileSoulforge;
import com.kentington.thaumichorizons.common.tiles.TileSpike;
import com.kentington.thaumichorizons.common.tiles.TileSyntheticNode;
import com.kentington.thaumichorizons.common.tiles.TileTransductionAmplifier;
import com.kentington.thaumichorizons.common.tiles.TileVat;
import com.kentington.thaumichorizons.common.tiles.TileVatMatrix;
import com.kentington.thaumichorizons.common.tiles.TileVatSlave;
import com.kentington.thaumichorizons.common.tiles.TileVisDynamo;
import com.kentington.thaumichorizons.common.tiles.TileVortex;
import com.kentington.thaumichorizons.common.tiles.TileVortexStabilizer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXBurst;
import thaumcraft.client.fx.particles.FXSparkle;
import thaumcraft.client.fx.particles.FXWisp;
import thaumcraft.client.renderers.item.ItemWandRenderer;
import thaumcraft.common.Thaumcraft;

public class ClientProxy extends CommonProxy {

    public IWandTriggerManager wandManager;

    public ClientProxy() {
        this.wandManager = new WandManagerTH();
    }

    @Override
    public void registerHandlers() {
        MinecraftForge.EVENT_BUS.register(ThaumicHorizons.instance.renderEventHandler);
        final IResourceManager resourceManager = Minecraft.getMinecraft().getResourceManager();
        if (resourceManager instanceof IReloadableResourceManager) {
            ((IReloadableResourceManager) resourceManager).registerReloadListener(ignored -> FXSonic.model = null);
        }
    }

    @Override
    public void registerKeyBindings() {
        FMLCommonHandler.instance().bus().register(new THKeyHandler());
    }

    @Override
    public void registerRenderers() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileNodeMonitor.class, new TileNodeMonitorRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSyntheticNode.class, new TileEtherealShardRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVisDynamo.class, new TileVisDynamoRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEssentiaDynamo.class, new TileEssentiaDynamoRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSoulExtractor.class, new TileSoulSieveRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileInspiratron.class, new TileInspiratronRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSoulforge.class, new TileSoulforgeRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVatSlave.class, new TileVatSlaveRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVatMatrix.class, new TileVatMatrixRender(0));
        ClientRegistry.bindTileEntitySpecialRenderer(TileBloodInfuser.class, new TileBloodInfuserRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSoulBeacon.class, new TileSoulBeaconRender());
        ClientRegistry
                .bindTileEntitySpecialRenderer(TileTransductionAmplifier.class, new TileTransductionAmplifierRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileRecombinator.class, new TileRecombinatorRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVortexStabilizer.class, new TileVortexStabilizerRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileVortex.class, new TileVortexRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSpike.class, new TileSpikeRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileCloud.class, new TileCloudRender());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSlot.class, new TileSlotRender());
        RenderingRegistry.registerEntityRenderingHandler(EntityAlchemitePrimed.class, new RenderAlchemitePrimed());
        RenderingRegistry.registerEntityRenderingHandler(EntitySyringe.class, new RenderSyringe());
        RenderingRegistry.registerEntityRenderingHandler(EntityBlastPhial.class, new BlastPhialRender());
        RenderingRegistry
                .registerEntityRenderingHandler(EntityChocolateCow.class, new RenderChocolateCow(new ModelCow(), 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityOrePig.class,
                new RenderOreBoar(new ModelPig(), new ModelPig(0.5f), 0.7f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityGuardianPanther.class,
                new RenderGuardianPanther(new ModelOcelot(), 1.0f));
        RenderingRegistry
                .registerEntityRenderingHandler(EntityFamiliar.class, new RenderFamiliar(new ModelFamiliar(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityGravekeeper.class,
                new RenderGravekeeper(new ModelOcelot(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityGoldChicken.class,
                new RenderGoldChicken(new ModelChicken(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityScholarChicken.class,
                new RenderScholarChicken(new ModelChicken(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityTaintPig.class,
                new RenderTaintfeeder(new ModelPig(), new ModelPig(0.5f), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityNetherHound.class,
                new RenderNetherHound(new ModelWolf(), new ModelWolf(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntitySeawolf.class,
                new RenderSeawolf(new ModelWolf(), new ModelWolf(), 0.5f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityLunarWolf.class,
                new RenderLunarWolf(new ModelWolf(), new ModelWolf(), 0.5f));
        RenderingRegistry
                .registerEntityRenderingHandler(EntityGolemTH.class, new RenderGolemTH(new ModelGolemTH(false)));
        RenderingRegistry
                .registerEntityRenderingHandler(EntityEndersteed.class, new RenderEndersteed(new ModelHorse(), 0.75f));
        RenderingRegistry
                .registerEntityRenderingHandler(EntityNightmare.class, new RenderNightmare(new ModelHorse(), 0.75f));
        RenderingRegistry.registerEntityRenderingHandler(EntityBoatGreatwood.class, new RenderBoatGreatwood());
        RenderingRegistry.registerEntityRenderingHandler(EntityBoatThaumium.class, new RenderBoatThaumium());
        RenderingRegistry.registerEntityRenderingHandler(
                EntityMeatSlime.class,
                new RenderMeatSlime(new ModelSlime(16), new ModelSlime(0), 0.25f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityMercurialSlime.class,
                new RenderMercurialSlime(new ModelSlime(16), new ModelSlime(0), 0.25f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityVoltSlime.class,
                new RenderVoltSlime(new ModelSlime(16), new ModelSlime(0), 0.25f));
        RenderingRegistry.registerEntityRenderingHandler(
                EntityMedSlime.class,
                new RenderMedSlime(new ModelSlime(16), new ModelSlime(0), 0.25f));
        RenderingRegistry.registerEntityRenderingHandler(EntitySheeder.class, new RenderSheeder());
        RenderingRegistry.registerEntityRenderingHandler(EntitySoul.class, new RenderSoul());
        RenderingRegistry
                .registerEntityRenderingHandler(EntityLightningBoltFinite.class, new RenderLightningBoltFinite());
        MinecraftForgeClient.registerItemRenderer(ThaumicHorizons.itemSyringeBloodSample, new ItemSyringeRender());
        MinecraftForgeClient.registerItemRenderer(ThaumicHorizons.itemSyringeHuman, new ItemSyringeRender());
        MinecraftForgeClient.registerItemRenderer(ThaumicHorizons.itemSyringeEmpty, new ItemSyringeRender());
        MinecraftForgeClient.registerItemRenderer(ThaumicHorizons.itemSyringeInjection, new ItemSyringeRender());
        MinecraftForgeClient.registerItemRenderer(ThaumicHorizons.itemCorpseEffigy, new ItemCorpseEffigyRender());
        MinecraftForgeClient.registerItemRenderer(ThaumicHorizons.itemInjector, new ItemInjectorRender());
        MinecraftForgeClient.registerItemRenderer(ThaumicHorizons.itemWandCastingDisposable, new ItemWandRenderer());
    }

    @Override
    public Object getClientGuiElement(final int ID, final EntityPlayer player, final World world, final int x,
            final int y, final int z) {
        if (world instanceof WorldClient) {
            switch (ID) {
                case 1 -> {
                    return new GuiVisDynamo(player, (TileVisDynamo) world.getTileEntity(x, y, z));
                }
                case 2 -> {
                    return new GuiSoulExtractor(player.inventory, (TileSoulExtractor) world.getTileEntity(x, y, z));
                }
                case 3 -> {
                    return new GuiInspiratron(player.inventory, (TileInspiratron) world.getTileEntity(x, y, z));
                }
                case 4 -> {
                    return new GuiSoulforge(player, (TileSoulforge) world.getTileEntity(x, y, z));
                }
                case 5 -> {
                    return new GuiBloodInfuser(player, (TileBloodInfuser) world.getTileEntity(x, y, z));
                }
                case 6 -> {
                    return new GuiInjector(player);
                }
                case 7 -> {
                    return new GuiVat(player, (TileVat) world.getTileEntity(x, y, z));
                }
                case 8 -> {
                    return new GuiCase(player.inventory, world, x, y, z);
                }
                case 9 -> {
                    return new GuiFingers(player.inventory);
                }
            }
        }
        return null;
    }

    @Override
    public void registerDisplayInformation() {
        ThaumicHorizons.blockJarRI = RenderingRegistry.getNextAvailableRenderId();
        MinecraftForgeClient
                .registerItemRenderer(Item.getItemFromBlock(ThaumicHorizons.blockJar), new ItemJarTHRenderer());
        RenderingRegistry.registerBlockHandler(new BlockJarTHRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileSoulJar.class, new TileJarTHRenderer());
        ThaumicHorizons.blockSyntheticNodeRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockSyntheticNodeRender());
        ThaumicHorizons.blockNodeMonRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockNodeMonitorRender());
        ThaumicHorizons.blockVisDynamoRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockVisDynamoRender());
        ThaumicHorizons.blockEssentiaDynamoRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockEssentiaDynamoRender());
        ThaumicHorizons.blockSoulSieveRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockSoulSieveRender());
        ThaumicHorizons.blockInspiratronRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockInspiratronRender());
        ThaumicHorizons.blockSoulforgeRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockSoulforgeRender());
        ThaumicHorizons.blockVatRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockVatRender());
        ThaumicHorizons.blockVatSolidRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockVatSolidRender());
        ThaumicHorizons.blockVatInteriorRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockVatInteriorRender());
        ThaumicHorizons.blockVatMatrixRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockVatMatrixRender());
        ThaumicHorizons.blockBloodInfuserRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockBloodInfuserRender());
        ThaumicHorizons.blockSoulBeaconRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockSoulBeaconRender());
        ThaumicHorizons.blockTransducerRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockTransductionAmplifierRender());
        ThaumicHorizons.blockRecombinatorRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockRecombinatorRender());
        ThaumicHorizons.blockVortexStabilizerRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockVortexStabilizerRender());
        ThaumicHorizons.blockSpikeRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockSpikeRenderer());
        ThaumicHorizons.blockSlotRI = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new BlockSlotRender());
    }

    @Override
    public void disintegrateFX(final double blockX, final double blockY, final double blockZ, final EntityPlayer p,
            final int howMany, final boolean enlarged) {
        if (enlarged) {
            for (int x = -1; x < 2; ++x) {
                for (int y = -1; y < 2; ++y) {
                    for (int z = -1; z < 2; ++z) {
                        for (int i = 0; i < howMany; ++i) {
                            final FXSparkle fx = new FXSparkle(
                                    p.worldObj,
                                    blockX + 0.5,
                                    blockY + 0.5,
                                    blockZ + 0.5,
                                    1.0f,
                                    0,
                                    6);
                            fx.motionX = (p.worldObj.rand.nextDouble() - 0.5) / 4.0;
                            fx.motionY = (p.worldObj.rand.nextDouble() - 0.5) / 4.0;
                            fx.motionZ = (p.worldObj.rand.nextDouble() - 0.5) / 4.0;
                            fx.noClip = true;
                            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
                        }
                    }
                }
            }
        } else {
            for (int j = 0; j < howMany; ++j) {
                final FXSparkle fx = new FXSparkle(p.worldObj, blockX + 0.5, blockY + 0.5, blockZ + 0.5, 1.0f, 0, 6);
                fx.motionX = (p.worldObj.rand.nextDouble() - 0.5) / 4.0;
                fx.motionY = (p.worldObj.rand.nextDouble() - 0.5) / 4.0;
                fx.motionZ = (p.worldObj.rand.nextDouble() - 0.5) / 4.0;
                fx.noClip = true;
                FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
            }
        }
    }

    @Override
    public void smeltFX(final double blockX, final double blockY, final double blockZ, final World w, final int howMany,
            final boolean enlarged) {
        if (enlarged) {
            for (int x = -1; x < 2; ++x) {
                for (int y = -1; y < 2; ++y) {
                    for (int z = -1; z < 2; ++z) {
                        for (int i = 0; i < howMany; ++i) {
                            final EntityFlameFX fx = new EntityFlameFX(
                                    w,
                                    blockX + 0.5 + x,
                                    blockY + 0.5 + y,
                                    blockZ + 0.5 + z,
                                    (w.rand.nextDouble() - 0.5) * 0.25,
                                    (w.rand.nextDouble() - 0.5) * 0.25,
                                    (w.rand.nextDouble() - 0.5) * 0.25);
                            fx.noClip = true;
                            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
                        }
                    }
                }
            }
        } else {
            for (int j = 0; j < howMany; ++j) {
                final EntityFlameFX fx = new EntityFlameFX(
                        w,
                        blockX + 0.5,
                        blockY + 0.5,
                        blockZ + 0.5,
                        (w.rand.nextDouble() - 0.5) * 0.25,
                        (w.rand.nextDouble() - 0.5) * 0.25,
                        (w.rand.nextDouble() - 0.5) * 0.25);
                fx.noClip = true;
                FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
            }
        }
    }

    @Override
    public void soulParticles(final int blockX, final int blockY, final int blockZ, final World world) {
        for (int i = 0; i < 10; ++i) {
            final EntitySpellParticleFX fx = new EntitySpellParticleFX(
                    world,
                    blockX + 0.5 + (world.rand.nextDouble() - 0.5) * 0.8,
                    blockY + 0.8,
                    blockZ + 0.5 + (world.rand.nextDouble() - 0.5) * 0.8,
                    0.0,
                    world.rand.nextDouble() * 0.25,
                    0.0);
            fx.noClip = true;
            FMLClientHandler.instance().getClient().effectRenderer.addEffect(fx);
        }
    }

    @Override
    public void containmentFX(final double blockX, final double blockY, final double blockZ, final EntityPlayer p,
            final Entity ent, final int times) {
        final double xSize = ent.boundingBox.maxX - ent.boundingBox.minX;
        final double ySize = ent.boundingBox.maxY - ent.boundingBox.minY;
        final double zSize = ent.boundingBox.maxZ - ent.boundingBox.minZ;
        final double radius = (xSize > ySize) ? (Math.max(xSize, zSize)) : (Math.max(ySize, zSize));
        final double xCenter = (ent.boundingBox.maxX + ent.boundingBox.minX) / 2.0;
        final double yCenter = (ent.boundingBox.maxY + ent.boundingBox.minY) / 2.0;
        final double zCenter = (ent.boundingBox.maxZ + ent.boundingBox.minZ) / 2.0;
        for (int i = 0; i < times; ++i) {
            double theta = p.worldObj.rand.nextDouble() * 3.141592653589793 * 2.0;
            double phi = p.worldObj.rand.nextDouble() * 3.141592653589793 * 2.0;
            final double z1 = zCenter + radius * Math.cos(phi);
            final double y1 = yCenter + radius * Math.sin(phi) * Math.sin(theta);
            final double x1 = xCenter + radius * Math.sin(phi) * Math.cos(theta);
            theta = p.worldObj.rand.nextDouble() * 3.141592653589793 * 2.0;
            phi = p.worldObj.rand.nextDouble() * 3.141592653589793 * 2.0;
            final double z2 = zCenter + radius * Math.cos(phi);
            final double y2 = yCenter + radius * Math.sin(phi) * Math.sin(theta);
            final double x2 = xCenter + radius * Math.sin(phi) * Math.cos(theta);
            Thaumcraft.proxy.arcLightning(
                    p.worldObj,
                    x1,
                    y1,
                    z1,
                    x2,
                    y2,
                    z2,
                    p.worldObj.rand.nextFloat() * 0.1f,
                    p.worldObj.rand.nextFloat() * 0.2f,
                    p.worldObj.rand.nextFloat() * 0.8f,
                    p.worldObj.rand.nextFloat());
        }
    }

    @Override
    public void disintegrateExplodeFX(final World worldObj, final double posX, final double posY, final double posZ) {
        final FXBurst ef = new FXBurst(worldObj, posX, posY, posZ, 1.0f);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(ef);
    }

    @Override
    public void illuminationFX(final World world, final int xCoord, final int yCoord, final int zCoord, final int md,
            final Color col) {
        if (world.rand.nextInt(9 - Thaumcraft.proxy.particleCount(2)) == 0) {
            final FXWisp ef = new FXWisp(
                    world,
                    xCoord + 0.55f,
                    yCoord + 0.5f,
                    zCoord + 0.55f,
                    0.5f,
                    col.getRed() / 255.0f + 0.01f,
                    col.getGreen() / 255.0f,
                    col.getBlue() / 255.0f);
            ef.setGravity(0.0f);
            ef.shrink = false;
            if (md == 0) {
                ef.blendmode = 0;
            }
            ParticleEngine.instance.addEffect(world, ef);
        }
    }

    @Override
    public void blockSplosionFX(final int x, final int y, final int z, final Block block, final int md) {
        Minecraft.getMinecraft().effectRenderer.addBlockDestroyEffects(x, y, z, block, md);
    }

    @Override
    public void alchemiteFX(final World worldObj, final double x, final double y, final double z) {
        final FXBurst ef = new FXBurst(worldObj, x, y, z, 10.0f);
        FMLClientHandler.instance().getClient().effectRenderer.addEffect(ef);
    }

    @Override
    public boolean readyToRender() {
        return FMLClientHandler.instance().getClient().renderViewEntity != null;
    }

    @Override
    public void addEffect(final Entity entity) {
        FMLClientHandler.instance().getClient().effectRenderer.addEffect((EntityFX) entity);
    }

    @Override
    public void lightningBolt(final World worldObj, final double x, final double y, final double z,
            final int boltLength) {
        Thaumcraft.proxy.arcLightning(
                worldObj,
                x + worldObj.rand.nextFloat() - 0.5,
                y + boltLength + 0.5,
                z + worldObj.rand.nextFloat() - 0.5,
                x + worldObj.rand.nextFloat() - 0.5,
                y + 1.0,
                z + worldObj.rand.nextFloat() - 0.5,
                0.8f,
                1.0f,
                1.0f,
                0.1f);
    }

    private final Map<TileCoord, VortexSound> activeVortexSounds = new HashMap<>();

    @Override
    public void playVortexSound(TileVortex tile) {
        if (tile == null || tile.isInvalid() || tile.getWorldObj() == null) return;

        TileCoord pos = new TileCoord(tile.xCoord, tile.yCoord, tile.zCoord);
        if (activeVortexSounds.containsKey(pos)) return;

        VortexSound sound = new VortexSound(tile);
        Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        activeVortexSounds.put(pos, sound);
    }

    @Override
    public void stopVortexSound(TileVortex tile) {
        if (tile == null) return;

        TileCoord pos = new TileCoord(tile.xCoord, tile.yCoord, tile.zCoord);
        VortexSound sound = activeVortexSounds.remove(pos);
        if (sound != null) {
            sound.markDone();
        }
    }

    // Inner class to replace BlockPos for 1.7.10
    private static class TileCoord {

        final int x, y, z;

        TileCoord(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TileCoord)) return false;
            TileCoord other = (TileCoord) o;
            return this.x == other.x && this.y == other.y && this.z == other.z;
        }

        @Override
        public int hashCode() {
            return x * 31 * 31 + y * 31 + z;
        }
    }

}
