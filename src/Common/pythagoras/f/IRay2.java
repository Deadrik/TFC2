//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Provides read-only access to a {@link Ray2}.
 */
public interface IRay2
{
    /**
     * Returns a reference to the ray's point of origin.
     */
    IVector origin ();

    /**
     * Returns a reference to the ray's unit direction vector.
     */
    IVector direction ();

    /**
     * Transforms this ray.
     *
     * @return a new ray containing the result.
     */
    Ray2 transform (Transform transform);

    /**
     * Transforms this ray, placing the result in the object provided.
     *
     * @return a reference to the result ray, for chaining.
     */
    Ray2 transform (Transform transform, Ray2 result);

    /**
     * Determines whether the ray intersects the specified point.
     */
    boolean intersects (IVector pt);

    /**
     * Finds the intersection between the ray and a line segment with the given start and end
     * points.
     *
     * @return true if the ray intersected the segment (in which case the result will contain the
     * point of intersection), false otherwise.
     */
    boolean getIntersection (IVector start, IVector end, Vector result);

    /**
     * Finds the intersection between the ray and a capsule with the given start point, end point,
     * and radius.
     *
     * @return true if the ray intersected the circle (in which case the result will contain the
     * point of intersection), false otherwise.
     */
    boolean getIntersection (IVector start, IVector end, float radius, Vector result);

    /**
     * Finds the intersection between the ray and a circle with the given center and radius.
     *
     * @return true if the ray intersected the circle (in which case the result will contain the
     * point of intersection), false otherwise.
     */
    boolean getIntersection (IVector center, float radius, Vector result);

    /**
     * Computes the nearest point on the Ray to the supplied point.
     * @return {@code result} for chaining.
     */
    Vector getNearestPoint (IVector point, Vector result);
}
