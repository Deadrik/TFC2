package com.bioxx.tfc2.api.animals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.bioxx.jmapgen.IslandParameters;
import com.bioxx.tfc2.api.interfaces.IAnimalDef;
import com.bioxx.tfc2.api.types.EnumAnimalDiet;

public class AnimalSpawnRegistry 
{
	private static AnimalSpawnRegistry instance = new AnimalSpawnRegistry();

	public static AnimalSpawnRegistry getInstance()
	{
		return instance;
	}

	Map<String, IAnimalDef> definitions = new HashMap<String, IAnimalDef>();

	public void register(IAnimalDef group)
	{
		definitions.put(group.getName(), group);
	}

	public ArrayList<IAnimalDef> getValidSpawnDefs(IslandParameters params, EnumAnimalDiet... diet)
	{
		ArrayList<IAnimalDef> outList = new ArrayList<IAnimalDef>();
		Iterator iter = definitions.values().iterator();
		while(iter.hasNext())
		{
			IAnimalDef group = (IAnimalDef)iter.next();
			boolean correctDiet = false;
			for(EnumAnimalDiet d : diet)
				if(group.getAnimalDiet() == d)
					correctDiet = true;

			if(correctDiet && group.canSpawn(params))
				outList.add(group);
		}
		return outList;
	}

	public IAnimalDef getDefFromName(String animalType)
	{
		return definitions.get(animalType);
	}

}
