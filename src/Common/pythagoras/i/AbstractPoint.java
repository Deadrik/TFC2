//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.i;

/**
 * Provides most of the implementation of {@link IPoint}, obtaining only the location from the
 * derived class.
 */
public abstract class AbstractPoint implements IPoint
{
    @Override // from interface IPoint
    public int distanceSq (int px, int py) {
        return Points.distanceSq(x(), y(), px, py);
    }

    @Override // from interface IPoint
    public int distanceSq (IPoint p) {
        return Points.distanceSq(x(), y(), p.x(), p.y());
    }

    @Override // from interface IPoint
    public int distance (int px, int py) {
        return Points.distance(x(), y(), px, py);
    }

    @Override // from interface IPoint
    public int distance (IPoint p) {
        return Points.distance(x(), y(), p.x(), p.y());
    }

    @Override // from interface IPoint
    public Point clone () {
        return new Point(this);
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractPoint) {
            AbstractPoint p = (AbstractPoint)obj;
            return x() == p.x() && y() == p.y();
        }
        return false;
    }

    @Override
    public int hashCode () {
        return x() ^ y();
    }

    @Override
    public String toString () {
        return Points.pointToString(x(), y());
    }
}
