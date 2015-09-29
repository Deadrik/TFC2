package com.bioxx.tfc2.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.containers.ContainerCart;
import com.bioxx.tfc2.core.PlayerInventory;
import com.bioxx.tfc2.entity.InventoryCart;

public class GuiCart extends GuiContainerTFC
{
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_cart.png");

	/**
	 * window height is calculated with this values, the more rows, the heigher
	 */

	public GuiCart(InventoryPlayer par1IInventory, InventoryCart chestInv, World world, int x, int y, int z)
	{
		super(new ContainerCart(par1IInventory, chestInv, world, x, y, z), 176, 88);
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
}
