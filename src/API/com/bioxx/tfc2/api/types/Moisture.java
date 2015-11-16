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

	/**
	 * Used by the weather manager to determine the required precipitation value before rain actually 
	 * begins to fall. This is inverse from the raw moisture value for each enum value. e.x. An island
	 * with a LOW moisture value has a raw value of 0.2. This means that the precipitation value will
	 * need to be at least 0.8 before rain will actually begin to fall.
	 */
	public double getInverse()
	{
		return 1.0-val;
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