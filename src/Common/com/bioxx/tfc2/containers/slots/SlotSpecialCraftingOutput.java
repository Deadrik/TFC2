package com.bioxx.tfc2.containers.slots;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.common.FMLCommonHandler;

import com.bioxx.tfc2.containers.ContainerSpecialCrafting;
import com.bioxx.tfc2.gui.GuiKnapping;

public class SlotSpecialCraftingOutput extends Slot
{
	private final IInventory craftMatrix;
	private EntityPlayer thePlayer;
	private Container container;

	public SlotSpecialCraftingOutput(Container container, EntityPlayer entityplayer, IInventory iinventory, IInventory iinventory1, int i, int j, int k)
	{
		super(iinventory1, i, j, k);
		this.container = container;
		thePlayer = entityplayer;
		craftMatrix = iinventory;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack)
	{
		return false;
	}


	@Override
	public ItemStack onTake(EntityPlayer player, ItemStack itemstack)
	{
		itemstack.onCrafting(player.world, thePlayer, slotNumber);
		FMLCommonHandler.instance().firePlayerCraftingEvent(player, itemstack, player.inventory);

		for (int i = 0; i < craftMatrix.getSizeInventory(); i++)
		{
			// Clear out everything in the crafting matrix.
			craftMatrix.setInventorySlotContents(i, ItemStack.EMPTY);
			if (player.world.isRemote)
			{
				((GuiKnapping) Minecraft.getMinecraft().currentScreen).resetButton(i);
			}
		}

		// Reset decreasedStack flag so another item can be created if the clay forming is reset with NEI.
		((ContainerSpecialCrafting) container).setDecreasedStack(false);
		return itemstack;
	}
}
