package com.bioxx.tfc2.api.Types;

import net.minecraft.util.IStringSerializable;

public enum WoodType implements IStringSerializable
{
	Andesite("Andesite", 10), Basalt("Basalt", 9), Blueschist("Blueschist", 12), Chert("Chert", 7), Claystone("Claystone", 4), Dacite("Dacite", 11), 
	Diorite("Diorite", 1), Dolomite("Dolomite", 6), Gabbro("Gabbro", 2), Gneiss("Gneiss", 14), Granite("Granite", 0), Limestone("Limestone", 5), 
	Marble("Marble", 15), Rhyolite("Rhyolite", 8), Schist("Schist", 13), Shale("Shale", 3);

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