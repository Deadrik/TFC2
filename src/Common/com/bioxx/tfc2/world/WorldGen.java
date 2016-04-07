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
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.RandomCollection;
import com.bioxx.tfc2.TFC;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.events.IslandGenEvent;
import com.bioxx.tfc2.api.trees.TreeRegistry;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.api.util.Helper;
import com.bioxx.tfc2.api.util.IThreadCompleteListener;
import com.bioxx.tfc2.networking.server.ServerMapRequestPacket;


public class WorldGen implements IThreadCompleteListener
{
	public static WorldGen instance;
	final java.util.Map<Integer, CachedIsland> islandCache;
	java.util.Map<Integer, CachedIsland> clientIslandCache;
	public World world;
	public static final int ISLAND_SIZE = 4096;

	private Queue<Integer> mapQueue;
	private ThreadBuild[] buildThreads;

	public WorldGen(World w) 
	{
		world = w;
		islandCache = Collections.synchronizedMap(new ConcurrentHashMap<Integer, CachedIsland>());
		clientIslandCache = Collections.synchronizedMap(new ConcurrentHashMap<Integer, CachedIsland>());
		mapQueue = new PriorityBlockingQueue<Integer>();
		buildThreads = new ThreadBuild[TFCOptions.maxThreadsForIslandGen];
	}

	public static void initialize(World world)
	{
		if(instance == null)
			instance = new WorldGen(world);
	}

	public IslandMap getClientIslandMap(int x, int z)
	{
		int id = Helper.combineCoords(x, z);
		if(!clientIslandCache.containsKey(id))
		{
			createFakeMap(x, z);
		}
		CachedIsland ci = clientIslandCache.get(id);
		return ci.getIslandMap();
	}

	/**
	 * Retrieves an island map from the cache or creates it if needed. This is a pass-through method to an internal method which retrieves
	 * the island map and hands neighboring island maps off to other threads for generation. Coordinates should already be in MapCoords form.
	 */
	public IslandMap getIslandMap(int x, int z)
	{
		int id = Helper.combineCoords(x, z);

		//First we try to load the map from disk if it exists
		if(!islandCache.containsKey(id))
		{
			loadMap(x, z);
		}
		//If the map did not exist on disk then create it from scratch
		if(!islandCache.containsKey(id))
		{
			createIsland(x, z);
		}

		return getMap(x, z);
	}

	@SideOnly(Side.CLIENT)
	private IslandMap createFakeMap(int x, int z)
	{
		long seed = world.getSeed()+Helper.combineCoords(x, z);
		TFC.network.sendToServer(new ServerMapRequestPacket(x, z));
		return createFakeMap(x, z, seed);
	}

	@SideOnly(Side.CLIENT)
	public IslandMap createFakeMap(int x, int z, long seed)
	{
		IslandParameters id = createParams(seed, x, z);
		IslandMap mapgen = new IslandMap(ISLAND_SIZE, seed);
		mapgen.newIsland(id);
		mapgen.generateFake();
		CachedIsland ci = new CachedIsland(mapgen);
		clientIslandCache.put(Helper.combineCoords(x, z), ci);
		return mapgen;
	}

	public void addMap(IslandMap map, int x, int z)
	{
		CachedIsland ci = new CachedIsland(map);
		islandCache.put(Helper.combineCoords(x, z), ci);
		System.out.println("Added Map: " + x + "," + z );
	}

