package jMapGen;

import jMapGen.graph.Center;

public class GorgeNode 
{
	private GorgeNode upCanyon;
	Center center;
	private GorgeNode downCanyon;

	public GorgeNode(Center c)
	{
		center = c;	
	}

	public void setUp(GorgeNode u)
	{
		upCanyon = u;
	}
	public void setDown(GorgeNode d)
	{
		downCanyon = d;
	}

	public GorgeNode getUp()
	{
		return this.upCanyon;
	}

	public GorgeNode getDown()
	{
		return this.downCanyon;
	}
}
