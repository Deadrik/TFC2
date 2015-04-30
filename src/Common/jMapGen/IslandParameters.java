package jMapGen;

import net.minecraft.nbt.NBTTagCompound;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.module.Module;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;

public class IslandParameters 
{
	// This class has factory functions for generating islands of
	// different shapes. The factory returns a function that takes a
	// normalized point (x and y are -1 to +1) and returns true if the
	// point should be on the island, and false if it should be water
	// (lake or ocean).
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
	 * 0x8
	 * 0x16
	 * 0x32
	 * 0x64
	 * 0x128
	 */
	private int features = 0;

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

	public void setFeatures(int f)
	{
		features = f;
	}

	public boolean shouldGenCanyons()
	{
		return (features & 1) > 0;
	}

	public boolean shouldGenVolcano()
	{
		return (features & 2) > 0;
	}

	public boolean shouldGenCliffs()
	{
		return (features & 4) > 0;
	}

	public void setCoords(int x, int z) 
	{
		this.xCoord = x;
		this.zCoord = z;
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		this.setFeatures(nbt.getInteger("features"));
		this.setCoords(nbt.getInteger("xCoord"), nbt.getInteger("zCoord"));
		this.oceanRatio = nbt.getDouble("oceanRatio");
		this.lakeThreshold = nbt.getDouble("lakeThreshold");
		this.islandMaxHeight = nbt.getDouble("islandMaxHeight");
		this.moistureMultiplier = nbt.getDouble("moistureMultiplier");
	}

	public void writeToNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("features", features);
		nbt.setInteger("xCoord", xCoord);
		nbt.setInteger("zCoord", zCoord);
		nbt.setDouble("oceanRatio", oceanRatio);
		nbt.setDouble("lakeThreshold", lakeThreshold);
		nbt.setDouble("islandMaxHeight", islandMaxHeight);
		nbt.setDouble("moistureMultiplier", moistureMultiplier);
	}
}
