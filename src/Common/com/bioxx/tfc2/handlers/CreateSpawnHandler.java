package com.bioxx.tfc2.handlers;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.world.WorldGen;

public class CreateSpawnHandler 
{
	@SubscribeEvent
	public void onCreateSpawn(WorldEvent.CreateSpawnPosition event)
	{
		World world = event.getWorld();

		world.findingSpawnPoint = true;
		Random random = new Random(world.getSeed());
		BlockPos blockpos = findTerrainPositionForSpawn(random);
		int i = 0;
		int j = world.provider.getAverageGroundLevel();
		int k = 0;

		if (blockpos != null)
		{
			i = blockpos.getX();
			k = blockpos.getZ();
		}

		int l = 0;

		/*while (!world.provider.canCoordinateBeSpawn(i, k))
		{
			i += random.nextInt(64) - random.nextInt(64);
			k += random.nextInt(64) - random.nextInt(64);
			++l;

			if (l == 1000)
			{
				break;
			}
		}*/

		world.getWorldInfo().setSpawn(new BlockPos(i, j, k));
		world.findingSpawnPoint = false;
		event.setCanceled(true);
	}

	public BlockPos findTerrainPositionForSpawn(Random random)
	{
		BlockPos blockpos = new BlockPos(0,0,0);
		IslandMap map = WorldGen.getInstance().getIslandMap(0, -2);
		/*Vector<Center> land = map.getCentersBelow(0.1, false);
		Center c = land.get(random.nextInt(land.size()));
		for(int i = 0; i < 10000; i++)
		{
			if(c.hasMarker(Marker.Coast))
			{
				blockpos = new BlockPos((int)c.point.x + map.getParams().getWorldX(), 0, (int)c.point.y + map.getParams().getWorldZ());
				return blockpos;
			}
			else if(c.elevation > 0)
			{
				c = c.downslope;
			}
			else
			{
				c = c.neighbors.get(0);
			}
		}*/
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		Center c = map.getClosestCenter(new Point(1500+random.nextInt(1000), 1500+random.nextInt(1000)));
		while(c.biome != BiomeType.BEACH)
		{
			indexList.add(c.index);
			if(c.downslope == null || c.downslope == c || indexList.contains(c.index))
				c = map.getClosestCenter(new Point(c.point.getX()-100+random.nextInt(200), c.point.getY()-100+random.nextInt(200)));

			indexList.add(c.index);

			if(c.hasAnyMarkersOf(Marker.Coast))
				break;

			c = c.downslope;
		}

		blockpos = new BlockPos((int)c.point.x + map.getParams().getWorldX(), 0, (int)c.point.y + map.getParams().getWorldZ());
		return blockpos;
	}
}
