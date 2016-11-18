package com.bioxx.tfc2.api.types;

import net.minecraft.util.text.TextFormatting;

public enum EnumFoodGroup
{
	Fruit,
	Vegetable,
	Grain,
	Protein,
	Dairy,
	None;

	public String getColoredAbbrv()
	{
		if(this == Fruit)
		{
			return TextFormatting.GREEN + "F";
		}
		else if(this == Vegetable)
		{
			return TextFormatting.DARK_GREEN + "V";
		}
		else if(this == Grain)
		{
			return TextFormatting.YELLOW + "G";
		}
		else if(this == Protein)
		{
			return TextFormatting.DARK_RED + "P";
		}
		else if(this == Dairy)
		{
			return TextFormatting.WHITE + "D";
		}
		else
		{
			return TextFormatting.DARK_GRAY + "None";
		}
	}

	public String getColoredName()
	{
		if(this == Fruit)
		{
			return TextFormatting.GREEN + "Fruit";
		}
		else if(this == Vegetable)
		{
			return TextFormatting.DARK_GREEN + "Vegetable";
		}
		else if(this == Grain)
		{
			return TextFormatting.YELLOW + "Grain";
		}
		else if(this == Protein)
		{
			return TextFormatting.DARK_RED + "Protein";
		}
		else if(this == Dairy)
		{
			return TextFormatting.WHITE + "Dairy";
		}
		else
		{
			return TextFormatting.DARK_GRAY + "None";
		}
	}
}
