package jMapGen.graph;

import jMapGen.BiomeType;
import jMapGen.Map;
import jMapGen.Point;

import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;


public class Center 
{
	public int index;

	public Point point;  // location

	/**
	 * 1 = water
	 * 2 = ocean
	 * 4 = coast
	 * 8 = coastwater
	 * 16 = border
	 * 32 = canyon
	 * 64 = lava
	 * 128 = valley
	 */
	private int flags = 0;

	public BiomeType biome;
	public double elevation = 0; // 0.0-1.0
	public double moisture = 0; // 0.0-1.0

	private double river = 0;
	public Vector<Center> upriver; // pointer to adjacent corner most uphill
	public Center downriver; // pointer to the downriver hex if this hex is a river
	public Center downslope; // pointer to adjacent center most downhill

	public Vector<Center> neighbors;
	public Vector<Edge> borders;
	public Vector<Corner> corners;

	public Center()
	{
		index = 0;
		neighbors = new  Vector<Center>();
		borders = new Vector<Edge>();
		corners = new Vector<Corner>();
	}

	public Center(int i)
	{
		this();
		index = i;
	}

	public void addRiver(double d)
	{
		river = Math.min(river + d, 4.0);
	}

	public void setRiver(double d)
	{
		river = Math.min(d, 4.0);
	}

