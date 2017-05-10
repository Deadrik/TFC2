package com.bioxx.tfc2.world;

import java.util.*;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.ChunkProviderOverworld;

import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;

import com.bioxx.jmapgen.*;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.attributes.*;
import com.bioxx.jmapgen.dungeon.*;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.processing.CaveAttrNode;
import com.bioxx.jmapgen.processing.OreAttrNode;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.combiner.Max;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Billow;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.libnoise.module.source.RidgedMulti;
import com.bioxx.tfc2.Core;
import com.bioxx.tfc2.TFCBlocks;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.Schematic.SchemBlock;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.ore.OreConfig;
import com.bioxx.tfc2.api.ore.OreConfig.VeinType;
import com.bioxx.tfc2.api.ore.OreRegistry;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.blocks.terrain.BlockDirt;
import com.bioxx.tfc2.blocks.terrain.BlockGrass;
import com.bioxx.tfc2.blocks.terrain.BlockGravel;
import com.bioxx.tfc2.blocks.terrain.BlockStone;

public class ChunkProviderSurface extends ChunkProviderOverworld 
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
	int chunkX, chunkZ;

	Plane turbMap;
	Plane turbMap1_4;
	Plane beachTurbMap;
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

	public ChunkProviderSurface(World worldIn, long seed, boolean enableMapFeatures, String rules) 
	{
		super(worldIn, seed, false, rules);
		worldObj = worldIn;
		rand = worldObj.rand;
		hexSamplePoints = new Point[16][6];
		//Setup the sampling hexagon for hex smoothing
		for(int i = 0; i < 16; i++)
		{
			double c = i;
			double a = 0.5*c;
			double b = Math.sin(60)*c;

			for(int j = 0; j < 6; j++)
			{
				hexSamplePoints[i][j] = hex_corner(i, j);
			}
		}

		turbMap = createNormalTurbMap(seed);

		turbMap1_4 = createPondTurbMap(seed);

		beachTurbMap = createBeachTurbMap(seed);
	}

	private Plane createNormalTurbMap(long seed)
	{
		/**
		 * Setup our turbulence Module
		 */
		Perlin pe = new Perlin();
		pe.setSeed (seed);
		pe.setFrequency (1f/16f);
		pe.setLacunarity(1.5);
		pe.setOctaveCount(6);
		pe.setNoiseQuality (com.bioxx.libnoise.NoiseQuality.BEST);

		Billow b = new Billow();
		b.setSeed(seed + 3);
		b.setFrequency (1f/30f);
		b.setLacunarity(1.5);
		b.setOctaveCount(2);

		Billow b2 = new Billow();
		b2.setSeed(seed + 50);
		b2.setFrequency (1f/40f);
		b2.setOctaveCount(2);

		Billow b3 = new Billow();
		b3.setSeed(seed + 500);
		b3.setFrequency (1f/50f);
		b3.setOctaveCount(3);

		Max m = new Max();
		m.setSourceModule(0, b2);
		m.setSourceModule(1, b);

		Max m2 = new Max();
		m2.setSourceModule(0, m);
		m2.setSourceModule(1, b3);

		Max m3 = new Max();
		m3.setSourceModule(0, m2);
		m3.setSourceModule(1, pe);

		RidgedMulti r = new RidgedMulti();
		r.setSeed(seed + 300);
		r.setFrequency (1f/20f);
		r.setOctaveCount(2);

		Max m4 = new Max();
		m4.setSourceModule(0, m3);
		m4.setSourceModule(1, r);

		//The scalebias makes our noise fit the range 0-1
		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, m4);
		return new Plane(sb2);
	}

	private Plane createPondTurbMap(long seed)
	{
		Perlin pe = new Perlin();
		pe.setSeed(seed);
		pe.setFrequency (1f/4f);
		pe.setLacunarity(5);
		pe.setOctaveCount(4);
		pe.setPersistence(1f/4f);
		pe.setNoiseQuality (com.bioxx.libnoise.NoiseQuality.BEST);

		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, pe);
		//Noise is normally +-2 so we scale by 0.5 to make it +-1.0
		sb2.setBias(0.5);
		sb2.setScale(0.25);
		return new Plane(sb2);
	}

	private Plane createBeachTurbMap(long seed)
	{
		Billow b = new Billow();
		b.setSeed(seed + 30);
		b.setFrequency (1f/15f);
		b.setLacunarity(1.5);
		b.setOctaveCount(2);

		Billow b2 = new Billow();
		b2.setSeed(seed + 300);
		b2.setFrequency (1f/15f);
		b2.setOctaveCount(2);

		Billow b3 = new Billow();
		b3.setSeed(seed + 3000);
		b3.setFrequency (1f/50f);
		b3.setOctaveCount(2);

		Max m = new Max();
		m.setSourceModule(0, b2);
		m.setSourceModule(1, b);

		Max m2 = new Max();
		m2.setSourceModule(0, m);
		m2.setSourceModule(1, b3);

		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, m2);

		return new Plane(sb2);
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z)
	{

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

		this.chunkX = chunkX;
		this.chunkZ = chunkZ;

		worldX = chunkX * 16;
		worldZ = chunkZ * 16;
		islandChunkX = worldX % MAP_SIZE;
		islandChunkZ = worldZ % MAP_SIZE;
		mapX = (chunkX >> 8);
		mapZ = (chunkZ >> 8);
		islandMap = WorldGen.getInstance().getIslandMap(mapX, mapZ);
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
		createDungeons(chunkprimer);

		if(TFCOptions.shouldStripChunks)
			stripChunk(chunkprimer);

		Chunk chunk = new Chunk(this.worldObj, chunkprimer, chunkX, chunkZ);
		chunk.setHeightMap(elevationMap);

		byte[] biomeArray = chunk.getBiomeArray();
		Point p = new Point(0, 0);
		for (int x = 0; x < 16; x++) 
		{
			for (int z = 0; z < 16; z++) 
			{
				Center c = getHex(p.plus(x, z));

				biomeArray[z << 4 | x] = (byte) Biome.getIdForBiome(c.biome.biome);
			}
		}
		chunk.setBiomeArray(biomeArray);
		chunk.generateSkylightMap();
		return chunk;  
	}

	@Override
	public void populate(int x, int z)
	{
		net.minecraft.block.BlockFalling.fallInstantly = true;

		BlockPos blockpos = new BlockPos(x * 16, 0, z * 16);
		Biome Biome = this.worldObj.getBiome(blockpos.add(16, 0, 16));
		this.rand.setSeed(this.worldObj.getSeed());
		long k = this.rand.nextLong() / 2L * 2L + 1L;
		long l = this.rand.nextLong() / 2L * 2L + 1L;
		this.rand.setSeed(x * k + z * l ^ this.worldObj.getSeed());
		boolean flag = false;
		ChunkPos ChunkPos = new ChunkPos(x, z);

		ForgeEventFactory.onChunkPopulate(true, this, this.worldObj, x, z, flag);

		TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, PopulateChunkEvent.Populate.EventType.LAKE);
		TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, PopulateChunkEvent.Populate.EventType.LAVA);

		Biome.decorate(this.worldObj, this.rand, new BlockPos(x * 16, 0, z * 16));

		if(TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, PopulateChunkEvent.Populate.EventType.ANIMALS))
		{
			BlockPos chunkWorldPos = new BlockPos(x * 16, 0, z * 16);
			worldX = x * 16;
			worldZ = z * 16;
			islandChunkX = worldX % MAP_SIZE;
			islandChunkZ = worldZ % MAP_SIZE;
			Point islandPos = new Point(islandChunkX, islandChunkZ).toIslandCoord();
			IslandMap map = Core.getMapForWorld(worldObj, chunkWorldPos);
			Center centerInChunk = null;

			Center temp = map.getClosestCenter(islandPos);
			if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
				centerInChunk = temp;
			else 
			{
				temp = map.getClosestCenter(islandPos.plus(15, 0));
				if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
					centerInChunk = temp;
				else
				{
					temp = map.getClosestCenter(islandPos.plus(0, 15));
					if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
						centerInChunk = temp;
					else
					{
						temp = map.getClosestCenter(islandPos.plus(15, 15));
						if(Core.isCenterInRect(temp, (int)islandPos.x, (int)islandPos.y, 16, 16))
							centerInChunk = temp;
					}
				}
			}
		}

		blockpos = blockpos.add(8, 0, 8);

		if (TerrainGen.populate(this, this.worldObj, this.rand, x, z, flag, PopulateChunkEvent.Populate.EventType.ICE))
		{
			for (int k2 = 0; k2 < 16; k2++)
			{
				for (int j3 = 0; j3 < 16; j3++)
				{
					BlockPos blockpos1 = this.worldObj.getPrecipitationHeight(blockpos.add(k2, 0, j3));
					BlockPos blockpos2 = blockpos1.down();

					if (this.worldObj.canBlockFreezeWater(blockpos2))
					{
						this.worldObj.setBlockState(blockpos2, Blocks.ICE.getDefaultState(), 2);
					}

					if (this.worldObj.canSnowAt(blockpos1, true))
					{
						this.worldObj.setBlockState(blockpos1, Blocks.SNOW_LAYER.getDefaultState(), 2);
					}
				}
			}
		}

		ForgeEventFactory.onChunkPopulate(false, this, this.worldObj, x, z, flag);

		net.minecraft.block.BlockFalling.fallInstantly = false;
	}

	/**
	 * This is for stripping a chunk of all but ore and BEDROCK for easier testing.
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
					if(state.getBlock() != TFCBlocks.Ore && state.getBlock() != Blocks.BEDROCK && state.getBlock() != Blocks.WOOL)
					{
						primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState());
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
		IBlockState air = Blocks.AIR.getDefaultState();
		//sand = Blocks.SAND.getDefaultState();
		IBlockState freshwater = Blocks.WATER.getDefaultState();//TFCBlocks.FreshWaterStatic.getDefaultState();
		IBlockState saltwater = Blocks.WATER.getDefaultState();//TFCBlocks.SaltWaterStatic.getDefaultState();
		IBlockState top = grass;
		IBlockState fill = dirt;
		int closestElev;

		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				p = new Point(x, z);
				closestCenter = this.getHex(p);
				closestElev = convertElevation(closestCenter.getElevation());
				if(islandMap.getParams().hasFeature(Feature.Desert) && closestCenter.getMoisture().isLessThanOrEqual(Moisture.MEDIUM))
				{
					top = sand;
					fill = sand;
					if(closestCenter.getMoisture().equals(Moisture.MEDIUM) && rand.nextBoolean())
					{
						top = grass;
						fill = dirt;
					}
				}
				else
				{
					top = grass;
					fill = dirt;
				}

				int hexElev = elevationMap[z << 4 | x];

				boolean isCliff = false;

				int h0 = x+1 < 16 ? elevationMap[z << 4 | (x+1)] : getHexElevation(getHex(p.plus(1,0)), p.plus(1,0));
				int h1 = z-1 > 0 ? elevationMap[(z-1) << 4 | x] : getHexElevation(getHex(p.plus(0,-1)), p.plus(0,-1));
				int h2 = x-1 > 0 ? elevationMap[z << 4 | (x-1)] : getHexElevation(getHex(p.plus(-1,0)), p.plus(-1,0));
				int h3 = z + 1 < 16 ? elevationMap[(z+1) << 4 | x] : getHexElevation(getHex(p.plus(0,1)), p.plus(0,1));

				if(hexElev - h0 > 2 || 
						hexElev - h1 > 2 ||
						hexElev - h2 > 2 ||
						hexElev - h3 > 2)
				{
					isCliff = true;
				}


				for(int y = 255; y >= 0; y--)
				{
					IBlockState block = chunkprimer.getBlockState(x, y, z);
					IBlockState blockUp = chunkprimer.getBlockState(x, y+1, z);
					BlockPos basePos = new BlockPos(x,y,z);

					if(block == Blocks.STONE.getDefaultState() && blockUp == Blocks.AIR.getDefaultState())
					{
						if(!isCliff || hexElev == closestElev)
						{
							chunkprimer.setBlockState(x, y, z, top);
							if(!isCliff)
							{
								chunkprimer.setBlockState(x, y-1, z, fill);
								chunkprimer.setBlockState(x, y-2, z, fill);
							}
						}

						if((closestCenter.biome == BiomeType.BEACH || closestCenter.biome == BiomeType.OCEAN) /*&& y <= Global.SEALEVEL + 2*/)
						{
							BlockPos pos = smoothCoast(chunkprimer, p, closestCenter, x, z, y);

							if(pos.getY() <= Global.SEALEVEL + 2)
							{
								top = sand;
								fill = sand;

								//This should prevent most cases of doublestacked sand
								if(isAir(chunkprimer, pos.down().north()) || isAir(chunkprimer, pos.down().south()) || isAir(chunkprimer, pos.down().east()) || isAir(chunkprimer, pos.down().west()))
								{
									chunkprimer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), air);
									elevationMap[z << 4 | x] = pos.getY()-1;
								}
								else
									setState(chunkprimer, pos, top);

								setState(chunkprimer, pos.down(1), fill);
								setState(chunkprimer, pos.down(2), fill);
							}

						}
					}

					if(block == Blocks.STONE.getDefaultState() && blockUp == Blocks.WATER.getDefaultState())
					{
						if((closestCenter.biome == BiomeType.BEACH || closestCenter.biome == BiomeType.OCEAN) && y <= Global.SEALEVEL + 2 && y > 10)
						{
							BlockPos pos = smoothCoast(chunkprimer, p, closestCenter, x, z, y);

							elevationMap[z << 4 | x] = pos.getY();
							chunkprimer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), sand);
							chunkprimer.setBlockState(pos.getX(), pos.getY()-1, pos.getZ(), sand);
							chunkprimer.setBlockState(pos.getX(), pos.getY()-2, pos.getZ(), sand);

						}
						//chunkprimer.setBlockState(x, y, z, sand);
					}

					if(chunkprimer.getBlockState(x, y, z) == Blocks.STONE.getDefaultState())
					{
						chunkprimer.setBlockState(x, y, z, stone);
					}

					if(closestCenter.hasAttribute(Attribute.River) && closestCenter.hasAnyMarkersOf(Marker.Pond))
					{
						RiverAttribute attrib = (RiverAttribute)closestCenter.getAttribute(Attribute.River);
						if(attrib.upriver == null || attrib.upriver.size() == 0)
						{
							boolean border = isLakeBorder(p, closestCenter, 7);
							h0 = this.getPondTurbulence(closestCenter, p, 2);
							if(!border && y < closestElev && y >= closestElev-1-h0)
							{

								chunkprimer.setBlockState(x, y, z, freshwater);
								chunkprimer.setBlockState(x, y-1, z, dirt);
							}
						}
					}

					if((closestCenter.biome == BiomeType.LAKE || closestCenter.biome == BiomeType.LAKESHORE) && closestCenter.hasAttribute(Attribute.Lake))
					{
						LakeAttribute attrib = (LakeAttribute)closestCenter.getAttribute(Attribute.Lake);
						//Not a border area, elev less than the water height, elev greater than the ground height beneath the water
						if(!isLakeBorder(p, closestCenter) && y < convertElevation(attrib.getLakeElev()) && y >= closestElev-attrib.getBorderDistance()*2-this.getTurbulence(closestCenter, p, 4)-1)
							chunkprimer.setBlockState(x, y, z, freshwater);
						if(getBlock(chunkprimer, x, y, z).isFullCube(getBlock(chunkprimer, x, y, z).getDefaultState()) && blockUp == freshwater)
						{
							chunkprimer.setBlockState(x, y, z, sand);
						}
					}
					else if((closestCenter.biome == BiomeType.MARSH || closestCenter.biome == BiomeType.SWAMP) && closestCenter.hasAttribute(Attribute.Lake))
					{
						LakeAttribute attrib = (LakeAttribute)closestCenter.getAttribute(Attribute.Lake);
						if(!isLakeBorder(p, closestCenter) && y < convertElevation(attrib.getLakeElev()) && y >= closestElev-this.getTurbulence(closestCenter, p, 1)-1 && this.rand.nextInt(100) < 70)
							chunkprimer.setBlockState(x, y, z, freshwater);
					}

					if(closestCenter.hasMarker(Marker.Ocean) && block.getBlock().getMaterial(block) == Material.ROCK && blockUp == saltwater)
					{
						chunkprimer.setBlockState(x, y, z, sand);
					}


				}
			}
		}
	}

	private BlockPos smoothCoast(ChunkPrimer chunkprimer, Point p, Center closestCenter, int x, int z, int y) 
	{
		IBlockState saltwater = Blocks.WATER.getDefaultState();//TFCBlocks.SaltWaterStatic.getDefaultState();
		BlockPos pos = new BlockPos(x, y, z);
		if( y > Global.SEALEVEL-4)
		{
			double distance = 1000;
			double clElev = -1;
			Point p2 = p.plus(islandChunkX, islandChunkZ).toIslandCoord();
			List<Center> oceanNeighbors = getOceanNeighbors(closestCenter);
			for(Center c : oceanNeighbors)
			{
				double d = p2.distance(c.point);
				if(d < distance)
					distance = d;
				clElev = Math.max(clElev, c.getElevation());
			}
			clElev = convertElevation(closestCenter.getElevation()) - convertElevation(clElev);
			int turb = (int)clElev;
			float range = 25f;
			if(oceanNeighbors.size() > 3)
				range /=2 ;
			//if(!closestCenter.hasAttribute(Attribute.River))
			//	turb += Math.max(this.getBeachTurb(closestCenter, p, 2), 0);
			turb *= Math.min(1.5f-(distance/range), 1.0f);
			turb = (int) Math.max(turb, 0f);
			pos = pos.down(turb);

			if(pos.getY() < 60)
				pos = new BlockPos(pos.getX(), 60 ,pos.getZ());

			for(int i = y; i > pos.getY(); i--)
			{
				if(i < Global.SEALEVEL)
				{
					chunkprimer.setBlockState(x, i, z, saltwater);
				}
				else 
				{
					if(elevationMap[z << 4 | x] > i)
					{
						elevationMap[z << 4 | x] = i;
					}
					chunkprimer.setBlockState(x, i, z, Blocks.AIR.getDefaultState());

				}
			}
		}
		return pos;
	}

	private ArrayList<Center> getOceanNeighbors(Center c)
	{
		ArrayList<Center> list = new ArrayList<Center>();
		for(Center n : c.neighbors)
		{
			if(n.biome == BiomeType.OCEAN)
				list.add(n);
		}
		return list;
	}

	protected int getBeachTurb(Center c, Point p, double scale)
	{
		Point p2 = p.plus(islandChunkX, islandChunkZ);

		Perlin b = new Perlin();
		b.setSeed(30);
		b.setFrequency (1f/15f);
		b.setLacunarity(1.5);
		b.setPersistence(0.25);
		b.setOctaveCount(2);

		Billow b2 = new Billow();
		b2.setSeed(300);
		b2.setFrequency (1f/15f);
		b2.setOctaveCount(2);

		Billow b3 = new Billow();
		b3.setSeed(3000);
		b3.setFrequency (1f/30f);
		b3.setOctaveCount(3);

		Max m = new Max();
		m.setSourceModule(0, b2);
		m.setSourceModule(1, b);

		Max m2 = new Max();
		m2.setSourceModule(0, m);
		m2.setSourceModule(1, b3);

		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, m2);

		return (int)(sb2.GetValue(p2.x, 0, p2.y) * scale);
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

	protected int getTurbulence(Center c, Point p, double scale)
	{
		Point p2 = p.plus(islandChunkX, islandChunkZ);
		double turb = Math.max(turbMap.GetValue(p2.x, p2.y), 0);
		return (int)(turb * scale);
	}

	protected int getPondTurbulence(Center c, Point p, double scale)
	{
		Point p2 = p.plus(islandChunkX, islandChunkZ);
		double turb = Math.max(createPondTurbMap(0).GetValue(p2.x, p2.y), 0);
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
		Center closestCenter = null;
		double[] dts = new double[] {0,0};
		double dist = 0;
		double loc = 0;

		int maxHeightOfChunk = 255;

		for(int x = 0; x < 16; x++)
		{
			for(int z = 0; z < 16; z++)
			{
				p = new Point(x, z);
				closestCenter = this.getHex(p);


				int hexElev = 0;
				if(!closestCenter.hasAttribute(Attribute.River) && !closestCenter.hasMarker(Marker.Coast) && !closestCenter.hasMarker(Marker.CoastWater) && !closestCenter.hasAttribute(Attribute.Lake))
				{
					//hexElev = convertElevation(getSmoothHeightHex(closestCenter, p));
					hexElev = convertElevation(getSmoothHeightHex(closestCenter, p)) + (int)Math.ceil(turbMap.GetValue(worldX+p.x, worldZ+p.y));
				}
				else if(closestCenter.hasMarker(Marker.CoastWater))
				{
					hexElev = convertElevation(closestCenter.getElevation()) + getBeachTurb(closestCenter, p, 2);
				}
				else 
				{
					hexElev = convertElevation(getSmoothHeightHex(closestCenter, p));
				}
				int scanElev = hexElev;

				if(closestCenter.biome == BiomeType.LAKE || closestCenter.biome == BiomeType.LAKESHORE)
				{
					LakeAttribute attrib = (LakeAttribute) closestCenter.getAttribute(Attribute.Lake);
					scanElev = convertElevation(attrib.getLakeElev());
				}

				if(closestCenter.hasMarker(Marker.Ocean))
				{
					Vector<Center> nearCenters = getCentersNear(p, 9);
					boolean onlyOcean = true;
					for(Center c : nearCenters)
					{
						if(!c.hasMarker(Marker.Ocean))
						{
							onlyOcean = false;
						}
					}

					if(onlyOcean)
						hexElev = convertElevation(getSmoothHeightHex(closestCenter, p, 9));
				}

				if(closestCenter.hasMarker(Marker.Spire))
				{
					Random r = new Random(closestCenter.index);
					double heightMult = r.nextDouble();
					double spireElev = closestCenter.getElevation() + (1-closestCenter.getElevation())*(0.5+heightMult*0.3);
					double diff = spireElev - closestCenter.getElevation();
					double rad = 20;
					dist = closestCenter.point.plus(-5+r.nextInt(11), -5+r.nextInt(11)).distance(p.plus(new Point(worldX, worldZ).toIslandCoord()));
					dist /= 20;
					dist = 1-dist;

					if(dist > 0.95)
						dist = 0.95;

					hexElev = convertElevation(getSmoothHeightHex(closestCenter, p) + (diff*(Math.pow(dist, 5))));
					scanElev = hexElev;
				}


				maxHeightOfChunk = Math.max(maxHeightOfChunk, scanElev);
				elevationMap[z << 4 | x] = hexElev;
				for(int y = Math.min(Math.max(scanElev, Global.SEALEVEL), 255); y >= 0; y--)
				{
					Block b = Blocks.AIR;
					if(y < hexElev)
					{
						b = Blocks.STONE;
					}
					else if(y < Global.SEALEVEL || (closestCenter.hasAttribute(Attribute.Lake) && y < scanElev))
					{
						b = Blocks.WATER;
					}

					if(y <= hexElev * 0.2)
						b = Blocks.BEDROCK;

					chunkprimer.setBlockState(x, y, z, b.getDefaultState());
				}
			}
		}
	}

	protected void carveRiverSpline(ChunkPrimer chunkprimer) 
	{
		ArrayList riverPoints = new ArrayList<Point>();

		/*if(this.islandMap.islandParams.shouldGenVolcano())
			riverStates = new IBlockState[] {Blocks.AIR.getDefaultState(), Blocks.flowing_lava.getDefaultState(), Blocks.gravel.getDefaultState()};*/

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
		BlockPos splineBlockPos, localBlockPos, depthBlockPos, surfaceBlockPos;
		Point islandPoint = new Point(islandChunkX, islandChunkZ).toIslandCoord();
		Center closest;
		IBlockState b;
		double wSq = 2;
		int min = 0, max = 1, rDepth =  Math.min((int)Math.ceil(attrib.getRiver()), 3);
		boolean doAir = false;
		int waterLevel, hexElev, terrainElev = 0;

		if(attrib != null)
		{
			wSq = Math.max(rDepth * rDepth*2, 1);



			for(double i = 0; i < 1; i+= 0.02)
			{
				//Get the spline point for this iteration
				splinePos = spline.getPoint(i);

				//Get the closest hex to this spline point
				closest = islandMap.getClosestCenter(splinePos);
				//translate splinePos from island space to local space
				splinePos = splinePos.minus(islandPoint);

				//If the spline point is outside chunk boundary than we skip it
				if(splinePos.x < -16 || splinePos.y < -16 || splinePos.x >= 32 || splinePos.y >= 32)
					continue;

				//Setup our base block position that we'll be carving around
				splineBlockPos = new BlockPos((int)Math.floor(splinePos.x), 0, (int)Math.floor(splinePos.y));

				//min and max are the maximum distances from the center that we will carve. This is based on river width
				min = (int)Math.floor(-attrib.getRiver());
				max = (int)attrib.getRiver();

				//Begin X/Z iteration around the base point
				for(double x = min; x <= max; x++)
				{
					for(double z = min; z <= max; z++)
					{
						//Add x and z to our base point to get the local block position
						localBlockPos = splineBlockPos.add(x, 0, z);
						//If this local position is outside of the chunk, end here so we don't crash
						if(localBlockPos.getX() < 0 || localBlockPos.getY() < 0 || localBlockPos.getZ() < 0 || 
								localBlockPos.getX() >= 16 || localBlockPos.getY() >= 256 || localBlockPos.getZ() >= 16)
						{
							continue;
						}
						//Get the water level for this location. This is the local terrain elevation - 1
						hexElev = convertElevation(center.getElevation());

						int riverHeightDnDiff = 0, riverHeightUpDiff = 0;
						if(dnCenter != null)
							riverHeightDnDiff = hexElev - convertElevation(dnCenter.getElevation());
						if(upCenter != null)
							riverHeightUpDiff = convertElevation(upCenter.getElevation()) - hexElev;

						terrainElev = this.elevationMap[(localBlockPos.getZ() << 4) | localBlockPos.getX()];
						waterLevel = Math.max(terrainElev-1, Global.SEALEVEL);
						if(center.biome == BiomeType.LAKE || center.biome == BiomeType.LAKESHORE)
						{
							waterLevel = Math.max(terrainElev-1, convertElevation(center.getElevation()));
						}
						else if(center.biome == BiomeType.RIVER && dnCenter!= null)
						{
							waterLevel = Math.max(terrainElev-1, convertElevation(dnCenter.getElevation()));
						}
						if(upCenter != null)
							waterLevel = Math.min(waterLevel, convertElevation(upCenter.getElevation()));


						localBlockPos = localBlockPos.add(0, waterLevel, 0);
						surfaceBlockPos = splineBlockPos.add(0, waterLevel, 0);

						int rd = -rDepth;
						if(terrainElev != hexElev)
						{
							rd = (int)(-rDepth/1.5f);
						}

						if(terrainElev != hexElev)
						{
							rDepth++;
						}

						for(int depth = rDepth; depth >= rd; depth--)
						{
							depthBlockPos = localBlockPos.add(0, depth, 0);
							doAir = false;
							IBlockState s = getState(chunkprimer, depthBlockPos);


							if(depth >= 0 && depthBlockPos.distanceSq(surfaceBlockPos.getX(), surfaceBlockPos.getY(), surfaceBlockPos.getZ()) < wSq)
							{
								if(!Core.isWater(s))
								{
									setState(chunkprimer, depthBlockPos, Blocks.AIR.getDefaultState());
								}
							}
							else if(depth < 0 && depthBlockPos.distanceSq(surfaceBlockPos.getX(), surfaceBlockPos.getY(), surfaceBlockPos.getZ()) <= wSq)
							{
								IBlockState fillState = Blocks.FLOWING_WATER.getDefaultState();
								//if(i > 0.4 && i < 0.6)
								//	fillState = Blocks.FLOWING_WATER.getDefaultState();

								if(!Core.isWater(s))
								{
									if(depthBlockPos.getY() <= hexElev)
										setState(chunkprimer, depthBlockPos, fillState);
									convertRiverBank(chunkprimer, depthBlockPos.down());
								}

								if(depthBlockPos.getY() == waterLevel || depthBlockPos.getY() == waterLevel-1)
								{
									doAir = true;
									convertRiverBank(chunkprimer, depthBlockPos.north(), doAir);
									convertRiverBank(chunkprimer, depthBlockPos.south(), doAir);
									convertRiverBank(chunkprimer, depthBlockPos.east(), doAir);
									convertRiverBank(chunkprimer, depthBlockPos.west(), doAir);
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
				Center closest = islandMap.getClosestCenter(pos.add(this.islandChunkX, 0, this.islandChunkZ));
				int elev = this.convertElevation(closest.getElevation());

				if(closest.hasAttribute(Attribute.River) && 
						(closest.biome == BiomeType.BEACH || closest.biome == BiomeType.MARSH || closest.biome == BiomeType.SWAMP)){return;}
				else
				{
					if(elevationMap[pos.getZ() << 4 | pos.getX()] != elev)
					{
						setState(chunkprimer, pos, TFCBlocks.Stone.getDefaultState().withProperty(BlockGravel.META_PROPERTY, islandMap.getParams().getSurfaceRock()));
						if(!Core.isStone(this.getState(chunkprimer, pos.down())))
						{
							setState(chunkprimer, pos.down(), TFCBlocks.Stone.getDefaultState().withProperty(BlockGravel.META_PROPERTY, islandMap.getParams().getSurfaceRock()));
							if(!Core.isStone(this.getState(chunkprimer, pos.down())))
							{
								setState(chunkprimer, pos.down(), TFCBlocks.Stone.getDefaultState().withProperty(BlockGravel.META_PROPERTY, islandMap.getParams().getSurfaceRock()));
							}
						}
					}
					else
					{
						setState(chunkprimer, pos, TFCBlocks.Gravel.getDefaultState().withProperty(BlockGravel.META_PROPERTY, islandMap.getParams().getSurfaceRock()));
					}
				}

				if(Core.isTerrain(getState(chunkprimer, pos.up(1))))
				{
					if(doAir && Core.isGravel(getState(chunkprimer, pos.up(1))))
					{
						convertRiverBank(chunkprimer, pos.up(1).north(), true);
						convertRiverBank(chunkprimer, pos.up(1).south(), true);
						convertRiverBank(chunkprimer, pos.up(1).east(), true);
						convertRiverBank(chunkprimer, pos.up(1).west(), true);
					}
					while(Core.isTerrain(getState(chunkprimer, pos.up())))
					{
						setState(chunkprimer, pos.up(), Blocks.AIR.getDefaultState());
						pos = pos.up();
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
		{
			if(c.hasAttribute(Attribute.River))
				return getSmoothHeightHex(c, p, 5);
			else if(getMaximumHeightDifferenceOfNeighbors(c) > 6)
				return getSmoothHeightHex(c, p, 2);
		}

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

	private int getMaximumHeightDifferenceOfNeighbors(Center c)
	{
		int max = 0;
		int centerHeight = this.convertElevation(c.getElevation());
		for(Center n : c.neighbors)
		{
			max = Math.max(Math.abs(centerHeight - this.convertElevation(n.getElevation())), max);
		}
		return max;
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

	private Block getBlock(ChunkPrimer chunkprimer, int x, int y, int z)
	{
		return chunkprimer.getBlockState(x, y, z).getBlock();
	}

	private boolean isAir(ChunkPrimer chunkprimer, BlockPos pos)
	{
		if(pos.getX() < 0 || pos.getX() > 15 || pos.getZ() < 0 || pos.getZ() > 15 || pos.getY() < 0 || pos.getY() > 255)
			return false;
		return chunkprimer.getBlockState(pos.getX(), pos.getY(), pos.getZ()) == Blocks.AIR.getDefaultState();
	}

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
					if(n.getNodeWidth() > n.getNodeHeight())
						wSq = n.getNodeWidth() * n.getNodeWidth();
					else 
						wSq = n.getNodeHeight() * n.getNodeHeight();

					points.clear();
					if(n.getPrev() != null)
						points.add(n.getPrevOffset());
					points.add(n.getOffset());
					if(n.getNext() != null)
						points.add(n.getNextOffset());

					spline = new Spline3D(points);

					ArrayList<BlockPos> grassList = new ArrayList<BlockPos>();

					//Iterate down the spline and carve the cave
					for(double i = 0; i < 1; i+= 0.05)
					{
						pos = spline.getPoint(i);

						//Create a list of all the BlockPos that lay within the bounds of the tunnel
						ArrayList<BlockPos> list = new ArrayList<BlockPos>();

						for(int x = -n.getNodeWidth(); x <= n.getNodeWidth(); x++)
						{
							for(int z = -n.getNodeWidth(); z <= n.getNodeWidth(); z++)
							{
								for(int y = -n.getNodeHeight(); y <= n.getNodeHeight(); y++)
								{
									pos2 = pos.add(x, y, z);
									if(pos2.distanceSq(pos.getX(), pos.getY(), pos.getZ()) <= wSq)
										list.add(pos.add(x, y, z));
								}	
							}
						}

						//Iterate through the list and turn every block into air if it is actually within the bounds
						IBlockState down, up, fillBlock, state;
						Iterator it = list.iterator();
						while(it.hasNext())
						{
							fillBlock = Blocks.AIR.getDefaultState();
							pos2 = (BlockPos) it.next();
							if(inEllipse(pos.getX(), pos.getY(), pos2.getX(), pos2.getY(), n.getNodeWidth(), n.getNodeHeight()) && 
									inEllipse(pos.getZ(), pos.getY(), pos2.getZ(), pos2.getY(), n.getNodeWidth(), n.getNodeHeight()) )
							{
								//pos3 is the coordinates in local chunkspace
								BlockPos pos3 = pos2.subtract(islandOffset);
								//Verify that that we are in the active chunk
								if(pos3.getX() >= 0 && pos3.getY() >= 0 && pos3.getZ() >= 0 && pos3.getX() < 16 && pos3.getY() < 256 && pos3.getZ() < 16)
								{
									state = getState(chunkprimer, pos3);
									Block b = state.getBlock();

									//If the block is not bedrock or water then its ok to carve it
									if(b != Blocks.BEDROCK && b.getMaterial(state) != Material.WATER)
									{
										down = getState(chunkprimer, pos3.down());
										up = getState(chunkprimer, pos3.up());

										if(b == TFCBlocks.Grass && Core.isDirt(down))
										{
											setState(chunkprimer, pos3.down(), TFCBlocks.Grass.getDefaultState().withProperty(BlockGrass.META_PROPERTY, down.getValue(BlockDirt.META_PROPERTY)));
										}

										if(down.getBlock().getMaterial(down) == Material.WATER)
											continue;

										//If the block above this is water then we do not want to carve this block so we dont have floating water
										if(up.getBlock().getMaterial(up) == Material.WATER)
											continue;

										//If this is a sea cave and we're blow sea level then change our fillblock to water
										if(n.isSeaCave() && pos3.getY() < Global.SEALEVEL)
											fillBlock = Blocks.WATER.getDefaultState();
										else if(c.hasAttribute(Attribute.River))
										{
											if(up.getBlock() != TFCBlocks.Gravel && b != TFCBlocks.Gravel)
												fillBlock = state;
										}

										//If we find a grass block then add it to a list for later use
										if(Core.isGrass(getState(chunkprimer, pos3)))
										{
											grassList.add(pos3);
										}

										BlockPos placerUp = pos3.up();
										/*while(Core.isSoil(getState(chunkprimer, placerUp)))
										{
											BlockPos placerDn = placerUp.down();
											IBlockState stateDn = getState(chunkprimer, placerDn);
											//keep moving down until we hit stone
											while(stateDn.getBlock() == Blocks.AIR)
											{
												placerDn = placerDn.down();
												stateDn = getState(chunkprimer, placerDn);
											}
											IBlockState upState = getState(chunkprimer, placerUp);
											setState(chunkprimer, placerDn, upState);
											setState(chunkprimer, placerUp, fillBlock);
											placerUp = placerUp.up();
										}*/

										//Try to remove orphan stone blocks
										if(Core.isStone(getState(chunkprimer, pos3.up())) && getState(chunkprimer, pos3.up(2)).getBlock() == Blocks.AIR)
											setState(chunkprimer, pos3.up(), Blocks.AIR.getDefaultState());



										setState(chunkprimer, pos3, fillBlock);

										if(Core.isSoil(up) && !Core.isGrass(up))
										{
											setState(chunkprimer, pos3.up(), TFCBlocks.Stone.getDefaultState().withProperty(BlockStone.META_PROPERTY, islandMap.getParams().getSurfaceRock()));
										}
									}
								}
							}
						}
					}

					Iterator it = grassList.iterator();
					while(it.hasNext())
					{
						pos = (BlockPos)it.next();
						IBlockState state = getState(chunkprimer, pos);
						while(state.getBlock() == Blocks.AIR)
						{
							pos = pos.down();
							state = getState(chunkprimer, pos);
						}
						setState(chunkprimer, pos, TFCBlocks.Grass.getDefaultState().withProperty(BlockGrass.META_PROPERTY, islandMap.getParams().getSurfaceRock()));
					}
				}
			}
		}
	}

	private boolean inEllipse(double originX, double originY, double x, double y, double radiusX, double radiusY)
	{
		double _x = Math.pow(x - originX, 2) /  Math.pow(radiusX, 2);
		double _y = Math.pow(y - originY, 2) /  Math.pow(radiusY, 2);

		if(_x + _y < 1)
			return true;

		return false;
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
										down = getState(chunkprimer, pos2.down());
										up = getState(chunkprimer, pos2.up());

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
		return Blocks.AIR.getDefaultState();
	}

	public void setState(ChunkPrimer primer, BlockPos pos, IBlockState state)
	{
		if(pos.getX() >= 0 && pos.getY() >= 0 && pos.getZ() >= 0 && pos.getX() < 16 && pos.getY() < 256 && pos.getZ() < 16)
			primer.setBlockState(pos.getX(), pos.getY(), pos.getZ(), state);
	}

	public void createDungeons(ChunkPrimer primer)
	{
		Point p = new Point(islandChunkX, islandChunkZ).toIslandCoord();
		int iChunkX = ((int)p.x >> 4);
		int iChunkZ = ((int)p.y >> 4);
		//world chunk coord for the top left of this island
		int islandStartChunkX = (islandMap.getParams().getXCoord() << 8);
		int islandStartChunkZ = (islandMap.getParams().getZCoord() << 8);

		for(Dungeon d : islandMap.dungeons)
		{
			int cX = iChunkX;
			int cZ = iChunkZ;

			DungeonChunk dc = d.getChunk(cX, cZ);
			if(dc != null)
			{
				Iterator<DungeonRoom> iter = dc.getRoomMap().values().iterator();
				while(iter.hasNext())
				{
					DungeonRoom dr = iter.next();
					if(dr != null)
					{
						genRoom(primer, d, dr);
					}
				}
			}
		}
	}

	protected void genRoom(ChunkPrimer primer, Dungeon dungeon, DungeonRoom room)
	{
		RoomSchematic schem = room.getSchematic();

		for(SchemBlock b : schem.getProcessedBlockList(dungeon))
		{
			DungeonDirection borderFacing = isOnBorder(b);
			if(borderFacing != null && b.state.getBlock() == Blocks.OAK_DOOR)
			{
				if(!room.hasConnection(borderFacing))
				{
					primer.setBlockState(8+b.pos.getX(), room.getPosition().getY() + b.pos.getY(), 8+b.pos.getZ(), dungeon.blockMap.get("dungeon_wall"));
					continue;
				}
				else if(room.hasConnection(borderFacing) && !room.getConnection(borderFacing).placeDoor)
				{
					primer.setBlockState(8+b.pos.getX(), room.getPosition().getY() + b.pos.getY(), 8+b.pos.getZ(), Blocks.AIR.getDefaultState());
					continue;
				}
			}
			else if(borderFacing != null && b.state.getBlock() == Blocks.AIR)
			{
				if(!room.hasConnection(borderFacing) && b.pos.getY() < 10)//the <10 check here makes sure that the surface sections of entrances
				{
					primer.setBlockState(8+b.pos.getX(), room.getPosition().getY() + b.pos.getY(), 8+b.pos.getZ(), dungeon.blockMap.get("dungeon_wall"));
					continue;
				}
			}
			primer.setBlockState(8+b.pos.getX(), room.getPosition().getY() + b.pos.getY(), 8+b.pos.getZ(), b.state);
		}
	}

	private DungeonDirection isOnBorder(SchemBlock b)
	{
		if(b.pos.getX() == -8)
			return DungeonDirection.WEST;
		else if(b.pos.getX() == 7)
			return DungeonDirection.EAST;
		else if(b.pos.getZ() == -8)
			return DungeonDirection.NORTH;
		else if(b.pos.getZ() == 7)
			return DungeonDirection.SOUTH;
		return null;
	}

}
