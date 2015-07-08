package com.bioxx.tfc2.CoreStuff;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class FluidTFC extends Fluid
{
	public FluidTFC(String fluidName, ResourceLocation still, ResourceLocation flow) {
		super(fluidName, still, flow);
	}

	private int color = 0xffffff;

	public FluidTFC setBaseColor(int c)
	{
		color = c;
		return this;
	}

	@Override
	public int getColor(FluidStack fs)
	{
		return color;
	}

	@Override
	public int getColor()
	{
		return color;
	}
}
