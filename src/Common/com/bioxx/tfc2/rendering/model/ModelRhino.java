package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelRhino extends ModelBase
{
	ModelRenderer Body;
	ModelRenderer Head;
	ModelRenderer LegRightRear;
	ModelRenderer LegRightFront;
	ModelRenderer LegLeftFront;
	ModelRenderer LegLeftRear;

	public ModelRhino()
	{
		textureWidth = 128;
		textureHeight = 64;
		setTextureOffset("Body.Body", 24, 12);
		setTextureOffset("Body.Tail", 30, 31);
		setTextureOffset("Body.Neck", 38, 0);
		setTextureOffset("Head.Head", 98, 0);
		setTextureOffset("Head.Snout", 0, 0);
		setTextureOffset("Head.EarRight", 0, 0);
		setTextureOffset("Head.EarLeft", 0, 0);
		setTextureOffset("Horns.HornFrontBase", 48, 43);
		setTextureOffset("Horns.HornFrontMid", 50, 38);
		setTextureOffset("Horns.HornFrontTip", 52, 35);
		setTextureOffset("Horns.HornBackBase", 40, 44);
		setTextureOffset("Horns.HornBackTip", 42, 41);
		setTextureOffset("LegRightRear.LegRtRearUpper", 0, 19);
		setTextureOffset("LegRightRear.LegRtRearLower", 0, 42);
		setTextureOffset("LegRightRear.LegRtRearFoot", 0, 55);
		setTextureOffset("LegRightFront.LegRtFrontUpper", 92, 25);
		setTextureOffset("LegRightFront.LegRtFntLower", 0, 42);
		setTextureOffset("LegRightFront.LegRtRearFoot", 0, 55);
		setTextureOffset("LegLeftFront.LegLftFrntUpper", 92, 25);
		setTextureOffset("LegLeftFront.LegLftFrntLower", 0, 42);
		setTextureOffset("LegLeftFront.LegLftFrntFoot", 0, 55);
		setTextureOffset("LegLeftRear.LegLftRearUpper", 0, 19);
		setTextureOffset("LegLeftRear.LegLftRearLower", 0, 42);
		setTextureOffset("LegLeftRear.LegLftRearFoot", 0, 55);

		Body = new ModelRenderer(this, "Body");
		Body.setRotationPoint(0F, 2F, 0F);
		setRotation(Body, 0F, 0F, 0F);
		Body.mirror = true;
		Body.addBox("Body", -8F, -7F, -15F, 16, 16, 36);
		Body.addBox("Tail", 0F, -5F, 21F, 1, 10, 1);
		Body.addBox("Neck", -3F, -5F, -20F, 6, 8, 5);
		Head = new ModelRenderer(this, "Head");
		Head.setRotationPoint(0F, 5F, -15F);
		setRotation(Head, 0.2617994F, 0F, 0F);
		Head.mirror = true;
		Head.addBox("Head", -4F, -9F, -7F, 8, 10, 7);
		Head.addBox("Snout", -4F, -7F, -18F, 8, 8, 11);
		Head.addBox("EarRight", -6F, -12F, -3F, 3, 4, 1);
		Head.addBox("EarLeft", 3F, -12F, -3F, 3, 4, 1);
		ModelRenderer Horns = new ModelRenderer(this, "Horns");
		Horns.setRotationPoint(0F, 1F, -3F);
		setRotation(Horns, 0F, 0F, 0F);
		Horns.mirror = true;
		Horns.addBox("HornFrontBase", -1.5F, -10F, -14.5F, 3, 2, 3);
		Horns.addBox("HornFrontMid", -1F, -13F, -14.3F, 2, 3, 2);
		Horns.addBox("HornFrontTip", -0.5F, -15F, -14.1F, 1, 2, 1);
		Horns.addBox("HornBackBase", -1F, -10F, -11F, 2, 2, 2);
		Horns.addBox("HornBackTip", -0.5F, -12F, -10.7F, 1, 2, 1);
		Head.addChild(Horns);
		LegRightRear = new ModelRenderer(this, "LegRightRear");
		LegRightRear.setRotationPoint(-6F, 3F, 15F);
		setRotation(LegRightRear, 0F, 0F, 0F);
		LegRightRear.mirror = true;
		LegRightRear.addBox("LegRtRearUpper", -4F, -4F, -5F, 6, 14, 9);
		LegRightRear.addBox("LegRtRearLower", -3F, 10F, -3F, 4, 8, 5);
		LegRightRear.addBox("LegRtRearFoot", -3.5F, 18F, -3.5F, 5, 3, 6);
		LegRightFront = new ModelRenderer(this, "LegRightFront");
		LegRightFront.setRotationPoint(-6F, 5F, -8F);
		setRotation(LegRightFront, 0F, 0F, 0F);
		LegRightFront.mirror = true;
		LegRightFront.addBox("LegRtFrontUpper", -4F, -6F, -5F, 6, 14, 9);
		LegRightFront.addBox("LegRtFntLower", -3F, 8F, -3F, 4, 8, 5);
		LegRightFront.addBox("LegRtRearFoot", -3.5F, 16F, -3.5F, 5, 3, 6);
		LegLeftFront = new ModelRenderer(this, "LegLeftFront");
		LegLeftFront.setRotationPoint(8F, 5F, -8F);
		setRotation(LegLeftFront, 0F, 0F, 0F);
		LegLeftFront.mirror = true;
		LegLeftFront.addBox("LegLftFrntUpper", -4F, -6F, -5F, 6, 14, 9);
		LegLeftFront.addBox("LegLftFrntLower", -3F, 8F, -3F, 4, 8, 5);
		LegLeftFront.addBox("LegLftFrntFoot", -3.5F, 16F, -3.5F, 5, 3, 6);
		LegLeftRear = new ModelRenderer(this, "LegLeftRear");
		LegLeftRear.setRotationPoint(8F, 3F, 15F);
		setRotation(LegLeftRear, 0F, 0F, 0F);
		LegLeftRear.mirror = true;
		LegLeftRear.addBox("LegLftRearUpper", -4F, -4F, -5F, 6, 14, 9);
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
		this.Head.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.Head.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);
		this.Body.rotateAngleX = 0;//((float)Math.PI / 2F);
		this.LegLeftFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
		this.LegLeftRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
		this.LegRightFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_;
		this.LegRightRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_;
	}

}

