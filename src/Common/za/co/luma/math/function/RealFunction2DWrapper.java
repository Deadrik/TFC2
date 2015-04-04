package za.co.luma.math.function;

import za.co.iocom.math.MathUtil;

/**
 * A class that wraps a RealFunction2D in a RealFunmtion2DDouble object.
 * 
 * @author Herman Tulleken
 */
public class RealFunction2DWrapper extends RealFunction2DDouble
{
	
	private RealFunction2D function;
	
	private double outputMin;
	private double outputMax;
	private double inputMax;
	private double inputMin;
	
	public RealFunction2DWrapper(RealFunction2D function, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		this.function = function;
		
		this.outputMin = outputMin;
		this.outputMax = outputMax;
		this.inputMax = inputMax;
		this.inputMin = inputMin;
	}

	@Override
	public double getDouble(double x, double y)
	{
		return MathUtil.lerp(function.getDouble((int) x, (int) y), inputMin, inputMax, outputMin, outputMax);
	}

}
