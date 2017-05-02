package com.bioxx.jmapgen.attributes;

import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;

public abstract class Attribute 
{
	public static UUID River = UUID.fromString("8b21f406-824d-4cde-aa8a-b326db41fb2e");
	public static UUID Gorge = UUID.fromString("81d18f09-9623-47de-a541-b3fc9f6c1969");
	public static UUID Lava = UUID.fromString("b53102e2-60c1-4dd8-b48f-1ea8a5801f1f");
	public static UUID Canyon = UUID.fromString("20117943-cd58-4efd-9757-7e0365e5601f");
	public static UUID Lake = UUID.fromString("ddc68a75-9b5b-4659-aac0-2ec2208c8ffe");
	public static UUID Cave = UUID.fromString("af4caad2-9915-4641-83d6-d631862f7220");
	public static UUID Ore = UUID.fromString("1f05a154-df49-4b01-84d8-46a4d162f125");
	public static UUID Portal = UUID.fromString("8712bb03-da52-4bbb-a0a6-849e38ac243d");
	public static UUID NeedZone = UUID.fromString("004d957a-6fca-4311-a757-6266fba20a89");

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
	public abstract void readFromNBT(NBTTagCompound nbt, com.bioxx.jmapgen.IslandMap map);
}
