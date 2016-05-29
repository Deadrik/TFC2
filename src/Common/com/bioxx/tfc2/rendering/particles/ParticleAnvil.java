package com.bioxx.tfc2.rendering.particles;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;

public abstract class ParticleAnvil extends EntityFX
{
	public static final VertexFormat VERTEX_FORMAT = (new VertexFormat()).addElement(DefaultVertexFormats.POSITION_3F).addElement(DefaultVertexFormats.TEX_2F).addElement(DefaultVertexFormats.COLOR_4UB).addElement(DefaultVertexFormats.TEX_2S).addElement(DefaultVertexFormats.NORMAL_3B).addElement(DefaultVertexFormats.PADDING_1B);

	protected ParticleAnvil(World worldIn, double posXIn, double posYIn, double posZIn) 
	{
		this(worldIn, posXIn, posYIn, posZIn, 0, 0, 0);

	}

	public ParticleAnvil(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
	{
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		init();
	}

	protected void init()
	{
		this.setMaxAge(100);
		this.xSpeed = 0;
		this.ySpeed = 0;
		this.zSpeed = 0;
	}

	protected abstract ResourceLocation getTexture();

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) 
	{
		Tessellator tessellator = Tessellator.getInstance();

		Core.bindTexture(getTexture());
		float f4 = 0.05f;
		float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		rotationXY = 0;
		rotationXZ = 0;
		height = 1f;
		worldRendererIn.begin(7, getVertexFormat());
		worldRendererIn.pos((double) (f5 - rotationX * f4 - rotationXY * f4),  (f6 - rotationZ * f4 * height), (double) (f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double) 1, (double) 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();
		worldRendererIn.pos((double) (f5 - rotationX * f4 + rotationXY * f4),  (f6 + rotationZ * f4 * height), (double) (f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double) 1, (double) 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();
		worldRendererIn.pos((double) (f5 + rotationX * f4 + rotationXY * f4),  (f6 + rotationZ * f4 * height), (double) (f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double) 0, (double) 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();
		worldRendererIn.pos((double) (f5 + rotationX * f4 - rotationXY * f4),  (f6 - rotationZ * f4 * height), (double) (f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double) 0, (double) 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();
		tessellator.draw();
	}

	protected VertexFormat getVertexFormat() {
		return DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP;
	}

}
