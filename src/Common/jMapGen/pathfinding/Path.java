package jMapGen.pathfinding;

import jMapGen.graph.Center;

import java.util.Vector;

public class Path 
{
	int totalCost = 0;
	public Vector<PathNode> path = new Vector<PathNode>();

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
