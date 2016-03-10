package com.bioxx.tfc2.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.PlayerInventory;

public class ContainerPlayerTFC extends ContainerPlayer
{
	private final EntityPlayer thePlayer;

	public ContainerPlayerTFC(InventoryPlayer playerInv, boolean par2, EntityPlayer player)
	{
		super(playerInv, par2, player);
		this.craftMatrix = new InventoryCrafting(this, 3, 3);
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		this.thePlayer = player;
		this.addSlotToContainer(new SlotCrafting(player, craftMatrix, craftResult, 0, 152, 36));
		int x;
		int y;

		for (x = 0; x < 2; ++x)
		{
			for (y = 0; y < 2; ++y)
				this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18, 18 + x * 18));
		}

		for (x = 0; x < playerInv.armorInventory.length; ++x)
		{
			int index = playerInv.getSizeInventory() - 1 - x;
			final int k = x;
			this.addSlotToContainer(new Slot(playerInv, index, 8, 8 + x * 18)
			{
				private static final String __OBFID = "CL_00001755";
				/**
				 * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1
				 * in the case of armor slots)
				 */
				@Override
				public int getSlotStackLimit()
				{
					return 1;
				}
				/**
				 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
				 */
				@Override
				public boolean isItemValid(ItemStack stack)
				{
					if (stack == null) return false;
					return stack.getItem().isValidArmor(stack, k, thePlayer);
				}
				@Override
				@SideOnly(Side.CLIENT)
				public String getSlotTexture()
				{
					return ItemArmor.EMPTY_SLOT_NAMES[k];
				}
			});
		}
		PlayerInventory.buildInventoryLayout(this, playerInv, 8, 90, false, true);

		//Manually built the remaining crafting slots because of an order issue. These have to be created after the default slots
		if(player.getEntityData().hasKey("craftingTable") || !player.worldObj.isRemote)
		{
			x = 2; y = 0; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18, 18 + x * 18));
			x = 2; y = 1; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18, 18 + x * 18));
			x = 0; y = 2; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18, 18 + x * 18));
			x = 1; y = 2; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18, 18 + x * 18));
			x = 2; y = 2; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18, 18 + x * 18));
		}
		else
		{
			//Have to create some dummy slots
			x = 2; y = 0; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18-50000, 18 + x * 18));
			x = 2; y = 1; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18-50000, 18 + x * 18));
			x = 0; y = 2; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18-50000, 18 + x * 18));
			x = 1; y = 2; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18-50000, 18 + x * 18));
			x = 2; y = 2; this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18-50000, 18 + x * 18));
		}
		PlayerInventory.addExtraEquipables(this, playerInv, 8, 90, false);
		this.onCraftMatrixChanged(this.craftMatrix);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory iinventory)
	{
		super.onCraftMatrixChanged(iinventory);

		Slot craftOut = (Slot) this.inventorySlots.get(0);
		if (craftOut != null && craftOut.getHasStack())
		{
			ItemStack craftResult = craftOut.getStack();
			if (craftResult != null)
			{
				//Removed During Port
				/*if (craftResult.getItem() instanceof ItemFoodTFC)
					FoodCraftingHandler.updateOutput(thePlayer, craftResult, craftMatrix);
				else
					CraftingHandler.transferNBT(false, thePlayer, craftResult, craftMatrix);*/
			}
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		if(!player.worldObj.isRemote)
		{
			super.onContainerClosed(player);

			for (int i = 0; i < 9; ++i)
			{
				ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
				if (itemstack != null)
					player.dropPlayerItemWithRandomChoice(itemstack, false);
			}

			this.craftResult.setInventorySlotContents(0, (ItemStack)null);
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
	{
		ItemStack origStack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotNum);
		//Slot equipmentSlot = (Slot) this.inventorySlots.get(50);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack(); 
			origStack = slotStack.copy();

			// Crafting Grid Output
			if (slotNum == 0)
			{
				//Removed During Port
				/*FoodCraftingHandler.preCraft(player, slotStack, craftMatrix);
				CraftingHandler.preCraft(player, slotStack, craftMatrix);*/

				if (!this.mergeItemStack(slotStack, 9, 45, true))
					return null;

				slot.onSlotChange(slotStack, origStack);
			}
			// From crafting grid input to inventory
			else if (slotNum >= 1 && slotNum < 5 || player.getEntityData().hasKey("craftingTable") && slotNum >= 45 && slotNum < 50)
			{
				if (!this.mergeItemStack(slotStack, 9, 45, true))
					return null;
			}
			// From armor or equipment slot to inventory
			else if (slotNum >= 5 && slotNum < 9 || slotNum == 50)
			{
				if (!this.mergeItemStack(slotStack, 9, 45, true))
					return null;
			}
			// From inventory to armor slots
			//Removed During Port
			/*else if (origStack.getItem() instanceof ItemArmor)
			{
				int armorSlotNum = 5 + ((ItemArmor) origStack.getItem()).armorType;
				if (origStack.getItem() instanceof ItemTFCArmor)
				{
					armorSlotNum = 5 + ((ItemTFCArmor) origStack.getItem()).getUnadjustedArmorType();

					if (!((Slot) this.inventorySlots.get(armorSlotNum)).getHasStack())
					{
						if (!this.mergeItemStack(slotStack, armorSlotNum, armorSlotNum + 1, false))
							return null;
					}
				}
				else if (!((Slot) this.inventorySlots.get(armorSlotNum)).getHasStack())
				{
					if (!this.mergeItemStack(slotStack, armorSlotNum, armorSlotNum + 1, false))
						return null;
				}
			}
			// From inventory to back slot
			else if (!equipmentSlot.getHasStack() && origStack.getItem() instanceof IEquipable)
			{
				IEquipable equipment = (IEquipable) origStack.getItem();
				if (equipment.getEquipType(origStack) == EquipType.BACK && (equipment == TFCItems.quiver || equipment.getTooHeavyToCarry(origStack)))
				{
					ItemStack backStack = slotStack.copy();
					backStack.stackSize = 1;
					equipmentSlot.putStack(backStack);
					slotStack.stackSize--;
				}
			}
			// Food from inventory/hotbar to crafting grid
			else if (slotNum >= 9 && slotNum < 45 && origStack.getItem() instanceof IFood && !(origStack.getItem() instanceof ItemMeal) && !isCraftingGridFull())
			{
				if (!this.mergeItemStack(slotStack, 1, 5, false) && slotStack.stackSize == 0)
					return null;
				else if (slotStack.stackSize > 0 && player.getEntityData().hasKey("craftingTable") && !this.mergeItemStack(slotStack, 45, 50, false))
					return null;
				else if (slotStack.stackSize > 0 && slotNum >= 9 && slotNum < 36)
				{
					if (!this.mergeItemStack(slotStack, 36, 45, false))
						return null;
				}
				else if (slotStack.stackSize > 0 && slotNum >= 36 && slotNum < 45)
				{
					if (!this.mergeItemStack(slotStack, 9, 36, false))
						return null;
				}
			}*/
			// From inventory to hotbar
			else if (slotNum >= 9 && slotNum < 36)
			{
				if (!this.mergeItemStack(slotStack, 36, 45, false))
					return null;
			}
			// From hotbar to inventory
			else if (slotNum >= 36 && slotNum < 45)
			{
				if (!this.mergeItemStack(slotStack, 9, 36, false))
					return null;
			}

			if (slotStack.stackSize <= 0)
				slot.putStack(null);
			else
				slot.onSlotChanged();

			if (slotStack.stackSize == origStack.stackSize)
				return null;

			slot.onPickupFromSlot(player, slotStack);
		}

		return origStack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean useEndIndex)
	{
		boolean flag1 = false;
		int k = startIndex;

		if (useEndIndex)
		{
			k = endIndex - 1;
		}

		Slot slot;
		ItemStack itemstack1;

		if (stack.isStackable())
		{
			while (stack.stackSize > 0 && (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex))
			{
				slot = (Slot)this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 != null && 
						itemstack1.getItem() == stack.getItem() && 
						(!stack.getHasSubtypes() || stack.getMetadata() == itemstack1.getMetadata()) && 
						ContainerTFC.areCompoundsEqual(stack, itemstack1))
				{
					if(stack.getItem() instanceof IFood && itemstack1.getItem() instanceof IFood)
					{
						long ex1 = Food.getDecayTimer(stack);
						long ex2 = Food.getDecayTimer(itemstack1);
						if(ex1 < ex2)
							Food.setDecayTimer(itemstack1, ex1);
					}

					int l = itemstack1.stackSize + stack.stackSize;

					if (l <= stack.getMaxStackSize())
					{
						stack.stackSize = 0;
						itemstack1.stackSize = l;
						slot.onSlotChanged();
						flag1 = true;
					}
					else if (itemstack1.stackSize < stack.getMaxStackSize())
					{
						stack.stackSize -= stack.getMaxStackSize() - itemstack1.stackSize;
						itemstack1.stackSize = stack.getMaxStackSize();
						slot.onSlotChanged();
						flag1 = true;
					}
				}

				if (useEndIndex)
				{
					--k;
				}
				else
				{
					++k;
				}
			}
		}

		if (stack.stackSize > 0)
		{
			if (useEndIndex)
			{
				k = endIndex - 1;
			}
			else
			{
				k = startIndex;
			}

			while (!useEndIndex && k < endIndex || useEndIndex && k >= startIndex)
			{
				slot = (Slot)this.inventorySlots.get(k);
				itemstack1 = slot.getStack();

				if (itemstack1 == null && slot.isItemValid(stack)) // Forge: Make sure to respect isItemValid in the slot.
				{
					slot.putStack(stack.copy());
					slot.onSlotChanged();
					stack.stackSize = 0;
					flag1 = true;
					break;
				}

				if (useEndIndex)
				{
					--k;
				}
				else
				{
					++k;
				}
			}
		}

		return flag1;
	}


	@Override
	/**
	 * Handles slot click.
	 *  
	 * @param mode 0 = basic click, 1 = shift click, 2 = hotbar, 3 = pick block, 4 = drop, 5 = ?, 6 = double click
	 */
	public ItemStack slotClick(int slotID, int clickedButton, int mode, EntityPlayer p)
	{
		if (slotID >= 0 && slotID < this.inventorySlots.size())
		{
			Slot sourceSlot = (Slot) this.inventorySlots.get(slotID);
			ItemStack slotStack = sourceSlot.getStack();

			//This section is for merging foods with differing expirations.
			if(mode == 0 && clickedButton == 0 && slotStack != null && p.inventory.getItemStack() != null)
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

					int l1 = clickedButton == 0 ? itemstack4.stackSize : 1;

					if (l1 > sourceSlot.getItemStackLimit(itemstack4) - slotStack.stackSize)
					{
						l1 = sourceSlot.getItemStackLimit(itemstack4) - slotStack.stackSize;
					}

					if (l1 > itemstack4.getMaxStackSize() - slotStack.stackSize)
					{
						l1 = itemstack4.getMaxStackSize() - slotStack.stackSize;
					}

					itemstack4.splitStack(l1);

					if (itemstack4.stackSize == 0)
					{
						p.inventory.setItemStack((ItemStack)null);
					}

					slotStack.stackSize += l1;
					return null;
				}
				else if (itemstack4.stackSize <= sourceSlot.getItemStackLimit(itemstack4))
				{
					sourceSlot.putStack(itemstack4);
					p.inventory.setItemStack(slotStack);
				}
			}

			// Hotbar press to remove from crafting output
			if (mode == 2 && slotID == 0 && slotStack != null)
			{
				//Removed During Port
				//CraftingHandler.preCraft(p, slotStack, craftMatrix);
			}
			// S and D hotkeys for trimming/combining food
			else if (mode == 7 && slotID >= 9 && slotID < 45)
			{
				if (sourceSlot.canTakeStack(p))
				{
					Slot destSlot = (Slot) this.inventorySlots.get(clickedButton);
					destSlot.putStack(slotStack);
					sourceSlot.putStack(null);
					return null;
				}
			}
			// Couldn't figure out what was causing the food dupe with a full inventory, so we're just going to block shift clicking for that case.
			else if (mode == 1 && slotID == 0 && isInventoryFull() && slotStack != null && slotStack.getItem() instanceof IFood)
				return null;
		}
		return super.slotClick(slotID, clickedButton, mode, p);
	}

	protected boolean isCraftingGridFull()
	{
		for(int i = 0; i < this.craftMatrix.getSizeInventory(); i++)
		{
			if(this.craftMatrix.getStackInSlot(i) == null)
				return false;
		}
		return true;
	}

	protected boolean isInventoryFull()
	{
		// Slots 9 through 44 are the standard inventory and hotbar.
		for (int i = 9; i < 45; i++)
		{
			if (((Slot) inventorySlots.get(i)).getStack() == null)
				return false;
		}
		return true;
	}

	public EntityPlayer getPlayer()
	{
		return this.thePlayer;
	}
}
