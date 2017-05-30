package com.bioxx.tfc2;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import com.bioxx.jmapgen.dungeon.DungeonSchemManager;
import com.bioxx.jmapgen.dungeon.DungeonTheme.EntranceType;
import com.bioxx.tfc2.api.FoodRegistry;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.api.trees.TreeConfig;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.trees.TreeSchematic;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.commands.*;
import com.bioxx.tfc2.core.PortalSchematic;
import com.bioxx.tfc2.core.util.FoodReader;
import com.bioxx.tfc2.core.util.FoodReader.FoodJSON;
import com.bioxx.tfc2.networking.client.CAnvilStrikePacket;
import com.bioxx.tfc2.networking.client.CFoodPacket;
import com.bioxx.tfc2.networking.client.CMapPacket;
import com.bioxx.tfc2.networking.server.SAnvilCraftingPacket;
import com.bioxx.tfc2.networking.server.SKnappingPacket;
import com.bioxx.tfc2.networking.server.SMapRequestPacket;
import com.bioxx.tfc2.world.WorldGen;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.ModVersion, useMetadata = false, dependencies = Reference.ModDependencies)
public class TFC
{
	@Instance
	public static TFC instance;

	public static Logger log = LogManager.getLogger("TFC");

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	public static SimpleNetworkWrapper network;

	public TFC() {}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		instance = this;
		log = event.getModLog();
		loadSettings();
		proxy.preInit(event);

		network = NetworkRegistry.INSTANCE.newSimpleChannel("TFC2");
		network.registerMessage(CMapPacket.Handler.class, CMapPacket.class, 0, Side.CLIENT);
		network.registerMessage(SMapRequestPacket.Handler.class, SMapRequestPacket.class, 1, Side.SERVER);
		network.registerMessage(SKnappingPacket.Handler.class, SKnappingPacket.class, 2, Side.SERVER);
		network.registerMessage(CAnvilStrikePacket.Handler.class, CAnvilStrikePacket.class, 3, Side.CLIENT);
		network.registerMessage(SAnvilCraftingPacket.Handler.class, SAnvilCraftingPacket.class, 4, Side.SERVER);
		network.registerMessage(CFoodPacket.Handler.class, CFoodPacket.class, 5, Side.CLIENT);

		//Register tree types and load tree schematics
		loadTrees();

		loadDungeonSchems();

		Core.PortalSchematic = new PortalSchematic("/assets/tfc2/schematics/portal.schematic", "portal");
		Core.PortalSchematic.Load();

