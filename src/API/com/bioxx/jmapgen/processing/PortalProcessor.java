package com.bioxx.jmapgen.processing;

import java.util.ArrayList;

import net.minecraft.util.EnumFacing;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.Point;
import com.bioxx.jmapgen.attributes.PortalAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.tfc2.api.util.Helper;

public class PortalProcessor 
{
	IslandMap map;

	public PortalProcessor(IslandMap m)
	{
		map = m;
	}

	public void generate()
	{
		//North
		Center northCenter = getValidCenter(map.getClosestCenter(new Point(2048, 1500)));
		northCenter.addAttribute(new PortalAttribute(Helper.combineCoords(map.getParams().getXCoord(), map.getParams().getZCoord()-1), EnumFacing.NORTH));

		Center southCenter = getValidCenter(map.getClosestCenter(new Point(2048, 2500)));
		southCenter.addAttribute(new PortalAttribute(Helper.combineCoords(map.getParams().getXCoord(), map.getParams().getZCoord()+1), EnumFacing.SOUTH));

		Center eastCenter = getValidCenter(map.getClosestCenter(new Point(2500, 2048)));
		eastCenter.addAttribute(new PortalAttribute(Helper.combineCoords(map.getParams().getXCoord()+1, map.getParams().getZCoord()), EnumFacing.EAST));

		Center westCenter = getValidCenter(map.getClosestCenter(new Point(1500, 2048)));
		westCenter.addAttribute(new PortalAttribute(Helper.combineCoords(map.getParams().getXCoord()-1, map.getParams().getZCoord()), EnumFacing.WEST));

	}

	private Center getValidCenter(Center c)
	{
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		ArrayList<Center> centerList = new ArrayList<Center>();
		while(c.biome != BiomeType.BEACH)
		{
			indexList.add(c.index);
			if(c.downslope == null || c.downslope == c || indexList.contains(c.index))
				c = map.getClosestCenter(new Point(c.point.getX()-100+map.mapRandom.nextInt(200), c.point.getY()-100+map.mapRandom.nextInt(200)));

			indexList.add(c.index);
			if(c.elevation < 0.3 && !c.hasAnyMarkersOf(Marker.Water, Marker.Spire, Marker.Coast) && c.biome != BiomeType.RIVER)
				centerList.add(c);

			if(c.hasAnyMarkersOf(Marker.Coast))
				break;

			c = c.downslope;
		}

		if(centerList.size() > 0)
			return centerList.get(map.mapRandom.nextInt(centerList.size()));
		return c;
	}
}
