package com.bioxx.tfc2.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.interfaces.IRegisterSelf;
import com.bioxx.tfc2.core.TFCTabs;

public class ItemPlank extends ItemTerra implements IRegisterSelf
{
	public ItemPlank()
	{
		this.hasSubtypes = true;
		this.maxSubTypeMeta = 18;
		this.subTypeNames = Core.capitalizeStringArray(new String[] {"ash","aspen","birch","chestnut",
				"douglas fir","hickory","maple","oak",
				"pine","sequoia","spruce","sycamore",
				"white cedar","willow","kapok","acacia",
				"rosewood","blackwood","palm"});
		this.setCreativeTab(TFCTabs.TFCMaterials);
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);

		if (is.getItemDamage() < Global.WOOD_STANDARD.length)
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global." + Global.WOOD_STANDARD[is.getItemDamage()]));
		else
			arraylist.add(TextFormatting.DARK_RED + Core.translate("global.unknown"));
	}

	@Override
	public String[] getSubTypeNames() 
	{
		return subTypeNames;
	}

	@Override
	public String getPath()
	{
		return "wood/plank/";
	}
}
