//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * A pyramidal frustum.
 */
public class Frustum
{
    /** Intersection types indicating that the frustum does not intersect, intersects, or fully
     * contains, respectively, the parameter. */
    public enum IntersectionType { NONE, INTERSECTS, CONTAINS };

    /**
     * Creates an empty (invalid) frustum.
     */
    public Frustum () {
        // initialize the vertices and planes of the frustum
        for (int ii = 0; ii < 8; ii++) {
            _vertices[ii] = new Vector3();
        }
        for (int ii = 0; ii < 6; ii++) {
            _planes[ii] = new Plane();
        }
    }

    /**
     * Returns a reference to the frustum's array of vertices.
     */
    public IVector3[] vertices () {
        return _vertices;
    }

    /**
     * Returns a reference to the bounds of this frustum.
     */
    public Box bounds () {
        return _bounds;
    }

    /**
     * Sets this frustum to one pointing in the Z- direction with the specified parameters
     * determining its size and shape (see the OpenGL documentation for
     * <code>gluPerspective</code>).
     *
     * @param fovy the vertical field of view, in radians.
     * @param aspect the aspect ratio (width over height).
     * @param znear the distance to the near clip plane.
     * @param zfar the distance to the far clip plane.
     * @return a reference to this frustum, for chaining.
     */
    public Frustum setToPerspective (float fovy, float aspect, float znear, float zfar) {
        float top = znear * FloatMath.tan(fovy / 2f), bottom = -top;
        float right = top * aspect, left = -right;
        return setToFrustum(left, right, bottom, top, znear, zfar);
    }

    /**
     * Sets this frustum to one pointing in the Z- direction with the specified parameters
     * determining its size and shape (see the OpenGL documentation for <code>glFrustum</code>).
     *
     * @return a reference to this frustum, for chaining.
     */
    public Frustum setToFrustum (
        float left, float right, float bottom, float top, float near, float far) {
        return setToProjection(left, right, bottom, top, near, far, Vector3.UNIT_Z, false, false);
    }

    /**
     * Sets this frustum to an orthographic one pointing in the Z- direction with the specified
     * parameters determining its size (see the OpenGL documentation for <code>glOrtho</code>).
     *
     * @return a reference to this frustum, for chaining.
     */
    public Frustum setToOrtho (
        float left, float right, float bottom, float top, float near, float far) {
        return setToProjection(left, right, bottom, top, near, far, Vector3.UNIT_Z, true, false);
    }

    /**
     * Sets this frustum to a perspective or orthographic projection with the specified parameters
     * determining its size and shape.
     *
     * @return a reference to this frustum, for chaining.
     */
    public Frustum setToProjection (
        float left, float right, float bottom, float top, float near,
        float far, IVector3 nearFarNormal, boolean ortho, boolean mirrored) {
        float nfnx = nearFarNormal.x(), nfny = nearFarNormal.y(), nfnz = nearFarNormal.z();
        if (ortho) {
            float nrz = -1f / nfnz;
            float xl = nfnx*left*nrz, xr = nfnx*right*nrz;
            float yb = nfny*bottom*nrz, yt = nfny*top*nrz;
            _vertices[0].set(left, bottom, xl + yb - near);
            _vertices[mirrored ? 3 : 1].set(right, bottom, xr + yb - near);
            _vertices[2].set(right, top, xr + yt - near);
            _vertices[mirrored ? 1 : 3].set(left, top, xl + yt - near);
            _vertices[4].set(left, bottom, xl + yb - far);
            _vertices[mirrored ? 7 : 5].set(right, bottom, xr + yb - far);
            _vertices[6].set(right, top, xr + yt - far);
            _vertices[mirrored ? 5 : 7].set(left, top, xl + yt - far);

        } else {
            float rn = 1f / near;
            float lrn = left * rn, rrn = right * rn;
            float brn = bottom * rn, trn = top * rn;

            float nz = near * nfnz;
            float z0 = nz / (nfnx*lrn + nfny*brn - nfnz);
            _vertices[0].set(-z0*lrn, -z0*brn, z0);
            float z1 = nz / (nfnx*rrn + nfny*brn - nfnz);
            _vertices[mirrored ? 3 : 1].set(-z1*rrn, -z1*brn, z1);
            float z2 = nz / (nfnx*rrn + nfny*trn - nfnz);
            _vertices[2].set(-z2*rrn, -z2*trn, z2);
            float z3 = nz / (nfnx*lrn + nfny*trn - nfnz);
            _vertices[mirrored ? 1 : 3].set(-z3*lrn, -z3*trn, z3);

            float fz = far * nfnz;
            float z4 = fz / (nfnx*lrn + nfny*brn - nfnz);
            _vertices[4].set(-z4*lrn, -z4*brn, z4);
            float z5 = fz / (nfnx*rrn + nfny*brn - nfnz);
            _vertices[mirrored ? 7 : 5].set(-z5*rrn, -z5*brn, z5);
            float z6 = fz / (nfnx*rrn + nfny*trn - nfnz);
            _vertices[6].set(-z6*rrn, -z6*trn, z6);
            float z7 = fz / (nfnx*lrn + nfny*trn - nfnz);
            _vertices[mirrored ? 5 : 7].set(-z7*lrn, -z7*trn, z7);
        }

        updateDerivedState();
        return this;
    }

