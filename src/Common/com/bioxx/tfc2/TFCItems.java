package com.bioxx.tfc2;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.items.ItemLooseRock;
import com.bioxx.tfc2.items.ItemTerra;

public class TFCItems 
{
	public static Item StoneAxe;
	public static Item LooseRock;
	public static Item StoneAxeHead;

	public static void Load()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Loading Items").toString());
		LooseRock = new ItemLooseRock().setUnlocalizedName("looserock");
		StoneAxe = new ItemTerra().setUnlocalizedName("stoneaxe");
		StoneAxeHead = new ItemTerra().setUnlocalizedName("stoneaxehead");


	}

	public static void Register()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Registering Items").toString());
		GameRegistry.registerItem(LooseRock, "looserock");
		GameRegistry.registerItem(StoneAxe, "stoneaxe");
		GameRegistry.registerItem(StoneAxeHead, "stoneaxehead");





		SetupHarvestLevels();
	}

	private static void SetupHarvestLevels()
	{
		StoneAxe.setHarvestLevel("axe", 1);
	}
}
