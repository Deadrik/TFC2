package com.bioxx.tfc2.world;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkProviderOverworld;

import com.bioxx.jmapgen.IslandMap;

public class ChunkProviderPaths extends ChunkProviderOverworld 
{
	private World worldObj;
	private Random rand;
	private static final int MAP_SIZE = 4096;
	int worldX;//This is the x coordinate of the chunk using world coords.
	int worldZ;//This is the z coordinate of the chunk using world coords.
	int islandChunkX;//This is the x coordinate of the chunk within the bounds of the island (0 - MAP_SIZE)
	int islandChunkZ;//This is the z coordinate of the chunk within the bounds of the island (0 - MAP_SIZE)
	int mapX;//This is the x coordinate of the chunk using world coords.
	int mapZ;//This is the z coordinate of the chunk using world coords.

	IslandMap islandMap;

	public ChunkProviderPaths(World worldIn, long seed, boolean enableMapFeatures, String rules) 
	{
		super(worldIn, seed, enableMapFeatures, rules);
		worldObj = worldIn;
		rand = worldObj.rand;
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		worldX = chunkX * 16;
		worldZ = chunkZ * 16;
		islandChunkX = worldX % MAP_SIZE;
		islandChunkZ = worldZ % MAP_SIZE;
		mapX = (chunkX >> 8);
		mapZ = (chunkZ >> 8);
		islandMap = WorldGen.instance.getIslandMap(((chunkX*16/8) >> 12), ((chunkZ*16/8) >> 12));


		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		generateTerrain(chunkprimer);
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		chunk.generateSkylightMap();
		return chunk;  
	}

	public void generateTerrain(ChunkPrimer primer)
	{
		for (int x = 0; x < 16; x++)
		{
			for (int z = 0; z < 16; z++)
			{
				//primer.setBlockState(x, 0, z, TFCBlocks.StoneSmooth.getDefaultState().withProperty(BlockStoneSmooth.META_PROPERTY, StoneType.Marble));
			}
		}
	}
}
