//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import pythagoras.util.Platform;

/**
 * Provides most of the implementation of {@link IVector}, obtaining only x and y from the derived
 * class.
 */
public abstract class AbstractVector implements IVector
{
    @Override // from interface IVector
    public float dot (IVector other) {
        return x()*other.x() + y()*other.y();
    }

    @Override // from interface IVector
    public Vector cross (IVector other) {
        return cross(other, new Vector());
    }

    @Override // from interface IVector
    public Vector cross (IVector other, Vector result) {
        float x = x(), y = y(), ox = other.x(), oy = other.y();
        return result.set(y*ox - x*oy, x*oy - y*ox);
    }

    @Override // from interface IVector
    public Vector negate () {
        return negate(new Vector());
    }

    @Override // from interface IVector
    public Vector negate (Vector result) {
        return result.set(-x(), -y());
    }

    @Override // from interface IVector
    public Vector normalize () {
        return normalize(new Vector());
    }

    @Override // from interface IVector
    public Vector normalize (Vector result) {
        return scale(1f / length(), result);
    }

    @Override // from interface IVector
    public float length () {
        return FloatMath.sqrt(lengthSq());
    }

    @Override // from interface IVector
    public float lengthSq () {
        float x = x(), y = y();
        return (x*x + y*y);
    }

    @Override // from interface IVector
    public boolean isZero () {
        return Vectors.isZero(x(), y());
    }

    @Override // from interface IVector
    public float distance (IVector other) {
        return FloatMath.sqrt(distanceSq(other));
    }

    @Override // from interface IVector
    public float distanceSq (IVector other) {
        float dx = x() - other.x(), dy = y() - other.y();
        return dx*dx + dy*dy;
    }

    @Override // from interface IVector
    public float angle () {
        return FloatMath.atan2(y(), x());
    }

    @Override // from interface IVector
    public float angleBetween (IVector other) {
        float cos = dot(other) / (length() * other.length());
        return cos >= 1f ? 0f : FloatMath.acos(cos);
    }

    @Override // from interface IVector
    public Vector scale (float v) {
        return scale(v, new Vector());
    }

    @Override // from interface IVector
    public Vector scale (float v, Vector result) {
        return result.set(x()*v, y()*v);
    }

    @Override // from interface IVector
    public Vector scale (IVector other) {
        return scale(other, new Vector());
    }

    @Override // from interface IVector
    public Vector scale (IVector other, Vector result) {
        return result.set(x()*other.x(), y()*other.y());
    }

    @Override // from interface IVector
    public Vector add (IVector other) {
        return add(other, new Vector());
    }

    @Override // from interface IVector
    public Vector add (IVector other, Vector result) {
        return add(other.x(), other.y(), result);
    }

    @Override // from interface IVector
    public Vector subtract (IVector other) {
        return subtract(other, new Vector());
    }

    @Override // from interface IVector
    public Vector subtract (IVector other, Vector result) {
        return add(-other.x(), -other.y(), result);
    }

    @Override // from interface IVector
    public Vector add (float x, float y) {
        return add(x, y, new Vector());
    }

    @Override // from interface IVector
    public Vector add (float x, float y, Vector result) {
        return result.set(x() + x, y() + y);
    }

    @Override // from interface IVector
    public Vector subtract (float x, float y) {
        return subtract(x, y, new Vector());
    }

    @Override // from interface IVector
    public Vector subtract (float x, float y, Vector result) {
        return result.set(x() - x, y() - y);
    }

    @Override // from interface IVector
    public Vector addScaled (IVector other, float v) {
        return addScaled(other, v, new Vector());
    }

    @Override // from interface IVector
    public Vector addScaled (IVector other, float v, Vector result) {
        return result.set(x() + other.x()*v, y() + other.y()*v);
    }

    @Override // from interface IVector
    public Vector rotate (float angle) {
        return rotate(angle, new Vector());
    }

    @Override // from interface IVector
    public Vector rotate (float angle, Vector result) {
        float x = x(), y = y();
        float sina = FloatMath.sin(angle), cosa = FloatMath.cos(angle);
        return result.set(x*cosa - y*sina, x*sina + y*cosa);
    }

    @Override // from interface IVector
    public Vector rotateAndAdd (float angle, IVector add, Vector result) {
        float x = x(), y = y();
        float sina = FloatMath.sin(angle), cosa = FloatMath.cos(angle);
        return result.set(x*cosa - y*sina + add.x(), x*sina + y*cosa + add.y());
    }

    @Override // from interface IVector
    public Vector rotateScaleAndAdd (float angle, float scale, IVector add, Vector result) {
        float x = x(), y = y();
        float sina = FloatMath.sin(angle), cosa = FloatMath.cos(angle);
        return result.set((x*cosa - y*sina)*scale + add.x(),
                          (x*sina + y*cosa)*scale + add.y());
    }

    @Override // from interface IVector
    public Vector lerp (IVector other, float t) {
        return lerp(other, t, new Vector());
    }

    @Override // from interface IVector
    public Vector lerp (IVector other, float t, Vector result) {
        float x = x(), y = y();
        float dx = other.x() - x, dy = other.y() - y;
        return result.set(x + t*dx, y + t*dy);
    }

    @Override // from interface IVector
    public Vector clone () {
        return new Vector(this);
    }

    @Override
    public boolean equals (Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof AbstractVector) {
            AbstractVector p = (AbstractVector)obj;
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
        return Vectors.vectorToString(x(), y());
    }
}
