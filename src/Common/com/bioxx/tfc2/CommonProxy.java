package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.bioxx.tfc2.api.TFCFluids;
import com.bioxx.tfc2.api.ore.OreConfig;
import com.bioxx.tfc2.api.ore.OreConfig.VeinType;
import com.bioxx.tfc2.api.ore.OreRegistry;
import com.bioxx.tfc2.api.types.OreType;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.FluidTFC;
import com.bioxx.tfc2.handlers.CreateSpawnHandler;
import com.bioxx.tfc2.handlers.ServerTickHandler;
import com.bioxx.tfc2.handlers.WorldLoadHandler;
import com.bioxx.tfc2.world.WorldProviderSurface;
import com.bioxx.tfc2.world.generators.WorldGenGrass;
import com.bioxx.tfc2.world.generators.WorldGenTreeTest;

public class CommonProxy
{

	public void preInit(FMLPreInitializationEvent event)
	{
		GameRegistry.registerWorldGenerator(new WorldGenTreeTest(), 0);
		GameRegistry.registerWorldGenerator(new WorldGenGrass(), 0);

		DimensionManager.unregisterDimension(0);
		DimensionManager.unregisterProviderType(0);
		DimensionManager.registerProviderType(0, WorldProviderSurface.class, true);
		DimensionManager.registerDimension(0, 0);

		ResourceLocation still = Core.CreateRes(Reference.getResID()+"blocks/water_still");
		ResourceLocation flow = Core.CreateRes(Reference.getResID()+"blocks/water_flow");
		TFCFluids.SALTWATER = new FluidTFC("saltwater", still, flow).setBaseColor(0xff001945);
		TFCFluids.FRESHWATER = new FluidTFC("freshwater", still, flow).setBaseColor(0xff001945);
		FluidRegistry.registerFluid(TFCFluids.SALTWATER);
		FluidRegistry.registerFluid(TFCFluids.FRESHWATER);
		TFCBlocks.LoadBlocks();
		TFCBlocks.RegisterBlocks();
		TFCFluids.SALTWATER.setBlock(TFCBlocks.SaltWater).setUnlocalizedName(TFCBlocks.SaltWater.getUnlocalizedName());
		TFCFluids.FRESHWATER.setBlock(TFCBlocks.FreshWater).setUnlocalizedName(TFCBlocks.FreshWater.getUnlocalizedName());

		setupOre();
	}

	public void init(FMLInitializationEvent event)
	{

	}

	public void postInit(FMLPostInitializationEvent event)
	{
		MinecraftForge.EVENT_BUS.register(new CreateSpawnHandler());
		MinecraftForge.EVENT_BUS.register(new WorldLoadHandler());
		FMLCommonHandler.instance().bus().register(new ServerTickHandler());
	}

	protected void setupOre()
	{
		OreRegistry.getInstance().registerOre(OreType.Bismuthinite.getName(), new OreConfig(VeinType.Seam, TFCBlocks.Ore, OreType.Bismuthinite, 1, 4, 1, 2), StoneType.getForSubTypes(StoneType.SubType.Metamorphic, StoneType.SubType.Sedimentary));
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
}
