package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.blocks.terrain.BlockStone;

public class WorldGenCliffRocks implements IWorldGenerator
{
	IslandMap map;
	public WorldGenCliffRocks()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen,IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 0)
			return;

		chunkX *= 16;
		chunkZ *= 16;

		map = Core.getMapForWorld(world, new BlockPos(chunkX, 0, chunkZ));

		IBlockState stone = TFCBlocks.Stone.getDefaultState().withProperty(BlockStone.META_PROPERTY, map.getParams().getSurfaceRock());
		int count = 10+random.nextInt(10);

		if(map.getParams().hasFeature(Feature.Cliffs))
			count *= 2;

		for(int i = 0; i < count; i++)
		{
			int x = random.nextInt(16);
			int z = random.nextInt(16);

			if(random.nextInt(100) > 10)
				continue;
			BlockPos pos = new BlockPos(chunkX+x, 0, chunkZ+z);
			Center center = map.getClosestCenter(pos);
			int nElev= mcElev(center.getHighestNeighbor().getElevation());
			int elev = mcElev(center.getElevation());
			if((center.hasMarker(Marker.Ocean) && !center.hasMarker(Marker.CoastWater)) || (nElev-elev) < 10)
				continue;

			int posY = world.getHeight(pos).getY();			
			pos = pos.add(0, posY, 0);

			while(!Core.isTerrain(world.getBlockState(pos)))
			{
				pos = pos.down();
				if(pos.getY() <= 0)
				{
					pos = new BlockPos(pos.getX(), 64, pos.getZ());
					break;
				}
			}


			int size = 1+random.nextInt(2);
			BlockPos pos2 = random.nextBoolean() ? pos.add(new BlockPos(1, 0, 0)) : null;
			BlockPos pos3 = random.nextBoolean() ? pos.add(new BlockPos(-1, 0, 0)) : null;
			BlockPos pos4 = random.nextBoolean() ? pos.add(new BlockPos(0, 0, 1)) : null;
			BlockPos pos5 = random.nextBoolean() ? pos.add(new BlockPos(0, 0, -1)) : null;
			for(int y = -2; y <= size; y++)
			{
				world.setBlockState(pos.add(0,y,0), stone, 2);
				if(pos2 != null && y < size)
					world.setBlockState(pos2.add(0,y,0), stone, 2);
				if(pos3 != null && y < size)
					world.setBlockState(pos3.add(0,y,0), stone, 2);
				if(pos4 != null && y < size)
					world.setBlockState(pos4.add(0,y,0), stone, 2);
				if(pos5 != null && y < size)
					world.setBlockState(pos5.add(0,y,0), stone, 2);
			}
		}
	}

	Center getCenterInChunk(IslandMap map, int x, int z)
	{
		Point p = new Point(x, z).toIslandCoord();
		Center c = map.getClosestCenter(p);
		Point p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		p = new Point(x+15, z).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		p = new Point(x, z+15).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		p = new Point(x+15, z+15).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x > 0 && p2.x < 16 && p2.y > 0 && p2.y < 16)
			return c;

		return null;
	}

	private int mcElev(double e)
	{
		return 64 + (int)(e*map.getParams().islandMaxHeight);
	}
}
