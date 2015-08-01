package com.bioxx.jMapGen.pathfinding;

import java.util.LinkedList;

import com.bioxx.jMapGen.graph.Center;

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
