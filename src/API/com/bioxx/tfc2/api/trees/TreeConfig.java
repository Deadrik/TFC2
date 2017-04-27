package com.bioxx.tfc2.api.trees;

import net.minecraft.block.state.IBlockState;

import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;

public class TreeConfig 
{
	public String name;

	public IBlockState wood;
	public IBlockState leaves;

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
	public ClimateTemp minTemp;
	/**
	 * Maximum Allowed Temperature
	 */
	public ClimateTemp maxTemp;

	public boolean isEvergreen;
	public boolean isSwampTree = false;

	public TreeConfig(String n, IBlockState w, IBlockState l, Moisture minR, Moisture maxR, ClimateTemp minT, ClimateTemp maxT, boolean eg)
	{
		name = n;
		minMoisture = minR;
		maxMoisture = maxR;
		minTemp = minT;
		maxTemp = maxT;
		isEvergreen = eg;
		wood = w;
		leaves = l;
	}

	public TreeConfig(String n, IBlockState w, IBlockState l, Moisture minR, Moisture maxR, ClimateTemp minT, ClimateTemp maxT, boolean eg, boolean swamp)
	{
		this(n, w, l, minR, maxR, minT, maxT, eg);
		isSwampTree = swamp;
	}
}
