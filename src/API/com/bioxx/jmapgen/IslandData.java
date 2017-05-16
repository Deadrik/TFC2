package com.bioxx.jmapgen;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

import com.bioxx.tfc2.api.WildlifeManager;
import com.bioxx.tfc2.api.types.PortalEnumType;

public class IslandData 
{
	public boolean isIslandUnlocked = false;
	public PortalEnumType northPortalState = PortalEnumType.Disabled;
	public PortalEnumType southPortalState = PortalEnumType.Disabled;
	public PortalEnumType eastPortalState = PortalEnumType.Disabled;
	public PortalEnumType westPortalState = PortalEnumType.Disabled;
	public int islandLevel = 0;
	public WildlifeManager wildlifeManager;

	public IslandData(IslandMap map, IslandParameters params)
	{
		wildlifeManager = new WildlifeManager(map);
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
		wildlifeManager.readFromNBT(nbt.getCompoundTag("wildlifeManager"));
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("isIslandUnlocked", isIslandUnlocked);
		nbt.setInteger("northPortalState", northPortalState.ordinal());
		nbt.setInteger("southPortalState", southPortalState.ordinal());
		nbt.setInteger("eastPortalState", eastPortalState.ordinal());
		nbt.setInteger("westPortalState", westPortalState.ordinal());
		nbt.setInteger("islandLevel", islandLevel);

		NBTTagCompound wmNBT = new NBTTagCompound();
		wildlifeManager.writeToNBT(wmNBT);
		nbt.setTag("wildlifeManager", wmNBT);
	}
}
