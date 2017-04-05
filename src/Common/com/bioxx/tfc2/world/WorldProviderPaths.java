package com.bioxx.tfc2.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.storage.WorldInfo;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WorldProviderPaths extends WorldProvider 
{

	@Override
	public IChunkGenerator createChunkGenerator()
	{
		return new ChunkProviderPaths(world, world.getSeed(), false, "");
	}

	@Override
	public boolean isSurfaceWorld()
	{
		return false;
	}

	@Override
	public boolean canBlockFreeze(BlockPos pos, boolean byWater)
	{
		return false;
	}

	@Override
	public boolean canSnowAt(BlockPos pos, boolean checkLight)
	{
		return false;
	}

	@Override
	public double getHorizon()
	{
		return 1;
	}

	@Override
	public Biome getBiomeForCoords(BlockPos pos)
	{
		return Biome.getBiome(1);
	}

	@Override
	public boolean doesWaterVaporize()
	{
		return true;
	}

	@Override
	public boolean hasNoSky()
	{
		return true;
	}

	@Override
	public boolean canDoLightning(net.minecraft.world.chunk.Chunk chunk)
	{
		return false;
	}

	@Override
	public boolean canDoRainSnowIce(net.minecraft.world.chunk.Chunk chunk)
	{
		return false;
	}

	@Override
	public boolean canRespawnHere()
	{
		return false;
	}

	/**
	 * Returns a double value representing the Y value relative to the top of the map at which void fog is at its
	 * maximum. The default factor of 0.03125 relative to 256, for example, means the void fog will be at its maximum at
	 * (256*0.03125), or 8.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public double getVoidFogYFactor()
	{
		return 1.0;
	}

	/**
	 * Returns true if the given X,Z coordinate should show environmental fog.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public boolean doesXZShowFog(int x, int z)
	{
		return true;
	}

	@Override
	public double getMovementFactor()
	{
		return 8.0;
	}

	@Override
	public float calculateCelestialAngle(long p_76563_1_, float p_76563_3_)
	{
		return 0.5F;
	}

	@Override
	public BlockPos getSpawnPoint()
	{
		WorldInfo info = this.world.getWorldInfo();
		return new BlockPos(info.getSpawnX(), info.getSpawnY(), info.getSpawnZ());
	}

	/**
	 * Return Vec3D with biome specific fog color
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public Vec3d getFogColor(float p_76562_1_, float p_76562_2_)
	{
		return new Vec3d(0.02, 0.029, 0.029);
	}

	/**
	 * Creates the light to brightness table
	 */
	@Override
	protected void generateLightBrightnessTable()
	{
		float f = 0.1F;

		for (int i = 0; i <= 15; ++i)
		{
			float f1 = 1.0F - (float)i / 15.0F;
			this.lightBrightnessTable[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * (1.0F - f) + f;
		}
	}

	@Override
	public DimensionType getDimensionType() 
	{
		return DimensionTFC.PATHS;
	}
}
