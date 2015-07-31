package com.bioxx.jMapGen.attributes;

import java.util.UUID;
import java.util.Vector;

import com.bioxx.jMapGen.graph.Center;

import net.minecraft.nbt.NBTTagCompound;

public class RiverAttribute extends Attribute 
{
	public Vector<Center> upriver;
	Center downriver;
	double river;

	public RiverAttribute() 
	{
		super();
	}

	public RiverAttribute(UUID i) 
	{
		super(i);
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

	public Center getDownRiver()
	{
		return downriver;
	}

	public void setDownRiver(Center d)
	{
		downriver = d;
	}

	public void addUpRiverCenter(Center c)
	{
		if(upriver == null)
			upriver = new Vector<Center>();
		if(!upriver.contains(c))
			upriver.add(c);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("uuid", id.toString());
		nbt.setDouble("river", river);
		if(downriver != null)
			nbt.setInteger("downriver", downriver.index);

		if(upriver != null && upriver.size() > 0)
		{
			int[] nArray = new int[upriver.size()];
			for(int i = 0; i < nArray.length; i++)
			{
				nArray[i] = upriver.get(i).index;
			}
			nbt.setIntArray("upriver", nArray);
		}

	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jMapGen.IslandMap m) 
	{
		this.id = UUID.fromString(nbt.getString("uuid"));
		river = nbt.getDouble("river");
		if(nbt.hasKey("downriver"))
			downriver = m.centers.get(nbt.getInteger("downriver"));
		if(nbt.hasKey("upriver"))
		{
			int[] nArray = nbt.getIntArray("upriver");
			upriver = new Vector<Center>();
			for(int i = 0; i < nArray.length; i++)
			{
				this.upriver.add(m.centers.get(nArray[i]));
			}
		}

	}

}
