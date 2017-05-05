package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElk extends ModelBase
{
	ModelRenderer Body;
	ModelRenderer Tail;
	ModelRenderer Mane;
	ModelRenderer LegFrontLUpper;
	ModelRenderer LegFrontRUpper;
	ModelRenderer LegRearRUpper;
	ModelRenderer LegRearLUpper;

	public ModelElk()
	{
		textureWidth = 64;
		textureHeight = 128;
		setTextureOffset("Body.Body", 1, 92);
		setTextureOffset("Tail.Tail", 44, 103);
		setTextureOffset("Mane.Mane", 32, 85);
		setTextureOffset("RightEar.RightEar", 13, 0);
		setTextureOffset("LeftEar.LeftEar", 14, 0);
		setTextureOffset("Neck.Neck", 37, 69);
		setTextureOffset("Head.Head", 0, 4);
		setTextureOffset("Dome.Dome", 4, 0);
		setTextureOffset("Nose.Nose", 18, 4);
		setTextureOffset("Jaw.Jaw", 20, 10);
		setTextureOffset("RightAntler1.RightAntler1", 60, 0);
		setTextureOffset("RightAntler1_1.RightAntler1_1", 52, 5);
		setTextureOffset("RightAntler1_2.RightAntler1_2", 56, 0);
		setTextureOffset("RightAntler1_3.RightAntler1_3", 52, 0);
		setTextureOffset("RightAntler2.RightAntler2", 52, 0);
		setTextureOffset("LeftAntler1.LeftAntler1", 60, 0);
		setTextureOffset("LeftAntler1_1.LeftAntler1_1", 52, 5);
		setTextureOffset("LeftAntler1_2.LeftAntler1_2", 56, 0);
		setTextureOffset("LeftAntler1_3.LeftAntler1_3", 52, 0);
		setTextureOffset("LeftAntler2.LeftAntler2", 52, 0);
		setTextureOffset("LegFrontLMid.LegFrontLMid", 0, 87);
		setTextureOffset("LegFrontLLower.LegFrontLLower", 2, 95);
		setTextureOffset("LegFrontRMid.LegFrontRMid", 0, 87);
		setTextureOffset("LegFrontRLower.LegFrontRLower", 2, 95);
		setTextureOffset("LegRearRLower.LegRearRLower", 2, 63);
		setTextureOffset("LegRearRMid.LegRearRMid", 0, 52);
		setTextureOffset("LegRearLMid.LegRearLMid", 0, 52);
		setTextureOffset("LegRearLLower.LegRearLLower", 2, 63);
		setTextureOffset("LegFrontLUpper.LegFrontLUpper", 0, 72);
		setTextureOffset("LegFrontRUpper.LegFrontRUpper", 0, 72);
		setTextureOffset("LegRearLUpper.LegRearLUpper", 0, 38);
		setTextureOffset("LegRearRUpper.LegRearRUpper", 0, 38);

		Body = new ModelRenderer(this,"Body");
		Body.addBox("Body", -5F, -10F, -8F, 10, 25, 11);
		Body.setRotationPoint(0F ,4F, -2F);
		setRotation(Body, 1.570796F, 0F, 0F);

		Tail = new ModelRenderer(this,"Tail");
		Tail.addBox("Tail", -1F, 0F, 0F, 2, 3, 1);
		Tail.setRotationPoint(0F ,1F, 13F);
		setRotation(Tail, 0F, 0F, 0F);

		Mane = new ModelRenderer(this,"Mane");
		Mane.addBox("Mane", -4F, -8F, -8F, 8, 10, 8);
		Mane.setRotationPoint(0F ,9.5F, -6F);
		setRotation(Mane, -0.1745329F, 0F, 0F);

		ModelRenderer Mane_Neck = new ModelRenderer(this,"Neck");
		Mane_Neck.addBox("Neck", -3F, 0F, -5F, 6, 8, 8);
		Mane_Neck.setRotationPoint(0F, -7.5F, -8F);
		setRotation(Mane_Neck, -0.1745329F, 0F, 0F);
		Mane.addChild(Mane_Neck);

		ModelRenderer Neck_Head = new ModelRenderer(this,"Head");
		Neck_Head.addBox("Head", -2F, -1F, -4F, 4, 5, 5);
		Neck_Head.setRotationPoint(0F, 1F, -6F);
		setRotation(Neck_Head, 0.3490658F, 0F, 0F);
		Mane_Neck.addChild(Neck_Head);

		ModelRenderer Head_RightEar = new ModelRenderer(this,"RightEar");
		Head_RightEar.addBox("RightEar", -3F, -1F, -0.5F, 4, 2, 1);
		Head_RightEar.setRotationPoint(-2.3F, -0.5F, 0F);
		setRotation(Head_RightEar, 0F, 0.1745329F, 0.5235988F);
		Neck_Head.addChild(Head_RightEar);

		ModelRenderer Head_LeftEar = new ModelRenderer(this,"LeftEar");
		Head_LeftEar.addBox("LeftEar", -1F, -1F, -0.5F, 4, 2, 1);
		Head_LeftEar.setRotationPoint(2.3F, -0.5F, 0F);
		setRotation(Head_LeftEar, 0F, -0.1745329F, -0.5235988F);
		Neck_Head.addChild(Head_LeftEar);

		ModelRenderer Head_Dome = new ModelRenderer(this,"Dome");
		Head_Dome.addBox("Dome", -1.5F, -2F, -1.5F, 3, 2, 2);
		Head_Dome.setRotationPoint(0F, 0F, 0F);
		setRotation(Head_Dome, 0F, 0F, 0F);
		Neck_Head.addChild(Head_Dome);

		ModelRenderer Head_Nose = new ModelRenderer(this,"Nose");
		Head_Nose.addBox("Nose", -1.5F, 0F, -4F, 3, 2, 4);
		Head_Nose.setRotationPoint(0F, -0.5F, -4F);
		setRotation(Head_Nose, 0.3490658F, 0F, 0F);
		Neck_Head.addChild(Head_Nose);

		ModelRenderer Head_Jaw = new ModelRenderer(this,"Jaw");
		Head_Jaw.addBox("Jaw", -1F, -1F, -3F, 2, 2, 3);
		Head_Jaw.setRotationPoint(0F, 2.2F, -3.3F);
		setRotation(Head_Jaw, 0.08726646F, -0.02792527F, 0F);
		Neck_Head.addChild(Head_Jaw);

		ModelRenderer Head_RightAntler1 = new ModelRenderer(this,"RightAntler1");
		Head_RightAntler1.addBox("RightAntler1", -0.5F, -7F, -0.5F, 1, 8, 1);
		Head_RightAntler1.setRotationPoint(-1F, -1.8F, -0.2F);
		setRotation(Head_RightAntler1, -1.047198F, -1.047198F, 0F);
		Neck_Head.addChild(Head_RightAntler1);

		ModelRenderer RightAntler1_RightAntler1_1 = new ModelRenderer(this,"RightAntler1_1");
		RightAntler1_RightAntler1_1.addBox("RightAntler1_1", -0.5F, -3F, -0.5F, 1, 3, 1);
		RightAntler1_RightAntler1_1.setRotationPoint(0F, -3F, 0F);
		setRotation(RightAntler1_RightAntler1_1, 1.22173F, 1.047198F, 0F);
		Head_RightAntler1.addChild(RightAntler1_RightAntler1_1);

		ModelRenderer RightAntler1_RightAntler1_2 = new ModelRenderer(this,"RightAntler1_2");
		RightAntler1_RightAntler1_2.addBox("RightAntler1_2", 0F, -6F, -0.5F, 1, 6, 1);
		RightAntler1_RightAntler1_2.setRotationPoint(-0.5F, -7F, 0F);
		setRotation(RightAntler1_RightAntler1_2, 0F, 0F, 1.047198F);
		Head_RightAntler1.addChild(RightAntler1_RightAntler1_2);

		ModelRenderer RightAntler1_RightAntler1_3 = new ModelRenderer(this,"RightAntler1_3");
		RightAntler1_RightAntler1_3.addBox("RightAntler1_3", -0.5F, -4F, -0.5F, 1, 4, 1);
		RightAntler1_RightAntler1_3.setRotationPoint(0F, -6.5F, 0F);
		setRotation(RightAntler1_RightAntler1_3, 0F, -0.7853982F, -1.047198F);
		Head_RightAntler1.addChild(RightAntler1_RightAntler1_3);

		ModelRenderer Head_RightAntler2 = new ModelRenderer(this,"RightAntler2");
		Head_RightAntler2.addBox("RightAntler2", -0.5F, -4F, -0.5F, 1, 4, 1);
		Head_RightAntler2.setRotationPoint(-1F, -1.8F, -0.2F);
		setRotation(Head_RightAntler2, 1.22173F, 0.5235988F, 0F);
		Neck_Head.addChild(Head_RightAntler2);

		ModelRenderer Head_LeftAntler1 = new ModelRenderer(this,"LeftAntler1");
		Head_LeftAntler1.addBox("LeftAntler1", -0.5F, -7F, -0.5F, 1, 8, 1);
		Head_LeftAntler1.setRotationPoint(1F, -1.8F, -0.2F);
		setRotation(Head_LeftAntler1, -1.047198F, 1.047198F, 0F);
		Neck_Head.addChild(Head_LeftAntler1);

		ModelRenderer LeftAntler1_LeftAntler1_1 = new ModelRenderer(this,"LeftAntler1_1");
		LeftAntler1_LeftAntler1_1.addBox("LeftAntler1_1", -0.5F, -3F, -0.5F, 1, 3, 1);
		LeftAntler1_LeftAntler1_1.setRotationPoint(0F, -3F, 0F);
		setRotation(LeftAntler1_LeftAntler1_1, 1.22173F, -1.047198F, 0F);
		Head_LeftAntler1.addChild(LeftAntler1_LeftAntler1_1);

		ModelRenderer LeftAntler1_LeftAntler1_2 = new ModelRenderer(this,"LeftAntler1_2");
		LeftAntler1_LeftAntler1_2.addBox("LeftAntler1_2", -1F, -6F, -0.5F, 1, 6, 1);
		LeftAntler1_LeftAntler1_2.setRotationPoint(0.5F, -7F, 0F);
		setRotation(LeftAntler1_LeftAntler1_2, 0F, 0F, -1.047198F);
		Head_LeftAntler1.addChild(LeftAntler1_LeftAntler1_2);

		ModelRenderer LeftAntler1_LeftAntler1_3 = new ModelRenderer(this,"LeftAntler1_3");
		LeftAntler1_LeftAntler1_3.addBox("LeftAntler1_3", -0.5F, -4F, -0.5F, 1, 4, 1);
		LeftAntler1_LeftAntler1_3.setRotationPoint(0F, -6.5F, 0F);
		setRotation(LeftAntler1_LeftAntler1_3, 0F, 0.7853982F, 1.047198F);
		Head_LeftAntler1.addChild(LeftAntler1_LeftAntler1_3);

		ModelRenderer Head_LeftAntler2 = new ModelRenderer(this,"LeftAntler2");
		Head_LeftAntler2.addBox("LeftAntler2", -0.5F, -4F, -0.5F, 1, 4, 1);
		Head_LeftAntler2.setRotationPoint(1F, -1.8F, -0.2F);
		setRotation(Head_LeftAntler2, 1.22173F, -0.5235988F, 0F);
		Neck_Head.addChild(Head_LeftAntler2);

		LegFrontLUpper = new ModelRenderer(this,"LegFrontLUpper");
		LegFrontLUpper.addBox("LegFrontLUpper",-2F, -2F, -3F, 4, 9, 6);
		LegFrontLUpper.setRotationPoint(3.5F ,6F, -8F);
		setRotation(LegFrontLUpper, 0F, 0F, 0F);

		ModelRenderer LegFrontLUpper_LegFrontLMid = new ModelRenderer(this,"LegFrontLMid");
		LegFrontLUpper_LegFrontLMid.addBox("LegFrontLMid", -1.5F, 7F, -1.5F, 3, 5, 3);
		LegFrontLUpper_LegFrontLMid.setRotationPoint(0F, 0F, 0F);
		setRotation(LegFrontLUpper_LegFrontLMid, 0F, 0F, 0F);
		LegFrontLUpper.addChild(LegFrontLUpper_LegFrontLMid);

		ModelRenderer LegFrontLUpper_LegFrontLLower = new ModelRenderer(this,"LegFrontLLower");
		LegFrontLUpper_LegFrontLLower.addBox("LegFrontLLower", -1F, 12F, -1.5F, 2, 6, 2);
		LegFrontLUpper_LegFrontLLower.setRotationPoint(0F, 0F, 0F);
		setRotation(LegFrontLUpper_LegFrontLLower, 0F, 0F, 0F);
		LegFrontLUpper.addChild(LegFrontLUpper_LegFrontLLower);

		LegFrontRUpper = new ModelRenderer(this,"LegFrontRUpper");
		LegFrontRUpper.mirror = true;
		LegFrontRUpper.addBox("LegFrontRUpper", -2F, -2F, -3F, 4, 9, 6);
		LegFrontRUpper.mirror = false;
		LegFrontRUpper.setRotationPoint(-3.5F ,6F, -8F);
		setRotation(LegFrontRUpper, 0F, 0F, 0F);

		ModelRenderer LegFrontRUpper_LegFrontRMid = new ModelRenderer(this,"LegFrontRMid");
		LegFrontRUpper_LegFrontRMid.mirror = true;
		LegFrontRUpper_LegFrontRMid.addBox("LegFrontRMid", -1.5F, 7F, -1.5F, 3, 5, 3);
		LegFrontRUpper_LegFrontRMid.mirror = false;
		LegFrontRUpper_LegFrontRMid.setRotationPoint(0F, 0F, 0F);
		setRotation(LegFrontRUpper_LegFrontRMid, 0F, 0F, 0F);
		LegFrontRUpper.addChild(LegFrontRUpper_LegFrontRMid);

		ModelRenderer LegFrontRUpper_LegFrontRLower = new ModelRenderer(this,"LegFrontRLower");
		LegFrontRUpper_LegFrontRLower.mirror = true;
		LegFrontRUpper_LegFrontRLower.addBox("LegFrontRLower", -1F, 12F, -1.5F, 2, 6, 2);
		LegFrontRUpper_LegFrontRLower.mirror = false;
		LegFrontRUpper_LegFrontRLower.setRotationPoint(0F, 0F, 0F);
		setRotation(LegFrontRUpper_LegFrontRLower, 0F, 0F, 0F);
		LegFrontRUpper.addChild(LegFrontRUpper_LegFrontRLower);

		LegRearRUpper = new ModelRenderer(this,"LegRearRUpper");
		LegRearRUpper.mirror = true;
		LegRearRUpper.addBox("LegRearRUpper", -1F, -4F, -3F, 4, 8, 6);
		LegRearRUpper.mirror = false;
		LegRearRUpper.setRotationPoint(-5F ,9F, 8F);
		setRotation(LegRearRUpper, 0F, 0F, 0F);

		ModelRenderer LegRearRUpper_LegRearRMid = new ModelRenderer(this,"LegRearRMid");
		LegRearRUpper_LegRearRMid.mirror = true;
		LegRearRUpper_LegRearRMid.addBox("LegRearRMid", -2.5F, 3F, -1.5F, 3, 7, 3);
		LegRearRUpper_LegRearRMid.mirror = false;
		LegRearRUpper_LegRearRMid.setRotationPoint(2F, 0F, -1F);
		setRotation(LegRearRUpper_LegRearRMid, 0.3490658F, 0F, 0F);
		LegRearRUpper.addChild(LegRearRUpper_LegRearRMid);

		ModelRenderer LegRearRMid_LegRearRLower = new ModelRenderer(this,"LegRearRLower");
		LegRearRMid_LegRearRLower.mirror = true;
		LegRearRMid_LegRearRLower.addBox("LegRearRLower", -2F, 8F, 2.5F, 2, 7, 2);
		LegRearRMid_LegRearRLower.mirror = false;
		LegRearRMid_LegRearRLower.setRotationPoint(0F, 0F, 0F);
		setRotation(LegRearRMid_LegRearRLower, -0.3490658F, 0F, 0F);
		LegRearRUpper_LegRearRMid.addChild(LegRearRMid_LegRearRLower);

		LegRearLUpper = new ModelRenderer(this,"LegRearLUpper");
		LegRearLUpper.addBox("LegRearLUpper", -3F, -4F, -3F, 4, 8, 6);
		LegRearLUpper.setRotationPoint(5F ,9F, 8F);
		setRotation(LegRearLUpper, 0F, 0F, 0F);

		ModelRenderer LegRearLUpper_LegRearLMid = new ModelRenderer(this,"LegRearLMid");
		LegRearLUpper_LegRearLMid.addBox("LegRearLMid", -2.5F, 3F, -1.5F, 3, 7, 3);
		LegRearLUpper_LegRearLMid.setRotationPoint(0F, 0F, -1F);
		setRotation(LegRearLUpper_LegRearLMid, 0.3490658F, 0F, 0F);
		LegRearLUpper.addChild(LegRearLUpper_LegRearLMid);

		ModelRenderer LegRearLMid_LegRearLLower = new ModelRenderer(this,"LegRearLLower");
		LegRearLMid_LegRearLLower.addBox("LegRearLLower", -2F, 8F, 2.5F, 2, 7, 2);
		LegRearLMid_LegRearLLower.setRotationPoint(0F, 0F, 0F);
		setRotation(LegRearLMid_LegRearLLower, -0.3490658F, 0F, 0F);
		LegRearLUpper_LegRearLMid.addChild(LegRearLMid_LegRearLLower);
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
		//GlStateManager.translate(0.0F, 0.0F, -0.5F);
		Body.render(scale);
		Tail.render(scale);
		LegFrontLUpper.render(scale);
		LegFrontRUpper.render(scale);
		LegRearLUpper.render(scale);
		LegRearRUpper.render(scale);
		Mane.render(scale);
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
		this.Mane.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.Mane.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);

		this.LegFrontLUpper.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegFrontRUpper.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRearLUpper.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.LegRearRUpper.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
	}

}

