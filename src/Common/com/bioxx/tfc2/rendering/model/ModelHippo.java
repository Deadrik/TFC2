package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelHippo extends ModelBase
{
	ModelRenderer Body;
	ModelRenderer Head;
	ModelRenderer Neck;
	ModelRenderer Tail;
	ModelRenderer LegRightRear;
	ModelRenderer LegRightFront;
	ModelRenderer LegLeftFront;
	ModelRenderer LegLeftRear;

	public ModelHippo()
	{
		textureWidth = 128;
		textureHeight = 64;
		setTextureOffset("Head.Jowls", 0, 13);
		setTextureOffset("Head.JawBridge", 36, 28);
		setTextureOffset("Head.JawEnd", 30, 36);
		setTextureOffset("Head.TuskRight", 54, 0);
		setTextureOffset("Head.TuskLeft", 54, 0);
		setTextureOffset("HeadUpper.SnoutEnd", 0, 27);
		setTextureOffset("HeadUpper.SnoutBridge", 30, 0);
		setTextureOffset("HeadUpper.Head", 0, 0);
		setTextureOffset("HeadUpper.EarRight", 23, 0);
		setTextureOffset("HeadUpper.EarLeft", 23, 0);
		setTextureOffset("LegRightRear.LegRtRearLower", 0, 42);
		setTextureOffset("LegRightRear.LegRtRearFoot", 0, 55);
		setTextureOffset("LegRightFront.LegRtFntLower", 0, 42);
		setTextureOffset("LegRightFront.LegRtRearFoot", 0, 55);
		setTextureOffset("LegLeftFront.LegLftFrntLower", 0, 42);
		setTextureOffset("LegLeftFront.LegLftFrntFoot", 0, 55);
		setTextureOffset("LegLeftRear.LegLftRearLower", 0, 42);
		setTextureOffset("LegLeftRear.LegLftRearFoot", 0, 55);

		Body = new ModelRenderer(this, 24, 12);
		Body.addBox(-9F, -7F, -15F, 18, 18, 34);
		Body.setRotationPoint(0F, 2F, 0F);
		Body.setTextureSize(128, 64);
		setRotation(Body, 0F, 0F, 0F);
		Tail = new ModelRenderer(this, 18, 42);
		Tail.addBox(-0.5F, 0F, 0F, 1, 12, 1);
		Tail.setRotationPoint(0F, -3F, 19F);
		Tail.setTextureSize(128, 64);
		setRotation(Tail, 0F, 0F, 0F);
		Neck = new ModelRenderer(this, 94, 0);
		Neck.addBox(-6.5F, -6.5F, 0F, 13, 13, 4);
		Neck.setRotationPoint(0F, 3F, -19F);
		Neck.setTextureSize(128, 64);
		setRotation(Neck, 0F, 0F, 0F);
		Head = new ModelRenderer(this, "Head");
		Head.setRotationPoint(0F, -2F, -19F);
		setRotation(Head, 0.2617994F, 0F, 0F);
		Head.addBox("Jowls", -5F, 5F, -8F, 10, 6, 8);
		Head.addBox("JawBridge", -3F, 5F, -12F, 6, 3, 4);
		Head.addBox("JawEnd", -4F, 5F, -18F, 8, 4, 6);
		Head.addBox("TuskRight", -3F, 2F, -17F, 1, 3, 1);
		Head.addBox("TuskLeft", 2F, 2F, -17F, 1, 3, 1);
		ModelRenderer HeadUpper = new ModelRenderer(this, "HeadUpper");
		HeadUpper.setRotationPoint(0F, 5F, 0F);
		setRotation(HeadUpper, 0F, 0F, 0F);
		HeadUpper.mirror = true;
		HeadUpper.addBox("SnoutEnd", -5F, -4F, -19F, 10, 4, 6);
		HeadUpper.addBox("SnoutBridge", -3F, -4F, -13F, 6, 4, 6);
		HeadUpper.addBox("Head", -4F, -6F, -7F, 8, 6, 7);
		HeadUpper.addBox("EarRight", -6F, -8F, -3F, 3, 3, 1);
		HeadUpper.addBox("EarLeft", 3F, -8F, -3F, 3, 3, 1);
		Head.addChild(HeadUpper);
		LegRightRear = new ModelRenderer(this, "LegRightRear");
		LegRightRear.setRotationPoint(-5F, 3F, 15F);
		setRotation(LegRightRear, 0F, 0F, 0F);
		//LegRightRear.mirror = true;
		LegRightRear.addBox("LegRtRearLower", -3F, 10F, -3F, 4, 8, 5);
		LegRightRear.addBox("LegRtRearFoot", -3.5F, 18F, -3.5F, 5, 3, 6);
		LegRightFront = new ModelRenderer(this, "LegRightFront");
		LegRightFront.setRotationPoint(-5F, 5F, -9F);
		setRotation(LegRightFront, 0F, 0F, 0F);
		//LegRightFront.mirror = true;
		LegRightFront.addBox("LegRtFntLower", -3F, 8F, -3F, 4, 8, 5);
		LegRightFront.addBox("LegRtRearFoot", -3.5F, 16F, -3.5F, 5, 3, 6);
		LegLeftFront = new ModelRenderer(this, "LegLeftFront");
		LegLeftFront.setRotationPoint(7F, 5F, -9F);
		setRotation(LegLeftFront, 0F, 0F, 0F);
		LegLeftFront.mirror = true;
		LegLeftFront.addBox("LegLftFrntLower", -3F, 8F, -3F, 4, 8, 5);
		LegLeftFront.addBox("LegLftFrntFoot", -3.5F, 16F, -3.5F, 5, 3, 6);
		LegLeftRear = new ModelRenderer(this, "LegLeftRear");
		LegLeftRear.setRotationPoint(7F, 3F, 15F);
		setRotation(LegLeftRear, 0F, 0F, 0F);
		LegLeftRear.mirror = true;
		LegLeftRear.addBox("LegLftRearLower", -3F, 10F, -3F, 4, 8, 5);
		LegLeftRear.addBox("LegLftRearFoot", -3.5F, 18F, -3.5F, 5, 3, 6);
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
		GlStateManager.translate(0.0F, 0.0F, 0F);
		this.Head.render(scale);
		this.Body.render(scale);
		this.Neck.render(scale);
		this.Tail.render(scale);
		LegLeftFront.render(scale);
		LegLeftRear.render(scale);
		LegRightFront.render(scale);
		LegRightRear.render(scale);
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
		float rotationDiv = 2;
		this.Head.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.Head.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		this.Body.rotateAngleX = 0;//((float)Math.PI / 2F);
		this.LegLeftFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegLeftRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRightFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRightRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
	}

}

