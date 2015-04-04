//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Provides read-only access to a {@link Point}.
 */
public interface IPoint extends XY, Cloneable
{
    /** Returns the squared Euclidian distance between this point and the specified point. */
    float distanceSq (float px, float py);

    /** Returns the squared Euclidian distance between this point and the supplied point. */
    float distanceSq (IPoint p);

    /** Returns the Euclidian distance between this point and the specified point. */
    float distance (float px, float py);

    /** Returns the Euclidian distance between this point and the supplied point. */
    float distance (IPoint p);

    /** Returns the angle (in radians) of the vector starting at this point and ending at the
     * supplied other point. */
    float direction (IPoint other);

    /** Multiplies this point by a scale factor.
     * @return a new point containing the result. */
    Point mult (float s);

    /** Multiplies this point by a scale factor and places the result in the supplied object.
     * @return a reference to the result, for chaining. */
    Point mult (float s, Point result);

    /** Translates this point by the specified offset.
     * @return a new point containing the result. */
    Point add (float x, float y);

    /** Translates this point by the specified offset and stores the result in the object provided.
     * @return a reference to the result, for chaining. */
    Point add (float x, float y, Point result);

    /** Subtracts the supplied point from {@code this}.
     * @return a new point containing the result. */
    Point subtract (float x, float y);

    /** Subtracts the supplied point from {@code this} and stores the result in {@code result}.
     * @return a reference to the result, for chaining. */
    Point subtract (float x, float y, Point result);

    /** Subtracts the supplied point from {@code this} and stores the result in {@code result}.
     * @return a reference to the result, for chaining. */
    Point subtract (IPoint other, Point result);

    /** Rotates this point around the origin by the specified angle.
     * @return a new point containing the result. */
    Point rotate (float angle);

    /** Rotates this point around the origin by the specified angle, storing the result in the
     * point provided.
     * @return a reference to the result point, for chaining. */
    Point rotate (float angle, Point result);

    /** Returns a mutable copy of this point. */
    Point clone ();
}
