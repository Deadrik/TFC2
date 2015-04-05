package com.bioxx.tfc2.World;

import jMapGen.Map;
import jMapGen.graph.Center;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;

public class ChunkManager extends WorldChunkManager 
{
	World world;
	public ChunkManager(long seed, WorldType type, String options)
	{
		super(seed, type, options);
	}
	public ChunkManager(World worldIn)
	{
		this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
		world = worldIn;
		WorldGen.initialize(world);
	}

	public BlockPos findTerrainPositionForSpawn(Random random)
	{
		BlockPos blockpos = new BlockPos(0,0,0);
		Map map = WorldGen.instance.getIslandMap(0, 0);
		Center c = map.centers.get(random.nextInt(map.centers.size()));
		for(int i = 0; i < 1000; i++)
		{
			if(c.coast)
			{
				blockpos = new BlockPos((int)c.point.x, 0, (int)c.point.y);
				return blockpos;
			}
			else if(c.elevation > 0)
			{
				c = c.downslope;
			}
			else
			{
				c = map.centers.get(random.nextInt(map.centers.size()));
			}
		}
		return blockpos;
	}
}
