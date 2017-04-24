package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.blocks.terrain.BlockStone;

public class WorldGenCliffNoise implements IWorldGenerator
{	
	int worldX = 0;
	int worldZ = 0;

	public WorldGenCliffNoise()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen,IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 100)//disabled for now
			return;

		chunkX *= 16;
		chunkZ *= 16;
		worldX = chunkX;
		worldZ = chunkZ;

		BlockPos bp = new BlockPos(chunkX, 0, chunkZ);
		IslandMap map = Core.getMapForWorld(world, new BlockPos(chunkX, 0, chunkZ));
		IBlockState stone = TFCBlocks.Stone.getDefaultState().withProperty(BlockStone.META_PROPERTY, map.getParams().getSurfaceRock());
		Perlin perlin = new Perlin(0, 1f/16f, 0.25);
		perlin.setLacunarity(2);
		perlin.setOctaveCount(2);

		for(int y = 255; y > Global.SEALEVEL; y--)
		{
			//EAST
			for(int z = 0; z < 16; z++)
			{
				int thickness = this.getThicknessFromFacing(world, worldX, y, worldZ+z, EnumFacing.EAST);
				if(thickness > -1)
				{
					double val = perlin.GetValue(worldX, y, worldZ+z)*0.5;//get VALUE and multiply by 0.5 to bring range into -1.0 to 1.0
					int scale = (int) Math.ceil(3 * val);
					if(scale > 0)
					{
						for(int x = 0; x <= scale; x++)
						{
							BlockPos pos = new BlockPos(worldX+thickness-x, y, worldZ+z);
							world.setBlockState(pos, stone, 2);
							if(Core.isSoil(world.getBlockState(pos.down())))
								world.setBlockState(pos.down(), stone, 2);
						}
					}
					else if(scale < 0)
					{
						for(int x = 0; x <= scale*(-1); x++)
						{
							world.setBlockState(new BlockPos(worldX+thickness+x, y, worldZ+z), Blocks.AIR.getDefaultState(), 2);
						}
					}
				}
			}

			//WEST
			for(int z = 0; z < 16; z++)
			{
				int thickness = this.getThicknessFromFacing(world, worldX, y, worldZ+z, EnumFacing.WEST);
				if(thickness > -1)
				{
					double val = perlin.GetValue(worldX, y, worldZ+z)*0.5;//get VALUE and multiply by 0.5 to bring range into -1.0 to 1.0
					int scale = (int) Math.ceil(3 * val);
					if(scale > 0)
					{
						for(int x = 0; x <= scale; x++)
						{
							BlockPos pos = new BlockPos(worldX+thickness-x, y, worldZ+z);
							world.setBlockState(pos, stone, 2);
							if(Core.isSoil(world.getBlockState(pos.down())))
								world.setBlockState(pos.down(), stone, 2);
						}
					}
					else if(scale < 0)
					{
						for(int x = 0; x <= scale*(-1); x++)
						{
							world.setBlockState(new BlockPos(worldX+thickness+x, y, worldZ+z), Blocks.AIR.getDefaultState(), 2);
						}
					}
				}
			}

			//NORTH
			for(int x = 0; x < 16; x++)
			{
				int thickness = this.getThicknessFromFacing(world, worldX+x, y, worldZ, EnumFacing.NORTH);
				if(thickness > -1)
				{
					double val = perlin.GetValue(worldX+x, y, worldZ)*0.5;//get VALUE and multiply by 0.5 to bring range into -1.0 to 1.0
					int scale = (int) Math.ceil(3 * val);
					if(scale > 0)
					{
						for(int z = 0; z <= scale; z++)
						{
							BlockPos pos = new BlockPos(worldX+x, y, worldZ+thickness+z);
							world.setBlockState(pos, stone, 2);
							if(Core.isSoil(world.getBlockState(pos.down())))
								world.setBlockState(pos.down(), stone, 2);
						}
					}
					else if(scale < 0)
					{
						for(int z = 0; z <= scale*(-1); z++)
						{
							world.setBlockState(new BlockPos(worldX+x, y, worldZ+thickness-z), Blocks.AIR.getDefaultState(), 2);
						}
					}
				}
			}

			//SOUTH
			for(int x = 0; x < 16; x++)
			{
				int thickness = this.getThicknessFromFacing(world, worldX+x, y, worldZ, EnumFacing.SOUTH);
				if(thickness > -1)
				{
					double val = perlin.GetValue(worldX+x, y, worldZ)*0.5;//get VALUE and multiply by 0.5 to bring range into -1.0 to 1.0
					int scale = (int) Math.ceil(3 * val);
					if(scale > 0)
					{
						for(int z = 0; z <= scale; z++)
						{
							BlockPos pos = new BlockPos(worldX+x, y, worldZ+thickness-z);
							world.setBlockState(pos, stone, 2);
							if(Core.isSoil(world.getBlockState(pos.down())))
								world.setBlockState(pos.down(), stone, 2);
						}
					}
					else if(scale < 0)
					{
						for(int z = 0; z <= scale*(-1); z++)
						{
							world.setBlockState(new BlockPos(worldX+x, y, worldZ+thickness+z), Blocks.AIR.getDefaultState(), 2);
						}
					}
				}
			}
		}
	}

	public int getRange(Random r, int range)
	{
		return (int) (Math.floor(range/2) + r.nextInt(range));
	}

	public double getDistance(BlockPos a, BlockPos b)
	{
		return a.distanceSq(b.getX(), b.getY(), b.getZ());
	}
	private int getThicknessFromFacing(World world, int x, int y, int z, EnumFacing facing)
	{
		int out = -1;
		switch(facing)
		{
		case EAST:
		{
			for(int _x = 0; _x < 16; _x++)
			{
				IBlockState state = world.getBlockState(new BlockPos(worldX+_x, y, z));
				if(state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid())
				{
					out = _x;
					if(state.getBlock() != TFCBlocks.Stone)
						return -1;
					break;
				}
			}

			IBlockState state = world.getBlockState(new BlockPos(worldX-1, y, z));
			if(out == 0 && (state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid()))
				return -1;
			break;
		}
		case NORTH:
		{
			for(int _z = 15; _z >= 0; _z--)
			{
				IBlockState state = world.getBlockState(new BlockPos(x, y, worldZ+_z));
				if(state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid())
				{
					out = _z;
					if(state.getBlock() != TFCBlocks.Stone)
						return -1;
					break;
				}
			}

			IBlockState state = world.getBlockState(new BlockPos(x, y, worldZ+16));
			if(out == 15 && (state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid()))
				return -1;
			break;
		}
		case SOUTH:
		{
			for(int _z = 0; _z < 16; _z++)
			{
				IBlockState state = world.getBlockState(new BlockPos(x, y, worldZ+_z));
				if(state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid())
				{
					out = _z;
					if(state.getBlock() != TFCBlocks.Stone)
						return -1;
					break;
				}
			}

			IBlockState state = world.getBlockState(new BlockPos(x, y, worldZ-1));
			if(out == 0 && (state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid()))
				return -1;
			break;
		}
		case WEST:
		{
			for(int _x = 15; _x >= 0; _x--)
			{
				IBlockState state = world.getBlockState(new BlockPos(worldX+_x, y, z));
				if(state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid())
				{
					out = _x;
					if(state.getBlock() != TFCBlocks.Stone)
						return -1;
					break;
				}
				if(state.getBlock() != TFCBlocks.Stone)
					return -1;
			}

			IBlockState state = world.getBlockState(new BlockPos(worldX+16, y, z));
			if(out == 15 && (state != Blocks.AIR.getDefaultState() && !state.getMaterial().isLiquid()))
				return -1;
			break;
		}
		default:
			break;	
		}

		return out;
	}

}
