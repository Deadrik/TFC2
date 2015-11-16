package com.bioxx.tfc2.api.types;

public enum Season 
{
	Spring, Summer, Fall, Winter;

	public static Season fromInt(int i)
	{
		return Season.values()[i];
	}
}
