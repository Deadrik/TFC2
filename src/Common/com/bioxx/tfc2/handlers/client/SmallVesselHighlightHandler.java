package com.bioxx.tfc2.handlers.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.blocks.BlockSmallVessel;
import com.bioxx.tfc2.tileentities.TileSmallVessel;

public class SmallVesselHighlightHandler 
{
	private static ResourceLocation selectTex = new ResourceLocation(Reference.ModID, "textures/select.png");
	public static final AxisAlignedBB aabb0 = new AxisAlignedBB(0.0,0,0.0, 0.5,0.01,0.5);
	public static final AxisAlignedBB aabb1 = new AxisAlignedBB(0.0,0,0.5, 0.5,0.01,1.0);
	public static final AxisAlignedBB aabb2 = new AxisAlignedBB(0.5,0,0.0, 1.0,0.01,0.5);
	public static final AxisAlignedBB aabb3 = new AxisAlignedBB(0.5,0,0.5, 1.0,0.01,1.0);

	@SubscribeEvent
	public void drawBlockHighlightEvent(DrawBlockHighlightEvent evt) 
	{
		RayTraceResult target = evt.getTarget();
		if(target.typeOfHit != RayTraceResult.Type.BLOCK)
			return;

		World world = evt.getPlayer().world;
		EntityPlayer player = evt.getPlayer();
		IBlockState state = world.getBlockState(target.getBlockPos());

		double posX = player.lastTickPosX + (player.posX - player.lastTickPosX) * evt.getPartialTicks();
		double posY = player.lastTickPosY + (player.posY - player.lastTickPosY) * evt.getPartialTicks();
		double posZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * evt.getPartialTicks();

		double hitX = Math.round((target.hitVec.xCoord - target.getBlockPos().getX())*100)/100.0d;
		double hitY = Math.round((target.hitVec.yCoord - target.getBlockPos().getY())*100)/100.0d;
		double hitZ = Math.round((target.hitVec.zCoord - target.getBlockPos().getZ())*100)/100.0d;

		AxisAlignedBB box = null;
		int index = getIndex(hitX, hitZ);

		if((state.getBlock() == TFCBlocks.SmallVessel) || (target.sideHit == EnumFacing.UP && player.getHeldItemMainhand().getItem() == TFCItems.PotteryVessel && 
				state.getBlock().isSideSolid(state, world, target.getBlockPos(), EnumFacing.UP) && player.isSneaking() && player.getHeldItemMainhand().getItemDamage() == 1))
		{

			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.disableCull();

			if(index == 0)
				box = aabb0.offset(target.getBlockPos());
			else if(index == 1)
				box = aabb1.offset(target.getBlockPos());
			else if(index == 2)
				box = aabb2.offset(target.getBlockPos());
			else
				box = aabb3.offset(target.getBlockPos());

			if(state.getBlock() != TFCBlocks.SmallVessel)
				box = box.offset(0, 1, 0);

			GlStateManager.enableTexture2D();
			Core.bindTexture(selectTex);
			drawFace(box.expand(0.002F, 0.002F, 0.002F).offset(-posX, -posY, -posZ), new float[]{1f,1f,1f, 1f}, EnumFacing.UP);

			GlStateManager.disableTexture2D();
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
		}
	}

	private static AxisAlignedBB getBox(TileSmallVessel tile, int index)
	{

		if(tile.getRotation() == EnumFacing.Axis.Z)
		{
			if(index == 0 && !tile.getStackInSlot(0).isEmpty())
				return BlockSmallVessel.aabb0_z;
			if(index == 1 && !tile.getStackInSlot(1).isEmpty())
				return BlockSmallVessel.aabb1_z;
			if(index == 2 && !tile.getStackInSlot(2).isEmpty())
				return BlockSmallVessel.aabb2_z;
			if(index == 3 && !tile.getStackInSlot(3).isEmpty())
				return BlockSmallVessel.aabb3_z;
		}
		else
		{
			if(index == 0 && !tile.getStackInSlot(0).isEmpty())
				return BlockSmallVessel.aabb0_x;
			if(index == 1 && !tile.getStackInSlot(1).isEmpty())
				return BlockSmallVessel.aabb1_x;
			if(index == 2 && !tile.getStackInSlot(2).isEmpty())
				return BlockSmallVessel.aabb2_x;
			if(index == 3 && !tile.getStackInSlot(3).isEmpty())
				return BlockSmallVessel.aabb3_x;
		}

		return new AxisAlignedBB(0.02,0,0.02,0.98,0.001,0.98);
	}

	private int getIndex(double hitX, double hitZ)
	{
		int index = 0;
		if(hitX < 0.5 && hitZ < 0.5)
			index = 0;
		else if(hitX < 0.5 && hitZ >= 0.5)
			index = 1;
		else if(hitX >= 0.5 && hitZ < 0.5)
			index = 2;
		else if(hitX >= 0.5 && hitZ >= 0.5)
			index = 3;
		return index;
	}

	public static void drawBox(AxisAlignedBB aabb, float[] color)
	{
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		//Top
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		tessellator.draw();

		//Bottom
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		tessellator.draw();

		//-x
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		tessellator.draw();

		//+x
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		tessellator.draw();

		//-z
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
		tessellator.draw();

		//+z
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
		buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
		tessellator.draw();
	}

	public static void drawOutlinedBox(AxisAlignedBB aabb)
	{
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
		buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
		buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();

		buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();

		buffer.pos(aabb.minX, aabb.minY, aabb.minZ).endVertex();
		buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();

		buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();

		buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).endVertex();
		buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).endVertex();

		buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();

		buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).endVertex();
		buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();

		buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();

		buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).endVertex();
		buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).endVertex();

		tessellator.draw();
	}

	public static void drawFace(AxisAlignedBB aabb, float[] color, EnumFacing facing)
	{
		//TODO: Add UV coords for other faces
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		if(facing == EnumFacing.UP)
		{
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).tex(0, 1).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).tex(0, 0).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).tex(1, 0).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).tex(1, 1).color(color[0], color[1], color[2], color[3]).endVertex();

			tessellator.draw();
		}
		else if(facing == EnumFacing.DOWN)
		{
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			tessellator.draw();
		}
		else if(facing == EnumFacing.WEST)
		{
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			tessellator.draw();
		}
		else if(facing == EnumFacing.EAST)
		{
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			tessellator.draw();
		}
		else if(facing == EnumFacing.NORTH)
		{
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(aabb.minX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.minY, aabb.minZ).color(color[0], color[1], color[2], color[3]).endVertex();
			tessellator.draw();
		}
		else if(facing == EnumFacing.SOUTH)
		{
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
			buffer.pos(aabb.maxX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			buffer.pos(aabb.minX, aabb.minY, aabb.maxZ).color(color[0], color[1], color[2], color[3]).endVertex();
			tessellator.draw();
		}
	}
}
