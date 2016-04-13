package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum EffectType implements IStringSerializable
{
	Fire("fire", 0),
	Water("water", 1),
	Electric("electric", 2),
	Steam("steam", 3),
	Smoke("smoke", 4),
	Acid("acid", 5),
	Char("char", 6);

	private String name;
	private int meta;

	EffectType(String s, int id)
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

	public static EffectType getTypeFromMeta(int meta)
	{
		for(int i = 0; i < EffectType.values().length; i++)
		{
			if(EffectType.values()[i].meta == meta)
				return EffectType.values()[i];
		}
		return null;
	}
}