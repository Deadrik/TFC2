package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
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
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen, IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 0)
			return;
		chunkX = chunkX * 16;
		chunkZ = chunkZ * 16 ;
		map = WorldGen.getInstance().getIslandMap(chunkX >> 12, chunkZ >> 12);
		Center c;
		Point p = new Point(chunkX, chunkZ);
		int chance, height;
		BlockPos bp;
		Material mat;
		//Standard placement
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				chance = 50;
				c = map.getClosestCenter(p.plus(x, z));
				height = Core.getHeight(world, chunkX+x, chunkZ+z);
				for(int y = height; y > 0; y--)
				{
					chance = 50;
					bp = new BlockPos(chunkX+x, y, chunkZ+z);
					if(!world.isAirBlock(bp))
					{
						continue;
					}
					//if we're on the surface then modify the chance based upon location
					if(y == height)
					{
						if(c.hasAttribute(Attribute.River))
							chance -= 10;
						if(mcElev(c.getHighestNeighbor().getElevation()) - mcElev(c.getElevation()) > 10 )
							chance -= 10;
					}
					else //otherwise only place on stone if underground
					{
						if(world.getBlockState(bp.down()).getMaterial() != Material.ROCK)
							chance = 0;
					}
					mat = world.getBlockState(bp.down()).getMaterial();
					if(chance > 0 && ((mat == Material.GROUND || mat == Material.ROCK) && random.nextInt(chance) == 0) && world.isAirBlock(bp))
					{
						Core.setBlock(world, TFCBlocks.LooseRocks.getStateFromMeta(map.getParams().getSurfaceRock().getMeta()), bp, 2);
					}
				}
			}
		}

	}

	private int mcElev(double e)
	{
		return 64 + (int)(e*map.getParams().islandMaxHeight);
	}

}
