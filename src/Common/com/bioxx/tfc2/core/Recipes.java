package com.bioxx.tfc2.core;

import java.util.List;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;

import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC.RecipeType;
import com.bioxx.tfc2.api.crafting.KilnManager;
import com.bioxx.tfc2.items.ToolHeadType;
import com.bioxx.tfc2.items.pottery.ClayMoldType;

public class Recipes 
{
	public final static int WILDCARD = 32767;

	public static void RegisterNormalRecipes()
	{
		CraftingManagerTFC manager = CraftingManagerTFC.getInstance();
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneAxe), new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_AXE.ordinal()), "stickWood");
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneKnife), new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_KNIFE.ordinal()), "stickWood");
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneShovel), new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_SHOVEL.ordinal()), "stickWood");
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.StoneHoe), new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_HOE.ordinal()), "stickWood");
		manager.addRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.Firestarter), " X","X ", 'X', "stickWood");
		manager.addRecipe(RecipeType.NORMAL, new ItemStack(TFCBlocks.Thatch), new Object[]{"XX", "XX", Character.valueOf('X'), new ItemStack(TFCItems.Straw, 1)});
		manager.addShapelessRecipe(RecipeType.NORMAL, new ItemStack(TFCItems.Straw, 4), new Object[]{new ItemStack(TFCBlocks.Thatch, 1)});

		manager.addShapelessRecipe(RecipeType.NORMAL_REPAIR, new ItemStack(TFCItems.StoneAxe), new ItemStack(TFCItems.StoneAxe, 1, WILDCARD), new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addShapelessRecipe(RecipeType.NORMAL_REPAIR, new ItemStack(TFCItems.StoneKnife), new ItemStack(TFCItems.StoneKnife, 1, WILDCARD), new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addShapelessRecipe(RecipeType.NORMAL_REPAIR, new ItemStack(TFCItems.StoneShovel), new ItemStack(TFCItems.StoneShovel, 1, WILDCARD), new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addShapelessRecipe(RecipeType.NORMAL_REPAIR, new ItemStack(TFCItems.StoneHoe), new ItemStack(TFCItems.StoneHoe, 1, WILDCARD), new ItemStack(TFCItems.LooseRock, 1, WILDCARD));

		List<IRecipe> list = CraftingManager.getInstance().getRecipeList();
		for(int i = 0; i < list.size(); i++)
		{
			IRecipe rec = list.get(i);
			if(rec.getRecipeOutput().getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
			{
				CraftingManager.getInstance().getRecipeList().remove(i);
			}

		}
	}

	public static void RegisterKnappingRecipes()
	{
		CraftingManagerTFC manager = CraftingManagerTFC.getInstance();
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_AXE.ordinal()), "         "," XXXX    ","XXXXXX X ","XXXXXXXXX","XXXXXXXXX","XXXXXXXXX","XXXXXX X "," XXXX    ","         ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_SHOVEL.ordinal()) ,"  XXXXX  "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX "," XXXXXXX ","   XXX   ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_KNIFE.ordinal()) ,"XX       ","XXX      ","XXXX     "," XXXX    ","  XXXX   ","   X XX  ","      XX ","       XX","        X", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));
		manager.addRecipe(RecipeType.KNAPPING, new ItemStack(TFCItems.ToolHead, 1, ToolHeadType.STONE_HOE.ordinal()) ,"         ","XXX      ","  XX     ","   XX    ","    XX   ","    XXX  ","    XXX  ","         ","         ", 'X', new ItemStack(TFCItems.LooseRock, 1, WILDCARD));

		manager.addRecipe(RecipeType.POTTERY, new ItemStack(TFCItems.PotteryJug) ,"XX  XXXXX","XX     XX","X    X XX","       XX","      XXX","      XXX","      XXX","      XXX","X    XXXX", 'X', new ItemStack(Items.CLAY_BALL, 1, WILDCARD));
		manager.addRecipe(RecipeType.POTTERY, new ItemStack(TFCItems.PotteryVessel) ,"XXX   XXX","XX     XX","X       X","         ","         ","         ","         ","         ","X       X", 'X', new ItemStack(Items.CLAY_BALL, 1, WILDCARD));

		manager.addShapelessRecipe(RecipeType.ANVIL, new ItemStack(Items.GOLD_INGOT, 1 , 0), new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Items.IRON_INGOT, 1));
	}

	public static void RegisterKilnRecipes()
	{
		KilnManager manager = KilnManager.getInstance();

		manager.registerRecipe(new ItemStack(TFCItems.PotteryJug, 1, 0), new ItemStack(TFCItems.PotteryJug, 1, 1));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryBowl, 1, 0), new ItemStack(TFCItems.PotteryBowl, 1, 1));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryVessel, 1, 0), new ItemStack(TFCItems.PotteryVessel, 1, 1));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryPot, 1, 0), new ItemStack(TFCItems.PotteryPot, 1, 1));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_AXE_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_AXE.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SHOVEL_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SHOVEL.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_HAMMER_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_HAMMER.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SWORD_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SWORD.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_JAVELIN_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_JAVELIN.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_PICK_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_PICK.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_MACE_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_MACE.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SCYTHE_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SCYTHE.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SAW_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_SAW.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_KNIFE_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_KNIFE.getMeta()));
		manager.registerRecipe(new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_HOE_CLAY.getMeta()), new ItemStack(TFCItems.PotteryMold, 1, ClayMoldType.MOLD_HOE.getMeta()));
	}
}
