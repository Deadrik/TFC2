//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

import java.io.Serializable;
import java.util.Random;

import pythagoras.util.Platform;

/**
 * A unit quaternion. Many of the formulas come from the
 * <a href="http://www.j3d.org/matrix_faq/matrfaq_latest.html">Matrix and Quaternion FAQ</a>.
 */
public class Quaternion implements IQuaternion, Serializable
{
    /** The identity quaternion. */
    public static final IQuaternion IDENTITY = new Quaternion(0f, 0f, 0f, 1f);

    /** The components of the quaternion. */
    public float x, y, z, w;

    /**
     * Creates a quaternion from four components.
     */
    public Quaternion (float x, float y, float z, float w) {
        set(x, y, z, w);
    }

    /**
     * Creates a quaternion from an array of values.
     */
    public Quaternion (float[] values) {
        set(values);
    }

    /**
     * Copy constructor.
     */
    public Quaternion (IQuaternion other) {
        set(other);
    }

    /**
     * Creates an identity quaternion.
     */
    public Quaternion () {
        set(0f, 0f, 0f, 1f);
    }

    /**
     * Copies the elements of another quaternion.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion set (IQuaternion other) {
        return set(other.x(), other.y(), other.z(), other.w());
    }

    /**
     * Copies the elements of an array.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion set (float[] values) {
        return set(values[0], values[1], values[2], values[3]);
    }

    /**
     * Sets all of the elements of the quaternion.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion set (float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    /**
     * Sets this quaternion to the rotation of the first normalized vector onto the second.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion fromVectors (IVector3 from, IVector3 to) {
        float angle = from.angle(to);
        if (angle < MathUtil.EPSILON) {
            return set(IDENTITY);
        }
        if (angle <= FloatMath.PI - MathUtil.EPSILON) {
            return fromAngleAxis(angle, from.cross(to).normalizeLocal());
        }
        // it's a 180 degree rotation; any axis orthogonal to the from vector will do
        Vector3 axis = new Vector3(0f, from.z(), -from.y());
        float length = axis.length();
        return fromAngleAxis(FloatMath.PI, length < MathUtil.EPSILON ?
                             axis.set(-from.z(), 0f, from.x()).normalizeLocal() :
                             axis.multLocal(1f / length));
    }

    /**
     * Sets this quaternion to the rotation of (0, 0, -1) onto the supplied normalized vector.
     *
     * @return a reference to the quaternion, for chaining.
     */
    public Quaternion fromVectorFromNegativeZ (IVector3 to) {
        return fromVectorFromNegativeZ(to.x(), to.y(), to.z());
    }

    /**
     * Sets this quaternion to the rotation of (0, 0, -1) onto the supplied normalized vector.
     *
     * @return a reference to the quaternion, for chaining.
     */
    public Quaternion fromVectorFromNegativeZ (float tx, float ty, float tz) {
        float angle = FloatMath.acos(-tz);
        if (angle < MathUtil.EPSILON) {
            return set(IDENTITY);
        }
        if (angle > FloatMath.PI - MathUtil.EPSILON) {
            return set(0f, 1f, 0f, 0f); // 180 degrees about y
        }
        float len = FloatMath.hypot(tx, ty);
        return fromAngleAxis(angle, ty/len, -tx/len, 0f);
    }

    /**
     * Sets this quaternion to one that rotates onto the given unit axes.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion fromAxes (IVector3 nx, IVector3 ny, IVector3 nz) {
        float nxx = nx.x(), nyy = ny.y(), nzz = nz.z();
        float x2 = (1f + nxx - nyy - nzz)/4f;
        float y2 = (1f - nxx + nyy - nzz)/4f;
        float z2 = (1f - nxx - nyy + nzz)/4f;
        float w2 = (1f - x2 - y2 - z2);
        return set(FloatMath.sqrt(x2) * (ny.z() >= nz.y() ? +1f : -1f),
                   FloatMath.sqrt(y2) * (nz.x() >= nx.z() ? +1f : -1f),
                   FloatMath.sqrt(z2) * (nx.y() >= ny.x() ? +1f : -1f),
                   FloatMath.sqrt(w2));
    }

    /**
     * Sets this quaternion to the rotation described by the given angle and normalized
     * axis.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion fromAngleAxis (float angle, IVector3 axis) {
        return fromAngleAxis(angle, axis.x(), axis.y(), axis.z());
    }

    /**
     * Sets this quaternion to the rotation described by the given angle and normalized
     * axis.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion fromAngleAxis (float angle, float x, float y, float z) {
        float sina = FloatMath.sin(angle / 2f);
        return set(x*sina, y*sina, z*sina, FloatMath.cos(angle / 2f));
    }

    /**
     * Sets this to a random rotation obtained from a completely uniform distribution.
     */
    public Quaternion randomize (Random rand) {
        // pick angles according to the surface area distribution
        return fromAngles(MathUtil.lerp(-FloatMath.PI, +FloatMath.PI, rand.nextFloat()),
                          FloatMath.asin(MathUtil.lerp(-1f, +1f, rand.nextFloat())),
                          MathUtil.lerp(-FloatMath.PI, +FloatMath.PI, rand.nextFloat()));
    }

