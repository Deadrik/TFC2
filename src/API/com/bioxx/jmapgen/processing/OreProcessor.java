package com.bioxx.jmapgen.processing;

import java.util.Vector;

import net.minecraft.util.math.BlockPos;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.OreAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.HexDirection;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.ore.OreConfig;
import com.bioxx.tfc2.api.ore.OreConfig.VeinType;
import com.bioxx.tfc2.api.ore.OreRegistry;

public class OreProcessor 
{
	IslandMap map;

	public OreProcessor(IslandMap m)
	{
		map = m;
	}

	public void generate()
	{
		OreConfig[] configs = OreRegistry.getInstance().getConfigsForStone(map.getParams().getSurfaceRock());

		Vector<Center> landCenters = map.getCentersAbove(0.02);

		if(landCenters.size() == 0)
			return;

		for(OreConfig oc : configs)
		{
			int total = Math.max(map.getParams().hasFeature(Feature.MineralRich) ? oc.getRarity()/2 : 1, 1);
			total = map.mapRandom.nextInt(map.getParams().hasFeature(Feature.MineralRich) ? oc.getRarity() * 2 : oc.getRarity()) + total;
			for(int i = 0; i < total; i++)
			{
				if(oc.getVeinType() == VeinType.Seam)
					genSeam(landCenters.get(map.mapRandom.nextInt(landCenters.size())), oc);
				else if(oc.getVeinType() == VeinType.Layer)
					genLayer(landCenters.get(map.mapRandom.nextInt(landCenters.size())), oc);
			}
		}
	}

