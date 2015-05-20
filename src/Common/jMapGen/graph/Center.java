package jMapGen.graph;

import jMapGen.BiomeType;
import jMapGen.Map;
import jMapGen.Point;
import jMapGen.attributes.Attribute;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;


public class Center 
{
	public int index;

	public Point point;  // location

	private EnumSet<Marker> flags = EnumSet.noneOf(Marker.class);

	public BiomeType biome;
	public double elevation = 0; // 0.0-1.0
	public double moisture = 0; // 0.0-1.0

	public Center downslope; // pointer to adjacent center most downhill

	public Vector<Center> neighbors;
	public Vector<Edge> borders;
	public Vector<Corner> corners;

	public HashMap<UUID, Attribute> attribMap;

	public Center()
	{
		index = 0;
		neighbors = new  Vector<Center>();
		borders = new Vector<Edge>();
		corners = new Vector<Corner>();
		attribMap = new HashMap<UUID, Attribute>();
	}

	public Center(int i)
	{
		this();
		index = i;
	}

	public double getElevation()
	{
		return this.elevation;
	}

	public void setElevation(double d)
	{
		this.elevation = d;
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

	public boolean hasAnyMarkersOf(Marker... m)
	{
		for(Marker mk : m)
			if(flags.contains(mk))
				return true;
		return false;
	}

	public void removeMarkers(Marker... m)
	{
		for(Marker mk : m)
			flags.remove(mk);
	}

	public Attribute getAttribute(UUID id)
	{
		return attribMap.get(id);
	}

	public boolean hasAttribute(UUID id)
	{
		return attribMap.containsKey(id);
	}

	public boolean addAttribute(Attribute a)
	{
		if(attribMap.containsKey(a.id))
			return false;

		attribMap.put(a.id, a);
		return true;
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

	/*boolean SameSide(pythagoras.d.Vector p1, pythagoras.d.Vector p2, pythagoras.d.Vector a, pythagoras.d.Vector b)
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

	}*/

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("index", index);
		nbt.setInteger("biome", biome.ordinal());
		nbt.setDouble("xCoord", point.x);
		nbt.setDouble("yCoord", point.y);
		int f = 0;
		for(Marker ff : flags)
		{
			f += ff.ordinal();
		}
		nbt.setInteger("flags", f);
		nbt.setDouble("elevation", elevation);
		nbt.setDouble("moisture", moisture);
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

		Iterator<Attribute> iter = attribMap.values().iterator();
		NBTTagList attribList = new NBTTagList();
		while(iter.hasNext())
		{
			Attribute a = iter.next();
			NBTTagCompound attribNBT = new NBTTagCompound();
			attribNBT.setString("class", a.getClass().getName());
			a.writeToNBT(attribNBT);
			attribList.appendTag(attribNBT);
		}
		nbt.setTag("attribMap", attribList);
	}

	public void readFromNBT(NBTTagCompound nbt, Map m)
	{
		try 
		{
			biome = BiomeType.values()[nbt.getInteger("biome")];
			point = new Point(nbt.getDouble("xCoord"), nbt.getDouble("yCoord"));
			setMarkers(nbt.getInteger("flags"));
			elevation = nbt.getDouble("elevation");
			moisture = nbt.getDouble("moisture");

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

			if(nbt.hasKey("attribMap"))
			{
				NBTTagList list = nbt.getTagList("attribMap", 10);
				for(int i = 0; i < list.tagCount(); i++)
				{
					NBTTagCompound aNBT = list.getCompoundTagAt(i);
					Object o = Class.forName(aNBT.getString("class")).newInstance();
					((Attribute)o).readFromNBT(aNBT, m);
					this.attribMap.put(((Attribute)o).id, (Attribute)o);
				}
			}

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * Used for reading stored nbt information
	 */
	private void setMarkers(int i)
	{
		for(Marker f : Marker.values())
		{
			if((i & f.ordinal()) > 0)
			{
				flags.add(f);
			}
		}
	}

	public enum Marker
	{
		//Important not to change this order if it can be helped.
		Water, Ocean, Coast, CoastWater, Border, Lava, Valley, SmallCrater;
	}
}
