package com.bioxx.tfc2.gui;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.api.SkillsManager;
import com.bioxx.tfc2.api.SkillsManager.Skill;
import com.bioxx.tfc2.containers.ContainerSkills;
import com.bioxx.tfc2.core.PlayerSkillData;


public class GuiSkills extends GuiContainerTFC
{
	public static ResourceLocation texture = new ResourceLocation(Reference.ModID, Reference.AssetPathGui + "gui_skills.png");
	protected EntityPlayer player;
	private int skillsPage;
	private static final int SKILLS_PER_PAGE = 9;
	private static final int skillBarWidth = 168;

	public GuiSkills(EntityPlayer player)
	{
		super(new ContainerSkills(player), 176, 166);
		this.setDrawInventory(false);
		this.player = player;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRenderer.drawString(Core.translate("gui.skillpage"), this.xSize / 2 - fontRenderer.getStringWidth(Core.translate("gui.skillpage")) / 2, 4, 4210752, false);
		PlayerSkillData ss = Core.getPlayerSkillData(player);
		int y = 10;
		int count = -1;
		for (Skill o : SkillsManager.instance.getSkillsArray())
		{
			count++;
			if (count < (SKILLS_PER_PAGE * skillsPage) + SKILLS_PER_PAGE && count >= (SKILLS_PER_PAGE * skillsPage))
			{
				bindTexture(texture);
				drawTexturedModalRect(4, y, 4, 208, skillBarWidth, 16);
				y += 12;
				float perc = ss.getPercToNextRank(o.skillName);
				drawTexturedModalRect(4, y, 4, skillBarWidth, skillBarWidth, 4);
				drawTexturedModalRect(4, y, 4, 172, (int) Math.floor(skillBarWidth * perc), 4);

				fontRenderer.drawString(Core.translate(o.skillName) + ": " + TextFormatting.DARK_BLUE + ss.getSkillRank(o.skillName).getLocalizedName(), 6, y - 9, 0, false);
				y += 3;
			}
		}
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
		guiTop = (height - ySize) / 2 - 3; //Shifted 3 pixels up to match other inventory tabs
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
		/*else if (guibutton.id == 2)
			Minecraft.getMinecraft().displayGuiScreen(new GuiCalendar(Minecraft.getMinecraft().player));*/
		else if (guibutton.id == 3)
			Minecraft.getMinecraft().displayGuiScreen(new GuiHealth(Minecraft.getMinecraft().player));
		else if (guibutton.id == 4)
		{
			if (skillsPage > 0)
				skillsPage--;
		}
		else if (guibutton.id == 5)
		{
			if (9 + (skillsPage * SKILLS_PER_PAGE) < SkillsManager.instance.getSkillsArray().size())
				skillsPage++;
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		if (skillsPage == 0)
			((GuiButton) buttonList.get(4)).enabled = false;
		else
			((GuiButton) buttonList.get(4)).enabled = true;

		if (9 + (skillsPage * SKILLS_PER_PAGE) < SkillsManager.instance.getSkillsArray().size())
			((GuiButton) buttonList.get(5)).enabled = true;
		else
			((GuiButton) buttonList.get(5)).enabled = false;
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
		buttonList.add(new GuiButtonPage(4, guiLeft + 4, guiTop + 144, 30, 15, 0, 177));
		buttonList.add(new GuiButtonPage(5, guiLeft + 142, guiTop + 144, 30, 15, 0, 192));
	}

	public class GuiButtonPage extends GuiButton
	{
		private int u, v;

		public GuiButtonPage(int id, int xPos, int yPos, int xSize, int ySize, int u, int v)
		{
			super(id, xPos, yPos, xSize, ySize, "");
			this.u = u;
			this.v = v;
		}

		@Override
		public void drawButton(Minecraft par1Minecraft, int xPos, int yPos)
		{
			if (this.visible)
			{
				bindTexture(texture);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				this.hovered = xPos >= this.xPosition && yPos >= this.yPosition && xPos < this.xPosition + this.width && yPos < this.yPosition + this.height;
				int k = this.getHoverState(this.hovered) - 1;
				this.drawTexturedModalRect(this.xPosition, this.yPosition, u + 30 * k, v, this.width, this.height);
				//this.drawTexturedModalRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + k * 20, this.width / 2, this.height);
				this.mouseDragged(par1Minecraft, xPos, yPos);

				//this.drawCenteredString(fontrenderer,  barrel.mode==0?TFC_Core.translate("gui.Barrel.ToggleOn"):TFC_Core.translate("gui.Barrel.ToggleOff"), this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, l);
			}
		}
	}
}
