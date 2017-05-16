package com.bioxx.tfc2.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.NeedZoneAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.threads.ThreadPathfind;
import com.bioxx.tfc2.api.animals.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.animals.Herd;
import com.bioxx.tfc2.api.animals.VirtualAnimal;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.util.IThreadCompleteListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WildlifeManager implements IThreadCompleteListener
{
	public static Logger log = LogManager.getLogger("IslandWildlifeManager");
	HashMap<UUID, Herd> herdMap = new HashMap<UUID, Herd>();
	ThreadPathfind[] pathfindThreads;
	long lastTickHour = -1;
	IslandMap map;

	public WildlifeManager(IslandMap map)
	{
		this.map = map;
	}

	public void addHerd(Herd h)
	{
		herdMap.put(h.getUUID(), h);
	}

	public Herd getHerd(UUID uuid)
	{
		return herdMap.get(uuid);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		herdMap.clear();
		NBTTagList invList = nbt.getTagList("herds", 10);
		for(int i = 0; i < invList.tagCount(); i++)
		{
			Herd h = new Herd("", null);
			h.readFromNBT((NBTTagCompound)invList.get(i), map);
			addHerd(h);
		}

		lastTickHour = nbt.getLong("lastTickHour");
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList herdList = new NBTTagList();
		for(int i = 0; i < herdMap.values().size(); i++)
		{
			Herd h = (Herd) herdMap.values().toArray()[i];
			NBTTagCompound herdTag = new NBTTagCompound();
			h.writeToNBT(herdTag);
			herdList.appendTag(herdTag);
		}
		nbt.setTag("herds", herdList);
		nbt.setLong("lastTickHour", lastTickHour);
	}

	/**
	 * This is called one time when the map is built to create the initial herds
	 */
	public void initialBuild(World world)
	{
		//comment this out before compiling
		if(map.getParams().getXCoord() != 0 || map.getParams().getZCoord() != -2)
			return;

		Vector<Center> needZones = map.filterKeepAttributes(map.filterOutMarkers(map.centers, Marker.Ocean), Attribute.NeedZone);

		for(String animalType : map.getParams().animalTypes)
		{
			IAnimalDef def = AnimalSpawnRegistry.getInstance().getDefFromName(animalType);

			//If no brain is defined then skip placement
			if(def.getBrainClass() == null)
				continue;

			Vector<Center> genCenters = def.getCentersForPlacement(map);

			if(genCenters == null)
			{
				genCenters = new Vector<Center>();
				//Filter out only our own need zones
				for(Center c : needZones)
				{
					NeedZoneAttribute attrib = (NeedZoneAttribute) c.getAttribute(Attribute.NeedZone);
					if(attrib.animalType.equals(def.getName()))
						genCenters.add(c);
				}
			}

			int numToGen = def.getMaxIslandPop(map.getParams());
			while(numToGen > 0)
			{
				ArrayList<VirtualAnimal> list = def.provideHerd(world);
				if(list.size() > 0)
				{
					numToGen -= list.size();
					Herd h = new Herd(def.getName(), genCenters.get(map.mapRandom.nextInt(genCenters.size())));
					h.addAllAnimals(list);
					this.addHerd(h);
					//numToGen = 0;//remove me
					//log.info("Created herd at: " + h.getHerdBrain().currentLocation.point.toString());
				}
				else
				{
					log.info("Attempted to create empty herd for: " + def.getName());
				}
			}

		}
	}

	public void process(World world, long currentHour)
	{
		if(currentHour > lastTickHour)
		{
			lastTickHour++;

			//Process the brains for each herd
			ArrayList<UUID> herdsToRemove = new ArrayList<UUID>();
			for(Herd h : herdMap.values())
			{
				h.brain.process(world, map, lastTickHour);
				//Mark herd for deletion if all of the animals are gone.
				if(h.animals.size() == 0)
					herdsToRemove.add(h.getUUID());
			}

			//Cull empty herds
			for(UUID uuid : herdsToRemove)
			{
				herdMap.remove(uuid);
			}
		}
	}

	public ArrayList<Herd> getHerdsInCenter(Center c)
	{
		ArrayList<Herd> herds = new ArrayList<Herd>();
		for(Herd h : herdMap.values())
		{
			if(h.brain.getLocation() == c)
			{
				herds.add(h);
			}
		}
		return herds;
	}


	@Override
	public void notifyOfThreadComplete(Thread thread) 
	{
		pathfindThreads[((ThreadPathfind)thread).threadID] = null;
	}


}
