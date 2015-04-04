package za.co.luma.math.function;

import za.co.iocom.math.MathUtil;

/**
 * Represents smooth noise, that is noise sampled from uniform noise, and
 * linearly interpolated.
 * 
 * @author Herman Tulleken (herman@luma.co.za)
 */
public class SmoothNoise2D
{
	private double[][] noise;

	/**
	 * Constructs a new SmoothNoise object.
	 * 
	 * @param maxWidth
	 *            The width of the noise plus the maximum sampling period.
	 * @param maxHeight
	 *            The height of the noise plus the maximum sampling period.
	 */
	public SmoothNoise2D(int maxWidth, int maxHeight)
	{
		noise = new double[maxWidth][maxHeight];

		for (int i = 0; i < maxWidth; i++)
		{
			for (int j = 0; j < maxHeight; j++)
			{
				noise[i][j] = MathUtil.random.nextDouble();
			}
		}
	}

	/**
	 * Gets the noise at the indicated coordinate, sampled at the given
	 * samplePeriod.
	 * 
	 * @return a value between 0 and 1.
	 */
	public double getNoise(int x, int y, int samplingPeriod)
	{
		int xx = x / samplingPeriod;

		int noiseX1 = xx * samplingPeriod;
		int noiseX2 = (xx + 1) * samplingPeriod;

		int yy = y / samplingPeriod;

		int noiseY1 = yy * samplingPeriod;
		int noiseY2 = (yy + 1) * samplingPeriod;

		double N1 = noise[noiseX1][noiseY1];
		double N2 = noise[noiseX1][noiseY2];
		double N3 = noise[noiseX2][noiseY1];
		double N4 = noise[noiseX2][noiseY2];

		int gg = samplingPeriod * samplingPeriod;
		int gx = samplingPeriod * (x - noiseX1);
		int gy = samplingPeriod * (y - noiseY1);
		int xy = (x - noiseX1) * (y - noiseY1);

		double noiseTmp = (gg - gx - gy + xy) * N1 + (gy - xy) * N2 + (gx - xy)
				* N3 + xy * N4;
		return noiseTmp / gg;
	}
}
