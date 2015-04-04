//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

import java.io.Serializable;

/**
 * An axis-aligned box.
 */
public class Box implements IBox, Serializable
{
    /** The unit box. */
    public static final Box UNIT = new Box(Vector3.UNIT_XYZ.negate(), Vector3.UNIT_XYZ);

    /** The zero box. */
    public static final Box ZERO = new Box(Vector3.ZERO, Vector3.ZERO);

    /** The empty box. */
    public static final Box EMPTY = new Box(Vector3.MAX_VALUE, Vector3.MIN_VALUE);

    /** A box that's as large as boxes can get. */
    public static final Box MAX_VALUE = new Box(Vector3.MIN_VALUE, Vector3.MAX_VALUE);

    /**
     * Creates a box with the values contained in the supplied minimum and maximum extents.
     */
    public Box (IVector3 minExtent, IVector3 maxExtent) {
        set(minExtent, maxExtent);
    }

    /**
     * Copy constructor.
     */
    public Box (IBox other) {
        set(other);
    }

    /**
     * Creates an empty box.
     */
    public Box () {
        setToEmpty();
    }

    /**
     * Sets the parameters of the box to the empty values ({@link Vector3#MAX_VALUE} and
     * {@link Vector3#MIN_VALUE} for the minimum and maximum, respectively).
     *
     * @return a reference to this box, for chaining.
     */
    public Box setToEmpty () {
        return set(Vector3.MAX_VALUE, Vector3.MIN_VALUE);
    }

    /**
     * Copies the parameters of another box.
     *
     * @return a reference to this box, for chaining.
     */
    public Box set (IBox other) {
        return set(other.minimumExtent(), other.maximumExtent());
    }

    /**
     * Sets the box parameters to the values contained in the supplied vectors.
     *
     * @return a reference to this box, for chaining.
     */
    public Box set (IVector3 minExtent, IVector3 maxExtent) {
        _minExtent.set(minExtent);
        _maxExtent.set(maxExtent);
        return this;
    }

    /**
     * Initializes this box with the extents of an array of points.
     *
     * @return a reference to this box, for chaining.
     */
    public Box fromPoints (IVector3... points) {
        setToEmpty();
        for (IVector3 point : points) {
            addLocal(point);
        }
        return this;
    }

    /**
     * Expands this box in-place to include the specified point.
     *
     * @return a reference to this box, for chaining.
     */
    public Box addLocal (IVector3 point) {
        return add(point, this);
    }

    /**
     * Expands this box to include the bounds of another box.
     *
     * @return a reference to this box, for chaining.
     */
    public Box addLocal (IBox other) {
        return add(other, this);
    }

    /**
     * Finds the intersection between this box and another box and places the result in this box.
     *
     * @return a reference to this box, for chaining.
     */
    public Box intersectLocal (IBox other) {
        return intersect(other, this);
    }

    // /**
    //  * Transforms this box in-place.
    //  *
    //  * @return a reference to this box, for chaining.
    //  */
    // public Box transformLocal (Transform3D transform) {
    //     return transform(transform, this);
    // }

    /**
     * Projects this box in-place.
     *
     * @return a reference to this box, for chaining.
     */
    public Box projectLocal (IMatrix4 matrix) {
        return project(matrix, this);
    }

    /**
     * Expands the box in-place by the specified amounts.
     *
     * @return a reference to this box, for chaining.
     */
    public Box expandLocal (double x, double y, double z) {
        return expand(x, y, z, this);
    }

    @Override // from IBox
    public IVector3 minimumExtent () {
        return _minExtent;
    }

    @Override // from IBox
    public IVector3 maximumExtent () {
        return _maxExtent;
    }

    @Override // from IBox
    public Vector3 center () {
        return center(new Vector3());
    }

    @Override // from IBox
    public Vector3 center (Vector3 result) {
        return _minExtent.add(_maxExtent, result).multLocal(0.5f);
    }

    @Override // from IBox
    public double diagonalLength () {
        return _minExtent.distance(_maxExtent);
    }

