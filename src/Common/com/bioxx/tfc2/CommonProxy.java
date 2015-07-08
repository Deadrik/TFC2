package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.bioxx.tfc2.CoreStuff.FluidTFC;
import com.bioxx.tfc2.api.TFCFluids;

public class CommonProxy
{
	public void registerRenderInformation()
	{
		// NOOP on server
	}

	public void registerFluids()
	{
		TFCFluids.SALTWATER = new FluidTFC("saltwater", new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow")).setBaseColor(0x354d35);
		TFCFluids.FRESHWATER = new FluidTFC("freshwater", new ResourceLocation("blocks/water_still"), new ResourceLocation("blocks/water_flow")).setBaseColor(0x354d35);


		FluidRegistry.registerFluid(TFCFluids.SALTWATER);
		FluidRegistry.registerFluid(TFCFluids.FRESHWATER);
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
