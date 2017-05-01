package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.Moisture;

public class WorldGenClayHex extends WorldGenHex
{
	public WorldGenClayHex()
	{

	}

	@Override
	public void generate(Random random, IslandMap map, Center closest, World world) 
	{
		super.generate(random, map, closest, world);
		if(world.provider.getDimension() != 0)
			return;

		Moisture cMoisture;

		float rand, m;

		int x = 0, y = 0, z = 0; 
		if(closest != null && (closest.biome == BiomeType.RIVER || closest.biome == BiomeType.LAKE || 
				closest.biome == BiomeType.LAKESHORE || closest.biome == BiomeType.POND || closest.biome == BiomeType.MARSH || closest.biome == BiomeType.SWAMP))
		{
			//islands that are more wet will have more clay
			if(random.nextInt(100) < 100-(iMoisture.getMoisture()*40))//This will need to be balanced with playtesting in the future
				return;

			BlockPos bp = null;
			int count = 0;
			while(count < 50)
			{
				Point p = new Point(centerX-20+random.nextInt(42), centerZ-20+random.nextInt(42));
				if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
				{
					bp = new BlockPos(centerX+x, Core.getHeight(world, centerX+x, centerZ+z), centerZ+z).down();
					IBlockState state = world.getBlockState(bp);
					if(Core.isWater(state))
					{
						while(Core.isWater(world.getBlockState(bp)))
						{
							bp = bp.down();
						}
						break;
					}
					count++;
				}
			}

			if(count == 50)
				return;


			for(int _x = -5; _x <= 5; _x++)
			{
				for(int _z = -5; _z <= 5; _z++)
				{
					for(int _y = -1; _y <= 1; _y++)
					{
						BlockPos pos = bp.add(_x, _y, _z);
						IBlockState state = world.getBlockState(pos);

						if(pos.distanceSq(bp) < 25 && (Core.isSoil(state) || Core.isGravel(state)) && !Core.isGrass(state))
							world.setBlockState(pos, Blocks.CLAY.getDefaultState());
					}
				}
			}
		}
	}
}
