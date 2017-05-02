package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.blocks.BlockCactus;
import com.bioxx.tfc2.blocks.BlockCactus.DesertCactusType;
import com.bioxx.tfc2.blocks.BlockVegDesert;
import com.bioxx.tfc2.blocks.BlockVegDesert.DesertVegType;

public class WorldGenGrassDryHex extends WorldGenHex
{
	public WorldGenGrassDryHex()
	{

	}

	@Override
	public void generate(Random random, IslandMap map, Center closest, World world) 
	{
		super.generate(random, map, closest, world);
		if(world.provider.getDimension() != 0)
			return;

		IBlockState state = TFCBlocks.VegDesert.getDefaultState();
		Moisture cMoisture;

		float rand, m;

		if(iMoisture != Moisture.LOW)
			return;

		//Place grass
		for(int x = -22; x <= 22; x++)
		{
			for(int z = -22; z < 22; z++)
			{
				Point p = new Point(centerX+x, centerZ+z);
				if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
				{
					BlockPos bp = new BlockPos(p.getX(), 0, p.getZ());
					bp = world.getTopSolidOrLiquidBlock(bp);
					closest = map.getClosestCenter(bp);
					if(world.getBlockState(bp).getBlock() != Blocks.AIR || closest.biome == BiomeType.BEACH)
					{
						continue;
					}

					if(Core.isGrass(world.getBlockState(bp.down())))
					{
						boolean genGrass = false;
						if(closest.biome == BiomeType.MARSH || closest.biome == BiomeType.LAKE || closest.biome == BiomeType.SWAMP)
							genGrass = random.nextFloat() < 0.75;
						else
							genGrass = random.nextFloat() < 0.25;
						if(genGrass)
						{
							cMoisture = closest.getMoisture();
							rand = random.nextFloat();

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

							boolean tall = rand > cMoisture.getInverse()*2;
							if( tall)
							{
								Core.setBlock(world, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.DoubleGrassBottomSparse), bp, 2);
								Core.setBlock(world, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.DoubleGrassTopSparse), bp.up(), 2);
							}
							else
							{
								Core.setBlock(world, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, dvt), bp, 2);
							}
						}
					}
					else if(map.getParams().hasFeature(Feature.Desert) && closest.getMoisture().isLessThanOrEqual(Moisture.MEDIUM) && !closest.hasMarker(Marker.Clearing))//aka we're actually in the desert areas
					{
						IBlockState downState = world.getBlockState(bp.down());
						if(Core.isSand(downState) && random.nextInt(20) == 0)
						{
							world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.SageBrush), 2);
						}
					}
				}
			}
		}

		BlockPos bp;
		if(map.getParams().hasFeature(Feature.Desert))
		{
			if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.SUBTROPICAL) && random.nextInt(7) == 0)
			{
				for(int i = 0; i < 6; i++)
				{
					Point p = new Point(centerX-20+random.nextInt(42), centerZ-20+random.nextInt(42));
					if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
					{
						bp = new BlockPos(p.getX(), 0, p.getZ());
						bp = world.getTopSolidOrLiquidBlock(bp);

						IBlockState downState = world.getBlockState(bp.down());

						if(closest.getMoisture().isLessThanOrEqual(Moisture.MEDIUM) && Core.isSand(downState))//we're actually in the desert areas
						{
							world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Tackweed), 2);
						}
					}
				}
			}

			int count = 3+random.nextInt(6);
			if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.TEMPERATE) && random.nextInt(10) == 0)
			{
				Point p = new Point(centerX-20+random.nextInt(42), centerZ-20+random.nextInt(42));
				for(int i = 0; i < count; i++)
				{
					bp = new BlockPos(p.getX()-4+random.nextInt(9), 0, p.getZ()-4+random.nextInt(9));
					bp = world.getTopSolidOrLiquidBlock(bp);

					IBlockState downState = world.getBlockState(bp.down());

					if(closest.getMoistureRaw() < Moisture.LOW.getMoisture() && Core.isSand(downState))//we're actually in the desert areas
					{
						world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Primrose), 2);
					}
				}
			}

			if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.SUBTROPICAL) && random.nextInt(20) == 0)
			{
				count = 4+random.nextInt(6);
				Point p = new Point(centerX-20+random.nextInt(42), centerZ-20+random.nextInt(42));
				for(int i = 0; i < count; i++)
				{
					bp = new BlockPos(p.getX()-4+random.nextInt(9), 0, p.getZ()-4+random.nextInt(9));
					bp = world.getTopSolidOrLiquidBlock(bp);
					IBlockState downState = world.getBlockState(bp.down());

					if(closest.getMoistureRaw() < Moisture.LOW.getMoisture() && Core.isSand(downState))//we're actually in the desert areas
					{
						world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Yucca), 2);
					}
				}
			}

			if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.SUBTROPICAL) && random.nextInt(5) == 0)
			{
				count = 1+random.nextInt(4);

				for(int i = 0; i < count; i++)
				{
					Point p = new Point(centerX-20+random.nextInt(42), centerZ-20+random.nextInt(42));
					bp = new BlockPos(p.getX(), 0, p.getZ());
					bp = world.getTopSolidOrLiquidBlock(bp);
					IBlockState downState = world.getBlockState(bp.down());

					if(closest.getMoistureRaw() < Moisture.MEDIUM.getMoisture() && Core.isSand(downState))//we're actually in the desert areas
					{
						world.setBlockState(bp, TFCBlocks.Cactus.getDefaultState().withProperty(BlockCactus.META_PROPERTY, DesertCactusType.Barrel), 2);
					}
				}
			}

			count = 3+random.nextInt(6);
			if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.TEMPERATE) && random.nextInt(10) == 0)
			{
				Point p = new Point(centerX-20+random.nextInt(42), centerZ-20+random.nextInt(42));
				for(int i = 0; i < count; i++)
				{
					bp = new BlockPos(p.getX()-4+random.nextInt(9), 0, p.getZ()-4+random.nextInt(9));
					bp = world.getTopSolidOrLiquidBlock(bp);
					IBlockState downState = world.getBlockState(bp.down());
					if(closest.getMoisture().isLessThanOrEqual(Moisture.MEDIUM) && Core.isSand(downState))//we're actually in the desert areas
					{
						world.setBlockState(bp, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.Ocatillo), 2);
					}
				}
			}
		}
	}
}
