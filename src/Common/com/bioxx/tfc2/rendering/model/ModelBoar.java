package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBoar extends ModelBase
{
	ModelRenderer LegFrontLeftLower;
	ModelRenderer LegRearRightLower;
	ModelRenderer LegFrontRightLower;
	ModelRenderer LegRearLeftLower;
	ModelRenderer HEAD;
	ModelRenderer BODY;

	public ModelBoar()
	{
		textureWidth = 64;
		textureHeight = 32;
		setTextureOffset("HEAD.Head", 0, 0);
		setTextureOffset("HEAD.Snout", 1, 14);
		setTextureOffset("HEAD.TuskLeft", 0, 22);
		setTextureOffset("HEAD.TuskRight", 0, 22);
		setTextureOffset("BODY.Torso", 16, 6);
		setTextureOffset("BODY.Tail", 52, 0);

		LegFrontLeftLower = new ModelRenderer(this, 56, 0);
		LegFrontLeftLower.addBox(-1F, 0F, -1F, 2, 7, 2);
		LegFrontLeftLower.setRotationPoint(4F, 17F, 4F);
		LegFrontLeftLower.setTextureSize(64, 32);
		LegFrontLeftLower.mirror = true;
		setRotation(LegFrontLeftLower, 0F, 0F, 0F);
		LegRearRightLower = new ModelRenderer(this, 56, 0);
		LegRearRightLower.addBox(-1F, 0F, -1F, 2, 7, 2);
		LegRearRightLower.setRotationPoint(-2F, 17F, 14F);
		LegRearRightLower.setTextureSize(64, 32);
		LegRearRightLower.mirror = true;
		setRotation(LegRearRightLower, 0F, 0F, 0F);
		LegFrontRightLower = new ModelRenderer(this, 56, 0);
		LegFrontRightLower.addBox(-1F, 0F, -1F, 2, 7, 2);
		LegFrontRightLower.setRotationPoint(-2F, 17F, 4F);
		LegFrontRightLower.setTextureSize(64, 32);
		LegFrontRightLower.mirror = true;
		setRotation(LegFrontRightLower, 0F, 0F, 0F);
		LegRearLeftLower = new ModelRenderer(this, 56, 0);
		LegRearLeftLower.addBox(-1F, 0F, -1F, 2, 7, 2);
		LegRearLeftLower.setRotationPoint(4F, 17F, 14F);
		LegRearLeftLower.setTextureSize(64, 32);
		LegRearLeftLower.mirror = true;
		setRotation(LegRearLeftLower, 0F, 0F, 0F);
		HEAD = new ModelRenderer(this, "HEAD");
		HEAD.setRotationPoint(0F, 14F, -1F);
		setRotation(HEAD, 0F, 0F, 0F);
		HEAD.mirror = true;
		HEAD.addBox("Head", -2F, -6F, -3F, 6, 8, 5);
		HEAD.addBox("Snout", -1F, -3F, -7F, 4, 4, 4);
		HEAD.addBox("TuskLeft", 3F, -1F, -6F, 1, 2, 1);
		HEAD.addBox("TuskRight", -2F, -1F, -6F, 1, 2, 1);
		BODY = new ModelRenderer(this, "BODY");
		BODY.setRotationPoint(0F, 13F, 13F);
		setRotation(BODY, 0F, 0F, 0F);
		BODY.mirror = true;
		BODY.addBox("Torso", -3F, -6F, -12F, 8, 10, 16);
		BODY.addBox("Tail", 0.5F, -5F, 4F, 1, 5, 1);
	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	@Override
	public void render(Entity entityIn, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float scale)
	{
		this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, scale, entityIn);

		if (this.isChild)
		{
			float f6 = 2.0F;
			GlStateManager.pushMatrix();
			HEAD.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			LegFrontLeftLower.render(scale);
			LegRearRightLower.render(scale);
			LegFrontRightLower.render(scale);
			LegRearLeftLower.render(scale);
			BODY.render(scale);
			GlStateManager.popMatrix();
		}
		else
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, -0.5F);
			LegFrontLeftLower.render(scale);
			LegRearRightLower.render(scale);
			LegFrontRightLower.render(scale);
			LegRearLeftLower.render(scale);
			HEAD.render(scale);
			BODY.render(scale);
			GlStateManager.popMatrix();
		}
	}

	/**
	 * Sets the model's various rotation angles. For bipeds, par1 and par2 are used for animating the movement of arms
	 * and legs, where par1 represents the time(so that arms and legs swing back and forth) and par2 represents how
	 * "far" arms and legs can swing at most.
	 */
	@Override
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_)
	{
		float f6 = (180F / (float)Math.PI);
		float rotationDiv = 2;
		this.HEAD.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.HEAD.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		this.BODY.rotateAngleX = 0;//((float)Math.PI / 2F);
		this.LegFrontLeftLower.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRearLeftLower.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegFrontRightLower.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRearRightLower.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
	}

}

