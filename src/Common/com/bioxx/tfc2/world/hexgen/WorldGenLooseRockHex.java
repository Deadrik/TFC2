package com.bioxx.tfc2.world.hexgen;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Global;

public class WorldGenLooseRockHex extends WorldGenHex
{
	public WorldGenLooseRockHex()
	{

	}

	@Override
	public void generate(Random random, IslandMap map, Center closest, World world) 
	{
		super.generate(random, map, closest, world);
		if(world.provider.getDimension() != 0)
			return;

		int chance, height;
		BlockPos bp;
		Material mat;

		int amountOfRocks = 25;

		if(closest.hasAttribute(Attribute.River))
			amountOfRocks -= 10;
		if(mcElev(closest.getHighestNeighbor().getElevation()) - mcElev(closest.getElevation()) > 10 )
			amountOfRocks += 10;

		for(int i = 0; i < amountOfRocks; i++)
		{
			Point p = new Point(centerX-20+random.nextInt(42), centerZ-20+random.nextInt(42));
			if(p.distanceSq(centerPos.getX(), centerPos.getZ()) < 400)
			{
				BlockPos pos = new BlockPos(p.getX(), 0, p.getZ());
				pos = world.getTopSolidOrLiquidBlock(pos);
				IBlockState state = world.getBlockState(pos.down());
				if(Core.isTerrain(state) && !Core.isWater(world.getBlockState(pos)))
					Core.setBlock(world, TFCBlocks.LooseRocks.getStateFromMeta(map.getParams().getSurfaceRock().getMeta()), pos, 2);
			}
		}

	}

	private int mcElev(double e)
	{
		return Global.SEALEVEL + (int)(e*map.getParams().islandMaxHeight);
	}

}
