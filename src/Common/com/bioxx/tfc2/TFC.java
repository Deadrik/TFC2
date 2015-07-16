package com.bioxx.tfc2;

import java.io.File;
import java.net.URISyntaxException;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.Commands.PrintImageMapCommand;
import com.bioxx.tfc2.Handlers.CreateSpawnHandler;
import com.bioxx.tfc2.Handlers.ServerTickHandler;
import com.bioxx.tfc2.Handlers.WorldLoadHandler;
import com.bioxx.tfc2.Networking.PacketPipeline;
import com.bioxx.tfc2.World.WorldProviderSurface;
import com.bioxx.tfc2.World.Generators.WorldGenGrass;
import com.bioxx.tfc2.World.Generators.WorldGenTreeTest;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.Trees.TreeConfig;
import com.bioxx.tfc2.api.Trees.TreeRegistry;
import com.bioxx.tfc2.api.Trees.TreeSchematic;

@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.ModVersion, dependencies = Reference.ModDependencies)
public class TFC
{
	@Instance("TFC")
	public static TFC instance;

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	// The packet pipeline
	public static final PacketPipeline packetPipeline = new PacketPipeline();

	public TFC() {}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		instance = this;
		proxy.preInit(event);
		//Register tree types and load tree schematics
		loadTrees();

		/*WorldType.DEFAULT = new WorldType(0, "TFCDefault");
		WorldType.FLAT = new TFCWorldType(1, "TFCFlat");
		WorldType.LARGE_BIOMES = new TFCWorldType(2, "TFCLargeBiomes");
		WorldType.AMPLIFIED = new TFCWorldType(3, "TFCAmplified");*/

		GameRegistry.registerWorldGenerator(new WorldGenTreeTest(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenGrass(), 0);

		DimensionManager.unregisterDimension(0);
		DimensionManager.unregisterProviderType(0);
		DimensionManager.registerProviderType(0, WorldProviderSurface.class, true);
		DimensionManager.registerDimension(0, 0);
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Register Packet Handler
		packetPipeline.initalise();

		// Register all the render stuff for the client
		proxy.registerRenderInformation();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		packetPipeline.postInitialise();
		MinecraftForge.EVENT_BUS.register(new CreateSpawnHandler());
		MinecraftForge.EVENT_BUS.register(new WorldLoadHandler());
		FMLCommonHandler.instance().bus().register(new ServerTickHandler());
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
	}

	public void loadSettings()
	{
		Configuration config;
		try
		{
			config = new Configuration(new File(TFC.proxy.getMinecraftDir(), "/config/Options.cfg"));
			config.load();
		} catch (Exception e) {
			System.out.println(new StringBuilder().append("[TFC2] Error while trying to access settings configuration!").toString());
			config = null;
		}
		System.out.println(new StringBuilder().append("[TFC2] Loading Settings").toString());
		/**Start setup here*/
		String GAMEL_HEADER = "Game";
		String ENGINE_HEADER = "Engine";

		//Engine
		TFCOptions.maxThreadsForIslandGen = TFCOptions.getIntFor(config, ENGINE_HEADER, "maxThreadsForIslandGen", 2, "Maximum number of neighboring islands that can be pregenerated at once. Setting this higher may reduce performance.");

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
		String treePath = "assets/tfc2/schematics/trees/";
		int i = 0;

		String wood = Core.textConvert(Global.WOOD_STANDARD[i]);

		tr.addWoodType(new TreeConfig(wood, i, 0.1, 1.0, 4F, 24F, false));i++; //Ash
		tr.addWoodType(new TreeConfig(wood, i, 0.15, 0.5F, -5F, 18F, false));i++; //Aspen
		tr.addWoodType(new TreeConfig(wood, i, 0.1, 0.25, -10F, 12F, false));i++; //Birch
		tr.addWoodType(new TreeConfig(wood, i, 0.1, 0.6, 3F, 24F, false));i++; //Chestnut

		tr.addWoodType(new TreeConfig(wood, i, 0.3, 0.6, 1F, 14F, true));i++; //Douglas Fir
		tr.addWoodType(new TreeConfig(wood, i, 0.1, 0.6, 4F, 24F, false));i++; //Hickory
		tr.addWoodType(new TreeConfig(wood, i, 0.1, 0.6, 3F, 20F, false));i++; //Maple
		tr.addWoodType(new TreeConfig(wood, i, 0.25, 0.6, 5F, 15F, false));i++; //Oak

		tr.addWoodType(new TreeConfig(wood, i, 0.1, 0.5, -15F, 24F, true));i++; //Pine
		tr.addWoodType(new TreeConfig(wood, i, 0.4, 1.0, 10F, 16F, true));i++; //Sequoia
		tr.addWoodType(new TreeConfig(wood, i, 0.1, 1.0, -5F, 24F, true));i++; //Spruce
		tr.addWoodType(new TreeConfig(wood, i, 0.2, 1.0, 6F, 30F, false));i++; //Sycamore

		tr.addWoodType(new TreeConfig(wood, i, 0.1, 1.0, -5F, 24F, true));i++; //White Ceder
		tr.addWoodType(new TreeConfig(wood, i, 0.2, 1.0, 4F, 30F, false));i++; //White Elm
		tr.addWoodType(new TreeConfig(wood, i, 0.5, 1.0, 10F, 30F, false));i++; //Willow
		//tr.addWoodType(new TreeConfiguration(Global.WOOD_STANDARD[i], i, 4000F, 16000F, 24F, 44F, 0F, 1F, false));i++; //Kapok

		//tr.addWoodType(new TreeConfiguration(Global.WOOD_STANDARD[i], i, 75F, 1000F, 20F, 50F, 0F, 1F, false));i++; //Acacia

		try
		{
			for (String s : Global.WOOD_STANDARD)
			{
				String tName = Core.textConvert(s);
				File root = new File(TFC.instance.getClass().getClassLoader().getResource(treePath + tName + "/").toURI());
				for( File f : root.listFiles())
				{
					int index = f.getName().indexOf('-');
					String schemType = f.getName().substring(0, index);
					if(f.isFile())
					{
						TreeSchematic schem = new TreeSchematic(treePath + tName + "/" + f.getName());
						if(schem.Load())
							TreeRegistry.instance.RegisterTree(schem, schemType);
					}
				}
			}
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

}
