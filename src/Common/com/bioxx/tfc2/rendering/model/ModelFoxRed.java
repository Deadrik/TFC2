package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelFoxRed extends ModelBase
{
	ModelRenderer Neck;
	ModelRenderer Body;
	ModelRenderer Head;
	ModelRenderer Tail;
	ModelRenderer LegLeftRear;
	ModelRenderer LegLeftFront;
	ModelRenderer LegRightFront;
	ModelRenderer LegRightRear;

	public ModelFoxRed()
	{
		textureWidth = 64;
		textureHeight = 32;
		setTextureOffset("HEAD.EarLeft", 0, 0);
		setTextureOffset("HEAD.EarLeftMiddle", 8, 2);
		setTextureOffset("HEAD.EarLeftTip", 14, 2);
		setTextureOffset("HEAD.EarRight", 0, 0);
		setTextureOffset("HEAD.EarRightMiddle", 8, 2);
		setTextureOffset("HEAD.EarRightTip", 14, 2);
		setTextureOffset("HEAD.Head", 0, 4);
		setTextureOffset("HEAD.Snout", 0, 14);
		setTextureOffset("HEAD.Jaw", 0, 20);
		setTextureOffset("Tail.TailBase", 54, 0);
		setTextureOffset("Tail.TailMiddle", 52, 4);
		setTextureOffset("Tail.TailTip", 54, 15);
		setTextureOffset("LegLEFTREAR.LegRearLeftLower", 10, 22);
		setTextureOffset("LegLEFTFRONT.LegFrontRightLower", 10, 22);
		setTextureOffset("LegRIGHTFRONT.LegFrontRightLower", 10, 22);
		setTextureOffset("LegRIGHTREAR.LegRearRightLower", 10, 22);

		Neck = new ModelRenderer(this, 20, 0);
		Neck.addBox(-2F, 0F, -2F, 4, 4, 5);
		Neck.setRotationPoint(0F, 9F, 1F);
		Neck.setTextureSize(64, 32);
		Neck.mirror = true;
		setRotation(Neck, -0.5235988F, 0F, 0F);
		Body = new ModelRenderer(this, 20, 9);
		Body.addBox(-3F, -3F, -13F, 6, 7, 16);
		Body.setRotationPoint(0F, 12F, 14F);
		Body.setTextureSize(64, 32);
		Body.mirror = true;
		setRotation(Body, 0F, 0F, 0F);
		Head = new ModelRenderer(this, "HEAD");
		Head.setRotationPoint(0F, 8F, -1F);
		setRotation(Head, 0F, 0F, 0F);
		Head.mirror = true;
		Head.addBox("EarLeft", 1F, -4F, -1F, 3, 3, 1);
		Head.addBox("EarLeftMiddle", 1.5F, -5F, -1F, 2, 1, 1);
		Head.addBox("EarLeftTip", -3F, -6F, -1F, 1, 1, 1);
		Head.addBox("EarRight", -4F, -4F, -1F, 3, 3, 1);
		Head.addBox("EarRightMiddle", -3.5F, -5F, -1F, 2, 1, 1);
		Head.addBox("EarRightTip", 2F, -6F, -1F, 1, 1, 1);
		Head.addBox("Head", -3F, -2F, -3F, 6, 6, 4);
		Head.addBox("Snout", -1.5F, 0F, -7F, 3, 2, 4);
		Head.addBox("Jaw", -1F, 2F, -6F, 2, 1, 3);
		Tail = new ModelRenderer(this, "Tail");
		Tail.setRotationPoint(0F, 10F, 17F);
		setRotation(Tail, 0.7853982F, 0F, 0F);
		Tail.mirror = true;
		Tail.addBox("TailBase", -1F, 0F, -1F, 2, 2, 2);
		Tail.addBox("TailMiddle", -1.5F, 2F, -1.5F, 3, 8, 3);
		Tail.addBox("TailTip", -1F, 10F, -1F, 2, 2, 2);
		LegLeftRear = new ModelRenderer(this, "LegLEFTREAR");
		LegLeftRear.setRotationPoint(2F, 16F, 15F);
		setRotation(LegLeftRear, 0F, 0F, 0F);
		LegLeftRear.mirror = true;
		LegLeftRear.addBox("LegRearLeftLower", -1F, 0F, -1F, 2, 8, 2);
		LegLeftFront = new ModelRenderer(this, "LegLEFTFRONT");
		LegLeftFront.setRotationPoint(2F, 16F, 3F);
		setRotation(LegLeftFront, 0F, 0F, 0F);
		LegLeftFront.mirror = true;
		LegLeftFront.addBox("LegFrontRightLower", -1F, 0F, -1F, 2, 8, 2);
		LegRightFront = new ModelRenderer(this, "LegRIGHTFRONT");
		LegRightFront.setRotationPoint(-2F, 16F, 3F);
		setRotation(LegRightFront, 0F, 0F, 0F);
		LegRightFront.mirror = true;
		LegRightFront.addBox("LegFrontRightLower", -1F, 0F, -1F, 2, 8, 2);
		LegRightRear = new ModelRenderer(this, "LegRIGHTREAR");
		LegRightRear.setRotationPoint(-2F, 16F, 15F);
		setRotation(LegRightRear, 0F, 0F, 0F);
		LegRightRear.mirror = true;
		LegRightRear.addBox("LegRearRightLower", -1F, 0F, -1F, 2, 8, 2);
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

		GlStateManager.pushMatrix();
		GlStateManager.scale(0.5, 0.5, 0.5);
		GlStateManager.translate(0.0F, 1.5, -0.5F);
		this.Head.render(scale);
		this.Neck.render(scale);
		this.Body.render(scale);
		LegLeftFront.render(scale);
		LegLeftRear.render(scale);
		LegRightFront.render(scale);
		LegRightRear.render(scale);
		Tail.render(scale);
		GlStateManager.popMatrix();
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
		this.Head.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.Head.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		this.Body.rotateAngleX = 0;//((float)Math.PI / 2F);
		this.LegLeftFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
		this.LegLeftRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
		this.LegRightFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
		this.LegRightRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
	}

}

