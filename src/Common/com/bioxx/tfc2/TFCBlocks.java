package com.bioxx.tfc2;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.Blocks.BlockEffect;
import com.bioxx.tfc2.Blocks.BlockFreshWater;
import com.bioxx.tfc2.Blocks.BlockLeaves;
import com.bioxx.tfc2.Blocks.BlockLogHorizontal;
import com.bioxx.tfc2.Blocks.BlockLogHorizontal2;
import com.bioxx.tfc2.Blocks.BlockLogNatural;
import com.bioxx.tfc2.Blocks.BlockLogVertical;
import com.bioxx.tfc2.Blocks.BlockLooseRocks;
import com.bioxx.tfc2.Blocks.BlockPlanks;
import com.bioxx.tfc2.Blocks.BlockSaltWater;
import com.bioxx.tfc2.Blocks.BlockSapling;
import com.bioxx.tfc2.Blocks.BlockVegetation;
import com.bioxx.tfc2.Blocks.Terrain.BlockDirt;
import com.bioxx.tfc2.Blocks.Terrain.BlockGrass;
import com.bioxx.tfc2.Blocks.Terrain.BlockGravel;
import com.bioxx.tfc2.Blocks.Terrain.BlockRubble;
import com.bioxx.tfc2.Blocks.Terrain.BlockSand;
import com.bioxx.tfc2.Blocks.Terrain.BlockStone;
import com.bioxx.tfc2.Items.ItemBlocks.ItemSoil;
import com.bioxx.tfc2.Items.ItemBlocks.ItemStone;
import com.bioxx.tfc2.Items.ItemBlocks.ItemWood;
import com.bioxx.tfc2.api.TFCFluids;

public class TFCBlocks
{
	public static Block Dirt;
	public static Block Grass;
	public static Block Stone;
	public static Block Rubble;
	public static Block Sand;
	public static Block Gravel;
	public static Block Planks;
	public static Block Effect;
	public static Block Vegetation;
	public static Block LooseRocks;
	public static Block FreshWater;
	public static Block SaltWater;
	public static Block Sapling;
	public static Block LogNatural;
	public static Block LogVertical;
	public static Block LogHorizontal;
	public static Block LogHorizontal2;
	public static Block Leaves;
	public static Block Ore;

	public static void LoadBlocks()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Loading Blocks").toString());

		// Unlocalized names should be lowercase, and separated by underscores. "this_is_an_example"

		Dirt = new BlockDirt().setHardness(2F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("dirt");
		Grass = new BlockGrass().setHardness(2F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("grass");
		Stone = new BlockStone().setHardness(20F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stone");
		Rubble = new BlockRubble().setHardness(10F).setStepSound(Block.soundTypeStone).setUnlocalizedName("rubble");
		Sand = new BlockSand().setHardness(1F).setStepSound(Block.soundTypeSand).setUnlocalizedName("sand");
		Gravel = new BlockGravel().setHardness(1F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("gravel");
		Planks = new BlockPlanks().setHardness(4F).setStepSound(Block.soundTypeWood).setUnlocalizedName("planks");
		Effect = new BlockEffect().setHardness(0.1F).setStepSound(Block.soundTypeWood).setUnlocalizedName("effect");
		Vegetation = new BlockVegetation().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("vegetation");
		LooseRocks = new BlockLooseRocks().setHardness(0.2F).setStepSound(Block.soundTypeStone).setUnlocalizedName("loose_rock");
		FreshWater = new BlockFreshWater(TFCFluids.FRESHWATER, Material.water).setUnlocalizedName("freshwater");
		SaltWater = new BlockSaltWater(TFCFluids.SALTWATER, Material.water).setUnlocalizedName("saltwater");
		Sapling = new BlockSapling().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("sapling");
		LogNatural = new BlockLogNatural().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_natural");
		LogVertical = new BlockLogVertical().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_vertical");
		LogHorizontal = new BlockLogHorizontal().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_horizontal");
		LogHorizontal2 = new BlockLogHorizontal2().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_horizontal2");
		Leaves = new BlockLeaves().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("leaves");
	}

	public static void RegisterBlocks()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Registering Blocks").toString());

		// Block registration strings should be lowercase, and separated by underscores. "this_is_an_example"
		// Preferably these strings should be identical to the corresponding unlocalized name.

		GameRegistry.registerBlock(Dirt, ItemSoil.class, "dirt");
		GameRegistry.registerBlock(Grass, ItemSoil.class, "grass");
		GameRegistry.registerBlock(Stone, ItemStone.class, "stone");
		GameRegistry.registerBlock(Rubble, ItemStone.class, "rubble");
		GameRegistry.registerBlock(Sand, ItemStone.class, "sand");
		GameRegistry.registerBlock(Gravel, ItemStone.class, "gravel");
		GameRegistry.registerBlock(Planks, ItemWood.class, "planks");
		GameRegistry.registerBlock(Effect, "effect");
		GameRegistry.registerBlock(Vegetation, "vegetation");
		GameRegistry.registerBlock(LooseRocks, "loose_rock");
		GameRegistry.registerBlock(FreshWater, "freshwater");
		GameRegistry.registerBlock(SaltWater, "saltwater");
		GameRegistry.registerBlock(Sapling, ItemWood.class, "sapling");
		GameRegistry.registerBlock(LogNatural, ItemWood.class, "log_natural");
		GameRegistry.registerBlock(LogVertical, ItemWood.class, "log_vertical");
		GameRegistry.registerBlock(LogHorizontal, ItemWood.class, "log_horizontal");
		GameRegistry.registerBlock(LogHorizontal2, ItemWood.class, "log_horizontal2");
		GameRegistry.registerBlock(Leaves, ItemWood.class, "leaves");

		LogNatural.setHarvestLevel("axe", 1);
		LogVertical.setHarvestLevel("axe", 1);
		LogHorizontal.setHarvestLevel("axe", 1);
		LogHorizontal2.setHarvestLevel("axe", 1);
	}
}
