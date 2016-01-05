package com.bioxx.tfc2.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileTorch extends TileEntity
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
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if (compound.hasKey("TFC2Data"))
		{
			this.torchTileData = compound.getCompoundTag("TFC2Data");
			torchTimer = torchTileData.getInteger("torchTimer");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		torchTileData.setInteger("torchTimer", torchTimer);
		compound.setTag("TFC2Data", this.torchTileData);
	}

	/**
	 * Gets a {@link NBTTagCompound} that can be used to store custom data for this tile entity.
	 * It will be written, and read from disc, so it persists over world saves.
	 *
	 * @return A compound tag for custom data
	 */
	@Override
	public NBTTagCompound getTileData()
	{
		if (this.torchTileData == null)
		{
			this.torchTileData = new NBTTagCompound();
		}
		return this.torchTileData;
	}

	/**
	 * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
	 * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(this.pos, 0, nbt);
	}
}
