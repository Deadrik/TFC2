package com.bioxx.tfc2.items.pottery;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import com.bioxx.tfc2.Core;

public class ItemPotteryMold extends ItemPotteryBase
{
	public ItemPotteryMold()
	{
		super();
		this.subTypeNames = ClayMoldType.getNamesArray();
		this.maxSubTypeMeta = this.subTypeNames.length - 1;
		displayMaterial = false;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
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

	@Override
	public boolean isClay(ItemStack stack)
	{
		return subTypeNames[stack.getItemDamage()].contains("clay");
	}
}
