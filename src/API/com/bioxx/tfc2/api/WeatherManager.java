package com.bioxx.tfc2.api;

import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.libnoise.model.Line;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.combiner.Max;
import com.bioxx.libnoise.module.combiner.Select;
import com.bioxx.libnoise.module.modifier.Clamp;
import com.bioxx.libnoise.module.modifier.TranslatePoint;
import com.bioxx.libnoise.module.source.Const;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.Season;
import com.bioxx.tfc2.api.types.SeasonalPeriod;
import com.bioxx.tfc2.core.Timekeeper;
import com.bioxx.tfc2.world.WorldGen;

/**
 * This class acts as the overall weather manager for all islands. A perlin map 
 * @author Bioxx
 *
 */
public class WeatherManager 
{
	private static WeatherManager instance;
	private int rainCounter = 0;
	public Line rainModelSpring;
	public Line rainModelSummer;
	public Line rainModelFall;
	public Line rainModelWinter;
	private Plane temperatureNoise;
	private World worldObj;

	public WeatherManager(World world)
	{
		worldObj = world;
	}

	public static void setupWeather(World world)
	{
		instance = new WeatherManager(world);
		instance.setupStorms(world);
	}

	public static WeatherManager getInstance()
	{
		return instance;
	}

	/**
	 * @param model Rain Model
	 * @param x Island Map XCoord
	 * @param z Island Map ZCoord
	 * @return Returns precipitation value from the perlin map
	 */
	public double getPrecipitationRaw(Line model, int x, int z)
	{
		//We use the x value for moving the noise over time
		//The y value is used as an offset for the island x coordinate
		//The z value is used as an offset for the island z coordinate
		return model.getValue(Timekeeper.getInstance().getTotalHalfHours(), x * 1000000, z * 1000000);
	}

	/**
	 * @return Returns an adjusted precipitation value for the island being queried
	 */
	public double getPreciptitation(int x, int z)
	{
		IslandMap island = WorldGen.instance.getIslandMap(x >> 12, z >> 12);
		Line model = getModelForClimate(island, worldObj);
		double raw = getPrecipitationRaw(model, x >> 12, z >> 12);
		//We divide the clamp by two so that there is more rain on average. Just using the clamp itself was too limiting
		double clamp = island.getParams().getIslandMoisture().getInverse() / 2D;
		double rain = Math.max(Math.min(raw - clamp, 1.0), 0.0);
		return rain;
	}

	public Line getModelForClimate(IslandMap map, World world)
	{
		Moisture moisture = map.getParams().getIslandMoisture();
		ClimateTemp temp = map.getParams().getIslandTemp();
		Season season = Timekeeper.getInstance().getSeason();

		//TODO: Expand this to allow islands to have more individualized weather

		if(season == Season.Spring)
			return rainModelSpring;
		else if(season == Season.Summer)
			return rainModelSummer;
		else if(season == Season.Fall)
			return rainModelFall;
		else if(season == Season.Winter)
			return rainModelWinter;

		return rainModelSummer;
	}

	public double getTemperature(BlockPos pos)
	{
		return getTemperature(pos.getX(), pos.getY(), pos.getZ());
	}

	public double getTemperature(int x, int y, int z)
	{
		//TODO: Add temperature effect based upon time of day.

		Timekeeper inst = Timekeeper.getInstance();
		// 1: Find the island information to get the general climate data
		IslandMap island = WorldGen.instance.getIslandMap(x >> 12, z >> 12);
		ClimateTemp climate = island.getParams().getIslandTemp();
		// 2: Get seasonal data to combine with the island information
		Season season = Timekeeper.getInstance().getSeason();
		SeasonalPeriod period = Timekeeper.getInstance().getSeasonalPeriod();
		// 3: Get local temperature noise
		double local = temperatureNoise.getModule().GetValue(x, Timekeeper.getInstance().getTotalDays(), z) * climate.getTempVar();
		// 4: Combine this information and adjust for elevation;
		double baseMin = climate.getTempMin();
		double baseMax = climate.getTempMax();
		double elevationModifier = (((y-64)/192D)*8.0);
		return baseMin + ((baseMax - baseMin)*period.getTempMultiplier()) + local - elevationModifier;
	}



	public void setupStorms(World world)
	{
		rainModelSummer = getSummerStorm();

		rainModelSpring = getSpringStorm();

		rainModelFall = getFallStorm();

		rainModelWinter = getWinterStorm();

		Perlin p0 = new Perlin(worldObj.getSeed(), 0.003, 0.8);
		p0.setOctaveCount(4);
		p0.setLacunarity(1.1);

		this.temperatureNoise = new Plane(p0);
	}

	private Line getSummerStorm()
	{
		/*
		 * Rain band module. High frequency causes the rain strength to fluctuate a lot.
		 */
		Perlin p0 = new Perlin(worldObj.getSeed(), 0.05, 0.8);
		p0.setOctaveCount(2);
		p0.setLacunarity(1.1);

		/*
		 * Used by the Selector for dry spells
		 */
		Const con0 = new Const(0);

		/*
		 * Control module - This creates the Large Storms
		 */
		Perlin p1 = new Perlin(worldObj.getSeed(), 0.008, 0.1);

		/**
		 * Select module uses the control module to determine how often it will be rainy over the course of the 
		 * season, then fills the rainy periods with rain bands from p0
		 */
		Select s0 = new Select(0.1, 1.0, 0.5, p1, con0, p0);

		/**
		 * The default storms are nice but its hard to get the amount of rain bands that we'd like 
		 * to see so we translate the initial rain map to get some new noise and then blend it into our final map
		 */
		TranslatePoint tp0 = new TranslatePoint();
		tp0.setSourceModule(0, p0);
		tp0.setZTranslation(1000);

		/**
		 * This is the selection module for our second rain band pass
		 */
		Select s1 = new Select(0.1, 1.0, 0.5, p1, con0, tp0);

		/**
		 * We use max to blend the rain band layers.
		 */
		Max m0 = new Max();
		m0.setSourceModule(0, s0);
		m0.setSourceModule(1, s1);

		/*
		 * Clamps our output to 0.0 - 1.0
		 */
		Clamp c0 = new Clamp(m0);

		return new Line(c0);
	}

