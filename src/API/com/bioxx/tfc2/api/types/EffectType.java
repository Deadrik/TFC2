package com.bioxx.tfc2.api.types;

import net.minecraft.util.IStringSerializable;

public enum EffectType implements IStringSerializable
{
	Fire("Fire", 0),
	Water("Water", 1),
	Electric("Electric", 2),
	Steam("Steam", 3),
	Smoke("Smoke", 4),
	Acid("Acid", 5),
	Char("Char", 6);

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