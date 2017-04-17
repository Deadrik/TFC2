package com.bioxx.tfc2.items.pottery;

public class ItemPotteryBowl extends ItemPotteryBase
{
	public ItemPotteryBowl()
	{
		super();
		this.subTypeNames = new String[] {"clay_bowl", "ceramic_bowl"};
		this.maxSubTypeMeta = 1;
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}
}
