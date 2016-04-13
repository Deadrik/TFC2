package com.bioxx.tfc2.api.events;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import com.bioxx.jmapgen.IslandMap;

public class CropEvent
{
	/**
	 * This event is fired on the {@link com.bioxx.tfc2.api.Global#EVENT_BUS}.<br>
	 * @author Bioxx
	 *
	 */
	@Cancelable
	public static class Harvest extends Event
	{
		public final IslandMap map;
		public final BlockPos pos;
		public final World world;

		public Harvest(World world, IslandMap map, BlockPos pos)
		{
			this.map = map;
			this.pos = pos;
			this.world = world;
		}
	}
}
