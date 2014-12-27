package com.bioxx.tfc2.World.TerrainTypes;

import java.awt.Color;

import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.model.Plane;
import net.royawesome.jlibnoise.module.source.Perlin;

public class TerrainType 
{
	protected int id;
	protected String name;
	protected Plane heightPlane;
	public int minHeight = 0;
	public int maxHeight = 255;
	protected Color mapColor;
	protected boolean canSmoothUpward = true;
	protected boolean canSmoothDownward = true;

	protected static TerrainType[] terrainList = new TerrainType[256];
	public static TerrainType Ocean = new TerrainOcean(0, "Ocean", Color.BLUE);
	public static TerrainType FlatlandsLow = new TerrainLowPlains(1, "Low Plains", Color.GREEN);
	public static TerrainType Beach = new TerrainBeach(2, "Beach", Color.YELLOW);
	public static TerrainType MountainsLow = new TerrainLowMountains(3, "Low Mountains", Color.GRAY);



	public TerrainType(int i, String n, Color c)
	{
		terrainList[i] = this;
		id = i;
		name = n;
		Perlin pe = new Perlin();
		pe.setSeed (0);
		pe.setFrequency (0.25);
		pe.setOctaveCount (4);
		pe.setNoiseQuality (NoiseQuality.BEST);
		heightPlane = new Plane(pe);
		mapColor = c;
	}

	public boolean getCanSmoothUpward()
	{
		return this.canSmoothUpward;
	}

	public boolean getCanSmoothDownward()
	{
		return this.canSmoothDownward;
	}

	public int getID()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public Color getMapColor()
	{
		return this.mapColor;
	}

	public Plane getHeightPlane()
	{
		return heightPlane;
	}

	public static TerrainType getTerrain(int i)
	{
		return terrainList[i];
	}
}
