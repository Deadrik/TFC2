package com.bioxx.jMapGen;

import com.bioxx.jMapGen.graph.Center;

public class RiverNode 
{
	Center upRiver;
	Center center;
	Center downRiver;

	public RiverNode(Center c)
	{
		center = c;	
	}

	public void setUpRiver(Center u)
	{
		upRiver = u;
	}
	public void setDownRiver(Center d)
	{
		downRiver = d;
	}
}
