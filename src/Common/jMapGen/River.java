package jMapGen;

import jMapGen.graph.Center;

import java.util.Vector;

public class River 
{
	public Vector<Center> centers;
	public Center riverStart;

	public River()
	{
		centers = new Vector<Center>();
	}

	public void addCenter(Center c)
	{
		centers.add(c);
	}

	public boolean hasCenter(Center c)
	{
		return centers.contains(c);
	}
}
