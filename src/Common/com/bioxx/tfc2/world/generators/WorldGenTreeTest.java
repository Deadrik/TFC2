package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.Core;
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
import com.bioxx.tfc2.world.ChunkManager;
import com.bioxx.tfc2.world.WorldGen;

public class WorldGenTreeTest implements IWorldGenerator
{
	TreeSchemManager tsm;
	TreeConfig tc;
	TreeSchematic schem;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		chunkX *= 16;
		chunkZ *= 16;

		if(world.getWorldChunkManager() instanceof ChunkManager)
		{
			int xM = (chunkX >> 12);
			int zM = (chunkZ >> 12);
			int xMLocal = chunkX & 4095;
			int zMLocal = chunkZ & 4095;
			IslandMap m = WorldGen.instance.getIslandMap(xM, zM);
			BlockPos chunkPos = new BlockPos(chunkX, 0, chunkZ);
			Center c = m.getSelectedHexagon(new Point(xMLocal+8, zMLocal+8));

			if(c.hasMarker(Marker.Ocean) || !TFCOptions.shouldGenTrees)
			{
				return;
			}

			//The theoretical max number of trees per chunk is 8.
			//We mult this by whichever is lower, the hex moisture or the island moisture.
			//This way base dry islands still feature less trees overall.
			int baseTrees = 12;
			baseTrees = (int)(baseTrees * Math.min(c.getMoisture().getMoisture(), m.getParams().getIslandMoisture().getMoisture()));
			int numTrees = random.nextInt(baseTrees+1)+1;
			//numTrees = (int)(numTrees * c.getMoisture().getMoisture());

			if(c.getMoisture() == Moisture.LOW)
				numTrees = random.nextDouble() < 0.25 ? 1 : 0;

			/**
			 * Do palm tree gen on valid islands
			 */
			if(c.getElevation() < 0.2 && c.getMoisture().getMoisture() >= Moisture.HIGH.getMoisture() && 
					m.getParams().getIslandTemp().getTemp() >= ClimateTemp.SUBTROPICAL.getTemp())
			{
				for(int l = 0; l < 3; l++)
				{
					genPalm(random, chunkX, chunkZ, world, chunkPos, m);
				}
			}

			for(int l = 0; l < numTrees; l++)
			{
				double rarity = random.nextDouble();
				if(rarity > 0.9)
					gen(random, chunkX, chunkZ, world, chunkPos, m, m.getParams().getRareTree());
				else if(rarity > 0.6)
					gen(random, chunkX, chunkZ, world, chunkPos, m, m.getParams().getUncommonTree());
				else
					gen(random, chunkX, chunkZ, world, chunkPos, m, m.getParams().getCommonTree());
			}
		}
	}

	private boolean gen(Random random, int chunkX, int chunkZ, World world, BlockPos chunkPos, IslandMap m, String wood) 
	{
		tsm = TreeRegistry.instance.managerFromString(wood);
		tc = TreeRegistry.instance.treeFromString(wood);

		if(tsm == null || tc == null)
			return false;

		BlockPos treePos = new BlockPos(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
		treePos = treePos.add(0, world.getHorizon(treePos).getY(), 0);
		Point p = new Point(treePos.getX(), treePos.getZ()).toIslandCoord();
		Center c = m.getSelectedHexagon(p);

		if(c.hasMarker(Marker.Ocean))
		{
			return false;
		}

		int growthStage = 0;
		if(c.getMoisture().isGreaterThan(Moisture.MEDIUM))
		{
			growthStage = random.nextInt(3);
		}
		else if(c.getMoisture().isGreaterThan(Moisture.LOW))
		{
			growthStage = random.nextInt(2);
		}
		boolean grown = false;

		for(;growthStage >= 0 && !grown; growthStage--)
		{
			schem = tsm.getRandomSchematic(random, growthStage);

			if( schem != null && canGrowHere(world, treePos.offsetDown(), schem, Math.max(growthStage, 1)))
			{
				grown = genTree(schem, tc, world, treePos);
			}
		}

		return grown;
	}

	private boolean genPalm(Random random, int chunkX, int chunkZ, World world, BlockPos chunkPos, IslandMap m) 
	{
		tsm = TreeRegistry.instance.managerFromString(WoodType.Palm.getName());
		tc = TreeRegistry.instance.treeFromString(WoodType.Palm.getName());

		BlockPos treePos = new BlockPos(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
		treePos = treePos.add(0, world.getHorizon(treePos).getY(), 0);
		Center c = m.getSelectedHexagon(new Point(treePos.getX() % 4096, treePos.getZ() % 4096));

		if(c.hasMarker(Marker.Ocean))
		{
			return false;
		}

		int growthStage = random.nextInt(3);
		boolean grown = false;
		for(;growthStage >= 0 && !grown; growthStage--)
		{
			schem = tsm.getRandomSchematic(random, growthStage);

			if( schem != null && canGrowHere(world, treePos.offsetDown(), schem, Math.max(growthStage, 1)))
			{
				grown = genTree(schem, tc, world, treePos);
			}
		}

		return grown;
	}


	//*****************
	// Private methods
	//*****************
	private boolean genTree(Schematic schem, TreeConfig tc, World world, BlockPos pos)
	{
		int rot = world.rand.nextInt(4);
		int index;
		int id;
		int meta;

		int baseX = pos.getX() - 1;
		int baseY = pos.getY();
		int baseZ = pos.getZ() - 1;

		for(SchemBlock b : schem.getBlockMap())
		{
			Process(world, baseX, baseY, baseZ, tc, schem, b.pos, rot, b.state);
		}

		return true;
	}

	private void Process(World world, int treeX, int treeY, int treeZ, TreeConfig tc,
			Schematic schem, BlockPos localPos, int rot, IBlockState state)
	{
		int localX = treeX + localPos.getX() * -1;
		int localZ = treeZ + localPos.getZ() * -1;
		int localY = treeY + localPos.getY();

		if(rot == 0)
		{
			localX = treeX + localPos.getX();
			localZ = treeZ + localPos.getZ();
		}
		else if(rot == 1)
		{
			localX = treeX + localPos.getX();
			localZ = treeZ + localPos.getZ() * -1;
		}
		else if(rot == 2)
		{
			localX = treeX  + localPos.getX() * -1;
			localZ = treeZ + localPos.getZ();
		}

		IBlockState block = tc.wood;
		BlockPos blockPos = new BlockPos(localX, localY, localZ);
		IBlockState leaves = tc.leaves;

		if(state.getBlock().getMaterial() == Material.wood)
		{
			world.setBlockState(blockPos, block, 2);
		}
		else if(state.getBlock().getMaterial() == Material.leaves)
		{
			if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
			{
				world.setBlockState(blockPos, leaves, 2);
			}
		}
		else
		{
			world.setBlockState(blockPos, state);
		}
	}

	private boolean canGrowHere(World world, BlockPos pos, TreeSchematic schem, int growthStage)
	{
		IBlockState ground;
		IBlockState above;
		BlockPos gPos = pos;
		BlockPos aPos = pos.offsetUp();
		int radius = Math.max(1, growthStage);
		int count = 0;

		for(int i = -radius; i <= radius; i++)
		{
			for(int k = -radius; k <= radius; k++)
			{
				ground = world.getBlockState(gPos.add(i, 0, k));

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
		for(int i = 0; i <= schem.getSizeY(); i++)
		{
			aPos = aPos.add(0, i, 0);
			above = world.getBlockState(aPos);
			if(!above.getBlock().isReplaceable(world, aPos))
			{
				return false;
			}
			if(above.getBlock().isLeaves(world, aPos))
			{
				count++;
			}
			//If we run into too many leaves, then don't place the tree here. This is not perfect as it
			//can not account for wide trees, but at least trees with a small radius will not stack.
			if(count > 2)
				return false;
		}

		return true;
	}
}
