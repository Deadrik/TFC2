//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Rectangle-related utility methods.
 */
public class Rectangles
{
    /**
     * Intersects the supplied two rectangles, writing the result into {@code dst}.
     */
    public static void intersect (IRectangle src1, IRectangle src2, Rectangle dst) {
        float x1 = Math.max(src1.minX(), src2.minX());
        float y1 = Math.max(src1.minY(), src2.minY());
        float x2 = Math.min(src1.maxX(), src2.maxX());
        float y2 = Math.min(src1.maxY(), src2.maxY());
        dst.setBounds(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Unions the supplied two rectangles, writing the result into {@code dst}.
     */
    public static void union (IRectangle src1, IRectangle src2, Rectangle dst) {
        float x1 = Math.min(src1.minX(), src2.minX());
        float y1 = Math.min(src1.minY(), src2.minY());
        float x2 = Math.max(src1.maxX(), src2.maxX());
        float y2 = Math.max(src1.maxY(), src2.maxY());
        dst.setBounds(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Computes the point inside the bounds of the rectangle that's closest to the given point,
     * writing the result into {@code out}.
     * @return {@code out} for call chaining convenience.
     */
    public static Point closestInteriorPoint (IRectangle r, IPoint p, Point out) {
        out.set(MathUtil.clamp(p.x(), r.minX(), r.maxX()),
                MathUtil.clamp(p.y(), r.minY(), r.maxY()));
        return out;
    }

    /**
     * Computes and returns the point inside the bounds of the rectangle that's closest to the
     * given point.
     */
    public static Point closestInteriorPoint (IRectangle r, IPoint p)  {
        return closestInteriorPoint(r, p, new Point());
    }

    /**
     * Returns the squared Euclidean distance between the given point and the nearest point inside
     * the bounds of the given rectangle. If the supplied point is inside the rectangle, the
     * distance will be zero.
     */
    public static float pointRectDistanceSq (IRectangle r, IPoint p) {
        Point p2 = closestInteriorPoint(r, p);
        return Points.distanceSq(p.x(), p.y(), p2.x, p2.y);
    }

    /**
     * Returns the Euclidean distance between the given point and the nearest point inside the
     * bounds of the given rectangle. If the supplied point is inside the rectangle, the distance
     * will be zero.
     */
    public static float pointRectDistance (IRectangle r, IPoint p) {
        return FloatMath.sqrt(pointRectDistanceSq(r, p));
    }
}
