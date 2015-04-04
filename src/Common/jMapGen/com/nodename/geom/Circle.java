package jMapGen.com.nodename.geom;

import jMapGen.Point;

public final class Circle extends Object
{
	public Point center;
	public double radius;

	public Circle(double centerX, double centerY, double radius)
	{
		super();
		this.center = new jMapGen.Point(centerX, centerY);
		this.radius = radius;
	}

	public String toString()
	{
		return "Circle (center: " + center + "; radius: " + radius + ")";
	}

}
