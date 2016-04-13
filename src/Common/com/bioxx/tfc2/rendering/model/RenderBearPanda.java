package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityBearPanda;

public class RenderBearPanda extends RenderLiving<EntityBearPanda>
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/bear_panda.png");

	public RenderBearPanda(RenderManager renderManager) 
	{
		super(renderManager, new ModelBearPanda(), 0.7f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBearPanda entity) 
	{
		return tex;
	}
}
