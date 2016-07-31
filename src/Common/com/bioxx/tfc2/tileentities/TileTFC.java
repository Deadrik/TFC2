package com.bioxx.tfc2.tileentities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
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

	public abstract void readSyncableNBT(NBTTagCompound nbt);

	public abstract void readNonSyncableNBT(NBTTagCompound nbt);

	@Override
	@Deprecated
	public NBTTagCompound writeToNBT(NBTTagCompound compound)
	{
		super.writeToNBT(compound);
		writeSyncableNBT(getTileData());
		writeNonSyncableNBT(getTileData());
		compound.setTag("TFC2Data", getTileData());
		return compound;
	}

	/**
	 * Any Tags saved here should be synced to clients
	 */
	public abstract void writeSyncableNBT(NBTTagCompound nbt);

	/**
	 * Any Tags saved here will only be known serverside
	 */
	public abstract void writeNonSyncableNBT(NBTTagCompound nbt);

	@Override
	public void onDataPacket(net.minecraft.network.NetworkManager net, SPacketUpdateTileEntity pkt)
	{
		this.readSyncableNBT(pkt.getNbtCompound());
		getWorld().markBlockRangeForRenderUpdate(getPos(), getPos().add(1, 1, 1));
	}

	/**
	 * Allows for a specialized description packet to be created. This is often used to sync tile entity data from the
	 * server to the client easily. For example this is used by signs to synchronise the text to be displayed.
	 */
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		this.writeSyncableNBT(nbt);
		return new SPacketUpdateTileEntity(this.pos, this.getBlockMetadata(), nbt);
	}
}
