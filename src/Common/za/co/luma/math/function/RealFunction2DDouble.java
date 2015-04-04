package za.co.luma.math.function;

/**
 * Class suitable for representing mappings from continuous 2D space [0, 1]*[0, 1] to [0, 1].
 * 
 * @author Herman Tulleken
 */
public abstract class RealFunction2DDouble
{
	public abstract double getDouble(double x, double y);
}
