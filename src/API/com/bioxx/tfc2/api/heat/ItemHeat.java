package com.bioxx.tfc2.api.heat;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemHeat 
{
	public static final String HEAT_TAG = "tfc2:heat";
	public static float Get(ItemStack is)
	{
		if(is.hasTagCompound())
		{
			return is.getTagCompound().getFloat(HEAT_TAG);
		}
		return 0;
	}

	public static void Set(ItemStack is, float heat)
	{
		if(!is.hasTagCompound())
		{
			is.setTagCompound(new NBTTagCompound());
		}

		is.getTagCompound().setFloat(HEAT_TAG, heat);
	}

	public static void Increase(ItemStack is, float heatToAdd)
	{
		if(!is.hasTagCompound())
		{
			is.setTagCompound(new NBTTagCompound());
		}

		float heat = is.getTagCompound().getFloat(HEAT_TAG) + heatToAdd;

		if(heat < 0)//Just in case we add a negative for some dumb reason
			is.getTagCompound().removeTag(HEAT_TAG);
		else
			is.getTagCompound().setFloat(HEAT_TAG, heat);
	}

	public static void Decrease(ItemStack is, float heatToSub)
	{
		if(!is.hasTagCompound())
		{
			is.setTagCompound(new NBTTagCompound());
		}

		float heat = is.getTagCompound().getFloat(HEAT_TAG) - heatToSub;

		if(heat <= 0)
			is.getTagCompound().removeTag(HEAT_TAG);
		else
			is.getTagCompound().setFloat(HEAT_TAG, heat);

		if(is.getTagCompound().getSize() == 0)
			is.setTagCompound(null);
	}
}
