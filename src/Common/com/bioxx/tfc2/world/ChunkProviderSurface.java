package com.bioxx.tfc2.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.MapGenBase;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.Spline2D;
import com.bioxx.jmapgen.Spline3D;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.CanyonAttribute;
import com.bioxx.jmapgen.attributes.CaveAttribute;
import com.bioxx.jmapgen.attributes.LakeAttribute;
import com.bioxx.jmapgen.attributes.OreAttribute;
import com.bioxx.jmapgen.attributes.RiverAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.processing.CaveAttrNode;
import com.bioxx.jmapgen.processing.OreAttrNode;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.ore.OreConfig;
import com.bioxx.tfc2.api.ore.OreConfig.VeinType;
import com.bioxx.tfc2.api.ore.OreRegistry;
import com.bioxx.tfc2.blocks.terrain.BlockDirt;
import com.bioxx.tfc2.blocks.terrain.BlockGrass;
import com.bioxx.tfc2.blocks.terrain.BlockGravel;
import com.bioxx.tfc2.blocks.terrain.BlockStone;

public class ChunkProviderSurface extends ChunkProviderGenerate 
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

	Plane turbMap;
	IslandMap islandMap;

	Vector<Center> centersInChunk;
	int[] elevationMap;
	/**
	 * Cache for Hex lookup.
	 */
	private Center[][] centerCache;
	/**
	 * A static array of sample points for performing our hex smoothing
	 */
	private static Point[][] hexSamplePoints;

	private MapGenBase caveGenerator;

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

		this.caveGenerator = new MapGenCaves();
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


		elevationMap = new int[256];
		worldX = chunkX * 16;
		worldZ = chunkZ * 16;
		islandChunkX = worldX % MAP_SIZE;
		islandChunkZ = worldZ % MAP_SIZE;
		mapX = (chunkX >> 8);
		mapZ = (chunkZ >> 8);
		islandMap = WorldGen.instance.getIslandMap(mapX, mapZ);
		centersInChunk = new Vector<Center>();

		for(int x = -16; x < 32; x++)
		{
			for(int z = -16; z < 32; z++)
			{
				getHex(new Point(x,z));
			}
		}

		this.rand.setSeed((long)chunkX * 341873128712L + (long)chunkZ * 132897987541L);
		ChunkPrimer chunkprimer = new ChunkPrimer();
		generateTerrain(chunkprimer, chunkX, chunkZ);
		decorate(chunkprimer, chunkX, chunkZ);
		carveRiverSpline(chunkprimer);
		carveCaves(chunkprimer);
		placeOreSeams(chunkprimer);
		placeOreLayers(chunkprimer);

		if(TFCOptions.shouldStripChunks)
			stripChunk(chunkprimer);

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		chunk.setHeightMap(elevationMap);
		chunk.generateSkylightMap();
		return chunk;  
	}

	/**
	 * This is for stripping a chunk of all but ore and bedrock for easier testing.
	 */
	protected void stripChunk(ChunkPrimer primer)
	{
		Point p;
		Center closestCenter;
		IBlockState state;
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				p = new Point(x, z);
				closestCenter = this.getHex(p);
				int hexElev = this.getHexElevation(closestCenter, p);

				if(closestCenter.hasAnyMarkersOf(Marker.Coast, Marker.Ocean))
					continue;

				for(int y = hexElev; y >= 0; y--)
				{
					state = primer.getBlockState(x, y, z);
					if(state.getBlock() != TFCBlocks.Ore && state.getBlock() != Blocks.bedrock && state.getBlock() != Blocks.wool)
					{
						primer.setBlockState(x, y, z, Blocks.air.getDefaultState());
					}
				}
			}
		}
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
			centerCache[x16][y16] = islandMap.getClosestCenter(p.plus(islandChunkX, islandChunkZ).toIslandCoord());
		}

		if(!centersInChunk.contains(centerCache[x16][y16]))
		{
			centersInChunk.add(centerCache[x16][y16]);
			for(Center n : centerCache[x16][y16].neighbors)
			{
				if(!centersInChunk.contains(n))
					centersInChunk.add(n);
			}
		}

		return centerCache[x16][y16];
	}

	protected void decorate(ChunkPrimer chunkprimer, int chunkX, int chunkZ)
	{
		Point p;
		Center closestCenter;
		IBlockState grass = TFCBlocks.Grass.getStateFromMeta(this.islandMap.getParams().getSurfaceRock().getMeta());
		IBlockState dirt = TFCBlocks.Dirt.getStateFromMeta(this.islandMap.getParams().getSurfaceRock().getMeta());
		IBlockState stone = TFCBlocks.Stone.getStateFromMeta(this.islandMap.getParams().getSurfaceRock().getMeta());
		IBlockState sand = TFCBlocks.Sand.getStateFromMeta(this.islandMap.getParams().getSurfaceRock().getMeta());

		/*if(islandMap.islandParams.getIslandMoisture() == Moisture.NONE)
		{
			grass = sand;
			dirt = sand;
		}*/
		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				p = new Point(x, z);
				closestCenter = this.getHex(p);
				int hexElev = this.getHexElevation(closestCenter, p);

				boolean isCliff = false;

				int h0 = getHexElevation(getHex(p.plus(1,0)), p.plus(1,0));
				int h1 = getHexElevation(getHex(p.plus(0,-1)), p.plus(0,-1));
				int h2 = getHexElevation(getHex(p.plus(-1,0)), p.plus(-1,0));
				int h3 = getHexElevation(getHex(p.plus(0,1)), p.plus(0,1));

				if(hexElev - h0 > 2 || 
						hexElev - h1 > 2 ||
						hexElev - h2 > 2 ||
						hexElev - h3 > 2)
				{
					isCliff = true;
				}

				for(int y = hexElev; y >= 0; y--)
				{
					IBlockState block = chunkprimer.getBlockState(x, y, z);
					IBlockState blockUp = chunkprimer.getBlockState(x, y+1, z);


					if(block == Blocks.stone.getDefaultState() && blockUp == Blocks.air.getDefaultState())
					{
						if(!isCliff || hexElev == convertElevation(closestCenter.getElevation()))
						{
							chunkprimer.setBlockState(x, y, z, grass);
							if(!isCliff)
							{
								chunkprimer.setBlockState(x, y-1, z, dirt);
								chunkprimer.setBlockState(x, y-2, z, dirt);
							}
						}

						if((closestCenter.biome == BiomeType.BEACH || closestCenter.biome == BiomeType.OCEAN) && y <= Global.SEALEVEL + 3)
						{
							chunkprimer.setBlockState(x, y, z, sand);
						}
					}

					if(chunkprimer.getBlockState(x, y, z) == Blocks.stone.getDefaultState())
					{
						chunkprimer.setBlockState(x, y, z, stone);
					}

					if(closestCenter.hasAttribute(Attribute.River) && closestCenter.hasAnyMarkersOf(Marker.Pond))
					{
						RiverAttribute attrib = (RiverAttribute)closestCenter.getAttribute(Attribute.River);
						if(attrib.upriver == null || attrib.upriver.size() == 0)
						{
							boolean border = isLakeBorder(p, closestCenter, 7);
							if(!border && y < this.convertElevation(closestCenter.getElevation()) && y >= this.convertElevation(closestCenter.getElevation())-1)
							{
								chunkprimer.setBlockState(x, y, z, TFCBlocks.FreshWater.getDefaultState());
								chunkprimer.setBlockState(x, y-1, z, dirt);
							}
						}
					}

					if(closestCenter.biome == BiomeType.LAKE && closestCenter.hasAttribute(Attribute.Lake))
					{
						LakeAttribute attrib = (LakeAttribute)closestCenter.getAttribute(Attribute.Lake);
						//Not a border area, elev less than the water height, elev greater than the ground height beneath the water
						if(!isLakeBorder(p, closestCenter) && y < convertElevation(attrib.getLakeElev()) && y >= this.convertElevation(closestCenter.getElevation())-this.getElevation(closestCenter, p, 4)-1)
							chunkprimer.setBlockState(x, y, z, TFCBlocks.FreshWater.getDefaultState());
						if(getBlock(chunkprimer, x, y, z).isSolidFullCube() && blockUp == TFCBlocks.FreshWater.getDefaultState())
						{
							chunkprimer.setBlockState(x, y, z, sand);
						}
					}
					else if(closestCenter.biome == BiomeType.MARSH)
					{
						LakeAttribute attrib = (LakeAttribute)closestCenter.getAttribute(Attribute.Lake);
						if(!isLakeBorder(p, closestCenter) && y < convertElevation(attrib.getLakeElev()) && y >= this.convertElevation(closestCenter.getElevation())-this.getElevation(closestCenter, p, 2)-1 && this.rand.nextInt(100) < 70)
							chunkprimer.setBlockState(x, y, z, TFCBlocks.FreshWater.getDefaultState());
					}

					if(closestCenter.hasMarker(Marker.Ocean) && block.getBlock().getMaterial() == Material.rock && blockUp == TFCBlocks.SaltWaterStatic.getDefaultState())
					{
						chunkprimer.setBlockState(x, y, z, sand);
					}
				}
			}
		}
	}

	private Block getBlock(ChunkPrimer chunkprimer, int x, int y, int z)
	{
		return chunkprimer.getBlockState(x, y, z).getBlock();
	}

	protected boolean isLakeBorder(Point p, Center c, double width)
	{
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

	protected boolean isLakeBorder(Point p, Center c)
	{
		return isLakeBorder(p, c, 3);
	}

	protected int getElevation(Center c, Point p, double scale)
	{
		Point p2 = p.plus(islandChunkX, islandChunkZ);
		double turb = (turbMap.GetValue(p2.x, p2.y));
		return (int)(turb * scale);
	}

	protected int getHexElevation(Center c, Point p)
	{
		return convertElevation(getSmoothHeightHex(c, p));
	}

	protected int convertElevation(double height)
	{
		return (int)(Global.SEALEVEL+height * islandMap.getParams().islandMaxHeight);
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

				int hexElev = 0;
				if(closestCenter.hasMarker(Marker.Ocean) && !closestCenter.hasMarker(Marker.CoastWater))
					hexElev = convertElevation(getSmoothHeightHex(closestCenter, p));
				else 
					hexElev = convertElevation(getSmoothHeightHex(closestCenter, p));



				elevationMap[z << 4 | x] = hexElev;
				for(int y = Math.min(Math.max(hexElev, Global.SEALEVEL), 255); y >= 0; y--)
				{
					Block b = Blocks.air;
					if(y < hexElev)
					{
						b = Blocks.stone;
					}
					else if(y < Global.SEALEVEL)
					{
						b = TFCBlocks.SaltWaterStatic;
					}

					if(y <= hexElev * 0.2)
						b = Blocks.bedrock;

					chunkprimer.setBlockState(x, y, z, b.getDefaultState());
				}
			}
		}
	}

	protected void carveRiverSpline(ChunkPrimer chunkprimer) 
	{
		ArrayList riverPoints = new ArrayList<Point>();

		/*if(this.islandMap.islandParams.shouldGenVolcano())
			riverStates = new IBlockState[] {Blocks.air.getDefaultState(), Blocks.flowing_lava.getDefaultState(), Blocks.gravel.getDefaultState()};*/

		for(Center c : centersInChunk)
		{
			RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.River));
			if(attrib != null && attrib.getRiver() > 0)
			{
				//If the river has multiple Up River locations then we need to handle the splines in two parts.
				if(attrib.upriver != null && attrib.upriver.size() > 1)
				{
					for(Center u : attrib.upriver)
					{
						RiverAttribute uAttrib = ((RiverAttribute)u.getAttribute(Attribute.River));
						riverPoints.clear();
						riverPoints.add(u.point.midpoint(c.point));
						riverPoints.add(c.point);
						carveRiver(chunkprimer, null, c, u, new Spline2D(riverPoints.toArray()), uAttrib);
					}
					//Now do the downriver side
					if(attrib.getDownRiver() != null)
					{
						riverPoints.clear();
						riverPoints.add(c.point);
						riverPoints.add(attrib.getDownRiver().point.midpoint(c.point));
						carveRiver(chunkprimer, attrib.getDownRiver(), c, null, new Spline2D(riverPoints.toArray()), attrib);
					}
				}
				else if(attrib.upriver != null && attrib.upriver.size() == 1)
				{
					riverPoints.clear();
					riverPoints.add(attrib.upriver.get(0).point.midpoint(c.point));
					riverPoints.add(attrib.getRiverMidpoint());
					if(attrib.getDownRiver() != null)
						riverPoints.add(attrib.getDownRiver().point.midpoint(c.point));
					carveRiver(chunkprimer, attrib.getDownRiver(), c, attrib.upriver.get(0), new Spline2D(riverPoints.toArray()), attrib);
				}
				else if(attrib.getDownRiver() != null)
				{
					riverPoints.clear();
					riverPoints.add(c.point);
					riverPoints.add(attrib.getDownRiver().point.midpoint(c.point));
					carveRiver(chunkprimer, attrib.getDownRiver(), c, null, new Spline2D(riverPoints.toArray()), attrib);
				}

			}
		}
	}

	protected void carveRiver(ChunkPrimer chunkprimer, Center dnCenter, Center center, Center upCenter, Spline2D spline, RiverAttribute attrib)
	{
		ArrayList<BlockPos> points = new ArrayList<BlockPos>();
		Point splinePos;
		BlockPos pos, pos2, pos3;
		Point iPoint = new Point(islandChunkX, islandChunkZ).toIslandCoord();
		Vec3i islandOffset = new Vec3i(iPoint.x, 0, iPoint.y);
		Center closest;
		IBlockState b;
		double wSq = 2;
		int min = 0, max = 1, rDepth =  Math.min((int)Math.ceil(attrib.getRiver()), 3);
		boolean doAir = false;
		int waterLevel, hexElev, terrainElev = 0;

		if(attrib != null)
		{
			wSq = Math.max(rDepth * rDepth, 1);

			for(double i = 0; i < 1; i+= 0.02)
			{
				//Get the spline point for this iteration
				splinePos = spline.getPoint(i);

				//Get the closest hex to this spline point
				closest = islandMap.getClosestCenter(splinePos);
				splinePos = splinePos.minus(iPoint);

				//If the spline point is outsi   de chunk boundary than we skip it
				if(splinePos.x < -16 || splinePos.y < -16 || splinePos.x >= 32 || splinePos.y >= 32)
					continue;

				//Setup our base position that we'll be carving around
				pos = new BlockPos((int)Math.floor(splinePos.x), 0, (int)Math.floor(splinePos.y));

				min = (int)Math.floor(-attrib.getRiver());
				max = (int)attrib.getRiver();

				//Begin X/Z iteration around the base point
				for(double x = min; x <= max; x++)
				{
					for(double z = min; z <= max; z++)
					{
						//Add x and z to our base point to get the local position
						pos2 = pos.add(x, 0, z);
						//If this local position is outside of the chunk, end here so we don't crash
						if(pos2.getX() < 0 || pos2.getY() < 0 || pos2.getZ() < 0 || pos2.getX() >= 16 || pos2.getY() >= 256 || pos2.getZ() >= 16)
						{
							continue;
						}
						//Get the water level for this location. This is the local terrain elevation - 1
						hexElev = convertElevation(center.getElevation());
						terrainElev = this.elevationMap[(pos2.getZ() << 4) | pos2.getX()];
						waterLevel = Math.max(terrainElev-1, Global.SEALEVEL);

						/*if(attrib.getRiver() >= 1)
							waterLevel--;*/

						/*if(attrib.getRiver() > 1D)
							System.out.println();*/

						pos2 = pos2.add(0, waterLevel, 0);
						for(int depth = rDepth; depth >= -rDepth; depth--)
						{
							pos3 = pos2.add(0, depth, 0);
							doAir = false;
							//If we're carving below the water level then we want to make the river bottom curved
							if(depth <= 0 && pos3.distanceSq(pos2.getX(), pos2.getY(), pos2.getZ()) > wSq)
							{
								continue;
							}
							else if(depth > 0 && pos3.distanceSq(pos2.getX(), pos2.getY(), pos2.getZ()) <= wSq)
							{
								setState(chunkprimer, pos3, Blocks.air.getDefaultState());
								continue;
							}
							else if(pos3.distanceSq(pos2.getX(), pos2.getY(), pos2.getZ()) <= wSq)
							{
								IBlockState fillState = TFCBlocks.FreshWater.getDefaultState();

								//If we're moving up or down a slope then don't place water
								if(/*(terrainElev != hexElev && dnCenter != null && terrainElev != this.convertElevation(dnCenter.getElevation())) ||*/ pos3.getY() >= waterLevel)
								{
									fillState = Blocks.air.getDefaultState();
								}
								//fillState = Blocks.air.getDefaultState();
								IBlockState s = getState(chunkprimer, pos3);
								//if(Core.isTerrain(s))
								{
									setState(chunkprimer, pos3, fillState);
									s = getState(chunkprimer, pos3.offsetUp());
									if(s.getBlock() == Blocks.air)
									{
										doAir = true;
									}

									convertRiverBank(chunkprimer, pos3.offsetNorth(), doAir);
									convertRiverBank(chunkprimer, pos3.offsetSouth(), doAir);
									convertRiverBank(chunkprimer, pos3.offsetEast(), doAir);
									convertRiverBank(chunkprimer, pos3.offsetWest(), doAir);
									convertRiverBank(chunkprimer, pos3.offsetDown());
								}
							}
						}
					}
				}
			}
		}
	}

	protected void convertRiverBank(ChunkPrimer chunkprimer, BlockPos pos)
	{
		convertRiverBank(chunkprimer,pos, true);
	}
	protected void convertRiverBank(ChunkPrimer chunkprimer, BlockPos pos, boolean doAir)
	{
		if(pos.getX() >= 0 && pos.getY() >= 0 && pos.getZ() >= 0 && pos.getX() < 16 && pos.getY() < 256 && pos.getZ() < 16)
		{
			IBlockState b = getState(chunkprimer, pos);
			if(Core.isTerrain(b))
			{
				setState(chunkprimer, pos, TFCBlocks.Gravel.getDefaultState().withProperty(BlockGravel.META_PROPERTY, islandMap.getParams().getSurfaceRock()));

				if(Core.isTerrain(getState(chunkprimer, pos.offsetUp(1))))
				{
					if(doAir && Core.isGravel(getState(chunkprimer, pos.offsetUp(1))))
					{
						convertRiverBank(chunkprimer, pos.offsetUp(1).offsetNorth(), true);
						convertRiverBank(chunkprimer, pos.offsetUp(1).offsetSouth(), true);
						convertRiverBank(chunkprimer, pos.offsetUp(1).offsetEast(), true);
						convertRiverBank(chunkprimer, pos.offsetUp(1).offsetWest(), true);
					}
					setState(chunkprimer, pos.offsetUp(1), Blocks.air.getDefaultState());
				}
			}
		}
	}

	protected void processRiverSpline(ChunkPrimer chunkprimer, Center c, Center u, Spline2D spline, double width, IBlockState[] fillBlocks) 
	{
		Point interval, temp;
		Center closest;
		int waterLevel = Global.SEALEVEL;
		//if(c.water)
		waterLevel = Math.max(convertElevation(c.elevation), waterLevel);

		RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.River));

		//This loop moves in increments of X% and attempts to carve the river at each point
		for(double m = 0; m < 1; m+= 0.03)
		{
			temp = new Point(islandChunkX, islandChunkZ).toIslandCoord();
			interval = spline.getPoint(m).floor().minus(temp);

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
					int yC = elevationMap[zC << 4 | xC];
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

					if(yC < Global.SEALEVEL+1)
						yC = Global.SEALEVEL;

					int y = -1, i = 0;
					if(attrib.getRiver() < 1)
						i = 1;
					for(; i < fillBlocks.length; y--, i++)
					{
						IBlockState bs = chunkprimer.getBlockState(xC, yC+y, zC);
						//We dont want to replace any existing water blocks
						if(bs != TFCBlocks.FreshWater.getDefaultState() && bs != TFCBlocks.SaltWaterStatic.getDefaultState() && bs != Blocks.flowing_lava.getDefaultState())
						{
							bs = fillBlocks[i];
							//This converts 60% of the river water into stationary blocks so that we cut down on the number of required updates.
							if(bs == TFCBlocks.FreshWater.getDefaultState() && (m < 0.2 || m > 0.8))
								bs = TFCBlocks.FreshWater.getDefaultState();
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
		if(this.islandMap.getParams().hasFeature(Feature.Cliffs))
			return getSmoothHeightHex(c, p, 2);

		if(islandMap.getParams().hasFeature(Feature.Canyons) || islandMap.getParams().hasFeature(Feature.Gorges))
		{
			//If this block is in a gorge hex
			if(c.hasAttribute(Attribute.Gorge))
			{
				return getSmoothHeightHex(c, p, 2);
			}
			//If this block is in a canyon hex
			if(c.hasAttribute(Attribute.Canyon))
			{
				CanyonAttribute a = (CanyonAttribute) c.getAttribute(Attribute.Canyon);
				if(a.nodeNum < 10)
					return getSmoothHeightHex(c, p, 5);
				return getSmoothHeightHex(c, p, 2);
			}
			//If we're in any other hex then we need to see if we are smoothing into a gorge or canyon hex. If so,
			//we want to limit the smoothing, otherwise we smooth this block like normal.
			Vector<Center> nearList = getCentersNear(p, 5);
			for(Center n : nearList)
			{
				if(n.hasAttribute(Attribute.Canyon) || n.hasAttribute(Attribute.Gorge))
					return getSmoothHeightHex(c, p, 2);
			}
		}

		if(c.hasAnyMarkersOf(Marker.Pond))
		{
			getSmoothHeightHex(c, p, 3);
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

	protected void carveCaves(ChunkPrimer chunkprimer)
	{
		ArrayList<BlockPos> points = new ArrayList<BlockPos>();
		BlockPos pos, pos2;
		Spline3D spline;
		Point iPoint = new Point(islandChunkX, islandChunkZ).toIslandCoord();
		Vec3i islandOffset = new Vec3i(iPoint.x, 0, iPoint.y);
		double wSq = 4;

		for(Center c : centersInChunk)
		{
			CaveAttribute attrib = ((CaveAttribute)c.getAttribute(Attribute.Cave));
			if(attrib != null)
			{
				for(CaveAttrNode n : attrib.nodes)
				{
					wSq = n.getNodeWidth() * n.getNodeWidth();
					points.clear();
					if(n.getPrev() != null)
						points.add(n.getPrevOffset().subtract(islandOffset));
					points.add(n.getOffset().subtract(islandOffset));
					if(n.getNext() != null)
						points.add(n.getNextOffset().subtract(islandOffset));

					spline = new Spline3D(points);
					for(double i = 0; i < 1; i+= 0.05)
					{
						pos = spline.getPoint(i);

						ArrayList<BlockPos> list = new ArrayList<BlockPos>();
						for(int y = -n.getNodeHeight(); y < n.getNodeHeight(); y++)
						{
							for(int x = -n.getNodeWidth(); x < n.getNodeWidth(); x++)
							{
								for(int z = -n.getNodeWidth(); z < n.getNodeWidth(); z++)
								{
									if(pos.add(x, y, z).distanceSq(pos.getX(), pos.getY(), pos.getZ()) <= wSq)
										list.add(pos.add(x, y, z));
								}	
							}
						}
						IBlockState down, up, fillBlock, state;
						Iterator it = list.iterator();
						while(it.hasNext())
						{
							fillBlock = Blocks.air.getDefaultState();
							pos2 = (BlockPos) it.next();
							if(pos.distanceSqToCenter(pos2.getX(), pos2.getY(), pos2.getZ()) <= wSq)
							{
								if(pos2.getX() >= 0 && pos2.getY() >= 0 && pos2.getZ() >= 0 && pos2.getX() < 16 && pos2.getY() < 256 && pos2.getZ() < 16)
								{
									state = getState(chunkprimer, pos2);
									Block b = state.getBlock();
									if(b != Blocks.bedrock && b.getMaterial() != Material.water)
									{
										down = getState(chunkprimer, pos2.offsetDown());
										up = getState(chunkprimer, pos2.offsetUp());
										if(Core.isDirt(down))
										{
											setState(chunkprimer, pos2.offsetDown(), TFCBlocks.Grass.getDefaultState().withProperty(BlockGrass.META_PROPERTY, down.getValue(BlockDirt.META_PROPERTY)));
										}

										if(down.getBlock().getMaterial() == Material.water)
											continue;

										if(up.getBlock().getMaterial() == Material.water)
											continue;

										if(n.isSeaCave() && pos2.getY() < Global.SEALEVEL)
											fillBlock = TFCBlocks.SaltWaterStatic.getDefaultState();
										else if(c.hasAttribute(Attribute.River))
										{
											if(up.getBlock() != TFCBlocks.Gravel && b != TFCBlocks.Gravel)
												fillBlock = state;
										}

										if(TFCOptions.shouldStripChunks)
											fillBlock = Blocks.wool.getDefaultState();

										setState(chunkprimer, pos2, fillBlock);

										if(Core.isSoil(up) && !Core.isGrass(up))
										{
											setState(chunkprimer, pos2.offsetUp(), TFCBlocks.Stone.getDefaultState().withProperty(BlockStone.META_PROPERTY, islandMap.getParams().getSurfaceRock()));
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected void placeOreSeams(ChunkPrimer chunkprimer)
	{
		ArrayList<BlockPos> points = new ArrayList<BlockPos>();
		BlockPos pos, pos2;
		Spline3D spline;
		Point iPoint = new Point(islandChunkX, islandChunkZ).toIslandCoord();
		Vec3i islandOffset = new Vec3i(iPoint.x, 0, iPoint.y);
		double wSq = 4;

		for(Center c : centersInChunk)
		{
			OreAttribute attrib = ((OreAttribute)c.getAttribute(Attribute.Ore));
			if(attrib != null)
			{
				for(OreAttrNode n : attrib.nodes)
				{
					OreConfig oc = OreRegistry.getInstance().getConfig(n.getOreType(), islandMap.getParams().getSurfaceRock());
					if(oc.getVeinType() != VeinType.Seam)
						continue;
					wSq = n.getNodeWidth() * n.getNodeWidth();
					points.clear();
					if(n.getPrev() != null)
						points.add(n.getPrevOffset().subtract(islandOffset));
					points.add(n.getOffset().subtract(islandOffset));
					if(n.getNext() != null)
						points.add(n.getNextOffset().subtract(islandOffset));

					spline = new Spline3D(points);
					for(double i = 0; i < 1; i+= 0.03)
					{
						pos = spline.getPoint(i);

						ArrayList<BlockPos> list = new ArrayList<BlockPos>();
						for(int y = -n.getNodeHeight(); y < n.getNodeHeight(); y++)
						{
							for(int x = -n.getNodeWidth(); x < n.getNodeWidth(); x++)
							{
								for(int z = -n.getNodeWidth(); z < n.getNodeWidth(); z++)
								{
									if(this.rand.nextDouble() < 0.75)
										list.add(pos.add(x, y, z));
								}	
							}
						}
						IBlockState down, up, fillBlock, state;
						Iterator it = list.iterator();
						while(it.hasNext())
						{
							fillBlock = oc.getOreBlockState();
							pos2 = (BlockPos) it.next();

							{
								if(pos2.getX() >= 0 && pos2.getY() >= 0 && pos2.getZ() >= 0 && pos2.getX() < 16 && pos2.getY() < 256 && pos2.getZ() < 16)
								{
									state = getState(chunkprimer, pos2);
									if(Core.isStone(state))
									{
										down = getState(chunkprimer, pos2.offsetDown());
										up = getState(chunkprimer, pos2.offsetUp());

										setState(chunkprimer, pos2, fillBlock);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected void placeOreLayers(ChunkPrimer chunkprimer)
	{
		Point p;
		BlockPos pos = new BlockPos(0,0,0);
		BlockPos pos2;
		double wSq = 4;
		IBlockState state;

		for(Center c : centersInChunk)
		{
			OreAttribute attrib = ((OreAttribute)c.getAttribute(Attribute.Ore));
			if(attrib != null)
			{
				for(OreAttrNode n : attrib.nodes)
				{
					OreConfig oc = OreRegistry.getInstance().getConfig(n.getOreType(), islandMap.getParams().getSurfaceRock());
					if(oc.getVeinType() != VeinType.Layer)
						continue;

					for(int x = 0; x < 16; x++)
					{
						for(int z = 0; z < 16; z++)
						{
							for(int y = n.getOffset().getY(); y < n.getOffset().getY() + n.getNodeHeight(); y++)
							{
								p = new Point(x, z);
								pos2 = pos.add(x, y, z);
								state = getState(chunkprimer, pos2);
								if(this.getHex(p) == c && Core.isStone(state))
								{
									//Add air check
									this.setState(chunkprimer, pos2, oc.getOreBlockState());
								}
							}
						}
					}
				}
			}
		}
	}

	public IBlockState getState(ChunkPrimer primer, BlockPos pos)
	{
		if(pos.getX() >= 0 && pos.getY() >= 0 && pos.getZ() >= 0 && pos.getX() < 16 && pos.getY() < 256 && pos.getZ() < 16)
			return primer.getBlockState(pos.getX(), pos.getY(), pos.getZ());
		return Blocks.air.getDefaultState();
	}

	public void setState(ChunkPrimer primer, BlockPos pos, IBlockState state)
	{
		if(pos.getX() >= 0 && pos.getY() >= 0 && pos.getZ() >= 0 && pos.getX() < 16 && pos.getY() < 256 && pos.getZ() < 16)
			primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), state);
	}

}
