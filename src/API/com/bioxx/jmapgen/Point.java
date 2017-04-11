package com.bioxx.jmapgen;

import java.awt.geom.Point2D;

import net.minecraft.util.math.BlockPos;

public class Point extends Point2D
{
	public static final Point ORIGIN = new Point(0,0);
	public double x, y;

	public Point()
	{
		x = 0;
		y = 0;
	}

	public Point(double X, double Y)
	{
		x = X;
		y = Y;
	}

	public Point(BlockPos pos)
	{
		x = pos.getX();
		y = pos.getZ();
	}

	@Override
	public double getX() 
	{
		return x;
	}

	@Override
	public double getY() 
	{
		return y;
	}

	/**
	 * This is the same as getY but since in mc we usually use z instead of y 
	 * this was added to make code more clear.
	 */
	public double getZ() 
	{
		return y;
	}

	@Override
	public void setLocation(double arg0, double arg1) 
	{
		x = arg0;
		y = arg1;
	}

	@Override
	public String toString() 
	{
		return x + "," + y;
	}

	public Point toIslandCoord()
	{
		int x1 = (int) (x % 4096);
		int y1 = (int) (y % 4096);
		if(x1 < 0)
			x1 = 4095 - x1 * -1;
		if(y < 0)
			y1 = 4095 - y1 * -1;
		return new Point(x1, y1);
	}

	public double getLength()
	{
		return this.distance(0, 0);
	}

	public static double distance(Point p0, Point p1)
	{
		return new Point(p0.x, p0.y).distance(p1);
	}

	public static Point interpolate(Point p0, Point p1, double amt)
	{
		Point p = p0.plus((p1.minus(p0).cross(amt)));
		return p;
	}

	public Point minus(Point p0)
	{
		Point out = new Point();
		out.x = this.x - p0.x;
		out.y = this.y - p0.y;
		return out;
	}

	public Point plus(Point p0)
	{
		Point out = new Point();
		out.x = this.x + p0.x;
		out.y = this.y + p0.y;
		return out;
	}

	public Point plus(double x, double y)
	{
		Point out = new Point();
		out.x = this.x + x;
		out.y = this.y + y;
		return out;
	}

	public Point cross(double d)
	{
		this.x *= d;
		this.y *= d;
		return this;
	}

	public Point floor()
	{
		this.x = Math.floor(this.x);
		this.y = Math.floor(this.y);
		return this;
	}

	public Point ceil()
	{
		this.x = Math.ceil(this.x);
		this.y = Math.ceil(this.y);
		return this;
	}

	public Point midpoint(Point p)
	{
		return new Point((x + p.x)/2, (y + p.y)/2);
	}

	public BlockPos toBlockPos()
	{
		return new BlockPos(x, 0, y);
	}
}
