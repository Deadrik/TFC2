package com.bioxx.tfc2.gui;

import java.awt.Rectangle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;

public class GuiInventoryButton extends GuiButton 
{
	private static ResourceLocation texture = new ResourceLocation(Reference.ModID+":textures/gui/inventory.png");

	public Rectangle iconRect;
	public Rectangle collideRect;
	public Rectangle buttonRect;

	public GuiInventoryButton(int index, Rectangle collideRect, Rectangle buttonRect, String s, Rectangle iconRect)
	{
		super(index, collideRect.x, collideRect.y, collideRect.width, collideRect.height, s);
		this.buttonRect = buttonRect;
		this.collideRect = collideRect;
		this.iconRect = iconRect;
	}


	@Override
	public void drawButton(Minecraft mc, int x, int y)
	{
		if (this.visible)
		{
			Core.bindTexture(texture);
			this.drawTexturedModalRect(this.xPosition, this.yPosition, buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height);

			this.hovered = x >= this.xPosition && y >= this.yPosition && x < this.xPosition + this.width && y < this.yPosition + this.height;

			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			if(iconRect != null) 
			{
				this.drawIcon(this.xPosition+4, this.yPosition+2, iconRect.x, iconRect.y, iconRect.width, iconRect.height);
			}

			this.mouseDragged(mc, x, y);

			if(hovered)
			{
				drawTooltip(x, y, this.displayString);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		}
	}

	public void drawIcon(int x, int y, int textureX, int textureY, int width, int height)
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos((double)(x + 0), (double)(y + 16), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double)(x + 16), (double)(y + 16), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
		vertexbuffer.pos((double)(x + 16), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + 0) * f1)).endVertex();
		vertexbuffer.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(textureX + 0) * f), (double)((float)(textureY + 0) * f1)).endVertex();
		tessellator.draw();
	}

	/*private boolean isPointInRegion(int mouseX, int mouseY)
	{
		int k1 = 0;//screen.getGuiLeft();
		int l1 = 0;//screen.getGuiTop();
		mouseX -= k1;
		mouseY -= l1;
		return mouseX >= xPosition - 1 && mouseX < xPosition + width + 1 && mouseY >= yPosition - 1 && mouseY < yPosition + height + 1;
	}*/

	public void drawTooltip(int mx, int my, String text) {
		/*List list = new ArrayList();
		list.add(text);
		FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
		screen.drawHoveringText(list, mx, my+15, fontrenderer);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);*/
	}
}
