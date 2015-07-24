package com.bioxx.tfc2;

import java.io.File;
import java.net.URISyntaxException;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.bioxx.tfc2.Blocks.BlockLeaves;
import com.bioxx.tfc2.Blocks.BlockLogNatural;
import com.bioxx.tfc2.Commands.PrintImageMapCommand;
import com.bioxx.tfc2.Networking.PacketPipeline;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.Trees.TreeConfig;
import com.bioxx.tfc2.api.Trees.TreeRegistry;
import com.bioxx.tfc2.api.Trees.TreeSchematic;
import com.bioxx.tfc2.api.Types.Moisture;
import com.bioxx.tfc2.api.Types.Temp;
import com.bioxx.tfc2.api.Types.WoodType;

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

	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		// Register Packet Handler
		packetPipeline.initalise();
		proxy.init(event);
		//Register tree types and load tree schematics
		loadTrees();
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

		//Engine
		TFCOptions.maxThreadsForIslandGen = TFCOptions.getIntFor(config, ENGINE_HEADER, "maxThreadsForIslandGen", 1, "Maximum number of neighboring islands that can be pregenerated at once. Setting this higher may reduce performance.");

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
		log.info("Loading Trees");
		tr.addTreeType(new TreeConfig(WoodType.Ash.getName(), getNaturalLog(WoodType.Ash), getLeaves(WoodType.Ash), Moisture.LOW, Moisture.MAX, Temp.POLAR, Temp.TEMPERATE, false)); //Ash
		tr.addTreeType(new TreeConfig(WoodType.Aspen.getName(), getNaturalLog(WoodType.Aspen), getLeaves(WoodType.Aspen), Moisture.MEDIUM, Moisture.HIGH, Temp.POLAR, Temp.TEMPERATE, false)); //Aspen
		tr.addTreeType(new TreeConfig(WoodType.Birch.getName(), getNaturalLog(WoodType.Birch), getLeaves(WoodType.Birch), Moisture.LOW, Moisture.MEDIUM, Temp.SUBPOLAR, Temp.TEMPERATE, false)); //Birch
		tr.addTreeType(new TreeConfig(WoodType.Chestnut.getName(), getNaturalLog(WoodType.Chestnut), getLeaves(WoodType.Chestnut), Moisture.LOW, Moisture.HIGH, Temp.TEMPERATE, Temp.SUBTROPICAL, false)); //Chestnut

		tr.addTreeType(new TreeConfig(WoodType.DouglasFir.getName(), getNaturalLog(WoodType.DouglasFir), getLeaves(WoodType.DouglasFir), Moisture.MEDIUM, Moisture.HIGH, Temp.SUBPOLAR, Temp.TEMPERATE, true)); //Douglas Fir
		tr.addTreeType(new TreeConfig(WoodType.Hickory.getName(), getNaturalLog(WoodType.Hickory), getLeaves(WoodType.Hickory), Moisture.LOW, Moisture.HIGH, Temp.SUBPOLAR, Temp.TEMPERATE, false)); //Hickory
		tr.addTreeType(new TreeConfig(WoodType.Maple.getName(), getNaturalLog(WoodType.Maple), getLeaves(WoodType.Maple), Moisture.LOW, Moisture.HIGH, Temp.SUBPOLAR, Temp.TEMPERATE, false)); //Maple
		tr.addTreeType(new TreeConfig(WoodType.Oak.getName(), getNaturalLog(WoodType.Oak), getLeaves(WoodType.Oak), Moisture.MEDIUM, Moisture.HIGH, Temp.SUBPOLAR, Temp.SUBTROPICAL, false)); //Oak

		tr.addTreeType(new TreeConfig(WoodType.Pine.getName(), getNaturalLog(WoodType.Pine), getLeaves(WoodType.Pine), Moisture.LOW, Moisture.VERYHIGH, Temp.POLAR, Temp.TEMPERATE, true)); //Pine
		tr.addTreeType(new TreeConfig(WoodType.Sequoia.getName(), getNaturalLog(WoodType.Sequoia), getLeaves(WoodType.Sequoia), Moisture.HIGH, Moisture.MAX, Temp.SUBPOLAR, Temp.TEMPERATE, true)); //Sequoia
		tr.addTreeType(new TreeConfig(WoodType.Spruce.getName(), getNaturalLog(WoodType.Spruce), getLeaves(WoodType.Spruce), Moisture.LOW, Moisture.MAX, Temp.POLAR, Temp.SUBTROPICAL, true)); //Spruce
		tr.addTreeType(new TreeConfig(WoodType.Sycamore.getName(), getNaturalLog(WoodType.Sycamore), getLeaves(WoodType.Sycamore), Moisture.MEDIUM, Moisture.MAX, Temp.TEMPERATE, Temp.SUBTROPICAL, false)); //Sycamore

		tr.addTreeType(new TreeConfig(WoodType.WhiteCedar.getName(), getNaturalLog(WoodType.WhiteCedar), getLeaves(WoodType.WhiteCedar), Moisture.LOW, Moisture.MAX, Temp.POLAR, Temp.SUBTROPICAL, true)); //White Cedar
		tr.addTreeType(new TreeConfig(WoodType.Willow.getName(), getNaturalLog(WoodType.Willow), getLeaves(WoodType.Willow), Moisture.HIGH, Moisture.MAX, Temp.TEMPERATE, Temp.SUBTROPICAL, false)); //Willow
		tr.addTreeType(new TreeConfig(WoodType.Kapok.getName(), getNaturalLog(WoodType.Kapok), getLeaves(WoodType.Kapok), Moisture.HIGH, Moisture.MAX, Temp.SUBTROPICAL, Temp.TROPICAL, false)); //Kapok
		tr.addTreeType(new TreeConfig(WoodType.Acacia.getName(), getNaturalLog(WoodType.Acacia), getLeaves(WoodType.Acacia), Moisture.NONE, Moisture.VERYHIGH, Temp.SUBTROPICAL, Temp.TROPICAL, false)); //Acacia

		tr.addTreeType(new TreeConfig("acacia_umbrella", getNaturalLog(WoodType.Acacia), getLeaves(WoodType.Acacia), Moisture.NONE, Moisture.LOW, Temp.TEMPERATE, Temp.TROPICAL, false)); //Acacia Umbrella

		try
		{
			for (String s : tr.getTreeNames())
			{
				String tName = Core.textConvert(s);
				File root = new File(TFC.instance.getClass().getClassLoader().getResource(treePath + tName + "/").toURI());
				for( File f : root.listFiles())
				{
					if(f.isFile())
					{
						TreeSchematic schem = new TreeSchematic(treePath + tName + "/" + f.getName());
						if(schem.Load())
							TreeRegistry.instance.RegisterSchematic(schem, s);
					}
				}
			}
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	private IBlockState getNaturalLog(WoodType w)
	{
		return TFCBlocks.LogNatural.getDefaultState().withProperty(BlockLogNatural.META_PROPERTY, w);
	}

	private IBlockState getLeaves(WoodType w)
	{
		return TFCBlocks.Leaves.getDefaultState().withProperty(BlockLeaves.META_PROPERTY, w);
	}

}
