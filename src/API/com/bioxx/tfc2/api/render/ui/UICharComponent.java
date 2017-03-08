package com.bioxx.tfc2.api.render.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

public class UICharComponent extends UIComponent 
{
	private static final ResourceLocation[] UNICODE_PAGE_LOCATIONS = new ResourceLocation[256];
	protected byte[] glyphWidth = new byte[65536];
	private double xPos, yPos, zPos;
	private float scale;
	public char renderChar;

	public UICharComponent(VertexFormat f, double x, double y, double z, int zLevel, float scale) 
	{
		super(f, zLevel);
		this.scale = scale;
		xPos = x;
		yPos = y;
		zPos = z;
	}

	private ResourceLocation getUnicodePageLocation(int page)
	{
		if (UNICODE_PAGE_LOCATIONS[page] == null)
		{
			UNICODE_PAGE_LOCATIONS[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", new Object[] {Integer.valueOf(page)}));
		}

		return UNICODE_PAGE_LOCATIONS[page];
	}

	private void loadGlyphTexture(int page)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.getUnicodePageLocation(page));
	}

	@Override
	public void setupGL()
	{
		GlStateManager.enableTexture2D();
		//Minecraft.getMinecraft().fontRendererObj.getCharWidth(renderChar);
		this.loadGlyphTexture(renderChar / 256);
	}

	public float setupChar(char ch)
	{
		int i = Minecraft.getMinecraft().fontRenderer.getCharWidth(ch) & 255;
		renderChar = ch;
		if (i == 0)
		{
			return 0.0F;
		}
		else
		{
			int k = i >>> 4;
			int l = i & 15;
			float f = (float)k;
			float f1 = (float)(l + 1);
			float f2 = (float)(ch % 16 * 16) + f;
			float f3 = (float)((ch & 255) / 16 * 16);
			float f4 = f1 - f - 0.02F;
			if(ch != ' ')
			{
				this.addVertex(new VertexTex(new Vec3d(this.xPos + (f4 / 2.0F)*scale, this.yPos, zPos+zLevel*0.001), (f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F));
				this.addVertex(new VertexTex(new Vec3d(this.xPos + (f4 / 2.0F)*scale, this.yPos + (7.99F)*scale, zPos+zLevel*0.001), (f2 + f4) / 256.0F, (f3) / 256.0F));
				this.addVertex(new VertexTex(new Vec3d(this.xPos, this.yPos, zPos+zLevel*0.001), f2 / 256.0F, (f3 + 15.98F) / 256.0F));
				this.addVertex(new VertexTex(new Vec3d(this.xPos, this.yPos + (7.99F)*scale, zPos+zLevel*0.001), f2 / 256.0F, (f3) / 256.0F));
			}

			//xPos += ((f1 - f) / 2.0F + 1.0F)*scale;
			return (f1 - f) / 2.0F + 1.0F;
		}
	}

	protected float renderUnicodeChar(char ch, boolean italic)
	{
		int i = this.glyphWidth[ch] & 255;

		if (i == 0)
		{
			return 0.0F;
		}
		else
		{
			int j = ch / 256;
			this.loadGlyphTexture(j);
			int k = i >>> 4;
			int l = i & 15;
			float f = (float)k;
			float f1 = (float)(l + 1);
			float f2 = (float)(ch % 16 * 16) + f;
			float f3 = (float)((ch & 255) / 16 * 16);
			float f4 = f1 - f - 0.02F;
			float f5 = italic ? 1.0F : 0.0F;

			Tessellator tess = Tessellator.getInstance();
			VertexBuffer buffer = tess.getBuffer();
			GlStateManager.enableTexture2D();
			buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);
			buffer.pos(this.xPos + (f4 / 2.0F + f5)*scale, this.yPos, zPos+zLevel).tex((f2 + f4) / 256.0F, (f3 + 15.98F) / 256.0F).endVertex();
			buffer.pos(this.xPos + (f4 / 2.0F - f5)*scale, this.yPos + (7.99F)*scale, zPos+zLevel).tex((f2 + f4) / 256.0F, (f3) / 256.0F).endVertex();
			buffer.pos(this.xPos + (f5)*scale, this.yPos, zPos+zLevel).tex(f2 / 256.0F, (f3 + 15.98F) / 256.0F).endVertex();
			buffer.pos(this.xPos - (f5)*scale, this.yPos + (7.99F)*scale, zPos+zLevel).tex(f2 / 256.0F, (f3) / 256.0F).endVertex();
			tess.draw();
			xPos += ((f1 - f) / 2.0F + 1.0F)*scale;
			return (f1 - f) / 2.0F + 1.0F;
		}
	}
}
