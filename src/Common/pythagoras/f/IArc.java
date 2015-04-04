//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Provides read-only access to an {@link Arc}.
 */
public interface IArc extends IRectangularShape, Cloneable
{
    /** An arc type indicating a simple, unconnected curve. */
    int OPEN = 0;

    /** An arc type indicating a closed curve, connected by a straight line from the starting to
     * the ending point of the arc. */
    int CHORD = 1;

    /** An arc type indicating a closed curve, connected by a line from the starting point of the
     * arc to the center of the circle defining the arc, and another straight line from that center
     * to the ending point of the arc. */
    int PIE = 2;

    /** Returns the type of this arc: {@link #OPEN}, etc. */
    int arcType ();

    /** Returns the starting angle of this arc. */
    float angleStart ();

    /** Returns the angular extent of this arc. */
    float angleExtent ();

    /** Returns the intersection of the ray from the center (defined by the starting angle) and the
     * elliptical boundary of the arc. */
    Point startPoint ();

    /** Writes the intersection of the ray from the center (defined by the starting angle) and the
     * elliptical boundary of the arc into {@code target}.
     * @return the supplied point. */
    Point startPoint (Point target);

    /** Returns the intersection of the ray from the center (defined by the starting angle plus the
     * angular extent of the arc) and the elliptical boundary of the arc. */
    Point endPoint ();

    /** Writes the intersection of the ray from the center (defined by the starting angle plus the
     * angular extent of the arc) and the elliptical boundary of the arc into {@code target}.
     * @return the supplied point. */
    Point endPoint (Point target);

    /** Returns whether the specified angle is within the angular extents of this arc. */
    boolean containsAngle (float angle);

    /** Returns a mutable copy of this arc. */
    Arc clone ();
}
