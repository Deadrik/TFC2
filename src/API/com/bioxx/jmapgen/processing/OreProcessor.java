package com.bioxx.jmapgen.processing;

import java.util.Vector;

import net.minecraft.util.BlockPos;

import com.bioxx.jmapgen.IslandMap;
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

		for(OreConfig oc : configs)
		{
			for(int i = 0; i < landCenters.size()/oc.getRarity(); i++)
			{
				if(oc.getVeinType() == VeinType.Seam)
					genSeam(landCenters.get(map.mapRandom.nextInt(landCenters.size())), oc);
			}
		}
	}

	private void genSeam(Center start, OreConfig oc)
	{
		int maxLength = oc.getMinSeamLength()+map.mapRandom.nextInt(oc.getMaxSeamLength()-oc.getMinSeamLength());
		int curLength = 0;
		Center prevCenter = null;
		Center center = start;
		Center nextCenter = start.getRandomNeighbor(map.mapRandom);
		Center sCenter, sNextCenter;

		OreAttrNode sCurNode, sNextNode;

		OreAttrNode curNode = new OreAttrNode(oc.getOreName());
		//Y Start between 8-(hexElevation-8)
		curNode.setOffset(new BlockPos(center.point.x, map.mapRandom.nextInt(mcElev(start.getElevation())-16)+8, center.point.y));
		OreAttrNode nextNode = new OreAttrNode(oc.getOreName());

		nextNode.setPrevOffset(getMidpoint(curNode.getOffset(), nextNode.getOffset()));
		curNode.setNextOffset(nextNode.getPrevOffset());

		int elevOffset = 0;
		double nextDir = 0.5;
		while(curLength < maxLength)
		{

			//Cycle the data to start the next iteration
			//Link our current and next nodes together
			curNode.setNext(nextCenter);
			nextNode.setPrev(center);

			//apply the node data to the current center
			addNode(center, curNode);

			//If the cave breaks into the surface again, we end the cave
			if(nextNode.offset.getY() > mcElev(nextCenter.getElevation()) || nextCenter.hasAnyMarkersOf(Marker.Border))
			{
				curLength = maxLength;
			}
			//Otherwise we continue

			curNode = nextNode;
			prevCenter = center;
			center = nextCenter;
			//Finished cycling

			curNode.setNodeHeight(oc.getVeinHeightMin()+map.mapRandom.nextInt(oc.getVeinHeightMax()-oc.getVeinHeightMin()));
			curNode.setNodeWidth(oc.getVeinWidthMin()+map.mapRandom.nextInt(oc.getVeinWidthMax()-oc.getVeinWidthMin()));

			//If the cave is long enough, we may want to create little subcave offshoots in random directions
			if(curLength > 3)
			{
				int subCaveCount = map.mapRandom.nextInt(5)+1;
				while(subCaveCount > 0)
				{
					sCenter = center;
					if(map.mapRandom.nextDouble() < 0.25)
						sNextCenter = sCenter;
					else
						sNextCenter = sCenter.getRandomNeighbor(map.mapRandom);
					sCurNode = new OreAttrNode(oc.getOreName());
					sCurNode.setOffset(curNode.getOffset());
					sCurNode.setNext(sNextCenter);
					sCurNode.setNodeHeight(1+map.mapRandom.nextInt(2));
					sCurNode.setNodeWidth(1+map.mapRandom.nextInt(2));
					sNextNode = new OreAttrNode(oc.getOreName());
					sNextNode.setOffset(new BlockPos(sNextCenter.point.x, sCurNode.getOffset().getY()+map.mapRandom.nextInt(20)-10, sNextCenter.point.x));
					sCurNode.setNextOffset(getMidpoint(sCurNode.getOffset(), sNextNode.getOffset()).add(10-map.mapRandom.nextInt(6), 10-map.mapRandom.nextInt(6), 10-map.mapRandom.nextInt(6)));
					sNextNode.setPrevOffset(sCurNode.getNextOffset());
					sNextNode.setPrev(sCenter);

					addNode(sCenter, sCurNode);
					addNode(sNextCenter, sNextNode);

					subCaveCount--;
				}
			}
			//first we perform the bias math
			elevOffset = (int)Math.floor((double)oc.getNoiseVertical() * 2.0d * oc.getNoiseBiasVertical());
			//then we apply the bias to the vertical noise
			elevOffset = elevOffset - map.mapRandom.nextInt(oc.getNoiseVertical());


			nextDir = 2.0d * oc.getPathBias();
			nextDir = nextDir - map.mapRandom.nextDouble();
			//Acquire the next hex: The path bias may cause a seam to spiral.
			HexDirection hexDir = center.getDirection(prevCenter);
			if(hexDir == null || nextDir > 0.4 && nextDir <= 0.6)
			{
				nextCenter = center.getRandomNeighborExcept(map.mapRandom, prevCenter);
			}
			else if(nextDir > 0.2 && nextDir <= 0.4)
			{
				nextCenter = center.getNeighbor(hexDir.getOpposite().getNextCounterClockwise());
			}
			else if(nextDir >= 0 && nextDir <= 0.2)
			{
				nextCenter = center.getNeighbor(hexDir.getOpposite().getNextCounterClockwise().getNextCounterClockwise());
			}
			else if(nextDir > 0.6 && nextDir <= 0.8)
			{
				nextCenter = center.getNeighbor(hexDir.getOpposite().getNextClockwise());
			}
			else if(nextDir > 0.8 && nextDir <= 1.0)
			{
				nextCenter = center.getNeighbor(hexDir.getOpposite().getNextClockwise().getNextClockwise());
			}

			//5% chance that a seam will spike vertically within the same hex
			if(map.mapRandom.nextDouble() < 0.05)
			{
				nextCenter = center;
			}

			//Create our next node
			nextNode = new OreAttrNode(oc.getOreName());

			nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY() + elevOffset, nextCenter.point.y));


			//Setup the midpoint offsets for each node
			nextNode.setPrevOffset(getMidpoint(curNode.getOffset(), nextNode.getOffset()).add(10-map.mapRandom.nextInt(6), 10-map.mapRandom.nextInt(6), 10-map.mapRandom.nextInt(6)));
			curNode.setNextOffset(nextNode.getPrevOffset());
			curLength++;
		}

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
			if(n.hasAttribute(Attribute.riverUUID))
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
