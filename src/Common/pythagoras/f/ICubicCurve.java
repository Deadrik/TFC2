//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Provides read-only access to a {@link CubicCurve}.
 */
public interface ICubicCurve extends IShape, Cloneable
{
    /** Returns the x-coordinate of the start of this curve. */
    float x1 ();

    /** Returns the y-coordinate of the start of this curve. */
    float y1 ();

    /** Returns the x-coordinate of the first control point. */
    float ctrlX1 ();

    /** Returns the y-coordinate of the first control point. */
    float ctrlY1 ();

    /** Returns the x-coordinate of the second control point. */
    float ctrlX2 ();

    /** Returns the y-coordinate of the second control point. */
    float ctrlY2 ();

    /** Returns the x-coordinate of the end of this curve. */
    float x2 ();

    /** Returns the y-coordinate of the end of this curve. */
    float y2 ();

    /** Returns a copy of the starting point of this curve. */
    Point p1 ();

    /** Returns a copy of the first control point of this curve. */
    Point ctrlP1 ();

    /** Returns a copy of the second control point of this curve. */
    Point ctrlP2 ();

    /** Returns a copy of the ending point of this curve. */
    Point p2 ();

    /** Returns the square of the flatness (maximum distance of a control point from the line
     * connecting the end points) of this curve. */
    float flatnessSq ();

    /** Returns the flatness (maximum distance of a control point from the line connecting the end
     * points) of this curve. */
    float flatness ();

    /** Subdivides this curve and stores the results into {@code left} and {@code right}. */
    void subdivide (CubicCurve left, CubicCurve right);

    /** Returns a mutable copy of this curve. */
    CubicCurve clone ();
}
