package com.bioxx.tfc2.tileentities;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.api.Crop;
import com.bioxx.tfc2.core.Timekeeper;

public class TileCrop extends TileTFC implements ITickable
{
	/**
	 * With this amount of nutrients and a replenishment of 25% per month 
	 * we can support 400 crops for exactly 4 months. If we reduce this to 
	 * 16.6666%, it takes 6 months to fully replenish and we can only support 
	 * 333 for 4 full months. We need to balance this for how much food that 
	 * we intend for players to need to eat.
	 */
	public static float DEFAULT_NUTRIENTS = 153600f;//192 hours in 1 month * 800 crops

	long plantedTimeStamp = 0;
	long lastTick = 0;
	float growth = 0;
	boolean isWild = false;
	Crop cropType = Crop.Corn;
	UUID farmerID;
	int hexID = -1;
	Center closestHex;

	public TileCrop()
	{
		plantedTimeStamp = Timekeeper.getInstance().getTotalTicks();
		lastTick = plantedTimeStamp;
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
			IslandMap map = Core.getMapForWorld(getWorld(), getPos());
			if(this.closestHex == null && hexID < 0)
			{
				closestHex = map.getClosestCenter(getPos());
				hexID = closestHex.index;
			}
			else if(this.closestHex == null)
			{
				this.closestHex = Core.getMapForWorld(getWorld(), getPos()).centers.get(hexID);
			}

			NBTTagCompound nbt = closestHex.getCustomNBT().getCompoundTag("TFC2_Data");
			NBTTagCompound data;
			if(!nbt.hasKey("CropData"))
				data = new NBTTagCompound();
			else
				data = nbt.getCompoundTag("CropData");

			if(!data.hasKey("nutrients"))
				data.setFloat("nutrients", GetMaxNutrients(map));

			byte[] hydrationArray = nbt.getByteArray("hydration");
			int hydraY = Math.min((int)Math.floor(pos.getY()/4), 64);
			boolean isIrrigated = hydrationArray.length == 0 ? false : (hydrationArray[hydraY] & 0xFF) > 100;
			float nutrients = data.getFloat("nutrients");
			float toGrow = 1f;

			if(nutrients < 0)
				toGrow -= 0.25f;
			if(nutrients < -19200)//48 hours over
				toGrow -= 0.25f;
			if(nutrients < -38400)//96 hours over
				toGrow -= 0.25f;
			if(!isIrrigated)
				toGrow -= 0.25f;
			nutrients -= 1;
			growth += toGrow;
			world.getMinecraftServer().getPlayerList().sendToAllNearExcept(null, pos.getX(), pos.getY(), pos.getZ(), 200, getWorld().provider.getDimension(), this.getUpdatePacket());
		}
	}

	public static float GetMaxNutrients(IslandMap map)
	{
		if(map.getParams().hasFeature(Feature.NutrientRich))
			return TileCrop.DEFAULT_NUTRIENTS * 2;
		return TileCrop.DEFAULT_NUTRIENTS;
	}

	public int getGrowthStage()
	{
		float hoursToGrow = 24*cropType.getGrowthPeriod();
		return Math.min((int)Math.floor(growth / hoursToGrow * cropType.getGrowthStages()), cropType.getGrowthStages()-1);
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

	public float getGrowth()
	{
		return growth;
	}

	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{
		cropType = Crop.fromID(nbt.getInteger("cropType"));
		growth = nbt.getFloat("growth");
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{
		isWild = nbt.getBoolean("isWild");
		plantedTimeStamp = nbt.getLong("plantedTimeStamp");
		farmerID = new UUID(nbt.getLong("farmerID_least"), nbt.getLong("farmerID_most"));
		this.hexID = nbt.getInteger("hexID");
	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("cropType", this.cropType.getID());
		nbt.setFloat("growth", growth);
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setBoolean("isWild", isWild);
		nbt.setLong("plantedTimeStamp", plantedTimeStamp);
		nbt.setLong("farmerID_least", this.farmerID.getLeastSignificantBits());
		nbt.setLong("farmerID_most", this.farmerID.getMostSignificantBits());
		nbt.setInteger("hexID", hexID);
	}
}
