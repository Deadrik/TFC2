package com.bioxx.tfc2.api.Types;

import net.minecraft.util.IStringSerializable;

public enum WoodType implements IStringSerializable
{
	Ash("Ash", 0),Aspen("Aspen", 1),Birch("Birch", 2),Chestnut("Chestnut", 3),
	DouglasFir("DouglasFir", 4),Hickory("Hickory", 5),Maple("Maple", 6),Oak("Oak", 7),
	Pine("Pine", 8),Sequoia("Sequoia", 9),Spruce("Spruce", 10),Sycamore("Sycamore", 11),
	WhiteCedar("WhiteCedar", 12),WhiteElm("WhiteElm", 13),Willow("Willow", 14);

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

	public static WoodType getStoneTypeFromMeta(int meta)
	{
		for(int i = 0; i < WoodType.values().length; i++)
		{
			if(WoodType.values()[i].meta == meta)
				return WoodType.values()[i];
		}
		return null;
	}
}