// Make a map out of a voronoi graph
// Original Author: amitp@cs.stanford.edu
// License: MIT
package jMapGen;

import jMapGen.com.nodename.Delaunay.DelaunayUtil;
import jMapGen.com.nodename.Delaunay.Voronoi;
import jMapGen.com.nodename.geom.LineSegment;
import jMapGen.graph.Center;
import jMapGen.graph.Corner;
import jMapGen.graph.CornerElevationSorter;
import jMapGen.graph.Edge;
import jMapGen.graph.MoistureComparator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

import za.co.iocom.math.MathUtil;


public class Map 
{
	public int NUM_POINTS = 4096*4;
	public int NUM_POINTS_SQ = (int) Math.sqrt(NUM_POINTS);

	// Passed in by the caller:
	public int SIZE;

	// Island shape is controlled by the islandRandom seed and the
	// type of island, passed in when we set the island shape. The
	// islandShape function uses both of them to determine whether any
	// point should be water or land.
	public IslandDefinition islandShape;


	// Island details are controlled by this random generator. The
	// initial map upon loading is always deterministic, but
	// subsequent maps reset this random number generator with a
	// random seed.
	public Random mapRandom = new Random();

	// These store the graph data
	public Vector<Point> points;  // Only useful during map construction
	public Vector<Center> centers;
	public Vector<Corner> corners;
	public Vector<Edge> edges;
	public Vector<River> rivers;
	public Vector<Lake> lakes;


	public Vector<Corner> riverSources;

	public long seed;

	public Map(int size, long s) 
	{
		SIZE = size;
		seed = s;
	}

	// Random parameters governing the overall shape of the island
	public void newIsland(long seed) 
	{
		islandShape = new IslandDefinition(seed, SIZE, 0.5);
		mapRandom.setSeed(seed);
		MathUtil.random = mapRandom;
	}

	public void newIsland(long seed, IslandDefinition is) 
	{
		islandShape = is;
		mapRandom.setSeed(seed);
		MathUtil.random = mapRandom;
		NUM_POINTS = is.SIZE*4;
		NUM_POINTS_SQ = (int) Math.sqrt(NUM_POINTS);
	}

	public void go() 
	{
		points = new Vector<Point>();
		edges = new Vector<Edge>();
		centers = new Vector<Center>();
		corners = new Vector<Corner>();
		lakes = new Vector<Lake>();
		rivers = new Vector<River>();

		points = this.generateHexagon(SIZE);
		//System.out.println("Points: " + points.size());
		Rectangle R = new Rectangle();
		R.setFrame(0, 0, SIZE, SIZE);
		//System.out.println("Starting Creating map Voronoi...");
		Voronoi voronoi = new Voronoi(points, R);
		//System.out.println("Finished Creating map Voronoi...");
		buildGraph(points, voronoi);

		// Determine the elevations and water at Voronoi corners.
		assignCornerElevations();

		// Determine polygon and corner type: ocean, coast, land.
		assignOceanCoastAndLand();

		redistributeElevations(landCorners(corners));
		//fixElevations(landCorners(corners));

		// Assign elevations to non-land corners
		for(Iterator<Corner> i = corners.iterator(); i.hasNext();)
		{
			Corner q = (Corner)i.next();
			if (q.ocean || q.coast) 
			{
				q.elevation = 0.0;
			}
		}

		// Polygon elevations are the average of their corners
		assignPolygonElevations();


		assignLakeElevations(lakeCenters(centers));

		// Determine downslope paths.
		calculateDownslopesCenter();

		// Determine watersheds: for every corner, where does it flow
		// out into the ocean? 
		calculateWatershedsCenter();

		// Create rivers.
		createRiversCenter();

		assignTerrainNoise2();

		assignMoisture();
		redistributeMoisture(landCenters(centers));

		assignBiomes();

		sortCornersClockwise();

		sortNeighborsClockwise();
	}

