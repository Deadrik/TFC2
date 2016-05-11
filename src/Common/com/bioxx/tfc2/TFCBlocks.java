package com.bioxx.tfc2;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
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

		Dirt = new BlockDirt().setHardness(2F).setUnlocalizedName("dirt");
		Grass = new BlockGrass().setHardness(2F).setUnlocalizedName("grass");
		Stone = new BlockStone().setHardness(5F).setUnlocalizedName("stone");
		Rubble = new BlockRubble().setHardness(6F).setUnlocalizedName("rubble");
		Sand = new BlockSand().setRegistryName("sand").setHardness(1F).setUnlocalizedName("sand");
		Gravel = new BlockGravel().setHardness(1F).setUnlocalizedName("gravel");
		Planks = new BlockPlanks().setHardness(4F).setUnlocalizedName("planks");
		Planks2 = new BlockPlanks2().setHardness(4F).setUnlocalizedName("planks2");
		Effect = new BlockEffect().setHardness(0.1F).setUnlocalizedName("effect");
		Vegetation = new BlockVegetation().setHardness(0.1F).setUnlocalizedName("vegetation");
		LooseRocks = new BlockLooseRocks().setHardness(0.2F).setUnlocalizedName("loose_rock");
		FreshWaterStatic = new BlockFreshWaterStatic(TFCFluids.FRESHWATER, Material.WATER).setUnlocalizedName("freshwater_static").setLightOpacity(3);
		FreshWater = new BlockFreshWater(TFCFluids.FRESHWATER, Material.WATER).setUnlocalizedName("freshwater").setLightOpacity(3);
		SaltWaterStatic = new BlockSaltWaterStatic(TFCFluids.SALTWATER, Material.WATER).setUnlocalizedName("saltwater_static").setLightOpacity(3);
		SaltWater = new BlockSaltWater(TFCFluids.SALTWATER, Material.WATER).setUnlocalizedName("saltwater").setLightOpacity(3);
		Sapling = new BlockSapling().setHardness(0.1F).setUnlocalizedName("sapling");
		Sapling2 = new BlockSapling2().setHardness(0.1F).setUnlocalizedName("sapling2");
		LogNatural = new BlockLogNatural().setHardness(6F).setUnlocalizedName("log_natural");
		LogNatural2 = new BlockLogNatural2().setHardness(6F).setUnlocalizedName("log_natural2");
		LogNaturalPalm = new BlockLogNaturalPalm().setHardness(6F).setUnlocalizedName("log_naturalpalm");
		LogVertical = new BlockLogVertical().setHardness(2F).setUnlocalizedName("log_vertical");
		LogVertical2 = new BlockLogVertical2().setHardness(2F).setUnlocalizedName("log_vertical2");
		LogHorizontal = new BlockLogHorizontal().setHardness(2F).setUnlocalizedName("log_horizontal");
		LogHorizontal2 = new BlockLogHorizontal2().setHardness(2F).setUnlocalizedName("log_horizontal2");
		LogHorizontal3 = new BlockLogHorizontal3().setHardness(2F).setUnlocalizedName("log_horizontal3");
		Leaves = new BlockLeaves().setHardness(0.1F).setUnlocalizedName("leaves");
		Leaves2 = new BlockLeaves2().setHardness(0.1F).setUnlocalizedName("leaves2");
		Ore = new BlockOre().setHardness(5F).setUnlocalizedName("ore");
		ClimbingRocks = new BlockClimbingRocks().setHardness(0.2F).setUnlocalizedName("climbing_rock");
		StoneBrick = new BlockStoneBrick().setHardness(5F).setResistance(5f).setUnlocalizedName("stonebrick");
		StoneSmooth = new BlockStoneSmooth().setHardness(5F).setResistance(5f).setUnlocalizedName("stonesmooth");
		Portal = new BlockPortal().setHardness(-1F).setResistance(6000000f).setUnlocalizedName("portal").setLightOpacity(3).setLightLevel(0.5f);
		PortalStone = new BlockPortalStone().setHardness(-1F).setResistance(6000000f).setUnlocalizedName("portalstone");
		TorchOn = new BlockTorchTFC(true).setHardness(0.0F).setUnlocalizedName("torch_on");
		TorchOff = new BlockTorchTFC(false).setHardness(0.0F).setUnlocalizedName("torch_off");
		Crop = new BlockCrop().setRegistryName("crop").setHardness(0.1F).setUnlocalizedName("crop");
		Farmland = new BlockFarmland().setHardness(2F).setUnlocalizedName("farmland");
		SupportBeam = new BlockWoodSupport().setHardness(2F).setUnlocalizedName("wood_support");
		SupportBeam2 = new BlockWoodSupport2().setHardness(2F).setUnlocalizedName("wood_support2");
		SupportBeam3 = new BlockWoodSupport3().setHardness(2F).setUnlocalizedName("wood_support3");

		StairsAsh = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Ash)).setUnlocalizedName("stairs_ash");
		StairsAspen = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Aspen)).setUnlocalizedName("stairs_aspen");
		StairsBirch = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Birch)).setUnlocalizedName("stairs_birch");
		StairsChestnut = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Chestnut)).setUnlocalizedName("stairs_chestnut");
		StairsDouglasFir = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.DouglasFir)).setUnlocalizedName("stairs_douglasfir");
		StairsHickory = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Hickory)).setUnlocalizedName("stairs_hickory");
		StairsMaple = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Maple)).setUnlocalizedName("stairs_maple");
		StairsOak = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Oak)).setUnlocalizedName("stairs_oak");
		StairsPine = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Pine)).setUnlocalizedName("stairs_pine");
		StairsSequoia = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Sequoia)).setUnlocalizedName("stairs_sequoia");
		StairsSpruce = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Spruce)).setUnlocalizedName("stairs_spruce");
		StairsSycamore = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Sycamore)).setUnlocalizedName("stairs_sycamore");
		StairsWhiteCedar = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.WhiteCedar)).setUnlocalizedName("stairs_whitecedar");
		StairsWillow = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Willow)).setUnlocalizedName("stairs_willow");
		StairsKapok = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Kapok)).setUnlocalizedName("stairs_kapok");
		StairsAcacia = new BlockStairsTFC(Planks.getDefaultState().withProperty(BlockPlanks.META_PROPERTY, WoodType.Acacia)).setUnlocalizedName("stairs_acacia");
		StairsRosewood = new BlockStairsTFC(Planks2.getDefaultState().withProperty(BlockPlanks2.META_PROPERTY, WoodType.Rosewood)).setUnlocalizedName("stairs_rosewood");
		StairsBlackwood = new BlockStairsTFC(Planks2.getDefaultState().withProperty(BlockPlanks2.META_PROPERTY, WoodType.Blackwood)).setUnlocalizedName("stairs_blackwood");
		StairsPalm = new BlockStairsTFC(Planks2.getDefaultState().withProperty(BlockPlanks2.META_PROPERTY, WoodType.Palm)).setUnlocalizedName("stairs_palm");
	}

	public static void RegisterBlocks()
	{
		TFC.log.info(new StringBuilder().append("[TFC2] Registering Blocks").toString());

		// Block registration strings should be lowercase, and separated by underscores. "this_is_an_example"
		// Preferably these strings should be identical to the corresponding unlocalized name.

		register(Dirt, ItemSoil.class);
		register(Grass, ItemSoil.class);
		register(Stone, ItemStone.class);
		register(Rubble, ItemStone.class);
		register(Sand, ItemStone.class);
		register(Gravel, ItemStone.class);
		register(Planks, ItemWood.class);
		register(Planks2, ItemWood.class);
		register(Effect, ItemOre.class);
		register(Vegetation);
		register(LooseRocks, ItemStone.class);
		register(FreshWater, ItemBlock.class);
		register(FreshWaterStatic, ItemBlock.class);
		register(SaltWater, ItemBlock.class);
		register(SaltWaterStatic, ItemBlock.class);
		register(Sapling, ItemWood.class);
		register(Sapling2, ItemWood.class);
		register(LogNatural, ItemWood.class);
		register(LogNatural2, ItemWood.class);
		register(LogNaturalPalm, ItemWood.class);
		register(LogVertical, ItemWood.class);
		register(LogVertical2, ItemWood.class);
		register(LogHorizontal, ItemWood.class);
		register(LogHorizontal2, ItemWood.class);
		register(LogHorizontal3, ItemWood.class);
		register(Leaves, ItemWood.class);
		register(Leaves2, ItemWood.class);
		register(Ore, ItemOre.class);
		register(ClimbingRocks);
		register(StoneBrick, ItemStone.class);
		register(StoneSmooth, ItemStone.class);
		register(Portal);
		register(PortalStone);
		register(TorchOn, ItemBlock.class);
		register(TorchOff, ItemBlock.class);
		register(Crop);
		register(Farmland);
		register(SupportBeam, ItemWoodSupport.class);
		register(SupportBeam2, ItemWoodSupport.class);
		register(SupportBeam3, ItemWoodSupport.class);
		stairsList = new ArrayList<Block>();
		stairsList.add(register(StairsAsh, ItemBlock.class));
		stairsList.add(register(StairsAspen, ItemBlock.class));
		stairsList.add(register(StairsBirch, ItemBlock.class));
		stairsList.add(register(StairsChestnut, ItemBlock.class));
		stairsList.add(register(StairsDouglasFir, ItemBlock.class));
		stairsList.add(register(StairsHickory, ItemBlock.class));
		stairsList.add(register(StairsMaple, ItemBlock.class));
		stairsList.add(register(StairsOak, ItemBlock.class));
		stairsList.add(register(StairsPine, ItemBlock.class));
		stairsList.add(register(StairsSequoia, ItemBlock.class));
		stairsList.add(register(StairsSpruce, ItemBlock.class));
		stairsList.add(register(StairsSycamore, ItemBlock.class));
		stairsList.add(register(StairsWhiteCedar, ItemBlock.class));
		stairsList.add(register(StairsWillow, ItemBlock.class));
		stairsList.add(register(StairsKapok, ItemBlock.class));
		stairsList.add(register(StairsAcacia, ItemBlock.class));
		stairsList.add(register(StairsRosewood, ItemBlock.class));
		stairsList.add(register(StairsBlackwood, ItemBlock.class));
		stairsList.add(register(StairsPalm, ItemBlock.class));

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

	private static Block register(Block b, Class<? extends ItemBlock> i)
	{
		try 
		{
			GameRegistry.register(b);
			ItemBlock ib = i.getDeclaredConstructor(Block.class).newInstance(b);
			GameRegistry.register(ib, b.getRegistryName());
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return b;
	}

	private static Block register(Block b)
	{
		return (Block) GameRegistry.register(b);
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
