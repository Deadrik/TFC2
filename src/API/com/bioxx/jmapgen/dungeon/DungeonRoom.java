package com.bioxx.jmapgen.dungeon;

import java.util.HashMap;
import java.util.Map;

public class DungeonRoom 
{
	public final RoomSchematic schematic;
	Map<DungeonDirection, RoomLink> linkMap = new HashMap<DungeonDirection, RoomLink>();
	RoomPos position;

	public DungeonRoom(RoomSchematic rs, RoomPos pos)
	{
		schematic = rs;
		position = pos;
	}

	public void addConnection(DungeonDirection c, RoomLink r)
	{
		linkMap.put(c, r);
	}

	public RoomLink getConnection(DungeonDirection c)
	{
		if(linkMap.containsKey(c))
			return linkMap.get(c);
		else return null;
	}

	public boolean hasConnection(DungeonDirection c)
	{
		return linkMap.containsKey(c);
	}

	public RoomPos getPosition() {
		return position;
	}

	public void removeConnection(DungeonDirection dir)
	{
		linkMap.remove(dir);
	}

	public void clearConnections()
	{
		for(DungeonDirection dir : DungeonDirection.values())
		{
			RoomLink rl = getConnection(dir);
			if(rl != null)
			{
				rl.room.removeConnection(dir.getOpposite());
				removeConnection(dir);
			}
		}
	}
}
