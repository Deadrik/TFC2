package com.bioxx.tfc2.handlers;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.core.Timekeeper;

public class WorldLoadHandler
{
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		if(event.getWorld().provider.getDimension() == 0)
		{
			Timekeeper.initialize(event.getWorld());
		}
	}
}