	private void assignTerrainNoise() {
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();
			if(!center.water && center.river == 0)
			{
				boolean nearWater = false;
				for(Iterator<Center> centerIter2 = center.neighbors.iterator(); centerIter2.hasNext();)
				{
					Center center2 = (Center)centerIter2.next();
					if(center2.water)
						nearWater  = true;
				}
				if(!nearWater && this.mapRandom.nextInt(100) < 30)
				{
					center.elevation += (0.15 * mapRandom.nextDouble() - 0.075);
				}
			}
		}
	}

	private void assignTerrainNoise2() {
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();
			if(!center.water && center.river == 0)
			{
				boolean nearWater = false;
				for(Iterator<Center> centerIter2 = center.neighbors.iterator(); centerIter2.hasNext();)
				{
					Center center2 = (Center)centerIter2.next();
					if(center2.water)
						nearWater  = true;
				}
				if(!nearWater && this.mapRandom.nextInt(100) < 50 && center.river == 0)
				{
					if(this.mapRandom.nextInt(100) < 70)
						center.elevation = getLowestNeighbor(center).elevation;
					else
						center.elevation = getHighestNeighbor(center).elevation;
				}
			}
		}
	}

	private Center getHighestNeighbor(Center c)
	{
		Center highest = c;
		for(Iterator<Center> centerIter2 = c.neighbors.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(highest == null || center2.elevation > highest.elevation)
				highest = center2;
		}
		return highest;
	}


	private Center getLowestNeighbor(Center c)
	{
		Center lowest = c;
		for(Iterator<Center> centerIter2 = c.neighbors.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(lowest == null || center2.elevation < lowest.elevation)
				lowest = center2;
		}
		return lowest;
	}

	private void sortCornersClockwise() {
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();
			Vector<Corner> sortedCorners = new Vector<Corner>();
			Point zeroPoint = new Point(center.point.x, center.point.y+1);
			for(Iterator<Corner> cornerIter = center.corners.iterator(); cornerIter.hasNext();)
			{
				Corner c = (Corner)cornerIter.next();
				if(sortedCorners.size() == 0)
					sortedCorners.add(c);
				else
				{
					boolean found = false;
					for(int i = 0; i < sortedCorners.size(); i++)
					{
						Corner c1 = sortedCorners.get(i);
						double c1angle = Math.atan2((c1.point.y - zeroPoint.y) , (c1.point.x - zeroPoint.x));
						double c2angle = Math.atan2((c.point.y - zeroPoint.y) , (c.point.x - zeroPoint.x));
						if(c2angle < c1angle)
						{
							sortedCorners.add(i, c);
							found = true;
							break;
						}
					}
					if(!found)
						sortedCorners.add(c);
				}
			}
			center.corners = sortedCorners;
		}
	}

	private void sortNeighborsClockwise() {
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();
			Vector<Center> sortedNeighbors = new Vector<Center>();
			Point zeroPoint = new Point(center.point.x, center.point.y+1);
			for(Iterator<Center> cornerIter = center.neighbors.iterator(); cornerIter.hasNext();)
			{
				Center c = (Center)cornerIter.next();
				if(sortedNeighbors.size() == 0)
					sortedNeighbors.add(c);
				else
				{
					boolean found = false;
					for(int i = 0; i < sortedNeighbors.size(); i++)
					{
						Center c1 = sortedNeighbors.get(i);
						double c1angle = Math.atan2((c1.point.y - zeroPoint.y) , (c1.point.x - zeroPoint.x));
						double c2angle = Math.atan2((c.point.y - zeroPoint.y) , (c.point.x - zeroPoint.x));
						if(c2angle < c1angle)
						{
							sortedNeighbors.add(i, c);
							found = true;
							break;
						}
					}
					if(!found)
						sortedNeighbors.add(c);
				}
			}
			center.neighbors = sortedNeighbors;
		}
	}

	public Vector<Point> generateHexagon(int size) {

		Vector<Point> points = new Vector<Point>();
		int N = (int) Math.sqrt(NUM_POINTS);
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) {
				points.add(new Point((0.5 + x)/N * size, (0.25 + 0.5*x%2 + y)/N * size));
			}
		}
		return points;
	}

	private void drawCorners(String suffix) {
		try 
		{
			System.out.println("Drawing hm-corners-"+suffix+".bmp");
			BufferedImage outBitmap = new BufferedImage(1024,1024,BufferedImage.TYPE_INT_RGB);
			Graphics2D g = (Graphics2D) outBitmap.getGraphics();

			g.setColor(Color.GRAY);
			for(int i = 0; i < edges.size(); i++)
			{
				Edge e = edges.get(i);

				if (e.river > 0)
					g.setColor(Color.BLUE);
				else 
					g.setColor(Color.GRAY);

				g.drawLine((int)e.vCorner0.point.x, (int)e.vCorner0.point.y, (int)e.vCorner1.point.x, (int)e.vCorner1.point.y);
			}

			float HSBOne = (1f/360f);
			float HSBGreen = HSBOne*60;
			for(int i = 0; i < corners.size(); i++)
			{
				Corner c = corners.get(i);


				//g.setColor(Color.getHSBColor(HSBGreen+((float)c.moisture*HSBGreen), 1, 1));
				if(c.water) g.setColor(Color.BLUE);
				else if(c.coast) g.setColor(Color.CYAN);
				else g.setColor(Color.YELLOW);

				g.drawRect((int)c.point.x, (int)c.point.y, 1,1);
			}
			ImageIO.write(outBitmap, "BMP", new File("hm-corners-"+suffix+".bmp"));
		} catch (IOException e) {e.printStackTrace();}
	}

	// Generate random points and assign them to be on the island or
	// in the water. Some water points are inland lakes; others are
	// ocean. We'll determine ocean later by looking at what's
	// connected to ocean.
	public Vector<Point> generateRandomPoints()
	{
		Point p; 
		int i; 
		Vector<Point> points = new Vector<Point>();

		for (i = 0; i < NUM_POINTS; i++) 
		{
			p = new Point(10 + mapRandom.nextDouble() * (SIZE-10),
					10 + mapRandom.nextDouble() * (SIZE-10));
			points.add(p);
		}
		return points;
	}

	// Although Lloyd relaxation improves the uniformity of polygon
	// sizes, it doesn't help with the edge lengths. Short edges can
	// be bad for some games, and lead to weird artifacts on
	// rivers. We can easily lengthen short edges by moving the
	// corners, but **we lose the Voronoi property**.  The corners are
	// moved to the average of the polygon centers around them. Short
	// edges become longer. Long edges tend to become shorter. The
	// polygons tend to be more uniform after this step.
	public void improveCorners() 
	{
		Vector<Point> newCorners = new Vector<Point>(corners.size());

		Point point; 
		int i; 
		Edge edge;

		// First we compute the average of the centers next to each corner.
		int count = 0;
		for(Corner q : corners)  
		{
			if (q.border) 
			{
				DelaunayUtil.setAtPosition(newCorners, q.index, q.point);
			} else {
				point = new Point(0.0, 0.0);
				for(Center r : q.touches)  
				{
					point.x += r.point.x;
					point.y += r.point.y;
				}
				point.x /= q.touches.size();
				point.y /= q.touches.size();
				DelaunayUtil.setAtPosition(newCorners, q.index, point);
				q.point = point;
			}
			count++;
		}

		// Move the corners to the new locations.
		for (i = 0; i < corners.size(); i++) {
			corners.get(i).point = newCorners.get(i);
		}

		// The edge midpoints were computed for the old corners and need
		// to be recomputed.
		for(i = 0; i < edges.size(); i++) 
		{
			edge = edges.get(i);
			if (edge.vCorner0 != null && edge.vCorner1 != null) 
			{
				edge.midpoint = edge.vCorner0.point != null && edge.vCorner1.point != null ? Point.interpolate(edge.vCorner0.point, edge.vCorner1.point, 0.5) : null;
			}
		}
	}


	// Create an array of corners that are on land only, for use by
	// algorithms that work only on land.  We return an array instead
	// of a vector because the redistribution algorithms want to sort
	// this array using Array.sortOn.
	public Vector<Corner> landCorners(Vector<Corner> corners) {
		Corner q; 
		Vector<Corner> locations = new Vector<Corner>();
		for (int i = 0; i < corners.size(); i++) {
			q = corners.get(i);
			if (!q.ocean && !q.coast) {
				locations.add(q);
			}
		}
		return locations;
	}

	public Vector<Center> landCenters(Vector<Center> centers) {
		Center q; 
		Vector<Center> locations = new Vector<Center>();
		for (int i = 0; i < centers.size(); i++) {
			q = centers.get(i);
			if (!q.ocean && !q.coast) {
				locations.add(q);
			}
		}
		return locations;
	}

	public Vector<Center> lakeCenters(Vector<Center> centers2) {
		Center q; 
		Vector<Center> locations = new Vector<Center>();
		for (int i = 0; i < centers2.size(); i++) {
			q = centers2.get(i);
			if (!q.ocean && q.water) {
				locations.add(q);
			}
		}
		return locations;
	}


	// Build graph data structure in 'edges', 'centers', 'corners',
	// based on information in the Voronoi results: point.neighbors
	// will be a list of neighboring points of the same type (corner
	// or center); point.edges will be a list of edges that include
	// that point. Each edge connects to four points: the Voronoi edge
	// edge.{v0,v1} and its dual Delaunay triangle edge edge.{d0,d1}.
	// For boundary polygons, the Delaunay edge will have one null
	// point, and the Voronoi edge may be null.
	public void buildGraph(Vector<Point> points, Voronoi voronoi) 
	{
		Center p; 
		Corner q; 
		Point point;
		Point other;

		Vector<jMapGen.com.nodename.Delaunay.Edge> libedges = voronoi.getEdges();
		HashMap<Point, Center> centerLookup = new HashMap<Point, Center>();

		//System.out.println("Starting buildGraph...");

		// Build Center objects for each of the points, and a lookup map
		// to find those Center objects again as we build the graph
		//System.out.println("Building Centers from " + points.size() + " total Points");
		for(int i = 0; i < points.size(); i++) 
		{
			point = points.get(i);
			p = new Center();
			p.index = centers.size();
			p.point = point;
			p.neighbors = new  Vector<Center>();
			p.borders = new Vector<Edge>();
			p.corners = new Vector<Corner>();
			centers.add(p);
			centerLookup.put(point, p);
		}

		// Workaround for Voronoi lib bug: we need to call region()
		// before Edges or neighboringSites are available
		for(int i = 0; i < centers.size(); i++) 
		{
			p = centers.get(i);
			voronoi.region(p.point);
		}


		// The Voronoi library generates multiple Point objects for
		// corners, and we need to canonicalize to one Corner object.
		// To make lookup fast, we keep an array of Points, bucketed by
		// x value, and then we only have to look at other Points in
		// nearby buckets. When we fail to find one, we'll create a new
		// Corner object.
		Vector<Vector<Corner>> _cornerMap = new Vector<Vector<Corner>>();
		_cornerMap.setSize((int)SIZE);

		for(int i = 0; i < libedges.size(); i++) 
		{
			jMapGen.com.nodename.Delaunay.Edge libedge = libedges.get(i);
			LineSegment dedge = libedge.delaunayLine();
			LineSegment vedge = libedge.voronoiEdge();

			// Fill the graph data. Make an Edge object corresponding to
			// the edge from the voronoi library.
			Edge edge = new Edge();
			edge.index = edges.size();
			edge.river = 0;
			edges.add(edge);
			edge.midpoint = vedge.p0 != null && vedge.p1 != null ? Point.interpolate(vedge.p0, vedge.p1, 0.5) : null;

			Corner c0 = makeCorner(vedge.p0, _cornerMap);
			Corner c1 = makeCorner(vedge.p1, _cornerMap);

			edge.setVoronoiEdge(c0, c1);
			edge.dCenter0 = centerLookup.get(dedge.p0);
			edge.dCenter1 = centerLookup.get(dedge.p1);

			// Centers point to edges. Corners point to edges.
			if (edge.dCenter0 != null) { edge.dCenter0.borders.add(edge); }
			if (edge.dCenter1 != null) { edge.dCenter1.borders.add(edge); }
			if (edge.vCorner0 != null) { edge.vCorner0.protrudes.add(edge); }
			if (edge.vCorner1 != null) { edge.vCorner1.protrudes.add(edge); }



			// Centers point to centers.
			if (edge.dCenter0 != null && edge.dCenter1 != null) 
			{
				addToCenterList(edge.dCenter0.neighbors, edge.dCenter1);
				addToCenterList(edge.dCenter1.neighbors, edge.dCenter0);
			}
			// Centers point to corners
			if (edge.dCenter0 != null) 
			{
				addToCornerList(edge.dCenter0.corners, edge.vCorner0);
				addToCornerList(edge.dCenter0.corners, edge.vCorner1);
			}
			if (edge.dCenter1 != null) 
			{
				addToCornerList(edge.dCenter1.corners, edge.vCorner0);
				addToCornerList(edge.dCenter1.corners, edge.vCorner1);
			}

			// Corners point to centers
			if (edge.vCorner0 != null) 
			{
				addToCenterList(edge.vCorner0.touches, edge.dCenter0);
				addToCenterList(edge.vCorner0.touches, edge.dCenter1);
			}
			if (edge.vCorner1 != null) 
			{
				addToCenterList(edge.vCorner1.touches, edge.dCenter0);
				addToCenterList(edge.vCorner1.touches, edge.dCenter1);
			}
		}

		//System.out.println("Finished buildGraph...");
	}

	@SuppressWarnings("unchecked")
	public Corner makeCorner(Point point, Vector<Vector<Corner>> _cornerMap) 
	{
		Corner q;
		int bucket;

		if (point == null) 
			return null;

		int minBucket = (int)(point.x) - 1;
		int maxBucket = (int)(point.x) + 1;

		for (bucket = minBucket; bucket <= maxBucket; bucket++) 
		{
			Vector<Corner> cornermap = (Vector<Corner>) DelaunayUtil.getAtPosition(_cornerMap, bucket);
			for(int i = 0; cornermap != null && i < cornermap.size(); i++) 
			{
				q = cornermap.get(i);
				double dx = point.x - q.point.x;
				double dy = point.y - q.point.y;
				double dxdy = dx*dx + dy*dy;
				if (dxdy < 1E-6) 
				{
					return q;
				}
			}
		}

		bucket = (int)(point.x);
		if (_cornerMap.size() <= bucket || _cornerMap.get(bucket) == null)
		{
			DelaunayUtil.setAtPosition(_cornerMap, bucket, new Vector<Corner>());
		}
		q = new Corner();
		q.index = corners.size();
		corners.add(q);

		q.point = point;
		q.border = (point.x == 0 || point.x == SIZE
				|| point.y == 0 || point.y == SIZE);
		q.touches = new Vector<Center>();
		q.protrudes = new Vector<Edge>();
		q.adjacent = new Vector<Corner>();		

		_cornerMap.get(bucket).add(q);

		return q;

	}


	void addToCornerList(Vector<Corner> v, Corner x) 
	{
		if (x != null && !v.contains(x)) { v.add(x); }
	}

	void addToCenterList(Vector<Center> v, Center x) 
	{
		if (x != null && v.indexOf(x) < 0) { v.add(x); }
	}


	// Determine elevations and water at Voronoi corners. By
	// construction, we have no local minima. This is important for
	// the downslope vectors later, which are used in the river
	// construction algorithm. Also by construction, inlets/bays
	// push low elevation areas inland, which means many rivers end
	// up flowing out through them. Also by construction, lakes
	// often end up on river paths because they don't raise the
	// elevation as much as other terrain does.
	public void assignCornerElevations() 
	{
		Corner baseCorner, adjacentCorner;
		LinkedList<Corner> queue = new LinkedList<Corner>();

		/**
		 * First we check each corner to see if it is land or water
		 * */
		for(Corner c : corners)
		{
			c.water = !inside(c.point);

			if (c.border) 
			{
				c.elevation = 0;
				queue.add(c);
			}
		}

		/**
		 * Next we assign the borders to have 0 elevation and all other corners to have MAX_VALUE. We also add
		 * the border points to a queue which contains all start points for elevation distribution.
		 */

		// Traverse the graph and assign elevations to each point. As we
		// move away from the map border, increase the elevations. This
		// guarantees that rivers always have a way down to the coast by
		// going downhill (no local minima).
		while (queue.size() > 0) {
			baseCorner = queue.pollFirst();

			for(int i = 0; i < baseCorner.adjacent.size(); i++)
			{

				adjacentCorner = baseCorner.adjacent.get(i);

				if(!adjacentCorner.border)
				{
					// Every step up is epsilon over water or 1 over land. The
					// number doesn't matter because we'll rescale the
					// elevations later.				
					double newElevation = 0.000000001 + baseCorner.elevation;

					if (!baseCorner.water && !adjacentCorner.water && newElevation < 0.20) 
					{
						newElevation += 0.05;
					}
					else if (!baseCorner.water && !adjacentCorner.water) 
					{
						newElevation += 1;
					}
					// If this point changed, we'll add it to the queue so
					// that we can process its neighbors too.
					if (newElevation < adjacentCorner.elevation) 
					{
						adjacentCorner.elevation = newElevation;
						highestElevation = highestElevation < newElevation ? newElevation : highestElevation;
						queue.add(adjacentCorner);
					}
				}
			}
		}
	}

	double highestElevation = 0;

	public void fixElevations(Vector<Corner> locations) 
	{
		for(Corner c : locations)
		{
			c.elevation /= highestElevation;
		}
	}

	public Vector<Corner> sortElevation(Vector<Corner> locations)
	{
		Vector<Corner> locationsOut = new Vector<Corner>();
		for(Iterator<Corner> iter = locations.iterator(); iter.hasNext();)
		{
			Corner c = iter.next();
			for(int o = 0; o < locationsOut.size(); o++)
			{
				Corner cOut = locationsOut.get(o);
				if(cOut.elevation < c.elevation)
				{
					locationsOut.add(o, c);
					if(cOut.elevation < 0)
						cOut.elevation = 0;
					break;
				}
			}
		}
		return locationsOut;
	}

	public Vector<Corner> sortMoisture(Vector<Corner> locations)
	{
		Vector<Corner> locationsOut = new Vector<Corner>();
		for(Iterator<Corner> iter = locations.iterator(); iter.hasNext();)
		{
			Corner c = iter.next();
			for(int o = 0; o < locationsOut.size(); o++)
			{
				Corner cOut = locationsOut.get(o);
				if(cOut.moisture < c.moisture)
				{
					locationsOut.add(o, c);
					break;
				}
			}
		}
		return locationsOut;
	}

	// Change the overall distribution of elevations so that lower
	// elevations are more common than higher
	// elevations. Specifically, we want elevation X to have frequency
	// (1-X).  To do this we will sort the corners, then set each
	// corner to its desired elevation.
	public void redistributeElevations(Vector<Corner> locations) {
		// SCALE_FACTOR increases the mountain area. At 1.0 the maximum
		// elevation barely shows up on the map, so we set it to 1.1.
		double SCALE_FACTOR = 1.0;

		Collections.sort(locations, new CornerElevationSorter());
		int locationsSize = locations.size();

		for (int i = 0; i < locationsSize; i++) {
			// Let y(x) be the total area that we want at elevation <= x.
			// We want the higher elevations to occur less than lower
			// ones, and set the area to be y(x) = 1 - (1-x)^2.
			double y = (double)i/(double)(locationsSize-1);
			// Now we have to solve for x, given the known y.
			//  *  y = 1 - (1-x)^2
			//  *  y = 1 - (1 - 2x + x^2)
			//  *  y = 2x - x^2
			//  *  x^2 - 2x + y = 0
			// From this we can use the quadratic equation to get:
			double sqrtScale = Math.sqrt(SCALE_FACTOR);
			double scale1Y = SCALE_FACTOR*(1-y);
			double sqrtscale1Y = Math.sqrt(scale1Y);

			double x = sqrtScale - sqrtscale1Y;
			if (x > 1.0) x = 1.0;  // TODO: does this break downslopes?
			locations.get(i).elevation = x;
		}
	}


	// Change the overall distribution of moisture to be evenly distributed.	
	public void redistributeMoisture(Vector<Center> locations) {
		int i;
		Collections.sort(locations, new MoistureComparator());
		Center c1;
		for (i = 0; i < locations.size(); i++) 
		{
			c1 = locations.get(i);
			double m = i/(double)(locations.size());
			c1.moisture = m;
		}
	}


	// Determine polygon and corner types: ocean, coast, land.
	public void assignOceanCoastAndLand() {
		// Compute polygon attributes 'ocean' and 'water' based on the
		// corner attributes. Count the water corners per
		// polygon. Oceans are all polygons connected to the edge of the
		// map. In the first pass, mark the edges of the map as ocean;
		// in the second pass, mark any water-containing polygon
		// connected an ocean as ocean.
		LinkedList<Center> queue = new LinkedList<Center>();
		Center p = null, r = null; 
		Corner q; 
		int numWater;

		for(int i = 0; i < centers.size(); i++)
		{
			p = centers.get(i);
			numWater = 0;
			for(int j = 0; j < p.corners.size(); j++)
			{
				q = p.corners.get(j);
				if (q.border) {
					p.border = true;
					p.ocean = true;
					q.water = true;
					queue.add(p);
				}
				if (q.water) {
					numWater += 1;
				}
			}
			p.water = (p.ocean || numWater >= p.corners.size() * this.islandShape.lakeThreshold);
		}
		while (queue.size() > 0) 
		{
			p = queue.pop();

			for(int j = 0; j < p.neighbors.size(); j++)
			{
				r = p.neighbors.get(j);
				if (r.water && !r.ocean) {
					r.ocean = true;
					queue.add(r);
				}
			}
		}

		int numOcean = 0;
		int numLand = 0;

		// Set the polygon attribute 'coast' based on its neighbors. If
		// it has at least one ocean and at least one land neighbor,
		// then this is a coastal polygon.
		for(int i = 0; i < centers.size(); i++)
		{
			p = centers.get(i);
			numOcean = 0;
			numLand = 0;

			for(int j = 0; j < p.neighbors.size(); j++)
			{
				r = p.neighbors.get(j);
				numOcean += (r.ocean ? 1 : 0);
				numLand += (!r.water ? 1 : 0);
			}

			p.coast = (numOcean > 0) && (numLand > 0);
			p.coastWater = p.ocean && (numLand > 0);
		}


		// Set the corner attributes based on the computed polygon
		// attributes. If all polygons connected to this corner are
		// ocean, then it's ocean; if all are land, then it's land;
		// otherwise it's coast.
		for(int j = 0; j < corners.size(); j++)
		{
			q = corners.get(j);
			numOcean = 0;
			numLand = 0;
			for(int i = 0; i < q.touches.size(); i++)
			{
				p = q.touches.get(i);
				numOcean += (p.ocean ? 1 : 0);
				numLand += (!p.water ? 1 : 0);
			}
			q.ocean = (numOcean == q.touches.size());
			q.coast = (numOcean > 0) && (numLand > 0);
			q.water = q.border || ((numLand != q.touches.size()) && !q.coast);

		}
	}


	// Polygon elevations are the average of the elevations of their corners.
	public void assignPolygonElevations() 
	{
		Center p; 
		Corner q; 
		double sumElevation;
		for(int i = 0; i < centers.size(); i++)
		{
			p = centers.get(i);
			sumElevation = 0.0;
			for(int j = 0; j < p.corners.size(); j++)
			{
				q = p.corners.get(j);
				sumElevation += q.elevation;
			}
			p.elevation = sumElevation / p.corners.size();
		}
	}

	public void assignLakeElevations(Vector<Center> centers) 
	{
		for(Center c : centers)
		{
			//if there are current no lakes, or the current center doesnt exist in any lakes already
			Lake exists = centerInExistingLake(c);
			if(lakes.isEmpty() || exists == null)
			{
				//default the lakeElevation 1
				double lakeElev = 1;

				//Create a new lake
				Lake lake = new Lake();

				//contains a list of centers that need to check outward to find the bounds of the lake.
				LinkedList<Center> centersToCheck = new LinkedList<Center>();

				// add the current center to the centersToCheck list
				lake.addCenter(c);
				//Add the center to the queue for outward propagation
				centersToCheck.add(c);

				while (centersToCheck.size() > 0) 
				{
					Center baseCenter = centersToCheck.pollFirst();

					for(Center adj : baseCenter.neighbors)
					{
						if(!lake.hasCenter(adj) && adj.water && !adj.ocean)
						{
							lake.addCenter(adj);
							centersToCheck.add(adj);
						}
					}			
				}
				lakes.add(lake);
			}
		}
		for(int i = 0; i < lakes.size(); i++)
		{
			Lake lake = lakes.get(i);
			for(Center c : lake.centers)
			{
				c.elevation = lake.lowestCenter.elevation;
			}
		}
	}

	private Lake centerInExistingLake(Center center)
	{
		for(Lake lake : lakes)
		{
			if(lake.hasCenter(center))
				return lake;
		}
		return null;
	}

	private void fixLakeCorners(Vector<Center> lakeCenters)
	{
		for(Center c : lakeCenters)
		{
			for(Corner cn : c.corners)
			{
				if(!cn.isShoreline())
					cn.elevation = c.elevation;
			}
		}
	}

	// Calculate downslope pointers.  At every point, we point to the
	// point downstream from it, or to itself.  This is used for
	// generating rivers and watersheds.
	private Vector<Center> centerInExistingLake(Vector<Vector<Center>> Lakes, Center center)
	{
		for(Vector<Center> lake : Lakes)
		{
			if(lake.contains(center))
				return lake;
		}
		return null;
	}

	public void calculateDownslopes() 
	{
		Corner upCorner, tempCorner, downCorner;

		for(int j = 0; j < corners.size(); j++)
		{
			upCorner = corners.get(j);
			downCorner = upCorner;
			for(int i = 0; i < upCorner.adjacent.size(); i++)
			{
				tempCorner= upCorner.adjacent.get(i);
				if (tempCorner.elevation <= downCorner.elevation) 
				{
					downCorner = tempCorner;
				}
			}	
			upCorner.downslope = downCorner;
		}
	}

	public void calculateDownslopesCenter() 
	{
		Center upCorner, tempCorner, downCorner;

		for(int j = 0; j < centers.size(); j++)
		{
			upCorner = centers.get(j);
			downCorner = upCorner;
			for(int i = 0; i < upCorner.neighbors.size(); i++)
			{
				tempCorner= upCorner.neighbors.get(i);
				if (tempCorner.elevation <= downCorner.elevation) 
				{
					downCorner = tempCorner;
				}
			}	
			upCorner.downslope = downCorner;
		}
	}

	public void calculateWatershedsCenter() 
	{
		Center workCorner, tempCorner; int i; boolean changed;

		// Initially the watershed pointer points downslope one step.      
		for(int j = 0; j < centers.size(); j++)
		{
			workCorner = centers.get(j);
			workCorner.watershed = workCorner;
			if (!workCorner.ocean && !workCorner.coast) 
			{
				workCorner.watershed = workCorner.downslope;
			}
		}
		// Follow the downslope pointers to the coast. Limit to 100
		// iterations although most of the time with NUM_POINTS=2000 it
		// only takes 20 iterations because most points are not far from
		// a coast.  TODO: can run faster by looking at
		// p.watershed.watershed instead of p.downslope.watershed.
		for (i = 0; i < 100; i++) {
			changed = false;
			for(int j = 0; j < centers.size(); j++)
			{
				workCorner = centers.get(j);
				if (!workCorner.ocean && !workCorner.coast && !workCorner.watershed.coast) {
					tempCorner = workCorner.downslope.watershed;
					if (!tempCorner.ocean) workCorner.watershed = tempCorner;
					changed = true;
				}
			}
			if (!changed) break;
		}
		// How big is each watershed?
		for(int j = 0; j < centers.size(); j++)
		{
			workCorner = centers.get(j);
			tempCorner = workCorner.watershed;
		}

	}

	public void createRiversCenter() 
	{
		Center c;
		Center prev;

		Vector<Center> possibleStarts = new Vector<Center>();

		for (int i = 0; i < SIZE/6; i++) 
		{
			possibleStarts.add(centers.get(mapRandom.nextInt(centers.size()-1)));
		}

		for (int i = 0; i < lakes.size(); i++) 
		{
			possibleStarts.add(lakes.get(i).lowestCenter);
		}

		for (int i = 0; i < possibleStarts.size(); i++) 
		{
			c = possibleStarts.get(i);

			if (c.ocean || c.elevation < 0.2 || c.elevation > 0.85) continue;

			River r = new River();

			int count = 0;
			while (true)
			{
				if (c == c.downslope || count > 250) 
				{
					break;
				}
				count++;
				Center next = getNextRiverCenter(c);
				c.river = c.river + 1;
				/*if(c.downslope.riverUp == null)
					c.downslope.riverUp = c;*/
				r.addCenter(c);
				if(c.water && (c.downslope.water || c.downslope == null))
					break;
				c = next;

			}
			if(r.centers.size() > 5)
				rivers.add(r);
		}	
	}

	public Center getNextRiverCenter(Center c)
	{
		Center next = c.downslope;
		if(this.mapRandom.nextInt(100) < 30 && c.river == 0)
		{
			for(Center n : c.neighbors)
			{
				if(n.river > 0)
					return n;
				if(n.ocean || n.water)
					return n;
				if(n.elevation <= c.elevation && n != c.downslope)
					next = n;
			}
		}
		return next;
	}

	// Calculate moisture. Freshwater sources spread moisture: rivers
	// and lakes (not oceans). Saltwater sources have moisture but do
	// not spread it (we set it at the end, after propagation).
	public void assignMoisture() 
	{
		LinkedList<Center> queue = new LinkedList<Center>();
		// Fresh water
		for(Center cr : centers)
		{
			if ((cr.water || cr.river > 0) && !cr.ocean) {
				cr.moisture = cr.river > 0? Math.min(3.0, (0.1 * (double)cr.river)) : 1.0;
				queue.push(cr);
			} else {
				cr.moisture = 0.0;
			}
		}
		while (queue.size() > 0) 
		{
			Center q = queue.pop();

			for(Center adjacent : q.neighbors)
			{
				double newMoisture = q.moisture * 0.9;
				if (newMoisture > adjacent.moisture) {
					adjacent.moisture = newMoisture;
					queue.push(adjacent);
				}
			}
		}
		// Salt water
		for(Center cr : centers)
		{
			if (cr.ocean || cr.coast) 
			{
				cr.moisture = 1.0;
			}
		}
	}


	// Polygon moisture is the average of the moisture at corners
	public void assignPolygonMoisture() {
		double sumMoisture;
		for(Center p : centers)
		{
			sumMoisture = 0.0;
			for(Corner q : p.corners)
			{
				if (q.moisture > 1.0) q.moisture = 1.0;
				sumMoisture += q.moisture;
			}
			p.moisture = sumMoisture / p.corners.size();
		}
	}


	// Assign a biome type to each polygon. If it has
	// ocean/coast/water, then that's the biome; otherwise it depends
	// on low/high elevation and low/medium/high moisture. This is
	// roughly based on the Whittaker diagram but adapted to fit the
	// needs of the island map generator.
	static public BiomeType getBiome(Center p) {
		if (p.ocean) {
			return BiomeType.OCEAN;
		} else if (p.water) {
			if (p.elevation < 0.1) return BiomeType.MARSH;
			if (p.elevation > 0.8) return BiomeType.ICE;
			return BiomeType.LAKE;
		} else if (p.coast) {
			return BiomeType.BEACH;
		} else if (p.elevation > 0.8) {
			if (p.moisture > 0.50) return BiomeType.SNOW;
			else if (p.moisture > 0.33) return BiomeType.TUNDRA;
			else if (p.moisture > 0.16) return BiomeType.BARE;
			else return BiomeType.SCORCHED;
		} else if (p.elevation > 0.6) {
			if (p.moisture > 0.66) return BiomeType.TAIGA;
			else if (p.moisture > 0.33) return BiomeType.SHRUBLAND;
			else return BiomeType.TEMPERATE_DESERT;
		} else if (p.elevation > 0.3) {
			if (p.moisture > 0.83) return BiomeType.TEMPERATE_RAIN_FOREST;
			else if (p.moisture > 0.50) return BiomeType.TEMPERATE_DECIDUOUS_FOREST;
			else if (p.moisture > 0.16) return BiomeType.GRASSLAND;
			else return BiomeType.TEMPERATE_DESERT;
		} else {
			if (p.moisture > 0.66) return BiomeType.TROPICAL_RAIN_FOREST;
			else if (p.moisture > 0.33) return BiomeType.TROPICAL_SEASONAL_FOREST;
			else if (p.moisture > 0.16) return BiomeType.GRASSLAND;
			else return BiomeType.SUBTROPICAL_DESERT;
		}
	}

	public void assignBiomes() 
	{
		for(int j = 0; j < centers.size(); j++)
		{
			Center p = centers.get(j);
			p.biome = getBiome(p);
		}
	}


	// Look up a Voronoi Edge object given two adjacent Voronoi
	// polygons, or two adjacent Voronoi corners
	public Edge lookupEdgeFromCenter(Center p, Center r) {
		for(int j = 0; j < p.borders.size(); j++)
		{
			Edge edge = p.borders.get(j);

			if (edge.dCenter0 == r || edge.dCenter1 == r) return edge;
		}
		return null;
	}

	public Edge lookupEdgeFromCorner(Corner q, Corner s) 
	{
		for(int j = 0; j < q.protrudes.size(); j++)
		{
			Edge edge = q.protrudes.get(j);
			if (edge.vCorner0 == s || edge.vCorner1 == s) return edge;
		}
		return null;
	}


	// Determine whether a given point should be on the island or in the water.
	public Boolean inside(Point p) 
	{
		return islandShape.insidePerlin(p);
	}

	double elevationBucket(Center p) 
	{
		if (p.ocean) return -1;
		else return Math.floor(p.elevation*10);
	}

	public Center getClosestCenter(Point p)
	{
		Center closest = null;
		double distance = Double.MAX_VALUE;

		for (int i = 1; i < centers.size(); i++)
		{
			double newDist = p.distanceSq(centers.get(i).point);
			if(newDist < distance)
			{
				distance = newDist;
				closest = centers.get(i);
			}
		}
		if(closest == null)
			System.out.println("Failed center check");
		return closest;
	}

	/**
	 * @return nearest Center point for the contianing hex
	 */
	public Center getSelectedHexagon(Point p)
	{
		//First we place the point in a local grid between 0 and the map width
		p.x = p.x % SIZE;
		p.y = p.y % SIZE;

		//If the point has any negative numbers, we add the map width to make it positive and get the correct location
		if(p.x < 0)
			p.x += SIZE;
		if(p.y < 0)
			p.y += SIZE;

		//Form the best guess coordinates
		int x = (int)Math.floor((p.x /(SIZE/NUM_POINTS_SQ)));
		int y = (int)Math.floor((p.y /(SIZE/NUM_POINTS_SQ)));

		Center orig = this.centers.get( NUM_POINTS_SQ*x+y);
		//Get the inCircle radius
		double r = 0;
		if(orig.corners.size() > 0)
		{
			r = Math.sqrt(3)/2*(orig.borders.get(0).midpoint.distanceSq(orig.point));
		}
		Center bestGuess = orig;
		double dist = p.distanceSq(orig.point);
		//Perform a quick test to see if the point is within the inCircle. If it is then we can skip the rest of the method and return the Best Guess
		if(dist < r)
			return bestGuess;

		for (int i = 0; i < orig.neighbors.size(); i++)
		{
			Center guess = orig.neighbors.get(i);
			double newDist = p.distanceSq(guess.point);
			if(newDist < dist)
			{
				dist = newDist;
				bestGuess = guess;
				if(dist < r)
					return bestGuess;
			}
			for (int j = 0; j < guess.neighbors.size(); j++)
			{
				Center guess2 = guess.neighbors.get(j);
				double newDist2 = p.distanceSq(guess2.point);
				if(newDist2 < dist)
				{
					dist = newDist2;
					bestGuess = guess2;
					if(dist < r)
						return bestGuess;
				}
			}
		}

		return bestGuess;
	}

	public Corner getClosestCorner(Point p)
	{
		Corner closest = corners.get(0);
		double distance = p.distance(corners.get(0).point);

		for (int i = 1; i < corners.size(); i++)
		{
			double newDist = p.distance(corners.get(i).point);
			if(newDist < distance)
			{
				distance = newDist;
				closest = corners.get(i);
			}
		}
		return closest;
	}
}