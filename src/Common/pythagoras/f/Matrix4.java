//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;
import java.nio.FloatBuffer;

import pythagoras.util.Platform;
import pythagoras.util.SingularMatrixException;

/**
 * A 4x4 column-major matrix.
 */
public final class Matrix4 implements IMatrix4, Serializable
{
    /** The identity matrix. */
    public static final IMatrix4 IDENTITY = new Matrix4();

    /** An empty matrix array. */
    public static final Matrix4[] EMPTY_ARRAY = new Matrix4[0];

    /** The values of the matrix. The names take the form {@mCOLROW}. */
    public float m00, m10, m20, m30;
    public float m01, m11, m21, m31;
    public float m02, m12, m22, m32;
    public float m03, m13, m23, m33;

    /**
     * Creates a matrix from its components.
     */
    public Matrix4 (
        float m00, float m10, float m20, float m30,
        float m01, float m11, float m21, float m31,
        float m02, float m12, float m22, float m32,
        float m03, float m13, float m23, float m33) {
        set(m00, m10, m20, m30,
            m01, m11, m21, m31,
            m02, m12, m22, m32,
            m03, m13, m23, m33);
    }

    /**
     * Creates a matrix from an array of values.
     */
    public Matrix4 (float[] values) {
        set(values);
    }

    /**
     * Creates a matrix from a float buffer.
     */
    public Matrix4 (FloatBuffer buf) {
        set(buf);
    }

    /**
     * Copy constructor.
     */
    public Matrix4 (IMatrix4 other) {
        set(other);
    }

    /**
     * Creates an identity matrix.
     */
    public Matrix4 () {
        setToIdentity();
    }

