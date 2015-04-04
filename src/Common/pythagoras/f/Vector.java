//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Represents a vector in a plane.
 */
public class Vector extends AbstractVector
{
    /** The x-component of the vector. */
    public float x;

    /** The y-component of the vector. */
    public float y;

    /** Creates a vector with the specified x and y components. */
    public Vector (float x, float y) {
        set(x, y);
    }

    /** Creates a vector equal to {@code other}. */
    public Vector (IVector other) {
        set(other);
    }

    /** Creates a vector with zero x and y components. */
    public Vector () {
    }

    /** Computes the cross product of this and the specified other vector, storing the result in
     * this vector.
     * @return a reference to this vector, for chaining. */
    public Vector crossLocal (IVector other) {
        return cross(other, this);
    }

    /** Negates this vector in-place.
     * @return a reference to this vector, for chaining. */
    public Vector negateLocal () {
        return negate(this);
    }

    /** Normalizes this vector in-place.
     * @return a reference to this vector, for chaining. */
    public Vector normalizeLocal () {
        return normalize(this);
    }

    /** Scales this vector in place, uniformly by the specified magnitude.
     * @return a reference to this vector, for chaining. */
    public Vector scaleLocal (float v) {
        return scale(v, this);
    }

    /** Scales this vector's x and y components, in place, independently by the x and y components
     * of the supplied vector.
     * @return a reference to this vector, for chaining. */
    public Vector scaleLocal (IVector other) {
        return scale(other, this);
    }

    /** Adds a vector in-place to this one.
     * @return a reference to this vector, for chaining. */
    public Vector addLocal (IVector other) {
        return add(other, this);
    }

    /** Subtracts a vector in-place from this one.
     * @return a reference to this vector, for chaining. */
    public Vector subtractLocal (IVector other) {
        return subtract(other, this);
    }

    /** Adds a vector in-place to this one.
     * @return a reference to this vector, for chaining. */
    public Vector addLocal (float x, float y) {
        return add(x, y, this);
    }

    /** Subtracts a vector in-place from this one.
     * @return a reference to this vector, for chaining. */
    public Vector subtractLocal (float x, float y) {
        return subtract(x, y, this);
    }

    /** Adds a scaled vector in-place to this one.
     * @return a reference to this vector, for chaining. */
    public Vector addScaledLocal (IVector other, float v) {
        return addScaled(other, v, this);
    }

    /** Rotates this vector in-place by the specified angle.
     * @return a reference to this vector, for chaining. */
    public Vector rotateLocal (float angle) {
        return rotate(angle, this);
    }

    /** Linearly interpolates between this and {@code other} in-place by the supplied amount.
     * @return a reference to this vector, for chaining. */
    public Vector lerpLocal (IVector other, float t) {
        return lerp(other, t, this);
    }

    /** Copies the elements of another vector.
     * @return a reference to this vector, for chaining. */
    public Vector set (IVector other) {
        return set(other.x(), other.y());
    }

    /** Copies the elements of an array.
     * @return a reference to this vector, for chaining. */
    public Vector set (float[] values) {
        return set(values[0], values[1]);
    }

    /** Sets all of the elements of the vector.
     * @return a reference to this vector, for chaining. */
    public Vector set (float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets this vector's angle, preserving its magnitude.
     * @return a reference to this vector, for chaining.
     */
    public Vector setAngle (float angle) {
        float l = length();
        return set(l * FloatMath.cos(angle), l * FloatMath.sin(angle));
    }

    /**
     * Sets this vector's magnitude, preserving its angle.
     */
    public Vector setLength (float length) {
        return normalizeLocal().scaleLocal(length);
    }

    @Override // from XY
    public float x () {
        return x;
    }

    @Override // from XY
    public float y () {
        return y;
    }
}
