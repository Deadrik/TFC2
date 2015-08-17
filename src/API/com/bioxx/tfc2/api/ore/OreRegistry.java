package com.bioxx.tfc2.api.ore;

import java.util.HashMap;

import com.bioxx.tfc2.api.types.StoneType;

public class OreRegistry 
{
	private HashMap<StoneType, HashMap<String, OreConfig>> oreMap = new HashMap<StoneType, HashMap<String, OreConfig>>();

	private static OreRegistry instance = new OreRegistry();

	public static OreRegistry getInstance()
	{
		return instance;
	}

	private OreRegistry()
	{
		//Prepare the hashmap for all stone types.
		for(StoneType s : StoneType.values())
		{
			oreMap.put(s, new HashMap<String, OreConfig>());
		}
	}

	public void registerOre(String n, OreConfig c, StoneType... types)
	{
		for(StoneType st : types)
		{
			oreMap.get(st).put(n, c);
		}
	}

	public OreConfig getConfig(String n, StoneType st)
	{
		return oreMap.get(st).get(n);
	}

	public OreConfig[] getConfigsForStone(StoneType st)
	{
		return oreMap.get(st).values().toArray(new OreConfig[0]);
	}

	public boolean isOreRegistered(StoneType st, String n)
	{
		return oreMap.get(st).containsKey(n);
	}
}
