package com.bioxx.tfc2.api.render.ui;

import java.awt.Color;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.Vec3d;

public class VertexColor extends Vertex
{
	Color color;
	public VertexColor(Vec3d p, Color c) 
	{
		super(p);
		color = c;
	}

	@Override
	public void addVertex(VertexBuffer buffer) 
	{
		buffer.pos(pos.xCoord, pos.yCoord, pos.zCoord).color(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, color.getAlpha()/255f).endVertex();
	}

}
