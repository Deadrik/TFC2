//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Defines an x/y coordinate. This is implemented by both {@code Point} and {@code Vector} so that
 * APIs which require an x/y coordinate, but don't really want to mak the distinction between a
 * translation vector versus a point in 2D space, can simply accept both.
 */
public interface XY
{
    /** The x coordinate. */
    double x ();

    /** The y coordinate. */
    double y ();
}
