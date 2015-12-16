package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityElephant;
import com.bioxx.tfc2.entity.EntityElephant.ElephantType;

public class RenderElephant extends RenderLiving
{
	ResourceLocation eletex = new ResourceLocation(Reference.ModID+":"+"textures/mob/elephant.png");

	public RenderElephant(RenderManager renderManager) 
	{
		super(renderManager, new ModelElephant(), 0.7f);
		this.shadowSize = 0.8f;
	}

	protected ResourceLocation getEntityTexture(EntityElephant entity) 
	{
		if(entity.getElephantType() == ElephantType.Elephant)
			return eletex;
		else
			return eletex;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return getEntityTexture((EntityElephant)entity);
	}
}
