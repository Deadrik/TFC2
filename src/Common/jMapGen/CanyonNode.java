package jMapGen;

import jMapGen.graph.Center;

public class CanyonNode 
{
	private CanyonNode upCanyon;
	Center center;
	private CanyonNode downCanyon;

	public CanyonNode(Center c)
	{
		center = c;	
	}

	public void setUp(CanyonNode u)
	{
		upCanyon = u;
	}
	public void setDown(CanyonNode d)
	{
		downCanyon = d;
	}

	public CanyonNode getUp()
	{
		return this.upCanyon;
	}

	public CanyonNode getDown()
	{
		return this.downCanyon;
	}
}
