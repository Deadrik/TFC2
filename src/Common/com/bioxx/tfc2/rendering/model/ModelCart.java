package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

import com.bioxx.tfc2.entity.EntityCart;

public class ModelCart extends ModelBase
{
	//fields
	ModelRenderer Cart;
	ModelRenderer LeftWheel;
	ModelRenderer RightWheel;
	ModelRenderer CartBox;

	public ModelCart()
	{
		textureWidth = 128;
		textureHeight = 64;
		setTextureOffset("CartBox.Bottom", 60, 0);
		setTextureOffset("CartBox.RightWall", 0, 36);
		setTextureOffset("CartBox.LeftWall", 0, 36);
		setTextureOffset("CartBox.FrontWall", 30, 0);
		setTextureOffset("CartBox.HandleRight", 45, 22);
		setTextureOffset("CartBox.HandleLeft", 45, 22);
		setTextureOffset("CartBox.HandleFront", 45, 19);
		setTextureOffset("RightWheel.R4", 40, 26);
		setTextureOffset("RightWheel.R3", 36, 25);
		setTextureOffset("RightWheel.R2", 32, 24);
		setTextureOffset("RightWheel.R1", 28, 23);
		setTextureOffset("RightWheel.Mid", 16, 18);
		setTextureOffset("RightWheel.L1", 12, 23);
		setTextureOffset("RightWheel.L2", 8, 24);
		setTextureOffset("RightWheel.L3", 4, 25);
		setTextureOffset("RightWheel.L4", 0, 26);
		setTextureOffset("LeftWheel.R1", 28, 23);
		setTextureOffset("LeftWheel.R2", 32, 24);
		setTextureOffset("LeftWheel.R3", 36, 25);
		setTextureOffset("LeftWheel.R4", 40, 26);
		setTextureOffset("LeftWheel.Mid", 16, 18);
		setTextureOffset("LeftWheel.L1", 12, 23);
		setTextureOffset("LeftWheel.L2", 8, 24);
		setTextureOffset("LeftWheel.L3", 4, 25);
		setTextureOffset("LeftWheel.L4", 0, 26);

		Cart = new ModelRenderer(this, "Cart");
		Cart.setRotationPoint(0F, 0F, 0F);
		setRotation(Cart, 0F, 0F, 0F);
		Cart.mirror = true;
		CartBox = new ModelRenderer(this, "CartBox");
		CartBox.setRotationPoint(0F, 17F, 0F);
		setRotation(CartBox, 0F, 0F, 0F);
		CartBox.mirror = true;
		CartBox.addBox("Bottom", -8F, 0F, -9F, 16, 1, 18);
		CartBox.addBox("RightWall", -8F, -6F, -9F, 1, 6, 18);
		CartBox.addBox("LeftWall", 7F, -6F, -9F, 1, 6, 18);
		CartBox.addBox("FrontWall", -7F, -6F, -9F, 14, 6, 1);
		CartBox.addBox("HandleRight", -6F, 0F, -19F, 1, 1, 10);
		CartBox.addBox("HandleLeft", 5F, 0F, -19F, 1, 1, 10);
		CartBox.addBox("HandleFront", -5F, 0F, -19F, 10, 1, 1);
		Cart.addChild(CartBox);
		RightWheel = new ModelRenderer(this, "RightWheel");
		RightWheel.setRotationPoint(-9F, 17.5F, 0F);
		setRotation(RightWheel, 0F, 0F, 0F);
		RightWheel.mirror = true;
		RightWheel.addBox("R4", 0F, -2.5F, 5.5F, 1, 5, 1);
		RightWheel.addBox("R3", 0F, -3.5F, 4.5F, 1, 7, 1);
		RightWheel.addBox("R2", 0F, -4.5F, 3.5F, 1, 9, 1);
		RightWheel.addBox("R1", 0F, -5.5F, 2.5F, 1, 11, 1);
		RightWheel.addBox("Mid", 0F, -6.5F, -2.5F, 1, 13, 5);
		RightWheel.addBox("L1", 0F, -5.5F, -3.5F, 1, 11, 1);
		RightWheel.addBox("L2", 0F, -4.5F, -4.5F, 1, 9, 1);
		RightWheel.addBox("L3", 0F, -3.5F, -5.5F, 1, 7, 1);
		RightWheel.addBox("L4", 0F, -2.5F, -6.5F, 1, 5, 1);
		Cart.addChild(RightWheel);
		LeftWheel = new ModelRenderer(this, "LeftWheel");
		LeftWheel.setRotationPoint(8F, 17.5F, 0F);
		setRotation(LeftWheel, 0F, 0F, 0F);
		LeftWheel.mirror = true;
		LeftWheel.addBox("R4", 0F, -2.5F, 5.5F, 1, 5, 1);
		LeftWheel.addBox("R1", 0F, -5.5F, 2.5F, 1, 11, 1);
		LeftWheel.addBox("Mid", 0F, -6.5F, -2.5F, 1, 13, 5);
		LeftWheel.addBox("R3", 0F, -3.5F, 4.5F, 1, 7, 1);
		LeftWheel.addBox("R2", 0F, -4.5F, 3.5F, 1, 9, 1);
		LeftWheel.addBox("L1", 0F, -5.5F, -3.5F, 1, 11, 1);
		LeftWheel.addBox("L2", 0F, -4.5F, -4.5F, 1, 9, 1);
		LeftWheel.addBox("L3", 0F, -3.5F, -5.5F, 1, 7, 1);
		LeftWheel.addBox("L4", 0F, -2.5F, -6.5F, 1, 5, 1);
		Cart.addChild(LeftWheel);
	}

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		render((EntityCart)entity, f, f1, f2, f3, f4, f5);
	}

	public void render(EntityCart entity, float f, float f1, float f2, float f3, float f4, float f5)
	{
		super.render(entity, f, f1, f2, f3, f4, f5);
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		if(entity.isBeingPulled() && entity.isMoving)
		{
			LeftWheel.rotateAngleX += 0.2*f;
			RightWheel.rotateAngleX +=0.2*f;
		}	
		if(!entity.isBeingPulled())
			CartBox.rotateAngleX = (float)Math.toRadians(20);
		else
			CartBox.rotateAngleX = (float)Math.toRadians(-10);
		Cart.render(f5);

	}

	private void setRotation(ModelRenderer model, float x, float y, float z)
	{
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}

	@Override
	public void setRotationAngles(float f, float f1, float f2, float f3, float f4, float f5, Entity e)
	{
		super.setRotationAngles(f, f1, f2, f3, f4, f5, e);
	}

}
