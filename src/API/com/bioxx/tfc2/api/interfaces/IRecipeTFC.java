package com.bioxx.tfc2.api.interfaces;

import java.util.List;

import net.minecraft.item.crafting.IRecipe;

public interface IRecipeTFC extends IRecipe 
{
	public abstract List<Object> getRecipeItems();
	public abstract int getRecipeWidth();
	public abstract int getRecipeHeight();
}
