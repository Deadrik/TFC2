//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * The base class for various {@link IShape} objects whose geometry is defined by a rectangular
 * frame.
 */
public abstract class RectangularShape implements IRectangularShape
{
    /**
     * Sets the location and size of the framing rectangle of this shape to the specified values.
     */
    public abstract void setFrame (double x, double y, double width, double height);

    /**
     * Sets the location and size of the framing rectangle of this shape to the supplied values.
     */
    public void setFrame (IPoint loc, IDimension size) {
        setFrame(loc.x(), loc.y(), size.width(), size.height());
    }

    /**
     * Sets the location and size of the framing rectangle of this shape to be equal to the
     * supplied rectangle.
     */
    public void setFrame (IRectangle r) {
        setFrame(r.x(), r.y(), r.width(), r.height());
    }

    /**
     * Sets the location and size of the framing rectangle of this shape based on the specified
     * diagonal line.
     */
    public void setFrameFromDiagonal (double x1, double y1, double x2, double y2) {
        double rx, ry, rw, rh;
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
        setFrame(rx, ry, rw, rh);
    }

    /**
     * Sets the location and size of the framing rectangle of this shape based on the supplied
     * diagonal line.
     */
    public void setFrameFromDiagonal (IPoint p1, IPoint p2) {
        setFrameFromDiagonal(p1.x(), p1.y(), p2.x(), p2.y());
    }

    /**
     * Sets the location and size of the framing rectangle of this shape based on the specified
     * center and corner points.
     */
    public void setFrameFromCenter (double centerX, double centerY,
                                    double cornerX, double cornerY) {
        double width = Math.abs(cornerX - centerX);
        double height = Math.abs(cornerY - centerY);
        setFrame(centerX - width, centerY - height, width * 2, height * 2);
    }

    /**
     * Sets the location and size of the framing rectangle of this shape based on the supplied
     * center and corner points.
     */
    public void setFrameFromCenter (IPoint center, IPoint corner) {
        setFrameFromCenter(center.x(), center.y(), corner.x(), corner.y());
    }

    @Override // from IRectangularShape
    public Point min ()
    {
        return new Point(minX(), minY());
    }

    @Override // from IRectangularShape
    public double minX () {
        return x();
    }

    @Override // from IRectangularShape
    public double minY () {
        return y();
    }

    @Override // from IRectangularShape
    public Point max ()
    {
        return new Point(maxX(), maxY());
    }

    @Override // from IRectangularShape
    public double maxX () {
        return x() + width();
    }

    @Override // from IRectangularShape
    public double maxY () {
        return y() + height();
    }

    @Override // from IRectangularShape
    public Point center ()
    {
        return new Point(centerX(), centerY());
    }

    @Override // from IRectangularShape
    public double centerX () {
        return x() + width() / 2;
    }

    @Override // from IRectangularShape
    public double centerY () {
        return y() + height() / 2;
    }

    @Override // from IRectangularShape
    public Rectangle frame () {
        return bounds();
    }

    @Override // from IRectangularShape
    public Rectangle frame (Rectangle target) {
        return bounds(target);
    }

    @Override // from interface IShape
    public boolean isEmpty () {
        return width() <= 0 || height() <= 0;
    }

    @Override // from interface IShape
    public boolean contains (IPoint point) {
        return contains(point.x(), point.y());
    }

    @Override // from interface IShape
    public boolean contains (IRectangle rect) {
        return contains(rect.x(), rect.y(), rect.width(), rect.height());
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

    @Override // from interface IShape
    public PathIterator pathIterator (Transform t, double flatness) {
        return new FlatteningPathIterator(pathIterator(t), flatness);
    }
}
