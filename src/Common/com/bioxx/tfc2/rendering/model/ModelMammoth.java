package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelMammoth extends ModelBase
{
	ModelRenderer Body;
	ModelRenderer Head;
	ModelRenderer LegRightRear;
	ModelRenderer LegRightFront;
	ModelRenderer LegLeftFront;
	ModelRenderer LegLeftRear;

	public ModelMammoth()
	{
		textureWidth = 256;
		textureHeight = 128;
		setTextureOffset("Body.BodyFront", 59, 81);
		setTextureOffset("Body.Tail", 49, 53);
		setTextureOffset("Body.BodyFringe", 149, 73);
		setTextureOffset("BodyBack.BodyBack", 61, 82);
		setTextureOffset("BodyBack.BodyBackFringe", 151, 43);
		setTextureOffset("Head.Head", 89, 23);
		setTextureOffset("Head.TopDome", 7, 4);
		setTextureOffset("Head.TuskLeftMiddle", 56, 39);
		setTextureOffset("Head.TuskRightMiddle", 56, 39);
		setTextureOffset("Head.HeadFringe", 166, 103);
		setTextureOffset("TrunkTop.TrunkTop", 143, 0);
		setTextureOffset("TrunkMiddle.TrunkMid", 143, 14);
		setTextureOffset("TrunkEnd.TrunkEnd", 143, 27);
		setTextureOffset("TuskRightBack.TuskRightBack", 50, 21);
		setTextureOffset("TuskLeftBack.TuskLeftBack", 50, 21);
		setTextureOffset("LegRightRear.LegRtRearUpper", 0, 0);
		setTextureOffset("LegRightRear.LegRtRearLower", 0, 86);
		setTextureOffset("LegRightRear.LegRtRearFoot", 0, 113);
		setTextureOffset("LegRightFront.LegRtFrontUpper", 0, 43);
		setTextureOffset("LegRightFront.LegRtFntLower", 0, 86);
		setTextureOffset("LegRightFront.LegRtRearFoot", 0, 113);
		setTextureOffset("LegLeftFront.LegLftFrntUpper", 0, 43);
		setTextureOffset("LegLeftFront.LegLftFrntLower", 0, 86);
		setTextureOffset("LegLeftFront.LegLftFrntFoot", 0, 113);
		setTextureOffset("LegLeftRear.LegLftRearUpper", 0, 0);
		setTextureOffset("LegLeftRear.LegLftRearLower", 0, 86);
		setTextureOffset("LegLeftRear.LegLftRearFoot", 0, 113);

		Body = new ModelRenderer(this, "Body");
		Body.setRotationPoint(0F, -14F, 0F);
		setRotation(Body, 0F, 0F, 0F);
		Body.mirror = true;
		Body.addBox("BodyFront", -10F, -12F, -20F, 20, 25, 22);
		Body.addBox("Tail", -1F, -4F, 24F, 2, 25, 2);
		Body.addBox("BodyFringe", -10F, 13F, -20F, 20, 8, 22);
		ModelRenderer BodyBack = new ModelRenderer(this, "BodyBack");
		BodyBack.setRotationPoint(0F, 0F, 0F);
		//setRotation(BodyBack, 0.3490659F, 0F, 0F);
		BodyBack.mirror = true;
		BodyBack.addBox("BodyBack", -9F, -8F, 2F, 18, 24, 22);
		BodyBack.addBox("BodyBackFringe", -9F, 16F, 2F, 18, 8, 22);
		Body.addChild(BodyBack);
		Head = new ModelRenderer(this, "Head");
		Head.setRotationPoint(0F, -20F, -20F);
		setRotation(Head, 0F, 0F, 0F);
		Head.mirror = true;
		Head.addBox("Head", -6F, -7F, -15F, 12, 20, 15);
		Head.addBox("TopDome", -4F, -10F, -13F, 8, 3, 11);
		Head.addBox("TuskLeftMiddle", 5F, 18.5F, -26.5F, 2, 2, 10);
		Head.addBox("TuskRightMiddle", -7F, 18.5F, -26.5F, 2, 2, 10);
		Head.addBox("HeadFringe", -6F, 13F, -15F, 12, 6, 15);
		ModelRenderer TrunkTop = new ModelRenderer(this, "TrunkTop");
		TrunkTop.setRotationPoint(0F, 13F, -12F);
		setRotation(TrunkTop, 0F, 0F, 0F);
		TrunkTop.mirror = true;
		TrunkTop.addBox("TrunkTop", -3F, 0F, -3F, 6, 8, 6);
		ModelRenderer TrunkMiddle = new ModelRenderer(this, "TrunkMiddle");
		TrunkMiddle.setRotationPoint(0F, 8F, 0F);
		setRotation(TrunkMiddle, 0F, 0F, 0F);
		TrunkMiddle.mirror = true;
		TrunkMiddle.addBox("TrunkMid", -2.5F, 0F, -2.5F, 5, 8, 5);
		ModelRenderer TrunkEnd = new ModelRenderer(this, "TrunkEnd");
		TrunkEnd.setRotationPoint(0F, 8F, 0F);
		setRotation(TrunkEnd, 0F, 0F, 0F);
		TrunkEnd.mirror = true;
		TrunkEnd.addBox("TrunkEnd", -2F, 0F, -2F, 4, 8, 4);
		TrunkMiddle.addChild(TrunkEnd);
		TrunkTop.addChild(TrunkMiddle);
		Head.addChild(TrunkTop);
		ModelRenderer EarLeft = new ModelRenderer(this, "EarLeft");
		EarLeft.setRotationPoint(6F, -3F, -5F);
		setRotation(EarLeft, 0F, 0F, 0F);
		EarLeft.mirror = true;
		Head.addChild(EarLeft);
		ModelRenderer EarRight = new ModelRenderer(this, "EarRight");
		EarRight.setRotationPoint(-6F, -3F, -5F);
		setRotation(EarRight, 0F, 0F, 0F);
		EarRight.mirror = true;
		Head.addChild(EarRight);

		ModelRenderer TuskRightBack = new ModelRenderer(this, "TuskRightBack");
		TuskRightBack.setRotationPoint(-6F, 9F, -7F);
		setRotation(TuskRightBack, 0.7853982F, 0F, 0F);
		TuskRightBack.addBox("TuskRightBack", -1.5F, -1.5F, -15F, 3, 3, 15);
		Head.addChild(TuskRightBack);

		ModelRenderer TuskLeftBack = new ModelRenderer(this, "TuskLeftBack");
		TuskLeftBack.setRotationPoint(6F, 9F, -7F);
		setRotation(TuskLeftBack, 0.7853982F, 0F, 0F);
		TuskLeftBack.addBox("TuskLeftBack", -1.5F, -1.5F, -15F, 3, 3, 15);
		Head.addChild(TuskLeftBack);

		LegRightRear = new ModelRenderer(this, "LegRightRear");
		LegRightRear.setRotationPoint(-4F, -15F, 14F);
		setRotation(LegRightRear, 0F, 0F, 0F);
		LegRightRear.mirror = true;
		LegRightRear.addBox("LegRtRearUpper", -10F, -2F, -7F, 10, 20, 15);
		LegRightRear.addBox("LegRtRearLower", -9F, 18F, -2F, 8, 15, 8);
		LegRightRear.addBox("LegRtRearFoot", -9.5F, 33F, -2.5F, 9, 6, 9);
		LegRightFront = new ModelRenderer(this, "LegRightFront");
		LegRightFront.setRotationPoint(-4F, -14F, -11F);
		setRotation(LegRightFront, 0F, 0F, 0F);
		LegRightFront.mirror = true;
		LegRightFront.addBox("LegRtFrontUpper", -10F, -7F, -7F, 10, 22, 15);
		LegRightFront.addBox("LegRtFntLower", -9F, 13F, -3F, 8, 19, 8);
		LegRightFront.addBox("LegRtRearFoot", -9.5F, 32F, -3.5F, 9, 6, 9);
		LegLeftFront = new ModelRenderer(this, "LegLeftFront");
		LegLeftFront.setRotationPoint(4F, -14F, -11F);
		setRotation(LegLeftFront, 0F, 0F, 0F);
		LegLeftFront.mirror = true;
		LegLeftFront.addBox("LegLftFrntUpper", 0F, -7F, -7F, 10, 22, 15);
		LegLeftFront.addBox("LegLftFrntLower", 1F, 13F, -3F, 8, 19, 8);
		LegLeftFront.addBox("LegLftFrntFoot", 0.5F, 32F, -3.5F, 9, 6, 9);
		LegLeftRear = new ModelRenderer(this, "LegLeftRear");
		LegLeftRear.setRotationPoint(4F, -15F, 14F);
		setRotation(LegLeftRear, 0F, 0F, 0F);
		LegLeftRear.mirror = true;
		LegLeftRear.addBox("LegLftRearUpper", 0F, -2F, -7F, 10, 20, 15);
		LegLeftRear.addBox("LegLftRearLower", 1F, 18F, -2F, 8, 15, 8);
		LegLeftRear.addBox("LegLftRearFoot", 0.5F, 33F, -2.5F, 9, 6, 9);
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
			//GlStateManager.translate(0.0F, this.childYOffset * scale, this.childZOffset * scale);
			this.Head.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			//TuskRightBack.render(scale);
			//TuskLeftBack.render(scale);
			this.Body.render(scale);
			LegLeftFront.render(scale);
			LegLeftRear.render(scale);
			LegRightFront.render(scale);
			LegRightRear.render(scale);
			GlStateManager.popMatrix();
		}
		else
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, -0.5F);
			//TuskRightBack.render(scale);
			//TuskLeftBack.render(scale);
			this.Head.render(scale);
			this.Body.render(scale);
			LegLeftFront.render(scale);
			LegLeftRear.render(scale);
			LegRightFront.render(scale);
			LegRightRear.render(scale);
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
		this.Head.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.Head.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		this.Body.rotateAngleX = 0;//((float)Math.PI / 2F);
		this.LegLeftFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegLeftRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRightFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRightRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
	}

}

