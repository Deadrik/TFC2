//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;

/**
 * Represents a magnitude in two dimensions.
 */
public class Dimension extends AbstractDimension implements Serializable
{
    /** The magnitude in the x-dimension. */
    public float width;

    /** The magnitude in the y-dimension. */
    public float height;

    /**
     * Creates a dimension with magnitude (0, 0).
     */
    public Dimension () {
        this(0, 0);
    }

    /**
     * Creates a dimension with the specified width and height.
     */
    public Dimension (float width, float height) {
        setSize(width, height);
    }

    /**
     * Creates a dimension with width and height equal to the supplied dimension.
     */
    public Dimension (IDimension d) {
        this(d.width(), d.height());
    }

    /**
     * Sets the magnitudes of this dimension to the specified width and height.
     */
    public void setSize (float width, float height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the magnitudes of this dimension to be equal to the supplied dimension.
     */
    public void setSize (IDimension d) {
        setSize(d.width(), d.height());
    }

    @Override // from interface IDimension
    public float width () {
        return width;
    }

    @Override // from interface IDimension
    public float height () {
        return height;
    }
}
