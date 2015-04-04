//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.nio.DoubleBuffer;

import pythagoras.util.SingularMatrixException;

/**
 * Provides read-only access to a {@link Matrix3}.
 */
interface IMatrix3
{
    /** Returns column 0, row 0 of the matrix. */
    double m00 ();

    /** Returns column 1, row 0 of the matrix. */
    double m10 ();

    /** Returns column 2, row 0 of the matrix. */
    double m20 ();

    /** Returns column 0, row 1 of the matrix. */
    double m01 ();

    /** Returns column 1, row 1 of the matrix. */
    double m11 ();

    /** Returns column 2, row 1 of the matrix. */
    double m21 ();

    /** Returns column 0, row 2 of the matrix. */
    double m02 ();

    /** Returns column 1, row 2 of the matrix. */
    double m12 ();

    /** Returns column 2, row 2 of the matrix. */
    double m22 ();

    /** Returns the matrix element at the specified row and column. */
    double element (int row, int col);

    /** Copies the requested row (0, 1, 2) into {@code result}. */
    void getRow (int row, Vector3 result);

    /** Copies the requested column (0, 1, 2) into {@code result}. */
    void getColumn (int col, Vector3 result);

    /**
     * Transposes this matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 transpose ();

    /**
     * Transposes this matrix, storing the result in the provided object.
     *
     * @return the result matrix, for chaining.
     */
    Matrix3 transpose (Matrix3 result);

    /**
     * Multiplies this matrix by another.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 mult (IMatrix3 other);

    /**
     * Multiplies this matrix by another and stores the result in the object provided.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix3 mult (IMatrix3 other, Matrix3 result);

    /**
     * Adds this matrix to another.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 add (IMatrix3 other);

    /**
     * Adds this matrix to another and stores the result in the object provided.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix3 add (IMatrix3 other, Matrix3 result);

    /**
     * Determines whether this matrix represents an affine transformation.
     */
    boolean isAffine ();

    /**
     * Multiplies this matrix by another, treating the matrices as affine.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 multAffine (IMatrix3 other);

    /**
     * Multiplies this matrix by another, treating the matrices as affine, and stores the result
     * in the object provided.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix3 multAffine (IMatrix3 other, Matrix3 result);

    /**
     * Inverts this matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 invert ();

    /**
     * Inverts this matrix and places the result in the given object.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix3 invert (Matrix3 result) throws SingularMatrixException;

    /**
     * Inverts this matrix as an affine matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 invertAffine ();

    /**
     * Inverts this matrix as an affine matrix and places the result in the given object.
     *
     * @return a reference to the result matrix, for chaining.
     */
    Matrix3 invertAffine (Matrix3 result) throws SingularMatrixException;

    /**
     * Linearly interpolates between this and the specified other matrix.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 lerp (IMatrix3 other, double t);

    /**
     * Linearly interpolates between this and the specified other matrix, placing the result in
     * the object provided.
     *
     * @return a reference to the result object, for chaining.
     */
    Matrix3 lerp (IMatrix3 other, double t, Matrix3 result);

    /**
     * Linearly interpolates between this and the specified other matrix, treating the matrices as
     * affine.
     *
     * @return a new matrix containing the result.
     */
    Matrix3 lerpAffine (IMatrix3 other, double t);

    /**
     * Linearly interpolates between this and the specified other matrix (treating the matrices as
     * affine), placing the result in the object provided.
     *
     * @return a reference to the result object, for chaining.
     */
    Matrix3 lerpAffine (IMatrix3 other, double t, Matrix3 result);

    /**
     * Places the contents of this matrix into the given buffer in the standard OpenGL order.
     *
     * @return a reference to the buffer, for chaining.
     */
    DoubleBuffer get (DoubleBuffer buf);

    /**
     * Transforms a vector in-place by the inner 3x3 part of this matrix.
     *
     * @return a reference to the vector, for chaining.
     */
    Vector3 transformLocal (Vector3 vector);

    /**
     * Transforms a vector by this matrix.
     *
     * @return a new vector containing the result.
     */
    Vector3 transform (IVector3 vector);

    /**
     * Transforms a vector by this matrix and places the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 transform (IVector3 vector, Vector3 result);

    /**
     * Transforms a point in-place by this matrix.
     *
     * @return a reference to the point, for chaining.
     */
    Vector transformPointLocal (Vector point);

    /**
     * Transforms a point by this matrix.
     *
     * @return a new vector containing the result.
     */
    Vector transformPoint (IVector point);

    /**
     * Transforms a point by this matrix and places the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector transformPoint (IVector point, Vector result);

    /**
     * Transforms a vector in-place by the inner 2x2 part of this matrix.
     *
     * @return a reference to the vector, for chaining.
     */
    Vector transformVectorLocal (Vector vector);

    /**
     * Transforms a vector by this inner 2x2 part of this matrix.
     *
     * @return a new vector containing the result.
     */
    Vector transformVector (IVector vector);

    /**
     * Transforms a vector by the inner 2x2 part of this matrix and places the result in the object
     * provided.
     *
     * @return a reference to the result, for chaining.
     */
    Vector transformVector (IVector vector, Vector result);

    /**
     * Extracts the rotation component of the matrix.
     */
    double extractRotation ();

    /**
     * Extracts the scale component of the matrix.
     *
     * @return a new vector containing the result.
     */
    Vector extractScale ();

    /**
     * Extracts the scale component of the matrix and places it in the provided result vector.
     *
     * @return a reference to the result vector, for chaining.
     */
    Vector extractScale (Vector result);

    /**
     * Returns an approximation of the uniform scale for this matrix (the square root of the
     * signed area of the parallelogram spanned by the axis vectors).
     */
    double approximateUniformScale ();
}
