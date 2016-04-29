package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityBigCat;
import com.bioxx.tfc2.entity.EntityBigCat.BigCatType;

public class RenderBigCat extends RenderLiving<EntityBigCat>
{
	static ResourceLocation texLeopard = new ResourceLocation(Reference.ModID+":"+"textures/mob/leopard.png");
	static ResourceLocation texPanther = new ResourceLocation(Reference.ModID+":"+"textures/mob/panther.png");
	static ResourceLocation texMountainLion = new ResourceLocation(Reference.ModID+":"+"textures/mob/mountain_lion.png");

	public RenderBigCat(RenderManager renderManager) 
	{
		super(renderManager, new ModelBigCat(), 1.0f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBigCat entity) 
	{
		if(entity.getCatType() == BigCatType.Leopard)
			return texLeopard;
		else if(entity.getCatType() == BigCatType.MountainLion)
			return texMountainLion;
		return texPanther;
	}
}
