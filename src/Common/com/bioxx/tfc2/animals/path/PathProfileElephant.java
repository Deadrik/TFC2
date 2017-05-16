package com.bioxx.tfc2.animals.path;

import com.bioxx.jmapgen.IslandMap;
import com.bioxx.jmapgen.graph.Center;

public class PathProfileElephant extends PathProfileDefaultLand 
{
	@Override
	public int getPathWeight(IslandMap map, Center prev, Center c) 
	{
		int out = super.getPathWeight(map, prev, c);
		return out;
	}

}
