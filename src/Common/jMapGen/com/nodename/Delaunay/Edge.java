package jMapGen.com.nodename.Delaunay;

import jMapGen.Point;
import jMapGen.com.nodename.geom.LineSegment;

import java.awt.Rectangle;
import java.util.Comparator;
import java.util.Vector;
/**
 * The line segment connecting the two Sites is part of the Delaunay triangulation;
 * the line segment connecting the two Vertices is part of the Voronoi diagram
 * @author ashaw
 * 
 */
public class Edge implements  Comparator<Edge>
{
	private static Vector<Edge> _pool = new Vector<Edge>();
	
	public static final Edge DELETED = new Edge();

	public int _edgeIndex;

	public Edge()
	{			
		_edgeIndex = _nedges++;
		init();
	}

	private void init()
	{	
		_sites = new Site[2];
	}

	/**
	 * This is the only way to create a new Edge 
	 * @param site0
	 * @param site1
	 * @return 
	 * @throws Exception 
	 * 
	 */
	public static Edge createBisectingEdge(Site site0, Site site1)
	{
		double dx, dy, absdx, absdy;
		double a, b, c;

		dx = site1.getX() - site0.getX();
		dy = site1.getY() - site0.getY();
		absdx = dx > 0 ? dx : -dx;
		absdy = dy > 0 ? dy : -dy;
		c = site0.getX() * dx + site0.getY() * dy + (dx * dx + dy * dy) * 0.5;
		if (absdx > absdy)
		{
			a = 1.0; b = dy/dx; c /= dx;
		}
		else
		{
			b = 1.0; a = dx/dy; c /= dy;
		}

		Edge edge = Edge.create();

		edge.setLeftSite(site0);
		edge.setRightSite(site1);
		
		site0.addEdge(edge);
		site1.addEdge(edge);

		edge._leftVertex = null;
		edge._rightVertex = null;

		edge.a = a; edge.b = b; edge.c = c;
		//trace("createBisectingEdge: a ", edge.a, "b", edge.b, "c", edge.c);

		return edge;
	}

	private static Edge create()
	{
		Edge edge;
		if (_pool.size() > 0)
		{
			edge = _pool.lastElement();
			_pool.remove(_pool.size()-1);
			edge.init();
		}
		else
		{
			edge = new Edge();
		}
		return edge;
	}

	public LineSegment delaunayLine()
	{
		// draw a line connecting the input Sites for which the edge is a bisector:
		Site ls = getLeftSite();
		Site rs = getRightSite();
		
		if(ls != null && rs != null)
			return new LineSegment(ls.getCoord(), rs.getCoord());
		
		return null;
	}

	public LineSegment voronoiEdge()
	{
		if (!getVisible()) return new LineSegment(null, null);
		return new LineSegment((Point)_clippedVertices[LR.LEFT.value],
				(Point)_clippedVertices[LR.RIGHT.value]);
	}

	public static int _nedges = 0;

	// the equation of the edge: ax + by = c
	public double a, b, c;

	// the two Voronoi vertices that the edge connects
	//		(if one of them is null, the edge extends to infinity)
	Vertex _leftVertex;
	public Vertex getLeftVertex()
	{
		return _leftVertex;
	}

	Vertex _rightVertex;
	public Vertex getRightVertex()
	{
		return _rightVertex;
	}
	
	Vertex vertex(LR leftRight)
	{
		return (leftRight == LR.LEFT) ? _leftVertex : _rightVertex;
	}
	
	void setVertex(LR leftRight, Vertex v)
	{
		if (leftRight == LR.LEFT)
		{
			_leftVertex = v;
		}
		else
		{
			_rightVertex = v;
		}
	}

	boolean isPartOfConvexHull()
	{
		return (_leftVertex == null || _rightVertex == null);
	}

	public double sitesDistance()
	{
		return Point.distance(this.getLeftSite().getCoord(), this.getRightSite().getCoord());
	}

	public static int compareSitesDistances_MAX(Edge edge0, Edge edge1)
	{
		double length0 = edge0.sitesDistance();
		double length1 = edge1.sitesDistance();
		if (length0 < length1)
		{
			return 1;
		}
		if (length0 > length1)
		{
			return -1;
		}
		return 0;
	}

	public static int compareSitesDistances(Edge edge0, Edge edge1)
	{
		return - compareSitesDistances_MAX(edge0, edge1);
	}