    /**
     * Sets this matrix to the identity matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToIdentity () {
        return set(1f, 0f, 0f, 0f,
                   0f, 1f, 0f, 0f,
                   0f, 0f, 1f, 0f,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets this matrix to all zeroes.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToZero () {
        return set(0f, 0f, 0f, 0f,
                   0f, 0f, 0f, 0f,
                   0f, 0f, 0f, 0f,
                   0f, 0f, 0f, 0f);
    }

    /**
     * Sets this to a matrix that first rotates, then translates.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToTransform (IVector3 translation, IQuaternion rotation) {
        return setToRotation(rotation).setTranslation(translation);
    }

    /**
     * Sets this to a matrix that first scales, then rotates, then translates.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToTransform (IVector3 translation, IQuaternion rotation, float scale) {
        return setToRotation(rotation).set(
            m00 * scale, m10 * scale, m20 * scale, translation.x(),
            m01 * scale, m11 * scale, m21 * scale, translation.y(),
            m02 * scale, m12 * scale, m22 * scale, translation.z(),
            0f, 0f, 0f, 1f);
}

/**
 * Sets this to a matrix that first scales, then rotates, then translates.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToTransform (IVector3 translation, IQuaternion rotation, IVector3 scale) {
        float sx = scale.x(), sy = scale.y(), sz = scale.z();
        return setToRotation(rotation).set(
            m00 * sx, m10 * sy, m20 * sz, translation.x(),
            m01 * sx, m11 * sy, m21 * sz, translation.y(),
            m02 * sx, m12 * sy, m22 * sz, translation.z(),
            0f, 0f, 0f, 1f);
    }

    /**
     * Sets this to a translation matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToTranslation (IVector3 translation) {
        return setToTranslation(translation.x(), translation.y(), translation.z());
    }

    /**
     * Sets this to a translation matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToTranslation (float x, float y, float z) {
        return set(1f, 0f, 0f, x,
                   0f, 1f, 0f, y,
                   0f, 0f, 1f, z,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets the translation component of this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setTranslation (IVector3 translation) {
        return setTranslation(translation.x(), translation.y(), translation.z());
    }

    /**
     * Sets the translation component of this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setTranslation (float x, float y, float z) {
        m30 = x;
        m31 = y;
        m32 = z;
        return this;
    }

    /**
     * Sets this to a rotation matrix that rotates one vector onto another.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToRotation (IVector3 from, IVector3 to) {
        float angle = from.angle(to);
        if (angle < MathUtil.EPSILON) {
            return setToIdentity();
        }
        if (angle <= FloatMath.PI - MathUtil.EPSILON) {
            return setToRotation(angle, from.cross(to).normalizeLocal());
        }
        // it's a 180 degree rotation; any axis orthogonal to the from vector will do
        Vector3 axis = new Vector3(0f, from.z(), -from.y());
        float length = axis.length();
        return setToRotation(FloatMath.PI, length < MathUtil.EPSILON ?
                             axis.set(-from.z(), 0f, from.x()).normalizeLocal() :
                             axis.multLocal(1f / length));
    }

    /**
     * Sets this to a rotation matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToRotation (float angle, IVector3 axis) {
        return setToRotation(angle, axis.x(), axis.y(), axis.z());
    }

    /**
     * Sets this to a rotation matrix. The formula comes from the OpenGL documentation for the
     * glRotatef function.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToRotation (float angle, float x, float y, float z) {
        float c = FloatMath.cos(angle), s = FloatMath.sin(angle), omc = 1f - c;
        float xs = x*s, ys = y*s, zs = z*s, xy = x*y, xz = x*z, yz = y*z;
        return set(x*x*omc + c, xy*omc - zs, xz*omc + ys, 0f,
                   xy*omc + zs, y*y*omc + c, yz*omc - xs, 0f,
                   xz*omc - ys, yz*omc + xs, z*z*omc + c, 0f,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets this to a rotation matrix. The formula comes from the
     * <a href="http://www.j3d.org/matrix_faq/matrfaq_latest.html">Matrix and Quaternion FAQ</a>.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToRotation (IQuaternion quat) {
        float x = quat.x(), y = quat.y(), z = quat.z(), w = quat.w();
        float xx = x*x, yy = y*y, zz = z*z;
        float xy = x*y, xz = x*z, xw = x*w;
        float yz = y*z, yw = y*w, zw = z*w;
        return set(1f - 2f*(yy + zz), 2f*(xy - zw), 2f*(xz + yw), 0f,
                   2f*(xy + zw), 1f - 2f*(xx + zz), 2f*(yz - xw), 0f,
                   2f*(xz - yw), 2f*(yz + xw), 1f - 2f*(xx + yy), 0f,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets this to a rotation plus scale matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToRotationScale (IMatrix3 rotScale) {
        return set(rotScale.m00(), rotScale.m01(), rotScale.m02(), 0f,
                   rotScale.m10(), rotScale.m11(), rotScale.m12(), 0f,
                   rotScale.m20(), rotScale.m21(), rotScale.m22(), 0f,
                   0, 0, 0, 1);
    }

    /**
     * Sets this to a scale matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToScale (IVector3 scale) {
        return setToScale(scale.x(), scale.y(), scale.z());
    }

    /**
     * Sets this to a uniform scale matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToScale (float s) {
        return setToScale(s, s, s);
    }

    /**
     * Sets this to a scale matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToScale (float x, float y, float z) {
        return set(x,  0f, 0f, 0f,
                   0f, y,  0f, 0f,
                   0f, 0f, z,  0f,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets this to a reflection across a plane intersecting the origin with the supplied normal.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToReflection (IVector3 normal) {
        return setToReflection(normal.x(), normal.y(), normal.z());
    }

    /**
     * Sets this to a reflection across a plane intersecting the origin with the supplied normal.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToReflection (float x, float y, float z) {
        float x2 = -2f*x, y2 = -2f*y, z2 = -2f*z;
        float xy2 = x2*y, xz2 = x2*z, yz2 = y2*z;
        return set(1f + x2*x, xy2, xz2, 0f,
                   xy2, 1f + y2*y, yz2, 0f,
                   xz2, yz2, 1f + z2*z, 0f,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets this to a reflection across the specified plane.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToReflection (IPlane plane) {
        return setToReflection(plane.normal(), plane.constant());
    }

    /**
     * Sets this to a reflection across the specified plane.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToReflection (IVector3 normal, float constant) {
        return setToReflection(normal.x(), normal.y(), normal.z(), constant);
    }

    /**
     * Sets this to a reflection across the specified plane.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToReflection (float x, float y, float z, float w) {
        float x2 = -2f*x, y2 = -2f*y, z2 = -2f*z;
        float xy2 = x2*y, xz2 = x2*z, yz2 = y2*z;
        float x2y2z2 = x*x + y*y + z*z;
        return set(1f + x2*x, xy2, xz2, x2*w*x2y2z2,
                   xy2, 1f + y2*y, yz2, y2*w*x2y2z2,
                   xz2, yz2, 1f + z2*z, z2*w*x2y2z2,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets this to a skew by the specified amount relative to the given plane.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToSkew (IPlane plane, IVector3 amount) {
        return setToSkew(plane.normal(), plane.constant(), amount);
    }

    /**
     * Sets this to a skew by the specified amount relative to the given plane.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToSkew (IVector3 normal, float constant, IVector3 amount) {
        return setToSkew(normal.x(), normal.y(), normal.z(), constant,
                         amount.x(), amount.y(), amount.z());
    }

    /**
     * Sets this to a skew by the specified amount relative to the given plane.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToSkew (float a, float b, float c, float d, float x, float y, float z) {
        return set(1f + a*x, b*x, c*x, d*x,
                   a*y, 1f + b*y, c*y, d*y,
                   a*z, b*z, 1f + c*z, d*z,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Sets this to a perspective projection matrix. The formula comes from the OpenGL
     * documentation for the gluPerspective function.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToPerspective (float fovy, float aspect, float near, float far) {
        float f = 1f / FloatMath.tan(fovy / 2f), dscale = 1f / (near - far);
        return set(f/aspect, 0f, 0f, 0f,
                   0f, f, 0f, 0f,
                   0f, 0f, (far+near) * dscale, 2f * far * near * dscale,
                   0f, 0f, -1f, 0f);
    }

    /**
     * Sets this to a perspective projection matrix. The formula comes from the OpenGL
     * documentation for the glFrustum function.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToFrustum (
        float left, float right, float bottom, float top, float near, float far) {
        return setToFrustum(left, right, bottom, top, near, far, Vector3.UNIT_Z);
    }

    /**
     * Sets this to a perspective projection matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToFrustum (
        float left, float right, float bottom, float top,
        float near, float far, IVector3 nearFarNormal) {
        float rrl = 1f / (right - left);
        float rtb = 1f / (top - bottom);
        float rnf = 1f / (near - far);
        float n2 = 2f * near;
        float s = (far + near) / (near*nearFarNormal.z() - far*nearFarNormal.z());
        return set(n2 * rrl, 0f, (right + left) * rrl, 0f,
                   0f, n2 * rtb, (top + bottom) * rtb, 0f,
                   s * nearFarNormal.x(), s * nearFarNormal.y(), (far + near) * rnf, n2 * far * rnf,
                   0f, 0f, -1f, 0f);
    }

    /**
     * Sets this to an orthographic projection matrix. The formula comes from the OpenGL
     * documentation for the glOrtho function.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToOrtho (
        float left, float right, float bottom, float top, float near, float far) {
        return setToOrtho(left, right, bottom, top, near, far, Vector3.UNIT_Z);
    }

    /**
     * Sets this to an orthographic projection matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 setToOrtho (
        float left, float right, float bottom, float top,
        float near, float far, IVector3 nearFarNormal) {
        float rlr = 1f / (left - right);
        float rbt = 1f / (bottom - top);
        float rnf = 1f / (near - far);
        float s = 2f / (near*nearFarNormal.z() - far*nearFarNormal.z());
        return set(-2f * rlr, 0f, 0f, (right + left) * rlr,
                   0f, -2f * rbt, 0f, (top + bottom) * rbt,
                   s * nearFarNormal.x(), s * nearFarNormal.y(), 2f * rnf, (far + near) * rnf,
                   0f, 0f, 0f, 1f);
    }

    /**
     * Copies the contents of another matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 set (IMatrix4 other) {
        return set(other.m00(), other.m10(), other.m20(), other.m30(),
                   other.m01(), other.m11(), other.m21(), other.m31(),
                   other.m02(), other.m12(), other.m22(), other.m32(),
                   other.m03(), other.m13(), other.m23(), other.m33());
    }

    /**
     * Copies the elements of a row-major array.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 set (float[] values) {
        return set(values[0], values[1], values[2], values[3],
                   values[4], values[5], values[6], values[7],
                   values[8], values[9], values[10], values[11],
                   values[12], values[13], values[14], values[15]);
    }

    /**
     * Sets the contents of this matrix from the supplied (column-major) buffer.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 set (FloatBuffer buf) {
        m00 = buf.get(); m01 = buf.get(); m02 = buf.get(); m03 = buf.get();
        m10 = buf.get(); m11 = buf.get(); m12 = buf.get(); m13 = buf.get();
        m20 = buf.get(); m21 = buf.get(); m22 = buf.get(); m23 = buf.get();
        m30 = buf.get(); m31 = buf.get(); m32 = buf.get(); m33 = buf.get();
        return this;
    }

    /**
     * Sets all of the matrix's components at once.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 set (
        float m00, float m10, float m20, float m30,
        float m01, float m11, float m21, float m31,
        float m02, float m12, float m22, float m32,
        float m03, float m13, float m23, float m33) {
        this.m00 = m00; this.m01 = m01; this.m02 = m02; this.m03 = m03;
        this.m10 = m10; this.m11 = m11; this.m12 = m12; this.m13 = m13;
        this.m20 = m20; this.m21 = m21; this.m22 = m22; this.m23 = m23;
        this.m30 = m30; this.m31 = m31; this.m32 = m32; this.m33 = m33;
        return this;
    }

    /**
     * Transposes this matrix in-place.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 transposeLocal () {
        return transpose(this);
    }

    /**
     * Multiplies this matrix in-place by another.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 multLocal (IMatrix4 other) {
        return mult(other, this);
    }

    /**
     * Multiplies this matrix in-place by another, treating the matricees as affine.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 multAffineLocal (IMatrix4 other) {
        return multAffine(other, this);
    }

    /**
     * Inverts this matrix in-place.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 invertLocal () {
        return invert(this);
    }

    /**
     * Inverts this matrix in-place as an affine matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 invertAffineLocal () {
        return invertAffine(this);
    }

    /**
     * Linearly interpolates between the this and the specified other matrix, placing the result in
     * this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 lerpLocal (IMatrix4 other, float t) {
        return lerp(other, t, this);
    }

    /**
     * Linearly interpolates between this and the specified other matrix (treating the matrices as
     * affine), placing the result in this matrix.
     *
     * @return a reference to this matrix, for chaining.
     */
    public Matrix4 lerpAffineLocal (IMatrix4 other, float t) {
        return lerpAffine(other, t, this);
    }

