package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.VertexBuffer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelBoxTFC extends ModelBox 
{

	private TexturedQuad[] quadList;

	public ModelBoxTFC(ModelRenderer renderer, int textureX,int textureY, float xPos, float yPos,
			float zPos, float width, float height, float depth, float p_i46359_10_, boolean mirror) 
	{
		super(renderer, textureX, textureY, xPos,yPos,zPos, (int)width, (int)height, (int)depth, p_i46359_10_, mirror);

		this.quadList = new TexturedQuad[6];
		float f = xPos + (float)width;
		float f1 = yPos + (float)height;
		float f2 = zPos + (float)depth;
		xPos = xPos - p_i46359_10_;
		yPos = yPos - p_i46359_10_;
		zPos = zPos - p_i46359_10_;
		f = f + p_i46359_10_;
		f1 = f1 + p_i46359_10_;
		f2 = f2 + p_i46359_10_;

		if (mirror)
		{
			float f3 = f;
			f = xPos;
			xPos = f3;
		}

		PositionTextureVertex positiontexturevertex7 = new PositionTextureVertex(xPos, yPos, zPos, 0.0F, 0.0F);
		PositionTextureVertex positiontexturevertex = new PositionTextureVertex(f, yPos, zPos, 0.0F, 8.0F);
		PositionTextureVertex positiontexturevertex1 = new PositionTextureVertex(f, f1, zPos, 8.0F, 8.0F);
		PositionTextureVertex positiontexturevertex2 = new PositionTextureVertex(xPos, f1, zPos, 8.0F, 0.0F);
		PositionTextureVertex positiontexturevertex3 = new PositionTextureVertex(xPos, yPos, f2, 0.0F, 0.0F);
		PositionTextureVertex positiontexturevertex4 = new PositionTextureVertex(f, yPos, f2, 0.0F, 8.0F);
		PositionTextureVertex positiontexturevertex5 = new PositionTextureVertex(f, f1, f2, 8.0F, 8.0F);
		PositionTextureVertex positiontexturevertex6 = new PositionTextureVertex(xPos, f1, f2, 8.0F, 0.0F);

		this.quadList[0] = new TexturedQuadTFC(new PositionTextureVertex[] {positiontexturevertex4, positiontexturevertex, positiontexturevertex1, positiontexturevertex5}, textureX + depth + width, textureY + depth, textureX + depth + width + depth, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);//Left side
		if(height == 0 || depth == 0)
			quadList[0] = null;
		this.quadList[1] = new TexturedQuadTFC(new PositionTextureVertex[] {positiontexturevertex7, positiontexturevertex3, positiontexturevertex6, positiontexturevertex2}, textureX, textureY + depth, textureX + depth, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);//Right Side
		if(height == 0 || depth == 0)
			quadList[1] = null;
		this.quadList[2] = new TexturedQuadTFC(new PositionTextureVertex[] {positiontexturevertex4, positiontexturevertex3, positiontexturevertex7, positiontexturevertex}, textureX + depth, textureY, textureX + depth + width, textureY + depth, renderer.textureWidth, renderer.textureHeight);//Top
		if(width == 0 || depth == 0)
			quadList[2] = null;
		this.quadList[3] = new TexturedQuadTFC(new PositionTextureVertex[] {positiontexturevertex1, positiontexturevertex2, positiontexturevertex6, positiontexturevertex5}, textureX + depth + width, textureY + depth, textureX + depth + width + width, textureY, renderer.textureWidth, renderer.textureHeight);//bottom
		if(width == 0 || depth == 0)
			quadList[3] = null;
		this.quadList[4] = new TexturedQuadTFC(new PositionTextureVertex[] {positiontexturevertex, positiontexturevertex7, positiontexturevertex2, positiontexturevertex1}, textureX + depth, textureY + depth, textureX + depth + width, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);//front
		if(height == 0 || width == 0)
			quadList[4] = null;
		this.quadList[5] = new TexturedQuadTFC(new PositionTextureVertex[] {positiontexturevertex3, positiontexturevertex4, positiontexturevertex5, positiontexturevertex6}, textureX + depth + width + depth, textureY + depth, textureX + depth + width + depth + width, textureY + depth + height, renderer.textureWidth, renderer.textureHeight);//back
		if(height == 0 || width == 0)
			quadList[5] = null;
		if (mirror)
		{
			for (int i = 0; i < this.quadList.length; ++i)
			{
				this.quadList[i].flipFace();
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(VertexBuffer renderer, float scale)
	{
		for (int i = 0; i < this.quadList.length; ++i)
		{
			if(this.quadList[i] != null)
				this.quadList[i].draw(renderer, scale);
		}
	}

	public static class TexturedQuadTFC extends TexturedQuad
	{

		public TexturedQuadTFC(PositionTextureVertex[] vertices, float texcoordU1, float texcoordV1, float texcoordU2, float texcoordV2, 
				float textureWidth, float textureHeight) 
		{
			super(vertices);
			float f = 0.0F / textureWidth;
			float f1 = 0.0F / textureHeight;
			vertices[0] = vertices[0].setTexturePosition((float)texcoordU2 / textureWidth - f, (float)texcoordV1 / textureHeight + f1);
			vertices[1] = vertices[1].setTexturePosition((float)texcoordU1 / textureWidth + f, (float)texcoordV1 / textureHeight + f1);
			vertices[2] = vertices[2].setTexturePosition((float)texcoordU1 / textureWidth + f, (float)texcoordV2 / textureHeight - f1);
			vertices[3] = vertices[3].setTexturePosition((float)texcoordU2 / textureWidth - f, (float)texcoordV2 / textureHeight - f1);
		}

	}

}
