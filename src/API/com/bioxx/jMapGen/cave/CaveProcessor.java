package com.bioxx.jMapGen.cave;

import java.util.Vector;

import net.minecraft.util.BlockPos;

import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.attributes.Attribute;
import com.bioxx.jMapGen.attributes.CaveAttribute;
import com.bioxx.jMapGen.graph.Center;
import com.bioxx.jMapGen.graph.Center.Marker;
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
			if(map.mapRandom.nextDouble() < 0.3)
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
			if(map.mapRandom.nextDouble() < 0.3)
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
		Center nextCenter = start.getHighestNeighbor();
		Center sCenter, sNextCenter;

		CaveAttrNode sCurNode, sNextNode;

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

			if(curLength > 3 && map.mapRandom.nextDouble() < 0.3)
			{
				int subCaveCount = map.mapRandom.nextInt(5)-1;
				while(subCaveCount > 0)
				{
					sCenter = center;
					sNextCenter = sCenter.getRandomNeighbor(map.mapRandom);
					sCurNode = new CaveAttrNode(100+subCaveCount);
					sCurNode.setOffset(curNode.getOffset());
					sCurNode.setNext(sNextCenter);
					sCurNode.setNodeHeight(1+map.mapRandom.nextInt(2));
					sCurNode.setNodeWidth(1+map.mapRandom.nextInt(2));
					sNextNode = new CaveAttrNode(100+subCaveCount);
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

			//Create our next node
			nextNode = new CaveAttrNode(caveId);
			if(isSeaCave)
			{
				nextCenter = center.getRandomFromGroup(map.mapRandom, center.getOnlyHigherCenters());
				if(nextCenter == null)
					break;
				nextNode.setSeaCave(true);
				curNode.setSeaCave(true);
			}
			else
			{
				if(nextCenter.hasMarker(Marker.Coast))
					break;
			}			

			nextNode.setOffset(new BlockPos(nextCenter.point.x, curNode.getOffset().getY() + elevOffset, nextCenter.point.y));

			if(nextCenter.hasAttribute(Attribute.riverUUID) && mcElev(nextCenter.getElevation()) - nextNode.getOffset().getY() < 10)
				break;

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
