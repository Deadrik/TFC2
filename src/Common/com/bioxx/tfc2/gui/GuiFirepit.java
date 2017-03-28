package com.bioxx.tfc2.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.containers.ContainerFirepit;
import com.bioxx.tfc2.tileentities.TileFirepit;

public class GuiFirepit extends GuiContainerTFC
{
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_firepit.png");

	TileFirepit tile;

	public GuiFirepit(InventoryPlayer par1IInventory, TileFirepit tile, World world, int x, int y, int z)
	{
		super(new ContainerFirepit(par1IInventory, tile, world, x, y, z), 176, 85);
		this.tile = tile;
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everythin in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{


	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		this.drawGui(texture);
		this.bindTexture(texture);
		int guiLeft = (this.width - this.xSize) / 2;
		int guiTop = (this.height - this.ySize) / 2;
		if (TileEntityFurnace.isBurning(this.tile))
		{
			int scale = this.getBurnLeftScaled(13);
			this.drawTexturedModalRect(guiLeft + 56, guiTop + 36 + 12 - scale, 176, 12 - scale, 14, scale + 1);
		}
		if(tile.getField(TileFirepit.FIELD_COOKINGMAX_TIMER) > 0)
		{
			int scale = this.getCookProgressScaled(24);
			this.drawTexturedModalRect(guiLeft + 79, guiTop + 34, 176, 14, scale + 1, 16);
		}
	}

	private int getCookProgressScaled(int pixels)
	{
		int i = this.tile.getField(TileFirepit.FIELD_COOKING_TIMER);
		int j = this.tile.getField(TileFirepit.FIELD_COOKINGMAX_TIMER);
		return j != 0 && i != 0 ? i * pixels / j : 0;
	}

	private int getBurnLeftScaled(int pixels)
	{
		int i = this.tile.getField(TileFirepit.FIELD_FUELMAX_TIMER);

		if (i == 0)
		{
			i = 200;
		}

		return this.tile.getField(TileFirepit.FIELD_FUEL_TIMER) * pixels / i;
	}
}
