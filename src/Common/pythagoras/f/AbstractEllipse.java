//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.util.NoSuchElementException;

/**
 * Provides most of the implementation of {@link IEllipse}, obtaining the framing rectangle from
 * the derived class.
 */
public abstract class AbstractEllipse extends RectangularShape implements IEllipse
{
    @Override // from IEllipse
    public Ellipse clone () {
        return new Ellipse(x(), y(), width(), height());
    }

    @Override // from interface IShape
    public boolean contains (float px, float py) {
        if (isEmpty()) return false;
        float a = (px - x()) / width() - 0.5f;
        float b = (py - y()) / height() - 0.5f;
        return a * a + b * b < 0.25f;
    }

    @Override // from interface IShape
    public boolean contains (float rx, float ry, float rw, float rh) {
        if (isEmpty() || rw <= 0f || rh <= 0f) return false;
        float rx1 = rx, ry1 = ry, rx2 = rx + rw, ry2 = ry + rh;
        return contains(rx1, ry1) && contains(rx2, ry1) && contains(rx2, ry2) && contains(rx1, ry2);
    }

    @Override // from interface IShape
    public boolean intersects (float rx, float ry, float rw, float rh) {
        if (isEmpty() || rw <= 0f || rh <= 0f) return false;
        float cx = x() + width() / 2f;
        float cy = y() + height() / 2f;
        float rx1 = rx, ry1 = ry, rx2 = rx + rw, ry2 = ry + rh;
        float nx = cx < rx1 ? rx1 : (cx > rx2 ? rx2 : cx);
        float ny = cy < ry1 ? ry1 : (cy > ry2 ? ry2 : cy);
        return contains(nx, ny);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform at) {
        return new Iterator(this, at);
    }

    /** An iterator over an {@link IEllipse}. */
    protected static class Iterator implements PathIterator
    {
        private final float x, y, width, height;
        private final Transform t;
        private int index;

        Iterator (IEllipse e, Transform t) {
            this.x = e.x();
            this.y = e.y();
            this.width = e.width();
            this.height = e.height();
            this.t = t;
            if (width < 0f || height < 0f) {
                index = 6;
            }
        }

        @Override public int windingRule () {
            return WIND_NON_ZERO;
        }

        @Override public boolean isDone () {
            return index > 5;
        }

        @Override public void next () {
            index++;
        }

        @Override public int currentSegment (float[] coords) {
            if (isDone()) {
                throw new NoSuchElementException("Iterator out of bounds");
            }
            if (index == 5) {
                return SEG_CLOSE;
            }
            int type;
            int count;
            if (index == 0) {
                type = SEG_MOVETO;
                count = 1;
                float[] p = POINTS[3];
                coords[0] = x + p[4] * width;
                coords[1] = y + p[5] * height;
            } else {
                type = SEG_CUBICTO;
                count = 3;
                float[] p = POINTS[index - 1];
                int j = 0;
                for (int i = 0; i < 3; i++) {
                    coords[j] = x + p[j++] * width;
                    coords[j] = y + p[j++] * height;
                }
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, count);
            }
            return type;
        }
    }

    // An ellipse is subdivided into four quarters by x and y axis. Each part is approximated by a
    // cubic Bezier curve. The arc in the first quarter starts in (a, 0) and finishes in (0, b)
    // points. Control points for the cubic curve are (a, 0), (a, m), (n, b) and (0, b) where n and
    // m are calculated based on the requirement that the Bezier curve in point 0.5 should lay on
    // the arc.

    /** The coefficient to calculate control points of Bezier curves. */
    private static final float U = 2f / 3f * (FloatMath.sqrt(2) - 1f);

    /** The points coordinates calculation table. */
    private static final float[][] POINTS = {
        { 1f,       0.5f + U, 0.5f + U, 1f,       0.5f, 1f },
        { 0.5f - U, 1f,       0f,       0.5f + U, 0f,   0.5f },
        { 0f,       0.5f - U, 0.5f - U, 0f,       0.5f, 0f },
        { 0.5f + U, 0f,       1f,       0.5f - U, 1f,   0.5f } };
}
