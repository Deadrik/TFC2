package com.bioxx.tfc2.Items.ItemBlocks;

import net.minecraft.block.Block;

import com.bioxx.tfc2.api.Global;

public class ItemStone extends ItemTerraBlock
{
	public ItemStone(Block b)
	{
		super(b);
		MetaNames = Global.STONE_ALL;
	}
}
