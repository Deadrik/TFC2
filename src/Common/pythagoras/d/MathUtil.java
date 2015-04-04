//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Math utility methods.
 */
public class MathUtil
{
    /** A small number. */
    public static final double EPSILON = 0.00001f;

    /** The circle constant, Tau (&#964;) http://tauday.com/ */
    public static final double TAU = (Math.PI * 2);

    /** Twice Pi. */
    public static final double TWO_PI = TAU;

    /** Pi times one half. */
    public static final double HALF_PI = (Math.PI * 0.5);

    /**
     * A cheaper version of {@link Math#round} that doesn't handle the special cases.
     */
    public static int round (double v) {
        return (v < 0f) ? (int)(v - 0.5f) : (int)(v + 0.5f);
    }

    /**
     * Returns the floor of v as an integer without calling the relatively expensive
     * {@link Math#floor}.
     */
    public static int ifloor (double v) {
        int iv = (int)v;
        return (v >= 0f || iv == v || iv == Integer.MIN_VALUE) ? iv : (iv - 1);
    }

    /**
     * Returns the ceiling of v as an integer without calling the relatively expensive
     * {@link Math#ceil}.
     */
    public static int iceil (double v) {
        int iv = (int)v;
        return (v <= 0f || iv == v || iv == Integer.MAX_VALUE) ? iv : (iv + 1);
    }

    /**
     * Clamps a value to the range [lower, upper].
     */
    public static double clamp (double v, double lower, double upper) {
        if (v < lower) return lower;
        else if (v > upper) return upper;
        else return v;
    }

    /**
     * Rounds a value to the nearest multiple of a target.
     */
    public static double roundNearest (double v, double target) {
        target = Math.abs(target);
        if (v >= 0) {
            return target * Math.floor((v + 0.5f * target) / target);
        } else {
            return target * Math.ceil((v - 0.5f * target) / target);
        }
    }

    /**
     * Checks whether the value supplied is in [lower, upper].
     */
    public static boolean isWithin (double v, double lower, double upper) {
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
    public static double normal (double normal, double mean, double stddev) {
        return stddev*normal + mean;
    }

    /**
     * Returns a random value according to the exponential distribution with the provided mean.
     *
     * @param random a uniformly distributed random value.
     * @param mean the desired mean.
     */
    public static double exponential (double random, double mean) {
        return -Math.log(1f - random) * mean;
    }

    /**
     * Linearly interpolates between two angles, taking the shortest path around the circle.
     * This assumes that both angles are in [-pi, +pi].
     */
    public static double lerpa (double a1, double a2, double t) {
        double ma1 = mirrorAngle(a1), ma2 = mirrorAngle(a2);
        double d = Math.abs(a2 - a1), md = Math.abs(ma1 - ma2);
        return (d < md) ? lerp(a1, a2, t) : mirrorAngle(lerp(ma1, ma2, t));
    }

    /**
     * Linearly interpolates between v1 and v2 by the parameter t.
     */
    public static double lerp (double v1, double v2, double t) {
        return v1 + t*(v2 - v1);
    }

    /**
     * Determines whether two values are "close enough" to equal.
     */
    public static boolean epsilonEquals (double v1, double v2) {
        return Math.abs(v1 - v2) < EPSILON;
    }

    /**
     * Returns the (shortest) distance between two angles, assuming that both angles are in
     * [-pi, +pi].
     */
    public static double angularDistance (double a1, double a2) {
        double ma1 = mirrorAngle(a1), ma2 = mirrorAngle(a2);
        return Math.min(Math.abs(a1 - a2), Math.abs(ma1 - ma2));
    }

    /**
     * Returns the (shortest) difference between two angles, assuming that both angles are in
     * [-pi, +pi].
     */
    public static double angularDifference (double a1, double a2) {
        double ma1 = mirrorAngle(a1), ma2 = mirrorAngle(a2);
        double diff = a1 - a2, mdiff = ma2 - ma1;
        return (Math.abs(diff) < Math.abs(mdiff)) ? diff : mdiff;
    }

    /**
     * Returns an angle in the range [-pi, pi).
     */
    public static double normalizeAngle (double a) {
        while (a < -Math.PI) {
            a += TWO_PI;
        }
        while (a >= Math.PI) {
            a -= TWO_PI;
        }
        return a;
    }

    /**
     * Returns an angle in the range [0, 2pi).
     */
    public static double normalizeAnglePositive (double a) {
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
    public static double mirrorAngle (double a) {
        return (a > 0f ? Math.PI : -Math.PI) - a;
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
    public static String toString (double value) {
        return toString(value, TO_STRING_DECIMAL_PLACES);
    }

    /**
     * Formats the supplied doubleing point value, truncated to the given number of decimal places.
     * The value is also always preceded by a sign (e.g. +1.0 or -0.5).
     */
    public static String toString (double value, int decimalPlaces) {
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
