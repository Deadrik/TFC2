package jMapGen;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.module.Module;
import com.bioxx.libnoise.module.combiner.Add;
import com.bioxx.libnoise.module.combiner.Min;
import com.bioxx.libnoise.module.combiner.Select;
import com.bioxx.libnoise.module.modifier.Clamp;
import com.bioxx.libnoise.module.modifier.Invert;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Const;
import com.bioxx.libnoise.module.source.Perlin;
import com.bioxx.libnoise.module.source.RidgedMulti;

//Factory class to build the 'inside' function that tells us whether
//a point should be on the island or in the water.
public class IslandShape 
{
	// This class has factory functions for generating islands of
	// different shapes. The factory returns a function that takes a
	// normalized point (x and y are -1 to +1) and returns true if the
	// point should be on the island, and false if it should be water
	// (lake or ocean).

	double oceanRatio = 0.5;
	int SIZE = 1024;

	// The radial island radius is based on overlapping sine waves 
	static public double ISLAND_FACTOR = 1.07;  // 1.0 means no small islands; 2.0 leads to a lot


	// The Perlin-based island combines perlin noise with the radius
	public IslandShape (long seed, int size, double oceans) 
	{
		SIZE = size;
		double landRatioMinimum = 0.1;
		double landRatioMaximum = 0.5;
		oceanRatio = ((landRatioMaximum - landRatioMinimum) * oceans) + landRatioMinimum;

		createShape(seed);
		createElevation(seed);

		/*if(IslandMapGen.DEBUG)
		{
			try
			{
				BufferedImage outBitmap = new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_RGB);
				Graphics2D g = (Graphics2D) outBitmap.getGraphics();

				for(int x = 0; x < SIZE; x++)
				{
					for(int z = 0; z < SIZE; z++)
					{
						if(insidePerlin(new Point(x,z)))
						{
							float h = (float) elevModule.GetValue(x, 0, z);
							g.setColor(Color.getHSBColor(0, 0, (h+2)/4));
							g.fillRect(x, z, 1, 1);
						}
					}
				}
				ImageIO.write(outBitmap, "BMP", new File("hm-elev.bmp"));

				for(int x = 0; x < SIZE; x++)
				{
					for(int z = 0; z < SIZE; z++)
					{
						if(insidePerlin(new Point(x,z)))
						{
							float h = (float) shapeModule.GetValue(x, 0, z);
							g.setColor(Color.getHSBColor(0, 0, (h+2)/4));
							g.fillRect(x, z, 1, 1);
						}

					}
				}
				ImageIO.write(outBitmap, "BMP", new File("hm-shape.bmp"));
			}
			catch(Exception e){e.printStackTrace();}
		}*/


	}

	public Module shapeModule;
	public Module elevModule;


	public void createShape(long seed)
	{
		Perlin modulePerl = new Perlin();
		modulePerl.setSeed((int)seed);
		modulePerl.setFrequency(0.0045);
		modulePerl.setOctaveCount(8);
		modulePerl.setNoiseQuality(NoiseQuality.BEST);

		ScaleBias sb = new ScaleBias();
		sb.setSourceModule(0, modulePerl);
		sb.setBias(0.0);
		sb.setScale(1.4);

		Clamp moduleClamp = new Clamp();
		moduleClamp.setSourceModule(0, sb);
		moduleClamp.setLowerBound(-2);
		moduleClamp.setUpperBound(2);

		shapeModule = moduleClamp;
	}

	public void createElevation(long seed)
	{		
		RidgedMulti rm = new RidgedMulti();
		rm.setSeed((int)seed);
		rm.setOctaveCount(8);
		rm.setFrequency(0.005);

		Const const0 = new Const();
		const0.setValue(0);

		Invert inv = new Invert();
		inv.setSourceModule(0, rm);

		Min min = new Min();
		min.setSourceModule(0, const0);
		min.setSourceModule(1, inv);

		Add add = new Add();
		add.setSourceModule(0, shapeModule);
		add.setSourceModule(1, inv);

		Select s0 = new Select();
		s0.setBounds(0, 2);
		s0.setSourceModule(0, shapeModule);
		s0.setSourceModule(1, min);
		s0.setControlModule(rm);
		s0.setEdgeFalloff(1);

		elevModule = s0;
	}

	public boolean insidePerlin(Point q)
	{
		Point np = new Point(2*(q.x/SIZE - 0.5), 2*(q.y/SIZE - 0.5));
		double height = (shapeModule.GetValue(q.x, 0, q.y)+2)/4;
		return height > oceanRatio+oceanRatio*np.getLength()*np.getLength();
	}

	public double elevPerlin(Point q)
	{
		double height = (elevModule.GetValue(q.x, 0, q.y))/1;

		return height;
	}
}
