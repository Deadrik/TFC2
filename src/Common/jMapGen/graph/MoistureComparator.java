package jMapGen.graph;

import java.util.Comparator;

public class MoistureComparator extends Center implements Comparator<Center>
{
	@Override
	public int compare(Center arg0, Center arg1) {
		int returnValue = 0;

		if(arg0.moisture < arg1.moisture)
			returnValue = -1;
		else if(arg0.moisture > arg1.moisture)
			returnValue = 1;

		return returnValue;
	}
}