    @Override // from IMatrix4
    public float m00 () {
        return m00;
    }

    @Override // from IMatrix4
    public float m10 () {
        return m10;
    }

    @Override // from IMatrix4
    public float m20 () {
        return m20;
    }

    @Override // from IMatrix4
    public float m30 () {
        return m30;
    }

    @Override // from IMatrix4
    public float m01 () {
        return m01;
    }

    @Override // from IMatrix4
    public float m11 () {
        return m11;
    }

    @Override // from IMatrix4
    public float m21 () {
        return m21;
    }

    @Override // from IMatrix4
    public float m31 () {
        return m31;
    }

    @Override // from IMatrix4
    public float m02 () {
        return m02;
    }

    @Override // from IMatrix4
    public float m12 () {
        return m12;
    }

    @Override // from IMatrix4
    public float m22 () {
        return m22;
    }

    @Override // from IMatrix4
    public float m32 () {
        return m32;
    }

    @Override // from IMatrix4
    public float m03 () {
        return m03;
    }

    @Override // from IMatrix4
    public float m13 () {
        return m13;
    }

    @Override // from IMatrix4
    public float m23 () {
        return m23;
    }

    @Override // from IMatrix4
    public float m33 () {
        return m33;
    }

    @Override // from IMatrix4
    public Matrix4 transpose () {
        return transpose(new Matrix4());
    }

