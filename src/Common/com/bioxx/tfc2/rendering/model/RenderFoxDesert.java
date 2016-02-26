package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;

public class RenderFoxDesert extends RenderLiving
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/fox_desert.png");

	public RenderFoxDesert() 
	{
		super(Minecraft.getMinecraft().getRenderManager(), new ModelFoxDesert(), 0.3f);
		this.shadowSize = 0.3f;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return tex;
	}
}
