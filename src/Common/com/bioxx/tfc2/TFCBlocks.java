package com.bioxx.tfc2;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.Blocks.Terrain.BlockDirt;
import com.bioxx.tfc2.Blocks.Terrain.BlockGrass;
import com.bioxx.tfc2.Blocks.Terrain.BlockStone;
import com.bioxx.tfc2.Items.ItemBlocks.ItemSoil;
import com.bioxx.tfc2.Items.ItemBlocks.ItemStone;

public class TFCBlocks
{
	public static Block Dirt;
	public static Block Grass;
	public static Block Stone;

	public static void LoadBlocks()
	{
		System.out.println(new StringBuilder().append("[TFC2] Loading Blocks").toString());

		Dirt = new BlockDirt().setHardness(2F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("dirt");
		Grass = new BlockGrass().setHardness(2F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("grass");
		Stone = new BlockStone().setHardness(2F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stone");
	}

	public static void RegisterBlocks()
	{
		System.out.println(new StringBuilder().append("[TFC2] Registering Blocks").toString());

		GameRegistry.registerBlock(Dirt, ItemSoil.class, "Dirt");
		GameRegistry.registerBlock(Grass, ItemSoil.class, "Grass");
		GameRegistry.registerBlock(Stone, ItemStone.class, "Stone");
	}
}
