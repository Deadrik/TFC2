//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Math utility methods.
 */
public class MathUtil
{
    /** A small number. */
    public static final float EPSILON = 0.00001f;

    /** The circle constant, Tau (&#964;) http://tauday.com/ */
    public static final float TAU = (float)(Math.PI * 2);

    /** Twice Pi. */
    public static final float TWO_PI = TAU;

    /** Pi times one half. */
    public static final float HALF_PI = (float)(Math.PI * 0.5);

    /**
     * A cheaper version of {@link Math#round} that doesn't handle the special cases.
     */
    public static int round (float v) {
        return (v < 0f) ? (int)(v - 0.5f) : (int)(v + 0.5f);
    }

    /**
     * Returns the floor of v as an integer without calling the relatively expensive
     * {@link Math#floor}.
     */
    public static int ifloor (float v) {
        int iv = (int)v;
        return (v >= 0f || iv == v || iv == Integer.MIN_VALUE) ? iv : (iv - 1);
    }

    /**
     * Returns the ceiling of v as an integer without calling the relatively expensive
     * {@link Math#ceil}.
     */
    public static int iceil (float v) {
        int iv = (int)v;
        return (v <= 0f || iv == v || iv == Integer.MAX_VALUE) ? iv : (iv + 1);
    }

    /**
     * Clamps a value to the range [lower, upper].
     */
    public static float clamp (float v, float lower, float upper) {
        if (v < lower) return lower;
        else if (v > upper) return upper;
        else return v;
    }

    /**
     * Rounds a value to the nearest multiple of a target.
     */
    public static float roundNearest (float v, float target) {
        target = Math.abs(target);
        if (v >= 0) {
            return target * FloatMath.floor((v + 0.5f * target) / target);
        } else {
            return target * FloatMath.ceil((v - 0.5f * target) / target);
        }
    }

    /**
     * Checks whether the value supplied is in [lower, upper].
     */
    public static boolean isWithin (float v, float lower, float upper) {
        return v >= lower && v <= upper;
    }

    /**
     * Returns a random value according to the normal distribution with the provided mean and
     * standard deviation.
     *
     * @param normal a normally distributed random value.
     * @param mean the desired mean.
     * @param stddev the desired standard deviation.
     */
    public static float normal (float normal, float mean, float stddev) {
        return stddev*normal + mean;
    }

    /**
     * Returns a random value according to the exponential distribution with the provided mean.
     *
     * @param random a uniformly distributed random value.
     * @param mean the desired mean.
     */
    public static float exponential (float random, float mean) {
        return -FloatMath.log(1f - random) * mean;
    }

    /**
     * Linearly interpolates between two angles, taking the shortest path around the circle.
     * This assumes that both angles are in [-pi, +pi].
     */
    public static float lerpa (float a1, float a2, float t) {
        float ma1 = mirrorAngle(a1), ma2 = mirrorAngle(a2);
        float d = Math.abs(a2 - a1), md = Math.abs(ma1 - ma2);
        return (d < md) ? lerp(a1, a2, t) : mirrorAngle(lerp(ma1, ma2, t));
    }

    /**
     * Linearly interpolates between v1 and v2 by the parameter t.
     */
    public static float lerp (float v1, float v2, float t) {
        return v1 + t*(v2 - v1);
    }

    /**
     * Determines whether two values are "close enough" to equal.
     */
    public static boolean epsilonEquals (float v1, float v2) {
        return Math.abs(v1 - v2) < EPSILON;
    }

    /**
     * Returns the (shortest) distance between two angles, assuming that both angles are in
     * [-pi, +pi].
     */
    public static float angularDistance (float a1, float a2) {
        float ma1 = mirrorAngle(a1), ma2 = mirrorAngle(a2);
        return Math.min(Math.abs(a1 - a2), Math.abs(ma1 - ma2));
    }

    /**
     * Returns the (shortest) difference between two angles, assuming that both angles are in
     * [-pi, +pi].
     */
    public static float angularDifference (float a1, float a2) {
        float ma1 = mirrorAngle(a1), ma2 = mirrorAngle(a2);
        float diff = a1 - a2, mdiff = ma2 - ma1;
        return (Math.abs(diff) < Math.abs(mdiff)) ? diff : mdiff;
    }

    /**
     * Returns an angle in the range [-pi, pi).
     */
    public static float normalizeAngle (float a) {
        while (a < -FloatMath.PI) {
            a += TWO_PI;
        }
        while (a >= FloatMath.PI) {
            a -= TWO_PI;
        }
        return a;
    }

    /**
     * Returns an angle in the range [0, 2pi).
     */
    public static float normalizeAnglePositive (float a) {
        while (a < 0f) {
            a += TWO_PI;
        }
        while (a >= TWO_PI) {
            a -= TWO_PI;
        }
        return a;
    }

    /**
     * Returns the mirror angle of the specified angle (assumed to be in [-pi, +pi]).
     */
    public static float mirrorAngle (float a) {
        return (a > 0f ? FloatMath.PI : -FloatMath.PI) - a;
    }

    /**
     * Sets the number of decimal places to show when formatting values. By default, they are
     * formatted to three decimal places.
     */
    public static void setToStringDecimalPlaces (int places) {
        if (places < 0) throw new IllegalArgumentException("Decimal places must be >= 0.");
        TO_STRING_DECIMAL_PLACES = places;
    }

    /**
     * Formats the supplied value, truncated to the currently configured number of decimal places.
     * The value is also always preceded by a sign (e.g. +1.0 or -0.5).
     */
    public static String toString (float value) {
        return toString(value, TO_STRING_DECIMAL_PLACES);
    }

    /**
     * Formats the supplied floating point value, truncated to the given number of decimal places.
     * The value is also always preceded by a sign (e.g. +1.0 or -0.5).
     */
    public static String toString (float value, int decimalPlaces) {
        StringBuilder buf = new StringBuilder();
        if (value >= 0) buf.append("+");
        else {
            buf.append("-");
            value = -value;
        }
        int ivalue = (int)value;
        buf.append(ivalue);
        if (decimalPlaces > 0) {
            buf.append(".");
            for (int ii = 0; ii < decimalPlaces; ii++) {
                value = (value - ivalue) * 10;
                ivalue = (int)value;
                buf.append(ivalue);
            }
            // trim trailing zeros
            for (int ii = 0; ii < decimalPlaces-1; ii++) {
                if (buf.charAt(buf.length()-1) == '0') {
                    buf.setLength(buf.length()-1);
                }
            }
        }
        return buf.toString();
    }

    protected static int TO_STRING_DECIMAL_PLACES = 3;
}
