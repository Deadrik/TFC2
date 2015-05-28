package com.bioxx.jMapGen;

import java.util.EnumSet;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.module.Module;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;

public class IslandParameters 
{
	protected Module shapeModule;
	double oceanRatio = 0.5;
	public double lakeThreshold = 0.3;
	int SIZE = 4096;
	public double islandMaxHeight = 100.0;
	public double moistureMultiplier = 1.0;

	private int xCoord = 0;
	private int zCoord = 0;

	/**
	 * 0x1 = Canyons
	 * 0x2 = Volcano
	 * 0x4 = Cliffs
	 * 0x8 = Sharper Mountains
	 * 0x16 = Even Sharper Mountains
	 * 0x32 = Valleys
	 * 0x64 = Small Craters
	 * 0x128 = Large Crater
	 */

	private EnumSet<Feature> features = EnumSet.noneOf(Feature.class);

	public IslandParameters() 
	{
		this(0, 4096, 0.5, 0.3);
	}

	public IslandParameters (long seed, int size, double oceans) 
	{
		this(seed, size, oceans, 0.3);
	}

	// The Perlin-based island combines perlin noise with the radius
	public IslandParameters (long seed, int size, double oceans, double lake) 
	{
		SIZE = size;
		double landRatioMinimum = 0.1;
		double landRatioMaximum = 0.55;
		oceanRatio = ((landRatioMaximum - landRatioMinimum) * oceans) + landRatioMinimum;
		lakeThreshold = lake;
		createShape(seed);
	}

	protected void createShape(long seed)
	{
		Perlin modulePerl = new Perlin();
		modulePerl.setSeed((int)seed);
		modulePerl.setFrequency(0.00058);
		modulePerl.setPersistence(0.7);
		modulePerl.setLacunarity(2.0);
		modulePerl.setOctaveCount(5);
		modulePerl.setNoiseQuality(NoiseQuality.BEST);

		ScaleBias sb = new ScaleBias();
		sb.setSourceModule(0, modulePerl);
		sb.setBias(0.0);
		sb.setScale(1.4);


		ScaleBias sb2 = new ScaleBias();
		sb2.setSourceModule(0, sb);
		sb2.setBias(0.5);
		sb2.setScale(0.25);

		shapeModule = sb2;
	}

	public boolean insidePerlin(Point q)
	{
		Point np = new Point(2.3*(q.x/SIZE - 0.5), 2.3*(q.y/SIZE - 0.5));
		double height = shapeModule.GetValue(q.x, 0, q.y);
		return height > oceanRatio+oceanRatio*np.getLength()*np.getLength();
	}

	public int getXCoord()
	{
		return this.xCoord;
	}

	public int getZCoord()
	{
		return this.zCoord;
	}

	public void setFeatures(Feature... f)
	{
		for(Feature fe : f)
			features.add(fe);
	}

	/**
	 * Used for reading stored nbt information
	 */
	private void setFeatures(int i)
	{
		for(Feature f : Feature.values())
		{
			if((i & f.ordinal()) > 0)
			{
				features.add(f);
			}
		}
	}

	public boolean hasFeature(Feature feat)
	{
		return features.contains(feat);
	}

	public void setCoords(int x, int z) 
	{
		this.xCoord = x;
		this.zCoord = z;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagCompound fnbt = nbt.getCompoundTag("features");
		for(Feature f : Feature.values())
		{
			if(fnbt.hasKey(f.toString()))
				features.add(f);
		}
		this.setCoords(nbt.getInteger("xCoord"), nbt.getInteger("zCoord"));
		this.oceanRatio = nbt.getDouble("oceanRatio");
		this.lakeThreshold = nbt.getDouble("lakeThreshold");
		this.islandMaxHeight = nbt.getDouble("islandMaxHeight");
		this.moistureMultiplier = nbt.getDouble("moistureMultiplier");
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		int feat = 0;
		NBTTagCompound fnbt = new NBTTagCompound();
		for(Feature ff : features)
		{
			fnbt.setBoolean(ff.toString(), true);
		}
		nbt.setTag("features", fnbt);
		nbt.setInteger("xCoord", xCoord);
		nbt.setInteger("zCoord", zCoord);
		nbt.setDouble("oceanRatio", oceanRatio);
		nbt.setDouble("lakeThreshold", lakeThreshold);
		nbt.setDouble("islandMaxHeight", islandMaxHeight);
		nbt.setDouble("moistureMultiplier", moistureMultiplier);
	}

	public enum Feature
	{
		//Important not to change this order if it can be helped.
		Gorges(0.3, "Gorges"), 
		Volcano(0.1, "Volcano"), 
		Cliffs(0.3, "Cliffs"), 
		SharperMountains(0.3, "Sharper Mountians"), 
		EvenSharperMountains(0.3, "Even Sharper Mountains"), 
		Valleys(0.6, "Valleys"), 
		SmallCraters(0.2, "Small Crater"), 
		LargeCrater(0.2, "Large Crater"), 
		Canyons(0.3, "Canyons");

		public final double rarity;
		private String name;
		private static final RandomCollection<Feature> pot = new RandomCollection<Feature>();

		private Feature(double r, String n)
		{
			rarity = r;
			name = n;
		}

		public static Feature getRandomFeature()
		{
			if(pot.size() == 0)
			{
				for(Feature f : Feature.values())
					pot.add(f.rarity, f);
			}

			return pot.next();
		}

		@Override
		public String toString()
		{
			return name;
		}
	}
}