    @Override // from IMatrix4
    public Matrix4 transpose (Matrix4 result) {
        return result.set(m00, m01, m02, m03,
                          m10, m11, m12, m13,
                          m20, m21, m22, m23,
                          m30, m31, m32, m33);
    }

    @Override // from IMatrix4
    public Matrix4 mult (IMatrix4 other) {
        return mult(other, new Matrix4());
    }

    @Override // from IMatrix4
    public Matrix4 mult (IMatrix4 other, Matrix4 result) {
        float m00 = this.m00, m10 = this.m10, m20 = this.m20, m30 = this.m30;
        float m01 = this.m01, m11 = this.m11, m21 = this.m21, m31 = this.m31;
        float m02 = this.m02, m12 = this.m12, m22 = this.m22, m32 = this.m32;
        float m03 = this.m03, m13 = this.m13, m23 = this.m23, m33 = this.m33;
        float om00 = other.m00(), om10 = other.m10(), om20 = other.m20(), om30 = other.m30();
        float om01 = other.m01(), om11 = other.m11(), om21 = other.m21(), om31 = other.m31();
        float om02 = other.m02(), om12 = other.m12(), om22 = other.m22(), om32 = other.m32();
        float om03 = other.m03(), om13 = other.m13(), om23 = other.m23(), om33 = other.m33();
        return result.set(m00*om00 + m10*om01 + m20*om02 + m30*om03,
                          m00*om10 + m10*om11 + m20*om12 + m30*om13,
                          m00*om20 + m10*om21 + m20*om22 + m30*om23,
                          m00*om30 + m10*om31 + m20*om32 + m30*om33,

                          m01*om00 + m11*om01 + m21*om02 + m31*om03,
                          m01*om10 + m11*om11 + m21*om12 + m31*om13,
                          m01*om20 + m11*om21 + m21*om22 + m31*om23,
                          m01*om30 + m11*om31 + m21*om32 + m31*om33,

                          m02*om00 + m12*om01 + m22*om02 + m32*om03,
                          m02*om10 + m12*om11 + m22*om12 + m32*om13,
                          m02*om20 + m12*om21 + m22*om22 + m32*om23,
                          m02*om30 + m12*om31 + m22*om32 + m32*om33,

                          m03*om00 + m13*om01 + m23*om02 + m33*om03,
                          m03*om10 + m13*om11 + m23*om12 + m33*om13,
                          m03*om20 + m13*om21 + m23*om22 + m33*om23,
                          m03*om30 + m13*om31 + m23*om32 + m33*om33);
    }

    @Override // from IMatrix4
    public boolean isAffine () {
        return (m03 == 0f && m13 == 0f && m23 == 0f && m33 == 1f);
    }

    @Override // from IMatrix4
    public boolean isMirrored () {
        return m00*(m11*m22 - m12*m21) + m01*(m12*m20 - m10*m22) + m02*(m10*m21 - m11*m20) < 0f;
    }

    @Override // from IMatrix4
    public Matrix4 multAffine (IMatrix4 other) {
        return multAffine(other, new Matrix4());
    }

