package com.bioxx.tfc2.handlers.client;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.BlockLeaves;
import com.bioxx.tfc2.world.WorldGen;

public class ClientRenderHandler
{
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event)
	{
		((BlockLeaves)TFCBlocks.Leaves).setGraphicsLevel(Minecraft.getMinecraft().gameSettings.fancyGraphics);
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
