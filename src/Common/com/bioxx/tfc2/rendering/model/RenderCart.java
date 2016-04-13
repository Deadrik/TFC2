package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityCart;

public class RenderCart extends Render<EntityCart>
{
	ResourceLocation tex = new ResourceLocation(Reference.ModID+":"+"textures/mob/cart.png");

	ModelCart mainModel;

	public RenderCart(RenderManager renderManager) 
	{
		super(renderManager);
		mainModel = new ModelCart();
		this.shadowSize = 1.0f;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityCart entity) 
	{
		return tex;
	}

	@Override
	public void doRender(EntityCart cart, double x, double y, double z, float yaw, float partialTicks)
	{
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)x, (float)y + 1.95F, (float)z-0);
		GlStateManager.rotate(180.0F - cart.rotationYaw, 0, 1.0F, 0.0F);
		GlStateManager.rotate(cart.rotationPitch, 1, 0.0F, 0.0F);
		float f4 = 1.0F;
		//GlStateManager.scale(f4, f4, f4);
		//GlStateManager.scale(1.0F / f4, 1.0F / f4, 1.0F / f4);
		this.bindEntityTexture(cart);
		GlStateManager.scale(-1.0F, -1.0F, 1.0F);
		GlStateManager.scale(1.3F, 1.3F, 1.3F);
		this.mainModel.render(cart, partialTicks, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		GlStateManager.popMatrix();
		super.doRender(cart, x, y, z, yaw, partialTicks);
	}
}
