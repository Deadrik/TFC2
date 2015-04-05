package com.bioxx.tfc2;

import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import com.bioxx.tfc2.Commands.PrintImageMapCommand;
import com.bioxx.tfc2.Handlers.CreateSpawnHandler;
import com.bioxx.tfc2.Handlers.WorldLoadHandler;
import com.bioxx.tfc2.Networking.PacketPipeline;
import com.bioxx.tfc2.World.WorldProviderSurface;

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

		TFCBlocks.LoadBlocks();
		TFCBlocks.RegisterBlocks();

		/*WorldType.DEFAULT = new WorldType(0, "TFCDefault");
		WorldType.FLAT = new TFCWorldType(1, "TFCFlat");
		WorldType.LARGE_BIOMES = new TFCWorldType(2, "TFCLargeBiomes");
		WorldType.AMPLIFIED = new TFCWorldType(3, "TFCAmplified");*/

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

}
