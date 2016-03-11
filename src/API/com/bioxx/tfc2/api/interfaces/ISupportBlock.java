package com.bioxx.tfc2.api.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public interface ISupportBlock 
{
	public int getMaxSupportWeight(IBlockState myState);

	public boolean isStructural(IBlockAccess world, BlockPos pos);
}