		//Read our built in food values first
		FoodReader foodReader;
		try
		{
			//List<String> list = Helper.getResourceFiles("/assets/tfc2/food/");
			//if(list.size() == 0)
			//	TFC.log.info("Food -> No internal files found");
			List<String> list = new ArrayList<String>();
			list.add("harvestcraftfood.json");
			list.add("tfc2food.json");

			for(String f : list)
			{
				foodReader = new FoodReader("/assets/tfc2/food/"+f);
				TFC.log.info("Food -> Reading " + foodReader.path);
				if(foodReader.read())
				{
					applyFoodValues(foodReader);
				}
			}
		}
		catch(Exception e)
		{
			TFC.log.error(e.getMessage());
		}
		//Now read from the user's mods folder
		foodReader = new FoodReader("");
		File folder = new File(TFC.proxy.getMinecraftDir(), "/mods/tfc2/food/");
		if(folder != null && folder.listFiles() != null)
		{
			for (final File fileEntry : folder.listFiles()) 
			{
				if(foodReader.read(fileEntry))
				{
					applyFoodValues(foodReader);
				}
			}
		}



	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(event);
	}

	@EventHandler
	public void modsLoaded(FMLPostInitializationEvent event)
	{
		ForgeModContainer.zombieBabyChance = 0;
		if(Loader.isModLoaded("harvestcraft"))
		{
			try 
			{
				Class HCclass = Class.forName("com.pam.harvestcraft.HarvestCraft");
				Object o = HCclass.getDeclaredField("instance").get(null);
				Object configObj = HCclass.getDeclaredField("config").get(o);

				Class configClass = Class.forName("com.pam.harvestcraft.config.ConfigHandler");

				//Gardens
				configClass.getDeclaredField("enablearidgardenGeneration").setBoolean(configObj, false);
				configClass.getDeclaredField("enablefrostgardenGeneration").setBoolean(configObj, false);
				configClass.getDeclaredField("enableshadedgardenGeneration").setBoolean(configObj, false);
				configClass.getDeclaredField("enablesoggygardenGeneration").setBoolean(configObj, false);
				configClass.getDeclaredField("enabletropicalgardenGeneration").setBoolean(configObj, false);
				configClass.getDeclaredField("enablewindygardenGeneration").setBoolean(configObj, false);

				//Fruit Trees
				configClass.getDeclaredField("temperatefruittreeRarity").setInt(configObj, 0);
				configClass.getDeclaredField("tropicalfruittreeRarity").setInt(configObj, 0);
				configClass.getDeclaredField("coniferousfruittreeRarity").setInt(configObj, 0);

			} catch (ClassNotFoundException e) 
			{
				log.warn("Unable to edit harvestcraft config class -> ClassNotFoundException");
			} catch (NoSuchFieldException e) 
			{
				log.warn("Unable to edit harvestcraft config class -> NoSuchFieldException");
			} catch (SecurityException e) 
			{
				log.warn("Unable to edit harvestcraft config class -> SecurityException");
			} catch (IllegalArgumentException e) {
				log.warn("Unable to edit harvestcraft config class -> IllegalArgumentException");
			} catch (IllegalAccessException e) {
				log.warn("Unable to edit harvestcraft config class -> IllegalAccessException");
			}

		}
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent evt)
	{
		evt.registerServerCommand(new PrintImageMapCommand());
		evt.registerServerCommand(new TeleportInIslandCommand());
		evt.registerServerCommand(new RemoveAreaCommand());
		evt.registerServerCommand(new StripChunkCommand());
		evt.registerServerCommand(new RegenChunkCommand());
		evt.registerServerCommand(new DebugCommand());
	}

	@EventHandler
	public void serverStarting(FMLServerStoppingEvent evt)
	{
		if(WorldGen.getInstance() != null)
		{
			WorldGen.getInstance().resetCache();
			WorldGen.ClearInstances();
		}
	}

	public void loadSettings()
	{
		Configuration config;
		try
		{
			config = new Configuration(new File(TFC.proxy.getMinecraftDir(), "/config/TFCOptions.cfg"));
			config.load();
		} catch (Exception e) {
			log.info(new StringBuilder().append("[TFC2] Error while trying to access settings configuration!").toString());
			config = null;
		}
		log.info(new StringBuilder().append("[TFC2] Loading Settings").toString());
		/**Start setup here*/
		String GAMEL_HEADER = "Game";
		String ENGINE_HEADER = "Engine";
		String DEBUG_HEADER = "Debug";

		//Game
		TFCOptions.torchBurnTime = TFCOptions.getIntFor(config, GAMEL_HEADER, "torchBurnTime", 48, "This is how many in-game hours torches will last before burning out. Set to 0 for infinitely burning torches.");
		//Engine
		TFCOptions.maxThreadsForIslandGen = TFCOptions.getIntFor(config, ENGINE_HEADER, "maxThreadsForIslandGen", 1, "Maximum number of neighboring islands that can be pregenerated at once. Setting this higher may reduce performance.");
		//Debug
		TFCOptions.shouldGenTrees = TFCOptions.getBooleanFor(config, DEBUG_HEADER, "shouldGenTrees", true, "Setting to false will cause no trees to generate.");
		TFCOptions.shouldStripChunks = TFCOptions.getBooleanFor(config, DEBUG_HEADER, "shouldStripChunks", false, "Setting to true will cause all land hexes to generated stripped.");

		/**Always end with this*/
		if (config != null)
			config.save();
	}


	//*****************
	// Private methods
	//*****************
	private void loadTrees()
	{
		TreeRegistry tr = TreeRegistry.instance;
		String treePath = "/assets/tfc2/schematics/trees/";
		log.info("Loading Trees");
		tr.addTreeType(new TreeConfig(WoodType.Ash.getName(), Core.getNaturalLog(WoodType.Ash), Core.getLeaves(WoodType.Ash), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, false, true)); //Ash
		tr.addTreeType(new TreeConfig(WoodType.Aspen.getName(), Core.getNaturalLog(WoodType.Aspen), Core.getLeaves(WoodType.Aspen), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, false, true)); //Aspen
		tr.addTreeType(new TreeConfig(WoodType.Birch.getName(), Core.getNaturalLog(WoodType.Birch), Core.getLeaves(WoodType.Birch), Moisture.LOW, Moisture.MEDIUM, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false, true)); //Birch
		tr.addTreeType(new TreeConfig(WoodType.Chestnut.getName(), Core.getNaturalLog(WoodType.Chestnut), Core.getLeaves(WoodType.Chestnut), Moisture.LOW, Moisture.HIGH, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false, true)); //Chestnut

		tr.addTreeType(new TreeConfig(WoodType.DouglasFir.getName(), Core.getNaturalLog(WoodType.DouglasFir), Core.getLeaves(WoodType.DouglasFir), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, true)); //Douglas Fir
		tr.addTreeType(new TreeConfig(WoodType.Hickory.getName(), Core.getNaturalLog(WoodType.Hickory), Core.getLeaves(WoodType.Hickory), Moisture.LOW, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Hickory
		tr.addTreeType(new TreeConfig(WoodType.Maple.getName(), Core.getNaturalLog(WoodType.Maple), Core.getLeaves(WoodType.Maple), Moisture.LOW, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Maple
		tr.addTreeType(new TreeConfig(WoodType.Oak.getName(), Core.getNaturalLog(WoodType.Oak), Core.getLeaves(WoodType.Oak), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.SUBTROPICAL, false, true)); //Oak

		tr.addTreeType(new TreeConfig(WoodType.Pine.getName(), Core.getNaturalLog(WoodType.Pine), Core.getLeaves(WoodType.Pine), Moisture.LOW, Moisture.VERYHIGH, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, true)); //Pine
		tr.addTreeType(new TreeConfig(WoodType.Sequoia.getName(), Core.getNaturalLog(WoodType.Sequoia), Core.getLeaves(WoodType.Sequoia), Moisture.HIGH, Moisture.MAX, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, true)); //Sequoia
		tr.addTreeType(new TreeConfig(WoodType.Spruce.getName(), Core.getNaturalLog(WoodType.Spruce), Core.getLeaves(WoodType.Spruce), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.SUBTROPICAL, true, true)); //Spruce
		tr.addTreeType(new TreeConfig(WoodType.Sycamore.getName(), Core.getNaturalLog(WoodType.Sycamore), Core.getLeaves(WoodType.Sycamore), Moisture.MEDIUM, Moisture.MAX, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false)); //Sycamore

		tr.addTreeType(new TreeConfig(WoodType.WhiteCedar.getName(), Core.getNaturalLog(WoodType.WhiteCedar), Core.getLeaves(WoodType.WhiteCedar), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.SUBTROPICAL, true)); //White Cedar
		tr.addTreeType(new TreeConfig(WoodType.Willow.getName(), Core.getNaturalLog(WoodType.Willow), Core.getLeaves(WoodType.Willow), Moisture.HIGH, Moisture.MAX, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false, true)); //Willow
		tr.addTreeType(new TreeConfig(WoodType.Kapok.getName(), Core.getNaturalLog(WoodType.Kapok), Core.getLeaves(WoodType.Kapok), Moisture.HIGH, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false, true)); //Kapok
		tr.addTreeType(new TreeConfig(WoodType.Acacia.getName(), Core.getNaturalLog(WoodType.Acacia), Core.getLeaves(WoodType.Acacia), Moisture.LOW, Moisture.LOW, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Acacia Umbrella

		tr.addTreeType(new TreeConfig(WoodType.Rosewood.getName(), Core.getNaturalLog(WoodType.Rosewood), Core.getLeaves(WoodType.Rosewood), Moisture.MEDIUM, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Rosewood
		tr.addTreeType(new TreeConfig(WoodType.Blackwood.getName(), Core.getNaturalLog(WoodType.Blackwood), Core.getLeaves(WoodType.Blackwood), Moisture.LOW, Moisture.VERYHIGH, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Blackwood
		tr.addTreeType(new TreeConfig(WoodType.Palm.getName(), Core.getNaturalLog(WoodType.Palm), Core.getLeaves(WoodType.Palm), Moisture.LOW, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Palm

		for (String s : tr.getTreeNames())
		{
			String tName = Core.textConvert(s);
			TFC.log.info("Registering Tree -> "+s);
			for(int i = 0; i < 3; i++)
			{
				String size = i == 0 ? "small" : i == 1 ? "normal" : "large";
				for(int j = 0; j < 99; j++)
				{
					String p = treePath + tName + "/"+size+"_"+String.format("%02d", j)+".schematic";

					TreeSchematic schem = new TreeSchematic(p, size+"_"+String.format("%02d", j), WoodType.getTypeFromString(s));
					//TFC.log.info(p + " | " + schem.getWoodType().getName());
					if(schem.Load())
					{
						schem.PostProcess();
						TreeRegistry.instance.RegisterSchematic(schem, s);
					}
					else
					{
						//TFC.log.info("Trouble loading "+p);
						break;
					}
				}
			}
		}
	}

	private void loadDungeonSchems()
	{
		log.info("Load Dungeon Schematics-Start");
		DungeonSchemManager dsm = DungeonSchemManager.getInstance();
		try
		{
			Gson gson = new Gson();
			JsonReader reader = new JsonReader(new InputStreamReader(getClass().getResourceAsStream("/assets/tfc2/schematics/dungeons/themes.json")));
			ArrayList<String> themes = new ArrayList<String>();
			reader.beginObject();
			while (reader.hasNext()) 
			{
				String arrayName = reader.nextName();

				if(arrayName.equals("themes"))
				{
					reader.beginArray();
					while (reader.hasNext()) 
					{
						themes.add(reader.nextString());
					}
					reader.endArray();
				}
				log.info("Loaded Dungeon Theme - " + arrayName);
			}

			reader.endObject();
			reader.close();

			for(String themeName : themes)
			{
				gson = new Gson();
				reader = new JsonReader(new InputStreamReader(getClass().getResourceAsStream("/assets/tfc2/schematics/dungeons/themes/"+ themeName +".json")));
				reader.beginObject();
				while (reader.hasNext()) 
				{
					String nextName = reader.nextName();
					if(nextName.equals("roomlist"))
					{
						ArrayList<String> rooms = new ArrayList<String>();
						reader.beginArray();
						while (reader.hasNext()) 
						{
							rooms.add(reader.nextString());
						}
						reader.endArray();
						dsm.loadRooms(themeName, rooms, "/assets/tfc2/schematics/dungeons/"+themeName+"/");
					}
					else if(nextName.equals("canbemaindungeon"))
					{
						dsm.getTheme(themeName).setCanBeMainDungeon(reader.nextBoolean());
					}
					else if(nextName.equals("entrancetype"))
					{
						dsm.getTheme(themeName).setEntranceType(EntranceType.fromString(reader.nextString()));
					}
				}

				reader.endObject();
				reader.close();
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		log.info("Load Dungeon Schematics-Finish");
	}

	private void applyFoodValues(FoodReader reader)
	{
		for(FoodJSON f : reader.foodList)
		{
			ResourceLocation rl = new ResourceLocation(f.itemName);
			Item i = ForgeRegistries.ITEMS.getValue(rl);
			if(i == null)
			{
				log.warn("FoodRegistry -> Item not found when searching ItemRegistry for object ->" + f.itemName);
				continue;
			}
			if(!(i instanceof IFood))
			{
				log.warn("Item ->" + f.itemName + " is not of type IFood");
				continue;
			}
			//IFood food = (IFood)i;
			//food.setExpirationTimer(f.decayTime);
			//food.setFoodGroup(f.foodGroup);

			FoodRegistry.getInstance().registerFood(f);
		}
	}
}
