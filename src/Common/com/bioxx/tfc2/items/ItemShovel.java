package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.bioxx.tfc2.TFCBlocks;
import com.google.common.collect.Sets;

public class ItemShovel extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.clay, Blocks.dirt, Blocks.farmland, Blocks.grass, Blocks.gravel, 
			Blocks.mycelium, Blocks.sand, Blocks.snow, Blocks.snow_layer, Blocks.soul_sand, TFCBlocks.Grass, TFCBlocks.Dirt});

	public ItemShovel(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
	}

}
