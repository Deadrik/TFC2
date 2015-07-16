package com.bioxx.tfc2.api.Trees;

public class TreeConfig 
{
	public String name;
	/**
	 * The Tree Index. This is used for assigning textures, creating items, and 
	 * choosing the correct tree schematics during world generation.
	 */
	public int index = -1;
	/**
	 * Minimum Allowed Moisture
	 */
	public double minMoisture;
	/**
	 * Maximum Allowed Moisture
	 */
	public double maxMoisture;
	/**
	 * Minimum Allowed Temperature
	 */
	public double minTemp;
	/**
	 * Maximum Allowed Temperature
	 */
	public double maxTemp;

	public boolean isEvergreen;

	public TreeConfig(String n, int i, double minR, double maxR, double minT, double maxT, boolean eg)
	{
		name = n;
		index = i;
		minMoisture = minR;
		maxMoisture = maxR;
		minTemp = minT;
		maxTemp = maxT;
		isEvergreen = eg;
	}


}
