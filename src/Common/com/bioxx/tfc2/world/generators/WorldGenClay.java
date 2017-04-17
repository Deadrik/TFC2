package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.Moisture;

public class WorldGenClay extends WorldGenTFC
{
	public WorldGenClay()
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

		int x = 0, y = 0, z = 0; 
		if(closest != null && (closest.biome == BiomeType.RIVER || closest.biome == BiomeType.LAKE || closest.biome == BiomeType.LAKESHORE || closest.biome == BiomeType.POND || closest.biome == BiomeType.MARSH))
		{
			//islands that are more wet will have more clay
			if(random.nextInt(100) < 100-(iMoisture.getMoisture()*40))//This will need to be balanced with playtesting in the future
				return;

			BlockPos bp = null;
			int count = 0;
			while(count < 50)
			{
				x = random.nextInt(16); z = random.nextInt(16);
				bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z).down();
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
