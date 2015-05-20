package com.bioxx.jMapGen.attributes;

import java.util.UUID;

import com.bioxx.jMapGen.graph.Center;

import net.minecraft.nbt.NBTTagCompound;

public class CanyonAttribute extends Attribute 
{
	Center up;
	Center down;
	public boolean isNode = false;
	public int nodeNum = 0;

	public CanyonAttribute() 
	{
		super();
	}

	public CanyonAttribute(UUID i, int num) 
	{
		super(i);
		nodeNum = num;
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
		nbt.setBoolean("isNode", isNode);
		nbt.setInteger("nodeNum", nodeNum);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jMapGen.Map m) 
	{
		this.id = UUID.fromString(nbt.getString("uuid"));
		if(nbt.hasKey("down"))
			down = m.centers.get(nbt.getInteger("down"));
		if(nbt.hasKey("up"))
			up = m.centers.get(nbt.getInteger("up"));
		isNode = nbt.getBoolean("isNode");
		nodeNum = nbt.getInteger("nodeNum");
	}

}
