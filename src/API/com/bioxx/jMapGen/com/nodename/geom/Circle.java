package com.bioxx.jMapGen.com.nodename.geom;

import com.bioxx.jMapGen.Point;

public final class Circle extends Object
{
	public Point center;
	public double radius;

	public Circle(double centerX, double centerY, double radius)
	{
		super();
		this.center = new com.bioxx.jMapGen.Point(centerX, centerY);
		this.radius = radius;
	}

	public String toString()
	{
		return "Circle (center: " + center + "; radius: " + radius + ")";
	}

}
