package jMapGen;

import jMapGen.graph.Center;

import java.util.Vector;

public class River 
{
	public Vector<RiverNode> nodes;
	public RiverNode riverStart;
	public int lengthToMerge = 0;

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
