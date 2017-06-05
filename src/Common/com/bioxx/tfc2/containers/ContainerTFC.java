package com.bioxx.tfc2.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.core.Food;

public class ContainerTFC extends Container
{
	public int bagsSlotNum;
	public EntityPlayer player;
	protected boolean isLoading;
	protected boolean doItemSaving;

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}

	/**
	 * Used by containers that represent items and need to save the inventory to that items NBT
	 */
	public void saveContents(ItemStack is)
	{
	}

	/**
	 * Used by containers that represent items and need to load an item from nbt
	 * @return null, as it is currently ignored
	 */
	public ItemStack loadContents(int slot) 
	{
		return null;
	}

	@Override
	/**
	 * Handles slot click.
	 *  
	 * @param mode 0 = basic click, 1 = shift click, 2 = hotbar, 3 = pick block, 4 = drop, 5 = ?, 6 = double click
	 */
	public ItemStack slotClick(int slotID, int dragType, ClickType clickTypeIn, EntityPlayer p)
	{
		if (slotID >= 0 && slotID < this.inventorySlots.size())
		{
			Slot sourceSlot = (Slot) this.inventorySlots.get(slotID);
			ItemStack slotStack = sourceSlot.getStack();

			//This section is for merging foods with differing expirations.
			if(clickTypeIn == ClickType.SWAP && slotStack != null && p.inventory.getItemStack() != null)
			{
				ItemStack itemstack4 = p.inventory.getItemStack();
				if (slotStack.getItem() == itemstack4.getItem() && slotStack.getMetadata() == itemstack4.getMetadata() && ContainerTFC.areCompoundsEqual(slotStack, itemstack4))
				{
					if(slotStack.getItem() instanceof IFood && itemstack4.getItem() instanceof IFood)
					{
						long ex1 = Food.getDecayTimer(slotStack);
						long ex2 = Food.getDecayTimer(itemstack4);
						if(ex2 < ex1)
							Food.setDecayTimer(slotStack, ex2);
					}

					//int l1 = clickedButton == 0 ? itemstack4.getMaxStackSize() : 1;
					int l1 = itemstack4.getMaxStackSize();

					if (l1 > sourceSlot.getItemStackLimit(itemstack4) - slotStack.getMaxStackSize())
					{
						l1 = sourceSlot.getItemStackLimit(itemstack4) - slotStack.getMaxStackSize();
					}

					if (l1 > itemstack4.getMaxStackSize() - slotStack.getMaxStackSize())
					{
						l1 = itemstack4.getMaxStackSize() - slotStack.getMaxStackSize();
					}

					itemstack4.splitStack(l1);

					if (itemstack4.getMaxStackSize() == 0)
					{
						p.inventory.setItemStack(ItemStack.EMPTY);
					}

					slotStack.grow(l1);
					return ItemStack.EMPTY;
				}
				else if (itemstack4.getMaxStackSize() <= sourceSlot.getItemStackLimit(itemstack4))
				{
					sourceSlot.putStack(itemstack4);
					p.inventory.setItemStack(slotStack);
				}
			}

			// Hotbar press to remove from crafting output
			if (clickTypeIn == ClickType.QUICK_MOVE && slotID == 0 && slotStack != null)
			{
				//Removed During Port
				//CraftingHandler.preCraft(p, slotStack, craftMatrix);
			}
			// S and D hotkeys for trimming/combining food
			/*else if (mode == 7 && slotID >= 9 && slotID < 45)
			{
				if (sourceSlot.canTakeStack(p))
				{
					Slot destSlot = (Slot) this.inventorySlots.get(clickedButton);
					destSlot.putStack(slotStack);
					sourceSlot.putStack(null);
					return null;
				}
			}*/
		}

		ItemStack is = super.slotClick(slotID, dragType, clickTypeIn, p);
		//saveContents(is);
		return is;
	}

	@Override
	protected boolean mergeItemStack(ItemStack is, int slotStart, int slotFinish, boolean par4)
	{
		boolean merged = false;
		int slotIndex = slotStart;

		if (par4)
			slotIndex = slotFinish - 1;

		Slot slot;
		ItemStack slotstack;

		if (is.isStackable())
		{
			while (is.getMaxStackSize() > 0 && (!par4 && slotIndex < slotFinish || par4 && slotIndex >= slotStart))
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				slotstack = slot.getStack();

				if (slotstack != null
						&& slotstack.getItem() == is.getItem()
						&& (!is.getHasSubtypes() || is.getMetadata() == slotstack.getMetadata())
						&& ItemStack.areItemStackTagsEqual(is, slotstack)
						&& slotstack.getMaxStackSize() < slot.getSlotStackLimit())
				{
					int mergedStackSize = is.getMaxStackSize() + getSmaller(slotstack.getMaxStackSize(), slot.getSlotStackLimit());

					//First we check if we can add the two stacks together and the resulting stack is smaller than the maximum size for the slot or the stack
					if (mergedStackSize <= is.getMaxStackSize() && mergedStackSize <= slot.getSlotStackLimit())
					{
						is.setCount(0);
						slotstack.setCount(mergedStackSize);
						slot.onSlotChanged();
						merged = true;
					}
					else if (slotstack.getMaxStackSize() < is.getMaxStackSize() && slotstack.getMaxStackSize() < slot.getSlotStackLimit())
					{
						// Slot stack size is greater than or equal to the item's max stack size. Most containers are this case.
						if (slot.getSlotStackLimit() >= is.getMaxStackSize())
						{
							is.shrink(is.getMaxStackSize() - slotstack.getMaxStackSize());
							slotstack.setCount(is.getMaxStackSize());
							slot.onSlotChanged();
							merged = true;
						}
						// Slot stack size is smaller than the item's normal max stack size. Example: Log Piles
						else if (slot.getSlotStackLimit() < is.getMaxStackSize())
						{
							is.shrink(slot.getSlotStackLimit() - slotstack.getMaxStackSize());
							slotstack.setCount(slot.getSlotStackLimit());
							slot.onSlotChanged();
							merged = true;
						}
					}
				}

				if (par4)
					--slotIndex;
				else
					++slotIndex;
			}
		}

		if (is.getMaxStackSize() > 0)
		{
			if (par4)
				slotIndex = slotFinish - 1;
			else
				slotIndex = slotStart;

			while (!par4 && slotIndex < slotFinish || par4 && slotIndex >= slotStart)
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				slotstack = slot.getStack();
				if (slotstack == null && slot.isItemValid(is) && slot.getSlotStackLimit() < is.getMaxStackSize())
				{
					ItemStack copy = is.copy();
					copy.setCount(slot.getSlotStackLimit());
					is.shrink(slot.getSlotStackLimit());
					slot.putStack(copy);
					slot.onSlotChanged();
					merged = true;
					//this.bagsSlotNum = slotIndex;
					break;
				}
				else if (slotstack == null && slot.isItemValid(is))
				{
					slot.putStack(is.copy());
					slot.onSlotChanged();
					is.setCount(0);
					merged = true;
					break;
				}

				if (par4)
					--slotIndex;
				else
					++slotIndex;
			}
		}

		return merged;
	}

	protected int getSmaller(int i, int j)
	{
		if(i < j)
			return i;
		else
			return j;
	}

	@Override
	public void detectAndSendChanges()
	{
		boolean shouldSave = false;
		boolean shouldReload = false;

		for (int i = 0; i < this.inventorySlots.size(); ++i)
		{
			ItemStack itemstack = ((Slot)this.inventorySlots.get(i)).getStack();//the visible slot item
			ItemStack itemstack1 = (ItemStack)this.inventoryItemStacks.get(i);//the real invisible item

			if (!areItemStacksEqual(itemstack1, itemstack))
			{
				if(doItemSaving && i >= 36 && !isLoading)
					shouldSave = true;

				itemstack1 = itemstack == ItemStack.EMPTY ? ItemStack.EMPTY : itemstack.copy();
				if(itemstack1 != null && itemstack1.getMaxStackSize() == 0)
					itemstack1 = ItemStack.EMPTY;
				this.inventoryItemStacks.set(i, itemstack1);

				if(shouldSave)
				{
					int slotNum = bagsSlotNum;
					ItemStack bag = player.inventory.getStackInSlot(slotNum);
					this.saveContents(bag);
					player.inventory.setInventorySlotContents(slotNum, bag);
					for (int j = 0; j < this.listeners.size(); ++j)
						((IContainerListener)this.listeners.get(j)).sendSlotContents(this, slotNum, (ItemStack)inventoryItemStacks.get(slotNum));
				}

				for (int j = 0; j < this.listeners.size(); ++j)
					((IContainerListener)this.listeners.get(j)).sendSlotContents(this, i, itemstack1);
			}
		}

		for (int i = 0; i < this.inventorySlots.size()-36; ++i)
		{
			//ItemStack itemstack = this.loadContents(i);
			//ItemStack itemstack1 = (ItemStack) this.inventoryItemStacks.get(i);//the real invisible item
			// This method was mysteriously deleted with no trace on github. However adding it back causes a crash.
			// if (!areItemStacksEqual(itemstack1, itemstack) && player.inventory.getItemStack() == null)
			{
				shouldReload = true;
			}
		}

		if(shouldReload && !isLoading)
			reloadContainer();

		this.isLoading = false;
	}

	/**
	 * This is only used if the container should be reloaded due to some change in information 
	 * that can't be updated in some other way.
	 */
	public void reloadContainer()
	{
	}

	public static boolean areItemStacksEqual(ItemStack is1, ItemStack is2)
	{
		return is1 == null && is2 == null || (is1 != null && is2 != null) && isItemStackEqual(is1, is2);
	}

	public static boolean isItemStackEqual(ItemStack is1, ItemStack is2)
	{
		return is1.getMaxStackSize() == is2.getMaxStackSize() && is1.getItem() == is2.getItem() && is1.getItemDamage() == is2.getItemDamage() &&
				(is1.hasTagCompound() || !is2.hasTagCompound()) &&
				(!is1.hasTagCompound() || areCompoundsEqual(is1, is2));
	}

	public static boolean areCompoundsEqual(ItemStack is1, ItemStack is2)
	{
		ItemStack is3 = is1.copy();
		ItemStack is4 = is2.copy();
		NBTTagCompound is3Tags = is3.getTagCompound();
		NBTTagCompound is4Tags = is4.getTagCompound();

		if (is3Tags == null)
			return is4Tags == null || is4Tags.hasNoTags();

		if (is4Tags == null)
			return is3Tags.hasNoTags();

		//Removed during porting this code to 1.8 due to there not being any heat infrastructure at the time.
		/*float temp3 = TFC_ItemHeat.getTemp(is1);
		float temp4 = TFC_ItemHeat.getTemp(is2);
		is3Tags.removeTag("temp");
		is4Tags.removeTag("temp");*/

		is3Tags.removeTag("Expiration");
		is4Tags.removeTag("Expiration");

		return is3Tags.equals(is4Tags) /*&&  Math.abs(temp3 - temp4) < 5*/;
	}

	public ItemStack transferStackInSlotTFC(EntityPlayer entityplayer, int slotNum)
	{
		return super.transferStackInSlot(entityplayer, slotNum);
	}

	@Override
	final public ItemStack transferStackInSlot(EntityPlayer entityplayer, int slotNum)
	{
		Slot slot = (Slot)this.inventorySlots.get( slotNum );
		ItemStack is = transferStackInSlotTFC(entityplayer, slotNum);

		// send a packet to make sure that the item is removed; that it stays removed.
		if ( ! slot.getHasStack() && entityplayer instanceof EntityPlayerMP && ! entityplayer.world.isRemote )
		{
			EntityPlayerMP mp = (EntityPlayerMP) entityplayer;
			mp.sendSlotContents( this, slot.slotNumber, slot.getStack() );
		}

		return is;
	}

}
