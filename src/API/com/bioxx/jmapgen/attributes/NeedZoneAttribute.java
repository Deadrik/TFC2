package com.bioxx.jmapgen.attributes;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.tfc2.api.animals.HerdGoalEnum;

public class NeedZoneAttribute extends Attribute 
{
	public String animalType;
	public HerdGoalEnum goalType;

	public NeedZoneAttribute() 
	{
		super(Attribute.NeedZone);
	}

	public NeedZoneAttribute(UUID i) 
	{
		super(i);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		nbt.setString("uuid", id.toString());
		nbt.setString("animalType", animalType);
		nbt.setString("goalType", goalType.getName());
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, com.bioxx.jmapgen.IslandMap m) 
	{
		this.id = UUID.fromString(nbt.getString("uuid"));
		goalType = HerdGoalEnum.getEnum(nbt.getString("goalType"));
		animalType = nbt.getString("animalType");
	}

}
