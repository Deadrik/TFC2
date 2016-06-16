package com.bioxx.tfc2.api.render.ui;

import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.util.math.Vec3d;

import com.bioxx.tfc2.api.util.Helper;

public abstract class Vertex
{
	public Vec3d pos;
	public Vertex(Vec3d p)
	{
		pos = p;
	}

	public abstract void addVertex(VertexBuffer buffer);

	public void rotate(Vec3d origin, Vec3d axis, double rotation)
	{
		pos = Helper.rotateVertex(origin, pos, axis, rotation);
	}

	public void translate(Vec3d trans)
	{
		pos = pos.add(trans);
	}

}