package com.bioxx.tfc2.World;

import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;

import com.bioxx.tfc2.World.Biome.TerrainType;
import com.bioxx.tfc2.World.Layers.GenLayerTFC;

public class ChunkManager extends WorldChunkManager 
{
	GenLayerTFC genTerrain;
	World world;
	public ChunkManager(long seed, WorldType type, String options)
	{
		super(seed, type, options);
		GenLayerTFC[] agenlayer = GenLayerTFC.initialize(seed, type, options);
		this.genTerrain = agenlayer[0];
		//this.biomeIndexLayer = agenlayer[1];
	}
	public ChunkManager(World worldIn)
	{
		this(worldIn.getSeed(), worldIn.getWorldInfo().getTerrainType(), worldIn.getWorldInfo().getGeneratorOptions());
		world = worldIn;
	}

	public TerrainType[] getTerrainData(int worldX, int worldZ, int rangeX, int rangeZ)
	{
		int[] ids = genTerrain.getInts(worldX, worldZ, rangeX, rangeZ);
		TerrainType[] biomeArray = new TerrainType[rangeX*rangeZ];
		for(int i = 0; i < ids.length; i++)
		{
			biomeArray[i] = TerrainType.getTerrain(ids[i]);
		}
		return biomeArray;
	}

	public TerrainType getBiomeAt(int x, int z)
	{
		int localX = x & 15;
		int localZ = z & 15;
		int chunkX = x >> 4;
		int chunkZ = z >> 4;
		TerrainType[] biomeArray = ((ChunkManager)world.getWorldChunkManager()).getTerrainData(chunkX << 4, chunkZ << 4, 16, 16);
		//PrintImageMapCommand.drawChunkBiomeImage(x, z, world, "testBiome");
		return biomeArray[localZ + localX*16];
	}
}
