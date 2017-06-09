package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

import com.bioxx.tfc2.api.types.Gender;
import com.bioxx.tfc2.entity.EntityTiger;

public class ModelTiger extends ModelBase
{
	ModelRenderer HEAD;
	ModelRenderer BODY;
	ModelRenderer LegLEFTREAR;
	ModelRenderer LegLEFTFRONT;
	ModelRenderer LegRIGHTFRONT;
	ModelRenderer LegRIGHTREAR;

	public ModelTiger()
	{
		textureWidth = 64;
		textureHeight = 32;
		setTextureOffset("HEAD.EarLeft", 0, 0);
		setTextureOffset("HEAD.EarRight", 0, 0);
		setTextureOffset("HEAD.LionMane", 6, 0);
		setTextureOffset("HEAD.Head", 0, 24);
		setTextureOffset("HEAD.Snout", 0, 11);
		setTextureOffset("HEAD.Jaw", 0, 15);
		setTextureOffset("BODY.Tail", 49, 0);
		setTextureOffset("BODY.Torso", 20, 9);
		setTextureOffset("Neck.Neck", 18, 16);
		setTextureOffset("LegLEFTREAR.LegRearLeftUpper", 53, 0);
		setTextureOffset("LegLEFTREAR.LegRearLeftLower", 54, 10);
		setTextureOffset("LegLEFTREAR.LegRearLeftPaw", 52, 18);
		setTextureOffset("LegLEFTFRONT.LegFrontLeftUpper", 53, 0);
		setTextureOffset("LegLEFTFRONT.LegFrontLeftLower", 54, 10);
		setTextureOffset("LegLEFTFRONT.LegFrontLeftPaw", 52, 18);
		setTextureOffset("LegRIGHTFRONT.LegFrontRightUpper", 53, 0);
		setTextureOffset("LegRIGHTFRONT.LegFrontRightLower", 54, 10);
		setTextureOffset("LegRIGHTFRONT.LegFrontRightPaw", 52, 18);
		setTextureOffset("LegRIGHTREAR.LegRearRightUpper", 53, 0);
		setTextureOffset("LegRIGHTREAR.LegRearRightLower", 54, 10);
		setTextureOffset("LegRIGHTREAR.LegRearRightPaw", 52, 18);

		HEAD = new ModelRenderer(this, "HEAD");
		HEAD.setRotationPoint(0F, 9F, 0F);
		setRotation(HEAD, 0F, 0F, 0F);
		HEAD.mirror = true;
		HEAD.addBox("EarLeft", 1.5F, -3F, -2F, 2, 2, 1);
		HEAD.addBox("EarRight", -3.5F, -3F, -2F, 2, 2, 1);
		HEAD.addBox("LionMane", -3.5F, -1F, -3F, 7, 5, 2);
		HEAD.addBox("Head", -2.5F, -2F, -4F, 5, 4, 4);
		HEAD.addBox("Snout", -1.5F, -0.5F, -6F, 3, 2, 2);
		HEAD.addBox("Jaw", -1F, 1.5F, -6F, 2, 1, 3);

		ModelRenderer Neck = new ModelRenderer(this, "Neck");
		Neck.setRotationPoint(0F, 0F, 0F);
		setRotation(Neck, -0.5235988F, 0F, 0F);
		Neck.mirror = true;
		Neck.addBox("Neck", -1.5F, 0.0F, -3F, 3, 3, 5);
		HEAD.addChild(Neck);

		BODY = new ModelRenderer(this, "BODY");
		BODY.setRotationPoint(0F, 12F, 14F);
		setRotation(BODY, 0F, 0F, 0F);
		BODY.mirror = true;
		BODY.addBox("Tail", -0.5F, -2F, 3F, 1, 12, 1);
		BODY.addBox("Torso", -3F, -3F, -13F, 6, 7, 16);

		LegLEFTREAR = new ModelRenderer(this, "LegLEFTREAR");
		LegLEFTREAR.setRotationPoint(2F, 11F, 15F);
		setRotation(LegLEFTREAR, 0F, 0F, 0F);
		LegLEFTREAR.mirror = true;
		LegLEFTREAR.addBox("LegRearLeftUpper", 0F, -1F, -1.5F, 2, 7, 3);
		LegLEFTREAR.addBox("LegRearLeftLower", 0F, 6F, -0.5F, 2, 6, 2);
		LegLEFTREAR.addBox("LegRearLeftPaw", -0.5F, 12F, -1.5F, 3, 1, 3);

		LegLEFTFRONT = new ModelRenderer(this, "LegLEFTFRONT");
		LegLEFTFRONT.setRotationPoint(2.5F, 11F, 3F);
		setRotation(LegLEFTFRONT, 0F, 0F, 0F);
		LegLEFTFRONT.mirror = true;
		LegLEFTFRONT.addBox("LegFrontLeftUpper", 0F, -1F, -1.5F, 2, 7, 3);
		LegLEFTFRONT.addBox("LegFrontLeftLower", 0F, 6F, -1F, 2, 6, 2);
		LegLEFTFRONT.addBox("LegFrontLeftPaw", -0.5F, 12F, -2F, 3, 1, 3);

		LegRIGHTFRONT = new ModelRenderer(this, "LegRIGHTFRONT");
		LegRIGHTFRONT.setRotationPoint(-2.5F, 11F, 3F);
		setRotation(LegRIGHTFRONT, 0F, 0F, 0F);
		LegRIGHTFRONT.addBox("LegFrontRightUpper", -2F, -1F, -1.5F, 2, 7, 3);
		LegRIGHTFRONT.addBox("LegFrontRightLower", -2F, 6F, -1F, 2, 6, 2);
		LegRIGHTFRONT.addBox("LegFrontRightPaw", -2.5F, 12F, -2F, 3, 1, 3);

		LegRIGHTREAR = new ModelRenderer(this, "LegRIGHTREAR");
		LegRIGHTREAR.setRotationPoint(-2F, 11F, 15F);
		setRotation(LegRIGHTREAR, 0F, 0F, 0F);
		LegRIGHTREAR.addBox("LegRearRightUpper", -2F, -1F, -1.5F, 2, 7, 3);
		LegRIGHTREAR.addBox("LegRearRightLower", -2F, 6F, -0.5F, 2, 6, 2);
		LegRIGHTREAR.addBox("LegRearRightPaw", -2.5F, 12F, -1.5F, 3, 1, 3);

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
		if(((EntityTiger)entityIn).getGender() == Gender.Male)
		{
			GlStateManager.scale(1.5, 1.5, 1.5);
			GlStateManager.translate(0.0, -0.5, -0.5);
		}
		else
		{
			GlStateManager.scale(1.4, 1.4, 1.4);
			GlStateManager.translate(0.0, -0.44, -0.5);
		}
		HEAD.render(scale);
		BODY.render(scale);
		LegLEFTREAR.render(scale);
		LegLEFTFRONT.render(scale);
		LegRIGHTFRONT.render(scale);
		LegRIGHTREAR.render(scale);
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
		this.HEAD.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.HEAD.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		this.LegLEFTFRONT.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.0F * p_78087_2_;
		this.LegLEFTREAR.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.4662F + (float)Math.PI) * 1.0F * p_78087_2_;
		this.LegRIGHTFRONT.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.0F * p_78087_2_;
		this.LegRIGHTREAR.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.4662F) * 1.0F * p_78087_2_;
	}

}

