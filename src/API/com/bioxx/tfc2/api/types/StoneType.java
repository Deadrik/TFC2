package com.bioxx.tfc2.api.types;

import java.util.ArrayList;

import net.minecraft.util.IStringSerializable;

public enum StoneType implements IStringSerializable
{
	Andesite("andesite", 10, SubType.IgneousExtrusive), Basalt("basalt", 9, SubType.IgneousExtrusive), Blueschist("blueschist", 12, SubType.Metamorphic), 
	Chert("chert", 7, SubType.Sedimentary), Claystone("claystone", 4, SubType.Sedimentary), Dacite("dacite", 11, SubType.IgneousExtrusive), 
	Diorite("diorite", 1, SubType.IgneousIntrusive), Dolomite("dolomite", 6, SubType.Sedimentary), Gabbro("gabbro", 2, SubType.IgneousIntrusive), 
	Gneiss("gneiss", 14, SubType.Metamorphic), Granite("granite", 0, SubType.IgneousIntrusive), Limestone("limestone", 5, SubType.Sedimentary), 
	Marble("marble", 15, SubType.Metamorphic), Rhyolite("rhyolite", 8, SubType.IgneousExtrusive), Schist("schist", 13, SubType.Metamorphic), 
	Shale("shale", 3, SubType.Sedimentary);

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

	public SubType getSubType()
	{
		return type;
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

		return list.toArray(new StoneType[list.size()]);
	}

	public enum SubType
	{
		IgneousIntrusive, IgneousExtrusive, Sedimentary, Metamorphic;
	}
}