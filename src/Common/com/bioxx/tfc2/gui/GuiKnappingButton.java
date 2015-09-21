package com.bioxx.tfc2.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;

public class GuiKnappingButton extends GuiButton 
{
	boolean highlight = true;
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
			{
				if(pi.specialCraftingType.getItem() == TFCItems.LooseRock)
				{
					Core.bindTexture(new ResourceLocation(Reference.ModID, "textures/blocks/rocks/" + StoneType.getStoneTypeFromMeta(pi.specialCraftingType.getItemDamage()).name() +" Raw.png"));

					//Same as drawTexturedModalRect except we need to set the UV ourselves
					drawLocal();
				}
				else
				{
					Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(pi.specialCraftingType, xPosition, yPosition);
				}
			}

			if (!this.enabled && pi.specialCraftingTypeAlternate != null)
			{
				if(pi.specialCraftingType.getItem() == TFCItems.LooseRock)
				{
					Core.bindTexture(new ResourceLocation(Reference.ModID, "textures/blocks/rocks/" + StoneType.getStoneTypeFromMeta(pi.specialCraftingTypeAlternate.getItemDamage()).name() +" Raw.png"));
					GL11.glColor4f(1f, 1f, 1f, 0.5f);
					//Same as drawTexturedModalRect except we need to set the UV ourselves
					drawLocal();
				}
				else
				{
					Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(pi.specialCraftingTypeAlternate, xPosition, yPosition);
				}
			}

			if(pi.shouldDrawKnappingHighlight && highlight)
			{
				Core.bindTexture(GuiKnapping.texture);

				GL11.glColor4f(1f, 1f, 1f, 0.2f);
				GlStateManager.enableBlend();
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 239, 16, 16);
				GlStateManager.disableBlend();
			}

			GL11.glTranslatef(-xPosition, -yPosition, 0);
			GL11.glScalef(2f, 2f, 1);
			GL11.glColor4f(1f, 1f, 1f, 1f);


			this.mouseDragged(par1Minecraft, this.xPosition, this.yPosition);
		}
	}

	private void drawLocal()
	{
		Tessellator localTessellator = Tessellator.getInstance();
		WorldRenderer localWorldRenderer = localTessellator.getWorldRenderer();
		localWorldRenderer.startDrawingQuads();
		localWorldRenderer.addVertexWithUV(xPosition + 0, yPosition + 16, this.zLevel, 0, 1);
		localWorldRenderer.addVertexWithUV(xPosition + 16, yPosition + 16, this.zLevel, 1, 1);
		localWorldRenderer.addVertexWithUV(xPosition + 16, yPosition + 0, this.zLevel, 1, 0);
		localWorldRenderer.addVertexWithUV(xPosition + 0, yPosition + 0, this.zLevel, 0, 0);
		localTessellator.draw();
	}

	@Override
	public void playPressSound(SoundHandler paramSoundHandler)
	{
		paramSoundHandler.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
	}

	public void highlight(boolean b)
	{
		highlight = b;
	}

}
