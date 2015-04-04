//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

/**
 * An interface provided by all shapes.
 */
public interface IShape
{
    /** Returns true if this shape encloses no area. */
    boolean isEmpty ();

    /** Returns true if this shape contains the specified point. */
    boolean contains (int x, int y);

    /** Returns true if this shape contains the supplied point. */
    boolean contains (IPoint point);

    /** Returns true if this shape completely contains the specified rectangle. */
    boolean contains (int x, int y, int width, int height);

    /** Returns true if this shape completely contains the supplied rectangle. */
    boolean contains (IRectangle r);

    /** Returns true if this shape intersects the specified rectangle. */
    boolean intersects (int x, int y, int width, int height);

    /** Returns true if this shape intersects the supplied rectangle. */
    boolean intersects (IRectangle r);

    /** Returns a copy of the bounding rectangle for this shape. */
    Rectangle bounds ();

    /** Initializes the supplied rectangle with this shape's bounding rectangle.
     * @return the supplied rectangle. */
    Rectangle bounds (Rectangle target);
}
