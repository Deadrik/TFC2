package com.bioxx.jmapgen.graph;

import java.util.Comparator;

public class MoistureComparator extends Center implements Comparator<Center>
{
	@Override
	public int compare(Center arg0, Center arg1) {
		int returnValue = 0;

		if(arg0.getMoistureRaw() < arg1.getMoistureRaw())
			returnValue = -1;
		else if(arg0.getMoistureRaw() > arg1.getMoistureRaw())
			returnValue = 1;

		return returnValue;
	}
}
