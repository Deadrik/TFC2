package com.bioxx.jmapgen.com.nodename.delaunay;

import java.util.Vector;

public class EdgeReorderer
{
	private Vector<Edge> _edges;
	private Vector<LR> _edgeOrientations;

	public Vector<Edge> getEdges()
	{
		return _edges;
	}
	public Vector<LR> getEdgeOrientations()
	{
		return _edgeOrientations;
	}

	@SuppressWarnings("rawtypes")
	public EdgeReorderer(Vector<Edge> origEdges, Class criterion) throws Exception
	{
		if (criterion != Vertex.class && criterion != Site.class)
		{
			throw new Exception("Edges: criterion must be Vertex or Site");
		}
		_edges = new Vector<Edge>();
		_edgeOrientations = new Vector<LR>();
		if (origEdges.size() > 0)
		{
			_edges = reorderEdges(origEdges, criterion);
		}
	}

	public void dispose()
	{
		_edges = null;
		_edgeOrientations = null;
	}

	@SuppressWarnings("rawtypes")
	private Vector<Edge> reorderEdges(Vector<Edge> origEdges, Class criterion)
	{
		int i = 0;
		int n = origEdges.size();
		Edge edge;
		// we're going to reorder the edges in order of traversal
		Vector<Boolean> done = new Vector<Boolean>();
		int nDone = 0;

		for(i = 0; i < n; i++)
		{
			done.add(false);
		}

		Vector<Edge> newEdges = new Vector<Edge>();
		i = 0;
		edge = origEdges.get(i);
		newEdges.add(edge);
		_edgeOrientations.add(LR.LEFT);
		ICoord firstPoint = (criterion == Vertex.class) ? edge._leftVertex : edge.getLeftSite();
		ICoord lastPoint = (criterion == Vertex.class) ? edge._rightVertex : edge.getRightSite();

		if (firstPoint == Vertex.VERTEX_AT_INFINITY || lastPoint == Vertex.VERTEX_AT_INFINITY)
		{
			return new Vector<Edge>();
		}

		done.set(i, true);
		++nDone;

		int loops = 0;
		while (nDone < n)
		{
			loops++;
			for (i = 1; i < n; ++i)
			{
				if (done.get(i))
				{
					continue;
				}
				edge = origEdges.get(i);
				
				ICoord leftPoint = (criterion == Vertex.class) ? edge._leftVertex : edge.getLeftSite();
				ICoord rightPoint = (criterion == Vertex.class) ? edge._rightVertex : edge.getRightSite();
				
				if (leftPoint == Vertex.VERTEX_AT_INFINITY || rightPoint == Vertex.VERTEX_AT_INFINITY)
				{
					return new Vector<Edge>();
				}
				
				if (leftPoint == lastPoint)
				{
					lastPoint = rightPoint;
					_edgeOrientations.add(LR.LEFT);
					newEdges.add(edge);
					done.set(i, true);
				}
				else if (rightPoint == firstPoint)
				{
					firstPoint = leftPoint;
					DelaunayUtil.unshiftArray(_edgeOrientations, LR.LEFT);
					DelaunayUtil.unshiftArray(newEdges, edge);
					done.set(i, true);
				}
				else if (leftPoint == firstPoint)
				{
					firstPoint = rightPoint;
					DelaunayUtil.unshiftArray(_edgeOrientations, LR.RIGHT);
					DelaunayUtil.unshiftArray(newEdges, edge);
					done.set(i, true);
				}
				else if (rightPoint == lastPoint)
				{
					lastPoint = leftPoint;
					_edgeOrientations.add(LR.RIGHT);
					newEdges.add(edge);
					done.set(i, true);
				}
				if (done.get(i))
				{
					++nDone;
				}
			}
			
			if (loops > n)
			{
				break;
			}
		}

		return newEdges;
	}

}