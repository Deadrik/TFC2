package com.bioxx.jmapgen;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.NeedZoneAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.pathfinding.CenterPath;
import com.bioxx.jmapgen.pathfinding.CenterPathFinder;
import com.bioxx.jmapgen.pathfinding.CenterPathNode;
import com.bioxx.jmapgen.threads.ThreadPathfind;
import com.bioxx.tfc2.api.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.VirtualAnimal;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.types.Gender;
import com.bioxx.tfc2.api.util.IThreadCompleteListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IslandWildlifeManager implements IThreadCompleteListener
{
	public static Logger log = LogManager.getLogger("IslandWildlifeManager");
	HashMap<UUID, Herd> herdMap = new HashMap<UUID, Herd>();
	ThreadPathfind[] pathfindThreads;
	long lastTickHour = -1;
	IslandMap map;

	public IslandWildlifeManager(IslandMap map)
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
		if(map.getParams().getXCoord() != 0 || map.getParams().getZCoord() != -2)
			return;
		Vector<Center> needZones = map.filterKeepAttributes(map.filterOutMarkers(map.centers, Marker.Ocean), Attribute.NeedZone);

		for(String animalType : map.getParams().animalTypes)
		{
			IAnimalDef def = AnimalSpawnRegistry.getInstance().getDefFromName(animalType);
			Vector<Center> myNeedZones = new Vector<Center>();
			//Filter out only our own need zones
			for(Center c : needZones)
			{
				NeedZoneAttribute attrib = (NeedZoneAttribute) c.getAttribute(Attribute.NeedZone);
				if(attrib.animalType.equals(def.getName()))
					myNeedZones.add(c);
			}

			int numToGen = def.getMaxIslandPop(map.getParams());
			while(numToGen > 0)
			{
				ArrayList<VirtualAnimal> list = def.provideHerd(world);
				if(list.size() > 0)
				{
					numToGen -= list.size();
					Herd h = new Herd(def.getName(), myNeedZones.get(map.mapRandom.nextInt(myNeedZones.size())));
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
			if(h.brain.currentLocation == c)
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

	public static class Herd
	{
		UUID uuid = UUID.randomUUID();
		String animalType;
		ArrayList<VirtualAnimal> animals = new ArrayList<VirtualAnimal>();
		HerdBrain brain;
		boolean isLoaded = false;//This doesnt need to be saved to disk

		public Herd(String type, Center curLoc)
		{
			animalType = type;
			brain = new HerdBrain(this,curLoc);
		}

		public String getAnimalType()
		{
			return animalType;
		}

		public HerdBrain getHerdBrain()
		{
			return brain;
		}

		public UUID getUUID()
		{
			return uuid;
		}

		public boolean isLoaded()
		{
			return isLoaded;
		}

		public void setLoaded()
		{
			isLoaded = true;
		}

		public void setUnloaded()
		{
			isLoaded = false;
		}

		public VirtualAnimal addAnimal(Gender g)
		{
			VirtualAnimal a = new VirtualAnimal(animalType, g);
			animals.add(a);
			return a;
		}

		public ArrayList<VirtualAnimal> getVirtualAnimals()
		{
			return this.animals;
		}

		public void addAllAnimals(ArrayList<VirtualAnimal> list)
		{
			animals.addAll(list);
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			NBTTagList invList = nbt.getTagList("animalList", 10);
			for(int i = 0; i < invList.tagCount(); i++)
			{
				VirtualAnimal a = new VirtualAnimal("", null);
				a.readFromNBT((NBTTagCompound)invList.get(i));
				animals.add(a);
			}
			brain.readFromNBT(nbt, map);
			animalType = nbt.getString("animalType");
			uuid = nbt.getUniqueId("uuid");
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			NBTTagList animalList = new NBTTagList();
			for(int i = 0; i < animals.size(); i++)
			{
				VirtualAnimal a = animals.get(i);
				NBTTagCompound animalTag = new NBTTagCompound();
				a.writeToNBT(animalTag);
				animalList.appendTag(animalTag);
			}
			nbt.setTag("animalList", animalList);
			brain.writeToNBT(nbt);
			nbt.setString("animalType", animalType);
			nbt.setUniqueId("uuid", uuid);
		}
	}

	public static class HerdBrain
	{
		Herd herd;
		HerdGoal currentGoal;
		HerdActivityEnum currentActivity;
		int activityTimer;
		Center currentLocation;

		int hoursWaitingOnHerd = 0;

		public HerdBrain(Herd h, Center curLoc)
		{
			herd = h;
			activityTimer = 1;
			currentActivity = HerdActivityEnum.WORKING;
			currentLocation = curLoc;
			currentGoal = new HerdGoal(HerdGoalEnum.REST, curLoc);
		}

		public void process(World world, IslandMap map, long currentHour)
		{
			activityTimer--;
			if(activityTimer <= 0)
			{
				if(currentActivity == HerdActivityEnum.WORKING)//The herd is ready to leave the current need zone
				{
					activityTimer = 1;

					//========Create a new goal========
					//1. Decide what the herd needs
					HerdGoalEnum goal = HerdGoalEnum.REST;
					if(currentGoal.goalType == HerdGoalEnum.REST)
					{
						goal = world.rand.nextBoolean() ? HerdGoalEnum.FOOD : HerdGoalEnum.WATER;
					}
					else if(currentGoal.goalType == HerdGoalEnum.FOOD)
					{
						goal = world.rand.nextBoolean() ? HerdGoalEnum.REST : HerdGoalEnum.WATER;
					}
					else
					{
						goal = world.rand.nextBoolean() ? HerdGoalEnum.REST : HerdGoalEnum.FOOD;
					}
					//2. Find an appropriate location to fill this need
					Vector<Center> allZones = map.filterKeepAttributes(map.centers, Attribute.NeedZone);
					Vector<Center> goalZones = new Vector<Center>();
					for(Center z : allZones)
					{
						NeedZoneAttribute attrib = (NeedZoneAttribute) z.getAttribute(Attribute.NeedZone);
						if(attrib.goalType == goal)
							goalZones.add(z);
					}
					Center goalLoc = goalZones.get(world.rand.nextInt(goalZones.size()));
					//3. Figure out a path to reach the destination
					CenterPathFinder pathfinder = new CenterPathFinder(AnimalSpawnRegistry.getInstance().getDefFromName(herd.animalType).getPathProfile());
					CenterPath path = pathfinder.findPath(map, currentLocation, goalLoc);
					//4. Move the herd
					if(path == null)
						return;
					currentGoal = new HerdGoal(goal, goalLoc, new HerdPath(currentHour, path));
					currentActivity = HerdActivityEnum.TRAVELING;
				}
				else if(currentActivity == HerdActivityEnum.TRAVELING)//The herd is actively moving to a new needzone
				{
					if(herd.isLoaded())
					{
						ArrayList<VirtualAnimal> animalsToRemove = new ArrayList<VirtualAnimal>();
						for(VirtualAnimal animal : herd.animals)
						{
							if(animal.getEntity() == null)
							{
								animalsToRemove.add(animal);
							}
							else
							{
								Center c = map.getClosestCenter(animal.getEntity().getPosition());
								if(c != currentLocation)
								{
									if(hoursWaitingOnHerd < 3)
									{
										hoursWaitingOnHerd++;
										activityTimer = 1;
										return;
									}
									else
									{
										animalsToRemove.add(animal);
									}
								}
							}
						}

						for(VirtualAnimal animal : animalsToRemove)
						{
							herd.animals.remove(animal);
						}
					}
					hoursWaitingOnHerd = 0;
					currentLocation = currentGoal.path.move();
					activityTimer = 1;
					if(currentGoal.goalLocation == currentLocation)
					{
						activityTimer = 24;
						currentActivity = HerdActivityEnum.WORKING;
					}
				}
				else if(currentActivity == HerdActivityEnum.WAITING)//Something interrupted this herd while it was traveling and it should recalculate its route
				{
					activityTimer = 2;
					currentLocation = currentGoal.path.move();
					currentGoal.path.recalculatePath(currentLocation);
				}
				else if(currentActivity == HerdActivityEnum.FLEEING)//If we have recently been fleeing from danger we should reset to waiting for now
				{
					activityTimer = 1;
					currentActivity = HerdActivityEnum.WAITING;
				}
			}
		}

		public HerdActivityEnum getActivity()
		{
			return currentActivity;
		}

		public void setLocation(Center center)
		{
			currentLocation = center;
		}

		public Center getLocation()
		{
			return currentLocation;
		}

		public void setGoal(HerdGoalEnum goal, Center loc)
		{
			currentGoal = new HerdGoal(goal, loc);
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			currentActivity = HerdActivityEnum.getEnum(nbt.getString("currentActivity"));
			activityTimer = nbt.getInteger("activityTimer");
			currentLocation = map.centers.get(nbt.getInteger("currentLocation"));
			currentGoal = new HerdGoal(HerdGoalEnum.REST, currentLocation);
			currentGoal.readFromNBT(nbt.getCompoundTag("currentGoal"), map);
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setInteger("activityTimer", activityTimer);

			NBTTagCompound goalNBT = new NBTTagCompound();
			currentGoal.writeToNBT(goalNBT);
			nbt.setTag("currentGoal", goalNBT);

			nbt.setInteger("currentLocation", currentLocation.index);
			nbt.setString("currentActivity", currentActivity.getName());
		}
	}

	public static class HerdPath
	{
		long calcTimestamp;
		LinkedList<Center> path = new LinkedList<Center>();

		public HerdPath(long timestamp)
		{
			calcTimestamp = timestamp;
		}

		public HerdPath(long timestamp, CenterPath cPath)
		{
			this(timestamp);
			for(CenterPathNode c : cPath.path)
			{
				addNode(c.center);
			}
		}

		public void addNode(Center c)
		{
			path.add(c);
		}

		public void recalculatePath(Center start)
		{

		}

		public Center move()
		{
			return path.removeLast();
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			this.calcTimestamp = nbt.getLong("timestamp");
			int[] pathArray = nbt.getIntArray("path");
			for(int i = 0; i < pathArray.length; i++)
			{
				path.add(map.centers.get(pathArray[i]));
			}
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setLong("timestamp", calcTimestamp);
			int[] pathArray = new int[path.size()];

			for(int i = 0; i < path.size(); i++)
			{
				pathArray[i] = path.get(i).index;
			}

			nbt.setIntArray("path", pathArray);
		}
	}

	public static class HerdGoal
	{
		HerdGoalEnum goalType;
		Center goalLocation;
		HerdPath path;

		public HerdGoal(HerdGoalEnum goal, Center loc)
		{
			goalType = goal;
			goalLocation = loc;
			path = new HerdPath(0L);
		}

		public HerdGoal(HerdGoalEnum goal, Center loc, HerdPath p)
		{
			this(goal, loc);
			path = p;
		}

		public void updateHerdPath(HerdPath p)
		{
			path = p;
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			goalType = HerdGoalEnum.getEnum(nbt.getString("goalType"));
			goalLocation = map.centers.get(nbt.getInteger("goalLocation"));
			HerdPath path = new HerdPath(0);
			path.readFromNBT(nbt.getCompoundTag("path"), map);
			this.path = path;
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setInteger("goalLocation", goalLocation.index);
			nbt.setString("goalType", goalType.getName());
			if(path != null){
				NBTTagCompound pathNBT = new NBTTagCompound();
				path.writeToNBT(pathNBT);
				nbt.setTag("path", pathNBT);
			}
		}
	}

	public static enum HerdActivityEnum implements IStringSerializable
	{
		TRAVELING("traveling"), WAITING("waiting"), FLEEING("fleeing"), WORKING("working");
		String name;

		HerdActivityEnum(String s)
		{
			name = s;
		}

		@Override
		public String getName() {
			return name;
		}

		public static HerdActivityEnum getEnum(String s)
		{
			for(HerdActivityEnum e : values())
			{
				if(e.name.equals(s))
					return e;
			}
			throw new IllegalArgumentException();
		}
	}

	public static enum HerdGoalEnum implements IStringSerializable
	{
		FOOD, WATER, REST;

		@Override
		public String getName() {
			if(this == FOOD)
				return "food";
			else if(this == WATER)
				return "water";
			else
				return "rest";
		}

		public static HerdGoalEnum getEnum(String s)
		{
			if(s.equals("rest"))
				return REST;
			else if(s.equals("food"))
				return FOOD;
			else if(s.equals("water"))
				return WATER;
			throw new IllegalArgumentException();
		}
	}


}
