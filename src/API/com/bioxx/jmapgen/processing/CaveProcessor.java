package com.bioxx.jmapgen.processing;

import java.util.Vector;

import net.minecraft.util.BlockPos;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.CaveAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.api.Global;

public class CaveProcessor 
{
	IslandMap map;

	public CaveProcessor(IslandMap m)
	{
		map = m;
	}

	public void generate()
	{
		Vector<Center> land = getCentersBetween(map.centers, 0.02, 1.0);
		Vector<Center> starts = new Vector<Center>();
		int majorCavesToGen = 30;

		if(land.size() == 0)
			return;

		Center s;
		for(int i = 0; i < majorCavesToGen; i++)
		{
			s = land.get(map.mapRandom.nextInt(land.size()));
			if(s.hasAnyMarkersOf(Marker.Border, Marker.Lava) || s.hasAttribute(Attribute.riverUUID))
			{ 
				i--;
				continue;
			}
			starts.add(s);
		}

		int caveCount = 0;
		for(Center c : starts)
		{
			gen(c, caveCount);
			caveCount++;
		}

		starts.clear();
		for(Center c : getBeachesWithCliffs(land))
		{
			if(map.mapRandom.nextDouble() < 0.6)
				starts.add(c);
		}

		for(Center c : starts)
		{
			gen(c, caveCount);
			caveCount++;
		}

		starts.clear();
		for(Center c : this.getCoastalOcean(map.centers))
		{
			if(map.mapRandom.nextDouble() < 0.5)
				starts.add(c);
		}

		for(Center c : starts)
		{
			gen(c, caveCount, true, 5);
			caveCount++;
		}
	}

	private void gen(Center start, int caveId)
	{
		this.gen(start, caveId, false, 50);
	}

	private void gen(Center start, int caveId, boolean isSeaCave, int maxLength)
	{
		int curLength = 0;
		Center prevCenter = null;
		Center center = start;
		Center nextCenter = getHighestNonRiverNeighbor(start);
		Center sCenter, sNextCenter;

		CaveAttrNode sCurNode, sNextNode;

		//First we will perform some preliminary validity checks
		if(start.hasAttribute(Attribute.riverUUID))
			return;

		CaveAttrNode curNode = new CaveAttrNode(caveId);//Start slightly above the ground
		CaveAttrNode nextNode = new CaveAttrNode(caveId);
		curNode.setOffset(new BlockPos(center.point.x, mcElev(start.getElevation()) + 3, center.point.y));

		//First form the mouth of the cave by either going straight down or at an angle.
		//If the start hex is next to a cliff then move into that
		if(mcElev(nextCenter.getElevation()) - mcElev(start.getElevation()) >= 8)
		{
			nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY(), nextCenter.point.y));
		}
		else
		{
			nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY()-10, nextCenter.point.y));
		}
		nextNode.setPrevOffset(getMidpoint(curNode.getOffset(), nextNode.getOffset()));
		curNode.setNextOffset(nextNode.getPrevOffset());

		if(isSeaCave)
			curNode.setSeaCave(true);

		int elevOffset = 0;
		while(curLength < maxLength)
		{

			//Cycle the data to start the next iteration
			curNode.setNext(nextCenter);
			nextNode.setPrev(center);
			addNode(center, curNode);

			//If the cave breaks into the surface again, we end the cave
			if(nextNode.offset.getY() > mcElev(nextCenter.getElevation()))
			{
				curLength = maxLength;
			}
			//Otherwise we continue

			curNode = nextNode;
			prevCenter = center;
			center = nextCenter;
			//Finished cycling

			curNode.setNodeHeight(1+map.mapRandom.nextInt(4));
			curNode.setNodeWidth(2+map.mapRandom.nextInt(4));
			if(mcElev(center.getElevation()) - curNode.offset.getY() > 20)
			{
				if(map.mapRandom.nextDouble() < 0.1)
					curNode.setMajorNode(true);
			}

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
					sCurNode = new CaveAttrNode(1000+subCaveCount);
					sCurNode.setOffset(curNode.getOffset());
					sCurNode.setNext(sNextCenter);
					sCurNode.setNodeHeight(1+map.mapRandom.nextInt(2));
					sCurNode.setNodeWidth(1+map.mapRandom.nextInt(2));
					sNextNode = new CaveAttrNode(1000+subCaveCount);
					sNextNode.setOffset(new BlockPos(sNextCenter.point.x, sCurNode.getOffset().getY()+map.mapRandom.nextInt(20)-10, sNextCenter.point.x));
					sCurNode.setNextOffset(getMidpoint(sCurNode.getOffset(), sNextNode.getOffset()).add(10-map.mapRandom.nextInt(6), 10-map.mapRandom.nextInt(6), 10-map.mapRandom.nextInt(6)));
					sNextNode.setPrevOffset(sCurNode.getNextOffset());
					sNextNode.setPrev(sCenter);

					addNode(sCenter, sCurNode);
					addNode(sNextCenter, sNextNode);

					subCaveCount--;
				}
			}

			elevOffset = map.mapRandom.nextInt(21)-10;


			//Acquire the next hex
			nextCenter = center.getRandomNeighborExcept(map.mapRandom, prevCenter);

			if(map.mapRandom.nextDouble() < 0.05)
			{
				nextCenter = center;
				elevOffset = map.mapRandom.nextInt(31)-15;
			}

			//Create our next node
			nextNode = new CaveAttrNode(caveId);
			if(curNode.isSeaCave)
			{
				nextCenter = center.getRandomFromGroup(map.mapRandom, center.getOnlyHigherCenters());
				if(nextCenter == null)
					break;				

				if(curNode.offset.getY() <= Global.SEALEVEL)
					nextNode.setSeaCave(true);
			}
			else
			{
				if(nextCenter.hasMarker(Marker.Coast))
					break;
			}



			nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY() + elevOffset, nextCenter.point.y));

			//If this cave is moving into a hex that has a surface river then we need to make sure that it doesnt try to peek the surface
			if(nextCenter.hasAttribute(Attribute.riverUUID))
			{
				int riverDiff = mcElev(nextCenter.getElevation()) - nextNode.getOffset().getY();
				if(riverDiff <= 10)
				{
					nextNode.getOffset().add(0, -5, 0);
					nextNode.setNodeHeight(1);
					nextNode.setNodeWidth(1);
				}
			}

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

	private void addNode(Center c, CaveAttrNode n)
	{
		CaveAttribute attrib = (CaveAttribute) c.getAttribute(Attribute.caveUUID);
		if(attrib == null)
		{
			attrib = new CaveAttribute();
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