    @Override // from IMatrix4
    public Matrix4 multAffine (IMatrix4 other, Matrix4 result) {
        float m00 = this.m00, m10 = this.m10, m20 = this.m20, m30 = this.m30;
        float m01 = this.m01, m11 = this.m11, m21 = this.m21, m31 = this.m31;
        float m02 = this.m02, m12 = this.m12, m22 = this.m22, m32 = this.m32;
        float om00 = other.m00(), om10 = other.m10(), om20 = other.m20(), om30 = other.m30();
        float om01 = other.m01(), om11 = other.m11(), om21 = other.m21(), om31 = other.m31();
        float om02 = other.m02(), om12 = other.m12(), om22 = other.m22(), om32 = other.m32();
        return result.set(m00*om00 + m10*om01 + m20*om02,
                          m00*om10 + m10*om11 + m20*om12,
                          m00*om20 + m10*om21 + m20*om22,
                          m00*om30 + m10*om31 + m20*om32 + m30,

                          m01*om00 + m11*om01 + m21*om02,
                          m01*om10 + m11*om11 + m21*om12,
                          m01*om20 + m11*om21 + m21*om22,
                          m01*om30 + m11*om31 + m21*om32 + m31,

                          m02*om00 + m12*om01 + m22*om02,
                          m02*om10 + m12*om11 + m22*om12,
                          m02*om20 + m12*om21 + m22*om22,
                          m02*om30 + m12*om31 + m22*om32 + m32,

                          0f, 0f, 0f, 1f);
    }

    @Override // from IMatrix4
    public Matrix4 invert () {
        return invert(new Matrix4());
    }

    /**
     * {@inheritDoc} This code is based on the examples in the
     * <a href="http://www.j3d.org/matrix_faq/matrfaq_latest.html">Matrix and Quaternion FAQ</a>.
     */
    @Override // from IMatrix4
    public Matrix4 invert (Matrix4 result) throws SingularMatrixException {
        float m00 = this.m00, m10 = this.m10, m20 = this.m20, m30 = this.m30;
        float m01 = this.m01, m11 = this.m11, m21 = this.m21, m31 = this.m31;
        float m02 = this.m02, m12 = this.m12, m22 = this.m22, m32 = this.m32;
        float m03 = this.m03, m13 = this.m13, m23 = this.m23, m33 = this.m33;
        // compute the determinant, storing the subdeterminants for later use
        float sd00 = m11*(m22*m33 - m23*m32) + m21*(m13*m32 - m12*m33) + m31*(m12*m23 - m13*m22);
        float sd10 = m01*(m22*m33 - m23*m32) + m21*(m03*m32 - m02*m33) + m31*(m02*m23 - m03*m22);
        float sd20 = m01*(m12*m33 - m13*m32) + m11*(m03*m32 - m02*m33) + m31*(m02*m13 - m03*m12);
        float sd30 = m01*(m12*m23 - m13*m22) + m11*(m03*m22 - m02*m23) + m21*(m02*m13 - m03*m12);
        float det = m00*sd00 + m20*sd20 - m10*sd10 - m30*sd30;
        if (Math.abs(det) == 0f) {
            // determinant is zero; matrix is not invertible
            throw new SingularMatrixException(this.toString());
        }
        float rdet = 1f / det;
        return result.set(
            +sd00 * rdet,
            -(m10*(m22*m33 - m23*m32) + m20*(m13*m32 - m12*m33) + m30*(m12*m23 - m13*m22)) * rdet,
            +(m10*(m21*m33 - m23*m31) + m20*(m13*m31 - m11*m33) + m30*(m11*m23 - m13*m21)) * rdet,
            -(m10*(m21*m32 - m22*m31) + m20*(m12*m31 - m11*m32) + m30*(m11*m22 - m12*m21)) * rdet,

            -sd10 * rdet,
            +(m00*(m22*m33 - m23*m32) + m20*(m03*m32 - m02*m33) + m30*(m02*m23 - m03*m22)) * rdet,
            -(m00*(m21*m33 - m23*m31) + m20*(m03*m31 - m01*m33) + m30*(m01*m23 - m03*m21)) * rdet,
            +(m00*(m21*m32 - m22*m31) + m20*(m02*m31 - m01*m32) + m30*(m01*m22 - m02*m21)) * rdet,

            +sd20 * rdet,
            -(m00*(m12*m33 - m13*m32) + m10*(m03*m32 - m02*m33) + m30*(m02*m13 - m03*m12)) * rdet,
            +(m00*(m11*m33 - m13*m31) + m10*(m03*m31 - m01*m33) + m30*(m01*m13 - m03*m11)) * rdet,
            -(m00*(m11*m32 - m12*m31) + m10*(m02*m31 - m01*m32) + m30*(m01*m12 - m02*m11)) * rdet,

            -sd30 * rdet,
            +(m00*(m12*m23 - m13*m22) + m10*(m03*m22 - m02*m23) + m20*(m02*m13 - m03*m12)) * rdet,
            -(m00*(m11*m23 - m13*m21) + m10*(m03*m21 - m01*m23) + m20*(m01*m13 - m03*m11)) * rdet,
            +(m00*(m11*m22 - m12*m21) + m10*(m02*m21 - m01*m22) + m20*(m01*m12 - m02*m11)) * rdet);
    }

    @Override // from IMatrix4
    public Matrix4 invertAffine () {
        return invertAffine(new Matrix4());
    }

