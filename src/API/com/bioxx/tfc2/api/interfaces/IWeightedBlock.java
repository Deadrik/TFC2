package com.bioxx.tfc2.api.interfaces;

import net.minecraft.block.state.IBlockState;

public interface IWeightedBlock 
{
	public int getWeight(IBlockState myState);
}
