// Annotate each edge with a noisy path, to make maps look more interesting.
// Author: amitp@cs.stanford.edu
// License: MIT

package jMapGen;

import jMapGen.com.nodename.Delaunay.DelaunayUtil;
import jMapGen.graph.Center;
import jMapGen.graph.Edge;

import java.util.Random;
import java.util.Vector;

public class NoisyEdges 
{
	static public double NOISY_LINE_TRADEOFF = 0.5;  // low: jagged vedge; high: jagged dedge

	public Vector<Vector<Point>> path0;  // edge index -> Vector.<Point>
	public Vector<Vector<Point>> path1;  // edge index -> Vector.<Point>

	public NoisyEdges() {path0 = new Vector<Vector<Point>>(); path1 = new Vector<Vector<Point>>();}

	// Build noisy line paths for each of the Voronoi edges. There are
	// two noisy line paths for each edge, each covering half the
	// distance: path0 is from v0 to the midpoint and path1 is from v1
	// to the midpoint. When drawing the polygons, one or the other
	// must be drawn in reverse order.
	public void buildNoisyEdges(Map map, Lava lava, Random random) {
		Center p; 
		Edge edge;
		for(int i = 0; i < map.centers.size(); i++) 
		{
			p = map.centers.get(i);
			for(int j = 0; j < p.borders.size(); j++) 
			{
				edge = p.borders.get(j);

				if (edge.dCenter0 != null && edge.dCenter1 != null && edge.vCorner0 != null && edge.vCorner1 != null && DelaunayUtil.getAtPosition(path0, edge.index) == null) 
				{
					double f = NOISY_LINE_TRADEOFF;
					Point t = Point.interpolate(edge.vCorner0.point, edge.dCenter0.point, f);
					Point q = Point.interpolate(edge.vCorner0.point, edge.dCenter1.point, f);
					Point r = Point.interpolate(edge.vCorner1.point, edge.dCenter0.point, f);
					Point s = Point.interpolate(edge.vCorner1.point, edge.dCenter1.point, f);

					int minLength = 10;
					if (edge.dCenter0.biome != edge.dCenter1.biome) minLength = 3;
					if (edge.dCenter0.isOcean() && edge.dCenter1.isOcean()) minLength = 100;
					if (edge.dCenter0.isCoast() || edge.dCenter1.isCoast()) minLength = 1;
					//if (edge.river == 1 || lava.lava.get(edge.index) != null) minLength = 1;

					DelaunayUtil.setAtPosition(path0, edge.index, buildNoisyLineSegments(random, edge.vCorner0.point, t, edge.midpoint, q, minLength));
					DelaunayUtil.setAtPosition(path1, edge.index, buildNoisyLineSegments(random, edge.vCorner1.point, s, edge.midpoint, r, minLength));
				}
			}
		}
	}


	// Helper function: build a single noisy line in a quadrilateral A-B-C-D,
	// and store the output points in a Vector.
	static public Vector<Point> buildNoisyLineSegments(Random random, Point A, Point B, Point C, Point D, double minLength) 
	{
		Vector<Point> points = new Vector<Point>();
		points.add(A);
		subdivide(random, A, B, C, D, minLength, points);
		points.add(C);
		return points;
	}

	static void subdivide(Random random, Point A, Point B, Point C, Point D, double minLength, Vector<Point> points) 
	{
		if (A.minus(C).getLength() < minLength || B.minus(D).getLength()  < minLength) 
		{
			return;
		}

		// Subdivide the quadrilateral
		double p = 0.2 + random.nextDouble()*0.6;  // vertical (along A-D and B-C)
		double q = 0.2 + random.nextDouble()*0.6;  // horizontal (along A-B and D-C)

		// Midpoints
		Point E = Point.interpolate(A, D, p);
		Point F = Point.interpolate(B, C, p);
		Point G = Point.interpolate(A, B, q);
		Point I = Point.interpolate(D, C, q);

		// Central point
		Point H = Point.interpolate(E, F, q);

		// Divide the quad into subquads, but meet at H
		double s = 1.0 - (-0.4 + random.nextDouble() * 0.8);
		double t = 1.0 - (-0.4 + random.nextDouble() * 0.8);

		subdivide(random, A, Point.interpolate(G, B, s), H, Point.interpolate(E, D, t), minLength, points);
		points.add(H);
		subdivide(random, H, Point.interpolate(F, C, s), C, Point.interpolate(I, D, t), minLength, points);
	}
}