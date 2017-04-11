package com.bioxx.tfc2.rendering;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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
				float f2 = MathHelper.sqrt(f * f + f1 * f1);
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
		rainStrength = WeatherManager.getInstance().getPrecipitation((int)mc.player.posX, (int)mc.player.posZ);
		if (rainStrength > 0.0)
		{
			mc.entityRenderer.enableLightmap();
			Entity entity = mc.getRenderViewEntity();
			int entityX = MathHelper.floor(entity.posX);
			int entityY = MathHelper.floor(entity.posY);
			int entityZ = MathHelper.floor(entity.posZ);
			Tessellator tessellator = Tessellator.getInstance();
			VertexBuffer worldrenderer = tessellator.getBuffer();
			GlStateManager.disableCull();
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.alphaFunc(516, 0.1F);
			double partialX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
			double partialY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
			double partialZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
			int l = MathHelper.floor(partialY);
			int precipDensity = 5;

			if (mc.gameSettings.fancyGraphics)
			{
				precipDensity = 10;
			}

			int j1 = -1;
			float f1 = this.rendererUpdateCount + partialTicks;
			worldrenderer.setTranslation(-partialX, -partialY, -partialZ);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos.MutableBlockPos mPos = new BlockPos.MutableBlockPos();

			for (int z = entityZ - precipDensity; z <= entityZ + precipDensity; z++)
			{
				for (int x = entityX - precipDensity; x <= entityX + precipDensity; x++)
				{
					int index = (z - entityZ + 16) * 32 + x - entityX + 16;
					double d3 = this.rainXCoords[index] * 0.5D;
					double d4 = this.rainYCoords[index] * 0.5D;
					mPos.setPos(x, 0, z);


					int j2 = world.getPrecipitationHeight(mPos).getY();
					int k2 = entityY - precipDensity;
					int l2 = entityY + precipDensity;

					if (k2 < j2)
					{
						k2 = j2;
					}

					if (l2 < j2)
					{
						l2 = j2;
					}

					int i3 = j2;

					if (j2 < l)
					{
						i3 = l;
					}

					if (k2 != l2)
					{
						world.rand.setSeed(x * x * 3121 + x * 45238971 ^ z * z * 418711 + z * 13761);
						mPos.setPos(x, k2, z);

						if (WeatherManager.getInstance().getTemperature(mPos) >= 0F)
						{
							if (j1 != 0)
							{
								if (j1 >= 0)
								{
									tessellator.draw();
								}

								j1 = 0;
								mc.getTextureManager().bindTexture(locationRainPng);
								worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
							}

							double d5 = ((this.rendererUpdateCount + x * x * 3121 + x * 45238971 + z * z * 418711 + z * 13761 & 0x1F) + partialTicks) / 32.0D * (3.0D + world.rand.nextDouble());
							double offsetX = x + 0.5F - entity.posX;
							double offsetZ = z + 0.5F - entity.posZ;
							float f3 = MathHelper.sqrt(offsetX * offsetX + offsetZ * offsetZ) / precipDensity;
							float precipAlpha = ((1.0F - f3 * f3) * 0.5F + 0.5F) * Math.max((float)rainStrength, 0.2f);
							mPos.setPos(x, i3, z);
							int j3 = world.getCombinedLight(mPos, 0);
							int k3 = (j3 >> 16 & 0xFFFF);
							int l3 = (j3 & 0xFFFF);
							worldrenderer.pos(x - d3 + 0.5D, k2, z - d4 + 0.5D).tex(0.0D, k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(k3, l3).endVertex();
							worldrenderer.pos(x + d3 + 0.5D, k2, z + d4 + 0.5D).tex(1.0D, k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(k3, l3).endVertex();
							worldrenderer.pos(x + d3 + 0.5D, l2, z + d4 + 0.5D).tex(1.0D, l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(k3, l3).endVertex();
							worldrenderer.pos(x - d3 + 0.5D, l2, z - d4 + 0.5D).tex(0.0D, l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(k3, l3).endVertex();
						}
						else
						{
							if (j1 != 1)
							{
								if (j1 >= 0)
								{
									tessellator.draw();
								}

								j1 = 1;
								mc.getTextureManager().bindTexture(locationSnowPng);
								worldrenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
							}

							double d8 = ((this.rendererUpdateCount & 0x1FF) + partialTicks) / 512.0F;
							double d9 = world.rand.nextDouble() + f1 * 0.01D * (float)world.rand.nextGaussian();
							double d10 = world.rand.nextDouble() + f1 * (float)world.rand.nextGaussian() * 0.001D;
							double d11 = x + 0.5F - entity.posX;
							double d12 = z + 0.5F - entity.posZ;
							float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / precipDensity;
							float precipAlpha = ((1.0F - f6 * f6) * 0.3F + 0.5F) * (float)rainStrength;
							mPos.setPos(x, i3, z);
							int i4 = (world.getCombinedLight(mPos, 0) * 3 + 15728880) / 4;
							int j4 = (i4 >> 16 & 0xFFFF);
							int k4 = (i4 & 0xFFFF);
							worldrenderer.pos(x - d3 + 0.5D, k2, z - d4 + 0.5D).tex(0.0D + d9, k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(j4, k4).endVertex();
							worldrenderer.pos(x + d3 + 0.5D, k2, z + d4 + 0.5D).tex(1.0D + d9, k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(j4, k4).endVertex();
							worldrenderer.pos(x + d3 + 0.5D, l2, z + d4 + 0.5D).tex(1.0D + d9, l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(j4, k4).endVertex();
							worldrenderer.pos(x - d3 + 0.5D, l2, z - d4 + 0.5D).tex(0.0D + d9, l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, precipAlpha).lightmap(j4, k4).endVertex();
						}
					}
				}
			}

			if (j1 >= 0)
			{
				tessellator.draw();
			}

			worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			mc.entityRenderer.disableLightmap();
		}
	}

	public static void addRainParticles(Random random, int rendererUpdateCount)
	{
		Minecraft mc = Minecraft.getMinecraft();
		WorldClient worldclient = mc.world;
		if(worldclient.provider.getDimension() != 0)
			return;
		float rainStrength = (float)WeatherManager.getInstance().getPrecipitation((int)mc.player.posX, (int)mc.player.posZ);
		double tempPlayer = WeatherManager.getInstance().getTemperature((int)mc.player.posX,(int)mc.player.posY, (int)mc.player.posZ);
		if(tempPlayer <= 0)
			return;

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
			int rainParticles = Math.max((int)(100.0F * rainStrength * rainStrength), 4);

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
				IBlockState state = worldclient.getBlockState(blockpos2);
				Block block = worldclient.getBlockState(blockpos2).getBlock();

				if (blockPos1.getY() <= blockpos.getY() + b0 && blockPos1.getY() >= blockpos.getY() - b0 && temp > 0)
				{
					float f1 = worldclient.rand.nextFloat();
					float f2 = worldclient.rand.nextFloat();
					AxisAlignedBB axisalignedbb = state.getBoundingBox(worldclient, blockpos2);

					if (block.getMaterial(state) == Material.LAVA)
					{
						mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)blockPos1.getX() + f1), (double)((float)blockPos1.getY() + 0.1F) - axisalignedbb.minY, (double)((float)blockPos1.getZ() + f2), 0.0D, 0.0D, 0.0D, new int[0]);
					}
					else if (block.getMaterial(state) != Material.AIR)
					{
						//block.setBlockBoundsBasedOnState(worldclient, blockpos2);
						++i;

						if (worldclient.rand.nextInt(i) == 0)
						{
							d0 = (double)((float)blockpos2.getX() + f1);
							d1 = (double)((float)blockpos2.getY() + 0.1F) + axisalignedbb.maxY - 1.0D;
							d2 = (double)((float)blockpos2.getZ() + f2);
						}

						mc.world.spawnParticle(EnumParticleTypes.WATER_DROP, (double)((float)blockpos2.getX() + f1), (double)((float)blockpos2.getY() + 0.1F) + axisalignedbb.maxY, (double)((float)blockpos2.getZ() + f2), 0.0D, 0.0D, 0.0D, new int[0]);
					}
				}
			}

			if (i > 0 && worldclient.rand.nextInt(3) < rainSoundCounter++)
			{
				rainSoundCounter = 0;

				if (d1 > (double)(blockpos.getY() + 1) && worldclient.getPrecipitationHeight(blockpos).getY() > MathHelper.floor((float)blockpos.getY()))
				{
					mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F*rainStrength, 0.5F, false);
				}
				else
				{
					mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F*rainStrength, 1.0F, false);
				}
			}
		}
	}

}
