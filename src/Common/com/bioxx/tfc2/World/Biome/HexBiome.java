package com.bioxx.tfc2.World.Biome;

import jMapGen.Map;
import jMapGen.graph.Center;

import java.awt.Color;

public class HexBiome 
{
	public static HexBiome OCEAN = new HexBiome(0x44447a);
	public static HexBiome LAKESHORE = new HexBiome(0x225588);
	public static HexBiome LAKE = new HexBiome(0x336699);
	public static HexBiome BEACH = new HexBiome(0xa09077);
	public static HexBiome SCORCHED = new HexBiome(0x555555);
	public static HexBiome TEMPERATE_DESERT = new HexBiome(0xc9d29b);
	public static HexBiome TROPICAL_DESERT = new HexBiome(0xd2b98b);
	public static HexBiome MARSH = new HexBiome(0x2f6666);
	public static HexBiome ICE = new HexBiome(0x99ffff);
	public static HexBiome SNOW = new HexBiome(0xffffff);
	public static HexBiome TUNDRA = new HexBiome(0xbbbbaa);
	public static HexBiome BARE = new HexBiome(0x888888);
	public static HexBiome TAIGA = new HexBiome(0x99aa77);
	public static HexBiome SHRUBLAND = new HexBiome(0x889977);
	public static HexBiome TEMPERATE_RAIN_FOREST = new HexBiome(0x448855);
	public static HexBiome TEMPERATE_DECIDUOUS_FOREST = new HexBiome(0x679459);
	public static HexBiome GRASSLAND = new HexBiome(0x88aa55);
	public static HexBiome TROPICAL_RAIN_FOREST = new HexBiome(0x337755);
	public static HexBiome TROPICAL_SEASONAL_FOREST = new HexBiome(0x559944);
	public static HexBiome SUBTROPICAL_DESERT = new HexBiome(0xd2b98b);

	public Color color;

	public HexBiome(int c)
	{
		color = Color.getColor("", c);
	}

	public void decorate(Map map, Center currentCenter)
	{

	}
}
