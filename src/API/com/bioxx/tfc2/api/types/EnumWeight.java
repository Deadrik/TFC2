package com.bioxx.tfc2.api.types;

public enum EnumWeight 
{
	VERYLIGHT("weight.verylight", 1),
	LIGHT("weight.light", 2),
	MEDIUM("weight.medium", 4),
	HEAVY("weight.heavy", 8),
	VERYHEAVY("weight.veryheavy", 16);

	public final int encumbrance;
	private final String name;
	private static final EnumWeight WEIGHTS[] = new EnumWeight[] {
			LIGHT, MEDIUM, HEAVY};

	private EnumWeight(String s, int i)
	{
		name = s;
		encumbrance = i;
	}

	/**
	 * This should always be used inside of a call to translate
	 */
	public String getUnlocalizedName()
	{
		return name;
	}

	/**
	 * This should be used when making comparisons to JSON information
	 */
	public String getName()
	{
		return name.replace("weight.", "");
	}

	public static EnumWeight fromString(String s)
	{
		for(EnumWeight size : values())
		{
			if(size.getName().equals(s))
				return size;
		}
		return EnumWeight.VERYHEAVY;
	}
}
