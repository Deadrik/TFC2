package com.bioxx.tfc2.animals;

import java.util.ArrayList;
import java.util.Vector;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLiving.SpawnPlacementType;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.CaveAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.pathfinding.IPathProfile;
import com.bioxx.jmapgen.processing.CaveAttrNode;
import com.bioxx.tfc2.animals.path.PathProfileElk;
import com.bioxx.tfc2.api.animals.HerdGoalEnum;
import com.bioxx.tfc2.api.animals.IHerdBrain;
import com.bioxx.tfc2.api.animals.LingerBrain;
import com.bioxx.tfc2.api.animals.VirtualAnimal;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.EnumAnimalDiet;
import com.bioxx.tfc2.api.types.Gender;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.entity.EntityBear;
import com.bioxx.tfc2.entity.EntityBear.BearType;

public class BearBrownAnimalDef implements IAnimalDef
{
	IPathProfile pathProfile;
	public BearBrownAnimalDef() 
	{
		pathProfile = new PathProfileElk();
	}

	@Override
	public String getName() {
		return "bearBrown";
	}

	@Override
	public int getMaxIslandPop(IslandParameters params) {
		return 100;
	}

	@Override
	public void onSpawn(Entity e) {
		((EntityBear)e).setBearType(BearType.Brown);
	}

	@Override
	public Class<? extends EntityLiving> getEntityClass() {
		return EntityBear.class;
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
	public boolean doesReplenishPopulation()
	{
		return false;
	}

	@Override
	public boolean isValidNeedZone(Center center, HerdGoalEnum goal) 
	{
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
		return false;
	}

	@Override
	public EnumAnimalDiet getAnimalDiet()
	{
		return EnumAnimalDiet.Omnivore;
	}

	@Override
	public IPathProfile getPathProfile()
	{
		return pathProfile;
	}

	@Override
	public Class<? extends IHerdBrain> getBrainClass() 
	{
		return LingerBrain.class;
	}

	@Override
	public Vector<Center> getCentersForPlacement(IslandMap map)
	{
		Vector<Center> out = new Vector<Center>();
		Vector<Center> filter = map.filterOutMarkers(map.centers, Marker.Coast, Marker.Water, Marker.Volcano);
		for(Center c : filter)
		{
			if(c.hasAttribute(Attribute.Cave))
			{
				CaveAttribute attrib = (CaveAttribute) c.getAttribute(Attribute.Cave);
				for(CaveAttrNode node : attrib.nodes)
				{
					if(node.isEntrance())
					{
						out.add(c);
						break;
					}
				}
			}
		}

		return out;
	}

}
