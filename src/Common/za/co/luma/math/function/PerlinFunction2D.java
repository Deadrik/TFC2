package za.co.luma.math.function;


/**
 * This class is sued to create 2D Perlin noise.
 * 
 * @author Herman Tulleken
 *
 */
public class PerlinFunction2D extends RealFunction2D
{
	private static final double DEFAULT_PERSISTANCE = 0.9;

	private final int octaves;
	
	private SmoothNoise2D[] textures;
	double persistence;
	int samplingPeriodMax;
	boolean normalize;

	public PerlinFunction2D(int width, int height, int octaves)
	{
		this(width, height, octaves, DEFAULT_PERSISTANCE, true);
	}
	
	public PerlinFunction2D(int width, int height, int octaves, double persistence, boolean normalize)
	{

		this.octaves = octaves;		
		textures = new SmoothNoise2D[octaves];		
		samplingPeriodMax = 1 << octaves;
		//samplingPeriodMax = width >> octaves;
		this.persistence = persistence;
		this.normalize = normalize;
		
		for (int i = 0; i < octaves; i++)
		{			
			textures[i] = new SmoothNoise2D(width + samplingPeriodMax + 1, height + samplingPeriodMax + 1);
		}
	}

	@Override
	public double getDouble(int u, int v)
	{
		double amplitude = 1.0;
		double totalAmplitude = 0.0;
		double col = 0;
		int samplingPeriod = samplingPeriodMax;

		for (int i = 0; i < octaves; i++)
		{
			amplitude *= persistence;
			totalAmplitude += amplitude;			
			col += (textures[i].getNoise(u, v, samplingPeriod)) * amplitude;			
			samplingPeriod /= 2;			
		}
		
		if (normalize)
		{
			col /= totalAmplitude; 
		}
		else //clamp
		{
			if (col > 1.0)
			{
				col = 1.0;
			}
			else if (col < 0.0)
			{
				col = 0.0;
			}
		}
		
		return col;
	}
}
