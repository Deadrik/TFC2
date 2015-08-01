package com.bioxx.jMapGen.graph;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bioxx.jMapGen.BiomeType;
import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.attributes.Attribute;
import com.bioxx.jMapGen.attributes.RiverAttribute;
import com.bioxx.tfc2.api.Types.Moisture;


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

		return this;
	}

	public Center getNeighbor(HexDirection dir)
	{
		switch(dir)
		{
		case North: return neighbors.get(1);
		case NorthEast: return neighbors.get(2);
		case SouthEast: return neighbors.get(3);
		case South: return neighbors.get(4);
		case SouthWest: return neighbors.get(5);
		case NorthWest: return neighbors.get(0);
		default: return neighbors.get(0);
		}
	}

	public HexDirection getDirection(Center c)
	{
		for(HexDirection n : HexDirection.values())
		{
			if(getNeighbor(n) == c)
				return n;
		}
		return null;
	}

	public Center getRandomNeighbor(Random r)
	{
		return neighbors.get(r.nextInt(neighbors.size()));
	}

	public Center getHighestNeighbor()
	{
		Center highest = this;
		for(Iterator<Center> centerIter2 = neighbors.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(highest == null || center2.elevation > highest.elevation)
				highest = center2;
		}
		RiverAttribute attrib = ((RiverAttribute)getAttribute(Attribute.riverUUID));
		if(attrib != null && attrib.upriver != null)
		{
			highest = getLowestFromGroup(attrib.upriver);
		}
		return highest;
	}

	public Center getLowestNeighbor()
	{
		Center lowest = this;
		for(Iterator<Center> centerIter2 = neighbors.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(lowest == null || center2.elevation < lowest.elevation)
				lowest = center2;
		}
		RiverAttribute attrib = ((RiverAttribute)getAttribute(Attribute.riverUUID));
		if(attrib != null && attrib.getDownRiver() != null)
			lowest = attrib.getDownRiver();
		return lowest;
	}

	private Center getLowestFromGroup(Vector<Center> group)
	{
		Center lowest = group.get(0);
		for(Iterator<Center> centerIter2 = group.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(lowest == null || center2.elevation < lowest.elevation)
				lowest = center2;
		}
		return lowest;
	}

	private Center getHighestFromGroup(Vector<Center> group)
	{
		Center highest = group.get(0);
		for(Iterator<Center> centerIter2 = group.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(highest == null || center2.elevation > highest.elevation)
				highest = center2;
		}
		return highest;
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

	public Moisture getMoisture()
	{
		return Moisture.fromVal(moisture);
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("index", index);
		nbt.setInteger("biome", biome.ordinal());
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

	public void readFromNBT(NBTTagCompound nbt, IslandMap m)
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
			if((i & f.getFlag()) > 0)
			{
				flags.add(f);
			}
		}
	}

	public enum Marker
	{
		//Important not to change this order if it can be helped.
		Water, Ocean, Coast, CoastWater, Border, Lava, Valley, SmallCrater, Pond;

		public int getFlag()
		{
			return 1 << this.ordinal();
		}
	}

	public enum HexDirection
	{
		North(0), South(1), NorthEast(2), NorthWest(3), SouthEast(4), SouthWest(5);

		int order;

		HexDirection (int o)
		{
			order = o;
		}
		public HexDirection getOpposite()
		{
			switch(this)
			{
			case North: return South;
			case South: return North;
			case NorthEast: return SouthWest;
			case NorthWest: return SouthEast;
			case SouthEast: return NorthWest;
			case SouthWest: return NorthEast;
			default: return North;
			}
		}

		public int getOrder()
		{
			return order;
		}
	}
}
