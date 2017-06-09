package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElephant extends ModelBase
{
	ModelRenderer Body;
	ModelRenderer Head;
	ModelRenderer LeftLegRear;
	ModelRenderer LeftLegFront;
	ModelRenderer RightLegRear;
	ModelRenderer RightLegFront;

	public ModelElephant()
	{
		textureWidth = 256;
		textureHeight = 128;
		setTextureOffset("Body.Body", 36, 58);
		setTextureOffset("Body.Tail", 49, 53);
		setTextureOffset("Head.Head", 89, 23);
		setTextureOffset("TuskRight.TuskRight", 50, 21);
		setTextureOffset("End.End", 56, 39);
		setTextureOffset("TuskLeft.TuskLeft", 50, 21);
		setTextureOffset("End.End", 56, 39);
		setTextureOffset("Head.TrunkTop", 143, 0);
		setTextureOffset("Head.TrunkMid", 143, 14);
		setTextureOffset("Head.TrunkEnd", 143, 27);
		setTextureOffset("EarLeft.EarLeft", 50, 0);
		setTextureOffset("EarRight.EarRight", 50, 0);
		setTextureOffset("LeftLegRear.Upper", 0, 0);
		setTextureOffset("LeftLegRear.Lower", 0, 86);
		setTextureOffset("LeftLegRear.Foot", 0, 113);
		setTextureOffset("LeftLegFront.Upper", 0, 0);
		setTextureOffset("LeftLegFront.Lower", 0, 86);
		setTextureOffset("LeftLegFront.Foot", 0, 113);
		setTextureOffset("RightLegRear.Upper", 0, 0);
		setTextureOffset("RightLegRear.Lower", 0, 86);
		setTextureOffset("RightLegRear.Foot", 0, 113);
		setTextureOffset("RightLegFront.Upper", 0, 0);
		setTextureOffset("RightLegFront.Lower", 0, 86);
		setTextureOffset("RightLegFront.Foot", 0, 113);


		Body = new ModelRenderer(this,"Body");
		Body.setRotationPoint(0F ,-14F, 0F);
		setRotation(Body, 0F, 0F, 0F);
		Body.addBox("Body",-10F, -12F, -20F, 20, 25, 45);
		Body.addBox("Tail",-1F, -8F, 25F, 2, 25, 2);

		Head = new ModelRenderer(this,"Head");
		Head.setRotationPoint(0F ,-20F, -20F);
		setRotation(Head, 0F, 0F, 0F);
		Head.addBox("Head",-6F, -7F, -15F, 12, 20, 15);
		Head.addBox("TrunkTop",-3F, 13F, -15F, 6, 8, 6);
		Head.addBox("TrunkMid",-2.5F, 21F, -14.5F, 5, 8, 5);
		Head.addBox("TrunkEnd",-2F, 29F, -14F, 4, 8, 4);

		ModelRenderer Head_TuskRight = new ModelRenderer(this,"TuskRight");
		Head_TuskRight.addBox("TuskRight", -1.5F, -1.5F, -15F, 3, 3, 15);
		Head_TuskRight.setRotationPoint(-6F, 9F, -7F);
		setRotation(Head_TuskRight, 0.7853982F, 0F, 0F);
		Head.addChild(Head_TuskRight);

		ModelRenderer TuskRight_End = new ModelRenderer(this,"End");
		TuskRight_End.addBox("End", -1F, 9.5F, -19.8F, 2, 2, 10);
		TuskRight_End.setRotationPoint(0F, 0F, 0F);
		setRotation(TuskRight_End, -0.7853982F, 0F, 0F);
		Head_TuskRight.addChild(TuskRight_End);

		ModelRenderer Head_TuskLeft = new ModelRenderer(this,"TuskLeft");
		Head_TuskLeft.addBox("TuskLeft", -1.5F, -1.5F, -15F, 3, 3, 15);
		Head_TuskLeft.setRotationPoint(6F, 9F, -7F);
		setRotation(Head_TuskLeft, 0.7853982F, 0F, 0F);
		Head.addChild(Head_TuskLeft);

		ModelRenderer TuskLeft_End = new ModelRenderer(this,"End");
		TuskLeft_End.addBox("End", -1F, 9.5F, -19.8F, 2, 2, 10);
		TuskLeft_End.setRotationPoint(0F, 0F, 0F);
		setRotation(TuskLeft_End, -0.7853982F, 0F, 0F);
		Head_TuskLeft.addChild(TuskLeft_End);

		ModelRenderer Head_EarLeft = new ModelRenderer(this,"EarLeft");
		Head_EarLeft.addBox("EarLeft", -3F, 0F, -1F, 15, 20, 1);
		Head_EarLeft.setRotationPoint(5F, -5F, -5F);
		setRotation(Head_EarLeft, 0F, -0.6108652F, -0.3490658F);
		Head.addChild(Head_EarLeft);

		ModelRenderer Head_EarRight = new ModelRenderer(this,"EarRight");
		Head_EarRight.mirror = true;
		Head_EarRight.addBox("EarRight", -12F, 0F, -1F, 15, 20, 1);
		Head_EarRight.mirror = false;
		Head_EarRight.setRotationPoint(-5F, -5F, -5F);
		setRotation(Head_EarRight, 0F, 0.6108652F, 0.3490658F);
		Head.addChild(Head_EarRight);

		LeftLegRear = new ModelRenderer(this,"LeftLegRear");
		LeftLegRear.setRotationPoint(4F ,-15F, 14F);
		setRotation(LeftLegRear, 0F, 0F, 0F);
		LeftLegRear.addBox("Upper",0F, -7F, -7F, 10, 25, 15);
		LeftLegRear.addBox("Lower",1F, 18F, -2F, 8, 15, 8);
		LeftLegRear.addBox("Foot",0.5F, 33F, -2.5F, 9, 6, 9);

		LeftLegFront = new ModelRenderer(this,"LeftLegFront");
		LeftLegFront.setRotationPoint(4F ,-14F, -11F);
		setRotation(LeftLegFront, 0F, 0F, 0F);
		LeftLegFront.addBox("Upper",0F, -7F, -7F, 10, 22, 15);
		LeftLegFront.addBox("Lower",1F, 13F, -3F, 8, 19, 8);
		LeftLegFront.addBox("Foot",0.5F, 32F, -3.5F, 9, 6, 9);

		RightLegRear = new ModelRenderer(this,"RightLegRear");
		RightLegRear.setRotationPoint(-4F ,-15F, 14F);
		setRotation(RightLegRear, 0F, 0F, 0F);
		RightLegRear.addBox("Upper",-10F, -7F, -7F, 10, 25, 15);
		RightLegRear.addBox("Lower",-9F, 18F, -2F, 8, 15, 8);
		RightLegRear.addBox("Foot",-9.5F, 33F, -2.5F, 9, 6, 9);

		RightLegFront = new ModelRenderer(this,"RightLegFront");
		RightLegFront.setRotationPoint(-4F ,-14F, -11F);
		setRotation(RightLegFront, 0F, 0F, 0F);
		RightLegFront.addBox("Upper",-10F, -7F, -7F, 10, 22, 15);
		RightLegFront.addBox("Lower",-9F, 13F, -3F, 8, 19, 8);
		RightLegFront.addBox("Foot",-9.5F, 32F, -3.5F, 9, 6, 9);
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
			LeftLegFront.render(scale);
			LeftLegRear.render(scale);
			RightLegFront.render(scale);
			RightLegRear.render(scale);
			GlStateManager.popMatrix();
		}
		else
		{
			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, 0.0F, -0.5F);
			this.Head.render(scale);
			this.Body.render(scale);
			LeftLegFront.render(scale);
			LeftLegRear.render(scale);
			RightLegFront.render(scale);
			RightLegRear.render(scale);
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
		this.LeftLegFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
		this.LeftLegRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.RightLegFront.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.RightLegRear.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
	}

}

