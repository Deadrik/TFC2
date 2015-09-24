package com.bioxx.tfc2.api.util;

public class Helper 
{
	public static int combineCoords(int x, int y)
	{
		short xs = (short)x;
		short ys = (short)y;
		return (xs << 16) | (ys & 0xFFFF);
	}

	public static int getXCoord(int c)
	{
		return (short)(c >> 16);
	}
	public static int getYCoord(int c)
	{
		return (short)(c & 0xffff);
	}

	/**
	 * @param angle Any angle in degrees
	 * @return Returns the original angle after making sure that it is bounded between 0 and 360 degrees
	 */
	public static double normalizeAngle(double angle)
	{
		angle = angle % 360;
		if(angle < 0)
			angle += 360;

		return angle;
	}
}
