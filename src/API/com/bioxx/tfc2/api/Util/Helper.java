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
		return ((x + y) * (x + y + 1) / 2) + y;
	}

	/**
	 * Inverse X function of Cantor's pairing function.
	 */
	public static int cantorX(int c) {
		int x = (int) Math.floor(Math.sqrt(0.25 + 2 * c) - 0.5);
		return x - cantorY(c);
	}

	/**
	 * Inverse Y function of Cantor's pairing function.
	 */
	private static int cantorY(int c) {
		int y = (int) Math.floor(Math.sqrt(0.25 + 2 * c) - 0.5);
		return c - y * (y + 1) / 2;
	}
}
