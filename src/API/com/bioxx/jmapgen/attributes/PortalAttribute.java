package com.bioxx.jmapgen.attributes;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

import com.bioxx.jmapgen.Spline3D;

public class PortalAttribute extends Attribute 
{
	public int destMapID = 0;
	public EnumFacing direction = EnumFacing.NORTH;

	ArrayList<BlockPos> path = new ArrayList<BlockPos>();
	Spline3D spline = null;

	public PortalAttribute() 
	{
		super(Attribute.Portal);
	}

	/**
	 * @param dest This is the id of the islandmap that should connect to this map
	 * @param dir 0 = North | 1 = South | 2 = East | 3 = West
	 */
	public PortalAttribute(int dest, EnumFacing dir) 
	{
		this();
		destMapID = dest;
		direction = dir;
	}

	public void setPath(ArrayList<BlockPos> p)
	{
		path = p;
		spline = new Spline3D(path);
	}

	/**
	 * @return returns a spline path from the list of BlockPos in path, may return null if path is not set.
	 */
	public Spline3D getSpline()
	{
		return spline;
	}


	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("uuid", id.toString());
		nbt.setInteger("destMapID", destMapID);
		nbt.setInteger("direction", direction.getHorizontalIndex());

		BlockPos p;
		if(path.size() > 0)
		{
			int[] x = new int[path.size()];
			int[] y = new int[path.size()];
			int[] z = new int[path.size()];
			for(int i = 0; i < path.size(); i++)
			{
				p = path.get(i);
				x[i] = p.getX();
				y[i] = p.getY();
				z[i] = p.getZ();
			}

			nbt.setIntArray("x", x);
			nbt.setIntArray("y", y);
			nbt.setIntArray("z", z);
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jmapgen.IslandMap m) 
	{
		this.id = UUID.fromString(nbt.getString("uuid"));
		destMapID = nbt.getInteger("destMapID");
		direction = EnumFacing.getHorizontal(nbt.getInteger("direction"));

		int[] x = nbt.getIntArray("x");
		int[] y = nbt.getIntArray("y");
		int[] z = nbt.getIntArray("z");
		path = new ArrayList<BlockPos>();
		for(int i = 0; i < x.length; i++)
		{
			path.add(new BlockPos(x[i], y[i], z[i]));
		}
		if(path.size() > 0)
			spline = new Spline3D(path);
	}

}
