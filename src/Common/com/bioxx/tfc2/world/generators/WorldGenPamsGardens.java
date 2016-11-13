package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;

public class WorldGenPamsGardens implements IWorldGenerator
{
	public WorldGenPamsGardens()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen,IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 0)
			return;

		if(random.nextInt(10) != 0)
			return;

		int numToGen = 5;

		Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
		chunkX *= 16;
		chunkZ *= 16;

		IBlockState state = TFCBlocks.Vegetation.getDefaultState();
		IslandMap map = Core.getMapForWorld(world, new BlockPos(chunkX, 0, chunkZ));
		Moisture iMoisture = map.getParams().getIslandMoisture();
		Moisture cMoisture;
		Center closest;
		float rand, m;
		BlockPos pos;
		boolean placed;

		Block aridGarden = Block.getBlockFromName("harvestcraft:aridgarden");
		Block frostGarden = Block.getBlockFromName("harvestcraft:frostgarden");
		Block soggyGarden = Block.getBlockFromName("harvestcraft:soggygarden");
		Block shadedGarden = Block.getBlockFromName("harvestcraft:shadedgarden");
		Block tropicalGarden = Block.getBlockFromName("harvestcraft:tropicalgarden");
		Block windyGarden = Block.getBlockFromName("harvestcraft:windygarden");

		for(int i = 0; i < numToGen; i++)
		{
			placed = false;
			pos = new BlockPos(chunkX+random.nextInt(16), 0, chunkZ+random.nextInt(16));
			pos = world.getTopSolidOrLiquidBlock(pos);
			closest = map.getClosestCenter(pos);
			cMoisture = closest.getMoisture();

			if(!placed && map.getParams().getIslandTemp().isGreaterThan(ClimateTemp.POLAR) && 
					map.getParams().getIslandTemp().isLessThan(ClimateTemp.TROPICAL) &&
					iMoisture.isGreaterThan(Moisture.LOW) && shadedGarden != null)
			{
				if(cMoisture.isGreaterThan(Moisture.MEDIUM) && shadedGarden.canPlaceBlockAt(world, pos))
				{
					world.setBlockState(pos, shadedGarden.getDefaultState(), 2);
					placed = true;
				}
			}

			if(!placed && map.getParams().getIslandTemp().isGreaterThan(ClimateTemp.SUBPOLAR) && 
					map.getParams().getIslandTemp().isLessThan(ClimateTemp.TROPICAL) &&
					iMoisture.isLessThan(Moisture.HIGH) && windyGarden != null)
			{
				if(cMoisture.isLessThan(Moisture.MEDIUM) && windyGarden.canPlaceBlockAt(world, pos) && world.isAirBlock(pos))
				{
					world.setBlockState(pos, windyGarden.getDefaultState(), 2);
					placed = true;
				}
			}

			if(!placed && map.getParams().getIslandTemp().isGreaterThanOrEqual(ClimateTemp.SUBTROPICAL) && 
					iMoisture.isLessThanOrEqual(Moisture.LOW) && aridGarden != null)
			{
				if(cMoisture.isGreaterThan(Moisture.MEDIUM) && aridGarden.canPlaceBlockAt(world, pos))
				{
					world.setBlockState(pos, aridGarden.getDefaultState(), 2);
					placed = true;
				}
			}

			if(!placed && map.getParams().getIslandTemp().isGreaterThanOrEqual(ClimateTemp.SUBPOLAR) && 
					closest.biome == BiomeType.MARSH && soggyGarden != null)
			{
				if(soggyGarden.canPlaceBlockAt(world, pos))
				{
					world.setBlockState(pos, soggyGarden.getDefaultState(), 2);
					placed = true;
				}
			}

			if(!placed && map.getParams().getIslandTemp().isGreaterThanOrEqual(ClimateTemp.TROPICAL) && 
					iMoisture.isGreaterThan(Moisture.MEDIUM) && tropicalGarden != null)
			{
				if(cMoisture.isGreaterThan(Moisture.MEDIUM) && tropicalGarden.canPlaceBlockAt(world, pos))
				{
					world.setBlockState(pos, tropicalGarden.getDefaultState(), 2);
					placed = true;
				}
			}

			if(!placed && map.getParams().getIslandTemp().isLessThanOrEqual(ClimateTemp.POLAR) &&
					iMoisture.isGreaterThan(Moisture.LOW) && frostGarden != null)
			{
				if(cMoisture.isGreaterThan(Moisture.LOW) && frostGarden.canPlaceBlockAt(world, pos))
				{
					world.setBlockState(pos, frostGarden.getDefaultState(), 2);
					placed = true;
				}
			}
		}

	}
}
