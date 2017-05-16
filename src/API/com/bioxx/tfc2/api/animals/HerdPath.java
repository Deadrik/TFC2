package com.bioxx.tfc2.api.animals;

import java.util.LinkedList;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.pathfinding.CenterPath;
import com.bioxx.jmapgen.pathfinding.CenterPathNode;

public class HerdPath
{
	long calcTimestamp;
	LinkedList<Center> path = new LinkedList<Center>();

	public HerdPath(long timestamp)
	{
		calcTimestamp = timestamp;
	}

	public HerdPath(long timestamp, CenterPath cPath)
	{
		this(timestamp);
		for(CenterPathNode c : cPath.path)
		{
			addNode(c.center);
		}
	}

	public void addNode(Center c)
	{
		path.add(c);
	}

	public void recalculatePath(Center start)
	{

	}

	public Center move()
	{
		return path.removeLast();
	}

	public void readFromNBT(NBTTagCompound nbt, IslandMap map)
	{
		this.calcTimestamp = nbt.getLong("timestamp");
		int[] pathArray = nbt.getIntArray("path");
		for(int i = 0; i < pathArray.length; i++)
		{
			path.add(map.centers.get(pathArray[i]));
		}
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setLong("timestamp", calcTimestamp);
		int[] pathArray = new int[path.size()];

		for(int i = 0; i < path.size(); i++)
		{
			pathArray[i] = path.get(i).index;
		}

		nbt.setIntArray("path", pathArray);
	}
}