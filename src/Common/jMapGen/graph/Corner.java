package jMapGen.graph;

import jMapGen.Point;

import java.util.Vector;

public class Corner
{
	public int index;

	public Point point;  // location
	public Boolean ocean = false;  // ocean
	public Boolean water = false;  // lake or ocean
	public Boolean coast = false;  // touches ocean and land polygons
	public Boolean border = false;  // at the edge of the map
	public double elevation;  // 0.0-1.0
	public double moisture;  // 0.0-1.0

	public Vector<Center> touches;
	public Vector<Edge> protrudes;
	public Vector<Corner> adjacent;

	public Corner downslope;  // pointer to adjacent corner most downhill

	public Corner()
	{
		ocean = false;
		water = false;
		coast = false;
		border = false;
		elevation = Double.MAX_VALUE;
	}

	public Edge getTouchingEdge(Corner c)
	{
		for (int i = 0; i < protrudes.size(); i++)
		{
			if(protrudes.get(i).vCorner0 == c || protrudes.get(i).vCorner1 == c)
				return protrudes.get(i);
		}
		return null;
	}

	public Center getClosestCenter(Point p)
	{
		Center closest = null;
		double distance = 1000000;

		for (Center c : touches)
		{
			double newDist = p.distanceSq(c.point);
			if(newDist < distance)
			{
				distance = newDist;
				closest = c;
			}
		}
		return closest;
	}

	public boolean isShoreline()
	{
		if(water)
		{
			for(Center c : touches)
			{
				if(!c.water)
					return true;
			}
		}
		return false;
	}
}