    // /**
    //  * Transforms this frustum in-place by the specified transformation.
    //  *
    //  * @return a reference to this frustum, for chaining.
    //  */
    // public Frustum transformLocal (Transform3D transform)
    // {
    //     return transform(transform, this);
    // }

    // /**
    //  * Transforms this frustum by the specified transformation.
    //  *
    //  * @return a new frustum containing the result.
    //  */
    // public Frustum transform (Transform3D transform)
    // {
    //     return transform(transform, new Frustum());
    // }

    // /**
    //  * Transforms this frustum by the specified transformation, placing the result in the object
    //  * provided.
    //  *
    //  * @return a reference to the result frustum, for chaining.
    //  */
    // public Frustum transform (Transform3D transform, Frustum result)
    // {
    //     // transform all of the vertices
    //     for (int ii = 0; ii < 8; ii++) {
    //         transform.transformPoint(_vertices[ii], result._vertices[ii]);
    //     }
    //     result.updateDerivedState();
    //     return result;
    // }

    /**
     * Determines the maximum signed distance of the point from the planes of the frustum. If
     * the distance is less than or equal to zero, the point lies inside the frustum.
     */
    public float distance (Vector3 point) {
        float distance = -Float.MAX_VALUE;
        for (Plane plane : _planes) {
            distance = Math.max(distance, plane.distance(point));
        }
        return distance;
    }

    /**
     * Checks whether the frustum intersects the specified box.
     */
    public IntersectionType intersectionType (Box box) {
        // exit quickly in cases where the bounding boxes don't overlap (equivalent to a separating
        // axis test using the axes of the box)
        if (!_bounds.intersects(box)) {
            return IntersectionType.NONE;
        }

        // consider each side of the frustum as a potential separating axis
        int ccount = 0;
        for (int ii = 0; ii < 6; ii++) {
            // determine how many vertices fall inside/outside the plane
            int inside = 0;
            Plane plane = _planes[ii];
            for (int jj = 0; jj < 8; jj++) {
                if (plane.distance(box.vertex(jj, _vertex)) <= 0f) {
                    inside++;
                }
            }
            if (inside == 0) {
                return IntersectionType.NONE;
            } else if (inside == 8) {
                ccount++;
            }
        }
        return (ccount == 6) ? IntersectionType.CONTAINS : IntersectionType.INTERSECTS;
    }

    /**
     * Computes the bounds of the frustum under the supplied rotation and places the results in
     * the box provided.
     *
     * @return a reference to the result box, for chaining.
     */
    public Box boundsUnderRotation (Matrix3 matrix, Box result) {
        result.setToEmpty();
        for (Vector3 vertex : _vertices) {
            result.addLocal(matrix.transform(vertex, _vertex));
        }
        return result;
    }

    /**
     * Sets the planes and bounding box of the frustum based on its vertices.
     */
    protected void updateDerivedState () {
        _planes[0].fromPoints(_vertices[0], _vertices[1], _vertices[2]); // near
        _planes[1].fromPoints(_vertices[5], _vertices[4], _vertices[7]); // far
        _planes[2].fromPoints(_vertices[1], _vertices[5], _vertices[6]); // left
        _planes[3].fromPoints(_vertices[4], _vertices[0], _vertices[3]); // right
        _planes[4].fromPoints(_vertices[3], _vertices[2], _vertices[6]); // top
        _planes[5].fromPoints(_vertices[4], _vertices[5], _vertices[1]); // bottom
        _bounds.fromPoints(_vertices);
    }

    /** The vertices of the frustum. */
    protected Vector3[] _vertices = new Vector3[8];

    /** The planes of the frustum (as derived from the vertices). The plane normals point out of
     * the frustum. */
    protected Plane[] _planes = new Plane[6];

    /** The frustum's bounding box (as derived from the vertices). */
    protected Box _bounds = new Box();

    /** A working vertex. */
    protected static Vector3 _vertex = new Vector3();
}
