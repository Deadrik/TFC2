//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Point-related utility methods.
 */
public class Points
{
    /** The point at the origin. */
    public static final IPoint ZERO = new Point(0f, 0f);

    /**
     * Returns the squared Euclidean distance between the specified two points.
     */
    public static float distanceSq (float x1, float y1, float x2, float y2) {
        x2 -= x1;
        y2 -= y1;
        return x2 * x2 + y2 * y2;
    }

    /**
     * Returns the Euclidean distance between the specified two points.
     */
    public static float distance (float x1, float y1, float x2, float y2) {
        return FloatMath.sqrt(distanceSq(x1, y1, x2, y2));
    }

    /**
     * Returns true if the supplied points' x and y components are equal to one another within
     * {@link MathUtil#EPSILON}.
     */
    public static boolean epsilonEquals (IPoint p1, IPoint p2) {
        return epsilonEquals(p1, p2, MathUtil.EPSILON);
    }

    /**
     * Returns true if the supplied points' x and y components are equal to one another within
     * {@code epsilon}.
     */
    public static boolean epsilonEquals (IPoint p1, IPoint p2, float epsilon) {
        return Math.abs(p1.x() - p2.x()) < epsilon && Math.abs(p1.y() - p2.y()) < epsilon;
    }

    /** Transforms a point as specified, storing the result in the point provided.
     * @return a reference to the result point, for chaining. */
    public static Point transform (float x, float y, float sx, float sy, float rotation,
                                   float tx, float ty, Point result) {
        return transform(x, y, sx, sy, FloatMath.sin(rotation), FloatMath.cos(rotation), tx, ty,
                         result);
    }

    /** Transforms a point as specified, storing the result in the point provided.
     * @return a reference to the result point, for chaining. */
    public static Point transform (float x, float y, float sx, float sy, float sina, float cosa,
                                   float tx, float ty, Point result) {
        return result.set((x*cosa - y*sina) * sx + tx, (x*sina + y*cosa) * sy + ty);
    }

    /** Inverse transforms a point as specified, storing the result in the point provided.
     * @return a reference to the result point, for chaining. */
    public static Point inverseTransform (float x, float y, float sx, float sy, float rotation,
                                          float tx, float ty, Point result) {
        x -= tx; y -= ty; // untranslate
        float sinnega = FloatMath.sin(-rotation), cosnega = FloatMath.cos(-rotation);
        float nx = (x * cosnega - y * sinnega); // unrotate
        float ny = (x * sinnega + y * cosnega);
        return result.set(nx / sx, ny / sy); // unscale
    }

    /**
     * Returns a string describing the supplied point, of the form <code>+x+y</code>,
     * <code>+x-y</code>, <code>-x-y</code>, etc.
     */
    public static String pointToString (float x, float y) {
        return MathUtil.toString(x) + MathUtil.toString(y);
    }
}
