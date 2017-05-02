package com.bioxx.jmapgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.api.types.Gender;

public class IslandWildlifeManager 
{
	HashMap<UUID, Herd> herdMap = new HashMap<UUID, Herd>();
	long lastTickHour = -1;
	IslandMap map;

	public IslandWildlifeManager(IslandMap map)
	{
		this.map = map;
	}

	public void addHerd(Herd h)
	{
		herdMap.put(h.uuid, h);
	}

	public Herd getHerd(UUID uuid)
	{
		return herdMap.get(uuid);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		herdMap .clear();
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
		}
		nbt.setTag("herds", herdList);
		nbt.setLong("lastTickHour", lastTickHour);
	}

	public void process(World world, long currentHour)
	{
		if(currentHour > lastTickHour)
		{
			lastTickHour++;
			ArrayList<UUID> herdsToRemove = new ArrayList<UUID>();
			for(Herd h : herdMap.values())
			{
				h.brain.process(world, map, currentHour);
				//Mark herd for deletion if all of the animals are gone.
				if(h.animals.size() == 0)
					herdsToRemove.add(h.uuid);
			}

			//Cull empty herds
			for(UUID uuid : herdsToRemove)
			{
				herdMap.remove(uuid);
			}
		}
	}

	public static class Herd
	{
		UUID uuid = UUID.randomUUID();
		String animalType;
		ArrayList<Animal> animals = new ArrayList<Animal>();
		HerdBrain brain;

		public Herd(String type, Center curLoc)
		{
			animalType = type;
			brain = new HerdBrain(curLoc);
		}

		public Animal addAnimal(Gender g)
		{
			Animal a = new Animal(animalType, g);
			animals.add(a);
			return a;
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			NBTTagList invList = nbt.getTagList("animalList", 10);
			for(int i = 0; i < invList.tagCount(); i++)
			{
				Animal a = new Animal("", null);
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
				Animal a = animals.get(i);
				NBTTagCompound animalTag = new NBTTagCompound();
				a.writeToNBT(animalTag);
				animalList.set(i, animalTag);
			}
			nbt.setTag("animalList", animalList);
			brain.writeToNBT(nbt);
			nbt.setString("animalType", animalType);
			nbt.setUniqueId("uuid", uuid);
		}
	}

	public static class HerdBrain
	{
		HerdGoal currentGoal;
		HerdActivityEnum currentActivity;
		int activityTimer;
		Center currentLocation;

		public HerdBrain(Center curLoc)
		{
			activityTimer = 0;
			currentActivity = HerdActivityEnum.WAITING;
			currentLocation = curLoc;
			currentGoal = new HerdGoal(HerdGoalEnum.REST, curLoc);
		}

		public void process(World world, IslandMap map, long currentHour)
		{
			activityTimer--;
			if(activityTimer == 0)
			{
				activityTimer = 1;

				//========Create a new goal========
				//1. Decide what the herd needs
				HerdGoalEnum goal = HerdGoalEnum.REST;
				if(currentGoal.goalType == HerdGoalEnum.REST)
				{
					goal = world.rand.nextBoolean() ? HerdGoalEnum.FOOD : HerdGoalEnum.WATER;
				}
				//2. Find an appropriate location to fill this need

				//3. Figure out a path to reach the destination

				//4. Move the herd
			}
		}

		public void setLocation(Center center)
		{
			currentLocation = center;
		}

		public void setGoal(HerdGoalEnum goal, Center loc)
		{
			currentGoal = new HerdGoal(goal, loc);
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			currentActivity = HerdActivityEnum.valueOf(nbt.getString("currentActivity"));
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
		LinkedList<Center> path = new LinkedList<Center>();

		public void addNode(Center c)
		{
			path.add(c);
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			int[] pathArray = nbt.getIntArray("path");
			for(int i = 0; i < pathArray.length; i++)
			{
				path.add(map.centers.get(pathArray[i]));
			}
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
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
			path = null;
		}

		public void readFromNBT(NBTTagCompound nbt, IslandMap map)
		{
			goalType = HerdGoalEnum.valueOf(nbt.getString("goalType"));
			goalLocation = map.centers.get(nbt.getInteger("goalLocation"));
			HerdPath path = new HerdPath();
			path.readFromNBT(nbt.getCompoundTag("path"), map);
			this.path = path;
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setInteger("goalLocation", goalLocation.index);
			nbt.setString("goalType", goalType.getName());
			NBTTagCompound pathNBT = new NBTTagCompound();
			path.writeToNBT(pathNBT);
			nbt.setTag("path", pathNBT);
		}
	}

	public static enum HerdActivityEnum implements IStringSerializable
	{
		TRAVELING, WAITING;

		@Override
		public String getName() {
			if(this == TRAVELING)
				return "traveling";
			else
				return "waiting";
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
	}

	public static class Animal
	{
		String animalType;
		Gender gender;

		public Animal(String type, Gender g)
		{
			animalType = type;
			gender = g;
		}

		public void readFromNBT(NBTTagCompound nbt)
		{
			animalType = nbt.getString("type");
			gender = Gender.valueOf(nbt.getString("gender"));
		}

		public void writeToNBT(NBTTagCompound nbt)
		{
			nbt.setString("type", animalType);
			nbt.setString("gender", gender.getName());
		}
	}
}
