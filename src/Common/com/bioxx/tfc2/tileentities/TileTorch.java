package com.bioxx.tfc2.tileentities;

import net.minecraft.nbt.NBTTagCompound;

public class TileTorch extends TileTFC
{
	private int torchTimer;
	NBTTagCompound torchTileData;

	public TileTorch()
	{
		torchTileData = new NBTTagCompound();
	}

	public TileTorch(int timer)
	{
		torchTimer = timer;
		torchTileData = new NBTTagCompound();
	}

	public void setTimer(int time)
	{
		this.torchTimer = time;
	}

	public int getTimer()
	{
		return torchTimer;
	}

	@Override
	public void readSyncableNBT(NBTTagCompound compound) 
	{
		torchTimer = compound.getInteger("torchTimer");
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound compound) {

	}

	@Override
	public void writeSyncableNBT(NBTTagCompound compound) {
		compound.setInteger("torchTimer", torchTimer);
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound compound) {

	}
}
