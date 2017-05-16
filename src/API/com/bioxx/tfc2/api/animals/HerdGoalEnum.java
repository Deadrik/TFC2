package com.bioxx.tfc2.api.animals;

import net.minecraft.util.IStringSerializable;

public enum HerdGoalEnum implements IStringSerializable
{
	FOOD, WATER, REST;

	@Override
	public String getName() {
		if(this == FOOD)
			return "food";
		else if(this == WATER)
			return "water";
		else
			return "rest";
	}

	public static HerdGoalEnum getEnum(String s)
	{
		if(s.equals("rest"))
			return REST;
		else if(s.equals("food"))
			return FOOD;
		else if(s.equals("water"))
			return WATER;
		throw new IllegalArgumentException();
	}
}