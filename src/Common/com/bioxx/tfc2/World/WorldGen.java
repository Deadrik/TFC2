package com.bioxx.tfc2.World;

import jMapGen.IslandDefinition;
import jMapGen.IslandMapGen;
import jMapGen.Map;

import java.util.HashMap;

import net.minecraft.world.World;

import com.bioxx.tfc2.api.Util.Helper;


public class WorldGen
{
	public static WorldGen instance;
	HashMap<Integer, CachedIsland> islandCache;
	World world;
	public static final int ISLAND_SIZE = 4096;

	public WorldGen(World w) 
	{
		world = w;
		islandCache = new HashMap<Integer, CachedIsland>();
	}

	public static void initialize(World world)
	{
		if(instance == null)
			instance = new WorldGen(world);
	}

	/**
	 * Coordinates should already be in MapCoords
	 */
	public Map getIslandMap(int x, int z)
	{
		int id = Helper.cantorize(x, z);
		if(islandCache.containsKey(id))
		{
			return islandCache.get(id).getIslandMap();
		}

		return createIsland(x, z);
	}

	private Map createIsland(int x, int z)
	{
		long seed = world.getSeed()+Helper.cantorize(x, z);
		IslandDefinition id = new IslandDefinition(seed, ISLAND_SIZE, 0.5, 0.3);
		IslandMapGen mapgen = new IslandMapGen(id, seed);
		islandCache.put(Helper.cantorize(x, z), new CachedIsland(mapgen));
		return mapgen.map;
	}

	public void resetCache()
	{
		islandCache = new HashMap<Integer, CachedIsland>();
	}

}
