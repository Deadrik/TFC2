//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import pythagoras.util.Platform;

/**
 * Provides most of the implementation of {@link IPoint}, obtaining only the location from the
 * derived class.
 */
public abstract class AbstractPoint implements IPoint
{
    @Override // from IPoint
    public float distanceSq (float px, float py) {
        return Points.distanceSq(x(), y(), px, py);
    }

    @Override // from IPoint
    public float distanceSq (IPoint p) {
        return Points.distanceSq(x(), y(), p.x(), p.y());
    }

    @Override // from IPoint
    public float distance (float px, float py) {
        return Points.distance(x(), y(), px, py);
    }

    @Override // from IPoint
    public float distance (IPoint p) {
        return Points.distance(x(), y(), p.x(), p.y());
    }

    @Override // from interface IPoint
    public float direction (IPoint other) {
        return FloatMath.atan2(other.y() - y(), other.x() - x());
    }

    @Override // from IPoint
    public Point mult (float s) {
        return mult(s, new Point());
    }

    @Override // from IPoint
    public Point mult (float s, Point result) {
        return result.set(x() * s, y() * s);
    }

    @Override // from IPoint
    public Point add (float x, float y) {
        return new Point(x() + x, y() + y);
    }

    @Override // from IPoint
    public Point add (float x, float y, Point result) {
        return result.set(x() + x, y() + y);
    }

    @Override // from IPoint
    public Point subtract (float x, float y) {
        return subtract(x, y, new Point());
    }

    @Override
    public Point subtract (float x, float y, Point result) {
        return result.set(x() - x, y() - y);
    }

    @Override
    public Point subtract (IPoint other, Point result) {
        return subtract(other.x(), other.y(), result);
    }

    @Override // from IPoint
    public Point rotate (float angle) {
        return rotate(angle, new Point());
    }

    @Override // from IPoint
    public Point rotate (float angle, Point result) {
        float x = x(), y = y();
        float sina = FloatMath.sin(angle), cosa = FloatMath.cos(angle);
        return result.set(x*cosa - y*sina, x*sina + y*cosa);
    }

    @Override // from IPoint
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
        return Platform.hashCode(x()) ^ Platform.hashCode(y());
    }

    @Override
    public String toString () {
        return Points.pointToString(x(), y());
    }
}
