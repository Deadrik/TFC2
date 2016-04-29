package com.bioxx.tfc2.handlers;

import java.util.Vector;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.processing.AnimalProcessor;
import com.bioxx.tfc2.api.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnEntry;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;
import com.bioxx.tfc2.api.events.IslandUpdateEvent;

public class IslandUpdateHandler 
{
	@SubscribeEvent
	public void handle(IslandUpdateEvent event)
	{
		for(SpawnEntry entry : event.map.getIslandData().animalEntries.values())
		{
			SpawnGroup group = AnimalSpawnRegistry.getInstance().getGroupFromName(entry.getGroupName());
			if(entry.hasRoomForNewSpawns())
			{
				//Don't spawn new entities unless there is enough for a new group
				if(entry.getAmountToSpawn() < group.getMaxGroupSpawn())
					continue;

				genSingleGroup(group, event.map);

			}
		}
	}

	public int genSingleGroup(SpawnGroup group, IslandMap map)
	{
		Vector<Center> spawnableList = AnimalProcessor.GetSpawnableCenters(group, map);
		Center c = spawnableList.get(map.mapRandom.nextInt(spawnableList.size()));
		NBTTagList spawnList = AnimalProcessor.GetCenterSpawnList(c);
		int groupCount = group.getMinGroupSpawn() + map.mapRandom.nextInt(1+group.getMaxGroupSpawn() - group.getMinGroupSpawn());
		for(int j = 0; j < groupCount; j++)
		{
			spawnList.appendTag(new NBTTagString(group.getGroupName()));
		}
		return groupCount;
	}
}
