package com.bioxx.tfc2.World.Generators;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jMapGen.Map;
import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.graph.Center;
import com.bioxx.jMapGen.graph.Center.Marker;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.World.ChunkManager;
import com.bioxx.tfc2.World.WorldGen;
import com.bioxx.tfc2.api.Schematic;
import com.bioxx.tfc2.api.Trees.TreeConfig;
import com.bioxx.tfc2.api.Trees.TreeRegistry;
import com.bioxx.tfc2.api.Trees.TreeSchemManager;
import com.bioxx.tfc2.api.Trees.TreeSchematic;
import com.bioxx.tfc2.api.Types.ClimateTemp;
import com.bioxx.tfc2.api.Types.Moisture;
import com.bioxx.tfc2.api.Types.WoodType;

public class WorldGenTreeTest implements IWorldGenerator
{

	TreeSchemManager tsm;
	TreeConfig tc;

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		chunkX *= 16;
		chunkZ *= 16;

		if(world.getWorldChunkManager() instanceof ChunkManager)
		{
			int xM = (chunkX >> 12);
			int zM = (chunkZ >> 12);
			Map m = WorldGen.instance.getIslandMap(xM, zM);
			BlockPos chunkPos = new BlockPos(chunkX, 0, chunkZ);
			Center c = m.getClosestCenter(new Point(chunkX+8, chunkZ+8));

			if(c.hasMarker(Marker.Ocean))
			{
				return;
			}

			//The theoretical max number of trees per chunk is 8.
			//We mult this by whichever is lower, the hex moisture or the island moisture.
			//This way base dry islands still feature less trees overall.
			int baseTrees = (int)(8 * Math.min(c.getMoisture().getMoisture(), m.islandParams.getIslandMoisture().getMoisture()));
			int numTrees = random.nextInt(baseTrees+1)+1;
			//numTrees = (int)(numTrees * c.getMoisture().getMoisture());

			if(c.getMoisture() == Moisture.LOW)
				numTrees = random.nextDouble() < 0.25 ? 1 : 0;

			/**
			 * Do palm tree gen on valid islands
			 */
			if(c.getElevation() < 0.2 && c.getMoisture().getMoisture() >= Moisture.HIGH.getMoisture() && 
					m.islandParams.getIslandTemp().getTemp() >= ClimateTemp.SUBTROPICAL.getTemp())
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
					gen(random, chunkX, chunkZ, world, chunkPos, m, m.islandParams.getRareTree());
				else if(rarity > 0.6)
					gen(random, chunkX, chunkZ, world, chunkPos, m, m.islandParams.getUncommonTree());
				else
					gen(random, chunkX, chunkZ, world, chunkPos, m, m.islandParams.getCommonTree());
			}
		}
	}

	private void gen(Random random, int chunkX, int chunkZ, World world, BlockPos chunkPos, Map m, String wood) 
	{
		TreeSchemManager tsm = TreeRegistry.instance.managerFromString(wood);
		TreeConfig tc = TreeRegistry.instance.treeFromString(wood);

		if(tsm == null || tc == null)
			return;

		BlockPos treePos = new BlockPos(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
		treePos = treePos.add(0, world.getHorizon(treePos).getY(), 0);
		Center c = m.getClosestCenter(new Point(treePos.getX(), treePos.getZ()));

		if(c.hasMarker(Marker.Ocean))
		{
			return;
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

		TreeSchematic schem = tsm.getRandomSchematic(random, growthStage);

		if( schem != null && canGrowHere(world, treePos.offsetDown(), schem))
		{
			genTree(schem, tc, world, treePos);
		}
	}

	private void genPalm(Random random, int chunkX, int chunkZ, World world, BlockPos chunkPos, Map m) 
	{
		TreeSchemManager tsm = TreeRegistry.instance.managerFromString(WoodType.Palm.getName());
		TreeConfig tc = TreeRegistry.instance.treeFromString(WoodType.Palm.getName());

		BlockPos treePos = new BlockPos(chunkX + random.nextInt(16), 0, chunkZ + random.nextInt(16));
		treePos = treePos.add(0, world.getHorizon(treePos).getY(), 0);
		Center c = m.getClosestCenter(new Point(treePos.getX(), treePos.getZ()));

		if(c.hasMarker(Marker.Ocean))
		{
			return;
		}

		int growthStage = random.nextInt(3);

		TreeSchematic schem = tsm.getRandomSchematic(random, growthStage);

		if( schem != null && canGrowHere(world, treePos.offsetDown(), schem))
		{
			genTree(schem, tc, world, treePos);
		}
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

		for(int y = 0; y < schem.getSizeY(); y++)
		{
			for(int z = 0; z < schem.getSizeZ(); z++)
			{
				for(int x = 0; x < schem.getSizeX(); x++)
				{
					index = x + schem.getSizeX() * (z + schem.getSizeZ() * y);
					id = schem.getBlockArray()[index];
					meta = schem.getDataArray()[index];
					if(id != Block.getIdFromBlock(Blocks.air))
						Process(world, baseX, baseY, baseZ, tc, schem, x + 1, y, z + 1, rot, Block.getBlockById(id), meta);
				}
			}
		}

		return true;
	}

	private void Process(World world, int treeX, int treeY, int treeZ, TreeConfig tc,
			Schematic schem, int schemX, int schemY, int schemZ, int rot, Block b, int meta)
	{
		int localX = treeX + schem.getCenterX() - schemX;
		int localZ = treeZ + schem.getCenterZ() - schemZ;
		int localY = treeY + schemY;

		if(rot == 0)
		{
			localX = treeX - schem.getCenterX() + schemX;
			localZ = treeZ - schem.getCenterZ() + schemZ;
		}
		else if(rot == 1)
		{
			localX = treeX - schem.getCenterX() + schemX;
			localZ = treeZ + schem.getCenterZ() - schemZ;
		}
		else if(rot == 2)
		{
			localX = treeX + schem.getCenterX() - schemX;
			localZ = treeZ - schem.getCenterZ() + schemZ;
		}

		IBlockState block = tc.wood;
		BlockPos blockPos = new BlockPos(localX, localY, localZ);
		IBlockState leaves = tc.leaves;

		if(b.getMaterial() == Material.wood)
		{
			world.setBlockState(blockPos, block, 2);
		}
		else if(b.getMaterial() == Material.leaves)
		{
			if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
			{
				world.setBlockState(blockPos, leaves, 2);
			}
		}
		else
		{
			world.setBlockState(blockPos, b.getStateFromMeta(meta));
		}
	}

	private boolean canGrowHere(World world, BlockPos pos, TreeSchematic schem)
	{
		IBlockState ground;
		IBlockState above;
		BlockPos gPos = pos;
		BlockPos aPos = pos.offsetUp();
		int count = 0;

		for(int i = -1; i <= 1; i++)
		{
			for(int k = -1; k <= 1; k++)
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
