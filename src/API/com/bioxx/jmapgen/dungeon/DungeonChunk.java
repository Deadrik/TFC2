package com.bioxx.jmapgen.dungeon;

public class DungeonChunk 
{
	public final int chunkX;
	public final int chunkZ;

	private DungeonRoom[] Rooms = new DungeonRoom[8];

	public DungeonChunk(int x, int z)
	{
		chunkX = x;
		chunkZ = z;
	}

	public DungeonRoom get(int y)
	{
		if(y < 0 || y >= 8)
			return null;
		return Rooms[y];
	}

	public void set(int y, DungeonRoom room)
	{
		if(y < 0 || y >= 8)
			return;
		Rooms[y] = room;
	}
}