    @Override // from IMatrix4
    public Matrix4 invertAffine (Matrix4 result) throws SingularMatrixException {
        float m00 = this.m00, m10 = this.m10, m20 = this.m20, m30 = this.m30;
        float m01 = this.m01, m11 = this.m11, m21 = this.m21, m31 = this.m31;
        float m02 = this.m02, m12 = this.m12, m22 = this.m22, m32 = this.m32;
        // compute the determinant, storing the subdeterminants for later use
        float sd00 = m11*m22 - m21*m12;
        float sd10 = m01*m22 - m21*m02;
        float sd20 = m01*m12 - m11*m02;
        float det = m00*sd00 + m20*sd20 - m10*sd10;
        if (Math.abs(det) == 0f) {
            // determinant is zero; matrix is not invertible
            throw new SingularMatrixException(this.toString());
        }
        float rdet = 1f / det;
        return result.set(
            +sd00 * rdet,
            -(m10*m22 - m20*m12) * rdet,
            +(m10*m21 - m20*m11) * rdet,
            -(m10*(m21*m32 - m22*m31) + m20*(m12*m31 - m11*m32) + m30*sd00) * rdet,

            -sd10 * rdet,
            +(m00*m22 - m20*m02) * rdet,
            -(m00*m21 - m20*m01) * rdet,
            +(m00*(m21*m32 - m22*m31) + m20*(m02*m31 - m01*m32) + m30*sd10) * rdet,

            +sd20 * rdet,
            -(m00*m12 - m10*m02) * rdet,
            +(m00*m11 - m10*m01) * rdet,
            -(m00*(m11*m32 - m12*m31) + m10*(m02*m31 - m01*m32) + m30*sd20) * rdet,

            0f, 0f, 0f, 1f);
    }

    @Override // from IMatrix4
    public Matrix4 lerp (IMatrix4 other, float t) {
        return lerp(other, t, new Matrix4());
    }

    @Override // from IMatrix4
    public Matrix4 lerp (IMatrix4 other, float t, Matrix4 result) {
        float m00 = this.m00, m10 = this.m10, m20 = this.m20, m30 = this.m30;
        float m01 = this.m01, m11 = this.m11, m21 = this.m21, m31 = this.m31;
        float m02 = this.m02, m12 = this.m12, m22 = this.m22, m32 = this.m32;
        float m03 = this.m03, m13 = this.m13, m23 = this.m23, m33 = this.m33;
        return result.set(m00 + t*(other.m00() - m00),
                          m10 + t*(other.m10() - m10),
                          m20 + t*(other.m20() - m20),
                          m30 + t*(other.m30() - m30),

                          m01 + t*(other.m01() - m01),
                          m11 + t*(other.m11() - m11),
                          m21 + t*(other.m21() - m21),
                          m31 + t*(other.m31() - m31),

                          m02 + t*(other.m02() - m02),
                          m12 + t*(other.m12() - m12),
                          m22 + t*(other.m22() - m22),
                          m32 + t*(other.m32() - m32),

                          m03 + t*(other.m03() - m03),
                          m13 + t*(other.m13() - m13),
                          m23 + t*(other.m23() - m23),
                          m33 + t*(other.m33() - m33));
    }

    @Override // from IMatrix4
    public Matrix4 lerpAffine (IMatrix4 other, float t) {
        return lerpAffine(other, t, new Matrix4());
    }

    @Override // from IMatrix4
    public Matrix4 lerpAffine (IMatrix4 other, float t, Matrix4 result) {
        float m00 = this.m00, m10 = this.m10, m20 = this.m20, m30 = this.m30;
        float m01 = this.m01, m11 = this.m11, m21 = this.m21, m31 = this.m31;
        float m02 = this.m02, m12 = this.m12, m22 = this.m22, m32 = this.m32;
        return result.set(m00 + t*(other.m00() - m00),
                          m10 + t*(other.m10() - m10),
                          m20 + t*(other.m20() - m20),
                          m30 + t*(other.m30() - m30),

                          m01 + t*(other.m01() - m01),
                          m11 + t*(other.m11() - m11),
                          m21 + t*(other.m21() - m21),
                          m31 + t*(other.m31() - m31),

                          m02 + t*(other.m02() - m02),
                          m12 + t*(other.m12() - m12),
                          m22 + t*(other.m22() - m22),
                          m32 + t*(other.m32() - m32),

                          0f, 0f, 0f, 1f);
    }

    @Override // from IMatrix4
    public FloatBuffer get (FloatBuffer buf) {
        buf.put(m00).put(m01).put(m02).put(m03);
        buf.put(m10).put(m11).put(m12).put(m13);
        buf.put(m20).put(m21).put(m22).put(m23);
        buf.put(m30).put(m31).put(m32).put(m33);
        return buf;
    }

    @Override // from IMatrix4
    public Vector3 projectPointLocal (Vector3 point) {
        return projectPoint(point, point);
    }

