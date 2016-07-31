package com.bioxx.tfc2.rendering.particles;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import com.bioxx.tfc2.Core;

public abstract class ParticleAnvil extends Particle
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
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		this.particleScale = 0.05f;
	}

	protected abstract ResourceLocation getTexture();

	@Override
	public void renderParticle(VertexBuffer worldRendererIn, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) 
	{
		Tessellator tessellator = Tessellator.getInstance();

		Core.bindTexture(getTexture());

		if(((float)this.particleAge/(float)this.particleMaxAge) > 1)
			return;

		float scale = particleScale-(particleScale/2f)*((float)this.particleAge/(float)this.particleMaxAge);
		float posX = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
		float posY = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
		float posZ = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);
		//rotationXY = 0;
		//rotationXZ = 0.05f;
		height = 1f;

		worldRendererIn.begin(7, getVertexFormat());
		worldRendererIn.pos((double) (posX - rotationX * scale - rotationXY * scale),  (posY - rotationZ * scale * height), (double) (posZ - rotationYZ * scale - rotationXZ * scale)).tex((double) 1, (double) 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();
		worldRendererIn.pos((double) (posX - rotationX * scale + rotationXY * scale),  (posY + rotationZ * scale * height), (double) (posZ - rotationYZ * scale + rotationXZ * scale)).tex((double) 1, (double) 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();
		worldRendererIn.pos((double) (posX + rotationX * scale + rotationXY * scale),  (posY + rotationZ * scale * height), (double) (posZ + rotationYZ * scale + rotationXZ * scale)).tex((double) 0, (double) 0).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();
		worldRendererIn.pos((double) (posX + rotationX * scale - rotationXY * scale),  (posY - rotationZ * scale * height), (double) (posZ + rotationYZ * scale - rotationXZ * scale)).tex((double) 0, (double) 1).color(this.particleRed, this.particleGreen, this.particleBlue, 1.0F).lightmap(0, 240).endVertex();

		tessellator.draw();
	}

	@Override
	public int getFXLayer()
	{
		return 3;
	}

	protected VertexFormat getVertexFormat() {
		return DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP;
	}

}
