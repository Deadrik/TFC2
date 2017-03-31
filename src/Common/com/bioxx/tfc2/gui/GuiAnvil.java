package com.bioxx.tfc2.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC;
import com.bioxx.tfc2.api.crafting.CraftingManagerTFC.RecipeType;
import com.bioxx.tfc2.api.interfaces.IRecipeTFC;
import com.bioxx.tfc2.containers.ContainerAnvil;
import com.bioxx.tfc2.tileentities.TileAnvil;

public class GuiAnvil extends GuiContainerTFC
{
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_anvil.png");

	TileAnvil anvil;
	private int recipeIndex = -1;
	private ItemStack is1 = ItemStack.EMPTY;
	private ItemStack is2 = ItemStack.EMPTY;

	public GuiAnvil(InventoryPlayer inventoryplayer, TileAnvil te, World world, int x, int y, int z)
	{
		super(new ContainerAnvil(inventoryplayer, te, world, x, y, z), 176, 86);
		anvil = te;
	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.clear();
		buttonList.add(new GuiButton(0, guiLeft+4, guiTop+62, 42, 20, "Craft"));

		List<IRecipeTFC> recipes = CraftingManagerTFC.getInstance().getRecipeList(RecipeType.ANVIL);

		int x = 0, y = 0;
		for (int i = 0; i < recipes.size(); i++)
		{
			y = i / 4;
			x = i % 4;
			GuiKnappingRecipeButton button = new GuiKnappingRecipeButton(1+i, 3+guiLeft+50 + (x * 18), 4+guiTop + (y * 19), 18, 18, recipes.get(i).getRecipeOutput());
			button.recipeIndex = i;
			if(recipes.get(i).matches(anvil.getInventory(), anvil.getWorld()))
				buttonList.add(button);
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if(anvil.getStackInSlot(0) != is1)
		{
			initGui();
			is1 = anvil.getStackInSlot(0);
		}
		if(anvil.getStackInSlot(1) != is2)
		{
			initGui();
			is2 = anvil.getStackInSlot(1);
		}

		if(recipeIndex == -1)
			buttonList.get(0).enabled = false;
		else
			buttonList.get(0).enabled = true;

		if(recipeIndex != anvil.getField(0))
		{
			GuiKnappingRecipeButton button;
			for(int i = 1; i < buttonList.size(); i++)
			{
				button = (GuiKnappingRecipeButton) buttonList.get(i);
				if(button.recipeIndex == anvil.getField(0))
				{
					button.enabled = false;
				}
				else
				{
					button.enabled = true;
				}
			}

			recipeIndex = anvil.getField(0);
		}

	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if(guibutton.id == 0)
		{
			anvil.startCrafting(EntityPlayer.getUUID(Minecraft.getMinecraft().player.getGameProfile()));
			Minecraft.getMinecraft().player.closeScreen();
		}
		else
		{
			anvil.setField(0, ((GuiKnappingRecipeButton)guibutton).recipeIndex);
		}

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p, int j)
	{
		drawGui(texture);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		//this.drawTexturedModalRect(this.guiLeft+175, this.guiTop, 176, 0, 80, 190);
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
	}
}
