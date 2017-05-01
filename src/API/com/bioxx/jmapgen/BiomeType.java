package com.bioxx.jmapgen;

import java.awt.Color;

import net.minecraft.world.biome.Biome;

import com.bioxx.tfc2.api.Global;

public enum BiomeType 
{
	OCEAN(0x44447a, Global.BIOME_OCEAN),
	DEEP_OCEAN(0x303059, Global.BIOME_DEEP_OCEAN),
	LAKESHORE(0x225588, Global.BIOME_LAKE),
	LAKE(0x336699, Global.BIOME_LAKE),
	BEACH(0xa09077, Global.BIOME_BEACH),
	SCORCHED(0x555555, Global.BIOME_SCORCHED),
	POLAR_DESERT(0x9bbcc4, Global.BIOME_POLAR_DESERT),
	TEMPERATE_DESERT(0x99b5a5, Global.BIOME_TEMPERATE_DESERT),
	TROPICAL_DESERT(0xbab494, Global.BIOME_TROPICAL_DESERT),
	SUBTROPICAL_DESERT(0xbab494, Global.BIOME_SUBTROPICAL_DESERT),
	MARSH(0x2f6666, Global.BIOME_MARSH),
	TUNDRA(0xbbbbaa, Global.BIOME_TUNDRA),
	BARE(0x888888, Global.BIOME_BARE),
	TAIGA(0x99aa77, Global.BIOME_TAIGA),
	SHRUBLAND(0x889977, Global.BIOME_SHRUBLAND),
	RAIN_FOREST(0x448855, Global.BIOME_RAIN_FOREST),
	DECIDUOUS_FOREST(0x679459, Global.BIOME_DECIDUOUS_FOREST),
	DRY_FOREST(0x679459, Global.BIOME_DRY_FOREST),
	GRASSLAND(0x88aa55, Global.BIOME_GRASSLAND),
	POND(0x336699, Global.BIOME_POND),
	RIVER(0x225588, Global.BIOME_RIVER),
	SWAMP(0x2f6666, Global.BIOME_SWAMP);

	public Color color;
	public Biome biome;

	private BiomeType(int c, Biome b)
	{
		color = Color.getColor("", c);
		biome = b;
	}
}
