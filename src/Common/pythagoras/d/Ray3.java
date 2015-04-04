//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.d;

/**
 * A ray consisting of an origin point and a unit direction vector.
 */
public class Ray3 implements IRay3
{
    /** The ray's point of origin. */
    public final Vector3 origin = new Vector3();

    /** The ray's unit direction vector. */
    public final Vector3 direction = new Vector3();

    /**
     * Creates a ray with the values contained in the supplied origin point and unit direction
     * vector.
     */
    public Ray3 (Vector3 origin, Vector3 direction) {
        set(origin, direction);
    }

    /**
     * Copy constructor.
     */
    public Ray3 (Ray3 other) {
        set(other);
    }

    /**
     * Creates an empty (invalid) ray.
     */
    public Ray3 () {
    }

    /**
     * Copies the parameters of another ray.
     *
     * @return a reference to this ray, for chaining.
     */
    public Ray3 set (Ray3 other) {
        return set(other.origin(), other.direction());
    }

    /**
     * Sets the ray parameters to the values contained in the supplied vectors.
     *
     * @return a reference to this ray, for chaining.
     */
    public Ray3 set (Vector3 origin, Vector3 direction) {
        this.origin.set(origin);
        this.direction.set(direction);
        return this;
    }

    // /**
    //  * Transforms this ray in-place.
    //  *
    //  * @return a reference to this ray, for chaining.
    //  */
    // public Ray3 transformLocal (Transform3D transform) {
    //     return transform(transform, this);
    // }

    @Override // from IRay3
    public Vector3 origin () {
        return origin;
    }

    @Override // from IRay3
    public Vector3 direction () {
        return direction;
    }

    // @Override // from IRay3
    // public Ray3 transform (Transform3D transform) {
    //     return transform(transform, new Ray3());
    // }

    // @Override // from IRay3
    // public Ray3 transform (Transform3D transform, Ray3 result) {
    //     transform.transformPoint(origin, result.origin);
    //     transform.transformVector(direction, result.direction).normalizeLocal();
    //     return result;
    // }

    @Override
    public String toString () {
        return "[origin=" + origin + ", direction=" + direction + "]";
    }
}