    @Override // from IMatrix4
    public Vector3 projectPoint (IVector3 point) {
        return projectPoint(point, new Vector3());
    }

    @Override // from IMatrix4
    public Vector3 projectPoint (IVector3 point, Vector3 result) {
        float px = point.x(), py = point.y(), pz = point.z();
        float rw = 1f / (m03*px + m13*py + m23*pz + m33);
        return result.set((m00*px + m10*py + m20*pz + m30) * rw,
                          (m01*px + m11*py + m21*pz + m31) * rw,
                          (m02*px + m12*py + m22*pz + m32) * rw);
    }

    @Override // from IMatrix4
    public Vector3 transformPointLocal (Vector3 point) {
        return transformPoint(point, point);
    }

    @Override // from IMatrix4
    public Vector3 transformPoint (IVector3 point) {
        return transformPoint(point, new Vector3());
    }

    @Override // from IMatrix4
    public Vector3 transformPoint (IVector3 point, Vector3 result) {
        float px = point.x(), py = point.y(), pz = point.z();
        return result.set(m00*px + m10*py + m20*pz + m30,
                          m01*px + m11*py + m21*pz + m31,
                          m02*px + m12*py + m22*pz + m32);
    }

    @Override // from IMatrix4
    public float transformPointZ (IVector3 point) {
        return m02*point.x() + m12*point.y() + m22*point.z() + m32;
    }

    @Override // from IMatrix4
    public Vector3 transformVectorLocal (Vector3 vector) {
        return transformVector(vector, vector);
    }

    @Override // from IMatrix4
    public Vector3 transformVector (IVector3 vector) {
        return transformVector(vector, new Vector3());
    }

    @Override // from IMatrix4
    public Vector3 transformVector (IVector3 vector, Vector3 result) {
        float vx = vector.x(), vy = vector.y(), vz = vector.z();
        return result.set(m00*vx + m10*vy + m20*vz,
                          m01*vx + m11*vy + m21*vz,
                          m02*vx + m12*vy + m22*vz);
    }

    @Override // from IMatrix4
    public Vector4 transform (IVector4 vector) {
        return transform(vector, new Vector4());
    }

    @Override // from IMatrix4
    public Vector4 transform (IVector4 vector, Vector4 result) {
        float vx = vector.x(), vy = vector.y(), vz = vector.z(), vw = vector.w();
        return result.set(m00*vx + m10*vy + m20*vz + m30*vw,
                          m01*vx + m11*vy + m21*vz + m31*vw,
                          m02*vx + m12*vy + m22*vz + m32*vw,
                          m03*vx + m13*vy + m23*vz + m33*vw);
    }

    @Override // from IMatrix4
    public Quaternion extractRotation () {
        return extractRotation(new Quaternion());
    }

    /**
     * {@inheritDoc} This uses the iterative polar decomposition algorithm described by
     * <a href="http://www.cs.wisc.edu/graphics/Courses/838-s2002/Papers/polar-decomp.pdf">Ken
     * Shoemake</a>.
     */
    @Override // from IMatrix4
    public Quaternion extractRotation (Quaternion result) throws SingularMatrixException {
        // start with the contents of the upper 3x3 portion of the matrix
        float n00 = this.m00, n10 = this.m10, n20 = this.m20;
        float n01 = this.m01, n11 = this.m11, n21 = this.m21;
        float n02 = this.m02, n12 = this.m12, n22 = this.m22;
        for (int ii = 0; ii < 10; ii++) {
            // store the results of the previous iteration
            float o00 = n00, o10 = n10, o20 = n20;
            float o01 = n01, o11 = n11, o21 = n21;
            float o02 = n02, o12 = n12, o22 = n22;

            // compute average of the matrix with its inverse transpose
            float sd00 = o11*o22 - o21*o12;
            float sd10 = o01*o22 - o21*o02;
            float sd20 = o01*o12 - o11*o02;
            float det = o00*sd00 + o20*sd20 - o10*sd10;
            if (Math.abs(det) == 0f) {
                // determinant is zero; matrix is not invertible
                throw new SingularMatrixException(this.toString());
            }
            float hrdet = 0.5f / det;
            n00 = +sd00 * hrdet + o00*0.5f;
            n10 = -sd10 * hrdet + o10*0.5f;
            n20 = +sd20 * hrdet + o20*0.5f;

            n01 = -(o10*o22 - o20*o12) * hrdet + o01*0.5f;
            n11 = +(o00*o22 - o20*o02) * hrdet + o11*0.5f;
            n21 = -(o00*o12 - o10*o02) * hrdet + o21*0.5f;

            n02 = +(o10*o21 - o20*o11) * hrdet + o02*0.5f;
            n12 = -(o00*o21 - o20*o01) * hrdet + o12*0.5f;
            n22 = +(o00*o11 - o10*o01) * hrdet + o22*0.5f;

            // compute the difference; if it's small enough, we're done
            float d00 = n00 - o00, d10 = n10 - o10, d20 = n20 - o20;
            float d01 = n01 - o01, d11 = n11 - o11, d21 = n21 - o21;
            float d02 = n02 - o02, d12 = n12 - o12, d22 = n22 - o22;
            if (d00*d00 + d10*d10 + d20*d20 + d01*d01 + d11*d11 + d21*d21 +
                d02*d02 + d12*d12 + d22*d22 < MathUtil.EPSILON) {
                break;
            }
        }
        // now that we have a nice orthogonal matrix, we can extract the rotation quaternion
        // using the method described in http://en.wikipedia.org/wiki/Rotation_matrix#Conversions
        float x2 = Math.abs(1f + n00 - n11 - n22);
        float y2 = Math.abs(1f - n00 + n11 - n22);
        float z2 = Math.abs(1f - n00 - n11 + n22);
        float w2 = Math.abs(1f + n00 + n11 + n22);
        result.set(
            0.5f * FloatMath.sqrt(x2) * (n12 >= n21 ? +1f : -1f),
            0.5f * FloatMath.sqrt(y2) * (n20 >= n02 ? +1f : -1f),
            0.5f * FloatMath.sqrt(z2) * (n01 >= n10 ? +1f : -1f),
            0.5f * FloatMath.sqrt(w2));
        return result;
    }

