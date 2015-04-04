//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.util.NoSuchElementException;

/**
 * Provides most of the implementation of {@link IQuadCurve}, obtaining only the start, end and
 * control point from the derived class.
 */
public abstract class AbstractQuadCurve implements IQuadCurve
{
    @Override // from interface IQuadCurve
    public Point p1 () {
        return new Point(x1(), y1());
    }

    @Override // from interface IQuadCurve
    public Point ctrlP () {
        return new Point(ctrlX(), ctrlY());
    }

    @Override // from interface IQuadCurve
    public Point p2 () {
        return new Point(x2(), y2());
    }

    @Override // from interface IQuadCurve
    public double flatnessSq () {
        return Lines.pointSegDistSq(ctrlX(), ctrlY(), x1(), y1(), x2(), y2());
    }

    @Override // from interface IQuadCurve
    public double flatness () {
        return Lines.pointSegDist(ctrlX(), ctrlY(), x1(), y1(), x2(), y2());
    }

    @Override // from interface IQuadCurve
    public void subdivide (QuadCurve left, QuadCurve right) {
        QuadCurves.subdivide(this, left, right);
    }

    @Override // from interface IQuadCurve
    public QuadCurve clone () {
        return new QuadCurve(x1(), y1(), ctrlX(), ctrlY(), x2(), y2());
    }

    @Override // from interface IShape
    public boolean isEmpty () {
        return true; // curves contain no space
    }

    @Override // from interface IShape
    public boolean contains (double px, double py) {
        return Crossing.isInsideEvenOdd(Crossing.crossShape(this, px, py));
    }

    @Override // from interface IShape
    public boolean contains (double rx, double ry, double rw, double rh) {
        int cross = Crossing.intersectShape(this, rx, ry, rw, rh);
        return cross != Crossing.CROSSING && Crossing.isInsideEvenOdd(cross);
    }

    @Override // from interface IShape
    public boolean contains (IPoint p) {
        return contains(p.x(), p.y());
    }

    @Override // from interface IShape
    public boolean contains (IRectangle r) {
        return contains(r.x(), r.y(), r.width(), r.height());
    }

    @Override // from interface IShape
    public boolean intersects (double rx, double ry, double rw, double rh) {
        int cross = Crossing.intersectShape(this, rx, ry, rw, rh);
        return cross == Crossing.CROSSING || Crossing.isInsideEvenOdd(cross);
    }

    @Override // from interface IShape
    public boolean intersects (IRectangle r) {
        return intersects(r.x(), r.y(), r.width(), r.height());
    }

    @Override // from interface IShape
    public Rectangle bounds () {
        return bounds(new Rectangle());
    }

    @Override // from interface IShape
    public Rectangle bounds (Rectangle target) {
        double x1 = x1(), y1 = y1(), x2 = x2(), y2 = y2();
        double ctrlx = ctrlX(), ctrly = ctrlY();
        double rx0 = Math.min(Math.min(x1, x2), ctrlx);
        double ry0 = Math.min(Math.min(y1, y2), ctrly);
        double rx1 = Math.max(Math.max(x1, x2), ctrlx);
        double ry1 = Math.max(Math.max(y1, y2), ctrly);
        target.setBounds(rx0, ry0, rx1 - rx0, ry1 - ry0);
        return target;
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t) {
        return new Iterator(this, t);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t, double flatness) {
        return new FlatteningPathIterator(pathIterator(t), flatness);
    }

    /** An iterator over an {@link IQuadCurve}. */
    protected static class Iterator implements PathIterator
    {
        private IQuadCurve c;
        private Transform t;
        private int index;

        Iterator (IQuadCurve q, Transform t) {
            this.c = q;
            this.t = t;
        }

        @Override public int windingRule () {
            return WIND_NON_ZERO;
        }

        @Override public boolean isDone () {
            return (index > 1);
        }

        @Override public void next () {
            index++;
        }

        @Override public int currentSegment (double[] coords) {
            if (isDone()) {
                throw new NoSuchElementException("Iterator out of bounds");
            }
            int type;
            int count;
            if (index == 0) {
                type = SEG_MOVETO;
                coords[0] = c.x1();
                coords[1] = c.y1();
                count = 1;
            } else {
                type = SEG_QUADTO;
                coords[0] = c.ctrlX();
                coords[1] = c.ctrlY();
                coords[2] = c.x2();
                coords[3] = c.y2();
                count = 2;
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, count);
            }
            return type;
        }
    }
}
