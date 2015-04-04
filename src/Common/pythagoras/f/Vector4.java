//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;
import java.nio.FloatBuffer;

import pythagoras.util.Platform;

/**
 * A four element vector.
 */
public class Vector4 implements IVector4, Serializable
{
    /** The components of the vector. */
    public float x, y, z, w;

    /**
     * Creates a vector from four components.
     */
    public Vector4 (float x, float y, float z, float w)
    {
        set(x, y, z, w);
    }

    /**
     * Creates a vector from four components.
     */
    public Vector4 (float[] values) {
        set(values);
    }

    /**
     * Creates a vector from a float buffer.
     */
    public Vector4 (FloatBuffer buf) {
        set(buf);
    }

    /**
     * Copy constructor.
     */
    public Vector4 (IVector4 other) {
        set(other);
    }

    /**
     * Creates a zero vector.
     */
    public Vector4 () {
    }

    /**
     * Copies the elements of another vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 set (IVector4 other) {
        return set(other.x(), other.y(), other.z(), other.w());
    }

    /**
     * Sets all of the elements of the vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 set (float[] values) {
        return set(values[0], values[1], values[2], values[3]);
    }

    /**
     * Sets all of the elements of the vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 set (FloatBuffer buf) {
        return set(buf.get(), buf.get(), buf.get(), buf.get());
    }

    /**
     * Sets all of the elements of the vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 set (float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Negates this vector in-place.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 negateLocal () {
        return negate(this);
    }

    /**
     * Absolute-values this vector in-place.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 absLocal () {
        return abs(this);
    }

    /**
     * Multiplies this vector by a scalar and stores the result back in this vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 multLocal (float v) {
        return mult(v, this);
    }

    /**
     * Multiplies this vector by a matrix (V * M) and stores the result back in this vector.
     *
     * @return a reference to this vector, for chaining.
     */
    public Vector4 multLocal (IMatrix4 matrix) {
        return mult(matrix, this);
    }

    @Override // from IVector4
    public float x () {
        return x;
    }

    @Override // from IVector4
    public float y () {
        return y;
    }

    @Override // from IVector4
    public float z () {
        return z;
    }

    @Override // from IVector4
    public float w () {
        return w;
    }

    @Override // from IVector4
    public FloatBuffer get (FloatBuffer buf) {
        return buf.put(x).put(y).put(z).put(w);
    }

    @Override // from IVector4
    public boolean epsilonEquals (IVector4 other, float epsilon) {
        return (Math.abs(x - other.x()) < epsilon &&
                Math.abs(y - other.y()) < epsilon &&
                Math.abs(z - other.z()) < epsilon &&
                Math.abs(w - other.w()) < epsilon);
    }

    @Override // from interface IVector4
    public Vector4 negate () {
        return negate(new Vector4());
    }

    @Override // from interface IVector4
    public Vector4 negate (Vector4 result) {
        return result.set(-x, -y, -z, -w);
    }

    @Override // from interface IVector4
    public Vector4 abs () {
        return abs(new Vector4());
    }

    @Override // from interface IVector4
    public Vector4 abs (Vector4 result) {
        return result.set(Math.abs(x), Math.abs(y), Math.abs(z), Math.abs(w));
    }

    @Override // from interface IVector4
    public Vector4 mult (float v) {
        return mult(v, new Vector4());
    }

    @Override // from interface IVector4
    public Vector4 mult (float v, Vector4 result) {
        return result.set(x*v, y*v, z*v, w*v);
    }

    @Override // from IVector4
    public Vector4 mult (IMatrix4 matrix) {
        return mult(matrix, new Vector4());
    }

    @Override // from IVector4
    public Vector4 mult (IMatrix4 matrix, Vector4 result) {
        float m00 = matrix.m00(), m10 = matrix.m10(), m20 = matrix.m20(), m30 = matrix.m30();
        float m01 = matrix.m01(), m11 = matrix.m11(), m21 = matrix.m21(), m31 = matrix.m31();
        float m02 = matrix.m02(), m12 = matrix.m12(), m22 = matrix.m22(), m32 = matrix.m32();
        float m03 = matrix.m03(), m13 = matrix.m13(), m23 = matrix.m23(), m33 = matrix.m33();
        float vx = x, vy = y, vz = z, vw = w;
        return result.set(m00*vx + m01*vy + m02*vz + m03*vw,
                          m10*vx + m11*vy + m12*vz + m13*vw,
                          m20*vx + m21*vy + m22*vz + m23*vw,
                          m30*vx + m31*vy + m32*vz + m33*vw);
    }

    @Override
    public String toString () {
        return "[" + x + ", " + y + ", " + z + ", " + w + "]";
    }

    @Override
    public int hashCode () {
        return Platform.hashCode(x) ^ Platform.hashCode(y) ^ Platform.hashCode(z) ^
            Platform.hashCode(w);
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof Vector4)) {
            return false;
        }
        Vector4 ovec = (Vector4)other;
        return (x == ovec.x && y == ovec.y && z == ovec.z && w == ovec.w);
    }
}
