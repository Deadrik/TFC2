package com.bioxx.tfc2.api;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.tfc2.api.types.Gender;

public class VirtualAnimal
{
	String animalType;
	Gender gender;

	public VirtualAnimal(String type, Gender g)
	{
		animalType = type;
		gender = g;
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