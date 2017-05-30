package com.bioxx.tfc2.api.types;

public enum EnumSize 
{
	TINY("size.tiny"),
	VERYSMALL("size.verysmall"),
	SMALL("size.small"),
	MEDIUM("size.medium"),
	LARGE("size.large"),
	VERYLARGE("size.verylarge"),
	HUGE("size.huge");

	private final String name;

	EnumSize(String n)
	{
		name = n;
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
		return name.replace("size.", "");
	}

	public static EnumSize fromString(String s)
	{
		for(EnumSize size : values())
		{
			if(size.getName().equals(s))
				return size;
		}
		return EnumSize.HUGE;
	}

}
