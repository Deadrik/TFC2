package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.BlockVegetation.VegType;

public class WorldGenGrass implements IWorldGenerator
{
	public WorldGenGrass()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(world.provider.getDimensionId() != 0)
			return;

		Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
		chunkX *= 16;
		chunkZ *= 16;

		IBlockState state = TFCBlocks.Vegetation.getDefaultState();
		IslandMap map = Core.getMapForWorld(world, new BlockPos(chunkX, 0, chunkZ));
		Moisture iMoisture = map.getParams().getIslandMoisture();
		Center closest;
		//Place grass
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				BlockPos bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
				closest = map.getClosestCenter(bp);
				if(world.getBlockState(bp).getBlock() != Blocks.air)
				{
					continue;
				}

				if(Core.isStone(world.getBlockState(bp.down())) && random.nextInt(3) == 0)
				{
					Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.Grass1), bp);
				}
				else if(Core.isGrass(world.getBlockState(bp.down())))
				{
					boolean genGrass = false;
					if(closest.biome == BiomeType.MARSH || closest.biome == BiomeType.LAKE)
						genGrass = random.nextInt(2) == 0;
					else
						genGrass = random.nextInt(5) == 0;
					if(genGrass)
					{
						float m = Core.getMoistureFromChunk(c, bp);
						if(random.nextFloat() > closest.getMoisture().getInverse())
						{
							Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassBottom), bp);
							Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassTop), bp.up());
						}
						else
							Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.Grass0), bp);
					}
				}
				else if(Core.isSand(world.getBlockState(bp.down())) && !closest.hasAnyMarkersOf(Marker.Coast, Marker.CoastWater) && 
						map.getParams().hasFeature(Feature.Desert) && random.nextInt(20) == 0)
				{
					world.setBlockState(bp, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DeadBush));
				}
			}
		}

	}

}
