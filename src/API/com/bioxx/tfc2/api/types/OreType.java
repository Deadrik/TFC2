package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum OreType implements IStringSerializable
{
	Bismuthinite("bismuthinite", 0),Cassiterite("cassiterite", 1),Garnierite("garnierite", 2),Hematite("hematite", 3),
	Limonite("limonite", 4),Magnetite("magnetite", 5),Malachite("malachite", 6),
	NativeGold("native_gold", 7),Sphalerite("sphalerite", 8),Tetrahedrite("tetrahedrite", 9),Galena("galena", 10),
	Anthracite("anthracite", 11),Lignite("lignite", 12);

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

	public static String[] getNamesArray()
	{
		String[] s = new String[values().length];
		for(int i = 0; i < OreType.values().length; i++)
		{
			s[i] = OreType.values()[i].getName();
		}
		return s;
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