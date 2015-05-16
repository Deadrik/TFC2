package jMapGen;

import java.util.Vector;

public class Gorge 
{
	public Vector<GorgeNode> nodes;
	public GorgeNode start;
	public double maxElev = Double.MIN_VALUE;
	public double minElev = Double.MAX_VALUE;

	public Gorge()
	{
		nodes = new Vector<GorgeNode>();
	}

	public void addNode(GorgeNode c)
	{
		if(c.center.elevation < minElev)
			minElev = c.center.elevation;
		if(c.center.elevation > maxElev)
			maxElev = c.center.elevation;
		nodes.add(c);
		if(nodes.size() == 1)
			start = c;
	}

	public boolean hasCenter(GorgeNode c)
	{
		return nodes.contains(c);
	}
}
