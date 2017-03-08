package com.bioxx.tfc2.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

public class InventoryCart implements IInventory 
{
	private String inventoryTitle;
	private int slotsCount;
	private ItemStack[] inventoryContents;
	private boolean hasCustomName;

	public InventoryCart(String title, boolean customName, int slotCount)
	{
		this.inventoryTitle = title;
		this.hasCustomName = customName;
		this.slotsCount = slotCount;
		this.inventoryContents = new ItemStack[slotCount];
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int index)
	{
		return index >= 0 && index < this.inventoryContents.length ? this.inventoryContents[index] : null;
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
	 * new stack.
	 */
	@Override
	public ItemStack decrStackSize(int index, int count)
	{
		if (this.inventoryContents[index] != null)
		{
			ItemStack itemstack;

			if (this.inventoryContents[index].getMaxStackSize() <= count)
			{
				itemstack = this.inventoryContents[index];
				this.inventoryContents[index] = null;
				this.markDirty();
				return itemstack;
			}
			else
			{
				itemstack = this.inventoryContents[index].splitStack(count);

				if (this.inventoryContents[index].getMaxStackSize() == 0)
				{
					this.inventoryContents[index] = null;
				}

				this.markDirty();
				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int index, ItemStack stack)
	{
		this.inventoryContents[index] = stack;

		if (stack != null && stack.getMaxStackSize() > this.getInventoryStackLimit())
		{
			stack.setCount(this.getInventoryStackLimit());
		}

		this.markDirty();
	}

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.slotsCount;
	}

	/**
	 * Returns true if this thing is named
	 */
	@Override
	public boolean hasCustomName()
	{
		return this.hasCustomName;
	}

	/**
	 * Sets the name of this inventory. This is displayed to the client on opening.
	 */
	public void setCustomName(String inventoryTitleIn)
	{
		this.hasCustomName = true;
		this.inventoryTitle = inventoryTitleIn;
	}

	/**
	 * Returns the maximum stack size for a inventory slot. Seems to always be 64, possibly will be extended. *Isn't
	 * this more of a set than a get?*
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/**
	 * For tile entities, ensures the chunk containing the tile entity is saved to disk later - the game won't think it
	 * hasn't changed and skip it.
	 */
	@Override
	public void markDirty()
	{
	}

	/**
	 * Do not make give this method the name canInteractWith because it clashes with Container
	 */
	@Override
	public boolean isUsableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	/**
	 * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
	 */
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack)
	{
		return true;
	}

	@Override
	public int getField(int id)
	{
		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		for (int i = 0; i < this.inventoryContents.length; ++i)
		{
			this.inventoryContents[i] = null;
		}
	}

	@Override
	public String getName() {
		return this.inventoryTitle;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) 
	{
		ItemStack is = inventoryContents[i];
		inventoryContents[i] = null;
		return is;
	}

	@Override
	public ITextComponent getDisplayName() {
		return null;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

}