	private IslandMap getMap(int x, int z)
	{
		int id = Helper.combineCoords(x, z);
		CachedIsland ci = islandCache.get(id);
		//Should only ever be 0 if this map was created but never accessed by the game.
		if(ci.lastAccess == 0)
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

	public boolean isMapLoaded(int x, int z)
	{
		return isMapLoaded(Helper.combineCoords(x, z));
	}

	public boolean isMapLoaded(int id)
	{
		return islandCache.get(id) != null;
	}

	private IslandMap createIsland(int x, int z)
	{
		long seed = world.getSeed()+Helper.combineCoords(x, z);
		IslandGenEvent.Pre preEvent = new IslandGenEvent.Pre(createParams(seed, x, z));
		Global.EVENT_BUS.post(preEvent);
		IslandMap mapgen = new IslandMap(ISLAND_SIZE, seed);
		mapgen.newIsland(preEvent.params);

		mapgen.getIslandData().islandLevel = Math.abs(x);
		if(x == 0)
		{
			mapgen.getIslandData().unlockIsland();
		}

		mapgen.generateFull();
		IslandGenEvent.Post postEvent = new IslandGenEvent.Post(mapgen);
		Global.EVENT_BUS.post(postEvent);
		CachedIsland ci = new CachedIsland(postEvent.islandMap);
		saveMap(ci);
		islandCache.put(Helper.combineCoords(x, z), ci);
		return ci.islandData;
	}

	private IslandParameters createParams(long seed, int x, int z)
	{
		IslandParameters id = new IslandParameters(seed, ISLAND_SIZE, 0.5, 0.3);
		Random r = new Random(seed);
		id.setCoords(x, z);
		int fcount = 2+r.nextInt(2+r.nextInt(3));
		Feature.setupFeatures(r);
		//Choose Features
		for(int i = 0; i < fcount; i++)
		{
			Feature f = Feature.getRandomFeature();
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
			t = r.nextBoolean() ? ClimateTemp.TEMPERATE : ClimateTemp.SUBTROPICAL;
		}
		else if(Math.abs(z) == 2)
		{
			t = ClimateTemp.TEMPERATE;
		}
		else if(Math.abs(z) == 3)
		{
			t = r.nextBoolean() ? ClimateTemp.TEMPERATE : ClimateTemp.SUBPOLAR;
		}
		else if(Math.abs(z) == 4)
		{
			t = ClimateTemp.POLAR;
		}

		id.setIslandTemp(t);


		Moisture m = Moisture.fromVal(r.nextDouble());
		id.setIslandMoisture(m);

		if(m == Moisture.LOW && r.nextDouble() < 0.4)
		{
			id.setFeatures(Feature.Desert);
		}

		id.setFeatures(Feature.Desert);

		String common = TreeRegistry.instance.getRandomTreeTypeForIsland(r, t, m);
		String uncommon = TreeRegistry.instance.getRandomTreeTypeForIsland(r, t, m);
		String rare = TreeRegistry.instance.getRandomTreeTypeForIsland(r, t, m);
		id.setTrees(common, uncommon, rare);

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
			clientIslandCache.clear();
		}
	}

	public void trimCache()
	{
		long now = System.currentTimeMillis();
		int key;
		Set<Integer> keys = islandCache.keySet();
		CachedIsland c;
		for(Iterator<Integer> iter = keys.iterator(); iter.hasNext();)
		{
			key = iter.next();
			c = islandCache.get(key);
			if(c != null && now-c.lastAccess > 20000)//20 seconds of no access will trim the map
			{
				saveMap(c);
				islandCache.remove(key);
			}
		}

		keys = clientIslandCache.keySet();

		for(Iterator<Integer> iter = keys.iterator(); iter.hasNext();)
		{
			key = iter.next();
			c = clientIslandCache.get(key);
			if(c != null && now-c.lastAccess > 360000)//5 minutes of no access will trim the map
			{
				clientIslandCache.remove(key);
			}
		}
	}

	public void saveMap(CachedIsland island)
	{
		try
		{
			File file1 = world.getSaveHandler().getMapFileFromName("Map " + island.islandData.getParams().getXCoord() + "," + 
					island.islandData.getParams().getZCoord());
			if (file1 != null)
			{
				NBTTagCompound islandNBT = new NBTTagCompound();
				island.islandData.writeToNBT(islandNBT);

				NBTTagCompound finalNBT = new NBTTagCompound();
				finalNBT.setTag("mapdata", islandNBT);
				island.islandData.getParams().writeToNBT(finalNBT);

				finalNBT.setLong("lastAccess", island.lastAccess);

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

			if (file1 != null && file1.exists())
			{
				FileInputStream input = new FileInputStream(file1);
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(input);
				input.close();
				IslandParameters ip = new IslandParameters();
				ip.readFromNBT(nbt);
				long seed = world.getSeed()+Helper.combineCoords(x, z);
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

		return (file1 != null && file1.exists());
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
		private String threadName;
		private Thread t;
		private int id;
		public final int threadID;

		public ThreadBuild(int threadid, String n, int cantorizedID)
		{
			threadName = n;
			id = cantorizedID;
			threadID = threadid;
		}

		private final Set<IThreadCompleteListener> listeners = new CopyOnWriteArraySet<IThreadCompleteListener>();
		public final void addListener(final IThreadCompleteListener listener) 
		{
			listeners.add(listener);
		}
		public final void removeListener(final IThreadCompleteListener listener) 
		{
			listeners.remove(listener);
		}
		private final void notifyListeners() 
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

}
