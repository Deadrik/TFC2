package com.bioxx.tfc2.World;

import jMapGen.Map;
import jMapGen.graph.Center;

import java.util.Random;
import java.util.Vector;

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
		Vector<Center> land = map.landCenters(map.centers);
		Center c = land.get(random.nextInt(land.size()));
		for(int i = 0; i < 10000; i++)
		{
			if(c.isCoast())
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
				c = land.get(random.nextInt(land.size()));
			}
		}
		blockpos = new BlockPos((int)c.point.x, 0, (int)c.point.y);
		return blockpos;
	}
}
