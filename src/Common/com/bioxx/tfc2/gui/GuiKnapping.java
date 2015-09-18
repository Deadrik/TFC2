package com.bioxx.tfc2.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.containers.ContainerSpecialCrafting;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.networking.server.KnappingUpdatePacket;

public class GuiKnapping extends GuiContainerTFC
{
	private boolean previouslyLoaded;
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_knapping.png");

	public GuiKnapping(InventoryPlayer inventoryplayer, ItemStack is, World world, int x, int y, int z)
	{
		super(new ContainerSpecialCrafting(inventoryplayer, is, world, x, y, z), 176, 103);
	}

	@Override
	public void onGuiClosed()
	{
		PlayerManagerTFC.getInstance().getClientPlayer().knappingInterface = new boolean[81];
		super.onGuiClosed();
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.clear();
		((ContainerSpecialCrafting) this.inventorySlots).setDecreasedStack(false);

		for (int y = 0; y < 9; y++)
		{
			for (int x = 0; x < 9; x++)
			{
				buttonList.add(new GuiKnappingButton(x + (y * 9), 16+guiLeft + (x * 8), 16+guiTop + (y * 8), 8, 8));
				// Normal Behavior
				if (!previouslyLoaded)
				{
					if (PlayerManagerTFC.getInstance().getClientPlayer().knappingInterface[y * 9 + x])
					{
						resetButton(y * 9 + x);
					}
				}
				// GUI has been reloaded, usually caused by looking up a recipe in NEI while having the interface open.
				else
				{
					/*
					 * For whatever reason all my attempts at implementing this for all crafting types just wouldn't work for the clay ones.
					 * Types that completely remove pieces (rocks, leather) work properly to save states when reloaded with this.
					 */
					/*if (PlayerManagerTFC.getInstance().getClientPlayer().specialCraftingType.getItem() != TFCItems.flatClay && 
							((ContainerSpecialCrafting) this.inventorySlots).craftMatrix.getStackInSlot(y * 5 + x) == null)
					{
						resetButton(y * 5 + x);
					}*/
				}
			}
		}

		previouslyLoaded = true;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		resetButton(guibutton.id);
		TFC.network.sendToServer(new KnappingUpdatePacket(guibutton.id));
	}

	public void resetButton(int id)
	{
		if(PlayerManagerTFC.getInstance().getClientPlayer().specialCraftingTypeAlternate == null)
		{
			((GuiKnappingButton) this.buttonList.get(id)).visible = false;
		}
		PlayerManagerTFC.getInstance().getClientPlayer().knappingInterface[id] = true;
		((GuiKnappingButton) this.buttonList.get(id)).enabled = false;
		((ContainerSpecialCrafting) this.inventorySlots).craftMatrix.setInventorySlotContents(id, null);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p, int j)
	{
		drawGui(texture);
	}

	/**
	 * This function is what controls the hotbar shortcut check when you press a
	 * number key when hovering a stack.
	 */
	@Override
	protected boolean checkHotbarKeys(int par1)
	{
		if (this.mc.thePlayer.inventory.currentItem != par1 - 2)
		{
			super.checkHotbarKeys(par1);
			return true;
		}
		else
			return false;
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) 
	{
		if (clickedMouseButton == 0)
		{
			for (int l = 0; l < this.buttonList.size(); ++l)
			{
				GuiButton guibutton = (GuiButton)this.buttonList.get(l);

				if (guibutton.mousePressed(this.mc, mouseX, mouseY))
				{
					ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, guibutton, this.buttonList);
					if (MinecraftForge.EVENT_BUS.post(event))
						break;

					if(selectedButton == event.button)
						continue;
					else
					{
						this.mouseReleased(mouseX, mouseY, 0);
					}

					this.selectedButton = event.button;
					event.button.playPressSound(this.mc.getSoundHandler());
					this.actionPerformed(event.button);
					if (this.equals(this.mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.button, this.buttonList));
				}
			}
		}
	}
}
