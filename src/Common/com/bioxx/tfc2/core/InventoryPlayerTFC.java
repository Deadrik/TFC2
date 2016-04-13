package com.bioxx.tfc2.core;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;

import com.bioxx.tfc2.Core;

public class InventoryPlayerTFC extends InventoryPlayer {

	public ItemStack[] extraEquipInventory = new ItemStack[Core.getExtraEquipInventorySize()];

	public InventoryPlayerTFC(EntityPlayer par1EntityPlayer) {
		super(par1EntityPlayer);
		this.player = par1EntityPlayer;
	}

	@Override
	public int getSizeInventory()
	{
		return this.mainInventory.length + armorInventory.length + this.extraEquipInventory.length;
	}

	@Override
	/**
	 * Returns the stack in slot i
	 */
	public ItemStack getStackInSlot(int par1)
	{
		ItemStack[] aitemstack = this.mainInventory;
		if (par1 >= this.mainInventory.length + this.extraEquipInventory.length)
		{
			par1 -= this.mainInventory.length + this.extraEquipInventory.length;
			aitemstack = this.armorInventory;
		}
		else if(par1 >= this.mainInventory.length){
			par1-= aitemstack.length;
			aitemstack = this.extraEquipInventory;
		}
		return aitemstack[par1];

	}

	/**
	 * Removes matching items from the inventory.
	 * @param itemIn The item to match, null ignores.
	 * @param metadataIn The metadata to match, -1 ignores.
	 * @param removeCount The number of items to remove. If less than 1, removes all matching items.
	 * @param itemNBT The NBT data to match, null ignores.
	 * @return The number of items removed from the inventory.
	 */
	@Override
	public int clearMatchingItems(Item itemIn, int metadataIn, int removeCount, NBTTagCompound itemNBT)
	{
		int k = 0;
		int l;
		ItemStack itemstack;
		int i1;

		k = super.clearMatchingItems(itemIn, metadataIn, removeCount, itemNBT);

		for(l = 0; l < this.extraEquipInventory.length; l++)
		{
			itemstack = this.extraEquipInventory[l];

			if (itemstack != null && (itemIn == null || itemstack.getItem() == itemIn) && (metadataIn <= -1 || itemstack.getMetadata() == metadataIn) && (itemNBT == null || (NBTUtil.areNBTEquals(itemNBT, itemstack.getTagCompound(), true))))
			{
				i1 = removeCount <= 0 ? itemstack.stackSize : Math.min(removeCount - k, itemstack.stackSize);
				k += i1;

				if (removeCount != 0)
				{
					this.extraEquipInventory[l].stackSize -= i1;

					if (this.extraEquipInventory[l].stackSize == 0)
					{
						this.extraEquipInventory[l] = null;
					}

					if (removeCount > 0 && k >= removeCount)
					{
						return k;
					}
				}
			}
		}

		return k;
	}

	@Override
	public void decrementAnimations()
	{
		for (int i = 0; i < this.extraEquipInventory.length; ++i)
		{
			if (this.extraEquipInventory[i] != null)
			{
				this.extraEquipInventory[i].updateAnimation(this.player.worldObj, this.player, i, this.currentItem == i);
			}
		}
		super.decrementAnimations();
	}

