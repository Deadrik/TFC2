package com.bioxx.tfc2.rendering;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.BiomeGenBase;

import net.minecraftforge.client.IRenderHandler;

import org.lwjgl.opengl.GL11;

import com.bioxx.tfc2.world.WeatherManager;

public class WeatherRenderer extends IRenderHandler
{
	private float[] rainXCoords = new float[1024];
	private float[] rainYCoords = new float[1024];
	private static final ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
	private static final ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
	private int rendererUpdateCount = 0;
	private static int rainSoundCounter = 0;
	public WeatherRenderer()
	{
		for (int i = 0; i < 32; ++i)
		{
			for (int j = 0; j < 32; ++j)
			{
				float f = (float)(j - 16);
				float f1 = (float)(i - 16);
				float f2 = MathHelper.sqrt_float(f * f + f1 * f1);
				this.rainXCoords[i << 5 | j] = -f1 / f2;
				this.rainYCoords[i << 5 | j] = f / f2;
			}
		}
	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) 
	{
		++this.rendererUpdateCount;
		double rainStrength = world.getRainStrength(partialTicks);
		rainStrength = WeatherManager.getInstance().getPreciptitation((int)mc.thePlayer.posX, (int)mc.thePlayer.posZ);

		if (rainStrength > 0)
		{
			mc.entityRenderer.enableLightmap();
			Entity entity = mc.getRenderViewEntity();
			int i = MathHelper.floor_double(entity.posX);
			int j = MathHelper.floor_double(entity.posY);
			int k = MathHelper.floor_double(entity.posZ);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			GlStateManager.disableCull();
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.alphaFunc(516, 0.1F);
			double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
			double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
			double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
			int l = MathHelper.floor_double(d1);
			byte b0 = 5;

			if (mc.gameSettings.fancyGraphics)
			{
				b0 = 10;
			}

			byte b1 = -1;
			float f2 = (float)this.rendererUpdateCount + partialTicks;

			if (mc.gameSettings.fancyGraphics)
			{
				b0 = 10;
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

			for (int zCoord = k - b0; zCoord <= k + b0; ++zCoord)
			{
				for (int xCoord = i - b0; xCoord <= i + b0; ++xCoord)
				{
					int k1 = (zCoord - k + 16) * 32 + xCoord - i + 16;
					float f3 = this.rainXCoords[k1] * 0.5F;
					float f4 = this.rainYCoords[k1] * 0.5F;
					BlockPos blockpos = new BlockPos(xCoord, 0, zCoord);
					BiomeGenBase biomegenbase = world.getBiomeGenForCoords(blockpos);

					if (biomegenbase.canSpawnLightningBolt() || biomegenbase.getEnableSnow())
					{
						int yCoord = world.getPrecipitationHeight(blockpos).getY();
						int i2 = j - b0;
						int j2 = j + b0;

						if (i2 < yCoord)
						{
							i2 = yCoord;
						}

						if (j2 < yCoord)
						{
							j2 = yCoord;
						}

						float f5 = 1.0F;
						int k2 = yCoord;

						if (yCoord < l)
						{
							k2 = l;
						}

						if (i2 != j2)
						{
							world.rand.setSeed((long)(xCoord * xCoord * 3121 + xCoord * 45238971 ^ zCoord * zCoord * 418711 + zCoord * 13761));
							float f7;
							double d4;

							if (WeatherManager.getInstance().getTemperature(xCoord, yCoord+20, zCoord) > 0.16)
							{
								if (b1 != 0)
								{
									if (b1 >= 0)
									{
										tessellator.draw();
									}

									b1 = 0;
									mc.getTextureManager().bindTexture(locationRainPng);
									worldrenderer.startDrawingQuads();
								}

								f7 = ((float)(this.rendererUpdateCount + xCoord * xCoord * 3121 + xCoord * 45238971 + zCoord * zCoord * 418711 + zCoord * 13761 & 31) + partialTicks) / 32.0F * (3.0F + world.rand.nextFloat());
								double d3 = (double)((float)xCoord + 0.5F) - entity.posX;
								d4 = (double)((float)zCoord + 0.5F) - entity.posZ;
								float f9 = MathHelper.sqrt_double(d3 * d3 + d4 * d4) / (float)b0;
								float f10 = 1.0F;
								worldrenderer.setBrightness(world.getCombinedLight(new BlockPos(xCoord, k2, zCoord), 0));
								worldrenderer.setColorRGBA_F(f10, f10, f10, ((1.0F - f9 * f9) * 0.5F + 0.5F) * (float)rainStrength);
								worldrenderer.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
								worldrenderer.addVertexWithUV((double)((float)xCoord - f3) + 0.5D, (double)i2, (double)((float)zCoord - f4) + 0.5D, (double)(0.0F * f5), (double)((float)i2 * f5 / 4.0F + f7 * f5));
								worldrenderer.addVertexWithUV((double)((float)xCoord + f3) + 0.5D, (double)i2, (double)((float)zCoord + f4) + 0.5D, (double)(1.0F * f5), (double)((float)i2 * f5 / 4.0F + f7 * f5));
								worldrenderer.addVertexWithUV((double)((float)xCoord + f3) + 0.5D, (double)j2, (double)((float)zCoord + f4) + 0.5D, (double)(1.0F * f5), (double)((float)j2 * f5 / 4.0F + f7 * f5));
								worldrenderer.addVertexWithUV((double)((float)xCoord - f3) + 0.5D, (double)j2, (double)((float)zCoord - f4) + 0.5D, (double)(0.0F * f5), (double)((float)j2 * f5 / 4.0F + f7 * f5));
								worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
							}
							else
							{
								if (b1 != 1)
								{
									if (b1 >= 0)
									{
										tessellator.draw();
									}

									b1 = 1;
									mc.getTextureManager().bindTexture(locationSnowPng);
									worldrenderer.startDrawingQuads();
								}

								f7 = ((float)(this.rendererUpdateCount & 511) + partialTicks) / 512.0F;
								float f13 = world.rand.nextFloat() + f2 * 0.01F * (float)world.rand.nextGaussian();
								float f8 = world.rand.nextFloat() + f2 * (float)world.rand.nextGaussian() * 0.001F;
								d4 = (double)((float)xCoord + 0.5F) - entity.posX;
								double d5 = (double)((float)zCoord + 0.5F) - entity.posZ;
								float f11 = MathHelper.sqrt_double(d4 * d4 + d5 * d5) / (float)b0;
								float f12 = 1.0F;
								worldrenderer.setBrightness((world.getCombinedLight(new BlockPos(xCoord, k2, zCoord), 0) * 3 + 15728880) / 4);
								worldrenderer.setColorRGBA_F(f12, f12, f12, ((1.0F - f11 * f11) * 0.3F + 0.5F) * (float)rainStrength);
								worldrenderer.setTranslation(-d0 * 1.0D, -d1 * 1.0D, -d2 * 1.0D);
								worldrenderer.addVertexWithUV((double)((float)xCoord - f3) + 0.5D, (double)i2, (double)((float)zCoord - f4) + 0.5D, (double)(0.0F * f5 + f13), (double)((float)i2 * f5 / 4.0F + f7 * f5 + f8));
								worldrenderer.addVertexWithUV((double)((float)xCoord + f3) + 0.5D, (double)i2, (double)((float)zCoord + f4) + 0.5D, (double)(1.0F * f5 + f13), (double)((float)i2 * f5 / 4.0F + f7 * f5 + f8));
								worldrenderer.addVertexWithUV((double)((float)xCoord + f3) + 0.5D, (double)j2, (double)((float)zCoord + f4) + 0.5D, (double)(1.0F * f5 + f13), (double)((float)j2 * f5 / 4.0F + f7 * f5 + f8));
								worldrenderer.addVertexWithUV((double)((float)xCoord - f3) + 0.5D, (double)j2, (double)((float)zCoord - f4) + 0.5D, (double)(0.0F * f5 + f13), (double)((float)j2 * f5 / 4.0F + f7 * f5 + f8));
								worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
							}
						}
					}
				}
			}

			if (b1 >= 0)
			{
				tessellator.draw();
			}

			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			mc.entityRenderer.disableLightmap();
		}
	}

	public static void addRainParticles(Random random, int rendererUpdateCount)
	{
		Minecraft mc = Minecraft.getMinecraft();
		WorldClient worldclient = mc.theWorld;
		if(worldclient.provider.getDimensionId() != 0)
			return;
		double rainStrength = WeatherManager.getInstance().getPreciptitation((int)mc.thePlayer.posX, (int)mc.thePlayer.posZ);

		if (!mc.gameSettings.fancyGraphics)
		{
			rainStrength /= 2.0F;
		}

		if (rainStrength > 0.0F)
		{
			worldclient.rand.setSeed((long)rendererUpdateCount * 312987231L);
			Entity entity = mc.getRenderViewEntity();
			BlockPos blockpos = new BlockPos(entity);
			byte b0 = 10;
			double d0 = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			int i = 0;
			int rainParticles = (int)(100.0F * rainStrength * rainStrength);

			if (mc.gameSettings.particleSetting == 1)
			{
				rainParticles >>= 1;
			}
			else if (mc.gameSettings.particleSetting == 2)
			{
				rainParticles = 0;
			}

			for (int k = 0; k < rainParticles; ++k)
			{
				BlockPos blockPos1 = worldclient.getPrecipitationHeight(blockpos.add(worldclient.rand.nextInt(b0) - worldclient.rand.nextInt(b0), 0, worldclient.rand.nextInt(b0) - worldclient.rand.nextInt(b0)));
				double temp = WeatherManager.getInstance().getTemperature(blockPos1);
				BlockPos blockpos2 = blockPos1.down();
				Block block = worldclient.getBlockState(blockpos2).getBlock();

				if (blockPos1.getY() <= blockpos.getY() + b0 && blockPos1.getY() >= blockpos.getY() - b0 && temp > 0.15)
				{
					float f1 = worldclient.rand.nextFloat();
					float f2 = worldclient.rand.nextFloat();

					if (block.getMaterial() == Material.lava)
					{
						mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)blockPos1.getX() + f1), (double)((float)blockPos1.getY() + 0.1F) - block.getBlockBoundsMinY(), (double)((float)blockPos1.getZ() + f2), 0.0D, 0.0D, 0.0D, new int[0]);
					}
					else if (block.getMaterial() != Material.air)
					{
						block.setBlockBoundsBasedOnState(worldclient, blockpos2);
						++i;

						if (worldclient.rand.nextInt(i) == 0)
						{
							d0 = (double)((float)blockpos2.getX() + f1);
							d1 = (double)((float)blockpos2.getY() + 0.1F) + block.getBlockBoundsMaxY() - 1.0D;
							d2 = (double)((float)blockpos2.getZ() + f2);
						}

						mc.theWorld.spawnParticle(EnumParticleTypes.WATER_DROP, (double)((float)blockpos2.getX() + f1), (double)((float)blockpos2.getY() + 0.1F) + block.getBlockBoundsMaxY(), (double)((float)blockpos2.getZ() + f2), 0.0D, 0.0D, 0.0D, new int[0]);
					}
				}
			}

			if (i > 0 && worldclient.rand.nextInt(3) < rainSoundCounter++)
			{
				rainSoundCounter = 0;

				if (d1 > (double)(blockpos.getY() + 1) && worldclient.getPrecipitationHeight(blockpos).getY() > MathHelper.floor_float((float)blockpos.getY()))
				{
					mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.1F, 0.5F, false);
				}
				else
				{
					mc.theWorld.playSound(d0, d1, d2, "ambient.weather.rain", 0.2F, 1.0F, false);
				}
			}
		}
	}

}
