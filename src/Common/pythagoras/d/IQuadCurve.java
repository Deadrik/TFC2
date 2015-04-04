//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Provides read-only access to a {@link QuadCurve}.
 */
public interface IQuadCurve extends IShape, Cloneable
{
    /** Returns the x-coordinate of the start of this curve. */
    double x1 ();

    /** Returns the y-coordinate of the start of this curve. */
    double y1 ();

    /** Returns the x-coordinate of the control point. */
    double ctrlX ();

    /** Returns the y-coordinate of the control point. */
    double ctrlY ();

    /** Returns the x-coordinate of the end of this curve. */
    double x2 ();

    /** Returns the y-coordinate of the end of this curve. */
    double y2 ();

    /** Returns a copy of the starting point of this curve. */
    Point p1 ();

    /** Returns a copy of the control point of this curve. */
    Point ctrlP ();

    /** Returns a copy of the ending point of this curve. */
    Point p2 ();

    /** Returns the square of the flatness (maximum distance of a control point from the line
     * connecting the end points) of this curve. */
    double flatnessSq ();

    /** Returns the flatness (maximum distance of a control point from the line connecting the end
     * points) of this curve. */
    double flatness ();

    /** Subdivides this curve and stores the results into {@code left} and {@code right}. */
    void subdivide (QuadCurve left, QuadCurve right);

    /** Returns a mutable copy of this curve. */
    QuadCurve clone ();
}
