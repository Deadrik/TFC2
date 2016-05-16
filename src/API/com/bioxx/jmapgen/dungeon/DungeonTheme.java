package com.bioxx.jmapgen.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.bioxx.jmapgen.RandomCollection;
import com.bioxx.jmapgen.dungeon.RoomSchematic.RoomType;

public class DungeonTheme 
{
	String themeName;
	Map<String, RoomSchematic> schematicMap;
	boolean canBeMainDungeon = true;
	EntranceType entranceType = EntranceType.Vertical;

	public DungeonTheme(String n)
	{
		themeName = n;
		schematicMap = new HashMap<String, RoomSchematic>();
	}

	public void loadRooms(ArrayList<String> roomNames, String path)
	{
		for(String s : roomNames)
		{
			RoomSchematic r = new RoomSchematic(path+s+".schematic", s);
			r.setTheme(themeName);
			r.Load();
			r.PostProcess();
			schematicMap.put(s.toLowerCase(), r);
		}
	}

	public RoomSchematic getSchematic(String n)
	{
		return schematicMap.get(n.toLowerCase());
	}

	public RoomSchematic getRandomEntrance(Random random)
	{
		RandomCollection<RoomSchematic> rooms = new RandomCollection<RoomSchematic>();
		for(RoomSchematic rs : schematicMap.values())
		{
			if(rs.getRoomType() == RoomType.Entrance && rs.getChooseWeight() > 0)
				rooms.add(rs.getChooseWeight(), rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.next();
	}

	public RoomSchematic getRandomRoomForDirection(Random random, DungeonDirection dir, RoomType rt)
	{
		RandomCollection<RoomSchematic> rooms = new RandomCollection<RoomSchematic>();
		for(RoomSchematic rs : schematicMap.values())
		{
			//Schematic must be able to connect in this direction, but must not have a matching schematic for this direction registered.
			if(rs.getConnections().contains(dir) && rs.getSetPieceMap().get(new RoomPos(0,0,0).offset(dir)) == null && rs.getRoomType() == rt && rs.getChooseWeight() > 0)
				rooms.add(rs.getChooseWeight(), rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.next();
	}

	public RoomSchematic getRandomRoomForDirection(Random random, DungeonDirection dir)
	{
		return getRandomRoomForDirection(random, dir, RoomType.Normal);
	}

	public RoomSchematic getRandomRoomSingleDirection(Random random, DungeonDirection dir)
	{
		RandomCollection<RoomSchematic> rooms = new RandomCollection<RoomSchematic>();
		for(RoomSchematic rs : schematicMap.values())
		{
			//Schematic must be able to connect in this direction, but must not have a matching schematic for this direction registered.
			if(rs.getConnections().contains(dir) && rs.getConnections().size() == 1 && rs.getSetPieceMap().get(new RoomPos(0,0,0).offset(dir)) == null && rs.getRoomType() == RoomType.Normal && rs.getChooseWeight() > 0)
				rooms.add(rs.getChooseWeight(), rs);
		}

		if(rooms.size() == 0)
			return null;

		return rooms.next();
	}

	public String getThemeName() {
		return themeName;
	}

	public boolean isCanBeMainDungeon() {
		return canBeMainDungeon;
	}

	public void setCanBeMainDungeon(boolean canBeMainDungeon) {
		this.canBeMainDungeon = canBeMainDungeon;
	}

	public static enum EntranceType
	{
		Vertical("vertical"), Horizontal("horizontal");

		String name;
		EntranceType(String n)
		{
			name = n;
		}

		public static EntranceType fromString(String s)
		{
			if(s.equals(Vertical.name))
				return Vertical;
			else if(s.equals(Horizontal.name))
				return Horizontal;
			else return null;
		}
	}

	public EntranceType getEntranceType() {
		return entranceType;
	}

	public void setEntranceType(EntranceType entranceType) {
		this.entranceType = entranceType;
	}
}
