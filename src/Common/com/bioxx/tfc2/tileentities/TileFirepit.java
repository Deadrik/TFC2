package com.bioxx.tfc2.tileentities;

import java.util.UUID;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.blocks.BlockFirepit;
import com.bioxx.tfc2.core.Timekeeper;

public class TileFirepit extends TileTFC implements ITickable, IInventory
{
	UUID workerID;
	NonNullList<ItemStack> inventory = NonNullList.<ItemStack>withSize(11, ItemStack.EMPTY);
	ItemStack cookingTool = ItemStack.EMPTY;

	public TileFirepit()
	{
		workerID = new UUID(0L, 0L);
	}

	/***********************************************************************************
	 * 1. Content
	 ***********************************************************************************/
	@Override
	public void update() 
	{
		Timekeeper time = Timekeeper.getInstance();

	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
	{
		return false;
	}

	public void ejectContents(boolean ejectFuel)
	{
		if(ejectFuel)
		{
			InventoryHelper.spawnItemStack(world, getPos().getX(), getPos().getY(), getPos().getZ(), this.getStackInSlot(0));
			this.setInventorySlotContents(0, ItemStack.EMPTY);
		}
		else
		{
			for(int i = 1; i < this.getSizeInventory(); i++)
			{
				InventoryHelper.spawnItemStack(world, getPos().getX(), getPos().getY(), getPos().getZ(), this.getStackInSlot(i));
				this.setInventorySlotContents(i, ItemStack.EMPTY);
			}
		}

	}

	/***********************************************************************************
	 * 2. Getters and Setters
	 ***********************************************************************************/

	public void setWorkerID(EntityPlayer player)
	{
		workerID = EntityPlayer.getUUID(player.getGameProfile());
	}

	public EntityPlayer getWorker()
	{
		if(!world.isRemote)
			return world.getMinecraftServer().getPlayerList().getPlayerByUUID(workerID);
		else return TFC.proxy.getPlayer();
	}

	public NonNullList<ItemStack> getInventory()
	{
		return this.inventory;
	}

	public boolean hasCookingTool()
	{
		if(ItemStack.areItemsEqual(this.getStackInSlot(1), new ItemStack(BlockFirepit.potItem)))
			return true;
		else if(ItemStack.areItemsEqual(this.getStackInSlot(1), new ItemStack(BlockFirepit.skilletItem)))
			return true;
		else if(ItemStack.areItemsEqual(this.getStackInSlot(1), new ItemStack(BlockFirepit.saucepanItem)))
			return true;
		return false;
	}

	public ItemStack getCookingTool()
	{
		return this.getStackInSlot(1);
	}

	public void setCookingTool(ItemStack tool)
	{
		this.setInventorySlotContents(1, tool);
		world.setBlockState(getPos(), this.getBlockType().getExtendedState(world.getBlockState(getPos()), world, getPos()));
	}

	/***********************************************************************************
	 * 3. NBT Methods
	 ***********************************************************************************/
	@Override
	public void readSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = nbt.getTagList("inventory", 10);
		inventory = Helper.readStackArrayFromNBTList(invList, getSizeInventory());
	}

	@Override
	public void readNonSyncableNBT(NBTTagCompound nbt)
	{
		workerID = new UUID(nbt.getLong("workerID_least"), nbt.getLong("workerID_most"));
	}

	@Override
	public void writeSyncableNBT(NBTTagCompound nbt)
	{
		NBTTagList invList = Helper.writeStackArrayToNBTList(inventory);
		nbt.setTag("inventory", invList);
	}

	@Override
	public void writeNonSyncableNBT(NBTTagCompound nbt)
	{
		nbt.setLong("workerID_least", this.workerID.getLeastSignificantBits());
		nbt.setLong("workerID_most", this.workerID.getMostSignificantBits());

	}

	/*********************************************************
	 * IInventory Implementation
	 *********************************************************/
	@Override
	public String getName() {
		return "Firepit";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public int getSizeInventory() {
		return 11;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		if(index < getSizeInventory())
			return inventory.get(index);
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack decrStackSize(int index, int count) 
	{
		if(inventory.get(index) != ItemStack.EMPTY)
		{
			if(inventory.get(index).getMaxStackSize() <= count)
			{
				ItemStack itemstack = inventory.get(index);
				inventory.set(index, ItemStack.EMPTY);
				TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
				return itemstack;
			}
			ItemStack itemstack1 = inventory.get(index).splitStack(count);
			if(inventory.get(index).getMaxStackSize() == 0)
				inventory.set(index, ItemStack.EMPTY);
			return itemstack1;
		}
		else
			return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) 
	{
		if(index < getSizeInventory())
		{
			ItemStack out = inventory.get(index);
			inventory.set(index, ItemStack.EMPTY);
			TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
			return out;
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) 
	{
		if(index < getSizeInventory())
		{
			inventory.set(index, stack);
			TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());//Is this needed?
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}

	@Override
	public int getInventoryStackLimit() 
	{
		return 1;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) 
	{
		TFC.proxy.sendToAllNear(getWorld(), getPos(), 200, this.getUpdatePacket());
		player.world.markBlockRangeForRenderUpdate(getPos(), getPos().add(1, 1, 1));
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {return true;}

	@Override
	public int getField(int id) 
	{
		return -1;
	}

	@Override
	public void setField(int id, int value) 
	{

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {

	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	/*********************************************************
	 * Associated Classes
	 *********************************************************/



}
