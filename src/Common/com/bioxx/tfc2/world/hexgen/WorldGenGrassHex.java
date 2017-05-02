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
import com.bioxx.tfc2.blocks.BlockVegDesert;
import com.bioxx.tfc2.blocks.BlockVegDesert.DesertVegType;
import com.bioxx.tfc2.blocks.BlockVegetation;
import com.bioxx.tfc2.blocks.BlockVegetation.VegType;

public class WorldGenGrassHex extends WorldGenHex
{
	public WorldGenGrassHex()
	{

	}

	@Override
	public void generate(Random random, IslandMap map, Center closest, World world) 
	{
		super.generate(random, map, closest, world);
		if(world.provider.getDimension() != 0)
			return;

		IBlockState state = TFCBlocks.Vegetation.getDefaultState();
		Moisture cMoisture;
		float rand, m;
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

					if(world.getBlockState(bp).getBlock() != Blocks.AIR || closest.biome == BiomeType.BEACH)
					{
						continue;
					}

					if(!map.getParams().hasFeature(Feature.Desert) && Core.isStone(world.getBlockState(bp.down())) && random.nextInt(3) == 0)
					{
						Core.setBlock(world, TFCBlocks.VegDesert.getDefaultState().withProperty(BlockVegDesert.META_PROPERTY, DesertVegType.ShortGrassSparse), bp, 2);
					}
					else if(Core.isGrass(world.getBlockState(bp.down())))
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

							VegType vt = VegType.Grass;
							if(iMoisture == Moisture.LOW)
							{
								continue;
							}
							else if(iMoisture == Moisture.MEDIUM)
							{
								rand = random.nextFloat();
								if(rand < 0.25)
									vt = VegType.ShortGrass;
								else if(rand < 0.5) vt = VegType.ShorterGrass;
							}
							else if(iMoisture.isGreaterThanOrEqual(Moisture.HIGH))
							{
								rand = random.nextFloat();
								if(rand < 0.25)
									vt = VegType.ShortGrass;
								else if(rand < 0.35) vt = VegType.ShorterGrass;
							}

							boolean tall = rand > cMoisture.getInverse()*2;

							if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.SUBTROPICAL) && iMoisture.isGreaterThanOrEqual(Moisture.VERYHIGH))
							{
								if(vt == VegType.Grass) vt = VegType.GrassLush;
								else if(vt == VegType.ShortGrass) vt = VegType.ShortGrassLush;
								else if(vt == VegType.ShorterGrass) vt = VegType.ShorterGrassLush;

								if(cMoisture.isGreaterThanOrEqual(Moisture.HIGH))
								{
									if(random.nextFloat() < cMoisture.getMoisture())
									{
										tall = false;
										vt = VegType.Toquilla;
									}
								}
							}

							if(closest.hasMarker(Marker.Clearing) && iMoisture.isGreaterThanOrEqual(Moisture.VERYHIGH) && tall)
							{
								Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassBottomLush), bp, 2);
								Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassTopLush), bp.up(), 2);
							}
							else if(closest.hasMarker(Marker.Clearing) && iMoisture.isGreaterThanOrEqual(Moisture.MEDIUM) && tall)
							{
								Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassBottom), bp, 2);
								Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, VegType.DoubleGrassTop), bp.up(), 2);
							}
							else
							{
								Core.setBlock(world, state.withProperty(BlockVegetation.META_PROPERTY, vt), bp, 2);
							}
						}
					}
				}
			}
		}
	}
}
