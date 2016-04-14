package com.bioxx.tfc2.items;

import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;

public class ItemTerraTool extends ItemTool 
{

	public ItemTerraTool(ToolMaterial mat, Set effective)
	{
		this(mat.getDamageVsEntity(), 1.0f, mat, effective);
	}

	protected ItemTerraTool(float attackDamage, float attackSpeed, ToolMaterial material, Set effectiveBlocks) 
	{
		super(attackDamage, attackSpeed, material, effectiveBlocks);
	}

	@Override
	public Item setUnlocalizedName(String unlocalizedName)
	{
		this.setRegistryName(unlocalizedName);
		return super.setUnlocalizedName(unlocalizedName);
	}

}
