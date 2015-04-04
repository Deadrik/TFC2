package com.bioxx.tfc2.World.TerrainTypes;

import java.awt.Color;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.module.Cache;
import com.bioxx.libnoise.module.combiner.Select;
import com.bioxx.libnoise.module.modifier.Clamp;
import com.bioxx.libnoise.module.modifier.Curve;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Billow;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.libnoise.module.source.RidgedMulti;

public class TerrainLowMountains extends TerrainType {

	public TerrainLowMountains(int i, String n, Color c) 
	{
		super(i, n, c);
		minNoiseHeight = 34;
		maxNoiseHeight = 250;

		Perlin pe = new Perlin();
		pe.setSeed (0);
		pe.setFrequency (1f/256f);
		pe.setPersistence(.25);
		pe.setNoiseQuality (NoiseQuality.BEST);

		RidgedMulti rm = new RidgedMulti();
		rm.setSeed(0);
		rm.setFrequency(1f/128f);
		rm.setOctaveCount(4);

		ScaleBias rmScale = new ScaleBias();
		rmScale.setSourceModule(0, rm);
		rmScale.setScale(0.125);

		Billow bi = new Billow();
		bi.setSeed(0);
		bi.setFrequency(1f/64f);
		bi.setOctaveCount(1);

		ScaleBias sp = new ScaleBias();
		sp.setSourceModule(0, bi);
		sp.setScale(0.125);
		sp.setBias(-0.75);

		Select sl = new Select();
		sl.setSourceModule(0, sp);
		sl.setSourceModule(1, rmScale);
		sl.setControlModule(pe);
		sl.setEdgeFalloff(0.125f);

		//The scalebias makes our noise fit the range 0-1
		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, sl);
		//Noise is normally +-2 so we scale by 0.25 to make it +-0.5
		sb2.setScale(0.25);
		//Next we offset by +0.5 which makes the noise 0-1
		sb2.setBias(0.5);

		Curve curveModule = new Curve();
		curveModule.setSourceModule(0, sb2);
		curveModule.AddControlPoint(0, 0);
		curveModule.AddControlPoint(0.5, 0.25);
		curveModule.AddControlPoint(0.75, 0.95);
		curveModule.AddControlPoint(1, 1);

		Clamp clampModule = new Clamp();
		clampModule.setLowerBound(0.0);
		clampModule.setUpperBound(1.0);

		Cache cacheModule = new Cache();
		cacheModule.setSourceModule(0, curveModule);

		heightPlane = new com.bioxx.libnoise.model.Plane(cacheModule);
	}

}
