package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntitySabertooth;

public class RenderSabertooth extends RenderLiving<EntitySabertooth>
{
	static ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/sabertooth.png");

	public RenderSabertooth(RenderManager renderManager) 
	{
		super(renderManager, new ModelSabertooth(), 1.0f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntitySabertooth entity) 
	{
		return tex;
	}
}
