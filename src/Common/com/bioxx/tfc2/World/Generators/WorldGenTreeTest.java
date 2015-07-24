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
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.Blocks.BlockSapling;
import com.bioxx.tfc2.CoreStuff.Schematic;
import com.bioxx.tfc2.World.ChunkManager;
import com.bioxx.tfc2.World.WorldGen;
import com.bioxx.tfc2.api.Trees.TreeConfig;
import com.bioxx.tfc2.api.Trees.TreeRegistry;
import com.bioxx.tfc2.api.Trees.TreeSchemManager;

public class WorldGenTreeTest implements IWorldGenerator
{

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

			gen(random, chunkX, chunkZ, world, chunkPos, m.islandParams.getCommonTree(), (int)((random.nextInt(6)+4)*c.getMoisture().getMoisture()));
			gen(random, chunkX, chunkZ, world, chunkPos, m.islandParams.getUncommonTree(), (int)(3*c.getMoisture().getMoisture()));
			if(random.nextBoolean())
				gen(random, chunkX, chunkZ, world, chunkPos, m.islandParams.getRareTree(), (int)(1*c.getMoisture().getMoisture()));
		}
	}

	private void gen(Random random, int chunkX, int chunkZ, World world, BlockPos chunkPos, String wood, int numTrees) 
	{
		int xCoord = 0;
		int zCoord = 0;
		int yCoord = 0;
		boolean isAirAbove = false;
		Schematic schem;
		TreeSchemManager tsm = TreeRegistry.instance.managerFromString(wood);
		TreeConfig tc = TreeRegistry.instance.treeFromString(wood);
		for(int l = 0; l < numTrees; l++)
		{
			int growthStage = random.nextInt(3);
			schem = tsm.getRandomSchematic(random);
			xCoord = chunkX + random.nextInt(16);
			yCoord = world.getHorizon(chunkPos).getY();
			zCoord = chunkZ + random.nextInt(16);
			BlockPos treePos = new BlockPos(xCoord, yCoord, zCoord);
			IBlockState b = world.getBlockState(treePos.offsetDown());
			isAirAbove = world.isAirBlock(treePos);

			if(canGrowHere(world, treePos.offsetDown(), 2)
					&& isAirAbove
					&& schem != null)
			{
				genTree(schem, tc, world, treePos);
			}
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
					if(id != Block.getIdFromBlock(Blocks.air))
						Process(world, baseX, baseY, baseZ, tc, schem, x + 1, y, z + 1, rot, Block.getBlockById(id));
				}
			}
		}

		return true;
	}

	private void Process(World world, int treeX, int treeY, int treeZ, TreeConfig tc,
			Schematic schem, int schemX, int schemY, int schemZ, int rot, Block b)
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
		else
		{
			if(world.getBlockState(blockPos).getBlock().isReplaceable(world, blockPos))
			{
				world.setBlockState(blockPos, leaves, 2);
			}
		}
	}

	private boolean canGrowHere(World world, BlockPos pos, int rad)
	{
		boolean ret = true;
		Block ground;
		Block above;
		BlockPos gPos = pos;
		BlockPos aPos = pos.offsetUp();

		for(int i = -rad; i <= rad; i++)
		{
			for(int k = -rad; k <= rad; k++)
			{
				ground = world.getBlockState(gPos.add(i, 0, k)).getBlock();
				above = world.getBlockState(aPos.add(i, 0, k)).getBlock();
				if(above == TFCBlocks.LogNatural)
				{
					return false;
				}
				if(!isBlockValid(world, gPos, ground))
				{
					return false;
				}
			}
		}

		return true;
	}

	private boolean isBlockValid(World world, BlockPos pos, Block block)
	{
		return block.canSustainPlant(world, pos, net.minecraft.util.EnumFacing.UP, (BlockSapling)TFCBlocks.Sapling);
	}
}