    @Override // from IBox
    public double longestEdge () {
        return Math.max(Math.max(_maxExtent.x - _minExtent.x, _maxExtent.y - _minExtent.y),
                        _maxExtent.z - _minExtent.z);
    }

    @Override // from IBox
    public boolean isEmpty () {
        return _minExtent.x > _maxExtent.x || _minExtent.y > _maxExtent.y ||
            _minExtent.z > _maxExtent.z;
    }

    @Override // from IBox
    public Box add (IVector3 point) {
        return add(point, new Box());
    }

    @Override // from IBox
    public Box add (IVector3 point, Box result) {
        result._minExtent.set(
            Math.min(_minExtent.x, point.x()),
            Math.min(_minExtent.y, point.y()),
            Math.min(_minExtent.z, point.z()));
        result._maxExtent.set(
            Math.max(_maxExtent.x, point.x()),
            Math.max(_maxExtent.y, point.y()),
            Math.max(_maxExtent.z, point.z()));
        return result;
    }

    @Override // from IBox
    public Box add (IBox other) {
        return add(other, new Box());
    }

    @Override // from IBox
    public Box add (IBox other, Box result) {
        IVector3 omin = other.minimumExtent(), omax = other.maximumExtent();
        result._minExtent.set(
            Math.min(_minExtent.x, omin.x()),
            Math.min(_minExtent.y, omin.y()),
            Math.min(_minExtent.z, omin.z()));
        result._maxExtent.set(
            Math.max(_maxExtent.x, omax.x()),
            Math.max(_maxExtent.y, omax.y()),
            Math.max(_maxExtent.z, omax.z()));
        return result;
    }

    @Override // from IBox
    public Box intersect (IBox other) {
        return intersect(other, new Box());
    }

    @Override // from IBox
    public Box intersect (IBox other, Box result) {
        IVector3 omin = other.minimumExtent(), omax = other.maximumExtent();
        result._minExtent.set(
            Math.max(_minExtent.x, omin.x()),
            Math.max(_minExtent.y, omin.y()),
            Math.max(_minExtent.z, omin.z()));
        result._maxExtent.set(
            Math.min(_maxExtent.x, omax.x()),
            Math.min(_maxExtent.y, omax.y()),
            Math.min(_maxExtent.z, omax.z()));
        return result;
    }

    // /**
    //  * Transforms this box.
    //  *
    //  * @return a new box containing the result.
    //  */
    // public Box transform (Transform3D transform) {
    //     return transform(transform, new Box());
    // }

    // /**
    //  * Transforms this box, placing the result in the provided object.
    //  *
    //  * @return a reference to the result box, for chaining.
    //  */
    // public Box transform (Transform3D transform, Box result) {
    //     // the corners of the box cover the eight permutations of ([minX|maxX], [minY|maxY],
    //     // [minZ|maxZ]). To find the new minimum and maximum for each element, we transform
    //     // selecting either the minimum or maximum for each component based on whether it will
    //     // increase or decrease the total (which depends on the sign of the matrix element).
    //     transform.update(Transform3D.AFFINE);
    //     Matrix4f matrix = transform.matrix();
    //     double minx =
    //         matrix.m00 * (matrix.m00 > 0f ? _minExtent.x : _maxExtent.x) +
    //         matrix.m10 * (matrix.m10 > 0f ? _minExtent.y : _maxExtent.y) +
    //         matrix.m20 * (matrix.m20 > 0f ? _minExtent.z : _maxExtent.z) + matrix.m30;
    //     double miny =
    //         matrix.m01 * (matrix.m01 > 0f ? _minExtent.x : _maxExtent.x) +
    //         matrix.m11 * (matrix.m11 > 0f ? _minExtent.y : _maxExtent.y) +
    //         matrix.m21 * (matrix.m21 > 0f ? _minExtent.z : _maxExtent.z) + matrix.m31;
    //     double minz =
    //         matrix.m02 * (matrix.m02 > 0f ? _minExtent.x : _maxExtent.x) +
    //         matrix.m12 * (matrix.m12 > 0f ? _minExtent.y : _maxExtent.y) +
    //         matrix.m22 * (matrix.m22 > 0f ? _minExtent.z : _maxExtent.z) + matrix.m32;
    //     double maxx =
    //         matrix.m00 * (matrix.m00 < 0f ? _minExtent.x : _maxExtent.x) +
    //         matrix.m10 * (matrix.m10 < 0f ? _minExtent.y : _maxExtent.y) +
    //         matrix.m20 * (matrix.m20 < 0f ? _minExtent.z : _maxExtent.z) + matrix.m30;
    //     double maxy =
    //         matrix.m01 * (matrix.m01 < 0f ? _minExtent.x : _maxExtent.x) +
    //         matrix.m11 * (matrix.m11 < 0f ? _minExtent.y : _maxExtent.y) +
    //         matrix.m21 * (matrix.m21 < 0f ? _minExtent.z : _maxExtent.z) + matrix.m31;
    //     double maxz =
    //         matrix.m02 * (matrix.m02 < 0f ? _minExtent.x : _maxExtent.x) +
    //         matrix.m12 * (matrix.m12 < 0f ? _minExtent.y : _maxExtent.y) +
    //         matrix.m22 * (matrix.m22 < 0f ? _minExtent.z : _maxExtent.z) + matrix.m32;
    //     result._minExtent.set(minx, miny, minz);
    //     result._maxExtent.set(maxx, maxy, maxz);
    //     return result;
    // }

