package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.blocks.terrain.BlockStone;

public class WorldGenCliffRocksHex extends WorldGenHex
{

	@Override
	public void generate(Random random, IslandMap map, Center closest, World world) 
	{
		super.generate(random, map, closest, world);
		if(world.provider.getDimension() != 0)
			return;

		IBlockState stone = TFCBlocks.Stone.getDefaultState().withProperty(BlockStone.META_PROPERTY, map.getParams().getSurfaceRock());
		int count = 10+random.nextInt(10);

		if(map.getParams().hasFeature(Feature.Cliffs))
			count *= 2;

		int nElev= mcElev(closest.getHighestNeighbor().getElevation());
		int elev = mcElev(closest.getElevation());
		if((closest.hasMarker(Marker.Ocean) && !closest.hasMarker(Marker.CoastWater)) || (nElev-elev) < 10)
			return;

		for(int i = 0; i < count; i++)
		{
			Point p = new Point(centerX-15+random.nextInt(32), centerZ-15+random.nextInt(32));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				if(random.nextInt(100) > 30)
					continue;
				BlockPos pos = new BlockPos(p.getX(), 0, p.getZ());
				pos = world.getTopSolidOrLiquidBlock(pos).add(0, -1, 0);

				if(Core.isTerrain(world.getBlockState(pos)))
				{
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
		}
	}

	private int mcElev(double e)
	{
		return Global.SEALEVEL + (int)(e*map.getParams().islandMaxHeight);
	}
}
