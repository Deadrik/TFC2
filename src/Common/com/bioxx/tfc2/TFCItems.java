package com.bioxx.tfc2;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.core.RegistryItemQueue;
import com.bioxx.tfc2.items.*;

public class TFCItems 
{
	public static Item StoneAxe;
	public static Item StoneShovel;
	public static Item StoneKnife;
	public static Item StoneHoe;
	public static Item LooseRock;
	public static Item StoneAxeHead;
	public static Item StoneShovelHead;
	public static Item StoneKnifeHead;
	public static Item StoneHoeHead;

	//Vegetables
	public static Item FoodCabbage;
	public static Item FoodTomato;
	public static Item FoodOnion;
	public static Item FoodPotato;
	public static Item FoodGarlic;
	public static Item FoodCarrot;
	public static Item FoodSquash;
	public static Item FoodGreenPepper;
	public static Item FoodYellowPepper;
	public static Item FoodRedPepper;
	public static Item FoodSoybean;
	public static Item FoodGreenbean;

	//Fruits
	public static Item FoodBanana;
	public static Item FoodRedApple;
	public static Item FoodGreenApple;
	public static Item FoodOrange;
	public static Item FoodLemon;
	public static Item FoodOlive;
	public static Item FoodCherry;
	public static Item FoodPeach;
	public static Item FoodPlum;
	//Grains
	public static Item FoodBarleyWhole;
	public static Item FoodBarleyGrain;
	public static Item FoodBarleyGround;
	public static Item FoodBarleyDough;
	public static Item FoodBarleyBread;

	public static Item FoodWheatWhole;
	public static Item FoodWheatGrain;
	public static Item FoodWheatGround;
	public static Item FoodWheatDough;
	public static Item FoodWheatBread;

	public static Item FoodOatWhole;
	public static Item FoodOatGrain;
	public static Item FoodOatGround;
	public static Item FoodOatDough;
	public static Item FoodOatBread;

	public static Item FoodRyeWhole;
	public static Item FoodRyeGrain;
	public static Item FoodRyeGround;
	public static Item FoodRyeDough;
	public static Item FoodRyeBread;

	public static Item FoodCornWhole;
	public static Item FoodCornGrain;
	public static Item FoodCornGround;
	public static Item FoodCornDough;
	public static Item FoodCornBread;

	public static Item FoodRiceWhole;
	public static Item FoodRiceGrain;
	public static Item FoodRiceGround;
	public static Item FoodRiceDough;
	public static Item FoodRiceBread;

	//Protiens
	public static Item FoodBeefRaw;
	public static Item FoodBeefCooked;
	public static Item FoodMuttonRaw;
	public static Item FoodMuttonCooked;
	public static Item FoodChickenRaw;
	public static Item FoodChickenCooked;
	public static Item FoodVenisonRaw;
	public static Item FoodVenisonCooked;
	public static Item FoodPorkRaw;
	public static Item FoodPorkCooked;
	public static Item FoodEgg;
	public static Item FoodEggCooked;
	//Dairy
	public static Item FoodCheese;

	//Seeds
	public static Item Seeds;

	public static void Load()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Loading Items").toString());
		LooseRock = registerItemOnly(new ItemLooseRock().setUnlocalizedName("looserock"));
		Seeds = registerItemOnly(new ItemSeeds().setUnlocalizedName("seeds"));

		StoneAxe = registerItem(new ItemAxe(ToolMaterial.STONE).setUnlocalizedName("stone_axe"));
		StoneShovel = registerItem(new ItemShovel(ToolMaterial.STONE).setUnlocalizedName("stone_shovel"));
		StoneKnife = registerItem(new ItemKnife(ToolMaterial.STONE).setUnlocalizedName("stone_knife"));
		StoneHoe = registerItem(new ItemHoe(ToolMaterial.STONE).setUnlocalizedName("stone_hoe"));

		StoneAxeHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_axe_head"));
		StoneShovelHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_shovel_head"));
		StoneKnifeHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_knife_head"));
		StoneHoeHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_hoe_head"));

