package com.bioxx.tfc2.world;

import java.util.LinkedList;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.events.HexUpdateEvent;

public class CachedIsland 
{
	public long lastAccess;
	public IslandMap islandData;
	private LinkedList<Center> updateQueue;

	public CachedIsland(IslandMap island)
	{
		islandData = island;
		updateQueue.addAll(island.centers);
	}

	public IslandMap getIslandMap()
	{
		lastAccess = System.currentTimeMillis();
		return islandData;
	}

	public void update()
	{
		for(int i = 0; i < 50; i++)
		{
			Center c = updateQueue.getFirst();
			HexUpdateEvent hue = new HexUpdateEvent(islandData, c);
			Global.EVENT_BUS.post(hue);
			updateQueue.addLast(c);
		}
	}
}
