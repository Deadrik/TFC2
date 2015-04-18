package com.bioxx.tfc2.World;

import jMapGen.BiomeType;
import jMapGen.Map;
import jMapGen.Point;
import jMapGen.Spline2D;
import jMapGen.graph.Center;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;

public class ChunkProviderSurface2 extends ChunkProviderGenerate 
{
	private World worldObj;
	private Random rand;
	private static final int MAP_SIZE = 4096;
	private static final int SEA_LEVEL = 64;
	int worldX;//This is the x coordinate of the chunk using world coords.
	int worldZ;//This is the z coordinate of the chunk using world coords.
	int islandX;//This is the x coordinate of the chunk within the bounds of the island (0 - MAP_SIZE)
	int islandZ;//This is the z coordinate of the chunk within the bounds of the island (0 - MAP_SIZE)
	int mapX;//This is the x coordinate of the chunk using world coords.
	int mapZ;//This is the z coordinate of the chunk using world coords.

	Plane turbMap;
	Map islandMap;

	Vector<Center> centersInChunk;
	int[][] elevationMap;


	/**
	 * Cache for Hex lookup.
	 */
	private Center[][] centerCache;
	/**
	 * A static array of sample points for performing our hex smoothing
	 */
	private static Point[] hexSamplePoints;

	public ChunkProviderSurface2(World worldIn, long seed, boolean enableMapFeatures, String rules) 
	{
		super(worldIn, seed, enableMapFeatures, rules);
		worldObj = worldIn;
		rand = worldObj.rand;

		//Setup the sampling hexagon for hex smoothing
		double c = 5;
		double a = 0.5*c;
		double b = Math.sin(60)*c;
		hexSamplePoints = new Point[6];
		hexSamplePoints[0] = new Point(0, b);
		hexSamplePoints[1] = new Point(a, 0);
		hexSamplePoints[2] = new Point(a+c, 0);
		hexSamplePoints[3] = new Point(2*c, b);
		hexSamplePoints[4] = new Point(a+c, 2*b);
		hexSamplePoints[5] = new Point(a, 2*b);

		/**
		 * Setup our turbulence Module
		 */
		Perlin pe = new Perlin();
		pe.setSeed (seed);
		pe.setFrequency (1f/16f);
		pe.setLacunarity(1.5);
		pe.setOctaveCount(4);
		pe.setNoiseQuality (com.bioxx.libnoise.NoiseQuality.BEST);

		//The scalebias makes our noise fit the range 0-1
		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, pe);
		//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
		sb2.setScale(0.25);
		//Next we offset by +0.5 which makes the noise 0-1
		sb2.setBias(0.5);

