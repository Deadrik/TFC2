//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.gwt.pythagoras.util;

/**
 * A platform instance that's used when running in GWT. Note that this is copied over top of the
 * JDK implementation in the pythagoras.jar file, so this is never actually compiled to bytecode.
 * Thus be careful when making changes to this file.
 */
public class Platform
{
    /**
     * Returns a hash code for the supplied float value.
     */
    public static int hashCode (float f1) {
        return (int)f1; // alas nothing better can be done in JavaScript
    }

    /**
     * Returns a hash code for the supplied double value.
     */
    public static int hashCode (double d1) {
        return (int)d1; // alas nothing better can be done in JavaScript
    }

    /**
     * Clones the supplied array of bytes.
     */
    public static native byte[] clone (byte[] values) /*-{
        return values.slice(0);
    }-*/;

    /**
     * Clones the supplied array of ints.
     */
    public static native int[] clone (int[] values) /*-{
        return values.slice(0);
    }-*/;

    /**
     * Clones the supplied array of floats.
     */
    public static native float[] clone (float[] values) /*-{
        return values.slice(0);
    }-*/;

    /**
     * Clones the supplied array of doubles.
     */
    public static native double[] clone (double[] values) /*-{
        return values.slice(0);
    }-*/;
}