	// Once clipVertices() is called, this Dictionary will hold two Points
	// representing the clipped coordinates of the left and right ends...
	private Point[] _clippedVertices;
	public Point[] getClippedEnds()
	{
		return _clippedVertices;
	}
	// unless the entire Edge is outside the bounds.
	// In that case visible will be false:
	boolean getVisible()
	{
		return _clippedVertices != null;
	}

	// the two input Sites for which this Edge is a bisector:
	private Site[] _sites;

	void setLeftSite(Site s)
	{
		_sites[LR.LEFT.value] = s;
	}
	Site getLeftSite()
	{
		return _sites[LR.LEFT.value];
	}
	void setRightSite(Site s)
	{
		_sites[LR.RIGHT.value] = s;
	}
	Site getRightSite()
	{
		return _sites[LR.RIGHT.value];
	}
	Site site(LR leftRight)
	{
		return _sites[leftRight.value];
	}

	

	public String toString()
	{
		return "Edge " + _edgeIndex + "; sites " + _sites[LR.LEFT.value] + ", " + _sites[LR.RIGHT.value]
				+ "; endVertices " + (_leftVertex != null ? _leftVertex.getVertexIndex() : "null") + ", "
				+ (_rightVertex != null ? _rightVertex.getVertexIndex() : "null") + "::";
	}

	/**
	 * Set _clippedVertices to contain the two ends of the portion of the Voronoi edge that is visible
	 * within the bounds.  If no part of the Edge falls within the bounds, leave _clippedVertices null. 
	 * @param bounds
	 * 
	 */
	void clipVertices(Rectangle bounds)
	{
		double xmin = bounds.x;
		double ymin = bounds.y;
		double xmax = bounds.getMaxX();
		double ymax = bounds.getMaxY();

		Vertex vertex0, vertex1;
		double x0, x1, y0, y1;

		if (a == 1.0 && b >= 0.0)
		{
			vertex0 = _rightVertex;
			vertex1 = _leftVertex;
		}
		else 
		{
			vertex0 = _leftVertex;
			vertex1 = _rightVertex;
		}

		if (a == 1.0)
		{
			y0 = ymin;
			if (vertex0 != null && vertex0.getY() > ymin)
			{
				y0 = vertex0.getY();
			}
			if (y0 > ymax)
			{
				return;
			}
			x0 = c - b * y0;

			y1 = ymax;
			if (vertex1 != null && vertex1.getY() < ymax)
			{
				y1 = vertex1.getY();
			}
			if (y1 < ymin)
			{
				return;
			}
			x1 = c - b * y1;

			if ((x0 > xmax && x1 > xmax) || (x0 < xmin && x1 < xmin))
			{
				return;
			}

			if (x0 > xmax)
			{
				x0 = xmax; y0 = (c - x0)/b;
			}
			else if (x0 < xmin)
			{
				x0 = xmin; y0 = (c - x0)/b;
			}

			if (x1 > xmax)
			{
				x1 = xmax; y1 = (c - x1)/b;
			}
			else if (x1 < xmin)
			{
				x1 = xmin; y1 = (c - x1)/b;
			}
		}
		else
		{
			x0 = xmin;
			if (vertex0 != null && vertex0.getX() > xmin)
			{
				x0 = vertex0.getX();
			}
			if (x0 > xmax)
			{
				return;
			}
			y0 = c - a * x0;

			x1 = xmax;
			if (vertex1 != null && vertex1.getX() < xmax)
			{
				x1 = vertex1.getX();
			}
			if (x1 < xmin)
			{
				return;
			}
			y1 = c - a * x1;

			if ((y0 > ymax && y1 > ymax) || (y0 < ymin && y1 < ymin))
			{
				return;
			}

			if (y0 > ymax)
			{
				y0 = ymax; x0 = (c - y0)/a;
			}
			else if (y0 < ymin)
			{
				y0 = ymin; x0 = (c - y0)/a;
			}

			if (y1 > ymax)
			{
				y1 = ymax; x1 = (c - y1)/a;
			}
			else if (y1 < ymin)
			{
				y1 = ymin; x1 = (c - y1)/a;
			}
		}

		_clippedVertices = new Point[2];
		if (vertex0 == _leftVertex)
		{
			_clippedVertices[LR.LEFT.value] = new Point(x0, y0);
			_clippedVertices[LR.RIGHT.value] = new Point(x1, y1);
		}
		else
		{
			_clippedVertices[LR.RIGHT.value] = new Point(x0, y0);
			_clippedVertices[LR.LEFT.value] = new Point(x1, y1);
		}
	}

	@Override
	public int compare(Edge arg0, Edge arg1) {

		return compareSitesDistances(arg0, arg1);
	}

}