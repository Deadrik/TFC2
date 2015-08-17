package com.bioxx.jmapgen.attributes;

import java.util.UUID;

import com.bioxx.jmapgen.graph.Center;

import net.minecraft.nbt.NBTTagCompound;

public class GorgeAttribute extends Attribute 
{
	Center up;
	Center down;
	public int gorgeID = 0;

	public GorgeAttribute() 
	{
		super();
	}

	public GorgeAttribute(UUID i) 
	{
		super(i);
	}

	public Center getDown()
	{
		return down;
	}

	public void setDown(Center d)
	{
		down = d;
	}

	public Center getUp()
	{
		return up;
	}

	public void setUp(Center u)
	{
		up = u;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("uuid", id.toString());
		if(down != null)
			nbt.setInteger("down", down.index);

		if(up != null)
			nbt.setInteger("up", up.index);

		nbt.setInteger("gorgeID", gorgeID);

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jmapgen.IslandMap m) 
	{
		this.id = UUID.fromString(nbt.getString("uuid"));
		if(nbt.hasKey("down"))
			down = m.centers.get(nbt.getInteger("down"));
		if(nbt.hasKey("up"))
			up = m.centers.get(nbt.getInteger("up"));
		gorgeID = nbt.getInteger("gorgeID");
	}

}
