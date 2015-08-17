package com.bioxx.jmapgen;

import java.util.Vector;

import com.bioxx.jmapgen.graph.Center;

public class Lake 
{
	public Vector<Center> centers;
	public Center lowestCenter;
	public int lakeID = 0;

	public Lake()
	{
		centers = new Vector<Center>();
	}

	public void addCenter(Center c)
	{
		if(lowestCenter == null || c.elevation < lowestCenter.elevation)
		{
			lowestCenter = c;
		}
		centers.add(c);
	}

	public boolean hasCenter(Center c)
	{
		return centers.contains(c);
	}
}
