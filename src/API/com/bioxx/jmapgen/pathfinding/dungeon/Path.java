package com.bioxx.jmapgen.pathfinding.dungeon;

import java.util.LinkedList;

import com.bioxx.jmapgen.graph.Center;

public class Path 
{
	int totalCost = 0;
	public LinkedList<PathNode> path = new LinkedList<PathNode>();

	public void addNode(PathNode c)
	{
		path.add(c);
	}

	public boolean contains(Center c)
	{
		for(PathNode pn : path)
		{
			if(pn.center == c)
				return true;
		}
		return false;
	}
}
