package com.bioxx.tfc2.world;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;
import net.minecraft.world.chunk.Chunk;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;

public class AnimalSpawner 
{
	public static void SpawnAnimalGroup(World world, SpawnGroup group, Chunk chunk)
	{
		BlockPos chunkWorldPos = new BlockPos(chunk.xPosition * 16, 0, chunk.zPosition * 16);
		IEntityLivingData ientitylivingdata = null;
		int groupSize = 1;

		/*if(group.getMinGroupSpawn() == group.getMaxGroupSpawn())
			groupSize = group.getMinGroupSpawn();
		else
			groupSize = group.getMinGroupSpawn() + world.rand.nextInt(group.getMaxGroupSpawn()-group.getMinGroupSpawn());*/

		int x = chunkWorldPos.getX();
		int z = chunkWorldPos.getZ();
		int xStart = x;
		int zStart = z;
		BlockPos pos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
		if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(group.getSpawnParams().getPlacementType(), world, pos))
		{
			try
			{
				EntityLiving e = group.getEntityClass().getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
				e.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), world.rand.nextFloat() * 360.0F, 0.0F);
				world.spawnEntity(e);
				ientitylivingdata = e.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(e)), ientitylivingdata);

			}
			catch(Exception e)
			{
				TFC.log.warn("Error while attempting to spawn entity ("+group.getGroupName()+") at " + pos.toString());
			}
		}

		x += world.rand.nextInt(5) - world.rand.nextInt(5);

		for (z += world.rand.nextInt(5) - world.rand.nextInt(5); x < chunkWorldPos.getX() || x >= chunkWorldPos.getX() + 16 || 
				z < chunkWorldPos.getZ() || z >= chunkWorldPos.getZ() + 16; z = zStart + world.rand.nextInt(5) - world.rand.nextInt(5))
		{
			x = xStart + world.rand.nextInt(5) - world.rand.nextInt(5);
		}
	}
}
