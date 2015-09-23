package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.block.Block;

import com.google.common.collect.Sets;

public class ItemHoe extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {});

	public ItemHoe(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
	}

}
