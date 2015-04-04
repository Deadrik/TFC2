//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Vector-related utility methods.
 */
public class Vectors
{
    /** A unit vector in the X+ direction. */
    public static final IVector UNIT_X = new Vector(1f, 0f);

    /** A unit vector in the Y+ direction. */
    public static final IVector UNIT_Y = new Vector(0f, 1f);

    /** The zero vector. */
    public static final IVector ZERO = new Vector(0f, 0f);

    /** A vector containing the minimum doubleing point value for all components
     * (note: the components are -{@link Float#MAX_VALUE}, not {@link Float#MIN_VALUE}). */
    public static final IVector MIN_VALUE = new Vector(-Float.MAX_VALUE, -Float.MAX_VALUE);

    /** A vector containing the maximum doubleing point value for all components. */
    public static final IVector MAX_VALUE = new Vector(Float.MAX_VALUE, Float.MAX_VALUE);

    /**
     * Creates a new vector from polar coordinates.
     */
    public static Vector fromPolar (double magnitude, double angle) {
        return new Vector(magnitude * Math.cos(angle), magnitude * Math.sin(angle));
    }

    /**
     * Creates a vector from {@code from} to {@code to}.
     */
    public static Vector from (IPoint from, IPoint to) {
        return new Vector(to.x() - from.x(), to.y() - from.y());
    }

    /**
     * Returns the magnitude of the specified vector.
     */
    public static double length (double x, double y) {
        return Math.sqrt(lengthSq(x, y));
    }

    /**
     * Returns the square of the magnitude of the specified vector.
     */
    public static double lengthSq (double x, double y) {
        return (x*x + y*y);
    }

    /**
     * Returns true if the supplied vector has zero magnitude.
     */
    public static boolean isZero (double x, double y) {
        return x == 0 && y == 0;
    }

    /**
     * Returns true if the supplied vector's x and y components are {@link MathUtil#EPSILON} close
     * to zero magnitude.
     */
    public static boolean isEpsilonZero (double x, double y) {
        return isEpsilonZero(x, y, MathUtil.EPSILON);
    }

    /**
     * Returns true if the supplied vector's x and y components are {@code epsilon} close to zero
     * magnitude.
     */
    public static boolean isEpsilonZero (double x, double y, double epsilon) {
        return Math.abs(x) <= epsilon && Math.abs(y) <= epsilon;
    }

    /**
     * Returns true if the supplied vectors' x and y components are equal to one another within
     * {@link MathUtil#EPSILON}.
     */
    public static boolean epsilonEquals (IVector v1, IVector v2) {
        return epsilonEquals(v1, v2, MathUtil.EPSILON);
    }

    /**
     * Returns true if the supplied vectors' x and y components are equal to one another within
     * {@code epsilon}.
     */
    public static boolean epsilonEquals (IVector v1, IVector v2, double epsilon) {
        return Math.abs(v1.x() - v2.x()) <= epsilon && Math.abs(v1.y() - v2.y()) <= epsilon;
    }

    /** Transforms a vector as specified (as a point, accounting for translation), storing the
     * result in the vector provided.
     * @return a reference to the result vector, for chaining. */
    public static Vector transform (double x, double y, double sx, double sy, double rotation,
                                    double tx, double ty, Vector result) {
        return transform(x, y, sx, sy, Math.sin(rotation), Math.cos(rotation), tx, ty,
                         result);
    }

    /**
     * Transforms a vector as specified, storing the result in the vector provided.
     * @return a reference to the result vector, for chaining.
     */
    public static Vector transform (double x, double y, double sx, double sy, double rotation,
                                    Vector result) {
        return transform(x, y, sx, sy, Math.sin(rotation), Math.cos(rotation), result);
    }

    /**
     * Transforms a vector as specified, storing the result in the vector provided.
     * @return a reference to the result vector, for chaining.
     */
    public static Vector transform (double x, double y, double sx, double sy, double sina, double cosa,
                                    Vector result) {
        return result.set((x*cosa - y*sina) * sx, (x*sina + y*cosa) * sy);
    }

    /** Transforms a vector as specified (as a point, accounting for translation), storing the
     * result in the vector provided.
     * @return a reference to the result vector, for chaining. */
    public static Vector transform (double x, double y, double sx, double sy, double sina, double cosa,
                                    double tx, double ty, Vector result) {
        return result.set((x*cosa - y*sina) * sx + tx, (x*sina + y*cosa) * sy + ty);
    }

    /**
     * Inverse transforms a vector as specified, storing the result in the vector provided.
     * @return a reference to the result vector, for chaining.
     */
    public static Vector inverseTransform (double x, double y, double sx, double sy, double rotation,
                                           Vector result) {
        double sinnega = Math.sin(-rotation), cosnega = Math.cos(-rotation);
        double nx = (x * cosnega - y * sinnega); // unrotate
        double ny = (x * sinnega + y * cosnega);
        return result.set(nx / sx, ny / sy); // unscale
    }

    /**
     * Returns a string describing the supplied vector, of the form <code>+x+y</code>,
     * <code>+x-y</code>, <code>-x-y</code>, etc.
     */
    public static String vectorToString (double x, double y) {
        return MathUtil.toString(x) + MathUtil.toString(y);
    }
}
