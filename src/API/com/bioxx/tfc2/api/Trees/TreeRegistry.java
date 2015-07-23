package com.bioxx.tfc2.api.Trees;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import com.bioxx.tfc2.api.Types.Moisture;
import com.bioxx.tfc2.api.Types.Temp;
import com.bioxx.tfc2.api.Types.WoodType;

public class TreeRegistry
{
	public static TreeRegistry instance = new TreeRegistry();
	private HashMap<String, TreeConfig> treeTypeHash = new HashMap<String, TreeConfig>();
	private Vector<TreeSchemManager> treeList;

	public TreeRegistry()
	{
		treeList = new Vector<TreeSchemManager>();
	}

	public void RegisterSchematic(TreeSchematic treeSchematic, String name)
	{
		WoodType index = checkValidity(name);

		if(index == null)
		{
			System.out.println("[TFC2] Registering Tree Type \"" + name + "\" Failed! There are no trees registered with that name.");
		}
		else
		{
			if(treeList.size() < treeTypeHash.size())
				treeList.setSize(treeTypeHash.size());

			if(treeList.get(index.getMeta()) == null)
				treeList.set(index.getMeta(), new TreeSchemManager(index.getMeta()));

			treeList.get(index.getMeta()).addSchem(treeSchematic);
		}
	}

	public String[] getTreeNames()
	{
		return (String[])treeTypeHash.keySet().toArray(new String[treeTypeHash.size()]);
	}

	public TreeSchematic getRandomTreeSchematic(Random R)
	{
		return treeList.get(R.nextInt(treeList.size())).getRandomSchematic(R);
	}

	/**
	 * @return Returns a random schematic for a specific tree type at any growth stage
	 */
	public TreeSchematic getRandomTreeSchematic(Random R, int treeID)
	{
		if(treeID > treeList.size() - 1) return null;
		return treeList.get(treeID).getRandomSchematic(R);
	}

	/**
	 * @return Returns a random schematic for a specific tree type at a specific growth stage
	 */
	public TreeSchematic getRandomTreeSchematic(Random R, int treeID, int growthStage)
	{
		if(treeID > treeList.size() - 1) return null;
		return treeList.get(treeID).getRandomSchematic(R, growthStage);
	}

	/**
	 * @return Returns a specific schematic
	 */
	public TreeSchematic getTreeSchematic(int treeID, int schemID, int growthStage)
	{
		if(treeID > treeList.size() - 1) return null;
		return treeList.get(treeID).getSchematic(schemID, growthStage);
	}

	public void addTreeType(TreeConfig configuration)
	{
		if(!treeTypeHash.containsKey(configuration.name))
		{
			treeTypeHash.put(configuration.name, configuration);
		}
	}

	public WoodType checkValidity(String n)
	{
		WoodType type = indexFromString(n);
		if(type != null ) return type;
		return null;
	}

	/**
	 * @param n Name of the Tree type. Used as the Key in the hash map for lookups.
	 * @return Tree index that is unique to that tree
	 */
	public WoodType indexFromString(String n)
	{
		if(treeTypeHash.containsKey(n))
			return ((TreeConfig) treeTypeHash.get(n)).wood;
		return null;
	}

	/**
	 * @param n Name of the Tree type. Used as the Key in the hash map for lookups.
	 * @return Full TreeConfig object
	 */
	public TreeConfig treeFromString(String n)
	{
		if(treeTypeHash.containsKey(n))
			return ((TreeConfig) treeTypeHash.get(n));
		return null;
	}
	/**
	 * @param id The tree type ID.
	 * @return Full TreeConfig object
	 */
	public TreeConfig treeFromID(int id)
	{
		WoodType wood = WoodType.getTypeFromMeta(id);
		for(;treeTypeHash.values().iterator().hasNext();)
		{
			TreeConfig config = (TreeConfig)treeTypeHash.values().iterator().next();
			if(config.wood == wood)
				return config;
		}
		return null;
	}

	public TreeConfig getRandomTree()
	{
		int id = new Random().nextInt(treeTypeHash.values().toArray().length);
		return treeFromID(id);
	}

	public String getRandomTreeTypeForIsland(Random r, Temp temp, Moisture moisture)
	{
		ArrayList<String> list = new ArrayList<String>();
		Iterator iter = treeTypeHash.keySet().iterator();
		while(iter.hasNext())
		{
			String tree = (String) iter.next();
			TreeConfig tc = treeFromString(tree);
			if(tc.minTemp.getTemp() <= temp.getTemp() && tc.maxTemp.getTemp() >= temp.getTemp() && 
					tc.minMoisture.getMoisture() <= moisture.getMoisture() && tc.maxMoisture.getMoisture() >= moisture.getMoisture())
				list.add(tree);
		}
		if(list.size() == 0)
			return null;
		if(list.size() == 1)
			return list.get(0);

		return list.get(r.nextInt(list.size()));
	}
}
