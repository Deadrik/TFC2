package com.bioxx.tfc2.rendering.tesr;

import java.awt.Color;

import javax.vecmath.Vector2d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.api.render.ui.*;
import com.bioxx.tfc2.blocks.BlockAnvil;
import com.bioxx.tfc2.core.Timekeeper;
import com.bioxx.tfc2.handlers.client.AnvilHighlightHandler;
import com.bioxx.tfc2.tileentities.TileAnvil;
import com.bioxx.tfc2.tileentities.TileAnvil.AnvilStrikePoint;

public class AnvilTESR extends TileEntitySpecialRenderer<TileAnvil> 
{
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_anvil.png");

	TileAnvil tile;
	public AnvilTESR()
	{

	}

	@Override
	public void renderTileEntityAt(TileAnvil te, double xPos, double yPos, double zPos, float partialTicks, int destroyStage) 
	{
		tile = te;
		if(te.getTimer() <= 0)
			return;

		//Render the floating UI
		renderFloatingUI(xPos, yPos, zPos);

		Vector2d TL;
		Vector2d TR;
		Vector2d BR;
		Vector2d BL;

		EntityPlayer player = Minecraft.getMinecraft().player;
		Timekeeper time = Timekeeper.getInstance();

		if(player.getHorizontalFacing() == EnumFacing.WEST)
		{
			TL = new Vector2d(1, 0);
			TR = new Vector2d(1, 1);
			BR = new Vector2d(0, 1);
			BL = new Vector2d(0, 0);
		}
		else if(player.getHorizontalFacing() == EnumFacing.EAST)
		{
			TL = new Vector2d(0, 1);
			TR = new Vector2d(0, 0);
			BR = new Vector2d(1, 0);
			BL = new Vector2d(1, 1);
		}
		else if(player.getHorizontalFacing() == EnumFacing.SOUTH)
		{
			TL = new Vector2d(1, 1);
			TR = new Vector2d(0, 1);
			BR = new Vector2d(0, 0);
			BL = new Vector2d(1, 0);
		}
		else//North
		{
			TL = new Vector2d(0, 0);
			TR = new Vector2d(1, 0);
			BR = new Vector2d(1, 1);
			BL = new Vector2d(0, 1);
		}

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();

		//Turn on transparency and disable culling so that our incorrect vertex ordering for the polygon facing is hidden
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableCull();
		GlStateManager.disableTexture2D();
		GlStateManager.disableLighting();

		//Render each hit box on top of the anvil
		for(int x = 0; x < 6; x++)
		{
			for(int z = 0; z < 4; z++)
			{
				int divX = x;
				int divZ = z;

				EnumFacing facing = te.getWorld().getBlockState(te.getPos()).getValue(BlockAnvil.FACING);
				AnvilStrikePoint point = te.getStrikePoint(divX, divZ);
				if(point == null)
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

				AxisAlignedBB aabb = new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);


				//draw the transparent cube
				AnvilHighlightHandler.drawBox(new AxisAlignedBB(aabb.minX,aabb.minY,aabb.minZ,aabb.maxX,aabb.maxY,aabb.maxZ), new float[]{1, 1, 1, 0.2f});

				GlStateManager.color(0.1F, 0.1F, 0.1F, 1.0F);
				GlStateManager.glLineWidth(3.0f);
				GlStateManager.enableOutlineMode(1);

				//draw the outline of the hit area
				AnvilHighlightHandler.drawOutlinedBox(new AxisAlignedBB(aabb.minX,aabb.minY,aabb.minZ,aabb.maxX,aabb.maxY,aabb.maxZ));

				GlStateManager.disableOutlineMode();
				GlStateManager.color(1F, 1F, 1F, 1.0F);



				//Render the Strike Type Texture on top of the cube

				GlStateManager.enableTexture2D();
				Core.bindTexture(point.getType().getTexture());
				GlStateManager.color(1F, 1F, 1F, 1.0f-0.9f*((float)(time.getTotalTicks()-point.getBirthTime())/(float)point.getLifeTime()));
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				buffer.pos(aabb.minX, aabb.maxY, aabb.minZ).tex(TL.x, TL.y).endVertex();
				buffer.pos(aabb.maxX, aabb.maxY, aabb.minZ).tex(TR.x, TR.y).endVertex();
				buffer.pos(aabb.maxX, aabb.maxY, aabb.maxZ).tex(BR.x, BR.y).endVertex();
				buffer.pos(aabb.minX, aabb.maxY, aabb.maxZ).tex(BL.x, BL.y).endVertex();
				tessellator.draw();

				GlStateManager.disableTexture2D();
			}
		}

