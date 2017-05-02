package com.bioxx.tfc2.world;

import java.util.LinkedList;

import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.events.HexUpdateEvent;
import com.bioxx.tfc2.api.events.IslandUpdateEvent;

public class CachedIsland 
{
	public long lastAccess;
	public IslandMap island;
	private LinkedList<Center> updateQueue;

	public CachedIsland(IslandMap island)
	{
		this.island = island;
		updateQueue = new LinkedList<Center>();
		updateQueue.addAll(island.centers);
	}

	public IslandMap getIslandMap()
	{
		lastAccess = System.currentTimeMillis();
		return island;
	}

	public void update(World world)
	{
		Global.EVENT_BUS.post(new IslandUpdateEvent(island, world));
		for(int i = 0; i < 50; i++)
		{
			Center c = updateQueue.pollFirst();
			HexUpdateEvent hue = new HexUpdateEvent(island, c);
			Global.EVENT_BUS.post(hue);
			updateQueue.addLast(c);
		}
	}
}
