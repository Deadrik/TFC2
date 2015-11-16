package com.bioxx.tfc2.api.types;


public enum SeasonalPeriod 
{
	EarlySpring(Season.Spring, 0.4), MidSpring(Season.Spring, 0.5), LateSpring(Season.Spring, 0.7), EarlySummer(Season.Summer, 0.8), 
	MidSummer(Season.Summer, 1.0), LateSummer(Season.Summer, 0.8), EarlyFall(Season.Fall, 0.7), MidFall(Season.Fall,0.6), 
	LateFall(Season.Fall, 0.5), EarlyWinter(Season.Winter, 0.3), MidWinter(Season.Winter, 0.0), LateWinter(Season.Winter, 0.2);

	Season baseSeason;
	double tempMult;

	private SeasonalPeriod(Season base, double temp)
	{
		baseSeason = base;
		tempMult = temp;
	}

	public boolean isSeason(Season s)
	{
		return s == baseSeason;
	}

	public static SeasonalPeriod fromInt(int i)
	{
		return SeasonalPeriod.values()[i];
	}

	public double getTempMultiplier()
	{
		return tempMult;
	}

	public SeasonalPeriod prevPeriod()
	{
		int index = this.ordinal() - 1;
		if(index < 0)
			index = values().length-1;
		return values()[index];
	}

	public SeasonalPeriod nextPeriod()
	{
		int index = this.ordinal() + 1;
		if(index >= values().length)
			index = 0;
		return values()[index];
	}
}
