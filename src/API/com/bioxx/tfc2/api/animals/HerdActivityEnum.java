package com.bioxx.tfc2.api.animals;

import net.minecraft.util.IStringSerializable;

public enum HerdActivityEnum implements IStringSerializable
{
	TRAVELING("traveling"), WAITING("waiting"), FLEEING("fleeing"), WORKING("working");
	String name;

	HerdActivityEnum(String s)
	{
		name = s;
	}

	@Override
	public String getName() {
		return name;
	}

	public static HerdActivityEnum getEnum(String s)
	{
		for(HerdActivityEnum e : values())
		{
			if(e.name.equals(s))
				return e;
		}
		throw new IllegalArgumentException();
	}
}