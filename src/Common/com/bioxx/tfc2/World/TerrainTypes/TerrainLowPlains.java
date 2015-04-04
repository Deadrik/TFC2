package com.bioxx.tfc2.World.TerrainTypes;

import java.awt.Color;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.model.Plane;
import com.bioxx.libnoise.module.Cache;
import com.bioxx.libnoise.module.modifier.Clamp;
import com.bioxx.libnoise.module.modifier.Curve;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.modifier.ScalePoint;
import com.bioxx.libnoise.module.source.Perlin;

public class TerrainLowPlains extends TerrainType {

	public TerrainLowPlains(int i, String n, Color c) 
	{
		super(i, n, c);
		minNoiseHeight = 33;
		maxNoiseHeight = 40;
		minSmoothHeight = 33;
		maxSmoothHeight = 50;

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
