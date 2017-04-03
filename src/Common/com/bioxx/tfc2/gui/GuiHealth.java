package com.bioxx.tfc2.gui;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.interfaces.IFoodStatsTFC;
import com.bioxx.tfc2.api.types.EnumFoodGroup;
import com.bioxx.tfc2.containers.ContainerSkills;

public class GuiHealth extends GuiContainerTFC
{
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_health.png");
	protected EntityPlayer player;

	public GuiHealth(EntityPlayer player)
	{
		super(new ContainerSkills(player), 176, 104);
		this.setDrawInventory(false);
		this.player = player;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		int x = 5; int y = 14;

		Core.bindTexture(texture);
		IFoodStatsTFC food = (IFoodStatsTFC)player.getFoodStats();
		drawTexturedModalRect(x+50, y, 0, 106, (int) (food.getNutritionMap().get(EnumFoodGroup.Fruit) / 20 * 24), 6);
		drawTexturedModalRect(x+50, y+10, 0, 106, (int) (food.getNutritionMap().get(EnumFoodGroup.Vegetable) / 20 * 24), 6);
		drawTexturedModalRect(x+50, y+20, 0, 106, (int) (food.getNutritionMap().get(EnumFoodGroup.Grain) / 20 * 24), 6);
		drawTexturedModalRect(x+50, y+30, 0, 106, (int) (food.getNutritionMap().get(EnumFoodGroup.Protein) / 20 * 24), 6);
		drawTexturedModalRect(x+50, y+40, 0, 106, (int) (food.getNutritionMap().get(EnumFoodGroup.Dairy) / 20 * 24), 6);

		fontRenderer.drawString(Core.translate("gui.food.fruit"), x, y, 0, false);
		fontRenderer.drawString(Core.translate("gui.food.vegetable"), x, y+10, 0, false);
		fontRenderer.drawString(Core.translate("gui.food.grain"), x, y+20, 0, false);
		fontRenderer.drawString(Core.translate("gui.food.protein"), x, y+30, 0, false);
		fontRenderer.drawString(Core.translate("gui.food.dairy"), x, y+40, 0, false);
		if (TFCOptions.enableDebugMode)
		{
			fontRenderer.drawString(Float.toString(food.getNutritionMap().get(EnumFoodGroup.Fruit)), 85, 13, 0, false);
			fontRenderer.drawString(Float.toString(food.getNutritionMap().get(EnumFoodGroup.Vegetable)), 85, 23, 0, false);
			fontRenderer.drawString(Float.toString(food.getNutritionMap().get(EnumFoodGroup.Grain)), 85, 33, 0, false);
			fontRenderer.drawString(Float.toString(food.getNutritionMap().get(EnumFoodGroup.Protein)), 85, 43, 0, false);
			fontRenderer.drawString(Float.toString(food.getNutritionMap().get(EnumFoodGroup.Dairy)), 85, 53, 0, false);
		}

		fontRenderer.drawString(Core.translate("gui.healthpage"), this.xSize / 2 - fontRenderer.getStringWidth(Core.translate("gui.healthpage")) / 2, 4, 4210752, false);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		this.drawGui(texture);
	}

	@Override
	protected void drawGui(ResourceLocation rl)
	{
		bindTexture(rl);
		guiLeft = (width - xSize) / 2;
		guiTop = (height - ySize) / 2; //Shifted 34 pixels up to match other inventory tabs
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize); //No inventory drawn, so shifted ySize is not necessary
	}

	@Override
	public void initGui()
	{
		super.initGui();
		createButtons();
	}

	@Override
	protected void actionPerformed(GuiButton guibutton)
	{
		if (guibutton.id == 0)
			Minecraft.getMinecraft().displayGuiScreen(new GuiInventoryTFC(Minecraft.getMinecraft().player));
		else if (guibutton.id == 1)
			Minecraft.getMinecraft().displayGuiScreen(new GuiSkills(Minecraft.getMinecraft().player));
		/*else if (guibutton.id == 2)
			Minecraft.getMinecraft().displayGuiScreen(new GuiCalendar(Minecraft.getMinecraft().player));*/
	}

	public void createButtons()
	{
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		buttonList.clear();
		buttonList.add(new GuiInventoryButton(0, new Rectangle(guiLeft+176, guiTop + 3, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Inventory.Inventory"), new Rectangle(1,223,32,32)));
		buttonList.add(new GuiInventoryButton(1, new Rectangle(guiLeft+176, guiTop + 22, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Inventory.Skills"), new Rectangle(100,223,32,32)));
		buttonList.add(new GuiInventoryButton(2, new Rectangle(guiLeft+176, guiTop + 41, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Calendar.Calendar"), new Rectangle(34,223,32,32)));
		buttonList.add(new GuiInventoryButton(3, new Rectangle(guiLeft+176, guiTop + 60, 25, 20), 
				new Rectangle(0, 103, 25, 20), Core.translate("gui.Inventory.Health"), new Rectangle(67,223,32,32)));
	}
}
