package com.bioxx.tfc2;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.api.WeatherManager;

@SideOnly(Side.CLIENT)
public class ClientOverrides 
{
	private static int rainSoundCounter;

	public static void addRainParticles(Random random, int rendererUpdateCount)
	{
		Minecraft mc = Minecraft.getMinecraft();
		WorldClient worldclient = mc.theWorld;
		double rainStrength = WeatherManager.getInstance().getPreciptitation((int)mc.thePlayer.posX, (int)mc.thePlayer.posZ);

		if (!mc.gameSettings.fancyGraphics)
		{
			rainStrength /= 2.0F;
		}

		if (rainStrength > 0.0F)
		{
			random.setSeed((long)rendererUpdateCount * 312987231L);
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
				BlockPos blockpos1 = worldclient.getPrecipitationHeight(blockpos.add(random.nextInt(b0) - random.nextInt(b0), 0, random.nextInt(b0) - random.nextInt(b0)));
				BiomeGenBase biomegenbase = worldclient.getBiomeGenForCoords(blockpos1);
				BlockPos blockpos2 = blockpos1.down();
				Block block = worldclient.getBlockState(blockpos2).getBlock();

				if (blockpos1.getY() <= blockpos.getY() + b0 && blockpos1.getY() >= blockpos.getY() - b0 && biomegenbase.canSpawnLightningBolt() && biomegenbase.getFloatTemperature(blockpos1) >= 0.15F)
				{
					float f1 = random.nextFloat();
					float f2 = random.nextFloat();

					if (block.getMaterial() == Material.lava)
					{
						mc.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)blockpos1.getX() + f1), (double)((float)blockpos1.getY() + 0.1F) - block.getBlockBoundsMinY(), (double)((float)blockpos1.getZ() + f2), 0.0D, 0.0D, 0.0D, new int[0]);
					}
					else if (block.getMaterial() != Material.air)
					{
						block.setBlockBoundsBasedOnState(worldclient, blockpos2);
						++i;

						if (random.nextInt(i) == 0)
						{
							d0 = (double)((float)blockpos2.getX() + f1);
							d1 = (double)((float)blockpos2.getY() + 0.1F) + block.getBlockBoundsMaxY() - 1.0D;
							d2 = (double)((float)blockpos2.getZ() + f2);
						}

						mc.theWorld.spawnParticle(EnumParticleTypes.WATER_DROP, (double)((float)blockpos2.getX() + f1), (double)((float)blockpos2.getY() + 0.1F) + block.getBlockBoundsMaxY(), (double)((float)blockpos2.getZ() + f2), 0.0D, 0.0D, 0.0D, new int[0]);
					}
				}
			}

			if (i > 0 && random.nextInt(3) < rainSoundCounter++)
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
