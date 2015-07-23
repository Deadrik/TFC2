package com.bioxx.tfc2.api.Trees;

import com.bioxx.tfc2.api.Types.Moisture;
import com.bioxx.tfc2.api.Types.Temp;
import com.bioxx.tfc2.api.Types.WoodType;

public class TreeConfig 
{
	public String name;
	/**
	 * The Tree Wood Type. This is used for assigning textures, creating items, and 
	 * choosing the correct tree schematics during world generation.
	 */
	public WoodType wood;
	/**
	 * Minimum Allowed Moisture
	 */
	public Moisture minMoisture;
	/**
	 * Maximum Allowed Moisture
	 */
	public Moisture maxMoisture;
	/**
	 * Minimum Allowed Temperature
	 */
	public Temp minTemp;
	/**
	 * Maximum Allowed Temperature
	 */
	public Temp maxTemp;

	public boolean isEvergreen;

	public TreeConfig(WoodType i, Moisture minR, Moisture maxR, Temp minT, Temp maxT, boolean eg)
	{
		name = i.getName();
		wood = i;
		minMoisture = minR;
		maxMoisture = maxR;
		minTemp = minT;
		maxTemp = maxT;
		isEvergreen = eg;
	}


}
