package com.bioxx.tfc2.World;

import jMapGen.Point;
import jMapGen.graph.Center;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.bioxx.libnoise.model.Line;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.Cache;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;

public class ChunkProviderSurface2 extends ChunkProviderGenerate 
{
	private World worldObj;
	private Random rand;
	private static final int MAP_SIZE = 4096;
	int worldX;//This is the x coordinate of the chunk using world coords.
	int worldZ;//This is the z coordinate of the chunk using world coords.
	int islandX;//This is the x coordinate of the chunk within the bounds of the island (0 - MAP_SIZE)
	int islandZ;//This is the z coordinate of the chunk within the bounds of the island (0 - MAP_SIZE)
	int mapX;//This is the x coordinate of the chunk using world coords.
	int mapZ;//This is the z coordinate of the chunk using world coords.

	Plane turbMap;
	Line edgeTurbulenceMap;

	/**
	 * Cache for Hex lookup.
	 */
	private Center[][] centerCache;
	/**
	 * A static array of sample points for performing our hex smoothing
	 */
	private static Point[] hexSmoothingPoints;

	public ChunkProviderSurface2(World worldIn, long seed, boolean enableMapFeatures, String rules) 
	{
		super(worldIn, seed, enableMapFeatures, rules);
		worldObj = worldIn;
		rand = worldObj.rand;

		double c = 5;
		double a = 0.5*c;
		double b = Math.sin(60)*c;
		hexSmoothingPoints = new Point[6];
		hexSmoothingPoints[0] = new Point(0, b);
		hexSmoothingPoints[1] = new Point(a, 0);
		hexSmoothingPoints[2] = new Point(a+c, 0);
		hexSmoothingPoints[3] = new Point(2*c, b);
		hexSmoothingPoints[4] = new Point(a+c, 2*b);
		hexSmoothingPoints[5] = new Point(a, 2*b);

		Perlin pe = new Perlin();
		pe.setSeed (seed);
		pe.setFrequency (1f/256f);
		pe.setPersistence(.9);
		pe.setLacunarity(1.5);
		pe.setOctaveCount(2);
		pe.setNoiseQuality (com.bioxx.libnoise.NoiseQuality.BEST);
		//The scalebias makes our noise fit the range 0-1
		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, pe);
		//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
		sb2.setScale(0.25);
		//Next we offset by +0.5 which makes the noise 0-1
		sb2.setBias(0.5);
		turbMap = new Plane(sb2);


		Perlin pe2 = new Perlin();
		pe2.setSeed (seed);
		pe2.setFrequency (2f);
		//pe2.setPersistence(.5);
		//pe2.setLacunarity(1.5);
		pe2.setOctaveCount(8);
		pe2.setNoiseQuality (com.bioxx.libnoise.NoiseQuality.STANDARD);
		ScaleBias sb3 = new ScaleBias();
		sb3.setSourceModule(0, pe2);
		//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
		sb3.setScale(0.5);
		//Next we offset by +0.5 which makes the noise 0-1
		//sb3.setBias(0.5);
		Cache edgeCache = new Cache();
		edgeCache.setSourceModule(0, sb3);

		edgeTurbulenceMap = new Line(edgeCache);
		edgeTurbulenceMap.setAttenuate(true);
	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		centerCache = new Center[48][48];
		worldX = chunkX * 16;
		worldZ = chunkZ * 16;
		islandX = worldX % MAP_SIZE;
		islandZ = worldZ % MAP_SIZE;
		mapX = worldX >> 12;
		mapZ = worldZ >> 12;
		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		generateTerrain(chunkprimer, chunkX, chunkZ);
		decorate(chunkprimer, chunkX, chunkZ);
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		chunk.generateSkylightMap();
		return chunk;  
	}

	/**
	 * @param p this Point should always be using local chunk coordinates
	 * @return Returns the nearest Hex for this map
	 */
	protected Center getHex(Point p)
	{
		int x = (int)p.x;
		int y = (int)p.y;
		int x16 = (int)p.x + 16;
		int y16 = (int)p.y + 16;
		if(centerCache[x16][y16] == null)
		{
			centerCache[x16][y16] = WorldGen.instance.getIslandMap(mapX, mapZ).getSelectedHexagon(p.plus(islandX, islandZ));
		}
		return centerCache[x16][y16];
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
					IBlockState block = chunkprimer.getBlockState(x, y, z);
					IBlockState blockUp = chunkprimer.getBlockState(x, y+1, z);

					if(block == Blocks.stone.getDefaultState() && 
							blockUp == Blocks.air.getDefaultState() && closestCenter.water && !closestCenter.ocean)
					{
						if(!isLakeBorder(p, closestCenter) /*&& y < 128+closestCenter.elevation*100D*/)
						{
							chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
						}
					}

					if(block == Blocks.stone.getDefaultState() && 
							blockUp == Blocks.air.getDefaultState())
					{
						chunkprimer.setBlockState(x, y, z, Blocks.grass.getDefaultState());

						if(closestCenter.river > 0)
						{
							if(closestCenter.downriver != null)
							{
								edgeTurbulenceMap.setPoints(closestCenter.point, closestCenter.downriver.point);
								double[] dts = distToSegmentSquared(closestCenter.point, closestCenter.downriver.point, p.plus(islandX, islandZ));
								double dist = dts[0];
								double loc = dts[1];
								double turb = edgeTurbulenceMap.getValue(loc);
								if(dist < squared(Math.max(closestCenter.river, 1.0)))
								{
									chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
								}
							}
							if(closestCenter.upriver != null && closestCenter.upriver.size() > 0)
							{
								for(Iterator<Center> iter = closestCenter.upriver.iterator(); iter.hasNext();)
								{
									Center up = iter.next();
									edgeTurbulenceMap.setPoints(closestCenter.point, up.point);
									double[] dts = distToSegmentSquared(closestCenter.point, up.point, p.plus(islandX, islandZ));
									double dist = dts[0];
									double loc = dts[1];
									double turb = edgeTurbulenceMap.getValue(loc);
									if(dist < squared(Math.max(up.river, 1.0)))
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
				Point p2 = p.plus(islandX, islandZ);
				double turb = (turbMap.GetValue(p2.x, p2.y));
				for(int y = 255; y >= 0; y--)
				{
					if(!closestCenter.ocean && y < 128+getSmoothHeightHex(closestCenter, p)*100D)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.stone.getDefaultState());
					}
					else if(y < 100)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.stone.getDefaultState());
					}
					else if(closestCenter.ocean && y < 128)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}

					else if(y < 128)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}

					if(y <= 1)
						chunkprimer.setBlockState(x, y, z, Blocks.bedrock.getDefaultState());
				}
			}
		}
	}

	protected double getSmoothHeightHex(Center c, Point p)
	{
		double h = c.elevation;
		if((getHex(hexSmoothingPoints[0].plus(p)) != c || getHex(hexSmoothingPoints[1].plus(p)) != c || 
				getHex(hexSmoothingPoints[2].plus(p)) != c || getHex(hexSmoothingPoints[3].plus(p)) != c || 
				getHex(hexSmoothingPoints[4].plus(p)) != c || getHex(hexSmoothingPoints[5].plus(p)) != c))
		{
			for(int i = 0; i < 6; i++)
			{
				h += getHex(hexSmoothingPoints[i].plus(p)).elevation;
			}

			h /= 7;
		}
		return c.elevation - (c.elevation - h);
	}

	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
	{

	}
}
