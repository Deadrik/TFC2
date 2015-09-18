package com.bioxx.tfc2.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class GuiKnappingButton extends GuiButton 
{
	public GuiKnappingButton(int index, int xPos, int yPos, int width, int height)
	{
		super(index, xPos, yPos, width, height, "");
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int xPos, int yPos)
	{
		if (this.visible)
		{
			this.hovered = xPos >= this.xPosition && yPos >= this.yPosition && xPos < this.xPosition + this.width && yPos < this.yPosition + this.height;
			PlayerInfo pi = PlayerManagerTFC.getInstance().getClientPlayer();
			GL11.glScalef(0.5f, 0.5f, 1f);
			GL11.glTranslatef(xPosition, yPosition, 0);
			if (pi.specialCraftingType != null)
				Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(pi.specialCraftingType, xPosition, yPosition);
			if (!this.enabled && pi.specialCraftingTypeAlternate != null)
				Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(pi.specialCraftingTypeAlternate, xPosition, yPosition);
			GL11.glTranslatef(-xPosition, -yPosition, 0);
			GL11.glScalef(2f, 2f, 1);


			this.mouseDragged(par1Minecraft, this.xPosition, this.yPosition);
		}
	}
}
