package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.BlockVegetation.VegType;

public class WorldGenCatTails extends WorldGenTFC
{
	public WorldGenCatTails()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen,IChunkProvider chunkProvider)
	{
		super.generate(random, chunkX, chunkZ, world, chunkGen, chunkProvider);
		if(world.provider.getDimension() != 0)
			return;

		chunkX *= 16;
		chunkZ *= 16;

		Moisture cMoisture;
		Center closest = this.getCenterInChunk(map, chunkX, chunkZ);
		float rand, m;

		if(closest != null)
		{
			//islands that are more wet will have more clay
			/*if(random.nextInt(100) < 100-(iMoisture.getMoisture()*40))//This will need to be balanced with playtesting in the future
				return;*/

			if(closest.biome == BiomeType.RIVER || closest.biome == BiomeType.LAKESHORE || closest.biome == BiomeType.POND)
			{
				for(int i = 0; i <= 50; i++)
				{
					int _x = -30 + random.nextInt(60);
					int _z = -30 + random.nextInt(60);
					BlockPos pos = new BlockPos(chunkX+_x, Core.getHeight(world, chunkX+_x, chunkZ+_z), chunkZ+_z);
					IBlockState stateDown = world.getBlockState(pos.down());
					IBlockState stateDown2 = world.getBlockState(pos.down(2));
					Point p = new Point(chunkX+_x, chunkZ+_z).toIslandCoord();

					if(p.distance(closest.point) > 20)
						continue;

					if(closest.biome == BiomeType.RIVER && random.nextInt(25) != 0)
						continue;

					if(Core.isWater(stateDown) && Core.isTerrain(stateDown2))
						world.setBlockState(pos, TFCBlocks.Vegetation.getDefaultState().withProperty(BlockVegetation.META_PROPERTY, VegType.Cattail));
				}
			}

			if(closest.biome == BiomeType.MARSH)
			{
				for(int _x = -30; _x <= 30; _x++)
				{
					for(int _z = -30; _z <= 30; _z++)
					{
						BlockPos pos = new BlockPos(chunkX+_x, Core.getHeight(world, chunkX+_x, chunkZ+_z), chunkZ+_z);
						IBlockState stateDown = world.getBlockState(pos.down());
						IBlockState stateDown2 = world.getBlockState(pos.down(2));
						Point p = new Point(chunkX+_x, chunkZ+_z).toIslandCoord();

						if(p.distance(closest.point) > 20)
							continue;

						if(Core.isWater(stateDown) && Core.isTerrain(stateDown2))
							world.setBlockState(pos, TFCBlocks.Vegetation.getDefaultState().withProperty(BlockVegetation.META_PROPERTY, VegType.Cattail));
					}
				}
			}
		}
	}
}
