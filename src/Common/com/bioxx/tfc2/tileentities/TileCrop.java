package com.bioxx.tfc2.tileentities;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;

import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.core.Timekeeper;

public class TileCrop extends TileTFC implements IUpdatePlayerListBox
{
	long plantedTimeStamp = 0;
	boolean isWild;
	Crop cropType;
	UUID farmerID;

	public TileCrop()
	{
		isWild = false;
	}

	public TileCrop(boolean isWild)
	{
		this.isWild = isWild;
	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		Timekeeper time = Timekeeper.getInstance();
		if(time.getTotalTicks() > plantedTimeStamp + Timekeeper.HOUR_LENGTH)
		{
			plantedTimeStamp += Timekeeper.HOUR_LENGTH;

			//TODO Grow stuff here
		}
	}

	/***********************************************************************************
	 * 2. Getters and Setters
	 ***********************************************************************************/
	public boolean getIsWild()
	{
		return isWild;
	}

	public void setIsWild(boolean w)
	{
		isWild = w;
	}

	public Crop getCropType()
	{
		return this.cropType;
	}

	public void setCropType(Crop c)
	{
		this.cropType = c;
	}

	public void setFarmerID(EntityPlayer player)
	{
		farmerID = EntityPlayer.getUUID(player.getGameProfile());
	}

	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{
		cropType = Crop.fromID(nbt.getInteger("cropType"));
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{
		isWild = nbt.getBoolean("isWild");
		plantedTimeStamp = nbt.getLong("plantedTimeStamp");
	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("cropType", this.cropType.getID());
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("isWild", isWild);
		nbt.setLong("plantedTimeStamp", plantedTimeStamp);
	}
}
