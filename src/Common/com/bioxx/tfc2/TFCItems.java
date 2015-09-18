package com.bioxx.tfc2;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.items.ItemLooseRock;

public class TFCItems 
{
	public static Item StoneAxe;
	public static Item LooseRock;

	public static void Load()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Loading Items").toString());

		StoneAxe = new Item().setUnlocalizedName("stoneaxe");
		LooseRock = new ItemLooseRock().setUnlocalizedName("looserock");

	}

	public static void Register()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Registering Items").toString());
		GameRegistry.registerItem(StoneAxe, "stoneaxe");
		GameRegistry.registerItem(LooseRock, "looserock");




		SetupHarvestLevels();
	}

	private static void SetupHarvestLevels()
	{
		StoneAxe.setHarvestLevel("axe", 1);
	}
}
