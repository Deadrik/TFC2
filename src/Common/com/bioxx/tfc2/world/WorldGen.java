package com.bioxx.tfc2.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.IslandParameters.Feature.FeatureSig;
import com.bioxx.jmapgen.RandomCollection;
import com.bioxx.tfc2.Reference;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.events.IslandGenEvent;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.api.util.IThreadCompleteListener;
import com.bioxx.tfc2.handlers.client.ClientRenderHandler;
import com.bioxx.tfc2.networking.server.SMapRequestPacket;


public class WorldGen implements IThreadCompleteListener
{
	private static WorldGen instance;
	private static WorldGen instanceClient;
	private static boolean SHOULD_RESET_SERVER = false;
	private static boolean SHOULD_RESET_CLIENT = false;
	private static IslandMap EMPTY_MAP = null;

	final java.util.Map<Integer, CachedIsland> islandCache;
	public World world;
	public long worldSeed = Long.MIN_VALUE;
	public static final int ISLAND_SIZE = 4096;

	private Queue<Integer> mapQueue;
	private ThreadBuild[] buildThreads;

	//We keep this list so that we dont spam the server with map request packets from things like grass blocks.
	private ArrayList<Integer> recentlyRequestedMaps = new ArrayList<Integer>();

	public static WorldGen getInstance()
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			return instance;
		return instanceClient;
	}

	public static void ClearInstances()
	{
		SHOULD_RESET_CLIENT = true;
		SHOULD_RESET_SERVER = true;
	}

	public WorldGen(World w) 
	{
		world = w;
		islandCache = Collections.synchronizedMap(new ConcurrentHashMap<Integer, CachedIsland>());
		mapQueue = new PriorityBlockingQueue<Integer>();
		buildThreads = new ThreadBuild[TFCOptions.maxThreadsForIslandGen];
		EMPTY_MAP = new IslandMap(ISLAND_SIZE, 0);
		IslandParameters ip = createParams(0, -2, 0);
		ip.setIslandTemp(ClimateTemp.TEMPERATE);
		ip.setIslandMoisture(Moisture.HIGH);
		EMPTY_MAP.newIsland(ip);
		EMPTY_MAP.generateFake();
	}

	public static void initialize(World world)
	{
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
		{
			if(instance == null || SHOULD_RESET_SERVER)
			{
				instance = new WorldGen(world);
				SHOULD_RESET_SERVER = false;
			}
		}
		else
		{
			if(instanceClient == null || SHOULD_RESET_CLIENT)
			{
				instanceClient = new WorldGen(world);
				SHOULD_RESET_CLIENT = false;
			}
		}
	}

	public IslandMap getIslandMap(BlockPos pos)
	{
		return getIslandMap(pos.getX(), pos.getZ());
	}

	/**
	 * Retrieves an island map from the cache or creates it if needed. This is a pass-through method to an internal method which retrieves
	 * the island map and hands neighboring island maps off to other threads for generation. Coordinates should already be in MapCoords form.
	 */
	public IslandMap getIslandMap(int x, int z)
	{
		int id = Helper.combineCoords(x, z);
		if(recentlyRequestedMaps.contains(id))
			return EMPTY_MAP;
		//First we try to load the map from disk if it exists
		if(!islandCache.containsKey(id))
		{
			loadMap(x, z);
		}
		//If the map did not exist on disk then create it from scratch
		if(!islandCache.containsKey(id))
		{
			if(this == instanceClient)
				createFakeMap(x, z);
			else
				createIsland(x, z);
		}

		return getMap(x, z);
	}

	private IslandMap createFakeMap(int x, int z)
	{
		if(recentlyRequestedMaps.contains(Helper.combineCoords(x, z)))
		{
			return EMPTY_MAP;
		}
		TFC.network.sendToServer(new SMapRequestPacket(x, z));
		recentlyRequestedMaps.add(Helper.combineCoords(x, z));
		return EMPTY_MAP;
	}

	private IslandMap getMap(int x, int z)
	{
		int id = Helper.combineCoords(x, z);
		CachedIsland ci = islandCache.get(id);

		if(ci == null)
			return EMPTY_MAP;
		//Should only ever be 0 if this map was created but never accessed by the game. Don't queue maps if clientside
		if(ci.lastAccess == 0 /*&& this != instanceClient*/)
		{
			//Add the neighbor maps to the mapQueue for generation in another thread
			mapQueue.add(Helper.combineCoords(x+1, z));
			mapQueue.add(Helper.combineCoords(x+1, z-1));
			mapQueue.add(Helper.combineCoords(x, z-1));
			mapQueue.add(Helper.combineCoords(x-1, z-1));
			mapQueue.add(Helper.combineCoords(x-1, z));
			mapQueue.add(Helper.combineCoords(x-1, z+1));
			mapQueue.add(Helper.combineCoords(x, z+1));
			mapQueue.add(Helper.combineCoords(x+1, z+1));
		}

		return ci.getIslandMap();
	}

	public void enqueueIsland(int x, int z)
	{
		mapQueue.add(Helper.combineCoords(x, z));
	}

	public boolean isMapLoaded(int x, int z)
	{
		return isMapLoaded(Helper.combineCoords(x, z));
	}

	public boolean isMapLoaded(int id)
	{
		return islandCache.get(id) != null;
	}

	public IslandMap createIsland(int x, int z)
	{
		return createIsland(x, z, world.getSeed()+Helper.combineCoords(x, z), false);
	}

	public IslandMap createIsland(int x, int z, long seed, boolean overwrite)
	{
		Random rand = new Random(seed);
		long seed2 = rand.nextLong();

		if(islandCache.containsKey(Helper.combineCoords(x, z)))
		{
			if(islandCache.get(Helper.combineCoords(x, z)).getIslandMap().seed == seed2)
				return islandCache.get(Helper.combineCoords(x, z)).getIslandMap();
		}

		IslandGenEvent.Pre preEvent = new IslandGenEvent.Pre(createParams(seed2, x, z));
		Global.EVENT_BUS.post(preEvent);
		IslandMap mapgen = new IslandMap(ISLAND_SIZE, seed2);
		mapgen.newIsland(preEvent.params);
		mapgen.generateFull();
		//Make sure we don't access IslandData until after generateFull because the data may become lost
		mapgen.getIslandData().islandLevel = Math.abs(x);
		if(x == 0)
		{
			mapgen.getIslandData().unlockIsland();
		}
		IslandGenEvent.Post postEvent = new IslandGenEvent.Post(mapgen);
		Global.EVENT_BUS.post(postEvent);
		CachedIsland ci = new CachedIsland(postEvent.islandMap);
		if(this != instanceClient) saveMap(ci);

		if(!islandCache.containsKey(Helper.combineCoords(x, z)))
			islandCache.put(Helper.combineCoords(x, z), ci);
		else if(overwrite && islandCache.containsKey(Helper.combineCoords(x, z)))
		{
			islandCache.remove(Helper.combineCoords(x, z));
			islandCache.put(Helper.combineCoords(x, z), ci);
		}
		return ci.island;
	}

	private IslandParameters createParams(long seed, int x, int z)
	{
		IslandParameters id = new IslandParameters(seed, ISLAND_SIZE, 0.5, 0.2);
		Random r = new Random(seed);
		id.setCoords(x, z);
		int fcount = 2+r.nextInt(1)+r.nextInt(1);
		Feature.setupFeatures(r);
		//Choose Major Features
		for(int i = 0; i < fcount; i++)
		{
			Feature f = Feature.getRandomFeature(FeatureSig.Major);
			if(f == Feature.Canyons)
				id.setFeatures(Feature.Gorges);

			if(f == Feature.NoLand && x == 0)
				continue;

			if((f == Feature.SharperMountains || f == Feature.EvenSharperMountains) && 
					(id.hasFeature(Feature.SharperMountains) || id.hasFeature(Feature.EvenSharperMountains)))
			{
				i--; 
				continue;
			}

			if(id.hasFeature(f)){i--; continue;}
			else id.setFeatures(f);
		}

		//Choose Minor Features
		fcount = r.nextInt(3)-r.nextInt(1)-r.nextInt(1);
		for(int i = 0; i < fcount; i++)
		{
			Feature f = Feature.getRandomFeature(FeatureSig.Minor);
			if(id.hasFeature(f)){i--; continue;}
			else id.setFeatures(f);
		}

		//id.setFeatures(Feature.Volcano);

		if(id.hasFeature(Feature.LowLand))
			id.removeFeatures(Feature.SharperMountains, Feature.EvenSharperMountains);

		//Remove all other features if this is supposed to be open ocean.
		if(id.hasFeature(Feature.NoLand))
		{
			id.clearFeatures();
			id.setFeatures(Feature.NoLand);
		}

		//All plots too far north or south will be water. The world is only 9 islands tall.
		if(z > 4 || z < -4)
		{
			id.clearFeatures();
			id.setFeatures(Feature.NoLand);
		}

		RandomCollection<Integer> heightPot = new RandomCollection<Integer>(r);
		heightPot.add(0.1, 64);
		heightPot.add(0.8, 96);
		heightPot.add(0.1, 128);
		id.islandMaxHeight = heightPot.next();

		if(id.hasFeature(Feature.LowLand))
		{
			id.islandMaxHeight = 32;
		}

		RandomCollection<StoneType> stonePot = new RandomCollection<StoneType>(r);
		//If the island has a volcano then we need to make sure that the island is properly volcanic.
		if(!id.hasFeature(Feature.Volcano))
		{
			for(StoneType s : StoneType.values())
			{
				stonePot.add(1.0, s);
			}
		}
		else
		{
			id.islandMaxHeight = 128;
			for(StoneType s : StoneType.getForSubTypes(StoneType.SubType.IgneousExtrusive))
			{
				stonePot.add(1.0, s);
			}
		}
		id.setSurfaceRock(stonePot.next());
		ClimateTemp t = ClimateTemp.TEMPERATE;
		//Tropical or Temperate
		if(Math.abs(z) == 0)
		{
			t = ClimateTemp.TROPICAL;
		}
		else if(Math.abs(z) == 1)
		{
			t = r.nextInt(100) < 25 ? ClimateTemp.TEMPERATE : ClimateTemp.SUBTROPICAL;
		}
		else if(Math.abs(z) == 2)
		{
			t = ClimateTemp.TEMPERATE;
		}
		else if(Math.abs(z) == 3)
		{
			t = r.nextInt(100) < 25 ? ClimateTemp.TEMPERATE : ClimateTemp.SUBPOLAR;
		}
		else if(Math.abs(z) == 4)
		{
			t = ClimateTemp.POLAR;
		}

		id.setIslandTemp(t);

		id.setFeatures(Feature.Valleys);


		Moisture m = Moisture.fromVal(r.nextDouble());
		id.setIslandMoisture(m);

		if(m == Moisture.LOW && r.nextDouble() < 0.4)
		{
			id.setFeatures(Feature.Desert);
		}

		String common = TreeRegistry.instance.getRandomTreeTypeForIsland(r, t, m);
		String uncommon = TreeRegistry.instance.getRandomTreeTypeForIsland(r, t, m);
		String rare = TreeRegistry.instance.getRandomTreeTypeForIsland(r, t, m);
		id.setTrees(common, uncommon, rare);

		/***
		 * Animals
		 */
		ArrayList<SpawnGroup> spawnGroups = AnimalSpawnRegistry.getInstance().getValidSpawnGroups(id);

		int max = Math.min(spawnGroups.size(), 4+r.nextInt(Math.max(spawnGroups.size()-4, 1)));
		for(int i = 0; i < max; i++)
		{
			SpawnGroup group = spawnGroups.get(r.nextInt(spawnGroups.size()));
			id.animalSpawnGroups.add(group);
			spawnGroups.remove(group);
		}

		if(id.hasFeature(Feature.Desert))
			id.setIslandMoisture(Moisture.LOW);

		return id;
	}

	public void resetCache()
	{
		synchronized(islandCache)
		{
			for(CachedIsland c : islandCache.values())
			{
				saveMap(c);
			}
			islandCache.clear();
		}
	}

	public void trimCache()
	{
		long now = System.currentTimeMillis();
		int key;
		Set<Integer> keys = islandCache.keySet();
		CachedIsland c;
		int timer = 20000;
		for(Iterator<Integer> iter = keys.iterator(); iter.hasNext();)
		{
			key = iter.next();
			c = islandCache.get(key);
			if(c != null && now-c.lastAccess > timer)//X seconds of no access will trim the map
			{
				saveMap(c);
				islandCache.remove(key);
			}
		}
	}

	public void saveMap(CachedIsland island)
	{
		try
		{
			File file1 = world.getSaveHandler().getMapFileFromName("Map " + island.island.getParams().getXCoord() + "," + 
					island.island.getParams().getZCoord());

			if(file1 == null && this == instanceClient)
			{
				File file = new File(".//mods//TFC2//cache//"+worldSeed+"//");
				if(!file.exists())
					file.mkdirs();
				file1 = new File(".//mods//TFC2//cache//"+ worldSeed +"//Map " + island.island.getParams().getXCoord() + "," + 
						island.island.getParams().getZCoord()+ ".dat");

			}

			if (file1 != null)
			{
				NBTTagCompound islandNBT = new NBTTagCompound();
				island.island.writeToNBT(islandNBT);

				NBTTagCompound finalNBT = new NBTTagCompound();
				finalNBT.setTag("mapdata", islandNBT);
				island.island.getParams().writeToNBT(finalNBT);

				finalNBT.setLong("lastAccess", island.lastAccess);
				finalNBT.setString("TFC2 Version", Reference.ModVersion);

				FileOutputStream fileoutputstream = new FileOutputStream(file1);
				CompressedStreamTools.writeCompressed(finalNBT, fileoutputstream);
				fileoutputstream.close();
			}
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}
	}

	public CachedIsland loadMap(int x, int z)
	{
		try
		{
			File file1 = world.getSaveHandler().getMapFileFromName("Map " + x + "," + z);

			if(file1 == null && this == instanceClient)
			{
				file1 = new File(".//mods//tfc2//cache//"+ worldSeed +"//Map " + x + "," + z + ".dat");
			}

			if (file1 != null && file1.exists())
			{
				FileInputStream input = new FileInputStream(file1);
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(input);
				input.close();
				if(this == instanceClient)
					if(!nbt.getString("TFC2 Version").equals(Reference.ModVersion))
					{
						file1.delete();
						return null;
					}
				IslandParameters ip = new IslandParameters();
				ip.readFromNBT(nbt);
				long seed = world.getSeed()+Helper.combineCoords(x, z);
				if(this == instanceClient)
					seed = this.worldSeed + Helper.combineCoords(x, z);
				IslandMap m = new IslandMap(ISLAND_SIZE, seed);
				m.newIsland(ip);
				m.readFromNBT(nbt.getCompoundTag("mapdata"));
				CachedIsland ci = new CachedIsland(m);
				ci.lastAccess = nbt.getLong("lastAccess");
				islandCache.put(Helper.combineCoords(x, z), ci);
				return ci;
			}

		}
		catch (Exception exception)
		{
			TFC.log.warn("Error Loading Island: " + x + ", " + z + " | Will rebuild Island Map");
			//exception.printStackTrace();
		}

		return null;
	}

	private boolean doesMapExist(int x, int z)
	{
		File file1 = world.getSaveHandler().getMapFileFromName("Map " + x + "," + z);

		if(this == instanceClient)
		{
			file1 = new File(".//mods//tfc2//cache//"+ worldSeed +"//Map " + x + "," + z + ".dat");
		}

		return (file1 != null && file1.exists());
	}

	public void forceBuildIsland(int x, int z, long seed)
	{

		for(int i = 0; i < buildThreads.length; i++)
		{
			if(buildThreads[i] == null)
			{
				buildThreads[i] = new ThreadBuildExact(i, "Map Build Thread: "+i, Helper.combineCoords(x, z), seed);
				buildThreads[i].setPriority(2);
				buildThreads[i].addListener(this);
				buildThreads[i].start();
				return;
			}
		}
	}

	public void buildFromQueue()
	{
		if(mapQueue.size() == 0)
			return;

		for(int i = 0; i < buildThreads.length; i++)
		{
			if(buildThreads[i] == null && mapQueue.size() > 0)
			{
				int id = mapQueue.poll();
				if(doesMapExist(Helper.getXCoord(id), Helper.getYCoord(id)))
					return;
				buildThreads[i] = new ThreadBuild(i, "Map Build Thread: "+i, id);
				buildThreads[i].setPriority(2);
				buildThreads[i].addListener(this);
				buildThreads[i].start();
			}
		}
	}

	public void runUpdateLoop()
	{
		for(CachedIsland ci : islandCache.values())
		{
			ci.update();
		}
	}

	private class ThreadBuild extends Thread
	{
		protected String threadName;
		protected Thread t;
		protected int id;
		public final int threadID;

		public ThreadBuild(int threadid, String n, int cantorizedID)
		{
			threadName = n;
			id = cantorizedID;
			threadID = threadid;
		}

		private Set<IThreadCompleteListener> listeners = new CopyOnWriteArraySet<IThreadCompleteListener>();
		public void addListener(final IThreadCompleteListener listener) 
		{
			listeners.add(listener);
		}
		public void removeListener(final IThreadCompleteListener listener) 
		{
			listeners.remove(listener);
		}
		private void notifyListeners() 
		{
			for (IThreadCompleteListener listener : listeners) 
			{
				listener.notifyOfThreadComplete(this);
			}
		}

		@Override
		public void run()
		{
			try
			{
				createIsland(Helper.getXCoord(id),Helper.getYCoord(id));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally 
			{
				notifyListeners();
			}
		}

		@Override
		public void start()
		{
			if (t == null)
			{
				t = new Thread (this, threadName);
				t.start();
			}
		}
	}

	@Override
	public void notifyOfThreadComplete(Thread thread) 
	{
		buildThreads[((ThreadBuild)thread).threadID] = null;
	}

	private class ThreadBuildExact extends ThreadBuild
	{
		private long seed;

		public ThreadBuildExact(int threadid, String n, int cantorizedID, long seed)
		{
			super(threadid, n, cantorizedID);
			this.seed = seed;
		}

		private final Set<IThreadCompleteListener> listeners = new CopyOnWriteArraySet<IThreadCompleteListener>();
		@Override
		public void addListener(final IThreadCompleteListener listener) 
		{
			listeners.add(listener);
		}
		@Override
		public void removeListener(final IThreadCompleteListener listener) 
		{
			listeners.remove(listener);
		}
		private void notifyListeners() 
		{
			for (IThreadCompleteListener listener : listeners) 
			{
				listener.notifyOfThreadComplete(this);
			}
		}

		@Override
		public void run()
		{
			try
			{
				if(doesMapExist(Helper.getXCoord(id),Helper.getYCoord(id)))
				{
					if(loadMap(Helper.getXCoord(id),Helper.getYCoord(id)) == null)
						createIsland(Helper.getXCoord(id),Helper.getYCoord(id), seed, true);
				}
				else
				{
					createIsland(Helper.getXCoord(id),Helper.getYCoord(id), seed, true);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally 
			{
				int size = recentlyRequestedMaps.size();
				for(int i = 0; i < size; i++)
				{
					if(recentlyRequestedMaps.get(i) == id)
					{
						recentlyRequestedMaps.remove(i);
					}
				}
				ClientRenderHandler.IsGeneratingFirstIsland  = false;
				notifyListeners();
			}

			ClientRenderHandler.IsGeneratingFirstIsland  = false;
		}

		@Override
		public void start()
		{
			if (t == null)
			{
				t = new Thread (this, threadName);
				t.start();
			}
		}
	}

}
