package com.bioxx.tfc2.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.containers.ContainerVessel;
import com.bioxx.tfc2.core.PlayerInventory;

public class GuiVessel extends GuiContainerTFC
{
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_vessel.png");

	/**
	 * window height is calculated with this values, the more rows, the heigher
	 */

	public GuiVessel(InventoryPlayer par1IInventory, World world)
	{
		super(new ContainerVessel(par1IInventory, world), 176, 86);
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everythin in front of the items)
	 */
	protected void drawGuiContainerForegroundLayer()
	{

	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
	{
		this.drawGui(texture);
		PlayerInventory.drawInventory(this, width, height, ySize-PlayerInventory.invYSize);
	}

	/**
	 * This function is what controls the hotbar shortcut check when you press a
	 * number key when hovering a stack.
	 */
	@Override
	protected boolean checkHotbarKeys(int key)
	{
		if (this.mc.player.inventory.currentItem != key)
		{
			super.checkHotbarKeys(key);
			return true;
		}
		else
			return false;
	}
}
