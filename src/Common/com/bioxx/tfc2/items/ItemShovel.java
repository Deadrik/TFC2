package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.core.TFCTabs;
import com.google.common.collect.Sets;

public class ItemShovel extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {Blocks.CLAY, Blocks.DIRT, Blocks.FARMLAND, Blocks.GRASS, Blocks.GRAVEL, 
			Blocks.MYCELIUM, Blocks.SAND, Blocks.SNOW, Blocks.SNOW_LAYER, Blocks.SOUL_SAND, TFCBlocks.Grass, TFCBlocks.Dirt});

	public ItemShovel(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
		this.setCreativeTab(TFCTabs.TFCTools);
	}

}
