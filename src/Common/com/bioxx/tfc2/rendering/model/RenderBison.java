package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityBison;

public class RenderBison extends RenderLiving<EntityBison>
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/bison.png");

	public RenderBison(RenderManager renderManager) 
	{
		super(renderManager, new ModelBison(), 0.7f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBison entity) 
	{
		return tex;
	}
}
