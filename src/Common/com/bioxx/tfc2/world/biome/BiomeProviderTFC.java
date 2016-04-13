package com.bioxx.tfc2.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeProviderSingle;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.tfc2.world.WorldGen;

public class BiomeProviderTFC extends BiomeProviderSingle 
{

	public BiomeProviderTFC() 
	{
		super(BiomeGenBase.getBiome(1));
	}

	@Override
	public BiomeGenBase getBiomeGenerator(BlockPos pos)
	{
		IslandMap map = WorldGen.getInstance().getIslandMap(pos);
		return BiomeGenBase.getBiome(1);
	}

}
