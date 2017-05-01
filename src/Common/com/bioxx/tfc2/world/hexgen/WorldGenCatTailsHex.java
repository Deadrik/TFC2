package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.BlockVegetation.VegType;

public class WorldGenCatTailsHex extends WorldGenHex
{
	public WorldGenCatTailsHex()
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

		if(closest != null)
		{
			if(closest.biome == BiomeType.RIVER || closest.biome == BiomeType.LAKESHORE || closest.biome == BiomeType.POND)
			{
				for(int i = 0; i <= 50; i++)
				{
					int _x = -20 + random.nextInt(41);
					int _z = -20 + random.nextInt(41);
					BlockPos pos = new BlockPos(centerX+_x, Core.getHeight(world, centerX+_x, centerZ+_z), centerZ+_z);
					IBlockState stateDown = world.getBlockState(pos.down());
					IBlockState stateDown2 = world.getBlockState(pos.down(2));
					Point p = new Point(centerX+_x, centerZ+_z).toIslandCoord();

					if(p.distanceSq(closest.point) > 400)
						continue;

					if(closest.biome == BiomeType.RIVER && random.nextInt(25) != 0)
						continue;

					if(Core.isWater(stateDown) && Core.isTerrain(stateDown2))
						world.setBlockState(pos, TFCBlocks.Vegetation.getDefaultState().withProperty(BlockVegetation.META_PROPERTY, VegType.Cattail));
				}
			}

			if(closest.biome == BiomeType.MARSH || closest.biome == BiomeType.SWAMP)
			{
				for(int _x = -20; _x <= 20; _x++)
				{
					for(int _z = -20; _z <= 20; _z++)
					{
						BlockPos pos = new BlockPos(centerX+_x, Core.getHeight(world, centerX+_x, centerZ+_z), centerZ+_z);
						IBlockState stateDown = world.getBlockState(pos.down());
						IBlockState stateDown2 = world.getBlockState(pos.down(2));
						Point p = new Point(centerX+_x, centerZ+_z).toIslandCoord();

						if(p.distanceSq(closest.point) > 400)
							continue;

						if(Core.isWater(stateDown) && Core.isTerrain(stateDown2))
							world.setBlockState(pos, TFCBlocks.Vegetation.getDefaultState().withProperty(BlockVegetation.META_PROPERTY, VegType.Cattail));
					}
				}
			}
		}
	}
}
