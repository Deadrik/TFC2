package com.bioxx.tfc2.api;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class TFCOptions
{
	// World Generation
	public static int maxThreadsForIslandGen = 1;
	public static boolean shouldGenTrees = true;
	public static boolean shouldStripChunks = false;

	// Player
	public static int healthGainRate = 1;
	public static int healthGainCap = 30;

	public static int torchBurnTime = 48;
	public static boolean enableDebugMode = true;

	public static boolean getBooleanFor(Configuration config,String heading, String item, boolean value)
	{
		if (config == null)
			return value;
		try
		{
			Property prop = config.get(heading, item, value);
			return prop.getBoolean(value);
		}
		catch (Exception e)
		{
			System.out.println("[TFC2] Error while trying to add Integer, config wasn't loaded properly!");
		}
		return value;
	}

	public static boolean getBooleanFor(Configuration config,String heading, String item, boolean value, String comment)
	{
		if (config == null)
			return value;
		try
		{
			Property prop = config.get(heading, item, value);
			prop.setComment(comment);
			return prop.getBoolean(value);
		}
		catch (Exception e)
		{
			System.out.println("[TFC2] Error while trying to add Integer, config wasn't loaded properly!");
		}
		return value;
	}

	public static int getIntFor(Configuration config, String heading, String item, int value)
	{
		if (config == null)
			return value;
		try
		{
			Property prop = config.get(heading, item, value);
			return prop.getInt(value);
		}
		catch (Exception e)
		{
			System.out.println("[TFC2] Error while trying to add Integer, config wasn't loaded properly!");
		}
		return value;
	}

	public static int getIntFor(Configuration config,String heading, String item, int value, String comment)
	{
		if (config == null)
			return value;
		try
		{
			Property prop = config.get(heading, item, value);
			prop.setComment(comment);
			return prop.getInt(value);
		}
		catch (Exception e)
		{
			System.out.println("[TFC2] Error while trying to add Integer, config wasn't loaded properly!");
		}
		return value;
	}

	public static double getDoubleFor(Configuration config,String heading, String item, double value, String comment)
	{
		if (config == null)
			return value;
		try
		{
			Property prop = config.get(heading, item, value);
			prop.setComment(comment);
			return prop.getDouble(value);
		}
		catch (Exception e)
		{
			System.out.println("[TFC2] Error while trying to add Double, config wasn't loaded properly!");
		}
		return value;
	}

	public static String getStringFor(Configuration config, String heading, String item, String value)
	{
		if (config == null)
			return value;
		try
		{
			Property prop = config.get(heading, item, value);
			return prop.getString();
		} catch (Exception e)
		{
			System.out.println("[TFC2] Error while trying to add String, config wasn't loaded properly!");
		}
		return value;
	}

	public static String getStringFor(Configuration config, String heading, String item, String value, String comment)
	{
		if (config == null)
			return value;
		try
		{
			Property prop = config.get(heading, item, value);
			prop.setComment(comment);
			return prop.getString();
		} catch (Exception e)
		{
			System.out.println("[TFC2] Error while trying to add String, config wasn't loaded properly!");
		}
		return value;
	}
}
