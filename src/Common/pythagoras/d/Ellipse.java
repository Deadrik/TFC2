//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.io.Serializable;

/**
 * Represents an ellipse that is described by a framing rectangle.
 */
public class Ellipse extends AbstractEllipse implements Serializable
{
    /** The x-coordinate of the framing rectangle. */
    public double x;

    /** The y-coordinate of the framing rectangle. */
    public double y;

    /** The width of the framing rectangle. */
    public double width;

    /** The height of the framing rectangle. */
    public double height;

    /**
     * Creates an ellipse with framing rectangle (0x0+0+0).
     */
    public Ellipse () {
    }

    /**
     * Creates an ellipse with the specified framing rectangle.
     */
    public Ellipse (double x, double y, double width, double height) {
        setFrame(x, y, width, height);
    }

    @Override // from interface IRectangularShape
    public double x () {
        return x;
    }

    @Override // from interface IRectangularShape
    public double y () {
        return y;
    }

    @Override // from interface IRectangularShape
    public double width () {
        return width;
    }

    @Override // from interface IRectangularShape
    public double height () {
        return height;
    }

    @Override // from RectangularShape
    public void setFrame (double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
