package com.bioxx.tfc2.world.generators;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.Spline3D;
import com.bioxx.tfc2.Core;

public class WorldGenErosion implements IWorldGenerator
{
	public WorldGenErosion()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen,IChunkProvider chunkProvider)
	{
		if(world.provider.getDimension() != 0)
			return;

		chunkX *= 16;
		chunkZ *= 16;

		BlockPos bp = new BlockPos(chunkX, 0, chunkZ);

		for(int i = 0; i < 5; i++)
		{
			int x = random.nextInt(16);
			int z = random.nextInt(16);

			int midHeight = Core.getHeight(world, bp.getX()+x, bp.getZ()+z);
			BlockPos localPos = bp.add(x, midHeight, z);
			BlockPos northPos = localPos.add(0, 0, 4);
			BlockPos southPos = localPos.add(0, 0, -4);
			BlockPos eastPos = localPos.add(4, 0, 0);
			BlockPos westPos = localPos.add(-4, 0, 0);

			int localHeight = Core.getHeight(world, localPos.getX(), localPos.getZ());
			if(midHeight - world.getHeight(northPos).getY() > 5)
			{
				ArrayList<BlockPos> list = new ArrayList<BlockPos>();
				list.add(southPos.add(getRange(random, 5), 0, 0));
				list.add(localPos);
				list.add(northPos.add(getRange(random, 5), 2, 0));

				carve(world, new Spline3D(list));
			}
			else if(midHeight - world.getHeight(southPos).getY() > 5)
			{
				ArrayList<BlockPos> list = new ArrayList<BlockPos>();
				list.add(northPos.add(getRange(random, 5), 0, 0));
				list.add(localPos);
				list.add(southPos.add(getRange(random, 5), 2, 0));

				carve(world, new Spline3D(list));
			}
			else if(midHeight - world.getHeight(eastPos).getY() > 5)
			{
				ArrayList<BlockPos> list = new ArrayList<BlockPos>();
				list.add(westPos.add(0, 0, getRange(random, 5)));
				list.add(localPos);
				list.add(eastPos.add(0, 2, getRange(random, 5)));

				carve(world, new Spline3D(list));
			}
			else if(midHeight - world.getHeight(westPos).getY() > 5)
			{
				ArrayList<BlockPos> list = new ArrayList<BlockPos>();
				list.add(eastPos.add(0, 0, getRange(random, 5)));
				list.add(localPos);
				list.add(westPos.add(0, 2, getRange(random, 5)));

				carve(world, new Spline3D(list));
			}
		}
	}

	public void carve(World world, Spline3D spline)
	{
		double wSq = 1.2;
		for(double i = 0; i < 1; i+=0.1)
		{
			BlockPos splinePos = spline.getPoint(i);

			//min and max are the maximum distances from the center that we will carve. This is based on river width
			int min = -1;
			int max = 1;


			//Begin X/Z iteration around the base point
			for(double x = min; x <= max; x++)
			{
				for(double z = min; z <= max; z++)
				{
					//Add x and z to our base point to get the local block position
					BlockPos localBlockPos = splinePos.add(x, 0, z);
					double dist = getDistance(localBlockPos.down(localBlockPos.getY()), splinePos.down(splinePos.getY()));
					if(dist <= wSq)
					{
						for(double y = -2; y <= 1; y++)
						{
							world.setBlockState(localBlockPos.add(0, y, 0), Blocks.AIR.getDefaultState());
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
}
