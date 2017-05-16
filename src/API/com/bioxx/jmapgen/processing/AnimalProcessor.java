package com.bioxx.jmapgen.processing;

import java.util.Vector;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.NeedZoneAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.api.animals.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.animals.HerdGoalEnum;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;

public class AnimalProcessor 
{
	IslandMap map;

	public AnimalProcessor(IslandMap m)
	{
		map = m;
	}

	public void generate()
	{
		for(String animal : map.getParams().animalTypes)
		{
			IAnimalDef def = AnimalSpawnRegistry.getInstance().getDefFromName(animal);
			if(def.shouldGenNeedZones())
			{
				Vector<Center> centers = map.filterOutMarkers(map.centers, Marker.Ocean);
				for(int i = 0; i < 10; i++)
				{
					Center c = centers.get(map.mapRandom.nextInt(centers.size()));
					if(!def.isValidNeedZone(c, HerdGoalEnum.FOOD) || c.hasAttribute(Attribute.NeedZone))
					{
						i--;
						continue;
					}

					NeedZoneAttribute attrib = new NeedZoneAttribute();
					attrib.animalType = def.getName();
					attrib.goalType = HerdGoalEnum.FOOD;
					c.addAttribute(attrib);
				}

				for(int i = 0; i < 50; i++)
				{
					Center c = centers.get(map.mapRandom.nextInt(centers.size()));
					if(!def.isValidNeedZone(c, HerdGoalEnum.REST) || c.hasAttribute(Attribute.NeedZone))
					{
						i--;
						continue;
					}

					NeedZoneAttribute attrib = new NeedZoneAttribute();
					attrib.animalType = def.getName();
					attrib.goalType = HerdGoalEnum.REST;
					c.addAttribute(attrib);
				}

				for(int i = 0; i < 10; i++)
				{
					Center c = centers.get(map.mapRandom.nextInt(centers.size()));
					if(!def.isValidNeedZone(c, HerdGoalEnum.WATER) || c.hasAttribute(Attribute.NeedZone))
					{
						i--;
						continue;
					}

					NeedZoneAttribute attrib = new NeedZoneAttribute();
					attrib.animalType = def.getName();
					attrib.goalType = HerdGoalEnum.WATER;
					c.addAttribute(attrib);
				}
			}
		}
	}


}
