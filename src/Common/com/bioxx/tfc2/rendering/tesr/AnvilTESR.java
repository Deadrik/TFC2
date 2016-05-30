package com.bioxx.tfc2.rendering.tesr;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.blocks.BlockAnvil;
import com.bioxx.tfc2.handlers.client.AnvilHighlightHandler;
import com.bioxx.tfc2.tileentities.TileAnvil;

public class AnvilTESR extends TileEntitySpecialRenderer<TileAnvil> {

	@Override
	public void renderTileEntityAt(TileAnvil te, double xPos, double yPos, double zPos, float partialTicks, int destroyStage) 
	{
		AxisAlignedBB[] boxList = getBoxList(te, xPos, yPos, zPos);

		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();
		//GlStateManager.disableDepth();
		GlStateManager.enableOutlineMode(1);
		//Draw the mini Box
		for(AxisAlignedBB aabb : boxList)
		{
			if(aabb != null)
				AnvilHighlightHandler.drawBox(new AxisAlignedBB(aabb.minX,aabb.minY,aabb.minZ,aabb.maxX,aabb.maxY,aabb.maxZ), new float[]{1, 1, 1, 0.2f});
		}

		GL11.glColor4f(0.1F, 0.1F, 0.1F, 1.0F);
		GL11.glLineWidth(3.0F);
		GL11.glDepthMask(false);
		GlStateManager.enableCull();
		GlStateManager.enableDepth();
		//Draw the mini Box
		for(AxisAlignedBB aabb : boxList)
		{
			if(aabb != null)
				AnvilHighlightHandler.drawOutlinedBox(new AxisAlignedBB(aabb.minX,aabb.minY,aabb.minZ,aabb.maxX,aabb.maxY,aabb.maxZ));
		}
		GlStateManager.disableOutlineMode();

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
	}

	public AxisAlignedBB[] getBoxList(TileAnvil te, double xPos, double yPos, double zPos)
	{
		AxisAlignedBB[] list = new AxisAlignedBB[24];

		for(int x = 0; x < 6; x++)
		{
			for(int z = 0; z < 4; z++)
			{

				int divX = x;
				int divZ = z;

				EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockAnvil.FACING);

				if(te.getStrikePoint(divX, divZ) == null)
					continue;

				//get the targeted sub block coords
				double subX = divX/8D;
				double subZ = divZ/8D;

				if(facing == EnumFacing.EAST || facing == EnumFacing.WEST)
				{
					subX = (divZ+2)/8D; subZ = (divX+1)/8D;
				}

				//create the box size
				double minX = xPos + subX;
				double minY = yPos + 0.63;
				double minZ = zPos + subZ;
				double maxX = minX + 0.125;
				double maxY = minY + 0.0625;
				double maxZ = minZ + 0.125;

				list[TileAnvil.getStrikePointIndex(divX, divZ)] = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
			}
		}
		return list;
	}

}
