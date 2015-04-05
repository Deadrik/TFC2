// Display the voronoi graph produced in Map.as
// Author: amitp@cs.stanford.edu
// License: MIT

package jMapGen;


public class IslandMapGen
{
	// Island shape is controlled by the islandRandom seed and the
	// type of island. The islandShape function uses both of them to
	// determine whether any point should be water or land.
	static final long islandSeedInitial = 0;

	// The map data
	public Map map;
	//public Roads roads;
	public Lava lava;


	public IslandMapGen(long s, int size) 
	{
		map = new Map(size, s);
		createNewIsland(s);
	}

	public IslandMapGen(long s, int size, IslandDefinition is) 
	{
		map = new Map(size, s);
		createNewIsland(s, is);
	}

	public void createNewIsland(long seed) 
	{
		map.newIsland(seed);
		map.go();
	}

	public void createNewIsland(long seed, IslandDefinition is) 
	{
		map.newIsland(seed, is);
		map.go();
	}
}

