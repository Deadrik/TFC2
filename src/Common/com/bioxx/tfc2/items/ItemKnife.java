package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.block.Block;

import com.bioxx.tfc2.core.TFCTabs;
import com.google.common.collect.Sets;

public class ItemKnife extends ItemTerraTool 
{
	private static final Set EFFECTIVE_ON = Sets.newHashSet(new Block[] {});

	public ItemKnife(ToolMaterial mat)
	{
		super(mat, EFFECTIVE_ON);
		this.setCreativeTab(TFCTabs.TFCWeapons);
	}

}
