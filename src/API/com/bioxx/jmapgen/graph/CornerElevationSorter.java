package com.bioxx.jmapgen.graph;

import java.util.Comparator;

public class CornerElevationSorter extends Corner implements Comparator<Corner>
{
	@Override
	public int compare(Corner arg0, Corner arg1) {
		int returnValue = 0;
		
		if(arg0.elevation < arg1.elevation)
			returnValue = -1;
		else if(arg0.elevation > arg1.elevation)
			returnValue = 1;
		
		return returnValue;
	}
}
