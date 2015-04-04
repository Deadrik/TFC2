//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Provides read-only access to a {@link RoundRectangle}.
 */
public interface IRoundRectangle extends IRectangularShape, Cloneable
{
    /** Returns the width of the corner arc. */
    double arcWidth ();

    /** Returns the height of the corner arc. */
    double arcHeight ();

    /** Returns a mutable copy of this round rectangle. */
    RoundRectangle clone ();
}
