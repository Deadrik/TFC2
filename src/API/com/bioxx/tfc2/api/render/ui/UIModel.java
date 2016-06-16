package com.bioxx.tfc2.api.render.ui;

import java.util.ArrayList;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.Vec3d;

public class UIModel
{
	public ArrayList<UIComponent> componentList;

	public UIModel() 
	{
		componentList = new ArrayList<UIComponent>();
	}

	public void setupGL()
	{
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableCull();
		GlStateManager.disableLighting();
	}

	public void draw(Tessellator tess, VertexBuffer buffer)
	{
		setupGL();
		for(UIComponent c : componentList)
		{
			c.draw(tess, buffer);
		}
	}

	public void rotate(Vec3d origin, Vec3d axis, double rotation)
	{
		for(UIComponent c : componentList)
		{
			c.rotate(origin, axis, rotation);
		}
	}

	public void addComponent(UIComponent c)
	{
		componentList.add(c);
	}

	public void translate(Vec3d trans)
	{
		for(UIComponent c : componentList)
		{
			c.translate(trans);
		}
	}
}
