package com.bioxx.tfc2.items.pottery;

import net.minecraft.item.ItemStack;

public class ItemPotteryVessel extends ItemPotteryBase
{
	public ItemPotteryVessel()
	{
		super();
		this.subTypeNames = new String[] {"clay_vessel", "ceramic_vessel"};
		this.maxSubTypeMeta = 1;
		this.maxStackSize = 1;
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}

	@Override
	public boolean isClay(ItemStack stack)
	{
		return stack.getItemDamage() == 0;
	}
}
