package com.bioxx.jmapgen.pathfinding;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

public interface IPathProfile 
{
	public int getPathWeight(IslandMap map, Center prev, Center c);
	public boolean shouldIgnoreCenter(IslandMap map, Center prev, Center c);
}
