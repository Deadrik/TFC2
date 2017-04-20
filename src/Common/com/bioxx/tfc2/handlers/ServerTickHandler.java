package com.bioxx.tfc2.handlers;

import net.minecraft.world.World;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

import com.bioxx.tfc2.world.WorldGen;

public class ServerTickHandler
{
	@SubscribeEvent
	public void onServerWorldTick(WorldTickEvent event)
	{
		World world = event.world;
		if(event.phase == Phase.START)
		{
			if(WorldGen.getInstance() != null)
			{
				if(world.provider.getDimension() == 0)
				{
					WorldGen.getInstance().trimCache();
					WorldGen.getInstance().buildFromQueue();
					WorldGen.getInstance().runUpdateLoop();
				}
			}			
		}
	}


}
