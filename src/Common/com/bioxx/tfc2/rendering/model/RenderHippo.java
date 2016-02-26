package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;

public class RenderHippo extends RenderLiving
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/hippo.png");

	public RenderHippo()
	{
		super(Minecraft.getMinecraft().getRenderManager(), new ModelHippo(), 0.7f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return tex;
	}
}
