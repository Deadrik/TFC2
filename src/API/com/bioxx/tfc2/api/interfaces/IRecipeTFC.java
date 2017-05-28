package com.bioxx.tfc2.api.interfaces;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface IRecipeTFC extends IRecipe 
{
	public abstract List<Object> getRecipeItems();
	public abstract int getRecipeWidth();
	public abstract int getRecipeHeight();
	public abstract boolean isRepairRecipe();
	public abstract boolean matches(NonNullList<ItemStack> var1, World world);
}
