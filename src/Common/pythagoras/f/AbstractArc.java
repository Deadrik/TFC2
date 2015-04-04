//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.util.NoSuchElementException;

/**
 * Provides most of the implementation of {@link IArc}, obtaining only the frame and other metrics
 * from the derived class.
 */
public abstract class AbstractArc extends RectangularShape implements IArc
{
    @Override // from interface IArc
    public Point startPoint () {
        return startPoint(new Point());
    }

    @Override // from interface IArc
    public Point startPoint (Point target) {
        float a = FloatMath.toRadians(angleStart());
        return target.set(x() + (1f + FloatMath.cos(a)) * width() / 2f,
                          y() + (1f - FloatMath.sin(a)) * height() / 2f);
    }

    @Override // from interface IArc
    public Point endPoint () {
        return endPoint(new Point());
    }

    @Override // from interface IArc
    public Point endPoint (Point target) {
        float a = FloatMath.toRadians(angleStart() + angleExtent());
        return target.set(x() + (1f + FloatMath.cos(a)) * width() / 2f,
                          y() + (1f - FloatMath.sin(a)) * height() / 2f);
    }

    @Override // from interface IArc
    public boolean containsAngle (float angle) {
        float extent = angleExtent();
        if (extent >= 360f) {
            return true;
        }
        angle = normAngle(angle);
        float a1 = normAngle(angleStart());
        float a2 = a1 + extent;
        if (a2 > 360f) {
            return angle >= a1 || angle <= a2 - 360f;
        }
        if (a2 < 0f) {
            return angle >= a2 + 360f || angle <= a1;
        }
        return (extent > 0f) ? a1 <= angle && angle <= a2 : a2 <= angle && angle <= a1;
    }

    @Override // from interface IArc
    public Arc clone () {
        return new Arc(x(), y(), width(), height(), angleStart(), angleExtent(),
                       arcType());
    }

    @Override // from RectangularShape
    public boolean isEmpty () {
        return arcType() == OPEN || super.isEmpty();
    }

    @Override // from RectangularShape
    public boolean contains (float px, float py) {
        // normalize point
        float nx = (px - x()) / width() - 0.5f;
        float ny = (py - y()) / height() - 0.5f;
        if ((nx * nx + ny * ny) > 0.25) {
            return false;
        }

        float extent = angleExtent();
        float absExtent = Math.abs(extent);
        if (absExtent >= 360f) {
            return true;
        }

        boolean containsAngle = containsAngle(FloatMath.toDegrees(-FloatMath.atan2(ny, nx)));
        if (arcType() == PIE) {
            return containsAngle;
        }
        if (absExtent <= 180f && !containsAngle) {
            return false;
        }

        Line l = new Line(startPoint(), endPoint());
        int ccw1 = l.relativeCCW(px, py);
        int ccw2 = l.relativeCCW(centerX(), centerY());
        return ccw1 == 0 || ccw2 == 0 || ((ccw1 + ccw2) == 0 ^ absExtent > 180f);
    }

    @Override // from RectangularShape
    public boolean contains (float rx, float ry, float rw, float rh) {
        if (!(contains(rx, ry) && contains(rx + rw, ry) &&
              contains(rx + rw, ry + rh) && contains(rx, ry + rh))) {
            return false;
        }

        float absExtent = Math.abs(angleExtent());
        if (arcType() != PIE || absExtent <= 180f || absExtent >= 360f) {
            return true;
        }

        Rectangle r = new Rectangle(rx, ry, rw, rh);
        float cx = centerX(), cy = centerY();
        if (r.contains(cx, cy)) {
            return false;
        }

        Point p1 = startPoint(), p2 = endPoint();
        return !r.intersectsLine(cx, cy, p1.x(), p1.y()) &&
            !r.intersectsLine(cx, cy, p2.x(), p2.y());
    }

    @Override // from RectangularShape
    public boolean intersects (float rx, float ry, float rw, float rh) {
        if (isEmpty() || rw <= 0f || rh <= 0f) {
            return false;
        }

        // check: does arc contain rectangle's points
        if (contains(rx, ry) || contains(rx + rw, ry) ||
            contains(rx, ry + rh) || contains(rx + rw, ry + rh)) {
            return true;
        }

        float cx = centerX(), cy = centerY();
        Point p1 = startPoint(), p2 = endPoint();

        // check: does rectangle contain arc's points
        Rectangle r = new Rectangle(rx, ry, rw, rh);
        if (r.contains(p1) || r.contains(p2) || (arcType() == PIE && r.contains(cx, cy))) {
            return true;
        }

        if (arcType() == PIE) {
            if (r.intersectsLine(p1.x(), p1.y(), cx, cy) ||
                r.intersectsLine(p2.x(), p2.y(), cx, cy)) {
                return true;
            }
        } else {
            if (r.intersectsLine(p1.x(), p1.y(), p2.x(), p2.y())) {
                return true;
            }
        }

        // nearest rectangle point
        float nx = cx < rx ? rx : (cx > rx + rw ? rx + rw : cx);
        float ny = cy < ry ? ry : (cy > ry + rh ? ry + rh : cy);
        return contains(nx, ny);
    }

