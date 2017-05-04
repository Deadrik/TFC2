package com.bioxx.jmapgen.threads;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.pathfinding.CenterPath;
import com.bioxx.jmapgen.pathfinding.CenterPathFinder;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.util.IThreadCompleteListener;

public class ThreadPathfind extends Thread
{
	protected String threadName;
	protected Thread t;
	public int herdID;
	public final int threadID;
	CenterPathFinder pathfinder;
	IslandMap map;
	Center start, end;
	IAnimalDef animal;

	public ThreadPathfind(int threadid, String n, int hid, IAnimalDef def, IslandMap map, Center start, Center end)
	{
		threadName = n;
		herdID = hid;
		threadID = threadid;
		this.map = map;
		pathfinder = new CenterPathFinder(def.getPathProfile());
		this.start = start;
		this.end = end;
		animal = def;
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
			CenterPath p = pathfinder.findPath(map, start, end);
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