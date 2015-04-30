package com.bioxx.tfc2.World;

import jMapGen.Map;

public class CachedIsland 
{
	public long lastAccess;
	Map islandData;

	public CachedIsland(Map island)
	{
		islandData = island;
	}

	public Map getIslandMap()
	{
		lastAccess = System.currentTimeMillis();
		return islandData;
	}
}
