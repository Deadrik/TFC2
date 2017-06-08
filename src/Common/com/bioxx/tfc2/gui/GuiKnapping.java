package com.bioxx.tfc2.gui;

import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC.RecipeType;
import com.bioxx.tfc2.api.interfaces.IRecipeTFC;
import com.bioxx.tfc2.containers.ContainerSpecialCrafting;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.networking.server.SKnappingPacket;

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
		PlayerManagerTFC.getInstance().getClientPlayer().shouldDrawKnappingHighlight = false;
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

		List<IRecipeTFC> recipes = CraftingManagerTFC.getInstance().getRecipeList(RecipeType.KNAPPING);
		if(PlayerManagerTFC.getInstance().getClientPlayer().specialCraftingType.getItem() == Items.CLAY_BALL)
			recipes = CraftingManagerTFC.getInstance().getRecipeList(RecipeType.POTTERY);
		int x = 0, y = 0;
		for (int i = 0; i < recipes.size(); i++)
		{
			y = i / 4;
			x = i % 4;
			buttonList.add(81, new GuiKnappingRecipeButton(81 + i, 3+guiLeft+176 + (x * 18), 4+guiTop + (y * 19), 18, 18, recipes.get(i).getRecipeOutput()));
		}

		previouslyLoaded = true;
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(guibutton.id < 81)
		{
			resetButton(guibutton.id);
			TFC.network.sendToServer(new SKnappingPacket(guibutton.id));

			/*PlayerInfo pi = PlayerManagerTFC.getInstance().getClientPlayer();
			StringBuilder out = new StringBuilder("");
			String[] temp = new String[]{"","","","","","","","",""};
			int x = 0, y = 0;
			for(int i = 0; i < 81; i++)
			{
				y = i / 9;
				//temp[y] += pi.knappingInterface[i] ? " " : "X";
				temp[y] += pi.knappingInterface[i] ? "X" : " ";
			}
			out = out.append("\"").append(temp[0]).append("\",").append("\"").append(temp[1]).append("\",")
					.append("\"").append(temp[2]).append("\",").append("\"").append(temp[3]).append("\",")
					.append("\"").append(temp[4]).append("\",").append("\"").append(temp[5]).append("\",")
					.append("\"").append(temp[6]).append("\",").append("\"").append(temp[7]).append("\",")
					.append("\"").append(temp[8]).append("\"");
			StringSelection selection = new StringSelection(out.toString());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			if(clipboard != null)
				clipboard.setContents(selection, selection);*/

		}
		else
		{
			PlayerManagerTFC.getInstance().getClientPlayer().shouldDrawKnappingHighlight = true;
			highlightGrid(guibutton.id);
		}
	}

	private void highlightGrid(int id)
	{
		for(int i = 0; i < 81; i++)
		{
			((GuiKnappingButton) this.buttonList.get(i)).highlight(true);
		}
		List<IRecipeTFC> recipes = CraftingManagerTFC.getInstance().getRecipeList(RecipeType.KNAPPING);
		if(PlayerManagerTFC.getInstance().getClientPlayer().specialCraftingType.getItem() == Items.CLAY_BALL)
			recipes = CraftingManagerTFC.getInstance().getRecipeList(RecipeType.POTTERY);
		IRecipeTFC rec = recipes.get(id - 81);
		for(int i = 0; i < rec.getRecipeSize(); i++)
		{
			int x = i % rec.getRecipeWidth();
			int y = i / rec.getRecipeWidth();
			if(PlayerManagerTFC.getInstance().getClientPlayer().specialCraftingType.getItem() == TFCItems.LooseRock)
			{
				if(rec.getRecipeItems().get(x+y*rec.getRecipeWidth()) != ItemStack.EMPTY)
					((GuiKnappingButton) this.buttonList.get(x+y*9)).highlight(false);
			}
			else
			{
				if(((ItemStack)(rec.getRecipeItems().get(x+y*rec.getRecipeWidth()))) == ItemStack.EMPTY)
					((GuiKnappingButton) this.buttonList.get(x+y*9)).highlight(false);
			}
		}
	}

	public void resetButton(int id)
	{
		if(PlayerManagerTFC.getInstance().getClientPlayer().specialCraftingTypeAlternate == null)
		{
			((GuiKnappingButton) this.buttonList.get(id)).visible = false;
		}
		PlayerManagerTFC.getInstance().getClientPlayer().knappingInterface[id] = true;
		((GuiKnappingButton) this.buttonList.get(id)).enabled = false;
		((ContainerSpecialCrafting) this.inventorySlots).craftMatrix.removeStackFromSlot(id);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ticks, int mouseX, int mouseY)
	{
		drawGui(texture);
		Core.bindTexture(texture);
		this.drawTexturedModalRect(this.guiLeft+175, this.guiTop, 176, 0, 80, 190);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{

	}

	/**
	 * This function is what controls the hotbar shortcut check when you press a
	 * number key when hovering a stack.
	 */
	@Override
	protected boolean checkHotbarKeys(int par1)
	{
		if (this.mc.player.inventory.currentItem != par1 - 2)
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
		// 1-st Check if the click falls inside the Knapping Grid boundaries. 
		// (Doing so reduces the lag & allows for super methods to run when inventory slots are clicked.)
		if (mouseY > 88+guiTop || mouseX > 88+guiLeft || mouseY < 16+guiTop || mouseX < 16+guiLeft)
		{
			super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
			return;
		}
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

					if(selectedButton == event.getButton())
						continue;
					else
					{
						this.mouseReleased(mouseX, mouseY, 0);
					}

					this.selectedButton = event.getButton();
					event.getButton().playPressSound(this.mc.getSoundHandler());
					this.actionPerformed(event.getButton());
					if (this.equals(this.mc.currentScreen))
						MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.getButton(), this.buttonList));
				}
			}
		}
		else
			super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
	}
}