    /**
     * Sets this quaternion to one that first rotates about x by the specified number of radians,
     * then rotates about z by the specified number of radians.
     */
    public Quaternion fromAnglesXZ (float x, float z) {
        float hx = x * 0.5f, hz = z * 0.5f;
        float sx = FloatMath.sin(hx), cx = FloatMath.cos(hx);
        float sz = FloatMath.sin(hz), cz = FloatMath.cos(hz);
        return set(cz*sx, sz*sx, sz*cx, cz*cx);
    }

    /**
     * Sets this quaternion to one that first rotates about x by the specified number of radians,
     * then rotates about y by the specified number of radians.
     */
    public Quaternion fromAnglesXY (float x, float y) {
        float hx = x * 0.5f, hy = y * 0.5f;
        float sx = FloatMath.sin(hx), cx = FloatMath.cos(hx);
        float sy = FloatMath.sin(hy), cy = FloatMath.cos(hy);
        return set(cy*sx, sy*cx, -sy*sx, cy*cx);
    }

    /**
     * Sets this quaternion to one that first rotates about x by the specified number of radians,
     * then rotates about y, then about z.
     */
    public Quaternion fromAngles (Vector3 angles) {
        return fromAngles(angles.x, angles.y, angles.z);
    }

    /**
     * Sets this quaternion to one that first rotates about x by the specified number of radians,
     * then rotates about y, then about z.
     */
    public Quaternion fromAngles (float x, float y, float z) {
        // TODO: it may be more convenient to define the angles in the opposite order (first z,
        // then y, then x)
        float hx = x * 0.5f, hy = y * 0.5f, hz = z * 0.5f;
        float sz = FloatMath.sin(hz), cz = FloatMath.cos(hz);
        float sy = FloatMath.sin(hy), cy = FloatMath.cos(hy);
        float sx = FloatMath.sin(hx), cx = FloatMath.cos(hx);
        float szsy = sz*sy, czsy = cz*sy, szcy = sz*cy, czcy = cz*cy;
        return set(
            czcy*sx - szsy*cx,
            czsy*cx + szcy*sx,
            szcy*cx - czsy*sx,
            czcy*cx + szsy*sx);
    }

    /**
     * Normalizes this quaternion in-place.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion normalizeLocal () {
        return normalize(this);
    }

    /**
     * Inverts this quaternion in-place.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion invertLocal () {
        return invert(this);
    }

    /**
     * Multiplies this quaternion in-place by another.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion multLocal (IQuaternion other) {
        return mult(other, this);
    }

    /**
     * Interpolates in-place between this and the specified other quaternion.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion slerpLocal (IQuaternion other, float t) {
        return slerp(other, t, this);
    }

    /**
     * Transforms a vector in-place by this quaternion.
     *
     * @return a reference to the vector, for chaining.
     */
    public Vector3 transformLocal (Vector3 vector) {
        return transform(vector, vector);
    }

    /**
     * Integrates in-place the provided angular velocity over the specified timestep.
     *
     * @return a reference to this quaternion, for chaining.
     */
    public Quaternion integrateLocal (IVector3 velocity, float t) {
        return integrate(velocity, t, this);
    }

    @Override // from IQuaternion
    public float x () {
        return x;
    }

    @Override // from IQuaternion
    public float y () {
        return y;
    }

    @Override // from IQuaternion
    public float z () {
        return z;
    }

    @Override // from IQuaternion
    public float w () {
        return w;
    }

    @Override // from IQuaternion
    public void get (float[] values) {
        values[0] = x;
        values[1] = y;
        values[2] = z;
        values[3] = w;
    }