    @Override // from IBox
    public Box project (IMatrix4 matrix) {
        return project(matrix, new Box());
    }

    @Override // from IBox
    public Box project (IMatrix4 matrix, Box result) {
        double minx = +Float.MAX_VALUE, miny = +Float.MAX_VALUE, minz = +Float.MAX_VALUE;
        double maxx = -Float.MAX_VALUE, maxy = -Float.MAX_VALUE, maxz = -Float.MAX_VALUE;
        for (int ii = 0; ii < 8; ii++) {
            double x = ((ii & (1 << 2)) == 0) ? _minExtent.x : _maxExtent.x;
            double y = ((ii & (1 << 1)) == 0) ? _minExtent.y : _maxExtent.y;
            double z = ((ii & (1 << 0)) == 0) ? _minExtent.z : _maxExtent.z;
            double rw = 1f / (matrix.m03()*x + matrix.m13()*y + matrix.m23()*z + matrix.m33());
            double px = (matrix.m00()*x + matrix.m10()*y + matrix.m20()*z + matrix.m30()) * rw;
            double py = (matrix.m01()*x + matrix.m11()*y + matrix.m21()*z + matrix.m31()) * rw;
            double pz = (matrix.m02()*x + matrix.m12()*y + matrix.m22()*z + matrix.m32()) * rw;
            minx = Math.min(minx, px);
            miny = Math.min(miny, py);
            minz = Math.min(minz, pz);
            maxx = Math.max(maxx, px);
            maxy = Math.max(maxy, py);
            maxz = Math.max(maxz, pz);
        }
        result._minExtent.set(minx, miny, minz);
        result._maxExtent.set(maxx, maxy, maxz);
        return result;
    }

    @Override // from IBox
    public Box expand (double x, double y, double z) {
        return expand(x, y, z, new Box());
    }

    @Override // from IBox
    public Box expand (double x, double y, double z, Box result) {
        result._minExtent.set(_minExtent.x - x, _minExtent.y - y, _minExtent.z - z);
        result._maxExtent.set(_maxExtent.x + x, _maxExtent.y + y, _maxExtent.z + z);
        return result;
    }

    @Override // from IBox
    public Vector3 vertex (int code, Vector3 result) {
        return result.set(((code & (1 << 2)) == 0) ? _minExtent.x : _maxExtent.x,
                          ((code & (1 << 1)) == 0) ? _minExtent.y : _maxExtent.y,
                          ((code & (1 << 0)) == 0) ? _minExtent.z : _maxExtent.z);
    }

