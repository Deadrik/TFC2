//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

/**
 * Provides read-only access to a {@link Point}.
 */
public interface IPoint extends Cloneable
{
    /** Returns this point's x-coordinate. */
    int x ();

    /** Returns this point's y-coordinate. */
    int y ();

    /** Returns the squared Euclidian distance between this point and the specified point. */
    int distanceSq (int px, int py);

    /** Returns the squared Euclidian distance between this point and the supplied point. */
    int distanceSq (IPoint p);

    /** Returns the Euclidian distance between this point and the specified point. */
    int distance (int px, int py);

    /** Returns the Euclidian distance between this point and the supplied point. */
    int distance (IPoint p);

    /** Returns a mutable copy of this point. */
    Point clone ();
}
