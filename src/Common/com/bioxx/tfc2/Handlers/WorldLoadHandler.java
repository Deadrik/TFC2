package com.bioxx.tfc2.Handlers;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.World.WorldGen;

public class WorldLoadHandler
{
	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload event)
	{
		WorldGen.instance.resetCache();
	}
}
