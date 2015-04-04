//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.util;

/**
 * Handles differences between the JDK and GWT platforms.
 */
public class Platform
{
    /**
     * Returns a hash code for the supplied float value.
     */
    public static int hashCode (float f1) {
        return Float.floatToIntBits(f1);
    }

    /**
     * Returns a hash code for the supplied double value.
     */
    public static int hashCode (double d1) {
        long bits = Double.doubleToLongBits(d1);
        return (int)(bits ^ (bits >>> 32));
    }

    /**
     * Clones the supplied array of bytes.
     */
    public static byte[] clone (byte[] values) {
        return values.clone();
    }

    /**
     * Clones the supplied array of ints.
     */
    public static int[] clone (int[] values) {
        return values.clone();
    }

    /**
     * Clones the supplied array of floats.
     */
    public static float[] clone (float[] values) {
        return values.clone();
    }

    /**
     * Clones the supplied array of doubles.
     */
    public static double[] clone (double[] values) {
        return values.clone();
    }
}
