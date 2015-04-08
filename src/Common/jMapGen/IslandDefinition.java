package jMapGen;

import com.bioxx.libnoise.NoiseQuality;
import com.bioxx.libnoise.module.Module;
import com.bioxx.libnoise.module.modifier.ScaleBias;
import com.bioxx.libnoise.module.source.Perlin;

//Factory class to build the 'inside' function that tells us whether
//a point should be on the island or in the water.
public class IslandDefinition 
{
	// This class has factory functions for generating islands of
	// different shapes. The factory returns a function that takes a
	// normalized point (x and y are -1 to +1) and returns true if the
	// point should be on the island, and false if it should be water
	// (lake or ocean).

	double oceanRatio = 0.5;
	public double lakeThreshold = 0.3;
	int SIZE = 4096;

	public IslandDefinition (long seed, int size, double oceans) 
	{
		this(seed, size, oceans, 0.3);
	}

	// The Perlin-based island combines perlin noise with the radius
	public IslandDefinition (long seed, int size, double oceans, double lake) 
	{
		SIZE = size;
		double landRatioMinimum = 0.1;
		double landRatioMaximum = 0.55;
		oceanRatio = ((landRatioMaximum - landRatioMinimum) * oceans) + landRatioMinimum;
		lakeThreshold = lake;
		createShape(seed);

		/*try
		{
			BufferedImage outBitmap = new BufferedImage(SIZE,SIZE,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) outBitmap.getGraphics();

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
			ImageIO.write(outBitmap, "BMP", new File("hm-shape-" + seed + ".bmp"));
		}
		catch(Exception e){e.printStackTrace();
		}*/
	}

	public Module shapeModule;


	public void createShape(long seed)
	{
		Perlin modulePerl = new Perlin();
		modulePerl.setSeed((int)seed);
		modulePerl.setFrequency(0.00055);
		modulePerl.setPersistence(0.55);
		modulePerl.setOctaveCount(8);
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
}
