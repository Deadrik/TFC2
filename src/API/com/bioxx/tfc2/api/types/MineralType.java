package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum MineralType implements IStringSerializable
{
	Borax("Borax", 0),Cinnabar("Cinnabar", 1),Cryolite("Cryolite", 2),Graphite("Graphite", 3),
	Kaolinite("Kaolinite", 4),Kimberlite("Kimberlite", 5),LapisLazuli("LapisLazuli", 6),Saltpeter("Saltpeter", 7),
	Sulfur("Sulfur", 8),Sylvite("Sylvite", 9);

	private String name;
	private int meta;

	MineralType(String s, int id)
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

	public static MineralType fromMeta(int meta)
	{
		for(int i = 0; i < MineralType.values().length; i++)
		{
			if(MineralType.values()[i].meta == meta)
				return MineralType.values()[i];
		}
		return null;
	}

	public static MineralType fromName(String n)
	{
		for(int i = 0; i < OreType.values().length; i++)
		{
			if(MineralType.values()[i].name.equals(n))
				return MineralType.values()[i];
		}
		return null;
	}

}