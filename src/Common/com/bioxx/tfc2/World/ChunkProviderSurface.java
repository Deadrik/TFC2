package com.bioxx.tfc2.World;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.model.Plane;
import net.royawesome.jlibnoise.module.Cache;
import net.royawesome.jlibnoise.module.modifier.Clamp;
import net.royawesome.jlibnoise.module.modifier.ScaleBias;
import net.royawesome.jlibnoise.module.source.Perlin;

import com.bioxx.tfc2.World.Biome.TerrainType;

public class ChunkProviderSurface extends ChunkProviderGenerate 
{
	private BiomeGenBase[] biomesForGeneration;
	private TerrainType[] biomeData;
	private World worldObj;
	private Random rand;

	static Perlin n0 = new Perlin();
	static Clamp c0 = new Clamp();
	static Cache noise = new Cache();
	static Plane heightmap;
	static Plane heightmap2;
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
		if(heightmap == null)
		{
			Perlin pe = new Perlin();
			pe.setSeed (0);
			pe.setFrequency (0.03125);
			pe.setPersistence (0.1);
			pe.setLacunarity (2.314);
			pe.setOctaveCount (6);
			pe.setNoiseQuality (NoiseQuality.BEST);

			ScaleBias sb = new ScaleBias();
			sb.setSourceModule(0, pe);
			sb.setScale(0.5);

			Cache c0 = new Cache();
			c0.setSourceModule(0, sb);

			heightmap = new Plane(c0);
		}

		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		//this.func_180518_a(p_73154_1_, p_73154_2_, chunkprimer);
		//this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, chunkX * 16, chunkZ * 16, 16, 16);
		//this.func_180517_a(p_73154_1_, p_73154_2_, chunkprimer, this.biomesForGeneration);
		biomeData = ((ChunkManager)worldObj.getWorldChunkManager()).getBiomeData(chunkX << 4, chunkZ << 4, 16, 16);
		noiseMap = genTerrainHeight(chunkX, chunkZ);
		generateTerrain(chunkprimer);

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		chunk.generateSkylightMap();
		return chunk;
	}

	protected void generateTerrain(ChunkPrimer chunkprimer)
	{
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				for(int y = 255; y >= 0; y--)
				{
					if(y == getHeight(x,z) && y >= 32)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.grass.getDefaultState());
					}
					else if(y <= getHeight(x,z))
					{
						chunkprimer.setBlockState(x, y, z, Blocks.dirt.getDefaultState());
					}
					if(y <= 32 && chunkprimer.getBlockState(x, y, z).getBlock() == Blocks.air)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
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
		int x = chunkX << 2;
		int z = chunkZ << 2;
		int width = 4;

		double[] noise = new double[(width+2)*(width+2)];

		for (int xOffset = 0; xOffset < width+2; ++xOffset)
		{
			for (int zOffset = 0; zOffset < width+2; ++zOffset)
			{
				noise[xOffset*(width+2)+zOffset] = getValue(x+xOffset, z+zOffset);
			}
		}

		double[] outHeight = new double[256];
		for (int xOffset = 0; xOffset < 16; ++xOffset)
		{
			for (int zOffset = 0; zOffset < 16; ++zOffset)
			{
				TerrainType b = this.biomeData[xOffset+16*zOffset];
				//double n = noise[((xOffset>>2)+1)*(width+2)+((zOffset>>2)+1)];
				double n = getValue((chunkX<<4)+xOffset, (chunkZ<<4)+zOffset);
				double diff = b.maxHeight-b.minHeight;
				outHeight[xOffset+16*zOffset] = b.minHeight + diff*n;
			}
		}


		return outHeight;
	}


	/**
	 * Needs work on smoothing transitions
	 */
	protected double getValue(int x, int z)
	{	
		TerrainType b = this.biomeData[(x&15)+16*(z&15)];
		return b.getHeightPlane().GetValue(x, z);

	}
}
