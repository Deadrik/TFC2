package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;

public class RenderBison extends RenderLiving
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/bison.png");

	public RenderBison(RenderManager renderManager) 
	{
		super(renderManager, new ModelBison(), 0.7f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return tex;
	}
}
