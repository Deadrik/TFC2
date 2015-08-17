// Make a map out of a voronoi graph
// Original Author: amitp@cs.stanford.edu
// License: MIT
package com.bioxx.jmapgen;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.CanyonAttribute;
import com.bioxx.jmapgen.attributes.GorgeAttribute;
import com.bioxx.jmapgen.attributes.LakeAttribute;
import com.bioxx.jmapgen.attributes.RiverAttribute;
import com.bioxx.jmapgen.com.nodename.delaunay.DelaunayUtil;
import com.bioxx.jmapgen.com.nodename.delaunay.Voronoi;
import com.bioxx.jmapgen.com.nodename.geom.LineSegment;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Corner;
import com.bioxx.jmapgen.graph.CornerElevationSorter;
import com.bioxx.jmapgen.graph.Edge;
import com.bioxx.jmapgen.graph.MoistureComparator;
import com.bioxx.jmapgen.graph.Center.HexDirection;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.pathfinding.PathFinder;
import com.bioxx.jmapgen.processing.CaveProcessor;

public class IslandMap 
{
	public int NUM_POINTS = 4096*4;
	public int NUM_POINTS_SQ = (int) Math.sqrt(NUM_POINTS);

	// Passed in by the caller:
	public int SIZE;
	// Island shape is controlled by the islandRandom seed and the
	// type of island, passed in when we set the island shape. The
	// islandShape function uses both of them to determine whether any
	// point should be water or land.
	protected IslandParameters islandParams;
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
	public long seed;

	public PathFinder pathfinder;

	private CaveProcessor caves;

	public IslandMap(int size, long s) 
	{
		SIZE = size;
		seed = s;
		points = new Vector<Point>();
		edges = new Vector<Edge>();
		centers = new Vector<Center>();
		corners = new Vector<Corner>();
		lakes = new Vector<Lake>();
		rivers = new Vector<River>();
		pathfinder = new PathFinder(this);
		caves = new CaveProcessor(this);
	}

	// Random parameters governing the overall shape of the island
	public void newIsland() 
	{
		islandParams = new IslandParameters(seed, SIZE, 0.5);
		mapRandom.setSeed(seed);
	}

	public void newIsland(IslandParameters is) 
	{
		islandParams = is;
		mapRandom.setSeed(seed);
		NUM_POINTS = is.SIZE*4;
		NUM_POINTS_SQ = (int) Math.sqrt(NUM_POINTS);
		is.createShape(seed);
		points.clear();
		edges.clear();
		centers.clear();
		corners.clear();
		lakes.clear();
		rivers.clear();
		pathfinder = new PathFinder(this);
		caves = new CaveProcessor(this);
	}

	public IslandParameters getParams()
	{
		return this.islandParams;
	}

	public void go() 
	{
		points = this.generateHexagon(SIZE);

		//System.out.println("Points: " + points.size());
		Rectangle R = new Rectangle();
		R.setFrame(0, 0, SIZE, SIZE);
		//System.out.println("Starting Creating map Voronoi...");
		Voronoi voronoi = new Voronoi(points, R);
		//System.out.println("Finished Creating map Voronoi...");
		buildGraph(points, voronoi);
		// Determine the elevations and water at Voronoi corners.
		int borderCount = assignCornerElevations();
		// Determine polygon and corner type: ocean, coast, land.
		assignOceanCoastAndLand();
		//If there is too much land on the borders then toss this island and start fresh
		if(borderCount > 20)
		{
			seed += 1234567;
			newIsland(islandParams);
			go();
			return;
		}
		redistributeElevations(landCorners(corners));
		//fixElevations(landCorners(corners));
		// Assign elevations to non-land corners
		for(Iterator<Corner> i = corners.iterator(); i.hasNext();)
		{
			Corner q = (Corner)i.next();
			if (q.hasMarker(Marker.Ocean) || q.hasMarker(Marker.Coast)) 
			{
				q.elevation = 0.0;
			}
		}

		if(!this.islandParams.hasFeature(Feature.NoLand))
		{

			// Polygon elevations are the average of their corners
			assignPolygonElevations();
			assignLakeElevations(lakeCenters(centers));

			// Determine downslope paths.
			calculateDownslopesCenter();

			createVolcano(getCentersAbove(0.8));

			createValleys(getCentersAbove(0.4));

			createCanyons();

			calculateDownslopesCenter();

			createGorges();

			// Determine downslope paths.
			calculateDownslopesCenter();
			// Create rivers.
			createRivers(getCentersAbove(0.25));
			assignSlopedNoise();
			assignHillyNoise();
			calculateDownslopesCenter();
		}
		else
		{
			// Assign elevations to non-land corners
			for(Iterator<Center> i = centers.iterator(); i.hasNext();)
			{
				Center q = (Center)i.next();
				q.setMarkers(Marker.Ocean, Marker.Water);
				q.setElevation(0);
			}
		}

		assignMoisture();
		redistributeMoisture(landCenters(centers));
		assignMoisturePostRedist();

		sortClockwise();
		setupBiomeInfo();

		caves.generate();
	}

