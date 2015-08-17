package com.bioxx.jmapgen.pathfinding;

import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;

public class PathNode 
{
	public Center center;
	public PathNode prev;
	public int nodeCost = 1;
	public int transitCost = 1;

	public PathNode(Center c, int cost)
	{
		center = c;
		nodeCost = cost;		
		if(c.hasMarker(Marker.Water))
			transitCost += 10000;
	}
}
