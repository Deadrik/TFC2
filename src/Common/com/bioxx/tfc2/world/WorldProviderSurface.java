package com.bioxx.tfc2.world;

import java.util.Iterator;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.IChunkGenerator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.rendering.SkyRenderer;
import com.bioxx.tfc2.rendering.WeatherRenderer;
import com.bioxx.tfc2.world.biome.BiomeProviderTFC;

public class WorldProviderSurface extends WorldProvider 
{
	WeatherRenderer weatherRenderer = new WeatherRenderer();
	SkyRenderer skyRenderer = new SkyRenderer();

	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraftforge.client.IRenderHandler getWeatherRenderer()
	{
		return weatherRenderer;
	}

	@Override
	protected void init()
	{
		this.hasSkyLight = true;
		biomeProvider = new BiomeProviderTFC();
		WorldGen.initialize(world);
		WeatherManager.setupWeather(world);
	}

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderSurface(this.world, world.getSeed(), false, "");
	}

	@Override
	public boolean canCoordinateBeSpawn(int x, int z)
	{
		BlockPos pos = new BlockPos(x, 0, z);
		Block b = Core.getGroundAboveSeaLevel(this.world, pos);
		return b == TFCBlocks.Sand && world.isAirBlock(pos.up()) && world.isAirBlock(pos.up(2));
	}

	@Override
	public boolean canBlockFreeze(BlockPos pos, boolean byWater)
	{
		int x = pos.getX() << 12;
		int z = pos.getZ() << 12;

		if(WorldGen.getInstance() == null)
			return false;

		/*Map m = WorldGen.getInstance().getIslandMap(x, z);

		if(m.islandParams.getIslandTemp() == ClimateTemp.SUBTROPICAL || m.islandParams.getIslandTemp() == ClimateTemp.TROPICAL)
			return false;*/

		return false;
	}

	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight)
	{
		int x = pos.getX() << 12;
		int z = pos.getZ() << 12;

		if(WorldGen.getInstance() == null)
			return false;

		/*Map m = WorldGen.getInstance().getIslandMap(x, z);

		if(m.islandParams.getIslandTemp() == ClimateTemp.SUBTROPICAL || m.islandParams.getIslandTemp() == ClimateTemp.TROPICAL)
			return false;*/

		return world.canSnowAtBody(pos, checkLight);
	}

	@Override
	public double getHorizon()
	{
		return 64D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public float getCloudHeight()
	{
		return 258;
	}

	/*@Override
	public Biome getBiomeForCoords(BlockPos pos)
	{
		//TODO make this read the islandmap and output relevant biome types
		return Biome.getBiome(0);
	}*/

	@Override
	public void updateWeather()
	{
		if (!hasNoSky())
		{
			if (!world.isRemote && world.provider.getDimension() == 0)
			{
				world.getWorldInfo().setRainTime(48000);
				world.getWorldInfo().setRaining(false);
				int i = world.getWorldInfo().getCleanWeatherTime();

				if (i > 0)
				{
					--i;
					world.getWorldInfo().setCleanWeatherTime(i);
					world.getWorldInfo().setThunderTime(world.getWorldInfo().isThundering() ? 1 : 2);
					world.getWorldInfo().setRainTime(world.getWorldInfo().isRaining() ? 1 : 2);
				}
				int j = world.getWorldInfo().getThunderTime();

				if (j <= 0)
				{
					if (world.getWorldInfo().isThundering())
					{
						world.getWorldInfo().setThunderTime(world.rand.nextInt(12000) + 3600);
					}
					else
					{
						world.getWorldInfo().setThunderTime(world.rand.nextInt(168000) + 12000);
					}
				}
				else
				{
					--j;
					world.getWorldInfo().setThunderTime(j);

					if (j <= 0)
					{
						world.getWorldInfo().setThundering(!world.getWorldInfo().isThundering());
					}
				}

				world.prevThunderingStrength = world.thunderingStrength;

				/*if (worldObj.getWorldInfo().isThundering())
				{
					worldObj.thunderingStrength = (float)((double)worldObj.thunderingStrength + 0.01D);
				}
				else
				{
					worldObj.thunderingStrength = (float)((double)worldObj.thunderingStrength - 0.01D);
				}

				worldObj.thunderingStrength = MathHelper.clamp_float(worldObj.thunderingStrength, 0.0F, 1.0F);*/


				world.prevRainingStrength = world.rainingStrength;

				Iterator iterator = world.playerEntities.iterator();

				//TODO: Add thunder support

				while (iterator.hasNext())
				{
					EntityPlayerMP player = (EntityPlayerMP) iterator.next();
					if(!player.isDead && player.dimension == 0)
					{
						float old = player.getEntityData().getFloat("oldPrecipitation");
						float precip = (float)WeatherManager.getInstance().getPrecipitation((int)player.posX, (int)player.posZ);
						if(precip != old)
						{
							player.connection.sendPacket(new SPacketChangeGameState(7, precip));
						}
					}
				}

			}
		}
	}

	@Override
	public DimensionType getDimensionType() {
		return DimensionTFC.SURFACE;
	}
}
