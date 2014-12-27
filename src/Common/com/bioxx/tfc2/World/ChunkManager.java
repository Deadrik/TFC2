package com.bioxx.tfc2.World;

import java.util.List;
import java.util.Random;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.IntCache;

import com.bioxx.tfc2.World.Layers.GenLayerTFC;
import com.bioxx.tfc2.World.TerrainTypes.TerrainType;

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

	public BlockPos findTerrainPosition(int x, int z, int range, List terrain, Random random)
	{
		IntCache.resetIntCache();
		int l = x - range >> 2;
		int i1 = z - range >> 2;
		int j1 = x + range >> 2;
		int k1 = z + range >> 2;
		int l1 = j1 - l + 1;
		int i2 = k1 - i1 + 1;
		int[] aint = this.genTerrain.getInts(l, i1, l1, i2);
		BlockPos blockpos = null;
		int j2 = 0;

		for (int k2 = 0; k2 < l1 * i2; ++k2)
		{
			int l2 = l + k2 % l1 << 2;
			int i3 = i1 + k2 / l1 << 2;
			TerrainType t = TerrainType.getTerrain(aint[k2]);

			if (terrain.contains(t) && (blockpos == null || random.nextInt(j2 + 1) == 0))
			{
				blockpos = new BlockPos(l2, 0, i3);
				++j2;
			}
		}

		return blockpos;
	}

	public BlockPos findTerrainPositionForSpawn(int z, int rangeX, int rangeZ, List terrain, Random random)
	{
		IntCache.resetIntCache();
		int xMinRange = 0 - rangeX >> 2;
			int zMinRange = z - rangeZ >> 2;
			int xMaxRange = 0 + rangeX >> 2;
		int zMaxRange = z + rangeZ >> 2;
		int sizeX = xMaxRange - xMinRange + 1;
		int sizeZ = zMaxRange - zMinRange + 1;
		int[] aint = this.genTerrain.getInts(xMinRange, zMinRange, sizeX, sizeZ);
		BlockPos blockpos = null;
		int j2 = 0;

		for (int k2 = 0; k2 < sizeX * sizeZ; ++k2)
		{
			int l2 = xMinRange + k2 % sizeX << 2;
			int i3 = zMinRange + k2 / sizeZ << 2;
			TerrainType t = TerrainType.getTerrain(aint[k2]);

			if (terrain.contains(t) && (blockpos == null || random.nextInt(j2 + 1) == 0))
			{
				blockpos = new BlockPos(l2, 0, i3);
				++j2;
			}
		}

		return blockpos;
	}
}
