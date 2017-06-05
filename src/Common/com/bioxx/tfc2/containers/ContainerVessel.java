package com.bioxx.tfc2.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.types.EnumSize;
import com.bioxx.tfc2.containers.slots.SlotSize;
import com.bioxx.tfc2.core.PlayerInventory;

public class ContainerVessel extends ContainerTFC
{
	private World world;
	public InventoryCrafting containerInv = new InventoryCrafting(this, 2, 2);

	public ContainerVessel(InventoryPlayer playerinv, World world)
	{
		this.player = playerinv.player;
		this.world = world;
		bagsSlotNum = player.inventory.currentItem;
		PlayerInventory.buildInventoryLayout(this, playerinv, 8, 97, true, true);
		layoutContainer(playerinv, 0, 0);
		if(!world.isRemote)
			loadBagInventory();
		this.doItemSaving = true;
	}

	public void loadBagInventory()
	{
		if(!player.inventory.getStackInSlot(bagsSlotNum).isEmpty() && 
				player.inventory.getStackInSlot(bagsSlotNum).hasTagCompound())
		{
			NBTTagList nbttaglist = player.inventory.getStackInSlot(bagsSlotNum).getTagCompound().getTagList("Items", 10);
			for(int i = 0; i < nbttaglist.tagCount(); i++)
			{
				this.isLoading = true;
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound1.getByte("Slot");
				if(byte0 >= 0 && byte0 < 4)
				{
					ItemStack is = new ItemStack(nbttagcompound1);
					if(is.getCount() >= 1)
						this.containerInv.setInventorySlotContents(byte0, is);
					else
						this.containerInv.setInventorySlotContents(byte0, ItemStack.EMPTY);
				}
			}
		}
	}

	@Override
	public void saveContents(ItemStack is)
	{
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < containerInv.getSizeInventory(); i++)
		{
			ItemStack contentStack = containerInv.getStackInSlot(i);

			if (!contentStack.isEmpty())
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				contentStack.writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}			

		if(!is.isEmpty())
		{
			if(nbttaglist.tagCount() == 0 && is.hasTagCompound())
				is.setTagCompound(null);
			else 
			{
				if(!is.hasTagCompound())
					is.setTagCompound(new NBTTagCompound());
				is.getTagCompound().setTag("Items", nbttaglist);
			}
		}
	}


	/**
	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
	 */
	@Override
	public ItemStack transferStackInSlotTFC(EntityPlayer player, int slotNum)
	{
		ItemStack origStack = ItemStack.EMPTY;
		Slot slot = (Slot)this.inventorySlots.get(slotNum);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack();
			origStack = slotStack.copy();

			// From pile to inventory
			if (slotNum < 36)
			{
				if (!this.mergeItemStack(slotStack, 36, inventorySlots.size(), true))
					return ItemStack.EMPTY;
			}
			else
			{
				if (!this.mergeItemStack(slotStack, 0, 36, false))
					return ItemStack.EMPTY;
			}

			if (slotStack.getMaxStackSize() <= 0)
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (slotStack.getMaxStackSize() == origStack.getMaxStackSize())
				return ItemStack.EMPTY;

			slot.onTake(player, slotStack);
		}

		return origStack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1)
	{
		return true;
	}

	protected void layoutContainer( IInventory chestInventory, int xSize, int ySize)
	{
		this.addSlotToContainer(new SlotSize(containerInv, 0, 71, 25, EnumSize.TINY, EnumSize.SMALL));
		this.addSlotToContainer(new SlotSize(containerInv, 1, 89, 25, EnumSize.TINY, EnumSize.SMALL));
		this.addSlotToContainer(new SlotSize(containerInv, 2, 71, 43, EnumSize.TINY, EnumSize.SMALL));
		this.addSlotToContainer(new SlotSize(containerInv, 3, 89, 43, EnumSize.TINY, EnumSize.SMALL));
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}
}
