package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum WoodType implements IStringSerializable
{
	Ash("ash", 0),Aspen("aspen", 1),Birch("birch", 2),Chestnut("chestnut", 3),
	DouglasFir("douglas_fir", 4),Hickory("hickory", 5),Maple("maple", 6),Oak("oak", 7),
	Pine("pine", 8),Sequoia("sequoia", 9),Spruce("spruce", 10),Sycamore("sycamore", 11),
	WhiteCedar("white_cedar", 12),Willow("willow", 13),Kapok("kapok",14),Acacia("acacia",15),
	Rosewood("rosewood", 16),Blackwood("blackwood", 17),Palm("palm", 18);

	private String name;
	private int meta;

	WoodType(String s, int id)
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

	public static WoodType getTypeFromMeta(int meta)
	{
		for(int i = 0; i < WoodType.values().length; i++)
		{
			if(WoodType.values()[i].meta == meta)
				return WoodType.values()[i];
		}
		return null;
	}

	public static WoodType getTypeFromString(String s)
	{
		for(int i = 0; i < WoodType.values().length; i++)
		{
			if(WoodType.values()[i].getName().equals(s))
				return WoodType.values()[i];
		}
		return null;
	}
}