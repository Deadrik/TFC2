package com.bioxx.tfc2;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.api.TFCFluids;
import com.bioxx.tfc2.blocks.BlockClimbingRocks;
import com.bioxx.tfc2.blocks.BlockEffect;
import com.bioxx.tfc2.blocks.BlockFreshWater;
import com.bioxx.tfc2.blocks.BlockFreshWaterStatic;
import com.bioxx.tfc2.blocks.BlockLeaves;
import com.bioxx.tfc2.blocks.BlockLeaves2;
import com.bioxx.tfc2.blocks.BlockLogHorizontal;
import com.bioxx.tfc2.blocks.BlockLogHorizontal2;
import com.bioxx.tfc2.blocks.BlockLogHorizontal3;
import com.bioxx.tfc2.blocks.BlockLogNatural;
import com.bioxx.tfc2.blocks.BlockLogNatural2;
import com.bioxx.tfc2.blocks.BlockLogNaturalPalm;
import com.bioxx.tfc2.blocks.BlockLogVertical;
import com.bioxx.tfc2.blocks.BlockLogVertical2;
import com.bioxx.tfc2.blocks.BlockLooseRocks;
import com.bioxx.tfc2.blocks.BlockPlanks;
import com.bioxx.tfc2.blocks.BlockPlanks2;
import com.bioxx.tfc2.blocks.BlockSaltWater;
import com.bioxx.tfc2.blocks.BlockSaltWaterStatic;
import com.bioxx.tfc2.blocks.BlockSapling;
import com.bioxx.tfc2.blocks.BlockSapling2;
import com.bioxx.tfc2.blocks.BlockStoneBrick;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.terrain.BlockDirt;
import com.bioxx.tfc2.blocks.terrain.BlockGrass;
import com.bioxx.tfc2.blocks.terrain.BlockGravel;
import com.bioxx.tfc2.blocks.terrain.BlockOre;
import com.bioxx.tfc2.blocks.terrain.BlockRubble;
import com.bioxx.tfc2.blocks.terrain.BlockSand;
import com.bioxx.tfc2.blocks.terrain.BlockStone;
import com.bioxx.tfc2.items.itemblocks.ItemOre;
import com.bioxx.tfc2.items.itemblocks.ItemSoil;
import com.bioxx.tfc2.items.itemblocks.ItemStone;
import com.bioxx.tfc2.items.itemblocks.ItemWood;

