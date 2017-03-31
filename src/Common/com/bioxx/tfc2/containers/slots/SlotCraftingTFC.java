package com.bioxx.tfc2.containers.slots;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.NonNullList;

import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;

public class SlotCraftingTFC extends Slot
{

	/** The craft matrix inventory linked to this result slot. */
	private final InventoryCrafting craftMatrix;
	/** The player that is using the GUI where this slot resides. */
	private final EntityPlayer player;
	/** The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset. */
	private int amountCrafted;

	public SlotCraftingTFC(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
		super(inventoryIn, slotIndex, xPosition, yPosition);
		this.player = player;
		this.craftMatrix = craftingInventory;
	}

	/**
	 * Check if the stack is allowed to be placed in this slot, used for armor slots as well as furnace fuel.
	 */
	@Override
	public boolean isItemValid(ItemStack stack)
	{
		return false;
	}

	/**
	 * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
	 * stack.
	 */
	@Override
	public ItemStack decrStackSize(int amount)
	{
		if (this.getHasStack())
		{
			this.amountCrafted += Math.min(amount, this.getStack().getCount());
		}

		return super.decrStackSize(amount);
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
	 * internal count then calls onCrafting(item).
	 */
	@Override
	protected void onCrafting(ItemStack stack, int amount)
	{
		this.amountCrafted += amount;
		this.onCrafting(stack);
	}

	@Override
	protected void onSwapCraft(int p_190900_1_)
	{
		this.amountCrafted += p_190900_1_;
	}

	/**
	 * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
	 */
	@Override
	protected void onCrafting(ItemStack stack)
	{
		if (this.amountCrafted > 0)
		{
			stack.onCrafting(this.player.world, this.player, this.amountCrafted);
			net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(this.player, stack, craftMatrix);
		}

		this.amountCrafted = 0;

		if (stack.getItem() == Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
		{
			this.player.addStat(AchievementList.BUILD_WORK_BENCH);
		}

		if (stack.getItem() instanceof ItemPickaxe)
		{
			this.player.addStat(AchievementList.BUILD_PICKAXE);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.FURNACE))
		{
			this.player.addStat(AchievementList.BUILD_FURNACE);
		}

		if (stack.getItem() instanceof ItemHoe)
		{
			this.player.addStat(AchievementList.BUILD_HOE);
		}

		if (stack.getItem() == Items.BREAD)
		{
			this.player.addStat(AchievementList.MAKE_BREAD);
		}

		if (stack.getItem() == Items.CAKE)
		{
			this.player.addStat(AchievementList.BAKE_CAKE);
		}

		if (stack.getItem() instanceof ItemPickaxe && ((ItemPickaxe)stack.getItem()).getToolMaterial() != Item.ToolMaterial.WOOD)
		{
			this.player.addStat(AchievementList.BUILD_BETTER_PICKAXE);
		}

		if (stack.getItem() instanceof ItemSword)
		{
			this.player.addStat(AchievementList.BUILD_SWORD);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.ENCHANTING_TABLE))
		{
			this.player.addStat(AchievementList.ENCHANTMENTS);
		}

		if (stack.getItem() == Item.getItemFromBlock(Blocks.BOOKSHELF))
		{
			this.player.addStat(AchievementList.BOOKCASE);
		}
	}

	@Override
	public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack)
	{
		this.onCrafting(stack);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(thePlayer);
		NonNullList<ItemStack> nonnulllist = CraftingManagerTFC.getInstance().getRemainingItems(this.craftMatrix, thePlayer.world);
		net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

		for (int i = 0; i < nonnulllist.size(); ++i)
		{
			ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
			ItemStack itemstack1 = (ItemStack)nonnulllist.get(i);

			if (!itemstack.isEmpty())
			{
				this.craftMatrix.decrStackSize(i, 1);
				itemstack = this.craftMatrix.getStackInSlot(i);
			}

			if (!itemstack1.isEmpty())
			{
				if (itemstack.isEmpty())
				{
					this.craftMatrix.setInventorySlotContents(i, itemstack1);
				}
				else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1))
				{
					itemstack1.grow(itemstack.getCount());
					this.craftMatrix.setInventorySlotContents(i, itemstack1);
				}
				else if (!this.player.inventory.addItemStackToInventory(itemstack1))
				{
					this.player.dropItem(itemstack1, false);
				}
			}
		}

		return stack;
	}

}