    @Override // from IBox
    public boolean contains (IVector3 point) {
        return contains(point.x(), point.y(), point.z());
    }

    @Override // from IBox
    public boolean contains (double x, double y, double z) {
        return (x >= _minExtent.x && x <= _maxExtent.x &&
                y >= _minExtent.y && y <= _maxExtent.y &&
                z >= _minExtent.z && z <= _maxExtent.z);
    }

    @Override // from IBox
    public double extentDistance (IBox other) {
        return other.minimumExtent().manhattanDistance(_minExtent) +
            other.maximumExtent().manhattanDistance(_maxExtent);
    }

    @Override // from IBox
    public boolean contains (IBox other) {
        IVector3 omin = other.minimumExtent(), omax = other.maximumExtent();
        return omin.x() >= _minExtent.x && omax.x() <= _maxExtent.x &&
            omin.y() >= _minExtent.y && omax.y() <= _maxExtent.y &&
            omin.z() >= _minExtent.z && omax.z() <= _maxExtent.z;
    }

    @Override // from IBox
    public boolean intersects (IBox other) {
        IVector3 omin = other.minimumExtent(), omax = other.maximumExtent();
        return _maxExtent.x >= omin.x() && _minExtent.x <= omax.x() &&
            _maxExtent.y >= omin.y() && _minExtent.y <= omax.y() &&
            _maxExtent.z >= omin.z() && _minExtent.z <= omax.z();
    }

    @Override // from IBox
    public boolean intersects (IRay3 ray) {
        IVector3 dir = ray.direction();
        return
            Math.abs(dir.x()) > MathUtil.EPSILON &&
                (intersectsX(ray, _minExtent.x) || intersectsX(ray, _maxExtent.x)) ||
            Math.abs(dir.y()) > MathUtil.EPSILON &&
                (intersectsY(ray, _minExtent.y) || intersectsY(ray, _maxExtent.y)) ||
            Math.abs(dir.z()) > MathUtil.EPSILON &&
                (intersectsZ(ray, _minExtent.z) || intersectsZ(ray, _maxExtent.z));
    }

    @Override // from IBox
    public boolean intersection (IRay3 ray, Vector3 result) {
        IVector3 origin = ray.origin();
        if (contains(origin)) {
            result.set(origin);
            return true;
        }
        IVector3 dir = ray.direction();
        double t = Float.MAX_VALUE;
        if (Math.abs(dir.x()) > MathUtil.EPSILON) {
            t = Math.min(t, intersectionX(ray, _minExtent.x));
            t = Math.min(t, intersectionX(ray, _maxExtent.x));
        }
        if (Math.abs(dir.y()) > MathUtil.EPSILON) {
            t = Math.min(t, intersectionY(ray, _minExtent.y));
            t = Math.min(t, intersectionY(ray, _maxExtent.y));
        }
        if (Math.abs(dir.z()) > MathUtil.EPSILON) {
            t = Math.min(t, intersectionZ(ray, _minExtent.z));
            t = Math.min(t, intersectionZ(ray, _maxExtent.z));
        }
        if (t == Float.MAX_VALUE) {
            return false;
        }
        origin.addScaled(dir, t, result);
        return true;
    }

    @Override // documentation inherited
    public String toString () {
        return "[min=" + _minExtent + ", max=" + _maxExtent + "]";
    }

    @Override // documentation inherited
    public int hashCode () {
        return _minExtent.hashCode() + 31*_maxExtent.hashCode();
    }

    @Override // documentation inherited
    public boolean equals (Object other) {
        if (!(other instanceof Box)) {
            return false;
        }
        Box obox = (Box)other;
        return _minExtent.equals(obox._minExtent) && _maxExtent.equals(obox._maxExtent);
    }

