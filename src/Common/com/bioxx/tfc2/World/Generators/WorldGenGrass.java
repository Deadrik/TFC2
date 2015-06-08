package com.bioxx.tfc2.World.Generators;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;

public class WorldGenGrass implements IWorldGenerator
{
	public WorldGenGrass()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
		chunkX *= 16;
		chunkZ *= 16;

		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				BlockPos bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
				/*if(world.getBlockState(bp.offsetDown()).getBlock().isSolidFullCube())
				{
					Core.setBlock(world, TFCBlocks.Vegetation, bp);
				}*/
				if(world.getBlockState(bp.offsetDown()).getBlock() == TFCBlocks.Stone || (random.nextInt(5) == 0 && world.getBlockState(bp.offsetDown()).getBlock() == TFCBlocks.Grass))
				{
					Core.setBlock(world, TFCBlocks.Vegetation, bp);
				}
			}
		}

	}

}
