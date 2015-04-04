//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

import java.io.Serializable;

/**
 * Represents a magnitude in two dimensions.
 */
public class Dimension extends AbstractDimension implements Serializable
{
    /** The magnitude in the x-dimension. */
    public int width;

    /** The magnitude in the y-dimension. */
    public int height;

    /**
     * Creates a dimension with magnitude (0, 0).
     */
    public Dimension () {
        this(0, 0);
    }

    /**
     * Creates a dimension with the specified width and height.
     */
    public Dimension (int width, int height) {
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
    public void setSize (int width, int height) {
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
    public int width () {
        return width;
    }

    @Override // from interface IDimension
    public int height () {
        return height;
    }
}