	private void createCanyons()
	{
		if(!this.islandParams.hasFeature(Feature.Canyons))
			return;

		Vector<Center> highCenters = getCentersAbove(0.8);
		Vector<Center> startCenters = new Vector<Center>();

		int maxCanyons = 5;

		for(int i = 0; i < maxCanyons; i++)
		{
			Center start = null;
			boolean found = false;
			int count = 0;
			while(found == false && count < 100)
			{
				found = true;
				start = highCenters.get(mapRandom.nextInt(highCenters.size()));

				if(start.hasAttribute(Attribute.canyonUUID) || startCenters.contains(start))
					found = false;

				for(Center c : startCenters)
				{
					if(start.point.distance(c.point) < 200)
						found = false;
				}
				count++;
			}
			startCenters.add(start);


			Vector<GenericNode> canyon = new Vector<GenericNode>();

			GenericNode curNode = new GenericNode(start);
			GenericNode nextNode = null;
			double minElevation = curNode.getCenter().getElevation();
			while(curNode != null)
			{
				canyon.add(curNode);
				nextNode = getCanyonNextNode(curNode);
				curNode.setDown(nextNode);
				if(nextNode != null)
				{
					nextNode.setUp(curNode);
					nextNode.nodeNum = curNode.nodeNum + 1;
				}
				minElevation = curNode.getCenter().getElevation();

				//Dont manipulate curNode after this, it may be null.
				curNode = nextNode;
			}
			//System.out.println("Canyon " +i + ": " + start.point.x + "," + start.point.y);

			//First we process the middle centers themselves
			for(GenericNode gn : canyon)
			{
				Center curCenter = gn.getCenter();

				double elevMult = Math.min(0.5, gn.nodeNum* 0.05);

				if(!curCenter.hasAttribute(Attribute.canyonUUID))
					curCenter.setElevation(Math.max(minElevation, gn.getCenter().getElevation()*(1-elevMult)));

				//Create a canyon attribute for the node center
				CanyonAttribute a = new CanyonAttribute(Attribute.canyonUUID, gn.nodeNum);
				//set the down center in the canyon attribute to the down node for this node
				if(gn.getDown() != null)
					a.setDown(gn.getDown().getCenter());
				if(gn.getUp() != null)
					a.setUp(gn.getUp().getCenter());

				curCenter.addAttribute(a);
			}
			//Next we go back through and process the neighbor centers
			for(GenericNode gn : canyon)
			{
				for(Center n : gn.getCenter().neighbors)
				{
					//If this center already has a canyon attribute than it must have already been processed.
					if(!n.hasAttribute(Attribute.canyonUUID) && !n.hasMarker(Marker.Water))
					{
						CanyonAttribute c = new CanyonAttribute(Attribute.canyonUUID, gn.nodeNum);
						c.setDown(gn.getCenter());
						if(n.addAttribute(c))
							n.setElevation(Math.max(minElevation, gn.getCenter().getElevation()));
					}
				}
			}
		}
	}

	private GenericNode getCanyonNextNode(GenericNode curNode)
	{

		if(curNode.getCenter().downslope == null || curNode.getCenter().downslope.hasMarker(Marker.Water))
			return null;

		RandomCollection<Center> possibles = new RandomCollection<Center>(this.mapRandom);
		for(Center n : curNode.getCenter().neighbors)
		{
			if(curNode.getUp() != null && n == curNode.getUp().getCenter())
				continue;

			if(n.hasAttribute(Attribute.canyonUUID))
				return null;

			if(n.hasMarker(Marker.Water))
			{
				return null;
			}
			else if(n.getElevation() < curNode.getCenter().getElevation())
				possibles.add(0.5, n);
			else
				possibles.add(0.1, n);

		}
		if(possibles.size() > 0)
			return new GenericNode(possibles.next());
		return null;
	}

	private void createVolcano(Vector<Center> candidates)
	{
		if(!this.islandParams.hasFeature(Feature.Volcano))
			return;
		Center mid = candidates.get(mapRandom.nextInt(candidates.size()));
		System.out.println("Volcano: X" + mid.point.x + " Z"+ mid.point.y);
		Vector<Center> caldera = new Vector<Center>();
		caldera.add(mid);
		caldera.addAll(mid.neighbors);
		double lowestElev = 1.0;
		for(Center c : caldera)
		{
			if(c.hasAttribute(Attribute.riverUUID))
				((RiverAttribute)c.getAttribute(Attribute.riverUUID)).setRiver(0);
			c.removeMarkers(Marker.Water);
			c.setMarkers(Marker.Lava);
			if(c.elevation < lowestElev)
				lowestElev = c.elevation;
		}

		for(Center c : caldera)
		{
			c.elevation = lowestElev * 0.85;
		}
	}

	private void createValleys(Vector<Center> candidates)
	{
		if(!this.islandParams.hasFeature(Feature.Valleys))
			return;

		int totalValleys = 1+mapRandom.nextInt(5);

		for(int count = 0; count < totalValleys; count++)
		{
			int minSize = 20+mapRandom.nextInt(30);
			Center mid = candidates.get(mapRandom.nextInt(candidates.size()));

			LinkedList<Center> valleyQueue = new LinkedList<Center>();
			Vector<Center> valleyFinal = new Vector<Center>();
			Vector<Lake> lakesToDrop = new Vector<Lake>();


			if(mid.hasMarker(Marker.Water))
				continue;

			valleyFinal.add(mid);
			valleyQueue.addAll(mid.neighbors);
			double minElevation = Float.MAX_VALUE;
			while(!valleyQueue.isEmpty())
			{
				Center c = valleyQueue.pop();
				//Make sure that we aren't readding a center that is already in the valley.
				if(valleyFinal.contains(c))
					continue;
				if(valleyFinal.size() <= minSize || mapRandom.nextInt(1+valleyFinal.size()-minSize) == 0 )
				{
					//If we hit a lake center, then we just drop the entire lake into the valley.
					if(c.hasMarker(Marker.Water) && !c.hasMarker(Marker.Ocean))
					{
						Lake l = centerInExistingLake(c);
						if(l != null && !lakesToDrop.contains(l))
						{
							lakesToDrop.add(l);
						}
					}
					else if(c.hasMarker(Marker.Ocean)) continue;

					valleyFinal.add(c);
					if(c.elevation < minElevation)
						minElevation = c.elevation;
					for(Center n : c.neighbors)
					{
						if(!valleyQueue.contains(n) && !valleyFinal.contains(n))
							valleyQueue.add(n);
					}
				}

			}
			if(valleyFinal.size() >= minSize)
			{
				//System.out.println("Valley: X" + mid.point.x + " Z"+ mid.point.y);
				for(Center n : valleyFinal)
				{
					n.elevation = minElevation*0.8 + (-convertMCToHeight(2) + mapRandom.nextDouble()*convertMCToHeight(5));//Math.max(minElevation, n.elevation*0.8);
					n.setMarkers(Marker.Valley);
				}
				for(Lake l : lakesToDrop)
				{
					for(Center c : l.centers)
					{
						c.elevation = minElevation*0.79;
					}
				}
			}
		}
	}

