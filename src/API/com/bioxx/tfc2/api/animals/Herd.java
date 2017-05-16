package com.bioxx.tfc2.api.animals;

import java.util.ArrayList;
import java.util.UUID;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.api.types.Gender;

public class Herd
{
	UUID uuid = UUID.randomUUID();
	public String animalType;
	public ArrayList<VirtualAnimal> animals = new ArrayList<VirtualAnimal>();
	public IHerdBrain brain;
	boolean isLoaded = false;//This doesnt need to be saved to disk

	public Herd(String type, Center curLoc)
	{
		animalType = type;
		brain = new MigrationBrain(this,curLoc);
	}

	public String getAnimalType()
	{
		return animalType;
	}

	public IHerdBrain getHerdBrain()
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