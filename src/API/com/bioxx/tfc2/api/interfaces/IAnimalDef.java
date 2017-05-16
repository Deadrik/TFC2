package com.bioxx.tfc2.api.interfaces;

import java.util.ArrayList;
import java.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.pathfinding.IPathProfile;
import com.bioxx.tfc2.api.animals.HerdGoalEnum;
import com.bioxx.tfc2.api.animals.IHerdBrain;
import com.bioxx.tfc2.api.animals.VirtualAnimal;
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
	 * @return A list of VirtualAnimals for use in {@link com.bioxx.tfc2.api.WildlifeManager}
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

	public boolean doesReplenishPopulation();

	public boolean isValidNeedZone(Center center, HerdGoalEnum goal);

	public SpawnPlacementType getPlacementType();

	public boolean shouldGenNeedZones();

	public EnumAnimalDiet getAnimalDiet();

	public IPathProfile getPathProfile();

	public Class<? extends IHerdBrain> getBrainClass();

	/**
	 * @param map
	 * @return a list of centers for placing this animal or null if letting the default behavior run
	 */
	public Vector<Center> getCentersForPlacement(IslandMap map);
}
