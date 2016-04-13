package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityMammoth;

public class RenderMammoth extends RenderLiving<EntityMammoth>
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/mammoth.png");

	public RenderMammoth(RenderManager renderManager) 
	{
		super(renderManager, new ModelMammoth(), 0.7f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMammoth entity) 
	{
		return tex;
	}
}
