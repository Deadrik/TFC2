package com.bioxx.tfc2.api.crafting;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

public class KilnManager 
{
	private static final KilnManager INSTANCE = new KilnManager();
	public static final KilnManager getInstance()
	{
		return INSTANCE;
	}

	private ArrayList<KilnEntry> recipeMap = new ArrayList<KilnEntry>();

	public void registerRecipe(ItemStack inStack, ItemStack outStack)
	{
		recipeMap.add(new KilnEntry(inStack, outStack));
	}

	public KilnEntry matches(ItemStack is)
	{
		for(KilnEntry entry : recipeMap)
		{
			if(ItemStack.areItemsEqual(is, entry.inStack))
				return entry;
		}
		return null;
	}

	public class KilnEntry
	{
		public final ItemStack inStack;
		public final ItemStack outStack;

		public KilnEntry(ItemStack i, ItemStack o)
		{
			inStack = i;
			outStack = o;
		}



	}

}
