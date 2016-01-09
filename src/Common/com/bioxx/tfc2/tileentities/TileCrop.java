package com.bioxx.tfc2.tileentities;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.core.Timekeeper;

public class TileCrop extends TileTFC implements IUpdatePlayerListBox
{
	long plantedTimeStamp = 0;
	long lastTick = 0;
	double growth = 0;
	boolean isWild = false;
	Crop cropType = Crop.Corn;
	UUID farmerID;
	Center closestHex;

	public TileCrop()
	{
		plantedTimeStamp = Timekeeper.getInstance().getTotalTicks();
	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		Timekeeper time = Timekeeper.getInstance();
		if(time.getTotalTicks() > lastTick + Timekeeper.HOUR_LENGTH)
		{
			lastTick += Timekeeper.HOUR_LENGTH;

			if(this.closestHex == null)
			{
				IslandMap map = Core.getMapForWorld(getWorld(), getPos());
				closestHex = map.getClosestCenter(getPos());
			}


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

	public Center getClosestHex()
	{
		return closestHex;
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
		farmerID = new UUID(nbt.getLong("farmerID_least"), nbt.getLong("farmerID_most"));
		this.closestHex = Core.getMapForWorld(getWorld(), getPos()).centers.get(nbt.getInteger("hexID"));
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
		nbt.setLong("farmerID_least", this.farmerID.getLeastSignificantBits());
		nbt.setLong("farmerID_most", this.farmerID.getMostSignificantBits());
		nbt.setInteger("hexID", this.closestHex.index);
	}
}
