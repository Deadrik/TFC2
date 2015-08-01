package com.bioxx.jMapGen.cave;

import java.util.Vector;

import net.minecraft.util.BlockPos;

import com.bioxx.jMapGen.IslandMap;
import com.bioxx.jMapGen.graph.Center;

public class CaveProcessor 
{
	IslandMap map;

	public CaveProcessor(IslandMap m)
	{
		map = m;
	}

	public void generate()
	{
		Vector<Center> land = getCentersBetween(map.centers, 0.1, 1.0);
		int majorCavesToGen = 20;

		for(int caveCount = 0; caveCount < majorCavesToGen; caveCount++)
		{
			gen(land.get(map.mapRandom.nextInt(land.size())));
		}
	}

	private void gen(Center start)
	{
		int maxLength = 50;
		int curLength = 0;
		Vector<CaveNode> nodeList = new Vector<CaveNode>();

		//First form the mouth of the cave by either going straight down or at an angle.
		//If the start hex is next to a cliff then move into that
		CaveNode curNode = new CaveNode(start, new BlockPos(0, 3, 0));//Start slightly above the ground
		CaveNode nextNode = new CaveNode(start.getHighestNeighbor(), new BlockPos(0, -10, 0));
		if(map.convertHeightToMC(nextNode.center.getElevation()) - map.convertHeightToMC(start.getElevation()) > 10)
		{
			nextNode.offset = curNode.offset;
		}
		int elevOffset = 0;
		while(curLength < maxLength)
		{
			//If the cave breaks into the surface again, we end the cave
			if(map.convertMCToHeight(nextNode.offset.getY()) > nextNode.center.elevation)
				break;
			curNode.next = nextNode;
			curNode = nextNode;
			elevOffset = map.mapRandom.nextInt(21)-10;
			//nextNode = new CaveNode(curNode.center.getRandomNeighbor(map.mapRandom), curNode.elev+elevOffset);

		}

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

	class CaveNode
	{
		public Center center;
		public BlockPos offset;
		public CaveNode next;
		public CaveNode prev;

		public CaveNode(Center c, BlockPos o)
		{
			center = c;
			offset = o;
		}
	}
}