    @Override // from RectangularShape
    public Rectangle bounds (Rectangle target) {
        if (isEmpty()) {
            target.setBounds(x(), y(), width(), height());
            return target;
        }

        float rx1 = x();
        float ry1 = y();
        float rx2 = rx1 + width();
        float ry2 = ry1 + height();

        Point p1 = startPoint(), p2 = endPoint();

        float bx1 = containsAngle(180f) ? rx1 : Math.min(p1.x(), p2.x());
        float by1 = containsAngle(90f) ? ry1 : Math.min(p1.y(), p2.y());
        float bx2 = containsAngle(0f) ? rx2 : Math.max(p1.x(), p2.x());
        float by2 = containsAngle(270f) ? ry2 : Math.max(p1.y(), p2.y());

        if (arcType() == PIE) {
            float cx = centerX();
            float cy = centerY();
            bx1 = Math.min(bx1, cx);
            by1 = Math.min(by1, cy);
            bx2 = Math.max(bx2, cx);
            by2 = Math.max(by2, cy);
        }
        target.setBounds(bx1, by1, bx2 - bx1, by2 - by1);
        return target;
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform at) {
        return new Iterator(this, at);
    }

    /** Returns a normalized angle (bound between 0 and 360 degrees). */
    protected float normAngle (float angle) {
        return angle - FloatMath.floor(angle / 360f) * 360f;
    }

    /** An iterator over an {@link IArc}. */
    protected static class Iterator implements PathIterator
    {
        /** The x coordinate of left-upper corner of the arc rectangle bounds */
        private float x;

        /** The y coordinate of left-upper corner of the arc rectangle bounds */
        private float y;

        /** The width of the arc rectangle bounds */
        private float width;

        /** The height of the arc rectangle bounds */
        private float height;

        /** The start angle of the arc in degrees */
        private float angle;

        /** The angle extent in degrees */
        private float extent;

        /** The closure type of the arc */
        private int type;

        /** The path iterator transformation */
        private Transform t;

        /** The current segment index */
        private int index;

        /** The number of arc segments the source arc subdivided to be approximated by Bezier
         * curves. Depends on extent value. */
        private int arcCount;

        /** The number of line segments. Depends on closure type. */
        private int lineCount;

        /** The step to calculate next arc subdivision point */
        private float step;

        /** The temporary value of cosinus of the current angle */
        private float cos;

        /** The temporary value of sinus of the current angle */
        private float sin;

        /** The coefficient to calculate control points of Bezier curves */
        private float k;

        /** The temporary value of x coordinate of the Bezier curve control vector */
        private float kx;

        /** The temporary value of y coordinate of the Bezier curve control vector */
        private float ky;

        /** The x coordinate of the first path point (MOVE_TO) */
        private float mx;

        /** The y coordinate of the first path point (MOVE_TO) */
        private float my;

        Iterator (IArc a, Transform t) {
            this.width = a.width() / 2f;
            this.height = a.height() / 2f;
            this.x = a.x() + width;
            this.y = a.y() + height;
            this.angle = -FloatMath.toRadians(a.angleStart());
            this.extent = -a.angleExtent();
            this.type = a.arcType();
            this.t = t;

            if (width < 0 || height < 0) {
                arcCount = 0;
                lineCount = 0;
                index = 1;
                return;
            }

            if (Math.abs(extent) >= 360f) {
                arcCount = 4;
                k = 4f / 3f * (FloatMath.sqrt(2f) - 1f);
                step = FloatMath.PI / 2f;
                if (extent < 0f) {
                    step = -step;
                    k = -k;
                }
            } else {
                arcCount = (int)Math.rint(Math.abs(extent) / 90f);
                step = FloatMath.toRadians(extent / arcCount);
                k = 4f / 3f * (1f - FloatMath.cos(step / 2f)) / FloatMath.sin(step / 2f);
            }

            lineCount = 0;
            if (type == CHORD) {
                lineCount++;
            } else if (type == PIE) {
                lineCount += 2;
            }
        }

        @Override public int windingRule () {
            return WIND_NON_ZERO;
        }

        @Override public boolean isDone () {
            return index > arcCount + lineCount;
        }

        @Override public void next () {
            index++;
        }

        @Override public int currentSegment (float[] coords) {
            if (isDone()) {
                throw new NoSuchElementException("Iterator out of bounds");
            }
            int type;
            int count;
            if (index == 0) {
                type = SEG_MOVETO;
                count = 1;
                cos = FloatMath.cos(angle);
                sin = FloatMath.sin(angle);
                kx = k * width * sin;
                ky = k * height * cos;
                coords[0] = mx = x + cos * width;
                coords[1] = my = y + sin * height;
            } else if (index <= arcCount) {
                type = SEG_CUBICTO;
                count = 3;
                coords[0] = mx - kx;
                coords[1] = my + ky;
                angle += step;
                cos = FloatMath.cos(angle);
                sin = FloatMath.sin(angle);
                kx = k * width * sin;
                ky = k * height * cos;
                coords[4] = mx = x + cos * width;
                coords[5] = my = y + sin * height;
                coords[2] = mx + kx;
                coords[3] = my - ky;
            } else if (index == arcCount + lineCount) {
                type = SEG_CLOSE;
                count = 0;
            } else {
                type = SEG_LINETO;
                count = 1;
                coords[0] = x;
                coords[1] = y;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, count);
            }
            return type;
        }
    }
}
