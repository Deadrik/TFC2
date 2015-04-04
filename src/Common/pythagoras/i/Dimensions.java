//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

/**
 * Dimension-related utility methods.
 */
public class Dimensions
{
    /**
     * Returns a string describing the supplied dimension, of the form {@code widthxheight}.
     */
    public static String dimenToString (int width, int height) {
        return width + "x" + height;
    }
}
