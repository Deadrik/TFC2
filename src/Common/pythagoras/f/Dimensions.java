//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Dimension-related utility methods.
 */
public class Dimensions
{
    /** A dimension width zero width and height. */
    public static final IDimension ZERO = new Dimension(0, 0);

    /**
     * Returns a string describing the supplied dimension, of the form <code>widthxheight</code>.
     */
    public static String dimenToString (float width, float height) {
        return width + "x" + height;
    }
}
