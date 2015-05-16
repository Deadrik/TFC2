package jMapGen.pathfinding;

import jMapGen.graph.Center;

public class PathNode 
{
	public Center center;
	public PathNode prev;
	public int nodeCost = 1;

	public PathNode(Center c, int cost)
	{
		center = c;
		nodeCost = cost;
	}
}
