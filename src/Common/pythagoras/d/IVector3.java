//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.nio.DoubleBuffer;

/**
 * Provides read-only access to a {@link Vector3}.
 */
public interface IVector3
{
    /** Returns the x-component of this vector. */
    double x ();

    /** Returns the y-component of this vector. */
    double y ();

    /** Returns the z-component of this vector. */
    double z ();

    /**
     * Computes and returns the dot product of this and the specified other vector.
     */
    double dot (IVector3 other);

    /**
     * Computes the cross product of this and the specified other vector.
     *
     * @return a new vector containing the result.
     */
    Vector3 cross (IVector3 other);

    /**
     * Computes the cross product of this and the specified other vector, placing the result
     * in the object supplied.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 cross (IVector3 other, Vector3 result);

    /**
     * Computes the triple product of this and the specified other vectors, which is equal to
     * <code>this.dot(b.cross(c))</code>.
     */
    double triple (IVector3 b, IVector3 c);

    /**
     * Negates this vector.
     *
     * @return a new vector containing the result.
     */
    Vector3 negate ();

    /**
     * Negates this vector, storing the result in the supplied object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 negate (Vector3 result);

    /**
     * Absolute-values this vector.
     *
     * @return a new vector containing the result.
     */
    Vector3 abs ();

    /**
     * Absolute-values this vector, storing the result in the supplied object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 abs (Vector3 result);

    /**
     * Normalizes this vector.
     *
     * @return a new vector containing the result.
     */
    Vector3 normalize ();

    /**
     * Normalizes this vector, storing the result in the object supplied.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 normalize (Vector3 result);

    /**
     * Returns the angle between this vector and the specified other vector.
     */
    double angle (IVector3 other);

    /**
     * Returns the length of this vector.
     */
    double length ();

    /**
     * Returns the squared length of this vector.
     */
    double lengthSquared ();

    /**
     * Returns the distance from this vector to the specified other vector.
     */
    double distance (IVector3 other);

    /**
     * Returns the squared distance from this vector to the specified other.
     */
    double distanceSquared (IVector3 other);

    /**
     * Returns the Manhattan distance between this vector and the specified other.
     */
    double manhattanDistance (IVector3 other);

    /**
     * Multiplies this vector by a scalar.
     *
     * @return a new vector containing the result.
     */
    Vector3 mult (double v);

    /**
     * Multiplies this vector by a scalar and places the result in the supplied object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 mult (double v, Vector3 result);

    /**
     * Multiplies this vector by another.
     *
     * @return a new vector containing the result.
     */
    Vector3 mult (IVector3 other);

    /**
     * Multiplies this vector by another, storing the result in the object provided.
     *
     * @return a reference to the result vector, for chaining.
     */
    Vector3 mult (IVector3 other, Vector3 result);

    /**
     * Adds a vector to this one.
     *
     * @return a new vector containing the result.
     */
    Vector3 add (IVector3 other);

    /**
     * Adds a vector to this one, storing the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 add (IVector3 other, Vector3 result);

    /**
     * Subtracts a vector from this one.
     *
     * @return a new vector containing the result.
     */
    Vector3 subtract (IVector3 other);

    /**
     * Subtracts a vector from this one and places the result in the supplied object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 subtract (IVector3 other, Vector3 result);

    /**
     * Adds a vector to this one.
     *
     * @return a new vector containing the result.
     */
    Vector3 add (double x, double y, double z);

    /**
     * Adds a vector to this one and stores the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 add (double x, double y, double z, Vector3 result);

    /**
     * Adds a scaled vector to this one.
     *
     * @return a new vector containing the result.
     */
    Vector3 addScaled (IVector3 other, double v);

    /**
     * Adds a scaled vector to this one and stores the result in the supplied vector.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 addScaled (IVector3 other, double v, Vector3 result);

    /**
     * Linearly interpolates between this and the specified other vector by the supplied amount.
     *
     * @return a new vector containing the result.
     */
    Vector3 lerp (IVector3 other, double t);

    /**
     * Linearly interpolates between this and the supplied other vector by the supplied amount,
     * storing the result in the supplied object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 lerp (IVector3 other, double t, Vector3 result);

    /**
     * Returns the element at the idx'th position of the vector.
     */
    double get (int idx);

    /**
     * Populates the supplied array with the contents of this vector.
     */
    void get (double[] values);

    /**
     * Populates the supplied buffer with the contents of this vector.
     *
     * @return a reference to the buffer, for chaining.
     */
    DoubleBuffer get (DoubleBuffer buf);
}
