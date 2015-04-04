//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.util.NoSuchElementException;

import pythagoras.util.Platform;

/**
 * Provides most of the implementation of {@link IRectangle}, obtaining only the location and
 * dimensions from the derived class.
 */
public abstract class AbstractRectangle extends RectangularShape implements IRectangle
{
    @Override // from interface IRectangle
    public Point location () {
        return location(new Point());
    }

    @Override // from interface IRectangle
    public Point location (Point target) {
        return target.set(x(), y());
    }

    @Override // from interface IRectangle
    public Dimension size () {
        return size(new Dimension());
    }

    @Override // from interface IRectangle
    public Dimension size (Dimension target) {
        target.setSize(width(), height());
        return target;
    }

    @Override // from interface IRectangle
    public Rectangle intersection (float rx, float ry, float rw, float rh) {
        float x1 = Math.max(x(), rx);
        float y1 = Math.max(y(), ry);
        float x2 = Math.min(maxX(), rx + rw);
        float y2 = Math.min(maxY(), ry + rh);
        return new Rectangle(x1, y1, x2 - x1, y2 - y1);
    }

    @Override // from interface IRectangle
    public Rectangle intersection (IRectangle r) {
        return intersection(r.x(), r.y(), r.width(), r.height());
    }

    @Override // from interface IRectangle
    public Rectangle union (IRectangle r) {
        Rectangle rect = new Rectangle(this);
        rect.add(r);
        return rect;
    }

    @Override // from interface IRectangle
    public boolean intersectsLine (float x1, float y1, float x2, float y2) {
        return Lines.lineIntersectsRect(x1, y1, x2, y2, x(), y(), width(), height());
    }

    @Override // from interface IRectangle
    public boolean intersectsLine (ILine l) {
        return intersectsLine(l.x1(), l.y1(), l.x2(), l.y2());
    }

    @Override // from interface IRectangle
    public int outcode (float px, float py) {
        int code = 0;

        if (width() <= 0) {
            code |= OUT_LEFT | OUT_RIGHT;
        } else if (px < x()) {
            code |= OUT_LEFT;
        } else if (px > maxX()) {
            code |= OUT_RIGHT;
        }

        if (height() <= 0) {
            code |= OUT_TOP | OUT_BOTTOM;
        } else if (py < y()) {
            code |= OUT_TOP;
        } else if (py > maxY()) {
            code |= OUT_BOTTOM;
        }

        return code;
    }

    @Override // from interface IRectangle
    public int outcode (IPoint p) {
        return outcode(p.x(), p.y());
    }

    @Override // from interface IRectangle
    public Rectangle clone () {
        return new Rectangle(this);
    }

    @Override // from interface IShape
    public boolean contains (float px, float py) {
        if (isEmpty()) return false;

        float x = x(), y = y();
        if (px < x || py < y) return false;

        px -= x;
        py -= y;
        return px <= width() && py <= height();
    }

    @Override // from interface IShape
    public boolean contains (float rx, float ry, float rw, float rh) {
        if (isEmpty()) return false;

        float x1 = x(), y1 = y(), x2 = x1 + width(), y2 = y1 + height();
        return (x1 <= rx) && (rx + rw <= x2) && (y1 <= ry) && (ry + rh <= y2);
    }

    @Override // from interface IShape
    public boolean intersects (float rx, float ry, float rw, float rh) {
        if (isEmpty()) return false;

        float x1 = x(), y1 = y(), x2 = x1 + width(), y2 = y1 + height();
        return (rx + rw > x1) && (rx < x2) && (ry + rh > y1) && (ry < y2);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t) {
        return new Iterator(this, t);
    }

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t, float flatness) {
        return new Iterator(this, t);
    }

    @Override // from Object
    public boolean equals (Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractRectangle) {
            AbstractRectangle r = (AbstractRectangle)obj;
            return r.x() == x() && r.y() == y() &&
                r.width() == width() && r.height() == height();
        }
        return false;
    }

    @Override // from Object
    public int hashCode () {
        return Platform.hashCode(x()) ^ Platform.hashCode(y()) ^
            Platform.hashCode(width()) ^ Platform.hashCode(height());
    }

    @Override // from Object
    public String toString () {
        return Dimensions.dimenToString(width(), height()) +
            Points.pointToString(x(), y());
    }

    /** An iterator over an {@link IRectangle}. */
    protected static class Iterator implements PathIterator
    {
        private float x, y, width, height;
        private Transform t;

        /** The current segment index. */
        private int index;

        Iterator (IRectangle r, Transform at) {
            this.x = r.x();
            this.y = r.y();
            this.width = r.width();
            this.height = r.height();
            this.t = at;
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
            if (index == 0) {
                type = SEG_MOVETO;
                coords[0] = x;
                coords[1] = y;
            } else {
                type = SEG_LINETO;
                switch (index) {
                case 1:
                    coords[0] = x + width;
                    coords[1] = y;
                    break;
                case 2:
                    coords[0] = x + width;
                    coords[1] = y + height;
                    break;
                case 3:
                    coords[0] = x;
                    coords[1] = y + height;
                    break;
                case 4:
                    coords[0] = x;
                    coords[1] = y;
                    break;
                }
            }
            if (t != null) {
                t.transform(coords, 0, coords, 0, 1);
            }
            return type;
        }
    }
}
