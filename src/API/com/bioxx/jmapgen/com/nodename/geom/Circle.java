package com.bioxx.jmapgen.com.nodename.geom;

import com.bioxx.jmapgen.Point;

public final class Circle extends Object
{
	public Point center;
	public double radius;

	public Circle(double centerX, double centerY, double radius)
	{
		super();
		this.center = new com.bioxx.jmapgen.Point(centerX, centerY);
		this.radius = radius;
	}

	public String toString()
	{
		return "Circle (center: " + center + "; radius: " + radius + ")";
	}

}
