package com.bioxx.tfc2.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Iterator;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.PriorityBlockingQueue;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import com.bioxx.jMapGen.IslandParameters;
import com.bioxx.jMapGen.IslandParameters.Feature;
import com.bioxx.jMapGen.Map;
import com.bioxx.jMapGen.RandomCollection;
import com.bioxx.tfc2.api.TFCOptions;
import com.bioxx.tfc2.api.Util.Helper;
import com.bioxx.tfc2.api.Util.IThreadCompleteListener;


public class WorldGen implements IThreadCompleteListener
{
	public static WorldGen instance;
	java.util.Map<Integer, CachedIsland> islandCache;
	World world;
	public static final int ISLAND_SIZE = 4096;

	private Queue<Integer> mapQueue;
	private ThreadBuild[] buildThreads;

	public WorldGen(World w) 
	{
		world = w;
		islandCache = Collections.synchronizedMap(new ConcurrentHashMap<Integer, CachedIsland>());
		mapQueue = new PriorityBlockingQueue<Integer>();
		buildThreads = new ThreadBuild[TFCOptions.maxThreadsForIslandGen];
	}

	public static void initialize(World world)
	{
		if(instance == null)
			instance = new WorldGen(world);
	}

	/**
	 * Retrieves an island map from the cache or creates it if needed. This is a pass-through method to an internal method which retrieves
	 * the island map and hands neighboring island maps off to other threads for generation. Coordinates should already be in MapCoords form.
	 */
	public Map getIslandMap(int x, int z)
	{
		int id = Helper.cantorize(x, z);

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

	private Map getMap(int x, int z)
	{
		int id = Helper.cantorize(x, z);
		CachedIsland ci = islandCache.get(id);
		//Should only ever be 0 if this map was created but never accessed by the game.
		if(ci.lastAccess == 0)
		{
			//Add the neighbor maps to the mapQueue for generation in another thread
			mapQueue.add(Helper.cantorize(x+1, z));
			mapQueue.add(Helper.cantorize(x+1, z-1));
			mapQueue.add(Helper.cantorize(x, z-1));
			mapQueue.add(Helper.cantorize(x-1, z-1));
			mapQueue.add(Helper.cantorize(x-1, z));
			mapQueue.add(Helper.cantorize(x-1, z+1));
			mapQueue.add(Helper.cantorize(x, z+1));
			mapQueue.add(Helper.cantorize(x+1, z+1));
		}

		return ci.getIslandMap();
	}

	private Map createIsland(int x, int z)
	{
		long seed = world.getSeed()+Helper.cantorize(x, z);
		IslandParameters id = createParams(seed, x, z);
		Map mapgen = new Map(4096, seed);
		mapgen.newIsland(id);
		mapgen.go();
		CachedIsland ci = new CachedIsland(mapgen);
		saveMap(ci);
		islandCache.put(Helper.cantorize(x, z), ci);
		return mapgen;
	}

	private IslandParameters createParams(long seed, int x, int z)
	{
		IslandParameters id = new IslandParameters(seed, ISLAND_SIZE, 0.5, 0.3);
		Random r = new Random(seed);
		id.setCoords(x, z);
		int fcount = 1+r.nextInt(1+r.nextInt(2));
		Feature.setupFeatures(r);
		//Choose Features
		for(int i = 0; i < fcount; i++)
		{
			Feature f = Feature.getRandomFeature();
			if(f == Feature.Canyons)
				id.setFeatures(Feature.Gorges);

			if((f == Feature.SharperMountains || f == Feature.EvenSharperMountains) && 
					(id.hasFeature(Feature.SharperMountains) || id.hasFeature(Feature.EvenSharperMountains)))
			{
				i--; 
				continue;
			}

			if(id.hasFeature(f)){i--; continue;}
			else id.setFeatures(f);
		}

		RandomCollection<Integer> heightPot = new RandomCollection<Integer>();
		heightPot.add(0.1, 64);
		heightPot.add(0.8, 96);
		heightPot.add(0.1, 128);
		id.islandMaxHeight = heightPot.next();

		RandomCollection<Double> moisturePot = new RandomCollection<Double>();
		moisturePot.add(0.1, 0.75D);
		moisturePot.add(0.8, 1D);
		moisturePot.add(0.1, 1.25D);
		id.moistureMultiplier = moisturePot.next();

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

		Set<Integer> keys = islandCache.keySet();

		for(Iterator<Integer> iter = keys.iterator(); iter.hasNext();)
		{
			int key = iter.next();
			CachedIsland c = islandCache.get(key);
			if(now-c.lastAccess > 12000)//12 seconds of no access will trim the map
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
			File file1 = world.getSaveHandler().getMapFileFromName(island.islandData.islandParams.getXCoord() + "," + 
					island.islandData.islandParams.getZCoord());

			if (file1 != null)
			{
				NBTTagCompound dataNBT = new NBTTagCompound();
				island.islandData.writeToNBT(dataNBT);

				NBTTagCompound finalNBT = new NBTTagCompound();
				finalNBT.setTag("data", dataNBT);
				island.islandData.islandParams.writeToNBT(finalNBT);

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
			File file1 = world.getSaveHandler().getMapFileFromName(x + "," + z);

			if (file1 != null && file1.exists())
			{
				FileInputStream input = new FileInputStream(file1);
				NBTTagCompound nbt = CompressedStreamTools.readCompressed(input);
				input.close();
				IslandParameters ip = new IslandParameters();
				ip.readFromNBT(nbt);
				long seed = world.getSeed()+Helper.cantorize(x, z);
				Map m = new Map(ISLAND_SIZE, seed);
				m.newIsland(ip);
				m.readFromNBT(nbt.getCompoundTag("data"));
				CachedIsland ci = new CachedIsland(m);
				ci.lastAccess = nbt.getLong("lastAccess");
				islandCache.put(Helper.cantorize(x, z), ci);
				return ci;
			}

		}
		catch (Exception exception)
		{
			exception.printStackTrace();
		}

		return null;
	}

	private boolean doesMapExist(int x, int z)
	{
		File file1 = world.getSaveHandler().getMapFileFromName(x + "," + z);

		return (file1 != null && file1.exists());
	}

	public void buildFromQueue()
	{
		if(mapQueue.size() == 0)
			return;

		for(int i = 0; i < buildThreads.length; i++)
		{
			if(buildThreads[i] == null)
			{
				int id = mapQueue.poll();
				if(doesMapExist(Helper.cantorX(id), Helper.cantorY(id)))
					return;
				buildThreads[i] = new ThreadBuild(i, "Map Build Thread: "+i, id);
				buildThreads[i].setPriority(2);
				buildThreads[i].addListener(this);
				buildThreads[i].start();
			}
		}
	}

	private class ThreadBuild extends Thread
	{
		private String threadName;
		private Thread t;
		private int cantorID;
		public final int threadID;

		public ThreadBuild(int threadid, String n, int cantorizedID)
		{
			threadName = n;
			cantorID = cantorizedID;
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
				createIsland(Helper.cantorX(cantorID),Helper.cantorY(cantorID));
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
