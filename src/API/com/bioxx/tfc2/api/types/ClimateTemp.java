package com.bioxx.tfc2.api.types;


public enum ClimateTemp
{
	POLAR(0, -50.0, 0.0, 10.0), 
	SUBPOLAR(0.25, -25.0, 15.0, 10.0), 
	TEMPERATE(0.5, -10.0, 30.0, 5.0), 
	SUBTROPICAL(0.75, 10.0, 35.0, 5.0), 
	TROPICAL(1, 25.0, 40.0, 5.0);

	double mapTemp;
	double weatherTempMin;
	double weatherTempMax;
	double weatherVariance;//this is how much the temperature can fluctuate outside of the base range. Used for local/daily temperatures

	ClimateTemp(double d, double min, double max, double variance)
	{
		mapTemp = d;
		weatherTempMin = min;
		weatherTempMax = max;
		weatherVariance = variance;
	}

	public double getMapTemp()
	{
		return mapTemp;
	}

	public double getTempMin()
	{
		return weatherTempMin;
	}

	public double getTempMax()
	{
		return weatherTempMax;
	}

	public double getTempVar()
	{
		return weatherVariance;
	}

	public boolean isCoolerThan(ClimateTemp m)
	{
		return mapTemp < m.getMapTemp();
	}

	public boolean isCoolerThanOrEqual(ClimateTemp m)
	{
		return mapTemp <= m.getMapTemp();
	}

	public boolean isWarmerThan(ClimateTemp m)
	{
		return mapTemp > m.getMapTemp();
	}

	public boolean isWarmerThanOrEqual(ClimateTemp m)
	{
		return mapTemp >= m.getMapTemp();
	}
}