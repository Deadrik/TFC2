//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

/**
 * Point-related utility methods.
 */
public class Points
{
    /**
     * Returns the squared Euclidian distance between the specified two points.
     */
    public static int distanceSq (int x1, int y1, int x2, int y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    /**
     * Returns the Euclidian distance between the specified two points, truncated to the nearest
     * integer.
     */
    public static int distance (int x1, int y1, int x2, int y2) {
        return (int)Math.sqrt(distanceSq(x1, y1, x2, y2));
    }

    /**
     * Returns the Manhattan distance between the specified two points.
     */
    public static int manhattanDistance (int x1, int y1, int x2, int y2)
    {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    /**
     * Returns a string describing the supplied point, of the form {@code +x+y}, {@code +x-y},
     * {@code -x-y}, etc.
     */
    public static String pointToString (int x, int y) {
        StringBuilder buf = new StringBuilder();
        if (x >= 0) buf.append("+");
        buf.append(x);
        if (y >= 0) buf.append("+");
        buf.append(y);
        return buf.toString();
    }
}
