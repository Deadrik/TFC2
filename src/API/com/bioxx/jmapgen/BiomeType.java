package com.bioxx.jmapgen;

import java.awt.Color;

import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;

public enum BiomeType 
{
	OCEAN(0x44447a, Biomes.OCEAN),
	DEEP_OCEAN(0x303059, Biomes.DEEP_OCEAN),
	LAKESHORE(0x225588, Biomes.RIVER),
	LAKE(0x336699, Biomes.RIVER),
	BEACH(0xa09077, Biomes.BEACH),
	SCORCHED(0x555555, Biomes.EXTREME_HILLS),
	POLAR_DESERT(0x9bbcc4, Biomes.DESERT),
	TEMPERATE_DESERT(0x99b5a5, Biomes.DESERT),
	TROPICAL_DESERT(0xbab494, Biomes.DESERT),
	SUBTROPICAL_DESERT(0xbab494, Biomes.DESERT),
	MARSH(0x2f6666, Biomes.SWAMPLAND),
	TUNDRA(0xbbbbaa, Biomes.TAIGA),
	BARE(0x888888, Biomes.EXTREME_HILLS),
	TAIGA(0x99aa77, Biomes.COLD_TAIGA),
	SHRUBLAND(0x889977, Biomes.PLAINS),
	RAIN_FOREST(0x448855, Biomes.JUNGLE),
	DECIDUOUS_FOREST(0x679459, Biomes.FOREST),
	DRY_FOREST(0x679459, Biomes.FOREST),
	GRASSLAND(0x88aa55, Biomes.PLAINS),
	POND(0x336699, Biomes.RIVER),
	RIVER(0x225588, Biomes.RIVER);

	public Color color;
	public Biome biome;

	private BiomeType(int c, Biome b)
	{
		color = Color.getColor("", c);
		biome = b;
	}
}
