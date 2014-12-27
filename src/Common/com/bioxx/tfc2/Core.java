package com.bioxx.tfc2;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class Core 
{
	public static Block getGroundAboveSeaLevel(World world, BlockPos pos)
	{
		BlockPos blockpos1;

		for (blockpos1 = new BlockPos(pos.getX(), 32, pos.getZ()); !world.isAirBlock(blockpos1.offsetUp()); blockpos1 = blockpos1.offsetUp())
		{
			;
		}

		return world.getBlockState(blockpos1).getBlock();
	}
}
