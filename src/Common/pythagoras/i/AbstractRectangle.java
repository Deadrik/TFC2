//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

/**
 * Provides most of the implementation of {@link IRectangle}, obtaining only the location and
 * dimensions from the derived class.
 */
public abstract class AbstractRectangle implements IRectangle
{
    @Override // from IRectangle
    public int minX () {
        return x();
    }

    @Override // from IRectangle
    public int minY () {
        return y();
    }

    @Override // from IRectangle
    public int maxX () {
        return x() + width() - 1;
    }

    @Override // from IRectangle
    public int maxY () {
        return y() + height() - 1;
    }

    @Override // from interface IRectangle
    public Point location () {
        return location(new Point());
    }

    @Override // from interface IRectangle
    public Point location (Point target) {
        target.setLocation(x(), y());
        return target;
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
    public Rectangle intersection (int rx, int ry, int rw, int rh) {
        int x1 = Math.max(x(), rx);
        int y1 = Math.max(y(), ry);
        int x2 = Math.min(maxX(), rx + rw - 1);
        int y2 = Math.min(maxY(), ry + rh - 1);
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
    public int outcode (int px, int py) {
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
    public boolean isEmpty () {
        return width() <= 0 || height() <= 0;
    }

    @Override // from interface IShape
    public boolean contains (int px, int py) {
        if (isEmpty()) return false;

        int x = x(), y = y();
        if (px < x || py < y) return false;

        px -= x;
        py -= y;
        return px < width() && py < height();
    }

    @Override // from interface IShape
    public boolean contains (IPoint point) {
        return contains(point.x(), point.y());
    }

    @Override // from interface IShape
    public boolean contains (int rx, int ry, int rw, int rh) {
        if (isEmpty()) return false;

        int x1 = x(), y1 = y(), x2 = x1 + width(), y2 = y1 + height();
        return (x1 <= rx) && (rx + rw <= x2) && (y1 <= ry) && (ry + rh <= y2);
    }

    @Override // from interface IShape
    public boolean contains (IRectangle rect) {
        return contains(rect.x(), rect.y(), rect.width(), rect.height());
    }

    @Override // from interface IShape
    public boolean intersects (int rx, int ry, int rw, int rh) {
        if (isEmpty()) return false;

        int x1 = x(), y1 = y(), x2 = x1 + width(), y2 = y1 + height();
        return (rx + rw > x1) && (rx < x2) && (ry + rh > y1) && (ry < y2);
    }

    @Override // from interface IShape
    public boolean intersects (IRectangle rect) {
        return intersects(rect.x(), rect.y(), rect.width(), rect.height());
    }

    @Override // from interface IShape
    public Rectangle bounds () {
        return bounds(new Rectangle());
    }

    @Override // from interface IShape
    public Rectangle bounds (Rectangle target) {
        target.setBounds(x(), y(), width(), height());
        return target;
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
        return x() ^ y() ^ width() ^ height();
    }

    @Override // from Object
    public String toString () {
        return Dimensions.dimenToString(width(), height()) +
            Points.pointToString(x(), y());
    }
}
