//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * Represents a geometric transform. Specialized implementations exist for identity, rigid body,
 * uniform, non-uniform, and affine transforms.
 */
public interface Transform
{
    /** Returns the uniform scale applied by this transform. The uniform scale will be approximated
     * for non-uniform transforms. */
    double uniformScale ();

    /** Returns the scale vector for this transform. */
    Vector scale ();

    /** Returns the x-component of the scale applied by this transform. Note that this will be
     * extracted and therefore approximate for affine transforms. */
    double scaleX ();

    /** Returns the y-component of the scale applied by this transform. Note that this will be
     * extracted and therefore approximate for affine transforms. */
    double scaleY ();

    /** Returns the rotation applied by this transform. Note that the rotation is extracted and
     * therefore approximate for affine transforms.
     * @throws NoninvertibleTransformException if the transform is not invertible. */
    double rotation ();

    /** Returns the translation vector for this transform. */
    Vector translation ();

    /** Returns the x-coordinate of the translation component. */
    double tx ();

    /** Returns the y-coordinate of the translation component. */
    double ty ();

    /** Copies the affine transform matrix into the supplied array.
     * @param matrix the array which receives {@code m00, m01, m10, m11, tx, ty}. */
    void get (double[] matrix);

    /** Sets the uniform scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not uniform or greater. */
    Transform setUniformScale (double scale);

    /** Sets the x and y scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if either supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform setScale (double scaleX, double scaleY);

    /** Sets the x scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform setScaleX (double scaleX);

    /** Sets the y scale of this transform.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform setScaleY (double scaleY);

    /** Sets the rotation component of this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setRotation (double angle);

    /** Sets the translation component of this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setTranslation (double tx, double ty);

    /** Sets the x-component of this transform's translation.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setTx (double tx);

    /** Sets the y-component of this transform's translation.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform setTy (double ty);

    /** Sets the affine transform matrix.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not affine or greater. */
    Transform setTransform (double m00, double m01, double m10, double m11,
                            double tx, double ty);

    /** Scales this transform in a uniform manner by the specified amount.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not uniform or greater. */
    Transform uniformScale (double scale);

    /** Scales this transform by the specified amount in the x and y dimensions.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if either supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform scale (double scaleX, double scaleY);

    /** Scales this transform by the specified amount in the x dimension.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform scaleX (double scaleX);

    /** Scales this transform by the specified amount in the y dimension.
     * @return this instance, for chaining.
     * @throws IllegalArgumentException if the supplied scale is zero.
     * @throws UnsupportedOperationException if the transform is not non-uniform or greater. */
    Transform scaleY (double scaleY);

    /** Rotates this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform rotate (double angle);

    /** Translates this transform.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform translate (double tx, double ty);

    /** Translates this transform in the x dimension.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform translateX (double tx);

    /** Translates this transform in the y dimension.
     * @return this instance, for chaining.
     * @throws UnsupportedOperationException if the transform is not rigid body or greater. */
    Transform translateY (double ty);

    /** Returns a new transform that represents the inverse of this transform.
     * @throws NoninvertibleTransformException if the transform is not invertible. */
    Transform invert ();

    /** Returns a new transform comprised of the concatenation of {@code other} to this transform
     * (i.e. {@code this x other}). */
    Transform concatenate (Transform other);

    /** Returns a new transform comprised of the concatenation of this transform to {@code other}
     * (i.e. {@code other x this}). */
    Transform preConcatenate (Transform other);

    /** Returns a new transform comprised of the linear interpolation between this transform and
     * the specified other. */
    Transform lerp (Transform other, double t);

    /** Transforms the supplied point, writing the result into {@code into}.
     * @param into a point into which to store the result, may be the same object as {@code p}.
     * @return {@code into} for chaining. */
    Point transform (IPoint p, Point into);

    /** Transforms the supplied points.
     * @param src the points to be transformed.
     * @param srcOff the offset into the {@code src} array at which to start.
     * @param dst the points into which to store the transformed points. May be {@code src}.
     * @param dstOff the offset into the {@code dst} array at which to start.
     * @param count the number of points to transform. */
    void transform (IPoint[] src, int srcOff, Point[] dst, int dstOff, int count);

    /** Transforms the supplied points.
     * @param src the points to be transformed (as {@code [x, y, x, y, ...]}).
     * @param srcOff the offset into the {@code src} array at which to start.
     * @param dst the points into which to store the transformed points. May be {@code src}.
     * @param dstOff the offset into the {@code dst} array at which to start.
     * @param count the number of points to transform. */
    void transform (double[] src, int srcOff, double[] dst, int dstOff, int count);

    /** Inverse transforms the supplied point, writing the result into {@code into}.
     * @param into a point into which to store the result, may be the same object as {@code p}.
     * @return {@code into}, for chaining.
     * @throws NoninvertibleTransformException if the transform is not invertible. */
    Point inverseTransform (IPoint p, Point into);

    /** Transforms the supplied vector as a point (accounting for translation), writing the result
     * into {@code into}.
     * @param into a vector into which to store the result, may be the same object as {@code v}.
     * @return {@code into}, for chaining. */
    Vector transformPoint (IVector v, Vector into);

    /** Transforms the supplied vector, writing the result into {@code into}.
     * @param into a vector into which to store the result, may be the same object as {@code v}.
     * @return {@code into}, for chaining. */
    Vector transform (IVector v, Vector into);

    /** Inverse transforms the supplied vector, writing the result into {@code into}.
     * @param into a vector into which to store the result, may be the same object as {@code v}.
     * @return {@code into}, for chaining.
     * @throws NoninvertibleTransformException if the transform is not invertible. */
    Vector inverseTransform (IVector v, Vector into);

    /** @deprecated Use {@link #copy}. */
    @Deprecated Transform clone ();

    /** Returns a copy of this transform. */
    Transform copy ();

    /** Returns an integer that increases monotonically with the generality of the transform
     * implementation. Used internally when combining transforms. */
    int generality ();
}