	@Override
	public ItemStack decrStackSize(int par1, int par2)
	{
		ItemStack[] aitemstack = this.mainInventory;

		if (par1 >= this.mainInventory.length + this.extraEquipInventory.length)
		{
			aitemstack = this.armorInventory;
			par1 -= this.mainInventory.length + this.extraEquipInventory.length;
		}
		else if(par1 >= this.mainInventory.length){
			par1-= aitemstack.length;
			aitemstack = this.extraEquipInventory;
		}


		if (aitemstack[par1] != null)
		{
			ItemStack itemstack;

			if (aitemstack[par1].stackSize <= par2)
			{
				itemstack = aitemstack[par1];
				aitemstack[par1] = null;
				return itemstack;
			}
			else
			{
				itemstack = aitemstack[par1].splitStack(par2);

				if (aitemstack[par1].stackSize == 0)
				{
					aitemstack[par1] = null;
				}

				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	@Override
	public void dropAllItems()
	{
		int i;

		for (i = 0; i < this.extraEquipInventory.length; ++i)
		{
			if (this.extraEquipInventory[i] != null)
			{
				this.player.dropItem(this.extraEquipInventory[i], true, false);
				this.extraEquipInventory[i] = null;
			}
		}
		super.dropAllItems();
	}

	@Override
	public boolean hasItemStack(ItemStack par1ItemStack)
	{
		int i;

		for (i = 0; i < this.extraEquipInventory.length; ++i)
		{
			if (this.extraEquipInventory[i] != null && this.extraEquipInventory[i].isItemEqual(par1ItemStack))
			{
				return true;
			}
		}
		return super.hasItemStack(par1ItemStack);
	}

	@Override
	public void setInventorySlotContents(int par1, ItemStack par2ItemStack)
	{

		ItemStack[] aitemstack = this.mainInventory;

		if (par1 >= this.mainInventory.length + this.extraEquipInventory.length)
		{
			par1 -= this.mainInventory.length + this.extraEquipInventory.length;
			aitemstack = this.armorInventory;
		}
		else if(par1 >= this.mainInventory.length){
			par1-= aitemstack.length;
			aitemstack = this.extraEquipInventory;
		}

		aitemstack[par1] = par2ItemStack;
	}

	/*
	 * This method is currently never being called properly.
	 * The copying of the extraEquipment is being handled with 
	 * com.bioxx.tfc.Core.Player.PlayerInfo.tempEquipment
	 * com.bioxx.tfc.Core.Player.PlayerTracker.onPlayerRespawn(PlayerRespawnEvent)
	 * and com.bioxx.tfc.Handlers.EntityLivingHandler.onEntityDeath(LivingDeathEvent)
	 */
	@Override
	public void copyInventory(InventoryPlayer par1InventoryPlayer)
	{
		if(par1InventoryPlayer instanceof InventoryPlayerTFC){
			this.copyInventoryTFC((InventoryPlayerTFC)par1InventoryPlayer);
		}
		else{
			super.copyInventory(par1InventoryPlayer);
		}
	}

	public void copyInventoryTFC(InventoryPlayerTFC par1InventoryPlayer)
	{
		int i;

		for (i = 0; i < this.extraEquipInventory.length; ++i)
		{
			this.extraEquipInventory[i] = ItemStack.copyItemStack(par1InventoryPlayer.extraEquipInventory[i]);
		}
		super.copyInventory(par1InventoryPlayer);
	}

	@Override
	public void readFromNBT(NBTTagList par1NBTTagList)
	{
		super.readFromNBT(par1NBTTagList);
		this.extraEquipInventory = new ItemStack[Core.getExtraEquipInventorySize()];

		NBTTagList extraList = player.getEntityData().getTagList("ExtraInventory", 10);

		for (int i = 0; i < extraList.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound = extraList.getCompoundTagAt(i);
			ItemStack itemstack = ItemStack.loadItemStackFromNBT(nbttagcompound);
			if (itemstack != null)
			{
				extraEquipInventory[i] = itemstack;
			}
		}
	}

	@Override
	public NBTTagList writeToNBT(NBTTagList par1NBTTagList)
	{
		super.writeToNBT(par1NBTTagList);

		int i;
		NBTTagCompound nbt;
		NBTTagList tagList = new NBTTagList();
		for (i = 0; i < extraEquipInventory.length; i++)
		{
			ItemStack is = extraEquipInventory[i];
			if (is != null)
			{
				nbt = new NBTTagCompound();
				nbt.setByte("Slot", (byte) i);
				is.writeToNBT(nbt);
				tagList.appendTag(nbt);
			}
		}
		player.getEntityData().setTag("ExtraInventory", tagList);
		return par1NBTTagList;
	}
}
