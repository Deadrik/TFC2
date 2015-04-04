package za.co.luma.geom;

import java.awt.geom.Point2D;

import za.co.iocom.math.MathUtil;

/**
 * Class that represents a 2D vector over real numbers.
 * 
 * @author Herman Tulleken (herman@luma.co.za)
 *
 */
public class Vector2DDouble extends Point2D
{
	public double x;
	public double y;

	public Vector2DDouble(double x, double y)
	{
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public double getX()
	{
		return x;
	}

	@Override
	public double getY()
	{
		return y;
	}

	@Override
	public void setLocation(double x, double y)
	{
		this.x = x;
		this.y = y;		
	}
	
	@Override
	public String toString()
	{
	return "<" + x + " " +y +">";
	}
	
	public double size()
	{
		return Math.sqrt(MathUtil.sqr(x)+MathUtil.sqr(y));
	}
	
	public void scale(double r)
	{
		x *= r;
		y *= r;
	}
	
	public Vector2DDouble unitVector()
	{
		Vector2DDouble v = new Vector2DDouble(x, y);
		
		 v.scale(1/size());
		 
		 return v;
	}

	/**
	 * Returns the Euclidean distance between two points.
	 * 
	 * @return
	 */
	public static double distance(Vector2DDouble p1, Vector2DDouble p2)
	{
		double x = p2.x - p1.x;
		double y = p2.y - p1.y;
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Returns the z-coordinate of the cross product of two points
	 * (embedded in 3D space).
	 * 
	 */
	public static double scalarCross(Vector2DDouble p1, Vector2DDouble p2)
	{
		return p1.x * p2.y - p1.y * p2.x;
	}

	/**
	 * Checks whether a and b lies on the same side of line p1-p2, or not.
	 * 
	 * If either or both points lie on the line, the method returns true.
	 */
	public static boolean sameSide(Vector2DDouble p1, Vector2DDouble p2,
			Vector2DDouble a, Vector2DDouble b)
	{
		double cp1 = scalarCross(minus(b, a), minus(p1, a));
		double cp2 = scalarCross(minus(b, a), minus(p2, a));

		return cp1 * cp2 >= 0;
	}

	/**
	 * Checks whether a point lies within the triangle described by three vertices.
	 * @param p The point.
	 * @param a One vertice of the triangle.
	 * @param b One vertice of the triangle.
	 * @param c One vertice of the triangle.
	 * 
	 * @return True if the point lies within the triangle, false otherwise.
	 */
	public static boolean pointInTriangle(Vector2DDouble p, Vector2DDouble a,
			Vector2DDouble b, Vector2DDouble c)
	{
		return sameSide(p, a, b, c) && sameSide(p, b, a, c)
				&& sameSide(p, c, a, b);
	}

	public static Vector2DDouble minus(Vector2DDouble p1, Vector2DDouble p2)
	{
		return new Vector2DDouble(p1.x - p2.x, p1.y - p2.y);
	}

	public static Vector2DDouble add(Vector2DDouble p1, Vector2DDouble p2)
	{
		return new Vector2DDouble(p1.getX() + p2.getX(), p1.getY() + p2.getY());
	}	
	
	public static Vector2DDouble getPointBetween(Vector2DDouble p1, Vector2DDouble p2, double r)
	{
		Vector2DDouble u = scale(subtract(p2, p1), r);

		return add(p1, u);

	}

	public static Vector2DDouble orthoUnit(Vector2DDouble p)
	{
		double size = size(p);
		return new Vector2DDouble(p.getY()/size, -p.getX()/size);
	}
	
	public static Vector2DDouble scale(Vector2DDouble p, double r)
	{
		return new Vector2DDouble(p.getX() * r, p.getY() * r);
	}

	public static double size(Vector2DDouble p)
	{
		return Math.sqrt(MathUtil.sqr(p.getX()) + MathUtil.sqr(p.getY()));
	}

	public static Vector2DDouble subtract(Vector2DDouble p1, Vector2DDouble p2)
	{
		return new Vector2DDouble(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}

	public static Vector2DDouble unit(Vector2DDouble p1)
	{
		return scale(p1, 1/size(p1));		
	}
	
	public void setSize(double size)
	{
		if(size() != 0.0)
			scale(size / size());
		else
		{
			x = 0;
			y = size;
		}	
	}
	
	public void setAngle(double angleInRad)
	{
		double r = size();
		
		x = r * Math.cos(angleInRad);
		y = r * Math.sin(angleInRad);
	}

	public double angle()
	{
		if (x > 0) 
		  return Math.atan(y/x);
		if(x < 0) 
		  return Math.atan(y/x) - Math.PI;
		else // x == 0
		{
		  if (y > 0) 
		    return Math.PI / 2;
		  if (y < 0) 
		    return  -Math.PI / 2;
		  else // y == 0 
		    return 0;//indeterminate
		}
	}
}
