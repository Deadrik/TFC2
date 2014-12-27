package com.bioxx.tfc2.World;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.bioxx.tfc2.World.Biome.TerrainType;

public class ChunkProviderSurface extends ChunkProviderGenerate 
{
	private BiomeGenBase[] biomesForGeneration;
	private TerrainType[] terrainData;
	private World worldObj;
	private Random rand;
	double[] noiseMap;

	public ChunkProviderSurface(World worldIn, long seed, boolean enableMapFeatures, String rules) 
	{
		super(worldIn, seed, enableMapFeatures, rules);
		worldObj = worldIn;
		rand = worldObj.rand;
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		//this.func_180518_a(p_73154_1_, p_73154_2_, chunkprimer);
		//this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		//this.func_180517_a(p_73154_1_, p_73154_2_, chunkprimer, this.biomesForGeneration);
		terrainData = ((ChunkManager)worldObj.getWorldChunkManager()).getTerrainData(chunkX << 4, chunkZ << 4, 16, 16);
		noiseMap = genTerrainHeight(chunkX, chunkZ);
		generateTerrain(chunkprimer);
		decorate(chunkprimer);
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		chunk.generateSkylightMap();
		return chunk;
	}

	protected void decorate(ChunkPrimer chunkprimer)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				for(int y = 255; y >= 0; y--)
				{
					if(chunkprimer.getBlockState(x, y, z) == Blocks.stone.getDefaultState())
					{
						if(chunkprimer.getBlockState(x, y+1, z) == Blocks.air.getDefaultState())
						{
							chunkprimer.setBlockState(x, y, z, Blocks.grass.getDefaultState());
						}

						if(getTerrain(x, z) == TerrainType.Ocean && y <= 34)
						{
							chunkprimer.setBlockState(x, y, z, Blocks.sand.getDefaultState());
						}
					}
				}
			}
		}
	}

	protected void generateTerrain(ChunkPrimer chunkprimer)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				for(int y = 255; y >= 0; y--)
				{
					if(y <= getHeight(x,z))
					{
						chunkprimer.setBlockState(x, y, z, Blocks.stone.getDefaultState());
					}
					if(y <= 32 && chunkprimer.getBlockState(x, y, z).getBlock() == Blocks.air)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}
					if(y == 0)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.bedrock.getDefaultState());
					}
				}
			}
		}
	}

	protected double getHeight(int x, int z)
	{
		return noiseMap[x+16*z];
	}

	protected double[] genTerrainHeight(int chunkX, int chunkZ)
	{
		//First we get a 3x3 chunk area of terrain info
		TerrainType[] terrainDataWide = ((ChunkManager)worldObj.getWorldChunkManager()).getTerrainData(chunkX-1 << 4, chunkZ-1 << 4, 48, 48);

		//Set the terrainData map for final use
		terrainData = ((ChunkManager)worldObj.getWorldChunkManager()).getTerrainData(chunkX << 4, chunkZ << 4, 16, 16);
		double[] outHeight = new double[256];
		for (int xOffset = 0; xOffset < 16; ++xOffset)
		{
			for (int zOffset = 0; zOffset < 16; ++zOffset)
			{
				TerrainType b = this.terrainData[xOffset+16*zOffset];
				double n = getValue((chunkX<<4)+xOffset, (chunkZ<<4)+zOffset);
				int maxH = b.maxHeight;
				int minH = b.minHeight;

				int avgMax = 0;
				int avgMin = 0;
				int avgCount = 0;
				for (int xR = -15; xR < 16; ++xR)
				{
					for (int zR = -15; zR < 16; ++zR)
					{
						avgCount++;
						TerrainType blend = terrainDataWide[(16+xOffset+xR)+48*(16+zOffset+zR)];
						avgMax+=blend.maxHeight;
						avgMin+=blend.minHeight;
					}
				}
				maxH = avgMax/avgCount;
				minH = avgMin/avgCount;
				double diff = maxH-minH;
				outHeight[xOffset+16*zOffset] = minH + diff*n;
			}
		}


		return outHeight;
	}


	protected double getValue(int x, int z)
	{	
		TerrainType b = this.terrainData[(x&15)+16*(z&15)];
		return b.getHeightPlane().GetValue(x, z);

	}

	protected TerrainType getTerrain(int x, int z)
	{	
		return this.terrainData[(x&15)+16*(z&15)];
	}
}
