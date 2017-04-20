package com.bioxx.tfc2.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC.RecipeType;
import com.bioxx.tfc2.containers.slots.SlotSpecialCraftingOutput;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerInventory;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class ContainerSpecialCrafting extends ContainerTFC
{
	/** The crafting matrix inventory (9x9).
	 *  Used for knapping and leather working */
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 9, 9);

	private SlotSpecialCraftingOutput outputSlot;
	private boolean decreasedStack;

	/** The crafting result, size 1. */
	public IInventory craftResult = new InventoryCraftResult();
	private World worldObj;
	private InventoryPlayer invPlayer;
	private boolean isConstructing;
	public ContainerSpecialCrafting(InventoryPlayer inventoryplayer, ItemStack is, World world, int x, int y, int z)
	{
		invPlayer = inventoryplayer;
		this.worldObj = world; // Must be set before inventorySlotContents to prevent NPE
		decreasedStack = false;
		isConstructing = true;
		bagsSlotNum = inventoryplayer.currentItem;
		for (int j1 = 0; j1 < 81; j1++)
		{
			if(is != null)
				craftMatrix.setInventorySlotContents(j1, is.copy());
		}

		outputSlot = new SlotSpecialCraftingOutput(this, inventoryplayer.player, craftMatrix, craftResult, 0, 128, 44);
		addSlotToContainer(outputSlot);

		PlayerInventory.buildInventoryLayout(this, inventoryplayer, 8, 108, true, true);

		this.onCraftMatrixChanged(this.craftMatrix);
		isConstructing = false;
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		super.onContainerClosed(player);
		if (!this.worldObj.isRemote)
		{
			ItemStack is = this.craftResult.getStackInSlot(0);
			if (is != ItemStack.EMPTY)
				player.entityDropItem(is, 0);
		}
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory ii)
	{
		RecipeType rt = RecipeType.KNAPPING;
		PlayerInfo pi = PlayerManagerTFC.getInstance().getPlayerInfoFromPlayer(invPlayer.player);
		if(pi.specialCraftingType != null && pi.specialCraftingType.getItem() == Items.CLAY_BALL)
			rt = RecipeType.POTTERY;

		ItemStack result = CraftingManagerTFC.getInstance().findMatchingRecipe(rt, this.craftMatrix, worldObj);

		// Handle decreasing the stack of the held item used to open the interface.
		if (!decreasedStack && !isConstructing)
		{

			// A valid clay recipe has been formed.
			/*if (pi.specialCraftingType.getItem() == TFCItems.flatClay)
			{
				if (result != null)
				{
					setDecreasedStack(true); // Mark container so it won't decrease again.
					if (!this.worldObj.isRemote && invPlayer.getCurrentItem().getMaxStackSize() >= 5) // Server only to prevent it removing multiple times.
						invPlayer.decrStackSize(invPlayer.currentItem, 5);
					else // Clientside or if the player doesn't have enough clay, return before the output slot is set.
					{
						setDecreasedStack(false);
						return;
					}
				}
			}
			// A piece of rock or leather has been removed.
			else*/ if (hasPieceBeenRemoved(pi))
			{
				setDecreasedStack(true); // Mark container so it won't decrease again.
				if (!this.worldObj.isRemote) // Server only to prevent it removing multiple times.
				{
					int count = 1;

					if(invPlayer.getStackInSlot(invPlayer.currentItem).getItem() == Items.CLAY_BALL)
						count = 5;

					invPlayer.decrStackSize(invPlayer.currentItem, count);
				}
			}
		}

		// The crafting output is only set if the input was consumed
		if (decreasedStack)
		{
			this.craftResult.setInventorySlotContents(0, result);

			// Trigger Achievements
			if (result != ItemStack.EMPTY && invPlayer.player != null)
			{
				Item item = result.getItem();

			}
		}
	}

	/**
	 * Called to transfer a stack from one inventory to the other eg. when shift clicking.
	 * @return null if successful, the original item stack otherwise
	 */
	@Override
	public ItemStack transferStackInSlotTFC(EntityPlayer player, int slotNum)
	{
		Slot slot = (Slot)this.inventorySlots.get(slotNum);
		if (slot == null || !slot.getHasStack())  return ItemStack.EMPTY;
		ItemStack slotStack = slot.getStack();
		ItemStack origStack = slotStack.copy(); 
		InventoryPlayer ip = player.inventory;

		// From Crafting Grid Output to inventory
		if (slot instanceof SlotSpecialCraftingOutput)
		{
			if (slotNum == 0 && !ip.addItemStackToInventory(slotStack))
				return ItemStack.EMPTY;
		}
		// From inventory to Hotbar
		else if (slotNum >= 1 && slotNum < 28 && !this.mergeItemStack(slotStack, 28, 37, false))
			return ItemStack.EMPTY;
		// From Hotbar to inventory
		else if (slotNum >= 28 && slotNum < 37 && !this.mergeItemStack(slotStack, 1, 28, false))
			return ItemStack.EMPTY;

		if (slotStack.isEmpty())
			slot.putStack(ItemStack.EMPTY);
		else
			slot.onSlotChanged();

		if (slotStack.getCount() == origStack.getCount())
			return ItemStack.EMPTY;

		ItemStack itemstack2 = slot.onTake(player, slotStack);
		if (slotNum == 0)
			player.dropItem(itemstack2, false);

		return origStack;
	}

	@Override
	/**
	 * Handles slot click when HotBar HotKeys 1-9 are pressed: ClickType = SWAP, dragType = HotBar slot number (0-8) 
	 */
	public ItemStack slotClick(int slotID, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		// 1. Freeze current slot (Main Hand held item)
		if (slotID == 28 + invPlayer.currentItem || clickTypeIn == ClickType.SWAP && dragType == invPlayer.currentItem)
			return ItemStack.EMPTY;
		// 2. Take items from crafting output slot & put them into HotBar slot 1-9. 
		//    - Works better than vanilla: merges crafted items with identical items in HotBar slot.
		//    - Correctly handles multiple items (in case knapping will ever output more than 1 item per stack).
		if (slotID == 0 && clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9)
		{
			Slot sourceSlot = (Slot) this.inventorySlots.get(slotID);
			if (sourceSlot == null)  return ItemStack.EMPTY;
			ItemStack sourceStack = sourceSlot.getStack();
			if (sourceStack == null || sourceStack.isEmpty())  return ItemStack.EMPTY;
			Slot targetSlot = (Slot) this.inventorySlots.get(28 + dragType);
			if (targetSlot == null)  return ItemStack.EMPTY;
			ItemStack targetStack = targetSlot.getStack();

			if (canAddItemToSlot(targetSlot, sourceStack, true)) 
			{
				if (targetStack == null || targetStack.isEmpty())
				{
					targetSlot.putStack(sourceStack);
					sourceSlot.putStack(ItemStack.EMPTY);
				}
				else
				{
					int sCnt = sourceStack.getCount(); 
					int tCnt = targetStack.getCount();
					int xferCnt = Math.min(targetStack.getMaxStackSize() - tCnt, sCnt);
					if (xferCnt <= 0)  return ItemStack.EMPTY;
					sourceStack.splitStack(xferCnt);
					targetStack.setCount(tCnt + xferCnt);
					if (xferCnt < sCnt)  return ItemStack.EMPTY;
				}
				sourceSlot.onSlotChanged();
				sourceSlot.onTake(player, sourceStack);
				return ItemStack.EMPTY;
			}
			else
				return ItemStack.EMPTY;
		}
		return super.slotClick(slotID, dragType, clickTypeIn, player);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player)
	{
		return true;
	}

	// Freeze current slot - disable double-click merging 
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn)
	{
		if (slotIn.getSlotIndex() == invPlayer.currentItem)  return false;
		else  return super.canMergeSlot(stack, slotIn);
	}

	// Freeze current slot - disable dragging  
	@Override
	public boolean canDragIntoSlot(Slot slotIn)
	{
		if (slotIn.getSlotIndex() == invPlayer.currentItem)  return false;
		else  return super.canDragIntoSlot(slotIn);
	}

	public boolean hasPieceBeenRemoved(PlayerInfo pi)
	{
		// Knapping interface is a boolean array where the value is true if that button has been pushed.
		for (int i = 0; i < this.craftMatrix.getSizeInventory(); i++)
		{
			if (this.craftMatrix.getStackInSlot(i) == ItemStack.EMPTY)
				return true;
		}

		// Reset the decreasedStack flag if no pieces have been removed.
		setDecreasedStack(false);
		return false;
	}

	public void setDecreasedStack(Boolean b)
	{
		this.decreasedStack = b;
	}

}
