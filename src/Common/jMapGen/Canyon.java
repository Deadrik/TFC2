package jMapGen;

import java.util.Vector;

public class Canyon 
{
	public Vector<CanyonNode> nodes;
	public CanyonNode start;
	public double maxElev = Double.MIN_VALUE;
	public double minElev = Double.MAX_VALUE;

	public Canyon()
	{
		nodes = new Vector<CanyonNode>();
	}

	public void addNode(CanyonNode c)
	{
		if(c.center.elevation < minElev)
			minElev = c.center.elevation;
		if(c.center.elevation > maxElev)
			maxElev = c.center.elevation;
		nodes.add(c);
		if(nodes.size() == 1)
			start = c;
	}

	public boolean hasCenter(CanyonNode c)
	{
		return nodes.contains(c);
	}
}
