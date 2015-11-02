package com.bioxx.tfc2.api.events;

import net.minecraftforge.fml.common.eventhandler.Event;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters;


public class IslandGenEvent
{
	/**
	 * This is called right after the parameters have been created and before the island itself is generated.<br>
	 * <br>
	 * {@link #params} is the set of island parameters that has been chosen for this island before generation.<br>
	 * <br>
	 * This event is fired on the {@link com.bioxx.tfc2.api.Global#EVENT_BUS}.<br>
	 * @author Bioxx
	 *
	 */
	public static class Pre extends Event
	{
		public final IslandParameters params;
		public Pre(IslandParameters p)
		{
			params = p;
		}
	}

	/**
	 * This is called immediately following the creation of the map and before this data is saved to the cache or used.<br>
	 * <br>
	 * {@link #islandMap} is the island map after TFC has built the island.<br>
	 * <br>
	 * This event is fired on the {@link com.bioxx.tfc2.api.Global#EVENT_BUS}.<br>
	 * @author Bioxx
	 *
	 */
	public static class Post extends Event
	{
		public final IslandMap islandMap;
		public Post(IslandMap map)
		{
			islandMap = map;
		}
	}
}
