package com.bioxx.tfc2;

import java.io.File;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import com.bioxx.tfc2.CoreStuff.FluidTFC;
import com.bioxx.tfc2.api.TFCFluids;

public class CommonProxy
{

	public void preInit(FMLPreInitializationEvent event)
	{
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
	}

	public void registerRenderInformation()
	{
		// NOOP on server
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
