package com.bioxx.tfc2.World;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;

import com.bioxx.jMapGen.BiomeType;
import com.bioxx.jMapGen.Map;
import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.Spline2D;
import com.bioxx.jMapGen.IslandParameters.Feature;
import com.bioxx.jMapGen.attributes.Attribute;
import com.bioxx.jMapGen.attributes.CanyonAttribute;
import com.bioxx.jMapGen.attributes.RiverAttribute;
import com.bioxx.jMapGen.graph.Center;
import com.bioxx.jMapGen.graph.Center.Marker;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;

public class ChunkProviderSurface extends ChunkProviderGenerate 
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
	private static Point[][] hexSamplePoints;

	public ChunkProviderSurface(World worldIn, long seed, boolean enableMapFeatures, String rules) 
	{
		super(worldIn, seed, enableMapFeatures, rules);
		worldObj = worldIn;
		rand = worldObj.rand;
		hexSamplePoints = new Point[11][6];
		//Setup the sampling hexagon for hex smoothing
		for(int i = 0; i < 11; i++)
		{
			double c = i;
			double a = 0.5*c;
			double b = Math.sin(60)*c;

			for(int j = 0; j < 6; j++)
			{
				hexSamplePoints[i][j] = hex_corner(i, j);
			}
		}

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

	private Point hex_corner(double size, int i)
	{
		double angle_deg = 60 * i   + 30;
		double angle_rad = Math.PI / 180 * angle_deg;
		return new Point(size * Math.cos(angle_rad),
				size * Math.sin(angle_rad));
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
				int hexElev = this.getHexElevation(closestCenter, p);
				for(int y = hexElev; y >= 0; y--)
				{
					IBlockState block = chunkprimer.getBlockState(x, y, z);
					IBlockState blockUp = chunkprimer.getBlockState(x, y+1, z);

					if(block == Blocks.stone.getDefaultState() && blockUp == Blocks.air.getDefaultState())
					{
						chunkprimer.setBlockState(x, y, z, Blocks.grass.getDefaultState());

						if((closestCenter.biome == BiomeType.BEACH || closestCenter.biome == BiomeType.OCEAN) && y <= SEA_LEVEL + 3)
						{
							chunkprimer.setBlockState(x, y, z, Blocks.sand.getDefaultState());
						}
					}

					if(closestCenter.biome == BiomeType.LAKE && !isLakeBorder(p, closestCenter) && y < hexElev && y >= hexElev-this.getElevation(closestCenter, p, 4)-1)
					{
						/*if(this.islandMap.islandParams.shouldGenVolcano())
							chunkprimer.setBlockState(x, y, z, Blocks.lava.getDefaultState());
						else*/
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}
					else if(closestCenter.biome == BiomeType.MARSH && !isLakeBorder(p, closestCenter) && y < hexElev && y >= hexElev-this.getElevation(closestCenter, p, 2)-1 && this.rand.nextInt(100) < 70)
					{
						chunkprimer.setBlockState(x, y, z, Blocks.water.getDefaultState());
					}

					if(closestCenter.hasMarker(Marker.Ocean) && block == Blocks.stone.getDefaultState() && blockUp == Blocks.water.getDefaultState())
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
		if(c2 != c && !c2.hasMarker(Marker.Water))
			return true;
		pt = p.plus(0, -width);
		c2 = getHex(pt);
		if(c2 != c && !c2.hasMarker(Marker.Water))
			return true;
		pt = p.plus(width, 0);
		c2 = getHex(pt);
		if(c2 != c && !c2.hasMarker(Marker.Water))
			return true;
		pt = p.plus(-width, width);
		c2 = getHex(pt);
		if(c2 != c && !c2.hasMarker(Marker.Water))
			return true;
		pt = p.plus(-width, -width);
		c2 = getHex(pt);
		if(c2 != c && !c2.hasMarker(Marker.Water))
			return true;
		pt = p.plus(width, width);
		c2 = getHex(pt);
		if(c2 != c && !c2.hasMarker(Marker.Water))
			return true;
		pt = p.plus(width, -width);
		c2 = getHex(pt);
		if(c2 != c && !c2.hasMarker(Marker.Water))
			return true;
		return false;
	}

	protected int getElevation(Center c, Point p, double scale)
	{
		Point p2 = p.plus(islandX, islandZ);
		double turb = (turbMap.GetValue(p2.x, p2.y));
		return (int)(turb * scale);
	}

	protected int getHexElevation(Center c, Point p)
	{
		return convertElevation(getSmoothHeightHex(c, p));
	}

	protected int convertElevation(double height)
	{
		return (int)(SEA_LEVEL+height * islandMap.islandParams.islandMaxHeight);
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
				for(int y = Math.min(Math.max(hexElev, SEA_LEVEL), 255); y >= 0; y--)
				{
					Block b = Blocks.air;
					if(!closestCenter.hasMarker(Marker.Ocean) && y < hexElev)
					{
						b = Blocks.stone;
					}
					else if(y < hexElev)
					{
						b = Blocks.stone;
					}
					else if(y < SEA_LEVEL)
					{
						b = Blocks.water;
					}

					if(y <= 1)
						b = Blocks.bedrock;

					chunkprimer.setBlockState(x, y, z, b.getDefaultState());
				}
			}
		}
	}

	protected void carveRiverSpline(ChunkPrimer chunkprimer) 
	{
		ArrayList riverPoints;
		int riverDepth = 0;

		IBlockState[] bankStates = new IBlockState[] {Blocks.air.getDefaultState(), Blocks.gravel.getDefaultState()};
		IBlockState[] riverStates = new IBlockState[] {Blocks.air.getDefaultState(), Blocks.flowing_water.getDefaultState(), Blocks.gravel.getDefaultState()};

		/*if(this.islandMap.islandParams.shouldGenVolcano())
			riverStates = new IBlockState[] {Blocks.air.getDefaultState(), Blocks.flowing_lava.getDefaultState(), Blocks.gravel.getDefaultState()};*/

		for(Center c : centersInChunk)
		{
			riverDepth = 0;
			RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.riverUUID));
			if(attrib != null && attrib.getRiver() > 0)
			{
				//If the river has multiple Up River locations then we need to handle the splines in two parts.
				if(attrib.upriver != null && attrib.upriver.size() > 1)
				{
					for(Center u : attrib.upriver)
					{
						RiverAttribute uAttrib = ((RiverAttribute)u.getAttribute(Attribute.riverUUID));
						riverDepth = 0;
						riverPoints = new ArrayList<Point>();
						riverPoints.add(c.getSharedEdge(u).midpoint);
						riverPoints.add(c.point);
						riverPoints.add(c.getSharedEdge(attrib.getDownRiver()).midpoint);
						//if(!c.water)
						{
							processRiverSpline(chunkprimer, c, u, new Spline2D(riverPoints.toArray()), uAttrib.getRiver()+1, bankStates);
							riverDepth--;
						}

						processRiverSpline(chunkprimer, c, u, new Spline2D(riverPoints.toArray()), uAttrib.getRiver(), riverStates);
					}
				}
				else if(attrib.upriver != null && attrib.upriver.size() == 1)
				{
					riverPoints = new ArrayList<Point>();
					Point upPoint = c.getSharedEdge(attrib.upriver.get(0)).midpoint;
					Point downPoint = c.getSharedEdge(attrib.getDownRiver()).midpoint;
					riverPoints.add(upPoint);
					riverPoints.add(c.point);
					riverPoints.add(downPoint);
					//if(c.river > 0 && !c.water)
					{
						processRiverSpline(chunkprimer, c, attrib.upriver.get(0), new Spline2D(riverPoints.toArray()), attrib.getRiver()+1, bankStates);
						riverDepth--;

					}
					processRiverSpline(chunkprimer, c, attrib.upriver.get(0), new Spline2D(riverPoints.toArray()), attrib.getRiver(), riverStates);
				}
				else
				{
					riverPoints = new ArrayList<Point>();
					riverPoints.add(c.point);
					riverPoints.add(c.getSharedEdge(attrib.getDownRiver()).midpoint);
					//if(c.river > 0 && !c.water)
					{
						processRiverSpline(chunkprimer, c, null, new Spline2D(riverPoints.toArray()), attrib.getRiver()+1, bankStates);
						riverDepth--;
					}
					processRiverSpline(chunkprimer, c, null, new Spline2D(riverPoints.toArray()), attrib.getRiver(), riverStates);
				}

			}
		}
	}

	protected void processRiverSpline(ChunkPrimer chunkprimer, Center c, Center u, Spline2D spline, double width, IBlockState[] fillBlocks) 
	{
		Point interval, temp;
		Center closest;
		int waterLevel = SEA_LEVEL;
		//if(c.water)
		waterLevel = convertElevation(c.elevation);

		RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.riverUUID));

		//This loop moves in increments of X% and attempts to carve the river at each point
		for(double m = 0; m < 1; m+= 0.03)
		{
			interval = spline.getPoint(m).floor().minus(new Point(worldX, worldZ));

			for(double x = -width; x <= width; x+=0.25)
			{
				for(double z = -width; z <= width; z+=0.25)
				{
					temp = interval.plus(x, z);

					int xC = (int)Math.floor(temp.x);
					int zC = (int)Math.floor(temp.y);

					//If we're outside the bounds of the chunk then skip this location
					if(xC < 0 || xC > 15)
						continue;
					if(zC < 0 || zC > 15)
						continue;
					closest = this.getHex(temp);
					//grab the local elevation from our elevation map
					int yC = elevationMap[xC][zC];
					if(yC < convertElevation(closest.elevation) && closest.hasMarker(Marker.Water) && this.isLakeBorder(temp, closest))
						yC = convertElevation(c.elevation);

					//This prevents our river from attempting to carve into a lake unless we're carving out a border section
					if(closest.hasMarker(Marker.Water) && !this.isLakeBorder(temp, closest))
						continue;

					//This makes sure that when we're carving a lake border, we dont carve below the water level
					if(attrib.getDownRiver() != null && attrib.getDownRiver().hasMarker(Marker.Water) && yC == convertElevation(attrib.getDownRiver().elevation))
						yC++;

					if(yC > waterLevel && m > 0.5)
						continue;

					if(u != null && yC > convertElevation(u.elevation) && m < 0.5)
						continue;

					if(yC < SEA_LEVEL+1)
						yC = SEA_LEVEL+1;

					for(int y = -1, i = 0; i < fillBlocks.length; y--, i++)
					{
						IBlockState bs = chunkprimer.getBlockState(xC, yC+y, zC);
						//We dont want to replace any existing water blocks
						if(bs != Blocks.water.getDefaultState() && bs != Blocks.flowing_water.getDefaultState() && bs != Blocks.flowing_lava.getDefaultState())
						{
							bs = fillBlocks[i];
							//This converts 60% of the river water into stationary blocks so that we cut down on the number of required updates.
							if(bs == Blocks.flowing_water.getDefaultState() && (m < 0.2 || m > 0.8))
								bs = Blocks.water.getDefaultState();
							chunkprimer.setBlockState(xC, yC+y, zC, fillBlocks[i]);
						}
					}
				}
			}
		}
	}

	protected double getSmoothHeightHex(Center c, Point p, int range)
	{
		double h = c.elevation;
		boolean isLakeBorder = false;
		boolean isLake = c.hasMarker(Marker.Water) && !c.hasMarker(Marker.Ocean);

		if(isLake)
			isLakeBorder = isLakeBorder(p, c);

		if(!(isLake && !isLakeBorder) && (getHex(hexSamplePoints[range][0].plus(p)) != c || getHex(hexSamplePoints[range][1].plus(p)) != c || 
				getHex(hexSamplePoints[range][2].plus(p)) != c || getHex(hexSamplePoints[range][3].plus(p)) != c || 
				getHex(hexSamplePoints[range][4].plus(p)) != c || getHex(hexSamplePoints[range][5].plus(p)) != c))
		{
			for(int i = 0; i < 6; i++)
			{
				h += getHex(hexSamplePoints[range][i].plus(p)).elevation;
			}

			h /= 7;
		}

		double outH = c.elevation - (c.elevation - h);
		//If this hex is a water hex and the smoothed elevation is lower than the hex elevation than we do not want to lower this cell
		if(c.hasMarker(Marker.Water) && isLakeBorder && outH < c.elevation)
			return c.elevation;
		return outH;
	}

	protected double getSmoothHeightHex(Center c, Point p)
	{
		if(this.islandMap.islandParams.hasFeature(Feature.Cliffs))
			return getSmoothHeightHex(c, p, 2);

		if(islandMap.islandParams.hasFeature(Feature.Canyons) || islandMap.islandParams.hasFeature(Feature.Gorges))
		{
			//If this block is in a gorge hex
			if(c.hasAttribute(Attribute.gorgeUUID))
			{
				return getSmoothHeightHex(c, p, 2);
			}
			//If this block is in a canyon hex
			if(c.hasAttribute(Attribute.canyonUUID))
			{
				CanyonAttribute a = (CanyonAttribute) c.getAttribute(Attribute.canyonUUID);
				if(a.nodeNum < 10)
					return getSmoothHeightHex(c, p, 5);
				return getSmoothHeightHex(c, p, 2);
			}
			//If we're in any other hex then we need to see if we are smoothing into a gorge or canyon hex. If so,
			//we want to limit the smoothing, otherwise we smooth this block like normal.
			Vector<Center> nearList = getCentersNear(p, 5);
			for(Center n : nearList)
			{
				if(n.hasAttribute(Attribute.canyonUUID) || n.hasAttribute(Attribute.gorgeUUID))
					return getSmoothHeightHex(c, p, 2);
			}
		}


		return getSmoothHeightHex(c, p, 5);
	}

	private Vector<Center> getCentersNear(Point p, int range)
	{
		Vector<Center> outList = new Vector<Center>();
		for(int i = 0; i < 6; i++)
		{
			Center c = getHex(hexSamplePoints[range][i].plus(p));
			if(!outList.contains(c))
				outList.add(c);
		}

		return outList;
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
