package com.bioxx.tfc2.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class GuiKnappingRecipeButton extends GuiButton 
{
	ItemStack is;
	public int recipeIndex = 0;

	public GuiKnappingRecipeButton(int index, int xPos, int yPos, int width, int height, ItemStack i)
	{
		super(index, xPos, yPos, width, height, "");
		is = i;
	}

	@Override
	public void drawButton(Minecraft par1Minecraft, int xPos, int yPos)
	{
		if (this.visible)
		{
			this.hovered = xPos >= this.xPosition && yPos >= this.yPosition && xPos < this.xPosition + this.width && yPos < this.yPosition + this.height;
			PlayerInfo pi = PlayerManagerTFC.getInstance().getClientPlayer();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			Core.bindTexture(GuiKnapping.texture);
			if(hovered && this.enabled)
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 203+18, 18, 18);
			else
			{
				if(!enabled)
					GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 203, 18, 18);
			}

			Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(is, xPosition+1, yPosition+1);

			this.mouseDragged(par1Minecraft, this.xPosition, this.yPosition);

			if(hovered)
			{
				((GuiContainerTFC)Minecraft.getMinecraft().currentScreen).drawTooltip(xPos, yPos, is.getTooltip(Minecraft.getMinecraft().player, false));
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}
}
