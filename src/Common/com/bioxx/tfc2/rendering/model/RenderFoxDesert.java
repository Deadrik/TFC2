package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityFoxDesert;

public class RenderFoxDesert extends RenderLiving<EntityFoxDesert>
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/fox_desert.png");

	public RenderFoxDesert(RenderManager manager) 
	{
		super(manager, new ModelFoxDesert(), 0.3f);
		this.shadowSize = 0.3f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityFoxDesert entity) 
	{
		return tex;
	}
}
