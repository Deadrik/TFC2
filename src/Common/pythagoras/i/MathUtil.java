//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

/**
 * Math utility methods.
 */
public class MathUtil
{
    /**
     * Clamps the supplied {@code value} to between {@code low} and {@code high} (both inclusive).
     */
    public static int clamp (int value, int low, int high) {
        if (value < low) return low;
        if (value > high) return high;
        return value;
    }

    /**
     * Computes the floored division {@code dividend/divisor} which is useful when dividing
     * potentially negative numbers into bins.
     *
     * <p> For example, the following numbers {@code floorDiv} 10 are:
     * <pre>
     * -15 -10 -8 -2 0 2 8 10 15
     *  -2  -1 -1 -1 0 0 0  1  1
     * </pre>
     */
    public static int floorDiv (int dividend, int divisor) {
        boolean numpos = dividend >= 0, denpos = divisor >= 0;
        if (numpos == denpos) return dividend / divisor;
        return denpos ? (dividend - divisor + 1) / divisor : (dividend - divisor - 1) / divisor;
    }
}
