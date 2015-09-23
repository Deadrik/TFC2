package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.google.common.collect.Sets;

public class ItemAxe extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin, Blocks.melon_block, Blocks.ladder});

	public ItemAxe(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
	}

}
