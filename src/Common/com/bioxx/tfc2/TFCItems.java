package com.bioxx.tfc2;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;

import com.bioxx.tfc2.core.RegistryItemQueue;
import com.bioxx.tfc2.core.TFCTabs;
import com.bioxx.tfc2.items.*;
import com.bioxx.tfc2.items.pottery.*;

public class TFCItems
{
	public static Item StoneAxe;
	public static Item StoneShovel;
	public static Item StoneKnife;
	public static Item StoneHoe;
	public static Item StoneHammer;

	public static Item ToolHead;

	public static Item Plank;
	public static Item LooseRock;
	public static Item Firestarter;
	public static Item Straw;

	public static Item PotteryJug;
	public static Item PotteryPot;
	public static Item PotteryVessel;
	public static Item PotteryMold;
	public static Item PotteryBowl;
	public static Item PotteryFireBrick;

	public static void Load()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Loading Items").toString());
		LooseRock = registerItemOnly(new ItemLooseRock().setUnlocalizedName("looserock"));
		Plank = registerItemOnly(new ItemPlank().setUnlocalizedName("plank"));

		StoneAxe = registerItem(new ItemAxe(ToolMaterial.STONE, 30).setUnlocalizedName("stone_axe"));
		StoneShovel = registerItem(new ItemShovel(ToolMaterial.STONE).setUnlocalizedName("stone_shovel"));
		StoneKnife = registerItem(new ItemKnife(ToolMaterial.STONE).setUnlocalizedName("stone_knife"));
		StoneHoe = registerItem(new ItemHoe(ToolMaterial.STONE).setUnlocalizedName("stone_hoe"));
		StoneHammer = registerItem(new ItemHoe(ToolMaterial.STONE).setUnlocalizedName("stone_hammer"));

		ToolHead = registerItemOnly(new ItemToolHead().setUnlocalizedName("tool_head"));

		Firestarter = registerItem(new ItemFirestarter().setUnlocalizedName("firestarter"));
		Straw = registerItem(new ItemTerra().setUnlocalizedName("straw").setCreativeTab(TFCTabs.TFCMaterials));

		PotteryJug = registerItemOnly(new ItemPotteryJug().setUnlocalizedName("jug"));
		PotteryPot = registerItemOnly(new ItemPotteryPot().setUnlocalizedName("pot"));
		PotteryVessel = registerItemOnly(new ItemPotteryVessel().setUnlocalizedName("vessel"));
		PotteryMold = registerItemOnly(new ItemPotteryMold().setUnlocalizedName("mold"));
		PotteryBowl = registerItemOnly(new ItemPotteryBowl().setUnlocalizedName("bowl"));
		PotteryFireBrick = registerItemOnly(new ItemPotteryFireBrick().setUnlocalizedName("fire_brick"));
	}

	public static void SetupCreativeTabs()
	{
		((TFCTabs) TFCTabs.TFCBuilding).setTabIconItemStack(new ItemStack(TFCBlocks.StoneBrick));
		((TFCTabs) TFCTabs.TFCDecoration).setTabIconItemStack(new ItemStack(TFCBlocks.StairsAsh));
		((TFCTabs) TFCTabs.TFCDevices).setTabIconItemStack(new ItemStack(TFCBlocks.Anvil));
		((TFCTabs) TFCTabs.TFCPottery).setTabIconItemStack(new ItemStack(Items.FLOWER_POT));
		((TFCTabs) TFCTabs.TFCMisc).setTabIconItemStack(new ItemStack(LooseRock));
		((TFCTabs) TFCTabs.TFCFoods).setTabIconItemStack(new ItemStack(Items.COOKED_CHICKEN));
		((TFCTabs) TFCTabs.TFCTools).setTabIconItemStack(new ItemStack(StoneAxe));
		((TFCTabs) TFCTabs.TFCWeapons).setTabIconItemStack(new ItemStack(Items.DIAMOND_SWORD));
		((TFCTabs) TFCTabs.TFCArmor).setTabIconItemStack(new ItemStack(Items.DIAMOND_CHESTPLATE));
		((TFCTabs) TFCTabs.TFCMaterials).setTabIconItemStack(new ItemStack(Items.REDSTONE));

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
		StoneShovel.setHarvestLevel("shovel", 1);
		StoneHoe.setHarvestLevel("hoe", 1);
	}
}
