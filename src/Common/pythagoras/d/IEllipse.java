//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Provides read-only access to an {@link Ellipse}.
 */
public interface IEllipse extends IRectangularShape, Cloneable
{
    /** Returns a mutable copy of this ellipse. */
    Ellipse clone ();
}
