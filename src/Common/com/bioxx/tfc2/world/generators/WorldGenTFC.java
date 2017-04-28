package com.bioxx.tfc2.world.generators;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import net.minecraftforge.fml.common.IWorldGenerator;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.types.Moisture;

public class WorldGenTFC implements IWorldGenerator
{
	public Chunk chunk;
	public IslandMap map;
	Moisture iMoisture;

	public WorldGenTFC()
	{

	}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGen,IChunkProvider chunkProvider)
	{
		chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		chunkX *= 16;
		chunkZ *= 16;

		map = Core.getMapForWorld(world, new BlockPos(chunkX, 0, chunkZ));
		iMoisture = map.getParams().getIslandMoisture();
	}

	public Center getCenterInChunk(int x, int z)
	{
		Point p = new Point(x, z).toIslandCoord();
		Center c = map.getClosestCenter(p);
		Point p2 = c.point.minus(p);
		if(p2.x >= 0 && p2.x < 16 && p2.y >= 0 && p2.y < 16)
			return c;

		p = new Point(x+15, z).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x >= 0 && p2.x < 16 && p2.y >= 0 && p2.y < 16)
			return c;

		p = new Point(x, z+15).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x >= 0 && p2.x < 16 && p2.y >= 0 && p2.y < 16)
			return c;

		p = new Point(x+15, z+15).toIslandCoord();
		c = map.getClosestCenter(p);
		p2 = c.point.minus(p);
		if(p2.x >= 0 && p2.x < 16 && p2.y >= 0 && p2.y < 16)
			return c;

		return null;
	}
}
