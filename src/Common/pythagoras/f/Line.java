//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;

/**
 * Represents a line segment.
 */
public class Line extends AbstractLine implements Serializable
{
    /** The x-coordinate of the start of this line segment. */
    public float x1;

    /** The y-coordinate of the start of this line segment. */
    public float y1;

    /** The x-coordinate of the end of this line segment. */
    public float x2;

    /** The y-coordinate of the end of this line segment. */
    public float y2;

    /**
     * Creates a line from (0,0) to (0,0).
     */
    public Line () {
    }

    /**
     * Creates a line from (x1,y1), to (x2,y2).
     */
    public Line (float x1, float y1, float x2, float y2) {
        setLine(x1, y1, x2, y2);
    }

    /**
     * Creates a line from p1 to p2.
     */
    public Line (IPoint p1, IPoint p2) {
        setLine(p1, p2);
    }

    /**
     * Sets the start and end point of this line to the specified values.
     */
    public void setLine (float x1, float y1, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Sets the start and end of this line to the specified points.
     */
    public void setLine (IPoint p1, IPoint p2) {
        setLine(p1.x(), p1.y(), p2.x(), p2.y());
    }

    @Override // from interface ILine
    public float x1 () {
        return x1;
    }

    @Override // from interface ILine
    public float y1 () {
        return y1;
    }

    @Override // from interface ILine
    public float x2 () {
        return x2;
    }

    @Override // from interface ILine
    public float y2 () {
        return y2;
    }
}
