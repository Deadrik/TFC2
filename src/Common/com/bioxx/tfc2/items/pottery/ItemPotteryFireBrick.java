package com.bioxx.tfc2.items.pottery;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

import com.bioxx.tfc2.Core;

public class ItemPotteryFireBrick extends ItemPotteryBase
{
	public ItemPotteryFireBrick()
	{
		super();
		this.subTypeNames = new String[] {"clay_fire_brick", "fire_brick"};
		this.maxSubTypeMeta = 1;
		this.displayMaterial = false;
	}

	@Override
	public void addInformation(ItemStack is, EntityPlayer player, List arraylist, boolean flag)
	{
		super.addInformation(is, player, arraylist, flag);
		if(is.getItemDamage() == 0)
			arraylist.add(TextFormatting.DARK_GRAY + Core.translate("global.clay"));
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}
}
