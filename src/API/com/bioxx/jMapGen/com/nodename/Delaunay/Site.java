package com.bioxx.jMapGen.com.nodename.Delaunay;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.com.nodename.geom.Polygon;
import com.bioxx.jMapGen.com.nodename.geom.Winding;

/**
 * Sites are used by edges to define which points that the edge is bisecting.
 * @author Bioxx
 *
 */
public class Site implements ICoord, Comparator<Site>
{
	private static Vector<Site> _pool = new Vector<Site>();

	public Site()
	{

	}

	public static Site create(Point p, int index)
	{
		if (_pool.size() > 0)
		{
			Site site = _pool.lastElement().init(p, index);
			_pool.remove(_pool.size()-1);
			return site;
		}
		else
		{
			return new Site(p, index);
		}
	}

	/**
	 * sort sites on y, then x, coord
	 * also change each site's _siteIndex to match its new position in the list
	 * so the _siteIndex can be used to identify the site for nearest-neighbor queries
	 * 
	 * haha "also" - means more than one responsibility...
	 * 
	 */
	@Override
	public int compare(Site s1, Site s2)
	{

		int returnValue = Voronoi.compareByYThenX(s1, s2);

		// swap _siteIndex values if necessary to match new ordering:
		int tempIndex;
		if (returnValue == -1)
		{
			if (s1._siteIndex > s2._siteIndex)
			{
				tempIndex = s1._siteIndex;
				s1._siteIndex = s2._siteIndex;
				s2._siteIndex = tempIndex;
			}
		}
		else if (returnValue == 1)
		{
			if (s2._siteIndex > s1._siteIndex)
			{
				tempIndex = s2._siteIndex;
				s2._siteIndex = s1._siteIndex;
				s1._siteIndex = tempIndex;
			}

		}

		return returnValue;
	}


	private static final double EPSILON = 0.005;
	private static boolean closeEnough(Point p0, Point p1)
	{
		return Point.distance(p0, p1) < EPSILON;
	}

	private Point _coord;
	public Point getCoord()
	{
		return _coord;
	}


	private int _siteIndex;

	// the edges that define this Site's Voronoi region:
	private Vector<Edge> _edges;
	Vector<Edge> getEdges()
	{
		return _edges;
	}
	// which end of each edge hooks up with the previous edge in _edges:
	private Vector<LR> _edgeOrientations;
	// ordered list of points that define the region clipped to bounds:
	private Vector<Point> _region;

	public Site(Point p, int index)
	{
		init(p, index);
	}

	private Site init(Point p, int index)
	{
		_coord = p;
		_siteIndex = index;
		_edges = new Vector<Edge>();
		_region = null;
		return this;
	}

	public String toString()
	{
		return "Site " + _siteIndex + ": " + _coord;
	}

	public void addEdge(Edge edge)
	{
		_edges.add(edge);
	}

	public Edge nearestEdge()
	{
		Collections.sort(_edges, new Edge());
		return _edges.firstElement();
	}

	public Vector<Site> neighborSites()
	{
		if (_edges == null || _edges.size() == 0)
		{
			return new Vector<Site>();
		}
		if (_edgeOrientations == null)
		{ 
			reorderEdges();
		}
		Vector<Site> list = new Vector<Site>();

		for(int i = 0; i < _edges.size(); i++)
		{
			list.add(neighborSite(_edges.get(i)));
		}
		return list;
	}

	private Site neighborSite(Edge edge)
	{
		if (this == edge.getLeftSite())
		{
			return edge.getRightSite();
		}
		if (this == edge.getRightSite())
		{
			return edge.getLeftSite();
		}
		return null;
	}

	Vector<Point> region(Rectangle clippingBounds)
	{
		if (_edges == null || _edges.size() == 0)
		{
			return new Vector<Point>();
		}
		if (_edgeOrientations == null)
		{ 
			reorderEdges();
			_region = clipToBounds(clippingBounds);
			if ((new Polygon(_region)).winding() == Winding.CLOCKWISE)
			{
				_region = reverseVector(_region);
			}
		}
		return _region;
	}

	Vector<Point> reverseVector(Vector<Point> v0)
	{
		Vector<Point> v1 = new Vector<Point>();
		for(int iter = v0.size()-1; iter >= 0; iter--)
		{
			v1.add(v0.get(iter));
		}
		return v1;
	}


