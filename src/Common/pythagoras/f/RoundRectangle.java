//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;

/**
 * Represents a rectangle with rounded corners, defined by an arc width and height.
 */
public class RoundRectangle extends AbstractRoundRectangle implements Serializable
{
    /** The x-coordinate of the framing rectangle. */
    public float x;

    /** The y-coordinate of the framing rectangle. */
    public float y;

    /** The width of the framing rectangle. */
    public float width;

    /** The height of the framing rectangle. */
    public float height;

    /** The width of the arc that defines the rounded corners. */
    public float arcwidth;

    /** The height of the arc that defines the rounded corners. */
    public float archeight;

    /**
     * Creates a rounded rectangle with frame (0x0+0+0) and corners of size (0x0).
     */
    public RoundRectangle () {
    }

    /**
     * Creates a rounded rectangle with the specified frame and corner dimensions.
     */
    public RoundRectangle (float x, float y, float width, float height,
                           float arcwidth, float archeight) {
        setRoundRect(x, y, width, height, arcwidth, archeight);
    }

    /**
     * Sets the frame and corner dimensions of this rectangle to the specified values.
     */
    public void setRoundRect (float x, float y, float width, float height,
                              float arcwidth, float archeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.arcwidth = arcwidth;
        this.archeight = archeight;
    }

    /**
     * Sets the frame and corner dimensions of this rectangle to be equal to those of the supplied
     * rectangle.
     */
    public void setRoundRect (IRoundRectangle rr) {
        setRoundRect(rr.x(), rr.y(), rr.width(), rr.height(),
                     rr.arcWidth(), rr.arcHeight());
    }

    @Override // from interface IRoundRectangle
    public float arcWidth () {
        return arcwidth;
    }

    @Override // from interface IRoundRectangle
    public float arcHeight () {
        return archeight;
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

    @Override // from RoundRectangle
    public void setFrame (float x, float y, float width, float height) {
        setRoundRect(x, y, width, height, arcwidth, archeight);
    }
}
