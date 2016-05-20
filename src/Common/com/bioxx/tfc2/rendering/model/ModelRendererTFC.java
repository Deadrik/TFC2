package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.TextureOffset;

public class ModelRendererTFC extends ModelRenderer 
{
	ModelBase baseModel;
	public ModelRendererTFC (ModelBase model, int texOffX, int texOffY) 
	{
		this(model);
		this.setTextureOffset(texOffX, texOffY);
	}

	public ModelRendererTFC (ModelBase model, String boxNameIn)
	{
		super(model, boxNameIn);
		baseModel = model;
	}

	public ModelRendererTFC(ModelBase model)
	{
		this(model, (String)null);
	}

	public ModelRendererTFC addBox(String partName, float offX, float offY, float offZ, float width, float height, float depth)
	{
		partName = this.boxName + "." + partName;
		TextureOffset textureoffset = this.baseModel.getTextureOffset(partName);
		this.setTextureOffset(textureoffset.textureOffsetX, textureoffset.textureOffsetY);
		this.cubeList.add((new ModelBoxTFC(this, textureoffset.textureOffsetX, textureoffset.textureOffsetY, offX, offY, offZ, width, height, depth, 0.0F, false)).setBoxName(partName));
		return this;
	}
}
