package com.bioxx.tfc2.handlers;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldEntitySpawner;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.HexGenRegistry;
import com.bioxx.tfc2.api.animals.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.animals.Herd;
import com.bioxx.tfc2.api.animals.IGenderedAnimal;
import com.bioxx.tfc2.api.animals.VirtualAnimal;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.interfaces.IHerdAnimal;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

public class ChunkLoadHandler
{
	public static HashMap<Integer, ArrayList<Center>> loadedCentersMap = new HashMap<Integer, ArrayList<Center>>();

	@SubscribeEvent
	public void onChunkLoad(ChunkEvent.Load event)
	{
		if(!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			BlockPos chunkWorldPos = new BlockPos(event.getChunk().xPosition * 16, 0, event.getChunk().zPosition * 16);
			IslandMap map = Core.getMapForWorld(event.getWorld(), chunkWorldPos);

			Point islandPos = new Point(chunkWorldPos.getX(), chunkWorldPos.getZ()).toIslandCoord();
			Center temp = map.getClosestCenter(islandPos);

			ArrayList<Center> genList = new ArrayList<Center>();
			genList.add(temp);
			genList.addAll(temp.neighbors);

			for(Center c : genList)
			{
				AxisAlignedBB aabb = c.getAABB();
				if(Core.areChunksLoadedInArea(event.getWorld().getChunkProvider(), 
						new ChunkPos((int)(map.getParams().getWorldX()+aabb.minX) >> 4, (int)(map.getParams().getWorldZ() + aabb.minZ) >> 4), 
						new ChunkPos((int)(map.getParams().getWorldX()+aabb.maxX) >> 4, (int)(map.getParams().getWorldZ() + aabb.maxZ) >> 4)))
				{
					if(!loadedCentersMap.containsKey(map.getParams().getCantorizedID()))
					{
						loadedCentersMap.put(map.getParams().getCantorizedID(), new ArrayList<Center>());
					}
					ArrayList<Center> loaded = loadedCentersMap.get(map.getParams().getCantorizedID());
					if(loaded.contains(c))
						continue;

					loaded.add(c);					

					if(!c.hasGenerated)
					{					
						HexGenRegistry.generate(map, c, event.getWorld());
						c.hasGenerated = true;
					}

					ArrayList<Herd> herdsToLoad = map.getIslandData().wildlifeManager.getHerdsInCenter(c);
					BlockPos centerPos = new BlockPos(map.getParams().getWorldX()+c.point.getX(), 0, map.getParams().getWorldZ()+c.point.getZ());
					if(herdsToLoad.size() > 0)
					{
						for(Herd h : herdsToLoad)
						{
							IAnimalDef def = AnimalSpawnRegistry.getInstance().getDefFromName(h.getAnimalType());
							h.setLoaded();
							for(VirtualAnimal animal : h.getVirtualAnimals())
							{
								BlockPos pos = event.getWorld().getTopSolidOrLiquidBlock(centerPos);
								if (WorldEntitySpawner.canCreatureTypeSpawnAtLocation(def.getPlacementType(), event.getWorld(), pos))
								{
									IEntityLivingData ientitylivingdata = null;
									try
									{
										EntityLiving e = def.getEntityClass().getConstructor(new Class[] {World.class}).newInstance(new Object[] {event.getWorld()});
										e.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), 0, 0.0F);
										pos = getSpawnLocation(e, e.getPosition(), 10);
										e.setLocationAndAngles(pos.getX(), pos.getY(), pos.getZ(), event.getWorld().rand.nextFloat() * 360.0F, 0.0F);
										event.getWorld().spawnEntity(e);
										ientitylivingdata = e.onInitialSpawn(event.getWorld().getDifficultyForLocation(new BlockPos(e)), ientitylivingdata);
										def.onSpawn(e);
										if(e instanceof IHerdAnimal)
										{
											((IHerdAnimal)e).setAnimalDef(def);
											((IHerdAnimal)e).setHerdUUID(h.getUUID());
										}
										if(e instanceof IGenderedAnimal)
										{
											((IGenderedAnimal)e).setGender(animal.getGender());
										}
										animal.setLoaded(e);

									}
									catch(Exception e)
									{
										TFC.log.warn("Error while attempting to spawn entity ("+def.getName()+") at " + pos.toString());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private BlockPos getSpawnLocation(EntityLiving e, BlockPos pos, int range)
	{
		BlockPos out = e.getPosition();
		int count = 0;
		while(!e.getCanSpawnHere() && count < 20)
		{
			count++;
			out = e.world.getTopSolidOrLiquidBlock(pos.add(-range+e.world.rand.nextInt(1+(range*2)), 0, -range+e.world.rand.nextInt(1+(range*2))));
		}

		return out;
	}

	@SubscribeEvent
	public void onChunkUnload(ChunkEvent.Unload event)
	{
		if(!event.getWorld().isRemote && event.getWorld().provider.getDimension() == 0)
		{
			BlockPos chunkWorldPos = new BlockPos(event.getChunk().xPosition * 16, 0, event.getChunk().zPosition * 16);
			IslandMap map = Core.getMapForWorld(event.getWorld(), chunkWorldPos);

			Point islandPos = new Point(chunkWorldPos.getX(), chunkWorldPos.getZ()).toIslandCoord();
			AxisAlignedBB chunkAABB = new AxisAlignedBB(islandPos.getX(), 0, islandPos.getZ(), islandPos.getX()+16, 1, islandPos.getZ()+16);
			Center temp = map.getClosestCenter(islandPos);

			ArrayList<Center> genList = new ArrayList<Center>();
			genList.add(temp);
			genList.addAll(temp.neighbors);

			if(loadedCentersMap.containsKey(map.getParams().getCantorizedID()))
			{
				ArrayList<Center> loaded = loadedCentersMap.get(map.getParams().getCantorizedID());
				for(Center c : genList)
				{	
					AxisAlignedBB aabb = c.getAABB();
					boolean intersect =aabb.intersectsWith(chunkAABB);
					if(intersect && loaded.contains(c) )
					{
						loaded.remove(c);
						ArrayList<Herd> herdsToUnload = map.getIslandData().wildlifeManager.getHerdsInCenter(c);
						for(Herd h : herdsToUnload)
						{
							h.setUnloaded();
							for(VirtualAnimal animal : h.getVirtualAnimals())
							{
								if(animal.getEntity() == null)
								{
									animal.setUnloaded();
									continue;
								}
								Predicate<Entity> predicate = Predicates.<Entity>and(EntitySelectors.NOT_SPECTATING, EntitySelectors.notRiding(animal.getEntity()));
								Entity closestEntity = animal.getEntity().world.getClosestPlayer(animal.getEntity().posX, animal.getEntity().posY, animal.getEntity().posZ, 100D, predicate);
								if(closestEntity == null)
								{
									animal.getEntity().setDead();
									animal.setUnloaded();
								}
							}
						}
					}
				}
			}


		}
	}
}
