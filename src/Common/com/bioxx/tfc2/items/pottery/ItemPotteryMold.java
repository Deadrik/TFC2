package com.bioxx.tfc2.items.pottery;

import java.util.List;

import com.bioxx.tfc2.Core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemPotteryMold extends ItemPotteryBase
{
	public ItemPotteryMold()
	{
		super();
		this.subTypeNames = ClayMoldType.getNamesArray();
		this.maxSubTypeMeta = this.subTypeNames.length - 1;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		if(is.getItemDamage() < 2)
		{
			String[] name = new String[] {"global.clay", "global.ceramic"};
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate(name[is.getItemDamage()]));
		}
		else
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate(this.subTypeNames[is.getItemDamage()]));
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}
}
