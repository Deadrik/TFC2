// Make a map out of a voronoi graph
// Original Author: amitp@cs.stanford.edu
// License: MIT
package com.bioxx.jmapgen;

import java.awt.Rectangle;
import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.attributes.*;
import com.bioxx.jmapgen.com.nodename.delaunay.DelaunayUtil;
import com.bioxx.jmapgen.com.nodename.delaunay.Voronoi;
import com.bioxx.jmapgen.com.nodename.geom.LineSegment;
import com.bioxx.jmapgen.dungeon.Dungeon;
import com.bioxx.jmapgen.graph.*;
import com.bioxx.jmapgen.graph.Center.HexDirection;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.pathfinding.CenterPathFinder;
import com.bioxx.jmapgen.processing.AnimalProcessor;
import com.bioxx.jmapgen.processing.CaveProcessor;
import com.bioxx.jmapgen.processing.OreProcessor;
import com.bioxx.jmapgen.processing.PortalProcessor;
import com.bioxx.tfc2.api.types.ClimateTemp;
import com.bioxx.tfc2.api.types.Moisture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IslandMap 
{
	public static Logger log = LogManager.getLogger("IslandMap");
	public int NUM_POINTS = 4096*4;
	public int NUM_POINTS_SQ = (int) Math.sqrt(NUM_POINTS);
	boolean builtVoronoi = false;
	// Passed in by the caller:
	public int SIZE;

	protected IslandParameters islandParams;

	protected IslandData islandData;
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

	public CenterPathFinder pathfinder;
	public Vector<Dungeon> dungeons;

	private CaveProcessor caves;
	private OreProcessor ores;
	private AnimalProcessor animalProc;
	private PortalProcessor portalProc;

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
		caves = new CaveProcessor(this);
		ores = new OreProcessor(this);
		animalProc = new AnimalProcessor(this);
		portalProc = new PortalProcessor(this);
		dungeons = new Vector<Dungeon>();
	}

	public void newIsland(IslandParameters is) 
	{
		islandParams = is;
		mapRandom.setSeed(seed);
		NUM_POINTS = is.SIZE*4;
		NUM_POINTS_SQ = (int) Math.sqrt(NUM_POINTS);
		is.createShape(seed);
		islandData = new IslandData(this, is);
	}

	public IslandParameters getParams()
	{
		return this.islandParams;
	}

	public IslandData getIslandData()
	{
		return this.islandData;
	}

	public void generateFake()
	{
		points.clear();
		edges.clear();
		centers.clear();
		corners.clear();
		lakes.clear();
		rivers.clear();
		caves = new CaveProcessor(this);
		ores = new OreProcessor(this);
		animalProc = new AnimalProcessor(this);
		dungeons.clear();

		points = this.generateHexagon(SIZE);
		Rectangle R = new Rectangle();
		R.setFrame(0, 0, SIZE, SIZE);
		//System.out.println("Starting Creating map Voronoi...");
		Voronoi voronoi = new Voronoi(points, R);
		//System.out.println("Finished Creating map Voronoi...");
		buildGraph(points, voronoi);
	}

	public void generateFull() 
	{
		points.clear();
		edges.clear();
		centers.clear();
		corners.clear();
		lakes.clear();
		rivers.clear();
		caves = new CaveProcessor(this);
		ores = new OreProcessor(this);
		animalProc = new AnimalProcessor(this);
		dungeons.clear();

		points = this.generateHexagon(SIZE);
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

		redistributeElevations(landCorners(corners));

		// Assign elevations to non-land corners
		for(Iterator<Corner> i = corners.iterator(); i.hasNext();)
		{
			Corner q = (Corner)i.next();
			if (q.hasMarker(Marker.Ocean) || q.hasMarker(Marker.Coast)) 
			{
				q.elevation = 0.0;
			}
		}
		sortClockwise();
		if(!this.islandParams.hasFeature(Feature.NoLand))
		{

			// Polygon elevations are the average of their corners
			assignPolygonElevations();
			//if(!this.islandParams.hasFeature(Feature.Desert))
			assignLakeElevations(lakeCenters(centers));

			// Determine downslope paths.
			calculateDownslopesCenter();

			createVolcano(getCentersAbove(getLandCenters(), 0.4));

			createValleys(getCentersAbove(0.4));

			createCanyons();

			calculateDownslopesCenter();
			createGorges();
			createRamps();

			createMesas();

			// Determine downslope paths.
			calculateDownslopesCenter();
			// Create rivers.
			//if(!this.getParams().hasFeature(Feature.Desert))
			createRivers(getCentersAbove(0.25));
			assignSlopedNoise();
			assignHillyNoise();
			createSpires();
			createClearings();
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
		//redistributeMoisture(getLandCenters());
		assignMoisturePostRedist();

		setupBiomeInfo();

		caves.generate();
		ores.generate();

		if(!this.getParams().hasFeature(Feature.NoLand))
		{
			portalProc.generate();
		}

		animalProc.generate();

	}

	public Center getPortalForFacing(EnumFacing facing)
	{
		for(Center c : centers)
		{
			if(c.hasAttribute(Attribute.Portal) && ((PortalAttribute)c.getAttribute(Attribute.Portal)).direction == facing)
				return c;
		}

		return null;
	}

	private void createSpires()
	{
		if(!this.getParams().hasFeature(Feature.Spires))
			return;

		Vector<Center> land = this.getLandCenters();
		Vector<Center> starts = new Vector<Center>();

		for(int i = 0; i < 20; i++)
		{
			Center c = land.get(this.mapRandom.nextInt(land.size()));
			LinkedList<Center> queue = new LinkedList<Center>();
			queue.add(c);
			int count = 1;
			while(!queue.isEmpty())
			{
				c = queue.pop();
				if((count < 20 && this.mapRandom.nextFloat() < 0.6) || count > 50)
					continue;

				if(c.hasAttribute(Attribute.River) || c.hasAnyMarkersOf(Marker.Water, Marker.Pond, Marker.Coast))
				{
					continue;
				}
				count++;
				if(mapRandom.nextBoolean())
					c.setMarkers(Marker.Spire);
				for(Center n : c.neighbors)
					if(!n.hasMarker(Marker.Spire))
					{
						queue.add(n);
					}
			}

		}

	}

	private void createMesas()
	{
		if(!this.islandParams.hasFeature(Feature.Mesas))
			return;

		Vector<Center> landCenters = this.getLandCenters();
		landCenters = this.filterOutMarkers(landCenters, Marker.SmallCrater, Marker.Volcano, Marker.Water, Marker.Coast);
		landCenters = filterOutAttributes(landCenters, Attribute.Canyon, Attribute.Gorge);
		for(int i = 0; i < 100; i++)
		{
			Center c = landCenters.get(mapRandom.nextInt(landCenters.size()));
			double elev = 1 - c.elevation;
			elev = c.elevation+((0.5+(mapRandom.nextDouble() * 0.3)) * elev);
			c.setMarkers(Marker.Mesa);
			c.setElevation(elev);
			if(mapRandom.nextBoolean())
				caves.gen(c.getRandomNeighbor(mapRandom), 1000+i, false, 2+mapRandom.nextInt(3));
		}
	}

	private void createCanyons()
	{
		if(!this.islandParams.hasFeature(Feature.Canyons))
			return;

		Vector<Center> highCenters = getCentersAbove(0.8);
		Vector<Center> startCenters = new Vector<Center>();

		if(highCenters.size() == 0)
			return;

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

				if(start.hasAttribute(Attribute.Canyon) || startCenters.contains(start))
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

				if(!curCenter.hasAttribute(Attribute.Canyon))
					curCenter.setElevation(Math.max(minElevation, gn.getCenter().getElevation()*(1-elevMult)));

				//Create a canyon attribute for the node center
				CanyonAttribute a = new CanyonAttribute(Attribute.Canyon, gn.nodeNum);
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
					if(!n.hasAttribute(Attribute.Canyon) && !n.hasMarker(Marker.Water))
					{
						CanyonAttribute c = new CanyonAttribute(Attribute.Canyon, gn.nodeNum);
						c.setDown(gn.getCenter());
						if(n.addAttribute(c))
						{
							n.setElevation(Math.max(minElevation, gn.getCenter().getElevation()));
							if(mapRandom.nextInt(5) == 0)
								caves.gen(gn.getCenter(), 2000+i, false, 2+mapRandom.nextInt(3));
						}
					}
				}
			}
		}
	}

	private void createRamps()
	{
		if(getParams().hasAnyFeatureOf(Feature.LowLand))
			return;

		Vector<Center> landCenters = this.getLandCenters();
		Vector<Center> cliffCenters = new Vector<Center>();
		double mcHeight = this.getParams().getMCBlockHeight();
		for(Center c : landCenters)
		{
			Center lowest = c.getLowestNeighbor();
			if(c.getElevation() - lowest.getElevation() > mcHeight * 10)
			{
				cliffCenters.add(c);
			}
		}
		Collections.sort(cliffCenters, new ElevationComparator());
		int count = (int) (cliffCenters.size()*0.1);
		for(int i = 0; i < count; i++)
		{
			int index = mapRandom.nextInt(cliffCenters.size());
			index = index + mapRandom.nextInt(cliffCenters.size() - index);//We try to skew towards higher elevations
			Center c = cliffCenters.get(index).getLowestNeighbor();

			while(true)
			{
				//Get the lowest neighbor and the height diff between this block and the neighbor
				Center highest = getRandomCenter(c.getOnlyHigherCenters());
				double blockDiff = (highest.getElevation()-c.getElevation()) * mcHeight;

				if(blockDiff <= 5)
					break;
				highest.setElevation(c.getElevation() + (4*mcHeight));
				c = highest;
			}
		}
	}

	private Center getRandomCenter(List<Center> list)
	{
		return list.get(mapRandom.nextInt(list.size()));
	}

	private void createClearings()
	{
		Vector<Center> landCenters = this.getLandCenters();
		Vector<Center> wetCenters = new Vector<Center>();
		for(Center c : landCenters)
		{
			if(mapRandom.nextInt(15) == 0)
			{
				c.setMarkers(Marker.Clearing);
				for(Center n : c.neighbors)
					if(mapRandom.nextBoolean())
						n.setMarkers(Marker.Clearing);
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

			if(n.hasAttribute(Attribute.Canyon))
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
		if(!this.islandParams.hasFeature(Feature.Volcano) || candidates.size() == 0)
			return;
		Center mid = candidates.get(mapRandom.nextInt(candidates.size()));
		System.out.println("Volcano: X" + mid.point.x + " Z"+ mid.point.y);
		Vector<Center> caldera = new Vector<Center>();
		caldera.add(mid);
		caldera.addAll(mid.neighbors);

		for(Center c : caldera)
		{
			c.resetMarkers();
			c.setMarkers(Marker.Lava, Marker.Volcano);
			c.setElevation(0.80);
		}

		LinkedList<Center> queue = new LinkedList<Center>();
		double elev = 0.03;
		double baseDist = mid.point.distanceSq(mid.neighbors.get(0).point);

		for(Center c : caldera)
		{
			for(Center n : c.neighbors)
			{
				if(!n.hasAnyMarkersOf(Marker.Volcano))
					queue.add(n);
			}
		}
		Center qc;
		double dist = 0;
		while(!queue.isEmpty())
		{
			qc = queue.pop();
			if(!qc.hasAnyMarkersOf(Marker.Volcano))
			{
				dist = mid.point.distanceSq(qc.point);
				dist /= baseDist;
				if(qc.getElevation() < 1-dist*elev)
				{
					qc.setMarkers(Marker.Volcano);
					qc.setElevation(1-dist*elev);
					if(qc.getElevation() < -1 || qc.getElevation() > 1)
						System.out.println(qc.getElevation());
					queue.addAll(qc.neighbors);
				}
				else
				{

				}
			}
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
					if(c.hasMarker(Marker.Water) && !c.hasAnyMarkersOf(Marker.Ocean, Marker.Pond))
					{
						Lake l = centerInExistingLake(c);
						if(l != null && !lakesToDrop.contains(l))
						{
							lakesToDrop.add(l);
						}
						continue;
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
					n.elevation = minElevation/**0.8*/ + (-convertMCToHeight(2) + mapRandom.nextDouble()*convertMCToHeight(5));//Math.max(minElevation, n.elevation*0.8);
					n.setMarkers(Marker.Valley);
				}
				for(Lake l : lakesToDrop)
				{
					for(Center c : l.centers)
					{
						c.elevation = minElevation/**0.79*/;
						if(c.hasAttribute(Attribute.Lake))
						{
							LakeAttribute attrib = (LakeAttribute) c.getAttribute(Attribute.Lake);
							attrib.setLakeElev(minElevation);
						}
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
			if(!center.hasAttribute(Attribute.Canyon) && !center.hasAttribute(Attribute.Gorge) && 
					!center.hasMarker(Marker.Coast) && this.mapRandom.nextInt(100) < 10 && !center.hasMarker(Marker.Water) && 
					center.getAttribute(Attribute.River) == null)
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
					if(!center2.hasMarker(Marker.Lava) && !center2.hasAttribute(Attribute.Gorge) && !center2.hasMarker(Marker.Coast) && center2.getAttribute(Attribute.River) == null && !center2.hasMarker(Marker.Water))
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
			if(!center.hasAttribute(Attribute.Gorge) && !center.hasMarker(Marker.Coast) && !center.hasMarker(Marker.Water) && !center.hasAttribute(Attribute.River))
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

	public Vector<Center> filterRange(Vector<Center> centers, Center home, double range)
	{
		double sq = range * range;
		Vector<Center> out = new  Vector<Center>();
		Vector<Center> queue = new  Vector<Center>();
		Vector<Center> checked = new  Vector<Center>();
		queue.add(home);
		Iterator<Center> iter = queue.iterator();
		while(iter.hasNext())
		{
			Center c = iter.next();
			if(checked.contains(c))
				continue;
			if(c.point.distanceSq(home.point) < sq)
			{
				checked.add(c);
				out.add(c);
				queue.addAll(c.neighbors);
			}
		}
		return out;
	}

	public Vector<Center> filterKeepMarkers(Vector<Center> centers, Marker... markers)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(c.hasAnyMarkersOf(markers))
				out.add(c);
		}
		return out;
	}

	public Vector<Center> filterOutMarkers(Vector<Center> centers, Marker... markers)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(!c.hasAnyMarkersOf(markers))
				out.add(c);
		}
		return out;
	}

	public Vector<Center> filterOutAttributes(Vector<Center> centers, UUID... attr)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			boolean keep = true;
			for(UUID uuid : attr)
			{
				if(c.hasAttribute(uuid))
				{
					keep = false;
					break;
				}
			}
			if(keep)
				out.add(c);
		}
		return out;
	}

	public Vector<Center> filterKeepAttributes(Vector<Center> centers, UUID... attr)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			for(UUID uuid : attr)
			{
				if(c.hasAttribute(uuid))
				{
					out.add(c);
					break;
				}
			}
		}
		return out;
	}

	public Vector<Center> filterKeepCoords(Vector<Center> centers, Point pMin, Point pMax)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(c.point.x > pMin.x && c.point.y > pMin.y && c.point.x < pMax.x && c.point.y < pMax.y)
				out.add(c);
		}
		return out;
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
		RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.River));
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
		RiverAttribute attrib = ((RiverAttribute)c.getAttribute(Attribute.River));
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
				center.elevation = -0.04/* - mapRandom.nextDouble()*0.03*/;
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
		if(point.x ==0 || point.x == SIZE || point.y == 0 || point.y == SIZE) 
			q.setMarkers(Marker.Border);	

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

	/* Determine elevations and water at Voronoi corners. By
	// construction, we have no local minima. This is important for
	// the downslope vectors later, which are used in the river
	// construction algorithm. Also by construction, inlets/bays
	// push low elevation areas inland, which means many rivers end
	// up flowing out through them. Also by construction, lakes
	// often end up on river paths because they don't raise the
	// elevation as much as other terrain does.*/
	private int assignCornerElevations() 
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
			{
				c.setMarkers(Marker.Water, Marker.Ocean);
			}
			else if(c.hasMarker(Marker.Border))
				numLandBorder++;

			if (c.hasMarker(Marker.Border)) 
			{
				c.elevation = 0;
				queue.add(c);
			}
		}

		assignLakes();

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

	public void assignLakes()
	{
		int lakeCount = 5;//Number of distinct lakes to attempt to generate
		int lakeSize = 160;//Size of lakes to generate

		Moisture m = getParams().getIslandMoisture();

		if(m.equals(Moisture.LOW))
			lakeSize = 80;
		else if(m.isGreaterThanOrEqual(Moisture.HIGH))
			lakeSize = 240;

		for(int lc = 0; lc < lakeCount; lc++)
		{
			int size = 0;
			ArrayList<Corner> lakeTiles = new ArrayList<Corner>();
			LinkedList<Corner> queue = new LinkedList<Corner>();
			Corner startCorner = null;
			while(startCorner == null)
			{
				Corner c = corners.get(this.mapRandom.nextInt(corners.size()));
				if (!c.hasMarker(Marker.Water))
					startCorner = c;
			}
			queue.add(corners.get(this.mapRandom.nextInt(corners.size())));

			while(!queue.isEmpty())
			{
				Corner c = queue.pop();

				if(mapRandom.nextInt(100) > 15 && !c.isShoreline() && !c.hasMarker(Marker.Water))
				{
					lakeTiles.add(c);
					if(size < lakeSize)
						queue.addAll(c.adjacent);
					size++;
				}
			}

			if(lakeTiles.size() < lakeSize / 3)
			{
				lc--;
			}
			else
			{
				for(Corner c : lakeTiles)
				{
					c.setMarkers(Marker.Water);
				}
			}
		}
	}

	private void resetMap()
	{
		for(Corner c : corners)
		{
			c.resetMarkers();
			c.elevation = 0;
		}

		for(Center c : centers)
		{
			c.resetMarkers();
			c.setElevation(0);
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

	/* Change the overall distribution of elevations so that lower
	// elevations are more common than higher
	// elevations. Specifically, we want elevation X to have frequency
	// (1-X).  To do this we will sort the corners, then set each
	// corner to its desired elevation.*/
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

	public void assignMoisture() 
	{
		LinkedList<Center> queue = new LinkedList<Center>();
		// Fresh water
		for(Center cr : centers)
		{
			RiverAttribute attrib = (RiverAttribute)cr.getAttribute(Attribute.River);
			if ((cr.hasMarker(Marker.Water) || (attrib != null && attrib.getRiver() > 0)) && !cr.hasMarker(Marker.Ocean)) 
			{
				double rivermult = attrib != null ? attrib.getRiver() : 0;
				cr.setMoistureRaw((attrib != null && attrib.getRiver() > 0) ? 1 : 1);
				/*if(this.getParams().hasFeature(Feature.Desert))
					cr.setMoistureRaw((Math.log10(cr.getMoistureRaw()*0.5)+2)/2);*/
				queue.push(cr);
			} 
			else 
			{
				cr.setMoistureRaw(0.0);
			}

			if (cr.hasMarker(Marker.Ocean)) 
			{
				cr.setMoistureRaw(1.0);
			}
			if (cr.hasMarker(Marker.Coast)) 
			{
				cr.setMoistureRaw(Math.max(0.5, cr.getMoistureRaw()));
			}
		}
		//This controls how far the moisture level spreads from the moisture source. Lower values cause less overall island moisture.
		double moistureMult =  0.6+(0.2 * this.islandParams.getIslandMoisture().getMoisture());
		double maxElev = islandParams.islandMaxHeight;


		if(this.getParams().hasFeature(Feature.Desert))
			moistureMult = (0.8 * moistureMult);


		while (queue.size() > 0) 
		{
			Center q = queue.pop();

			for(Center adjacent : q.neighbors)
			{
				double newMoisture = q.getMoistureRaw() * moistureMult;
				double elevDiff = this.convertHeightToMC(adjacent.getElevation()) - this.convertHeightToMC(q.getElevation());
				if(!islandParams.hasFeature(Feature.LowLand) && elevDiff > 0)
				{
					newMoisture = q.getMoistureRaw() * (moistureMult * (1-(elevDiff*2/maxElev)));
				}

				if (newMoisture > adjacent.getMoistureRaw()) 
				{
					adjacent.setMoistureRaw(newMoisture);
					queue.push(adjacent);
				}
			}
		}
		// Salt water
		/*for(Center cr : centers)
		{
			if (cr.hasMarker(Marker.Ocean)) 
			{
				cr.setMoistureRaw(1.0);
			}
			if (cr.hasMarker(Marker.Coast)) 
			{
				cr.setMoistureRaw(Math.max(0.5, cr.getMoistureRaw()));
			}
		}*/
	}

	// Change the overall distribution of moisture to be evenly distributed.	
	public void redistributeMoisture(Vector<Center> locations) {
		int i;
		Collections.sort(locations, new MoistureComparator());
		Center c1;
		for (i = 0; i < locations.size(); i++) 
		{
			c1 = locations.get(i);
			float m = i/(float)(locations.size());
			if(this.getParams().hasFeature(Feature.Desert))
				m = m*0.25f;
			c1.setMoistureRaw(m);
		}
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
					double newMoisture = q.getMoistureRaw() * moistureMult;
					if (newMoisture > adjacent.getMoistureRaw()) 
					{
						adjacent.setMoistureRaw(newMoisture);
						queue.push(adjacent);
					}
				}
				else if(adjacent.hasMarker(Marker.Ocean))
				{
					adjacent.setMoistureRaw(1.0);
				}
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
				LakeAttribute attrib = new LakeAttribute(Attribute.Lake);
				if(c.getElevation() < lake.lowestCenter.getElevation())
					attrib.setLakeElev(c.getElevation() );
				attrib.setLakeElev(lake.lowestCenter.getElevation());
				attrib.setLakeID(lakeID);
				//Here we try to smooth the centers around lakes a bit
				for(Center n : c.neighbors)
				{
					if(n.hasAttribute(Attribute.Lake))
					{
						LakeAttribute nAttrib = (LakeAttribute) n.getAttribute(Attribute.Lake);
						if(nAttrib.getBorderDistance() < attrib.getBorderDistance())
							attrib.setBorderDistance(nAttrib.getBorderDistance() + 1);
						else if (nAttrib.getBorderDistance() > attrib.getBorderDistance()+1)
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
		return getCentersAbove(centers, elev);
	}

	public Vector<Center> getCentersAbove(Vector<Center> inCenters, double elev)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : inCenters)
		{
			if (c.elevation >= elev)
				out.add(c);
		}
		return out;
	}

	public Vector<Center> getCentersBelow(Vector<Center> inCenters, double elev, boolean allowWater)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : inCenters)
		{
			if (c.elevation <= elev)
			{
				if(allowWater || !c.hasMarker(Marker.Water))
					out.add(c);
			}
		}
		return out;
	}

	public Vector<Center> getCentersBelow(double elev, boolean allowWater)
	{
		return getCentersBelow(centers, elev, allowWater);
	}

	public Vector<Center> getLandCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(!c.hasMarker(Marker.Water))
				out.add(c);
		}
		return out;
	}

	public Vector<Center> getWaterCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(c.hasMarker(Marker.Water))
				out.add(c);
		}
		return out;
	}

	public Vector<Center> getRiverCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(c.hasAttribute(Attribute.River))
				out.add(c);
		}
		return out;
	}

	public Vector<Center> getOceanCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(c.hasMarker(Marker.Ocean))
				out.add(c);
		}
		return out;
	}

	public Vector<Center> getLakeCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(c.biome == BiomeType.LAKE)
				out.add(c);
		}
		return out;
	}

	public Vector<Center> getMarshCenters()
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(c.biome == BiomeType.MARSH)
				out.add(c);
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
			if(c.hasAttribute(Attribute.Canyon))
			{
				CanyonAttribute a = (CanyonAttribute) c.getAttribute(Attribute.Canyon);
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
				if(possibleStarts.contains(n) || n.hasAttribute(Attribute.Canyon))
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
					if(!cn.center.hasAttribute(Attribute.Gorge))
					{
						cn.center.setElevation(Math.max(gorge.minElev,cn.center.elevation - Math.min(diff * 0.5, 0.2)));
						if(cn.getUp() != null && cn.center.getElevation() > cn.getUp().center.getElevation())
						{
							cn.center.setElevation(cn.getUp().center.getElevation());
						}

						GorgeAttribute a = new GorgeAttribute(Attribute.Gorge);
						if(cn.getUp() != null)
							a.setUp(cn.getUp().center);
						if(cn.getDown() != null)
							a.setDown(cn.getDown().center);
						a.gorgeID = id;
						cn.center.addAttribute(a);
						if(mapRandom.nextInt(6) == 0)
							caves.gen(cn.center, 3000+id, false, 2+mapRandom.nextInt(3));
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
				if(n.hasAttribute(Attribute.Gorge))
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

			boolean nextToWater = false;
			for(Center n : c.neighbors)
			{
				if(n.hasMarker(Marker.Water))
				{
					nextToWater=true;
					possibleStarts.add(n);
				}
			}
			if(!nextToWater)
				possibleStarts.add(c);
		}


		if(this.islandParams.hasFeature(Feature.Gorges))
		{
			for(Center cn : centers)
			{
				if(cn.hasAttribute(Attribute.Gorge))
				{
					if(((GorgeAttribute)cn.getAttribute(Attribute.Gorge)).getUp() == null && 
							(mapRandom.nextDouble() > 0.25 || cn.hasAttribute(Attribute.Canyon)))
					{
						possibleStarts.add(cn);
					}
				}
			}
		}

		for (int i = 0; i < lakes.size(); i++) 
		{
			Center lowest = lakes.get(i).lowestCenter;

			for(Center cen : lowest.neighbors)
			{
				if(!cen.hasMarker(Marker.Water))
				{
					possibleStarts.add(lowest);
					break;
				}
			}
		}

		buildRiver(possibleStarts);
	}

	private void buildRiver(Vector<Center> possibleStarts) {
		Center c;
		for (int i = 0; i < possibleStarts.size(); i++) 
		{
			c = possibleStarts.get(i);
			RiverAttribute cAttrib = ((RiverAttribute)c.getAttribute(Attribute.River));
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
				RiverAttribute nextAttrib = ((RiverAttribute)nextNode.center.getAttribute(Attribute.River));

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
			RiverAttribute startAttrib = (RiverAttribute)r.riverStart.center.getAttribute(Attribute.River);
			if(r.riverStart == null || (startAttrib != null && startAttrib.getRiver() != 0) || r.nodes.size() < 4)
				isValid = false;

			if(isValid && r.riverStart != null && r.riverStart.center != null)
			{
				Lake lake = centerInExistingLake(r.riverStart.center);
				if(r.riverStart.center.hasMarker(Marker.Water) && lake != null && lake.centers.size() > 8)
					r.riverWidth = 4 - 3 * r.riverStart.center.elevation;
				else if(r.riverStart.center.hasAttribute(Attribute.Gorge))
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
							if(n.getAttribute(Attribute.River) != null && 
									((RiverAttribute)n.getAttribute(Attribute.River)).getRiver() > 0)
							{
								rivers.remove(r);
								cancelRiver = true;
								break;
							}
						}
						r.riverStart.center.setMarkers(Marker.Pond, Marker.Water);
					}
					else
					{
						if(j < r.nodes.size())
							nextNode = r.nodes.get(j);
						else nextNode = null;

						//Sanity
						RiverAttribute riverAttrib = ((RiverAttribute)curNode.center.getAttribute(Attribute.River));
						if(riverAttrib == null)
						{
							riverAttrib = new RiverAttribute(Attribute.River);
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
							RiverAttribute nextAttrib = ((RiverAttribute)nextNode.center.getAttribute(Attribute.River));
							if(nextAttrib == null)
							{
								nextAttrib = new RiverAttribute(Attribute.River);
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
				RiverAttribute Attrib = ((RiverAttribute)rn.center.getAttribute(Attribute.River));
				if(rn.upRiver != null && rn.downRiver != null && Attrib.upriver != null && Attrib.upriver.size() == 1)
				{
					HexDirection hd = rn.center.getDirection(rn.upRiver);
					HexDirection dn = rn.center.getDirection(rn.downRiver);
					if(hd.getOpposite() == dn)
					{
						double x = 0;
						double y = 0;

						if(hd == HexDirection.North || hd == HexDirection.South)
							if(((rn.center.index >> 1) & 1) > 0)
							{x = 6 - Attrib.getRiver();}
							else
							{x = -6 + Attrib.getRiver();}
						else if(hd == HexDirection.NorthEast || hd == HexDirection.SouthWest)
						{
							if(((rn.center.index >> 1) & 1) > 0)
							{y = -6 + Attrib.getRiver(); x = -6 + Attrib.getRiver();}
							else
							{y = 6 - Attrib.getRiver(); x = 6 - Attrib.getRiver();}
						}
						else if(hd == HexDirection.SouthEast || hd == HexDirection.NorthWest)
						{
							if(((rn.center.index >> 1) & 1) > 0)
							{y = -6 + Attrib.getRiver(); x = 6 - Attrib.getRiver();}
							else
							{y = 6 - Attrib.getRiver(); x = -6 + Attrib.getRiver();}
						}
						else
						{
							System.out.println("River:" + hd.toString() + " : " + dn.toString());
						}


						Attrib.setRiverMidpoint(rn.center.point.plus(x, y));
					}
				}
			}
		}
	}

	public RiverNode getNextRiverNode(River river, RiverNode curNode)
	{
		RiverAttribute curAttrib = (RiverAttribute)curNode.center.getAttribute(Attribute.River);
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
					if(n.getAttribute(Attribute.River) != null && ((RiverAttribute)n.getAttribute(Attribute.River)).getRiver() > 0)
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

	/* Assign a biome type to each polygon. If it has
	// ocean/coast/water, then that's the biome; otherwise it depends
	// on low/high elevation and low/medium/high moisture. This is
	// roughly based on the Whittaker diagram but adapted to fit the
	// needs of the island map generator.*/
	public BiomeType getBiome(Center p) 
	{
		ClimateTemp temp = getParams().getIslandTemp();
		float m = p.getMoistureRaw();
		m *= this.getParams().getIslandMoisture().getMoisture();
		if (p.hasMarker(Marker.Ocean)) 
		{
			if(p.hasMarker(Marker.CoastWater))
				return BiomeType.OCEAN;
			else 
				return BiomeType.DEEP_OCEAN;
		} 
		else if (p.hasMarker(Marker.Water)) 
		{
			if (this.getParams().getIslandMoisture().isGreaterThanOrEqual(Moisture.VERYHIGH) && p.elevation < 0.1) 
				return BiomeType.SWAMP;
			if (p.elevation < 0.1) 
				return BiomeType.MARSH;
			if(p.hasAttribute(Attribute.Lake))
			{
				for(Center n : p.neighbors)
					if(!n.hasMarker(Marker.Water))
						return BiomeType.LAKESHORE;
				return BiomeType.LAKE;
			}
			return BiomeType.POND;
		} 
		else if (p.hasMarker(Marker.Coast) && p.getElevation() < 0.035) 
		{
			return BiomeType.BEACH;
		} 
		else if(p.hasAttribute(Attribute.River))
		{
			return BiomeType.RIVER;
		}
		else if(getParams().hasFeature(Feature.Desert) && p.getMoisture().isLessThanOrEqual(Moisture.MEDIUM))
		{
			if(temp.equals(ClimateTemp.TEMPERATE))
				return BiomeType.TEMPERATE_DESERT;
			else if(temp.equals(ClimateTemp.SUBTROPICAL))
				return BiomeType.SUBTROPICAL_DESERT;
			else if(temp.equals(ClimateTemp.TROPICAL))
				return BiomeType.TROPICAL_DESERT;
			else if(temp.isCoolerThanOrEqual(ClimateTemp.SUBPOLAR))
				return BiomeType.POLAR_DESERT;
		}
		else if(getParams().hasFeature(Feature.Desert))
		{
			return BiomeType.DRY_FOREST;
		}
		else if(temp.equals(ClimateTemp.POLAR))
		{
			if(p.getMoisture().isGreaterThanOrEqual(Moisture.VERYHIGH))
				return BiomeType.TAIGA;
			else if(p.getMoisture().isGreaterThanOrEqual(Moisture.MEDIUM))
				return BiomeType.TUNDRA;
			else return BiomeType.BARE;
		}
		else if(temp.equals(ClimateTemp.SUBPOLAR))
		{
			if(p.getMoisture().isGreaterThanOrEqual(Moisture.MEDIUM))
				return BiomeType.TAIGA;
			else if(p.getMoisture().isGreaterThanOrEqual(Moisture.LOW))
				return BiomeType.TUNDRA;
			else return BiomeType.BARE;
		}
		else if(temp.equals(ClimateTemp.TEMPERATE))
		{
			if(p.getMoisture().isGreaterThanOrEqual(Moisture.MEDIUM))
				return BiomeType.DECIDUOUS_FOREST;
			else if(p.getMoisture().isGreaterThanOrEqual(Moisture.LOW))
				return BiomeType.SHRUBLAND;
			else return BiomeType.GRASSLAND;
		}
		else if(temp.equals(ClimateTemp.SUBTROPICAL))
		{
			if(p.getMoisture().isGreaterThanOrEqual(Moisture.MEDIUM))
				return BiomeType.RAIN_FOREST;
			else return BiomeType.GRASSLAND;
		}
		else if(temp.equals(ClimateTemp.TROPICAL))
		{
			if(p.getMoisture().isGreaterThanOrEqual(Moisture.MEDIUM))
				return BiomeType.RAIN_FOREST;
			else return BiomeType.GRASSLAND;
		}

		return BiomeType.GRASSLAND;
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
		return inside(p, true);
	}

	public Boolean inside(Point p, boolean clamp) 
	{
		return islandParams.insidePerlin(p, clamp);
	}

	double elevationBucket(Center p) 
	{
		if (p.hasMarker(Marker.Ocean)) return -1;
		else return Math.floor(p.elevation*10);
	}

	/**
	 * @param p This blockPos should be in World Coords
	 */
	public Center getClosestCenter(BlockPos p)
	{
		return getClosestCenter(new Point(p.getX() % 4096, p.getZ() % 4096));
	}

	/**
	 * @return nearest Center point for the containing hex
	 */
	public Center getClosestCenter(Point param)
	{
		Point p = param.toIslandCoord();
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

		nList = new NBTTagList();
		for(Dungeon d : dungeons)
		{
			NBTTagCompound n = new NBTTagCompound();
			d.writeToNBT(n);
			nList.appendTag(n);
		}
		nbt.setTag("dungeons", nList);

		NBTTagCompound dataNBT = new NBTTagCompound();
		this.islandData.writeToNBT(dataNBT);
		nbt.setTag("data", dataNBT);
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagList centerList = nbt.getTagList("centers", 10);
		NBTTagList cornerList = nbt.getTagList("corners", 10);
		NBTTagList edgeList = nbt.getTagList("edges", 10);
		Center c;

		centers.clear();
		corners.clear();
		edges.clear();

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
			if(c.hasAttribute(Attribute.Lake))
			{
				int lakeID = ((LakeAttribute)c.getAttribute(Attribute.Lake)).getLakeID();
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

		NBTTagList dungeonList = nbt.getTagList("dungeons", 10);
		for(int i = 0; i < dungeonList.tagCount(); i++)
		{
			Dungeon d = new Dungeon("generic", 0, 0, 0);
			d.readFromNBT(this, dungeonList.getCompoundTagAt(i));
			dungeons.add(d);
		}

		this.islandData = new IslandData(this, this.getParams());
		islandData.readFromNBT(nbt.getCompoundTag("data"));
	}


}