package com.bioxx.tfc2;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ServerOverrides
{
	public static boolean isSoil(World world, BlockPos pos)
	{
		return Core.isSoil(world.getBlockState(pos));
	}

}
