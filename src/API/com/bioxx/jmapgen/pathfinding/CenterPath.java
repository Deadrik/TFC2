package com.bioxx.jmapgen.pathfinding;

import java.util.LinkedList;

import com.bioxx.jmapgen.graph.Center;

public class CenterPath 
{
	public LinkedList<CenterPathNode> path = new LinkedList<CenterPathNode>();

	public void addNode(CenterPathNode c)
	{
		path.add(c);
	}

	public boolean contains(Center c)
	{
		for(CenterPathNode pn : path)
		{
			if(pn.center == c)
				return true;
		}
		return false;
	}
}
