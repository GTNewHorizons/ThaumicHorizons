//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.client.lib;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.kentington.thaumichorizons.common.ThaumicHorizons;
import com.kentington.thaumichorizons.common.entities.EntityBoatThaumium;
import com.kentington.thaumichorizons.common.items.lenses.ILens;
import com.kentington.thaumichorizons.common.items.lenses.ItemLensCase;
import com.kentington.thaumichorizons.common.items.lenses.LensManager;
import com.kentington.thaumichorizons.common.lib.THKeyHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketHandler;
import com.kentington.thaumichorizons.common.lib.networking.PacketLensChangeToServer;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.nodes.IRevealer;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;

public class RenderEventHandler {

    static float radialHudScale;
    TreeMap<String, Integer> foci;
    HashMap<String, ItemStack> fociItem;
    HashMap<String, Boolean> fociHover;
    HashMap<String, Float> fociScale;
    long lastTime;
    boolean lastState;
    float breakProgress;
    public int cacheX;
    public int cacheY;
    public int cacheZ;
    int evanescentStage;
    Block[][] tempBlock;
    int[][] tempMD;
    public ForgeDirection tempDir;
    public ArrayList<EntityLivingBase> thingsThatSparkle;

    public RenderEventHandler() {
        this.foci = new TreeMap<>();
        this.fociItem = new HashMap<>();
        this.fociHover = new HashMap<>();
        this.fociScale = new HashMap<>();
        this.lastTime = 0L;
        this.lastState = false;
        this.breakProgress = 0.0f;
        this.cacheX = Integer.MAX_VALUE;
        this.cacheY = Integer.MAX_VALUE;
        this.cacheZ = Integer.MAX_VALUE;
        this.evanescentStage = 0;
        this.tempBlock = new Block[3][3];
        this.tempMD = new int[3][3];
        this.thingsThatSparkle = new ArrayList<>();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderOverlay(final RenderGameOverlayEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final long time = System.nanoTime() / 1000000L;
        if (event.type == RenderGameOverlayEvent.ElementType.TEXT) {
            this.handleFociRadial(mc, time, event);
            final ItemStack goggles = mc.thePlayer.inventory.armorItemInSlot(3);
            if (LensManager.nightVisionOffTime > 0L
                    && (goggles == null || !(goggles.getItem() instanceof IRevealer)
                            || goggles.stackTagCompound == null)
                    && mc.thePlayer.getActivePotionEffect(Potion.nightVision) != null
                    && mc.thePlayer.getActivePotionEffect(Potion.nightVision).getIsAmbient()) {
                mc.thePlayer.removePotionEffect(Potion.nightVision.id);
                LensManager.nightVisionOffTime = 0L;
            }
            if (goggles != null && goggles.getItem() instanceof IRevealer
                    && goggles.stackTagCompound != null
                    && goggles.stackTagCompound.getString("Lens") != null
                    && !goggles.stackTagCompound.getString("Lens").equals("")) {
                final ILens theLens = (ILens) LensManager.getLens(goggles.stackTagCompound.getString("Lens"));
                if (theLens != null) {
                    theLens.handleRender(mc, event.partialTicks);
                }
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void handleFociRadial(final Minecraft mc, final long time, final RenderGameOverlayEvent event) {
        if (THKeyHandler.radialActive || RenderEventHandler.radialHudScale > 0.0f) {
            final long timeDiff = System.currentTimeMillis() - THKeyHandler.lastPressV;
            if (THKeyHandler.radialActive) {
                if (mc.currentScreen != null) {
                    THKeyHandler.radialActive = false;
                    THKeyHandler.radialLock = true;
                    mc.setIngameFocus();
                    mc.setIngameNotInFocus();
                    return;
                }
                if (RenderEventHandler.radialHudScale == 0.0f) {
                    this.foci.clear();
                    this.fociItem.clear();
                    this.fociHover.clear();
                    this.fociScale.clear();
                    int pouchcount = 0;
                    ItemStack item = null;
                    final IInventory baubles = BaublesApi.getBaubles(mc.thePlayer);
                    for (int a = 0; a < 4; ++a) {
                        if (baubles.getStackInSlot(a) != null
                                && baubles.getStackInSlot(a).getItem() instanceof ItemLensCase) {
                            ++pouchcount;
                            item = baubles.getStackInSlot(a);
                            final ItemStack[] inv = ((ItemLensCase) item.getItem()).getInventory(item);
                            for (int q = 0; q < inv.length; ++q) {
                                item = inv[q];
                                if (item != null && item.getItem() instanceof ILens) {
                                    this.foci.put(((ILens) item.getItem()).lensName(), q + pouchcount * 1000);
                                    this.fociItem.put(((ILens) item.getItem()).lensName(), item.copy());
                                    this.fociScale.put(((ILens) item.getItem()).lensName(), 1.0f);
                                    this.fociHover.put(((ILens) item.getItem()).lensName(), false);
                                }
                            }
                        }
                    }
                    for (int a = 0; a < 36; ++a) {
                        item = mc.thePlayer.inventory.mainInventory[a];
                        if (item != null && item.getItem() instanceof ILens) {
                            this.foci.put(((ILens) item.getItem()).lensName(), a);
                            this.fociItem.put(((ILens) item.getItem()).lensName(), item.copy());
                            this.fociScale.put(((ILens) item.getItem()).lensName(), 1.0f);
                            this.fociHover.put(((ILens) item.getItem()).lensName(), false);
                        }
                        if (item != null && item.getItem() instanceof ItemLensCase) {
                            ++pouchcount;
                            final ItemStack[] inv = ((ItemLensCase) item.getItem()).getInventory(item);
                            for (int q = 0; q < inv.length; ++q) {
                                item = inv[q];
                                if (item != null && item.getItem() instanceof ILens) {
                                    this.foci.put(((ILens) item.getItem()).lensName(), q + pouchcount * 1000);
                                    this.fociItem.put(((ILens) item.getItem()).lensName(), item.copy());
                                    this.fociScale.put(((ILens) item.getItem()).lensName(), 1.0f);
                                    this.fociHover.put(((ILens) item.getItem()).lensName(), false);
                                }
                            }
                        }
                    }
                    if (this.foci.size() > 0 && mc.inGameHasFocus) {
                        mc.inGameHasFocus = false;
                        mc.mouseHelper.ungrabMouseCursor();
                    }
                }
            } else if (mc.currentScreen == null && this.lastState) {
                if (Display.isActive() && !mc.inGameHasFocus) {
                    mc.inGameHasFocus = true;
                    mc.mouseHelper.grabMouseCursor();
                }
                this.lastState = false;
            }
            this.renderFocusRadialHUD(
                    event.resolution.getScaledWidth_double(),
                    event.resolution.getScaledHeight_double(),
                    time,
                    event.partialTicks);
            if (time > this.lastTime) {
                for (final String key : this.fociHover.keySet()) {
                    if (this.fociHover.get(key)) {
                        if (!THKeyHandler.radialActive && !THKeyHandler.radialLock) {
                            PacketHandler.INSTANCE.sendToServer(new PacketLensChangeToServer(mc.thePlayer, key));
                            THKeyHandler.radialLock = true;
                        }
                        if (this.fociScale.get(key) >= 1.3f) {
                            continue;
                        }
                        this.fociScale.put(key, this.fociScale.get(key) + 0.025f);
                    } else {
                        if (this.fociScale.get(key) <= 1.0f) {
                            continue;
                        }
                        this.fociScale.put(key, this.fociScale.get(key) - 0.025f);
                    }
                }
                if (!THKeyHandler.radialActive) {
                    RenderEventHandler.radialHudScale -= 0.05f;
                } else if (THKeyHandler.radialActive && RenderEventHandler.radialHudScale < 1.0f) {
                    RenderEventHandler.radialHudScale += 0.05f;
                }
                if (RenderEventHandler.radialHudScale > 1.0f) {
                    RenderEventHandler.radialHudScale = 1.0f;
                }
                if (RenderEventHandler.radialHudScale < 0.0f) {
                    RenderEventHandler.radialHudScale = 0.0f;
                    THKeyHandler.radialLock = false;
                }
                this.lastTime = time + 5L;
                this.lastState = THKeyHandler.radialActive;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private void renderFocusRadialHUD(final double sw, final double sh, final long time, final float partialTicks) {
        final RenderItem ri = new RenderItem();
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer.inventory.armorItemInSlot(3) == null
                || !(mc.thePlayer.inventory.armorItemInSlot(3).getItem() instanceof IRevealer)) {
            return;
        }
        final ItemStack goggles = mc.thePlayer.inventory.armorItemInSlot(3);
        ILens lens = null;
        if (goggles.stackTagCompound != null) {
            lens = (ILens) LensManager.getLens(goggles.stackTagCompound.getString("Lens"));
        }
        final int i = (int) (Mouse.getEventX() * sw / mc.displayWidth);
        final int j = (int) (sh - Mouse.getEventY() * sh / mc.displayHeight - 1.0);
        final int k = Mouse.getEventButton();
        if (this.fociItem.size() == 0) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, sw, sh, 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -2000.0f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glTranslated(sw / 2.0, sh / 2.0, 0.0);
        ItemStack tt = null;
        final float width = 16.0f + this.fociItem.size() * 2.5f;
        UtilsFX.bindTexture("textures/misc/radial.png");
        GL11.glPushMatrix();
        GL11.glRotatef(partialTicks + mc.thePlayer.ticksExisted % 720 / 2.0f, 0.0f, 0.0f, 1.0f);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        UtilsFX.renderQuadCenteredFromTexture(
                width * 2.75f * RenderEventHandler.radialHudScale,
                0.5f,
                0.5f,
                0.5f,
                200,
                771,
                0.5f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glPopMatrix();
        UtilsFX.bindTexture("textures/misc/radial2.png");
        GL11.glPushMatrix();
        GL11.glRotatef(-(partialTicks + mc.thePlayer.ticksExisted % 720 / 2.0f), 0.0f, 0.0f, 1.0f);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        UtilsFX.renderQuadCenteredFromTexture(
                width * 2.55f * RenderEventHandler.radialHudScale,
                0.5f,
                0.5f,
                0.5f,
                200,
                771,
                0.5f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glPopMatrix();
        if (lens != null) {
            GL11.glPushMatrix();
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            final ItemStack item = new ItemStack((Item) lens);
            item.stackTagCompound = null;
            ri.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, item, -8, -8);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            final int mx = (int) (i - sw / 2.0);
            final int my = (int) (j - sh / 2.0);
            if (mx >= -10 && mx <= 10 && my >= -10 && my <= 10) {
                tt = new ItemStack((Item) lens);
            }
        }
        GL11.glScaled(
                RenderEventHandler.radialHudScale,
                RenderEventHandler.radialHudScale,
                RenderEventHandler.radialHudScale);
        float currentRot = -90.0f * RenderEventHandler.radialHudScale;
        final float pieSlice = 360.0f / this.fociItem.size();
        String key = this.foci.firstKey();
        for (int a = 0; a < this.fociItem.size(); ++a) {
            final double xx = MathHelper.cos(currentRot / 180.0f * (float) Math.PI) * width;
            final double yy = MathHelper.sin(currentRot / 180.0f * (float) Math.PI) * width;
            currentRot += pieSlice;
            GL11.glPushMatrix();
            GL11.glTranslated(xx, yy, 100.0);
            GL11.glScalef(this.fociScale.get(key), this.fociScale.get(key), this.fociScale.get(key));
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableGUIStandardItemLighting();
            final ItemStack item2 = this.fociItem.get(key).copy();
            item2.stackTagCompound = null;
            ri.renderItemIntoGUI(mc.fontRenderer, mc.renderEngine, item2, -8, -8);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            if (!THKeyHandler.radialLock && THKeyHandler.radialActive) {
                final int mx2 = (int) (i - sw / 2.0 - xx);
                final int my2 = (int) (j - sh / 2.0 - yy);
                if (mx2 >= -10 && mx2 <= 10 && my2 >= -10 && my2 <= 10) {
                    this.fociHover.put(key, true);
                    tt = this.fociItem.get(key);
                    if (k == 0) {
                        THKeyHandler.radialActive = false;
                        THKeyHandler.radialLock = true;
                        PacketHandler.INSTANCE.sendToServer(new PacketLensChangeToServer(mc.thePlayer, key));
                        break;
                    }
                } else {
                    this.fociHover.put(key, false);
                }
            }
            key = this.foci.higherKey(key);
        }
        GL11.glPopMatrix();
        if (tt != null) {
            UtilsFX.drawCustomTooltip(
                    mc.currentScreen,
                    ri,
                    mc.fontRenderer,
                    tt.getTooltip(mc.thePlayer, mc.gameSettings.advancedItemTooltips),
                    -4,
                    20,
                    11);
        }
        GL11.glDepthMask(true);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void blockHighlight(final DrawBlockHighlightEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final ItemStack goggles = mc.thePlayer.inventory.armorItemInSlot(3);
        if (goggles != null && goggles.getItem() instanceof IRevealer && goggles.stackTagCompound != null) {
            if (goggles.stackTagCompound.getString("Lens") != null
                    && !goggles.stackTagCompound.getString("Lens").equals("")) {
                final ILens theLens = (ILens) LensManager.getLens(goggles.stackTagCompound.getString("Lens"));
                if (theLens == ThaumicHorizons.itemLensEarth) {
                    if (this.cacheX != event.target.blockX || this.cacheY != event.target.blockY
                            || this.cacheZ != event.target.blockZ
                            || this.tempDir != ForgeDirection.getOrientation(event.target.sideHit)) {
                        this.resetBlocks(mc.thePlayer);
                    }
                    if (event.player.worldObj.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ)
                            .getMaterial() != Material.air) {
                        this.cacheX = event.target.blockX;
                        this.cacheY = event.target.blockY;
                        this.cacheZ = event.target.blockZ;
                        this.tempDir = ForgeDirection.getOrientation(event.target.sideHit);
                    } else {
                        this.resetBlocks(mc.thePlayer);
                    }
                } else {
                    this.resetBlocks(mc.thePlayer);
                }
            } else {
                this.resetBlocks(mc.thePlayer);
            }
        } else if (this.evanescentStage != 0) {
            this.resetBlocks(mc.thePlayer);
        }
    }

    void setBlocksEvanescent(final EntityPlayer p) {
        if (p.isSwingInProgress) {
            this.breakProgress += p.worldObj.getBlock(this.cacheX, this.cacheY, this.cacheZ)
                    .getPlayerRelativeBlockHardness(p, p.worldObj, this.cacheX, this.cacheY, this.cacheZ);
            if (this.breakProgress > 1.0f) {
                Minecraft.getMinecraft().getNetHandler().addToSendQueue(
                        new C07PacketPlayerDigging(2, p.getEntityId(), this.cacheX, this.cacheY, this.cacheZ));
                // TODO: is it correct? X, Y, and Z are passed not as usually expected.
                Minecraft.getMinecraft().playerController
                        .onPlayerDestroyBlock(p.getEntityId(), this.cacheX, this.cacheY, this.cacheZ);
                this.breakProgress = 0.0f;
                return;
            }
        }
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                if (this.tempDir == ForgeDirection.UP || this.tempDir == ForgeDirection.DOWN) {
                    if (p.worldObj.getTileEntity(this.cacheX + i, this.cacheY, this.cacheZ + j) == null
                            && p.worldObj.getBlock(this.cacheX + i, this.cacheY, this.cacheZ + j).getLightValue() == 0
                            && p.worldObj.getBlock(this.cacheX + i, this.cacheY, this.cacheZ + j).canCollideCheck(
                                    p.worldObj.getBlockMetadata(this.cacheX + i, this.cacheY, this.cacheZ + j),
                                    false)) {
                        if (p.worldObj.getBlock(this.cacheX + i, this.cacheY, this.cacheZ + j)
                                != ThaumicHorizons.blockEvanescent) {
                            this.tempBlock[i + 1][j + 1] = p.worldObj
                                    .getBlock(this.cacheX + i, this.cacheY, this.cacheZ + j);
                            this.tempMD[i + 1][j + 1] = p.worldObj
                                    .getBlockMetadata(this.cacheX + i, this.cacheY, this.cacheZ + j);
                        }
                        if (this.tempBlock[i + 1][j + 1].getMaterial() != Material.air) {
                            p.worldObj.setBlock(
                                    this.cacheX + i,
                                    this.cacheY,
                                    this.cacheZ + j,
                                    ThaumicHorizons.blockEvanescent);
                        }
                    } else if (p.worldObj.getBlock(this.cacheX + i, this.cacheY, this.cacheZ + j)
                            != ThaumicHorizons.blockEvanescent) {
                                this.tempBlock[i + 1][j + 1] = null;
                            }
                } else if (this.tempDir == ForgeDirection.NORTH || this.tempDir == ForgeDirection.SOUTH) {
                    if (p.worldObj.getTileEntity(this.cacheX + i, this.cacheY + j, this.cacheZ) == null
                            && p.worldObj.getBlock(this.cacheX + i, this.cacheY + j, this.cacheZ).getLightValue() == 0
                            && p.worldObj.getBlock(this.cacheX + i, this.cacheY + j, this.cacheZ).canCollideCheck(
                                    p.worldObj.getBlockMetadata(this.cacheX + i, this.cacheY + j, this.cacheZ),
                                    false)) {
                        if (p.worldObj.getBlock(this.cacheX + i, this.cacheY + j, this.cacheZ)
                                != ThaumicHorizons.blockEvanescent) {
                            this.tempBlock[i + 1][j + 1] = p.worldObj
                                    .getBlock(this.cacheX + i, this.cacheY + j, this.cacheZ);
                            this.tempMD[i + 1][j + 1] = p.worldObj
                                    .getBlockMetadata(this.cacheX + i, this.cacheY + j, this.cacheZ);
                        }
                        if (this.tempBlock[i + 1][j + 1].getMaterial() != Material.air) {
                            p.worldObj.setBlock(
                                    this.cacheX + i,
                                    this.cacheY + j,
                                    this.cacheZ,
                                    ThaumicHorizons.blockEvanescent);
                        }
                    } else if (p.worldObj.getBlock(this.cacheX + i, this.cacheY + j, this.cacheZ)
                            != ThaumicHorizons.blockEvanescent) {
                                this.tempBlock[i + 1][j + 1] = null;
                            }
                } else if (p.worldObj.getTileEntity(this.cacheX, this.cacheY + j, this.cacheZ + i) == null
                        && p.worldObj.getBlock(this.cacheX, this.cacheY + j, this.cacheZ + i).getLightValue() == 0
                        && p.worldObj.getBlock(this.cacheX, this.cacheY + j, this.cacheZ + i).canCollideCheck(
                                p.worldObj.getBlockMetadata(this.cacheX, this.cacheY + j, this.cacheZ + i),
                                false)) {
                                    if (p.worldObj.getBlock(this.cacheX, this.cacheY + j, this.cacheZ + i)
                                            != ThaumicHorizons.blockEvanescent) {
                                        this.tempBlock[i + 1][j + 1] = p.worldObj
                                                .getBlock(this.cacheX, this.cacheY + j, this.cacheZ + i);
                                        this.tempMD[i + 1][j + 1] = p.worldObj
                                                .getBlockMetadata(this.cacheX, this.cacheY + j, this.cacheZ + i);
                                    }
                                    if (this.tempBlock[i + 1][j + 1].getMaterial() != Material.air) {
                                        p.worldObj.setBlock(
                                                this.cacheX,
                                                this.cacheY + j,
                                                this.cacheZ + i,
                                                ThaumicHorizons.blockEvanescent);
                                    }
                                } else
                    if (p.worldObj.getBlock(this.cacheX, this.cacheY + j, this.cacheZ + i)
                            != ThaumicHorizons.blockEvanescent) {
                                this.tempBlock[i + 1][j + 1] = null;
                            }
            }
        }
        this.evanescentStage = 2;
    }

    public void resetBlocks(final EntityPlayer p) {
        this.breakProgress = 0.0f;
        for (int i = -1; i < 2; ++i) {
            for (int j = -1; j < 2; ++j) {
                if (this.tempDir == ForgeDirection.UP || this.tempDir == ForgeDirection.DOWN) {
                    if (this.tempBlock[i + 1][j + 1] != null
                            && p.worldObj.getBlock(this.cacheX + i, this.cacheY, this.cacheZ + j)
                                    == ThaumicHorizons.blockEvanescent) {
                        p.worldObj.setBlock(
                                this.cacheX + i,
                                this.cacheY,
                                this.cacheZ + j,
                                this.tempBlock[i + 1][j + 1],
                                this.tempMD[i + 1][j + 1],
                                4);
                    }
                } else if (this.tempDir == ForgeDirection.NORTH || this.tempDir == ForgeDirection.SOUTH) {
                    if (this.tempBlock[i + 1][j + 1] != null
                            && p.worldObj.getBlock(this.cacheX + i, this.cacheY + j, this.cacheZ)
                                    == ThaumicHorizons.blockEvanescent) {
                        p.worldObj.setBlock(
                                this.cacheX + i,
                                this.cacheY + j,
                                this.cacheZ,
                                this.tempBlock[i + 1][j + 1],
                                this.tempMD[i + 1][j + 1],
                                4);
                    }
                } else if (this.tempBlock[i + 1][j + 1] != null
                        && p.worldObj.getBlock(this.cacheX, this.cacheY + j, this.cacheZ + i)
                                == ThaumicHorizons.blockEvanescent) {
                                    p.worldObj.setBlock(
                                            this.cacheX,
                                            this.cacheY + j,
                                            this.cacheZ + i,
                                            this.tempBlock[i + 1][j + 1],
                                            this.tempMD[i + 1][j + 1],
                                            4);
                                }
            }
        }
        this.tempBlock = new Block[3][3];
        this.tempMD = new int[3][3];
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderTick(final TickEvent.RenderTickEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }
        final ItemStack goggles = mc.thePlayer.inventory.armorItemInSlot(3);
        if (goggles != null && goggles.getItem() instanceof IRevealer
                && goggles.stackTagCompound != null
                && goggles.stackTagCompound.getString("Lens") != null
                && !goggles.stackTagCompound.getString("Lens").equals("")) {
            final ILens theLens = (ILens) LensManager.getLens(goggles.stackTagCompound.getString("Lens"));
            if (theLens == ThaumicHorizons.itemLensEarth) {
                this.setBlocksEvanescent(mc.thePlayer);
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderLast(final RenderWorldLastEvent event) {
        for (final EntityLivingBase entity : this.thingsThatSparkle) {
            if (Minecraft.getMinecraft().thePlayer.getDistanceSqToEntity(entity) < 32.0
                    && entity.worldObj.rand.nextFloat() > 0.95f) {
                final float angle = (float) (entity.worldObj.rand.nextFloat() * 2.0f * 3.141592653589793);
                Thaumcraft.proxy.sparkle(
                        (float) entity.posX + entity.width * (float) Math.cos(angle),
                        (float) entity.posY + entity.height * (entity.worldObj.rand.nextFloat() - 0.1f) * 1.2f,
                        (float) entity.posZ + entity.width * (float) Math.sin(angle),
                        2.0f,
                        7,
                        0.0f);
            }
        }
        if (this.evanescentStage == 2) {
            final float temp = this.breakProgress;
            this.resetBlocks(Minecraft.getMinecraft().thePlayer);
            this.breakProgress = temp;
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void clearWater(final RenderBlockOverlayEvent event) {
        final Minecraft mc = Minecraft.getMinecraft();
        final ItemStack goggles = mc.thePlayer.inventory.armorItemInSlot(3);
        if (goggles != null && goggles.getItem() instanceof IRevealer
                && goggles.stackTagCompound != null
                && goggles.stackTagCompound.getString("Lens") != null
                && !goggles.stackTagCompound.getString("Lens").equals("")) {
            final ILens theLens = (ILens) LensManager.getLens(goggles.stackTagCompound.getString("Lens"));
            if (theLens == ThaumicHorizons.itemLensWater) {
                event.setCanceled(true);
            }
        }
        if (event.overlayType == RenderBlockOverlayEvent.OverlayType.FIRE
                && mc.thePlayer.ridingEntity instanceof EntityBoatThaumium) {
            event.setCanceled(true);
        }
    }

    static {
        RenderEventHandler.radialHudScale = 0.0f;
    }
}
