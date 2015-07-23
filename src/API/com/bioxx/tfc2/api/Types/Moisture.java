package com.bioxx.tfc2.api.Types;


public enum Moisture
{
	NONE(0), LOW(0.2), MEDIUM(0.4), HIGH(0.6), VERYHIGH(0.8), MAX(1.0);

	double val;

	Moisture(double d)
	{
		val = d;
	}

	public double getMoisture()
	{
		return val;
	}
}