		turbMap = new Plane(sb2);


	}

	@Override
	public Chunk provideChunk(int chunkX, int chunkZ)
	{
		centerCache = new Center[48][48];
		elevationMap = new int[16][16];
		worldX = chunkX * 16;
		worldZ = chunkZ * 16;
		islandX = worldX % MAP_SIZE;
		islandZ = worldZ % MAP_SIZE;
		mapX = worldX >> 12;
		mapZ = worldZ >> 12;
		islandMap = WorldGen.instance.getIslandMap(mapX, mapZ);
		centersInChunk = new Vector<Center>();

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
			centerCache[x16][y16] = islandMap.getSelectedHexagon(p.plus(islandX, islandZ));
		}

		if(!centersInChunk.contains(centerCache[x16][y16]))
		{
			centersInChunk.add(centerCache[x16][y16]);
			centersInChunk.addAll(centerCache[x16][y16].neighbors);
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
				int elev = getElevation(closestCenter, p);
				int hexElev = this.getHexElevation(closestCenter, p);
				for(int y = elev; y >= 0; y--)
				{
					IBlockState block = chunkprimer.getBlockState(x, y, z);
					IBlockState blockUp = chunkprimer.getBlockState(x, y+1, z);

					if(block == Blocks.stone.getDefaultState() && blockUp == Blocks.air.getDefaultState())
					{
						/*if(!isLakeBorder(p, closestCenter) && y <= hexElev && closestCenter.water && !closestCenter.ocean)
						{
							chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
						}
						else if(!isLakeBorder(p, closestCenter) && y <= hexElev&& 
								closestCenter.biome == BiomeType.MARSH && this.rand.nextBoolean())
						{
							chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
						}*/

						chunkprimer.setBlockState(x, y, z, Blocks.grass.getDefaultState());

						if((closestCenter.biome == BiomeType.BEACH || closestCenter.biome == BiomeType.OCEAN) && y <= SEA_LEVEL + 3)
						{
							chunkprimer.setBlockState(x, y, z, Blocks.sand.getDefaultState());
						}
					}

					if(closestCenter.ocean && block == Blocks.stone.getDefaultState() && blockUp == Blocks.water.getDefaultState())
					{
						chunkprimer.setBlockState(x, y, z, Blocks.sand.getDefaultState());
					}
				}
			}
		}

		carveRiverSpline(chunkprimer);
	}

	protected boolean isLakeBorder(Point p, Center c)
	{
		double width = 3;
		Point pt = p.plus(0, width);
		Center c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(0, -width);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(width, 0);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(-width, width);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(-width, -width);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(width, width);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		pt = p.plus(width, -width);
		c2 = getHex(pt);
		if(c2 != c && !c2.water)
			return true;
		return false;
	}

	protected int getElevation(Center c, Point p)
	{
		Point p2 = p.plus(islandX, islandZ);
		double turb = (turbMap.GetValue(p2.x, p2.y));
		return getHexElevation(c, p) + (int)(turb * 1.5);
	}

	protected int getHexElevation(Center c, Point p)
	{
		return convertElevation(getSmoothHeightHex(c, p));
	}

	protected int convertElevation(double height)
	{
		return (int)(SEA_LEVEL+height * islandMap.islandShape.islandMaxHeight);
	}

	protected void generateTerrain(ChunkPrimer chunkprimer, int chunkX, int chunkZ)
	{
		Point p;
		Center closestCenter;
		double[] dts = new double[] {0,0};
		double dist = 0;
		double loc = 0;

		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				p = new Point(x, z);
				closestCenter = this.getHex(p);

				int hexElev = getHexElevation(closestCenter, p);
				elevationMap[x][z] = hexElev;
				for(int y = Math.max(hexElev, SEA_LEVEL); y >= 0; y--)
				{
					if(!closestCenter.ocean && y < hexElev)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.stone.getDefaultState());
					}
					else if(y < hexElev)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.stone.getDefaultState());
					}
					else if(closestCenter.ocean && y < SEA_LEVEL)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}
					else if(y < SEA_LEVEL)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}

					if(y <= 1)
						chunkprimer.setBlockState(x, y, z, Blocks.bedrock.getDefaultState());

					if(closestCenter.biome == BiomeType.LAKE && !isLakeBorder(p, closestCenter) && y == hexElev-1)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}
					else if(closestCenter.biome == BiomeType.MARSH && !isLakeBorder(p, closestCenter) && y == hexElev-1 && this.rand.nextBoolean())
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}
				}
			}
		}
	}

	protected void carveRiverSpline(ChunkPrimer chunkprimer) 
	{
		ArrayList riverPoints;
		int riverDepth = 0;
		for(Center c : centersInChunk)
		{
			riverDepth = 0;
			if(c.river > 0)
			{
				//If the river has multiple Up River locations then we need to handle the splines in two parts.
				if(c.upriver != null && c.upriver.size() > 1)
				{
					for(Center u : c.upriver)
					{
						riverDepth = 0;
						riverPoints = new ArrayList<Point>();
						riverPoints.add(c.getSharedEdge(u).midpoint);
						riverPoints.add(c.point);
						riverPoints.add(c.getSharedEdge(c.downriver).midpoint);
						if(u.river > 0 && !c.water)
						{
							processRiverSpline(chunkprimer, c, new Spline2D(riverPoints.toArray()), u.river+1, riverDepth, Blocks.air.getDefaultState());
							riverDepth--;
						}

						processRiverSpline(chunkprimer, c, new Spline2D(riverPoints.toArray()), u.river, riverDepth, Blocks.flowing_water.getDefaultState());
					}
				}
				else if(c.upriver != null && c.upriver.size() == 1)
				{
					riverPoints = new ArrayList<Point>();
					Point upPoint = c.getSharedEdge(c.upriver.get(0)).midpoint;
					Point downPoint = c.getSharedEdge(c.downriver).midpoint;
					riverPoints.add(upPoint);
					riverPoints.add(c.point);
					riverPoints.add(downPoint);
					if(c.river > 0 && !c.water)
					{
						processRiverSpline(chunkprimer, c, new Spline2D(riverPoints.toArray()), c.river+1, riverDepth, Blocks.air.getDefaultState());
						riverDepth--;

					}
					processRiverSpline(chunkprimer, c, new Spline2D(riverPoints.toArray()), c.river, riverDepth, Blocks.flowing_water.getDefaultState());
				}
				else
				{
					riverPoints = new ArrayList<Point>();
					riverPoints.add(c.point);
					riverPoints.add(c.getSharedEdge(c.downriver).midpoint);
					if(c.river > 0 && !c.water)
					{
						processRiverSpline(chunkprimer, c, new Spline2D(riverPoints.toArray()), c.river+1, riverDepth, Blocks.air.getDefaultState());
						riverDepth--;
					}
					processRiverSpline(chunkprimer, c, new Spline2D(riverPoints.toArray()), c.river, riverDepth, Blocks.flowing_water.getDefaultState());
				}

			}
		}
	}

	protected void processRiverSpline(ChunkPrimer chunkprimer, Center c, Spline2D spline, int width, int yOffset, IBlockState fillBlock) 
	{
		Point interval, temp;
		int waterLevel = SEA_LEVEL;
		if(c.water)
			waterLevel = convertElevation(c.elevation);
		//This loop moves in increments of X% and attempts to carve the river at each point
		for(double m = 0; m < 1; m+= 0.02)
		{
			interval = spline.getPoint(m).floor().minus(new Point(worldX, worldZ));
			for(int x = -width; x <= width; x++)
			{
				for(int z = -width; z <= width; z++)
				{
					temp = interval.plus(x, z);
					int xC = (int)Math.floor(temp.x);
					int zC = (int)Math.floor(temp.y);

					//If we're outside the bounds of the chunk then skip this location
					if(xC < 0 || xC > 15)
						continue;
					if(zC < 0 || zC > 15)
						continue;
					//grab the local elevation from our elevation map
					int yC = elevationMap[xC][zC]+yOffset;

					IBlockState bs = chunkprimer.getBlockState(xC, yC-2, zC);
					if(yC < convertElevation(c.elevation) && c.water && this.isLakeBorder(temp, c))
						yC = convertElevation(c.elevation)+yOffset;

					if(bs != Blocks.flowing_water.getDefaultState() && bs != Blocks.water.getDefaultState())
					{
						//First we place the fill block
						if(bs != Blocks.gravel.getDefaultState())
							chunkprimer.setBlockState(xC, yC-1, zC, fillBlock);
						//Next we replace the underblock with gravel
						if(chunkprimer.getBlockState(xC, yC-2, zC) == Blocks.stone.getDefaultState())
							chunkprimer.setBlockState(xC, yC-2, zC, Blocks.gravel.getDefaultState());
					}
				}
			}
		}
	}

	protected double getSmoothHeightHex(Center c, Point p)
	{
		double h = c.elevation;
		boolean isLakeBorder = false;
		boolean isOcean = c.ocean;
		boolean isLake = c.water && !c.ocean;
		boolean isLand = !c.water;
		//if(isLand || isLake)
		{
			if(isLake)
				isLakeBorder = isLakeBorder(p, c);

			if(/*((isLake && isLakeBorder) || isLand)*/ !(isLake && !isLakeBorder) && (getHex(hexSamplePoints[0].plus(p)) != c || getHex(hexSamplePoints[1].plus(p)) != c || 
					getHex(hexSamplePoints[2].plus(p)) != c || getHex(hexSamplePoints[3].plus(p)) != c || 
					getHex(hexSamplePoints[4].plus(p)) != c || getHex(hexSamplePoints[5].plus(p)) != c))
			{
				for(int i = 0; i < 6; i++)
				{
					h += getHex(hexSamplePoints[i].plus(p)).elevation;
				}

				h /= 7;
			}
		}
		double outH = c.elevation - (c.elevation - h);
		//If this hex is a water hex and the smoothed elevation is lower than the hex elevation than we do not want to lower this cell
		if(c.water && isLakeBorder && outH < c.elevation)
			return c.elevation;
		return outH;
	}

	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_)
	{

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
}
