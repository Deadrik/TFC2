package com.bioxx.jmapgen.dungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DungeonSchemManager 
{
	private static DungeonSchemManager instance = new DungeonSchemManager();
	private Map<String, DungeonTheme> themeMap = new HashMap<String, DungeonTheme>();

	public static DungeonSchemManager getInstance()
	{
		return instance;
	}

	public void loadRooms(String theme, ArrayList<String> roomNames, String path)
	{
		DungeonTheme themeObject;
		if(themeMap.containsKey(theme.toLowerCase()))
			themeObject = themeMap.get(theme.toLowerCase());
		else
			themeObject = new DungeonTheme(theme);

		themeObject.loadRooms(roomNames, path);
		themeMap.put(theme.toLowerCase(), themeObject);
	}

	public DungeonTheme getRandomTheme(Random r)
	{
		if(themeMap.size() == 1)
			return (DungeonTheme)themeMap.values().toArray()[0];
		else
		{
			return (DungeonTheme)themeMap.values().toArray()[r.nextInt(themeMap.size())];
		}
	}

	public DungeonTheme getTheme(String t)
	{
		return themeMap.get(t);
	}
}
