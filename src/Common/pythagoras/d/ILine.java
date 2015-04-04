//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Provides read-only access to a {@link Line}.
 */
public interface ILine extends IShape, Cloneable
{
    /** Returns the x-coordinate of the start of this line. */
    double x1 ();

    /** Returns the y-coordinate of the start of this line. */
    double y1 ();

    /** Returns the x-coordinate of the end of this line. */
    double x2 ();

    /** Returns the y-coordinate of the end of this line. */
    double y2 ();

    /** Returns a copy of the starting point of this line. */
    Point p1 ();

    /** Initializes the supplied point with this line's starting point.
     * @return the supplied point. */
    Point p1 (Point target);

    /** Returns a copy of the ending point of this line. */
    Point p2 ();

    /** Initializes the supplied point with this line's ending point.
     * @return the supplied point. */
    Point p2 (Point target);

    /** Returns the square of the distance from the specified point to the line defined by this
     * line segment. */
    double pointLineDistSq (double px, double py);

    /** Returns the square of the distance from the supplied point to the line defined by this line
     * segment. */
    double pointLineDistSq (IPoint p);

    /** Returns the distance from the specified point to the line defined by this line segment. */
    double pointLineDist (double px, double py);

    /** Returns the distance from the supplied point to the line defined by this line segment. */
    double pointLineDist (IPoint p);

    /** Returns the square of the distance from the specified point this line segment. */
    double pointSegDistSq (double px, double py);

    /** Returns the square of the distance from the supplied point this line segment. */
    double pointSegDistSq (IPoint p);

    /** Returns the distance from the specified point this line segment. */
    double pointSegDist (double px, double py);

    /** Returns the distance from the supplied point this line segment. */
    double pointSegDist (IPoint p);

    /** Returns an indicator of where the specified point (px,py) lies with respect to this line
     * segment. */
    int relativeCCW (double px, double py);

    /** Returns an indicator of where the specified point lies with respect to this line segment. */
    int relativeCCW (IPoint p);

    /** Returns a mutable copy of this line. */
    Line clone ();
}
