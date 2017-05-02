package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;

public class WorldGenPamsGardensHex extends WorldGenHex
{
	public WorldGenPamsGardensHex()
	{

	}

	@Override
	public void generate(Random random, IslandMap map, Center closest, World world) 
	{
		super.generate(random, map, closest, world);
		if(world.provider.getDimension() != 0)
			return;

		int numToGen = 5;


		IBlockState state = TFCBlocks.Vegetation.getDefaultState();
		Moisture iMoisture = map.getParams().getIslandMoisture();
		Moisture cMoisture;
		float rand, m;
		BlockPos pos;
		boolean placed;

		Block aridGarden = Block.getBlockFromName("harvestcraft:aridgarden");
		Block frostGarden = Block.getBlockFromName("harvestcraft:frostgarden");
		Block soggyGarden = Block.getBlockFromName("harvestcraft:soggygarden");
		Block shadedGarden = Block.getBlockFromName("harvestcraft:shadedgarden");
		Block tropicalGarden = Block.getBlockFromName("harvestcraft:tropicalgarden");
		Block windyGarden = Block.getBlockFromName("harvestcraft:windygarden");

		boolean genArid = random.nextInt(5) != 0;

		//Shaded gardens should appear in wooded areas
		if(map.getParams().getIslandTemp().isWarmerThan(ClimateTemp.POLAR) && 
				map.getParams().getIslandTemp().isCoolerThan(ClimateTemp.TROPICAL) &&
				iMoisture.isGreaterThan(Moisture.LOW) && shadedGarden != null)
		{
			Point p = new Point(centerX-15+random.nextInt(32), centerZ-15+random.nextInt(32));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				if(random.nextInt(7) == 0)
				{
					numToGen = 5;
					pos = new BlockPos(p.getX(), 0, p.getZ());
					pos = world.getTopSolidOrLiquidBlock(pos);
					for(int i = 0; i < numToGen; i++)
					{
						pos = pos.add(-5+random.nextInt(11), 0, -5+random.nextInt(11));
						pos = world.getTopSolidOrLiquidBlock(pos);
						cMoisture = closest.getMoisture();
						if(cMoisture.isGreaterThan(Moisture.MEDIUM) && !closest.hasMarker(Marker.Water) && shadedGarden.canPlaceBlockAt(world, pos))
						{
							world.setBlockState(pos, shadedGarden.getDefaultState(), 2);
						}
					}
				}
			}
		}

