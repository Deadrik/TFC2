package com.bioxx.tfc2.core;

import net.minecraft.item.ItemStack;

import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC.RecipeType;

public class Recipes 
{
	public final static int WILDCARD = 32767;

	public static void RegisterNormalRecipes()
	{

	}

	public static void RegisterKnappingRecipes()
	{
		CraftingManagerTFC manager = CraftingManagerTFC.getInstance();
		manager.addRecipe(RecipeType.Knapping, new ItemStack(TFCItems.StoneAxeHead, 1, 0), "         "," XXXX    ","XXXXXX X ","XXXXXXXXX","XXXXXXXXX","XXXXXXXXX","XXXXXX X "," XXXX    ","         ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.Knapping, new ItemStack(TFCItems.StoneShovelHead, 1, 0) ,"  XXXXX  "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX ","   XXX   ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.Knapping, new ItemStack(TFCItems.StoneKnifeHead, 1, 0) ,"XX       ","XXX      ","XXXX     "," XXXX    ","  XXXX   ","   X XX  ","      XX ","       XX","        X", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.Knapping, new ItemStack(TFCItems.StoneHoeHead, 1, 0) ,"         ","XXX      ","  XX     ","   XX    ","    XX   ","    XXX  ","    XXX  ","         ","         ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
	}
}
