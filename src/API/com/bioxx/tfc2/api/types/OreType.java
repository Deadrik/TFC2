package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum OreType implements IStringSerializable
{
	Bismuthinite("Bismuthinite", 0),Cassiterite("Cassiterite", 1),Garnierite("Garnierite", 2),Hematite("Hematite", 3),
	Limonite("Limonite", 4),Magnetite("Magnetite", 5),Malachite("Malachite", 6),
	NativeGold("Native Gold", 7),Sphalerite("Sphalerite", 8),Tetrahedrite("Tetrahedrite", 9),Galena("Galena", 10),
	BituminousCoal("Bituminous Coal", 11),Lignite("Lignite", 12);

	private String name;
	private int meta;

	OreType(String s, int id)
	{
		name = s;
		meta = id;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getMeta()
	{
		return meta;
	}

	public static OreType fromMeta(int meta)
	{
		for(int i = 0; i < OreType.values().length; i++)
		{
			if(OreType.values()[i].meta == meta)
				return OreType.values()[i];
		}
		return null;
	}

	public static OreType fromName(String n)
	{
		for(int i = 0; i < OreType.values().length; i++)
		{
			if(OreType.values()[i].name.equals(n))
				return OreType.values()[i];
		}
		return null;
	}

}