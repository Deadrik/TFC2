//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

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

    /** A vector containing the minimum floating point value for all components
     * (note: the components are -{@link Float#MAX_VALUE}, not {@link Float#MIN_VALUE}). */
    public static final IVector MIN_VALUE = new Vector(-Float.MAX_VALUE, -Float.MAX_VALUE);

    /** A vector containing the maximum floating point value for all components. */
    public static final IVector MAX_VALUE = new Vector(Float.MAX_VALUE, Float.MAX_VALUE);

    /**
     * Creates a new vector from polar coordinates.
     */
    public static Vector fromPolar (float magnitude, float angle) {
        return new Vector(magnitude * FloatMath.cos(angle), magnitude * FloatMath.sin(angle));
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
    public static float length (float x, float y) {
        return FloatMath.sqrt(lengthSq(x, y));
    }

    /**
     * Returns the square of the magnitude of the specified vector.
     */
    public static float lengthSq (float x, float y) {
        return (x*x + y*y);
    }

    /**
     * Returns true if the supplied vector has zero magnitude.
     */
    public static boolean isZero (float x, float y) {
        return x == 0 && y == 0;
    }

    /**
     * Returns true if the supplied vector's x and y components are {@link MathUtil#EPSILON} close
     * to zero magnitude.
     */
    public static boolean isEpsilonZero (float x, float y) {
        return isEpsilonZero(x, y, MathUtil.EPSILON);
    }

    /**
     * Returns true if the supplied vector's x and y components are {@code epsilon} close to zero
     * magnitude.
     */
    public static boolean isEpsilonZero (float x, float y, float epsilon) {
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
    public static boolean epsilonEquals (IVector v1, IVector v2, float epsilon) {
        return Math.abs(v1.x() - v2.x()) <= epsilon && Math.abs(v1.y() - v2.y()) <= epsilon;
    }

    /** Transforms a vector as specified (as a point, accounting for translation), storing the
     * result in the vector provided.
     * @return a reference to the result vector, for chaining. */
    public static Vector transform (float x, float y, float sx, float sy, float rotation,
                                    float tx, float ty, Vector result) {
        return transform(x, y, sx, sy, FloatMath.sin(rotation), FloatMath.cos(rotation), tx, ty,
                         result);
    }

    /**
     * Transforms a vector as specified, storing the result in the vector provided.
     * @return a reference to the result vector, for chaining.
     */
    public static Vector transform (float x, float y, float sx, float sy, float rotation,
                                    Vector result) {
        return transform(x, y, sx, sy, FloatMath.sin(rotation), FloatMath.cos(rotation), result);
    }

    /**
     * Transforms a vector as specified, storing the result in the vector provided.
     * @return a reference to the result vector, for chaining.
     */
    public static Vector transform (float x, float y, float sx, float sy, float sina, float cosa,
                                    Vector result) {
        return result.set((x*cosa - y*sina) * sx, (x*sina + y*cosa) * sy);
    }

    /** Transforms a vector as specified (as a point, accounting for translation), storing the
     * result in the vector provided.
     * @return a reference to the result vector, for chaining. */
    public static Vector transform (float x, float y, float sx, float sy, float sina, float cosa,
                                    float tx, float ty, Vector result) {
        return result.set((x*cosa - y*sina) * sx + tx, (x*sina + y*cosa) * sy + ty);
    }

    /**
     * Inverse transforms a vector as specified, storing the result in the vector provided.
     * @return a reference to the result vector, for chaining.
     */
    public static Vector inverseTransform (float x, float y, float sx, float sy, float rotation,
                                           Vector result) {
        float sinnega = FloatMath.sin(-rotation), cosnega = FloatMath.cos(-rotation);
        float nx = (x * cosnega - y * sinnega); // unrotate
        float ny = (x * sinnega + y * cosnega);
        return result.set(nx / sx, ny / sy); // unscale
    }

    /**
     * Returns a string describing the supplied vector, of the form <code>+x+y</code>,
     * <code>+x-y</code>, <code>-x-y</code>, etc.
     */
    public static String vectorToString (float x, float y) {
        return MathUtil.toString(x) + MathUtil.toString(y);
    }
}
