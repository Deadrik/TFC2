package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.BlockAnvil;
import com.bioxx.tfc2.tileentities.TileAnvil;

public class AnvilHighlightHandler 
{
	@SubscribeEvent
	public void drawBlockHighlightEvent(DrawBlockHighlightEvent evt) 
	{
		if(evt.getTarget().typeOfHit != RayTraceResult.Type.BLOCK)
			return;

		if(evt.getTarget().sideHit != EnumFacing.UP)
			return;

		World world = evt.getPlayer().world;

		if(world.getBlockState(evt.getTarget().getBlockPos()).getBlock() != TFCBlocks.Anvil)
			return;

		double posX = evt.getPlayer().lastTickPosX + (evt.getPlayer().posX - evt.getPlayer().lastTickPosX) * evt.getPartialTicks();
		double posY = evt.getPlayer().lastTickPosY + (evt.getPlayer().posY - evt.getPlayer().lastTickPosY) * evt.getPartialTicks();
		double posZ = evt.getPlayer().lastTickPosZ + (evt.getPlayer().posZ - evt.getPlayer().lastTickPosZ) * evt.getPartialTicks();

		double hitX = Math.round((evt.getTarget().hitVec.xCoord - evt.getTarget().getBlockPos().getX())*100)/100.0d;
		double hitY = Math.round((evt.getTarget().hitVec.yCoord - evt.getTarget().getBlockPos().getY())*100)/100.0d;
		double hitZ = Math.round((evt.getTarget().hitVec.zCoord - evt.getTarget().getBlockPos().getZ())*100)/100.0d;

		int divX = (int) Math.floor(hitX * 8);
		int divY = (int) Math.floor(hitY * 8);
		int divZ = (int) Math.floor(hitZ * 8);

		//get the targeted sub block coords
		double subX = divX/8D;
		double subY = divY/8D;
		double subZ = divZ/8D;

		TileAnvil te = (TileAnvil) world.getTileEntity(evt.getTarget().getBlockPos());
		EnumFacing facing = world.getBlockState(evt.getTarget().getBlockPos()).getValue(BlockAnvil.FACING);
		if(facing == EnumFacing.NORTH || facing == EnumFacing.SOUTH)
		{
			if(divX == 0 || divX == 7 || divZ < 2 || divZ > 5)
				return;

			divX -= 1; divZ -= 2;

			if(te.getStrikePoint(divX, divZ) == null)
				return;
		}

		if(facing == EnumFacing.EAST || facing == EnumFacing.WEST)
		{

			if(divZ == 0 || divZ == 7 || divX < 2 || divX > 5)
				return;

			divX -= 2; divZ -= 1;

			if(te.getStrikePoint(divZ, divX) == null)
				return;
		}

		//create the box size
		double minX = evt.getTarget().getBlockPos().getX() + subX;
		double minY = evt.getTarget().getBlockPos().getY() + subY;
		double minZ = evt.getTarget().getBlockPos().getZ() + subZ;
		double maxX = minX + 0.125;
		double maxY = minY + 0.07;
		double maxZ = minZ + 0.125;

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();

		//Draw the mini Box
		drawBox(new AxisAlignedBB(minX,minY,minZ,maxX,maxY,maxZ).expand(0.002F, 0.002F, 0.002F).offset(-posX, -posY, -posZ), new float[]{1,0.5f,0, 0.5f});

		GlStateManager.enableTexture2D();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
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
}
