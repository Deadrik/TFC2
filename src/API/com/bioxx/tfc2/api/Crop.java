package com.bioxx.tfc2.api;

import java.util.ArrayList;

import net.minecraft.util.IStringSerializable;

public class Crop implements IStringSerializable
{
	public static ArrayList<Crop> cropList = new ArrayList<Crop>();

	public static Crop Corn = new Crop("corn", 0, 6);
	public static Crop Cabbage = new Crop("cabbage", 1, 6).setGrowthPeriod(24);
	public static Crop Tomato = new Crop("tomato", 2, 8);

	String name;
	int id;
	float initialGrowthPeriod = 32f;//Time in days
	int numberOfGrowthStages = 6;

	public Crop(String n, int id, int numGrowthStages)
	{
		name = n;
		this.id = id;
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

	public Crop setGrowthPeriod(float g)
	{
		this.initialGrowthPeriod = g;
		return this;
	}

	public float getGrowthPeriod()
	{
		return initialGrowthPeriod;
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
}
