package com.bioxx.tfc2.world;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;

public class AnimalSpawner 
{
	public static void SpawnAnimalGroup(World world, IAnimalDef group, BlockPos pos)
	{

		IEntityLivingData ientitylivingdata = null;
		int groupSize = 1;

		pos = world.getTopSolidOrLiquidBlock(pos);
		if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(group.getPlacementType(), world, pos))
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
				TFC.log.warn("Error while attempting to spawn entity ("+group.getName()+") at " + pos.toString());
			}
		}
	}
}