public class TFCBlocks
{
	public static Block Dirt;
	public static Block Grass;
	public static Block Stone;
	public static Block Rubble;
	public static Block Sand;
	public static Block Gravel;
	public static Block Planks;
	public static Block Planks2;
	public static Block Effect;
	public static Block Vegetation;
	public static Block LooseRocks;
	public static Block FreshWater;
	public static Block SaltWater;
	public static Block FreshWaterStatic;
	public static Block SaltWaterStatic;
	public static Block Sapling;
	public static Block Sapling2;
	public static Block LogNatural;
	public static Block LogNatural2;
	public static Block LogNaturalPalm;
	public static Block LogVertical;
	public static Block LogVertical2;
	public static Block LogHorizontal;
	public static Block LogHorizontal2;
	public static Block LogHorizontal3;
	public static Block Leaves;
	public static Block Leaves2;
	public static Block Ore;
	public static Block ClimbingRocks;
	public static Block StoneBrick;
	public static Block StoneSmooth;

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
		Planks2 = new BlockPlanks2().setHardness(4F).setStepSound(Block.soundTypeWood).setUnlocalizedName("planks2");
		Effect = new BlockEffect().setHardness(0.1F).setStepSound(Block.soundTypeWood).setUnlocalizedName("effect");
		Vegetation = new BlockVegetation().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("vegetation");
		LooseRocks = new BlockLooseRocks().setHardness(0.2F).setStepSound(Block.soundTypeStone).setUnlocalizedName("loose_rock");
		FreshWaterStatic = new BlockFreshWaterStatic(TFCFluids.FRESHWATER, Material.water).setUnlocalizedName("freshwater").setLightOpacity(3);
		FreshWater = new BlockFreshWater(TFCFluids.FRESHWATER, Material.water).setUnlocalizedName("freshwater").setLightOpacity(3);
		SaltWaterStatic = new BlockSaltWaterStatic(TFCFluids.SALTWATER, Material.water).setUnlocalizedName("saltwater").setLightOpacity(3);
		SaltWater = new BlockSaltWater(TFCFluids.SALTWATER, Material.water).setUnlocalizedName("saltwater").setLightOpacity(3);
		Sapling = new BlockSapling().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("sapling");
		Sapling2 = new BlockSapling2().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("sapling2");
		LogNatural = new BlockLogNatural().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_natural");
		LogNatural2 = new BlockLogNatural2().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_natural2");
		LogNaturalPalm = new BlockLogNaturalPalm().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("LogNaturalPalm");
		LogVertical = new BlockLogVertical().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_vertical");
		LogVertical2 = new BlockLogVertical2().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_vertical2");
		LogHorizontal = new BlockLogHorizontal().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_horizontal");
		LogHorizontal2 = new BlockLogHorizontal2().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_horizontal2");
		LogHorizontal3 = new BlockLogHorizontal3().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("log_horizontal3");
		Leaves = new BlockLeaves().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("leaves");
		Leaves2 = new BlockLeaves2().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("leaves2");
		Ore = new BlockOre().setHardness(20F).setStepSound(Block.soundTypeStone).setUnlocalizedName("ore");
		ClimbingRocks = new BlockClimbingRocks().setHardness(0.2F).setStepSound(Block.soundTypeStone).setUnlocalizedName("climbing_rock");
		StoneBrick = new BlockStoneBrick().setHardness(20F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stonebrick");
		StoneSmooth = new BlockStoneBrick().setHardness(20F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stonesmooth");
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
		GameRegistry.registerBlock(Planks2, ItemWood.class, "planks2");
		GameRegistry.registerBlock(Effect, ItemOre.class, "effect");
		GameRegistry.registerBlock(Vegetation, "vegetation");
		GameRegistry.registerBlock(LooseRocks, "loose_rock");
		GameRegistry.registerBlock(FreshWater, "freshwater");
		GameRegistry.registerBlock(FreshWaterStatic, "freshwater_static");
		GameRegistry.registerBlock(SaltWater, "saltwater");
		GameRegistry.registerBlock(SaltWaterStatic, "saltwater_static");
		GameRegistry.registerBlock(Sapling, ItemWood.class, "sapling");
		GameRegistry.registerBlock(Sapling2, ItemWood.class, "sapling2");
		GameRegistry.registerBlock(LogNatural, ItemWood.class, "log_natural");
		GameRegistry.registerBlock(LogNatural2, ItemWood.class, "log_natural2");
		GameRegistry.registerBlock(LogNaturalPalm, ItemWood.class, "log_naturalpalm");
		GameRegistry.registerBlock(LogVertical, ItemWood.class, "log_vertical");
		GameRegistry.registerBlock(LogVertical2, ItemWood.class, "log_vertical2");
		GameRegistry.registerBlock(LogHorizontal, ItemWood.class, "log_horizontal");
		GameRegistry.registerBlock(LogHorizontal2, ItemWood.class, "log_horizontal2");
		GameRegistry.registerBlock(LogHorizontal3, ItemWood.class, "log_horizontal3");
		GameRegistry.registerBlock(Leaves, ItemWood.class, "leaves");
		GameRegistry.registerBlock(Leaves2, ItemWood.class, "leaves2");
		GameRegistry.registerBlock(Ore, ItemOre.class, "ore");
		GameRegistry.registerBlock(ClimbingRocks, "climbing_rock");
		GameRegistry.registerBlock(StoneBrick, ItemStone.class, "stonebrick");
		GameRegistry.registerBlock(StoneSmooth, ItemStone.class, "stonesmooth");

		LogNatural.setHarvestLevel("axe", 1);
		LogNatural2.setHarvestLevel("axe", 1);
		LogNaturalPalm.setHarvestLevel("axe", 1);
		LogVertical.setHarvestLevel("axe", 1);
		LogVertical2.setHarvestLevel("axe", 1);
		LogHorizontal.setHarvestLevel("axe", 1);
		LogHorizontal2.setHarvestLevel("axe", 1);
		LogHorizontal3.setHarvestLevel("axe", 1);
	}
}
