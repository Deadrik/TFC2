package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.trees.TreeConfig;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.trees.TreeSchemManager;
import com.bioxx.tfc2.api.trees.TreeSchematic;
import com.bioxx.tfc2.api.types.Moisture;

public class WorldGenSwampTreesHex extends WorldGenTreesHex
{
	TreeSchemManager tsm;
	TreeConfig tc;
	TreeSchematic schem;

	public WorldGenSwampTreesHex()
	{
		replaceSoil = true;
	}

	@Override
	public void generate(Random random, IslandMap map, Center c, World world)
	{
		//We turn off treegen temporarily to prevent the super class from trying to place trees. We only want the
		//super.super class to setup the important variables.
		boolean b = TFCOptions.shouldGenTrees;
		TFCOptions.shouldGenTrees = false;
		super.generate(random, map, c, world);
		TFCOptions.shouldGenTrees = b;
		replaceSoil = true;
		if(world.provider.getDimension() != 0)
			return;

		if(map == null || c == null)
			return;

		if(c.biome != BiomeType.SWAMP || c.hasMarker(Marker.Clearing) || c.hasMarker(Marker.Ocean) || !TFCOptions.shouldGenTrees)
		{
			return;
		}

		//We mult by whichever is lower, the hex moisture or the island moisture.
		//This way base dry islands still feature less trees overall.
		int baseTrees = 100;
		baseTrees = (int)(baseTrees * Math.min(c.getMoisture().getMoisture(), map.getParams().getIslandMoisture().getMoisture()));

		if(c.biome == BiomeType.DRY_FOREST)
			baseTrees /= 2;

		int numTrees = random.nextInt(baseTrees/2+1)+baseTrees/2;

		if(c.getMoisture() == Moisture.LOW)
			numTrees = random.nextDouble() < 0.25 ? 1 : 0;

		for(int l = 0; l < numTrees; l++)
		{
			double rarity = random.nextDouble();
			TreeReturn out;

			out = gen(random, world, c, map, map.getParams().getSwampTree());

			if(out.size != TreeReturnEnum.None && out.baseCount > 4)
			{
				numTrees -= 1;
			}
		}
	}

	@Override
	protected TreeReturn gen(Random random, World world, Center c, IslandMap m, String wood) 
	{
		tsm = TreeRegistry.instance.managerFromString(wood);
		tc = TreeRegistry.instance.treeFromString(wood);
		if(tsm == null || tc.equals(""))
		{
			TFC.log.info("Can't locate :" + wood);
			return new TreeReturn(TreeReturnEnum.None, 0);
		}

		BlockPos genPos = centerPos.add(-20+random.nextInt(41), 0, -20+random.nextInt(41));
		if(genPos.distanceSq(centerPos) > 400)//20*20
			return new TreeReturn(TreeReturnEnum.None, 0);

		genPos = genPos.add(0, map.convertHeightToMC(c.getElevation())+Global.SEALEVEL-1, 0);
		int y = world.getTopSolidOrLiquidBlock(genPos).getY();

		int growthStage = 0;
		if(m.getParams().getIslandMoisture().isGreaterThan(Moisture.LOW) && !m.getParams().hasFeature(Feature.Desert))
		{
			if(c.getMoisture().isGreaterThan(Moisture.MEDIUM) && m.getParams().getIslandMoisture().isGreaterThan(Moisture.MEDIUM))
			{
				growthStage = random.nextInt(3);
			}
			else if(c.getMoisture().isGreaterThan(Moisture.LOW))
			{
				growthStage = random.nextInt(2);
			}
		}

		TreeReturnEnum grown = TreeReturnEnum.None;
		for(;growthStage >= 0 && grown == TreeReturnEnum.None; growthStage--)
		{
			schem = tsm.getRandomSchematic(random, growthStage);
			BlockPos treePos = genPos.add(-(schem.getCenterX()-1), 0, -(schem.getCenterZ()-1));
			IBlockState s = world.getBlockState(treePos);
			if( schem != null && canGrowHere(world, treePos, schem, Math.max(growthStage, 1)))
			{
				if(buildTree(schem, tc, world, random, treePos, c))
				{
					grown = TreeReturnEnum.fromSize(growthStage);
				}
			}
		}

		return new TreeReturn(grown, schem.getBaseCount());
	}

	@Override
	protected boolean canGrowHere(World world, BlockPos pos, TreeSchematic schem, int growthStage)
	{
		IBlockState ground;
		IBlockState above;
		BlockPos gPos = pos;
		BlockPos aPos = pos.up();
		int radius = Math.max(1, growthStage);
		int count = 0;
		int failCount = 0;

		if(!world.getBlockState(aPos).getBlock().isReplaceable(world, aPos))
			return false;
		ground = world.getBlockState(gPos);

		//this should validate the ground
		for(int i = -radius; i <= radius; i++)
		{
			for(int k = -radius; k <= radius; k++)
			{
				count++;
				ground = world.getBlockState(gPos.add(i, 0, k));

				if(!Core.isSoil(ground))
				{
					failCount++;
				}
			}
		}

		//if > 25% of the blocks fail then 
		if(failCount > count * 0.5 )
			return false;

		return true;
	}
}
