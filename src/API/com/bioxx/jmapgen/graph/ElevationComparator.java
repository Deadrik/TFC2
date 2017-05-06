package com.bioxx.jmapgen.graph;

import java.util.Comparator;

public class ElevationComparator extends Center implements Comparator<Center>
{
	@Override
	public int compare(Center arg0, Center arg1) {
		int returnValue = 0;

		if(arg0.getElevation() < arg1.getElevation())
			returnValue = -1;
		else if(arg0.getElevation() > arg1.getElevation())
			returnValue = 1;

		return returnValue;
	}
}
