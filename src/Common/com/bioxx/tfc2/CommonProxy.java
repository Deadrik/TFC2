package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.LakeAttribute;
import com.bioxx.jmapgen.attributes.RiverAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.api.*;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnParameters;
import com.bioxx.tfc2.api.SkillsManager.Skill;
import com.bioxx.tfc2.api.ore.OreConfig;
import com.bioxx.tfc2.api.ore.OreConfig.VeinType;
import com.bioxx.tfc2.api.ore.OreRegistry;
import com.bioxx.tfc2.api.types.*;
import com.bioxx.tfc2.core.FluidTFC;
import com.bioxx.tfc2.core.Recipes;
import com.bioxx.tfc2.core.TFC_Sounds;
import com.bioxx.tfc2.entity.*;
import com.bioxx.tfc2.entity.EntityBear.BearType;
import com.bioxx.tfc2.entity.EntityTiger.TigerType;
import com.bioxx.tfc2.handlers.*;
import com.bioxx.tfc2.handlers.client.DrinkWaterHandler;
import com.bioxx.tfc2.potion.PotionTFC;
import com.bioxx.tfc2.world.DimensionTFC;
import com.bioxx.tfc2.world.generators.*;

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
	}

	public void init(FMLInitializationEvent event)
	{
		registerGuiHandler();

		MinecraftForge.EVENT_BUS.register(new PlayerTracker());
		Global.EVENT_BUS.register(new CreateDungeonHandler());

		registerEntities();

		ForgeRegistries.POTIONS.register(PotionTFC.THIRST_POTION);
	}

	protected void registerWorldGen()
	{
		GameRegistry.registerWorldGenerator(new WorldGenCliffNoise(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenCliffRocks(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenPortals(), 2);
		GameRegistry.registerWorldGenerator(new WorldGenStalag(), 4);
		GameRegistry.registerWorldGenerator(new WorldGenLooseRock(), 5);
		GameRegistry.registerWorldGenerator(new WorldGenClay(), 5);
		GameRegistry.registerWorldGenerator(new WorldGenPamsGardens(), 25);
		GameRegistry.registerWorldGenerator(new WorldGenTrees(), 10);
		GameRegistry.registerWorldGenerator(new WorldGenCatTails(), 100);
		GameRegistry.registerWorldGenerator(new WorldGenGrass(), 100);
		GameRegistry.registerWorldGenerator(new WorldGenGrassDry(), 100);

	}

	protected void registerEntities() 
	{
		DataSerializersTFC.register();
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Cart"), EntityCart.class, "Cart", 0, TFC.instance, 80, 3, true, 0x000000, 0x00ff00);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Bear"), EntityBear.class, "Bear", 1, TFC.instance, 80, 3, true, 0x000000, 0xff0000);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"BearPanda"), EntityBearPanda.class, "BearPanda", 2, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Lion"), EntityLion.class, "Lion", 3, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Tiger"), EntityTiger.class, "Tiger", 4, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Rhino"), EntityRhino.class, "Rhino", 5, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Elephant"), EntityElephant.class, "Elephant", 6, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Mammoth"), EntityMammoth.class, "Mammoth", 7, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Boar"), EntityBoar.class, "Boar", 8, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Bison"), EntityBison.class, "Bison", 9, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"FoxRed"), EntityFoxRed.class, "FoxRed", 10, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"FoxArctic"), EntityFoxArctic.class, "FoxArctic", 11, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"FoxDesert"), EntityFoxDesert.class, "FoxDesert", 12, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Hippo"), EntityHippo.class, "Hippo", 13, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"BigCat"), EntityBigCat.class, "BigCat", 14, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Sabertooth"), EntitySabertooth.class, "Sabertooth", 15, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
		EntityRegistry.registerModEntity(Core.CreateRes(Reference.getResID()+"Elk"), EntityElk.class, "Elk", 16, TFC.instance, 80, 3, true, 0x000000, 0xffffff);
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
		AnimalSpawnRegistry.getInstance().register(new SpawnGroup("Elephant",  EntityElephant.class, 2, 4, 20, 20, new SpawnParameters(ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, Moisture.LOW, Moisture.MAX)
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
		}));
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
}
