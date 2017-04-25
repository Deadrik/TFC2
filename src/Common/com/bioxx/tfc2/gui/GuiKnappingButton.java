package com.bioxx.tfc2.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFCItems;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.core.PlayerInfo;
import com.bioxx.tfc2.core.PlayerManagerTFC;
import com.bioxx.tfc2.core.TFC_Sounds;

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
				else if(pi.specialCraftingType.getItem() == Items.CLAY_BALL)
				{
					Core.bindTexture(new ResourceLocation(Reference.ModID, "textures/items/pottery/clay flat light.png"));

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
				else if(pi.specialCraftingType.getItem() == Items.CLAY_BALL)
				{
					Core.bindTexture(new ResourceLocation(Reference.ModID, "textures/items/pottery/clay flat dark.png"));
					GL11.glColor4f(1f, 1f, 1f, 1.0f);
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

				GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.4f);
				GlStateManager.enableBlend();
				//GlStateManager.colorMask(false, true, true, true);
				this.drawTexturedModalRect(this.xPosition, this.yPosition, 0, 239, 16, 16);
				//GlStateManager.colorMask(true, true, true, true);
				GlStateManager.disableBlend();
			}

			GL11.glTranslatef(-xPosition, -yPosition, 0);
			GL11.glScalef(2f, 2f, 1);
			GL11.glColor4f(1f, 1f, 1f, 1f);


			this.mouseDragged(par1Minecraft, this.xPosition, this.yPosition);
		}
	}

	protected void drawLocal()
	{
		float f = 0.00390625F;
		float f1 = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexBuffer.pos(xPosition + 0, yPosition + 16, this.zLevel).tex(0, 1).endVertex();
		vertexBuffer.pos(xPosition + 16, yPosition + 16, this.zLevel).tex(1, 1).endVertex();
		vertexBuffer.pos(xPosition + 16, yPosition + 0, this.zLevel).tex(1, 0).endVertex();
		vertexBuffer.pos(xPosition + 0, yPosition + 0, this.zLevel).tex(0, 0).endVertex();
		tessellator.draw();
	}

	@Override
	public void playPressSound(SoundHandler paramSoundHandler)
	{
		PlayerInfo pi = PlayerManagerTFC.getInstance().getClientPlayer();
		if(pi.specialCraftingType.getItem() == TFCItems.LooseRock)
			paramSoundHandler.playSound(net.minecraft.client.audio.PositionedSoundRecord.getMasterRecord(TFC_Sounds.KNAPPING, 1.0F));
	}

	public void highlight(boolean b)
	{
		highlight = b;
	}

}
