package com.bioxx.tfc2.handlers.client;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.world.World;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.BlockLeaves;
import com.bioxx.tfc2.world.WorldGen;

public class ClientRenderHandler
{
	public static boolean IsGeneratingFirstIsland = false;
	static boolean skipRender = false;

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		((BlockLeaves)TFCBlocks.Leaves).setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent event)
	{

		if(event.phase == Phase.START)
		{
			if(ClientRenderHandler.IsGeneratingFirstIsland)
			{
				Minecraft.getMinecraft().skipRenderWorld = true;
				skipRender = true;
			}
			else
			{
				skipRender = false;
			}
		}
		if(event.phase == Phase.END)
		{
			if(skipRender && ClientRenderHandler.IsGeneratingFirstIsland)
			{
				String gen = Core.translate("gui.generatingmapmessage");
				FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
				ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
				int sizeX = Minecraft.getMinecraft().displayWidth/2;
				int sizeY = Minecraft.getMinecraft().displayHeight/2;

				renderer.drawString(gen, sizeX/2 - (renderer.getStringWidth(gen) / 2)+1, sizeY/2+1, Color.black.getRGB());
				renderer.drawString(gen, sizeX/2 - (renderer.getStringWidth(gen) / 2), sizeY/2, Color.white.getRGB());
				Minecraft.getMinecraft().skipRenderWorld = false;
				skipRender = false;
			}
		}
	}

	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		World world = Minecraft.getMinecraft().world;
		if(event.phase == Phase.START)
		{
			if(WorldGen.getInstance() != null)
			{
				if(world != null && world.provider.getDimension() == 0)
				{
					WorldGen.getInstance().trimCache();
					WorldGen.getInstance().buildFromQueue();
					//We don't update on the client
					//WorldGen.getInstance().runUpdateLoop();
				}
			}			
		}
	}
}
