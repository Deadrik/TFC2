package za.co.iocom.math;

import java.util.Random;

/**
 * Class MathUtil contains some utility functions to calculate special mathematical functions.
 */
public class MathUtil
{
	public static Random random = new Random();

	/**
	 * Calculates the nth triangular number, and returns the result. The nth triangular number is simply all integers up
	 * to n summed, that is Tn = 1 + 2 + 3 + ... + n. The 0th triangular number is by convention 0, while it is not
	 * defined for negative values of n.
	 * 
	 * @param n
	 *            The triangular number to be calculated. n has to be non-nagative.
	 * @throws NegativeNumberException
	 *             when a negative argument is passed to the function.
	 * 
	 * @return the nth triangular number.
	 */
	public static int getTriangular(int n) throws NegativeNumberException
	{
		if (n < 0)
		{
			throw new NegativeNumberException(n);
		}
		else if (n == 0)
		{
			return 0;
		}
		else
		{
			return (n * (n + 1)) / 2; // Add n to the
		// previous
		// triangular
		// number
		}
	}

	/**
	 * Calculates the factorial of n, that is n!, and returns the result. The factorial of n is simply all integers up
	 * to n multiplied, that is Tn = 1 * 2 * 3 * ... * n. The 0! is by convention 1, while it is not defined for
	 * negative values of n.
	 * 
	 * @param n
	 *            The factorial to be calculated. n has to be non-nagative.
	 * @throws NegativeNumberException
	 *             when a negative argument is passed to the function.
	 * 
	 * @return the factorial of n number.
	 */
	public static int getFactorial(int n) throws NegativeNumberException
	{
		if (n < 0)
		{
			throw new NegativeNumberException(n);
		}
		else if (n == 0)
		{
			return 1;
		}
		else
		{
			int f = 1;
			
			for (int i = 0; i < n; i++)
			{
				f *= i;
			}

			return f;
		}
	}

	/**
	 * Returns the square of the given number.
	 */
	public static int sqr(int x)
	{
		return x * x;
	}

	/**
	 * Returns the square of the given number.
	 */
	public static double sqr(double x)
	{
		return x * x;
	}

	/**
	 * Returns the square of the given number.
	 */
	public static double sqr(float x)
	{
		return x * x;
	}

	/**
	 * Returns the square of the given number.
	 */
	public static double sqr(long x)
	{
		return x * x;
	}

	/**
	 * Returns the square of the given number.
	 */
	public static double sqr(short x)
	{
		return x * x;
	}

	/**
	 * Returns the square of the given number.
	 */
	public static double sqr(byte x)
	{
		return x * x;
	}

	/**
	 * Linearly interpolates a value in a given range.
	 * 
	 * If the value is below the inputMin, the outputMin is returned. If the value is above the inputMax, the outputMax
	 * is returned.
	 * 
	 * Otherwise, the returned value is outputMin + ((value - inputMin) / (inputMax - inputMin)) * (outputMax -
	 * outputMin).
	 * 
	 */
	public static double lerp(double value, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		if (value >= inputMax)
		{
			return outputMax;
		}

		return ramp(value, inputMin, inputMax, outputMin, outputMax);
	}

	/**
	 * This function is a smooth approximation for lerp.
	 */
	public static double sigmoid(double value, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		// temporary implementation
		if (value >= inputMax)
		{
			return outputMax;
		}

		return ramp(value, inputMin, inputMax, outputMin, outputMax);
	}

	/**
	 * If the value is below the inputMin, the outputMin is returned.
	 * 
	 * Otherwise, the returned value is outputMin + ((value - inputMin) / (inputMax - inputMin)) * (outputMax -
	 * outputMin).
	 */
	public static double ramp(double value, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		if (value <= inputMin)
		{
			return outputMin;
		}

		return line(value, inputMin, inputMax, outputMin, outputMax);
	}

	/**
	 * The returned value is outputMin + ((value - inputMin) / (inputMax - inputMin)) * (outputMax - outputMin).
	 */
	public static double line(double value, double inputMin, double inputMax, double outputMin, double outputMax)
	{
		return outputMin + ((value - inputMin) * (outputMax - outputMin) / (inputMax - inputMin));
	}
	
	/**
	 * Returns the minimum of three values.
	 * 
	 */
	public static double min(double v1, double v2, double v3)
	{
		return Math.min(Math.min(v1, v2), v3);
	}

	/**
	 * Returns the maximum of three values.
	 */
	public static double max(double v1, double v2, double v3)
	{
		return Math.max(Math.max(v1, v2), v3);
	}

	/**
	 * Linearly interpolates between two values.
	 */
	public static double lerp(double v1, double v2, double ratio)
	{
		return (v1 * (1 - ratio) + v2 * ratio);
	}
}