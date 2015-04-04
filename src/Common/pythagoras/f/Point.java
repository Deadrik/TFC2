//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;

/**
 * Represents a point on a plane.
 */
public class Point extends AbstractPoint implements Serializable
{
    /** The x-coordinate of the point. */
    public float x;

    /** The y-coordinate of the point. */
    public float y;

    /**
     * Constructs a point at (0, 0).
     */
    public Point () {
    }

    /**
     * Constructs a point at the specified coordinates.
     */
    public Point (float x, float y) {
        set(x, y);
    }

    /**
     * Constructs a point with coordinates equal to the supplied point.
     */
    public Point (IPoint p) {
        set(p.x(), p.y());
    }

    /** Sets the coordinates of this point to be equal to those of the supplied point.
     * @return a reference to this this, for chaining. */
    public Point set (IPoint p) {
        return set(p.x(), p.y());
    }

    /** Sets the coordinates of this point to the supplied values.
     * @return a reference to this this, for chaining. */
    public Point set (float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /** Multiplies this point by a scale factor.
     * @return a a reference to this point, for chaining. */
    public Point multLocal (float s) {
        return mult(s, this);
    }

    /** Translates this point by the specified offset.
     * @return a reference to this point, for chaining. */
    public Point addLocal (float dx, float dy) {
        return add(dx, dy, this);
    }

    /** Rotates this point in-place by the specified angle.
     * @return a reference to this point, for chaining. */
    public Point rotateLocal (float angle) {
        return rotate(angle, this);
    }

    /** Subtracts the supplied x/y from this point.
     * @return a reference to this point, for chaining. */
    public Point subtractLocal (float x, float y) {
        return subtract(x, y, this);
    }

    @Override // from XY
    public float x () {
        return x;
    }

    @Override // from XY
    public float y () {
        return y;
    }
}
