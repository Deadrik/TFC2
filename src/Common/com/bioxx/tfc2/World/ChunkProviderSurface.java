package com.bioxx.tfc2.World;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.bioxx.tfc2.World.TerrainTypes.TerrainType;

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
						TerrainType t = getTerrain(x, z);
						if(chunkprimer.getBlockState(x, y+1, z) == Blocks.air.getDefaultState() || chunkprimer.getBlockState(x, y+1, z) == Blocks.water.getDefaultState())
						{
							if((t == TerrainType.Ocean || t == TerrainType.Beach) && y <= 35)
							{
								chunkprimer.setBlockState(x, y, z, Blocks.sand.getDefaultState());
								chunkprimer.setBlockState(x, y-1, z, Blocks.sand.getDefaultState());
								chunkprimer.setBlockState(x, y-2, z, Blocks.sand.getDefaultState());
							}
							else
							{
								chunkprimer.setBlockState(x, y, z, Blocks.grass.getDefaultState());
								chunkprimer.setBlockState(x, y-1, z, Blocks.dirt.getDefaultState());
								chunkprimer.setBlockState(x, y-2, z, Blocks.dirt.getDefaultState());
							}
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
						//chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
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
		double[] nsHeight = new double[48*48];
		double[] outHeight = new double[256];

		for (int xOffset = 0; xOffset < 48; ++xOffset)
		{
			for (int zOffset = 0; zOffset < 48; ++zOffset)
			{
				TerrainType t = terrainDataWide[(xOffset)+48*(zOffset)];
				nsHeight[(xOffset)+48*(zOffset)] = t.minHeight + (t.maxHeight-t.minHeight)*t.getHeightPlane().GetValue((chunkX << 4)-16+xOffset, (chunkZ << 4)-16+zOffset);
			}
		}
		for (int xOffset = 0; xOffset < 16; ++xOffset)
		{
			for (int zOffset = 0; zOffset < 16; ++zOffset)
			{
				TerrainType base = this.terrainData[xOffset+16*zOffset];
				double n = base.minHeight;//getValue((chunkX<<4)+xOffset, (chunkZ<<4)+zOffset);
				int maxH = base.maxHeight;
				int minH = base.minHeight;
				double diff = 0;
				int radius = 16;
				int count = 1;
				/*if(base != TerrainType.Beach)*/
				{

					for (int xR = -radius; xR <= radius; ++xR)
					{
						for (int zR = -radius; zR <= radius; ++zR)
						{
							TerrainType blend = terrainDataWide[(16+xOffset+(xR))+48*(16+zOffset+(zR))];
							double ns = nsHeight[(16+xOffset+(xR))+48*(16+zOffset+(zR))];

							n = (n + ns);
							count++;
						}
					}
				}
				n/= count;
				outHeight[xOffset+16*zOffset] = n;
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

	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
	{

	}
}
