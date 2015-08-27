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
}
