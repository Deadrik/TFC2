package com.bioxx.tfc2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import com.bioxx.tfc2.animals.BearBrownAnimalDef;
import com.bioxx.tfc2.animals.ElephantAnimalDef;
import com.bioxx.tfc2.animals.ElkAnimalDef;
import com.bioxx.tfc2.api.*;
import com.bioxx.tfc2.api.SkillsManager.Skill;
import com.bioxx.tfc2.api.animals.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.ore.OreConfig;
import com.bioxx.tfc2.api.ore.OreConfig.VeinType;
import com.bioxx.tfc2.api.ore.OreRegistry;
import com.bioxx.tfc2.api.types.OreType;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.api.util.SizeWeightReader;
import com.bioxx.tfc2.api.util.SizeWeightReader.SizeWeightJSON;
import com.bioxx.tfc2.core.FluidTFC;
import com.bioxx.tfc2.core.Recipes;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.entity.*;
import com.bioxx.tfc2.handlers.*;
import com.bioxx.tfc2.handlers.client.DrinkWaterHandler;
import com.bioxx.tfc2.potion.PotionTFC;
import com.bioxx.tfc2.world.DimensionTFC;
import com.bioxx.tfc2.world.generators.WorldGenStalag;
import com.bioxx.tfc2.world.hexgen.*;

public class CommonProxy
{
	public void preInit(FMLPreInitializationEvent event)
	{
		TFC_Sounds.register();
		registerWorldGen();

		DimensionManager.unregisterDimension(0);
		DimensionManager.registerDimension(0, DimensionTFC.SURFACE);
		DimensionManager.registerDimension(2, DimensionTFC.PATHS);

		ResourceLocation still = Core.CreateRes(Reference.getResID()+"blocks/water_still");
		ResourceLocation flow = Core.CreateRes(Reference.getResID()+"blocks/water_flow");
		TFCFluids.SALTWATER = new FluidTFC("saltwater", still, flow).setBaseColor(0xff001945);
		TFCFluids.FRESHWATER = new FluidTFC("freshwater", still, flow).setBaseColor(0xff001945);
		FluidRegistry.registerFluid(TFCFluids.SALTWATER);
		FluidRegistry.registerFluid(TFCFluids.FRESHWATER);
		registerCrops();
		TFCBlocks.LoadBlocks();
		TFCBlocks.RegisterBlocks();
		TFCBlocks.RegisterTileEntites();
		TFCItems.Load();
		TFCItems.SetupCreativeTabs();
		TFCItems.Register();
		registerCropProduce();//Must run after item setup
		setupOre();
		registerOreDictionary();

		SkillsManager.instance.registerSkill(new Skill("gui.skill.woodworker", 1.0f, 1f));
		SkillsManager.instance.registerSkill(new Skill("gui.skill.smith", 1.0f, 1f));
		SkillsManager.instance.registerSkill(new Skill("gui.skill.toolsmith", 1.0f, 10f));
		SkillsManager.instance.registerSkill(new Skill("gui.skill.weaponsmith", 1.0f, 10f));
		SkillsManager.instance.registerSkill(new Skill("gui.skill.armorsmith", 1.0f, 10f));
		SkillsManager.instance.registerSkill(new Skill("gui.skill.farmer", 1.0f, 1f));
		SkillsManager.instance.registerSkill(new Skill("gui.skill.fisherman", 1.0f, 1f));
		SkillsManager.instance.registerSkill(new Skill("gui.skill.butcher", 1.0f, 1f));

		readSizeWeight();
	}

	public void init(FMLInitializationEvent event)
	{
		registerGuiHandler();

		MinecraftForge.EVENT_BUS.register(new PlayerTracker());
		Global.EVENT_BUS.register(new CreateDungeonHandler());

		registerEntities();

		ForgeRegistries.POTIONS.register(PotionTFC.THIRST_POTION);
		ForgeRegistries.POTIONS.register(PotionTFC.ENCUMB_MEDIUM_POTION);
		ForgeRegistries.POTIONS.register(PotionTFC.ENCUMB_HEAVY_POTION);
		ForgeRegistries.POTIONS.register(PotionTFC.ENCUMB_MAX_POTION);
	}

