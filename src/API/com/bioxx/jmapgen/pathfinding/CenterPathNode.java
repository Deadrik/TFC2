package com.bioxx.jmapgen.pathfinding;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

public class CenterPathNode 
{
	public Center center;
	public CenterPathNode prev;
	public int nodeCost = 1;
	public int transitCost = 1;

	public CenterPathNode(Center c, int cost)
	{
		center = c;
		nodeCost = cost;
	}

	public CenterPathNode(Center c, CenterPathNode p, int cost)
	{
		center = c;
		nodeCost = cost;
		prev = p;
	}

	public CenterPathNode calculate(IslandMap map, IPathProfile profile)
	{
		transitCost = profile.getPathWeight(map, prev != null ? prev.center : null, center);
		return this;
	}
}
