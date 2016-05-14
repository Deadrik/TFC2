package com.bioxx.jmapgen.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.bioxx.jmapgen.RandomCollection;
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
			r.setTheme(theme);
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
		for(RoomSchematic rs : themeMap.get(theme.toLowerCase()))
		{
			if(rs.getFileName().equalsIgnoreCase(schemName))
				return rs;
		}
		return null;
	}

	public RoomSchematic getRandomEntrance(Random random, String theme)
	{
		RandomCollection<RoomSchematic> rooms = new RandomCollection<RoomSchematic>();
		for(RoomSchematic rs : themeMap.get(theme.toLowerCase()))
		{
			if(rs.getRoomType() == RoomType.Entrance && rs.getChooseWeight() > 0)
				rooms.add(rs.getChooseWeight(), rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.next();
	}

	public RoomSchematic getRandomRoomForDirection(Random random, String theme, DungeonDirection dir, RoomType rt)
	{
		RandomCollection<RoomSchematic> rooms = new RandomCollection<RoomSchematic>();
		for(RoomSchematic rs : themeMap.get(theme.toLowerCase()))
		{
			//Schematic must be able to connect in this direction, but must not have a matching schematic for this direction registered.
			if(rs.getConnections().contains(dir) && rs.getSetPieceMap().get(new RoomPos(0,0,0).offset(dir)) == null && rs.getRoomType() == rt && rs.getChooseWeight() > 0)
				rooms.add(rs.getChooseWeight(), rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.next();
	}

	public RoomSchematic getRandomRoomForDirection(Random random, String theme, DungeonDirection dir)
	{
		return getRandomRoomForDirection(random, theme, dir, RoomType.Normal);
	}

	public RoomSchematic getRandomRoomSingleDirection(Random random, String theme, DungeonDirection dir)
	{
		RandomCollection<RoomSchematic> rooms = new RandomCollection<RoomSchematic>();
		for(RoomSchematic rs : themeMap.get(theme.toLowerCase()))
		{
			//Schematic must be able to connect in this direction, but must not have a matching schematic for this direction registered.
			if(rs.getConnections().contains(dir) && rs.getConnections().size() == 1 && rs.getSetPieceMap().get(new RoomPos(0,0,0).offset(dir)) == null && rs.getRoomType() == RoomType.Normal && rs.getChooseWeight() > 0)
				rooms.add(rs.getChooseWeight(), rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.next();
	}
}
