package com.bioxx.jmapgen.com.nodename.delaunay;

import java.awt.Rectangle;

import com.bioxx.jmapgen.Point;

public class BoundsCheck 
{
	public static final int TOP = 1;
	public static final int  BOTTOM = 2;
	public static final int  LEFT = 4;
	public static final int  RIGHT = 8;
	
	/**
	 * 
	 * @param point
	 * @param bounds
	 * @return an int with the appropriate bits set if the Point lies on the corresponding bounds lines
	 * 
	 */
	public static int check(Point point, Rectangle bounds)
	{
		int value = 0;
		if (point.x == bounds.getMinX())
		{
			value |= LEFT;
		}
		if (point.x == bounds.getMaxX())
		{
			value |= RIGHT;
		}
		if (point.y == bounds.getMinY())
		{
			value |= TOP;
		}
		if (point.y == bounds.getMaxY())
		{
			value |= BOTTOM;
		}
		return value;
	}
}