    @Override // from IMatrix4
    public Matrix3 extractRotationScale (Matrix3 result) {
        return result.set(m00, m01, m02,
                          m10, m11, m12,
                          m20, m21, m22);
    }

    @Override // from IMatrix4
    public Vector3 extractScale () {
        return extractScale(new Vector3());
    }

    @Override // from IMatrix4
    public Vector3 extractScale (Vector3 result) {
        return result.set(FloatMath.sqrt(m00*m00 + m01*m01 + m02*m02),
                          FloatMath.sqrt(m10*m10 + m11*m11 + m12*m12),
                          FloatMath.sqrt(m20*m20 + m21*m21 + m22*m22));
    }

    @Override // from IMatrix4
    public float approximateUniformScale () {
        return FloatMath.cbrt(m00*(m11*m22 - m12*m21) +
                              m01*(m12*m20 - m10*m22) +
                              m02*(m10*m21 - m11*m20));
    }

    @Override // from IMatrix4
    public boolean epsilonEquals (IMatrix4 other, float epsilon) {
        return (Math.abs(m00 - other.m00()) < epsilon &&
                Math.abs(m10 - other.m10()) < epsilon &&
                Math.abs(m20 - other.m20()) < epsilon &&
                Math.abs(m30 - other.m30()) < epsilon &&

                Math.abs(m01 - other.m01()) < epsilon &&
                Math.abs(m11 - other.m11()) < epsilon &&
                Math.abs(m21 - other.m21()) < epsilon &&
                Math.abs(m31 - other.m31()) < epsilon &&

                Math.abs(m02 - other.m02()) < epsilon &&
                Math.abs(m12 - other.m12()) < epsilon &&
                Math.abs(m22 - other.m22()) < epsilon &&
                Math.abs(m32 - other.m32()) < epsilon &&

                Math.abs(m03 - other.m03()) < epsilon &&
                Math.abs(m13 - other.m13()) < epsilon &&
                Math.abs(m23 - other.m23()) < epsilon &&
                Math.abs(m33 - other.m33()) < epsilon);
    }

    @Override
    public String toString () {
        return ("[[" + m00 + ", " + m10 + ", " + m20 + ", " + m30 + "], " +
                "["  + m01 + ", " + m11 + ", " + m21 + ", " + m31 + "], " +
                "["  + m02 + ", " + m12 + ", " + m22 + ", " + m32 + "], " +
                "["  + m03 + ", " + m13 + ", " + m23 + ", " + m33 + "]]");
    }

    @Override
    public int hashCode () {
        return Platform.hashCode(m00) ^ Platform.hashCode(m10) ^
                Platform.hashCode(m20) ^ Platform.hashCode(m30) ^
            Platform.hashCode(m01) ^ Platform.hashCode(m11) ^
                Platform.hashCode(m21) ^ Platform.hashCode(m31) ^
            Platform.hashCode(m02) ^ Platform.hashCode(m12) ^
                Platform.hashCode(m22) ^ Platform.hashCode(m32) ^
            Platform.hashCode(m03) ^ Platform.hashCode(m13) ^
                Platform.hashCode(m23) ^ Platform.hashCode(m33);
    }

    @Override
    public boolean equals (Object other) {
        if (!(other instanceof Matrix4)) {
            return false;
        }
        Matrix4 omat = (Matrix4)other;
        return (m00 == omat.m00 && m10 == omat.m10 && m20 == omat.m20 && m30 == omat.m30 &&
                m01 == omat.m01 && m11 == omat.m11 && m21 == omat.m21 && m31 == omat.m31 &&
                m02 == omat.m02 && m12 == omat.m12 && m22 == omat.m22 && m32 == omat.m32 &&
                m03 == omat.m03 && m13 == omat.m13 && m23 == omat.m23 && m33 == omat.m33);
    }
}