	protected void registerWorldGen()
	{
		//GameRegistry.registerWorldGenerator(new WorldGenCliffNoise(), 1);
		//GameRegistry.registerWorldGenerator(new WorldGenCliffRocks(), 1);
		//GameRegistry.registerWorldGenerator(new WorldGenPortals(), 2);
		GameRegistry.registerWorldGenerator(new WorldGenStalag(), 4);

		HexGenRegistry.registerWorldGenerator(new WorldGenCliffRocksHex(), 1);
		HexGenRegistry.registerWorldGenerator(new WorldGenPortalsHex(), 2);
		HexGenRegistry.registerWorldGenerator(new WorldGenClayHex(), 5);
		HexGenRegistry.registerWorldGenerator(new WorldGenLooseRockHex(), 5);
		HexGenRegistry.registerWorldGenerator(new WorldGenTreesHex(), 10);
		HexGenRegistry.registerWorldGenerator(new WorldGenSwampTreesHex(), 10);
		HexGenRegistry.registerWorldGenerator(new WorldGenCatTailsHex(), 100);
		HexGenRegistry.registerWorldGenerator(new WorldGenGrassHex(), 100);
		HexGenRegistry.registerWorldGenerator(new WorldGenGrassDryHex(), 100);
		HexGenRegistry.registerWorldGenerator(new WorldGenPamsGardensHex(), 25);

		Biome.registerBiome(200, "BIOME_BARE", Global.BIOME_BARE);
		Biome.registerBiome(201, "BIOME_BEACH", Global.BIOME_BEACH);
		Biome.registerBiome(202, "BIOME_DECIDUOUS_FOREST", Global.BIOME_DECIDUOUS_FOREST);
		Biome.registerBiome(203, "BIOME_DEEP_OCEAN", Global.BIOME_DEEP_OCEAN);
		Biome.registerBiome(204, "BIOME_DRY_FOREST", Global.BIOME_DRY_FOREST);
		Biome.registerBiome(205, "BIOME_GRASSLAND", Global.BIOME_GRASSLAND);
		Biome.registerBiome(206, "BIOME_LAKE", Global.BIOME_LAKE);
		Biome.registerBiome(207, "BIOME_MARSH", Global.BIOME_MARSH);
		Biome.registerBiome(208, "BIOME_OCEAN", Global.BIOME_OCEAN);
		Biome.registerBiome(209, "BIOME_POLAR_DESERT", Global.BIOME_POLAR_DESERT);
		Biome.registerBiome(210, "BIOME_POND", Global.BIOME_POND);
		Biome.registerBiome(211, "BIOME_RAIN_FOREST", Global.BIOME_RAIN_FOREST);
		Biome.registerBiome(212, "BIOME_RIVER", Global.BIOME_RIVER);
		Biome.registerBiome(213, "BIOME_SCORCHED", Global.BIOME_SCORCHED);
		Biome.registerBiome(214, "BIOME_SHRUBLAND", Global.BIOME_SHRUBLAND);
		Biome.registerBiome(215, "BIOME_SUBTROPICAL_DESERT", Global.BIOME_SUBTROPICAL_DESERT);
		Biome.registerBiome(216, "BIOME_TAIGA", Global.BIOME_TAIGA);
		Biome.registerBiome(217, "BIOME_TEMPERATE_DESERT", Global.BIOME_TEMPERATE_DESERT);
		Biome.registerBiome(218, "BIOME_TROPICAL_DESERT", Global.BIOME_TROPICAL_DESERT);
		Biome.registerBiome(219, "BIOME_TUNDRA", Global.BIOME_TUNDRA);
		Biome.registerBiome(220, "BIOME_SWAMP", Global.BIOME_SWAMP);

		BiomeDictionary.addTypes(Global.BIOME_BARE, Type.SPARSE, Type.DEAD, Type.WASTELAND);
		BiomeDictionary.addTypes(Global.BIOME_BEACH, Type.BEACH);
		BiomeDictionary.addTypes(Global.BIOME_DECIDUOUS_FOREST, Type.FOREST);
		BiomeDictionary.addTypes(Global.BIOME_DEEP_OCEAN, Type.OCEAN);
		BiomeDictionary.addTypes(Global.BIOME_DRY_FOREST, Type.DRY, Type.FOREST);
		BiomeDictionary.addTypes(Global.BIOME_GRASSLAND, Type.PLAINS);
		BiomeDictionary.addTypes(Global.BIOME_LAKE, Type.WATER);
		BiomeDictionary.addTypes(Global.BIOME_MARSH, Type.WET, Type.LUSH, Type.SWAMP);
		BiomeDictionary.addTypes(Global.BIOME_OCEAN, Type.OCEAN);
		BiomeDictionary.addTypes(Global.BIOME_POLAR_DESERT, Type.COLD, Type.SPARSE, Type.DRY, Type.SANDY, Type.SNOWY);
		BiomeDictionary.addTypes(Global.BIOME_POND, Type.WATER);
		BiomeDictionary.addTypes(Global.BIOME_RAIN_FOREST, Type.HOT, Type.DENSE, Type.WET, Type.JUNGLE, Type.LUSH, Type.FOREST);
		BiomeDictionary.addTypes(Global.BIOME_RIVER, Type.RIVER);
		BiomeDictionary.addTypes(Global.BIOME_SCORCHED, Type.HOT, Type.SPARSE, Type.DRY, Type.DEAD, Type.WASTELAND);
		BiomeDictionary.addTypes(Global.BIOME_SHRUBLAND, Type.DRY, Type.PLAINS);
		BiomeDictionary.addTypes(Global.BIOME_SUBTROPICAL_DESERT, Type.HOT, Type.SPARSE, Type.DRY, Type.SANDY);
		BiomeDictionary.addTypes(Global.BIOME_TAIGA, Type.COLD, Type.CONIFEROUS, Type.FOREST, Type.SNOWY);
		BiomeDictionary.addTypes(Global.BIOME_TEMPERATE_DESERT, Type.SPARSE, Type.DRY, Type.SANDY);
		BiomeDictionary.addTypes(Global.BIOME_TROPICAL_DESERT, Type.HOT, Type.SPARSE, Type.DRY, Type.SANDY);
		BiomeDictionary.addTypes(Global.BIOME_TUNDRA, Type.COLD, Type.SPARSE, Type.SNOWY);
		BiomeDictionary.addTypes(Global.BIOME_SWAMP, Type.WET, Type.SPOOKY, Type.LUSH, Type.SWAMP);
	}