	public double getRiver()
	{
		return river;
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

	public boolean isCoastWater()
	{
		return (flags & 8) > 0;
	}

	public void setCoastWater(boolean b)
	{
		if(b)
			flags |= 8;
		else if(isCoastWater())
			flags ^= 8;
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

	public boolean isCanyon()
	{
		return (flags & 32) > 0;
	}

	public void setCanyon(boolean b)
	{
		if(b)
			flags |= 32;
		else if(isCanyon())
			flags ^= 32;
	}

	public boolean isLava()
	{
		return (flags & 64) > 0;
	}

	public void setLava(boolean b)
	{
		if(b)
			flags |= 64;
		else if(isCanyon())
			flags ^= 64;
	}

	public boolean isValley()
	{
		return (flags & 128) > 0;
	}

	public void setValley(boolean b)
	{
		if(b)
			flags |= 128;
		else if(isCanyon())
			flags ^= 128;
	}

	public void addUpRiverCenter(Center c)
	{
		if(upriver == null)
			upriver = new Vector<Center>();
		if(!upriver.contains(c))
			upriver.add(c);
	}

	public Center getClosestNeighbor(Point p)
	{
		double angle = (Math.atan2((p.y - point.y) , (p.x - point.x)) * 180 / Math.PI) - 30;
		if((angle >= 330 || angle < 30) && neighbors.size() > 0)
			return neighbors.get(0);
		if(angle < 90 && neighbors.size() > 1)
			return neighbors.get(1);
		if(angle < 150 && neighbors.size() > 2)
			return neighbors.get(2);
		if(angle < 210 && neighbors.size() > 3)
			return neighbors.get(3);
		if(angle < 270 && neighbors.size() > 4)
			return neighbors.get(4);
		if(angle < 330 && neighbors.size() >= 5)
			return neighbors.get(5);

		return null;
	}

	public Corner getClosestCorner(Point p)
	{
		Corner closest = corners.get(0);
		double distance = p.distanceSq(corners.get(0).point);

		for (int i = 1; i < corners.size(); i++)
		{
			double newDist = p.distanceSq(corners.get(i).point);
			if(newDist < distance)
			{
				distance = newDist;
				closest = corners.get(i);
			}
		}
		return closest;
	}

	/**
	 * Returns an edge shared between two centers. May return null if neighbors are not supplied.
	 */
	public Edge	getSharedEdge(Center c)
	{
		for(Edge e : borders)
		{
			if(e.dCenter0 == this && e.dCenter1 == c)
				return e;
			if(e.dCenter1 == this && e.dCenter0 == c)
				return e;
		}
		return null;
	}

	boolean SameSide(pythagoras.d.Vector p1, pythagoras.d.Vector p2, pythagoras.d.Vector a, pythagoras.d.Vector b)
	{
		pythagoras.d.Vector cp1 = b.subtract(a).cross(p1.subtract(a));
		pythagoras.d.Vector cp2 = b.subtract(a).cross(p2.subtract(a));

		if (cp1.dot(cp2) >= 0) 
			return true;
		else return false;
	}

	boolean PointInTriangle(pythagoras.d.Vector p, pythagoras.d.Vector a, pythagoras.d.Vector b, pythagoras.d.Vector c)
	{
		if (SameSide(p,a, b,c) && SameSide(p,b, a,c) && SameSide(p,c, a,b)) 
			return true;
		else return false;
	}

	boolean InTriangle(pythagoras.d.Vector p, pythagoras.d.Vector a, pythagoras.d.Vector b, pythagoras.d.Vector c)
	{
		// Compute vectors        
		pythagoras.d.Vector v0 = c.subtract(a);
		pythagoras.d.Vector v1 = b.subtract(a);
		pythagoras.d.Vector v2 = p.subtract(a);

		// Compute dot products
		double dot00 = v0.dot(v0);
		double dot01 = v0.dot(v1);
		double dot02 = v0.dot(v2);
		double dot11 = v1.dot(v1);
		double dot12 = v1.dot(v2);

		// Compute barycentric coordinates
		double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		return (u >= 0) && (v >= 0) && (u + v < 1);

	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("index", index);
		nbt.setInteger("biome", biome.ordinal());
		nbt.setDouble("xCoord", point.x);
		nbt.setDouble("yCoord", point.y);
		nbt.setInteger("flags", flags);
		nbt.setDouble("elevation", elevation);
		nbt.setDouble("moisture", moisture);
		nbt.setDouble("river", river);
		if(downriver != null)
			nbt.setInteger("downriver", downriver.index);
		if(downslope != null)
			nbt.setInteger("downslope", downslope.index);

		int[] nArray = new int[neighbors.size()];
		for(int i = 0; i < nArray.length; i++)
		{
			nArray[i] = neighbors.get(i).index;
		}
		nbt.setIntArray("neighbors", nArray);

		nArray = new int[corners.size()];
		for(int i = 0; i < nArray.length; i++)
		{
			nArray[i] = corners.get(i).index;
		}
		nbt.setIntArray("corners", nArray);

		nArray = new int[borders.size()];
		for(int i = 0; i < nArray.length; i++)
		{
			nArray[i] = borders.get(i).index;
		}
		nbt.setIntArray("borders", nArray);

		if(upriver != null && upriver.size() > 0)
		{
			nArray = new int[upriver.size()];
			for(int i = 0; i < nArray.length; i++)
			{
				nArray[i] = upriver.get(i).index;
			}
			nbt.setIntArray("upriver", nArray);
		}
	}

	public void readFromNBT(NBTTagCompound nbt, Map m)
	{
		try 
		{
			biome = BiomeType.values()[nbt.getInteger("biome")];
			point = new Point(nbt.getDouble("xCoord"), nbt.getDouble("yCoord"));
			flags = nbt.getInteger("flags");
			elevation = nbt.getDouble("elevation");
			moisture = nbt.getDouble("moisture");
			river = nbt.getDouble("river");
			if(nbt.hasKey("downriver"))
				downriver = m.centers.get(nbt.getInteger("downriver"));
			if(nbt.hasKey("downslope"))
				downslope = m.centers.get(nbt.getInteger("downslope"));
			int[] nArray = nbt.getIntArray("neighbors");
			for(int i = 0; i < nArray.length; i++)
			{
				this.neighbors.add(m.centers.get(nArray[i]));
			}
			nArray = nbt.getIntArray("corners");
			for(int i = 0; i < nArray.length; i++)
			{
				this.corners.add(m.corners.get(nArray[i]));
			}
			nArray = nbt.getIntArray("borders");
			for(int i = 0; i < nArray.length; i++)
			{
				this.borders.add(m.edges.get(nArray[i]));
			}
			if(nbt.hasKey("upriver"))
			{
				nArray = nbt.getIntArray("upriver");
				upriver = new Vector<Center>();
				for(int i = 0; i < nArray.length; i++)
				{
					this.upriver.add(m.centers.get(nArray[i]));
				}
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
