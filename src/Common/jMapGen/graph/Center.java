package jMapGen.graph;

import jMapGen.BiomeType;
import jMapGen.Point;

import java.util.Vector;


public class Center 
{
	public int index;

	public Point point;  // location
	public Boolean water = false;  // lake or ocean
	public Boolean ocean = false;  // ocean
	public Boolean coast = false;  // land polygon touching an ocean
	public Boolean border = false;  // at the edge of the map
	public BiomeType biome;  // biome type (see article)
	public double elevation = 0;  // 0.0-1.0
	public double moisture = 0;  // 0.0-1.0

	public int river = 0;
	public Vector<Center> upriver;  // pointer to adjacent corner most uphill
	public Center downslope;  // pointer to adjacent corner most downhill
	public Center watershed;  // pointer to coastal corner, or null

	public Vector<Center> neighbors;
	public Vector<Edge> borders;
	public Vector<Corner> corners;

	public Corner getClosestCorner(Point p)
	{
		Corner closest = corners.get(0);
		double distance = p.distanceSq(corners.get(0).point);

		for (int i = 1; i < corners.size(); i++)
		{
			double newDist = p.distanceSq(corners.get(i).point);
			if(newDist < distance)
			{
				distance = newDist;
				closest = corners.get(i);
			}
		}
		return closest;
	}

	public Edge getClosestTriangle(Point local)
	{
		Corner closest = corners.get(0);

		pythagoras.d.Vector p = new pythagoras.d.Vector(local.x, local.y);
		pythagoras.d.Vector a;
		pythagoras.d.Vector b = new pythagoras.d.Vector(point.x, point.y);
		pythagoras.d.Vector c;

		for (Corner c0 : corners)
		{
			a = new pythagoras.d.Vector(c0.point.x, c0.point.y);
			for (Corner c1 : corners)
			{
				Edge e = c0.getTouchingEdge(c1);
				if(c0 != c1 && e != null)
				{
					c = new pythagoras.d.Vector(c1.point.x, c1.point.y);
					if(InTriangle(p,a,b,c))
					{
						return e;
					}
				}
			}
		}

		return null;
	}

	public Edge getClosestTriangle2(Point local)
	{
		Corner closest = this.getClosestCorner(local);

		pythagoras.d.Vector p = new pythagoras.d.Vector(local.x, local.y);
		pythagoras.d.Vector a;
		pythagoras.d.Vector b = new pythagoras.d.Vector(point.x, point.y);
		pythagoras.d.Vector c;

		for (int i = 0; i < corners.size(); i++)
		{
			Corner c0 = corners.get(i);
			a = new pythagoras.d.Vector(c0.point.x, c0.point.y);
			Corner c1;
			if(i+1 == corners.size())
			{
				c1 = corners.get(0);
			}
			else
			{
				c1 = corners.get(i+1);
			}
			c = new pythagoras.d.Vector(c1.point.x, c1.point.y);

			if(this.pointInTriangleBB(a.x, a.y, b.x, b.y, c.x, c.y, p.x, p.y) && InTriangle(p,a,b,c))
			{
				Edge e = new Edge();
				e.setVoronoiEdge(c0, c1);
				return e;
			}
		}
		return null;
	}

	public Corner getNextClosestCorner(Point p)
	{
		Corner first = getClosestCorner(p);
		Corner closest = first.adjacent.get(0);
		double distance = p.distanceSq(first.adjacent.get(0).point);

		for (int i = 1; i < first.adjacent.size(); i++)
		{

			double newDist = p.distanceSq(first.adjacent.get(i).point);
			if(newDist < distance)
			{
				distance = newDist;
				closest = first.adjacent.get(i);
			}

		}
		return closest;
	}

	public Corner getNextClosestCornerEdge(Point local, Corner first)
	{
		Corner closest = first.adjacent.get(0);
		double distance = Double.MAX_VALUE;

		pythagoras.d.Vector p = new pythagoras.d.Vector(local.x, local.y);
		pythagoras.d.Vector a = new pythagoras.d.Vector(first.point.x, first.point.y);
		pythagoras.d.Vector b = new pythagoras.d.Vector(point.x, point.y);
		pythagoras.d.Vector c;

		for (int i = 0; i < corners.size(); i++)
		{
			Corner _corner = corners.get(i);
			if(_corner.getTouchingEdge(first) != null)
			{
				c = new pythagoras.d.Vector(_corner.point.x, _corner.point.y);
				if(InTriangle(p,a,b,c))
				{
					return closest;
				}
			}
		}
		return closest;
	}

