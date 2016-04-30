package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelElk extends ModelBase
{
	ModelRenderer Body;
	ModelRenderer Rump;
	ModelRenderer Tail;
	ModelRenderer Neck;
	ModelRenderer Leg4a;
	ModelRenderer Leg3a;
	ModelRenderer Leg1;
	ModelRenderer Leg2;
	ModelRenderer Belly;

	public ModelElk()
	{
		textureWidth = 64;
		textureHeight = 128;
		setTextureOffset("RightEar.RightEar", 26, 16);
		setTextureOffset("LeftEar.LeftEar", 26, 16);
		setTextureOffset("Neck1.Neck1", 0, 78);
		setTextureOffset("Neck2.Neck2", 25, 78);
		setTextureOffset("Neck3.Neck3", 0, 93);
		setTextureOffset("Head.Head", 0, 28);
		setTextureOffset("Dome.Dome", 0, 23);
		setTextureOffset("Nose.Nose", 11, 39);
		setTextureOffset("Jaw.Jaw", 0, 39);
		setTextureOffset("RightAntler1.RightAntler1", 0, 45);
		setTextureOffset("RightAntler1_1.RightAntler1_1", 5, 51);
		setTextureOffset("RightAntler1_2.RightAntler1_2", 42, 0);
		setTextureOffset("RightAntler1_3.RightAntler1_3", 37, 0);
		setTextureOffset("RightAntler2.RightAntler2", 5, 45);
		setTextureOffset("LeftAntler1.LeftAntler1", 0, 45);
		setTextureOffset("LeftAntler1_1.LeftAntler1_1", 5, 51);
		setTextureOffset("LeftAntler1_2.LeftAntler1_2", 42, 0);
		setTextureOffset("LeftAntler1_3.LeftAntler1_3", 37, 0);
		setTextureOffset("LeftAntler2.LeftAntler2", 5, 45);
		setTextureOffset("Leg4b.Leg4b", 42, 11);
		setTextureOffset("Leg4c.Leg4c", 47, 0);
		setTextureOffset("Leg3b.Leg3b", 42, 11);
		setTextureOffset("Leg3c.Leg3c", 47, 0);
		setTextureOffset("Leg1c.Leg1c", 13, 14);
		setTextureOffset("Box42.Box42", 0, 0);
		setTextureOffset("Leg1a.Leg1a", 0, 0);
		setTextureOffset("Leg1b.Leg1b", 0, 14);
		setTextureOffset("Box42.Box42", 0, 0);
		setTextureOffset("Leg2a.Leg2a", 0, 0);
		setTextureOffset("Leg2b.Leg2b", 0, 14);
		setTextureOffset("Leg2c.Leg2c", 13, 14);

		Body = new ModelRenderer(this,"Body");
		Body.addBox(-5F, -10F, -8F, 10, 19, 12);
		Body.setRotationPoint(0F ,4F, -2F);
		setRotation(Body, 1.570796F, 0F, 0F);

		Rump = new ModelRenderer(this,"Rump");
		Rump.addBox(-5F, 0F, -9F, 10, 6, 9);
		Rump.setRotationPoint(0F ,0F, 7F);
		setRotation(Rump, 1.570796F, 0F, 0F);

		Tail = new ModelRenderer(this,"Tail");
		Tail.addBox(-1F, 0F, 0F, 2, 3, 1);
		Tail.setRotationPoint(0F ,1F, 13F);
		setRotation(Tail, 0F, 0F, 0F);

		Neck = new ModelRenderer(this,"Neck");
		Neck.setRotationPoint(0F ,8.5F, -5F);
		setRotation(Neck, 0F, 0F, 0F);

		ModelRenderer Neck_Neck1 = new ModelRenderer(this,"Neck1");
		Neck_Neck1.addBox("Neck1", -3F, -7.5F, -13F, 6, 8, 5);
		Neck_Neck1.setRotationPoint(0F, 0F, 0F);
		setRotation(Neck_Neck1, 0F, 0F, 0F);
		Neck.addChild(Neck_Neck1);

		ModelRenderer Neck_Neck2 = new ModelRenderer(this,"Neck2");
		Neck_Neck2.addBox("Neck2", -4F, -8F, -8F, 8, 10, 8);
		Neck_Neck2.setRotationPoint(0F, 0F, 0F);
		setRotation(Neck_Neck2, 0F, 0F, 0F);
		Neck.addChild(Neck_Neck2);

		ModelRenderer Neck_Neck3 = new ModelRenderer(this,"Neck3");
		Neck_Neck3.addBox("Neck3", -4.5F, 0F, 0F, 9, 3, 6);
		Neck_Neck3.setRotationPoint(0F, -8F, 0F);
		setRotation(Neck_Neck3, -0.3490658F, 0F, 0F);
		Neck.addChild(Neck_Neck3);

		ModelRenderer Neck1_Head = new ModelRenderer(this,"Head");
		Neck1_Head.addBox("Head", -2F, -1F, -4F, 4, 5, 5);
		Neck1_Head.setRotationPoint(0F, -6.7F, -13.8F);
		setRotation(Neck1_Head, 0F, 0F, 0F);
		Neck_Neck1.addChild(Neck1_Head);

		ModelRenderer Head_Dome = new ModelRenderer(this,"Dome");
		Head_Dome.addBox("Dome", -1.5F, -2F, -1.5F, 3, 2, 2);
		Head_Dome.setRotationPoint(0F, 0F, 0F);
		setRotation(Head_Dome, 0F, 0F, 0F);
		Neck1_Head.addChild(Head_Dome);

		ModelRenderer Head_Nose = new ModelRenderer(this,"Nose");
		Head_Nose.addBox("Nose", -1.5F, 0F, -4F, 3, 2, 4);
		Head_Nose.setRotationPoint(0F, -1F, -4F);
		setRotation(Head_Nose, 0.5235988F, 0F, 0F);
		Neck1_Head.addChild(Head_Nose);

		ModelRenderer Head_Jaw = new ModelRenderer(this,"Jaw");
		Head_Jaw.addBox("Jaw", -1F, -1F, -3F, 2, 2, 3);
		Head_Jaw.setRotationPoint(0F, 2.2F, -3.3F);
		setRotation(Head_Jaw, 0.08726646F, -0.02792527F, 0F);
		Neck1_Head.addChild(Head_Jaw);

		ModelRenderer Head_RightEar = new ModelRenderer(this,"RightEar");
		Head_RightEar.addBox("RightEar", -3F, -1F, -0.5F, 4, 2, 1);
		Head_RightEar.setRotationPoint(-2.3F, -0.5F, 0F);
		setRotation(Head_RightEar, 0F, 0.1745329F, 0.5235988F);
		Neck1_Head.addChild(Head_RightEar);

		ModelRenderer Head_LeftEar = new ModelRenderer(this,"LeftEar");
		Head_LeftEar.addBox("LeftEar", -1F, -1F, -0.5F, 4, 2, 1);
		Head_LeftEar.setRotationPoint(2.3F, -0.5F, 0F);
		setRotation(Head_LeftEar, 0F, -0.1745329F, -0.5235988F);
		Neck1_Head.addChild(Head_LeftEar);

		ModelRenderer Head_RightAntler1 = new ModelRenderer(this,"RightAntler1");
		Head_RightAntler1.addBox("RightAntler1", -0.5F, -7F, -0.5F, 1, 8, 1);
		Head_RightAntler1.setRotationPoint(-1F, -1.8F, -0.2F);
		setRotation(Head_RightAntler1, -1.047198F, -1.047198F, 0F);
		Neck1_Head.addChild(Head_RightAntler1);

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
		Neck1_Head.addChild(Head_RightAntler2);

		ModelRenderer Head_LeftAntler1 = new ModelRenderer(this,"LeftAntler1");
		Head_LeftAntler1.addBox("LeftAntler1", -0.5F, -7F, -0.5F, 1, 8, 1);
		Head_LeftAntler1.setRotationPoint(1F, -1.8F, -0.2F);
		setRotation(Head_LeftAntler1, -1.047198F, 1.047198F, 0F);
		Neck1_Head.addChild(Head_LeftAntler1);

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
		Neck1_Head.addChild(Head_LeftAntler2);

		Leg4a = new ModelRenderer(this,"Leg4a");
		Leg4a.addBox(-2F, -2F, -3F, 4, 9, 6);
		Leg4a.setRotationPoint(3.5F ,6F, -8F);
		setRotation(Leg4a, 0F, 0F, 0F);

		ModelRenderer Leg4a_Leg4b = new ModelRenderer(this,"Leg4b");
		Leg4a_Leg4b.addBox("Leg4b", -1.5F, 7F, -1.5F, 3, 5, 3);
		Leg4a_Leg4b.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg4a_Leg4b, 0F, 0F, 0F);
		Leg4a.addChild(Leg4a_Leg4b);

		ModelRenderer Leg4a_Leg4c = new ModelRenderer(this,"Leg4c");
		Leg4a_Leg4c.addBox("Leg4c", -1F, 12F, -1.5F, 2, 6, 2);
		Leg4a_Leg4c.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg4a_Leg4c, 0F, 0F, 0F);
		Leg4a.addChild(Leg4a_Leg4c);

		Leg3a = new ModelRenderer(this,"Leg3a");
		Leg3a.mirror = true;
		Leg3a.addBox(-2F, -2F, -3F, 4, 9, 6);
		Leg3a.mirror = false;
		Leg3a.setRotationPoint(-3.5F ,6F, -8F);
		setRotation(Leg3a, 0F, 0F, 0F);

		ModelRenderer Leg3a_Leg3b = new ModelRenderer(this,"Leg3b");
		Leg3a_Leg3b.mirror = true;
		Leg3a_Leg3b.addBox("Leg3b", -1.5F, 7F, -1.5F, 3, 5, 3);
		Leg3a_Leg3b.mirror = false;
		Leg3a_Leg3b.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg3a_Leg3b, 0F, 0F, 0F);
		Leg3a.addChild(Leg3a_Leg3b);

		ModelRenderer Leg3a_Leg3c = new ModelRenderer(this,"Leg3c");
		Leg3a_Leg3c.mirror = true;
		Leg3a_Leg3c.addBox("Leg3c", -1F, 12F, -1.5F, 2, 6, 2);
		Leg3a_Leg3c.mirror = false;
		Leg3a_Leg3c.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg3a_Leg3c, 0F, 0F, 0F);
		Leg3a.addChild(Leg3a_Leg3c);

		Leg1 = new ModelRenderer(this,"Leg1");
		Leg1.setRotationPoint(-3.5F ,7F, 7F);
		setRotation(Leg1, 0F, 0F, 0F);

		ModelRenderer Leg1_Leg1c = new ModelRenderer(this,"Leg1c");
		Leg1_Leg1c.mirror = true;
		Leg1_Leg1c.addBox("Leg1c", -1F, 8F, 3.5F, 2, 9, 2);
		Leg1_Leg1c.mirror = false;
		Leg1_Leg1c.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg1_Leg1c, 0F, 0F, 0F);
		Leg1.addChild(Leg1_Leg1c);

		ModelRenderer Leg1_Box42 = new ModelRenderer(this,"Box42");
		Leg1_Box42.addBox("Box42", 0F, 0F, 0F, 1, 1, 1);
		Leg1_Box42.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg1_Box42, 0F, 0F, 0F);
		Leg1.addChild(Leg1_Box42);

		ModelRenderer Leg1_Leg1a = new ModelRenderer(this,"Leg1a");
		Leg1_Leg1a.mirror = true;
		Leg1_Leg1a.addBox("Leg1a", -2F, -2F, -2F, 4, 7, 6);
		Leg1_Leg1a.mirror = false;
		Leg1_Leg1a.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg1_Leg1a, 0.5235988F, 0F, 0F);
		Leg1.addChild(Leg1_Leg1a);

		ModelRenderer Leg1_Leg1b = new ModelRenderer(this,"Leg1b");
		Leg1_Leg1b.mirror = true;
		Leg1_Leg1b.addBox("Leg1b", -1.5F, 4.8F, -1.5F, 3, 5, 3);
		Leg1_Leg1b.mirror = false;
		Leg1_Leg1b.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg1_Leg1b, 0.5235988F, 0F, 0F);
		Leg1.addChild(Leg1_Leg1b);

		Leg2 = new ModelRenderer(this,"Leg2");
		Leg2.setRotationPoint(3.5F ,7F, 7F);
		setRotation(Leg2, 0F, 0F, 0F);

		ModelRenderer Leg2_Box42 = new ModelRenderer(this,"Box42");
		Leg2_Box42.addBox("Box42", 0F, 0F, 0F, 1, 1, 1);
		Leg2_Box42.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg2_Box42, 0F, 0F, 0F);
		Leg2.addChild(Leg2_Box42);

		ModelRenderer Leg2_Leg2a = new ModelRenderer(this,"Leg2a");
		Leg2_Leg2a.addBox("Leg2a", -2F, -2F, -2F, 4, 7, 6);
		Leg2_Leg2a.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg2_Leg2a, 0.5235988F, 0F, 0F);
		Leg2.addChild(Leg2_Leg2a);

		ModelRenderer Leg2_Leg2b = new ModelRenderer(this,"Leg2b");
		Leg2_Leg2b.addBox("Leg2b", -1.5F, 4.8F, -1.5F, 3, 5, 3);
		Leg2_Leg2b.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg2_Leg2b, 0.5235988F, 0F, 0F);
		Leg2.addChild(Leg2_Leg2b);

		ModelRenderer Leg2_Leg2c = new ModelRenderer(this,"Leg2c");
		Leg2_Leg2c.addBox("Leg2c", -1F, 8F, 3.5F, 2, 9, 2);
		Leg2_Leg2c.setRotationPoint(0F, 0F, 0F);
		setRotation(Leg2_Leg2c, 0F, 0F, 0F);
		Leg2.addChild(Leg2_Leg2c);

		Belly = new ModelRenderer(this,"Belly");
		Belly.addBox(-4.5F, 0F, -2F, 9, 7, 2);
		Belly.setRotationPoint(0F ,9F, 13F);
		setRotation(Belly, -1.134464F, 0F, 0F);
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
		GlStateManager.translate(0.0F, 0.0F, -0.5F);
		Leg4a.render(scale);
		Leg1.render(scale);
		Leg3a.render(scale);
		Leg2.render(scale);
		Neck.render(scale);
		Body.render(scale);
		Rump.render(scale);
		Tail.render(scale);
		Belly.render(scale);
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
		this.Neck.rotateAngleX = p_78087_5_ / (180F / (float)Math.PI);
		this.Neck.rotateAngleY = p_78087_4_ / (180F / (float)Math.PI);

		this.Leg4a.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
		this.Leg2.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.Leg3a.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F + (float)Math.PI) * 1.4F * p_78087_2_/ rotationDiv;
		this.Leg1.rotateAngleX = MathHelper.cos(p_78087_1_ * 0.6662F) * 1.4F * p_78087_2_/ rotationDiv;
	}

}

