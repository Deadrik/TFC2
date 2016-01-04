package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
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
		if(world.provider.getDimensionId() != 0)
			return;
		chunkX *= 16;
		chunkZ *= 16;
		map = WorldGen.instance.getIslandMap(chunkX >> 12, chunkZ >> 12);
		Center c;
		Point p = new Point(chunkX, chunkZ);
		int chance;
		BlockPos bp;
		//Standard placement
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				int elev = Core.getHeight(world, chunkX+x, chunkZ+z);
				int elevN = z-1 >= 0 ? (Core.getHeight(world, chunkX+x, chunkZ+z-1) - elev) : 0;
				int elevS = z+1 < 16 ? (Core.getHeight(world, chunkX+x, chunkZ+z+1) - elev) : 0;
				int elevE = x+1 < 16 ? (Core.getHeight(world, chunkX+x+1, chunkZ+z) - elev) : 0;
				int elevW = x-1 >= 0 ? (Core.getHeight(world, chunkX+x-1, chunkZ+z) - elev) : 0;

				chance = 50;
				c = map.getClosestCenter(p.plus(x, z));
				bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
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

				int e = elevN;
				EnumFacing facing = EnumFacing.NORTH;

				if(elevS > e || (elevS == e && random.nextBoolean()))
				{e = elevS; facing = EnumFacing.SOUTH;}
				if(elevE > e || (elevE == e && random.nextBoolean()))
				{e = elevE; facing = EnumFacing.EAST;}
				if(elevW > e || (elevW == e && random.nextBoolean()))
				{e = elevW; facing = EnumFacing.WEST;}

				if(e > 2)
				{
					for(int i = 1; i < e; i++)
					{
						if(random.nextFloat() < 0.3)
						{
							bp = new BlockPos(chunkX+x, elev+i, chunkZ+z);
							if(Core.isStone(world.getBlockState(bp.offset(facing))))
								Core.setBlock(world, TFCBlocks.ClimbingRocks.getStateFromMeta(map.getParams().getSurfaceRock().getMeta()), bp);
						}
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
