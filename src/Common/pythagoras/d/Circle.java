//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.io.Serializable;

/**
 * Represents a circle on a plane.
 */
public class Circle extends AbstractCircle implements Serializable
{
    /** The x-coordinate of the circle. */
    public double x;

    /** The y-coordinate of the circle. */
    public double y;

    /** The radius of the circle. */
    public double radius;

    /**
     * Constructs a circle at (0, 0) with radius 0
     */
    public Circle () {
    }

    /**
     * Constructs a circle with the specified properties
     */
    public Circle (double x, double y, double radius) {
        set(x, y, radius);
    }

    /**
     * Constructs a circle with the specified properties
     */
    public Circle (IPoint p, double radius) {
        this(p.x(), p.y(), radius);
    }

    /**
     * Constructs a circle with properties equal to the supplied circle.
     */
    public Circle (ICircle c) {
        this(c.x(), c.y(), c.radius());
    }

    /** Sets the properties of this circle to be equal to those of the supplied circle.
     * @return a reference to this this, for chaining. */
    public Circle set (ICircle c) {
        return set(c.x(), c.y(), c.radius());
    }

    /** Sets the properties of this circle to the supplied values.
     * @return a reference to this this, for chaining. */
    public Circle set (double x, double y, double radius) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        return this;
    }

    @Override
    public double x () {
        return x;
    }

    @Override
    public double y () {
        return y;
    }

    @Override
    public double radius () {
        return radius;
    }
}
