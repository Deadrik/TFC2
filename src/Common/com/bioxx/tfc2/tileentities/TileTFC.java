package com.bioxx.tfc2.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileTFC extends TileEntity 
{
	@Override
	@Deprecated
	public void readFromNBT(NBTTagCompound compound)
	{
		super.readFromNBT(compound);
		if(getTileData().hasNoTags())
			getTileData().merge(compound.getCompoundTag("TFC2Data"));
		readSyncableNBT(getTileData());
		readNonSyncableNBT(getTileData());
	}

	public abstract void readSyncableNBT(NBTTagCompound compound);

	public abstract void readNonSyncableNBT(NBTTagCompound compound);

	@Override
	@Deprecated
	public void writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		writeSyncableNBT(getTileData());
		writeNonSyncableNBT(getTileData());
		compound.setTag("TFC2Data", getTileData());
	}

	/**
	 * Any Tags saved here should be synced to clients
	 */
	public abstract void writeSyncableNBT(NBTTagCompound compound);

	/**
	 * Any Tags saved here will only be known serverside
	 */
	public abstract void writeNonSyncableNBT(NBTTagCompound compound);

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.S35PacketUpdateTileEntity pkt)
	{
		this.readSyncableNBT(pkt.getNbtCompound());
	}

	/**
	 * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
	 * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
	 */
	@Override
	public Packet getDescriptionPacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeSyncableNBT(nbt);
		return new S35PacketUpdateTileEntity(this.pos, this.getBlockMetadata(), nbt);
	}
}
