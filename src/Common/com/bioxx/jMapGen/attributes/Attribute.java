package com.bioxx.jMapGen.attributes;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public abstract class Attribute 
{
	public static UUID riverUUID = UUID.fromString("8b21f406-824d-4cde-aa8a-b326db41fb2e");
	public static UUID gorgeUUID = UUID.fromString("81d18f09-9623-47de-a541-b3fc9f6c1969");
	public static UUID lavaUUID = UUID.fromString("b53102e2-60c1-4dd8-b48f-1ea8a5801f1f");
	public static UUID canyonUUID = UUID.fromString("20117943-cd58-4efd-9757-7e0365e5601f");

	public UUID id;

	protected Attribute()
	{
	}

	protected Attribute(UUID i)
	{
		id = i;
	}

	@Override
	public String toString()
	{
		return id.toString();
	}

	public abstract void writeToNBT(NBTTagCompound nbt);
	public abstract void readFromNBT(NBTTagCompound nbt, com.bioxx.jMapGen.Map map);
}
