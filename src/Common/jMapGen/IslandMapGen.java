// Display the voronoi graph produced in Map.as
// Author: amitp@cs.stanford.edu
// License: MIT

package jMapGen;

import java.awt.Graphics2D;

public class IslandMapGen
{
	static public int SIZE = 4096;
	public static boolean DEBUG = true;

	// Island shape is controlled by the islandRandom seed and the
	// type of island. The islandShape function uses both of them to
	// determine whether any point should be water or land.
	static public long islandSeedInitial = 665;


	// The map data
	public Map map;
	//public Roads roads;
	public Lava lava;
	public NoisyEdges noisyEdges;

	public static Graphics2D graphics;


	public IslandMapGen(int s, Graphics2D g) 
	{
		islandSeedInitial = s;
		graphics = g;
		map = new Map(SIZE, islandSeedInitial);
		createNewIsland();
	}

	public IslandMapGen(long s) 
	{
		islandSeedInitial = s;
		map = new Map(SIZE, islandSeedInitial);
		createNewIsland();
	}

	public void createNewIsland() 
	{
		map.newIsland(islandSeedInitial);
		map.go();
	}
}

