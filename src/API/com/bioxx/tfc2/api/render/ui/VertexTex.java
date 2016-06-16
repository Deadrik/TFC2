package com.bioxx.tfc2.api.render.ui;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.Vec3d;

public class VertexTex extends Vertex
{
	public double u, v;
	public VertexTex(Vec3d p, double u, double v) 
	{
		super(p);
		this.u = u;
		this.v = v;
	}

	@Override
	public void addVertex(VertexBuffer buffer) 
	{
		buffer.pos(pos.xCoord, pos.yCoord, pos.zCoord).tex(u, v).endVertex();
	}

}
