package com.bioxx.jmapgen.dungeon;

public enum DungeonDirection
{
	NORTH("north"), SOUTH("south"), EAST("east"), WEST("west"), UP("up"), DOWN("down");

	public String name;

	DungeonDirection(String s)
	{
		name = s;
	}

	public static DungeonDirection fromString(String s)
	{
		for(DungeonDirection rc : DungeonDirection.values())
		{
			if(rc.name.equalsIgnoreCase(s))
				return rc;
		}
		return null;
	}

	public DungeonDirection getOpposite()
	{
		switch(this)
		{
		case NORTH: 
			return SOUTH;
		case SOUTH: 
			return NORTH;
		case DOWN: 
			return UP;
		case EAST:
			return WEST;
		case UP:
			return DOWN;
		case WEST:
			return EAST;
		default: return null;
		}
	}
}