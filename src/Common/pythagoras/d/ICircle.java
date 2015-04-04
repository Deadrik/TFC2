//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Provides read-only access to a {@link Circle}.
 */
public interface ICircle
{
    /** Returns this circle's x-coordinate. */
    double x ();

    /** Returns this circle's y-coordinate. */
    double y ();

    /** Returns this circle's radius. */
    double radius ();

    /** Returns true if this circle intersects the supplied circle. */
    boolean intersects (ICircle c);

    /** Returns true if this circle contains the supplied point. */
    boolean contains (IPoint p);

    /** Returns true if this circle contains the specified point. */
    boolean contains (double x, double y);

    /** Translates the circle by the specified offset.
     * @return a new Circle containing the result. */
    Circle offset (double x, double y);

    /** Translates the circle by the specified offset and stores the result in the supplied object.
     * @return a reference to the result, for chaining. */
    Circle offset (double x, double y, Circle result);

    /** Returns a mutable copy of this circle. */
    Circle clone ();
}
