//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.util.NoSuchElementException;

/**
 * Provides most of the implementation of {@link ILine}, obtaining only the start and end points
 * from the derived class.
 */
public abstract class AbstractLine implements ILine
{
    @Override // from interface ILine
    public Point p1 () {
        return p1(new Point());
    }

    @Override // from interface ILine
    public Point p1 (Point target) {
        return target.set(x1(), y1());
    }

    @Override // from interface ILine
    public Point p2 () {
        return p2(new Point());
    }

    @Override // from interface ILine
    public Point p2 (Point target) {
        return target.set(x2(), y2());
    }

    @Override // from interface ILine
    public float pointLineDistSq (float px, float py) {
        return Lines.pointLineDistSq(px, py, x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public float pointLineDistSq (IPoint p) {
        return Lines.pointLineDistSq(p.x(), p.y(), x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public float pointLineDist (float px, float py) {
        return Lines.pointLineDist(px, py, x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public float pointLineDist (IPoint p) {
        return Lines.pointLineDist(p.x(), p.y(), x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public float pointSegDistSq (float px, float py) {
        return Lines.pointSegDistSq(px, py, x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public float pointSegDistSq (IPoint p) {
        return Lines.pointSegDistSq(p.x(), p.y(), x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public float pointSegDist (float px, float py) {
        return Lines.pointSegDist(px, py, x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public float pointSegDist (IPoint p) {
        return Lines.pointSegDist(p.x(), p.y(), x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public int relativeCCW (float px, float py) {
        return Lines.relativeCCW(px, py, x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public int relativeCCW (IPoint p) {
        return Lines.relativeCCW(p.x(), p.y(), x1(), y1(), x2(), y2());
    }

    @Override // from interface ILine
    public Line clone () {
        return new Line(x1(), y1(), x2(), y2());
    }

    @Override // from interface IShape
    public boolean isEmpty () {
        return false;
    }

    @Override // from interface IShape
    public boolean contains (float x, float y) {
        return false;
    }

    @Override // from interface IShape
    public boolean contains (IPoint point) {
        return false;
    }

    @Override // from interface IShape
    public boolean contains (float x, float y, float w, float h) {
        return false;
    }

    @Override // from interface IShape
    public boolean contains (IRectangle r) {
        return false;
    }

    @Override // from interface IShape
    public boolean intersects (float rx, float ry, float rw, float rh) {
        return Lines.lineIntersectsRect(x1(), y1(), x2(), y2(), rx, ry, rw, rh);
    }

    @Override // from interface IShape
    public boolean intersects (IRectangle r) {
        return r.intersectsLine(this);
    }

    @Override // from interface IShape
    public Rectangle bounds () {
        return bounds(new Rectangle());
    }

    @Override // from interface IShape
    public Rectangle bounds (Rectangle target) {
        float x1 = x1(), x2 = x2(), y1 = y1(), y2 = y2();
        float rx, ry, rw, rh;
        if (x1 < x2) {
            rx = x1;
            rw = x2 - x1;
        } else {
            rx = x2;
            rw = x1 - x2;
        }
        if (y1 < y2) {
            ry = y1;
            rh = y2 - y1;
        } else {
            ry = y2;
            rh = y1 - y2;
        }
        target.setBounds(rx, ry, rw, rh);
        return target;
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform at) {
        return new Iterator(this, at);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform at, float flatness) {
        return new Iterator(this, at);
    }

    /** An iterator over an {@link ILine}. */
    protected static class Iterator implements PathIterator
    {
        private float x1, y1, x2, y2;
        private Transform t;
        private int index;

        Iterator (ILine l, Transform at) {
            this.x1 = l.x1();
            this.y1 = l.y1();
            this.x2 = l.x2();
            this.y2 = l.y2();
            this.t = at;
        }

        @Override public int windingRule () {
            return WIND_NON_ZERO;
        }

        @Override public boolean isDone () {
            return index > 1;
        }

        @Override public void next () {
            index++;
        }

        @Override public int currentSegment (float[] coords) {
            if (isDone()) {
                throw new NoSuchElementException("Iterator out of bounds");
            }
            int type;
            if (index == 0) {
                type = SEG_MOVETO;
                coords[0] = x1;
                coords[1] = y1;
            } else {
                type = SEG_LINETO;
                coords[0] = x2;
                coords[1] = y2;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, 1);
            }
            return type;
        }
    }
}
