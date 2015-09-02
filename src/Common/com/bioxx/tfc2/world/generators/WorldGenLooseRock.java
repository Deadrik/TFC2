package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.world.WorldGen;

public class WorldGenLooseRock implements IWorldGenerator
{
	IslandMap map;
	public WorldGenLooseRock()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		chunkX *= 16;
		chunkZ *= 16;
		map = WorldGen.instance.getIslandMap(chunkX >> 12, chunkZ >> 12);
		Center c;
		Point p = new Point(chunkX, chunkZ);
		int chance;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				chance = 50;
				c = map.getClosestCenter(p.plus(x, z));
				BlockPos bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
				if(world.getBlockState(bp).getBlock() != Blocks.air)
				{
					continue;
				}
				if(c.hasAttribute(Attribute.River))
					chance -= 10;
				if(mcElev(c.getHighestNeighbor().getElevation()) - mcElev(c.getElevation()) > 10 )
					chance -= 10;
				if((world.getBlockState(bp.down()).getBlock() == TFCBlocks.Grass && random.nextInt(chance) == 0))
				{
					Core.setBlock(world, TFCBlocks.LooseRocks.getStateFromMeta(map.getParams().getSurfaceRock().getMeta()), bp);
				}
			}
		}

	}

	private int mcElev(double e)
	{
		return 64 + (int)(e*map.getParams().islandMaxHeight);
	}

}
