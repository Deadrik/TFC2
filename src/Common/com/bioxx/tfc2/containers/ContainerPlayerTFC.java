package com.bioxx.tfc2.containers;

import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.interfaces.IFood;
import com.bioxx.tfc2.containers.slots.SlotCraftingTFC;
import com.bioxx.tfc2.core.Food;
import com.bioxx.tfc2.core.PlayerInventory;

public class ContainerPlayerTFC extends ContainerPlayer
{

	public ContainerPlayerTFC(InventoryPlayer playerInv, boolean par2, EntityPlayer playerIn)
	{
		super(playerInv, par2, playerIn);
		this.craftMatrix = new InventoryCrafting(this, 3, 3);
		this.inventorySlots.clear();
		this.inventoryItemStacks.clear();
		this.addSlotToContainer(new SlotCraftingTFC(player, craftMatrix, craftResult, 0, 152, 36));
		int x;
		int y;

		for (x = 0; x < 2; ++x)
		{
			for (y = 0; y < 2; ++y)
				this.addSlotToContainer(new Slot(craftMatrix, y + x * 3, 82 + y * 18, 18 + x * 18));
		}

		for (x = 0; x < playerInv.armorInventory.size(); ++x)
		{
			int index = 36 +(3-x);
			final int k = x;
			final EntityEquipmentSlot ees = VALID_EQUIPMENT_SLOTS[k];
			this.addSlotToContainer(new Slot(playerInv, index, 8, 8 + x * 18)
			{
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
					return stack.getItem().isValidArmor(stack, ees, player);
				}

				@Override
				public boolean canTakeStack(EntityPlayer playerIn)
				{
					ItemStack itemstack = this.getStack();
					return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false : super.canTakeStack(playerIn);
				}

				@Override
				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture()
				{
					return ItemArmor.EMPTY_SLOT_NAMES[ees.getIndex()];
				}
			});
		}
		PlayerInventory.buildInventoryLayout(this, playerInv, 8, 107, false, true);

		//Manually built the remaining crafting slots because of an order issue. These have to be created after the default slots
		if(player.getEntityData().hasKey("craftingTable") || !player.world.isRemote)
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

		this.addSlotToContainer(new Slot(playerInv, 40, 62, 80)
		{
			/**
			 * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
			 */
			@Override
			public boolean isItemValid(ItemStack stack)
			{
				return super.isItemValid(stack);
			}
			@Override
			@SideOnly(Side.CLIENT)
			public String getSlotTexture()
			{
				return "minecraft:items/empty_armor_slot_shield";
			}
		});
		this.onCraftMatrixChanged(this.craftMatrix);
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */
	@Override
	public void onCraftMatrixChanged(IInventory iinventory)
	{
		if(player == null)
			return;
		super.onCraftMatrixChanged(iinventory);
		ItemStack is2 = CraftingManagerTFC.getInstance().findMatchingRecipe(this.craftMatrix, this.player.world);
		if(!is2.isEmpty())
			this.craftResult.setInventorySlotContents(0, is2);

		Slot craftOut = (Slot) this.inventorySlots.get(0);
		if (craftOut != null && craftOut.getHasStack())
		{
			ItemStack craftResult = craftOut.getStack();
			if (craftResult != ItemStack.EMPTY)
			{
				//Removed During Port
				/*if (craftResult.getItem() instanceof ItemFoodTFC)
					FoodCraftingHandler.updateOutput(thePlayer, craftResult, craftMatrix);
				else
					CraftingHandler.transferNBT(false, thePlayer, craftResult, craftMatrix);*/
			}
		}
		for (int i = 0; i < iinventory.getSizeInventory(); i++)
		{
			ItemStack is = iinventory.getStackInSlot(i);
			if(is != ItemStack.EMPTY && is.getCount() == 0)
			{
				iinventory.setInventorySlotContents(i, ForgeHooks.getContainerItem(is));
			}
		}

	}

