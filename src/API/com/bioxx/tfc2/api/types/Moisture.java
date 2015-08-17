package com.bioxx.tfc2.api.types;


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

	public boolean isLessThan(Moisture m)
	{
		return val < m.getMoisture();
	}

	public boolean isLessThanOrEqual(Moisture m)
	{
		return val <= m.getMoisture();
	}

	public boolean isGreaterThan(Moisture m)
	{
		return val > m.getMoisture();
	}

	public boolean isGreaterThanOrEqual(Moisture m)
	{
		return val >= m.getMoisture();
	}
}