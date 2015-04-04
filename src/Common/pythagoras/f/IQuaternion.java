//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Provides read-only access to a {@link Quaternion}.
 */
public interface IQuaternion
{
    /** Returns the x-component of this quaternion. */
    float x ();

    /** Returns the y-component of this quaternion. */
    float y ();

    /** Returns the z-component of this quaternion. */
    float z ();

    /** Returns the w-component of this quaternion. */
    float w ();

    /**
     * Populates the supplied array with the contents of this quaternion.
     */
    void get (float[] values);

    /**
     * Checks whether any of the components of this quaternion are not-numbers.
     */
    boolean hasNaN ();

    /**
     * Computes the angles to pass to {@link Quaternion#fromAngles} to reproduce this rotation,
     * placing them in the provided vector. This uses the factorization method described in David
     * Eberly's <a href="http://www.geometrictools.com/Documentation/EulerAngles.pdf">Euler Angle
     * Formulas</a>.
     *
     * @return a reference to the result vector, for chaining.
     */
    Vector3 toAngles (Vector3 result);

    /**
     * Computes and returns the angles to pass to {@link Quaternion#fromAngles} to reproduce this
     * rotation.
     *
     * @return a new vector containing the resulting angles.
     */
    Vector3 toAngles ();

    /**
     * Normalizes this quaternion.
     *
     * @return a new quaternion containing the result.
     */
    Quaternion normalize ();

    /**
     * Normalizes this quaternion, storing the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Quaternion normalize (Quaternion result);

    /**
     * Inverts this quaternion.
     *
     * @return a new quaternion containing the result.
     */
    Quaternion invert ();

    /**
     * Inverts this quaternion, storing the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Quaternion invert (Quaternion result);

    /**
     * Multiplies this quaternion by another.
     *
     * @return a new quaternion containing the result.
     */
    Quaternion mult (IQuaternion other);

    /**
     * Multiplies this quaternion by another and stores the result in the provided object.
     *
     * @return a reference to the result, for chaining.
     */
    Quaternion mult (IQuaternion other, Quaternion result);

    /**
     * Interpolates between this and the specified other quaternion.
     *
     * @return a new quaternion containing the result.
     */
    Quaternion slerp (IQuaternion other, float t);

    /**
     * Interpolates between this and the specified other quaternion, placing the result in the
     * object provided. Based on the code in Nick Bobick's article,
     * <a href="http://www.gamasutra.com/features/19980703/quaternions_01.htm">Rotating Objects
     * Using Quaternions</a>.
     *
     * @return a reference to the result quaternion, for chaining.
     */
    Quaternion slerp (IQuaternion other, float t, Quaternion result);

    /**
     * Transforms a vector by this quaternion.
     *
     * @return a new vector containing the result.
     */
    Vector3 transform (IVector3 vector);

    /**
     * Transforms a vector by this quaternion and places the result in the provided object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transform (IVector3 vector, Vector3 result);

    /**
     * Transforms the unit x vector by this quaternion, placing the result in the provided object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transformUnitX (Vector3 result);

    /**
     * Transforms the unit y vector by this quaternion, placing the result in the provided object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transformUnitY (Vector3 result);

    /**
     * Transforms the unit z vector by this quaternion, placing the result in the provided object.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transformUnitZ (Vector3 result);

    /**
     * Transforms a vector by this quaternion and adds another vector to it, placing the result
     * in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transformAndAdd (IVector3 vector, IVector3 add, Vector3 result);

    /**
     * Transforms a vector by this quaternion, applies a uniform scale, and adds another vector to
     * it, placing the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transformScaleAndAdd (IVector3 vector, float scale, IVector3 add, Vector3 result);

    /**
     * Transforms a vector by this quaternion and returns the z coordinate of the result.
     */
    float transformZ (IVector3 vector);

    /**
     * Returns the amount of rotation about the z axis (for the purpose of flattening the
     * rotation).
     */
    float getRotationZ ();

    /**
     * Integrates the provided angular velocity over the specified timestep.
     *
     * @return a new quaternion containing the result.
     */
    Quaternion integrate (IVector3 velocity, float t);

    /**
     * Integrates the provided angular velocity over the specified timestep, storing the result in
     * the object provided.
     *
     * @return a reference to the result object, for chaining.
     */
    Quaternion integrate (IVector3 velocity, float t, Quaternion result);
}
