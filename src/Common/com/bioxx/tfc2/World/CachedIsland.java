package com.bioxx.tfc2.World;

import com.bioxx.jMapGen.Map;

public class CachedIsland 
{
	public long lastAccess;
	public Map islandData;

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