	@Override
	public void onContainerClosed(EntityPlayer player)
	{
		if(!player.world.isRemote)
		{
			super.onContainerClosed(player);

			for (int i = 0; i < 9; ++i)
			{
				ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);
				if (!itemstack.isEmpty())
					player.dropItem(itemstack, false);
			}

			this.craftResult.setInventorySlotContents(0, ItemStack.EMPTY);
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotNum)
	{
		ItemStack origStack = ItemStack.EMPTY;
		Slot slot = (Slot) this.inventorySlots.get(slotNum);
		//Slot equipmentSlot = (Slot) this.inventorySlots.get(50);

		if (slot != null && slot.getHasStack())
		{
			ItemStack slotStack = slot.getStack(); 
			origStack = slotStack.copy();
			InventoryPlayer ip = player.inventory;

			// Crafting Grid Output to inventory
			if (slotNum == 0)
			{
				//Removed During Port
				/*FoodCraftingHandler.preCraft(player, slotStack, craftMatrix);
				CraftingHandler.preCraft(player, slotStack, craftMatrix);*/

				if (!this.mergeItemStack(slotStack, 9, 45, true))
					return ItemStack.EMPTY;

				slot.onSlotChange(slotStack, origStack);
			}
			// From crafting grid input to inventory
			else if (slotNum >= 1 && slotNum < 5 || player.getEntityData().hasKey("craftingTable") && slotNum >= 45 && slotNum < 50)
			{
				if (!this.mergeItemStack(slotStack, 9, 45, true))
					return ItemStack.EMPTY;
				onCraftMatrixChanged(ip);
			}
			// From armor or equipment slot to inventory
			else if (slotNum >= 5 && slotNum < 9 || slotNum == 50)
			{
				if (!this.mergeItemStack(slotStack, 9, 45, true))
					return ItemStack.EMPTY;
			}
			// From inventory to armor slots
			//Removed During Port
			/*
			else if (origStack.getItem() instanceof ItemArmor)
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
					backStack.getMaxStackSize() = 1;
					equipmentSlot.putStack(backStack);
					slotStack.getMaxStackSize()--;
				}
			}
			// Food from inventory/hotbar to crafting grid
			else if (slotNum >= 9 && slotNum < 45 && origStack.getItem() instanceof IFood && !(origStack.getItem() instanceof ItemMeal) && !isCraftingGridFull())
			{
				if (!this.mergeItemStack(slotStack, 1, 5, false) && slotStack.getMaxStackSize() == 0)
					return null;
				else if (slotStack.getMaxStackSize() > 0 && player.getEntityData().hasKey("craftingTable") && !this.mergeItemStack(slotStack, 45, 50, false))
					return null;
				else if (slotStack.getMaxStackSize() > 0 && slotNum >= 9 && slotNum < 36)
				{
					if (!this.mergeItemStack(slotStack, 36, 45, false))
						return null;
				}
				else if (slotStack.getMaxStackSize() > 0 && slotNum >= 36 && slotNum < 45)
				{
					if (!this.mergeItemStack(slotStack, 9, 36, false))
						return null;
				}
			}*/
			// From inventory to hotbar
			else if (slotNum >= 9 && slotNum < 36)
			{
				if (!this.mergeItemStack(slotStack, 36, 45, false))
					return ItemStack.EMPTY;
			}
			// From hotbar to inventory
			else if (slotNum >= 36 && slotNum < 45)
			{
				if (!this.mergeItemStack(slotStack, 9, 36, false))
					return ItemStack.EMPTY;
			}

			if (slotStack.isEmpty())
				slot.putStack(ItemStack.EMPTY);
			else
				slot.onSlotChanged();

			if (slotStack.getCount() == origStack.getCount())
				return ItemStack.EMPTY;

			ItemStack itemstack2 = slot.onTake(player, slotStack);
			if (slotNum == 0)
				player.dropItem(itemstack2, false);
		}

		return origStack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
	{
		boolean flag = false;
		int i = startIndex;

		if (reverseDirection)
		{
			i = endIndex - 1;
		}

		if (stack.isStackable())
		{
			while (!stack.isEmpty())
			{
				if (reverseDirection)
				{
					if (i < startIndex)
					{
						break;
					}
				}
				else if (i >= endIndex)
				{
					break;
				}

				Slot slot = (Slot)this.inventorySlots.get(i);
				ItemStack itemstack = slot.getStack();

				if (!itemstack.isEmpty() && itemstack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getMetadata() == itemstack.getMetadata()) && ContainerTFC.areCompoundsEqual(stack, itemstack))
				{
					int j = itemstack.getCount() + stack.getCount();
					int maxSize = Math.min(slot.getSlotStackLimit(), stack.getMaxStackSize());

					if(stack.getItem() instanceof IFood && itemstack.getItem() instanceof IFood)
					{
						long ex1 = Food.getDecayTimer(stack);
						long ex2 = Food.getDecayTimer(itemstack);
						if(ex1 < ex2)
							Food.setDecayTimer(itemstack, ex1);
					}

					if (j <= maxSize)
					{
						stack.setCount(0);
						itemstack.setCount(j);
						slot.onSlotChanged();
						flag = true;
					}
					else if (itemstack.getCount() < maxSize)
					{
						stack.shrink(maxSize - itemstack.getCount());
						itemstack.setCount(maxSize);
						slot.onSlotChanged();
						flag = true;
					}
				}

				if (reverseDirection)
				{
					--i;
				}
				else
				{
					++i;
				}
			}
		}

		if (!stack.isEmpty())
		{
			if (reverseDirection)
			{
				i = endIndex - 1;
			}
			else
			{
				i = startIndex;
			}

			while (true)
			{
				if (reverseDirection)
				{
					if (i < startIndex)
					{
						break;
					}
				}
				else if (i >= endIndex)
				{
					break;
				}

				Slot slot1 = (Slot)this.inventorySlots.get(i);
				ItemStack itemstack1 = slot1.getStack();

				if (itemstack1.isEmpty() && slot1.isItemValid(stack))
				{
					if (stack.getCount() > slot1.getSlotStackLimit())
					{
						slot1.putStack(stack.splitStack(slot1.getSlotStackLimit()));
					}
					else
					{
						slot1.putStack(stack.splitStack(stack.getCount()));
					}

					slot1.onSlotChanged();
					flag = true;
					break;
				}

				if (reverseDirection)
				{
					--i;
				}
				else
				{
					++i;
				}
			}
		}

		return flag;
	}


