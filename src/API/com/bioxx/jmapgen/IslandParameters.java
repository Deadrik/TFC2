package com.bioxx.jmapgen;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.module.Module;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.tfc2.api.AnimalSpawnRegistry;
import com.bioxx.tfc2.api.AnimalSpawnRegistry.SpawnGroup;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import com.bioxx.tfc2.api.types.StoneType;
import com.bioxx.tfc2.api.types.WoodType;

public class IslandParameters 
{
	protected Module shapeModule;
	double oceanRatio = 0.5;
	public double lakeThreshold = 0.3;
	int SIZE = 4096;
	public double islandMaxHeight = 100.0;

	private int xCoord = 0;
	private int zCoord = 0;

	private long seed;

	private EnumSet<Feature> features = EnumSet.noneOf(Feature.class);
	private StoneType surfaceRock = StoneType.Granite;
	private String treeCommon = WoodType.Ash.getName();
	private String treeUncommon = WoodType.Ash.getName();
	private String treeRare = WoodType.Ash.getName();
	private Moisture moisture = Moisture.MEDIUM;
	private ClimateTemp temp = ClimateTemp.TEMPERATE;

	public ArrayList<SpawnGroup> animalSpawnGroups = new ArrayList<SpawnGroup>();

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
		this.seed = seed;
		SIZE = size;
		double landRatioMinimum = 0.1;
		double landRatioMaximum = 0.55;
		oceanRatio = ((landRatioMaximum - landRatioMinimum) * oceans) + landRatioMinimum;
		lakeThreshold = lake;
		createShape(seed);
	}

	public void createShape(long seed)
	{
		Perlin modulePerl = new Perlin();
		modulePerl.setSeed(seed);
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

	public void removeFeatures(Feature... f)
	{
		for(Feature fe : f)
			features.remove(fe);
	}

	/**
	 * Removes all Island Features. Should not be used outside of island generation.
	 */
	public void clearFeatures()
	{
		features.clear();
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

	public StoneType getSurfaceRock()
	{
		return this.surfaceRock;
	}

	public void setSurfaceRock(StoneType s)
	{
		surfaceRock = s;
	}

	public String getCommonTree()
	{
		return this.treeCommon;
	}

	public String getUncommonTree()
	{
		return this.treeUncommon;
	}

	public String getRareTree()
	{
		return this.treeRare;
	}

	public void setTrees(String t0, String t1, String t2)
	{
		treeCommon = t0;
		treeUncommon = t1;
		treeRare = t2;
	}

	public Moisture getIslandMoisture()
	{
		return moisture;
	}

	public ClimateTemp getIslandTemp()
	{
		return temp;
	}

	public void setIslandTemp(ClimateTemp t)
	{
		temp = t;
	}

	public void setIslandMoisture(Moisture m)
	{
		moisture = m;
	}

	public int getWorldX()
	{
		return this.xCoord*SIZE;
	}

	public int getWorldZ()
	{
		return this.zCoord*SIZE;
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
		this.surfaceRock = StoneType.getStoneTypeFromMeta(nbt.getInteger("surfaceRock"));
		this.treeCommon = nbt.getString("treeCommon");
		this.treeUncommon = nbt.getString("treeUncommon");
		this.treeRare = nbt.getString("treeRare");
		this.moisture = Moisture.values()[nbt.getInteger("moisture")];
		this.temp = ClimateTemp.values()[nbt.getInteger("temp")];
		this.seed = nbt.getLong("seed");
		this.animalSpawnGroups = new ArrayList<SpawnGroup>();
		fnbt = nbt.getCompoundTag("spawnGroups");
		for(int i = 0; i < fnbt.getSize(); i++)
		{
			SpawnGroup group = AnimalSpawnRegistry.getInstance().getGroupFromName(fnbt.getString("animal-"+i));
			if(group != null)
			{
				animalSpawnGroups.add(group);
			}
		}
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
		nbt.setInteger("surfaceRock", this.surfaceRock.getMeta());
		nbt.setString("treeCommon", treeCommon);
		nbt.setString("treeUncommon", treeUncommon);
		nbt.setString("treeRare", treeRare);
		nbt.setInteger("moisture", moisture.ordinal());
		nbt.setInteger("temp", temp.ordinal());
		nbt.setLong("seed", seed);

		fnbt = new NBTTagCompound();
		for(int i = 0; i < animalSpawnGroups.size(); i++)
		{
			fnbt.setString("animal-"+i, animalSpawnGroups.get(i).getGroupName());
		}
		nbt.setTag("spawnGroups", fnbt);
	}

	public enum Feature
	{
		Gorges(1, "Gorges"), 
		Volcano(0.001, "Volcano"), 
		Cliffs(1, "Cliffs"), 
		SharperMountains(1, "Sharper Mountains"), 
		EvenSharperMountains(1, "Even Sharper Mountains"), 
		Valleys(0.6, "Valleys"), 
		SmallCraters(0.75, "Small Crater"), 
		LargeCrater(0.75, "Large Crater"), 
		Canyons(1, "Canyons"),
		NoLand(1,"NO LAND", false),
		LowLand(0.5,"Low Land"),
		MineralRich(0.25,"Mineral Rich", FeatureSig.Minor),
		Spires(0.25,"Spires", FeatureSig.Minor),
		NutrientRich(0.5,"Nutrient Rich", FeatureSig.Minor),
		Desert(0.0,"Desert", false),
		DiverseCrops(0.5,"Diverse Crops", FeatureSig.Minor),
		RampantWildAnimals(0.25,"Rampant Wild Animals", FeatureSig.Minor);

		public final double rarity;
		private String name;
		private static RandomCollection<Feature> potMajor = new RandomCollection<Feature>();
		private static RandomCollection<Feature> potMinor = new RandomCollection<Feature>();
		private boolean shouldGen = true;
		public FeatureSig featureSig = FeatureSig.Major;

		private Feature(double r, String n)
		{
			rarity = r;
			name = n;
			featureSig = FeatureSig.Major;
		}

		private Feature(double r, String n, FeatureSig sig)
		{
			this(r, n);
			featureSig = sig;
		}

		private Feature(double r, String n, boolean gen)
		{
			this(r, n);
			shouldGen = gen;
		}

		public static void setupFeatures(Random r)
		{
			potMajor = new RandomCollection<Feature>(r);
			if(potMajor.size() == 0)
			{
				for(Feature f : Feature.values())
				{
					if(f.shouldGen && f.featureSig == FeatureSig.Major)
						potMajor.add(f.rarity, f);
				}
			}

			potMinor = new RandomCollection<Feature>(r);
			if(potMinor.size() == 0)
			{
				for(Feature f : Feature.values())
				{
					if(f.shouldGen && f.featureSig == FeatureSig.Minor)
						potMinor.add(f.rarity, f);
				}
			}
		}

		public static Feature getRandomFeature(FeatureSig sig)
		{
			if(sig == FeatureSig.Major)
				return potMajor.next();
			else
				return potMinor.next();
		}

		@Override
		public String toString()
		{
			return name;
		}

		public enum FeatureSig
		{
			Major, Minor;
		}
	}
}
