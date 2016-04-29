package com.bioxx.jmapgen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnEntry;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;
import com.bioxx.tfc2.api.types.PortalEnumType;

public class IslandData 
{
	public boolean isIslandUnlocked = false;
	public PortalEnumType northPortalState = PortalEnumType.Disabled;
	public PortalEnumType southPortalState = PortalEnumType.Disabled;
	public PortalEnumType eastPortalState = PortalEnumType.Disabled;
	public PortalEnumType westPortalState = PortalEnumType.Disabled;
	public int islandLevel = 0;
	public Map<String, SpawnEntry> animalEntries = new HashMap<String, SpawnEntry>();

	public IslandData(IslandParameters params)
	{
		Iterator iter = params.animalSpawnGroups.iterator();
		while(iter.hasNext())
		{
			SpawnGroup group = (SpawnGroup) iter.next();
			SpawnEntry entry = new SpawnEntry(group.getGroupName(), group.getMaxPopulation());
			animalEntries.put(group.getGroupName(), entry);
		}
	}

	public void unlockIsland()
	{
		isIslandUnlocked = true;
		northPortalState = PortalEnumType.Enabled;
		southPortalState = PortalEnumType.Enabled;
		eastPortalState = PortalEnumType.Enabled;
		westPortalState = PortalEnumType.Enabled;
	}

	public void enablePortal(EnumFacing facing)
	{
		if(facing == EnumFacing.NORTH)
			northPortalState = PortalEnumType.Enabled;
		else if(facing == EnumFacing.SOUTH)
			southPortalState = PortalEnumType.Enabled;
		else if(facing == EnumFacing.EAST)
			eastPortalState = PortalEnumType.Enabled;
		else if(facing == EnumFacing.WEST)
			westPortalState = PortalEnumType.Enabled;
	}

	public PortalEnumType getPortalState(EnumFacing facing)
	{
		if(facing == EnumFacing.NORTH)
			return northPortalState;
		else if(facing == EnumFacing.SOUTH)
			return southPortalState;
		else if(facing == EnumFacing.EAST)
			return eastPortalState;
		else if(facing == EnumFacing.WEST)
			return westPortalState;

		return PortalEnumType.Disabled;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		isIslandUnlocked = nbt.getBoolean("isIslandUnlocked");
		northPortalState = PortalEnumType.values()[nbt.getInteger("northPortalState")];
		southPortalState = PortalEnumType.values()[nbt.getInteger("southPortalState")];
		eastPortalState = PortalEnumType.values()[nbt.getInteger("eastPortalState")];
		westPortalState = PortalEnumType.values()[nbt.getInteger("westPortalState")];
		islandLevel = nbt.getInteger("islandLevel");

		NBTTagCompound fnbt = nbt.getCompoundTag("animalEntries");
		Iterator iter = fnbt.getKeySet().iterator();
		while(iter.hasNext())
		{
			String key = (String)iter.next();
			NBTTagCompound gnbt = fnbt.getCompoundTag(key);
			SpawnEntry entry = new SpawnEntry(key, gnbt.getInteger("available"), gnbt.getInteger("current"));
			animalEntries.put(key, entry);
		}

	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("isIslandUnlocked", isIslandUnlocked);
		nbt.setInteger("northPortalState", northPortalState.ordinal());
		nbt.setInteger("southPortalState", southPortalState.ordinal());
		nbt.setInteger("eastPortalState", eastPortalState.ordinal());
		nbt.setInteger("westPortalState", westPortalState.ordinal());
		nbt.setInteger("islandLevel", islandLevel);
		NBTTagCompound fnbt = new NBTTagCompound();
		Iterator iter = animalEntries.keySet().iterator();
		while(iter.hasNext())
		{
			String group = (String)iter.next();
			SpawnEntry entry = animalEntries.get(group);
			NBTTagCompound gnbt = new NBTTagCompound();
			gnbt.setInteger("available", entry.getAmountToSpawn());
			gnbt.setInteger("current", entry.getTotalPopulation());
			fnbt.setTag(group, gnbt);
		}
		nbt.setTag("animalEntries", fnbt);
	}
}
