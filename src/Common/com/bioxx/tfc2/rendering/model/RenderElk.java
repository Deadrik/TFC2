package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityElk;

public class RenderElk extends RenderLiving<EntityElk>
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/elk.png");

	public RenderElk(RenderManager renderManager) 
	{
		super(renderManager, new ModelElk(), 0.7f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityElk entity) 
	{
		return tex;
	}
}
