package com.bioxx.tfc2;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.api.TFCFluids;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.*;
import com.bioxx.tfc2.blocks.liquids.BlockFreshWater;
import com.bioxx.tfc2.blocks.liquids.BlockFreshWaterStatic;
import com.bioxx.tfc2.blocks.liquids.BlockSaltWater;
import com.bioxx.tfc2.blocks.liquids.BlockSaltWaterStatic;
import com.bioxx.tfc2.blocks.terrain.*;
import com.bioxx.tfc2.blocks.vanilla.BlockTorchTFC;
import com.bioxx.tfc2.items.itemblocks.*;
import com.bioxx.tfc2.tileentities.TileCrop;
import com.bioxx.tfc2.tileentities.TileFarmland;
import com.bioxx.tfc2.tileentities.TileTorch;

public class TFCBlocks
{
	public static ArrayList<Block> stairsList;
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
	public static Block Portal;
	public static Block PortalStone;
	public static Block TorchOn;
	public static Block TorchOff;
	public static Block Crop;
	public static Block Farmland;
	public static Block SupportBeam;
	public static Block SupportBeam2;
	public static Block SupportBeam3;
	public static Block StairsAsh;
	public static Block StairsAspen;
	public static Block StairsBirch;
	public static Block StairsChestnut;
	public static Block StairsDouglasFir;
	public static Block StairsHickory;
	public static Block StairsMaple;
	public static Block StairsOak;
	public static Block StairsPine;
	public static Block StairsSequoia;
	public static Block StairsSpruce;
	public static Block StairsSycamore;
	public static Block StairsWhiteCedar;
	public static Block StairsWillow;
	public static Block StairsKapok;
	public static Block StairsAcacia;
	public static Block StairsRosewood;
	public static Block StairsBlackwood;
	public static Block StairsPalm;

	public static void LoadBlocks()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Loading Blocks").toString());

		// Unlocalized names should be lowercase, and separated by underscores. "this_is_an_example"

