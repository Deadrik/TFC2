package com.bioxx.tfc2;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.*;
import com.bioxx.tfc2.blocks.terrain.*;
import com.bioxx.tfc2.blocks.vanilla.BlockTorchTFC;
import com.bioxx.tfc2.items.itemblocks.*;
import com.bioxx.tfc2.tileentities.*;

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
	public static Block VegDesert;
	public static Block LooseRocks;
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
	//public static Block Crop;
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
	public static Block Anvil;
	public static Block StoneStalag;
	public static Block StoneStalac;
	public static Block Firepit;
	public static Block Cactus;
	public static Block Thatch;
	public static Block PitKiln;
	public static Block AncientDevice;
	public static Block SmallVessel;

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
		VegDesert = new BlockVegDesert().setHardness(0.1F).setUnlocalizedName("veg_desert");
		LooseRocks = new BlockLooseRocks().setHardness(0.2F).setUnlocalizedName("loose_rock");
		Sapling = new BlockSapling().setHardness(0.1F).setUnlocalizedName("sapling");
		Sapling2 = new BlockSapling2().setHardness(0.1F).setUnlocalizedName("sapling2");
		LogNatural = new BlockLogNatural().setHardness(20F).setUnlocalizedName("log_natural");
		LogNatural2 = new BlockLogNatural2().setHardness(20F).setUnlocalizedName("log_natural2");
		LogNaturalPalm = new BlockLogNaturalPalm().setHardness(12F).setUnlocalizedName("log_naturalpalm");
		LogVertical = new BlockLogVertical().setHardness(6F).setUnlocalizedName("log_vertical");
		LogVertical2 = new BlockLogVertical2().setHardness(6F).setUnlocalizedName("log_vertical2");
		LogHorizontal = new BlockLogHorizontal().setHardness(6F).setUnlocalizedName("log_horizontal");
		LogHorizontal2 = new BlockLogHorizontal2().setHardness(6F).setUnlocalizedName("log_horizontal2");
		LogHorizontal3 = new BlockLogHorizontal3().setHardness(6F).setUnlocalizedName("log_horizontal3");
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
		//Crop = new BlockCrop().setRegistryName("crop").setHardness(0.1F).setUnlocalizedName("crop");
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

		Anvil = new BlockAnvil().setHardness(20F).setUnlocalizedName("anvil");
		StoneStalag = new BlockStoneStalag().setHardness(5F).setUnlocalizedName("stoneStalag");
		StoneStalac = new BlockStoneStalac().setHardness(5F).setUnlocalizedName("stoneStalac");
		Firepit = new BlockFirepit().setHardness(4F).setUnlocalizedName("firepit");
		Cactus = new BlockCactus().setHardness(4F).setUnlocalizedName("cactus");
		Thatch = new BlockThatch().setHardness(4F).setUnlocalizedName("thatch");
		PitKiln = new BlockPitKiln().setHardness(4F).setUnlocalizedName("pitkiln");
		AncientDevice = new BlockAncientDevice().setHardness(-1F).setResistance(6000000f).setUnlocalizedName("ancientdevice");
		SmallVessel = new BlockSmallVessel().setHardness(0.2f).setUnlocalizedName("smallvessel");
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
		register(VegDesert);
		register(LooseRocks, ItemStone.class);
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
		register(Cactus);
		register(Thatch, ItemThatch.class);
		register(Farmland);
		register(SupportBeam, ItemWoodSupport.class);
		register(SupportBeam2, ItemWoodSupport.class);
		register(SupportBeam3, ItemWoodSupport.class);

		register(Anvil, ItemBlock.class);
		register(StoneStalag, ItemStone.class);
		register(StoneStalac, ItemStone.class);
		register(Firepit, ItemBlock.class);
		register(PitKiln);
		register(AncientDevice);
		register(SmallVessel);

		stairsList = new ArrayList<Block>();
		stairsList.add(register(StairsAsh, ItemStair.class));
		stairsList.add(register(StairsAspen, ItemStair.class));
		stairsList.add(register(StairsBirch, ItemStair.class));
		stairsList.add(register(StairsChestnut, ItemStair.class));
		stairsList.add(register(StairsDouglasFir, ItemStair.class));
		stairsList.add(register(StairsHickory, ItemStair.class));
		stairsList.add(register(StairsMaple, ItemStair.class));
		stairsList.add(register(StairsOak, ItemStair.class));
		stairsList.add(register(StairsPine, ItemStair.class));
		stairsList.add(register(StairsSequoia, ItemStair.class));
		stairsList.add(register(StairsSpruce, ItemStair.class));
		stairsList.add(register(StairsSycamore, ItemStair.class));
		stairsList.add(register(StairsWhiteCedar, ItemStair.class));
		stairsList.add(register(StairsWillow, ItemStair.class));
		stairsList.add(register(StairsKapok, ItemStair.class));
		stairsList.add(register(StairsAcacia, ItemStair.class));
		stairsList.add(register(StairsRosewood, ItemStair.class));
		stairsList.add(register(StairsBlackwood, ItemStair.class));
		stairsList.add(register(StairsPalm, ItemStair.class));

		Stone.setHarvestLevel("pickaxe", 1);
		StoneBrick.setHarvestLevel("pickaxe", 1);
		StoneSmooth.setHarvestLevel("pickaxe", 1);
		StoneStalag.setHarvestLevel("pickaxe", 1);
		StoneStalac.setHarvestLevel("pickaxe", 1);
		Ore.setHarvestLevel("pickaxe", 1);

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
		Planks.setHarvestLevel("axe", 1);
		Planks2.setHarvestLevel("axe", 1);
		Thatch.setHarvestLevel("axe", 1);

		Dirt.setHarvestLevel("shovel", 1);
		Grass.setHarvestLevel("shovel", 1);
		Gravel.setHarvestLevel("shovel", 1);
		Sand.setHarvestLevel("shovel", 1);
		Farmland.setHarvestLevel("shovel", 1);
		PitKiln.setHarvestLevel("shovel", 1);


		/*************************************
		       Set Block Flammability
		 *************************************/
		for(Block b : stairsList)
			Blocks.FIRE.setFireInfo(b, 5, 20);
		Blocks.FIRE.setFireInfo(LogNatural, 5, 5);
		Blocks.FIRE.setFireInfo(LogNatural2, 5, 5);
		Blocks.FIRE.setFireInfo(LogNaturalPalm, 5, 5);
		Blocks.FIRE.setFireInfo(LogVertical, 5, 5);
		Blocks.FIRE.setFireInfo(LogVertical2, 5, 5);
		Blocks.FIRE.setFireInfo(LogHorizontal, 5, 5);
		Blocks.FIRE.setFireInfo(LogHorizontal2, 5, 5);
		Blocks.FIRE.setFireInfo(LogHorizontal3, 5, 5);
		Blocks.FIRE.setFireInfo(SupportBeam, 5, 20);
		Blocks.FIRE.setFireInfo(SupportBeam2, 5, 20);
		Blocks.FIRE.setFireInfo(SupportBeam3, 5, 20);
		Blocks.FIRE.setFireInfo(Planks, 5, 20);
		Blocks.FIRE.setFireInfo(Planks2, 5, 20);
		Blocks.FIRE.setFireInfo(Thatch, 60, 20);
		Blocks.FIRE.setFireInfo(Leaves, 30, 60);
		Blocks.FIRE.setFireInfo(Leaves2, 30, 60);
		Blocks.FIRE.setFireInfo(Sapling, 60, 100);
		Blocks.FIRE.setFireInfo(Sapling2, 60, 100);

	}

	/**
	 * This version of register should be used to add a block to the ore dictionary
	 */
	private static Block register(Block b, Class<? extends ItemBlock> i, String oreDictName)
	{
		OreDictionary.registerOre(oreDictName, new ItemStack(b, 1, Short.MAX_VALUE));
		return register(b, i);
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
		registerTileEntity(TileAnvil.class, "anvil");
		registerTileEntity(TileFirepit.class, "firepit");
		registerTileEntity(TilePitKiln.class, "pitkiln");
		registerTileEntity(TileSmallVessel.class, "smallvessel");
	}

	private static void registerTileEntity(Class<? extends TileEntity> c, String id)
	{
		GameRegistry.registerTileEntity(c, Reference.ModID+":"+id);
	}

	public static Block getStairsForWood(WoodType wood)
	{
		switch(wood)
		{
		case Acacia:
			return TFCBlocks.StairsAcacia;
		case Ash:
			return TFCBlocks.StairsAsh;
		case Aspen:
			return TFCBlocks.StairsAspen;
		case Birch:
			return TFCBlocks.StairsBirch;
		case Blackwood:
			return TFCBlocks.StairsBlackwood;
		case Chestnut:
			return TFCBlocks.StairsChestnut;
		case DouglasFir:
			return TFCBlocks.StairsDouglasFir;
		case Hickory:
			return TFCBlocks.StairsHickory;
		case Kapok:
			return TFCBlocks.StairsKapok;
		case Maple:
			return TFCBlocks.StairsMaple;
		case Oak:
			return TFCBlocks.StairsOak;
		case Palm:
			return TFCBlocks.StairsPalm;
		case Pine:
			return TFCBlocks.StairsPine;
		case Rosewood:
			return TFCBlocks.StairsRosewood;
		case Sequoia:
			return TFCBlocks.StairsSequoia;
		case Spruce:
			return TFCBlocks.StairsSpruce;
		case Sycamore:
			return TFCBlocks.StairsSycamore;
		case WhiteCedar:
			return TFCBlocks.StairsWhiteCedar;
		case Willow:
			return TFCBlocks.StairsWillow;
		default:
			return TFCBlocks.StairsOak;

		}
	}
}
