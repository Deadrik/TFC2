//
// Pythagoras - a collection of geometry classes
// http://github.com/samskivert/pythagoras

package pythagoras.f;

/**
 * Does something extraordinary.
 */
public interface IBox
{
    /**
     * Returns a reference to the box's minimum extent.
     */
    IVector3 minimumExtent ();

    /**
     * Returns a reference to the box's maximum extent.
     */
    IVector3 maximumExtent ();

    /**
     * Returns the center of the box as a new vector.
     */
    Vector3 center ();

    /**
     * Places the location of the center of the box into the given result vector.
     *
     * @return a reference to the result vector, for chaining.
     */
    Vector3 center (Vector3 result);

    /**
     * Returns the length of the box's diagonal (the distance from minimum to maximum extent).
     */
    float diagonalLength ();

    /**
     * Returns the length of the box's longest edge.
     */
    float longestEdge ();

    /**
     * Determines whether the box is empty (whether any of its minima are greater than their
     * corresponding maxima).
     */
    boolean isEmpty ();

    /**
     * Retrieves one of the eight vertices of the box. The code parameter identifies the vertex
     * with flags indicating which values should be selected from the minimum extent, and which
     * from the maximum extent. For example, the code 011b selects the vertex with the minimum x,
     * maximum y, and maximum z.
     *
     * @return a reference to the result, for chaining.
     */
    Vector3 vertex (int code, Vector3 result);

    /**
     * Determines whether this box contains the specified point.
     */
    boolean contains (IVector3 point);

    /**
     * Determines whether this box contains the specified point.
     */
    boolean contains (float x, float y, float z);

    /**
     * Returns the sum of the Manhattan distances between the extents of this box and the
     * specified other box.
     */
    float extentDistance (IBox other);

    /**
     * Determines whether this box completely contains the specified box.
     */
    boolean contains (IBox other);

    /**
     * Determines whether this box intersects the specified other box.
     */
    boolean intersects (IBox other);

    /**
     * Expands this box to include the specified point.
     *
     * @return a new box containing the result.
     */
    Box add (IVector3 point);

    /**
     * Expands this box to include the specified point, placing the result in the object
     * provided.
     *
     * @return a reference to the result box, for chaining.
     */
    Box add (IVector3 point, Box result);

    /**
     * Expands this box to include the bounds of another box.
     *
     * @return a new box containing the result.
     */
    Box add (IBox other);

    /**
     * Expands this box to include the bounds of another box, placing the result in the object
     * provided.
     *
     * @return a reference to the result box, for chaining.
     */
    Box add (IBox other, Box result);

    /**
     * Finds the intersection between this box and another box.
     *
     * @return a new box containing the result.
     */
    Box intersect (IBox other);

    /**
     * Finds the intersection between this box and another box and places the result in the
     * provided object.
     *
     * @return a reference to this box, for chaining.
     */
    Box intersect (IBox other, Box result);

    /**
     * Projects this box.
     *
     * @return a new box containing the result.
     */
    Box project (IMatrix4 matrix);

    /**
     * Projects this box, placing the result in the object provided.
     *
     * @return a reference to the result, for chaining.
     */
    Box project (IMatrix4 matrix, Box result);

    /**
     * Expands the box by the specified amounts.
     *
     * @return a new box containing the result.
     */
    Box expand (float x, float y, float z);

    /**
     * Expands the box by the specified amounts, placing the result in the object provided.
     *
     * @return a reference to the result box, for chaining.
     */
    Box expand (float x, float y, float z, Box result);

    /**
     * Determines whether the specified ray intersects this box.
     */
    boolean intersects (IRay3 ray);

    /**
     * Finds the location of the (first) intersection between the specified ray and this box. This
     * will be the ray origin if the ray starts inside the box.
     *
     * @param result a vector to hold the location of the intersection.
     * @return true if the ray intersects the box (in which case the result vector will be
     * populated with the location of the intersection), false if not.
     */
    boolean intersection (IRay3 ray, Vector3 result);
}
