package com.bioxx.tfc2;

import net.minecraft.block.Block;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import org.apache.commons.lang3.text.WordUtils;

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

	public static int getHeight(World world, int worldX, int worldZ)
	{
		Chunk c = world.getChunkFromChunkCoords(worldX >> 4, worldZ >> 4); 
		return c.getHeight(worldX & 15, worldZ & 15);
	}

	/**
	 * Sets the block using setActualState for the given block. Helper method to reduce repeated code usage
	 * @return Returns if the block is successfully set
	 */
	public static boolean setBlock(World world, Block b, BlockPos bp)
	{
		return world.setBlockState(bp, b.getActualState(b.getDefaultState(), world, bp));
	}

	public static String translate(String s)
	{
		return StatCollector.translateToLocal(s);
	}

	public static String textConvert(String s)
	{
		return WordUtils.capitalize(s, '_').replaceAll("_", " ");
	}
}
