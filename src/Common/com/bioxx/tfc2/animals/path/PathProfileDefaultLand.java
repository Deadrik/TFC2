package com.bioxx.tfc2.animals.path;

import com.bioxx.jmapgen.BiomeType;
import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.attributes.Attribute;
import com.bioxx.jmapgen.attributes.RiverAttribute;
import com.bioxx.jmapgen.graph.Center;
import com.bioxx.jmapgen.graph.Center.Marker;
import com.bioxx.jmapgen.pathfinding.IPathProfile;

public class PathProfileDefaultLand implements IPathProfile {

	@Override
	public int getPathWeight(IslandMap map, Center prev, Center c) 
	{
		int weight = 1;
		if(c.hasAnyMarkersOf(Marker.Ocean, Marker.Lava, Marker.Mesa, Marker.CoastWater))
			return Integer.MAX_VALUE;

		if(c.biome == BiomeType.RIVER)
		{
			RiverAttribute a = (RiverAttribute)c.getAttribute(Attribute.River);
			weight += 2 * a.getRiver();
		}
		else if(c.biome == BiomeType.LAKESHORE || c.biome == BiomeType.POND)
		{
			weight += 3;
		}

		//if(Math.abs(prev.getElevation() - c.getElevation()) > map.getParams().getMCBlockHeight() * 10)
		if(prev != null)
		{
			double height = (prev.getElevation() - c.getElevation()) / map.getParams().getMCBlockHeight();
			weight += Math.abs(height);
		}

		return weight;
	}

	@Override
	public boolean shouldIgnoreCenter(IslandMap map, Center prev, Center c)
	{
		if(c.hasAnyMarkersOf(Marker.Ocean, Marker.Lava, Marker.Mesa, Marker.CoastWater))
			return true;
		double elev = Math.abs((prev.getElevation() - c.getElevation()) / map.getParams().getMCBlockHeight());
		if(elev > 5)
			return true;

		return false;
	}
}