	private Line getSpringStorm()
	{
		/*
		 * Rain band module. High frequency causes the rain strength to fluctuate a lot.
		 */
		Perlin p0 = new Perlin(worldObj.getSeed(), 0.1, 0.8);
		p0.setOctaveCount(2);
		p0.setLacunarity(1.1);

		/*
		 * Used by the Selector for dry spells
		 */
		Const con0 = new Const(0);

		/*
		 * Control module - This creates the Large Storms
		 */
		Perlin p1 = new Perlin(worldObj.getSeed(), 0.019, 0.1);

		/**
		 * Select module uses the control module to determine how often it will be rainy over the course of the 
		 * season, then fills the rainy periods with rain bands from p0
		 */
		Select s0 = new Select(0.1, 1.0, 0.5, p1, con0, p0);

		/**
		 * The default storms are nice but its hard to get the amount of rain bands that we'd like 
		 * to see so we translate the initial rain map to get some new noise and then blend it into our final map
		 */
		TranslatePoint tp0 = new TranslatePoint();
		tp0.setSourceModule(0, p0);
		tp0.setZTranslation(1000);

		/**
		 * This is the selection module for our second rain band pass
		 */
		Select s1 = new Select(0.1, 1.0, 0.5, p1, con0, tp0);

		/**
		 * We use max to blend the rain band layers.
		 */
		Max m0 = new Max();
		m0.setSourceModule(0, s0);
		m0.setSourceModule(1, s1);

		/*
		 * Clamps our output to 0.0 - 1.0
		 */
		Clamp c0 = new Clamp(m0);

		return new Line(c0);
	}

	private Line getFallStorm()
	{
		/*
		 * Rain band module. High frequency causes the rain strength to fluctuate a lot.
		 */
		Perlin p0 = new Perlin(worldObj.getSeed(), 0.05, 0.8);
		p0.setOctaveCount(2);
		p0.setLacunarity(1.1);

		/*
		 * Used by the Selector for dry spells
		 */
		Const con0 = new Const(0);

		/*
		 * Control module - This creates the Large Storms
		 */
		Perlin p1 = new Perlin(worldObj.getSeed(), 0.008, 0.1);

		/**
		 * Select module uses the control module to determine how often it will be rainy over the course of the 
		 * season, then fills the rainy periods with rain bands from p0
		 */
		Select s0 = new Select(0.1, 1.0, 0.5, p1, con0, p0);

		/**
		 * The default storms are nice but its hard to get the amount of rain bands that we'd like 
		 * to see so we translate the initial rain map to get some new noise and then blend it into our final map
		 */
		TranslatePoint tp0 = new TranslatePoint();
		tp0.setSourceModule(0, p0);
		tp0.setZTranslation(1000);

		/**
		 * This is the selection module for our second rain band pass
		 */
		Select s1 = new Select(0.1, 1.0, 0.5, p1, con0, tp0);

		/**
		 * We use max to blend the rain band layers.
		 */
		Max m0 = new Max();
		m0.setSourceModule(0, s0);
		m0.setSourceModule(1, s1);

		/*
		 * Clamps our output to 0.0 - 1.0
		 */
		Clamp c0 = new Clamp(m0);

		return new Line(c0);
	}

	private Line getWinterStorm()
	{
		/*
		 * Rain band module. High frequency causes the rain strength to fluctuate a lot.
		 */
		Perlin p0 = new Perlin(worldObj.getSeed(), 0.05, 0.8);
		p0.setOctaveCount(2);
		p0.setLacunarity(1.1);

		/*
		 * Used by the Selector for dry spells
		 */
		Const con0 = new Const(0);

		/*
		 * Control module - This creates the Large Storms
		 */
		Perlin p1 = new Perlin(worldObj.getSeed(), 0.008, 0.1);

		/**
		 * Select module uses the control module to determine how often it will be rainy over the course of the 
		 * season, then fills the rainy periods with rain bands from p0
		 */
		Select s0 = new Select(0.1, 1.0, 0.5, p1, con0, p0);

		/**
		 * The default storms are nice but its hard to get the amount of rain bands that we'd like 
		 * to see so we translate the initial rain map to get some new noise and then blend it into our final map
		 */
		TranslatePoint tp0 = new TranslatePoint();
		tp0.setSourceModule(0, p0);
		tp0.setZTranslation(1000);

		/**
		 * This is the selection module for our second rain band pass
		 */
		Select s1 = new Select(0.1, 1.0, 0.5, p1, con0, tp0);

		/**
		 * We use max to blend the rain band layers.
		 */
		Max m0 = new Max();
		m0.setSourceModule(0, s0);
		m0.setSourceModule(1, s1);

		/*
		 * Clamps our output to 0.0 - 1.0
		 */
		Clamp c0 = new Clamp(m0);

		return new Line(c0);
	}
}
