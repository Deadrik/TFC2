package com.bioxx.jmapgen.dungeon;

public class RoomLink
{
	public final DungeonRoom room;
	public final boolean placeDoor;

	public RoomLink(DungeonRoom room, boolean door)
	{
		this.room = room;
		placeDoor = door;
	}
}