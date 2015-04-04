//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import pythagoras.util.Platform;

/**
 * Provides most of the implementation of {@link ICircle}, obtaining only the location and radius
 * from the derived class.
 */
public abstract class AbstractCircle implements ICircle
{
    @Override // from ICircle
    public boolean intersects (ICircle c) {
        float maxDist = radius() + c.radius();
        return Points.distanceSq(x(), y(), c.x(), c.y()) < (maxDist * maxDist);
    }

    @Override // from ICircle
    public boolean contains (IPoint p) {
        float r = radius();
        return Points.distanceSq(x(), y(), p.x(), p.y()) < r * r;
    }

    @Override // from ICircle
    public boolean contains (float x, float y) {
        float r = radius();
        return Points.distanceSq(x(), y(), x, y) < r * r;
    }

    @Override // from ICircle
    public Circle offset (float x, float y) {
        return new Circle(x() + x, y() + y, radius());
    }

    @Override // from ICircle
    public Circle offset (float x, float y, Circle result) {
        result.set(x() + x, y() + y, radius());
        return result;
    }

    @Override // from ICircle
    public Circle clone () {
        return new Circle(this);
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractCircle) {
            AbstractCircle c = (AbstractCircle)obj;
            return x() == c.x() && y() == c.y() && radius() == c.radius();
        }
        return false;
    }

    @Override
    public int hashCode () {
        return Platform.hashCode(x()) ^ Platform.hashCode(y()) ^ Platform.hashCode(radius());
    }
}
