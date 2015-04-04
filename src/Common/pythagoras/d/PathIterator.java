//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Used to return the boundary of an {@link IShape}, one segment at a time.
 */
public interface PathIterator
{
    /** Specifies the even/odd rule for determining the interior of a path. */
    int WIND_EVEN_ODD = 0;

    /** Specifies the non-zero rule for determining the interior of a path. */
    int WIND_NON_ZERO = 1;

    /** Indicates the starting location for a new subpath. */
    int SEG_MOVETO = 0;

    /** Indicates the end point of a line to be drawn from the most recently specified point. */
    int SEG_LINETO = 1;

    /** Indicates a pair of points that specify a quadratic parametric curve to be drawn from the
     * most recently specified point. */
    int SEG_QUADTO = 2;

    /** Indicates a pair of points that specify a cubic parametric curve to be drawn from the most
     * recently specified point. */
    int SEG_CUBICTO = 3;

    /** Indicates that the preceding subpath should be closed by appending a line segment back to
     * the point corresponding to the most recent {@link #SEG_MOVETO}. */
    int SEG_CLOSE = 4;

    /**
     * Returns the winding rule used to determine the interior of this path.
     */
    int windingRule ();

    /**
     * Returns true if this path has no additional segments.
     */
    boolean isDone ();

    /**
     * Advances this path to the next segment.
     */
    void next ();

    /**
     * Returns the coordinates and type of the current path segment. The number of points stored in
     * {@code coords} differs by path segment type: 0 - {@link #SEG_CLOSE}, 1 - {@link
     * #SEG_MOVETO}, {@link #SEG_LINETO}, 2 - {@link #SEG_QUADTO}, 3 - {@link #SEG_CUBICTO}.
     *
     * @param coords a buffer into which the current coordinates will be copied. It must be of
     * length 6. Each point is stored as a pair of x,y coordinates.
     * @return the path segment type, e.g. {@link #SEG_MOVETO}.
     */
    int currentSegment (double[] coords);
}
