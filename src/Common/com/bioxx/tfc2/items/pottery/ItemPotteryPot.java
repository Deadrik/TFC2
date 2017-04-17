package com.bioxx.tfc2.items.pottery;

public class ItemPotteryPot extends ItemPotteryBase
{
	public ItemPotteryPot()
	{
		super();
		this.subTypeNames = new String[] {"clay_pot", "ceramic_pot"};
		this.maxSubTypeMeta = 1;
		this.maxStackSize = 1;
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}
}
