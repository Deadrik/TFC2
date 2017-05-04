package com.bioxx.tfc2.api.interfaces;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.jmapgen.IslandWildlifeManager.HerdGoalEnum;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.pathfinding.IPathProfile;
import com.bioxx.tfc2.api.VirtualAnimal;
import com.bioxx.tfc2.api.types.EnumAnimalDiet;

public interface IAnimalDef 
{
	public String getName();
	/**
	 * @return Maximum amount of this animal allowed to be present on an island at once
	 */
	public int getMaxIslandPop(IslandParameters params);

	public void onSpawn(Entity e);

	/**
	 * @return Get Class for spawning
	 */
	public Class<? extends EntityLiving> getEntityClass();

	/**
	 * @return A list of VirtualAnimals for use in {@link com.bioxx.jmapgen.IslandWildlifeManager}
	 */
	public ArrayList<VirtualAnimal> provideHerd(World world);

	/**
	 * @return Is this Center a valid location for animals of this type to start?
	 */
	public boolean canSpawn(Center center);

	/**
	 * @return Is this island qualified to host this species
	 */
	public boolean canSpawn(IslandParameters params);

	public boolean doesMigrate();

	public boolean doesReplenishPopulation();

	public boolean isValidNeedZone(Center center, HerdGoalEnum goal);

	public SpawnPlacementType getPlacementType();

	public boolean shouldGenNeedZones();

	public EnumAnimalDiet getAnimalDiet();

	public IPathProfile getPathProfile();
}
