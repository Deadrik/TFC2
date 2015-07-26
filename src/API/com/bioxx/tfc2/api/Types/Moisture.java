package com.bioxx.tfc2.api.Types;


public enum Moisture
{
	LOW(0.2), MEDIUM(0.4), HIGH(0.6), VERYHIGH(0.8), MAX(1.0);

	double val;

	Moisture(double d)
	{
		val = d;
	}

	public double getMoisture()
	{
		return val;
	}

	public static Moisture fromVal(double d)
	{
		if(d <= 0.2)
			return LOW;
		else if(d <= 0.4)
			return MEDIUM;
		else if(d <= 0.6)
			return HIGH;
		else if(d <= 0.8)
			return VERYHIGH;
		else return MAX;
	}
}