    @Override // from IQuaternion
    public boolean hasNaN () {
        return Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z) || Float.isNaN(w);
    }

    @Override // from IQuaternion
    public Vector3 toAngles (Vector3 result) {
        float sy = 2f*(y*w - x*z);
        if (sy < 1f - MathUtil.EPSILON) {
            if (sy > -1 + MathUtil.EPSILON) {
                return result.set(FloatMath.atan2(y*z + x*w, 0.5f - (x*x + y*y)),
                                  FloatMath.asin(sy),
                                  FloatMath.atan2(x*y + z*w, 0.5f - (y*y + z*z)));
            } else {
                // not a unique solution; x + z = atan2(-m21, m11)
                return result.set(0f,
                                  -MathUtil.HALF_PI,
                                  FloatMath.atan2(x*w - y*z, 0.5f - (x*x + z*z)));
            }
        } else {
            // not a unique solution; x - z = atan2(-m21, m11)
            return result.set(0f,
                              MathUtil.HALF_PI,
                              -FloatMath.atan2(x*w - y*z, 0.5f - (x*x + z*z)));
        }
    }

    @Override // from IQuaternion
    public Vector3 toAngles () {
        return toAngles(new Vector3());
    }

    @Override // from IQuaternion
    public Quaternion normalize () {
        return normalize(new Quaternion());
    }

    @Override // from IQuaternion
    public Quaternion normalize (Quaternion result) {
        float rlen = 1f / FloatMath.sqrt(x*x + y*y + z*z + w*w);
        return result.set(x*rlen, y*rlen, z*rlen, w*rlen);
    }

    @Override // from IQuaternion
    public Quaternion invert () {
        return invert(new Quaternion());
    }

    @Override // from IQuaternion
    public Quaternion invert (Quaternion result) {
        return result.set(-x, -y, -z, w);
    }

    @Override // from IQuaternion
    public Quaternion mult (IQuaternion other) {
        return mult(other, new Quaternion());
    }

    @Override // from IQuaternion
    public Quaternion mult (IQuaternion other, Quaternion result) {
        float ox = other.x(), oy = other.y(), oz = other.z(), ow = other.w();
        return result.set(w*ox + x*ow + y*oz - z*oy,
                          w*oy + y*ow + z*ox - x*oz,
                          w*oz + z*ow + x*oy - y*ox,
                          w*ow - x*ox - y*oy - z*oz);
    }

    @Override // from IQuaternion
    public Quaternion slerp (IQuaternion other, float t) {
        return slerp(other, t, new Quaternion());
    }

    @Override // from IQuaternion
    public Quaternion slerp (IQuaternion other, float t, Quaternion result) {
        float ox = other.x(), oy = other.y(), oz = other.z(), ow = other.w();
        float cosa = x*ox + y*oy + z*oz + w*ow, s0, s1;

        // adjust signs if necessary
        if (cosa < 0f) {
            cosa = -cosa;
            ox = -ox;
            oy = -oy;
            oz = -oz;
            ow = -ow;
        }

        // calculate coefficients; if the angle is too close to zero, we must fall back
        // to linear interpolation
        if ((1f - cosa) > MathUtil.EPSILON) {
            float angle = FloatMath.acos(cosa), sina = FloatMath.sin(angle);
            s0 = FloatMath.sin((1f - t) * angle) / sina;
            s1 = FloatMath.sin(t * angle) / sina;
        } else {
            s0 = 1f - t;
            s1 = t;
        }

        return result.set(s0*x + s1*ox, s0*y + s1*oy, s0*z + s1*oz, s0*w + s1*ow);
    }

    @Override // from IQuaternion
    public Vector3 transform (IVector3 vector) {
        return transform(vector, new Vector3());
    }

    @Override // from IQuaternion
    public Vector3 transform (IVector3 vector, Vector3 result) {
        float xx = x*x, yy = y*y, zz = z*z;
        float xy = x*y, xz = x*z, xw = x*w;
        float yz = y*z, yw = y*w, zw = z*w;
        float vx = vector.x(), vy = vector.y(), vz = vector.z();
        float vx2 = vx*2f, vy2 = vy*2f, vz2 = vz*2f;
        return result.set(vx + vy2*(xy - zw) + vz2*(xz + yw) - vx2*(yy + zz),
                          vy + vx2*(xy + zw) + vz2*(yz - xw) - vy2*(xx + zz),
                          vz + vx2*(xz - yw) + vy2*(yz + xw) - vz2*(xx + yy));
    }

    @Override // from IQuaternion
    public Vector3 transformUnitX (Vector3 result) {
        return result.set(1f - 2f*(y*y + z*z), 2f*(x*y + z*w), 2f*(x*z - y*w));
    }

    @Override // from IQuaternion
    public Vector3 transformUnitY (Vector3 result) {
        return result.set(2f*(x*y - z*w), 1f - 2f*(x*x + z*z), 2f*(y*z + x*w));
    }

    @Override // from IQuaternion
    public Vector3 transformUnitZ (Vector3 result) {
        return result.set(2f*(x*z + y*w), 2f*(y*z - x*w), 1f - 2f*(x*x + y*y));
    }

    @Override // from IQuaternion
    public Vector3 transformAndAdd (IVector3 vector, IVector3 add, Vector3 result) {
        float xx = x*x, yy = y*y, zz = z*z;
        float xy = x*y, xz = x*z, xw = x*w;
        float yz = y*z, yw = y*w, zw = z*w;
        float vx = vector.x(), vy = vector.y(), vz = vector.z();
        float vx2 = vx*2f, vy2 = vy*2f, vz2 = vz*2f;
        return result.set(vx + vy2*(xy - zw) + vz2*(xz + yw) - vx2*(yy + zz) + add.x(),
                          vy + vx2*(xy + zw) + vz2*(yz - xw) - vy2*(xx + zz) + add.y(),
                          vz + vx2*(xz - yw) + vy2*(yz + xw) - vz2*(xx + yy) + add.z());
    }

    @Override // from IQuaternion
    public Vector3 transformScaleAndAdd (IVector3 vector, float scale, IVector3 add,
                                         Vector3 result) {
        float xx = x*x, yy = y*y, zz = z*z;
        float xy = x*y, xz = x*z, xw = x*w;
        float yz = y*z, yw = y*w, zw = z*w;
        float vx = vector.x(), vy = vector.y(), vz = vector.z();
        float vx2 = vx*2f, vy2 = vy*2f, vz2 = vz*2f;
        return result.set(
            (vx + vy2*(xy - zw) + vz2*(xz + yw) - vx2*(yy + zz)) * scale + add.x(),
            (vy + vx2*(xy + zw) + vz2*(yz - xw) - vy2*(xx + zz)) * scale + add.y(),
            (vz + vx2*(xz - yw) + vy2*(yz + xw) - vz2*(xx + yy)) * scale + add.z());
    }

    @Override // from IQuaternion
    public float transformZ (IVector3 vector) {
        return vector.z() + vector.x()*2f*(x*z - y*w) +
            vector.y()*2f*(y*z + x*w) - vector.z()*2f*(x*x + y*y);
    }

    @Override // from IQuaternion
    public float getRotationZ () {
        return FloatMath.atan2(2f*(x*y + z*w), 1f - 2f*(y*y + z*z));
    }

    @Override // from IQuaternion
    public Quaternion integrate (IVector3 velocity, float t) {
        return integrate(velocity, t, new Quaternion());
    }

    @Override // from IQuaternion
    public Quaternion integrate (IVector3 velocity, float t, Quaternion result) {
        // TODO: use Runge-Kutta integration?
        float qx = 0.5f * velocity.x();
        float qy = 0.5f * velocity.y();
        float qz = 0.5f * velocity.z();
        return result.set(x + t*(qx*w + qy*z - qz*y),
                          y + t*(qy*w + qz*x - qx*z),
                          z + t*(qz*w + qx*y - qy*x),
                          w + t*(-qx*x - qy*y - qz*z)).normalizeLocal();
    }

    @Override // documentation inherited
    public String toString () {
        return "[" + x + ", " + y + ", " + z + ", " + w + "]";
    }

    @Override // documentation inherited
    public int hashCode () {
        return Platform.hashCode(x) ^ Platform.hashCode(y) ^ Platform.hashCode(z) ^
            Platform.hashCode(w);
    }

    @Override // documentation inherited
    public boolean equals (Object other) {
        if (!(other instanceof Quaternion)) {
            return false;
        }
        Quaternion oquat = (Quaternion)other;
        return (x == oquat.x && y == oquat.y && z == oquat.z && w == oquat.w) ||
            (x == -oquat.x && y == -oquat.y && z == -oquat.z && w == -oquat.x);
    }
}
