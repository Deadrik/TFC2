//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * An interface provided by all shapes.
 */
public interface IShape
{
    /** Returns true if this shape encloses no area. */
    boolean isEmpty ();

    /** Returns true if this shape contains the specified point. */
    boolean contains (double x, double y);

    /** Returns true if this shape contains the supplied point. */
    boolean contains (IPoint point);

    /** Returns true if this shape completely contains the specified rectangle. */
    boolean contains (double x, double y, double width, double height);

    /** Returns true if this shape completely contains the supplied rectangle. */
    boolean contains (IRectangle r);

    /** Returns true if this shape intersects the specified rectangle. */
    boolean intersects (double x, double y, double width, double height);

    /** Returns true if this shape intersects the supplied rectangle. */
    boolean intersects (IRectangle r);

    /** Returns a copy of the bounding rectangle for this shape. */
    Rectangle bounds ();

    /** Initializes the supplied rectangle with this shape's bounding rectangle.
     * @return the supplied rectangle. */
    Rectangle bounds (Rectangle target);

    /**
     * Returns an iterator over the path described by this shape.
     *
     * @param at if supplied, the points in the path are transformed using this.
     */
    PathIterator pathIterator (Transform at);

    /**
     * Returns an iterator over the path described by this shape.
     *
     * @param at if supplied, the points in the path are transformed using this.
     * @param flatness when approximating curved segments with lines, this controls the maximum
     *        distance the lines are allowed to deviate from the approximated curve, thus a higher
     *        flatness value generally allows for a path with fewer segments.
     */
    PathIterator pathIterator (Transform at, double flatness);
}
