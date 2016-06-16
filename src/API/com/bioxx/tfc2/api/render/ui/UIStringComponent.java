package com.bioxx.tfc2.api.render.ui;

import java.util.ArrayList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.Vec3d;

public class UIStringComponent extends UIComponent
{
	float scale = 0.01f;
	int color = 0;
	String UIText = "";
	ArrayList<UICharComponent> charCompList = new ArrayList<UICharComponent>();

	public UIStringComponent(String text, double x, double y, double z, float scale, int zLevel, int color) 
	{
		super(DefaultVertexFormats.POSITION_TEX);
		this.scale = scale;
		UIText = text;
		float toNextChar = 0;
		this.color = color;
		for (int i = 0; i < UIText.length(); ++i)
		{
			char c = UIText.charAt(i);
			UICharComponent ui = new UICharComponent(DefaultVertexFormats.POSITION_TEX, x+toNextChar, y, z, zLevel, this.scale);
			toNextChar += ui.setupChar(c)*this.scale;
			charCompList.add(ui);
		}
	}


	@Override
	public void draw(Tessellator tess, VertexBuffer buffer)
	{
		GlStateManager.color(color & 0xff0000, color & 0x00ff00, color & 0x0000ff);
		for(UIComponent c : charCompList)
		{
			c.draw(tess, buffer);
		}
	}

	@Override
	public void rotate(Vec3d origin, Vec3d axis, double rotation)
	{
		for(UIComponent c : charCompList)
		{
			c.rotate(origin, axis, rotation);
		}
	}

	@Override
	public void translate(Vec3d trans)
	{
		for(UIComponent c : charCompList)
		{
			c.translate(trans);
		}
	}
}
