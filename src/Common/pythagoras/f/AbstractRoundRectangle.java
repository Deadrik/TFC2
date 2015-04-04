//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.util.NoSuchElementException;

/**
 * Provides most of the implementation of {@link IRoundRectangle}, obtaining the framing rectangle
 * from the derived class.
 */
public abstract class AbstractRoundRectangle extends RectangularShape implements IRoundRectangle
{
    @Override // from interface IRoundRectangle
    public RoundRectangle clone () {
        return new RoundRectangle(x(), y(), width(), height(),
                                  arcWidth(), arcHeight());
    }

    @Override // from interface IShape
    public boolean contains (float px, float py) {
        if (isEmpty()) return false;

        float rx1 = x(), ry1 = y();
        float rx2 = rx1 + width(), ry2 = ry1 + height();
        if (px < rx1 || px >= rx2 || py < ry1 || py >= ry2) {
            return false;
        }

        float aw = arcWidth() / 2f, ah = arcHeight() / 2f;
        float cx, cy;
        if (px < rx1 + aw) {
            cx = rx1 + aw;
        } else if (px > rx2 - aw) {
            cx = rx2 - aw;
        } else {
            return true;
        }

        if (py < ry1 + ah) {
            cy = ry1 + ah;
        } else if (py > ry2 - ah) {
            cy = ry2 - ah;
        } else {
            return true;
        }

        px = (px - cx) / aw;
        py = (py - cy) / ah;
        return px * px + py * py <= 1f;
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

        float x1 = x(), y1 = y(), x2 = x1 + width(), y2 = y1 + height();
        float rx1 = rx, ry1 = ry, rx2 = rx + rw, ry2 = ry + rh;
        if (rx2 < x1 || x2 < rx1 || ry2 < y1 || y2 < ry1) {
            return false;
        }

        float cx = (x1 + x2) / 2f, cy = (y1 + y2) / 2f;
        float nx = cx < rx1 ? rx1 : (cx > rx2 ? rx2 : cx);
        float ny = cy < ry1 ? ry1 : (cy > ry2 ? ry2 : cy);
        return contains(nx, ny);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform at) {
        return new Iterator(this, at);
    }

    /** Provides an iterator over an {@link IRoundRectangle}. */
    protected static class Iterator implements PathIterator
    {
        private final float x, y, width, height, aw, ah;
        private final Transform t;
        private int index;

        Iterator (IRoundRectangle rr, Transform at) {
            this.x = rr.x();
            this.y = rr.y();
            this.width = rr.width();
            this.height = rr.height();
            this.aw = Math.min(width, rr.arcWidth());
            this.ah = Math.min(height, rr.arcHeight());
            this.t = at;
            if (width < 0f || height < 0f || aw < 0f || ah < 0f) {
                index = POINTS.length;
            }
        }

        @Override public int windingRule () {
            return WIND_NON_ZERO;
        }

        @Override public boolean isDone () {
            return index > POINTS.length;
        }

        @Override public void next () {
            index++;
        }

        @Override public int currentSegment (float[] coords) {
            if (isDone()) {
                throw new NoSuchElementException("Iterator out of bounds");
            }
            if (index == POINTS.length) {
                return SEG_CLOSE;
            }
            int j = 0;
            float[] p = POINTS[index];
            for (int i = 0; i < p.length; i += 4) {
                coords[j++] = x + p[i + 0] * width + p[i + 1] * aw;
                coords[j++] = y + p[i + 2] * height + p[i + 3] * ah;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, j / 2);
            }
            return TYPES[index];
        }
    }

    // the path for round corners is generated the same way as for Ellipse

    /** The segment types correspond to points array. */
    protected static final int[] TYPES = {
        PathIterator.SEG_MOVETO, PathIterator.SEG_LINETO, PathIterator.SEG_CUBICTO,
        PathIterator.SEG_LINETO, PathIterator.SEG_CUBICTO, PathIterator.SEG_LINETO,
        PathIterator.SEG_CUBICTO, PathIterator.SEG_LINETO, PathIterator.SEG_CUBICTO
    };

    /** The coefficient to calculate control points of Bezier curves. */
    protected static final float U = 0.5f - 2f / 3f * (FloatMath.sqrt(2f) - 1f);

    /** The points coordinates calculation table. */
    protected static final float[][] POINTS = {
        { 0f, 0.5f, 0f, 0f }, // MOVETO
        { 1f, -0.5f, 0f, 0f }, // LINETO
        { 1f, -U, 0f, 0f, 1f, 0f, 0f, U, 1f, 0f, 0f, 0.5f }, // CUBICTO
        { 1f, 0f, 1f, -0.5f }, // LINETO
        { 1f, 0f, 1f, -U, 1f, -U, 1f, 0f, 1f, -0.5f, 1f, 0f }, // CUBICTO
        { 0f, 0.5f, 1f, 0f }, // LINETO
        { 0f, U, 1f, 0f, 0f, 0f, 1f, -U, 0f, 0f, 1f, -0.5f }, // CUBICTO
        { 0f, 0f, 0f, 0.5f }, // LINETO
        { 0f, 0f, 0f, U, 0f, U, 0f, 0f, 0f, 0.5f, 0f, 0f }, // CUBICTO
    };
}
