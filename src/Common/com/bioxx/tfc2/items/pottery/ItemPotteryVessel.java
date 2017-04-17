package com.bioxx.tfc2.items.pottery;

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
}
