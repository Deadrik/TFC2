package com.bioxx.jMapGen.attributes;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public class LakeAttribute extends Attribute 
{
	int lakeID = -1;
	int borderDistance = 1000;
	boolean isMarsh = false;
	double lakeElev = 0;

	public LakeAttribute() 
	{
		super();
	}

	public LakeAttribute(UUID i) 
	{
		super(i);
	}

	public void setLakeID(int id)
	{
		lakeID = id;
	}

	public int getLakeID()
	{
		return lakeID;
	}

	public void setBorderDistance(int dist)
	{
		borderDistance = dist;
	}

	public int getBorderDistance()
	{
		return borderDistance;
	}

	public void setMarsh(boolean m)
	{
		isMarsh = m;
	}

	public boolean getMarsh()
	{
		return isMarsh;
	}

	public void setLakeElev(double id)
	{
		lakeElev = id;
	}

	public double getLakeElev()
	{
		return lakeElev;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("uuid", id.toString());
		nbt.setInteger("lakeID", lakeID);
		nbt.setInteger("borderDistance", borderDistance);
		nbt.setBoolean("isMarsh", isMarsh);
		nbt.setDouble("lakeElev", lakeElev);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jMapGen.IslandMap m) 
	{
		this.id = UUID.fromString(nbt.getString("uuid"));
		this.lakeID = nbt.getInteger("lakeID");
		this.borderDistance = nbt.getInteger("borderDistance");
		this.isMarsh = nbt.getBoolean("isMarsh");
		this.lakeElev = nbt.getDouble("lakeElev");
	}

}
