//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * An interface implemented by {@link IShape} classes whose geometry is defined by a rectangular
 * frame. The framing rectangle <em>defines</em> the geometry, but may in some cases differ from
 * the <em>bounding</em> rectangle of the shape.
 */
public interface IRectangularShape extends IShape
{
    /** Returns the x-coordinate of the upper-left corner of the framing rectangle. */
    float x ();

    /** Returns the y-coordinate of the upper-left corner of the framing rectangle. */
    float y ();

    /** Returns the width of the framing rectangle. */
    float width ();

    /** Returns the height of the framing rectangle. */
    float height ();

    /** Returns the minimum x,y-coordinate of the framing rectangle. */
    Point min ();

    /** Returns the minimum x-coordinate of the framing rectangle. */
    float minX ();

    /** Returns the minimum y-coordinate of the framing rectangle. */
    float minY ();

    /** Returns the maximum x,y-coordinate of the framing rectangle. */
    Point max ();

    /** Returns the maximum x-coordinate of the framing rectangle. */
    float maxX ();

    /** Returns the maximum y-coordinate of the framing rectangle. */
    float maxY ();

    /** Returns the center of the framing rectangle. */
    Point center ();

    /** Returns the x-coordinate of the center of the framing rectangle. */
    float centerX ();

    /** Returns the y-coordinate of the center of the framing rectangle. */
    float centerY ();

    /** Returns a copy of this shape's framing rectangle. */
    Rectangle frame ();

    /** Initializes the supplied rectangle with this shape's framing rectangle.
     * @return the supplied rectangle. */
    Rectangle frame (Rectangle target);
}