    /**
     * Helper method for {@link #intersects(Ray3)}. Determines whether the ray intersects the box
     * at the plane where x equals the value specified.
     */
    protected boolean intersectsX (IRay3 ray, double x) {
        IVector3 origin = ray.origin(), dir = ray.direction();
        double t = (x - origin.x()) / dir.x();
        if (t < 0f) {
            return false;
        }
        double iy = origin.y() + t*dir.y(), iz = origin.z() + t*dir.z();
        return iy >= _minExtent.y && iy <= _maxExtent.y &&
            iz >= _minExtent.z && iz <= _maxExtent.z;
    }

    /**
     * Helper method for {@link #intersects(Ray3)}. Determines whether the ray intersects the box
     * at the plane where y equals the value specified.
     */
    protected boolean intersectsY (IRay3 ray, double y) {
        IVector3 origin = ray.origin(), dir = ray.direction();
        double t = (y - origin.y()) / dir.y();
        if (t < 0f) {
            return false;
        }
        double ix = origin.x() + t*dir.x(), iz = origin.z() + t*dir.z();
        return ix >= _minExtent.x && ix <= _maxExtent.x &&
            iz >= _minExtent.z && iz <= _maxExtent.z;
    }

    /**
     * Helper method for {@link #intersects(Ray3)}. Determines whether the ray intersects the box
     * at the plane where z equals the value specified.
     */
    protected boolean intersectsZ (IRay3 ray, double z) {
        IVector3 origin = ray.origin(), dir = ray.direction();
        double t = (z - origin.z()) / dir.z();
        if (t < 0f) {
            return false;
        }
        double ix = origin.x() + t*dir.x(), iy = origin.y() + t*dir.y();
        return ix >= _minExtent.x && ix <= _maxExtent.x &&
            iy >= _minExtent.y && iy <= _maxExtent.y;
    }

    /**
     * Helper method for {@link #intersection}. Finds the <code>t</code> value where the ray
     * intersects the box at the plane where x equals the value specified, or returns
     * {@link Float#MAX_VALUE} if there is no such intersection.
     */
    protected double intersectionX (IRay3 ray, double x) {
        IVector3 origin = ray.origin(), dir = ray.direction();
        double t = (x - origin.x()) / dir.x();
        if (t < 0f) {
            return Float.MAX_VALUE;
        }
        double iy = origin.y() + t*dir.y(), iz = origin.z() + t*dir.z();
        return (iy >= _minExtent.y && iy <= _maxExtent.y &&
            iz >= _minExtent.z && iz <= _maxExtent.z) ? t : Float.MAX_VALUE;
    }

    /**
     * Helper method for {@link #intersection}. Finds the <code>t</code> value where the ray
     * intersects the box at the plane where y equals the value specified, or returns
     * {@link Float#MAX_VALUE} if there is no such intersection.
     */
    protected double intersectionY (IRay3 ray, double y) {
        IVector3 origin = ray.origin(), dir = ray.direction();
        double t = (y - origin.y()) / dir.y();
        if (t < 0f) {
            return Float.MAX_VALUE;
        }
        double ix = origin.x() + t*dir.x(), iz = origin.z() + t*dir.z();
        return (ix >= _minExtent.x && ix <= _maxExtent.x &&
            iz >= _minExtent.z && iz <= _maxExtent.z) ? t : Float.MAX_VALUE;
    }

    /**
     * Helper method for {@link #intersection}. Finds the <code>t</code> value where the ray
     * intersects the box at the plane where z equals the value specified, or returns
     * {@link Float#MAX_VALUE} if there is no such intersection.
     */
    protected double intersectionZ (IRay3 ray, double z) {
        IVector3 origin = ray.origin(), dir = ray.direction();
        double t = (z - origin.z()) / dir.z();
        if (t < 0f) {
            return Float.MAX_VALUE;
        }
        double ix = origin.x() + t*dir.x(), iy = origin.y() + t*dir.y();
        return (ix >= _minExtent.x && ix <= _maxExtent.x &&
            iy >= _minExtent.y && iy <= _maxExtent.y) ? t : Float.MAX_VALUE;
    }

    /** The box's minimum extent. */
    protected final Vector3 _minExtent = new Vector3();

    /** The box's maximum extent. */
    protected final Vector3 _maxExtent = new Vector3();
}