	private void genLayer(Center start, OreConfig oc)
	{
		/**
		 * Stage 0: Setup all of our variables
		 */
		int maxLength = oc.getMinSeamLength()+map.mapRandom.nextInt(oc.getMaxSeamLength()-oc.getMinSeamLength());
		int curLength = 0;
		HexDirection seamDir = HexDirection.values()[map.mapRandom.nextInt(HexDirection.values().length)];
		Center prevCenter = null;
		Center center = start;
		Center nextCenter = start.getNeighbor(seamDir);
		Center sCenter, sNextCenter;

		OreAttrNode sCurNode, sNextNode;

		OreAttrNode curNode = new OreAttrNode(oc.getOreName());

		int startElev = (int)(mcElev(start.getElevation())*0.75);
		startElev = map.mapRandom.nextInt(Math.max(startElev, 10));

		curNode.setOffset(new BlockPos(center.point.x, startElev, center.point.y));
		setupHeightAndWidth(oc, curNode);
		OreAttrNode nextNode = new OreAttrNode(oc.getOreName());
		nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY(), nextCenter.point.y));


		int elevOffset = 0;
		double nextDir = 0.5;
		while(curLength < maxLength)
		{
			/**
			 * Stage 1: Cycle the data to start the next iteration
			 */
			//Link our current and next nodes together
			curNode.setNext(nextCenter);
			nextNode.setPrev(center);

			//apply the node data to the current center
			addNode(center, curNode);

			//Elevation validation
			if(nextNode.offset.getY() > mcElev(nextCenter.getElevation()))
			{
				int diff = nextNode.offset.getY() - mcElev(nextCenter.getElevation());
				nextNode.offset = nextNode.offset.add(0, (-diff) - 10, 0);
			}
			else if(nextNode.offset.getY() < (int)(mcElev(nextCenter.getElevation()) * 0.2))
			{
				int diff =  (int)(mcElev(nextCenter.getElevation()) * 0.2) - nextNode.offset.getY();
				nextNode.offset = nextNode.offset.add(0, diff+2 , 0);
			}

			if(nextCenter.hasAnyMarkersOf(Marker.Border))
			{
				curLength = maxLength;
				continue;
			}

			//Otherwise we continue
			curNode = nextNode;
			prevCenter = center;
			center = nextCenter;
			//Finished cycling
			/**
			 * Stage 2: Setup up our nodes with relevant data
			 */
			setupHeightAndWidth(oc, curNode);
			//first we perform the bias math
			elevOffset = (int)Math.floor((double)oc.getNoiseVertical() * 2.0d);
			//then we apply the bias to the vertical noise
			elevOffset = oc.getNoiseVertical() - map.mapRandom.nextInt(elevOffset);

			nextCenter = center.getNeighbor(seamDir.getRandomTurnBig(map.mapRandom));

			//Sanity
			if(nextCenter == null)
				nextCenter = center.getRandomNeighbor(map.mapRandom);

			//Create our next node
			nextNode = new OreAttrNode(oc.getOreName());
			nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY() + elevOffset, nextCenter.point.y));

			curLength++;
		}
	}

	private void genSeam(Center start, OreConfig oc)
	{
		/**
		 * Stage 0: Setup all of our variables
		 */
		int maxLength = oc.getMinSeamLength()+map.mapRandom.nextInt(oc.getMaxSeamLength()-oc.getMinSeamLength());
		int curLength = 0;
		HexDirection seamDir = HexDirection.values()[map.mapRandom.nextInt(HexDirection.values().length)];
		Center prevCenter = null;
		Center center = start;
		Center nextCenter = start.getNeighbor(seamDir);
		Center sCenter, sNextCenter;

		OreAttrNode sCurNode, sNextNode;

		OreAttrNode curNode = new OreAttrNode(oc.getOreName());

		int startElev = (int)(mcElev(start.getElevation())*0.75);
		startElev = map.mapRandom.nextInt(Math.max(startElev, 10));

		curNode.setOffset(new BlockPos(center.point.x, startElev, center.point.y));
		setupHeightAndWidth(oc, curNode);
		OreAttrNode nextNode = new OreAttrNode(oc.getOreName());
		nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY(), nextCenter.point.y));
		nextNode.setPrevOffset(getMidpoint(curNode.getOffset(), nextNode.getOffset()));
		curNode.setNextOffset(nextNode.getPrevOffset());

		int elevOffset = 0;
		double nextDir = 0.5;
		while(curLength < maxLength)
		{
			/**
			 * Stage 1: Cycle the data to start the next iteration
			 */
			//Link our current and next nodes together
			curNode.setNext(nextCenter);
			nextNode.setPrev(center);

			//apply the node data to the current center
			addNode(center, curNode);

			//Elevation validation
			if(nextNode.offset.getY() > mcElev(nextCenter.getElevation()))
			{
				int diff = nextNode.offset.getY() - mcElev(nextCenter.getElevation());
				nextNode.offset = nextNode.offset.add(0, (-diff) - 10, 0);
			}
			else if(nextNode.offset.getY() < (int)(mcElev(nextCenter.getElevation()) * 0.2))
			{
				int diff =  (int)(mcElev(nextCenter.getElevation()) * 0.2) - nextNode.offset.getY();
				nextNode.offset = nextNode.offset.add(0, diff+2 , 0);
			}

			if(nextCenter.hasAnyMarkersOf(Marker.Border))
			{
				curLength = maxLength;
				continue;
			}

			//Otherwise we continue
			curNode = nextNode;
			prevCenter = center;
			center = nextCenter;
			//Finished cycling
			/**
			 * Stage 2: Setup up our nodes with relevant data
			 */
			setupHeightAndWidth(oc, curNode);

			//Create little offshoots of the main seam.
			if(oc.getSubSeamRarity() > 0 && map.mapRandom.nextInt(oc.getSubSeamRarity()) == 0)
			{
				int subSeamLength = map.mapRandom.nextInt(2)+1;
				HexDirection subSeamDir = seamDir.getRandomTurnBig(map.mapRandom, false);
				//Perform initial setup
				sCenter = center;
				sCurNode = new OreAttrNode(oc.getOreName());
				sCurNode.setOffset(curNode.getOffset());
				sCurNode.setNodeHeight(Math.max(curNode.getNodeHeight()/2, 1));
				sCurNode.setNodeWidth(Math.max(curNode.getNodeWidth()/2, 1));
				sNextNode = new OreAttrNode(oc.getOreName());
				sNextCenter = sCenter.getNeighbor(subSeamDir);
				sCurNode.setNext(sNextCenter);

				if(sNextCenter == null)
					break;

				elevOffset = oc.getNoiseVertical()*2 - map.mapRandom.nextInt(oc.getNoiseVertical() * 4);
				sNextNode.setOffset(new BlockPos(sNextCenter.point.x, sCurNode.getOffset().getY() + elevOffset, sNextCenter.point.y));
				sCurNode.setNextOffset(getMidpoint(sCurNode.getOffset(), sNextNode.getOffset()));
				sNextNode.setPrevOffset(sCurNode.getNextOffset());
				sNextNode.setPrev(sCenter);
				addNode(sCenter, sCurNode);

				while(subSeamLength > 0)
				{
					// Begin Cycling
					sCurNode = sNextNode;
					sCenter = sNextCenter;
					// End Cycling 

					if(oc.getNoiseVertical() >= 10 && map.mapRandom.nextDouble() < 0.15)
					{
						sNextCenter = sCenter;
						elevOffset = oc.getNoiseVertical() - map.mapRandom.nextInt((int)Math.floor(oc.getNoiseVertical() * 2));
					}
					else
					{
						sNextCenter = sCenter.getNeighbor(subSeamDir.getRandomTurnSmall(map.mapRandom));
						elevOffset = oc.getNoiseVertical() - map.mapRandom.nextInt((int)Math.floor(oc.getNoiseVertical() * 2));
					}

					if(sNextCenter == null)
						break;

					sCurNode.setNodeHeight(Math.max(curNode.getNodeHeight()/2, 1));
					sCurNode.setNodeWidth(Math.max(curNode.getNodeWidth()/2, 1));

					sNextNode = new OreAttrNode(oc.getOreName());
					sNextNode.setOffset(new BlockPos(sNextCenter.point.x, sCurNode.getOffset().getY() + elevOffset, sNextCenter.point.y));

					//Elevation validation
					if(sNextNode.offset.getY() > mcElev(sNextCenter.getElevation()))
					{
						int diff = sNextNode.offset.getY() - mcElev(sNextCenter.getElevation());
						sNextNode.offset = sNextNode.offset.add(0, (-diff) - 10, 0);
					}
					else if(sNextNode.offset.getY() < (int)(mcElev(sNextCenter.getElevation()) * 0.2))
					{
						int diff =  (int)(mcElev(sNextCenter.getElevation()) * 0.2) - sNextNode.offset.getY();
						sNextNode.offset = sNextNode.offset.add(0, diff+2 , 0);
					}

					//Link the current node and the next node together
					sCurNode.setNext(sNextCenter);
					sCurNode.setNextOffset(getMidpoint(sCurNode.getOffset(), sNextNode.getOffset()).add(
							oc.getNoiseHorizontal()-map.mapRandom.nextInt((oc.getNoiseHorizontal()*2)), 
							elevOffset, 
							oc.getNoiseHorizontal()-map.mapRandom.nextInt((oc.getNoiseHorizontal()*2))));
					sNextNode.setPrevOffset(sCurNode.getNextOffset());
					sNextNode.setPrev(sCenter);

					addNode(sCenter, sCurNode);
					subSeamLength--;
				}
			}

			//first we perform the bias math
			elevOffset = (int)Math.floor((double)oc.getNoiseVertical() * 2.0d);
			//then we apply the bias to the vertical noise
			elevOffset = oc.getNoiseVertical() - map.mapRandom.nextInt(elevOffset);

			nextCenter = center.getNeighbor(seamDir.getRandomTurnSmall(map.mapRandom));

			if(map.mapRandom.nextDouble() < 0.05) //Otherwise, 5% chance that a seam will spike vertically within the same hex
			{
				nextCenter = center;
				elevOffset *= 2;
			}

			//Sanity
			if(nextCenter == null)
				nextCenter = center.getRandomNeighbor(map.mapRandom);

			//Create our next node
			nextNode = new OreAttrNode(oc.getOreName());

			nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY() + elevOffset, nextCenter.point.y));

			int horiz = oc.getNoiseHorizontal()*2;
			//Setup the midpoint offsets for each node
			nextNode.setPrevOffset(getMidpoint(curNode.getOffset(), nextNode.getOffset()).add(oc.getNoiseHorizontal()-map.mapRandom.nextInt(horiz), 0, oc.getNoiseHorizontal()-map.mapRandom.nextInt(horiz)));
			curNode.setNextOffset(nextNode.getPrevOffset());
			curLength++;
		}

	}

	private void setupHeightAndWidth(OreConfig oc, OreAttrNode curNode) {
		if(oc.getVeinHeightMin() == oc.getVeinHeightMax())
			curNode.setNodeHeight(oc.getVeinHeightMin());
		else
			curNode.setNodeHeight(oc.getVeinHeightMin()+map.mapRandom.nextInt(oc.getVeinHeightMax()-oc.getVeinHeightMin()));

		if(oc.getVeinWidthMin() == oc.getVeinWidthMax())
			curNode.setNodeWidth(oc.getVeinWidthMin());
		else
			curNode.setNodeWidth(oc.getVeinWidthMin()+map.mapRandom.nextInt(oc.getVeinWidthMax()-oc.getVeinWidthMin()));
	}

	private BlockPos getMidpoint(BlockPos p0, BlockPos p1)
	{
		int x = (p0.getX() + p1.getX()) / 2;
		int y = (p0.getY() + p1.getY()) / 2;
		int z = (p0.getZ() + p1.getZ()) / 2;
		return new BlockPos(x, y, z);
	}

	private void addNode(Center c, OreAttrNode n)
	{
		OreAttribute attrib = (OreAttribute) c.getAttribute(Attribute.Ore);
		if(attrib == null)
		{
			attrib = new OreAttribute();
			c.addAttribute(attrib);
		}
		attrib.addNode(n);

	}

	private Center getHighestNonRiverNeighbor(Center c)
	{
		Center out = null;
		for(Center n : c.neighbors)
		{
			if(n.hasAttribute(Attribute.River))
				continue;
			if(out == null || n.getElevation() > out.getElevation())
				out = n;
		}

		return out;
	}

	private int mcElev(double d)
	{
		return map.convertHeightToMC(d)+Global.SEALEVEL;
	}

	private Vector<Center> getCentersBetween(Vector<Center> centers, double min, double max)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if (c.elevation > min && c.getElevation() < max)
				out.add(c);
		}
		return out;
	}

	private Vector<Center> getBeachesWithCliffs(Vector<Center> centers)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(!c.hasMarker(Marker.Coast))
				continue;
			Center n = c.getHighestNeighbor();
			if(mcElev(n.getElevation()) - mcElev(c.getElevation()) < 8)
				continue;

			out.add(c);
		}
		return out;
	}

	private Vector<Center> getCoastalOcean(Vector<Center> centers)
	{
		Vector<Center> out = new Vector<Center>();
		for(Center c : centers)
		{
			if(!c.hasMarker(Marker.CoastWater))
				continue;
			Center n = c.getLowestNeighbor();
			if(mcElev(c.getElevation()) - mcElev(n.getElevation()) < 8)
				continue;

			out.add(n);
		}
		return out;
	}


}
