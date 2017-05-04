package com.bioxx.tfc2.animals;

import java.util.ArrayList;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.world.World;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.IslandWildlifeManager.HerdGoalEnum;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.pathfinding.IPathProfile;
import com.bioxx.tfc2.animals.path.PathProfileElk;
import com.bioxx.tfc2.api.VirtualAnimal;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.EnumAnimalDiet;
import com.bioxx.tfc2.api.types.Gender;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.entity.EntityElk;

public class ElkAnimalDef implements IAnimalDef
{
	IPathProfile pathProfile;
	public ElkAnimalDef() 
	{
		pathProfile = new PathProfileElk();
	}

	@Override
	public String getName() {
		return "elk";
	}

	@Override
	public int getMaxIslandPop(IslandParameters params) {
		return 100;
	}

	@Override
	public void onSpawn(Entity e) {

	}

	@Override
	public Class<? extends EntityLiving> getEntityClass() {
		return EntityElk.class;
	}

	@Override
	public ArrayList<VirtualAnimal> provideHerd(World world) 
	{
		ArrayList<VirtualAnimal> out = new ArrayList<VirtualAnimal>();

		if(world.rand.nextFloat() < 0.3)//Generate a solo male
		{
			out.add(new VirtualAnimal(getName(), Gender.Male));
		}
		else//Generate a small herd that may or may not have a male
		{
			int amount = 2+world.rand.nextInt(5);
			if(world.rand.nextBoolean())
			{
				out.add(new VirtualAnimal(getName(), Gender.Male));
				amount--;
			}
			for(int i = 0; i < amount; i++)
			{
				out.add(new VirtualAnimal(getName(), Gender.Female));
			}
		}

		return out;
	}

	@Override
	public boolean canSpawn(Center c) 
	{
		if(c.hasAnyMarkersOf(Marker.Ocean, Marker.Coast, Marker.Water, Marker.Mesa))
			return false;

		return true;
	}

	@Override
	public boolean canSpawn(IslandParameters params) 
	{
		ClimateTemp temp = params.getIslandTemp();
		Moisture m = params.getIslandMoisture();

		if(temp.isWarmerThanOrEqual(ClimateTemp.POLAR) && temp.isCoolerThanOrEqual(ClimateTemp.TEMPERATE) && m.isGreaterThanOrEqual(Moisture.MEDIUM))
		{
			if(!params.hasFeature(Feature.Desert))
				return true;
		}

		return false;
	}

	@Override
	public boolean doesMigrate()
	{
		return true;
	}

	@Override
	public boolean doesReplenishPopulation()
	{
		return true;
	}

	@Override
	public boolean isValidNeedZone(Center center, HerdGoalEnum goal) 
	{
		if(center.hasAnyMarkersOf(Marker.Ocean, Marker.Coast, Marker.Water, Marker.Mesa, Marker.Lava))
			return false;
		if(goal == HerdGoalEnum.FOOD)
		{			
			if(center.hasAnyMarkersOf(Marker.Clearing))
				return true;
		}
		else if(goal == HerdGoalEnum.REST)
		{
			if(center.biome == BiomeType.DECIDUOUS_FOREST)
				return true;
		}
		else if(goal == HerdGoalEnum.WATER)
		{
			if(center.biome == BiomeType.LAKESHORE || center.biome == BiomeType.POND || center.biome == BiomeType.RIVER)
				return true;
		}
		return false;
	}

	@Override
	public SpawnPlacementType getPlacementType()
	{
		return SpawnPlacementType.ON_GROUND;
	}

	@Override
	public boolean shouldGenNeedZones()
	{
		return true;
	}

	@Override
	public EnumAnimalDiet getAnimalDiet()
	{
		return EnumAnimalDiet.Herbivore;
	}

	@Override
	public IPathProfile getPathProfile()
	{
		return pathProfile;
	}

}
