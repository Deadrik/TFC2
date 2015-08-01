package com.bioxx.jMapGen;

import java.util.Vector;

import com.bioxx.jMapGen.graph.Center;

public class River 
{
	public Vector<RiverNode> nodes;
	public RiverNode riverStart;
	public double riverWidth = 0.5;
	public int lengthToMerge = 0;
	public boolean hasWater = true;

	public River()
	{
		nodes = new Vector<RiverNode>();
	}

	public void addCenter(Center c)
	{
		nodes.add(new RiverNode(c));
	}

	public void addNode(RiverNode c)
	{
		nodes.add(c);
		if(nodes.size() == 1)
			riverStart = c;
	}

	public boolean hasCenter(RiverNode c)
	{
		return nodes.contains(c);
	}
}
