package com.bioxx.tfc2.world;

import com.bioxx.jmapgen.IslandMap;

public class CachedIsland 
{
	public long lastAccess;
	public IslandMap islandData;

	public CachedIsland(IslandMap island)
	{
		islandData = island;
	}

	public IslandMap getIslandMap()
	{
		lastAccess = System.currentTimeMillis();
		return islandData;
	}
}