	@Override
	/**
	 * Handles slot click.
	 */
	public ItemStack slotClick(int slotID, int dragType, ClickType clickTypeIn, EntityPlayer p)
	{
		if (clickTypeIn == ClickType.SWAP && slotID >= 5 && slotID <=8)  //Vanilla armor slots
			return ItemStack.EMPTY;  //Disable HotBar Keys for now (to prevent free items exploits)
		if (slotID >= 0 && slotID < this.inventorySlots.size())
		{
			Slot sourceSlot = (Slot) this.inventorySlots.get(slotID);
			ItemStack slotStack = sourceSlot.getStack();
			if (slotStack == null)  return ItemStack.EMPTY;

			//--- Hotbar slots 1-9 HotKeys --- 
			if (clickTypeIn == ClickType.SWAP && dragType >= 0 && dragType < 9)
			{
				int hbID = 36 + dragType;

				if (slotID == 0)  //Crafting output slot
				{
					if (mergeItemStack(slotStack, hbID, hbID+1, false))
					{
						sourceSlot.onSlotChanged();
						ItemStack itemstack2 = sourceSlot.onTake(p, slotStack);
						p.dropItem(itemstack2, false);
						return ItemStack.EMPTY;
					}
					else
						return ItemStack.EMPTY;
				}
			}

			if(clickTypeIn == ClickType.PICKUP && !p.inventory.getItemStack().isEmpty())
			{
				ItemStack mouseStack = p.inventory.getItemStack();
				if (slotStack.getItem() == mouseStack.getItem() && slotStack.getMetadata() == mouseStack.getMetadata() && ContainerTFC.areCompoundsEqual(slotStack, mouseStack))
				{
					if(slotStack.getItem() instanceof IFood && mouseStack.getItem() instanceof IFood)
					{
						long ex1 = Food.getDecayTimer(slotStack);
						long ex2 = Food.getDecayTimer(mouseStack);
						if(ex2 < ex1)
							Food.setDecayTimer(slotStack, ex2);
					}

					int mouseStackSize = mouseStack.getCount();

					if (mouseStackSize > sourceSlot.getItemStackLimit(mouseStack) - slotStack.getCount())
					{
						mouseStackSize = sourceSlot.getItemStackLimit(mouseStack) - slotStack.getCount();
					}

					if (mouseStackSize > mouseStack.getMaxStackSize() - slotStack.getCount())
					{
						mouseStackSize = mouseStack.getMaxStackSize() - slotStack.getCount();
					}

					mouseStack.splitStack(mouseStackSize);

					if (mouseStack.getCount() == 0)
					{
						p.inventory.setItemStack(ItemStack.EMPTY);
					}

					slotStack.grow(mouseStackSize);
					return ItemStack.EMPTY;
				}
				else if (mouseStack.getMaxStackSize() <= sourceSlot.getItemStackLimit(mouseStack))
				{
					sourceSlot.putStack(mouseStack);
					p.inventory.setItemStack(slotStack);
					return ItemStack.EMPTY;
				}
			}

			// Hotbar press to remove from crafting output
			if (clickTypeIn == ClickType.QUICK_CRAFT && slotID == 0)
			{
				//Removed During Port
				//CraftingHandler.preCraft(p, slotStack, craftMatrix);
			}
			// S and D hotkeys for trimming/combining food

			// Couldn't figure out what was causing the food dupe with a full inventory, so we're just going to block shift clicking for that case.
			/*else if (mode == 1 && slotID == 0 && isInventoryFull() && slotStack.getItem() instanceof IFood)
				return null;*/
		}
		return super.slotClick(slotID, dragType, clickTypeIn, p);
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
		return this.player;
	}
}