	protected void registerEntities() 
	{
		DataSerializersTFC.register();
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"cart"), EntityCart.class, "cart", 0, TFC.instance, 80, 3, true, 0x000000, 0x00ff00);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"bear"), EntityBear.class, "bear", 1, TFC.instance, 80, 3, true, 0x000000, 0xff0000);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"bearpanda"), EntityBearPanda.class, "bearpanda", 2, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"lion"), EntityLion.class, "lion", 3, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"tiger"), EntityTiger.class, "tiger", 4, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"rhino"), EntityRhino.class, "rhino", 5, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"elephant"), EntityElephant.class, "elephant", 6, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"mammoth"), EntityMammoth.class, "mammoth", 7, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"boar"), EntityBoar.class, "boar", 8, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"bison"), EntityBison.class, "bison", 9, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"foxred"), EntityFoxRed.class, "foxred", 10, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"foxarctic"), EntityFoxArctic.class, "foxarctic", 11, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"foxdesert"), EntityFoxDesert.class, "foxdesert", 12, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"hippo"), EntityHippo.class, "hippo", 13, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"bigcat"), EntityBigCat.class, "bigcat", 14, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"sabertooth"), EntitySabertooth.class, "sabertooth", 15, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"elk"), EntityElk.class, "elk", 16, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
	}

	public void postInit(FMLPostInitializationEvent event)
	{
		Recipes.RegisterNormalRecipes();
		Recipes.RegisterKnappingRecipes();
		Recipes.RegisterKilnRecipes();
		MinecraftForge.EVENT_BUS.register(new CreateSpawnHandler());
		MinecraftForge.EVENT_BUS.register(new WorldLoadHandler());
		MinecraftForge.EVENT_BUS.register(new EntityLivingHandler());
		MinecraftForge.EVENT_BUS.register(new JoinWorldHandler());
		MinecraftForge.EVENT_BUS.register(new ChunkLoadHandler());
		MinecraftForge.EVENT_BUS.register(new ServerTickHandler());
		MinecraftForge.EVENT_BUS.register(new DrinkWaterHandler());
		MinecraftForge.EVENT_BUS.register(new BlockHarvestHandler());
		MinecraftForge.EVENT_BUS.register(new TeleportHandler());
		Global.EVENT_BUS.register(new HexUpdateHandler());
		Global.EVENT_BUS.register(new IslandUpdateHandler());
		registerAnimals();
		registerFuel();
	}

	protected void setupOre()
	{
		OreRegistry.getInstance().registerOre(OreType.Bismuthinite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Bismuthinite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/1), StoneType.getForSubTypes(StoneType.SubType.IgneousExtrusive, StoneType.SubType.Sedimentary));
		OreRegistry.getInstance().registerOre(OreType.Anthracite.getName(), new OreConfig(VeinType.Layer, TFCBlocks.Ore, OreType.Anthracite, /*wMin*/0, /*wMax*/0, /*hMin*/1, /*hMax*/3).setNoiseVertical(1).setMinSeamLength(2).setMaxSeamLength(8), new StoneType[] {StoneType.Chert, StoneType.Dolomite, StoneType.Limestone});
		OreRegistry.getInstance().registerOre(OreType.Lignite.getName(), new OreConfig(VeinType.Layer, TFCBlocks.Ore, OreType.Lignite, /*wMin*/0, /*wMax*/0, /*hMin*/1, /*hMax*/3).setNoiseVertical(1).setMinSeamLength(2).setMaxSeamLength(8), new StoneType[] {StoneType.Shale, StoneType.Claystone});
		OreRegistry.getInstance().registerOre(OreType.Cassiterite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Cassiterite, /*wMin*/1, /*wMax*/2, /*hMin*/1, /*hMax*/1).setRarity(1).setSubSeamRarity(3).setMinSeamLength(20).setMaxSeamLength(40), StoneType.getForSubTypes(StoneType.SubType.IgneousIntrusive));
		OreRegistry.getInstance().registerOre(OreType.Tetrahedrite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Tetrahedrite, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/3), StoneType.getForSubTypes(StoneType.SubType.Metamorphic));
		OreRegistry.getInstance().registerOre(OreType.Sphalerite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Sphalerite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/1), StoneType.getForSubTypes(StoneType.SubType.Metamorphic));
		OreRegistry.getInstance().registerOre(OreType.Garnierite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Garnierite, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/1).setRarity(3), StoneType.getForSubTypes(StoneType.SubType.IgneousIntrusive));
		OreRegistry.getInstance().registerOre(OreType.Hematite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Hematite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/2).setRarity(1).setSubSeamRarity(5), StoneType.getForSubTypes(StoneType.SubType.IgneousExtrusive));
		OreRegistry.getInstance().registerOre(OreType.Magnetite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Magnetite, /*wMin*/1, /*wMax*/2, /*hMin*/3, /*hMax*/4).setSubSeamRarity(5), new StoneType[] {StoneType.Chert, StoneType.Dolomite, StoneType.Claystone});
		OreRegistry.getInstance().registerOre(OreType.Limonite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Limonite, /*wMin*/2, /*wMax*/3, /*hMin*/1, /*hMax*/2).setRarity(1), new StoneType[] {StoneType.Shale, StoneType.Limestone});
		OreRegistry.getInstance().registerOre(OreType.Malachite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Malachite, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/1).setRarity(20).setMinSeamLength(3).setMaxSeamLength(10), new StoneType[] {StoneType.Marble});
		OreRegistry.getInstance().registerOre(OreType.NativeGold.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.NativeGold, /*wMin*/1, /*wMax*/1, /*hMin*/1, /*hMax*/1).setRarity(8).setMinSeamLength(4).setMaxSeamLength(8), StoneType.getForSubTypes(StoneType.SubType.IgneousIntrusive, StoneType.SubType.IgneousExtrusive));
		OreRegistry.getInstance().registerOre(OreType.Galena.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Galena, /*wMin*/1, /*wMax*/2, /*hMin*/1, /*hMax*/2).setRarity(2), StoneType.getForSubTypes(StoneType.SubType.Metamorphic, StoneType.SubType.IgneousExtrusive));
	}

	protected void registerCrops()
	{
		Crop.registerCrop(Crop.Corn);
		Crop.registerCrop(Crop.Cabbage);
		Crop.registerCrop(Crop.Tomato);
		Crop.registerCrop(Crop.Wheat);
		Crop.registerCrop(Crop.Barley);
		Crop.registerCrop(Crop.Rye);
		Crop.registerCrop(Crop.Oat);
		Crop.registerCrop(Crop.Rice);
	}

	protected void registerCropProduce()
	{
		/*FoodRegistry.getInstance().registerCropProduce(Crop.Corn, new ItemStack(TFCItems.FoodCornWhole, 1, 0));
		FoodRegistry.getInstance().registerCropProduce(Crop.Cabbage, new ItemStack(TFCItems.FoodCabbage, 1, 0));
		FoodRegistry.getInstance().registerCropProduce(Crop.Tomato, new ItemStack(TFCItems.FoodTomato, 1, 0));
		FoodRegistry.getInstance().registerCropProduce(Crop.Wheat, new ItemStack(TFCItems.FoodWheatWhole, 1, 0));
		FoodRegistry.getInstance().registerCropProduce(Crop.Barley, new ItemStack(TFCItems.FoodBarleyWhole, 1, 0));
		FoodRegistry.getInstance().registerCropProduce(Crop.Rye, new ItemStack(TFCItems.FoodRyeWhole, 1, 0));
		FoodRegistry.getInstance().registerCropProduce(Crop.Oat, new ItemStack(TFCItems.FoodOatWhole, 1, 0));
		FoodRegistry.getInstance().registerCropProduce(Crop.Rice, new ItemStack(TFCItems.FoodRiceWhole, 1, 0));*/
	}

	protected void registerAnimals()
	{
		AnimalSpawnRegistry.getInstance().register(new ElkAnimalDef());
		AnimalSpawnRegistry.getInstance().register(new ElephantAnimalDef());
		AnimalSpawnRegistry.getInstance().register(new BearBrownAnimalDef());
		//AnimalSpawnRegistry.getInstance().register(new AnimalDef("Elephant",  EntityElephant.class, 2, 10, 50, 50, new AnimalSpawnParams(ClimateTemp.POLAR, ClimateTemp.TROPICAL, Moisture.LOW, Moisture.MAX)));
		/*AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Elephant",  EntityElephant.class, 2, 4, 20, 20, new SpawnParameters(ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, Moisture.LOW, Moisture.MAX)
		{
			@Override
			public boolean canSpawnInDesert()
			{
				return true;
			}
		}));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Brown Bear",  EntityBear.class, 1, 1, 200, 30, new SpawnParameters(ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, Moisture.MEDIUM, Moisture.MAX)){
			@Override
			public void onSpawn(EntityLiving e)
			{
				((EntityBear)e).setBearType(BearType.Brown);
			}
		});
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Black Bear",  EntityBear.class, 1, 1, 200, 30, new SpawnParameters(ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, Moisture.MEDIUM, Moisture.MAX)){
			@Override
			public void onSpawn(EntityLiving e)
			{
				((EntityBear)e).setBearType(BearType.Black);
			}
		});
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Polar Bear",  EntityBear.class, 1, 1, 300, 30, new SpawnParameters(ClimateTemp.POLAR, ClimateTemp.POLAR, Moisture.LOW, Moisture.MAX)){
			@Override
			public void onSpawn(EntityLiving e)
			{
				((EntityBear)e).setBearType(BearType.Polar);
			}
		});
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Panda Bear",  EntityBearPanda.class, 1, 1, 200, 20, 
				new SpawnParameters(ClimateTemp.TEMPERATE, ClimateTemp.TEMPERATE, Moisture.HIGH, Moisture.MAX)
		{
			@Override
			public boolean canSpawnHere(IslandMap map, Center closest)
			{
				//TODO make this check for a bamboo forest
				return super.canSpawnHere(map, closest);
			}
		}){
			@Override
			public void onSpawn(EntityLiving e)
			{
				((EntityBearPanda)e).setBearType(BearType.Panda);
			}
		});
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Bison",  EntityBison.class, 3, 5, 300, 50, new SpawnParameters(ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, Moisture.LOW, Moisture.MEDIUM)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Cow",  EntityCow.class, 2, 4, 300, 50, new SpawnParameters(ClimateTemp.SUBPOLAR, ClimateTemp.SUBTROPICAL, Moisture.LOW, Moisture.HIGH)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Boar",  EntityBoar.class, 2, 4, 500, 50, new SpawnParameters(ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, Moisture.LOW, Moisture.MEDIUM)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Wolf",  EntityWolf.class, 2, 4, 200, 20, new SpawnParameters(ClimateTemp.POLAR, ClimateTemp.TEMPERATE, Moisture.LOW, Moisture.MAX)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Fox Arctic",  EntityFoxArctic.class, 1, 1, 200, 30, new SpawnParameters(ClimateTemp.POLAR, ClimateTemp.SUBPOLAR, Moisture.MEDIUM, Moisture.MAX)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Fox Red",  EntityFoxRed.class, 1, 1, 200, 30, new SpawnParameters(ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, Moisture.MEDIUM, Moisture.HIGH)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Fox Desert",  EntityFoxDesert.class, 1, 1, 200, 30, new SpawnParameters(ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, Moisture.LOW, Moisture.LOW){
			@Override
			public boolean canSpawnInDesert()
			{
				return true;
			}
		}));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Hippo",  EntityHippo.class, 1, 1, 50, 20, new SpawnParameters(ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, Moisture.LOW, Moisture.MEDIUM, EntityLiving.SpawnPlacementType.IN_WATER){
			@Override
			public boolean canSpawnHere(IslandMap map, Center closest)
			{
				boolean isValid = false;
				if(closest.hasAttribute(Attribute.River))
				{
					RiverAttribute attrib = (RiverAttribute) closest.getAttribute(Attribute.River);
					isValid = attrib.getRiver() >= 1.0;
				}

				if(closest.hasMarker(Marker.Pond))
					isValid = true;

				if(closest.hasAttribute(Attribute.Lake))
				{
					LakeAttribute attrib = (LakeAttribute) closest.getAttribute(Attribute.Lake);
					if(attrib.getBorderDistance() <= 1)
						isValid = true;
				}

				return isValid;
			}

			@Override
			public boolean canSpawnInDesert()
			{
				return true;
			}
		}));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Lion",  EntityLion.class, 1, 3, 200, 30, new SpawnParameters(ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, Moisture.LOW, Moisture.HIGH){
			@Override
			public boolean canSpawnHere(IslandMap map, Center closest)
			{
				boolean isValid = super.canSpawnHere(map, closest);

				//This makes sure that the hex isn't too heavily forested
				if(closest.getMoisture().isGreaterThan(Moisture.MEDIUM))
					return false;

				return isValid;
			}
		}));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Rhino",  EntityRhino.class, 1, 1, 50, 20, new SpawnParameters(ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, Moisture.LOW, Moisture.HIGH){
			@Override
			public boolean canSpawnHere(IslandMap map, Center closest)
			{
				boolean isValid = super.canSpawnHere(map, closest);

				//This makes sure that the hex isn't too heavily forested
				if(closest.getMoisture().isGreaterThan(Moisture.LOW))
					return false;

				return isValid;
			}

			@Override
			public boolean canSpawnInDesert()
			{
				return true;
			}
		}));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Tiger",  EntityTiger.class, 1, 1, 100, 20, new SpawnParameters(ClimateTemp.TEMPERATE, ClimateTemp.TROPICAL, Moisture.MEDIUM, Moisture.MAX))
		{
			@Override
			public void onSpawn(EntityLiving e)
			{
				((EntityTiger)e).setTigerType(TigerType.Normal);
			}
		});
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Snow Tiger",  EntityTiger.class, 1, 1, 100, 20, new SpawnParameters(ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, Moisture.MEDIUM, Moisture.MAX))
		{
			@Override
			public void onSpawn(EntityLiving e)
			{
				((EntityTiger)e).setTigerType(TigerType.Snow);
			}
		});
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Ocelot",  EntityOcelot.class, 2, 4, 300, 40, new SpawnParameters(ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, Moisture.HIGH, Moisture.MAX)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Chicken",  EntityChicken.class, 2, 4, 500, 50, new SpawnParameters(ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, Moisture.MEDIUM, Moisture.VERYHIGH)));
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Sheep",  EntitySheep.class, 2, 4, 300, 30, new SpawnParameters(ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, Moisture.MEDIUM, Moisture.VERYHIGH){
			@Override
			public boolean canSpawnHere(IslandMap map, Center closest)
			{
				boolean isValid = super.canSpawnHere(map, closest);

				if(closest.getElevation() < 0.5)
					return false;

				return isValid;
			}
		}));*/
	}

	protected void registerFuel()
	{
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical2, 1, WoodType.Rosewood.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical2, 1, WoodType.Blackwood.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical2, 1, WoodType.Palm.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Acacia.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Ash.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Aspen.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Birch.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Chestnut.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.DouglasFir.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Hickory.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Kapok.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Maple.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Oak.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Pine.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Sequoia.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Spruce.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Sycamore.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.WhiteCedar.getMeta()), 2000);
		Global.AddFirepitFuel(new ItemStack(TFCBlocks.LogVertical, 1, WoodType.Willow.getMeta()), 2000);
	}

	protected void registerOreDictionary()
	{
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogVertical, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogVertical2, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogHorizontal, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogHorizontal2, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogHorizontal3, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogNatural, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogNatural2, 1, OreDictionary.WILDCARD_VALUE));
		OreDictionary.registerOre("logWood", new ItemStack(TFCBlocks.LogNaturalPalm, 1, OreDictionary.WILDCARD_VALUE));
	}

	public void registerGuiHandler()
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(TFC.instance, new GuiHandler());
	}

	public void setupFluids()
	{
		//FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid(TFCFluids.LAVA.getName()), new ItemStack(TFCItems.BlueSteelBucketLava), new ItemStack(TFCItems.BlueSteelBucketEmpty));
	}

	public File getMinecraftDir()
	{
		return FMLCommonHandler.instance().getMinecraftServerInstance().getFile("");/*new File(".");*/
	}

	public void registerKeys()
	{

	}

	public void registerKeyBindingHandler()
	{

	}

	public void uploadKeyBindingsToGame()
	{

	}

	public void onClientLogin() 
	{
	}

	public boolean isClientSide()
	{
		return false;
	}

	public void sendToAllNear(World world, BlockPos pos, int range, Packet<?> packet)
	{
		world.getMinecraftServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), range, world.provider.getDimension(), packet);
	}

	public EntityPlayer getPlayer()
	{
		return null;
	}

	public void readSizeWeight()
	{

		SizeWeightReader reader;
		try
		{
			//List<String> list = Helper.getResourceFiles("/assets/tfc2/food/");
			//if(list.size() == 0)
			//	TFC.log.info("Food -> No internal files found");
			List<String> list = new ArrayList<String>();
			list.add("harvestcraft_sizeweight.json");
			list.add("tfc2_sizeweight.json");

			for(String f : list)
			{
				reader = new SizeWeightReader("/assets/tfc2/sizeweight/"+f);
				TFC.log.info("SizeWeight -> Reading " + reader.path);
				if(reader.read())
				{
					applySizeWeightValues(reader);
				}
			}
		}
		catch(Exception e)
		{
			TFC.log.error(e.getMessage());
		}
		//Now read from the user's mods folder
		reader = new SizeWeightReader("");
		File folder = new File(TFC.proxy.getMinecraftDir(), "/mods/tfc2/sizeweight/");
		if(folder != null && folder.listFiles() != null)
		{
			for (final File fileEntry : folder.listFiles()) 
			{
				if(reader.read(fileEntry))
				{
					applySizeWeightValues(reader);
				}
			}
		}
	}

	private void applySizeWeightValues(SizeWeightReader reader)
	{
		for(SizeWeightJSON json : reader.list)
		{
			ResourceLocation rl = new ResourceLocation(json.itemName);
			Item i = ForgeRegistries.ITEMS.getValue(rl);
			if(i == null)
			{
				TFC.log.warn("SizeWeightRegistry -> Item not found when searching ItemRegistry for object ->" + json.itemName);
				continue;
			}

			SizeWeightRegistry.GetInstance().addProperty(json);
		}
	}
}
