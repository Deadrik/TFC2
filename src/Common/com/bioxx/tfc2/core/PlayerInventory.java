package com.bioxx.tfc2.core;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.containers.slots.SlotForShowOnly;

public class PlayerInventory
{
	public static int invXSize = 176;
	public static int invYSize = 90;
	private static ResourceLocation invTexture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_inventory_lower.png");
	public static InventoryCrafting containerInv;
	private static int index;

	public static void buildInventoryLayout(Container container, InventoryPlayer inventory, int x, int y, boolean freezeSlot, boolean toolBarAfterMainInv)
	{
		index = 0;
		if(!toolBarAfterMainInv)
			addToolbarSlots(container, inventory, x, y, freezeSlot);

		for(int i = 0; i < 3; ++i)
		{
			for(int k = 0; k < 9; ++k)
			{
				index =  k + (i+1) * 9;
				addSlotToContainer(container, new Slot(inventory, index, x + k * 18, y + i * 18));
			}
		}

		if(toolBarAfterMainInv)
			addToolbarSlots(container, inventory, x, y, freezeSlot);

		/*ItemStack is = getInventory(inventory.player).extraEquipInventory[0];
		if(is != null)
		{
			if(is.getItem() instanceof ItemQuiver)
			{
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 178, y));
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 178, y+18));
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 178, y+36));
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 178, y+54));
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 196, y));
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 196, y+18));
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 196, y+36));
				addSlotToContainer(container, new SlotQuiver(containerInv, index++, x + 196, y+54));
			}
			loadBagInventory(is, container);
		}*/
	}

	public static void loadBagInventory(ItemStack is, Container c)
	{
		if(is != null && is.hasTagCompound())
		{
			NBTTagList nbttaglist = is.getTagCompound().getTagList("Items", 10);
			containerInv = new InventoryCrafting(c, 4, 2);
			for(int i = 0; i < nbttaglist.tagCount(); i++)
			{
				NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
				byte byte0 = nbttagcompound1.getByte("Slot");
				if(byte0 >= 0 && byte0 < 8)
					containerInv.setInventorySlotContents(byte0, new ItemStack(nbttagcompound1));
			}
		}
	}

	public static void addExtraEquipables(Container container, InventoryPlayer inventory, int x, int y, boolean freezeSlot){
		int index = 36; // Should be the correct index
		//Removed during port
		//addSlotToContainer(container, new SlotExtraEquipable(inventory, index, 8 + 18, 8 + 18, IEquipable.EquipType.BACK));
	}

	private static void addToolbarSlots(Container container, InventoryPlayer inventory, int x, int y, boolean freezeSlot) 
	{
		for(int j = 0; j < 9; ++j)
		{
			if(freezeSlot && j == inventory.currentItem)
				addSlotToContainer(container, new SlotForShowOnly(inventory, j, x + j * 18, y+58));
			else
				addSlotToContainer(container, new Slot(inventory, j, x + j * 18, y+58));
		}
	}

	public static void buildInventoryLayout(Container container, InventoryPlayer inventory, int x, int y, boolean freezeSlot)
	{
		buildInventoryLayout(container, inventory, x, y, false, false);
	}

	public static void buildInventoryLayout(Container container, InventoryPlayer inventory, int x, int y)
	{
		buildInventoryLayout(container, inventory, x, y, false);
	}

	protected static Slot addSlotToContainer(Container container, Slot par1Slot)
	{
		par1Slot.slotNumber = container.inventorySlots.size();
		container.inventorySlots.add(par1Slot);
		container.inventoryItemStacks.add(ItemStack.EMPTY);
		return par1Slot;
	}

	public static void drawInventory(GuiContainer container, int screenWidth, int screenHeight, int upperGuiHeight)
	{
		Core.bindTexture(invTexture);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiX = (screenWidth - invXSize) / 2;
		int guiY = (screenHeight - (upperGuiHeight+invYSize)) / 2 + upperGuiHeight;
		container.drawTexturedModalRect(guiX, guiY, 0, 0, invXSize, invYSize);

		//encumbrance bar
		float eMult = Math.min(Core.getEncumbrance(net.minecraft.client.Minecraft.getMinecraft().player.inventory.mainInventory) / 80f, 1.0f);
		if(eMult < 0.5)
			GL11.glColor4f(0.0F, 0.8F, 0.0F, 1.0F);
		else if(eMult < 0.75)
			GL11.glColor4f(1.0F, 0.8F, 0.0F, 1.0F);
		else
			GL11.glColor4f(0.8F, 0.0F, 0.0F, 1.0F);
		container.drawTexturedModalRect(guiX+8, guiY+5, 2, 245, (int)(160 * eMult), 3);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		//encumbrance meter
		container.drawTexturedModalRect(guiX+7, guiY+4, 1, 249, 162, 5);
	}

	/*public static InventoryPlayerTFC getInventory(EntityPlayer p)
	{
		return (InventoryPlayerTFC)p.inventory;
	}*/

	public static void upgradePlayerCrafting(EntityPlayer player)
	{
		if(player.getEntityData().hasKey("craftingTable"))
		{
			player.inventoryContainer.getSlot(45).xPos += 50000;
			player.inventoryContainer.getSlot(46).xPos += 50000;
			player.inventoryContainer.getSlot(47).xPos += 50000;
			player.inventoryContainer.getSlot(48).xPos += 50000;
			player.inventoryContainer.getSlot(49).xPos += 50000;
		}
	}


	public static ItemStack transferStackInSlot(EntityPlayer player, ItemStack stackToXfer)
	{
		return null;
	}
}
