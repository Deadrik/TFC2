package com.bioxx.tfc2.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.containers.slots.SlotFirepitOutput;
import com.bioxx.tfc2.containers.slots.SlotForShowOnly;
import com.bioxx.tfc2.core.PlayerInventory;
import com.bioxx.tfc2.tileentities.TileFirepit;

public class ContainerCookingPot extends ContainerTFC
{
	private World world;
	private TileFirepit firepit;
	private EntityPlayer player;
	private NonNullList<Integer> fields = NonNullList.withSize(4, 0);

	public ContainerCookingPot(InventoryPlayer playerinv, TileFirepit firepit, World world, int x, int y, int z)
	{
		this.player = playerinv.player;

		this.world = world;
		this.firepit = firepit;
		firepit.openInventory(player);
		PlayerInventory.buildInventoryLayout(this, playerinv, 8, 96, false, true);
		layoutContainer(playerinv, firepit, 0, 0);
	}

	/**
	 * Callback for when the crafting gui is closed.
	 */
	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);
		if(!world.isRemote)
			firepit.closeInventory(par1EntityPlayer);
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

			if (slotNum < 36)
			{
				if (!this.mergeItemStack(slotStack, 36, inventorySlots.size()-1, false))
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

	protected void layoutContainer(IInventory playerInventory, IInventory chestInventory, int xSize, int ySize)
	{
		this.addSlotToContainer(new Slot(chestInventory, 0, 13, 52));
		this.addSlotToContainer(new SlotForShowOnly(chestInventory, 1, 53, 16));
		this.addSlotToContainer(new Slot(chestInventory, 2, 71, 16));
		this.addSlotToContainer(new Slot(chestInventory, 3, 89, 16));
		this.addSlotToContainer(new Slot(chestInventory, 4, 53, 34));
		this.addSlotToContainer(new Slot(chestInventory, 5, 71, 34));
		this.addSlotToContainer(new Slot(chestInventory, 6, 89, 34));
		this.addSlotToContainer(new Slot(chestInventory, 7, 53, 52));
		this.addSlotToContainer(new Slot(chestInventory, 8, 71, 52));
		this.addSlotToContainer(new Slot(chestInventory, 9, 89, 52));
		this.addSlotToContainer(new SlotFirepitOutput(player, chestInventory, 10, 146, 34));
	}

	public EntityPlayer getPlayer()
	{
		return player;
	}

	@Override
	public void addListener(IContainerListener listener)
	{
		super.addListener(listener);
		listener.sendAllWindowProperties(this, this.firepit);
	}

	/**
	 * Looks for changes made in the container, sends them to every listener.
	 */
	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();

		for (int i = 0; i < this.listeners.size(); ++i)
		{
			IContainerListener icontainerlistener = (IContainerListener)this.listeners.get(i);

			for(int j = 0; j < firepit.getFieldCount(); j++)
			{
				if(fields.get(j) != this.firepit.getField(j))
				{
					icontainerlistener.sendProgressBarUpdate(this, j, this.firepit.getField(j));
				}
			}
		}

		for(int j = 0; j < firepit.getFieldCount(); j++)
		{
			if(fields.get(j) != this.firepit.getField(j))
			{
				fields.set(j, this.firepit.getField(j));
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data)
	{
		this.firepit.setField(id, data);
	}
}
