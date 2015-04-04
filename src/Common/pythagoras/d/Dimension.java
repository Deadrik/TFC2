//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.io.Serializable;

/**
 * Represents a magnitude in two dimensions.
 */
public class Dimension extends AbstractDimension implements Serializable
{
    /** The magnitude in the x-dimension. */
    public double width;

    /** The magnitude in the y-dimension. */
    public double height;

    /**
     * Creates a dimension with magnitude (0, 0).
     */
    public Dimension () {
        this(0, 0);
    }

    /**
     * Creates a dimension with the specified width and height.
     */
    public Dimension (double width, double height) {
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
    public void setSize (double width, double height) {
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
    public double width () {
        return width;
    }

    @Override // from interface IDimension
    public double height () {
        return height;
    }
}
