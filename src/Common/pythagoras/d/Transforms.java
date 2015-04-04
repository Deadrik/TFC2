//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * {@link Transform} related utility methods.
 */
public class Transforms
{
    /**
     * Creates and returns a new shape that is the supplied shape transformed by this transform's
     * matrix.
     */
    public static IShape createTransformedShape (Transform t, IShape src) {
        if (src == null) {
            return null;
        }
        if (src instanceof Path) {
            return ((Path)src).createTransformedShape(t);
        }
        PathIterator path = src.pathIterator(t);
        Path dst = new Path(path.windingRule());
        dst.append(path, false);
        return dst;
    }

    /**
     * Multiplies the supplied two affine transforms, storing the result in {@code into}. {@code
     * into} may refer to the same instance as {@code a} or {@code b}.
     * @return {@code into} for chaining.
     */
    public static <T extends Transform> T multiply (AffineTransform a, AffineTransform b, T into) {
        return multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty,
                        b.m00, b.m01, b.m10, b.m11, b.tx, b.ty, into);
    }

    /**
     * Multiplies the supplied two affine transforms, storing the result in {@code into}. {@code
     * into} may refer to the same instance as {@code a}.
     * @return {@code into} for chaining.
     */
    public static <T extends Transform> T multiply (
        AffineTransform a, double m00, double m01, double m10, double m11, double tx, double ty, T into) {
        return multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty, m00, m01, m10, m11, tx, ty, into);
    }

    /**
     * Multiplies the supplied two affine transforms, storing the result in {@code into}. {@code
     * into} may refer to the same instance as {@code b}.
     * @return {@code into} for chaining.
     */
    public static <T extends Transform> T multiply (
        double m00, double m01, double m10, double m11, double tx, double ty, AffineTransform b, T into) {
        return multiply(m00, m01, m10, m11, tx, ty, b.m00, b.m01, b.m10, b.m11, b.tx, b.ty, into);
    }

    /**
     * Multiplies the supplied two affine transforms, storing the result in {@code into}.
     * @return {@code into} for chaining.
     */
    public static <T extends Transform> T multiply (
        double am00, double am01, double am10, double am11, double atx, double aty,
        double bm00, double bm01, double bm10, double bm11, double btx, double bty, T into) {
        into.setTransform(am00 * bm00 + am10 * bm01,
                          am01 * bm00 + am11 * bm01,
                          am00 * bm10 + am10 * bm11,
                          am01 * bm10 + am11 * bm11,
                          am00 *  btx + am10 *  bty + atx,
                          am01 *  btx + am11 *  bty + aty);
        return into;
    }
}
