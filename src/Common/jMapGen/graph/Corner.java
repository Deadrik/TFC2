package jMapGen.graph;

import jMapGen.Map;
import jMapGen.Point;

import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;

public class Corner
{
	public int index = 0;

	public Point point;  // location

	private int flags = 0;
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
		if(isWater())
		{
			for(Center c : touches)
			{
				if(!c.isWater())
					return true;
			}
		}
		return false;
	}

	public boolean isWater()
	{
		return (flags & 1) > 0;
	}

	public void setWater(boolean b)
	{
		if(b)
			flags |= 1;
		else if(isWater())
			flags ^= 1;
	}

	public boolean isOcean()
	{
		return (flags & 2) > 0;
	}

	public void setOcean(boolean b)
	{
		if(b)
			flags |= 2;
		else if(isOcean())
			flags ^= 2;
	}

	public boolean isCoast()
	{
		return (flags & 4) > 0;
	}

	public void setCoast(boolean b)
	{
		if(b)
			flags |= 4;
		else if(isCoast())
			flags ^= 4;
	}

	public boolean isBorder()
	{
		return (flags & 16) > 0;
	}

	public void setBorder(boolean b)
	{
		if(b)
			flags |= 16;
		else if(isBorder())
			flags ^= 16;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("index", index);
		nbt.setDouble("xCoord", point.x);
		nbt.setDouble("yCoord", point.y);
		nbt.setInteger("flags", flags);
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

	public void readFromNBT(NBTTagCompound nbt, Map m)
	{
		this.point = new Point(nbt.getDouble("xCoord"), nbt.getDouble("yCoord"));
		this.flags = nbt.getInteger("flags");
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
}
