package com.bioxx.tfc2.api.Types;


public enum Temp
{
	FRIGID(0), COLD(0.16), COOL(0.33), TEMPERATE(0.5), WARM(0.66), HOT(0.83), SCORCHING(1);

	double temp;

	Temp(double d)
	{
		temp = d;
	}

	public double getTemp()
	{
		return temp;
	}
}