package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.trees.TreeConfig;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.trees.TreeSchematic;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.WoodType;
import com.bioxx.tfc2.blocks.BlockLeaves;
import com.bioxx.tfc2.blocks.BlockLeaves2;
import com.bioxx.tfc2.blocks.BlockLogNatural;
import com.bioxx.tfc2.blocks.BlockLogNatural2;
import com.bioxx.tfc2.commands.PrintImageMapCommand;
import com.bioxx.tfc2.commands.RemoveAreaCommand;
import com.bioxx.tfc2.commands.StripChunkCommand;
import com.bioxx.tfc2.commands.TeleportInIslandCommand;
import com.bioxx.tfc2.networking.PacketPipeline;
import com.bioxx.tfc2.world.WorldGen;

@Mod(modid = Reference.ModID, name = Reference.ModName, version = Reference.ModVersion, dependencies = Reference.ModDependencies)
public class TFC
{
	@Instance("TFC")
	public static TFC instance;

	public static Logger log = LogManager.getLogger("TFC");

	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;

	// The packet pipeline
	public static final PacketPipeline packetPipeline = new PacketPipeline();

	public TFC() {}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		instance = this;
		loadSettings();
		proxy.preInit(event);
		//Register tree types and load tree schematics
		loadTrees();

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Register Packet Handler
		packetPipeline.initalise();
		proxy.init(event);

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		packetPipeline.postInitialise();
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
	}

	@EventHandler
	public void serverStarting(FMLServerStoppingEvent evt)
	{
		if(WorldGen.instance != null)
		{
			WorldGen.instance.resetCache();
			WorldGen.instance = null;
		}
	}

	public void loadSettings()
	{
		Configuration config;
		try
		{
			config = new Configuration(new File(TFC.proxy.getMinecraftDir(), "/config/Options.cfg"));
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
		tr.addTreeType(new TreeConfig(WoodType.Ash.getName(), getNaturalLog(WoodType.Ash), getLeaves(WoodType.Ash), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, false)); //Ash
		tr.addTreeType(new TreeConfig(WoodType.Aspen.getName(), getNaturalLog(WoodType.Aspen), getLeaves(WoodType.Aspen), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, false)); //Aspen
		tr.addTreeType(new TreeConfig(WoodType.Birch.getName(), getNaturalLog(WoodType.Birch), getLeaves(WoodType.Birch), Moisture.LOW, Moisture.MEDIUM, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Birch
		tr.addTreeType(new TreeConfig(WoodType.Chestnut.getName(), getNaturalLog(WoodType.Chestnut), getLeaves(WoodType.Chestnut), Moisture.LOW, Moisture.HIGH, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false)); //Chestnut

		tr.addTreeType(new TreeConfig(WoodType.DouglasFir.getName(), getNaturalLog(WoodType.DouglasFir), getLeaves(WoodType.DouglasFir), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, true)); //Douglas Fir
		tr.addTreeType(new TreeConfig(WoodType.Hickory.getName(), getNaturalLog(WoodType.Hickory), getLeaves(WoodType.Hickory), Moisture.LOW, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Hickory
		tr.addTreeType(new TreeConfig(WoodType.Maple.getName(), getNaturalLog(WoodType.Maple), getLeaves(WoodType.Maple), Moisture.LOW, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, false)); //Maple
		tr.addTreeType(new TreeConfig(WoodType.Oak.getName(), getNaturalLog(WoodType.Oak), getLeaves(WoodType.Oak), Moisture.MEDIUM, Moisture.HIGH, ClimateTemp.SUBPOLAR, ClimateTemp.SUBTROPICAL, false)); //Oak

		tr.addTreeType(new TreeConfig(WoodType.Pine.getName(), getNaturalLog(WoodType.Pine), getLeaves(WoodType.Pine), Moisture.LOW, Moisture.VERYHIGH, ClimateTemp.POLAR, ClimateTemp.TEMPERATE, true)); //Pine
		tr.addTreeType(new TreeConfig(WoodType.Sequoia.getName(), getNaturalLog(WoodType.Sequoia), getLeaves(WoodType.Sequoia), Moisture.HIGH, Moisture.MAX, ClimateTemp.SUBPOLAR, ClimateTemp.TEMPERATE, true)); //Sequoia
		tr.addTreeType(new TreeConfig(WoodType.Spruce.getName(), getNaturalLog(WoodType.Spruce), getLeaves(WoodType.Spruce), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.SUBTROPICAL, true)); //Spruce
		tr.addTreeType(new TreeConfig(WoodType.Sycamore.getName(), getNaturalLog(WoodType.Sycamore), getLeaves(WoodType.Sycamore), Moisture.MEDIUM, Moisture.MAX, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false)); //Sycamore

		tr.addTreeType(new TreeConfig(WoodType.WhiteCedar.getName(), getNaturalLog(WoodType.WhiteCedar), getLeaves(WoodType.WhiteCedar), Moisture.LOW, Moisture.MAX, ClimateTemp.POLAR, ClimateTemp.SUBTROPICAL, true)); //White Cedar
		tr.addTreeType(new TreeConfig(WoodType.Willow.getName(), getNaturalLog(WoodType.Willow), getLeaves(WoodType.Willow), Moisture.HIGH, Moisture.MAX, ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, false)); //Willow
		tr.addTreeType(new TreeConfig(WoodType.Kapok.getName(), getNaturalLog(WoodType.Kapok), getLeaves(WoodType.Kapok), Moisture.HIGH, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Kapok
		tr.addTreeType(new TreeConfig(WoodType.Acacia.getName(), getNaturalLog(WoodType.Acacia), getLeaves(WoodType.Acacia), Moisture.LOW, Moisture.LOW, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Acacia Umbrella

		tr.addTreeType(new TreeConfig(WoodType.Rosewood.getName(), getNaturalLog(WoodType.Rosewood), getLeaves(WoodType.Rosewood), Moisture.MEDIUM, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Rosewood
		tr.addTreeType(new TreeConfig(WoodType.Blackwood.getName(), getNaturalLog(WoodType.Blackwood), getLeaves(WoodType.Blackwood), Moisture.LOW, Moisture.VERYHIGH, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Blackwood
		tr.addTreeType(new TreeConfig(WoodType.Palm.getName(), getNaturalLog(WoodType.Palm), getLeaves(WoodType.Palm), Moisture.LOW, Moisture.MAX, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL, false)); //Palm

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
						TreeRegistry.instance.RegisterSchematic(schem, s);
					else
						break;
				}
			}
		}
	}

	private IBlockState getNaturalLog(WoodType w)
	{
		if(w.getMeta() >= 16)
			return TFCBlocks.LogNatural2.getDefaultState().withProperty(BlockLogNatural2.META_PROPERTY, w);
		return TFCBlocks.LogNatural.getDefaultState().withProperty(BlockLogNatural.META_PROPERTY, w);
	}

	private IBlockState getLeaves(WoodType w)
	{
		if(w.getMeta() >= 16)
			return TFCBlocks.Leaves2.getDefaultState().withProperty(BlockLeaves2.META_PROPERTY, w);
		return TFCBlocks.Leaves.getDefaultState().withProperty(BlockLeaves.META_PROPERTY, w);
	}

}
