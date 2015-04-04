//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;
import java.nio.FloatBuffer;

import pythagoras.util.Platform;

/**
 * A three element vector.
 */
public class Vector3 implements IVector3, Serializable
{
    /** A unit vector in the X+ direction. */
    public static final IVector3 UNIT_X = new Vector3(1f, 0f, 0f);

    /** A unit vector in the Y+ direction. */
    public static final IVector3 UNIT_Y = new Vector3(0f, 1f, 0f);

    /** A unit vector in the Z+ direction. */
    public static final IVector3 UNIT_Z = new Vector3(0f, 0f, 1f);

    /** A vector containing unity for all components. */
    public static final IVector3 UNIT_XYZ = new Vector3(1f, 1f, 1f);

    /** A normalized version of UNIT_XYZ. */
    public static final IVector3 NORMAL_XYZ = UNIT_XYZ.normalize();

    /** The zero vector. */
    public static final IVector3 ZERO = new Vector3(0f, 0f, 0f);

    /** A vector containing the minimum floating point value for all components
     * (note: the components are -{@link Float#MAX_VALUE}, not {@link Float#MIN_VALUE}). */
    public static final IVector3 MIN_VALUE =
        new Vector3(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);

    /** A vector containing the maximum floating point value for all components. */
    public static final IVector3 MAX_VALUE =
        new Vector3(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);

    /** The components of the vector. */
    public float x, y, z;

    /**
     * Creates a vector from three components.
     */
    public Vector3 (float x, float y, float z) {
        set(x, y, z);
    }

    /**
     * Creates a vector from an array of values.
     */
    public Vector3 (float[] values) {
        set(values);
    }

    /**
     * Copy constructor.
     */
    public Vector3 (IVector3 other) {
        set(other);
    }

    /**
     * Creates a zero vector.
     */
    public Vector3 () {
    }

