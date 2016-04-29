package com.bioxx.jmapgen.processing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;

public class AnimalProcessor 
{
	IslandMap map;

	public AnimalProcessor(IslandMap m)
	{
		map = m;
	}

	public void generate()
	{
		ArrayList<SpawnGroup> spawnGroups = map.getParams().animalSpawnGroups;

		Iterator<SpawnGroup> spawnGroupsIter = spawnGroups.iterator();

		//Cycle through each spawnable animal for this island
		while(spawnGroupsIter.hasNext())
		{
			SpawnGroup workingGroup = spawnGroupsIter.next();
			Vector<Center> spawnableList = GetSpawnableCenters(workingGroup, map);
			//Each animal has X number that should be spawned at any given time.
			for(int i = 0; i < workingGroup.getMaxConcurrent(); i++)
			{
				Center c = spawnableList.get(map.mapRandom.nextInt(spawnableList.size()));
				NBTTagList spawnList = GetCenterSpawnList(c);
				int groupCount = workingGroup.getMinGroupSpawn() + map.mapRandom.nextInt(1+workingGroup.getMaxGroupSpawn() - workingGroup.getMinGroupSpawn());
				for(int j = 0; j < groupCount && i < workingGroup.getMaxConcurrent(); j++)
				{
					spawnList.appendTag(new NBTTagString(workingGroup.getGroupName()));
					i++;
				}
				//map.getIslandData().animalEntries.get(workingGroup.getGroupName()).availablePopulation -= groupCount;
				c.getCustomNBT().setTag("animalsToSpawn", spawnList);
			}
		}
	}

	public static NBTTagList GetCenterSpawnList(Center c)
	{
		if(c.getCustomNBT().hasKey("animalsToSpawn"))
		{
			return c.getCustomNBT().getTagList("animalsToSpawn", 8);
		}
		else
		{
			return new NBTTagList();
		}
	}

	public static Vector<Center> GetSpawnableCenters(SpawnGroup group, IslandMap map)
	{
		Vector<Center> spawnableList = new Vector<Center>();

		for(Center c : map.centers)
		{
			if(group.getSpawnParams().canSpawnHere(map, c))
				spawnableList.add(c);
		}

		return spawnableList;
	}
}
