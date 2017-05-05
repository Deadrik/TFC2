package com.bioxx.jmapgen.graph;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.AxisAlignedBB;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.RiverAttribute;
import com.bioxx.tfc2.api.types.Moisture;


public class Center 
{
	public int index;

	public Point point;  // location

	private EnumSet<Marker> flags = EnumSet.noneOf(Marker.class);

	public BiomeType biome;
	public double elevation = 0; // 0.0-1.0
	private float moisture = 0; // 0.0-1.0

	public Center downslope; // pointer to adjacent center most downhill

	public Vector<Center> neighbors;
	public Vector<Edge> borders;
	public Vector<Corner> corners;

	public HashMap<UUID, Attribute> attribMap;
	public boolean hasGenerated = false;

	private NBTTagCompound customNBT;

	public Center()
	{
		index = 0;
		neighbors = new  Vector<Center>();
		borders = new Vector<Edge>();
		corners = new Vector<Corner>();
		attribMap = new HashMap<UUID, Attribute>();
		customNBT = new NBTTagCompound();
	}

	public Center(int i)
	{
		this();
		index = i;
	}

	public float getMoistureRaw()
	{
		return this.moisture;
	}

	public void setMoistureRaw(double d)
	{
		this.moisture = (float)d;
	}

	public void setMoistureRaw(float d)
	{
		this.moisture = d;
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

	public void resetMarkers()
	{
		flags.clear();
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

	public double getAverageElevation()
	{
		double sum = elevation;
		int total = 1;
		for(Center n : neighbors)
		{
			total++;
			sum += n.getElevation();
		}

		sum/=total;
		return sum;
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
		case North: return neighbors.size() > 1 ? neighbors.get(1) : null;
		case NorthEast: return neighbors.size() > 2 ? neighbors.get(2) : null;
		case SouthEast: return neighbors.size() > 3 ? neighbors.get(3) : null;
		case South: return neighbors.size() > 4 ? neighbors.get(4) : null;
		case SouthWest: return neighbors.size() > 5 ? neighbors.get(5) : null;
		case NorthWest: return neighbors.size() > 0 ? neighbors.get(0) : null;
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

	public Center getRandomNeighborExcept(Random r, Center c)
	{
		Center out = getRandomNeighbor(r);
		while(out == c)
			out = getRandomNeighbor(r);
		return out;
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
		RiverAttribute attrib = ((RiverAttribute)getAttribute(Attribute.River));
		if(attrib != null && attrib.upriver != null)
		{
			highest = getHighestFromGroup(attrib.upriver);
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
		RiverAttribute attrib = ((RiverAttribute)getAttribute(Attribute.River));
		if(attrib != null && attrib.getDownRiver() != null)
			lowest = attrib.getDownRiver();
		return lowest;
	}

	public Center getLowestFromGroup(Vector<Center> group)
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

	public Center getHighestFromGroup(Vector<Center> group)
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

	public Center getRandomFromGroup(Random r, Vector<Center> centers)
	{
		if(centers.size() == 0)
			return null;
		return centers.get(r.nextInt(centers.size()));
	}

	public Vector<Center> getOnlyHigherCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : neighbors)
			if(c.getElevation() > getElevation())
				out.add(c);

		return out;
	}

	public Vector<Center> getOnlyLowerCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : neighbors)
			if(c.getElevation() < getElevation())
				out.add(c);

		return out;
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
		long f = 0;
		for(Marker ff : flags)
		{
			f += ff.getFlag();
		}
		nbt.setLong("flags", f);
		nbt.setDouble("elevation", elevation);
		nbt.setFloat("moisture", moisture);
		nbt.setBoolean("hasGenerated", hasGenerated);


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

		nbt.setTag("CustomData", this.customNBT);
	}

	public void readFromNBT(NBTTagCompound nbt, IslandMap m)
	{
		try 
		{
			biome = BiomeType.values()[nbt.getInteger("biome")];
			point = new Point(nbt.getDouble("xCoord"), nbt.getDouble("yCoord"));
			setMarkers(nbt.getLong("flags"));
			elevation = nbt.getDouble("elevation");
			moisture = nbt.getFloat("moisture");
			hasGenerated = nbt.getBoolean("hasGenerated");

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
			this.customNBT = nbt.getCompoundTag("CustomData");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	/**
	 * @return Access to an nbt tag used specifically for saving custom data to a hex.
	 */
	public NBTTagCompound getCustomNBT()
	{
		return this.customNBT;
	}

	public AxisAlignedBB getAABB()
	{
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxZ = Double.MIN_VALUE;

		for(Corner c : corners)
		{
			minX = Math.min(minX, c.point.x);
			maxX = Math.max(maxX, c.point.x);
			minZ = Math.min(minZ, c.point.y);
			maxZ = Math.max(maxZ, c.point.y);
		}

		return new AxisAlignedBB(minX, 0, minZ, maxX, 1, maxZ);
	}


	/**
	 * Used for reading stored nbt information
	 */
	private void setMarkers(long i)
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
		Water(1), 
		Ocean(2), 
		Coast(3), 
		CoastWater(4), 
		Border(5), 
		Lava(6), 
		Valley(7), 
		SmallCrater(8), 
		Pond(9), 
		Spire(10),
		Volcano(11),
		Mesa(12),
		Clearing(13);

		long flag;
		Marker(int f)
		{
			//Sets it to a bit flag
			flag = 1 << f;
		}

		public long getFlag()
		{
			return flag;
		}
	}

	public enum HexDirection
	{
		NorthWest(0), North(1), NorthEast(2), SouthEast(3), South(4), SouthWest(5);

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

		public HexDirection getNextClockwise()
		{
			if( order == 5)
				return values()[0];
			else
				return values()[order+1];
		}

		public HexDirection getNextCounterClockwise()
		{
			if( order == 0)
				return values()[5];
			else
				return values()[order-1];
		}

		public HexDirection getRandomTurnSmall(Random r, boolean mid)
		{
			int rand = mid ? 5 : 4;
			switch(r.nextInt(rand))
			{
			case 0: return getNextClockwise();
			case 1: return getNextCounterClockwise();
			default: return this;
			}
		}

		public HexDirection getRandomTurnSmall(Random r)
		{
			return getRandomTurnSmall(r, true);
		}

		public HexDirection getRandomTurnBig(Random r, boolean mid)
		{
			int rand = mid ? 5 : 4;
			switch(r.nextInt(rand))
			{
			case 0: return getNextClockwise();
			case 1: return getNextCounterClockwise();
			case 2: return getNextClockwise().getNextClockwise();
			case 3: return getNextCounterClockwise().getNextCounterClockwise();
			default: return this;
			}
		}

		public HexDirection getRandomTurnBig(Random r)
		{
			return getRandomTurnBig(r, true);
		}

		public int getOrder()
		{
			return order;
		}
	}
}
