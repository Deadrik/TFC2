package com.bioxx.tfc2.rendering.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.entity.EntityCart;

public class RenderCart extends Render
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
	protected ResourceLocation getEntityTexture(Entity entity) 
	{
		return tex;
	}

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

	/**
	 * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
	 * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
	 * (Render<T extends Entity>) and this method has signature public void func_76986_a(T entity, double d, double d1,
	 * double d2, float f, float f1). But JAD is pre 1.5 so doe
	 *  
	 * @param entityYaw The yaw rotation of the passed entity
	 */
	@Override
	public void doRender(Entity entity, double x, double y, double z, float entityYaw, float partialTicks)
	{
		this.doRender((EntityCart)entity, x, y, z, entityYaw, partialTicks);
	}

}
