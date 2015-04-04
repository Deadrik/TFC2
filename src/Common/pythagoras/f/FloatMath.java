//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Utility methods and constants for single-precision floating point math. Extends {@link MathUtil}
 * with shim methods that call through to {@link Math} and convert the results to float.
 */
public class FloatMath extends MathUtil
{
    /** The ratio of a circle's circumference to its diameter. */
    public static final float PI = (float)Math.PI;

    /** The base value of the natural logarithm. */
    public static final float E = (float)Math.E;

    /**
     * Computes and returns the sine of the given angle.
     *
     * @see Math#sin
     */
    public static float sin (float a)
    {
        return (float)Math.sin(a);
    }

    /**
     * Computes and returns the cosine of the given angle.
     *
     * @see Math#cos
     */
    public static float cos (float a)
    {
        return (float)Math.cos(a);
    }

    /**
     * Computes and returns the tangent of the given angle.
     *
     * @see Math#tan
     */
    public static float tan (float a)
    {
        return (float)Math.tan(a);
    }

    /**
     * Computes and returns the arc sine of the given value.
     *
     * @see Math#asin
     */
    public static float asin (float a)
    {
        return (float)Math.asin(a);
    }

    /**
     * Computes and returns the arc cosine of the given value.
     *
     * @see Math#acos
     */
    public static float acos (float a)
    {
        return (float)Math.acos(a);
    }

    /**
     * Computes and returns the arc tangent of the given value.
     *
     * @see Math#atan
     */
    public static float atan (float a)
    {
        return (float)Math.atan(a);
    }

    /**
     * Computes and returns the arc tangent of the given values.
     *
     * @see Math#atan2
     */
    public static float atan2 (float y, float x)
    {
        return (float)Math.atan2(y, x);
    }

    /**
     * Converts from radians to degrees.
     *
     * @see Math#toDegrees
     */
    public static float toDegrees (float a)
    {
        return a * (180f / PI);
    }

    /**
     * Converts from degrees to radians.
     *
     * @see Math#toRadians
     */
    public static float toRadians (float a)
    {
        return a * (PI / 180f);
    }

    /**
     * Returns the square root of the supplied value.
     *
     * @see Math#sqrt
     */
    public static float sqrt (float v)
    {
        return (float)Math.sqrt(v);
    }

    /**
     * Returns the cube root of the supplied value.
     *
     * @see Math#cbrt
     */
    public static float cbrt (float v)
    {
        return (float)Math.cbrt(v);
    }

    /**
     * Computes and returns sqrt(x*x + y*y).
     *
     * @see Math#hypot
     */
    public static float hypot (float x, float y)
    {
        return (float)Math.hypot(x, y);
    }

    /**
     * Returns e to the power of the supplied value.
     *
     * @see Math#exp
     */
    public static float exp (float v)
    {
        return (float)Math.exp(v);
    }

    /**
     * Returns the natural logarithm of the supplied value.
     *
     * @see Math#log
     */
    public static float log (float v)
    {
        return (float)Math.log(v);
    }

    /**
     * Returns the base 10 logarithm of the supplied value.
     *
     * @see Math#log10
     */
    public static float log10 (float v)
    {
        return (float)Math.log10(v);
    }

    /**
     * Returns v to the power of e.
     *
     * @see Math#pow
     */
    public static float pow (float v, float e)
    {
        return (float)Math.pow(v, e);
    }

    /**
     * Returns the floor of v.
     *
     * @see Math#floor
     */
    public static float floor (float v)
    {
        return (float)Math.floor(v);
    }

    /**
     * Returns the ceiling of v.
     *
     * @see Math#ceil
     */
    public static float ceil (float v)
    {
        return (float)Math.ceil(v);
    }
}
