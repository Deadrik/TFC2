package com.bioxx.tfc2.api.render.ui;

import java.util.ArrayList;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.math.Vec3d;

import org.lwjgl.opengl.GL11;

public class UIComponent
{
	public ArrayList<Vertex> vertexList;
	public VertexFormat format;
	public int GLMode = GL11.GL_TRIANGLE_STRIP;
	public int zLevel = 0;

	public UIComponent(VertexFormat f)
	{
		vertexList = new ArrayList<Vertex>();
		format = f;
	}

	public UIComponent(VertexFormat f, int z)
	{
		vertexList = new ArrayList<Vertex>();
		format = f;
		zLevel = z;
	}

	public void setupGL()
	{

	}

	public void draw(Tessellator tess, VertexBuffer buffer)
	{
		setupGL();
		buffer.begin(GLMode, format);
		for(Vertex v : vertexList)
		{
			v.addVertex(buffer);
		}
		tess.draw();
	}

	public void addVertex(Vertex v)
	{
		v.pos = v.pos.addVector(0, 0, 0.0001*zLevel);
		vertexList.add(v);
	}

	public void rotate(Vec3d origin, Vec3d axis, double rotation)
	{
		for(Vertex v : vertexList)
		{
			v.rotate(origin, axis, rotation);
		}
	}

	public void translate(Vec3d trans)
	{
		for(Vertex v : vertexList)
		{
			v.translate(trans);
		}
	}
}
