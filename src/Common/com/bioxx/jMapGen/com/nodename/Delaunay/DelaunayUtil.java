package com.bioxx.jMapGen.com.nodename.Delaunay;

import java.util.Vector;

import com.bioxx.jMapGen.Point;
import com.bioxx.jMapGen.com.nodename.geom.LineSegment;

public class DelaunayUtil
{
	public static Vector<LineSegment> delaunayLinesForEdges(Vector<Edge> edges)
	{
		Vector<LineSegment> segments = new Vector<LineSegment>();
		for(int i = 0; i < edges.size(); i++)
		{
			segments.add(edges.get(i).delaunayLine());
		}
		return segments;
	}
	
	public static Vector<Edge> selectEdgesForSitePoint(Point coord, Vector<Edge> edgesToTest)
	{		
		Vector<Edge> edges = new Vector<Edge>();
		for(int i = 0; i < edges.size(); i++)
		{
			Edge e = edges.get(i);
			if((e.getLeftSite() != null && e.getLeftSite().getCoord() == coord)
					||  (e.getRightSite() != null && e.getRightSite().getCoord() == coord))
				edges.add(edges.get(i));
		}
		return edges;
	}
	
	public static Vector<LineSegment> visibleLineSegments(Vector<Edge> edges)
	{
		Vector<LineSegment> segments = new Vector<LineSegment>();
	
		for(int i = 0; i < edges.size(); i++)
		{
			Edge edge = edges.get(i);
			if (edge.getVisible())
			{
				Point p1 = edge.getClippedEnds()[LR.LEFT.value];
				Point p2 = edge.getClippedEnds()[LR.RIGHT.value];
				segments.add(new LineSegment(p1, p2));
			}
		}
		
		return segments;
	}
	
	public static void setAtPosition(Vector v, int index, Object value)
	{
		if(index >= v.size())
		{
			v.setSize(index+1);
		}
		v.set(index, value);
	}
	
	public static int unshiftArray(Vector v, Object value)
	{
		Vector n = new Vector();
		n.add(value);
		n.addAll(v);
		return n.size();
	}
	
	public static Object getAtPosition(Vector v, int index)
	{
		if(index > v.size())
		{
			v.setSize(index+1);
		}
		try
		{
			return v.get(index);
		}
		catch(IndexOutOfBoundsException e)
		{
			return null;
		}
	}
	
//	public static Vector<Edge> selectNonIntersectingEdges(BitmapData keepOutMask, Vector<Edge> edgesToTest):
//	{
//		if (keepOutMask == null)
//		{
//			return edgesToTest;
//		}
//		
//		var zeroPoint:Point = new Point();
//		return edgesToTest.filter(myTest);
//		
//		function myTest(edge:Edge, index:int, vector:Vector.<Edge>):Boolean
//		{
//			var delaunayLineBmp:BitmapData = edge.makeDelaunayLineBmp();
//			var notIntersecting:Boolean = !(keepOutMask.hitTest(zeroPoint, 1, delaunayLineBmp, zeroPoint, 1));
//			delaunayLineBmp.dispose();
//			return notIntersecting;
//		}
//	}
}