		Dirt = new BlockDirt().setHardness(2F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("dirt");
		Grass = new BlockGrass().setHardness(2F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("grass");
		Stone = new BlockStone().setHardness(5F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stone");
		Rubble = new BlockRubble().setHardness(6F).setStepSound(Block.soundTypeStone).setUnlocalizedName("rubble");
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
		Ore = new BlockOre().setHardness(5F).setStepSound(Block.soundTypeStone).setUnlocalizedName("ore");
		ClimbingRocks = new BlockClimbingRocks().setHardness(0.2F).setStepSound(Block.soundTypeStone).setUnlocalizedName("climbing_rock");
		StoneBrick = new BlockStoneBrick().setHardness(5F).setResistance(5f).setStepSound(Block.soundTypeStone).setUnlocalizedName("stonebrick");
		StoneSmooth = new BlockStoneSmooth().setHardness(5F).setResistance(5f).setStepSound(Block.soundTypeStone).setUnlocalizedName("stonesmooth");
		Portal = new BlockPortal().setHardness(-1F).setResistance(6000000f).setStepSound(Block.soundTypeStone).setUnlocalizedName("portal").setLightOpacity(3).setLightLevel(0.5f);
		PortalStone = new BlockPortalStone().setHardness(-1F).setResistance(6000000f).setUnlocalizedName("portalstone");
		TorchOn = new BlockTorchTFC(true).setHardness(0.0F).setStepSound(Block.soundTypeWood).setUnlocalizedName("torch_on");
		TorchOff = new BlockTorchTFC(false).setHardness(0.0F).setStepSound(Block.soundTypeWood).setUnlocalizedName("torch_off");
		Crop = new BlockCrop().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("crop");
		Farmland = new BlockFarmland().setHardness(2F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("farmland");
		SupportBeam = new BlockWoodSupport().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("wood_support");
		SupportBeam2 = new BlockWoodSupport2().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("wood_support2");
		SupportBeam3 = new BlockWoodSupport3().setHardness(2F).setStepSound(Block.soundTypeWood).setUnlocalizedName("wood_support3");

		StairsAsh = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Ash)).setUnlocalizedName("stairs_ash");
		StairsAspen = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Aspen)).setUnlocalizedName("stairs_aspen");
		StairsBirch = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Birch)).setUnlocalizedName("stairs_birch");
		StairsChestnut = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Chestnut)).setUnlocalizedName("stairs_chestnut");
		StairsDouglasFir = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.DouglasFir)).setUnlocalizedName("stairs_douglasfir");
		StairsHickory = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Hickory)).setUnlocalizedName("stairs_hickory");
		StairsMaple = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Maple)).setUnlocalizedName("stairs_maple");
		StairsOak = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Oak)).setUnlocalizedName("stairs_oak");
		StairsPine = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Pine)).setUnlocalizedName("stairs_pine");
		StairsSequoia = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Sequoia)).setUnlocalizedName("stairs_sequoia");
		StairsSpruce = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Spruce)).setUnlocalizedName("stairs_spruce");
		StairsSycamore = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Sycamore)).setUnlocalizedName("stairs_sycamore");
		StairsWhiteCedar = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.WhiteCedar)).setUnlocalizedName("stairs_whitecedar");
		StairsWillow = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Willow)).setUnlocalizedName("stairs_willow");
		StairsKapok = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Kapok)).setUnlocalizedName("stairs_kapok");
		StairsAcacia = new BlockStairs(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Acacia)).setUnlocalizedName("stairs_acacia");
		StairsRosewood = new BlockStairs(Planks2.getDefaultState().withProperty(BlockPlanks2.META_PROPERTY, WoodType.Rosewood)).setUnlocalizedName("stairs_rosewood");
		StairsBlackwood = new BlockStairs(Planks2.getDefaultState().withProperty(BlockPlanks2.META_PROPERTY, WoodType.Blackwood)).setUnlocalizedName("stairs_blackwood");
		StairsPalm = new BlockStairs(Planks2.getDefaultState().withProperty(BlockPlanks2.META_PROPERTY, WoodType.Palm)).setUnlocalizedName("stairs_palm");
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
		GameRegistry.registerBlock(Portal, "portal");
		GameRegistry.registerBlock(PortalStone, "portalstone");
		GameRegistry.registerBlock(TorchOn, "torch_on");
		GameRegistry.registerBlock(TorchOff, "torch_off");
		GameRegistry.registerBlock(Crop, "crop");
		GameRegistry.registerBlock(Farmland, "farmland");
		GameRegistry.registerBlock(SupportBeam, ItemWoodSupport.class, "wood_support");
		GameRegistry.registerBlock(SupportBeam2, ItemWoodSupport.class, "wood_support2");
		GameRegistry.registerBlock(SupportBeam3, ItemWoodSupport.class, "wood_support3");
		stairsList = new ArrayList<Block>();
		stairsList.add(GameRegistry.registerBlock(StairsAsh, "stairs_ash"));
		stairsList.add(GameRegistry.registerBlock(StairsAspen, "stairs_aspen"));
		stairsList.add(GameRegistry.registerBlock(StairsBirch, "stairs_birch"));
		stairsList.add(GameRegistry.registerBlock(StairsChestnut, "stairs_chestnut"));
		stairsList.add(GameRegistry.registerBlock(StairsDouglasFir, "stairs_douglasfir"));
		stairsList.add(GameRegistry.registerBlock(StairsHickory, "stairs_hickory"));
		stairsList.add(GameRegistry.registerBlock(StairsMaple, "stairs_maple"));
		stairsList.add(GameRegistry.registerBlock(StairsOak, "stairs_oak"));
		stairsList.add(GameRegistry.registerBlock(StairsPine, "stairs_pine"));
		stairsList.add(GameRegistry.registerBlock(StairsSequoia, "stairs_sequoia"));
		stairsList.add(GameRegistry.registerBlock(StairsSpruce, "stairs_spruce"));
		stairsList.add(GameRegistry.registerBlock(StairsSycamore, "stairs_sycamore"));
		stairsList.add(GameRegistry.registerBlock(StairsWhiteCedar, "stairs_whitecedar"));
		stairsList.add(GameRegistry.registerBlock(StairsWillow, "stairs_willow"));
		stairsList.add(GameRegistry.registerBlock(StairsKapok, "stairs_kapok"));
		stairsList.add(GameRegistry.registerBlock(StairsAcacia, "stairs_acacia"));
		stairsList.add(GameRegistry.registerBlock(StairsRosewood, "stairs_rosewood"));
		stairsList.add(GameRegistry.registerBlock(StairsBlackwood, "stairs_blackwood"));
		stairsList.add(GameRegistry.registerBlock(StairsPalm, "stairs_palm"));

		LogNatural.setHarvestLevel("axe", 1);
		LogNatural2.setHarvestLevel("axe", 1);
		LogNaturalPalm.setHarvestLevel("axe", 1);
		LogVertical.setHarvestLevel("axe", 1);
		LogVertical2.setHarvestLevel("axe", 1);
		LogHorizontal.setHarvestLevel("axe", 1);
		LogHorizontal2.setHarvestLevel("axe", 1);
		LogHorizontal3.setHarvestLevel("axe", 1);
		SupportBeam.setHarvestLevel("axe", 1);
		SupportBeam2.setHarvestLevel("axe", 1);
		SupportBeam3.setHarvestLevel("axe", 1);

	}

	public static void RegisterTileEntites()
	{
		registerTileEntity(TileTorch.class, "torch");
		registerTileEntity(TileCrop.class, "crop");
		registerTileEntity(TileFarmland.class, "farmland");
	}

	private static void registerTileEntity(Class<? extends TileEntity> c, String id)
	{
		GameRegistry.registerTileEntity(c, Reference.ModID+":"+id);
	}
}
