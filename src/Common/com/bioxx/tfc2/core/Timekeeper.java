package com.bioxx.tfc2.core;

import net.minecraft.world.World;

import com.bioxx.tfc2.api.types.Season;
import com.bioxx.tfc2.api.types.SeasonalPeriod;

public class Timekeeper 
{
	World worldObj;
	static Timekeeper instance;
	public static final long ticksInYear = 2304000L;//24000*96
	public static final long ticksInSeason = ticksInYear/4;
	public static final long ticksInPeriod = ticksInSeason/3;

	public static Timekeeper getInstance()
	{
		return instance;
	}

	public static void initialize(World world)
	{
		instance = new Timekeeper(world);
	}

	public Timekeeper(World world)
	{
		worldObj = world;
	}

	public long getTotalTicks()
	{
		return worldObj.getWorldTime();
	}

	public long getTotalDays()
	{
		return getTotalTicks() / 24000;
	}

	public long getTotalHours()
	{
		return getTotalTicks() / 1000;
	}

	public double getClockTime()
	{
		double time = (getTotalTicks() % 24000) / 1000D;
		time += 6;
		return time % 24;		
	}

	public Season getSeason()
	{
		long mod = getTotalTicks() % ticksInYear;
		int season = (int)Math.floor(mod/ticksInSeason);
		return Season.fromInt(season);
	}

	public SeasonalPeriod getSeasonalPeriod()
	{
		long mod = getTotalTicks() % ticksInYear;
		int season = (int)Math.floor(mod/ticksInPeriod);
		return SeasonalPeriod.fromInt(season);
	}

	public long getTimeSinceLastPeriod()
	{
		long mod = getTotalTicks() % ticksInYear;
		return mod % ticksInPeriod;
	}

	public long getTimeUntilEndofPeriod()
	{
		long mod = getTotalTicks() % ticksInYear;
		return ticksInPeriod - (mod % ticksInPeriod);
	}
}
