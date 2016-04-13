package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityBoar;
import com.bioxx.tfc2.entity.EntityBoar.BoarStage;

public class RenderBoar extends RenderLiving<EntityBoar>
{
	ResourceLocation tex_brown = new ResourceLocation(Reference.ModID+":"+"textures/mob/boar_brown.png");
	ResourceLocation tex_black = new ResourceLocation(Reference.ModID+":"+"textures/mob/boar_black.png");
	ResourceLocation tex_pink1 = new ResourceLocation(Reference.ModID+":"+"textures/mob/boar_pink1.png");
	ResourceLocation tex_pink2 = new ResourceLocation(Reference.ModID+":"+"textures/mob/boar_pink2.png");
	ResourceLocation tex_pink3 = new ResourceLocation(Reference.ModID+":"+"textures/mob/boar_pink3.png");

	public RenderBoar(RenderManager renderManager) 
	{
		super(renderManager, new ModelBoar(), 0.7f);
		this.shadowSize = 0.8f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityBoar entity) 
	{
		if(entity.getBoarStage() == BoarStage.Black)
			return tex_black;
		else if(entity.getBoarStage() == BoarStage.Pink1)
			return tex_pink1;
		else if(entity.getBoarStage() == BoarStage.Pink2)
			return tex_pink2;
		else if(entity.getBoarStage() == BoarStage.Pink3)
			return tex_pink3;
		else
			return tex_brown;
	}
}
