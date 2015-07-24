package com.bioxx.tfc2.api.Types;


public enum ClimateTemp
{
	POLAR(0), SUBPOLAR(0.25), TEMPERATE(0.5), SUBTROPICAL(0.75), TROPICAL(1);

	double temp;

	ClimateTemp(double d)
	{
		temp = d;
	}

	public double getTemp()
	{
		return temp;
	}
}