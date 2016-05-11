package com.bioxx.jmapgen.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.bioxx.jmapgen.dungeon.RoomSchematic.RoomType;

public class DungeonSchemManager 
{
	private static DungeonSchemManager instance = new DungeonSchemManager();
	public Map<String, ArrayList<RoomSchematic>> themeMap = new HashMap<String, ArrayList<RoomSchematic>>();

	public static DungeonSchemManager getInstance()
	{
		return instance;
	}

	public void loadRooms(String theme, ArrayList<String> roomNames, String path)
	{
		ArrayList<RoomSchematic> rooms;
		if(themeMap.containsKey(theme.toLowerCase()))
			rooms = themeMap.get(theme.toLowerCase());
		else
			rooms = new ArrayList<RoomSchematic>();

		for(String s : roomNames)
		{
			RoomSchematic r = new RoomSchematic(path+s+".schematic", s);
			r.Load();
			r.PostProcess();
			rooms.add(r);
		}
		themeMap.put(theme.toLowerCase(), rooms);
	}

	public String getRandomTheme(Random r)
	{
		if(themeMap.size() == 1)
			return themeMap.keySet().iterator().next();
		else
		{
			return (String)themeMap.keySet().toArray()[r.nextInt(themeMap.size())];
		}
	}

	public RoomSchematic getSchematic(String theme, String schemName)
	{
		ArrayList<RoomSchematic> rooms = themeMap.get(theme.toLowerCase());
		for(RoomSchematic rs : rooms)
		{
			if(rs.getFileName().equalsIgnoreCase(schemName))
				return rs;
		}
		return null;
	}

	public RoomSchematic getRandomEntrance(Random random, String theme)
	{
		ArrayList<RoomSchematic> rooms = new ArrayList<RoomSchematic>();
		for(RoomSchematic rs : themeMap.get(theme.toLowerCase()))
		{
			if(rs.getRoomType() == RoomType.Entrance)
				rooms.add(rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.get(random.nextInt(rooms.size()));
	}

	public RoomSchematic getRandomRoomForDirection(Random random, String theme, DungeonDirection dir, RoomType rt)
	{
		ArrayList<RoomSchematic> rooms = new ArrayList<RoomSchematic>();
		for(RoomSchematic rs : themeMap.get(theme.toLowerCase()))
		{
			if(rs.connections.contains(dir) && rs.getRoomType() == rt)
				rooms.add(rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.get(random.nextInt(rooms.size()));
	}

	public RoomSchematic getRandomRoomForDirection(Random random, String theme, DungeonDirection dir)
	{
		return getRandomRoomForDirection(random, theme, dir, RoomType.Normal);
	}
}