	private void reorderEdges()
	{
		try {
			EdgeReorderer reorderer = new EdgeReorderer(_edges, Vertex.class);
			_edges = reorderer.getEdges();

			_edgeOrientations = reorderer.getEdgeOrientations();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private Vector<Point> clipToBounds(Rectangle bounds)
	{
		Vector<Point> points = new Vector<Point>();
		int n = _edges.size();
		int i = 0;
		Edge edge;
		while (i < n && _edges.get(i).getVisible() == false)
		{
			++i;
		}

		if (i == n)
		{
			// no edges visible
			return new Vector<Point>();
		}
		edge = _edges.get(i);
		LR orientation = _edgeOrientations.get(i);
		points.add(edge.getClippedEnds()[orientation.value]);
		points.add(edge.getClippedEnds()[LR.other(orientation).value]);

		for (int j = i + 1; j < n; ++j)
		{
			edge = _edges.get(j);
			if (edge.getVisible() == false)
			{
				continue;
			}
			connect(points, j, bounds, false);
		}
		// close up the polygon by adding another corner point of the bounds if needed:
		connect(points, i, bounds, true);

		return points;
	}

	private void connect(Vector<Point> points, int j, Rectangle bounds, Boolean closingUp)
	{
		Point rightPoint = points.get(points.size() - 1);
		Edge newEdge = _edges.get(j);
		LR newOrientation = _edgeOrientations.get(j);
		// the point that  must be connected to rightPoint:
		Point newPoint = newEdge.getClippedEnds()[newOrientation.value];
		if (!closeEnough(rightPoint, newPoint))
		{
			// The points do not coincide, so they must have been clipped at the bounds;
			// see if they are on the same border of the bounds:
			if (rightPoint.x != newPoint.x
					&&  rightPoint.y != newPoint.y)
			{
				// They are on different borders of the bounds;
				// insert one or two corners of bounds as needed to hook them up:
				// (NOTE this will not be correct if the region should take up more than
				// half of the bounds rect, for then we will have gone the wrong way
				// around the bounds and included the smaller part rather than the larger)
				int rightCheck = BoundsCheck.check(rightPoint, bounds);
				int newCheck = BoundsCheck.check(newPoint, bounds);
				double px, py;
				if (rightCheck == BoundsCheck.RIGHT)
				{
					px = bounds.getMaxX();
					if (newCheck == BoundsCheck.BOTTOM)
					{
						py = bounds.getMaxY();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.TOP)
					{
						py = bounds.getMinY();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.LEFT)
					{
						if (rightPoint.y - bounds.y + newPoint.y - bounds.y < bounds.height)
						{
							py = bounds.getMinY();
						}
						else
						{
							py = bounds.getMaxY();
						}
						points.add(new Point(px, py));
						points.add(new Point(bounds.getMinX(), py));
					}
				}
				else if (rightCheck == BoundsCheck.LEFT)
				{
					px = bounds.getMinX();
					if (newCheck == BoundsCheck.BOTTOM)
					{
						py = bounds.getMaxY();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.TOP)
					{
						py = bounds.getMinY();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.RIGHT)
					{
						if (rightPoint.y - bounds.y + newPoint.y - bounds.y < bounds.height)
						{
							py = bounds.getMinY();
						}
						else
						{
							py = bounds.getMaxY();
						}
						points.add(new Point(px, py));
						points.add(new Point(bounds.getMaxX(), py));
					}
				}
				else if (rightCheck == BoundsCheck.TOP)
				{
					py = bounds.getMinY();
					if (newCheck == BoundsCheck.RIGHT)
					{
						px = bounds.getMaxX();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.LEFT)
					{
						px = bounds.getMinX();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.BOTTOM)
					{
						if (rightPoint.x - bounds.x + newPoint.x - bounds.x < bounds.width)
						{
							px = bounds.getMinX();
						}
						else
						{
							px = bounds.getMaxX();
						}
						points.add(new Point(px, py));
						points.add(new Point(px, bounds.getMaxY()));
					}
				}
				else if (rightCheck == BoundsCheck.BOTTOM)
				{
					py = bounds.getMaxY();
					if (newCheck == BoundsCheck.RIGHT)
					{
						px = bounds.getMaxX();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.LEFT)
					{
						px = bounds.getMinX();
						points.add(new Point(px, py));
					}
					else if (newCheck == BoundsCheck.TOP)
					{
						if (rightPoint.x - bounds.x + newPoint.x - bounds.x < bounds.width)
						{
							px = bounds.getMinX();
						}
						else
						{
							px = bounds.getMaxX();
						}
						points.add(new Point(px, py));
						points.add(new Point(px, bounds.getMinY()));
					}
				}
			}
			if (closingUp)
			{
				// newEdge's ends have already been added
				return;
			}
			points.add(newPoint);
		}
		Point newRightPoint = newEdge.getClippedEnds()[LR.other(newOrientation).value];
		if (!closeEnough(points.get(0), newRightPoint))
		{
			points.add(newRightPoint);
		}
	}

	double getX()
	{
		return _coord.x;
	}

	double getY()
	{
		return _coord.y;
	}

	double dist(ICoord p)
	{
		return Point.distance(p.getCoord(), this._coord);
	}

}
