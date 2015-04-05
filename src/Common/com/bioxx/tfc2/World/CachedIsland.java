package com.bioxx.tfc2.World;

import jMapGen.IslandMapGen;
import jMapGen.Map;

public class CachedIsland 
{
	public long lastAccess;
	IslandMapGen islandData;

	public CachedIsland(IslandMapGen island)
	{
		islandData = island;
	}

	public Map getIslandMap()
	{
		lastAccess = System.currentTimeMillis();
		return islandData.map;
	}
}
