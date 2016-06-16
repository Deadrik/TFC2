package com.bioxx.tfc2.gui;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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
			buttonList.add(new GuiKnappingRecipeButton(1+i, 3+guiLeft+50 + (x * 18), 4+guiTop + (y * 19), 18, 18, recipes.get(i).getRecipeOutput()));
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();

		if(recipeIndex != anvil.getField(0))
		{
			GuiButton button;
			for(int i = 1; i < buttonList.size(); i++)
			{
				button = buttonList.get(i);
				if(i-1 == anvil.getField(0))
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
			anvil.startCrafting(EntityPlayer.getUUID(Minecraft.getMinecraft().thePlayer.getGameProfile()));
		}
		else
			anvil.setField(0, guibutton.id-1);

	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int p, int j)
	{
		drawGui(texture);
	}

	@Override
	protected void drawForeground(int guiLeft, int guiTop)
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
