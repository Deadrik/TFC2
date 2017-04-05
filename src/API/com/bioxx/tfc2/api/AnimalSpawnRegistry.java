package com.bioxx.tfc2.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.entity.EntityLiving;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;

public class AnimalSpawnRegistry 
{
	private static AnimalSpawnRegistry instance = new AnimalSpawnRegistry();

	public static AnimalSpawnRegistry getInstance()
	{
		return instance;
	}

	public Map<String, SpawnGroup> entityMap = new HashMap<String, SpawnGroup>();

	public void register(SpawnGroup group)
	{
		entityMap.put(group.getGroupName(), group);
	}

	public ArrayList<SpawnGroup> getValidSpawnGroups(IslandParameters map)
	{
		ArrayList<SpawnGroup> outList = new ArrayList<SpawnGroup>();
		Iterator iter = entityMap.values().iterator();
		while(iter.hasNext())
		{
			SpawnGroup group = (SpawnGroup)iter.next();
			if(group.getSpawnParams().isIslandValid(map))
				outList.add(group);
		}
		return outList;
	}

	public SpawnGroup getGroupFromName(String groupName)
	{
		return entityMap.get(groupName);
	}

	public static class SpawnGroup
	{
		String groupName;
		Class<?extends EntityLiving> entityClass;
		int minSpawnGroupSize;
		int maxSpawnGroupSize;
		int maxPopulation;
		int maxConcurrent;
		SpawnParameters spawnParams;

		public SpawnGroup(String name, Class<?extends EntityLiving> c, int minGroup, int maxGroup, int maxtotal, int maxConcurrent, SpawnParameters parameters)
		{
			groupName = name;
			entityClass = c;
			minSpawnGroupSize = minGroup;
			maxSpawnGroupSize = maxGroup;
			maxPopulation = maxtotal;
			spawnParams = parameters;
			this.maxConcurrent = maxConcurrent;
		}

		/**
		 * Should be overriden to setup the entity
		 */
		public void onSpawn(EntityLiving e){}

		public String getGroupName() {
			return groupName;
		}

		public Class<? extends EntityLiving> getEntityClass() {
			return entityClass;
		}

		public int getMinGroupSpawn() {
			return minSpawnGroupSize;
		}

		public int getMaxGroupSpawn() {
			return maxSpawnGroupSize;
		}

		public int getMaxPopulation() {
			return maxPopulation;
		}

		public int getMaxConcurrent() {
			return maxConcurrent;
		}

		public SpawnParameters getSpawnParams()
		{
			return spawnParams;
		}

	}

	public static class SpawnParameters
	{
		ClimateTemp minTemp, maxTemp;
		Moisture minMoisture, maxMoisture;
		EntityLiving.SpawnPlacementType placementType;

		public SpawnParameters(ClimateTemp minTemp, ClimateTemp maxTemp, Moisture minMoisture, Moisture maxMoisture)
		{
			this.minTemp = minTemp;
			this.maxTemp = maxTemp;
			this.minMoisture = minMoisture;
			this.maxMoisture = maxMoisture;
			placementType = EntityLiving.SpawnPlacementType.ON_GROUND;
		}

		public SpawnParameters(ClimateTemp minTemp, ClimateTemp maxTemp, Moisture minMoisture, Moisture maxMoisture, EntityLiving.SpawnPlacementType placement)
		{
			this(minTemp, maxTemp, minMoisture, maxMoisture);
			placementType = placement;
		}

		public boolean isIslandValid(IslandParameters map)
		{
			Moisture m = map.getIslandMoisture();
			ClimateTemp temp = map.getIslandTemp();

			if(m.isLessThan(minMoisture) || m.isGreaterThan(maxMoisture))
				return false;
			if(temp.isCoolerThan(minTemp) || temp.isWarmerThan(maxTemp))
				return false;

			return true;
		}

		/**
		 * This is used when the island tick is looking for a hex to queue spawning. This is NOT used during the actual entity spawning.
		 */
		public boolean canSpawnHere(IslandMap map, Center closest)
		{
			if(placementType == EntityLiving.SpawnPlacementType.ON_GROUND && closest.hasMarker(Marker.Water))
				return false;
			else if(placementType == EntityLiving.SpawnPlacementType.IN_WATER && !closest.hasMarker(Marker.Water))
				return false;

			return true;
		}

		public EntityLiving.SpawnPlacementType getPlacementType()
		{
			return placementType;
		}

		public boolean canSpawnInDesert()
		{
			return false;
		}
	}

	public static class SpawnEntry
	{
		private String groupName;
		private int totalPopulation;//Current total island population, including unplaced animals
		private int availablePopulation;//Amount waiting to be placed

		public SpawnEntry(String name, int total)
		{
			groupName = name;
			totalPopulation = total;
			availablePopulation = 0;
		}

		public SpawnEntry(String name, int avail, int total)
		{
			groupName = name;
			totalPopulation = total;
			availablePopulation = avail;
		}

		public int getTotalPopulation()
		{
			return totalPopulation;
		}

		public String getGroupName()
		{
			return groupName;
		}

		/**
		 * Removes an animal from the total poplation counter and makes a slot available for placing a new animal on the island
		 */
		public void removeAnimal()
		{
			totalPopulation--;
			availablePopulation++;
		}

		/**
		 * Simply adds a new animal to the total population count
		 */
		public void addNewAnimal(int amt)
		{
			totalPopulation+=amt;
		}

		public boolean hasRoomForNewSpawns()
		{
			return availablePopulation > 0;
		}

		public int getAmountToSpawn()
		{
			return availablePopulation;
		}
	}

}
