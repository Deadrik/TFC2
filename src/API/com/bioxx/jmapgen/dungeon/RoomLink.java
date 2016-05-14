package com.bioxx.jmapgen.dungeon;

import net.minecraft.nbt.NBTTagCompound;

public class RoomLink
{
	public boolean placeDoor;

	public RoomLink(boolean door)
	{
		placeDoor = door;
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("placeDoor", this.placeDoor);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		placeDoor = nbt.getBoolean("placeDoor");
	}
}