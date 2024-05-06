//
// Decompiled by Procyon v0.5.30
//

package com.kentington.thaumichorizons.common.items.lenses;

import java.awt.Color;
import java.text.DecimalFormat;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.kentington.thaumichorizons.common.ThaumicHorizons;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.INode;
import thaumcraft.api.research.IScanEventHandler;
import thaumcraft.api.research.ScanResult;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketScannedToServer;
import thaumcraft.common.lib.research.ScanManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;

public class ItemLensOrderEntropy extends Item implements ILens {

    ScanResult startScan;
    int count;
    boolean isNew;
    IIcon icon;

    public ItemLensOrderEntropy() {
        this.startScan = null;
        this.count = 250;
        this.isNew = true;
        this.setCreativeTab(ThaumicHorizons.tabTH);
    }

    public String lensName() {
        return "LensOrderEntropy";
    }

    @SideOnly(Side.CLIENT)
    public void handleRender(final Minecraft mc, final float partialTicks) {
        if (Minecraft.getMinecraft().thePlayer.worldObj.isRemote) {
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            final EntityPlayer p = Minecraft.getMinecraft().thePlayer;
            this.isNew = false;
            String text = "?";
            final ScanResult scan = this.doScan(new ItemStack(ConfigItems.itemThaumometer), p.worldObj, p, this.count);
            if (scan != null) {
                AspectList aspects = null;
                if (!this.isNew) {
                    aspects = ScanManager.getScanAspects(scan, p.worldObj);
                }
                ItemStack stack = null;
                if (scan.id > 0) {
                    stack = new ItemStack(Item.getItemById(scan.id), 1, scan.meta);
                    if (stack.getItem() != null) {
                        try {
                            text = stack.getDisplayName();
                        } catch (Exception ignored) {}
                    } else if (stack.getItem() != null) {
                        try {
                            text = stack.getItem().getItemStackDisplayName(stack);
                        } catch (Exception ignored) {}
                    }
                }
                if (scan.type == 2) {
                    if (!(scan.entity instanceof EntityItem)) {
                        text = scan.entity.getCommandSenderName();
                    } else {
                        text = ((EntityItem) scan.entity).getEntityItem().getDisplayName();
                    }
                    x = scan.entity.posX;
                    y = scan.entity.posY;
                    z = scan.entity.posZ;
                } else {
                    final MovingObjectPosition mop = EntityUtils.getMovingObjectPositionFromPlayer(p.worldObj, p, true);
                    if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                        x = mop.blockX;
                        y = mop.blockY;
                        z = mop.blockZ;
                        final TileEntity tile = p.worldObj.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
                        if (scan.type == 3 && scan.phenomena.startsWith("NODE") && tile instanceof INode) {
                            if (!this.isNew) {
                                aspects = ((INode) tile).getAspects();
                            }
                            if (p.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ) == ConfigBlocks.blockAiry) {
                                text = StatCollector.translateToLocal(
                                        p.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ).getUnlocalizedName()
                                                + "."
                                                + p.worldObj.getBlockMetadata(mop.blockX, mop.blockY, mop.blockZ)
                                                + ".name");
                            } else {
                                text = StatCollector.translateToLocal(
                                        p.worldObj.getBlock(mop.blockX, mop.blockY, mop.blockZ).getLocalizedName());
                            }
                            text = text + " ("
                                    + StatCollector
                                            .translateToLocal("nodetype." + ((INode) tile).getNodeType() + ".name");
                            if (((INode) tile).getNodeModifier() != null) {
                                text = text + ", "
                                        + StatCollector.translateToLocal(
                                                "nodemod." + ((INode) tile).getNodeModifier() + ".name");
                            }
                            text += ")";
                        }
                    }
                }
                if (aspects != null || text.length() > 0) {
                    this.renderNameAndAspects(aspects, text);
                }
            }
            if (scan != null && scan.equals(this.startScan) && this.isNew) {
                --this.count;
                this.renderNameAndAspects(null, text);
                if (this.count <= 5) {
                    this.startScan = null;
                    if (ScanManager.completeScan(p, scan, "@")) {
                        PacketHandler.INSTANCE.sendToServer(new PacketScannedToServer(scan, p, "@"));
                    }
                    this.count = 250;
                }
                if (this.count % 20 == 0) {
                    p.worldObj.playSound(
                            p.posX,
                            p.posY,
                            p.posZ,
                            "thaumcraft:cameraticks",
                            0.2f,
                            0.45f + p.worldObj.rand.nextFloat() * 0.1f,
                            false);
                }
            } else {
                this.startScan = scan;
                this.count = 250;
            }
        }
    }

    private void renderNameAndAspects(final AspectList aspects, final String text) {
        final ScaledResolution sr = new ScaledResolution(
                Minecraft.getMinecraft(),
                Minecraft.getMinecraft().displayWidth,
                Minecraft.getMinecraft().displayHeight);
        final int w = sr.getScaledWidth();
        final int h = sr.getScaledHeight();
        if (aspects != null && aspects.size() > 0) {
            int num = 0;
            int yOff = 0;
            int thisRow = 0;
            final int size = 18;
            thisRow = Math.min(aspects.size() - num, 5);
            for (final Aspect asp : aspects.getAspects()) {
                yOff = num / 5 * size;
                this.drawAspectTag(
                        asp,
                        aspects.getAmount(asp),
                        w / 2 - size * thisRow / 2 + size * (num % 5),
                        h / 2 + 16 + yOff,
                        w);
                if (++num % 5 == 0) {
                    thisRow = Math.min(aspects.size() - num, 5);
                }
            }
        }
        if (text.length() > 0) {
            Minecraft.getMinecraft().ingameGUI.drawString(
                    Minecraft.getMinecraft().fontRenderer,
                    text,
                    w / 2 - Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2,
                    h / 2 - 16,
                    16777215);
        }
    }

    private ScanResult doScan(final ItemStack stack, final World world, final EntityPlayer p, final int count) {
        final Entity pointedEntity = EntityUtils.getPointedEntity(p.worldObj, p, 0.5, 10.0, 0.0f, true);
        if (pointedEntity == null) {
            final MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(p.worldObj, p, true);
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                final TileEntity tile = world.getTileEntity(mop.blockX, mop.blockY, mop.blockZ);
                if (tile instanceof INode) {
                    final ScanResult sr = new ScanResult((byte) 3, 0, 0, null, "NODE" + ((INode) tile).getId());
                    if (ScanManager.isValidScanTarget(p, sr, "@")) {
                        Thaumcraft.proxy.blockRunes(
                                world,
                                mop.blockX,
                                mop.blockY + 0.25,
                                mop.blockZ,
                                0.3f + world.rand.nextFloat() * 0.7f,
                                0.0f,
                                0.3f + world.rand.nextFloat() * 0.7f,
                                15,
                                0.03f);
                        this.isNew = true;
                        return sr;
                    }
                    return sr;
                } else {
                    final Block bi = world.getBlock(mop.blockX, mop.blockY, mop.blockZ);
                    if (bi != Blocks.air) {
                        final int md = bi.getDamageValue(world, mop.blockX, mop.blockY, mop.blockZ);
                        ItemStack is = bi.getPickBlock(mop, p.worldObj, mop.blockX, mop.blockY, mop.blockZ);
                        ScanResult sr2 = null;
                        try {
                            if (is == null) {
                                is = BlockUtils.createStackedBlock(bi, md);
                            }
                        } catch (Exception ignored) {}
                        try {
                            if (is == null) {
                                sr2 = new ScanResult((byte) 1, Block.getIdFromBlock(bi), md, null, "");
                            } else {
                                sr2 = new ScanResult(
                                        (byte) 1,
                                        Item.getIdFromItem(is.getItem()),
                                        is.getItemDamage(),
                                        null,
                                        "");
                            }
                        } catch (Exception ignored) {}
                        if (ScanManager.isValidScanTarget(p, sr2, "@")) {
                            Thaumcraft.proxy.blockRunes(
                                    world,
                                    mop.blockX,
                                    mop.blockY + 0.25,
                                    mop.blockZ,
                                    0.3f + world.rand.nextFloat() * 0.7f,
                                    0.0f,
                                    0.3f + world.rand.nextFloat() * 0.7f,
                                    15,
                                    0.03f);
                            this.isNew = true;
                            return sr2;
                        }
                        return sr2;
                    }
                }
            }
            for (final IScanEventHandler seh : ThaumcraftApi.scanEventhandlers) {
                final ScanResult scan = seh.scanPhenomena(stack, world, p);
                if (scan != null) {
                    return scan;
                }
            }
            return null;
        }
        final ScanResult sr3 = new ScanResult((byte) 2, 0, 0, pointedEntity, "");
        if (ScanManager.isValidScanTarget(p, sr3, "@")) {
            Thaumcraft.proxy.blockRunes(
                    world,
                    pointedEntity.posX - 0.5,
                    pointedEntity.posY + pointedEntity.getEyeHeight() / 2.0f,
                    pointedEntity.posZ - 0.5,
                    0.3f + world.rand.nextFloat() * 0.7f,
                    0.0f,
                    0.3f + world.rand.nextFloat() * 0.7f,
                    (int) (pointedEntity.height * 15.0f),
                    0.03f);
            this.isNew = true;
            return sr3;
        }
        return sr3;
    }

    public String getUnlocalizedName(final ItemStack par1ItemStack) {
        return "item.LensOrderEntropy";
    }

    public void drawAspectTag(final Aspect aspect, final int amount, final int x, final int y, final int sw) {
        GL11.glPushMatrix();
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569f);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        final Minecraft mc = Minecraft.getMinecraft();
        final Color color = new Color(aspect.getColor());
        mc.renderEngine.bindTexture(aspect.getImage());
        GL11.glColor4f(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
        final Tessellator var9 = Tessellator.instance;
        var9.startDrawingQuads();
        var9.setColorRGBA_F(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, 0.5f);
        var9.addVertexWithUV(x + 0.0, y + 16.0, 0.0, 0.0, 1.0);
        var9.addVertexWithUV(x + 16.0, y + 16.0, 0.0, 1.0, 1.0);
        var9.addVertexWithUV(x + 16.0, y + 0.0, 0.0, 1.0, 0.0);
        var9.addVertexWithUV(x + 0.0, y + 0.0, 0.0, 0.0, 0.0);
        var9.draw();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final DecimalFormat myFormatter = new DecimalFormat("#######.##");
        final String am = myFormatter.format(amount);
        mc.fontRenderer.drawString(am, 24 + x * 2, 32 - mc.fontRenderer.FONT_HEIGHT + y * 2, 16777215);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1f);
        GL11.glPopMatrix();
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(final IIconRegister ir) {
        this.icon = ir.registerIcon("thaumichorizons:lensorderentropy");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(final int par1) {
        return this.icon;
    }

    public void handleRemoval(final EntityPlayer p) {}
}