		GlStateManager.enableLighting();
		GlStateManager.enableTexture2D();
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
	}

	public void renderFloatingUI(double xPos, double yPos, double zPos)
	{
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		EntityPlayer player = Minecraft.getMinecraft().player;

		double scale = 0.25;
		double panelPosX = xPos+0.5;
		double panelPosY = yPos+0.7;
		double panelPosZ = zPos+0.5;
		Vec3d origin = new Vec3d(panelPosX, panelPosY, panelPosZ); 
		double panelSizeX = 2 * scale;
		double panelSizeY = 1.0 * scale;

		Vec3d BL = new Vec3d(panelPosX-panelSizeX,  panelPosY-panelSizeY, panelPosZ);
		Vec3d TL = new Vec3d(panelPosX-panelSizeX,  panelPosY+panelSizeY, panelPosZ);
		Vec3d TR = new Vec3d(panelPosX+panelSizeX,  panelPosY+panelSizeY, panelPosZ);
		Vec3d BR = new Vec3d(panelPosX+panelSizeX,  panelPosY-panelSizeY, panelPosZ);

		double pixelScaleX = panelSizeX*2/64.0;
		double pixelScaleY = panelSizeY*2/32.0;

		UIModel ui = new UIModel();
		UIComponent mainPanel = new UIComponent(DefaultVertexFormats.POSITION_TEX)
		{
			@Override
			public void setupGL()
			{
				GlStateManager.enableTexture2D();
				Core.bindTexture(texture);
			}
		};
		mainPanel.addVertex(new VertexTex(TL, 15f/256f, 92f/256f));
		mainPanel.addVertex(new VertexTex(BL, 15f/256f, 124f/256f));
		mainPanel.addVertex(new VertexTex(TR, 77f/256f, 92f/256f));
		mainPanel.addVertex(new VertexTex(BR, 77f/256f, 124f/256f));
		ui.addComponent(mainPanel);

		UIComponent timerBarBorder = new UIComponent(DefaultVertexFormats.POSITION_COLOR, 1)
		{
			@Override
			public void setupGL()
			{
				GlStateManager.disableTexture2D();
			}
		};
		timerBarBorder.addVertex(new VertexColor(TL.addVector(pixelScaleX*4, -pixelScaleY*4, 0), new Color(0,0,0,1f)));
		timerBarBorder.addVertex(new VertexColor(TL.addVector(pixelScaleX*4, -pixelScaleY*9, 0), new Color(0,0,0,1f)));
		timerBarBorder.addVertex(new VertexColor(TL.addVector(pixelScaleX*60, -pixelScaleY*4, 0), new Color(0,0,0,1f)));
		timerBarBorder.addVertex(new VertexColor(TL.addVector(pixelScaleX*60, -pixelScaleY*9, 0), new Color(0,0,0,1f)));
		ui.addComponent(timerBarBorder);

		UIComponent timerBarBack = new UIComponent(DefaultVertexFormats.POSITION_COLOR, 2)
		{
			@Override
			public void setupGL()
			{
				GlStateManager.disableTexture2D();
			}
		};
		timerBarBack.addVertex(new VertexColor(TL.addVector(pixelScaleX*5, -pixelScaleY*5, 0), new Color(1,0,0,1f)));
		timerBarBack.addVertex(new VertexColor(TL.addVector(pixelScaleX*5, -pixelScaleY*8, 0), new Color(1,0,0,1f)));
		timerBarBack.addVertex(new VertexColor(TL.addVector(pixelScaleX*59, -pixelScaleY*5, 0), new Color(1,0,0,1f)));
		timerBarBack.addVertex(new VertexColor(TL.addVector(pixelScaleX*59, -pixelScaleY*8, 0), new Color(1,0,0,1f)));

		ui.addComponent(timerBarBack);

		UIComponent timerBarFront = new UIComponent(DefaultVertexFormats.POSITION_COLOR, 4)
		{
			@Override
			public void setupGL()
			{
				GlStateManager.disableTexture2D();
			}
		};
		timerBarFront.addVertex(new VertexColor(TL.addVector(pixelScaleX*5, -pixelScaleY*5, 0), new Color(1,1,1,1f)));
		timerBarFront.addVertex(new VertexColor(TL.addVector(pixelScaleX*5, -pixelScaleY*8, 0), new Color(1,1,1,1f)));
		timerBarFront.addVertex(new VertexColor(TL.addVector(pixelScaleX*5+pixelScaleX*54*(1f-tile.getTimer()/2000f), -pixelScaleY*5, 0), new Color(1,1,1,1f)));
		timerBarFront.addVertex(new VertexColor(TL.addVector(pixelScaleX*5+pixelScaleX*54*(1f-tile.getTimer()/2000f), -pixelScaleY*8, 0), new Color(1,1,1,1f)));
		ui.addComponent(timerBarFront);

		UIStringComponent textComp = new UIStringComponent(""+tile.getTimer(), TL.xCoord+pixelScaleX*5.5, TL.yCoord-pixelScaleY*8.5, TL.zCoord, 0.008f, 5, 0x0000000);
		ui.addComponent(textComp);

		/*UIComponent progressBar = new UIComponent(DefaultVertexFormats.POSITION_COLOR, 4)
		{
			@Override
			public void setupGL()
			{
				GlStateManager.disableTexture2D();
			}
		};
		progressBar.addVertex(new VertexColor(TL.addVector(pixelScaleX*6, -pixelScaleY*15, 0), new Color(0,0,0,1f)));
		progressBar.addVertex(new VertexColor(TL.addVector(pixelScaleX*6, -pixelScaleY*16, 0), new Color(0,0,0,1f)));
		progressBar.addVertex(new VertexColor(TL.addVector(pixelScaleX*6+pixelScaleX*52, -pixelScaleY*15, 0), new Color(0,0,0,1f)));
		progressBar.addVertex(new VertexColor(TL.addVector(pixelScaleX*6+pixelScaleX*52, -pixelScaleY*16, 0), new Color(0,0,0,1f)));
		ui.addComponent(progressBar);

		UIComponent workMarker = new UIComponent(DefaultVertexFormats.POSITION_TEX, 6)
		{
			@Override
			public void setupGL()
			{
				GlStateManager.enableTexture2D();
				Core.bindTexture(texture);
			}
		};
		workMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*3, -pixelScaleY*10, 0), 0f/256f, 92f/256f));
		workMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*3, -pixelScaleY*16, 0), 0f/256f, 98f/256f));
		workMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*10, -pixelScaleY*10, 0), 7f/256f, 92f/256f));
		workMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*10, -pixelScaleY*16, 0), 7f/256f, 98f/256f));
		ui.addComponent(workMarker);

		UIComponent recipeMarker = new UIComponent(DefaultVertexFormats.POSITION_TEX, 7)
		{
			@Override
			public void setupGL()
			{
				GlStateManager.enableTexture2D();
				Core.bindTexture(texture);
			}
		};
		recipeMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*3, -pixelScaleY*16, 0), 7f/256f, 92f/256f));
		recipeMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*3, -pixelScaleY*22, 0), 7f/256f, 98f/256f));
		recipeMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*10, -pixelScaleY*16, 0), 14f/256f, 92f/256f));
		recipeMarker.addVertex(new VertexTex(TL.addVector(pixelScaleX*10, -pixelScaleY*22, 0), 14f/256f, 98f/256f));
		ui.addComponent(recipeMarker);*/

		switch(player.getHorizontalFacing())
		{
		case EAST:
			ui.rotate(origin, new Vec3d(0,1,0), Math.toRadians(-90));
			ui.rotate(origin, new Vec3d(0,0,1), Math.toRadians(-45));
			ui.translate(new Vec3d(0.5,0.15,0));
			break;
		case NORTH:
			ui.rotate(origin, new Vec3d(1,0,0), Math.toRadians(-45));
			ui.translate(new Vec3d(0,0.15,-0.6));
			break;
		case SOUTH:
			ui.rotate(origin, new Vec3d(0,1,0), Math.toRadians(180));
			ui.rotate(origin, new Vec3d(1,0,0), Math.toRadians(45));
			ui.translate(new Vec3d(0,0.15,0.6));
			break;
		case WEST:
			ui.rotate(origin, new Vec3d(0,1,0), Math.toRadians(90));
			ui.rotate(origin, new Vec3d(0,0,1), Math.toRadians(45));
			ui.translate(new Vec3d(-0.5,0.15,0));
			break;
		default:
			break;
		}


		ui.draw(tessellator, buffer);
	}
}
