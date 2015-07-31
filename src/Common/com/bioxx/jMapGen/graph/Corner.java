package com.bioxx.jMapGen.graph;

import java.util.EnumSet;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.graph.Center.Marker;

public class Corner
{
	public int index = 0;

	public Point point;  // location

	private EnumSet<Marker> flags = EnumSet.noneOf(Marker.class);
	public double elevation;  // 0.0-1.0
	public double moisture;  // 0.0-1.0

	public Vector<Center> touches;
	public Vector<Edge> protrudes;
	public Vector<Corner> adjacent;

	public Corner()
	{
		elevation = Double.MAX_VALUE;
		touches = new Vector<Center>();
		protrudes = new Vector<Edge>();
		adjacent = new Vector<Corner>();
	}

	public Corner(int i)
	{
		this();
		index = i;
	}

	public void setMarkers(Marker... m)
	{
		for(Marker mk : m)
			flags.add(mk);
	}

	public boolean hasMarker(Marker m)
	{
		return flags.contains(m);
	}

	public void removeMarkers(Marker... m)
	{
		for(Marker mk : m)
			flags.remove(mk);
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
		if(this.hasMarker(Marker.Water))
		{
			for(Center c : touches)
			{
				if(!c.hasMarker(Marker.Water))
					return true;
			}
		}
		return false;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("index", index);
		nbt.setDouble("xCoord", point.x);
		nbt.setDouble("yCoord", point.y);
		int f = 0;
		for(Marker ff : flags)
		{
			f += ff.getFlag();
		}
		nbt.setInteger("flags", f);
		nbt.setDouble("elevation", elevation);
		nbt.setDouble("moisture", moisture);

		int[] nArray = new int[touches.size()];
		for(int i = 0; i < nArray.length; i++)
		{
			nArray[i] = touches.get(i).index;
		}
		nbt.setIntArray("touches", nArray);

		nArray = new int[protrudes.size()];
		for(int i = 0; i < nArray.length; i++)
		{
			nArray[i] = protrudes.get(i).index;
		}
		nbt.setIntArray("protrudes", nArray);

		nArray = new int[adjacent.size()];
		for(int i = 0; i < nArray.length; i++)
		{
			nArray[i] = adjacent.get(i).index;
		}
		nbt.setIntArray("adjacent", nArray);
	}

	public void readFromNBT(NBTTagCompound nbt, IslandMap m)
	{
		this.point = new Point(nbt.getDouble("xCoord"), nbt.getDouble("yCoord"));
		setMarkers(nbt.getInteger("flags"));
		elevation = nbt.getDouble("elevation");
		moisture = nbt.getDouble("moisture");

		int[] nArray = nbt.getIntArray("touches");
		for(int i = 0; i < nArray.length; i++)
		{
			this.touches.add(m.centers.get(nArray[i]));
		}
		nArray = nbt.getIntArray("adjacent");
		for(int i = 0; i < nArray.length; i++)
		{
			this.adjacent.add(m.corners.get(nArray[i]));
		}
		nArray = nbt.getIntArray("protrudes");
		for(int i = 0; i < nArray.length; i++)
		{
			this.protrudes.add(m.edges.get(nArray[i]));
		}
	}

	/**
	 * Used for reading stored nbt information
	 */
	private void setMarkers(int i)
	{
		for(Marker f : Marker.values())
		{
			if((i & f.getFlag()) > 0)
			{
				flags.add(f);
			}
		}
	}
}