		//Vegetables
		FoodCabbage = registerItem(new ItemFoodTFC(EnumFoodGroup.Vegetable, 1f, 1).setExpiration(3600).setUnlocalizedName("food_cabbage"));
		FoodTomato = registerItem(new ItemFoodTFC(EnumFoodGroup.Vegetable, 1f, 1).setExpiration(3600).setUnlocalizedName("food_tomato"));

		//Fruits
		FoodBanana = registerItem(new ItemFoodTFC(EnumFoodGroup.Fruit, 1f, 1).setExpiration(3600).setUnlocalizedName("food_banana"));

		//Grains
		FoodWheatWhole = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 1f, 1).setIsEdible(false).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_wheatwhole"));
		FoodWheatGrain = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 0.1f, 1).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_wheatgrain"));
		FoodWheatGround = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 0.1f, 1).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_wheatground"));
		FoodWheatDough = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 0.1f, 1).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_wheatdough"));
		FoodWheatBread = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 1f, 1).setExpiration(3600).setUnlocalizedName("food_wheatbread"));
		FoodBarleyWhole = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 1f, 1).setIsEdible(false).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_barleywhole"));
		FoodBarleyGrain = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 0.1f, 1).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_barleygrain"));
		FoodBarleyGround = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 0.1f, 1).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_barleyground"));
		FoodBarleyDough = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 0.1f, 1).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_barleydough"));
		FoodBarleyBread = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 1f, 1).setExpiration(3600).setUnlocalizedName("food_barleybread"));
		FoodRyeWhole = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 1f, 1).setIsEdible(false).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_ryewhole"));
		FoodOatWhole = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 1f, 1).setIsEdible(false).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_oatwhole"));
		FoodRiceWhole = registerItem(new ItemFoodTFC(EnumFoodGroup.Grain, 1f, 1).setIsEdible(false).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_ricewhole"));
		FoodCornWhole = registerItem(new ItemFoodTFC(EnumFoodGroup.Vegetable, 0f, 0).setIsEdible(false).setCanUseRaw(false).setExpiration(3600).setUnlocalizedName("food_cornwhole"));
		//Protein
		FoodBeefRaw = registerItem(new ItemFoodTFC(EnumFoodGroup.Protein, 1f, 1).setExpiration(360).setUnlocalizedName("food_beefraw"));
		FoodBeefCooked = registerItem(new ItemFoodTFC(EnumFoodGroup.Protein, 1f, 1).setExpiration(360).setUnlocalizedName("food_beefcooked"));

		//Dairy		
		FoodCheese = registerItem(new ItemFoodTFC(EnumFoodGroup.Dairy, 1f, 1).setExpiration(7200).setUnlocalizedName("food_cheese"));



	}

	public static void Register()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Registering Items").toString());
		RegistryItemQueue.getInstance().registerItems();

		SetupHarvestLevels();
	}

	/**
	 * Registers the item with the game registry and also registers a single ItemMeshDefinition for this item.
	 */
	private static Item registerItem(Item i)
	{
		RegistryItemQueue.getInstance().addFull(i);
		return i;
	}

	/**
	 * Registers the item with the game registry.<br>
	 * <br>
	 * Should be used for items that have multiple variants where we need to manually create a MeshDef
	 */
	private static Item registerItemOnly(Item i)
	{
		RegistryItemQueue.getInstance().addItemOnly(i);

		/*if(i instanceof IRegisterSelf)
		{
			for(int c = 0; c < ((IRegisterSelf)i).getSubTypeNames().length; c++)
			{
				String path = ((IRegisterSelf)i).getPath();
				String subName = ((IRegisterSelf)i).getSubTypeNames()[c];
				ModelLoader.setCustomModelResourceLocation(i, c, new ModelResourceLocation(Reference.ModID + ":"+path+subName, "inventory"));
			}
		}*/

		return i;
	}

	private static void SetupHarvestLevels()
	{
		StoneAxe.setHarvestLevel("axe", 1);
	}
}
