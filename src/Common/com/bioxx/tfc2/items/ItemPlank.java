package com.bioxx.tfc2.items;

import net.minecraft.creativetab.CreativeTabs;

import com.bioxx.tfc2.api.interfaces.IRegisterSelf;

public class ItemPlank extends ItemTerra implements IRegisterSelf
{
	public ItemPlank()
	{
		this.hasSubtypes = true;
		this.maxSubTypeMeta = 17;
		this.subTypeNames = new String[] {"ash","aspen","birch","chestnut",
				"douglas fir","hickory","maple","oak",
				"pine","sequoia","spruce","sycamore",
				"white cedar","willow","kapok","acacia",
				"rosewood","blackwood","palm"};
		this.setCreativeTab(CreativeTabs.FOOD);
	}

	@Override
	public String[] getSubTypeNames() 
	{
		return subTypeNames;
	}

	@Override
	public String getPath()
	{
		return "Wood/Plank/";
	}
}
