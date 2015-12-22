package com.bioxx.tfc2;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;

import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.core.RegistryItemQueue;
import com.bioxx.tfc2.items.ItemAxe;
import com.bioxx.tfc2.items.ItemFoodTFC;
import com.bioxx.tfc2.items.ItemHoe;
import com.bioxx.tfc2.items.ItemKnife;
import com.bioxx.tfc2.items.ItemLooseRock;
import com.bioxx.tfc2.items.ItemShovel;
import com.bioxx.tfc2.items.ItemToolHead;

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

	public static Item FoodCabbage;

	public static void Load()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Loading Items").toString());
		LooseRock = registerItemOnly(new ItemLooseRock().setUnlocalizedName("looserock"));
		StoneAxe = registerItem(new ItemAxe(ToolMaterial.STONE).setUnlocalizedName("stone_axe"));
		StoneShovel = registerItem(new ItemShovel(ToolMaterial.STONE).setUnlocalizedName("stone_shovel"));
		StoneKnife = registerItem(new ItemKnife(ToolMaterial.STONE).setUnlocalizedName("stone_knife"));
		StoneHoe = registerItem(new ItemHoe(ToolMaterial.STONE).setUnlocalizedName("stone_hoe"));

		StoneAxeHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_axe_head"));
		StoneShovelHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_shovel_head"));
		StoneKnifeHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_knife_head"));
		StoneHoeHead = registerItem(new ItemToolHead().setUnlocalizedName("stone_hoe_head"));

		FoodCabbage = registerItem(new ItemFoodTFC(EnumFoodGroup.Vegetable, 1f, 1).setExpiration(3600).setUnlocalizedName("food_cabbage"));


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
		return i;
	}

	private static void SetupHarvestLevels()
	{
		StoneAxe.setHarvestLevel("axe", 1);
	}
}
