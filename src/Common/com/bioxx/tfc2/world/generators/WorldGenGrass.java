package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.blocks.BlockVegDesert;
import com.bioxx.tfc2.blocks.BlockVegDesert.DesertVegType;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.BlockVegetation.VegType;

public class WorldGenGrass implements IWorldGenerator
{
	public WorldGenGrass()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen,IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 0)
			return;

		Chunk c = world.getChunkFromChunkCoords(chunkX, chunkZ);
		chunkX *= 16;
		chunkZ *= 16;

		IBlockState state = TFCBlocks.Vegetation.getDefaultState();
		IslandMap map = Core.getMapForWorld(world, new BlockPos(chunkX, 0, chunkZ));
		Moisture iMoisture = map.getParams().getIslandMoisture();
		Moisture cMoisture;
		Center closest;
		float rand, m;
		//Place grass
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				BlockPos bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
				closest = map.getClosestCenter(bp);
				if(world.getBlockState(bp).getBlock() != Blocks.AIR || closest.biome == BiomeType.BEACH)
				{
					continue;
				}

				if(!map.getParams().hasFeature(Feature.Desert) && Core.isStone(world.getBlockState(bp.down())) && random.nextInt(3) == 0)
				{
					Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.Grass), bp, 2);
				}
				else if(Core.isGrass(world.getBlockState(bp.down())))
				{
					boolean genGrass = false;
					if(closest.biome == BiomeType.MARSH || closest.biome == BiomeType.LAKE)
						genGrass = random.nextFloat() < 0.75;
					else
						genGrass = random.nextFloat() < 0.25;
					if(genGrass)
					{
						cMoisture = closest.getMoisture();
						rand = random.nextFloat();

						VegType vt = VegType.Grass;
						DesertVegType dvt = DesertVegType.GrassSparse;
						if(iMoisture == Moisture.LOW)
						{
							if(closest.getMoisture().isLessThanOrEqual(Moisture.MEDIUM))
							{
								if(random.nextFloat() < 0.5)
									dvt = DesertVegType.ShortGrassSparse;
								else dvt = DesertVegType.ShorterGrassSparse;
							}
						}
						else if(iMoisture == Moisture.MEDIUM)
						{
							rand = random.nextFloat();
							if(rand < 0.25)
								vt = VegType.ShortGrass;
							else if(rand < 0.5) vt = VegType.ShorterGrass;
						}
						else if(iMoisture == Moisture.HIGH)
						{
							rand = random.nextFloat();
							if(rand < 0.25)
								vt = VegType.ShortGrass;
							else if(rand < 0.35) vt = VegType.ShorterGrass;
						}

						boolean tall = rand > cMoisture.getInverse()*2;
						if(iMoisture.isGreaterThanOrEqual(Moisture.VERYHIGH) && tall)
						{
							Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassBottomLush), bp, 2);
							Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassTopLush), bp.up(), 2);
						}
						else if(iMoisture.isGreaterThan(Moisture.MEDIUM) && tall)
						{
							Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassBottom), bp, 2);
							Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassTop), bp.up(), 2);
						}
						else if(iMoisture.isGreaterThanOrEqual(Moisture.LOW) && tall)
						{
							Core.setBlock(world, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.DoubleGrassBottomSparse), bp, 2);
							Core.setBlock(world, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.DoubleGrassTopSparse), bp.up(), 2);
						}
						else
						{
							if(iMoisture == Moisture.LOW)
								Core.setBlock(world, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, dvt), bp, 2);
							else
								Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, vt), bp, 2);
						}
					}
				}
				else if(map.getParams().hasFeature(Feature.Desert) && closest.getMoisture().isLessThanOrEqual(Moisture.MEDIUM))//aka we're actually in the desert areas
				{
					IBlockState downState = world.getBlockState(bp.down());
					if(Core.isSand(downState) && random.nextInt(20) == 0)
					{
						world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Ocatillo), 2);
					}
				}
			}
		}

		int x = 0, y = 0, z = 0; 
		if(map.getParams().hasFeature(Feature.Desert))
		{
			for(int i = 0; i < 4; i++)
			{
				x = random.nextInt(16); z = random.nextInt(16);
				BlockPos bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
				closest = map.getClosestCenter(bp);
				IBlockState downState = world.getBlockState(bp.down());

				if(closest.getMoisture().isLessThanOrEqual(Moisture.MEDIUM) && Core.isSand(downState))//we're actually in the desert areas
				{
					world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Tackweed), 2);
				}
			}
			int count = 3+random.nextInt(6);
			if(random.nextInt(10) == 0)
			{
				x = random.nextInt(16); z = random.nextInt(16);
				for(int i = 0; i < count; i++)
				{
					x += -4+random.nextInt(9); z += -4+random.nextInt(9);
					BlockPos bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
					closest = map.getClosestCenter(bp);
					IBlockState downState = world.getBlockState(bp.down());

					if(closest.getMoistureRaw() < Moisture.LOW.getMoisture() && Core.isSand(downState))//we're actually in the desert areas
					{
						world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Primrose), 2);
					}
				}
			}

			if(random.nextInt(20) == 0)
			{
				x = random.nextInt(16); z = random.nextInt(16);
				for(int i = 0; i < count; i++)
				{
					x += -4+random.nextInt(9); z += -4+random.nextInt(9);
					BlockPos bp = new BlockPos(chunkX+x, Core.getHeight(world, chunkX+x, chunkZ+z), chunkZ+z);
					closest = map.getClosestCenter(bp);
					IBlockState downState = world.getBlockState(bp.down());

					if(closest.getMoistureRaw() < Moisture.LOW.getMoisture() && Core.isSand(downState))//we're actually in the desert areas
					{
						world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Yucca), 2);
					}
				}
			}
		}

	}
}
