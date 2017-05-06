package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.blocks.terrain.BlockStoneStalac;
import com.bioxx.tfc2.blocks.terrain.BlockStoneStalag;
import com.bioxx.tfc2.world.WorldGen;

public class WorldGenStalag implements IWorldGenerator
{
	IslandMap map;
	public WorldGenStalag()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen, IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 0)
			return;
		chunkX = chunkX * 16;
		chunkZ = chunkZ * 16 ;
		map = WorldGen.getInstance().getIslandMap(chunkX >> 12, chunkZ >> 12);
		IBlockState stalac = TFCBlocks.StoneStalac.getDefaultState().withProperty(BlockStoneStalag.META_PROPERTY, map.getParams().getSurfaceRock());
		IBlockState stalag = TFCBlocks.StoneStalag.getDefaultState().withProperty(BlockStoneStalag.META_PROPERTY, map.getParams().getSurfaceRock());
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				BlockPos pos = new BlockPos(chunkX+x, 0, chunkZ+z);
				Center closest = map.getClosestCenter(pos);
				int elev = mcElev(closest.getElevation());
				for(int y = 1; y < elev*0.8; y++)
				{
					pos = new BlockPos(chunkX+x, y, chunkZ+z);
					IBlockState state = world.getBlockState(pos);
					if(state.getBlock().isAir(state, world, pos) && Core.isStone(world.getBlockState(pos.down())))
					{
						//75% chance to skip
						if(random.nextFloat() > 0.05)
							continue;

						int height = measureHeight(world, pos);
						if(height > 2)
						{
							if(height == 3 && random.nextFloat() > 0.2)
							{
								setStone(world, pos, stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 1));
								setStone(world, pos.up(1), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 2));
								setStone(world, pos.up(2), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 1));
							}
							else if(height == 4 && random.nextFloat() > 0.4)
							{
								setStone(world, pos, stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 1));
								setStone(world, pos.up(1), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 2));
								setStone(world, pos.up(2), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 2));
								setStone(world, pos.up(3), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 1));
							}
							else if(height == 5 && random.nextFloat() > 0.5)
							{
								setStone(world, pos, stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 0));
								setStone(world, pos.up(1), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 1));
								setStone(world, pos.up(2), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 2));
								setStone(world, pos.up(3), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 1));
								setStone(world, pos.up(4), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 0));
							}
							else if(height == 6 && random.nextFloat() > 0.5)
							{
								setStone(world, pos, stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 0));
								setStone(world, pos.up(1), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 1));
								setStone(world, pos.up(2), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 2));
								setStone(world, pos.up(3), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 2));
								setStone(world, pos.up(4), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 1));
								setStone(world, pos.up(5), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 0));
							}
							else if (height >= 7)
							{
								setStone(world, pos, stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 0));
								setStone(world, pos.up(1), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 1));
								setStone(world, pos.up(2), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 2));

								setStone(world, pos.up(height - 3), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 2));
								setStone(world, pos.up(height - 2), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 1));
								setStone(world, pos.up(height - 1), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 0));
							}
							else if(height == 7)
							{
								setStone(world, pos, stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 0));
								setStone(world, pos.up(1), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 1));
								setStone(world, pos.up(2), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 2));

								setStone(world, pos.up(4), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 2));
								setStone(world, pos.up(5), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 1));
								setStone(world, pos.up(6), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 0));
							}
							else if(height == 8)
							{
								setStone(world, pos, stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 0));
								setStone(world, pos.up(1), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 1));
								setStone(world, pos.up(2), stalac.withProperty(BlockStoneStalac.SIZE_PROPERTY, 2));


								setStone(world, pos.up(5), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 2));
								setStone(world, pos.up(6), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 1));
								setStone(world, pos.up(7), stalag.withProperty(BlockStoneStalag.SIZE_PROPERTY, 0));
							}
							y += height;
						}
					}
				}
			}
		}

	}

	private void setStone(World world, BlockPos pos, IBlockState stone)
	{
		world.setBlockState(pos, stone);
	}

	public int measureHeight(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		int height = 0;
		while(!Core.isStone(state))
		{
			height++;
			state = world.getBlockState(pos.up(height));
			if(height > 20 || (!Core.isStone(state) && !state.getBlock().isAir(state, world, pos)))
				return 0;
		}
		return height;
	}

	private int mcElev(double e)
	{
		return Global.SEALEVEL + (int)(e*map.getParams().islandMaxHeight);
	}

}
