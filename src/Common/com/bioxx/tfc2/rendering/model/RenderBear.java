package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityBear;
import com.bioxx.tfc2.entity.EntityBear.BearType;

public class RenderBear extends RenderLiving<EntityBear>
{
	static ResourceLocation texBrown = new ResourceLocation(Reference.ModID+":"+"textures/mob/bear_brown.png");
	static ResourceLocation texPolar = new ResourceLocation(Reference.ModID+":"+"textures/mob/bear_polar.png");

	public RenderBear(RenderManager renderManager) 
	{
		super(renderManager, new ModelBear(), 1.1f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBear entity) 
	{
		if(entity.getBearType() == BearType.Brown)
			return texBrown;
		else if(entity.getBearType() == BearType.Black)
			return texBrown;
		else
			return texPolar;
	}
}
