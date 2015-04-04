package com.bioxx.tfc2.World;

import jMapGen.IslandMapGen;
import jMapGen.Point;
import jMapGen.graph.Center;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

public class ChunkProviderSurface2 extends ChunkProviderGenerate 
{
	private World worldObj;
	private Random rand;
	private static final int MAP_SIZE = 4096;
	private static IslandMapGen mapgen = new IslandMapGen(0 + 0 * 10000 + 0);
	private long SEED = 0;
	int worldX ;
	int worldZ;
	int islandX;
	int islandZ;

	private Center[][] CenterCache;

	public ChunkProviderSurface2(World worldIn, long seed, boolean enableMapFeatures, String rules) 
	{
		super(worldIn, seed, enableMapFeatures, rules);
		worldObj = worldIn;
		rand = worldObj.rand;
		SEED = seed;

	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		CenterCache = new Center[48][48];
		worldX = chunkX * 16;
		worldZ = chunkZ * 16;
		islandX = worldX % MAP_SIZE;
		islandZ = worldZ % MAP_SIZE;
		//mapgen = new IslandMapGen(this.SEED + islandX * 10000 + islandZ);
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		generateTerrain(chunkprimer, chunkX, chunkZ);
		decorate(chunkprimer, chunkX, chunkZ);
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		chunk.generateSkylightMap();
		return chunk;  
	}

	protected Center getHex(Point p)
	{
		int x = (int)p.x;
		int y = (int)p.y;
		int x16 = (int)p.x + 16;
		int y16 = (int)p.y + 16;
		/*if(CenterCache[x16][y16] == null)
		{
			//CenterCache[x16][y16] = mapgen.map.getClosestCenterHex(p.plus(islandX, islandZ));
			CenterCache[x16][y16] = mapgen.map.getSelectedHexagon2(p.plus(islandX, islandZ));
		}*/
		return mapgen.map.getSelectedHexagon(p.plus(islandX, islandZ));
	}

	protected void decorate(ChunkPrimer chunkprimer, int chunkX, int chunkZ)
	{
		Point p;
		Center closestCenter;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				p = new Point(x, z);
				closestCenter = this.getHex(p);
				for(int y = 255; y >= 0; y--)
				{
					if(chunkprimer.getBlockState(x, y, z) == Blocks.stone.getDefaultState() && 
							chunkprimer.getBlockState(x, y+1, z) == Blocks.air.getDefaultState() && closestCenter.water && !closestCenter.ocean)
					{
						if(!isLakeBorder(p, closestCenter))
						{
							chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
						}
					}

					if(chunkprimer.getBlockState(x, y, z) == Blocks.stone.getDefaultState() && 
							chunkprimer.getBlockState(x, y+1, z) == Blocks.air.getDefaultState())
					{
						chunkprimer.setBlockState(x, y, z, Blocks.grass.getDefaultState());
						if(closestCenter.river > 0)
						{
							if(closestCenter.downslope != null)
							{
								double d = distToSegmentSquared(closestCenter.point, closestCenter.downslope.point, p.plus(islandX, islandZ))[0];
								if(d < 25)//Sqrt = 5
								{
									chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
								}
							}
							if(closestCenter.upriver != null && closestCenter.upriver.size() > 0)
							{
								for(Iterator<Center> iter = closestCenter.upriver.iterator(); iter.hasNext();)
								{
									double d = distToSegmentSquared(closestCenter.point, iter.next().point, p.plus(islandX, islandZ))[0];
									if(d < 25)//Sqrt = 5
									{
										chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private double[] distToSegmentSquared(Point v, Point w, Point local) 
	{
		double l2 = dist2(v, w);    
		if (l2 == 0) return new double[] {dist2(local, v), -1};
		double t = ((local.x - v.x) * (w.x - v.x) + (local.y - v.y) * (w.y - v.y)) / l2;
		if (t < 0) return new double[] {dist2(local, v), 0};
		if (t > 1) return new double[] {dist2(local, w), 1};
		return new double[] {dist2(local, new Point( v.x + t * (w.x - v.x),  v.y + t * (w.y - v.y) )), t};
	}

	private double squared(double d) { return d * d; }

	private double dist2(Point v, Point w) { return squared(v.x - w.x) + squared(v.y - w.y); }

	protected boolean isLakeBorder(Point p, Center c)
	{
		Point pt = p.plus(0, 1);
		Center c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(0, -1);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(1, 0);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(-1, 1);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(-1, -1);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(1, 1);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(1, -1);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;

		return false;
	}

	protected void generateTerrain(ChunkPrimer chunkprimer, int chunkX, int chunkZ)
	{
		Point p;
		Center closestCenter;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				p = new Point(x, z);
				closestCenter = this.getHex(p);
				for(int y = 255; y >= 0; y--)
				{
					if(!closestCenter.ocean && y < 128+closestCenter.elevation*128D)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.stone.getDefaultState());
					}
					else if(closestCenter.ocean && y < 100)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.stone.getDefaultState());
					}
					else if(closestCenter.ocean && y < 128)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}
					if(y <= 1)
						chunkprimer.setBlockState(x, y, z, Blocks.bedrock.getDefaultState());
				}
			}
		}
	}

	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
	{

	}
}
