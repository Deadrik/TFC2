package com.bioxx.tfc2;

import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
		LooseRock = new ItemLooseRock().setUnlocalizedName("looserock");
		StoneAxe = new ItemAxe(ToolMaterial.STONE).setUnlocalizedName("stone_axe");
		StoneShovel = new ItemShovel(ToolMaterial.STONE).setUnlocalizedName("stones_hovel");
		StoneKnife = new ItemKnife(ToolMaterial.STONE).setUnlocalizedName("stone_knife");
		StoneHoe = new ItemHoe(ToolMaterial.STONE).setUnlocalizedName("stone_hoe");

		StoneAxeHead = new ItemToolHead().setUnlocalizedName("stone_axe_head");
		StoneShovelHead = new ItemToolHead().setUnlocalizedName("stone_shovel_head");
		StoneKnifeHead = new ItemToolHead().setUnlocalizedName("stone_knife_head");
		StoneHoeHead = new ItemToolHead().setUnlocalizedName("stone_hoe_head");

		FoodCabbage = new ItemFoodTFC().setExpiration(3600).setUnlocalizedName("food_cabbage");


	}

	public static void Register()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Registering Items").toString());
		registerItem(LooseRock, "looserock");
		registerItem(StoneAxe, "stone_axe");
		registerItem(StoneShovel, "stone_shovel");
		registerItem(StoneKnife, "stone_knife");
		registerItem(StoneHoe, "stone_hoe");
		registerItem(StoneAxeHead, "stone_axe_head");
		registerItem(StoneShovelHead, "stone_shovel_head");
		registerItem(StoneKnifeHead, "stone_knife_head");
		registerItem(StoneHoeHead, "stone_hoe_head");
		registerItem(FoodCabbage, "food_cabbage");




		SetupHarvestLevels();
	}

	private static void registerItem(Item i, String name)
	{
		GameRegistry.registerItem(i, name);
		if(TFC.proxy.isClientSide())
			RegistryItemQueue.getInstance().addItemToQueue(i, name);
	}

	private static void SetupHarvestLevels()
	{
		StoneAxe.setHarvestLevel("axe", 1);
	}
}
