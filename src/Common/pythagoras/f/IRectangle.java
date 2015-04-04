//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Provides read-only access to a {@link Rectangle}.
 */
public interface IRectangle extends IRectangularShape, Cloneable
{
    /** The bitmask that indicates that a point lies to the left of this rectangle. See
     * {@link #outcode}. */
    int OUT_LEFT = 1;

    /** The bitmask that indicates that a point lies above this rectangle. See {@link #outcode}. */
    int OUT_TOP = 2;

    /** The bitmask that indicates that a point lies to the right of this rectangle. See
     * {@link #outcode}. */
    int OUT_RIGHT = 4;

    /** The bitmask that indicates that a point lies below this rectangle. See {@link #outcode}. */
    int OUT_BOTTOM = 8;

    /** Returns a copy of this rectangle's upper-left corner. */
    Point location ();

    /** Initializes the supplied point with this rectangle's upper-left corner.
     * @return the supplied point. */
    Point location (Point target);

    /** Returns a copy of this rectangle's size. */
    Dimension size ();

    /** Initializes the supplied dimension with this rectangle's size.
     * @return the supplied dimension. */
    Dimension size (Dimension target);

    /** Returns the intersection of the specified rectangle and this rectangle (i.e. the largest
     * rectangle contained in both this and the specified rectangle). */
    Rectangle intersection (float rx, float ry, float rw, float rh);

    /** Returns the intersection of the supplied rectangle and this rectangle (i.e. the largest
     * rectangle contained in both this and the supplied rectangle). */
    Rectangle intersection (IRectangle r);

    /** Returns the union of the supplied rectangle and this rectangle (i.e. the smallest rectangle
     * that contains both this and the supplied rectangle). */
    Rectangle union (IRectangle r);

    /** Returns true if the specified line segment intersects this rectangle. */
    boolean intersectsLine (float x1, float y1, float x2, float y2);

    /** Returns true if the supplied line segment intersects this rectangle. */
    boolean intersectsLine (ILine l);

    /** Returns a set of flags indicating where the specified point lies in relation to the bounds
     * of this rectangle. See {@link #OUT_LEFT}, etc. */
    int outcode (float px, float py);

    /** Returns a set of flags indicating where the supplied point lies in relation to the bounds of
     * this rectangle. See {@link #OUT_LEFT}, etc. */
    int outcode (IPoint point);

    /** Returns a mutable copy of this rectangle. */
    Rectangle clone ();
}
