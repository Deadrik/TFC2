package com.bioxx.tfc2.api.types;

import java.util.ArrayList;

import net.minecraft.util.IStringSerializable;

public enum StoneType implements IStringSerializable
{
	Andesite("Andesite", 10, SubType.IgneousExtrusive), Basalt("Basalt", 9, SubType.IgneousExtrusive), Blueschist("Blueschist", 12, SubType.Metamorphic), 
	Chert("Chert", 7, SubType.Sedimentary), Claystone("Claystone", 4, SubType.Sedimentary), Dacite("Dacite", 11, SubType.IgneousExtrusive), 
	Diorite("Diorite", 1, SubType.IgneousIntrusive), Dolomite("Dolomite", 6, SubType.Sedimentary), Gabbro("Gabbro", 2, SubType.IgneousIntrusive), 
	Gneiss("Gneiss", 14, SubType.Metamorphic), Granite("Granite", 0, SubType.IgneousIntrusive), Limestone("Limestone", 5, SubType.Sedimentary), 
	Marble("Marble", 15, SubType.Metamorphic), Rhyolite("Rhyolite", 8, SubType.IgneousExtrusive), Schist("Schist", 13, SubType.Metamorphic), 
	Shale("Shale", 3, SubType.Sedimentary);

	private String name;
	private int meta;
	private SubType type;

	StoneType(String s, int id, SubType su)
	{
		name = s;
		meta = id;
		type = su;
	}

	@Override
	public String getName() {
		return name;
	}

	public int getMeta()
	{
		return meta;
	}

	public static StoneType getStoneTypeFromMeta(int meta)
	{
		for(int i = 0; i < StoneType.values().length; i++)
		{
			if(StoneType.values()[i].meta == meta)
				return StoneType.values()[i];
		}
		return null;
	}

	public static StoneType[] getForSubTypes(StoneType.SubType... types)
	{
		ArrayList<StoneType> list = new ArrayList<StoneType>();
		for(StoneType.SubType s : types)
		{
			for(int i = 0; i < StoneType.values().length; i++)
			{
				if(StoneType.values()[i].type == s)
					list.add(StoneType.values()[i]);
			}
		}

		if(list.size() == 0)
			return null;

		return list.toArray(new StoneType[0]);
	}

	public enum SubType
	{
		IgneousIntrusive, IgneousExtrusive, Sedimentary, Metamorphic;
	}
}