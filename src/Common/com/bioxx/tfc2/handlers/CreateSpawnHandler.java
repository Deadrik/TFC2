package com.bioxx.tfc2.handlers;

import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.tfc2.world.ChunkManager;

public class CreateSpawnHandler 
{
	@SubscribeEvent
	public void onCreateSpawn(WorldEvent.CreateSpawnPosition event)
	{
		World world = event.world;
		world.findingSpawnPoint = true;
		ChunkManager worldchunkmanager = (ChunkManager)world.getWorldChunkManager();
		Random random = new Random(world.getSeed());
		BlockPos blockpos = worldchunkmanager.findTerrainPositionForSpawn(random);
		int i = 0;
		int j = world.provider.getAverageGroundLevel();
		int k = 0;

		if (blockpos != null)
		{
			i = blockpos.getX();
			k = blockpos.getZ();
		}

		int l = 0;

		while (!world.provider.canCoordinateBeSpawn(i, k))
		{
			i += random.nextInt(64) - random.nextInt(64);
			k += random.nextInt(64) - random.nextInt(64);
			++l;

			if (l == 1000)
			{
				break;
			}
		}

		world.getWorldInfo().setSpawn(new BlockPos(i, j, k));
		world.findingSpawnPoint = false;
		event.setCanceled(true);
	}
}
