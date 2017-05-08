package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.material.Material;
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
import com.bioxx.tfc2.api.Schematic;
import com.bioxx.tfc2.api.Schematic.SchemBlock;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.trees.TreeConfig;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.trees.TreeSchemManager;
import com.bioxx.tfc2.api.trees.TreeSchematic;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.WoodType;

public class WorldGenTreesHex extends WorldGenHex
{
	TreeSchemManager tsm;
	TreeConfig tc;
	TreeSchematic schem;
	boolean replaceSoil = false;

	@Override
	public void generate(Random random, IslandMap map, Center c, World world)
	{
		super.generate(random, map, c, world);
		if(world.provider.getDimension() != 0)
			return;

		if(map == null || c == null)
			return;

		if(c.hasMarker(Marker.Clearing) || c.hasMarker(Marker.Ocean) || !TFCOptions.shouldGenTrees)
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

		// Do palm tree gen on valid islands
		if(c.getElevation() < 0.2 && c.getMoisture().isGreaterThanOrEqual(Moisture.HIGH) && 
				map.getParams().getIslandTemp().isWarmerThanOrEqual(ClimateTemp.SUBTROPICAL))
		{
			for(int l = 0; l < 3; l++)
			{
				if(c.biome != BiomeType.MARSH)
					genPalm(random, world, c, map);
			}
		}

		for(int l = 0; l < numTrees; l++)
		{
			double rarity = random.nextDouble();
			TreeReturn out;

			if(c.biome == BiomeType.MARSH)
				out = gen(random, world, c, map, map.getParams().getSwampTree());
			else if(rarity > 0.9)
				out = gen(random, world, c, map, map.getParams().getRareTree());
			else if(rarity > 0.6)
				out = gen(random, world, c, map, map.getParams().getUncommonTree());
			else
				out = gen(random, world, c, map, map.getParams().getCommonTree());

			if(out.size != TreeReturnEnum.None && out.baseCount > 4)
			{
				numTrees -= 1;
			}
		}
	}

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

		genPos = world.getTopSolidOrLiquidBlock(genPos);

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
			if( schem != null && canGrowHere(world, genPos.down(), schem, Math.max(growthStage, 1)))
			{
				if(buildTree(schem, tc, world, random, genPos, c))
				{
					grown = TreeReturnEnum.fromSize(growthStage);
				}
			}
		}

		return new TreeReturn(grown, schem.getBaseCount());
	}

	protected TreeReturn genPalm(Random random, World world, Center c, IslandMap m) 
	{
		tsm = TreeRegistry.instance.managerFromString(WoodType.Palm.getName());
		tc = TreeRegistry.instance.treeFromString(WoodType.Palm.getName());

		BlockPos genPos = centerPos.add(-20+random.nextInt(41), 0, -20+random.nextInt(41));
		BlockPos treePos;
		genPos = genPos.add(0, Global.SEALEVEL, 0);

		if(c.hasMarker(Marker.Ocean))
		{
			return new TreeReturn(TreeReturnEnum.None, 0);
		}

		int growthStage = random.nextInt(3);
		TreeReturnEnum grown = TreeReturnEnum.None;
		for(;growthStage >= 0 && grown == TreeReturnEnum.None; growthStage--)
		{
			schem = tsm.getRandomSchematic(random, growthStage);
			if( schem != null && canGrowHere(world, genPos.down(), schem, Math.max(growthStage, 1)))
			{
				if(buildTree(schem, tc, world, random, genPos, c))
				{
					grown = TreeReturnEnum.fromSize(growthStage);
				}
			}
		}

		return new TreeReturn(grown, schem.getBaseCount());
	}


	//*****************
	// Private methods
	//*****************
	protected boolean buildTree(Schematic schem, TreeConfig tc, World world, Random rand, BlockPos pos, Center c)
	{
		int rot = rand.nextInt(4);//This causes world gen to change every other time we run the regen command. Not sure why.
		int index;
		int id;
		int meta;
		BlockPos treePos = pos;
		boolean capture = world.captureBlockSnapshots;
		world.captureBlockSnapshots = false;
		for(SchemBlock b : schem.getBlockMap())
		{
			Process(world, tc, schem, this.rotateTree(treePos, b.pos, rot), b.state, c);
		}
		world.captureBlockSnapshots = capture;
		return true;
	}

	protected void Process(World world, TreeConfig tc, Schematic schem, BlockPos blockPos, IBlockState state, Center c)
	{
		if(state.getBlock().getMaterial(state) == Material.WOOD)
		{
			if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos) || (replaceSoil && Core.isSoil(world.getBlockState(blockPos))))
				world.setBlockState(blockPos, tc.wood, 2);
		}
		else if(state.getBlock().getMaterial(state) == Material.LEAVES)
		{
			if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
			{
				world.setBlockState(blockPos, tc.leaves, 2);
			}
		}
		else
		{
			world.setBlockState(blockPos, state, 2);
		}
	}

	protected BlockPos rotateTree(BlockPos treePos, BlockPos localPos, int rot)
	{
		int localX = treePos.getX() + (localPos.getX() * -1) - 1;
		int localZ = treePos.getZ() + (localPos.getZ() * -1) - 1;
		int localY = treePos.getY() + localPos.getY();

		if(rot == 0)
		{
			localX = treePos.getX() + localPos.getX() + 1;
			localZ = treePos.getZ() + localPos.getZ() + 1;
		}
		else if(rot == 1)
		{
			localX = treePos.getX() + localPos.getZ() + 1;
			localZ = treePos.getZ() + (localPos.getX() * -1) - 1;
		}
		else if(rot == 2)
		{
			localX = treePos.getX()  + (localPos.getZ() * -1) -1;
			localZ = treePos.getZ() + localPos.getX()+1;
		}

		return new BlockPos(localX, localY, localZ);
	}

	protected boolean canGrowHere(World world, BlockPos pos, TreeSchematic schem, int growthStage)
	{
		IBlockState ground;
		IBlockState above;
		BlockPos gPos = pos;
		BlockPos aPos = pos.up();
		int radius = Math.max(1, growthStage);
		int count = 0;
		int failCount = 0;

		if(!world.isAirBlock(aPos))
			return false;

		//this should validate the ground
		for(int i = -radius; i <= radius; i++)
		{
			for(int k = -radius; k <= radius; k++)
			{
				count++;
				ground = world.getBlockState(gPos.add(i, 0, k));

				if(schem.getWoodType() != WoodType.Palm && !Core.isSoil(ground))
				{
					return false;
				}
				else if(schem.getWoodType() == WoodType.Palm && !Core.isSoil(ground) && !Core.isSand(ground))
				{
					return false;
				}

				for(int y = 1; y <= schem.getSizeY(); y++)
				{
					if(Core.isNaturalLog(world.getBlockState(gPos.add(0, y, 0))))
						return false;
				}
			}
		}

		/*if(failCount > count * 0.25 )
			return false;*/

		return true;
	}

	public static class TreeReturn
	{
		public final TreeReturnEnum size;
		public final int baseCount;

		public TreeReturn(TreeReturnEnum e, int c)
		{
			size = e;
			baseCount = c;
		}
	}

	public enum TreeReturnEnum
	{
		None, Small, Normal, Large;

		public static TreeReturnEnum fromSize(int size)
		{
			if(size == 0)
				return Small;
			else if(size == 1)
				return Normal;
			else
				return Large;
		}
	}
}
