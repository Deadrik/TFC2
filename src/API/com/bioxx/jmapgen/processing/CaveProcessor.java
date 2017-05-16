package com.bioxx.jmapgen.processing;

import java.util.Vector;

import net.minecraft.util.math.BlockPos;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.IslandParameters.Feature;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.CaveAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.api.Global;
import com.bioxx.tfc2.api.types.StoneType;

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
		int majorCavesToGen = 40;
		if(map.getParams().hasFeature(Feature.DoubleCaves))
			majorCavesToGen *= 2;
		else if(map.getParams().hasFeature(Feature.TripleCaves))
			majorCavesToGen *= 3;

		if(land.size() == 0)
			return;

		Center s;
		for(int i = 0; i < majorCavesToGen; i++)
		{
			s = land.get(map.mapRandom.nextInt(land.size()));

			//We don't want any caves to start on the edge of the map or in a lava tile.
			if(s.hasAnyMarkersOf(Marker.Border, Marker.Lava) || s.hasAttribute(Attribute.River))
			{ 
				i--;
				continue;
			}
			starts.add(s);
		}

		int caveCount = 0;//This keeps track of the total number of caves that we have generated so far.
		if(starts.size() > 0)
			for(Center c : starts)
			{
				gen(c, caveCount);
				caveCount++;
			}

		//After we gen the initial major cave systems, we will attempt to start caves from beach cliffs.
		starts.clear();
		for(Center c : getBeachesWithCliffs(land))
		{
			if(map.mapRandom.nextDouble() < 0.6)
				starts.add(c);
		}
		if(starts.size() > 0)
			for(Center c : starts)
			{
				gen(c, caveCount);
				caveCount++;
			}

		//Next we generate sea caves from ocean coastal tiles
		starts.clear();
		for(Center c : this.getCoastalOcean(map.centers))
		{
			if(map.mapRandom.nextDouble() < 0.5)
				starts.add(c);
		}
		if(starts.size() > 0)
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

	public void gen(Center start, int caveId, boolean isSeaCave, int maxLength)
	{
		int minCaveSize = 2;
		int maxCaveSize = 6;//This makes a cave have a 2-8 radius

		StoneType stone = map.getParams().getSurfaceRock();
		if(stone.getSubType() == StoneType.SubType.Sedimentary)
		{
			maxCaveSize = 2;//This makes a cave have a 2-4 radius
		}
		else if(stone.getSubType() == StoneType.SubType.Metamorphic)
		{
			maxCaveSize = 3;//This makes a cave have a 2-5 radius
		}
		else if(stone.getSubType() == StoneType.SubType.IgneousExtrusive)
		{
			maxCaveSize = 4;//This makes a cave have a 2-6 radius
		}

		int curLength = 0;
		Center prevCenter = null;
		Center center = start;
		Center nextCenter = getHighestNonRiverNeighbor(start);
		Center sCenter, sNextCenter;

		CaveAttrNode sCurNode, sNextNode;

		//First we will perform some preliminary validity checks
		if(start.hasAttribute(Attribute.River) || (!isSeaCave && start.hasMarker(Marker.Water)))
			return;

		CaveAttrNode curNode = new CaveAttrNode(caveId);//Start slightly above the ground
		CaveAttrNode nextNode = new CaveAttrNode(caveId);
		curNode.setOffset(new BlockPos(center.point.x, mcElev(start.getElevation()) + 3, center.point.y));
		curNode.setEntrance(true);
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
					sCurNode.setNodeHeight(1+map.mapRandom.nextInt(3));
					sCurNode.setNodeWidth(2+map.mapRandom.nextInt(2));
					sNextNode = new CaveAttrNode(1000+subCaveCount);
					sNextNode.setOffset(new BlockPos(sNextCenter.point.x, sCurNode.getOffset().getY()+map.mapRandom.nextInt(20)-10, sNextCenter.point.y));
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
			if(map.mapRandom.nextDouble() < 0.05)//5% chance to move vertically in the same hex
			{
				nextCenter = center;
				elevOffset = map.mapRandom.nextInt(31)-15;
			}
			else
			{
				nextCenter = center.getRandomNeighborExcept(map.mapRandom, prevCenter);
			}

			curNode.setNodeHeight(minCaveSize+map.mapRandom.nextInt(maxCaveSize));
			curNode.setNodeWidth(minCaveSize+map.mapRandom.nextInt(maxCaveSize));

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

			int midOffsetX = -5+map.mapRandom.nextInt(11);
			int midOffsetY = -5+map.mapRandom.nextInt(11);
			int midOffsetZ = -5+map.mapRandom.nextInt(11);
			//If this cave is moving into a hex that has a surface river then we need to make sure that it doesnt try to peek the surface
			if(nextCenter.hasAttribute(Attribute.River) || (!isSeaCave && nextCenter.hasMarker(Marker.Water)))
			{
				int riverDiff = mcElev(nextCenter.getElevation()) - nextNode.getOffset().getY();
				if(riverDiff <= 10)
				{
					nextNode.getOffset().add(0, -5, 0);
					nextNode.setNodeHeight(1);
					nextNode.setNodeWidth(1);
					midOffsetY -= 10;
				}
			}

			//Setup the midpoint offsets for each node
			nextNode.setPrevOffset(getMidpoint(curNode.getOffset(), nextNode.getOffset()).add(midOffsetX, midOffsetY, midOffsetZ));
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
		CaveAttribute attrib = (CaveAttribute) c.getAttribute(Attribute.Cave);
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