		//Windy Gardens should be in open plains
		if( map.getParams().getIslandTemp().isWarmerThan(ClimateTemp.SUBPOLAR) && 
				map.getParams().getIslandTemp().isCoolerThan(ClimateTemp.TROPICAL) &&
				iMoisture.isLessThan(Moisture.HIGH) && windyGarden != null)
		{
			Point p = new Point(centerX-15+random.nextInt(32), centerZ-15+random.nextInt(32));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				if(random.nextInt(7) == 0)
				{
					numToGen = 8;
					pos = new BlockPos(p.getX(), 0, p.getZ());
					pos = world.getTopSolidOrLiquidBlock(pos);
					for(int i = 0; i < numToGen; i++)
					{
						pos = pos.add(-5+random.nextInt(11), 0, -5+random.nextInt(11));
						pos = world.getTopSolidOrLiquidBlock(pos);
						cMoisture = closest.getMoisture();
						if(cMoisture.isLessThan(Moisture.MEDIUM) && windyGarden.canPlaceBlockAt(world, pos) && world.isAirBlock(pos))
						{
							world.setBlockState(pos, windyGarden.getDefaultState(), 2);
						}
					}
				}
			}
		}

		//Arid Gardens should be in warm desert locations with a modest amount of water
		if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.TEMPERATE) && 
				iMoisture.isLessThanOrEqual(Moisture.LOW) && aridGarden != null)
		{
			Point p = new Point(centerX-15+random.nextInt(32), centerZ-15+random.nextInt(32));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				if(random.nextInt(7) == 0)
				{
					numToGen = 5;
					pos = new BlockPos(p.getX(), 0, p.getZ());
					pos = world.getTopSolidOrLiquidBlock(pos);
					for(int i = 0; i < numToGen; i++)
					{
						pos = pos.add(-5+random.nextInt(11), 0, -5+random.nextInt(11));
						pos = world.getTopSolidOrLiquidBlock(pos);
						cMoisture = closest.getMoisture();
						if(cMoisture.isGreaterThan(Moisture.MEDIUM) && aridGarden.canPlaceBlockAt(world, pos))
						{
							world.setBlockState(pos, aridGarden.getDefaultState(), 2);
						}
					}
				}
			}
		}

		//Soggy Gardens should only appear in swamps or near rivers
		if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.SUBPOLAR) && soggyGarden != null)
		{
			Point p = new Point(centerX-15+random.nextInt(32), centerZ-15+random.nextInt(32));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				if(random.nextInt(4) == 0)
				{
					numToGen = 12;
					pos = new BlockPos(p.getX(), 0, p.getZ());
					pos = world.getTopSolidOrLiquidBlock(pos);
					for(int i = 0; i < numToGen; i++)
					{
						pos = pos.add(-5+random.nextInt(11), 0, -5+random.nextInt(11));
						pos = world.getTopSolidOrLiquidBlock(pos);
						cMoisture = closest.getMoisture();
						if((closest.biome == BiomeType.MARSH || closest.biome == BiomeType.RIVER || closest.biome == BiomeType.SWAMP) && soggyGarden.canPlaceBlockAt(world, pos))
						{
							//We dont want rivers to have as many soggy gardens so we give it an additional chance to fail placement
							if(closest.biome == BiomeType.RIVER && random.nextBoolean())
								continue;
							world.setBlockState(pos, soggyGarden.getDefaultState(), 2);
						}
					}
				}
			}
		}

		if(map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.SUBTROPICAL) && 
				iMoisture.isGreaterThanOrEqual(Moisture.MEDIUM) && tropicalGarden != null)
		{
			Point p = new Point(centerX-15+random.nextInt(32), centerZ-15+random.nextInt(32));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				if(random.nextInt(7) == 0)
				{
					numToGen = 8;
					pos = new BlockPos(p.getX(), 0, p.getZ());
					pos = world.getTopSolidOrLiquidBlock(pos);
					for(int i = 0; i < numToGen; i++)
					{
						pos = pos.add(-5+random.nextInt(11), 0, -5+random.nextInt(11));
						pos = world.getTopSolidOrLiquidBlock(pos);
						cMoisture = closest.getMoisture();
						if(cMoisture.isGreaterThan(Moisture.MEDIUM) && tropicalGarden.canPlaceBlockAt(world, pos))
						{
							world.setBlockState(pos, tropicalGarden.getDefaultState(), 2);
						}
					}
				}
			}
		}

		if(map.getParams().getIslandTemp().isCoolerThanOrEqual(ClimateTemp.SUBPOLAR) &&
				iMoisture.isGreaterThan(Moisture.LOW) && frostGarden != null)
		{
			Point p = new Point(centerX-15+random.nextInt(32), centerZ-15+random.nextInt(32));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				if(random.nextInt(9) == 0)
				{
					numToGen = 8;
					pos = new BlockPos(p.getX(), 0, p.getZ());
					pos = world.getTopSolidOrLiquidBlock(pos);
					for(int i = 0; i < numToGen; i++)
					{
						pos = pos.add(-5+random.nextInt(11), 0, -5+random.nextInt(11));
						pos = world.getTopSolidOrLiquidBlock(pos);
						cMoisture = closest.getMoisture();
						if(cMoisture.isGreaterThan(Moisture.LOW) && frostGarden.canPlaceBlockAt(world, pos))
						{
							world.setBlockState(pos, frostGarden.getDefaultState(), 2);
						}
					}
				}
			}
		}

	}
}
