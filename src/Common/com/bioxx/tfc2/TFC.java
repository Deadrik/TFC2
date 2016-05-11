package com.bioxx.tfc2;

import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import com.bioxx.jmapgen.dungeon.DungeonSchemManager;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.trees.TreeConfig;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.trees.TreeSchematic;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.commands.*;
import com.bioxx.tfc2.core.PortalSchematic;
import com.bioxx.tfc2.networking.client.ClientMapPacket;
import com.bioxx.tfc2.networking.server.KnappingUpdatePacket;
import com.bioxx.tfc2.networking.server.ServerMapRequestPacket;
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
		network.registerMessage(ClientMapPacket.Handler.class, ClientMapPacket.class, 0, Side.CLIENT);
		network.registerMessage(ServerMapRequestPacket.Handler.class, ServerMapRequestPacket.class, 1, Side.SERVER);
		network.registerMessage(KnappingUpdatePacket.Handler.class, KnappingUpdatePacket.class, 2, Side.SERVER);

		//Register tree types and load tree schematics
		loadTrees();

		loadDungeonSchems();

		Core.PortalSchematic = new PortalSchematic("/assets/tfc2/schematics/portal.schematic", "portal");
		Core.PortalSchematic.Load();

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
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent evt)
	{
		evt.registerServerCommand(new PrintImageMapCommand());
		evt.registerServerCommand(new TeleportInIslandCommand());
		evt.registerServerCommand(new RemoveAreaCommand());
		evt.registerServerCommand(new StripChunkCommand());
		evt.registerServerCommand(new RegenChunkCommand());
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
		tr.addTreeType(new TreeConfig(WoodType.Ash.getName(), Core.getNaturalLog(WoodType.Ash), Core.getLeaves(WoodType.Ash), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, false)); //Ash
		tr.addTreeType(new TreeConfig(WoodType.Aspen.getName(), Core.getNaturalLog(WoodType.Aspen), Core.getLeaves(WoodType.Aspen), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, false)); //Aspen
		tr.addTreeType(new TreeConfig(WoodType.Birch.getName(), Core.getNaturalLog(WoodType.Birch), Core.getLeaves(WoodType.Birch), Moisture.LOW, Moisture.MEDIUM, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Birch
		tr.addTreeType(new TreeConfig(WoodType.Chestnut.getName(), Core.getNaturalLog(WoodType.Chestnut), Core.getLeaves(WoodType.Chestnut), Moisture.LOW, Moisture.HIGH, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false)); //Chestnut

		tr.addTreeType(new TreeConfig(WoodType.DouglasFir.getName(), Core.getNaturalLog(WoodType.DouglasFir), Core.getLeaves(WoodType.DouglasFir), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, true)); //Douglas Fir
		tr.addTreeType(new TreeConfig(WoodType.Hickory.getName(), Core.getNaturalLog(WoodType.Hickory), Core.getLeaves(WoodType.Hickory), Moisture.LOW, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Hickory
		tr.addTreeType(new TreeConfig(WoodType.Maple.getName(), Core.getNaturalLog(WoodType.Maple), Core.getLeaves(WoodType.Maple), Moisture.LOW, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Maple
		tr.addTreeType(new TreeConfig(WoodType.Oak.getName(), Core.getNaturalLog(WoodType.Oak), Core.getLeaves(WoodType.Oak), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.SUBTROPICAL, false)); //Oak

		tr.addTreeType(new TreeConfig(WoodType.Pine.getName(), Core.getNaturalLog(WoodType.Pine), Core.getLeaves(WoodType.Pine), Moisture.LOW, Moisture.VERYHIGH, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, true)); //Pine
		tr.addTreeType(new TreeConfig(WoodType.Sequoia.getName(), Core.getNaturalLog(WoodType.Sequoia), Core.getLeaves(WoodType.Sequoia), Moisture.HIGH, Moisture.MAX, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, true)); //Sequoia
		tr.addTreeType(new TreeConfig(WoodType.Spruce.getName(), Core.getNaturalLog(WoodType.Spruce), Core.getLeaves(WoodType.Spruce), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.SUBTROPICAL, true)); //Spruce
		tr.addTreeType(new TreeConfig(WoodType.Sycamore.getName(), Core.getNaturalLog(WoodType.Sycamore), Core.getLeaves(WoodType.Sycamore), Moisture.MEDIUM, Moisture.MAX, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false)); //Sycamore

		tr.addTreeType(new TreeConfig(WoodType.WhiteCedar.getName(), Core.getNaturalLog(WoodType.WhiteCedar), Core.getLeaves(WoodType.WhiteCedar), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.SUBTROPICAL, true)); //White Cedar
		tr.addTreeType(new TreeConfig(WoodType.Willow.getName(), Core.getNaturalLog(WoodType.Willow), Core.getLeaves(WoodType.Willow), Moisture.HIGH, Moisture.MAX, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false)); //Willow
		tr.addTreeType(new TreeConfig(WoodType.Kapok.getName(), Core.getNaturalLog(WoodType.Kapok), Core.getLeaves(WoodType.Kapok), Moisture.HIGH, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Kapok
		tr.addTreeType(new TreeConfig(WoodType.Acacia.getName(), Core.getNaturalLog(WoodType.Acacia), Core.getLeaves(WoodType.Acacia), Moisture.LOW, Moisture.LOW, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Acacia Umbrella

		tr.addTreeType(new TreeConfig(WoodType.Rosewood.getName(), Core.getNaturalLog(WoodType.Rosewood), Core.getLeaves(WoodType.Rosewood), Moisture.MEDIUM, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Rosewood
		tr.addTreeType(new TreeConfig(WoodType.Blackwood.getName(), Core.getNaturalLog(WoodType.Blackwood), Core.getLeaves(WoodType.Blackwood), Moisture.LOW, Moisture.VERYHIGH, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Blackwood
		tr.addTreeType(new TreeConfig(WoodType.Palm.getName(), Core.getNaturalLog(WoodType.Palm), Core.getLeaves(WoodType.Palm), Moisture.LOW, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Palm

		for (String s : tr.getTreeNames())
		{
			String tName = Core.textConvert(s);
			for(int i = 0; i < 3; i++)
			{
				String size = i == 0 ? "Small" : i == 1 ? "Normal" : "Large";
				for(int j = 0; j < 99; j++)
				{
					String p = treePath + tName + "/"+size+"_"+String.format("%02d", j)+".schematic";

					TreeSchematic schem = new TreeSchematic(p, size+"_"+String.format("%02d", j), WoodType.getTypeFromString(s));
					if(schem.Load())
					{
						schem.PostProcess();
						TreeRegistry.instance.RegisterSchematic(schem, s);
					}
					else
						break;
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

			reader.beginObject();
			while (reader.hasNext()) 
			{
				String arrayName = reader.nextName();
				ArrayList<String> roomNames = new ArrayList<String>();
				reader.beginArray();
				while (reader.hasNext()) 
				{
					roomNames.add(reader.nextString());
				}
				reader.endArray();
				dsm.loadRooms(arrayName, roomNames, "/assets/tfc2/schematics/dungeons/");
				log.info("Loaded Dungeon Theme - " + arrayName);
			}

			reader.endObject();

			reader.close();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		log.info("Load Dungeon Schematics-Finish");
	}

}
