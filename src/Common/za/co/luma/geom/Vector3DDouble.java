package za.co.luma.geom;

import za.co.iocom.math.MathUtil;

/**
 * Class that represents a real 3D vector.
 * 
 * @author Herman Tulleken
 */
public class Vector3DDouble
{
	public double x;
	public double y;
	public double z;

	/**
	 * Constructs a new vector with the given coordinates.
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param z
	 *            z-coordinate
	 */
	public Vector3DDouble(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @return The x-coordinate
	 */
	public double getX()
	{
		return x;
	}

	/**
	 * @return The y-coordinate
	 */
	public double getY()
	{
		return y;
	}

	/**
	 * @return The z-coordinate
	 */
	public double getZ()
	{
		return z;
	}

	/**
	 * Sets the new coordinates for this vector.
	 * 
	 * @param x
	 *            x-coordinate
	 * @param y
	 *            y-coordinate
	 * @param z
	 *            z-coordinate
	 */
	public void setLocation(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	/**
	 * Returns the vector coordinates, delimited with angled brackets and separated with commas.
	 */
	public String toString()
	{
		return "<" + x + " " + y + " " + z + ">";
	}

	/**
	 * Returns the size (Euclidean length) of this vector.
	 * 
	 * @return A non-negative real number denoting the length of this vector.
	 */
	public double size()
	{
		return Math.sqrt(MathUtil.sqr(x) + MathUtil.sqr(y) + MathUtil.sqr(z));
	}

	/**
	 * Scale this vector by a given factor.
	 * 
	 * @param r
	 *            The scaling factor.
	 */
	public void scale(double r)
	{
		x *= r;
		y *= r;
		z *= r;
	}

	/**
	 * Returns a unit length vector in the same direction is this vector.
	 * 
	 * @return A new vector of unit length, pointing in the same direction as this vector.
	 */
	public Vector3DDouble unitVector()
	{
		Vector3DDouble v = new Vector3DDouble(x, y, z);
		v.scale(1 / size());

		return v;
	}

	/**
	 * Returns the Euclidean distance between two points.
	 * 
	 * @return
	 */
	public static double distance(Vector3DDouble p1, Vector3DDouble p2)
	{
		double x = p2.x - p1.x;
		double y = p2.y - p1.y;
		double z = p2.z - p1.z;

		return Math.sqrt(x * x + y * y + z * z);
	}

	/**
	 * Returns the z-coordinate of the cross product of two points projected on the plane z = 0, (embedded in 3D space).
	 */
	public static double scalarCross(Vector3DDouble p1, Vector3DDouble p2)
	{
		return p1.x * p2.y - p1.y * p2.x;
	}

	/**
	 * Checks whether a and b lies on the same side of line p1-p2, or not.
	 * 
	 * If either or both points lie on the line, the method returns true.
	 */
	public static boolean sameSide(Vector3DDouble p1, Vector3DDouble p2, Vector3DDouble a, Vector3DDouble b)
	{
		double cp1 = scalarCross(subtract(b, a), subtract(p1, a));
		double cp2 = scalarCross(subtract(b, a), subtract(p2, a));

		return cp1 * cp2 >= 0;
	}

	/**
	 * Checks whether a point lies within the triangle described by three vertices.
	 * 
	 * @param p
	 *            The point.
	 * @param a
	 *            One vertice of the triangle.
	 * @param b
	 *            One vertice of the triangle.
	 * @param c
	 *            One vertice of the triangle.
	 * 
	 * @return True if the point lies within the triangle, false otherwise.
	 */
	public static boolean pointInTriangle(Vector3DDouble p, Vector3DDouble a, Vector3DDouble b, Vector3DDouble c)
	{
		return sameSide(p, a, b, c) && sameSide(p, b, a, c) && sameSide(p, c, a, b);
	}

	/**
	 * Adds two vectors, and return the result.
	 * 
	 * @return A new vector, the sum of the parameters.
	 */
	public static Vector3DDouble add(Vector3DDouble p1, Vector3DDouble p2)
	{
		return new Vector3DDouble(p1.getX() + p2.getX(), p1.getY() + p2.getY(), p1.getZ() + p2.getZ());
	}

	/**
	 * Gets a point between two points. More correct, returns a new vector laying in the same line as p2 - p1.
	 * 
	 * @param p1
	 * @param p2
	 * @param r
	 *            Controls the distance of the new point from the other points. If r is 0, the new point coincides with
	 *            p1, if r is 1, the new point coincides with p2. If r lies between 0, and 1, the new point lies between
	 *            p1 and p2. If r is smaller than 0, p1 lies between the new point and p2, and if r is greater than 1,
	 *            p2 lies between the new point and p1.
	 * @return A new vector
	 */
	public static Vector3DDouble getPointBetween(Vector3DDouble p1, Vector3DDouble p2, double r)
	{
		Vector3DDouble u = scale(subtract(p2, p1), r);

		return add(p1, u);

	}

	/**
	 * Returns a new vector, in the same direction as p, but sacled by a factor r.
	 * 
	 * @param p
	 *            Direction and original size.
	 * @param r
	 *            Scaling fator
	 * 
	 * @return A new vector, a factor of p1.
	 */
	public static Vector3DDouble scale(Vector3DDouble p, double r)
	{
		return new Vector3DDouble(p.getX() * r, p.getY() * r, p.getZ());
	}

	/**
	 * Returns the Euclidean length of this vector.
	 * 
	 * @param p
	 * @return
	 */
	public static double size(Vector3DDouble p)
	{
		return Math.sqrt(MathUtil.sqr(p.getX()) + MathUtil.sqr(p.getY()) + MathUtil.sqr(p.getZ()));
	}

	/**
	 * Subtracts p2 from p1 and returns the result as a new vector.
	 */
	public static Vector3DDouble subtract(Vector3DDouble p1, Vector3DDouble p2)
	{
		return new Vector3DDouble(p1.getX() - p2.getX(), p1.getY() - p2.getY(), p1.getZ() - p2.getZ());
	}

	/**
	 * Returns a new unit vector in the same direcction as the given vector.
	 */
	public static Vector3DDouble unit(Vector3DDouble p1)
	{
		return scale(p1, 1 / size(p1));
	}

	/**
	 * Returns a z-coordinate so that the vector (x, y, z) lies in the same plane as the plane which contains both p and
	 * q.
	 */
	public static double getHeight(double x, double y, Vector3DDouble p, Vector3DDouble q)
	{
		double alpha = (x * q.y - y * q.x) / (p.x * q.y - p.y * q.x);
		double beta = (x - alpha * p.x) / q.x;
		double z = alpha * p.z + beta * q.z;

		return z;
	}
}
