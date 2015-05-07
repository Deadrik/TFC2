package com.bioxx.tfc2;

import net.minecraft.block.Block;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.Blocks.Terrain.BlockDirt;
import com.bioxx.tfc2.Blocks.Terrain.BlockGrass;
import com.bioxx.tfc2.Items.ItemBlocks.ItemSoil;

public class TFCBlocks
{
	public static Block Dirt;
	public static Block Grass;

	public static void LoadBlocks()
	{
		System.out.println(new StringBuilder().append("[TFC2] Loading Blocks").toString());

		Dirt = new BlockDirt().setHardness(2F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("dirt");
		Grass = new BlockGrass().setHardness(2F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("grass");
	}

	public static void RegisterBlocks()
	{
		System.out.println(new StringBuilder().append("[TFC2] Registering Blocks").toString());

		GameRegistry.registerBlock(Dirt, ItemSoil.class, "Dirt");
		GameRegistry.registerBlock(Grass, ItemSoil.class, "Grass");

		for(String name : Global.STONE_ALL)
		{
			ModelBakery.addVariantName(Item.getItemFromBlock(Dirt), Reference.ModID + ":" + name);
			ModelBakery.addVariantName(Item.getItemFromBlock(Grass), Reference.ModID + ":" + name);
		}
	}
}
