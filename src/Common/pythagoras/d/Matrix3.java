//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.io.Serializable;
import java.nio.DoubleBuffer;

import pythagoras.util.Platform;
import pythagoras.util.SingularMatrixException;

/**
 * A 3x3 column-major matrix.
 */
public class Matrix3 implements IMatrix3, Serializable
{
    /** the identity matrix. */
    public static final Matrix3 IDENTITY = new Matrix3();

    /** The values of the matrix. The names take the form {@mCOLROW}. */
    public double m00, m10, m20;
    public double m01, m11, m21;
    public double m02, m12, m22;

    /**
     * Creates a matrix from its components.
     */
    public Matrix3 (double m00, double m10, double m20,
                    double m01, double m11, double m21,
                    double m02, double m12, double m22) {
        set(m00, m10, m20,
            m01, m11, m21,
            m02, m12, m22);
    }

    /**
     * Creates a matrix from an array of values.
     */
    public Matrix3 (double[] values) {
        set(values);
    }

    /**
     * Copy constructor.
     */
    public Matrix3 (Matrix3 other) {
        set(other);
    }

    /**
     * Creates an identity matrix.
     */
    public Matrix3 () {
        setToIdentity();
    }

    /**
     * Sets the matrix element at the specified row and column.
     */
    public void setElement (int row, int col, double value) {
        switch (col) {
        case 0:
            switch (row) {
            case 0: m00 = value; return;
            case 1: m01 = value; return;
            case 2: m02 = value; return;
            }
            break;
        case 1:
            switch (row) {
            case 0: m10 = value; return;
            case 1: m11 = value; return;
            case 2: m12 = value; return;
            }
            break;
        case 2:
            switch (row) {
            case 0: m20 = value; return;
            case 1: m21 = value; return;
            case 2: m22 = value; return;
            }
            break;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    /**
     * Sets the specified row (0, 1, 2) to the supplied values.
     */
    public void setRow (int row, double x, double y, double z) {
        switch (row) {
        case 0: m00 = x; m10 = y; m20 = z; break;
        case 1: m01 = x; m11 = y; m21 = z; break;
        case 2: m02 = x; m12 = y; m22 = z; break;
        default: throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Sets the specified row (0, 1, 2) to the supplied vector.
     */
    public void setRow (int row, Vector3 v) {
        setRow(row, v.x(), v.y(), v.z());
    }

    /**
     * Sets the specified column (0, 1, 2) to the supplied values.
     */
    public void setColumn (int col, double x, double y, double z) {
        switch (col) {
        case 0: m00 = x; m01 = y; m02 = z; break;
        case 1: m10 = x; m11 = y; m12 = z; break;
        case 2: m20 = x; m21 = y; m22 = z; break;
        default: throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Sets the specified column (0, 1, 2) to the supplied vector.
     */
    public void setColumn (int col, Vector3 v) {
        setColumn(col, v.x(), v.y(), v.z());
    }

    /**
     * Sets this matrix to the identity matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToIdentity () {
        return set(1f, 0f, 0f,
                   0f, 1f, 0f,
                   0f, 0f, 1f);
    }

    /**
     * Sets this matrix to all zeroes.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToZero () {
        return set(0f, 0f, 0f,
                   0f, 0f, 0f,
                   0f, 0f, 0f);
    }

    /**
     * Sets this to a rotation matrix that rotates one vector onto another.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToRotation (IVector3 from, IVector3 to) {
        double angle = from.angle(to);
        return (angle < 0.0001f) ?
            setToIdentity() : setToRotation(angle, from.cross(to).normalizeLocal());
    }

    /**
     * Sets this to a rotation matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToRotation (double angle, IVector3 axis) {
        return setToRotation(angle, axis.x(), axis.y(), axis.z());
    }

    /**
     * Sets this to a rotation matrix. The formula comes from the OpenGL documentation for the
     * glRotatef function.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToRotation (double angle, double x, double y, double z) {
        double c = Math.cos(angle), s = Math.sin(angle), omc = 1f - c;
        double xs = x*s, ys = y*s, zs = z*s, xy = x*y, xz = x*z, yz = y*z;
        return set(x*x*omc + c, xy*omc - zs, xz*omc + ys,
                   xy*omc + zs, y*y*omc + c, yz*omc - xs,
                   xz*omc - ys, yz*omc + xs, z*z*omc + c);
    }

    /**
     * Sets this to a rotation matrix. The formula comes from the
     * <a href="http://www.j3d.org/matrix_faq/matrfaq_latest.html">Matrix and Quaternion FAQ</a>.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToRotation (IQuaternion quat) {
        double qx = quat.x(), qy = quat.y(), qz = quat.z(), qw = quat.w();
        double xx = qx*qx, yy = qy*qy, zz = qz*qz;
        double xy = qx*qy, xz = qx*qz, xw = qx*qw;
        double yz = qy*qz, yw = qy*qw, zw = qz*qw;
        return set(1f - 2f*(yy + zz), 2f*(xy - zw), 2f*(xz + yw),
                   2f*(xy + zw), 1f - 2f*(xx + zz), 2f*(yz - xw),
                   2f*(xz - yw), 2f*(yz + xw), 1f - 2f*(xx + yy));
    }

    /**
     * Sets this to a scale matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToScale (IVector3 scale) {
        return setToScale(scale.x(), scale.y(), scale.z());
    }

    /**
     * Sets this to a uniform scale matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToScale (double s) {
        return setToScale(s, s, s);
    }

    /**
     * Sets this to a scale matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToScale (double x, double y, double z) {
        return set(x,  0f, 0f,
                   0f, y,  0f,
                   0f, 0f, z);
    }

    /**
     * Sets this to a reflection across a plane intersecting the origin with the supplied normal.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToReflection (IVector3 normal) {
        return setToReflection(normal.x(), normal.y(), normal.z());
    }

    /**
     * Sets this to a reflection across a plane intersecting the origin with the supplied normal.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToReflection (double x, double y, double z) {
        double x2 = -2f*x, y2 = -2f*y, z2 = -2f*z;
        double xy2 = x2*y, xz2 = x2*z, yz2 = y2*z;
        return set(1f + x2*x, xy2, xz2,
                   xy2, 1f + y2*y, yz2,
                   xz2, yz2, 1f + z2*z);
    }

    /**
     * Sets this to a matrix that first rotates, then translates.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToTransform (IVector translation, double rotation) {
        return setToRotation(rotation).setTranslation(translation);
    }

    /**
     * Sets this to a matrix that first scales, then rotates, then translates.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToTransform (IVector translation, double rotation, double scale) {
        return setToRotation(rotation).set(m00 * scale, m10 * scale, translation.x(),
                                           m01 * scale, m11 * scale, translation.y(),
                                           0f, 0f, 1f);
    }

    /**
     * Sets this to a matrix that first scales, then rotates, then translates.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToTransform (IVector translation, double rotation, IVector scale) {
        double sx = scale.x(), sy = scale.y();
        return setToRotation(rotation).set(m00 * sx, m10 * sy, translation.x(),
                                           m01 * sx, m11 * sy, translation.y(),
                                           0f, 0f, 1f);
    }

    /**
     * Sets this to a translation matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToTranslation (IVector translation) {
        return setToTranslation(translation.x(), translation.y());
    }

    /**
     * Sets this to a translation matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToTranslation (double x, double y) {
        return set(1f, 0f, x,
                   0f, 1f, y,
                   0f, 0f, 1f);
    }

    /**
     * Sets the translation component of this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setTranslation (IVector translation) {
        return setTranslation(translation.x(), translation.y());
    }

    /**
     * Sets the translation component of this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setTranslation (double x, double y) {
        m20 = x;
        m21 = y;
        return this;
    }

    /**
     * Sets this to a rotation matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 setToRotation (double angle) {
        double sina = Math.sin(angle), cosa = Math.cos(angle);
        return set(cosa, -sina, 0f,
                   sina, cosa, 0f,
                   0f, 0f, 1f);
    }

    /**
     * Transposes this matrix in-place.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 transposeLocal () {
        return transpose(this);
    }

    /**
     * Multiplies this matrix in-place by another.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 multLocal (IMatrix3 other) {
        return mult(other, this);
    }

    /**
     * Adds {@code other} to this matrix, in place.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 addLocal (IMatrix3 other) {
        return add(other, this);
    }

    /**
     * Multiplies this matrix in-place by another, treating the matricees as affine.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 multAffineLocal (IMatrix3 other) {
        return multAffine(other, this);
    }

    /**
     * Inverts this matrix in-place.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 invertLocal () {
        return invert(this);
    }

    /**
     * Inverts this matrix in-place as an affine matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 invertAffineLocal () {
        return invertAffine(this);
    }

    /**
     * Linearly interpolates between the this and the specified other matrix, placing the result in
     * this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 lerpLocal (IMatrix3 other, double t) {
        return lerp(other, t, this);
    }

    /**
     * Linearly interpolates between this and the specified other matrix (treating the matrices as
     * affine), placing the result in this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 lerpAffineLocal (IMatrix3 other, double t) {
        return lerpAffine(other, t, this);
    }

    /**
     * Copies the contents of another matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 set (IMatrix3 other) {
        return set(other.m00(), other.m10(), other.m20(),
                   other.m01(), other.m11(), other.m21(),
                   other.m02(), other.m12(), other.m22());
    }

    /**
     * Copies the elements of an array.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 set (double[] values) {
        return set(values[0], values[1], values[2],
                   values[3], values[4], values[5],
                   values[6], values[7], values[8]);
    }

    /**
     * Sets all of the matrix's components at once.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix3 set (
        double m00, double m10, double m20,
        double m01, double m11, double m21,
        double m02, double m12, double m22) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02;
        this.m10 = m10; this.m11 = m11; this.m12 = m12;
        this.m20 = m20; this.m21 = m21; this.m22 = m22;
        return this;
    }

    @Override // from IMatrix3
    public double m00 () {
        return m00;
    }

    @Override // from IMatrix3
    public double m10 () {
        return m10;
    }

    @Override // from IMatrix3
    public double m20 () {
        return m20;
    }

    @Override // from IMatrix3
    public double m01 () {
        return m01;
    }

    @Override // from IMatrix3
    public double m11 () {
        return m11;
    }

    @Override // from IMatrix3
    public double m21 () {
        return m21;
    }

    @Override // from IMatrix3
    public double m02 () {
        return m02;
    }

    @Override // from IMatrix3
    public double m12 () {
        return m12;
    }

    @Override // from IMatrix3
    public double m22 () {
        return m22;
    }

    @Override // from IMatrix3
    public double element (int row, int col) {
        switch (col) {
        case 0:
            switch (row) {
            case 0: return m00;
            case 1: return m01;
            case 2: return m02;
            }
            break;
        case 1:
            switch (row) {
            case 0: return m10;
            case 1: return m11;
            case 2: return m12;
            }
            break;
        case 2:
            switch (row) {
            case 0: return m20;
            case 1: return m21;
            case 2: return m22;
            }
            break;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override // from IMatrix3
    public void getRow (int row, Vector3 result) {
        switch (row) {
        case 0: result.x = m00; result.y = m10; result.z = m20; break;
        case 1: result.x = m01; result.y = m11; result.z = m21; break;
        case 2: result.x = m02; result.y = m12; result.z = m22; break;
        default: throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override // from IMatrix3
    public void getColumn (int col, Vector3 result) {
        switch (col) {
        case 0: result.x = m00; result.y = m01; result.z = m02; break;
        case 1: result.x = m10; result.y = m11; result.z = m12; break;
        case 2: result.x = m20; result.y = m21; result.z = m22; break;
        default: throw new ArrayIndexOutOfBoundsException();
        }
    }

    @Override // from IMatrix3
    public Matrix3 transpose () {
        return transpose(new Matrix3());
    }

    @Override // from IMatrix3
    public Matrix3 transpose (Matrix3 result) {
        return result.set(m00, m01, m02,
                          m10, m11, m12,
                          m20, m21, m22);
    }

    @Override // from IMatrix3
    public Matrix3 mult (IMatrix3 other) {
        return mult(other, new Matrix3());
    }

    @Override // from IMatrix3
    public Matrix3 mult (IMatrix3 other, Matrix3 result) {
        double m00 = this.m00, m01 = this.m01, m02 = this.m02;
        double m10 = this.m10, m11 = this.m11, m12 = this.m12;
        double m20 = this.m20, m21 = this.m21, m22 = this.m22;
        double om00 = other.m00(), om01 = other.m01(), om02 = other.m02();
        double om10 = other.m10(), om11 = other.m11(), om12 = other.m12();
        double om20 = other.m20(), om21 = other.m21(), om22 = other.m22();
        return result.set(m00*om00 + m10*om01 + m20*om02,
                          m00*om10 + m10*om11 + m20*om12,
                          m00*om20 + m10*om21 + m20*om22,

                          m01*om00 + m11*om01 + m21*om02,
                          m01*om10 + m11*om11 + m21*om12,
                          m01*om20 + m11*om21 + m21*om22,

                          m02*om00 + m12*om01 + m22*om02,
                          m02*om10 + m12*om11 + m22*om12,
                          m02*om20 + m12*om21 + m22*om22);
    }

    @Override // from IMatrix3
    public Matrix3 add (IMatrix3 other) {
        return add(other, new Matrix3());
    }

    @Override // from IMatrix3
    public Matrix3 add (IMatrix3 other, Matrix3 result) {
        return result.set(m00 + other.m00(), m01 + other.m01(), m02 + other.m02(),
                          m10 + other.m10(), m11 + other.m11(), m12 + other.m12(),
                          m20 + other.m20(), m21 + other.m21(), m22 + other.m22());
    }

    @Override // from IMatrix3
    public boolean isAffine () {
        return (m02 == 0f && m12 == 0f && m22 == 1f);
    }

    @Override // from IMatrix3
    public Matrix3 multAffine (IMatrix3 other) {
        return multAffine(other, new Matrix3());
    }

    @Override // from IMatrix3
    public Matrix3 multAffine (IMatrix3 other, Matrix3 result) {
        double m00 = this.m00, m01 = this.m01;
        double m10 = this.m10, m11 = this.m11;
        double m20 = this.m20, m21 = this.m21;
        double om00 = other.m00(), om01 = other.m01();
        double om10 = other.m10(), om11 = other.m11();
        double om20 = other.m20(), om21 = other.m21();
        return result.set(m00*om00 + m10*om01,
                          m00*om10 + m10*om11,
                          m00*om20 + m10*om21 + m20,

                          m01*om00 + m11*om01,
                          m01*om10 + m11*om11,
                          m01*om20 + m11*om21 + m21,

                          0f, 0f, 1f);
    }

    @Override // from IMatrix3
    public Matrix3 invert () {
        return invert(new Matrix3());
    }

    /**
     * {@inheritDoc} This code is based on the examples in the
     * <a href="http://www.j3d.org/matrix_faq/matrfaq_latest.html">Matrix and Quaternion FAQ</a>.
     */
    @Override // from IMatrix3
    public Matrix3 invert (Matrix3 result) throws SingularMatrixException {
        double m00 = this.m00, m01 = this.m01, m02 = this.m02;
        double m10 = this.m10, m11 = this.m11, m12 = this.m12;
        double m20 = this.m20, m21 = this.m21, m22 = this.m22;
        // compute the determinant, storing the subdeterminants for later use
        double sd00 = m11*m22 - m21*m12;
        double sd10 = m01*m22 - m21*m02;
        double sd20 = m01*m12 - m11*m02;
        double det = m00*sd00 + m20*sd20 - m10*sd10;
        if (Math.abs(det) == 0f) {
            // determinant is zero; matrix is not invertible
            throw new SingularMatrixException(this.toString());
        }
        double rdet = 1f / det;
        return result.set(+sd00 * rdet,
                          -(m10*m22 - m20*m12) * rdet,
                          +(m10*m21 - m20*m11) * rdet,

                          -sd10 * rdet,
                          +(m00*m22 - m20*m02) * rdet,
                          -(m00*m21 - m20*m01) * rdet,

                          +sd20 * rdet,
                          -(m00*m12 - m10*m02) * rdet,
                          +(m00*m11 - m10*m01) * rdet);
    }

    @Override // from IMatrix3
    public Matrix3 invertAffine () {
        return invertAffine(new Matrix3());
    }

    @Override // from IMatrix3
    public Matrix3 invertAffine (Matrix3 result) throws SingularMatrixException {
        double m00 = this.m00, m01 = this.m01;
        double m10 = this.m10, m11 = this.m11;
        double m20 = this.m20, m21 = this.m21;
        // compute the determinant, storing the subdeterminants for later use
        double det = m00*m11 - m10*m01;
        if (Math.abs(det) == 0f) {
            // determinant is zero; matrix is not invertible
            throw new SingularMatrixException(this.toString());
        }
        double rdet = 1f / det;
        return result.set(+m11 * rdet,
                          -m10 * rdet,
                          +(m10*m21 - m20*m11) * rdet,

                          -m01 * rdet,
                          +m00 * rdet,
                          -(m00*m21 - m20*m01) * rdet,

                          0f, 0f, 1f);
    }

    @Override // from IMatrix3
    public Matrix3 lerp (IMatrix3 other, double t) {
        return lerp(other, t, new Matrix3());
    }

    @Override // from IMatrix3
    public Matrix3 lerp (IMatrix3 other, double t, Matrix3 result) {
        double m00 = this.m00, m01 = this.m01, m02 = this.m02;
        double m10 = this.m10, m11 = this.m11, m12 = this.m12;
        double m20 = this.m20, m21 = this.m21, m22 = this.m22;
        double om00 = other.m00(), om01 = other.m01(), om02 = other.m02();
        double om10 = other.m10(), om11 = other.m11(), om12 = other.m12();
        double om20 = other.m20(), om21 = other.m21(), om22 = other.m22();
        return result.set(m00 + t*(om00 - m00),
                          m10 + t*(om10 - m10),
                          m20 + t*(om20 - m20),

                          m01 + t*(om01 - m01),
                          m11 + t*(om11 - m11),
                          m21 + t*(om21 - m21),

                          m02 + t*(om02 - m02),
                          m12 + t*(om12 - m12),
                          m22 + t*(om22 - m22));
    }

    @Override // from IMatrix3
    public Matrix3 lerpAffine (IMatrix3 other, double t) {
        return lerpAffine(other, t, new Matrix3());
    }

    @Override // from IMatrix3
    public Matrix3 lerpAffine (IMatrix3 other, double t, Matrix3 result) {
        double m00 = this.m00, m01 = this.m01;
        double m10 = this.m10, m11 = this.m11;
        double m20 = this.m20, m21 = this.m21;
        double om00 = other.m00(), om01 = other.m01();
        double om10 = other.m10(), om11 = other.m11();
        double om20 = other.m20(), om21 = other.m21();
        return result.set(m00 + t*(om00 - m00),
                          m10 + t*(om10 - m10),
                          m20 + t*(om20 - m20),

                          m01 + t*(om01 - m01),
                          m11 + t*(om11 - m11),
                          m21 + t*(om21 - m21),

                          0f, 0f, 1f);
    }

    @Override // from IMatrix3
    public DoubleBuffer get (DoubleBuffer buf) {
        buf.put(m00).put(m01).put(m02);
        buf.put(m10).put(m11).put(m12);
        buf.put(m20).put(m21).put(m22);
        return buf;
    }

    @Override // from IMatrix3
    public Vector3 transformLocal (Vector3 vector) {
        return transform(vector, vector);
    }

    @Override // from IMatrix3
    public Vector3 transform (IVector3 vector) {
        return transform(vector, new Vector3());
    }

    @Override // from IMatrix3
    public Vector3 transform (IVector3 vector, Vector3 result) {
        double vx = vector.x(), vy = vector.y(), vz = vector.z();
        return result.set(m00*vx + m10*vy + m20*vz,
                          m01*vx + m11*vy + m21*vz,
                          m02*vx + m12*vy + m22*vz);
    }

    @Override // from IMatrix3
    public Vector transformPointLocal (Vector point) {
        return transformPoint(point, point);
    }

    @Override // from IMatrix3
    public Vector transformPoint (IVector point) {
        return transformPoint(point, new Vector());
    }

    @Override // from IMatrix3
    public Vector transformPoint (IVector point, Vector result) {
        double px = point.x(), py = point.y();
        return result.set(m00*px + m10*py + m20, m01*px + m11*py + m21);
    }

    @Override // from IMatrix3
    public Vector transformVectorLocal (Vector vector) {
        return transformVector(vector, vector);
    }

    @Override // from IMatrix3
    public Vector transformVector (IVector vector) {
        return transformVector(vector, new Vector());
    }

    @Override // from IMatrix3
    public Vector transformVector (IVector vector, Vector result) {
        double vx = vector.x(), vy = vector.y();
        return result.set(m00*vx + m10*vy, m01*vx + m11*vy);
    }

    /**
     * {@inheritDoc} This uses the iterative polar decomposition algorithm described by
     * <a href="http://www.cs.wisc.edu/graphics/Courses/838-s2002/Papers/polar-decomp.pdf">Ken
     * Shoemake</a>.
     */
    @Override // from IMatrix3
    public double extractRotation () {
        // start with the contents of the upper 2x2 portion of the matrix
        double n00 = m00, n10 = m10;
        double n01 = m01, n11 = m11;
        for (int ii = 0; ii < 10; ii++) {
            // store the results of the previous iteration
            double o00 = n00, o10 = n10;
            double o01 = n01, o11 = n11;

            // compute average of the matrix with its inverse transpose
            double det = o00*o11 - o10*o01;
            if (Math.abs(det) == 0f) {
                // determinant is zero; matrix is not invertible
                throw new SingularMatrixException(this.toString());
            }
            double hrdet = 0.5f / det;
            n00 = +o11 * hrdet + o00*0.5f;
            n10 = -o01 * hrdet + o10*0.5f;

            n01 = -o10 * hrdet + o01*0.5f;
            n11 = +o00 * hrdet + o11*0.5f;

            // compute the difference; if it's small enough, we're done
            double d00 = n00 - o00, d10 = n10 - o10;
            double d01 = n01 - o01, d11 = n11 - o11;
            if (d00*d00 + d10*d10 + d01*d01 + d11*d11 < MathUtil.EPSILON) {
                break;
            }
        }
        // now that we have a nice orthogonal matrix, we can extract the rotation
        return Math.atan2(n01, n00);
    }

    @Override // from IMatrix3
    public Vector extractScale () {
        return extractScale(new Vector());
    }

    @Override // from IMatrix3
    public Vector extractScale (Vector result) {
        double m00 = this.m00, m01 = this.m01, m10 = this.m10, m11 = this.m11;
        return result.set(Math.sqrt(m00*m00 + m01*m01),
                          Math.sqrt(m10*m10 + m11*m11));
    }

    @Override // from IMatrix3
    public double approximateUniformScale () {
        double cp = m00*m11 - m01*m10;
        return (cp < 0f) ? -Math.sqrt(-cp) : Math.sqrt(cp);
    }

    @Override
    public String toString () {
        return ("[[" + m00 + ", " + m10 + ", " + m20 + "], " +
                "["  + m01 + ", " + m11 + ", " + m21 + "], " +
                "["  + m02 + ", " + m12 + ", " + m22 + "]]");
    }

    @Override
    public int hashCode () {
        return Platform.hashCode(m00) ^ Platform.hashCode(m10) ^ Platform.hashCode(m20) ^
            Platform.hashCode(m01) ^ Platform.hashCode(m11) ^ Platform.hashCode(m21) ^
            Platform.hashCode(m02) ^ Platform.hashCode(m12) ^ Platform.hashCode(m22);
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof Matrix3)) {
            return false;
        }
        Matrix3 omat = (Matrix3)other;
        return (m00 == omat.m00 && m10 == omat.m10 && m20 == omat.m20 &&
                m01 == omat.m01 && m11 == omat.m11 && m21 == omat.m21 &&
                m02 == omat.m02 && m12 == omat.m12 && m22 == omat.m22);
    }
}
