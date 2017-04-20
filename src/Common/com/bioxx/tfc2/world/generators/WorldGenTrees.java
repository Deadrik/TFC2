package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.Point;
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
import com.bioxx.tfc2.world.WorldGen;

public class WorldGenTrees implements IWorldGenerator
{
	TreeSchemManager tsm;
	TreeConfig tc;
	TreeSchematic schem;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen, IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 0)
			return;
		chunkX *= 16;
		chunkZ *= 16;

		int xM = (chunkX >> 12);
		int zM = (chunkZ >> 12);
		int xMLocal = chunkX & 4095;
		int zMLocal = chunkZ & 4095;
		IslandMap m = WorldGen.getInstance().getIslandMap(xM, zM);
		BlockPos chunkPos = new BlockPos(chunkX, 0, chunkZ);
		Center c = m.getClosestCenter(new Point(xMLocal+8, zMLocal+8));

		if(c.hasMarker(Marker.Ocean) || !TFCOptions.shouldGenTrees)
		{
			return;
		}

		//The theoretical max number of trees per chunk is 8.
		//We mult this by whichever is lower, the hex moisture or the island moisture.
		//This way base dry islands still feature less trees overall.
		int baseTrees = 100;
		baseTrees = (int)(baseTrees * Math.min(c.getMoisture().getMoisture(), m.getParams().getIslandMoisture().getMoisture()));

		if(c.biome == BiomeType.DRY_FOREST)
			baseTrees /= 2;

		int numTrees = random.nextInt(baseTrees+1)+1;
		//numTrees = (int)(numTrees * c.getMoisture().getMoisture());

		if(c.getMoisture() == Moisture.LOW)
			numTrees = random.nextDouble() < 0.25 ? 1 : 0;

		// Do palm tree gen on valid islands
		if(c.getElevation() < 0.2 && c.getMoisture().getMoisture() >= Moisture.HIGH.getMoisture() && 
				m.getParams().getIslandTemp().getMapTemp() >= ClimateTemp.SUBTROPICAL.getMapTemp())
		{
			for(int l = 0; l < 3; l++)
			{
				genPalm(random, chunkX, chunkZ, world, chunkPos, m);
			}
		}

		for(int l = 0; l < numTrees; l++)
		{
			double rarity = random.nextDouble();
			TreeReturn out;
			if(rarity > 0.9)
				out = gen(random, chunkX, chunkZ, world, chunkPos, m, m.getParams().getRareTree());
			else if(rarity > 0.6)
				out = gen(random, chunkX, chunkZ, world, chunkPos, m, m.getParams().getUncommonTree());
			else
				out = gen(random, chunkX, chunkZ, world, chunkPos, m, m.getParams().getCommonTree());

			if(out.baseCount > 4)
			{
				numTrees -= 1;
			}
		}
	}

	private TreeReturn gen(Random random, int chunkX, int chunkZ, World world, BlockPos chunkPos, IslandMap m, String wood) 
	{
		tsm = TreeRegistry.instance.managerFromString(wood);
		tc = TreeRegistry.instance.treeFromString(wood);
		if(tsm == null || tc == null)
		{
			TFC.log.info("Can't locate :" + wood);
			return new TreeReturn(TreeReturnEnum.None, 0);
		}

		BlockPos genPos = new BlockPos(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
		BlockPos treePos;
		genPos = world.getTopSolidOrLiquidBlock(genPos);
		Point p = new Point(genPos.getX(), genPos.getZ()).toIslandCoord();
		Center c = m.getClosestCenter(p);
		//Obviously we arent going to gen in an ocean hex so we can speed up some generation by skipping this location
		if(c.hasMarker(Marker.Ocean))
		{
			return new TreeReturn(TreeReturnEnum.None, 0);
		}

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
			treePos = genPos.add(-(schem.getCenterX()-1), 0, -(schem.getCenterZ()-1));
			if( schem != null && canGrowHere(world, treePos.down(), schem, Math.max(growthStage, 1)))
			{
				if(genTree(schem, tc, world, random, treePos))
				{
					grown = TreeReturnEnum.fromSize(growthStage);
				}
			}
		}

		return new TreeReturn(grown, schem.getBaseCount());
	}

	private TreeReturn genPalm(Random random, int chunkX, int chunkZ, World world, BlockPos chunkPos, IslandMap m) 
	{
		tsm = TreeRegistry.instance.managerFromString(WoodType.Palm.getName());
		tc = TreeRegistry.instance.treeFromString(WoodType.Palm.getName());

		BlockPos genPos = new BlockPos(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
		BlockPos treePos;
		genPos = genPos.add(0, Global.SEALEVEL, 0);
		Center c = m.getClosestCenter(new Point(genPos.getX() % 4096, genPos.getZ() % 4096));

		if(c.hasMarker(Marker.Ocean))
		{
			return new TreeReturn(TreeReturnEnum.None, 0);
		}

		int growthStage = random.nextInt(3);
		TreeReturnEnum grown = TreeReturnEnum.None;
		for(;growthStage >= 0 && grown == TreeReturnEnum.None; growthStage--)
		{
			schem = tsm.getRandomSchematic(random, growthStage);
			treePos = genPos.add(-(schem.getCenterX()-1), 0, -(schem.getCenterZ()-1));

			if( schem != null && canGrowHere(world, treePos.down(), schem, Math.max(growthStage, 1)))
			{
				if(genTree(schem, tc, world, random, treePos))
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
	private boolean genTree(Schematic schem, TreeConfig tc, World world, Random rand, BlockPos pos)
	{
		int rot = rand.nextInt(4);//This causes world gen to change every other time we run the regen command. Not sure why.
		int index;
		int id;
		int meta;
		BlockPos treePos = pos.add(1, 0, 1);
		boolean capture = world.captureBlockSnapshots;
		world.captureBlockSnapshots = false;
		for(SchemBlock b : schem.getBlockMap())
		{
			Process(world, tc, schem, this.rotateTree(treePos, b.pos, rot), b.state);
		}
		world.captureBlockSnapshots = capture;
		return true;
	}

	private void Process(World world, TreeConfig tc, Schematic schem, BlockPos blockPos, IBlockState state)
	{
		if(state.getBlock().getMaterial(state) == Material.WOOD)
		{
			if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
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

	private BlockPos rotateTree(BlockPos treePos, BlockPos localPos, int rot)
	{
		int localX = treePos.getX() + (localPos.getX() * -1) - 2;
		int localZ = treePos.getZ() + (localPos.getZ() * -1) - 2;
		int localY = treePos.getY() + localPos.getY();

		if(rot == 0)
		{
			localX = treePos.getX() + localPos.getX() + 1;
			localZ = treePos.getZ() + localPos.getZ() + 1;
		}
		else if(rot == 1)
		{
			localX = treePos.getX() + localPos.getZ();
			localZ = treePos.getZ() + (localPos.getX() * -1) - 2;
		}
		else if(rot == 2)
		{
			localX = treePos.getX()  + (localPos.getZ() * -1) -2;
			localZ = treePos.getZ() + localPos.getX();
		}

		return new BlockPos(localX, localY, localZ);
	}

	private boolean canGrowHere(World world, BlockPos pos, TreeSchematic schem, int growthStage)
	{
		IBlockState ground;
		IBlockState above;
		BlockPos gPos = pos;
		BlockPos aPos = pos.up();
		int radius = Math.max(1, growthStage);
		int count = 0;

		if(!world.isAirBlock(aPos))
			return false;

		//this should validate the ground
		for(int i = -radius; i <= radius; i++)
		{
			for(int k = -radius; k <= radius; k++)
			{
				ground = world.getBlockState(gPos.add(i, 0, k));

				if(!world.canBlockSeeSky(gPos.add(i, 1, k)))
					return false;

				if(schem.getWoodType() != WoodType.Palm && !Core.isSoil(ground))
				{
					return false;
				}
				else if(schem.getWoodType() == WoodType.Palm && !Core.isSoil(ground) && !Core.isSand(ground))
				{
					return false;
				}
			}
		}

		//Scan to the tree height to make sure there is enough room for the tree
		/*for(int i = 0; i <= schem.getSizeY(); i++)
		{
			aPos = aPos.add(0, i, 0);
			above = world.getBlockState(aPos);
			if(!above.getBlock().isReplaceable(world, aPos))
			{
				return false;
			}
			if(above.getBlock().isLeaves(above, world, aPos))
			{
				count++;
			}
			//If we run into too many leaves, then don't place the tree here. This is not perfect as it
			//can not account for wide trees, but at least trees with a small radius will not stack.
			if(count > 2)
				return false;
		}*/

		return true;
	}

	private class TreeReturn
	{
		public final TreeReturnEnum size;
		public final int baseCount;

		public TreeReturn(TreeReturnEnum e, int c)
		{
			size = e;
			baseCount = c;
		}
	}

	private enum TreeReturnEnum
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
