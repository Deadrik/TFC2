package com.bioxx.tfc2.handlers;

import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.core.Timekeeper;

public class WorldLoadHandler
{
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load event)
	{
		World world = event.world;
		if(world.provider.getDimensionId() == 0)
		{
			Timekeeper.initialize(world);
		}
	}
}
