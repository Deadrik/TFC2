package com.bioxx.tfc2.items.pottery;

public class ItemPotteryMold extends ItemPotteryBase
{
	public ItemPotteryMold()
	{
		super();
		this.subTypeNames = new String[] {"clay_mold", "ceramic_mold"};
		this.maxSubTypeMeta = 1;
	}

	@Override
	public String[] getSubTypeNames()
	{
		return this.subTypeNames;
	}
}