    /**
     * Computes the cross product of this and the specified other vector, storing the result
     * in this vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 crossLocal (IVector3 other) {
        return cross(other, this);
    }

    /**
     * Negates this vector in-place.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 negateLocal () {
        return negate(this);
    }

    /**
     * Absolute-values this vector in-place.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 absLocal () {
        return abs(this);
    }

    /**
     * Normalizes this vector in-place.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 normalizeLocal () {
        return normalize(this);
    }

    /**
     * Multiplies this vector in-place by a scalar.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 multLocal (float v) {
        return mult(v, this);
    }

    /**
     * Multiplies this vector in-place by another.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 multLocal (IVector3 other) {
        return mult(other, this);
    }

    /**
     * Adds a vector in-place to this one.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 addLocal (IVector3 other) {
        return add(other, this);
    }

    /**
     * Subtracts a vector in-place from this one.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 subtractLocal (IVector3 other) {
        return subtract(other, this);
    }

    /**
     * Adds a vector in-place to this one.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 addLocal (float x, float y, float z) {
        return add(x, y, z, this);
    }

    /**
     * Adds a scaled vector in-place to this one.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 addScaledLocal (IVector3 other, float v) {
        return addScaled(other, v, this);
    }

    /**
     * Linearly interpolates between this and the specified other vector in-place by the supplied
     * amount.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 lerpLocal (IVector3 other, float t) {
        return lerp(other, t, this);
    }
    /**
     * Copies the elements of another vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 set (IVector3 other) {
        return set(other.x(), other.y(), other.z());
    }

    /**
     * Copies the elements of an array.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 set (float[] values) {
        return set(values[0], values[1], values[2]);
    }

    /**
     * Sets all of the elements of the vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector3 set (float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override // from IVector3
    public float x () {
        return x;
    }

    @Override // from IVector3
    public float y () {
        return y;
    }

    @Override // from IVector3
    public float z () {
        return z;
    }

    @Override // from interface IVector3
    public float dot (IVector3 other) {
        return x*other.x() + y*other.y() + z*other.z();
    }

    @Override // from interface IVector3
    public Vector3 cross (IVector3 other) {
        return cross(other, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 cross (IVector3 other, Vector3 result) {
        float x = this.x, y = this.y, z = this.z;
        float ox = other.x(), oy = other.y(), oz = other.z();
        return result.set(y*oz - z*oy, z*ox - x*oz, x*oy - y*ox);
    }

    @Override // from interface IVector3
    public float triple (IVector3 b, IVector3 c) {
        float bx = b.x(), by = b.y(), bz = b.z();
        float cx = c.x(), cy = c.y(), cz = c.z();
        return x()*(by*cz - bz*cy) + y()*(bz*cx - bx*cz) + z()*(bx*cy - by*cx);
    }

    @Override // from interface IVector3
    public Vector3 negate () {
        return negate(new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 negate (Vector3 result) {
        return result.set(-x, -y, -z);
    }

    @Override // from interface IVector3
    public Vector3 abs () {
        return abs(new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 abs (Vector3 result) {
        return result.set(Math.abs(x), Math.abs(y), Math.abs(z));
    }

    @Override // from interface IVector3
    public Vector3 normalize () {
        return normalize(new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 normalize (Vector3 result) {
        return mult(1f / length(), result);
    }

    @Override // from interface IVector3
    public float angle (IVector3 other) {
        return FloatMath.acos(dot(other) / (length() * other.length()));
    }

    @Override // from interface IVector3
    public float length () {
        return FloatMath.sqrt(lengthSquared());
    }

    @Override // from interface IVector3
    public float lengthSquared () {
        float x = this.x, y = this.y, z = this.z;
        return (x*x + y*y + z*z);
    }

    @Override // from interface IVector3
    public float distance (IVector3 other) {
        return FloatMath.sqrt(distanceSquared(other));
    }

    @Override // from interface IVector3
    public float distanceSquared (IVector3 other) {
        float dx = x - other.x(), dy = y - other.y(), dz = z - other.z();
        return dx*dx + dy*dy + dz*dz;
    }

    @Override // from interface IVector3
    public float manhattanDistance (IVector3 other) {
        return Math.abs(x - other.x()) + Math.abs(y - other.y()) + Math.abs(z - other.z());
    }

    @Override // from interface IVector3
    public Vector3 mult (float v) {
        return mult(v, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 mult (float v, Vector3 result) {
        return result.set(x*v, y*v, z*v);
    }

    @Override // from interface IVector3
    public Vector3 mult (IVector3 other) {
        return mult(other, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 mult (IVector3 other, Vector3 result) {
        return result.set(x*other.x(), y*other.y(), z*other.z());
    }

    @Override // from interface IVector3
    public Vector3 add (IVector3 other) {
        return add(other, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 add (IVector3 other, Vector3 result) {
        return add(other.x(), other.y(), other.z(), result);
    }

    @Override // from interface IVector3
    public Vector3 subtract (IVector3 other) {
        return subtract(other, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 subtract (IVector3 other, Vector3 result) {
        return add(-other.x(), -other.y(), -other.z(), result);
    }

    @Override // from interface IVector3
    public Vector3 add (float x, float y, float z) {
        return add(x, y, z, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 add (float x, float y, float z, Vector3 result) {
        return result.set(this.x + x, this.y + y, this.z + z);
    }

    @Override // from interface IVector3
    public Vector3 addScaled (IVector3 other, float v) {
        return addScaled(other, v, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 addScaled (IVector3 other, float v, Vector3 result) {
        return result.set(x + other.x()*v, y + other.y()*v, z + other.z()*v);
    }

    @Override // from interface IVector3
    public Vector3 lerp (IVector3 other, float t) {
        return lerp(other, t, new Vector3());
    }

    @Override // from interface IVector3
    public Vector3 lerp (IVector3 other, float t, Vector3 result) {
        float x = this.x, y = this.y, z = this.z;
        return result.set(x + t*(other.x() - x), y + t*(other.y() - y), z + t*(other.z() - z));
    }

    @Override // from interface IVector3
    public float get (int idx) {
        switch (idx) {
        case 0: return x;
        case 1: return y;
        case 2: return z;
        }
        throw new IndexOutOfBoundsException(String.valueOf(idx));
    }

    @Override // from interface IVector3
    public void get (float[] values) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
    }

    @Override // from interface IVector3
    public FloatBuffer get (FloatBuffer buf) {
        return buf.put(x).put(y).put(z);
    }

    @Override
    public String toString () {
        return "[" + x + ", " + y + ", " + z + "]";
    }

    @Override
    public int hashCode () {
        return Platform.hashCode(x) ^ Platform.hashCode(y) ^ Platform.hashCode(z);
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof Vector3)) {
            return false;
        }
        Vector3 ovec = (Vector3)other;
        return (x == ovec.x && y == ovec.y && z == ovec.z);
    }
}
