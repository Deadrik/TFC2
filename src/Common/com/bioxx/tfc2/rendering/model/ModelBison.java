package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBison extends ModelBase
{
	ModelRenderer Body;
	ModelRenderer Head;
	ModelRenderer LegRightRear;
	ModelRenderer LegRightFront;
	ModelRenderer LegLeftFront;
	ModelRenderer LegLeftRear;

	public ModelBison()
	{
		textureWidth = 128;
		textureHeight = 64;
		setTextureOffset("Body.BodyFront", 68, 11);
		setTextureOffset("Body.BodyBack", 25, 25);
		setTextureOffset("Body.Tail", 0, 43);
		setTextureOffset("Body.BodyFringe", 76, 45);
		setTextureOffset("Head.HeadTop", 0, 51);
		setTextureOffset("Head.HeadMain", 3, 1);
		setTextureOffset("Head.EarRight", 52, 60);
		setTextureOffset("Head.EarLeft", 52, 60);
		setTextureOffset("Head.Beard", 61, 0);
		setTextureOffset("Head.Jaw", 55, 17);
		setTextureOffset("Horns.HornLeftBase", 60, 60);
		setTextureOffset("Horns.HornLeftTip", 72, 60);
		setTextureOffset("Horns.HornRightBase", 60, 60);
		setTextureOffset("Horns.HornRightTip", 72, 60);
		setTextureOffset("LegRightRear.LegRtRearUpper", 0, 20);
		setTextureOffset("LegRightRear.LegRtRearLower", 84, 0);
		setTextureOffset("LegRightRear.LegRtRearFoot", 21, 21);
		setTextureOffset("LegRightFront.LegRtFrontUpper", 31, 0);
		setTextureOffset("LegRightFront.LegRtFntLower", 84, 0);
		setTextureOffset("LegRightFront.LegRtRearFoot", 21, 21);
		setTextureOffset("LegLeftFront.LegLftFrntUpper", 31, 0);
		setTextureOffset("LegLeftFront.LegLftFrntLower", 84, 0);
		setTextureOffset("LegLeftFront.LegLftFrntFoot", 21, 21);
		setTextureOffset("LegLeftRear.LegLftRearUpper", 0, 20);
		setTextureOffset("LegLeftRear.LegLftRearLower", 84, 0);
		setTextureOffset("LegLeftRear.LegLftRearFoot", 21, 21);

		Body = new ModelRenderer(this, "Body");
		Body.setRotationPoint(0F, 2F, 0F);
		setRotation(Body, 0F, 0F, 0F);
		Body.mirror = true;
		Body.addBox("BodyFront", -7F, -7F, -15F, 14, 17, 16);
		Body.addBox("BodyBack", -6F, -5F, 1F, 12, 15, 19);
		Body.addBox("Tail", -0.5F, -2F, 20F, 1, 10, 1);
		Body.addBox("BodyFringe", -6F, 10F, -14F, 12, 5, 14);
		Head = new ModelRenderer(this, "Head");
		Head.setRotationPoint(0F, 1F, -15F);
		setRotation(Head, 0F, 0F, 0F);
		Head.mirror = true;
		Head.addBox("HeadTop", -4F, -4F, -8.5F, 8, 4, 9);
		Head.addBox("HeadMain", -3F, 0F, -8F, 6, 11, 8);
		Head.addBox("EarRight", -6F, 1F, -3F, 3, 3, 1);
		Head.addBox("EarLeft", 3F, 1F, -3F, 3, 3, 1);
		Head.addBox("Beard", -2F, 13F, -6F, 4, 6, 5);
		Head.addBox("Jaw", -2F, 11F, -7F, 4, 2, 6);
		ModelRenderer  Horns = new ModelRenderer(this, "Horns");
		Horns.setRotationPoint(0F, 1F, -3F);
		setRotation(Horns, 0F, 0F, 0F);
		Horns.mirror = true;
		Horns.addBox("HornLeftBase", 3F, -2F, -3F, 4, 2, 2);
		Horns.addBox("HornLeftTip", 6F, -5F, -2.5F, 1, 3, 1);
		Horns.addBox("HornRightBase", -7F, -2F, -3F, 4, 2, 2);
		Horns.addBox("HornRightTip", -7F, -5F, -2.5F, 1, 3, 1);
		Head.addChild(Horns);
		LegRightRear = new ModelRenderer(this, "LegRightRear");
		LegRightRear.setRotationPoint(-4F, 3F, 15F);
		setRotation(LegRightRear, 0F, 0F, 0F);
		LegRightRear.mirror = true;
		LegRightRear.addBox("LegRtRearUpper", -4F, -4F, -5F, 6, 14, 9);
		LegRightRear.addBox("LegRtRearLower", -3F, 10F, -3F, 4, 6, 5);
		LegRightRear.addBox("LegRtRearFoot", -2.5F, 16F, -2.5F, 3, 5, 3);
		LegRightFront = new ModelRenderer(this, "LegRightFront");
		LegRightFront.setRotationPoint(-5F, 5F, -8F);
		setRotation(LegRightFront, 0F, 0F, 0F);
		LegRightFront.mirror = true;
		LegRightFront.addBox("LegRtFrontUpper", -4F, -6F, -5F, 6, 14, 9);
		LegRightFront.addBox("LegRtFntLower", -3F, 8F, -3F, 4, 6, 5);
		LegRightFront.addBox("LegRtRearFoot", -2.5F, 14F, -2.5F, 3, 5, 3);
		LegLeftFront = new ModelRenderer(this, "LegLeftFront");
		LegLeftFront.setRotationPoint(7F, 5F, -8F);
		setRotation(LegLeftFront, 0F, 0F, 0F);
		LegLeftFront.mirror = true;
		LegLeftFront.addBox("LegLftFrntUpper", -4F, -6F, -5F, 6, 14, 9);
		LegLeftFront.addBox("LegLftFrntLower", -3F, 8F, -3F, 4, 6, 5);
		LegLeftFront.addBox("LegLftFrntFoot", -2.5F, 14F, -2.5F, 3, 5, 3);
		LegLeftRear = new ModelRenderer(this, "LegLeftRear");
		LegLeftRear.setRotationPoint(6F, 3F, 15F);
		setRotation(LegLeftRear, 0F, 0F, 0F);
		LegLeftRear.mirror = true;
		LegLeftRear.addBox("LegLftRearUpper", -4F, -4F, -5F, 6, 14, 9);
		LegLeftRear.addBox("LegLftRearLower", -3F, 10F, -3F, 4, 6, 5);
		LegLeftRear.addBox("LegLftRearFoot", -2.5F, 16F, -2.5F, 3, 5, 3);
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
			Head.render(scale);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);
			LegLeftFront.render(scale);
			LegRightRear.render(scale);
			LegRightFront.render(scale);
			LegLeftRear.render(scale);
			Body.render(scale);
			GlStateManager.popMatrix();
		}
		else
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, -0.5F);
			LegLeftFront.render(scale);
			LegRightRear.render(scale);
			LegRightFront.render(scale);
			LegLeftRear.render(scale);
			Head.render(scale);
			Body.render(scale);
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

