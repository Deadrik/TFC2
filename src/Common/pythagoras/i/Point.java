//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

import java.io.Serializable;

/**
 * Represents a point on a plane.
 */
public class Point extends AbstractPoint implements Serializable
{
    /** The x-coordinate of the point. */
    public int x;

    /** The y-coordinate of the point. */
    public int y;

    /**
     * Constructs a point at (0, 0).
     */
    public Point () {
    }

    /**
     * Constructs a point at the specified coordinates.
     */
    public Point (int x, int y) {
        setLocation(x, y);
    }

    /**
     * Constructs a point with coordinates equal to the supplied point.
     */
    public Point (IPoint p) {
        setLocation(p.x(), p.y());
    }

    /**
     * Sets the coordinates of this point to be equal to those of the supplied point.
     */
    public void setLocation (IPoint p) {
        setLocation(p.x(), p.y());
    }

    /**
     * Sets the coordinates of this point to the supplied values.
     */
    public void setLocation (int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * A synonym for {@link #setLocation}.
     */
    public void move (int x, int y) {
        setLocation(x, y);
    }

    /**
     * Translates this point by the specified offset.
     */
    public void translate (int dx, int dy) {
        x += dx;
        y += dy;
    }

    @Override // from interface IPoint
    public int x () {
        return x;
    }

    @Override // from interface IPoint
    public int y () {
        return y;
    }
}
