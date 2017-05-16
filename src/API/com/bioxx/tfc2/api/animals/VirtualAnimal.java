package com.bioxx.tfc2.api.animals;

import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.tfc2.api.types.Gender;

public class VirtualAnimal
{
	String animalType;
	Gender gender;
	boolean isLoaded = false;
	public EntityLiving entity;

	public VirtualAnimal(String type, Gender g)
	{
		animalType = type;
		gender = g;
	}

	public boolean isLoaded()
	{
		return isLoaded;
	}

	public void setLoaded(EntityLiving e)
	{
		isLoaded = true;
		entity = e;
	}

	public void setUnloaded()
	{
		isLoaded = false;
	}

	public EntityLiving getEntity()
	{
		return entity;
	}

	public Gender getGender()
	{
		return gender;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		animalType = nbt.getString("type");
		gender = Gender.valueOf(nbt.getString("gender"));
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setString("type", animalType);
		nbt.setString("gender", gender.getName());
	}
}