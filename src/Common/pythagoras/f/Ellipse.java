//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;

/**
 * Represents an ellipse that is described by a framing rectangle.
 */
public class Ellipse extends AbstractEllipse implements Serializable
{
    /** The x-coordinate of the framing rectangle. */
    public float x;

    /** The y-coordinate of the framing rectangle. */
    public float y;

    /** The width of the framing rectangle. */
    public float width;

    /** The height of the framing rectangle. */
    public float height;

    /**
     * Creates an ellipse with framing rectangle (0x0+0+0).
     */
    public Ellipse () {
    }

    /**
     * Creates an ellipse with the specified framing rectangle.
     */
    public Ellipse (float x, float y, float width, float height) {
        setFrame(x, y, width, height);
    }

    @Override // from interface IRectangularShape
    public float x () {
        return x;
    }

    @Override // from interface IRectangularShape
    public float y () {
        return y;
    }

    @Override // from interface IRectangularShape
    public float width () {
        return width;
    }

    @Override // from interface IRectangularShape
    public float height () {
        return height;
    }

    @Override // from RectangularShape
    public void setFrame (float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