	public Corner getNextClosestCorner(Point local, Corner first)
	{
		Corner closest = first.adjacent.get(0);
		double distance = Double.MAX_VALUE;

		pythagoras.d.Vector p = new pythagoras.d.Vector(local.x, local.y);
		pythagoras.d.Vector a = new pythagoras.d.Vector(first.point.x, first.point.y);
		pythagoras.d.Vector b = new pythagoras.d.Vector(point.x, point.y);
		pythagoras.d.Vector c;

		for (int i = 0; i < corners.size(); i++)
		{
			Corner _corner = corners.get(i);
			if(_corner != first)
			{
				c = new pythagoras.d.Vector(_corner.point.x, _corner.point.y);
				double dist = local.distanceSq(_corner.point.x, _corner.point.y);
				if(InTriangle(p,a,b,c) && dist < distance)
				{
					closest = _corner;
					distance = dist;
				}
			}
		}
		return closest;
	}

	boolean SameSide(pythagoras.d.Vector p1, pythagoras.d.Vector p2, pythagoras.d.Vector a, pythagoras.d.Vector b)
	{
		pythagoras.d.Vector cp1 = b.subtract(a).cross(p1.subtract(a));
		pythagoras.d.Vector cp2 = b.subtract(a).cross(p2.subtract(a));

		if (cp1.dot(cp2) >= 0) 
			return true;
		else return false;
	}

	boolean PointInTriangle(pythagoras.d.Vector p, pythagoras.d.Vector a, pythagoras.d.Vector b, pythagoras.d.Vector c)
	{
		if (SameSide(p,a, b,c) && SameSide(p,b, a,c) && SameSide(p,c, a,b)) 
			return true;
		else return false;
	}

	boolean InTriangle(pythagoras.d.Vector p, pythagoras.d.Vector a, pythagoras.d.Vector b, pythagoras.d.Vector c)
	{
		// Compute vectors        
		pythagoras.d.Vector v0 = c.subtract(a);
		pythagoras.d.Vector v1 = b.subtract(a);
		pythagoras.d.Vector v2 = p.subtract(a);

		// Compute dot products
		double dot00 = v0.dot(v0);
		double dot01 = v0.dot(v1);
		double dot02 = v0.dot(v2);
		double dot11 = v1.dot(v1);
		double dot12 = v1.dot(v2);

		// Compute barycentric coordinates
		double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
		double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		// Check if point is in triangle
		return (u >= 0) && (v >= 0) && (u + v < 1);

	}

	double Epsilon = 0.001;
	double EpsilonSq = Epsilon*Epsilon;

	double side(double x1, double y1, double x2, double y2, double x, double y)
	{
		return (y2 - y1)*(x - x1) + (-x2 + x1)*(y - y1);
	}

	boolean naivePointInTriangle(double x1, double y1, double x2, double y2, double x3, double y3, double x, double y)
	{
		boolean checkSide1 = side(x1, y1, x2, y2, x, y) >= 0;
		boolean checkSide2 = side(x2, y2, x3, y3, x, y) >= 0;
		boolean checkSide3 = side(x3, y3, x1, y1, x, y) >= 0;
		return checkSide1 && checkSide2 && checkSide3;
	}

	boolean pointInTriangleBB(double x1, double y1, double x2, double y2, double x3, double y3, double x, double y)
	{
		double xMin = Math.min(x1, Math.min(x2, x3)) - Epsilon;
		double xMax = Math.max(x1, Math.max(x2, x3)) + Epsilon;
		double yMin = Math.min(y1, Math.min(y2, y3)) - Epsilon;
		double yMax = Math.max(y1, Math.max(y2, y3)) + Epsilon;

		if ( x < xMin || xMax < x || y < yMin || yMax < y )
			return false;
		else
			return true;
	}

	double distanceSquarePointToSegment(double x1, double y1, double x2, double y2, double x, double y)
	{
		double p1_p2_squareLength = (x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1);
		double dotProduct = ((x - x1)*(x2 - x1) + (y - y1)*(y2 - y1)) / p1_p2_squareLength;
		if ( dotProduct < 0 )
		{
			return (x - x1)*(x - x1) + (y - y1)*(y - y1);
		}
		else if ( dotProduct <= 1 )
		{
			double p_p1_squareLength = (x1 - x)*(x1 - x) + (y1 - y)*(y1 - y);
			return p_p1_squareLength - dotProduct * dotProduct * p1_p2_squareLength;
		}
		else
		{
			return (x - x2)*(x - x2) + (y - y2)*(y - y2);
		}
	}

	boolean accuratePointInTriangle(double x1, double y1, double x2, double y2, double x3, double y3, double x, double y)
	{
		if (! pointInTriangleBB(x1, y1, x2, y2, x3, y3, x, y))
			return false;

		if (naivePointInTriangle(x1, y1, x2, y2, x3, y3, x, y))
			return true;

		if (distanceSquarePointToSegment(x1, y1, x2, y2, x, y) <= EpsilonSq)
			return true;
		if (distanceSquarePointToSegment(x2, y2, x3, y3, x, y) <= EpsilonSq)
			return true;
		if (distanceSquarePointToSegment(x3, y3, x1, y1, x, y) <= EpsilonSq)
			return true;

		return false;
	}
}
