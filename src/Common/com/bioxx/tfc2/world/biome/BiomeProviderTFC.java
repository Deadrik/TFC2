package com.bioxx.tfc2.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProviderSingle;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.world.WorldGen;

public class BiomeProviderTFC extends BiomeProviderSingle 
{

	public BiomeProviderTFC() 
	{
		super(Biome.getBiome(1));
	}

	@Override
	public Biome getBiome(BlockPos pos)
	{
		IslandMap map = WorldGen.getInstance().getIslandMap(pos);
		return Biome.getBiome(1);
	}

}
