package com.bioxx.tfc2.api;

import java.util.ArrayList;
import java.util.EnumSet;

import net.minecraft.util.IStringSerializable;

import com.bioxx.tfc2.api.types.ClimateTemp;

public class Crop implements IStringSerializable, Comparable
{
	public static final ArrayList<Crop> cropList = new ArrayList<Crop>();

	public static Crop Corn = new Crop("corn", 0, 6);
	public static Crop Cabbage = new Crop("cabbage", 1, 6).setGrowthPeriod(24);
	public static Crop Tomato = new Crop("tomato", 2, 8);
	public static Crop Wheat = new Crop("wheat", 3, 8);
	public static Crop Barley = new Crop("barley", 6, 8);
	public static Crop Rye = new Crop("rye", 4, 8);
	public static Crop Oat = new Crop("oat", 5, 8);
	public static Crop Rice = new Crop("rice", 7, 8);

	String name;
	int id;
	float initialGrowthPeriod = 32f;//Time in days
	int numberOfGrowthStages = 6;
	EnumSet<ClimateTemp> wildGrowthZones = EnumSet.noneOf(ClimateTemp.class);

	public Crop(String name, int id, int numGrowthStages)
	{
		this.name = name;
		this.id = id;
		this.numberOfGrowthStages = numGrowthStages;
		addClimateZones(ClimateTemp.TEMPERATE, ClimateTemp.SUBTROPICAL, ClimateTemp.TROPICAL);
	}

	public static void registerCrop(Crop c)
	{
		cropList.add(c);
	}

	@Override
	public String toString()
	{
		return name;
	}

	public int getID()
	{
		return id;
	}

	public int getGrowthStages()
	{
		return numberOfGrowthStages;
	}

	public Crop setGrowthPeriod(float g)
	{
		this.initialGrowthPeriod = g;
		return this;
	}

	public float getGrowthPeriod()
	{
		return initialGrowthPeriod;
	}

	public Crop addClimateZones(ClimateTemp... ct)
	{
		for(ClimateTemp c : ct)
		{
			wildGrowthZones.add(c);
		}
		return this;
	}

	public Crop setClimateZones(ClimateTemp... ct)
	{
		wildGrowthZones.clear();
		for(ClimateTemp c : ct)
		{
			wildGrowthZones.add(c);
		}
		return this;
	}

	public static Crop fromID(int id)
	{
		for(Crop c : Crop.cropList)
		{
			if(c.getID() == id)
				return c;
		}

		return null;
	}

	@Override
	public String getName() 
	{
		return name;
	}

	@Override
	public int compareTo(Object o)
	{
		if(o instanceof Crop)
		{
			if(((Crop)o).id < id)
				return -1;
			else if(((Crop)o).id == id)
				return 0;
			else if(((Crop)o).id > id)
				return 1;
		}
		return -1;
	}

	public static ArrayList<Crop> getCropsForTemp(ClimateTemp temp)
	{
		ArrayList<Crop> suitableCrops = new ArrayList<Crop>();
		for(Crop c : cropList)
		{
			if(c.wildGrowthZones.contains(temp))
			{
				suitableCrops.add(c);
			}
		}

		return suitableCrops;
	}
}
