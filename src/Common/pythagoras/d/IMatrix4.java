//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.nio.DoubleBuffer;

/**
 * Provides read-only access to a {@link Matrix4}.
 */
public interface IMatrix4
{
    /** Returns the (0,0)th component of the matrix. */
    double m00 ();

    /** Returns the (1,0)th component of the matrix. */
    double m10 ();

    /** Returns the (2,0)th component of the matrix. */
    double m20 ();

    /** Returns the (3,0)th component of the matrix. */
    double m30 ();

    /** Returns the (0,1)th component of the matrix. */
    double m01 ();

    /** Returns the (1,1)th component of the matrix. */
    double m11 ();

    /** Returns the (2,1)th component of the matrix. */
    double m21 ();

    /** Returns the (3,1)th component of the matrix. */
    double m31 ();

    /** Returns the (0,2)th component of the matrix. */
    double m02 ();

    /** Returns the (1,2)th component of the matrix. */
    double m12 ();

    /** Returns the (2,2)th component of the matrix. */
    double m22 ();

    /** Returns the (3,2)th component of the matrix. */
    double m32 ();

    /** Returns the (0,3)th component of the matrix. */
    double m03 ();

    /** Returns the (1,3)th component of the matrix. */
    double m13 ();

    /** Returns the (2,3)th component of the matrix. */
    double m23 ();

    /** Returns the (3,3)th component of the matrix. */
    double m33 ();

    /**
     * Transposes this matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix4 transpose ();

    /**
     * Transposes this matrix, storing the result in the provided object.
     *
     * @return the result matrix, for chaining.
     */
    Matrix4 transpose (Matrix4 result);

    /**
     * Multiplies this matrix by another.
     *
     * @return a new matrix containing the result.
     */
    Matrix4 mult (IMatrix4 other);

    /**
     * Multiplies this matrix by another and stores the result in the object provided.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix4 mult (IMatrix4 other, Matrix4 result);

    /**
     * Determines whether this matrix represents an affine transformation.
     */
    boolean isAffine ();

    /**
     * Determines whether the matrix is mirrored.
     */
    boolean isMirrored ();

    /**
     * Multiplies this matrix by another, treating the matrices as affine.
     *
     * @return a new matrix containing the result.
     */
    Matrix4 multAffine (IMatrix4 other);

    /**
     * Multiplies this matrix by another, treating the matrices as affine, and stores the result
     * in the object provided.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix4 multAffine (IMatrix4 other, Matrix4 result);

    /**
     * Inverts this matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix4 invert ();

    /**
     * Inverts this matrix and places the result in the given object.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix4 invert (Matrix4 result);

    /**
     * Inverts this matrix as an affine matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix4 invertAffine ();

    /**
     * Inverts this matrix as an affine matrix and places the result in the given object.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix4 invertAffine (Matrix4 result);

    /**
     * Linearly interpolates between this and the specified other matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix4 lerp (IMatrix4 other, double t);

    /**
     * Linearly interpolates between this and the specified other matrix, placing the result in
     * the object provided.
     *
     * @return a reference to the result object, for chaining.
     */
    Matrix4 lerp (IMatrix4 other, double t, Matrix4 result);

    /**
     * Linearly interpolates between this and the specified other matrix, treating the matrices as
     * affine.
     *
     * @return a new matrix containing the result.
     */
    Matrix4 lerpAffine (IMatrix4 other, double t);

    /**
     * Linearly interpolates between this and the specified other matrix (treating the matrices as
     * affine), placing the result in the object provided.
     *
     * @return a reference to the result object, for chaining.
     */
    Matrix4 lerpAffine (IMatrix4 other, double t, Matrix4 result);

    /**
     * Places the contents of this matrix into the given buffer in the standard OpenGL order.
     *
     * @return a reference to the buffer, for chaining.
     */
    DoubleBuffer get (DoubleBuffer buf);

    /**
     * Projects the supplied point in-place using this matrix.
     *
     * @return a reference to the point, for chaining.
     */
    Vector3 projectPointLocal (Vector3 point);

    /**
     * Projects the supplied point using this matrix.
     *
     * @return a new vector containing the result.
     */
    Vector3 projectPoint (IVector3 point);

    /**
     * Projects the supplied point using this matrix and places the result in the object supplied.
     *
     * @return a reference to the result vector, for chaining.
     */
    Vector3 projectPoint (IVector3 point, Vector3 result);

    /**
     * Transforms a point in-place by this matrix.
     *
     * @return a reference to the point, for chaining.
     */
    Vector3 transformPointLocal (Vector3 point);

    /**
     * Transforms a point by this matrix.
     *
     * @return a new vector containing the result.
     */
    Vector3 transformPoint (IVector3 point);

    /**
     * Transforms a point by this matrix and places the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transformPoint (IVector3 point, Vector3 result);

    /**
     * Transforms a point by this matrix and returns the resulting z coordinate.
     */
    double transformPointZ (IVector3 point);

    /**
     * Transforms a vector in-place by the inner 3x3 part of this matrix.
     *
     * @return a reference to the vector, for chaining.
     */
    Vector3 transformVectorLocal (Vector3 vector);

    /**
     * Transforms a vector by this inner 3x3 part of this matrix.
     *
     * @return a new vector containing the result.
     */
    Vector3 transformVector (IVector3 vector);

    /**
     * Transforms a vector by the inner 3x3 part of this matrix and places the result in the object
     * provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transformVector (IVector3 vector, Vector3 result);

    /**
     * Transforms {@code vector} by this matrix (M * V).
     *
     * @return a new vector containing the result.
     */
    Vector4 transform (IVector4 vector);

    /**
     * Transforms {@code vector} by this matrix (M * V) and stores the result in the object
     * provided.
     *
     * @return a reference to the result vector, for chaining.
     */
    Vector4 transform (IVector4 vector, Vector4 result);

    /**
     * Extracts the rotation component of the matrix.
     *
     * @return a new quaternion containing the result.
     */
    Quaternion extractRotation ();

    /**
     * Extracts the rotation component of the matrix and places it in the provided result
     * quaternion.
     *
     * @return a reference to the result quaternion, for chaining.
     */
    Quaternion extractRotation (Quaternion result);

    /**
     * Extracts the rotation and scale components and places them in the provided result.
     *
     * @return a reference to {@code result}, for chaining.
     */
    Matrix3 extractRotationScale (Matrix3 result);

    /**
     * Extracts the scale component of the matrix.
     *
     * @return a new vector containing the result.
     */
    Vector3 extractScale ();

    /**
     * Extracts the scale component of the matrix and places it in the provided result vector.
     *
     * @return a reference to the result vector, for chaining.
     */
    Vector3 extractScale (Vector3 result);

    /**
     * Returns an approximation of the uniform scale for this matrix (the cube root of the
     * signed volume of the parallelepiped spanned by the axis vectors).
     */
    double approximateUniformScale ();

    /**
     * Compares this matrix to another with the provided epsilon.
     */
    boolean epsilonEquals (IMatrix4 other, double epsilon);
}
