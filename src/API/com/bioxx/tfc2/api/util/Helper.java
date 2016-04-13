package com.bioxx.tfc2.api.util;

import net.minecraft.util.math.BlockPos;

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

	public static double dist2dSq(BlockPos a, BlockPos b)
	{
		double d0 = a.getX() - b.getX();
		double d2 = a.getZ() - b.getZ();

		return d0 * d0 + d2 * d2;
	}
}