	/**
	 * This method chooses a random hex and raises it by a random amount, no higher than the highest hex within 4 hexes.
	 * All neighboring hexes are also elevated to a lesser degree.
	 */
	private void assignHillyNoise() 
	{
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();
			//10% change of any hex being selected as long as it is not a water or canyon hex, and does not contain a river.
			if(!center.hasAttribute(Attribute.canyonUUID) && !center.hasAttribute(Attribute.gorgeUUID) && 
					!center.hasMarker(Marker.Coast) && this.mapRandom.nextInt(100) < 10 && !center.hasMarker(Marker.Water) && 
					center.getAttribute(Attribute.riverUUID) == null)
			{
				Center highest = this.getHighestNeighbor(center);
				highest = this.getHighestNeighbor(highest);
				highest = this.getHighestNeighbor(highest);
				highest = this.getHighestNeighbor(highest);

				double diff = highest.elevation - center.elevation;
				double mult = 0.5;

				if(center.hasMarker(Marker.Valley))
					mult = 0.1;

				center.elevation += diff * (mult + mult*mapRandom.nextDouble());
				if(center.elevation <= 0)
					return;

				for(Iterator<Center> centerIter2 = center.neighbors.iterator(); centerIter2.hasNext();)
				{
					Center center2 = (Center)centerIter2.next();
					if(!center2.hasMarker(Marker.Lava) && !center2.hasAttribute(Attribute.gorgeUUID) && !center2.hasMarker(Marker.Coast) && center2.getAttribute(Attribute.riverUUID) == null && !center2.hasMarker(Marker.Water))
					{
						center2.elevation += Math.max(0, (center.elevation - center2.elevation)*mapRandom.nextDouble());
						if(center2.elevation <= 0)
							return;
					}
				}
			}
		}
	}

	/**
	 * This Method Adds some noise to the world by perturbing random hexes between the lowest and highest adjacent hexes.
	 */
	private void assignSlopedNoise() 
	{
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();
			if(!center.hasAttribute(Attribute.gorgeUUID) && !center.hasMarker(Marker.Coast) && !center.hasMarker(Marker.Water) && !center.hasAttribute(Attribute.riverUUID))
			{
				boolean nearWater = false;
				for(Iterator<Center> centerIter2 = center.neighbors.iterator(); centerIter2.hasNext();)
				{
					Center center2 = (Center)centerIter2.next();
					if(center2.hasMarker(Marker.Water))
						nearWater  = true;
				}

				if(!center.hasMarker(Marker.Lava) && !nearWater && this.mapRandom.nextInt(100) < 50)
				{
					Center lowest = getLowestNeighbor(center);
					Center highest = getHighestNeighbor(center);

					if(this.mapRandom.nextInt(100) < 70)
						center.elevation -= mapRandom.nextDouble() * (center.elevation - lowest.elevation);
					else
						center.elevation += mapRandom.nextDouble() * (center.elevation - highest.elevation);

					center.elevation = Math.min(Math.max(0, center.elevation), 1.0);
					if(center.elevation <= 0)
						return;
				}
			}
		}
	}

	public Center getHighestNeighbor(Center c)
	{
		Center highest = c;
		for(Iterator<Center> centerIter2 = c.neighbors.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(highest == null || center2.elevation > highest.elevation)
				highest = center2;
		}
		RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.riverUUID));
		if(attrib != null && attrib.upriver != null)
		{
			highest = getLowestFromGroup(attrib.upriver);
		}
		return highest;
	}

	public Center getLowestNeighbor(Center c)
	{
		Center lowest = c;
		for(Iterator<Center> centerIter2 = c.neighbors.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(lowest == null || center2.elevation < lowest.elevation)
				lowest = center2;
		}
		RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.riverUUID));
		if(attrib != null && attrib.getDownRiver() != null)
			lowest = attrib.getDownRiver();
		return lowest;
	}

	private Center getLowestFromGroup(Vector<Center> group)
	{
		Center lowest = group.get(0);
		for(Iterator<Center> centerIter2 = group.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(lowest == null || center2.elevation < lowest.elevation)
				lowest = center2;
		}
		return lowest;
	}

	private Center getHighestFromGroup(Vector<Center> group)
	{
		Center highest = group.get(0);
		for(Iterator<Center> centerIter2 = group.iterator(); centerIter2.hasNext();)
		{
			Center center2 = (Center)centerIter2.next();
			if(highest == null || center2.elevation > highest.elevation)
				highest = center2;
		}
		return highest;
	}

	private void sortClockwise() 
	{
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();
			Vector<Corner> sortedCorners = new Vector<Corner>();
			Vector<Center> sortedNeighbors = new Vector<Center>();
			Point zeroPoint = new Point(center.point.x, center.point.y+1);
			//Sort neighbors clockwise
			for(Iterator<Corner> iter = center.corners.iterator(); iter.hasNext();)
			{
				Corner c = (Corner)iter.next();
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
			//Sort neighbors clockwise
			for(Iterator<Center> iter = center.neighbors.iterator(); iter.hasNext();)
			{
				Center c = (Center)iter.next();
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
			center.corners = sortedCorners;
		}
	}

	private void setupBiomeInfo() 
	{
		double edgeDistance = 0.10;
		double min = this.SIZE * edgeDistance;
		double max = this.SIZE * (1 -edgeDistance);
		for(Iterator<Center> centerIter = centers.iterator(); centerIter.hasNext();)
		{
			Center center = (Center)centerIter.next();

			//Assign biome information to each hex
			center.biome = getBiome(center);

			//If this hex is near the map border we want to count the number of hexes in the connected island.
			//If there are too few then we will delete this tiny island to make the islands look better
			if(!center.hasMarker(Marker.Water) && (center.point.x < min || center.point.x > max ||
					center.point.y < min || center.point.y > max))
			{
				Vector<Center> island = countIsland(center, 25);
				if(island != null && island.size() > 0)
				{
					for(Center n : island)
					{
						n.setMarkers(Marker.Water, Marker.Ocean);
						n.biome = BiomeType.OCEAN;
					}
				}
			}

			if(center.hasMarker(Marker.CoastWater))
				center.elevation = -0.01 - mapRandom.nextDouble()*0.03;
			else if(center.hasMarker(Marker.Ocean))
				center.elevation = -0.1 - mapRandom.nextDouble()*0.25;


		}
	}

	/**
	 * @return May return null if the island is too big.
	 */
	public Vector<Center> countIsland(Center start, int maxSize) 
	{
		Vector<Center> outList = new Vector<Center>();
		LinkedList<Center> checkList = new LinkedList<Center>();

		outList.add(start);
		checkList.add(start);

		while(checkList.size() > 0)
		{
			Center c = checkList.pollFirst();
			for(Center n : c.neighbors)
			{
				if(!checkList.contains(n) && !outList.contains(n) && !n.hasMarker(Marker.Water))
				{
					outList.add(n);
					checkList.addLast(n);
				}
			}

			if(outList.size() >= maxSize)
				return null;
		}


		return outList;
	}

	public Vector<Point> generateHexagon(int size) {

		Vector<Point> points = new Vector<Point>();
		int N = (int) Math.sqrt(NUM_POINTS);
		double xC, yC;
		for (int x = 0; x < N; x++) {
			for (int y = 0; y < N; y++) 
			{
				xC = (0.5 + x) / N * (size);
				yC = (0.25 + 0.5 * x % 2 + y) / N * (size);
				points.add(new Point(xC, yC));
			}
		}
		return points;
	}

	/** 
	 * Create an array of corners that are on land only, for use by
	 * algorithms that work only on land.  We return an array instead
	 * of a vector because the redistribution algorithms want to sort
	 * this array using Array.sortOn.
	 */
	public Vector<Corner> landCorners(Vector<Corner> corners) {
		Corner q; 
		Vector<Corner> locations = new Vector<Corner>();
		for (int i = 0; i < corners.size(); i++) {
			q = corners.get(i);
			if (!q.hasMarker(Marker.Ocean) && !q.hasMarker(Marker.Coast)) {
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
			if (!q.hasMarker(Marker.Ocean) && !q.hasMarker(Marker.Coast)) {
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
			if (!q.hasMarker(Marker.Ocean) && q.hasMarker(Marker.Water)) {
				locations.add(q);
			}
		}
		return locations;
	}

	/**
	// Build graph data structure in 'edges', 'centers', 'corners',
	// based on information in the Voronoi results: point.neighbors
	// will be a list of neighboring points of the same type (corner
	// or center); point.edges will be a list of edges that include
	// that point. Each edge connects to four points: the Voronoi edge
	// edge.{v0,v1} and its dual Delaunay triangle edge edge.{d0,d1}.
	// For boundary polygons, the Delaunay edge will have one null
	// point, and the Voronoi edge may be null.
	 */
	public void buildGraph(Vector<Point> points, Voronoi voronoi) 
	{
		Center p; 
		Corner q; 
		Point point;
		Point other;

		Vector<com.bioxx.jmapgen.com.nodename.delaunay.Edge> libedges = voronoi.getEdges();
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
			com.bioxx.jmapgen.com.nodename.delaunay.Edge libedge = libedges.get(i);
			LineSegment dedge = libedge.delaunayLine();
			LineSegment vedge = libedge.voronoiEdge();

			// Fill the graph data. Make an Edge object corresponding to
			// the edge from the voronoi library.
			Edge edge = new Edge();
			edge.index = edges.size();
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
		if(point.x == 0 || point.x == SIZE
				|| point.y == 0 || point.y == SIZE) q.setMarkers(Marker.Border);	

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
	public int assignCornerElevations() 
	{
		Corner baseCorner, adjacentCorner;
		LinkedList<Corner> queue = new LinkedList<Corner>();
		int numLandBorder = 0;
		/**
		 * First we check each corner to see if it is land or water
		 * */
		for(Corner c : corners)
		{
			if(!inside(c.point))
				c.setMarkers(Marker.Water);
			else if(c.hasMarker(Marker.Border))
				numLandBorder++;

			if (c.hasMarker(Marker.Border)) 
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

				if(!adjacentCorner.hasMarker(Marker.Border))
				{
					// Every step up is epsilon over water or 1 over land. The
					// number doesn't matter because we'll rescale the
					// elevations later.				
					double newElevation = 0.000000001 + baseCorner.elevation;

					if (!baseCorner.hasMarker(Marker.Water) && !adjacentCorner.hasMarker(Marker.Water)) 
					{
						newElevation += 1;
					}
					// If this point changed, we'll add it to the queue so
					// that we can process its neighbors too.
					if (newElevation < adjacentCorner.elevation) 
					{
						adjacentCorner.elevation = newElevation;
						queue.add(adjacentCorner);
					}
				}
			}
		}

		return numLandBorder;
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
	public void redistributeElevations(Vector<Corner> locations) 
	{
		// SCALE_FACTOR increases the mountain area. At 1.0 the maximum
		// elevation barely shows up on the map, so we set it to 1.1.
		double SCALE_FACTOR = 1.0;

		Collections.sort(locations, new CornerElevationSorter());
		int locationsSize = locations.size();
		Corner c;

		for (int i = 0; i < locationsSize; i++) 
		{
			c = locations.get(i);
			double y = (double)i/(double)(locationsSize-1);
			double x = y;
			if(this.islandParams.hasFeature(Feature.SharperMountains) && y >= 0.05)
			{
				x = Math.pow(y, 2);	
			}
			else if(this.islandParams.hasFeature(Feature.EvenSharperMountains) && y >= 0.05)
			{
				x = Math.pow(y, 3);
			}
			else
			{
				// Now we have to solve for x, given the known y.
				//  *  y = 1 - (1-x)^2
				//  *  y = 1 - (1 - 2x + x^2)
				//  *  y = 2x - x^2
				//  *  x^2 - 2x + y = 0
				// From this we can use the quadratic equation to get:
				double sqrtScale = Math.sqrt(SCALE_FACTOR);
				double scale1Y = SCALE_FACTOR*(1-y);
				double sqrtscale1Y = Math.sqrt(scale1Y);

				x = sqrtScale - sqrtscale1Y;
			}

			if (x > 1.0) 
				x = 1.0;

			c.elevation = x;
			if(!c.hasMarker(Marker.Water) && !c.isShoreline())
				c.elevation +=0.01;
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

	public void assignMoisture() 
	{
		LinkedList<Center> queue = new LinkedList<Center>();
		// Fresh water
		for(Center cr : centers)
		{
			RiverAttribute attrib = (RiverAttribute)cr.getAttribute(Attribute.riverUUID);
			if ((cr.hasMarker(Marker.Water) || (attrib != null && attrib.getRiver() > 0)) && !cr.hasMarker(Marker.Ocean)) 
			{
				double rivermult = attrib != null ? attrib.getRiver() : 0;
				cr.moisture = (attrib != null && attrib.getRiver() > 0) ? Math.min(3.0, (0.1 * rivermult)) : 1.0;
				queue.push(cr);
			} 
			else 
			{
				cr.moisture = 0.0;
			}
		}
		//This controls how far the moisture level spreads from the moisture source. Lower values cause less overall island moisture.
		double moistureMult = 1 - (0.6 * this.islandParams.getIslandMoisture().getMoisture());
		while (queue.size() > 0) 
		{
			Center q = queue.pop();

			for(Center adjacent : q.neighbors)
			{
				double newMoisture = q.moisture * moistureMult;
				if (newMoisture > adjacent.moisture) 
				{
					adjacent.moisture = newMoisture;
					queue.push(adjacent);
				}
			}
		}
		// Salt water
		for(Center cr : centers)
		{
			if (cr.hasMarker(Marker.Ocean)) 
			{
				cr.moisture = 1.0;
			}
			if (cr.hasMarker(Marker.Coast)) 
			{
				cr.moisture = Math.max(0.5, cr.moisture);
			}
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
		Center c = null, r = null; 
		Corner q; 
		int numWater;

		for(int i = 0; i < centers.size(); i++)
		{
			c = centers.get(i);
			numWater = 0;
			for(int j = 0; j < c.corners.size(); j++)
			{
				q = c.corners.get(j);
				if (q.hasMarker(Marker.Border)) 
				{
					c.setMarkers(Marker.Border, Marker.Ocean);
					q.setMarkers(Marker.Water);
					queue.add(c);
				}
				if (q.hasMarker(Marker.Water)) 
				{
					numWater += 1;
				}
			}

			if((c.hasMarker(Marker.Ocean) || numWater >= c.corners.size() * this.islandParams.lakeThreshold))
				c.setMarkers(Marker.Water);
		}
		while (queue.size() > 0) 
		{
			c = queue.pop();

			for(int j = 0; j < c.neighbors.size(); j++)
			{
				r = c.neighbors.get(j);
				if (r.hasMarker(Marker.Water) && !r.hasMarker(Marker.Ocean)) {
					r.setMarkers(Marker.Ocean);
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
			c = centers.get(i);
			numOcean = 0;
			numLand = 0;

			for(int j = 0; j < c.neighbors.size(); j++)
			{
				r = c.neighbors.get(j);
				numOcean += (r.hasMarker(Marker.Ocean) ? 1 : 0);
				numLand += (!r.hasMarker(Marker.Water) ? 1 : 0);
			}

			if((numOcean > 0) && !c.hasMarker(Marker.Ocean))
				c.setMarkers(Marker.Coast);
			if(c.hasMarker(Marker.Ocean) && (numLand > 0))
				c.setMarkers(Marker.CoastWater);
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
				c = q.touches.get(i);
				numOcean += (c.hasMarker(Marker.Ocean) ? 1 : 0);
				numLand += (!c.hasMarker(Marker.Water) ? 1 : 0);
			}
			if(numOcean == q.touches.size())
				q.setMarkers(Marker.Ocean);
			if((numOcean > 0) && (numLand > 0))
				q.setMarkers(Marker.Coast);
			if(q.hasMarker(Marker.Border) || ((numLand != q.touches.size()) && !q.hasMarker(Marker.Coast)))
				q.setMarkers(Marker.Water);

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
			//If we are generating cliffs then we multiply the elevation by .85 to keep it <= 1.0 and add 0.15
			if(this.islandParams.hasFeature(Feature.Cliffs) && !p.hasMarker(Marker.Ocean) && !p.hasMarker(Marker.Coast) && p.elevation >= 0)
				p.elevation = Math.max((p.elevation * 0.85) + 0.15, 0.15);
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
						if(!lake.hasCenter(adj) && adj.hasMarker(Marker.Water) && !adj.hasMarker(Marker.Ocean))
						{
							lake.addCenter(adj);
							centersToCheck.add(adj);
						}
					}			
				}
				lakes.add(lake);
			}
		}
		for(int lakeID = 0; lakeID < lakes.size(); lakeID++)
		{
			Lake lake = lakes.get(lakeID);
			lake.lakeID = lakeID;
			for(Center c : lake.centers)
			{
				c.elevation = lake.lowestCenter.elevation;
				LakeAttribute attrib = new LakeAttribute(Attribute.lakeUUID);
				attrib.setLakeElev(lake.lowestCenter.elevation);
				attrib.setLakeID(lakeID);
				//Here we try to smooth the centers around lakes a bit
				for(Center n : c.neighbors)
				{
					if(n.hasAttribute(Attribute.lakeUUID))
					{
						LakeAttribute nAttrib = (LakeAttribute) n.getAttribute(Attribute.lakeUUID);
						if(nAttrib.getBorderDistance() < attrib.getBorderDistance())
							attrib.setBorderDistance(nAttrib.getBorderDistance() + 1);
						else if (nAttrib.getBorderDistance() > attrib.getBorderDistance())
							nAttrib.setBorderDistance(attrib.getBorderDistance() + 1);
					}
					if(!n.hasMarker(Marker.Water))
					{
						attrib.setBorderDistance(0);
						if(n.elevation < c.elevation)//Neighbor is lower than the lake
							n.elevation += (c.elevation - n.elevation)/2;
						else if(c.elevation < n.elevation)//Neighbor is higher than the lake
							n.elevation -= (n.elevation - c.elevation)/2;
					}
				}
				if(c.getElevation() < 0.1)
					attrib.setMarsh(true);
				c.addAttribute(attrib);
				if(attrib.getBorderDistance() > 0)
				{
					double total = c.getElevation() - (c.getElevation() * 0.9D);
					c.setElevation(c.getElevation() - (total * (attrib.getBorderDistance() / 10D)));
				}
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
				if (convertHeightToMC(tempCorner.elevation) <= convertHeightToMC(downCorner.elevation)) 
				{
					downCorner = tempCorner;
				}
			}	
			upCorner.downslope = downCorner;
		}
	}

	public Vector<Center> getCentersAbove(double elev)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if (c.elevation >= elev)
				out.add(c);
		}
		return out;
	}

	public Vector<Center> getCentersBelow(double elev, boolean allowWater)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if (c.elevation <= elev)
			{
				if(allowWater || !c.hasMarker(Marker.Water))
					out.add(c);
			}
		}
		return out;
	}

	private void createGorges()
	{
		if(!this.islandParams.hasFeature(Feature.Gorges))
			return;

		Vector<Center> possibleStarts = new Vector<Center>();
		Vector<Gorge> gorges = new Vector<Gorge>();
		Gorge gorge = null;

		Vector<Center> highCenters = this.getCentersAbove(0.5);
		for(Center c : centers)
		{
			if(c.hasAttribute(Attribute.canyonUUID))
			{
				CanyonAttribute a = (CanyonAttribute) c.getAttribute(Attribute.canyonUUID);
				if(a.isNode && a.nodeNum < 10)
					possibleStarts.add(c);
			}
		}

		for (int i = 0; i < 100; i++) 
		{
			boolean flag = true;
			Center c = highCenters.get(mapRandom.nextInt(highCenters.size()-1));
			for(Center n : c.neighbors)
			{
				if(possibleStarts.contains(n) || n.hasAttribute(Attribute.canyonUUID))
				{
					flag = false;
					break;
				}
			}
			if(flag)
				possibleStarts.add(c);
		}
		int id = 0;
		for(Center c : possibleStarts)
		{
			if(c.hasMarker(Marker.Water))
				continue;
			gorge = new Gorge();
			GorgeNode curNode = new GorgeNode(c);
			GorgeNode nextNode = curNode;
			int count = 0;
			id++;
			while (true)
			{
				if (c == null || count > 250 || curNode == null || curNode.center.hasMarker(Marker.Water)) 
				{
					break;
				}
				count++;

				//calculate the next node
				nextNode = getNextGorgeNode(curNode);
				if(nextNode != null)
					nextNode.setUp(curNode);
				//set the downriver center for this node to the next center
				curNode.setDown(nextNode);
				gorge.addNode(curNode);

				//set the current working center to our next node before starting over
				curNode = nextNode;
			}

			if(gorge != null && gorge.nodes.size() > 2)
			{
				gorges.add(gorge);
				for(GorgeNode cn : gorge.nodes)
				{
					double diff = cn.center.getElevation() - gorge.minElev;
					double elev = cn.center.getElevation();
					if(!cn.center.hasAttribute(Attribute.gorgeUUID))
					{
						cn.center.setElevation(Math.max(gorge.minElev,cn.center.elevation - Math.min(diff * 0.5, 0.2)));
						if(cn.getUp() != null && cn.center.getElevation() > cn.getUp().center.getElevation())
						{
							cn.center.setElevation(cn.getUp().center.getElevation());
						}

						GorgeAttribute a = new GorgeAttribute(Attribute.gorgeUUID);
						if(cn.getUp() != null)
							a.setUp(cn.getUp().center);
						if(cn.getDown() != null)
							a.setDown(cn.getDown().center);
						a.gorgeID = id;
						cn.center.addAttribute(a);
					}
				}
			}
		}
	}

	public GorgeNode getNextGorgeNode(GorgeNode cur)
	{
		RandomCollection<Center> possibles = new RandomCollection<Center>(this.mapRandom);

		//Go through each neighbor and find all possible hexes at the same elevation or lower
		for(Center n : cur.center.neighbors)
		{
			//If the elevations are the same or lower then this might be an ok location
			if(convertHeightToMC(n.elevation) < convertHeightToMC(cur.center.elevation))
			{
				//If next to a gorge hex then we finish here
				if(n.hasAttribute(Attribute.gorgeUUID))
					return null;
				if(n.hasMarker(Marker.Ocean) || n.hasMarker(Marker.Water))
				{
					return null;
				}

				//If the elevation is <= our current cell elevation then we allow this cell to be selected
				possibles.add(0.5, n);
			}
			else if(convertHeightToMC(n.elevation) == convertHeightToMC(cur.center.elevation))
			{
				possibles.add(0.1, n);
			}
		}

		if(possibles.size() > 0)
		{
			return new GorgeNode(possibles.next());
		}

		return null;
	}

	public void createRivers(Vector<Center> land) 
	{
		Center c;
		Center prev;

		Vector<Center> possibleStarts = new Vector<Center>();
		int starts = (int)Math.floor(100 * this.islandParams.getIslandMoisture().getMoisture());
		if(this.islandParams.hasFeature(Feature.Gorges))
		{
			starts = (int)Math.floor(15 * this.islandParams.getIslandMoisture().getMoisture());
		}

		for (int i = 0; i < starts; i++) 
		{
			c = land.get(mapRandom.nextInt(land.size()-1));

			//We dont want rivers to start inside of valleys
			if(c.hasMarker(Marker.Valley))
				continue;

			possibleStarts.add(c);
		}


		if(this.islandParams.hasFeature(Feature.Gorges))
		{
			for(Center cn : centers)
			{
				if(cn.hasAttribute(Attribute.gorgeUUID))
				{
					if(((GorgeAttribute)cn.getAttribute(Attribute.gorgeUUID)).getUp() == null && 
							(mapRandom.nextDouble() > 0.25 || cn.hasAttribute(Attribute.canyonUUID)))
					{
						possibleStarts.add(cn);
					}
				}
			}
		}

		for (int i = 0; i < lakes.size(); i++) 
		{
			possibleStarts.add(lakes.get(i).lowestCenter);
			for(Center cen : lakes.get(i).lowestCenter.neighbors)
			{
				if(cen.hasMarker(Marker.Water) && mapRandom.nextBoolean())
					possibleStarts.add(cen);
			}
		}

		buildRiver(possibleStarts);
	}

	private void buildRiver(Vector<Center> possibleStarts) {
		Center c;
		for (int i = 0; i < possibleStarts.size(); i++) 
		{
			c = possibleStarts.get(i);
			RiverAttribute cAttrib = ((RiverAttribute)c.getAttribute(Attribute.riverUUID));
			if (c.hasMarker(Marker.Ocean) || c.elevation > 0.85 || (cAttrib != null && cAttrib.getRiver() > 0)) continue;

			River r = new River();
			RiverNode curNode = new RiverNode(c);
			r.addNode(curNode);
			RiverNode nextNode = curNode;
			int count = 0;
			while (true)
			{
				if (c == null || c == c.downslope || count > 250 || (c.hasMarker(Marker.Water) && curNode != r.riverStart)) 
				{
					break;
				}
				count++;
				curNode = nextNode;
				//calculate the next rivernode
				nextNode = getNextRiverNode(r, curNode);
				if(nextNode == null)
					break;
				RiverAttribute nextAttrib = ((RiverAttribute)nextNode.center.getAttribute(Attribute.riverUUID));

				//set the downriver center for this node to the next center
				curNode.setDownRiver(nextNode.center);
				nextNode.setUpRiver(curNode.center);
				//add the next node to the river graph
				r.addNode(nextNode);
				//If the current hex is water then we exit early unless this is the first node in the river
				if((c.hasMarker(Marker.Water) && curNode != r.riverStart) && (curNode.downRiver == null || curNode.downRiver.hasMarker(Marker.Water)))
					break;

				//Keep track of the length of a river before it joins another river or reaches its end
				if(nextAttrib == null || nextAttrib.getRiver() == 0)
					r.lengthToMerge++;
				//set the current working center to our next node before starting over
				c = nextNode.center;
			}

			//If this river is long enough to be acceptable and it eventually empties into a water hex then we process the river into the map
			boolean isValid = false;
			//Is the riverstart valid
			if(r.riverStart != null && r.riverStart.center.hasMarker(Marker.Water) && r.nodes.lastElement().center.hasMarker(Marker.Water) &&
					(r.riverStart != r.nodes.lastElement()) && r.nodes.lastElement().center.elevation < r.riverStart.center.elevation)
				isValid = true;
			if(r.lengthToMerge > 4 && r.nodes.lastElement().center.hasMarker(Marker.Water))
				isValid = true;
			else
				isValid = false;
			RiverAttribute startAttrib = (RiverAttribute)r.riverStart.center.getAttribute(Attribute.riverUUID);
			if(r.riverStart == null || (startAttrib != null && startAttrib.getRiver() != 0) || r.nodes.size() < 4)
				isValid = false;

			if(isValid)
			{
				if(r.riverStart.center.hasMarker(Marker.Water) && this.centerInExistingLake(r.riverStart.center).centers.size() > 8)
					r.riverWidth = 4 - 3 * r.riverStart.center.elevation;
				else if(r.riverStart.center.hasAttribute(Attribute.gorgeUUID))
					r.riverWidth = 1;
				else
					r.riverWidth = 0.5;
				//Add this river to the river collection
				rivers.add(r);
				curNode = r.nodes.get(0);
				nextNode = curNode;
				boolean cancelRiver = false;

				//Propogate through each node in this river and setup RiverAttributes for each Center
				for (int j = 0; j <= r.nodes.size() && !cancelRiver; j++) 
				{
					//On the first node, we add a small pond and make sure that this river does not start too close to another river.
					if(j == 0)
					{
						for(Center n :r.riverStart.center.neighbors)
						{
							if(n.getAttribute(Attribute.riverUUID) != null && 
									((RiverAttribute)n.getAttribute(Attribute.riverUUID)).getRiver() > 0)
							{
								rivers.remove(r);
								cancelRiver = true;
								break;
							}
						}
						r.riverStart.center.setMarkers(Marker.Pond);
					}
					else
					{
						if(j < r.nodes.size())
							nextNode = r.nodes.get(j);
						else nextNode = null;

						//Sanity
						RiverAttribute riverAttrib = ((RiverAttribute)curNode.center.getAttribute(Attribute.riverUUID));
						if(riverAttrib == null)
						{
							riverAttrib = new RiverAttribute(Attribute.riverUUID);
							curNode.center.addAttribute(riverAttrib);
							//curNode.center.setElevation(Math.max(curNode.center.getElevation() - this.convertMCToHeight(1), 0));
						}

						riverAttrib.addRiver(r.riverWidth);
						riverAttrib.setRiverMidpoint(curNode.center.point);
						if(nextNode != null)
						{
							riverAttrib.setDownRiver(nextNode.center);
							if(nextNode.center.getElevation() > curNode.center.getElevation())
								nextNode.center.setElevation(curNode.center.getElevation());
							//Sanity
							RiverAttribute nextAttrib = ((RiverAttribute)nextNode.center.getAttribute(Attribute.riverUUID));
							if(nextAttrib == null)
							{
								nextAttrib = new RiverAttribute(Attribute.riverUUID);
								nextNode.center.addAttribute(nextAttrib);
							}

							nextAttrib.addUpRiverCenter(curNode.center);
							curNode = nextNode;
						}
					}
				}
			}
		}

		//After the rivers are built, we traverse through them one more time to add some jitter to the straight rivers
		for(River river : rivers)
		{
			for(RiverNode rn : river.nodes)
			{
				RiverAttribute Attrib = ((RiverAttribute)rn.center.getAttribute(Attribute.riverUUID));
				if(rn.upRiver != null && rn.downRiver != null && Attrib.upriver != null && Attrib.upriver.size() == 1)
				{
					HexDirection hd = rn.center.getDirection(rn.upRiver);
					HexDirection dn = rn.center.getDirection(rn.downRiver);
					if(hd.getOpposite() == dn)
					{
						double dist = 7.5 - Attrib.getRiver();
						double x = 0;
						double y = 0;

						/*if(hd == HexDirection.North || hd == HexDirection.South)
							if((rn.center.index & 1) > 0)
							{x = 6 - Attrib.getRiver(); y = -4 + Attrib.getRiver();}
							else
							{x = -6 + Attrib.getRiver(); y = 4 - Attrib.getRiver();}
						else if(hd == HexDirection.NorthEast || hd == HexDirection.SouthWest)
						{
							if((rn.center.index & 1) > 0)
							{y = -6 + Attrib.getRiver(); x = -6 + Attrib.getRiver();}
							else
							{y = 6 - Attrib.getRiver(); x = 6 - Attrib.getRiver();}
						}
						else if(hd == HexDirection.SouthEast || hd == HexDirection.NorthWest)
						{
							if((rn.center.index & 1) > 0)
							{y = -6 + Attrib.getRiver(); x = 6 - Attrib.getRiver();}
							else
							{y = 6 - Attrib.getRiver(); x = -6 + Attrib.getRiver();}
						}
						else
						{
							System.out.println("River:" + hd.toString() + " : " + dn.toString());
						}*/


						Attrib.setRiverMidpoint(rn.center.point.plus(x, y));
					}
				}
			}
		}
	}

	public RiverNode getNextRiverNode(River river, RiverNode curNode)
	{
		RiverAttribute curAttrib = (RiverAttribute)curNode.center.getAttribute(Attribute.riverUUID);
		Center next = (curAttrib != null ? curAttrib.getDownRiver() : null);
		if(next != null)
			return new RiverNode(next);

		RandomCollection<Center> possibles = new RandomCollection<Center>(this.mapRandom);

		//The river will attempt to meander if we aren't propagating down an existing river
		if(curAttrib == null || curAttrib.getRiver() == 0)
		{
			int curMCElev = convertHeightToMC(curNode.center.elevation);
			//Go through each neighbor and find all possible hexes at the same elevation or lower
			for(Center n : curNode.center.neighbors)
			{
				int nMCElev = convertHeightToMC(n.elevation);
				//Make sure that we aren't trying to flow backwards if the hexes are on the same level
				if(n == curNode.upRiver)
					continue;
				//We dont want our rivers to turn at very sharp angles so we check our previous node to make sure that it is not neighbors with this node
				if(n.neighbors.contains(curNode.upRiver))
					continue;

				//If the elevations are the same or lower then this might be an ok location
				if(nMCElev <= curMCElev)
				{
					//If next to a water hex then we move to it instead of anything else
					if(n.hasMarker(Marker.Ocean) || n.hasMarker(Marker.Water))
					{
						//Unless we are dealing with a lake tile and this is the first River node
						if(river.riverStart == curNode && !n.hasMarker(Marker.Ocean))
							continue;
						return new RiverNode(n);
					}

					//If one of the neighbors is also a river then we want to join it
					if(n.getAttribute(Attribute.riverUUID) != null && ((RiverAttribute)n.getAttribute(Attribute.riverUUID)).getRiver() > 0)
						return new RiverNode(n);

					if(curNode.center.elevation - n.elevation > 0.06)
						return new RiverNode(n);

					//If the elevation is <= our current cell elevation then we allow this cell to be selected
					if(nMCElev == curMCElev)
						possibles.add(0.2, n);
					else if(nMCElev < curMCElev-5)
						possibles.add(1.0,n);
					else
						possibles.add(0.5,n);
				}
			}
		}
		if(possibles.size() > 0)
		{
			Center p = possibles.next();
			return new RiverNode(p);
		}

		return null;
	}

	public int convertHeightToMC(double d)
	{
		return (int)Math.floor(this.islandParams.islandMaxHeight * d);
	}

	public double convertMCToHeight(int i)
	{
		return i/this.islandParams.islandMaxHeight;
	}

	public void assignMoisturePostRedist() 
	{
		LinkedList<Center> queue = new LinkedList<Center>();
		// Fresh water
		for(Center cr : centers)
		{
			if (cr.hasMarker(Marker.Coast)) 
			{
				queue.push(cr);
			} 
		}

		while (queue.size() > 0) 
		{
			Center q = queue.pop();

			for(Center adjacent : q.neighbors)
			{
				if(!adjacent.hasMarker(Marker.Ocean) && adjacent.getElevation() - q.getElevation() < 0.08)
				{
					double moistureMult = Math.max(1 - adjacent.getElevation() / 0.25, 0);
					double newMoisture = q.moisture * moistureMult;
					if (newMoisture > adjacent.moisture) 
					{
						adjacent.moisture = newMoisture;
						queue.push(adjacent);
					}
				}
			}
		}
	}

	// Assign a biome type to each polygon. If it has
	// ocean/coast/water, then that's the biome; otherwise it depends
	// on low/high elevation and low/medium/high moisture. This is
	// roughly based on the Whittaker diagram but adapted to fit the
	// needs of the island map generator.
	public BiomeType getBiome(Center p) 
	{
		if (p.hasMarker(Marker.Ocean)) {
			return BiomeType.OCEAN;
		} else if (p.hasMarker(Marker.Water)) {
			if (p.elevation < 0.1) return BiomeType.MARSH;
			//if (p.elevation > 0.8) return BiomeType.ICE;
			return BiomeType.LAKE;
		} else if (p.hasMarker(Marker.Coast)) {
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
		return islandParams.insidePerlin(p);
	}

	double elevationBucket(Center p) 
	{
		if (p.hasMarker(Marker.Ocean)) return -1;
		else return Math.floor(p.elevation*10);
	}

	/*public Center getClosestCenter(Point p)
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
	}*/

	/**
	 * @return nearest Center point for the containing hex
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
		if(NUM_POINTS_SQ*x+y >= centers.size())
			return centers.get(0);

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

	public void writeToNBT(NBTTagCompound nbt)
	{
		NBTTagList nList = new NBTTagList();
		for(Center c : centers)
		{
			NBTTagCompound n = new NBTTagCompound();
			c.writeToNBT(n);
			nList.appendTag(n);
		}
		nbt.setTag("centers", nList);

		nList = new NBTTagList();
		for(Corner c : corners)
		{
			NBTTagCompound n = new NBTTagCompound();
			c.writeToNBT(n);
			nList.appendTag(n);
		}
		nbt.setTag("corners", nList);

		nList = new NBTTagList();
		for(Edge e : edges)
		{
			NBTTagCompound n = new NBTTagCompound();
			e.writeToNBT(n);
			nList.appendTag(n);
		}
		nbt.setTag("edges", nList);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList centerList = nbt.getTagList("centers", 10);
		NBTTagList cornerList = nbt.getTagList("corners", 10);
		NBTTagList edgeList = nbt.getTagList("edges", 10);
		Center c;

		//First we create empty centers, corners, and edges that can be referenced from each other
		for(int i = 0; i < centerList.tagCount(); i++)
		{
			centers.add(new Center(i));
		}

		for(int i = 0; i < cornerList.tagCount(); i++)
		{
			corners.add(new Corner(i));
		}

		for(int i = 0; i < edgeList.tagCount(); i++)
		{
			edges.add(new Edge(i));
		}

		for(int i = 0; i < centers.size(); i++)
		{
			c = centers.get(i);
			c.readFromNBT(centerList.getCompoundTagAt(i), this);

			//Rebuild the lake list
			if(c.hasAttribute(Attribute.lakeUUID))
			{
				int lakeID = ((LakeAttribute)c.getAttribute(Attribute.lakeUUID)).getLakeID();
				if(lakes.size() <= lakeID)
					lakes.setSize(lakeID+1);
				if(lakes.get(lakeID) == null)
				{
					lakes.set(lakeID, new Lake());
					lakes.get(lakeID).lakeID = lakeID;
				}

				lakes.get(lakeID).addCenter(c);
			}
		}

		for(int i = 0; i < corners.size(); i++)
		{
			corners.get(i).readFromNBT(cornerList.getCompoundTagAt(i), this);
		}

		for(int i = 0; i < edges.size(); i++)
		{
			edges.get(i).readFromNBT(edgeList.getCompoundTagAt(i), this);
		}
	}
}