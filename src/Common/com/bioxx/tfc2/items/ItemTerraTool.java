package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.item.ItemTool;

public class ItemTerraTool extends ItemTool 
{

	public ItemTerraTool(ToolMaterial mat, Set effective)
	{
		this(mat.getDamageVsEntity(), mat, effective);
	}

	protected ItemTerraTool(float attackDamage, ToolMaterial material, Set effectiveBlocks) 
	{
		super(attackDamage, material, effectiveBlocks);
	}

}
