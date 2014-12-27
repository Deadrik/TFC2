package com.bioxx.tfc2.World.Biome;

import java.awt.Color;

import net.royawesome.jlibnoise.NoiseQuality;
import net.royawesome.jlibnoise.model.Plane;
import net.royawesome.jlibnoise.module.Cache;
import net.royawesome.jlibnoise.module.modifier.Clamp;
import net.royawesome.jlibnoise.module.modifier.Curve;
import net.royawesome.jlibnoise.module.modifier.ScaleBias;
import net.royawesome.jlibnoise.module.modifier.ScalePoint;
import net.royawesome.jlibnoise.module.source.Perlin;

public class TerrainLowPlains extends TerrainType {

	public TerrainLowPlains(int i, String n, Color c) 
	{
		super(i, n, c);
		minHeight = 32;
		maxHeight = 40;

		Perlin pe = new Perlin();
		pe.setSeed (0);
		pe.setFrequency (0.03125);
		pe.setOctaveCount (3);
		pe.setNoiseQuality (NoiseQuality.BEST);

		ScalePoint sp = new ScalePoint();
		sp.setSourceModule(0, pe);
		sp.setxScale(.33);
		sp.setyScale(.33);
		sp.setzScale(.33);

		//The scalebias makes our noise fit the range 0-1
		ScaleBias sb = new ScaleBias(sp);
		//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
		sb.setScale(0.25);
		//Next we offset by +0.5 which makes the noise 0-1
		sb.setBias(0.5);

		//We use the Curve Module to pull the noise away from the middle toward the extremes. i.e. Add contrast.
		Curve curveModule = new Curve();
		curveModule.setSourceModule(0, sb);
		curveModule.AddControlPoint(0, 0);
		curveModule.AddControlPoint(0.35, 0.1);
		curveModule.AddControlPoint(0.75, 0.9);
		curveModule.AddControlPoint(1, 1);

		Clamp clampModule = new Clamp();
		clampModule.setSourceModule(0, curveModule);
		clampModule.setLowerBound(0);
		clampModule.setUpperBound(1);

		Cache cacheModule = new Cache();
		cacheModule.setSourceModule(0, clampModule);
		this.heightPlane = new Plane(cacheModule);
	}

}
