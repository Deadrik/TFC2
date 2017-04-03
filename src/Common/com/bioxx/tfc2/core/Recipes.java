package com.bioxx.tfc2.core;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC.RecipeType;

public class Recipes 
{
	public final static int WILDCARD = 32767;

	public static void RegisterNormalRecipes()
	{
		CraftingManagerTFC manager = CraftingManagerTFC.getInstance();
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneAxe), new ItemStack(TFCItems.StoneAxeHead), "stickWood");
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneKnife), new ItemStack(TFCItems.StoneKnifeHead), "stickWood");
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneShovel), new ItemStack(TFCItems.StoneShovelHead), "stickWood");
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneHoe), new ItemStack(TFCItems.StoneHoeHead), "stickWood");
		manager.addRecipe(RecipeType.NORMAL, new ItemStack(TFCBlocks.Firepit), " Y ","YYY","XXX",'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD), 'Y', "stickWood");
		manager.addRecipe(RecipeType.NORMAL, new ItemStack(TFCBlocks.Firepit), " X ","X  ", 'X', "stickWood");
	}

	public static void RegisterKnappingRecipes()
	{
		CraftingManagerTFC manager = CraftingManagerTFC.getInstance();
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.StoneAxeHead, 1, 0), "         "," XXXX    ","XXXXXX X ","XXXXXXXXX","XXXXXXXXX","XXXXXXXXX","XXXXXX X "," XXXX    ","         ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.StoneShovelHead, 1, 0) ,"  XXXXX  "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX ","   XXX   ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.StoneKnifeHead, 1, 0) ,"XX       ","XXX      ","XXXX     "," XXXX    ","  XXXX   ","   X XX  ","      XX ","       XX","        X", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.StoneHoeHead, 1, 0) ,"         ","XXX      ","  XX     ","   XX    ","    XX   ","    XXX  ","    XXX  ","         ","         ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));

		manager.addShapelessRecipe(RecipeType.ANVIL, new ItemStack(Items.GOLD_INGOT, 1 , 0), new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Items.IRON_INGOT, 1));
	}
}
