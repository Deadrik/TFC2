package com.bioxx.tfc2.core;

import net.minecraft.item.ItemStack;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.types.StoneType;

public class Recipes 
{
	public static void RegisterKnappingRecipes()
	{
		CraftingManagerTFC manager = CraftingManagerTFC.getInstance();
		manager.addRecipe(new ItemStack(TFCBlocks.Rubble, 1, StoneType.Dacite.getMeta()), "XXX", "XXX", "XXX", 'X', new ItemStack(TFCItems.LooseRock, 1, StoneType.Dacite.getMeta()));
	}
}
