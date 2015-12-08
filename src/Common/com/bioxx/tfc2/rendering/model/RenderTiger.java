package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityTiger;
import com.bioxx.tfc2.entity.EntityTiger.TigerType;

public class RenderTiger extends RenderLiving
{
	static ResourceLocation texTiger = new ResourceLocation(Reference.ModID+":"+"textures/mob/tiger.png");
	static ResourceLocation texTigerSnow = new ResourceLocation(Reference.ModID+":"+"textures/mob/tiger_snow.png");

	public RenderTiger(RenderManager renderManager) 
	{
		super(renderManager, new ModelTiger(), 1.0f);
		this.shadowSize = 0.8f;
	}

	protected ResourceLocation getEntityTexture(EntityTiger entity) 
	{
		if(entity.getTigerType() == TigerType.Snow)
			return texTigerSnow;
		return texTiger;
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return getEntityTexture((EntityTiger)entity);
	}
}
