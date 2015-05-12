package com.bioxx.tfc2.Items.ItemBlocks;

import net.minecraft.block.Block;

import com.bioxx.tfc2.api.Global;

public class ItemPlanks extends ItemTerraBlock
{
	public ItemPlanks(Block b)
	{
		super(b);
		MetaNames = Global.WOOD_STANDARD;
	}
}
