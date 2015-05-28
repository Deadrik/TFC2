package com.bioxx.tfc2.api.Util;

public class Helper 
{
	/**
	 * Applies Cantor's pairing function to 2D coordinates.
	 *
	 * @param k1 X-coordinate
	 * @param k2 Y-coordinate
	 * @return Unique 1D value
	 */
	public static int cantorize(int x, int y) 
	{
		//Here we do some hijinks to handle negative coords
		if(x < 0)
			x = x * -1 + 2100000;
		if(y < 0)
			y = y * -1 + 2100000;
		return ((x + y) * (x + y + 1) / 2) + y;
	}

	/**
	 * Inverse X function of Cantor's pairing function.
	 */
	public static int cantorX(int c) {
		int x = (int) Math.floor(Math.sqrt(0.25 + 2 * c) - 0.5);
		x = x - cantorY(c);

		if(x > 2100000)
			x = (x-2100000) * -1;

		return x;
	}

	/**
	 * Inverse Y function of Cantor's pairing function.
	 */
	public static int cantorY(int c) {
		int y = (int) Math.floor(Math.sqrt(0.25 + 2 * c) - 0.5);
		y = c - y * (y + 1) / 2;
		if(y > 2100000)
			y = (y-2100000) * -1;
		return y;
	}
}
