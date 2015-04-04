//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;

/**
 * Represents a quadratic curve.
 */
public class QuadCurve extends AbstractQuadCurve implements Serializable
{
    /** The x-coordinate of the start of this curve. */
    public float x1;

    /** The y-coordinate of the start of this curve. */
    public float y1;

    /** The x-coordinate of the control point. */
    public float ctrlx;

    /** The y-coordinate of the control point. */
    public float ctrly;

    /** The x-coordinate of the end of this curve. */
    public float x2;

    /** The y-coordinate of the end of this curve. */
    public float y2;

    /**
     * Creates a quad curve with all points at (0,0).
     */
    public QuadCurve () {
    }

    /**
     * Creates a quad curve with the specified start, control, and end points.
     */
    public QuadCurve (float x1, float y1, float ctrlx, float ctrly, float x2, float y2) {
        setCurve(x1, y1, ctrlx, ctrly, x2, y2);
    }

    /**
     * Configures the start, control, and end points for this curve.
     */
    public void setCurve (float x1, float y1, float ctrlx, float ctrly, float x2, float y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.ctrlx = ctrlx;
        this.ctrly = ctrly;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Configures the start, control, and end points for this curve.
     */
    public void setCurve (IPoint p1, IPoint cp, IPoint p2) {
        setCurve(p1.x(), p1.y(), cp.x(), cp.y(), p2.x(), p2.y());
    }

    /**
     * Configures the start, control, and end points for this curve, using the values at the
     * specified offset in the {@code coords} array.
     */
    public void setCurve (float[] coords, int offset) {
        setCurve(coords[offset + 0], coords[offset + 1],
                 coords[offset + 2], coords[offset + 3],
                 coords[offset + 4], coords[offset + 5]);
    }

    /**
     * Configures the start, control, and end points for this curve, using the values at the
     * specified offset in the {@code points} array.
     */
    public void setCurve (IPoint[] points, int offset) {
        setCurve(points[offset + 0].x(), points[offset + 0].y(),
                 points[offset + 1].x(), points[offset + 1].y(),
                 points[offset + 2].x(), points[offset + 2].y());
    }

    /**
     * Configures the start, control, and end points for this curve to be the same as the supplied
     * curve.
     */
    public void setCurve (IQuadCurve curve) {
        setCurve(curve.x1(), curve.y1(), curve.ctrlX(), curve.ctrlY(),
                 curve.x2(), curve.y2());
    }

    @Override // from interface IQuadCurve
    public float x1 () {
        return x1;
    }

    @Override // from interface IQuadCurve
    public float y1 () {
        return y1;
    }

    @Override // from interface IQuadCurve
    public float ctrlX () {
        return ctrlx;
    }

    @Override // from interface IQuadCurve
    public float ctrlY () {
        return ctrly;
    }

    @Override // from interface IQuadCurve
    public float x2 () {
        return x2;
    }

    @Override // from interface IQuadCurve
    public float y2 () {
        return y2;
    }
}
